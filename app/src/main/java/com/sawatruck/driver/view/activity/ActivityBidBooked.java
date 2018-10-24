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
import com.sawatruck.driver.entities.User;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.Logger;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.utils.Notice;
import com.sawatruck.driver.utils.Serializer;
import com.sawatruck.driver.utils.StringUtil;
import com.sawatruck.driver.utils.UserManager;
import com.sawatruck.driver.view.design.CustomTextView;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;


public class ActivityBidBooked extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.btn_cancel) View btnCancel;
    @Bind(R.id.btn_start) CustomTextView btnStartDeliver;
    @Bind(R.id.txt_offer_name) CustomTextView txtOfferName;
    @Bind(R.id.txt_offer_type) CustomTextView txtOfferType;
    @Bind(R.id.txt_load_location) CustomTextView txtLoadLocation;
    @Bind(R.id.txt_delivery_location) CustomTextView txtDeliveryLocation;
    @Bind(R.id.txt_load_date) CustomTextView txtLoadDate;
    @Bind(R.id.txt_delivery_date) CustomTextView txtDeliveryDate;
    @Bind(R.id.txt_distance) CustomTextView txtDistance;
    @Bind(R.id.img_car_photo) ImageView imgCarPhoto;
    @Bind(R.id.btn_showmap) Button btnShowMap;
    @Bind(R.id.txt_load_details) CustomTextView txtLoadDetails;

    private OfferDetail offer;
    private int TravelStatus = 0;
    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.driver_activity_bid_start, null);
        ButterKnife.bind(this, view);

        btnStartDeliver.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnShowMap.setOnClickListener(this);

        getOfferDetail();

        return view;
    }

    private void getOfferDetail() {
        String offerID = getIntent().getStringExtra(Constant.INTENT_OFFER_ID);
        HttpUtil httpUtil = HttpUtil.getInstance();
        httpUtil.getClient().addHeader("Authorization", UserManager.with(context).getCurrentUser().getToken());
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
                    offer = BaseApplication.getGson().fromJson(offerObject.toString(), OfferDetail.class);

                    btnCancel.setVisibility(View.VISIBLE);
                    initView();


                    int  trackingStatus = Integer.valueOf(offer.getTrackingStatus());
                    if(trackingStatus >= 6) {
                        btnCancel.setVisibility(View.GONE);
                        getTravelStatus(offer);
                    } else
                        btnStartDeliver.setVisibility(View.VISIBLE);

                    if(trackingStatus == 5)
                        btnStartDeliver.setText(getString(R.string.btn_resume));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (responseBody != null)
                    Misc.showResponseMessage( responseBody);
            }
        });
    }

    private void initView() {
        try {
            txtDeliveryDate.setText( Misc.getTimeZoneDate(offer.getLoad().getUnloadDateEnd()));
            txtLoadDate.setText(Misc.getTimeZoneDate(offer.getLoad().getLoadDateFrom()));
            txtLoadLocation.setText(offer.getLoad().getFromLocation().getCityName() + "," + offer.getLoad().getFromLocation().getCountryName());
            txtDeliveryLocation.setText(offer.getLoad().getToLocation().getCityName() + "," + offer.getLoad().getToLocation().getCountryName());
            txtOfferName.setText(offer.getFullName());
            txtDistance.setText(offer.getLoad().getDistance().concat(getString(R.string.kilometer)));
            txtLoadDetails.setText(offer.getLoad().getLoadDetails());


//            BaseApplication.getPicasso().load(offer.getLoad().getLoadType()).placeholder(R.drawable.ico_truck).into(imgCarPhoto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setAppTitle(getString(R.string.title_bid_details));
        showNavHome(false);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_start:
                if (offer != null) {
                    if (offer.getLoad().getTravel() == null) {
                        Notice.show(R.string.travel_id_no_exist);
                        return;
                    }
                } else {
                    Notice.show(R.string.loading_travel);
                }
                startTravel(offer);
                break;
            case R.id.btn_showmap:
                showMap();
                break;
            case R.id.btn_cancel:
                onBackPressed();
                break;
        }
    }

    //TODO TravelType From Ad =>1, From offer => 2
    private void getTravelStatus(final OfferDetail offer) {
        try {
            final String travelID = offer.getLoad().getTravel().getTravelID();
            HttpUtil httpUtil = HttpUtil.getInstance();
            Logger.error("travelID");
            Logger.error(travelID);
            User user  = UserManager.with(BaseApplication.getContext()).getCurrentUser();
            httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());
            httpUtil.get(Constant.GET_TRAVEL_BY_ID + "/" + travelID, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] responseBody) {
                    String paramString = new String(responseBody);
                    paramString = StringUtil.escapeString(paramString);
                    try {
                        JSONObject jsonObject = new JSONObject(paramString);
                        TravelStatus = jsonObject.getInt("Status");

                        if(TravelStatus == 2) {
                            btnStartDeliver.setText(getString(R.string.btn_collect_payment));
                        } else if (TravelStatus == 4) {
                            btnStartDeliver.setText(getString(R.string.btn_rate));
                        }
                        if(TravelStatus<=4)
                            btnStartDeliver.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] errorString, Throwable throwable) {
                    String paramString = new String(errorString);
                    paramString = StringUtil.escapeString(paramString);
                    Logger.error(paramString);
                    btnStartDeliver.setVisibility(View.VISIBLE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO TravelType From Ad =>1, From offer => 2
    private void startTravel(final OfferDetail offer) {
        try {
            final String travelID = offer.getLoad().getTravel().getTravelID();
            int  trackingStatus = Integer.valueOf(offer.getTrackingStatus());
            Intent intent;
            if (trackingStatus < 3) {
                intent = new Intent(context, ActivityPickup.class);
                intent.putExtra(Constant.INTENT_TRAVEL_ID, travelID);
                intent.putExtra(Constant.INTENT_TRACKING_STATUS, trackingStatus);
                startActivity(intent);
            } else if (trackingStatus < 6) {
                intent = new Intent(context, ActivityTravel.class);
                intent.putExtra(Constant.INTENT_TRAVEL_ID, travelID);
                intent.putExtra(Constant.INTENT_TRACKING_STATUS, trackingStatus);
                startActivity(intent);
            }
            else if(trackingStatus == 6) {
                if(TravelStatus == 2) {
                    intent = new Intent(context, ActivityCollectPayment.class);
                    intent.putExtra(Constant.INTENT_TRAVEL_ID, travelID);
                    context.startActivity(intent);

                } else if (TravelStatus == 4) {
                    intent = new Intent(context, ActivityRating.class);
                    intent.putExtra(Constant.INTENT_TRAVEL_ID, travelID);
                    context.startActivity(intent);
                }
            }
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showMap() {
        try {
            Intent intent = new Intent(this, ActivityMap.class);
            String fromLocation = Serializer.getInstance().serializeLocation(offer.getLoad().getFromLocation());
            String toLocation = Serializer.getInstance().serializeLocation(offer.getLoad().getToLocation());
            intent.putExtra(Constant.INTENT_FROM_LOCATION, fromLocation);
            intent.putExtra(Constant.INTENT_TO_LOCATION, toLocation);
            startActivity(intent);
        } catch (Exception e) {
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
