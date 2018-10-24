package com.sawatruck.driver.view.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.utils.AppSettings;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.Logger;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.utils.NetUtil;
import com.sawatruck.driver.utils.Notice;
import com.sawatruck.driver.utils.UserManager;
import com.sawatruck.driver.view.design.CustomEditText;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ActivityVerifyAccount extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.btn_verify_account) TextView btnVerifyAccount;
    @Bind(R.id.edit_Code1) CustomEditText editCode1;
    @Bind(R.id.edit_Code2) CustomEditText editCode2;
    @Bind(R.id.edit_Code3) CustomEditText editCode3;
    @Bind(R.id.edit_Code4) CustomEditText editCode4;

    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_verifyaccount,null);
        ButterKnife.bind(this, view);

        editCode1.addTextChangedListener(textWatcher);
        editCode2.addTextChangedListener(textWatcher);
        editCode3.addTextChangedListener(textWatcher);
        editCode4.addTextChangedListener(textWatcher);


        editCode1.setOnClickListener(this);
        editCode2.setOnClickListener(this);
        editCode3.setOnClickListener(this);
        editCode4.setOnClickListener(this);

        btnVerifyAccount.setOnClickListener(this);


        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

        setAppTitle(getString(R.string.title_verify_account));
        showNavHome(false);
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_verify_account:
                VerifyAccountAsyncTask verifyAccountAsyncTask = new VerifyAccountAsyncTask();
                btnVerifyAccount.setEnabled(false);
                hideKeyboard();
                verifyAccountAsyncTask.execute();
                break;
            case R.id.edit_Code1:
                editCode1.requestFocus();
                break;
            case R.id.edit_Code2:
                editCode2.requestFocus();
                break;
            case R.id.edit_Code3:
                editCode3.requestFocus();
                break;
            case R.id.edit_Code4:
                editCode4.requestFocus();
                break;
        }
    }


    private class VerifyAccountAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            verifyAccount();
            return null;
        }
    }

    private  void verifyAccount(){
        String phoneCode = editCode1.getText().toString() + editCode2.getText().toString() + editCode3.getText().toString() + editCode4.getText().toString();

        Logger.error("--------phoneCode-------------" + phoneCode);
        try {
            JSONObject json = new JSONObject();
            json.put("PhoneConfirmationCode", phoneCode);
            String email = getIntent().getStringExtra("email");
            json.put("email", email);
            HttpUtil.postBody(Constant.VERIFY_PHONE_API, json.toString(), new HttpUtil.ResponseHandler() {
                @Override
                public void onSuccess(final String responseBody) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnVerifyAccount.setEnabled(true);
                            Misc.showResponseMessage(responseBody);
                            UserManager.with(getApplicationContext()).setUserType(1);
                            Intent intent = new Intent(ActivityVerifyAccount.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }

                @Override
                public void onFailure(final String errorString) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Misc.showResponseMessage(errorString);
                            btnVerifyAccount.setEnabled(true);
                        }
                    });

                }

            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    final private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(s.length() == 0) return;
            if(editCode1.hasFocus()) {
                editCode2.requestFocus();
            }
            else if(editCode2.hasFocus()) {
                editCode3.requestFocus();
            }
            else if(editCode3.hasFocus()) {
                editCode4.requestFocus();
            }
            else if(editCode4.hasFocus()) {
                editCode1.requestFocus();
            }
        }
    };
}


