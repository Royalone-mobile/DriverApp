package com.sawatruck.driver.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.Truck;
import com.sawatruck.driver.entities.User;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.Logger;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.utils.Notice;
import com.sawatruck.driver.utils.Serializer;
import com.sawatruck.driver.utils.StringUtil;
import com.sawatruck.driver.utils.UserManager;
import com.sawatruck.driver.view.activity.ActivitySubmitAd;
import com.sawatruck.driver.view.activity.BaseActivity;
import com.sawatruck.driver.view.adapter.SelectTruckAdapter;
import com.todddavies.components.progressbar.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by royal on 8/19/2017.
 */


public class FragmentPostAd extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, SelectTruckAdapter.OnTruckClickListener {
    public static final String TAG = FragmentPostAd.class.getSimpleName();

    @Bind(R.id.rc_container) RecyclerView rcContainer;
    @Bind(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.progressbar_loading) ProgressWheel loadingProgress;

    private SelectTruckAdapter selectTruckAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_postads, container, false);
        ButterKnife.bind(this,view);

        BaseActivity baseActivity = (BaseActivity) getActivity();
        baseActivity.setAppTitle(getResources().getString(R.string.title_post_ads));
        baseActivity.showOptions(false);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        initView();

        rcContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Logger.error("MenuTruck touch");
                selectTruckAdapter.notifyDataSetChanged();
                return false;
            }
        });

        return view;
    }

    public void initView() {
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        rcContainer.setVerticalScrollBarEnabled(true);
        rcContainer.setLayoutManager(mLayoutManager);
        selectTruckAdapter = new SelectTruckAdapter(getContext());
        rcContainer.setAdapter(selectTruckAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadingProgress.setVisibility(View.VISIBLE);
        loadingProgress.startSpinning();
        selectTruckAdapter.initializeAdapter();
        selectTruckAdapter.setOnTruckClickListener(this);
        getMyTrucks();
    }

    public void getMyTrucks(){
        HttpUtil httpUtil = new HttpUtil();
        User user  = UserManager.with(getContext()).getCurrentUser();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());

        httpUtil.get(Constant.GET_MY_TRUCKS_API, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String paramString = new String(responseBody);
                paramString = StringUtil.escapeString(paramString);


                ArrayList<Truck> trucksList = new ArrayList<>();
                try {
                    JSONArray jsonArray = new JSONArray(paramString);
                    for(int j=0; j<jsonArray.length(); j++) {
                        Truck truck = BaseApplication.getGson().fromJson(jsonArray.get(j).toString(), Truck.class);
                        trucksList.add(truck);
                    }

                    if(trucksList.size() == 0) {
                        Notice.show(getString(R.string.empty_truck));
                        return;
                    }
                    if(trucksList.size()==1){
                        GoToAdScreen(trucksList.get(0));
                    }
                    selectTruckAdapter.setTruckList(trucksList);
                    selectTruckAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Misc.showResponseMessage(responseBody);
            }
            @Override
            public void onFinish(){
                try {
                    loadingProgress.setVisibility(View.GONE);
                    loadingProgress.stopSpinning();
                    if (swipeRefreshLayout.isRefreshing())
                        swipeRefreshLayout.setRefreshing(false);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        selectTruckAdapter.initializeAdapter();
        getMyTrucks();
    }

    @Override
    public void OnTruckClicked(Truck truck) {
        GoToAdScreen(truck);
    }

    private void GoToAdScreen(Truck truck){
        try {
            Intent intent = new Intent(getActivity(), ActivitySubmitAd.class);
            intent.putExtra(Constant.INTENT_SUBMIT_AD, 1);

            intent.putExtra(Constant.TRUCK_ID, truck.getID());
            startActivity(intent);
        }catch (Exception e){

        }
    }
}
