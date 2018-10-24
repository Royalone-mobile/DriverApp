package com.sawatruck.driver.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.Advertisement;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.Logger;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.utils.UserManager;
import com.sawatruck.driver.view.activity.ActivityAdDetail;
import com.sawatruck.driver.view.activity.ActivityCollectPayment;
import com.sawatruck.driver.view.activity.ActivityPickup;
import com.sawatruck.driver.view.activity.ActivityRating;
import com.sawatruck.driver.view.activity.ActivityTravel;
import com.sawatruck.driver.view.design.CustomTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by royal on 8/20/2017.
 */


public class AdAdapter extends RecyclerView.Adapter<AdAdapter.MyViewHolder> {
    private List<Advertisement> adList = new ArrayList<>();
    private Context context;
    private int tabPosition = -1;
    @Override
    public AdAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.driver_ad_list_item, parent, false);
        return new AdAdapter.MyViewHolder(v);
    }

    public AdAdapter(Context context, int tabPosition) {
        this.context = context;
        this.tabPosition = tabPosition;
    }

    @Override
    public void onBindViewHolder(AdAdapter.MyViewHolder holder, final int position) {
        Advertisement advertisement = adList.get(position);

        holder.txtPickupDate.setText(Misc.getTimeZoneDate(advertisement.getPickupDate()));
        holder.txtDeliveryDate.setText(Misc.getTimeZoneDate(advertisement.getDeliveryDate()));

        holder.txtBudgetPrice.setText(advertisement.getBudget() + " " + advertisement.getCurrency());
        holder.txtPickupLocation.setText(advertisement.getPickupCity() + ", " + advertisement.getPickupCountry());
        holder.txtDeliveryLocation.setText(advertisement.getDeliveryCity() + ", " + advertisement.getDeliveryCountry());
        holder.txtTruckType1.setText(advertisement.getTruckTypeName1());
        holder.txtTruckType2.setText(advertisement.getTruckTypeName2());
        holder.txtBidCount.setText(advertisement.getNewBookingsCount());
        holder.txtAvailableDays.setText(advertisement.getAvailable());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                Advertisement advertisement = adList.get(position);
                if(tabPosition == 0) {
                    getTravelStatus(advertisement);
                }
                else {
                    intent = new Intent(context, ActivityAdDetail.class);
                    intent.putExtra(Constant.INTENT_ADVERTISEMENT_ID, advertisement.getID());
                    context.startActivity(intent);
                }
            }
        });

        try {
            BaseApplication.getPicasso().load(advertisement.getTruckImageURL()).placeholder(R.drawable.ico_truck).into(holder.imgTruck);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean clicked = false;
    private void getTravelStatus(final Advertisement advertisement){
        if(clicked) return;
        clicked = true;
        HttpUtil httpUtil = HttpUtil.getInstance();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, UserManager.with(context).getCurrentUser().getToken());

        int  trackingStatus = Integer.valueOf(advertisement.getTrackingStatus());
        Intent intent;

        if(trackingStatus <3) {
            intent = new Intent(context, ActivityPickup.class);
            intent.putExtra(Constant.INTENT_TRAVEL_ID, advertisement.getID());
            intent.putExtra(Constant.INTENT_TRACKING_STATUS, trackingStatus);
            context.startActivity(intent);
            clicked = false;
            return;
        } else if(trackingStatus < 6){
            intent = new Intent(context, ActivityTravel.class);
            intent.putExtra(Constant.INTENT_TRAVEL_ID, advertisement.getID());
            intent.putExtra(Constant.INTENT_TRACKING_STATUS, trackingStatus);
            context.startActivity(intent);
            clicked = false;
            return;
        }

        int Status = Integer.valueOf(advertisement.getTravelStatus());
        Logger.error("Travel Status = " + Status);

        clicked = false;
        if(Status  == 2){
            intent = new Intent(context, ActivityCollectPayment.class);
            intent.putExtra(Constant.INTENT_TRAVEL_ID, advertisement.getID());
            context.startActivity(intent);

            return;
        }
        else if(Status == 3){
            intent = new Intent(context, ActivityRating.class);
            intent.putExtra(Constant.INTENT_TRAVEL_ID, advertisement.getID());
            context.startActivity(intent);
            return;
        }
    }


    @Override
    public int getItemCount() {
        return adList.size();
    }

    public List<Advertisement> getAdList() {
        return adList;
    }

    public void setAdList(List<Advertisement> adList) {
        this.adList = adList;
    }

    public void initializeAdapter(){
        this.adList = new ArrayList<>();
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.txt_truck_type_1) CustomTextView txtTruckType1;
        @Bind(R.id.txt_truck_type_2) CustomTextView txtTruckType2;
        @Bind(R.id.img_truck) ImageView imgTruck;
        @Bind(R.id.txt_budget_price) CustomTextView txtBudgetPrice;
        @Bind(R.id.txt_pickup_location) CustomTextView txtPickupLocation;
        @Bind(R.id.txt_delivery_location) CustomTextView txtDeliveryLocation;
        @Bind(R.id.txt_pickup_date) CustomTextView txtPickupDate;
        @Bind(R.id.txt_delivery_date) CustomTextView txtDeliveryDate;
        @Bind(R.id.txt_available_days) CustomTextView txtAvailableDays;
        @Bind(R.id.txt_bid_count) CustomTextView txtBidCount;
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}