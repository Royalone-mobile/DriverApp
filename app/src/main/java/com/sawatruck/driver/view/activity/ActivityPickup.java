package com.sawatruck.driver.view.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.controller.LocationUpdateService;
import com.sawatruck.driver.entities.CancelReason;
import com.sawatruck.driver.entities.User;
import com.sawatruck.driver.utils.AppSettings;
import com.sawatruck.driver.utils.GoogleMapPath;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.Logger;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.utils.StringUtil;
import com.sawatruck.driver.utils.UserManager;
import com.sawatruck.driver.view.design.CustomTextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by royal on 8/23/2017.
 */


public class ActivityPickup extends BaseActivity implements View.OnClickListener, OnMapReadyCallback {
    @Bind(R.id.btn_delivery_details) View btnDeliveryDetails;
    @Bind(R.id.btn_cancel_delivery) View btnCancelDelivery;
    @Bind(R.id.btn_pickup_load) CustomTextView btnPickupLoad;
    @Bind(R.id.btn_pickup_arrive) CustomTextView btnPickupArrive;
    @Bind(R.id.txt_load_location) CustomTextView txtLoadLocation;
    @Bind(R.id.btn_navigate) View btnNavigate;

    private ArrayList<CancelReason> cancelReasons = new ArrayList<>();
    private SupportMapFragment supportMapFragment;
    private GoogleMap _googleMap;
    private int ZOOM = 15;

    private Marker prevMarker;
    private LatLng _loadLatLng;

    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.driver_activity_pickupload,null);
        ButterKnife.bind(this, view);

        btnDeliveryDetails.setOnClickListener(this);
        btnCancelDelivery.setOnClickListener(this);
        btnPickupLoad.setOnClickListener(this);
        btnNavigate.setOnClickListener(this);
        btnNavigate.setTag(false);

        btnPickupArrive.setOnClickListener(this);

        btnPickupLoad.setEnabled(false);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);


        getTravel(getIntent().getStringExtra(Constant.INTENT_TRAVEL_ID));

        if(getIntent().getIntExtra(Constant.INTENT_TRACKING_STATUS,0) != 3 ) {
            if (LocationUpdateService.isActive) {
                if (LocationUpdateService.getTravelID().equals(getIntent().getStringExtra(Constant.INTENT_TRAVEL_ID))) {
                    btnPickupLoad.setVisibility(View.GONE);
                    btnPickupArrive.setVisibility(View.VISIBLE);
                } else
                    btnPickupLoad.setText(getString(R.string.pickup));
            } else {
                btnPickupLoad.setText(getString(R.string.pickup));
            }
        }
        return view;
    }


    private void getTravel(final String travelID){
        HttpUtil httpUtil = HttpUtil.getInstance();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, UserManager.with(context).getCurrentUser().getToken());

        httpUtil.get(Constant.GET_TRAVEL_BY_ID +"/" + travelID, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] responseBody) {
                try {
                    String paramString = new String(responseBody);
                    paramString = StringUtil.escapeString(paramString);
                    JSONObject responseObject= new JSONObject(paramString);
                    JSONObject locationObject = responseObject.getJSONObject("FromLocation");
                    String cityName  = locationObject.getString("CityName");
                    String countryName  = locationObject.getString("Name");
                    txtLoadLocation.setText(cityName.concat(",").concat(countryName));

                    Double latitude = locationObject.getDouble("Latitude");
                    Double longitude = locationObject.getDouble("Longitude");

                    _loadLatLng = new LatLng(latitude, longitude);

                    btnPickupLoad.setEnabled(true);

                    onMapReady(_googleMap);

                    String trackingNumber = responseObject.getString("TrackingNumber");
                    setAppTitle(getString(R.string.title_pickup_load) + " (" + trackingNumber + ")");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] errorString, Throwable throwable) {
                Logger.error("getTravel onFailure");
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        setAppTitle(getString(R.string.title_pickup_load));
        registerReceiver(locationReceiver, new IntentFilter("onLocationUpdate"));
        showNavHome(false);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent;
        switch (id){
            case R.id.btn_cancel_delivery:
                getCancelReasons();
                break;
            case R.id.btn_delivery_details:
                try {
                    intent = new Intent(this, ActivityDeliveryDetails.class);
                    intent.putExtra(Constant.INTENT_TRAVEL_ID, getIntent().getStringExtra(Constant.INTENT_TRAVEL_ID));
                    startActivity(intent);
                }catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.btn_pickup_load:
                if(btnPickupLoad.getText().equals(getString(R.string.resume_pickup))) {
                    startOrResumeTravel();
                }
                else {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        String travelID = getIntent().getStringExtra(Constant.INTENT_TRAVEL_ID);
                        jsonObject.put("TravelID", travelID);
                        int trackingStatus = getIntent().getIntExtra("trackingStatus",0);

                        if(trackingStatus == 1 || trackingStatus == 2) {
                            startOrResumeTravel();
                            return;
                        }

                        Logger.error(AppSettings.with(this).getCurrentLat() + "," + AppSettings.with(this).getCurrentLng());
                        jsonObject.put("Lat", AppSettings.with(this).getCurrentLat().toString());
                        jsonObject.put("long", AppSettings.with(this).getCurrentLng().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    StartPickupAsyncTask startPickupAsyncTask = new StartPickupAsyncTask();
                    startPickupAsyncTask.execute(jsonObject.toString());

                    btnPickupLoad.setEnabled(false);
                }
                break;
            case R.id.btn_navigate:
                navigateLocation();
                break;
            case R.id.btn_pickup_arrive:
                alertConfirmArrival();
                break;
        }
    }

    private void navigateLocation(){
        LatLng currentLatLng = new LatLng(AppSettings.with(this).getCurrentLat(), AppSettings.with(this).getCurrentLng());


        try {
            if((boolean)btnNavigate.getTag() == false) {
                _googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, ZOOM));
                btnNavigate.setTag(true);
            } else {
                _googleMap.clear();
                onMapReady(_googleMap);
                btnNavigate.setTag(false);
                updateMap();
            }

        }catch(Exception e) {
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

    private void getCancelReasons(){
        HttpUtil httpUtil = HttpUtil.getInstance();
        User user  = UserManager.with(context).getCurrentUser();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());
        httpUtil.get(Constant.CANCEL_REASON_API, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String paramString = new String(responseBody);
                paramString = StringUtil.escapeString(paramString);

                try {
                    JSONArray jsonArray = new JSONArray(paramString);
                    for(int j=0; j<jsonArray.length(); j++) {
                        CancelReason cancelReason = BaseApplication.getGson().fromJson(jsonArray.get(j).toString(), CancelReason.class);
                        cancelReasons.add(cancelReason);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                alertCancelDelivery();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Misc.showResponseMessage(responseBody);
            }
        });
    }

    private void alertCancelDelivery(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final View viewCancelBooking = getLayoutInflater().inflate(R.layout.dialog_cancel_delivery,null);

        final RadioGroup radioGroup = (RadioGroup)viewCancelBooking.findViewById(R.id.radioGroup);

        for(CancelReason cancelReason:cancelReasons){
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(cancelReason.getName());
            radioGroup.addView(radioButton);
        }

        try{
            RadioButton radioButton =(RadioButton) radioGroup.getChildAt(0);
            radioButton.setChecked(true);
        }catch (Exception e){
            e.printStackTrace();
        }

        final Button btnOk = (Button)viewCancelBooking.findViewById(R.id.btn_ok);
        final Button btnCancel = (Button)viewCancelBooking.findViewById(R.id.btn_cancel);

        builder.setView(viewCancelBooking);

        final AlertDialog alertDialog = builder.create();

        alertDialog.show();
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancelDeliveryAsyncTask cancelDeliveryAsyncTask = new CancelDeliveryAsyncTask();
                cancelDeliveryAsyncTask.execute(getIntent().getStringExtra(Constant.INTENT_TRAVEL_ID));
            }
        });
    }

    private class CancelDeliveryAsyncTask extends AsyncTask<String, Void, Void> {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(String... params) {
            String travelID = params[0];
            String json = params[1];

            HttpUtil.putBody(Constant.CANCEL_DELIVERY_API + "/"+ travelID , json, new HttpUtil.ResponseHandler() {
                @Override
                public void onSuccess(final String responseBody) {
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Misc.showResponseMessage(responseBody);
                                ActivityPickup.this.finish();
                                MainActivity.applyAddAd();

                            }
                        });
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
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


    private class StartPickupAsyncTask extends AsyncTask<String, Void, Void> {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(String... params) {
            String json = params[0];

            HttpUtil.postBody(Constant.PICKUP_API, json, new HttpUtil.ResponseHandler() {
                @Override
                public void onSuccess(final String responseBody) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Misc.showResponseMessage(responseBody);
                            startOrResumeTravel();
                            btnPickupLoad.setVisibility(View.GONE);
                            btnPickupArrive.setVisibility(View.VISIBLE);
                        }
                    });
                }

                @Override
                public void onFailure(final String errorString) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Misc.showResponseMessage(errorString);
                            btnPickupLoad.setEnabled(true);
                        }
                    });
                }

            });
            return null;
        }
    }

    private class ArrivedPickupAsyncTask extends AsyncTask<String, Void, Void> {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(String... params) {
            String json = params[0];

            HttpUtil.postBody(Constant.ARRIVED_PICKUP_API, json, new HttpUtil.ResponseHandler() {
                @Override
                public void onSuccess(final String responseBody) {
                    try {
                        Intent intent = new Intent(context, ActivityTravel.class);
                        intent.putExtra(Constant.INTENT_TRACKING_STATUS, 3);
                        intent.putExtra(Constant.INTENT_TRAVEL_ID, getIntent().getStringExtra(Constant.INTENT_TRAVEL_ID));
                        startActivity(intent);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Misc.showResponseMessage(responseBody);
                            }
                        });
                        finish();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(final String errorString) {
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btnPickupArrive.setEnabled(true);
                                Misc.showResponseMessage( errorString);
                            }
                        });
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            return null;
        }
    }

    private void alertConfirmArrival(){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

        final View searchView = getLayoutInflater().inflate(R.layout.dialog_confirm_arrival_pickup_location,null);
        final TextView txtConfirmArrival = (TextView)searchView.findViewById(R.id.txt_confirm_arrival);
        final Button btnOk = (Button)searchView.findViewById(R.id.btn_ok);
        final Button btnCancel = (Button)searchView.findViewById(R.id.btn_cancel);

        txtConfirmArrival.setText(Html.fromHtml(getString(R.string.notice_confirm_arrival)));
        builder.setView(searchView);
        final android.app.AlertDialog alertDialog = builder.create();

        alertDialog.show();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                stopLocationUpdateService();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("TravelID", getIntent().getStringExtra(Constant.INTENT_TRAVEL_ID));
                    jsonObject.put("Lat", AppSettings.with(context).getCurrentLat().toString());
                    jsonObject.put("long", AppSettings.with(context).getCurrentLng().toString());

                    Logger.error(AppSettings.with(context).getCurrentLat() + "," + AppSettings.with(context).getCurrentLng());

                } catch (Exception e) {
                    e.printStackTrace();
                }
                btnPickupArrive.setEnabled(false);
                ArrivedPickupAsyncTask  arrivedPickupAsyncTask = new ArrivedPickupAsyncTask();
                arrivedPickupAsyncTask.execute(jsonObject.toString());
            }
        });
    }

    private BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if(!LocationUpdateService.getTravelID().equals(getIntent().getStringExtra(Constant.INTENT_TRAVEL_ID)))
                    return;
                updateMap();

                if(btnPickupLoad.getVisibility() == View.VISIBLE) {
                    btnPickupLoad.setVisibility(View.GONE);
                    btnPickupArrive.setVisibility(View.VISIBLE);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private void drawLocations(){
        try {
            Location currentLocation =  new Location("location");
            currentLocation.setLatitude(AppSettings.with(this).getCurrentLat());
            currentLocation.setLongitude(AppSettings.with(this).getCurrentLng());

            LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
//            double circleRad = currentLocation.distanceTo(loadLocation)/2*1000;//multiply by 1000 to make units in KM
//            ZOOM = Misc.getZoomLevel(circleRad) + 8;

            _googleMap.addMarker(new MarkerOptions().position(_loadLatLng).title(getString(R.string.load_location)).draggable(true).flat(true).
                    icon(BitmapDescriptorFactory.fromBitmap(Misc.getMarkerBitmap(R.drawable.pickup_marker))));

            _googleMap.addMarker(new MarkerOptions().position(currentLatLng).title(getString(R.string.current_location)).draggable(true).flat(true).
                    icon(BitmapDescriptorFactory.fromBitmap(Misc.getMarkerBitmap(R.drawable.ico_truck_tracking))));

            _googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(_loadLatLng, ZOOM));

            GoogleMapPath googleMapPath = new GoogleMapPath(_googleMap, _loadLatLng, currentLatLng);
            googleMapPath.drawPath();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            _googleMap = googleMap;
            _googleMap.getUiSettings().setZoomControlsEnabled(true);
            drawLocations();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void updateMap() {
        LatLng currentLatLng = new LatLng(AppSettings.with(this).getCurrentLat(), AppSettings.with(this).getCurrentLng());
        try {

            Location currentLocation =  new Location("location");
            currentLocation.setLatitude(AppSettings.with(this).getCurrentLat());
            currentLocation.setLongitude(AppSettings.with(this).getCurrentLng());

//            double circleRad = currentLocation.distanceTo(loadLocation)/2*1000;//multiply by 1000 to make units in KM
//            ZOOM = Misc.getZoomLevel(circleRad) + 8;

            if(prevMarker==null) {
                prevMarker = _googleMap.addMarker(new MarkerOptions().position(currentLatLng).title(getString(R.string.current_location)).draggable(true).flat(true).
                        icon(BitmapDescriptorFactory.fromBitmap(Misc.getMarkerBitmap(R.drawable.ico_truck_tracking))));

                _googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, ZOOM));
            }
            else {
//                Logger.error("Bearing= " +String.valueOf(getBearing(prevMarker.getPosition(), currentLatLng)));
//                Logger.error("current location = " + currentLatLng.toString());
//                rotateMarker(prevMarker, getBearing(prevMarker.getPosition(), currentLatLng));
//                prevMarker.setPosition(currentLatLng);

                animateMarkerNew(currentLocation, prevMarker);

                _googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));

            }

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void startOrResumeTravel(){
        startLocationUpdateService();
    }

    private void stopLocationUpdateService() {
        Intent intent = new Intent(this, LocationUpdateService.class);
        PendingIntent pIntent = PendingIntent.getService(this, Constant.TRACKING_REQUEST_CODE, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
        LocationUpdateService.isActive = false;

        LocationUpdateService.setTravelID("");
        LocationUpdateService.setServiceType(0);
    }

    private void startLocationUpdateService() {
        try {
            Intent intent = new Intent(BaseApplication.getContext(), LocationUpdateService.class);
            LocationUpdateService.setTravelID(getIntent().getStringExtra(Constant.INTENT_TRAVEL_ID));
            LocationUpdateService.setServiceType(1);
            Logger.error("TravelID =  " + LocationUpdateService.getTravelID());
            PendingIntent pIntent = PendingIntent.getService(this, Constant.TRACKING_REQUEST_CODE, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());
            AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarm.cancel(pIntent);

            LocationUpdateService.isActive = true;
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), Constant.TRACKING_INTERVAL, pIntent);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(locationReceiver);
        stopLocationUpdateService();
        prevMarker = null;
    }

    private void animateMarkerNew(final Location destination, final Marker marker) {

        if (marker != null) {

            final LatLng startPosition = marker.getPosition();
            final LatLng endPosition = new LatLng(destination.getLatitude(), destination.getLongitude());

            final float startRotation = marker.getRotation();
            final LatLngInterpolatorNew latLngInterpolator = new LatLngInterpolatorNew.LinearFixed();

            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(3000); // duration 3 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition);


                        marker.setRotation(Misc.getBearing(startPosition, new LatLng(destination.getLatitude(), destination.getLongitude())));
                    } catch (Exception ex) {
                        //I don't care atm..
                    }
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                }
            });
            valueAnimator.start();
        }
    }

    private interface LatLngInterpolatorNew {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements LatLngInterpolatorNew {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }
    }
}