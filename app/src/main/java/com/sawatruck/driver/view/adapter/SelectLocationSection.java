package com.sawatruck.driver.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.google.android.gms.maps.model.LatLng;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.controller.GetNearbyLocationTask;
import com.sawatruck.driver.controller.LocationUpdateService;
import com.sawatruck.driver.controller.NearbyLocation;
import com.sawatruck.driver.entities.AddressDetail;
import com.sawatruck.driver.repository.DBController;
import com.sawatruck.driver.utils.Logger;
import com.sawatruck.driver.view.activity.ActivityCities;
import com.sawatruck.driver.view.activity.ActivityShowLocation;
import com.sawatruck.driver.view.design.CustomTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * Created by royal on 9/5/2017.
 */


public class SelectLocationSection extends StatelessSection {
    private Context context;
    protected List<Object> list = new ArrayList<>();

    String headerTitle="";

    GetNearbyLocationTask getLocationTask ;

    double latitude, longitude;


    public SelectLocationSection(Context context, String headerTitle) {
        super(R.layout.search_location_header, R.layout.select_location_item);
        this.headerTitle = headerTitle;
        this.context = context;
    }

    public void clearList() {
        this.list.clear();
    }

    public OnAddLocationsListener getOnAddLocationsListener() {
        return onAddLocationsListener;
    }

    public void setOnAddLocationsListener(OnAddLocationsListener onAddLocationsListener) {
        this.onAddLocationsListener = onAddLocationsListener;
    }

    public interface OnAddLocationsListener{
        void OnAddLocations();
    }

    private OnAddLocationsListener onAddLocationsListener;
    public void execute(double latitude, double longitude) {
        this.clearList();
        this.latitude = latitude;
        this.longitude = longitude;

        getLocationTask = new GetNearbyLocationTask(this);
        getLocationTask.execute(latitude, longitude);
        // get the Songs from the web asynchronously
    }

    public void executeKeyword(double latitude, double longitude,String keyword) {
        this.clearList();
        this.latitude = latitude;
        this.longitude = longitude;

        getLocationTask = new GetNearbyLocationTask(this);
        getLocationTask.reset();
        getLocationTask.execute(latitude, longitude, keyword);
        // get the Songs from the web asynchronously
    }
    public void cancelQuery(){

    }
    public void setList(List<NearbyLocation> l) {
        this.initializeAdapter();
        if (l != null && l.size() > 0) {
            this.list.addAll(l);
        }
    }

    public void appendList(List<NearbyLocation> l) {
        if (l != null && l.size() > 0) {
            this.list.addAll(l);
        }
        if(onAddLocationsListener!=null)
            onAddLocationsListener.OnAddLocations();
    }

    public SelectLocationSection() {
        super(R.layout.search_location_header, R.layout.select_location_item);
    }

    public SelectLocationSection(Context context, ArrayList<AddressDetail> addressDetails) {
        super(R.layout.search_location_header, R.layout.select_location_item);
        this.context = context;
    }

    @Override
    public int getContentItemsTotal() {
        return list.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }


    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final NearbyLocation nearbyLocation = (NearbyLocation)list.get(position);
        final ItemViewHolder itemHolder = (ItemViewHolder)holder;
        itemHolder.txtLocation1.setText(nearbyLocation.getName());
        itemHolder.txtLocation2.setText(nearbyLocation.getVincity());

        if(headerTitle.equals("Saved Locations")){
            itemHolder.toggleSave.setChecked(true);
        }

        DBController dbController = DBController.getInstance().open(BaseApplication.getContext());

        NearbyLocation searchResult = dbController.getSavedLocationByLatLng(new LatLng(nearbyLocation.getLatitude(), nearbyLocation.getLongitude()));

        if(searchResult!=null) {
            itemHolder.toggleSave.setChecked(true);
        }
        else {
            itemHolder.toggleSave.setChecked(false);
        }

        dbController.closeDB();

        itemHolder.toggleSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBController dbController = DBController.getInstance().open(BaseApplication.getContext());

                NearbyLocation searchResult = dbController.getSavedLocationByLatLng(new LatLng(nearbyLocation.getLatitude(), nearbyLocation.getLongitude()));
                if(searchResult==null) {
                    dbController.insertSavedLocation(nearbyLocation);
                    itemHolder.toggleSave.setChecked(true);
                }
                else {
                    dbController.deleteSavedLocation(nearbyLocation);
                    itemHolder.toggleSave.setChecked(false);
                }
                dbController.closeDB();
                ActivityCities.notifyDataChanged();
            }
        });



        itemHolder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {

                    if(!headerTitle.equals("Recent Locations")) {
                        itemHolder.toggleSave.setChecked(true);

                        DBController dbController = DBController.getInstance().open(context);
                        NearbyLocation searchResult = dbController.getRecentLocationByLatLng(new LatLng(nearbyLocation.getLatitude(), nearbyLocation.getLongitude()));
                        if (searchResult != null)
                            dbController.deleteRecentLocation(nearbyLocation);
                        else
                            dbController.insertRecentLocation(nearbyLocation);

                        dbController.closeDB();
                    }

                    Intent intent  = new Intent(context, ActivityShowLocation.class);
                    intent.putExtra(Constant.INTENT_LOCATION, nearbyLocation);
                    ((ActivityCities)context).startActivityForResult(intent,Constant.GET_PLACE_REQUEST_CODE);
//                    ActivityCities.notifyDataChanged();
//                    activityCities.finishWithParsing(nearbyLocation.getLatitude(),nearbyLocation.getLongitude());

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder headerHolder = (HeaderViewHolder)holder;
        headerHolder.imgPhoto.setImageResource(R.drawable.select_location_marker);
        headerHolder.txtLocationName.setText(headerTitle);
    }

    public void initializeAdapter() {
        this.list = new ArrayList<>();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.txt_location1) CustomTextView txtLocation1;
        @Bind(R.id.txt_location2) CustomTextView txtLocation2;
        @Bind(R.id.toggle_save) ToggleButton toggleSave;
        @Bind(R.id.layout_item) View layoutItem;
        public ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.img_photo) ImageView imgPhoto;
        @Bind(R.id.txt_name) CustomTextView txtLocationName;

        public HeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }

}