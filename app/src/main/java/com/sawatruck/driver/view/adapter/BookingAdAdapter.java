package com.sawatruck.driver.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.fuzzproductions.ratingbar.RatingBar;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.AdvertisementBooking;
import com.sawatruck.driver.entities.User;
import com.sawatruck.driver.utils.ActivityUtil;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.Logger;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.utils.UserManager;
import com.sawatruck.driver.view.activity.ActivityAdDetail;
import com.sawatruck.driver.view.activity.ActivityMessage;
import com.sawatruck.driver.view.activity.MainActivity;
import com.sawatruck.driver.view.design.CircularImage;
import com.sawatruck.driver.view.design.CustomTextView;
import com.sawatruck.driver.view.fragments.FragmentMenuAd;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by royal on 9/21/2017.
 */


public class BookingAdAdapter extends RecyclerView.Adapter<BookingAdAdapter.MyViewHolder> {
    private List<AdvertisementBooking> advertisementBookings = new ArrayList<>();
    private Context context;
    @Override
    public BookingAdAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.booking_ad_item, parent, false);
        return new BookingAdAdapter.MyViewHolder(v);
    }

    public BookingAdAdapter(Context context) {
        this.context = context;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final AdvertisementBooking advertisementBooking = advertisementBookings.get(position);
        try {
            BaseApplication.getPicasso().load(advertisementBooking.getUserImageUrl()).placeholder(R.drawable.ico_truck).into(holder.imgAvatar);

            Logger.error(advertisementBooking.getUserRating());

            float rating = Float.valueOf(advertisementBooking.getUserRating())/100.0f;
            Logger.error("calculated rating = " + rating);
            holder.ratingLoader.setRating(Float.valueOf(advertisementBooking.getUserRating())/20.0f);
        }catch (Exception e){
            e.printStackTrace();
        }
        holder.txtUserName.setText(advertisementBooking.getUserFullName());


        holder.btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ActivityMessage.class);
                intent.putExtra(Constant.INTENT_USER_ID, advertisementBooking.getUserID());
                intent.putExtra(Constant.INTENT_USERNAME, advertisementBooking.getUserFullName());
                ActivityUtil.goOtherActivityFlipTransition(context,intent);
            }
        });

        holder.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpUtil httpUtil = HttpUtil.getInstance();
                User user  = UserManager.with(context).getCurrentUser();
                httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());

                HttpUtil.getInstance().put(Constant.REJECT_AD_BOOKING_API + "/" + advertisementBooking.getID(), new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        ActivityAdDetail activityAdDetail = (ActivityAdDetail)context;
                        activityAdDetail.finish();

                        FragmentMenuAd.Initial_TAB  = 2;
                        MainActivity.applyAddAd();

                        Misc.showResponseMessage(responseBody);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Misc.showResponseMessage(responseBody);
                    }
                });
            }
        });

        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpUtil httpUtil = HttpUtil.getInstance();
                User user  = UserManager.with(context).getCurrentUser();
                httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());
                RequestParams requestParams = new RequestParams();
                requestParams.put("id", advertisementBooking.getID());
                HttpUtil.getInstance().put(Constant.ACCEPT_AD_BOOKING_API +"/" + advertisementBooking.getID(), new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        ActivityAdDetail activityAdDetail = (ActivityAdDetail)context;
                        activityAdDetail.finish();
                        FragmentMenuAd.Initial_TAB  = 0;
                        MainActivity.applyAddAd();
                        Misc.showResponseMessage(responseBody);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Misc.showResponseMessage(responseBody);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return advertisementBookings.size();
    }

    public void setAdvertisementBookings(List<AdvertisementBooking> advertisementBookings) {
        this.advertisementBookings = advertisementBookings;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.img_avatar) CircularImage imgAvatar;
        @Bind(R.id.txt_username) CustomTextView txtUserName;
        @Bind(R.id.btn_message) Button btnMessage;
        @Bind(R.id.btn_reject) Button btnReject;
        @Bind(R.id.btn_accept) Button btnAccept;
        @Bind(R.id.rating_loader) RatingBar ratingLoader;
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}