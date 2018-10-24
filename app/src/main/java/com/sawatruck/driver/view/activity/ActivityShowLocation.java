package com.sawatruck.driver.view.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.controller.NearbyLocation;
import com.sawatruck.driver.entities.AddressDetail;
import com.sawatruck.driver.entities.SawaTruckLocation;
import com.sawatruck.driver.repository.DBController;
import com.sawatruck.driver.utils.Logger;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.utils.Serializer;
import com.sawatruck.driver.view.design.CustomTextView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by royal on 8/31/2017.
 */

public class ActivityShowLocation extends BaseActivity implements OnMapReadyCallback, View.OnClickListener {
    @Bind(R.id.btn_confirm) View btnConfirm;
    @Bind(R.id.txt_location1) CustomTextView txtLocation1;
    @Bind(R.id.txt_location2) CustomTextView txtLocation2;
    @Bind(R.id.toggle_save) ToggleButton toggleSave;
    private SupportMapFragment supportMapFragment;
    private GoogleMap mMap;
    public  int ZOOM = 15;


    private NearbyLocation location;

    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_show_location,null);
        ButterKnife.bind(this, view);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        btnConfirm.setOnClickListener(this);


        location  = (NearbyLocation) getIntent().getSerializableExtra(Constant.INTENT_LOCATION);
        Logger.error(location.getLatitude() + " , " + location.getLongitude());

        txtLocation1.setText(location.getName());
        txtLocation2.setText(location.getVincity());

        DBController dbController = DBController.getInstance().open(BaseApplication.getContext());

        NearbyLocation searchResult = dbController.getSavedLocationByLatLng(new LatLng(location.getLatitude(), location.getLongitude()));

        if(searchResult!=null) {
            toggleSave.setChecked(true);
        }
        else {
            toggleSave.setChecked(false);
        }

        dbController.closeDB();

        toggleSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBController dbController = DBController.getInstance().open(BaseApplication.getContext());

                NearbyLocation searchResult = dbController.getSavedLocationByLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                if(searchResult==null) {
                    dbController.insertSavedLocation(location);
                    toggleSave.setChecked(true);
                }
                else {
                    dbController.deleteSavedLocation(location);
                    toggleSave.setChecked(false);
                }
                dbController.closeDB();
            }
        });


        Animation animation = AnimationUtils.loadAnimation(context, R.anim.notice_flow);
        animation.setRepeatMode(Animation.RESTART);
        animation.setRepeatCount(Animation.INFINITE);
        txtLocation2.startAnimation(animation);


        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

        setAppTitle(getResources().getString(R.string.title_map));
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

    public void finishWithParsing(Double latitude, Double longitude) {
        LocationAsyncTask locationAsyncTask = new LocationAsyncTask();
        Location location = new Location("location");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        locationAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,location);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_confirm:
                confirmLocation();
                break;
        }
    }

    private void confirmLocation() {
        finishWithParsing(location.getLatitude(),location.getLongitude());
    }


    private class LocationAsyncTask extends AsyncTask<Location, Void, Void> {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(Location... params) {
            Location location = params[0];

            final AddressDetail addressDetail = Misc.getAddressDetail(location.getLatitude(), location.getLongitude());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finishWithResult(addressDetail);
                }
            });
            return null;
        }
    }


    public void finishWithResult(AddressDetail addressDetail) {
        Intent intent = new Intent();
        intent.putExtra("address", Serializer.getInstance().serializeAddressDetail(addressDetail));
        setResult(RESULT_OK, intent);
        finish();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        try {

            Location location1 =  new Location("location");
            location1.setLatitude(location.getLatitude());
            location1.setLongitude(location.getLongitude());


            LatLng location = new LatLng(Double.valueOf(this.location.getLatitude()), Double.valueOf(this.location.getLongitude()));

            googleMap.addMarker(new MarkerOptions().position(location).title("SawatruckLocation").draggable(true).flat(true).
                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, ZOOM));

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
