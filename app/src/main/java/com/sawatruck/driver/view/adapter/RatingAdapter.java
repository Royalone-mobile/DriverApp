package com.sawatruck.driver.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fuzzproductions.ratingbar.RatingBar;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.Rating;
import com.sawatruck.driver.view.design.CircularImage;
import com.sawatruck.driver.view.design.CustomTextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by royal on 8/20/2017.
 */


public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.MyViewHolder> {
    private ArrayList<Rating> ratingList = new ArrayList<>();

    private Context context;

    @Override
    public RatingAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.rating_list_item, parent, false);
        return new RatingAdapter.MyViewHolder(v);
    }

    public RatingAdapter() {

    }

    public RatingAdapter(Context context) {
        this.context = context;
    }

    @Override
    public void onBindViewHolder(RatingAdapter.MyViewHolder holder, int position) {
        Rating rating = ratingList.get(position);
        holder.txtUserName.setText(rating.getName());
        holder.txtDate.setText(rating.getDate());
        holder.txtMessage.setText(rating.getMsg());
        holder.ratingSuccess.setRating(Float.valueOf(rating.getRank())/20.0f);

        try {
            BaseApplication.getPicasso().load(rating.getImgUrl()).placeholder(R.drawable.ico_user).fit().into(holder.imgAvatar);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return ratingList.size();
    }

    public ArrayList<Rating> getRatingList() {
        return ratingList;
    }

    public void setRatingList(ArrayList<Rating> ratingList) {
        this.ratingList = ratingList;
    }

    public void initializeAdapter() {
        this.ratingList = new ArrayList<>();
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.txt_username) CustomTextView txtUserName;
        @Bind(R.id.txt_date) CustomTextView txtDate;
        @Bind(R.id.txt_message) CustomTextView txtMessage;
        @Bind(R.id.img_avatar) CircularImage imgAvatar;
        @Bind(R.id.rating_success) RatingBar ratingSuccess;
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}