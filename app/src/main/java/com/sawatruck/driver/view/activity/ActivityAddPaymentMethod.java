package com.sawatruck.driver.view.activity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.sawatruck.driver.R;
import com.sawatruck.driver.view.design.CustomEditText;
import com.sawatruck.driver.view.design.FourDigitCardFormatWatcher;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ActivityAddPaymentMethod extends BaseActivity implements View.OnClickListener{
    @Bind(R.id.edit_cardholdername) CustomEditText editCardHolderName;
    @Bind(R.id.edit_expiredate) CustomEditText editExpireDate;
    @Bind(R.id.edit_cardnumber) CustomEditText editCardNumber;
    @Bind(R.id.edit_CVV) CustomEditText editCVV;
    @Bind(R.id.btn_cancel) View btnCancel;
    @Bind(R.id.btn_charge) View btnCharge;
    @Bind(R.id.radio_creditcard) RadioButton radioCreditCard;
    @Bind(R.id.radio_paypal) RadioButton radioPayPal;

    private String mLastInput;


    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_add_payment_method,null);
        ButterKnife.bind(this, view);
        btnCharge.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        editCardNumber.addTextChangedListener(new FourDigitCardFormatWatcher());
        editExpireDate.addTextChangedListener(textWatcher);
        return view;
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
        setAppTitle(getResources().getString(R.string.title_add_payment_method));
        showNavHome(false);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_charge:
                break;
            case R.id.btn_cancel:
                finish();
                break;
        }
    }
}
