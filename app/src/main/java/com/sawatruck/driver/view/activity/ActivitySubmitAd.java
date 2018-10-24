package com.sawatruck.driver.view.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bruce.pickerview.popwindow.DatePickerPopWin;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.AddressDetail;
import com.sawatruck.driver.entities.Advertisement;
import com.sawatruck.driver.entities.Currency;
import com.sawatruck.driver.entities.User;
import com.sawatruck.driver.utils.AppSettings;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.Logger;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.utils.Notice;
import com.sawatruck.driver.utils.Serializer;
import com.sawatruck.driver.utils.StringUtil;
import com.sawatruck.driver.utils.UserManager;
import com.sawatruck.driver.view.design.CustomTextView;
import com.sawatruck.driver.view.design.TimePickerPopWin;
import com.sawatruck.driver.view.fragments.FragmentMenuAd;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import java.util.Locale;
/**
 * Created by royal on 8/22/2017.
 */

public class ActivitySubmitAd extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.radio_days_7) RadioButton radioDays7;
    @Bind(R.id.radio_days_3) RadioButton radioDays3;
    @Bind(R.id.radio_hour_24) RadioButton radioHour24;
    @Bind(R.id.edit_budget_price) EditText editBudgetPrice;
    @Bind(R.id.chk_negotiable) CheckBox chkNegotiable;
    @Bind(R.id.spinner_currency) BetterSpinner spinnerCurrency;
    @Bind(R.id.btn_delivery_location) View btnDeliveryLocation;
    @Bind(R.id.btn_pickup_date) View btnPickupDate;
    @Bind(R.id.btn_pickup_time) View btnPickupTime;

    @Bind(R.id.btn_pickup_location) View btnPickupLocation;
    @Bind(R.id.btn_rate) View btnSubmit;
    @Bind(R.id.txt_pickup_date) TextView txtPickupDate;
    @Bind(R.id.txt_pickup_time) TextView txtPickupTime;
    @Bind(R.id.btn_back) View btnBack;
    @Bind(R.id.txt_load_location) CustomTextView txtPickupLocation;
    @Bind(R.id.txt_delivery_location) CustomTextView txtDeliveryLocation;
    private ArrayList<Currency> currencies = new ArrayList<>();

    private AddressDetail pickupLocation, deliveryLocation;
    private Date loadDate;
    private Currency currentCurrency;
    private String truckID="";
    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_postads,null);
        ButterKnife.bind(this, view);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        loadDate = new Date();
        loadDate.setDate(loadDate.getDate() + 1);
        Locale locale = new Locale(Locale.ENGLISH.getLanguage());
        SimpleDateFormat convertFormat = new SimpleDateFormat("yyyy-MM-dd",locale);

        String strDate = convertFormat.format(loadDate);
        txtPickupDate.setText(strDate);
        txtPickupTime.setText("08:00AM");

        btnSubmit.setOnClickListener(this);
        btnPickupDate.setOnClickListener(this);
        btnPickupTime.setOnClickListener(this);
        btnPickupLocation.setOnClickListener(this);
        btnDeliveryLocation.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        Intent intent = getIntent();

        truckID = intent.getStringExtra(Constant.TRUCK_ID);

        int editOrPost = getIntent().getIntExtra(Constant.INTENT_SUBMIT_AD,0);

        try {
            if (editOrPost == 2) {
                String strAdvertisement = getIntent().getStringExtra("advertisement");
                Advertisement advertisement = Serializer.getInstance().deserializeAdvertisement(strAdvertisement);

                txtDeliveryLocation.setText(advertisement.getDeliveryLocation().getCityName() + ", " + advertisement.getDeliveryLocation().getCountryName());


                txtPickupDate.setText(Misc.getTimeZoneDate(advertisement.getPickupDate()) );
                txtPickupLocation.setText(advertisement.getPickupLocation().getCityName() + ", " + advertisement.getPickupLocation().getCountryName());

                chkNegotiable.setChecked(advertisement.isNegotiable());

                editBudgetPrice.setText(advertisement.getBudget());

                pickupLocation = Misc.getAddressDetail(Double.valueOf(advertisement.getPickupLocation().getLatitude()), Double.valueOf(advertisement.getPickupLocation().getLongitude()));
                deliveryLocation = Misc.getAddressDetail(Double.valueOf(advertisement.getDeliveryLocation().getLatitude()), Double.valueOf(advertisement.getDeliveryLocation().getLongitude()));

                if(truckID.equals("")) {

                    truckID = advertisement.getTruckID();

                    Logger.error("edit truck ad id = " + truckID);
                }

            } else {
                getAddress(AppSettings.with(this).getCurrentLat(), AppSettings.with(this).getCurrentLng());
            }
            initSpinnerCurrency();
            getCurrencies();
            spinnerCurrency.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    currentCurrency = currencies.get(position);
                }
            });
        }catch (Exception e) {

        }
        return view;
    }

    public void getAddress(Double latitude, Double longitude) {
        LocationAsyncTask locationAsyncTask = new LocationAsyncTask();
        Location location = new Location("location");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        locationAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,location);
    }

    private class LocationAsyncTask extends AsyncTask<Location, Void, Void> {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(Location... params) {
            Location location = params[0];

            final AddressDetail addressDetail = Misc.getAddressDetail(location.getLatitude(), location.getLongitude());

            pickupLocation = addressDetail;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txtPickupLocation.setText(addressDetail.getFormatted_address());
                }
            });
            return null;
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        int editOrPost = getIntent().getIntExtra(Constant.INTENT_SUBMIT_AD,0);
        if(editOrPost == 1)
            setAppTitle(getResources().getString(R.string.title_post_ads));
        else if(editOrPost == 2)
            setAppTitle(getResources().getString(R.string.title_edit_ad));
        showNavHome(false);
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
                    Locale locale = new Locale(Locale.ENGLISH.getLanguage());
                    DateFormat f = new SimpleDateFormat("yyyy-MM-dd",locale);
                    tempDate  = f.parse(dateDesc);
                    tempDate.setDate(day+1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if(Misc.dateToLong(new Date())>Misc.dateToLong(tempDate)) {
                    Notice.show( R.string.error_date_ahead_today);
                    return;
                }
                tempDate.setDate(day);
                java.text.NumberFormat mNumberFormat= java.text.NumberFormat.getIntegerInstance(Locale.US);
                mNumberFormat.setGroupingUsed(false);

                txtPickupDate.setText(String.format("%s-%s-%s", mNumberFormat.format(year), mNumberFormat.format(month),mNumberFormat.format(day)));
                //txtPickupDate.setText(String.format("%d-%d-%d",year, month, day));
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


    public void getCurrencies(){
        HttpUtil httpUtil = new HttpUtil();
        User user  = UserManager.with(context).getCurrentUser();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());
        httpUtil.get(Constant.GET_CURRENCIES_API, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String paramString = new String(responseBody);
                paramString = StringUtil.escapeString(paramString);

                currencies = new ArrayList<>();
                ArrayList<String> codeList = new ArrayList<>();

                try {
                    JSONArray jsonArray = new JSONArray(paramString);
                    for(int j=0; j<jsonArray.length(); j++) {
                        Currency currency = BaseApplication.getGson().fromJson(jsonArray.get(j).toString(), Currency.class);
                        currencies.add(currency);
                        codeList.add(currency.getCode());
                    }

                    ArrayAdapter adapter = new ArrayAdapter(
                            context,
                            R.layout.spinner_item,
                            codeList);
                    adapter.setDropDownViewResource(R.layout.spinner_select);
                    spinnerCurrency.setAdapter(adapter);

                    if(currencies.size()>0)
                        spinnerCurrency.setSelection(0);


                    for(Currency currency :currencies){
                        if(currency.getID().equals(UserManager.with(context).getCurrentUser().getDefaultCurrencyID())) {
                            currentCurrency = currency;
                            spinnerCurrency.setHint(currentCurrency.getCode());
                        }
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
        ArrayList<String> currencies = new ArrayList<>();
        ArrayAdapter adapter = new ArrayAdapter(
                context,
                R.layout.spinner_item,
                currencies);
        adapter.setDropDownViewResource(R.layout.spinner_select);
        spinnerCurrency.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        int id =v.getId();
        switch (id) {
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.btn_rate:
                int editOrPost = getIntent().getIntExtra(Constant.INTENT_SUBMIT_AD,0);
                if(editOrPost == 1)
                    try {
                        handlePostAd();
                    } catch (Exception e) {
                                       e.printStackTrace();
                    }
                else if(editOrPost == 2)
                    try {
                        handleEditAd();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                break;
            case R.id.btn_delivery_location:
                getDeliveryLocation();
                break;
            case R.id.btn_pickup_date:
                getPickupDate();
                break;
            case R.id.btn_pickup_location:
                getPickupLocation();
                break;
            case R.id.btn_pickup_time:
                getPickupTime();
                break;
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) return;
        try {
            String json = data.getStringExtra("address");
            AddressDetail addressDetail = Serializer.getInstance().deserializeAddressDetail(json);
            switch (requestCode) {
                case Constant.PICKUP_LOCATION_REQUEST_CODE:
                    pickupLocation = addressDetail;
                    Logger.error(Serializer.getInstance().serializeAddressDetail(pickupLocation));
                    txtPickupLocation.setText(addressDetail.getFormatted_address());
                    break;
                case Constant.DELIVERY_LOCATION_REQUEST_CODE:
                    deliveryLocation = addressDetail;
                    Logger.error(Serializer.getInstance().serializeAddressDetail(deliveryLocation));
                    txtDeliveryLocation.setText(addressDetail.getFormatted_address());
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getPickupLocation(){
        Intent intent = new Intent(this, ActivityCities.class);
        startActivityForResult(intent,Constant.PICKUP_LOCATION_REQUEST_CODE);
    }

    public void getDeliveryLocation(){
        Intent intent = new Intent(this, ActivityCities.class);
        startActivityForResult(intent,Constant.DELIVERY_LOCATION_REQUEST_CODE);
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

    public void handlePostAd() throws Exception {
        if(txtPickupDate.getText().toString().length() ==0){
            Notice.show(getString(R.string.error_enter_pickup_date));
            txtPickupDate.requestFocus();
            return;
        }


        if(txtPickupLocation.getText().toString().length() ==0){
            Notice.show(getString(R.string.error_enter_pickup_location));
            return;
        }

        if(txtDeliveryLocation.getText().toString().length() ==0){
            Notice.show(getString(R.string.error_enter_delivery_location));
            return;
        }

        if(editBudgetPrice.getText().toString().length() ==0){
            editBudgetPrice.setError(getString(R.string.error_enter_budget_price));
            editBudgetPrice.requestFocus();
            return;
        }

        if(currentCurrency == null) {
            Notice.show(getString(R.string.error_enter_currency));
            return;
        }

        int availableDays = 0;
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if(radioHour24.isChecked()) {
            availableDays = 1;
        }
        if(radioDays3.isChecked()){
            availableDays = 3;
        }

        if(radioDays7.isChecked()){
            availableDays = 7;
        }

        Locale locale = new Locale(Locale.ENGLISH.getLanguage());
        SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd hh:mmaa",locale);
        Date date = parseFormat.parse(txtPickupDate.getText().toString() + " " + txtPickupTime.getText().toString());

        JSONObject rootObject = new JSONObject();
        rootObject.put("TruckID", truckID);
        rootObject.put("PickupDate", Misc.convert2UTC(date));
        rootObject.put("DeliveryDate", null);
        rootObject.put("Budget", editBudgetPrice.getText().toString());
        rootObject.put("CurrencyID", currentCurrency.getID());
        rootObject.put("Negotiable", String.valueOf(chkNegotiable.isChecked()));
        rootObject.put("AvailableDays", String.valueOf(availableDays));
        JSONObject pickupLocationObject = new JSONObject();
        pickupLocationObject.put("latitude", String.valueOf(pickupLocation.getLatitude()));
        pickupLocationObject.put("longitude", String.valueOf(pickupLocation.getLongitude()));
        pickupLocationObject.put("formatted_address", pickupLocation.getFormatted_address());
        pickupLocationObject.put("country", pickupLocation.getCountry());
        pickupLocationObject.put("city", pickupLocation.getCity());
        pickupLocationObject.put("StreetNumber", pickupLocation.getStreetNumber());
        pickupLocationObject.put("route", pickupLocation.getRoute());
        pickupLocationObject.put("PostalCodePrefix", pickupLocation.getPostalCodePrefix());
        pickupLocationObject.put("AdministrativeAreaLevel1", pickupLocation.getAdministrativeAreaLevel1());
        pickupLocationObject.put("AdministrativeAreaLevel2",  pickupLocation.getAdministrativeAreaLevel2());
        pickupLocationObject.put("AdministrativeAreaLevel3", pickupLocation.getAdministrativeAreaLevel3());
        pickupLocationObject.put("AddressDetails",  pickupLocation.getAddressDetails());


        JSONObject deliveryLocationObject = new JSONObject();
        deliveryLocationObject.put("latitude", String.valueOf(deliveryLocation.getLatitude()));
        deliveryLocationObject.put("longitude", String.valueOf(deliveryLocation.getLongitude()));
        deliveryLocationObject.put("formatted_address", deliveryLocation.getFormatted_address());
        deliveryLocationObject.put("country", deliveryLocation.getCountry());
        deliveryLocationObject.put("city", deliveryLocation.getCity());
        deliveryLocationObject.put("StreetNumber", deliveryLocation.getStreetNumber());
        deliveryLocationObject.put("route", deliveryLocation.getRoute());
        deliveryLocationObject.put("PostalCodePrefix", deliveryLocation.getPostalCodePrefix());
        deliveryLocationObject.put("AdministrativeAreaLevel1", deliveryLocation.getAdministrativeAreaLevel1());
        deliveryLocationObject.put("AdministrativeAreaLevel2",  deliveryLocation.getAdministrativeAreaLevel2());
        deliveryLocationObject.put("AdministrativeAreaLevel3", deliveryLocation.getAdministrativeAreaLevel3());
        deliveryLocationObject.put("AddressDetails",  deliveryLocation.getAddressDetails());

        rootObject.put("PickupLocation", pickupLocationObject);
        rootObject.put("DeliveryLocation", deliveryLocationObject);


        String json = StringUtil.escapeString(rootObject.toString());
        Logger.error(json);

        btnSubmit.setEnabled(false);
        AddAdAsyncTask addAdAsyncTask = new AddAdAsyncTask();
        addAdAsyncTask.execute(json);
    }


    private class AddAdAsyncTask extends AsyncTask<String, Void, Void> {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(String... params) {
            String json = params[0];

            HttpUtil.postBody(Constant.ADD_AD_API , json, new HttpUtil.ResponseHandler() {
                @Override
                public void onSuccess(final String responseBody) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            Misc.showResponseMessage(responseBody);
                            ActivitySubmitAd.this.finish();

                            FragmentMenuAd.Initial_TAB  = 1;
                            MainActivity.applyAddAd();
                            btnSubmit.setEnabled(true);
                        }
                    });
                }

                @Override
                public void onFailure(final String errorString) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            btnSubmit.setEnabled(true);
                            Misc.showResponseMessage(errorString);
                        }
                    });
                }
            });
            return null;
        }
    }

    private class EditAdAsyncTask extends AsyncTask<String, Void, Void> {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(String... params) {
            String advertisementID = params[0];
            String json = params[1];

            HttpUtil.putBody(Constant.EDIT_AD_API + "/"+ advertisementID , json, new HttpUtil.ResponseHandler() {
                @Override
                public void onSuccess(final String responseBody) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            Misc.showResponseMessage(responseBody);
                            ActivitySubmitAd.this.finish();


                            FragmentMenuAd.Initial_TAB  = 1;
                            MainActivity.applyAddAd();
                            btnSubmit.setEnabled(true);

                        }
                    });
                }

                @Override
                public void onFailure(final String errorString) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            btnSubmit.setEnabled(true);
                            Misc.showResponseMessage(errorString);
                        }
                    });

                }
            });
            return null;
        }
    }

    private void handleEditAd() throws Exception {
            String AdvertisementID = getIntent().getStringExtra("advertisement_id");

            if (txtPickupDate.getText().toString().length() == 0) {
                Notice.show(getString(R.string.error_enter_pickup_date));
                txtPickupDate.requestFocus();
                return;
            }


            if (txtPickupLocation.getText().toString().length() == 0) {
                Notice.show(getString(R.string.error_enter_pickup_location));
                return;
            }

            if (txtDeliveryLocation.getText().toString().length() == 0) {
                Notice.show(getString(R.string.error_enter_delivery_location));
                return;
            }

            if (editBudgetPrice.getText().toString().length() == 0) {
                editBudgetPrice.setError(getString(R.string.error_enter_budget_price));
                editBudgetPrice.requestFocus();
                return;
            }
            if(currentCurrency == null) {
                Notice.show(getString(R.string.error_enter_currency));
                return;
            }




            int availableDays = 0;
            if (radioHour24.isChecked()) {
                availableDays = 1;
            }
            if (radioDays3.isChecked()) {
                availableDays = 3;
            }

            if (radioDays7.isChecked()) {
                availableDays = 7;
            }


            JSONObject rootObject = new JSONObject();
            rootObject.put("ID", AdvertisementID);
            rootObject.put("TruckID",truckID);
            rootObject.put("PickupDate", txtPickupDate.getText().toString().concat(" ").concat(txtPickupTime.getText().toString()));
            rootObject.put("DeliveryDate", null);
            rootObject.put("Budget", editBudgetPrice.getText().toString());
            rootObject.put("CurrencyID", currentCurrency.getID());
            rootObject.put("Negotiable", String.valueOf(chkNegotiable.isChecked()));
            rootObject.put("AvailableDays", String.valueOf(availableDays));
            JSONObject pickupLocationObject = new JSONObject();
            pickupLocationObject.put("latitude", String.valueOf(pickupLocation.getLatitude()));
            pickupLocationObject.put("longitude", String.valueOf(pickupLocation.getLongitude()));
            pickupLocationObject.put("formatted_address", pickupLocation.getFormatted_address());
            pickupLocationObject.put("country", pickupLocation.getCountry());
            pickupLocationObject.put("city", pickupLocation.getCity());
            pickupLocationObject.put("StreetNumber", pickupLocation.getStreetNumber());
            pickupLocationObject.put("route", pickupLocation.getRoute());
            pickupLocationObject.put("PostalCodePrefix", pickupLocation.getPostalCodePrefix());
            pickupLocationObject.put("AdministrativeAreaLevel1", pickupLocation.getAdministrativeAreaLevel1());
            pickupLocationObject.put("AdministrativeAreaLevel2", pickupLocation.getAdministrativeAreaLevel2());
            pickupLocationObject.put("AdministrativeAreaLevel3", pickupLocation.getAdministrativeAreaLevel3());
            pickupLocationObject.put("AddressDetails", pickupLocation.getAddressDetails());


            JSONObject deliveryLocationObject = new JSONObject();
            deliveryLocationObject.put("latitude", String.valueOf(deliveryLocation.getLatitude()));
            deliveryLocationObject.put("longitude", String.valueOf(deliveryLocation.getLongitude()));
            deliveryLocationObject.put("formatted_address", deliveryLocation.getFormatted_address());
            deliveryLocationObject.put("country", deliveryLocation.getCountry());
            deliveryLocationObject.put("city", deliveryLocation.getCity());
            deliveryLocationObject.put("StreetNumber", deliveryLocation.getStreetNumber());
            deliveryLocationObject.put("route", deliveryLocation.getRoute());
            deliveryLocationObject.put("PostalCodePrefix", deliveryLocation.getPostalCodePrefix());
            deliveryLocationObject.put("AdministrativeAreaLevel1", deliveryLocation.getAdministrativeAreaLevel1());
            deliveryLocationObject.put("AdministrativeAreaLevel2", deliveryLocation.getAdministrativeAreaLevel2());
            deliveryLocationObject.put("AdministrativeAreaLevel3", deliveryLocation.getAdministrativeAreaLevel3());
            deliveryLocationObject.put("AddressDetails", deliveryLocation.getAddressDetails());

            rootObject.put("PickupLocation", pickupLocationObject);
            rootObject.put("DeliveryLocation", deliveryLocationObject);

            String json = StringUtil.escapeString(rootObject.toString());
            Logger.error(json);

            btnSubmit.setEnabled(false);
            EditAdAsyncTask editAdAsyncTask = new EditAdAsyncTask();
            editAdAsyncTask.execute(AdvertisementID, json);

    }
}