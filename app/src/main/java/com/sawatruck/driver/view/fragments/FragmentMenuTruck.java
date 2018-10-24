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
import com.sawatruck.driver.utils.StringUtil;
import com.sawatruck.driver.utils.UserManager;

import com.sawatruck.driver.view.activity.ActivitySubmitTruck;
import com.sawatruck.driver.view.activity.BaseActivity;
import com.sawatruck.driver.view.adapter.TruckAdapter;
import com.todddavies.components.progressbar.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by royal on 8/22/2017.
 */

public class FragmentMenuTruck extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String ARG_POSITION = "position";
    public static final String TAG = FragmentMenuTruck.class.getSimpleName();

    @Bind(R.id.rc_container) RecyclerView rcContainer;
    @Bind(R.id.btn_add_truck) View btnAddTruck;
    @Bind(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.progressbar_loading) ProgressWheel loadingProgress;

    TruckAdapter truckAdapter;

    public static FragmentMenuTruck getInstance(int position) {
        FragmentMenuTruck f = new FragmentMenuTruck();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_truck, container, false);
        ButterKnife.bind(this, view);
        initView();
        BaseActivity baseActivity = (BaseActivity) getActivity();
        baseActivity.setAppTitle(getResources().getString(R.string.sidemenu_mytruck));
        baseActivity.showOptions(false);
        btnAddTruck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ActivitySubmitTruck.class);
                intent.putExtra(Constant.INTENT_SUBMIT_TRUCK, 1);
                startActivity(intent);
            }
        });

        rcContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Logger.error("MenuTruck touch");
                truckAdapter.notifyDataSetChanged();
                return false;
            }
        });
        return view;
    }

    public void initView() {
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        rcContainer.setVerticalScrollBarEnabled(true);
        rcContainer.setLayoutManager(mLayoutManager);
        truckAdapter = new TruckAdapter(getContext());
        rcContainer.setAdapter(truckAdapter);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    @Override
    public void onResume() {
        super.onResume();

        loadingProgress.setVisibility(View.VISIBLE);
        loadingProgress.startSpinning();
        truckAdapter.initializeAdapter();

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
                    Logger.error(trucksList.toString());

                    truckAdapter.setTruckList(trucksList);
                    truckAdapter.notifyDataSetChanged();

                    if(trucksList.size() == 0){
                        Notice.show(R.string.error_no_truck);
                    }

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
                loadingProgress.setVisibility(View.GONE);
                loadingProgress.stopSpinning();
                if(swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onRefresh() {
        truckAdapter.initializeAdapter();
        getMyTrucks();
    }
}

