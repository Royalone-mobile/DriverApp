package com.sawatruck.driver.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.entities.NotificationModel;
import com.sawatruck.driver.utils.AppSettings;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.Logger;
import com.sawatruck.driver.utils.UserManager;
import com.sawatruck.driver.view.activity.ActivityNotification;

import cz.msebera.android.httpclient.Header;
import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by royal on 1/11/2018.
 */

public class DeleteNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.error("Deleted Notification");

        try {
            makeSeen(intent.getStringExtra("ID"));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void makeSeen(String NotificationID) {
        HttpUtil httpUtil = HttpUtil.getInstance();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, UserManager.with(BaseApplication.getContext()).getCurrentUser().getToken());

        httpUtil.put(Constant.MAKE_SEEN_NOTIFICATION_API + "/" + NotificationID, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                int notificationCount = AppSettings.with(BaseApplication.getContext()).getNotificationCount();
                notificationCount = notificationCount - 1;
                AppSettings.with(BaseApplication.getContext()).setNotificationCount(notificationCount);
                ShortcutBadger.applyCount(BaseApplication.getContext(), notificationCount);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }
}
