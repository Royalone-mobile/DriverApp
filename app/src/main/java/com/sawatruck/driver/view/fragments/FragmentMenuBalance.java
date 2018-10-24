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
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.Balance;
import com.sawatruck.driver.utils.ActivityUtil;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.Logger;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.utils.StringUtil;
import com.sawatruck.driver.utils.UserManager;
import com.sawatruck.driver.view.activity.ActivityAddPaymentMethod;
import com.sawatruck.driver.view.activity.ActivityChargeBalance;
import com.sawatruck.driver.view.activity.ActivityClientDues;
import com.sawatruck.driver.view.activity.ActivityPickup;
import com.sawatruck.driver.view.activity.ActivityTransactionHistory;
import com.sawatruck.driver.view.activity.BaseActivity;
import com.sawatruck.driver.view.activity.MainActivity;
import com.sawatruck.driver.view.design.CustomTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by royal on 8/19/2017.
 */


public class FragmentMenuBalance extends BaseFragment implements View.OnClickListener {
    public static final String TAG = FragmentMenuBalance.class.getSimpleName();

    @Bind(R.id.btn_transaction_history) View btnTransactionHistory;
    @Bind(R.id.btn_add_promo_code) View btnAddPromoCode;
    @Bind(R.id.btn_charge_balance) View btnChargeBalance;
    @Bind(R.id.txt_balance) CustomTextView txtBalance;
    @Bind(R.id.txt_point) CustomTextView txtPoint;
    @Bind(R.id.txt_promocode) CustomTextView txtPromoCode;
    @Bind(R.id.txt_client_dues) CustomTextView txtClientDues;
    @Bind(R.id.btn_client_dues) View btnClientDues;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mybalance, container, false);
        ButterKnife.bind(this,view);

        BaseActivity baseActivity = (BaseActivity)getActivity();
        baseActivity.setAppTitle(getResources().getString(R.string.sidemenu_mybalance));
        baseActivity.showOptions(false);

        btnTransactionHistory.setOnClickListener(this);
        btnAddPromoCode.setOnClickListener(this);
        btnChargeBalance.setOnClickListener(this);
        btnClientDues.setOnClickListener(this);


        txtBalance.setBold(true);
        txtPoint.setBold(true);
        txtPromoCode.setBold(true);

        return view;
    }

    public void getMyBalance(){
        HttpUtil httpUtil = HttpUtil.getInstance();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, UserManager.with(getContext()).getCurrentUser().getToken());
        Logger.error(UserManager.with(getContext()).getCurrentUser().getToken());
        httpUtil.get(Constant.USER_GETBALANCE_API, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String paramString = new String(responseBody);
                paramString = StringUtil.escapeString(paramString);
                try {
                    JSONObject contentObject = new JSONObject(paramString).getJSONObject("Object");

                    Balance balance = BaseApplication.getGson().fromJson(contentObject.toString(), Balance.class);
                    String strBalance = NumberFormat.getInstance().format(Double.valueOf(balance.getBalance()));

                    txtBalance.setText(balance.getCurrency() + " " + strBalance);
                    txtPoint.setText(NumberFormat.getInstance().format(Double.valueOf(balance.getPoints())));
                    txtPromoCode.setText(balance.getPromoCode());
                    txtClientDues.setText(balance.getDues());

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

    @Override
    public void onResume() {
        super.onResume();
        getMyBalance();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_transaction_history:
                viewTransactionHistory();
                break;
            case R.id.btn_add_promo_code:
                addPromoCode();
                break;
            case R.id.btn_charge_balance:
                chargeBalance();
                break;
            case R.id.btn_client_dues:
                viewClientDues();
                break;
        }
    }

    private void viewClientDues() {
        Intent intent = new Intent(getActivity(), ActivityClientDues.class);
        ActivityUtil.goOtherActivityFlipTransition(getContext(), intent);
    }

    public void viewTransactionHistory(){
        Intent intent = new Intent(getActivity(), ActivityTransactionHistory.class);
        ActivityUtil.goOtherActivityFlipTransition(getContext(), intent);
    }


    public void addPromoCode(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        final View viewAddPromocode = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_promocode,null);

        final EditText editPromoCode = (EditText)viewAddPromocode.findViewById(R.id.edit_promocode);

        final Button btnOk = (Button)viewAddPromocode.findViewById(R.id.btn_ok);
        final Button btnCancel = (Button)viewAddPromocode.findViewById(R.id.btn_cancel);


        builder.setView(viewAddPromocode);

        final AlertDialog alertDialog = builder.create();

        alertDialog.show();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editPromoCode.getText().length() == 0){
                    editPromoCode.setError(getString(R.string.enter_promocode));
                    editPromoCode.requestFocus();
                    return;
                }
                try {
                    AddPromoCodeAsyncTask addPromoCodeAsyncTask = new AddPromoCodeAsyncTask();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("code", editPromoCode.getText().toString());
                    addPromoCodeAsyncTask.execute(jsonObject.toString());
                }catch (Exception e) {

                }

            }
        });
    }

    private class AddPromoCodeAsyncTask extends AsyncTask<String, Void, Void> {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(String... params) {
            String json = params[0];

            HttpUtil.putBody(Constant.ADD_PROMOCODE_API , json, new HttpUtil.ResponseHandler() {
                @Override
                public void onSuccess(final String responseBody) {
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Misc.showResponseMessage(responseBody);
                            }
                        });
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
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

    public void chargeBalance(){
        Intent intent = new Intent(getActivity(), ActivityChargeBalance.class);
        ActivityUtil.goOtherActivityFlipTransition(getContext(), intent);
    }
}
