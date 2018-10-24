package com.sawatruck.driver.view.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.Load;
import com.sawatruck.driver.entities.Truck;
import com.sawatruck.driver.entities.User;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.utils.NetUtil;
import com.sawatruck.driver.utils.Notice;
import com.sawatruck.driver.utils.Serializer;
import com.sawatruck.driver.utils.StringUtil;
import com.sawatruck.driver.utils.UserManager;
import com.sawatruck.driver.view.adapter.SelectTruckAdapter;
import com.todddavies.components.progressbar.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by royal on 9/2/2017.
 */

public class ActivitySelectTruck extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, SelectTruckAdapter.OnTruckClickListener {
    @Bind(R.id.rc_container) RecyclerView rcContainer;
    @Bind(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.progressbar_loading) ProgressWheel loadingProgress;

    private Load load;
    private SelectTruckAdapter selectTruckAdapter;

    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_select_truck,null);
        ButterKnife.bind(this, view);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        initView();
        load = Serializer.getInstance().deserializeLoad(getIntent().getStringExtra("load"));
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        showNavHome(false);
        setAppTitle(getString(R.string.title_send_offer));
        loadingProgress.setVisibility(View.VISIBLE);
        loadingProgress.startSpinning();
        selectTruckAdapter.initializeAdapter();
        selectTruckAdapter.setOnTruckClickListener(this);
        getMyTrucks();

    }

    private void initView() {
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        rcContainer.setVerticalScrollBarEnabled(true);
        rcContainer.setLayoutManager(mLayoutManager);
        selectTruckAdapter = new SelectTruckAdapter(this);
        rcContainer.setAdapter(selectTruckAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getMyTrucks(){
        HttpUtil httpUtil = new HttpUtil();
        User user  = UserManager.with(context).getCurrentUser();
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

                    selectTruckAdapter.setTruckList(trucksList);
                    selectTruckAdapter.notifyDataSetChanged();

                    if(trucksList.size() == 0)
                        Notice.show(R.string.error_no_truck);
                    if(trucksList.size()==1) {
                        GoToSendOfferActivity(trucksList.get(0));
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
        GoToSendOfferActivity(truck);
    }

    private void GoToSendOfferActivity(Truck truck){
        Intent intent = new Intent(this, ActivitySendOffer.class);
        intent.putExtra("load", Serializer.getInstance().serializeLoad(load));
        intent.putExtra("truck", Serializer.getInstance().serializeTruck(truck));
        startActivity(intent);
        finish();
    }
}
