package com.sawatruck.driver.view.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.ClientDues;
import com.sawatruck.driver.entities.User;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.utils.NetUtil;
import com.sawatruck.driver.utils.Notice;
import com.sawatruck.driver.utils.StringUtil;
import com.sawatruck.driver.utils.UserManager;
import com.sawatruck.driver.view.adapter.ClientDuesAdapter;
import com.todddavies.components.progressbar.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by royal on 9/2/2017.
 */

public class ActivityClientDues extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    @Bind(R.id.rc_container) RecyclerView rcContainer;
    @Bind(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.progressbar_loading) ProgressWheel loadingProgress;

    private ClientDuesAdapter clientDuesAdapter;

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
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        showNavHome(false);
        setAppTitle(getString(R.string.title_client_dues));
        loadingProgress.setVisibility(View.VISIBLE);
        loadingProgress.startSpinning();
        clientDuesAdapter.initializeAdapter();
        getClientDues();

    }

    private void initView() {
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        rcContainer.setVerticalScrollBarEnabled(true);
        rcContainer.setLayoutManager(mLayoutManager);
        clientDuesAdapter = new ClientDuesAdapter(this);
        rcContainer.setAdapter(clientDuesAdapter);
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

    private void getClientDues(){
        HttpUtil httpUtil = new HttpUtil();
        User user  = UserManager.with(context).getCurrentUser();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());
        httpUtil.post(Constant.GET_CLIENT_DUES_API, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String paramString = new String(responseBody);
                paramString = StringUtil.escapeString(paramString);


                ArrayList<ClientDues> list = new ArrayList<>();
                try {
                    JSONObject jsonObject = new JSONObject(paramString);
                    JSONArray jsonArray = jsonObject.getJSONArray("Object");
                    for(int j=0; j<jsonArray.length(); j++) {
                        ClientDues clientDues = BaseApplication.getGson().fromJson(jsonArray.get(j).toString(), ClientDues.class);
                        list.add(clientDues);
                    }

                    clientDuesAdapter.setClientDues(list);
                    clientDuesAdapter.notifyDataSetChanged();


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
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        clientDuesAdapter.initializeAdapter();
        getClientDues();
    }

}
