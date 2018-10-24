package com.sawatruck.driver.view.activity;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.view.MenuItem;
import android.view.View;

import com.fuzzproductions.ratingbar.RatingBar;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.utils.StringUtil;
import com.sawatruck.driver.utils.UserManager;
import com.sawatruck.driver.view.design.CustomEditText;
import com.sawatruck.driver.view.design.CustomTextView;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by royal on 8/23/2017.
 */


public class ActivityRating extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.rating_rank) RatingBar rateTravelRank;
    @Bind(R.id.edit_comment) CustomEditText editComment;
    @Bind(R.id.btn_rate) View btnRate;
    @Bind(R.id.txt_current_time) CustomTextView txtCurrentTime;
    @Bind(R.id.txt_load_location) CustomTextView txtLoadLocation;
    @Bind(R.id.txt_delivery_location) CustomTextView txtDeliveryLocation;
    @Bind(R.id.txt_fare) CustomTextView txtFare;

    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_rating,null);
        ButterKnife.bind(this, view);

        BaseApplication.getHandler().post(timerRunnable);
        btnRate.setOnClickListener(this);

        initView();
        return view;
    }

    private void initView(){
        HttpUtil httpUtil = HttpUtil.getInstance();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, UserManager.with(context).getCurrentUser().getToken());

        httpUtil.get(Constant.GET_TRAVEL_BY_ID +"/" + getIntent().getStringExtra(Constant.INTENT_TRAVEL_ID), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] responseBody) {
                try {
                    String paramString = new String(responseBody);
                    paramString = StringUtil.escapeString(paramString);
                    JSONObject responseObject= new JSONObject(paramString);
                    JSONObject locationObject = responseObject.getJSONObject("ToLocation");
                    String cityName  = locationObject.getString("CityName");
                    String countryName  = locationObject.getString("Name");
                    txtDeliveryLocation.setText(cityName.concat(",").concat(countryName));
                    locationObject = responseObject.getJSONObject("FromLocation");
                    cityName  = locationObject.getString("CityName");
                    countryName  = locationObject.getString("Name");
                    txtLoadLocation.setText(cityName.concat(",").concat(countryName));

                    String fare = responseObject.getString("CurrencySymbol").concat(" ").concat(responseObject.getString("Price"));
                    txtFare.setText(fare);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] responseBody, Throwable throwable) {
                Misc.showResponseMessage(responseBody);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        setAppTitle(getString(R.string.title_rating));
        showNavHome(false);
    }

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            txtCurrentTime.setText(Misc.getUTCDatetimeAsStringWithPM());
            BaseApplication.getHandler().postDelayed(this,1000);
        }
    };

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_rate:
                rateTravel();
                break;
        }
    }

    private class RateAsyncTask extends AsyncTask<String, Void, Void> {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(String... params) {
            String json = params[0];

            HttpUtil.postBody(Constant.RATE_TRAVEL_API, json, new HttpUtil.ResponseHandler() {
                @Override
                public void onSuccess(final String responseBody) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Misc.showResponseMessage(responseBody);
                            ActivityRating.this.finish();
                        }
                    });
                }

                @Override
                public void onFailure(final String errorString) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Misc.showResponseMessage(errorString);
                        }
                    });
                }

            });
            return null;
        }
    }

    private void rateTravel(){
        JSONObject jsonObject = new JSONObject();
        String strComment = editComment.getText().toString();
        if(strComment.length() == 0) {
            editComment.setError(getResources().getString(R.string.error_enter_comment));
            editComment.requestFocus();
            return;
        }
        float rating = rateTravelRank.getRating();

        try {
            jsonObject.put("TravelID", getIntent().getStringExtra(Constant.INTENT_TRAVEL_ID));
            jsonObject.put("Rank", rating*20);
            jsonObject.put("Message", editComment.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        RateAsyncTask rateAsyncTask = new RateAsyncTask();
        rateAsyncTask.execute(jsonObject.toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BaseApplication.getHandler().removeCallbacks(timerRunnable);
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
}

