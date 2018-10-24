package com.sawatruck.driver.utils;


import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by SKY on 9/19/2017.
 */

public class HttpHelper {
    final static String TAG = "HttpHelper";
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType MEDIA_TYPE_IMAGE = MediaType.parse("image/*");
    static public void makeMultipartPostRequest(String url, LinkedHashMap<String, Object> params, final HttpResponseListener listener) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        for ( Map.Entry<String, Object> entry : params.entrySet() ) {
            Object  value = entry.getValue();
            if(value != null) {
                if(value instanceof File) {
                    builder.addFormDataPart(entry.getKey(), "imagefile.jpg", RequestBody.create(MEDIA_TYPE_IMAGE,(File)value));
                }else {
                    builder.addFormDataPart(entry.getKey(), (String) entry.getValue());
                }

            }


        }

        RequestBody formBody = builder.build();
        try{

            Request request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build();
            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if(listener != null)
                        listener.OnFailure(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) {
                    ResponseBody responseBody = response.body();
                    try{
                        if(listener != null)
                            listener.OnSuccess(responseBody.string());
                    }catch (Exception e) {
                        if(listener != null)
                            listener.OnFailure(e.getMessage());
                    }
                }
            });


        }catch (Exception e){
            if (listener != null)
                listener.OnFailure(e.getMessage());
        }

    }

    static public void makePurePostRequest(String url, LinkedHashMap<String, String> params,
                                           final HttpResponseListener listener){
        FormBody.Builder builder = new FormBody.Builder();
        // Add Params to Builder
        for ( Map.Entry<String, String> entry : params.entrySet() ) {
            if(entry.getValue() != null) {
                builder.add( entry.getKey(), entry.getValue() );
            }

        }
        RequestBody formBody = builder.build();
        try{

            Request request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build();
            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if(listener != null)
                        listener.OnFailure(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) {
                    ResponseBody responseBody = response.body();
                    try{
                        if(listener != null)
                            listener.OnSuccess(responseBody.string());
                    }catch (Exception e) {
                        if(listener != null)
                            listener.OnFailure(e.getMessage());
                    }
                }
            });


        }catch (Exception e){
            if (listener != null)
                listener.OnFailure(e.getMessage());
        }
    }

}
