package com.sawatruck.driver.utils;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.Foreground;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.User;

import java.io.InputStream;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.methods.HttpPut;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class HttpUtil {
    private static final int TIME_OUT = 60000;
    private final AsyncHttpClient client = new AsyncHttpClient();

    private static HttpUtil _instance = new HttpUtil();
    public static HttpUtil getInstance() {
        return _instance;
    }


    public HttpUtil(){

        client.setTimeout(TIME_OUT);
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");

    }
    public void get(String paramString, AsyncHttpResponseHandler paramAsyncHttpResponseHandler) {
        try {
            if (!NetUtil.isNetworkAvailable(BaseApplication.getContext())) {
                if(UserManager.with(BaseApplication.getContext()).getUserType() == 1)
                    if(Foreground.get().isForeground())
                    {
                        Logger.error("----------------------isForeground-----------------------");
                        Notice.show(R.string.network_unavailable);
                    }

                return;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        if(AppSettings.with(BaseApplication.getContext()).getLangCode().equals(BaseApplication.getContext().getString(R.string.locale_arabic))){
            client.addHeader("Accept-Language", "ar-Global");
        }
        else if(AppSettings.with(BaseApplication.getContext()).getLangCode().equals(BaseApplication.getContext().getString(R.string.locale_english))){
            client.addHeader("Accept-Language", "en-Global");
        }

        client.get(paramString, paramAsyncHttpResponseHandler);
    }

    public void get(String paramString, BinaryHttpResponseHandler paramBinaryHttpResponseHandler) {
        client.get(paramString, paramBinaryHttpResponseHandler);
    }

    public void get(String paramString, JsonHttpResponseHandler paramJsonHttpResponseHandler) {
        client.get(paramString, paramJsonHttpResponseHandler);
    }

    public void get(String paramString, RequestParams paramRequestParams, AsyncHttpResponseHandler paramAsyncHttpResponseHandler) {
        try {
            if (!NetUtil.isNetworkAvailable(BaseApplication.getContext())) {
                if(UserManager.with(BaseApplication.getContext()).getUserType() == 1)
                    if(Foreground.get().isForeground())
                    {
                        Logger.error("----------------------isForeground-----------------------");
                        Notice.show(R.string.network_unavailable);
                    }
                return;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        if(AppSettings.with(BaseApplication.getContext()).getLangCode().equals(BaseApplication.getContext().getString(R.string.locale_arabic))){
            client.addHeader("Accept-Language", "ar-Global");
        }
        else if(AppSettings.with(BaseApplication.getContext()).getLangCode().equals(BaseApplication.getContext().getString(R.string.locale_english))){
            client.addHeader("Accept-Language", "en-Global");
        }

        client.get(paramString, paramRequestParams, paramAsyncHttpResponseHandler);
    }

    public void get(String paramString, RequestParams paramRequestParams, JsonHttpResponseHandler paramJsonHttpResponseHandler) {
        client.get(paramString, paramRequestParams, paramJsonHttpResponseHandler);
    }

    public AsyncHttpClient getClient() {
        return client;
    }

    public void post(String paramString, AsyncHttpResponseHandler paramAsyncHttpResponseHandler) {
        try {
            if (!NetUtil.isNetworkAvailable(BaseApplication.getContext())) {
                if(UserManager.with(BaseApplication.getContext()).getUserType() == 1)
                    if(Foreground.get().isForeground())
                    {
                        Logger.error("----------------------isForeground-----------------------");
                        Notice.show(R.string.network_unavailable);
                    }
                return;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        if(AppSettings.with(BaseApplication.getContext()).getLangCode().equals(BaseApplication.getContext().getString(R.string.locale_arabic))){
            client.addHeader("Accept-Language", "ar-Global");
        }
        else if(AppSettings.with(BaseApplication.getContext()).getLangCode().equals(BaseApplication.getContext().getString(R.string.locale_english))){
            client.addHeader("Accept-Language", "en-Global");
        }

        client.post(paramString, paramAsyncHttpResponseHandler);
    }

    public void post(String paramString, BinaryHttpResponseHandler paramBinaryHttpResponseHandler) {
        client.post(paramString, paramBinaryHttpResponseHandler);
    }

    public void post(String paramString, JsonHttpResponseHandler paramJsonHttpResponseHandler) {
        client.post(paramString, paramJsonHttpResponseHandler);
    }

    public void post(String paramString, RequestParams paramRequestParams, AsyncHttpResponseHandler paramAsyncHttpResponseHandler) {
        try {
            if (!NetUtil.isNetworkAvailable(BaseApplication.getContext())) {
                if(UserManager.with(BaseApplication.getContext()).getUserType() == 1)
                    if(Foreground.get().isForeground())
                    {
                        Logger.error("----------------------isForeground-----------------------");
                        Notice.show(R.string.network_unavailable);
                    }
                return;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        if(AppSettings.with(BaseApplication.getContext()).getLangCode().equals(BaseApplication.getContext().getString(R.string.locale_arabic))){
            client.addHeader("Accept-Language", "ar-Global");
        }
        else if(AppSettings.with(BaseApplication.getContext()).getLangCode().equals(BaseApplication.getContext().getString(R.string.locale_english))){
            client.addHeader("Accept-Language", "en-Global");
        }

        client.post(paramString, paramRequestParams, paramAsyncHttpResponseHandler);
    }

    public void postBody(Context context, String url, HttpEntity httpEntity, String contentType,  AsyncHttpResponseHandler paramAsyncHttpResponseHandler){
        client.post(context,url,httpEntity,contentType,paramAsyncHttpResponseHandler);
    }


    public void put(String paramString, RequestParams paramRequestParams, AsyncHttpResponseHandler paramAsyncHttpResponseHandler) {
        client.put(paramString, paramRequestParams, paramAsyncHttpResponseHandler);
    }

    public void put(String paramString, AsyncHttpResponseHandler paramAsyncHttpResponseHandler) {
        client.put(paramString, paramAsyncHttpResponseHandler);
    }


    public void post(String paramString, RequestParams paramRequestParams, JsonHttpResponseHandler paramJsonHttpResponseHandler) {
        client.post(paramString, paramRequestParams, paramJsonHttpResponseHandler);
    }

    public static void putBody(String url, String body, ResponseHandler responseHandler){
        try {
            if (!NetUtil.isNetworkAvailable(BaseApplication.getContext())) {
                if(UserManager.with(BaseApplication.getContext()).getUserType() == 1)
                    if(Foreground.get().isForeground())
                    {
                        Logger.error("----------------------isForeground-----------------------");
                        Notice.show(R.string.network_unavailable);
                    }
                return;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        HttpClient httpclient = new DefaultHttpClient();
        HttpPut httpPut = new HttpPut(url);
        try {
            StringEntity se = new StringEntity(body);
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httpPut.setEntity(se);

            se.setContentEncoding(HTTP.UTF_8);
            httpPut.addHeader("Accept", "application/json");
            httpPut.addHeader("Content-type", "application/json");

            if(AppSettings.with(BaseApplication.getContext()).getLangCode().equals(BaseApplication.getContext().getString(R.string.locale_arabic))){
                httpPut.addHeader("Accept-Language", "ar-Global");
            }
            else if(AppSettings.with(BaseApplication.getContext()).getLangCode().equals(BaseApplication.getContext().getString(R.string.locale_english))){
                httpPut.addHeader("Accept-Language", "en-Global");
            }


            User user  = UserManager.with(BaseApplication.getContext()).getCurrentUser();

            if(user!=null) {
                if(user.getToken()!=null)
                    httpPut.setHeader(Constant.PARAM_AUTHORIZATION, user.getToken());
            }

            HttpResponse httpResponse = httpclient.execute(httpPut);

            InputStream inputStream = httpResponse.getEntity().getContent();
            String response = StringUtil.getStringFromInputStream(inputStream);
            response = StringUtil.escapeString(response);

            if(httpResponse.getStatusLine().getStatusCode()==200||httpResponse.getStatusLine().getStatusCode()==201){
                responseHandler.onSuccess(response);
            }
            else {
                responseHandler.onFailure(response);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface ResponseHandler{
        void onSuccess(String responseBody);
        void onFailure(String errorString);
    }

    public static void postBody(String url, String body, ResponseHandler responseHandler){
        try {
            if (!NetUtil.isNetworkAvailable(BaseApplication.getContext())) {
                if(UserManager.with(BaseApplication.getContext()).getUserType() == 1)
                    if(Foreground.get().isForeground())
                    {
                        Logger.error("----------------------isForeground-----------------------");
                        Notice.show(R.string.network_unavailable);
                    }
                return;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        try {
            StringEntity se = new StringEntity(body, HTTP.UTF_8);

//                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httpPost.setEntity(se);

            httpPost.addHeader("Accept", "application/json");
            httpPost.addHeader("Content-type", "application/json");


            if(AppSettings.with(BaseApplication.getContext()).getLangCode().equals(BaseApplication.getContext().getString(R.string.locale_arabic))){
                httpPost.addHeader("Accept-Language", "ar-Global");
            }
            else if(AppSettings.with(BaseApplication.getContext()).getLangCode().equals(BaseApplication.getContext().getString(R.string.locale_english))){
                httpPost.addHeader("Accept-Language", "en-Global");
            }


            User user  = UserManager.with(BaseApplication.getContext()).getCurrentUser();
            if(user!=null) {
                if(user.getToken()!=null)
                    httpPost.setHeader(Constant.PARAM_AUTHORIZATION, user.getToken());
            }
            HttpResponse httpResponse = httpclient.execute(httpPost);


            InputStream inputStream = httpResponse.getEntity().getContent();
            String response = StringUtil.getStringFromInputStream(inputStream);
            response = StringUtil.escapeString(response);

            if(httpResponse.getStatusLine().getStatusCode()==200||httpResponse.getStatusLine().getStatusCode()==201){
                responseHandler.onSuccess(response);
            }
            else {
                responseHandler.onFailure(response);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
