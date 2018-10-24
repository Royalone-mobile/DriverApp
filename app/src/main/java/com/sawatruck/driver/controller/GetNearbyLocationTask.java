package com.sawatruck.driver.controller;

import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.view.adapter.SelectLocationSection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by royal on 10/20/2017.
 */

public class GetNearbyLocationTask {

    SelectLocationSection locationsAdapter;
    protected boolean noMoreVideoPages = false;
    public String NextToken = null;


    public GetNearbyLocationTask(SelectLocationSection locationsAdapter){
        this.locationsAdapter = locationsAdapter;
    }

    public void execute(double latitude, double longitude) {
        if(NextToken == null){
            getAddress(latitude, longitude, "", false);
        }
        else {
            if(!noMoreVideoPages)
                getNextAddress();
        }
    }


    public void execute(double latitude, double longitude, String keyword) {
        if(NextToken == null){
            getAddress(latitude, longitude, keyword, true);
        }
        else {
            if(!noMoreVideoPages)
                getNextAddress();
        }
    }

    public void getNextAddress(){
        HttpUtil.getInstance().get("https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=" + Constant.GOOGLE_MAP_KEY +
                "&pagetoken=" + NextToken, new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ArrayList<NearbyLocation> locations = new ArrayList<>();
                String paramString = new String(responseBody);
                Log.println(Log.ASSERT,"TAG", paramString);
                try {
                    JSONObject jsonObject = new JSONObject(paramString);
                    Log.println(Log.ASSERT,"TAG", paramString);

                    NextToken = "";
                    if(jsonObject.has("next_page_token"))
                        NextToken = jsonObject.getString("next_page_token");

                    String strResults = jsonObject.getString("results");

                    if (NextToken.equals("")) {
                        noMoreVideoPages = true;
                    }

                    JSONArray jsonArray = new JSONArray(strResults);
                    for(int i = 0; i<jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String name = jsonObject1.getString("name");
                        String vicinity = jsonObject1.getString("vicinity");
                        JSONArray jsonArray1 = jsonObject1.getJSONArray("types");

                        boolean isLocality = false;
                        for(int j= 0; j<jsonArray1.length(); j++) {
                            String str  = jsonArray1.getString(j);
                            Log.println(Log.ASSERT, str, str);

                            if(str.equals("locality") || str.equals("political") || str.equals("sublocality"))
                                isLocality = true;

                        }
                        NearbyLocation location = new NearbyLocation();
                        location.setName(name);
                        location.setVincity(vicinity);
                        location.setLatitude(jsonObject1.getJSONObject("geometry").getJSONObject("location").getDouble("lat"));
                        location.setLongitude(jsonObject1.getJSONObject("geometry").getJSONObject("location").getDouble("lng"));
                        if(!isLocality)
                            locations.add(location);
                    }
                    locationsAdapter.appendList(locations);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }
    public void getAddress(Double latitude, Double longitude, String keyword, boolean bKeyword){
//        https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&key=AIzaSyDbyAkQGz9tSBdyHPagbcaDDRfxl9czPJc

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?radius=500&key=" + Constant.GOOGLE_MAP_KEY;
        url = url.concat("&location=").concat(String.valueOf(latitude)).concat(",").concat(String.valueOf(longitude));
        if(bKeyword)
            url = url.concat("&keyword=").concat(keyword);
        HttpUtil.getInstance().get(url, new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ArrayList<NearbyLocation> locations = new ArrayList<>();
                String paramString = new String(responseBody);
                Log.println(Log.ASSERT,"TAG", paramString);
                try {
                    JSONObject jsonObject = new JSONObject(paramString);
                    Log.println(Log.ASSERT,"TAG", paramString);
                    NextToken = "";
                    if(jsonObject.has("next_page_token"))
                        NextToken = jsonObject.getString("next_page_token");

                    String strResults = jsonObject.getString("results");

                    if (NextToken.equals("")) {
                        noMoreVideoPages = true;
                    }

                    JSONArray jsonArray = new JSONArray(strResults);
                    for(int i = 0; i<jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String name = jsonObject1.getString("name");
                        String vicinity = jsonObject1.getString("vicinity");
                        JSONArray jsonArray1 = jsonObject1.getJSONArray("types");

                        boolean isLocality = false;
                        for(int j= 0; j<jsonArray1.length(); j++) {
                            String str  = jsonArray1.getString(j);
                            Log.println(Log.ASSERT, str, str);
                            if(str.equals("locality") || str.equals("political") || str.equals("sublocality"))
                                isLocality = true;
                        }
                        NearbyLocation location = new NearbyLocation();
                        location.setName(name);
                        location.setVincity(vicinity);

                        location.setLatitude(jsonObject1.getJSONObject("geometry").getJSONObject("location").getDouble("lat"));
                        location.setLongitude(jsonObject1.getJSONObject("geometry").getJSONObject("location").getDouble("lng"));
                        if(!isLocality)
                            locations.add(location);
                    }
                    locationsAdapter.clearList();
                    locationsAdapter.appendList(locations);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    public boolean noMoreVideoPages() {
        return noMoreVideoPages;
    }

    public void reset() {
        NextToken = null;
        noMoreVideoPages = false;
    }
}
