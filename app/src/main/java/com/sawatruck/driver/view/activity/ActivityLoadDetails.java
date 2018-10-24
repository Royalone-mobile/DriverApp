package com.sawatruck.driver.view.activity;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.Currency;
import com.sawatruck.driver.entities.Load;
import com.sawatruck.driver.entities.LoadPhoto;
import com.sawatruck.driver.entities.User;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.Logger;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.utils.Serializer;
import com.sawatruck.driver.utils.StringUtil;
import com.sawatruck.driver.utils.UserManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;


public class ActivityLoadDetails extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.txt_load_location) TextView txtLoadLocation;
    @Bind(R.id.txt_sender) TextView txtSender;
    @Bind(R.id.txt_load_type) TextView txtLoadType;
    @Bind(R.id.txt_delivery_location) TextView txtDeliveryLocation;
    @Bind(R.id.txt_load_date) TextView txtLoadDate;
    @Bind(R.id.txt_delivery_date) TextView txtDeliveryDate;
    @Bind(R.id.txt_distance) TextView txtDistance;
    @Bind(R.id.txt_load_details) TextView txtLoadDetails;
    @Bind(R.id.img_load_photo) ImageView imgLoadPhoto;
    @Bind(R.id.btn_send_bids) View btnSendBids;
    @Bind(R.id.btn_showmap) Button btnShowMap;


    private Load load;
    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_loaddetails,null);
        ButterKnife.bind(this, view);
        btnSendBids.setOnClickListener(this);
        btnShowMap.setOnClickListener(this);


        String strLoad =  getIntent().getStringExtra("load_info");
        load = Serializer.getInstance().deserializeLoad(strLoad);


        String strLoadID =  getIntent().getStringExtra(Constant.INTENT_LOAD_ID);

        try {
            if (strLoadID.length() != 0) {
                HttpUtil httpUtil = new HttpUtil();
                User user  = UserManager.with(this).getCurrentUser();
                httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());
                httpUtil.get(Constant.GET_LOADS_API + "/" + strLoadID, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String paramString = new String(responseBody);
                        paramString = StringUtil.escapeString(paramString);
                        try {
                            JSONObject jsonObject = new JSONObject(paramString);
                            JSONObject jsonObject1 =  jsonObject.getJSONObject("Object");
                            load = BaseApplication.getGson().fromJson(jsonObject1.toString(), Load.class);
                            initView();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Logger.error("onFailure");
                    }

                });
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        initView();
        return view;
    }

    private void initView(){
        try {
            if (load.getLoadPhotos().size() > 0) {
                LoadPhoto loadPhoto = load.getLoadPhotos().get(0);
                BaseApplication.getPicasso().load(loadPhoto.getPhotoPath()).placeholder(R.drawable.ico_truck).into(imgLoadPhoto);
            }

            txtDeliveryDate.setText(Misc.getTimeZoneDate(load.getUnloadDateEnd()));
            txtLoadDate.setText(Misc.getTimeZoneDate(load.getLoadDateFrom()));
            txtLoadType.setText(load.getLoadType());
            txtSender.setText(load.getUser().getFullName());
            txtLoadLocation.setText(load.getFromLocation().getCityName() + ", " + load.getFromLocation().getCountryName());
            txtDeliveryLocation.setText(load.getToLocation().getCityName() + ", " + load.getToLocation().getCountryName());
            txtDistance.setText(load.getDistance().concat(getString(R.string.kilometer)));
            txtLoadDetails.setText(load.getLoadDetails());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        setAppTitle(getString(R.string.title_load_details));
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

        switch (id){
            case R.id.btn_send_bids:
                sendOffer();
                break;
            case R.id.btn_showmap:
                onShowMap();
                break;
        }
    }

    private void onShowMap(){
        Intent intent = new Intent(this, ActivityMap.class);
        String fromLocation = Serializer.getInstance().serializeLocation(load.getFromLocation());
        String toLocation  = Serializer.getInstance().serializeLocation(load.getToLocation());
        intent.putExtra(Constant.INTENT_FROM_LOCATION, fromLocation);
        intent.putExtra(Constant.INTENT_TO_LOCATION, toLocation);
        startActivity(intent);
    }

    private void sendOffer(){
        int userType = UserManager.with(BaseApplication.getContext()).getUserType();
        if(userType == 0) {
            Intent intent = new Intent(this, ActivitySignIn.class);
            startActivity(intent);
            finish();
        }
        else {
            Intent intent = new Intent(this, ActivitySelectTruck.class);
            intent.putExtra("load", Serializer.getInstance().serializeLoad(load));
            startActivity(intent);
            finish();
        }
    }
}

