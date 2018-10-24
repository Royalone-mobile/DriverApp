package com.sawatruck.driver.view.activity;

import android.location.Location;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.SawaTruckLocation;
import com.sawatruck.driver.utils.GoogleMapPath;
import com.sawatruck.driver.utils.Logger;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.utils.Serializer;

import butterknife.ButterKnife;


public class ActivityMap extends BaseActivity implements OnMapReadyCallback {
    private SupportMapFragment supportMapFragment;
    private GoogleMap mMap;
    private int ZOOM = 10;
    private SawaTruckLocation fromLocation, toLocation;

    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_map,null);
        ButterKnife.bind(this, view);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        String strFromLocation = getIntent().getStringExtra(Constant.INTENT_FROM_LOCATION);
        String strToLocation = getIntent().getStringExtra(Constant.INTENT_TO_LOCATION);
        fromLocation = Serializer.getInstance().deserializeLocation(strFromLocation);
        toLocation = Serializer.getInstance().deserializeLocation(strToLocation);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        try {

            Location location1 =  new Location("location");
            Location location2 =  new Location("location");
            location1.setLatitude(Double.valueOf(fromLocation.getLatitude()));
            location1.setLongitude(Double.valueOf(fromLocation.getLongitude()));
            location2.setLatitude(Double.valueOf(toLocation.getLatitude()));
            location2.setLongitude(Double.valueOf(toLocation.getLongitude()));

            Logger.error(String.valueOf(location1.distanceTo(location2)));
            double circleRad = location1.distanceTo(location2)/2*1000;//multiply by 1000 to make units in KM
            Logger.error(String.valueOf(circleRad));
            ZOOM = Misc.getZoomLevel(circleRad)+7;
            Logger.error(String.valueOf(ZOOM));

            LatLng loadLocation = new LatLng(Double.valueOf(fromLocation.getLatitude()), Double.valueOf(fromLocation.getLongitude()));
            LatLng deliveryLocation = new LatLng(Double.valueOf(toLocation.getLatitude()), Double.valueOf(toLocation.getLongitude()));

            googleMap.addMarker(new MarkerOptions().position(loadLocation).title(getString(R.string.load_location)).draggable(true).flat(true).
                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

            googleMap.addMarker(new MarkerOptions().position(deliveryLocation).title(getString(R.string.delivery_location)).draggable(true).flat(true).
                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loadLocation, ZOOM));

            GoogleMapPath googleMapPath = new GoogleMapPath(mMap, loadLocation, deliveryLocation);
            googleMapPath.drawPath();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
