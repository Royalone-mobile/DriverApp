package com.sawatruck.driver.view.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.view.MenuItem;
import android.view.View;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.Customer;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.Logger;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.utils.StringUtil;
import com.sawatruck.driver.utils.UserManager;
import com.sawatruck.driver.view.design.CustomTextView;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by royal on 8/17/2017.
 */

public class ActivityDeliveryDetails extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.txt_sender_name) CustomTextView txtSenderName;
    @Bind(R.id.txt_sender_phone) CustomTextView txtSenderPhone;
    @Bind(R.id.txt_recipient_name) CustomTextView txtRecipientName;
    @Bind(R.id.txt_recipient_phone) CustomTextView txtRecipientPhone;
    @Bind(R.id.btn_call_sender) CustomTextView btnCallSender;
    @Bind(R.id.btn_message_sender) CustomTextView btnMessageSender;
    @Bind(R.id.btn_call_recipient) CustomTextView btnCallRecipient;
    @Bind(R.id.btn_message_recipient) CustomTextView btnMessageRecipient;
    @Bind(R.id.txt_delivery_location) CustomTextView txtDeliveryLocation;
    @Bind(R.id.txt_pickup_location) CustomTextView txtPickupLocation;
    @Bind(R.id.txt_delivery_details) CustomTextView txtDeliveryDetails;
    @Bind(R.id.txt_delivery_date) CustomTextView txtDeliveryDate;
    @Bind(R.id.txt_pickup_date) CustomTextView txtPickupDate;
    @Bind(R.id.txt_confirmation_code) CustomTextView txtConfirmationCode;

    private Customer loader;
    private String RecipientId, RecipientName, RecipientPhoneNumber;

    String _confirmationCode = "";

    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.driver_activity_deliverydetails, null);
        ButterKnife.bind(this, view);

        btnCallSender.setOnClickListener(this);
        btnMessageSender.setOnClickListener(this);
        btnCallRecipient.setOnClickListener(this);
        btnMessageRecipient.setOnClickListener(this);

        //Get rEcipeint id is null, hide message recipient button
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

                    Logger.error("DeliveryDetails = " + responseObject.toString());
                    JSONObject locationObject = responseObject.getJSONObject("ToLocation");
                    String cityName  = locationObject.getString("CityName");
                    String countryName  = locationObject.getString("Name");

                    txtDeliveryLocation.setText(cityName.concat(",").concat(countryName));
                    _confirmationCode  = responseObject.getString("ConfirmationCode");

                    locationObject = responseObject.getJSONObject("FromLocation");
                    cityName  = locationObject.getString("CityName");
                    countryName  = locationObject.getString("Name");
                    txtPickupLocation.setText(cityName.concat(",").concat(countryName));
                    JSONObject loaderObject =  responseObject.getJSONObject("Loader");
                    loader = BaseApplication.getGson().fromJson(loaderObject.toString(), Customer.class);

                    RecipientId = responseObject.getString("RecipientId");
                    RecipientName = responseObject.getString("RecipientName");
                    RecipientPhoneNumber = responseObject.getString("RecipientPhoneNumber");

                    txtSenderName.setText(loader.getFullName());
                    txtSenderPhone.setText(loader.getPhoneNumber());

                    txtRecipientName.setText(RecipientName);
                    txtRecipientPhone.setText(RecipientPhoneNumber);

//                    txtConfirmationCode.setText(_confirmationCode);


                    if(RecipientId == null || RecipientId.equals(""))
                        btnMessageRecipient.setVisibility(View.GONE);

                    JSONObject loadObject = responseObject.getJSONObject("Load");
                    String pickupDate  = "";
                    String deliveryDate = "";
                    if(loadObject.has("PickupDate")) {
                        pickupDate = loadObject.getString("PickupDate");
                    }

                    if(loadObject.has("DeliveryDate")) {
                        deliveryDate = loadObject.getString("DeliveryDate");
                    }

                    if(loadObject.has("FromLocation")) {
                        pickupDate = loadObject.getJSONObject("FromLocation").getString("Date");
                    }

                    if(loadObject.has("ToLocation")) {
                        deliveryDate = loadObject.getJSONObject("ToLocation").getString("Date");
                    }

                    txtPickupDate.setText( Misc.getTimeZoneDate(pickupDate));
                    txtDeliveryDate.setText( Misc.getTimeZoneDate(deliveryDate));
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
    public void onResume() {
        super.onResume();
        setAppTitle(getString(R.string.title_delivery_details));
        showNavHome(false);
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
    public void onClick(View v) {
        int id = v.getId();
        Intent intent = new Intent();
        try {
            switch (id) {
                case R.id.btn_call_sender:
                    callPerson(loader.getPhoneNumber());
                    break;
                case R.id.btn_message_sender:
                    intent = new Intent(this, ActivityMessage.class);
                    intent.putExtra(Constant.INTENT_USER_ID, loader.getUserID());
                    intent.putExtra(Constant.INTENT_USERNAME, loader.getUserName());
                    break;
                case R.id.btn_call_recipient:
                    callPerson(RecipientPhoneNumber);
                    break;
                case R.id.btn_message_recipient:
                    intent = new Intent(this, ActivityMessage.class);
                    intent.putExtra(Constant.INTENT_USER_ID, RecipientId);
                    intent.putExtra(Constant.INTENT_USERNAME, RecipientName);
                    break;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        startActivity(intent);
    }

    public void callPerson(String phoneNumber){
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(callIntent);
    }
}
