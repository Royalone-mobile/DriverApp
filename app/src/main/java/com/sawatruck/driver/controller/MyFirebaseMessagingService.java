package com.sawatruck.driver.controller;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.utils.AppSettings;
import com.sawatruck.driver.utils.Logger;
import com.sawatruck.driver.view.activity.ActivityAdDetail;
import com.sawatruck.driver.view.activity.ActivityBidAccepted;
import com.sawatruck.driver.view.activity.ActivityBidBooked;
import com.sawatruck.driver.view.activity.ActivityBidOpened;
import com.sawatruck.driver.view.activity.ActivityLoadDetails;
import com.sawatruck.driver.view.activity.ActivityMessage;
import com.sawatruck.driver.view.activity.ActivityNotification;
import com.sawatruck.driver.view.activity.ActivityRating;


/**
 * Created by royal on 9/23/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    public static int notification_number = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

    }

    @Override
    public void handleIntent(Intent intent) {
        Logger.error("handleIntent");
        if(intent.getExtras()==null) return;
        if(!AppSettings.with(BaseApplication.getContext()).getNotificationSetting()) return;

        PendingIntent contentIntent;
        String Text= "";
        String Title = getString(R.string.app_name);
        try{
            String ID = intent.getStringExtra("ID");
            String ScreenName = intent.getStringExtra("ScreenName");
            Title = intent.getStringExtra("Title");
            Text   = intent.getStringExtra("Text");
            contentIntent = getPendingIntent(ID,ScreenName,intent);
        }catch (Exception e){
            e.printStackTrace();
            return;
        }

        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(BaseApplication.getContext()).setSmallIcon(R.mipmap.ic_sawatruck)
                        .setContentTitle(Title).setContentText(Text).setContentIntent(contentIntent);


        Intent deleteIntent = new Intent(this, DeleteNotificationReceiver.class);
        deleteIntent.putExtra("ID", intent.getStringExtra("ID"));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, deleteIntent, 0);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);
        mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        mBuilder.setAutoCancel(true);



        mBuilder.setDeleteIntent(pendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) BaseApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(Constant.FIREBASE_NOTIFY, mBuilder.build());



        playNotificationSound(BaseApplication.getContext());
    }

    public static void playNotificationSound(Context context) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context, notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PendingIntent getPendingIntent(String ID, String ScreenName, Intent fcmIntent) {
        Intent intent =new Intent(BaseApplication.getContext(), ActivityNotification.class);
        int Status = 0;

        if(fcmIntent.hasExtra("Status"))
            Status =  Integer.valueOf(fcmIntent.getStringExtra("Status"));
        Logger.error(ScreenName);
        switch (ScreenName) {
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
                if(fcmIntent.hasExtra("Title"))
                    title = fcmIntent.getStringExtra("Title");
                intent.putExtra(Constant.INTENT_USERNAME,title);
                intent.putExtra(Constant.INTENT_USER_ID, ID);
                break;
            case "RatingScreen":
                intent = new Intent(BaseApplication.getContext(), ActivityRating.class);
                intent.putExtra(Constant.INTENT_TRAVEL_ID, ID);
                break;
            case "TravelDetails":
                break;
        }

        PendingIntent contentIntent = PendingIntent.getActivity(BaseApplication.getContext(), notification_number, intent, 0);
        Logger.error("----------Notification Number----------------" + notification_number);
        notification_number++;
        return contentIntent;
    }
}