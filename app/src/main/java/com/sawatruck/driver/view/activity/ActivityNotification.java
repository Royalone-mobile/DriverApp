package com.sawatruck.driver.view.activity;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.NotificationModel;
import com.sawatruck.driver.entities.NotificationModels;
import com.sawatruck.driver.entities.User;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.Logger;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.utils.StringUtil;
import com.sawatruck.driver.utils.UserManager;
import com.sawatruck.driver.view.adapter.NotificationSection;
import com.sawatruck.driver.view.design.CustomTextView;
import com.todddavies.components.progressbar.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;


public class ActivityNotification extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    @Bind(R.id.rc_container) RecyclerView rcContainer;
    @Bind(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.progressbar_loading) ProgressWheel loadingProgress;
    private SectionedRecyclerViewAdapter sectionAdapter;
    private static ActivityNotification _instance;

    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_notification,null);
        ButterKnife.bind(this, view);
        swipeRefreshLayout.setOnRefreshListener(this);
        initView();

        _instance = this;
        return view;
    }

    private void initView(){
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        rcContainer.setVerticalScrollBarEnabled(true);
        rcContainer.setLayoutManager(mLayoutManager);

        sectionAdapter = new SectionedRecyclerViewAdapter();
        rcContainer.setAdapter(sectionAdapter);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//            @Override
//            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                Logger.error("onMove");
//                sectionAdapter.notifyDataSetChanged();
//                return false;
//            }
//
//            @Override
//            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
//                // Remove item from backing list here
//                Logger.error("onSwiped" + swipeDir);
//                try {
//                    NotificationModel notificationModel = (NotificationModel) viewHolder.itemView.getTag();
//                    notificationModel.getID()
//
//                }catch (Exception e){
//
//                }
//            }
//        });
//
//        itemTouchHelper.attachToRecyclerView(rcContainer);
    }

    @Override
    public void onResume(){
        super.onResume();
        setAppTitle(getResources().getString(R.string.title_notifications));

        showNavHome(false);

        loadingProgress.setVisibility(View.VISIBLE);
        loadingProgress.startSpinning();
        getNotifications();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        _instance = null;
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

    @Override
    public void onRefresh() {
        sectionAdapter.removeAllSections();
        sectionAdapter.notifyDataSetChanged();
        getNotifications();
    }

    private void getNotifications() {
        HttpUtil httpUtil = new HttpUtil();
        User user  = UserManager.with(this).getCurrentUser();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());
        RequestParams requestParams = new RequestParams();
        requestParams.put("Length", "999");
        requestParams.put("Start", "0");
        httpUtil.post(Constant.GET_MY_NOTIFICATIONS_API, requestParams,  new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String paramString = new String(responseBody);

                    JSONObject jsonObject = new JSONObject(paramString);
                    JSONArray jsonArray = (JSONArray) jsonObject.get("Data");
                    final ArrayList<NotificationModel> notificationModels = new ArrayList<>();
                    for (int j = 0; j < jsonArray.length(); j++) {
                        NotificationModel notificationModel = BaseApplication.getGson().fromJson(jsonArray.get(j).toString(), NotificationModel.class);
                        notificationModels.add(notificationModel);
                    }

                    ArrayList<NotificationModels> notifications =  Misc.getSortedNotifications(notificationModels);
                    for(NotificationModels models:notifications){
                        NotificationSection notificationSection = new NotificationSection(ActivityNotification.this, models);
                        if(models.size()>0)
                            sectionAdapter.addSection(Misc.getDateStringFromDate(models.get(0).getNotification().getDate()),notificationSection);
                    }
                    sectionAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Misc.showResponseMessage(responseBody);
            }

            @Override
            public void onFinish() {
                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
                loadingProgress.setVisibility(View.GONE);
                loadingProgress.stopSpinning();
            }
        });
    }

    public static void resetNotifications() {
        try {
            if (_instance != null) {
                HashMap<String, Section> sectionMap = _instance.sectionAdapter.getSectionsMap();
                for (String key : sectionMap.keySet()) {
                    Section section = sectionMap.get(key);
                    if (section.getContentItemsTotal() == 0) {
                        _instance.sectionAdapter.removeSection(key);
                    }
                }
                _instance.sectionAdapter.notifyDataSetChanged();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}
