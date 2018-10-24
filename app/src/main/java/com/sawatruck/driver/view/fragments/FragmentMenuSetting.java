package com.sawatruck.driver.view.fragments;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;

import com.codemybrainsout.ratingdialog.RatingDialog;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.User;
import com.sawatruck.driver.utils.AppSettings;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.Logger;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.utils.Notice;
import com.sawatruck.driver.utils.UserManager;
import com.sawatruck.driver.view.activity.ActivityChangePassword;
import com.sawatruck.driver.view.activity.ActivityEditProfile;
import com.sawatruck.driver.view.activity.ActivityViewProfile;
import com.sawatruck.driver.view.activity.BaseActivity;
import com.sawatruck.driver.view.activity.MainActivity;
import com.sawatruck.driver.view.design.CustomTextView;
import com.suke.widget.SwitchButton;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by royal on 8/19/2017.
 */


public class FragmentMenuSetting extends BaseFragment implements View.OnClickListener {
    @Bind(R.id.btn_edit_profile) View btnEditProfile;
    @Bind(R.id.btn_change_password) View btnChangePassword;
    @Bind(R.id.btn_language) View btnLanguage;
    @Bind(R.id.btn_rate_app) View btnRateApp;
    @Bind(R.id.switch_notification) SwitchButton switchNotification;
    @Bind(R.id.btn_view_profile) View btnViewProfile;
    @Bind(R.id.btn_verify_email) View btnVerifyEmail;
    @Bind(R.id.layout_verify_email) View layoutVerifyEmail;
    @Bind(R.id.txt_language) CustomTextView txtLanguage;
    public static final String TAG = FragmentMenuSetting.class.getSimpleName();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this,view);

        BaseActivity baseActivity = (BaseActivity)getActivity();
        baseActivity.setAppTitle(getResources().getString(R.string.title_setting));
        baseActivity.showOptions(false);

        btnViewProfile.setOnClickListener(this);
        btnEditProfile.setOnClickListener(this);
        btnChangePassword.setOnClickListener(this);
        btnLanguage.setOnClickListener(this);
        btnRateApp.setOnClickListener(this);
        btnVerifyEmail.setOnClickListener(this);

        switchNotification.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                AppSettings.with(getContext()).setNotificationSetting(isChecked);
            }
        });

        if(AppSettings.with(getContext()).getNotificationSetting())
            switchNotification.setChecked(true);
        else
            switchNotification.setChecked(false);

        if(AppSettings.with(BaseApplication.getContext()).getLangCode().equals(getString(R.string.locale_arabic))){
            txtLanguage.setText(R.string.arabic);
        }
        else if(AppSettings.with(BaseApplication.getContext()).getLangCode().equals(getString(R.string.locale_english))){
            txtLanguage.setText(R.string.english);
        }


        if(UserManager.with(getContext()).getCurrentUser().isEmailConfirmed())
            layoutVerifyEmail.setVisibility(View.GONE);
        else
            layoutVerifyEmail.setVisibility(View.VISIBLE);

       checkEmailVerified();

        return view;
    }

    private void checkEmailVerified(){
        HttpUtil httpUtil = HttpUtil.getInstance();
        final User user  = UserManager.with(getContext()).getCurrentUser();

        RequestParams params = new RequestParams();
        params.put("email", user.getEmail());

        httpUtil.get(Constant.IS_EMAIL_VERIFIED_API, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] responseBody) {
                user.setEmailConfirmed(true);

                UserManager.with(getContext()).setCurrentUser(user);

                if(UserManager.with(getContext()).getCurrentUser().isEmailConfirmed())
                    layoutVerifyEmail.setVisibility(View.GONE);
                else
                    layoutVerifyEmail.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] errorString, Throwable throwable) {
                user.setEmailConfirmed(false);
                layoutVerifyEmail.setVisibility(View.VISIBLE);
            }
        });
    }



    @Override
    public void onClick(View v) {
        int id =v.getId();
        Intent intent;
        switch (id) {
            case R.id.btn_edit_profile:
                intent = new Intent(getActivity(), ActivityEditProfile.class);
                startActivity(intent);
                break;
            case R.id.btn_change_password:
                intent = new Intent(getActivity(), ActivityChangePassword.class);
                startActivity(intent);
                break;
            case R.id.btn_language:
                alertSelectLanguage();
                break;
            case R.id.btn_rate_app:
                rateApp();
                break;
            case R.id.btn_view_profile:
                intent = new Intent(getActivity(), ActivityViewProfile.class);
                startActivity(intent);
                break;
            case R.id.btn_verify_email:
                verifyEmail();
                break;
        }
    }

    private void verifyEmail() {
        VerifyEmailAsyncTask  verifyEmailAsyncTask = new VerifyEmailAsyncTask();

        JSONObject jsonObject = new JSONObject();
        String json = "";
        try {
            jsonObject.put("Email", UserManager.with(getContext()).getCurrentUser().getEmail());
            json = jsonObject.toString();
            Logger.error(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        verifyEmailAsyncTask.execute(json);
    }

    private class VerifyEmailAsyncTask extends AsyncTask<String, Void, Void> {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(String... params) {
            String json = params[0];



            HttpUtil.postBody( Constant.VERIFY_EMAIL_API, json, new HttpUtil.ResponseHandler() {
                @Override
                public void onSuccess(final String responseBody) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Misc.showResponseMessage(responseBody);
                        }
                    });
                }

                @Override
                public void onFailure(final String errorString) {
                  getActivity().runOnUiThread(new Runnable() {
                      @Override
                      public void run() {
                          Misc.showResponseMessage(errorString);
                      }
                  });
                }
            });
            return null;
        }
    }
    private void rateApp(){
        final RatingDialog ratingDialog = new RatingDialog.Builder(getActivity())
                .onRatingBarFormSumbit(new RatingDialog.Builder.RatingDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {

                    }
                }).build();


        ratingDialog.show();
        ratingDialog.getRatingBarView().setRating(5.0f);
    }

    public void alertSelectLanguage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final View viewSelLang = getActivity().getLayoutInflater().inflate(R.layout.dialog_sel_language,null);

        final RadioButton radioArabic = (RadioButton)viewSelLang.findViewById(R.id.radio_arabic);
        final RadioButton radioEnglish = (RadioButton)viewSelLang.findViewById(R.id.radio_english);
        final RadioButton radioSpanish = (RadioButton)viewSelLang.findViewById(R.id.radio_spanish);

        final Button btnOk = (Button)viewSelLang.findViewById(R.id.btn_ok);
        final Button btnCancel = (Button)viewSelLang.findViewById(R.id.btn_cancel);

        builder.setView(viewSelLang);

        final AlertDialog alertDialog = builder.create();

        alertDialog.show();

        if(AppSettings.with(BaseApplication.getContext()).getLangCode().equals(getString(R.string.locale_arabic))){
            radioArabic.setChecked(true);
        }
        else if(AppSettings.with(BaseApplication.getContext()).getLangCode().equals(getString(R.string.locale_english))){
            radioEnglish.setChecked(true);
        }

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioArabic.isChecked()) {
                    AppSettings.with(BaseApplication.getContext()).setLangCode(getString(R.string.locale_arabic));
                    txtLanguage.setText(R.string.arabic);
                }
                else if(radioEnglish.isChecked()){
                    AppSettings.with(BaseApplication.getContext()).setLangCode(getString(R.string.locale_english));
                    txtLanguage.setText(R.string.english);
                } else if(radioSpanish.isChecked()) {
                    AppSettings.with(BaseApplication.getContext()).setLangCode(getString(R.string.locale_spanish));
                    txtLanguage.setText(R.string.spanish);
                }
                Misc.applyLocale(BaseApplication.getContext());
                Notice.show(getString(R.string.change_language_sucess));
                alertDialog.dismiss();
                getActivity().finish();
                Intent intent = new Intent(BaseApplication.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }
}

