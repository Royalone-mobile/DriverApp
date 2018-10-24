package com.sawatruck.driver.view.fragments;

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
import com.sawatruck.driver.entities.Rating;
import com.sawatruck.driver.entities.User;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.utils.StringUtil;
import com.sawatruck.driver.utils.UserManager;
import com.sawatruck.driver.view.adapter.RatingAdapter;
import com.todddavies.components.progressbar.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by royal on 8/19/2017.
 */

public class FragmentRatingReceived extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String ARG_POSITION = "position";
    @Bind(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.rc_container) RecyclerView rcContainer;
    RatingAdapter ratingAdapter;
    @Bind(R.id.progressbar_loading)
    ProgressWheel loadingProgress;
    public static FragmentRatingReceived getInstance(int position) {
        FragmentRatingReceived f = new FragmentRatingReceived();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;

    }

    public void initView(){
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        rcContainer.setVerticalScrollBarEnabled(true);
        rcContainer.setLayoutManager(mLayoutManager);
        ratingAdapter = new RatingAdapter(getContext());
        rcContainer.setAdapter(ratingAdapter);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        ButterKnife.bind(this,view);
        initView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadingProgress.setVisibility(View.VISIBLE);
        loadingProgress.startSpinning();
        ratingAdapter.initializeAdapter();
        getMyReceivedRating();
    }

    @Override
    public void onRefresh() {
        ratingAdapter.initializeAdapter();
        getMyReceivedRating();
    }


    private void getMyReceivedRating() {
        HttpUtil httpUtil = HttpUtil.getInstance();
        User user  = UserManager.with(getContext()).getCurrentUser();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());
        httpUtil.get(Constant.GET_USER_RATING_API, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String paramString = new String(responseBody);
                paramString = StringUtil.escapeString(paramString);
                ArrayList<Rating> ratingList = new ArrayList<>();
                try {
                    JSONObject jsonObject = new JSONObject(paramString);
                    JSONObject jsonObject1 = (JSONObject) jsonObject.get("Object");
                    JSONArray jsonArray = jsonObject1.getJSONArray("Rater");
                    for(int j =0 ; j<jsonArray.length(); j++) {
                        JSONObject ratingObject = (JSONObject) jsonArray.get(j);
                        Rating rating = BaseApplication.getGson().fromJson(ratingObject.toString(), Rating.class);
                        ratingList.add(rating);
                    }
                    ratingAdapter.setRatingList(ratingList);
                    ratingAdapter.notifyDataSetChanged();

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
}