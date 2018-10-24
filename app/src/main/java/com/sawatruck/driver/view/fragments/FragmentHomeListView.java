package com.sawatruck.driver.view.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.Load;
import com.sawatruck.driver.entities.User;
import com.sawatruck.driver.utils.AppSettings;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.Logger;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.utils.UserManager;
import com.sawatruck.driver.view.adapter.LoadsAdapter;
import com.sawatruck.driver.view.design.HideShowScrollListener;
import com.todddavies.components.progressbar.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by royal on 8/20/2017.
 */


public class FragmentHomeListView extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String ARG_POSITION = "position";
    @Bind(R.id.rc_container) RecyclerView rcContainer;
    @Bind(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.progressbar_loading) ProgressWheel loadingProgress;
    public LoadsAdapter loadsAdapter;
    public ArrayList<Load> loads = new ArrayList<>();
    public static boolean fromMapView = false;
    static FragmentHomeListView _instance;
    public static FragmentHomeListView getInstance(int position) {
        FragmentHomeListView f = new FragmentHomeListView();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        ButterKnife.bind(this,view);
        initView();
        _instance = this;
        return view;
    }

    public void initView(){
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        rcContainer.setVerticalScrollBarEnabled(true);
        rcContainer.setLayoutManager(mLayoutManager);
        loadsAdapter = new LoadsAdapter(getContext());

        rcContainer.setAdapter(loadsAdapter);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        rcContainer.addOnScrollListener(new HideShowScrollListener() {

            @Override
            public void onHide() {
                final FragmentMenuHome parentFragment = (FragmentMenuHome) getParentFragment();
                parentFragment.getPostAddButton().animate().setDuration(200).translationY(parentFragment.getPostAddButton().getHeight()).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        parentFragment.getPostAddButton().setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onShow() {
                final FragmentMenuHome parentFragment = (FragmentMenuHome) getParentFragment();
                parentFragment.getPostAddButton().animate().setDuration(200).translationY(0).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        parentFragment.getPostAddButton().setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onScrolled() {

            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();
        try {
            if(!fromMapView)
                searchLoads();
            else {
                loadsAdapter.setLoads(loads);
                loadsAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        loadingProgress.setVisibility(View.VISIBLE);
        loadingProgress.startSpinning();
        loadsAdapter.initializeAdapter();
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        _instance = null;
        fromMapView = false;
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onStop(){
        super.onStop();
        _instance = null;
        fromMapView = false;
    }

    @Override
    public void onPause(){
        super.onPause();
        _instance = null;
        fromMapView = false;
    }

    private void searchLoads() throws JSONException {
		loads = new ArrayList<>();

        LatLng latLng = new LatLng(AppSettings.with(getContext()).getCurrentLat(), AppSettings.with(getContext()).getCurrentLng());
        RequestParams requestParams = new RequestParams();

        requestParams.put("Start","0");
        requestParams.put("Length","999");
        requestParams.put("FromLatitude", String.valueOf(latLng.latitude));
        requestParams.put("FromLongitude", String.valueOf(latLng.longitude));
//        requestParams.put("FromLatitude", "1.538776");
//        requestParams.put("FromLongitude", "103.639930");

        requestParams.put("Distance", Constant.DISTANCE_RADIUS);
        requestParams.put("TruckTypeID", String.valueOf(AppSettings.with(getContext()).getTruckType()));
        requestParams.put("TruckClassID", String.valueOf(AppSettings.with(getContext()).getTruckClass()));
        requestParams.put("LoadTypeID", String.valueOf(AppSettings.with(getContext()).getLoadType()));

        Logger.error(requestParams.toString());


        HttpUtil httpUtil = HttpUtil.getInstance();
        User user  = UserManager.with(getActivity()).getCurrentUser();

        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());

        httpUtil.post(Constant.LOADS_SEARCH_API, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String paramString = new String(responseBody);
                    JSONObject jsonObject = new JSONObject(paramString);
                    JSONArray jsonArray = (JSONArray) jsonObject.get("Data");

                    for (int j = 0; j < jsonArray.length(); j++) {
                        Load load = BaseApplication.getGson().fromJson(jsonArray.get(j).toString(), Load.class);
                        loads.add(load);
                    }

                    loadsAdapter.setLoads(loads);
                    loadsAdapter.notifyDataSetChanged();

                } catch (Exception e) {
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
       loadsAdapter.initializeAdapter();
        try {
            searchLoads();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
