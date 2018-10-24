package com.sawatruck.driver.view.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BuildConfig;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.controller.LocationUpdateService;
import com.sawatruck.driver.utils.AppSettings;
import com.sawatruck.driver.utils.GoogleMapPath;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.Logger;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.utils.Notice;
import com.sawatruck.driver.utils.StringUtil;
import com.sawatruck.driver.utils.UserManager;
import com.sawatruck.driver.view.design.CustomEditText;
import com.sawatruck.driver.view.design.CustomTextView;

import org.json.JSONObject;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;


public class ActivityTravel extends BaseActivity implements View.OnClickListener, OnMapReadyCallback {
    @Bind(R.id.txt_delivery_location) CustomTextView txtDeliveryLocation;
    @Bind(R.id.btn_delivery_details) View btnDeliveryDetails;
    @Bind(R.id.btn_start_travel) CustomTextView btnStartTravel;
    @Bind(R.id.btn_end_travel) CustomTextView btnEndTravel;
    @Bind(R.id.btn_navigate) View btnNavigate;
    private SupportMapFragment supportMapFragment;

    public int ZOOM = 15;
    private GoogleMap _googleMap;
    private Marker prevMarker;

    private LatLng _loadLatLng, _deliveryLatLng;
    String _confirmationCode = "";

    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_travel,null);
        ButterKnife.bind(this, view);
        btnDeliveryDetails.setOnClickListener(this);
        btnNavigate.setOnClickListener(this);

        btnNavigate.setTag(false);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        btnStartTravel.setEnabled(false);
        btnStartTravel.setOnClickListener(this);
        btnEndTravel.setOnClickListener(this);

        int trackingStatus = getIntent().getIntExtra(Constant.INTENT_TRACKING_STATUS,0);

        if(trackingStatus == 5||trackingStatus == 4) {
            startLocationUpdateService();
        }

        getTravel(getIntent().getStringExtra(Constant.INTENT_TRAVEL_ID));

        return view;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(locationReceiver);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            stopLocationUpdateService();
        }
                prevMarker = null;
    }


    private void getTravel(final String travelID){
        HttpUtil httpUtil = HttpUtil.getInstance();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, UserManager.with(context).getCurrentUser().getToken());

        httpUtil.get(Constant.GET_TRAVEL_BY_ID +"/" + travelID, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] responseBody) {
                try {

                    btnStartTravel.setEnabled(true);
                    String paramString = new String(responseBody);
                    paramString = StringUtil.escapeString(paramString);
                    JSONObject responseObject= new JSONObject(paramString);
                    JSONObject locationObject = responseObject.getJSONObject("ToLocation");
                    String cityName  = locationObject.getString("CityName");
                    String countryName  = locationObject.getString("Name");
                    txtDeliveryLocation.setText(cityName.concat(",").concat(countryName));

                    Double latitude = locationObject.getDouble("Latitude");
                    Double longitude = locationObject.getDouble("Longitude");

                    _deliveryLatLng = new LatLng(latitude, longitude);

                    locationObject = responseObject.getJSONObject("FromLocation");
                    latitude = locationObject.getDouble("Latitude");
                    longitude = locationObject.getDouble("Longitude");
                    _loadLatLng = new LatLng(latitude, longitude);

                    _confirmationCode  = responseObject.getString("ConfirmationCode");
                    onMapReady(_googleMap);

                    String trackingNumber = responseObject.getString("TrackingNumber");
                    setAppTitle(getString(R.string.title_travel) + " (" + trackingNumber + ")");
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


        btnDeliveryDetails.setEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {  Manifest.permission.ACCESS_FINE_LOCATION , Manifest.permission.ACCESS_COARSE_LOCATION  } ,100);
        }

        setAppTitle(getString(R.string.title_travel));
        showNavHome(false);

        registerReceiver(locationReceiver, new IntentFilter("onLocationUpdate"));
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent;
        switch (id) {
            case R.id.btn_delivery_details:
                try {
                    intent = new Intent(this, ActivityDeliveryDetails.class);
                    intent.putExtra(Constant.INTENT_TRAVEL_ID, getIntent().getStringExtra(Constant.INTENT_TRAVEL_ID));
                    startActivity(intent);
                }catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_navigate:
                navigateLocation();
                break;

            case R.id.btn_start_travel:
                startTracking();
                break;
            case R.id.btn_end_travel:
                alertDeliveryConfirmation();
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

    private class StartTravelAsyncTask extends AsyncTask<String, Void, Void> {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(String... params) {
            String json = params[0];

            HttpUtil.postBody(Constant.START_TRAVEL_API, json, new HttpUtil.ResponseHandler() {
                @Override
                public void onSuccess(final String responseBody) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            startLocationUpdateService();

                            Misc.showResponseMessage(responseBody);
                        }
                    });
                }

                @Override
                public void onFailure(final String errorString) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Misc.showResponseMessage(errorString);
                            btnStartTravel.setEnabled(true);
                        }
                    });
                }

            });
            return null;
        }
    }

    private class EndTrackingAsyncTask extends AsyncTask<String, Void, Void> {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(String... params) {
            String json = params[0];

            HttpUtil.postBody(Constant.END_TRACKING_API, json, new HttpUtil.ResponseHandler() {
                @Override
                public void onSuccess(final String responseBody) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Misc.showResponseMessage(responseBody);
                            stopLocationUpdateService();
                            Intent intent = new Intent(ActivityTravel.this, ActivityCollectPayment.class);
                            intent.putExtra(Constant.INTENT_TRAVEL_ID, getIntent().getStringExtra(Constant.INTENT_TRAVEL_ID));
                            startActivity(intent);
                            finish();
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void alertDeliveryConfirmation(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View confirmationView = getLayoutInflater().inflate(R.layout.dialog_delivery_confirmation,null);

        final CustomEditText editCode = (CustomEditText)confirmationView.findViewById(R.id.edit_code);
        final CustomTextView btnReset = (CustomTextView) confirmationView.findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editCode.setText("");
            }
        });

        final Button btnOk = (Button)confirmationView.findViewById(R.id.btn_ok);
        final Button btnCancel = (Button)confirmationView.findViewById(R.id.btn_cancel);
        final Button btnDecline = (Button)confirmationView.findViewById(R.id.btn_decline);



        builder.setView(confirmationView);

        final AlertDialog alertDialog = builder.create();

        alertDialog.show();

        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityTravel.this, ActivityDeclineTravel.class);
                intent.putExtra(Constant.INTENT_TRAVEL_ID, getIntent().getStringExtra(Constant.INTENT_TRAVEL_ID));
                startActivity(intent);
                finish();
                alertDialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                btnEndTravel.setEnabled(true);
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnEndTravel.setEnabled(false);


                if(_confirmationCode == null) {
                    Notice.show( getString(R.string.error_confirmation_empty));
                    return;
                }

                Logger.error("_confirmationCode= " +_confirmationCode);
                if(!_confirmationCode.equals( editCode.getText().toString())){
                    Notice.show( getString(R.string.error_confirmation_not_match));
                    return;
                }

                EndTracking();


                alertDialog.dismiss();
            }
        });
    }

    public void EndTracking(){
        try {
            EndTrackingAsyncTask endTrackingAsyncTask = new EndTrackingAsyncTask();
            endTrackingAsyncTask.execute( buildJSON());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String buildJSON() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("TravelID", getIntent().getStringExtra(Constant.INTENT_TRAVEL_ID));
        jsonObject.put("Lat", AppSettings.with(this).getCurrentLat().toString());
        jsonObject.put("long", AppSettings.with(this).getCurrentLng().toString());
        return jsonObject.toString();
    }


    public void startTracking(){
        try {
            int trackingStatus = getIntent().getIntExtra(Constant.INTENT_TRACKING_STATUS,0);

            if(trackingStatus != 3 && trackingStatus != 4) {
                startLocationUpdateService();
                return;
            }

            btnStartTravel.setEnabled(false);
            StartTravelAsyncTask startTravelAsyncTask = new StartTravelAsyncTask();
            startTravelAsyncTask.execute( buildJSON());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if(!LocationUpdateService.getTravelID().equals(getIntent().getStringExtra(Constant.INTENT_TRAVEL_ID)))
                    return;
                updateMap();


            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    public void updateMap(){
        LatLng currentLatLng = new LatLng(AppSettings.with(this).getCurrentLat(), AppSettings.with(this).getCurrentLng());
        try {
            Location currentLocation =  new Location("location");
            currentLocation.setLatitude(AppSettings.with(this).getCurrentLat());
            currentLocation.setLongitude(AppSettings.with(this).getCurrentLng());

            if(prevMarker==null) {
                prevMarker = _googleMap.addMarker(new MarkerOptions().position(currentLatLng).title(getString(R.string.current_location)).draggable(true).flat(true).
                        icon(BitmapDescriptorFactory.fromBitmap(Misc.getMarkerBitmap(R.drawable.ico_truck_tracking))));

                _googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, ZOOM));
            }
            else {
                animateMarkerNew(currentLocation, prevMarker);
                _googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
            }

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


    private void drawLocations(){
        try {

            LatLng currentLatLng = new LatLng(AppSettings.with(this).getCurrentLat(), AppSettings.with(this).getCurrentLng());

            Location currentLocation =  new Location("location");
            currentLocation.setLatitude(AppSettings.with(this).getCurrentLat());
            currentLocation.setLongitude(AppSettings.with(this).getCurrentLng());

            _googleMap.addMarker(new MarkerOptions().position(currentLatLng).title(getString(R.string.current_location)).draggable(true).flat(true).
                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

            _googleMap.addMarker(new MarkerOptions().position(_deliveryLatLng).title(getString(R.string.delivery_location)).draggable(true).flat(true).
                    icon(BitmapDescriptorFactory.fromBitmap(Misc.getMarkerBitmap(R.drawable.delivery_marker))));
            _googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, ZOOM));
            GoogleMapPath googleMapPath = new GoogleMapPath(_googleMap, currentLatLng, _deliveryLatLng);
            googleMapPath.drawPath();

        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public void startLocationUpdateService() {
        try {

            if(btnStartTravel.getVisibility() == View.VISIBLE) {
                btnEndTravel.setVisibility(View.VISIBLE);
                btnStartTravel.setVisibility(View.GONE);
            }

            Intent intent = new Intent(BaseApplication.getContext(), LocationUpdateService.class);
            LocationUpdateService.setTravelID(getIntent().getStringExtra(Constant.INTENT_TRAVEL_ID));
            LocationUpdateService.setServiceType(2);

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

    private boolean isMarkerRotating = false;
    protected void stopLocationUpdateService() {
        Intent intent = new Intent(this, LocationUpdateService.class);
        PendingIntent pIntent = PendingIntent.getService(this, Constant.TRACKING_REQUEST_CODE, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        LocationUpdateService.isActive = false;
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);

        LocationUpdateService.setTravelID("");
        LocationUpdateService.setServiceType(0);
    }

    private void rotateMarker(final Marker marker, final float toRotation) {
        if(!isMarkerRotating) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final float startRotation = marker.getRotation();
            final long duration = 1000;

            final Interpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    isMarkerRotating = true;

                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);

                    float rot = t * toRotation + (1 - t) * startRotation;

                    marker.setRotation(-rot > 180 ? rot / 2 : rot);
                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    } else {
                        isMarkerRotating = false;
                    }
                }
            });
        }
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

                    // if (mMarker != null) {
                    // mMarker.remove();
                    // }
                    // mMarker = googleMap.addMarker(new MarkerOptions().position(endPosition).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_car)));

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
