package com.sawatruck.driver.view.fragments;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.Transaction;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.Logger;
import com.sawatruck.driver.utils.StringUtil;
import com.sawatruck.driver.view.adapter.TransactionHistoryAdapter;
import com.todddavies.components.progressbar.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by royal on 8/19/2017.
 */


public class FragmentOutTransaction extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String ARG_POSITION = "position";
    @Bind(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.rc_container) RecyclerView rcContainer;
    TransactionHistoryAdapter transactionAdapter;

    @Bind(R.id.progressbar_loading) ProgressWheel loadingProgress;
    public static FragmentOutTransaction getInstance(int position) {
        FragmentOutTransaction f = new FragmentOutTransaction();
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
        transactionAdapter = new TransactionHistoryAdapter(getContext());
        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        rcContainer.setAdapter(transactionAdapter);
    }
    @Override
    public void onResume() {
        super.onResume();

        loadingProgress.setVisibility(View.VISIBLE);
        loadingProgress.startSpinning();
        transactionAdapter.initializeAdapter();
        try {
            getAllTransaction();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class TransactionAsyncTask extends AsyncTask<String, Void, Void> {

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(String... params) {
            String json = params[0];

            HttpUtil.postBody(Constant.TRANSACTION_HISTORY_API, json, new HttpUtil.ResponseHandler() {
                @Override
                public void onSuccess(String responseBody) {
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        JSONArray jsonArray = (JSONArray) jsonObject.get("Data");
                        final ArrayList<Transaction> transactions = new ArrayList<>();
                        for (int j = 0; j < jsonArray.length(); j++) {
                            Transaction transaction = BaseApplication.getGson().fromJson(jsonArray.get(j).toString(), Transaction.class);
                            Transaction.TransactionDetail transactionDetail = transaction.getTransactionDetail().get(0);
                            if(transactionDetail.getValue()<0)
                                transactions.add(transaction);
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                transactionAdapter.setTransactionList(transactions);
                                transactionAdapter.notifyDataSetChanged();

                                if(swipeRefreshLayout.isRefreshing())
                                    swipeRefreshLayout.setRefreshing(false);
                                loadingProgress.setVisibility(View.GONE);
                                loadingProgress.stopSpinning();
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(String errorString) {
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if(swipeRefreshLayout.isRefreshing())
                                swipeRefreshLayout.setRefreshing(false);
                            loadingProgress.setVisibility(View.GONE);
                            loadingProgress.stopSpinning();
                        }
                    });
                }
            });
            return null;
        }
    }
    public void getAllTransaction() throws JSONException {
        JSONObject rootObject = new JSONObject();

        JSONObject searchObject = new JSONObject();
        searchObject.put("Value", "");
        searchObject.put("Regex", false);
        rootObject.put("Length", 20);
        rootObject.put("Start", 0);
        rootObject.put("Search", searchObject);

        JSONObject dateObject = new JSONObject();
        dateObject.put("Name","Date");
        dateObject.put("Data","");
        dateObject.put("Orderable",true);
        dateObject.put("Searchable",false);

        JSONObject searchObject1 = new JSONObject();
        searchObject1.put("Value", "");
        searchObject1.put("Regex", false);

        dateObject.put("Search", searchObject1);

        JSONObject transactionTypeObject = new JSONObject();
        transactionTypeObject.put("Name","TransactionType");
        transactionTypeObject.put("Data","");
        transactionTypeObject.put("Orderable",false);
        transactionTypeObject.put("Searchable",true);

        JSONObject searchObject2 = new JSONObject();
        searchObject2.put("Value", "");
        searchObject2.put("Regex", false);

        transactionTypeObject.put("Search", searchObject2);

        JSONObject paymentMethodTypeObject = new JSONObject();
        paymentMethodTypeObject.put("Name","PaymentMethodType");
        paymentMethodTypeObject.put("Data","");
        paymentMethodTypeObject.put("Orderable",false);
        paymentMethodTypeObject.put("Searchable",true);

        JSONObject searchObject3 = new JSONObject();
        searchObject3.put("Value", "");
        searchObject3.put("Regex", false);


        paymentMethodTypeObject.put("Search", searchObject3);

        JSONArray columnArray = new JSONArray();
        columnArray.put(dateObject);
        columnArray.put(transactionTypeObject);
        columnArray.put(paymentMethodTypeObject);

        JSONArray orderArray = new JSONArray();
        JSONObject orderObject = new JSONObject();
        orderArray.put(orderObject);
        orderObject.put("Column", 0);
        orderObject.put("Dir", "Asc");


        rootObject.put("Columns", columnArray);
        rootObject.put("Order", orderArray);


        String json = StringUtil.escapeString(rootObject.toString());

        TransactionAsyncTask transactionAsyncTask = new TransactionAsyncTask();
        transactionAsyncTask.execute(json);
    }

    @Override
    public void onRefresh() {
        transactionAdapter.initializeAdapter();
        try {
            getAllTransaction();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
