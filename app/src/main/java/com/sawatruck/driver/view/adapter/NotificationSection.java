package com.sawatruck.driver.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.NotificationModel;
import com.sawatruck.driver.entities.User;
import com.sawatruck.driver.utils.AppSettings;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.Logger;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.utils.CircleTransformation;
import com.sawatruck.driver.utils.StringUtil;
import com.sawatruck.driver.utils.UserManager;
import com.sawatruck.driver.view.activity.ActivityAdDetail;
import com.sawatruck.driver.view.activity.ActivityBidAccepted;
import com.sawatruck.driver.view.activity.ActivityBidBooked;
import com.sawatruck.driver.view.activity.ActivityBidOpened;
import com.sawatruck.driver.view.activity.ActivityCollectPayment;
import com.sawatruck.driver.view.activity.ActivityLoadDetails;
import com.sawatruck.driver.view.activity.ActivityMessage;
import com.sawatruck.driver.view.activity.ActivityNotification;
import com.sawatruck.driver.view.activity.ActivityRating;
import com.sawatruck.driver.view.activity.ActivityTravel;
import com.sawatruck.driver.view.design.CustomTextView;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * Created by royal on 9/20/2017.
 */

public class NotificationSection extends StatelessSection {
    private Context context;
    private ArrayList<NotificationModel> notifications = new ArrayList<>();

    public NotificationSection(Context context) {
        super(R.layout.layout_section_header, R.layout.notification_item);
        this.context = context;
    }
    public NotificationSection(ArrayList<NotificationModel> notifications) {
        super(R.layout.layout_section_header, R.layout.notification_item);
        this.notifications = notifications;
    }

    public NotificationSection(Context context, ArrayList<NotificationModel> notifications) {
        super(R.layout.layout_section_header, R.layout.notification_item);
        this.notifications = notifications;
        this.context = context;
    }

    @Override
    public int getContentItemsTotal() {
        return notifications.size();
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
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

        if(notifications.size()>0)

            headerHolder.txtNotificationDate.setText(Misc.getNotificationSpanFromDate(context,Misc.getDateFromString(notifications.get(0).getNotification().getDate())));
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ItemViewHolder itemHolder = (ItemViewHolder) holder;

        final NotificationModel notificationModel = notifications.get(position);

        itemHolder.txtTime.setText(Misc.getTimeStringFromDate(Misc.getTimeZoneDate(notificationModel.getNotification().getDate())));
        itemHolder.txtMessage.setText(notificationModel.getNotification().getMessage());

        itemHolder.txtMessage.setSelected(true);

        itemHolder.itemView.setTag(notificationModel);

        try {
            BaseApplication.getPicasso().load(notificationModel.getNotification().getImageUrl()).placeholder(R.drawable.ico_user).transform(new CircleTransformation()).fit().into(itemHolder.imgSender);
        }catch (Exception e){
            e.printStackTrace();
        }

        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeSeen(notificationModel);
                handleNotification(notificationModel);
            }
        });
    }


    private void makeSeen(final NotificationModel notificationModel) {
        HttpUtil httpUtil = HttpUtil.getInstance();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, UserManager.with(context).getCurrentUser().getToken());

        httpUtil.put(Constant.MAKE_SEEN_NOTIFICATION_API + "/" + notificationModel.getID(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                notifications.remove(notificationModel);
                ActivityNotification.resetNotifications();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Logger.error("onFailure");
            }
        });
    }


    private void handleNotification(final NotificationModel notificationModel) {
        Intent intent = new Intent();
        int Status = 0;
        try {
            String ID = notificationModel.getNotification().getTargetID();

            if(notificationModel.getNotification().getStatus()!=null)
              Status = Integer.valueOf(notificationModel.getNotification().getStatus());

            Logger.error( "ScreenNAme= " + notificationModel.getNotification().getScreenName());
            switch (notificationModel.getNotification().getScreenName()) {
                case "BidDetails":
                    Logger.error("BidDetails");

                    switch (Status) {
                        case 5:
                            intent = new Intent(BaseApplication.getContext(), ActivityBidBooked.class);
                            break;
                        case 2:
                            intent = new Intent(BaseApplication.getContext(), ActivityBidAccepted.class);
                            break;
                        case 1:
                            intent = new Intent(BaseApplication.getContext(), ActivityBidOpened.class);
                            break;
                    }
                    intent.putExtra(Constant.INTENT_OFFER_ID, ID);
                    break;
                case "LoadDetails":
                    Logger.error("LoadDetails");
                    intent = new Intent(BaseApplication.getContext(), ActivityLoadDetails.class);
                    intent.putExtra(Constant.INTENT_LOAD_ID, ID);
                    break;
                case "AdvertisementDetails":
                    Logger.error("AdvertisementDetails");
                    intent = new Intent(BaseApplication.getContext(), ActivityAdDetail.class);
                    intent.putExtra(Constant.INTENT_ADVERTISEMENT_ID, ID);
                    break;
                case "AdBookingDetails":
                    Logger.error("AdBookingDetails");
                    intent = new Intent(BaseApplication.getContext(), ActivityAdDetail.class);
                    intent.putExtra(Constant.INTENT_ADVERTISEMENT_ID, ID);

                    break;
                case "ChatScreen":
                    Logger.error("ChatScreen");
                    intent = new Intent(BaseApplication.getContext(), ActivityMessage.class);
                    String title = "";
                    title = notificationModel.getNotification().getTitle();
                    intent.putExtra(Constant.INTENT_USERNAME, title);
                    intent.putExtra(Constant.INTENT_USER_ID, ID);
                    break;
                case "RatingScreen":
                    intent = new Intent(BaseApplication.getContext(), ActivityRating.class);
                    intent.putExtra(Constant.INTENT_TRAVEL_ID, ID);
                    break;
                case "TravelDetails":
                    showTravel(ID);
                    break;
                case "CollectPayment":
                    showCollectPayment(ID);
                    break;
            }
            context.startActivity(intent);
        }catch (Exception e) {

        }
    }

    private void showCollectPayment(final String travelID) {
        Logger.error(travelID);
        UserManager userManager = UserManager.with(BaseApplication.getContext());
        Logger.error(userManager.getCurrentUser().getToken());
        Intent intent =  new Intent(BaseApplication.getContext(), ActivityCollectPayment.class);
        intent.putExtra(Constant.INTENT_TRAVEL_ID, travelID);
        context.startActivity(intent);
    }

    private void showTravel(final String travelID) {
        HttpUtil.getInstance().get(Constant.GET_TRAVEL_BY_ID + "/" + travelID, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] responseBody) {
                try {
                    Logger.error("showTracking");
                    Logger.error(travelID);
                    String paramString = new String(responseBody);
                    paramString = StringUtil.escapeString(paramString);

                    Intent intent =  new Intent(BaseApplication.getContext(), ActivityTravel.class);
                    JSONObject jsonObject = new JSONObject(paramString);
                    intent.putExtra(Constant.INTENT_TRAVEL_ID, travelID);

                    if(jsonObject.has("TrackingStatus")) {
                        Integer trackingStatus = jsonObject.getInt("TrackingStatus");
                        intent.putExtra(Constant.INTENT_TRACKING_STATUS, trackingStatus);
                    } else
                        intent.putExtra(Constant.INTENT_TRACKING_STATUS, 0);

                    context.startActivity(intent);

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }

    public ArrayList<NotificationModel> getNotifications() {
        return notifications;
    }

    public void setNotifications(ArrayList<NotificationModel> notifications) {
        this.notifications = notifications;
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.img_sender) ImageView imgSender;
        @Bind(R.id.txt_message) CustomTextView txtMessage;
        @Bind(R.id.txt_time) CustomTextView txtTime;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.txt_notification_date) CustomTextView txtNotificationDate;

        public HeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);

        }
    }
}
