package com.sawatruck.driver.view.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bruce.pickerview.popwindow.DatePickerPopWin;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.Currency;
import com.sawatruck.driver.entities.Load;
import com.sawatruck.driver.entities.Truck;
import com.sawatruck.driver.entities.User;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.Logger;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.utils.Notice;
import com.sawatruck.driver.utils.Serializer;
import com.sawatruck.driver.utils.StringUtil;
import com.sawatruck.driver.utils.UserManager;
import com.sawatruck.driver.view.design.CustomTextView;
import com.sawatruck.driver.view.design.TimePickerPopWin;
import com.sawatruck.driver.view.fragments.FragmentMenuBids;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import java.util.Locale;
/**
 * Created by royal on 8/22/2017.
 */

public class ActivitySendOffer extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.edit_budget_price) EditText editBudgetPrice;
    @Bind(R.id.btn_delivery_date) View btnDeliveryDate;
    @Bind(R.id.btn_pickup_date) View btnPickupDate;
    @Bind(R.id.txt_pickup_timePostOffer) TextView txtPickupTime;
    @Bind(R.id.btn_pickup_timePostOffer) View btnPickupTime;
    @Bind(R.id.btn_rate) View btnSubmit;
    @Bind(R.id.txt_delivery_date) CustomTextView txtDeliveryDate;
    @Bind(R.id.txt_pickup_date) CustomTextView txtPickupDate;
    @Bind(R.id.spinner_currency) BetterSpinner spinnerCurrency;
    @Bind(R.id.btn_back) View btnBack;
    @Bind(R.id.edit_cover_lettter) EditText editCoverLetter;

    private ArrayList<Currency> currencies = new ArrayList<>();
    private Currency currentCurrency;

    private Truck truck;
    private Load load;
    Date loadDate = new Date(), unloadDate = new Date();

    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_send_offer,null);
        ButterKnife.bind(this, view);



        btnSubmit.setOnClickListener(this);
        btnPickupDate.setOnClickListener(this);
        btnPickupTime.setOnClickListener(this);
        btnDeliveryDate.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        Intent intent = getIntent();
        load = Serializer.getInstance().deserializeLoad(getIntent().getStringExtra("load"));
        truck = Serializer.getInstance().deserializeTruck(intent.getStringExtra("truck"));
        Locale locale = new Locale(Locale.ENGLISH.getLanguage());
        SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd",locale);
        try{
            Date date = parseFormat.parse(load.getLoadDateFrom());

            java.text.NumberFormat mNumberFormat= java.text.NumberFormat.getIntegerInstance(Locale.US);
            mNumberFormat.setGroupingUsed(false);

            java.util.Calendar calendar = 	java.util.Calendar.getInstance();
            calendar.setTime(date);

            txtPickupDate.setText(String.format("%s-%s-%s", mNumberFormat.format(calendar.get(java.util.Calendar.YEAR)), mNumberFormat.format(calendar.get(java.util.Calendar.MONTH)+1),mNumberFormat.format(calendar.get(	java.util.Calendar.DAY_OF_MONTH))));

            date = parseFormat.parse(load.getUnloadDateFrom());
            calendar.setTime(date);
            txtDeliveryDate.setText(String.format("%s-%s-%s", mNumberFormat.format(calendar.get(java.util.Calendar.YEAR)), mNumberFormat.format(calendar.get(java.util.Calendar.MONTH)+1),mNumberFormat.format(calendar.get(java.util.Calendar.DAY_OF_MONTH))));

            txtPickupTime.setText("08:00AM");

        }
        catch (ParseException e)
        {

        }
        initSpinnerCurrency();
        getCurrencies();

        spinnerCurrency.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentCurrency = currencies.get(position);
            }
        });



        return view;
    }


    public void getCurrencies(){
        HttpUtil httpUtil = new HttpUtil();
        User user  = UserManager.with(this).getCurrentUser();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());
        httpUtil.get(Constant.GET_CURRENCIES_API, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String paramString = new String(responseBody);
                paramString = StringUtil.escapeString(paramString);

                currencies = new ArrayList<>();
                ArrayList<String> codeList = new ArrayList<>();
                int defaultCurrencyPosition = 0;
                try {
                    JSONArray jsonArray = new JSONArray(paramString);
                    for(int j=0; j<jsonArray.length(); j++) {
                        Currency currency = BaseApplication.getGson().fromJson(jsonArray.get(j).toString(), Currency.class);
                        currencies.add(currency);
                        codeList.add(currency.getCode());

                        if(currency.getID().equals(UserManager.with(context).getCurrentUser().getDefaultCurrencyID())){
                            currentCurrency = currency;
                        }

                        defaultCurrencyPosition = j;
                    }

                    ArrayAdapter adapter = new ArrayAdapter(
                            context,
                            R.layout.spinner_item,
                            codeList);

                    adapter.setDropDownViewResource(R.layout.spinner_select);
                    spinnerCurrency.setAdapter(adapter);


                    if(currencies.size()>0) {
                        CharSequence charSequence = currentCurrency.getCode();
                        spinnerCurrency.setHint(charSequence);
                        spinnerCurrency.setSelection(defaultCurrencyPosition);
                        spinnerCurrency.setSelected(true);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }

        });
    }

    public void initSpinnerCurrency(){
        ArrayAdapter adapter = new ArrayAdapter(
                this,
                R.layout.spinner_item,
                currencies);
        adapter.setDropDownViewResource(R.layout.spinner_select);
        spinnerCurrency.setAdapter(adapter);
    }

    public void getPickupTime(){

        TimePickerPopWin timePickerPopWin=new TimePickerPopWin.Builder(this, new       TimePickerPopWin.OnTimePickListener() {
            @Override
            public void onTimePickCompleted(int hour, int minute, String AM_PM, String time) {
                Toast.makeText(context, time, Toast.LENGTH_SHORT).show();
                txtPickupTime.setText(time);
            }
        }).textConfirm(getString(R.string.btn_confirm)) //text of confirm button
                .textCancel("")
                .btnTextSize(16)
                .viewTextSize(25)
                .colorCancel(Color.parseColor("#999999"))
                .colorConfirm(Color.parseColor("#009900"))
                .build();
        timePickerPopWin.showPopWin(this);
    }

    public void getPickupDate(){
        Date now = new Date();
        SimpleDateFormat convertFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = "";
        if(txtPickupDate.getText().length() == 0) {
            strDate = convertFormat.format(now);
        }
        else if(txtPickupDate.getText().length() >= 0){
            try {
                Date date = parseFormat.parse(txtPickupDate.getText().toString());

                strDate = convertFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        DatePickerPopWin pickerPopWin = new DatePickerPopWin.Builder(this, new DatePickerPopWin.OnDatePickedListener() {
            @Override
            public void onDatePickCompleted(int year, int month, int day, String dateDesc) {
                Date tempDate = null;

                try {
                    DateFormat f = new SimpleDateFormat("yyyy-MM-dd");
                    tempDate  = f.parse(dateDesc);
                    tempDate.setDate(day+1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if(Misc.dateToLong(new Date())>Misc.dateToLong(tempDate)) {
                    Notice.show(R.string.error_date_ahead_today);
                    return;
                }
                tempDate.setDate(day);
                java.text.NumberFormat mNumberFormat= java.text.NumberFormat.getIntegerInstance(Locale.US);
                mNumberFormat.setGroupingUsed(false);


                txtPickupDate.setText(String.format("%s-%s-%s", mNumberFormat.format(year), mNumberFormat.format(month), mNumberFormat.format(day)));

                //txtPickupDate.setText(String.format("%d-%d-%d", year, month, day));
                loadDate = tempDate;
            }
        }).textConfirm(getString(R.string.btn_confirm)) //text of confirm button
                .textCancel("")
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .minYear(1990) //min year in loop
                .maxYear(2030) // max year in loop
                .showDayMonthYear(true) // shows like dd mm yyyy (default is false)
                .dateChose(strDate) // date chose when init popwindow
                .build();
        pickerPopWin.showPopWin(this);

    }

    public void getDeliveryDate(){
        Date now = new Date();
        now.setDate(now.getDate() + 1);
        SimpleDateFormat convertFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = "";
        if(txtDeliveryDate.getText().length() == 0) {
            strDate = convertFormat.format(now);
        }
        else if(txtDeliveryDate.getText().length() >= 0){
            try {
                Date date = parseFormat.parse(txtDeliveryDate.getText().toString());
                strDate = convertFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        DatePickerPopWin pickerPopWin = new DatePickerPopWin.Builder(this, new DatePickerPopWin.OnDatePickedListener() {
            @Override
            public void onDatePickCompleted(int year, int month, int day, String dateDesc) {
                Locale locale = new Locale(Locale.ENGLISH.getLanguage());

                DateFormat f = new SimpleDateFormat("yyyy-MM-dd",locale);
                Date tempDate = null;
                try {
                    tempDate  = f.parse(dateDesc);
                    tempDate.setDate(day+1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if(Misc.dateToLong(new Date())>Misc.dateToLong(tempDate)) {
                    Notice.show(R.string.error_date_ahead_today);
                    return;
                }

                tempDate.setDate(day);

                if(Misc.dateToLong(tempDate)<Misc.dateToLong(loadDate)) {
                    Notice.show( R.string.error_delivery_date_ahead);
                    return;
                }

                unloadDate = tempDate;
                java.text.NumberFormat mNumberFormat= java.text.NumberFormat.getIntegerInstance(Locale.US);
                mNumberFormat.setGroupingUsed(false);

                txtDeliveryDate.setText(String.format("%s-%s-%s", mNumberFormat.format(year), mNumberFormat.format(month), mNumberFormat.format(day)));

                //txtDeliveryDate.setText(String.format("%d-%d-%d", year, month, day));
            }
        }).textConfirm(getString(R.string.btn_confirm)) //text of confirm button
                .textCancel("")
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .minYear(1990) //min year in loop
                .maxYear(2030) // max year in loop
                .showDayMonthYear(true) // shows like dd mm yyyy (default is false)
                .dateChose(strDate) // date chose when init popwindow
                .build();
        pickerPopWin.showPopWin(this);
    }



    @Override
    public void onResume(){
        super.onResume();

        setAppTitle(getString(R.string.title_send_offer));
        showNavHome(false);
    }

    @Override
    public void onClick(View v) {
        int id =v.getId();
        switch (id) {
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.btn_rate:
                try {
                    onSubmit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_delivery_date:
                getDeliveryDate();
                break;
            case R.id.btn_pickup_date:
                getPickupDate();
                break;
            case R.id.btn_pickup_timePostOffer:
                getPickupTime();
                break;
        }
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

    public void onSubmit() throws Exception {
        if(txtPickupDate.getText().toString().length() ==0){
            Notice.show(getString(R.string.error_enter_pickup_date));
            return;
        }

        if(txtDeliveryDate.getText().toString().length() == 0) {
            Notice.show(getString(R.string.error_enter_delivery_date));
            return;
        }

        if(editCoverLetter.getText().toString().length() ==0){
            editCoverLetter.setError(getString(R.string.error_enter_cover_letter));
            editCoverLetter.requestFocus();
            return;
        }

        if(editBudgetPrice.getText().toString().length() ==0){
            editBudgetPrice.setError(getString(R.string.error_enter_budget_price));
            editBudgetPrice.requestFocus();
            return;
        }

        if(currentCurrency == null) {
            Notice.show(getString(R.string.choose_currency));
            return;
        }

        Locale locale = new Locale(Locale.ENGLISH.getLanguage());
        SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd hh:mmaa",locale);
        Date date = parseFormat.parse(txtPickupDate.getText().toString() + " " + txtPickupTime.getText().toString());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("TruckID", truck.getID());
        jsonObject.put("PickupDate",  Misc.convert2UTC(date));
        jsonObject.put("DeliveryDate", txtDeliveryDate.getText().toString());
        jsonObject.put("Price", editBudgetPrice.getText().toString());
        jsonObject.put("InsuranceDescription", "");
        jsonObject.put("TrucksNumber", "1");
        jsonObject.put("NumberOfManpOwer", String.valueOf(truck.getColorID()));
        jsonObject.put("LoadID", load.getID());
        jsonObject.put("CoverLetter", editCoverLetter.getText().toString());
        jsonObject.put("NeedToDismantle", false);
        jsonObject.put("CurrencyID", currentCurrency.getID());

        String json = StringUtil.escapeString(jsonObject.toString());

        Logger.error("Send Offer JSON");
        Logger.error(json);
        Logger.error(UserManager.with(this).getCurrentUser().getToken());

        SendOfferAsyncTask sendOfferAsyncTask = new SendOfferAsyncTask();
        sendOfferAsyncTask.execute(json);

    }

    private class SendOfferAsyncTask extends AsyncTask<String, Void, Void> {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(String... params) {
            String json = params[0];

            HttpUtil.postBody(Constant.ADD_NEW_OFFER_API, json, new HttpUtil.ResponseHandler() {
                @Override
                public void onSuccess(final String responseBody) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Misc.showResponseMessage(responseBody);
                        }
                    });
                    ActivitySendOffer.this.finish();
                    FragmentMenuBids.Initial_TAB = 2;
                    MainActivity.applyAddOffer();
                }

                @Override
                public void onFailure(final String errorString) {
                    runOnUiThread(new Runnable() {
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
}

