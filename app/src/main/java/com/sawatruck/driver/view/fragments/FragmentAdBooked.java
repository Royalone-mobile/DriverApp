package com.sawatruck.driver.view.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.Advertisement;
import com.sawatruck.driver.entities.User;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.Logger;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.utils.Notice;
import com.sawatruck.driver.utils.StringUtil;
import com.sawatruck.driver.utils.UserManager;
import com.sawatruck.driver.view.adapter.AdAdapter;
import com.todddavies.components.progressbar.ProgressWheel;

import org.json.JSONArray;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by royal on 8/19/2017.
 */


public class FragmentAdBooked extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String ARG_POSITION = "position";
    public static final String TAG = FragmentAdBooked.class.getSimpleName();

    @Bind(R.id.rc_container) RecyclerView rcContainer;
    @Bind(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.progressbar_loading) ProgressWheel loadingProgress;

    private AdAdapter adAdapter;

    public static FragmentAdBooked getInstance(int position) {
        FragmentAdBooked f = new FragmentAdBooked();
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
        return view;
    }

    public void initView(){
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        rcContainer.setVerticalScrollBarEnabled(true);
        rcContainer.setLayoutManager(mLayoutManager);
        adAdapter = new AdAdapter(getContext(),0);
        rcContainer.setAdapter(adAdapter);

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
        adAdapter.initializeAdapter();

        getBookedAd();
    }

    @Override
    public void onRefresh() {
        adAdapter.initializeAdapter();
        getBookedAd();
    }

    public void getBookedAd(){
        RequestParams params = new RequestParams();
        params.put("status", String.valueOf(Constant.AdvertisementStatus.Booked));
        HttpUtil httpUtil = new HttpUtil();

        User user  = UserManager.with(getContext()).getCurrentUser();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());
        httpUtil.get(Constant.GET_MY_AD_API, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String paramString = new String(responseBody);
                paramString = StringUtil.escapeString(paramString);
                Logger.error(paramString);
                ArrayList<Advertisement> adList = new ArrayList<>();
                try {
                    JSONArray jsonArray = new JSONArray(paramString);
                    for(int j=0; j<jsonArray.length(); j++) {
                        Advertisement ad = BaseApplication.getGson().fromJson(jsonArray.get(j).toString(), Advertisement.class);
                        adList.add(ad);
                    }

                    adAdapter.setAdList(adList);
                    adAdapter.notifyDataSetChanged();
                    if(adList.size() == 0){
                        FragmentMenuAd fragmentMenuAd = (FragmentMenuAd) getParentFragment();
                        if(fragmentMenuAd.pager.getCurrentItem() == 0)
                            Notice.show(R.string.no_booked_ad);
                    }
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
}
