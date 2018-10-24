package com.sawatruck.driver.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.GetToDo;
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
import com.sawatruck.driver.view.adapter.ToDoAdapter;
import com.sawatruck.driver.view.adapter.TruckAdapter;
import com.todddavies.components.progressbar.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by royal on 8/22/2017.
 */

public class FragmentMenuToDo extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String ARG_POSITION = "position";
    public static final String TAG = FragmentMenuToDo.class.getSimpleName();

    @Bind(R.id.rc_container) RecyclerView rcContainer;
    @Bind(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.progressbar_loading) ProgressWheel loadingProgress;

    ToDoAdapter toDoAdapter;

    public static FragmentMenuToDo getInstance(int position) {
        FragmentMenuToDo f = new FragmentMenuToDo();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo, container, false);
        ButterKnife.bind(this, view);
        initView();
        BaseActivity baseActivity = (BaseActivity) getActivity();
        baseActivity.setAppTitle(getResources().getString(R.string.sidemenu_todo));
        baseActivity.showOptions(false);

        return view;
    }

    public void initView() {
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        rcContainer.setVerticalScrollBarEnabled(true);
        rcContainer.setLayoutManager(mLayoutManager);
        toDoAdapter = new ToDoAdapter(getContext());
        rcContainer.setAdapter(toDoAdapter);

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
        toDoAdapter.initializeAdapter();
        fetchGetToDo();
    }

    private void fetchGetToDo() {
        HttpUtil httpUtil = HttpUtil.getInstance();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, UserManager.with(BaseApplication.getContext()).getCurrentUser().getToken());

        httpUtil.get(Constant.GET_TO_DO_API, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String paramString = new String(responseBody);
                    paramString = StringUtil.escapeString(paramString);
                    JSONArray jsonArray= new JSONArray(paramString);
                    ArrayList<GetToDo> todoList = new ArrayList<>();
                    for(int i = 0 ; i<jsonArray.length(); i ++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        GetToDo getToDo = BaseApplication.getGson().fromJson(jsonObject.toString(), GetToDo.class);
                        todoList.add(getToDo);
                    }
                    toDoAdapter.setTodos(todoList);
                    toDoAdapter.notifyDataSetChanged();
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
        toDoAdapter.initializeAdapter();
        fetchGetToDo();
    }
}

