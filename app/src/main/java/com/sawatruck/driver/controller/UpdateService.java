package com.sawatruck.driver.controller;

/**
 * Created by royal on 11/30/2017.
 */


import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.entities.UpdateApk;
import com.sawatruck.driver.utils.AppSettings;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.Logger;
import com.sawatruck.driver.utils.StringUtil;
import com.sawatruck.driver.view.activity.MainActivity;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by royalone on 2017-03-22.
 */

public class UpdateService extends Service {
    Handler handler = new Handler();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        handler.removeCallbacks(updateRunnable);
    }
    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            getUpdateVersion();
        }
    };

    private Runnable newPointRunnable = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(newPointRunnable,60000*5);
            Logger.error("newPointRunnable");
            if(LocationUpdateService.getTravelID().equals(""))
                return;
            LatLng currentLatLng = new LatLng(AppSettings.with(BaseApplication.getContext()).getCurrentLat(), AppSettings.with(BaseApplication.getContext()).getCurrentLng());
            sendLocationToServer(currentLatLng);
        }
    };


    public void sendLocationToServer(LatLng currentLatLng){
        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("TravelID",LocationUpdateService.getTravelID());

            if(LocationUpdateService.getServiceType() == 1){
                jsonObject.put("Status","OnThePickUpWay");
            }else {
                jsonObject.put("Status","OnTheWay");
            }
//			Logger.error("sendLocationToServer");
//			Logger.error(currentLatLng.latitude + "," + currentLatLng.longitude);
            jsonObject.put("Lat",currentLatLng.latitude);
            jsonObject.put("long",currentLatLng.longitude);

            AddNewPointAsyncTask locationAsyncTask = new AddNewPointAsyncTask();
            locationAsyncTask.execute( jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class AddNewPointAsyncTask extends AsyncTask<String, Void, Void> {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(String... params) {
            String json = params[0];

            HttpUtil.postBody(Constant.ADD_NEW_POINT_API, json, new HttpUtil.ResponseHandler() {
                @Override
                public void onSuccess(final String responseBody) {
                }

                @Override
                public void onFailure(final String errorString) {
                    Logger.error(errorString);
                }

            });
            return null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.postDelayed(updateRunnable,3000);
        handler.post(newPointRunnable);
        return START_NOT_STICKY;
    }

    private void getUpdateVersion(){
        HttpUtil.getInstance().get("http://192.168.1.125/apk_update.json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] responseBody) {
                try {
                    Logger.error("getUpdateVersion onSuccess");
                    String paramString = new String(responseBody);
                    paramString = StringUtil.escapeString(paramString);
                    JSONObject responseObject = new JSONObject(paramString);
                    UpdateApk updateApk  = BaseApplication.getGson().fromJson(responseObject.toString(), UpdateApk.class);
                    double latestVersion = Double.valueOf(updateApk.getVersion());
                    double currentVersion = Double.valueOf(getVersionName(BaseApplication.getContext()));

                    handler.removeCallbacks(updateRunnable);
                    if(latestVersion>currentVersion) {
                        MainActivity.installNewVersion(updateApk.getUrl());
                        installNewVersion(updateApk.getUrl());
                    }
                }catch (Exception e) {
                    handler.postDelayed(updateRunnable,3000);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Logger.error("getUpdateVersion onFailure");
                handler.postDelayed(updateRunnable,3000);
            }

        });
    }

    private void installNewVersion(String url){
        Intent updateIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(url));

        updateIntent.setDataAndType(Uri.parse(url),
                "application/vnd.android.package-archive");
        updateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

//        startActivity(updateIntent);
    }

    public static int getVersionCode(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException ex) {}
        return 0;
    }

    public static String getVersionName(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException ex) {}
        return "";
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopSelf();
    }

}
