package com.sawatruck.driver.view.activity;

import android.support.v4.content.ContextCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.fuzzproductions.ratingbar.RatingBar;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.UserInfo;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.utils.NetUtil;
import com.sawatruck.driver.utils.Notice;
import com.sawatruck.driver.utils.StringUtil;
import com.sawatruck.driver.utils.UserManager;
import com.sawatruck.driver.view.design.CircularImage;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by royal on 8/21/2017.
 */

public class ActivityViewProfile extends BaseActivity {
    UserInfo userInfo = new UserInfo();

    @Bind(R.id.txt_full_name) TextView txtFullName;
    @Bind(R.id.txt_name) TextView txtCountryName;
    @Bind(R.id.txt_username) TextView txtUserName;
    @Bind(R.id.txt_bio) TextView txtBio;
    @Bind(R.id.txt_job_count) TextView txtJobCount;
    @Bind(R.id.txt_job) TextView txtJob;
    @Bind(R.id.txt_email_confirmed) TextView txtEmailConfirmed;
    @Bind(R.id.txt_phone_confirmed) TextView txtPhoneConfirmed;
    @Bind(R.id.txt_terms_confirmed) TextView txtTermsConfirmed;
    @Bind(R.id.txt_account_create) TextView txtAccountCreate;
    @Bind(R.id.txt_last_login) TextView txtLastLogin;
    @Bind(R.id.rating_success) RatingBar ratingJobSuccess;
    @Bind(R.id.img_user_avatar) CircularImage imgAvatar;

    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_profile,null);
        ButterKnife.bind(this, view);

        return view;
    }
    @Override
    public void onResume(){
        super.onResume();
        setAppTitle(getString(R.string.title_profile));
        showNavHome(false);
        getProfile();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getProfile(){
        HttpUtil httpUtil = HttpUtil.getInstance();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION,         UserManager.with(this).getCurrentUser().getToken());

        HttpUtil.getInstance().get(Constant.USER_VIEWPROFILE_API, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String paramString = new String(responseBody);
                paramString = StringUtil.escapeString(paramString);
                try {
                    JSONObject jsonObject = new JSONObject(paramString);
                    JSONObject contentObject =  jsonObject.getJSONObject("Object");
                    userInfo = BaseApplication.getGson().fromJson(contentObject.toString(), UserInfo.class);
                    setContent();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Misc.showResponseMessage(responseBody);
            }
        });
    }

    public void setContent(){
        txtFullName.setText(userInfo.getFullName());
        txtCountryName.setText(userInfo.getCountryName());
        txtUserName.setText(userInfo.getUserName());
        txtBio.setText(userInfo.getDetails());
        txtJobCount.setText(String.valueOf(userInfo.getCompletedJobs()));
        txtJob.setText(userInfo.getCompletedJobs()>1?getResources().getString(R.string.jobs):getResources().getString(R.string.job));
        txtAccountCreate.setText(userInfo.getCreateDate());
        txtLastLogin.setText(userInfo.getLastLoginDate());
        ratingJobSuccess.setRating(Float.parseFloat(userInfo.getRating())/20.0f);


        try {
            BaseApplication.getPicasso().load(userInfo.getImageUrl()).placeholder(R.drawable.ico_truck).into(imgAvatar);
        }catch (Exception e){
            e.printStackTrace();
        }


        if(userInfo.isEmailConfirmed()){
            txtEmailConfirmed.setText(getString(R.string.verified));
            txtEmailConfirmed.setTextColor(ContextCompat.getColor(ActivityViewProfile.this.context, R.color.colorDarkGreen));
        }
        else {
            txtEmailConfirmed.setText(getString(R.string.not_verified));
            txtEmailConfirmed.setTextColor(getResources().getColor(R.color.colorRed));
        }

        if(userInfo.isPhoneNumberConfirmed()){
            txtPhoneConfirmed.setText(getString(R.string.verified));
            txtPhoneConfirmed.setTextColor(getResources().getColor(R.color.colorDarkGreen));
        }
        else{
            txtPhoneConfirmed.setText(getString(R.string.not_verified));
            txtPhoneConfirmed.setTextColor(getResources().getColor(R.color.colorRed));
        }

        if(userInfo.isTermsAccept()){
            txtTermsConfirmed.setText(getString(R.string.verified));
            txtTermsConfirmed.setTextColor(getResources().getColor(R.color.colorDarkGreen));
        }
        else{
            txtTermsConfirmed.setText(getString(R.string.not_verified));
            txtTermsConfirmed.setTextColor(getResources().getColor(R.color.colorRed));
        }
    }
}

