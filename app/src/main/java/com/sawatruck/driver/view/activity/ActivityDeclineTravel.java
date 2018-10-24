package com.sawatruck.driver.view.activity;

import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.CancelReason;
import com.sawatruck.driver.entities.User;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.utils.StringUtil;
import com.sawatruck.driver.utils.UserManager;
import com.sawatruck.driver.view.design.CustomEditText;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class ActivityDeclineTravel extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.edit_complain) CustomEditText editComplain;
    @Bind(R.id.btn_submit_complain) View btnSubmitComplain;

    private ArrayList<CancelReason> complaintReasons = new ArrayList<>();
    private int complainReason = -1;
    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_decline_travel,null);
        ButterKnife.bind(this, view);
        btnSubmitComplain.setOnClickListener(this);
        getComplaintReasons();
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

        setAppTitle(getString(R.string.title_complain));
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
            case R.id.btn_submit_complain:
                submitComplain();
                break;
        }
    }


    private void getComplaintReasons(){
        HttpUtil httpUtil = HttpUtil.getInstance();
        User user  = UserManager.with(context).getCurrentUser();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());
        httpUtil.get(Constant.TRAVEL_COMPLAINT_REASON_API, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String paramString = new String(responseBody);
                paramString = StringUtil.escapeString(paramString);

                try {
                    JSONArray jsonArray = new JSONArray(paramString);
                    for(int j=0; j<jsonArray.length(); j++) {
                        CancelReason complaintReason = BaseApplication.getGson().fromJson(jsonArray.get(j).toString(), CancelReason.class);
                        complaintReasons.add(complaintReason);
                    }

                    addComplaintReasonRadioBox();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Misc.showResponseMessage(responseBody);
            }
        });
    }

    private void addComplaintReasonRadioBox() {
        final RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radio_group);

        for(final CancelReason complaintReason:complaintReasons){
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(complaintReason.getName());
            radioGroup.addView(radioButton);
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    complainReason =  complaintReason.getID();
                }
            });
        }

        try{
            RadioButton radioButton =(RadioButton) radioGroup.getChildAt(0);
            complainReason = complaintReasons.get(0).getID();
            radioButton.setChecked(true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void submitComplain() {
        String note = editComplain.getText().toString();


        if(complainReason<0) return;
        if(note.length() == 0){
            editComplain.setError(getString(R.string.error_enter_complain));
            editComplain.requestFocus();
            return;
        }

        HttpUtil httpUtil = HttpUtil.getInstance();
        User user  = UserManager.with(context).getCurrentUser();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());
        RequestParams requestParams = new RequestParams();


        requestParams.put("note", note);
        requestParams.put("TravelID", getIntent().getStringExtra("travel_id"));
        requestParams.put("ComplaintReasonID", complainReason);
        btnSubmitComplain.setEnabled(false);
        httpUtil.post(Constant.POST_COMPLAIN_API, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Misc.showResponseMessage(responseBody);
                ActivityDeclineTravel.this.finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Misc.showResponseMessage(responseBody);
            }

            @Override
            public void onFinish(){
                btnSubmitComplain.setEnabled(true);
            }
        });

    }
}


