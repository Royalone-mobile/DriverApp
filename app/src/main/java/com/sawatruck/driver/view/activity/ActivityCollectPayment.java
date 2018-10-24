package com.sawatruck.driver.view.activity;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.User;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.Logger;
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

public class ActivityCollectPayment extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.txt_distance) CustomTextView txtDistance;
    @Bind(R.id.txt_load_location) CustomTextView txtLoadLocation;
    @Bind(R.id.txt_delivery_location) CustomTextView txtDeliveryLocation;
    @Bind(R.id.txt_current_time) CustomTextView txtCurrentTime;
    @Bind(R.id.txt_price) CustomTextView txtPrice;

    @Bind(R.id.layout_receive_payment) View layoutReceivePayment;
    @Bind(R.id.layout_decline_payment) View layoutDeclinePayment;
    @Bind(R.id.radio_receive_payment)  RadioButton radioReceivePayment;
    @Bind(R.id.radio_decline_payment)  RadioButton radioDeclinePayment;

    @Bind(R.id.btn_collect_payment) View btnCollectPayment;
    @Bind(R.id.btn_submit_complain) View btnSubmitComplain;

    @Bind(R.id.edit_complain) CustomEditText editComplain;

    @Bind(R.id.txt_payment_type) CustomTextView txtPaymentType;
    @Bind(R.id.txt_net_price) CustomTextView txtNetPrice;
    @Bind(R.id.txt_discount) CustomTextView txtDiscount;

    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_collect_payment,null);
        ButterKnife.bind(this, view);

        BaseApplication.getHandler().post(timerRunnable);

        radioReceivePayment.setOnClickListener(this);
        radioDeclinePayment.setOnClickListener(this);
        btnCollectPayment.setOnClickListener(this);
        btnSubmitComplain.setOnClickListener(this);

        initView();
        return view;
    }

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            txtCurrentTime.setText(Misc.getUTCDatetimeAsStringWithPM());
            BaseApplication.getHandler().postDelayed(this,1000);
        }
    };

    @Override
    public void onResume(){
        super.onResume();
        setAppTitle(getString(R.string.title_your_travel));
        showNavHome(false);
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

                    String strPrice = responseObject.getString("CurrencySymbol").concat(" ").concat(responseObject.getString("Price"));
                    String strNetPrice = responseObject.getString("CurrencySymbol").concat(" ").concat(responseObject.getString("NetPrice"));
                    String strDiscount = responseObject.getString("CurrencySymbol").concat(" ").concat(responseObject.getString("discount"));
                    txtPrice.setText(strPrice);
                    txtNetPrice.setText(strNetPrice);
                    txtDiscount.setText(strDiscount);

                    String distance = responseObject.getString("TravelDistance");
                    txtDistance.setText(distance.concat(" km"));
                    boolean isCash = responseObject.getBoolean("IsCash");

                    if(isCash) {
                        txtPaymentType.setText(getString(R.string.cash));
                    }
                    else
                        txtPaymentType.setText(getString(R.string.card));

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
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_collect_payment:
                collectPayment();
                break;
            case R.id.radio_receive_payment:
                layoutReceivePayment.setVisibility(View.VISIBLE);
                layoutDeclinePayment.setVisibility(View.GONE);
                
                btnCollectPayment.setVisibility(View.VISIBLE);
                btnSubmitComplain.setVisibility(View.GONE);
                break;
            case R.id.radio_decline_payment:
                layoutReceivePayment.setVisibility(View.GONE);
                layoutDeclinePayment.setVisibility(View.VISIBLE);
                btnSubmitComplain.setVisibility(View.VISIBLE);
                btnCollectPayment.setVisibility(View.GONE);
                break;
            case R.id.btn_submit_complain:
                submitComplain();
                break;
        }
    }

    private void submitComplain() {
        String note = editComplain.getText().toString();

        if(note.length() == 0){
            editComplain.setError(getString(R.string.error_enter_complain));
            editComplain.requestFocus();
            return;
        }

        HttpUtil httpUtil = HttpUtil.getInstance();
        User user  = UserManager.with(context).getCurrentUser();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());
        RequestParams requestParams = new RequestParams();

        requestParams.put("note", note);
        requestParams.put("TravelID", getIntent().getStringExtra(Constant.INTENT_TRAVEL_ID));

        btnSubmitComplain.setEnabled(false);

        httpUtil.post(Constant.POST_COMPLAIN_API, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Misc.showResponseMessage(responseBody);
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Misc.showResponseMessage(responseBody);
            }

            @Override
            public void onFinish(){
                btnSubmitComplain.setEnabled(true);
            }
        });
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


    private void collectPayment(){
        HttpUtil httpUtil = HttpUtil.getInstance();
        User user  = UserManager.with(context).getCurrentUser();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());

        RequestParams requestParams = new RequestParams();
        requestParams.put("travelID", getIntent().getStringExtra(Constant.INTENT_TRAVEL_ID));

        Logger.error(requestParams.toString());
        btnCollectPayment.setEnabled(false);

        String url =Constant.COLLECT_PAYMENT_API +"?travelID=" + getIntent().getStringExtra(Constant.INTENT_TRAVEL_ID);
        Logger.error(url);
        httpUtil.post(url , requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Misc.showResponseMessage(responseBody);
                    Intent intent = new Intent(ActivityCollectPayment.this, ActivityRating.class);
                    intent.putExtra(Constant.INTENT_TRAVEL_ID,  getIntent().getStringExtra(Constant.INTENT_TRAVEL_ID));
                    startActivity(intent);
                    finish();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Misc.showResponseMessage(responseBody);
            }

            @Override
            public void onFinish(){
                btnCollectPayment.setEnabled(true);
            }
        });
    }

}