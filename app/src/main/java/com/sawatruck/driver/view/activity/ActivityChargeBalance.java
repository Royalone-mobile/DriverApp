package com.sawatruck.driver.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.User;
import com.sawatruck.driver.repository.DBController;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.Logger;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.utils.Notice;
import com.sawatruck.driver.utils.UserManager;
import com.sawatruck.driver.view.design.CustomEditText;
import com.sawatruck.driver.view.design.FourDigitCardFormatWatcher;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import java.math.BigDecimal;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;


public class ActivityChargeBalance extends BaseActivity implements View.OnClickListener{
    @Bind(R.id.edit_cardholdername) CustomEditText editCardHolderName;
    @Bind(R.id.edit_expiredate) CustomEditText editExpireDate;
    @Bind(R.id.edit_balance) CustomEditText editBalance;
    @Bind(R.id.edit_cardnumber) CustomEditText editCardNumber;
    @Bind(R.id.edit_CVV) CustomEditText editCVV;
    @Bind(R.id.btn_cancel) View btnCancel;
    @Bind(R.id.btn_charge) View btnCharge;
    @Bind(R.id.radio_creditcard) RadioButton radioCreditCard;
    @Bind(R.id.radio_paypal) RadioButton radioPayPal;
    @Bind(R.id.layout_card) View layoutCard;

    private String paymentAmount;
    private String mLastInput;
    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_chargebalance,null);
        ButterKnife.bind(this, view);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        btnCharge.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        editCardNumber.addTextChangedListener(new FourDigitCardFormatWatcher());
        editExpireDate.addTextChangedListener(textWatcher);
        layoutCard.setVisibility(View.GONE);
        radioCreditCard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    layoutCard.setVisibility(View.VISIBLE);
                else
                    layoutCard.setVisibility(View.GONE);
            }
        });
        return view;
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
    public void onResume(){
        super.onResume();
        setAppTitle(getResources().getString(R.string.title_charge_balance));
        showNavHome(false);
    }


    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                String input = s.toString();
                if (s.length() == 2 && !mLastInput.endsWith("/")) {
                    int month = Integer.parseInt(input);
                    if (month <= 12) {
                        editExpireDate.setText(editExpireDate.getText().toString() + "/");
                        editExpireDate.setSelection(editExpireDate.getText().toString().length());
                    }
                } else if (s.length() == 2 && mLastInput.endsWith("/")) {
                    int month = Integer.parseInt(input);
                    if (month <= 12) {
                        editExpireDate.setText(editExpireDate.getText().toString().substring(0, 1));
                        editExpireDate.setSelection(editExpireDate.getText().toString().length());
                    } else {
                        editExpireDate.setText("");
                        editExpireDate.setSelection(editExpireDate.getText().toString().length());
                        Toast.makeText(getApplicationContext(), "Enter a valid month", Toast.LENGTH_LONG).show();
                    }
                } else if (s.length() == 1) {
                    int month = Integer.parseInt(input);
                    if (month > 1) {
                        editExpireDate.setText("0" + editExpireDate.getText().toString() + "/");
                        editExpireDate.setSelection(editExpireDate.getText().toString().length());
                    }
                } else {

                }
                mLastInput = editExpireDate.getText().toString();
            }catch(Exception e){
                e.printStackTrace();
            }
        }

    };


    public void chargeBalanceByPaypal() {
        paymentAmount = editBalance.getText().toString();

        com.paypal.android.sdk.payments.PayPalPayment payment = new com.paypal.android.sdk.payments.PayPalPayment(new BigDecimal(paymentAmount), "USD", "Sawatruck Paypal Payment",
                com.paypal.android.sdk.payments.PayPalPayment.PAYMENT_INTENT_SALE);

        //Creating Paypal Payment activity intent
        Intent intent = new Intent(this, PaymentActivity.class);

        //putting the paypal configuration to the intent
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, Constant.paypalConfig);
        //Puting paypal payment to the intent
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        //Starting the intent activity for result
        //the request code will be used on the method onActivityResult
        startActivityForResult(intent, Constant.PAYPAL_REQUEST_CODE);
        btnCharge.setEnabled(false);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //If the result is from paypal
        if (requestCode == Constant.PAYPAL_REQUEST_CODE) {

            //If the result is OK i.e. user has not canceled the payment
            if (resultCode == Activity.RESULT_OK) {
                //Getting the payment confirmation
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                Logger.error("Payerment ID = ");
                Logger.error(confirm.getProofOfPayment().getPaymentId());
                submitPaymentID(confirm.getProofOfPayment().getPaymentId());
            }
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_charge:
                if(radioCreditCard.isChecked())
                    createStripeToken();
                else
                    chargeBalanceByPaypal();
                break;
            case R.id.btn_cancel:
                finish();
                break;
        }
    }


    private void submitPaymentID(String paymentID){
        HttpUtil httpUtil = HttpUtil.getInstance();
        User user  = UserManager.with(this).getCurrentUser();

        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());

        httpUtil.post("http://api.sawatruck.com/api/AccountFinance/ChargeByPaybal_Verify/?paymentId=" + paymentID , new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Misc.showResponseMessage(responseBody);
                ActivityChargeBalance.this.finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Misc.showResponseMessage(responseBody);
            }
        });
    }

    public void createStripeToken(){
        String strCardNumber = editCardNumber.getText().toString();
        String strCVV = editCVV.getText().toString();
        String strExpireDate = editExpireDate.getText().toString();


        if(strCardNumber.length() == 0) {
            editCardNumber.setError(getResources().getString(R.string.error_enter_cardnumber));
            return;
        }

        if(strCVV.length() == 0){
            editCVV.setError(getResources().getString(R.string.error_enter_cvv));
            return;
        }
        if(strExpireDate.length() == 0 || strExpireDate.length()<5){
            editExpireDate.setError(getResources().getString(R.string.error_enter_expiredate));
            return;
        }

        String expireDate = editExpireDate.getText().toString();
        String expireMonth = expireDate.substring(0, 2);
        String expireYear = "20" +  expireDate.substring(3,5);
        if(Integer.valueOf(expireMonth)>12){
            editExpireDate.setError(getResources().getString(R.string.error_enter_correct_expire_month));
            return;
        }
        btnCharge.setEnabled(false);
        Stripe stripe = new Stripe(context, Constant.STRIPE_API_KEY);
        stripe.createToken(
                new Card(strCardNumber, Integer.valueOf(expireMonth), Integer.valueOf(expireYear), strCVV),
                new TokenCallback() {
                    @Override
                    public void onError(Exception error) {
                        try {
                            btnCharge.setEnabled(true);
                            if (error.getLocalizedMessage() != null) {
                                Notice.show(error.getLocalizedMessage());
                            }
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onSuccess(Token token) {
                        try {
                            chargeBalanceByCard(token);
                        }catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

    }
    private void chargeBalanceByCard(final Token token) {
        String strBalance = editBalance.getText().toString();

        String strCardHolderName = editCardHolderName.getText().toString();
        if(strBalance.length() == 0) {
            editBalance.setError(getResources().getString(R.string.error_enter_balance));
            editBalance.requestFocus();
            return;
        }
        if(strCardHolderName.length() == 0) {
            editCardHolderName.setError(getResources().getString(R.string.error_enter_cardholder_name));
            editCardHolderName.requestFocus();
            return;
        }
        HttpUtil httpUtil = HttpUtil.getInstance();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, UserManager.with(context).getCurrentUser().getToken());
        RequestParams params = new RequestParams();

        params.put("token", token.getId());

        Long balance = Long.valueOf(strBalance);
        params.put("amt", String.valueOf(balance));
        params.put("desc", strCardHolderName);

        if(token.getBankAccount()!=null)
            params.put("currency", token.getBankAccount().getCurrency());
        else
            params.put("currency", "usd");


        httpUtil.post(Constant.CHARGE_BALANCE_STRIPE_API, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Misc.showResponseMessage(responseBody);
                ActivityChargeBalance.this.finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Misc.showResponseMessage(responseBody);
            }

            @Override
            public void onFinish(){
                btnCharge.setEnabled(true);
            }
        });
    }
}