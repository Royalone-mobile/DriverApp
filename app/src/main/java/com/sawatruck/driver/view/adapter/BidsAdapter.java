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
import com.sawatruck.driver.entities.Offer;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.view.activity.ActivityBidAccepted;
import com.sawatruck.driver.view.activity.ActivityBidBooked;
import com.sawatruck.driver.view.activity.ActivityBidOpened;
import com.sawatruck.driver.view.design.CustomTextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by royal on 8/22/2017.
 */


public class BidsAdapter extends RecyclerView.Adapter<BidsAdapter.MyViewHolder> {
    private Context context;
    private int tabPosition = -1;
    private ArrayList<Offer> offers = new ArrayList<>();

    @Override
    public BidsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.driver_bid_list_item, parent, false);
        return new BidsAdapter.MyViewHolder(v);
    }


    public BidsAdapter(Context context, int tabPosition) {
        this.context = context;
        this.tabPosition = tabPosition;
    }

    @Override
    public void onBindViewHolder(BidsAdapter.MyViewHolder holder, final int position) {
        final Offer offer = offers.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                switch (tabPosition) {
                    case 0:
                        intent = new Intent(context, ActivityBidBooked.class);
                        break;
                    case 1:
                        intent = new Intent(context, ActivityBidAccepted.class);
                        break;
                    case 2:
                        intent = new Intent(context, ActivityBidOpened.class);
                        break;
                }
                intent.putExtra(Constant.INTENT_OFFER_ID, offer.getID());
                context.startActivity(intent);
            }
        });


        try {
            holder.txtBidPrice.setText(context.getString(R.string.my_bid).concat(" ").concat(String.valueOf(offer.getPrice())).concat(" ").concat(offer.getCurrency()));
            holder.txtBudgetPrice.setText(context.getString(R.string.offer).concat(" ").concat(String.valueOf(offer.getLoadBudget())).concat(" ").concat(offer.getLoadBudgetCurrency()));
            holder.txtDeliveryDate.setText(Misc.getTimeZoneDate(offer.getDeliveryDate()));
            holder.txtLoadDate.setText(Misc.getTimeZoneDate(offer.getPickupDate()) );
            holder.txtLoadLocation.setText(offer.getPickupCity().concat(",").concat(offer.getPickupCountry()));
            holder.txtDeliveryLocation.setText(offer.getDeliveryCity().concat(",").concat(offer.getDeliveryCountry()));
            holder.txtOfferName.setText(offer.getName());

            BaseApplication.getPicasso().load(offer.getLoadTypeImgURL()).placeholder(R.drawable.ico_truck).into(holder.imgCarPhoto);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return offers.size();
    }

    public void setOffers(ArrayList<Offer> offers) {
        this.offers = offers;
    }

    public ArrayList<Offer> getOffers() {
        return offers;
    }

    public void initializeAdapter() {
        this.offers  =  new ArrayList<>();
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.txt_offer_name) CustomTextView txtOfferName;
        @Bind(R.id.txt_budget_price) CustomTextView txtBudgetPrice;
        @Bind(R.id.txt_bid_price) CustomTextView txtBidPrice;
        @Bind(R.id.txt_load_location) CustomTextView txtLoadLocation;
        @Bind(R.id.txt_delivery_location) CustomTextView txtDeliveryLocation;
        @Bind(R.id.txt_delivery_date) CustomTextView txtDeliveryDate;
        @Bind(R.id.txt_load_date) CustomTextView txtLoadDate;
        @Bind(R.id.img_car_photo) ImageView imgCarPhoto;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}