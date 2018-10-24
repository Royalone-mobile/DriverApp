package com.sawatruck.driver.view.activity;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.OfferDetail;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.utils.Serializer;
import com.sawatruck.driver.utils.StringUtil;
import com.sawatruck.driver.utils.UserManager;
import com.sawatruck.driver.view.design.CustomTextView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;


public class ActivityBidAccepted extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.txt_offer_name) CustomTextView txtOfferName;
    @Bind(R.id.txt_offer_type) CustomTextView txtOfferType;
    @Bind(R.id.txt_load_location) CustomTextView txtLoadLocation;
    @Bind(R.id.txt_delivery_location) CustomTextView txtDeliveryLocation;
    @Bind(R.id.txt_load_date) CustomTextView txtLoadDate;
    @Bind(R.id.txt_delivery_date) CustomTextView txtDeliveryDate;
    @Bind(R.id.txt_distance) CustomTextView txtDistance;
    @Bind(R.id.img_car_photo) ImageView imgCarPhoto;
    @Bind(R.id.btn_showmap) Button btnShowMap;
    @Bind(R.id.btn_confirm) View btnConfirm;

    private OfferDetail offer;

    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_bid_accepted,null);
        ButterKnife.bind(this, view);
        btnConfirm.setOnClickListener(this);
        btnShowMap.setOnClickListener(this);
        getOfferDetail();
        return view;
    }


    private void getOfferDetail() {
        String offerID = getIntent().getStringExtra(Constant.INTENT_OFFER_ID);
        HttpUtil httpUtil = HttpUtil.getInstance();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, UserManager.with(context).getCurrentUser().getToken());
        RequestParams params = new RequestParams();
        params.put("id", offerID);

        httpUtil.get(Constant.GET_OFFER_API, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String paramString = new String(responseBody);
                paramString = StringUtil.escapeString(paramString);

                try {
                    JSONObject jsonObject = new JSONObject(paramString);
                    JSONObject offerObject = jsonObject.getJSONObject("Object");
                    offer =  BaseApplication.getGson().fromJson(offerObject.toString(), OfferDetail.class);
                    initView();
                    btnConfirm.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Misc.showResponseMessage(responseBody);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        setAppTitle(getString(R.string.title_bid_details));
        showNavHome(false);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_confirm:
                confirmOffer();
                break;
            case R.id.btn_showmap:
                showMap();
                break;
        }
    }


    private void confirmOffer() {
            HttpUtil httpUtil = HttpUtil.getInstance();
            httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, UserManager.with(context).getCurrentUser().getToken());
            btnConfirm.setEnabled(false);
            httpUtil.put(Constant.CONFIRM_OFFER_API + "/" + offer.getID(), new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        if (responseBody != null)
                            Misc.showResponseMessage(responseBody);
                        finish();

                        btnConfirm.setEnabled(true);
                        if (MainActivity.getInstance() != null)
                            MainActivity.getInstance().onNavItemSelected(1);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    try {
                        Misc.showResponseMessage(responseBody);
                        btnConfirm.setEnabled(true);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
    }

    private void showMap() {
        Intent intent = new Intent(this, ActivityMap.class);
        String fromLocation = Serializer.getInstance().serializeLocation(offer.getLoad().getFromLocation());
        String toLocation  = Serializer.getInstance().serializeLocation(offer.getLoad().getToLocation());
        intent.putExtra(Constant.INTENT_FROM_LOCATION, fromLocation);
        intent.putExtra(Constant.INTENT_TO_LOCATION, toLocation);
        startActivity(intent);
    }

    private void initView(){
        try {
            txtDeliveryDate.setText(offer.getLoad().getUnloadDateEnd());
            txtLoadDate.setText(offer.getLoad().getLoadDateFrom());
            txtLoadLocation.setText(offer.getLoad().getFromLocation().getCityName() + "," + offer.getLoad().getFromLocation().getCountryName());
            txtDeliveryLocation.setText(offer.getLoad().getToLocation().getCityName() + "," + offer.getLoad().getToLocation().getCountryName());
            txtOfferName.setText(offer.getFullName());
            txtDistance.setText(offer.getLoad().getDistance().concat(getString(R.string.kilometer)));
            BaseApplication.getPicasso().load(offer.getCarPhoto()).placeholder(R.drawable.ico_truck).into(imgCarPhoto);
        }catch (Exception e){
            e.printStackTrace();
        }
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
