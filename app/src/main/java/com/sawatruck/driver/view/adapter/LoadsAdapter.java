package com.sawatruck.driver.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makeramen.roundedimageview.RoundedImageView;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.Load;
import com.sawatruck.driver.entities.LoadPhoto;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.utils.Serializer;
import com.sawatruck.driver.view.activity.ActivityLoadDetails;
import com.sawatruck.driver.view.design.CustomTextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by royal on 8/20/2017.
 */


public class LoadsAdapter extends RecyclerView.Adapter<LoadsAdapter.MyViewHolder> {
    private ArrayList<Load> loads = new ArrayList<>();
    private Context context;
    private int tabPosition = -1;

    @Override
    public LoadsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.load_list_item, parent, false);
        return new LoadsAdapter.MyViewHolder(v);
    }

    public LoadsAdapter(Context context) {
        this.context = context;
    }

    public LoadsAdapter(Context context, int tabPosition) {
        this.context = context;
        this.tabPosition = tabPosition;
    }

    @Override
    public void onBindViewHolder(LoadsAdapter.MyViewHolder holder, final int position) {
        Load load = loads.get(position);

        if(load.getLoadPhotos().size()>0) {
            LoadPhoto loadPhoto = load.getLoadPhotos().get(0);

            try {
                BaseApplication.getPicasso().load(loadPhoto.getPhotoPath()).placeholder(R.drawable.ico_truck).into(holder.imgLoadPhoto);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        holder.txtBudgetPrice.setText(load.getBudget() + " " + load.getCurrency());
        holder.txtDeliveryDate.setText(Misc.getTimeZoneDate(load.getUnloadDateEnd()));
        holder.txtLoadDate.setText(Misc.getTimeZoneDate(load.getLoadDateFrom()));
        holder.txtLoadName.setText(load.getName());
        holder.txtLoadLocation.setText(load.getFromLocation().getCityName() + ", " + load.getFromLocation().getCountryName());
        holder.txtDeliveryLocation.setText(load.getToLocation().getCityName() + ", " + load.getToLocation().getCountryName());
        holder.txtLoadType.setText(load.getLoadType());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ActivityLoadDetails.class);
                Load load = loads.get(position);
                String strLoad = Serializer.getInstance().serializeLoad(load);
                intent.putExtra("load_info", strLoad);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return loads.size();
    }

    public void setLoads(ArrayList<Load> loads) {
        this.loads = loads;
    }

    public ArrayList<Load> getLoads(){
        return this.loads;
    }

    public void initializeAdapter() {
        this.loads = new ArrayList<>();
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.img_load_photo) RoundedImageView imgLoadPhoto;
        @Bind(R.id.txt_load_type) CustomTextView txtLoadName;
        @Bind(R.id.txt_sender) CustomTextView txtLoadType;
        @Bind(R.id.txt_budget_price) CustomTextView txtBudgetPrice;
        @Bind(R.id.txt_load_location) CustomTextView txtLoadLocation;
        @Bind(R.id.txt_load_date) CustomTextView txtLoadDate;
        @Bind(R.id.txt_delivery_location) CustomTextView txtDeliveryLocation;
        @Bind(R.id.txt_delivery_date) CustomTextView txtDeliveryDate;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}