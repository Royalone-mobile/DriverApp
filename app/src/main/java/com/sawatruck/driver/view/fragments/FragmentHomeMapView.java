package com.sawatruck.driver.view.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.Load;
import com.sawatruck.driver.entities.User;
import com.sawatruck.driver.utils.AppSettings;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.Logger;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.utils.UserManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by royal on 8/20/2017.
 */

public class FragmentHomeMapView extends BaseFragment implements OnMapReadyCallback {
    private static final String ARG_POSITION = "position";
    public static int ZOOM = 10;
    private GoogleMap mMap;
    private SupportMapFragment supportMapFragment;

    private  ArrayList<Load> loads = new ArrayList<>();

    public static FragmentHomeMapView getInstance(int position) {
        FragmentHomeMapView f = new FragmentHomeMapView();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_map_view, container, false);
        supportMapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        return view;
    }


    private void searchLoads() throws JSONException {
        loads = new ArrayList<>();

        LatLng latLng = new LatLng(AppSettings.with(getContext()).getCurrentLat(), AppSettings.with(getContext()).getCurrentLng());
        RequestParams requestParams = new RequestParams();

        requestParams.put("Start","0");
        requestParams.put("Length","999");
        requestParams.put("FromLatitude", String.valueOf(latLng.latitude));
        requestParams.put("FromLongitude", String.valueOf(latLng.longitude));
//        requestParams.put("FromLatitude", "1.538776");
//        requestParams.put("FromLongitude", "103.639930");

        requestParams.put("Distance", Constant.DISTANCE_RADIUS);
        requestParams.put("TruckTypeID", String.valueOf(AppSettings.with(getContext()).getTruckType()));
        requestParams.put("TruckClassID", String.valueOf(AppSettings.with(getContext()).getTruckClass()));
        requestParams.put("LoadTypeID", String.valueOf(AppSettings.with(getContext()).getLoadType()));

        Logger.error(requestParams.toString());


        HttpUtil httpUtil = HttpUtil.getInstance();
        User user  = UserManager.with(getActivity()).getCurrentUser();

        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());

        httpUtil.post(Constant.LOADS_SEARCH_API, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String paramString = new String(responseBody);
                    JSONObject jsonObject = new JSONObject(paramString);
                    JSONArray jsonArray = (JSONArray) jsonObject.get("Data");

                    for (int j = 0; j < jsonArray.length(); j++) {
                        Load load = BaseApplication.getGson().fromJson(jsonArray.get(j).toString(), Load.class);
                        loads.add(load);
                    }
                    onMapReady(mMap);
                } catch (Exception e) {
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
    public void onMapReady(GoogleMap googleMap) {
        LatLng currentLatLng = new LatLng(AppSettings.with(getContext()).getCurrentLat(), AppSettings.with(getContext()).getCurrentLng());
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, ZOOM));
        mMap = googleMap;
        if(mMap==null) return;
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        if(loads.size() == 0) {
            try {
                searchLoads();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        double maxCircleRad = 0.0f;


        try{
            LatLng firstLatLng = new LatLng(Double.valueOf(loads.get(0).getFromLocation().getLatitude()), Double.valueOf(loads.get(0).getFromLocation().getLongitude()));

            List<LatLng> locations = new ArrayList<>();
            LatLng prevLatLng = firstLatLng;
            for(Load load:loads){
                LatLng loadLocation = new LatLng(Double.valueOf(load.getFromLocation().getLatitude()), Double.valueOf(load.getFromLocation().getLongitude()));
                double circleRad = getCircleRad(prevLatLng,loadLocation);
                prevLatLng = loadLocation;

                if (circleRad >= maxCircleRad) {
                    maxCircleRad = circleRad;
                }

                locations.add(loadLocation);

                ZOOM = Misc.getZoomLevel(maxCircleRad);

            }

            final HashMap<LatLng, ArrayList<Load>> maps =  getPackets(loads);
            for(Object key: maps.keySet()){
                Bitmap bmp = makeBitmap(getContext(), String.valueOf(maps.get(key).size()));
                LatLng latLng = (LatLng) key;
                Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng).title(getString(R.string.loads)).draggable(true).flat(true).
                        icon(BitmapDescriptorFactory.fromBitmap(bmp)));
            }
            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    final HashMap<LatLng, ArrayList<Load>> maps =  getPackets(loads);
                    LatLng latLng =  marker.getPosition();
                    ArrayList<Load> loads = maps.get(latLng);

                    FragmentMenuHome fragmentMenuHome = (FragmentMenuHome)getParentFragment();

                    fragmentMenuHome.tab2.loads = loads;

                    fragmentMenuHome.pager.setCurrentItem(1);
                    fragmentMenuHome.tab2.loadsAdapter.setLoads(loads);
                    fragmentMenuHome.tab2.loadsAdapter.notifyDataSetChanged();
                }
            });
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    return false;
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public  HashMap<LatLng, ArrayList<Load>> getPackets(List<Load> loads) {
        HashMap<LatLng, ArrayList<Load>> hashMap = new HashMap<>();
        for (Load load : loads) {
            LatLng latLng = new LatLng(Double.valueOf(load.getFromLocation().getLatitude()), Double.valueOf(load.getFromLocation().getLongitude()));
            if (hashMap.get(latLng) == null) {
                ArrayList<Load> newList = new ArrayList<>();
                newList.add(load);
                hashMap.put(latLng, newList);
            } else {
                ArrayList<Load> newList = hashMap.get(latLng);
                newList.add(load);
                hashMap.put(latLng, newList);
            }
        }
        return hashMap;
    }

    public Bitmap makeBitmap(Context context, String text) {
        Resources resources = context.getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.marker_load);
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED); // Text color
        paint.setTextSize(14 * scale); // Text size
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE); // Text shadow
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        int x = bitmap.getWidth() - bounds.width()/2 - bitmap.getWidth()/2; // 10 for padding from right
        int y = bitmap.getHeight()/2 + bounds.height()/2;

        canvas.drawText(text, x, y, paint);

        return  bitmap;
    }

    private double getCircleRad(LatLng latLng1, LatLng latLng2) {
        Location fromLocation =  new Location("");
        Location toLocation =  new Location("");
        fromLocation.setLatitude(latLng1.latitude);
        toLocation.setLatitude(latLng2.latitude);
        fromLocation.setLongitude(latLng1.longitude);
        toLocation.setLongitude(latLng2.longitude);
        return fromLocation.distanceTo(toLocation);
    }
}
