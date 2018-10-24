package com.sawatruck.driver.view.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.Inbox;
import com.sawatruck.driver.entities.User;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.Logger;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.utils.StringUtil;
import com.sawatruck.driver.utils.UserManager;
import com.sawatruck.driver.view.activity.BaseActivity;
import com.sawatruck.driver.view.adapter.InboxAdapter;
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


public class FragmentMenuInBox extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String ARG_POSITION = "position";
    public static final String TAG = FragmentMenuInBox.class.getSimpleName();

    @Bind(R.id.list_inbox) ListView listInbox;
    @Bind(R.id.progressbar_loading) ProgressWheel loadingProgress;
    @Bind(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;

    InboxAdapter inboxAdapter;
    public static FragmentMenuInBox getInstance(int position) {
        FragmentMenuInBox f = new FragmentMenuInBox();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);
        ButterKnife.bind(this, view);
        initView();
        BaseActivity baseActivity = (BaseActivity) getActivity();
        baseActivity.setAppTitle(getResources().getString(R.string.sidemenu_inbox));
        baseActivity.showOptions(false);


        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        inboxAdapter = new InboxAdapter(getContext());
        listInbox.setAdapter(inboxAdapter);

        return view;
    }

    public void initView() {
        inboxAdapter = new InboxAdapter(getContext());
        listInbox.setAdapter(inboxAdapter);
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
        inboxAdapter.initializeAdapter();
        try {
            getInboxMessages();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getInboxMessages() throws Exception {
        HttpUtil httpUtil = HttpUtil.getInstance();
        User user  = UserManager.with(getContext()).getCurrentUser();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());
        httpUtil.post(Constant.GET_USER_INBOX_API, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String paramString = new String(responseBody);
                paramString = StringUtil.escapeString(paramString);

                ArrayList<Inbox> inboxList = new ArrayList<>();
                try {
                    JSONArray jsonArray= new JSONArray(paramString);
                    for(int j=0; j<jsonArray.length(); j++) {
                        Inbox inbox = BaseApplication.getGson().fromJson(jsonArray.get(j).toString(), Inbox.class);
                        inboxList.add(inbox);
                    }

                    inboxAdapter.setInboxList(inboxList);
                    inboxAdapter.notifyDataSetChanged();


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
                    if (swipeRefreshLayout.isRefreshing())
                        swipeRefreshLayout.setRefreshing(false);
                    loadingProgress.setVisibility(View.GONE);
                    loadingProgress.stopSpinning();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        inboxAdapter.initializeAdapter();
        try {
            getInboxMessages();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}
