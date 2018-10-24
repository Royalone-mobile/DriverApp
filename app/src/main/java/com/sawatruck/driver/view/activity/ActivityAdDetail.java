package com.sawatruck.driver.view.activity;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.Advertisement;
import com.sawatruck.driver.entities.User;
import com.sawatruck.driver.utils.ActivityUtil;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.Logger;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.utils.Serializer;
import com.sawatruck.driver.utils.StringUtil;
import com.sawatruck.driver.utils.UserManager;
import com.sawatruck.driver.view.adapter.BookingAdAdapter;
import com.sawatruck.driver.view.design.CustomTextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;


public class ActivityAdDetail extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.txt_truck_type_1) CustomTextView txtTruckType1;
    @Bind(R.id.txt_truck_type_2) CustomTextView txtTruckType2;
    @Bind(R.id.txt_distance) CustomTextView txtDistance;
    @Bind(R.id.txt_budget) CustomTextView txtBudget;
    @Bind(R.id.txt_available_days) CustomTextView txtAvailableDays;
    @Bind(R.id.btn_showmap) View btnShowMap;
    @Bind(R.id.toolbar_edit) View btnEditAd;
    @Bind(R.id.rc_container) RecyclerView rcContainer;

    private Advertisement advertisement = new Advertisement();

    private BookingAdAdapter bookingAdAdapter;


    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_ad_details,null);
        ButterKnife.bind(this, view);
        btnShowMap.setOnClickListener(this);
        btnEditAd.setOnClickListener(this);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        rcContainer.setVerticalScrollBarEnabled(true);
        rcContainer.setLayoutManager(mLayoutManager);
        bookingAdAdapter = new BookingAdAdapter(this);
        rcContainer.setAdapter(bookingAdAdapter);
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

        setAppTitle(getString(R.string.title_ad_details));
        showNavHome(false);
        getAdById();
    }


    private  void getAdById(){
        String strAdID = getIntent().getStringExtra(Constant.INTENT_ADVERTISEMENT_ID);
        HttpUtil httpUtil = HttpUtil.getInstance();
        User user  = UserManager.with(context).getCurrentUser();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());

        Logger.error("Advertisemetn ID");
        Logger.error(strAdID);
        httpUtil.get(Constant.GET_AD_BY_ID_API + "/" + strAdID, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String paramString = new String(responseBody);
                    paramString = StringUtil.escapeString(paramString);

                    advertisement = BaseApplication.getGson().fromJson(paramString, Advertisement.class);
                    txtTruckType1.setText(advertisement.getTruckTypeName1());
                    txtTruckType2.setText(advertisement.getTruckTypeName2());
                    txtBudget.setText(advertisement.getCurrency().concat(" ").concat(advertisement.getBudget()));
                    txtDistance.setText(advertisement.getDistance().concat(getString(R.string.kilometer)));


                    txtAvailableDays.setText(advertisement.getAvailable());

                    bookingAdAdapter.setAdvertisementBookings(advertisement.getBookings());
                    bookingAdAdapter.notifyDataSetChanged();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(responseBody != null)
                    Misc.showResponseMessage(responseBody);
            }
        });
    }

    private void onShowMap(){
        Intent intent = new Intent(this, ActivityMap.class);
        String fromLocation = Serializer.getInstance().serializeLocation(advertisement.getPickupLocation());
        String toLocation  = Serializer.getInstance().serializeLocation(advertisement.getDeliveryLocation());
        intent.putExtra(Constant.INTENT_FROM_LOCATION, fromLocation);
        intent.putExtra(Constant.INTENT_TO_LOCATION, toLocation);
        startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_showmap:
                onShowMap();
                break;
            case R.id.toolbar_edit:
                Intent intent = new Intent(context, ActivityEditAdSelectTruck.class);
                intent.putExtra(Constant.INTENT_ADVERTISEMENT_ID,advertisement.getID());
                intent.putExtra("advertisement", Serializer.getInstance().serializeAdvertisement(advertisement));
                ActivityUtil.goOtherActivityFlipTransition(context,intent);
                finish();
                break;
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
