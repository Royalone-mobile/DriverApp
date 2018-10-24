package com.sawatruck.driver.controller;

import android.content.Context;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.AppSettings;
import com.sawatruck.driver.utils.Logger;
import com.sawatruck.driver.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


/**
 * Created by royal on 8/26/2017.
 */

public class RegionCodeAPI {
    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }
    private RegionCodeAPI() {

    }
    private static RegionCodeAPI _instance = new RegionCodeAPI();

    public static RegionCodeAPI getInstance() {
        return _instance;
    }

    public void execute(){
        HttpUtil.getInstance().get("http://ip-api.com/json", new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String paramString = new String(responseBody);
                paramString = StringUtil.escapeString(paramString);
                try {
                    JSONObject jsonObject = new JSONObject(paramString);
                    String regionCode = jsonObject.getString("countryCode");
                    String city = jsonObject.getString("city");
                    if(!city.equals(""))
                        AppSettings.with(context).setCurrentCity(city);
                    AppSettings.with(context).setCurrentLat(jsonObject.getDouble("lat"));
                    AppSettings.with(context).setCurrentLng(jsonObject.getDouble("lon"));

                    if(!regionCode.equals("")) {
                        AppSettings.with(context).setRegionCode(regionCode);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Logger.error("RegionCodeAPI onFailure");
            }
        });
    }

}
