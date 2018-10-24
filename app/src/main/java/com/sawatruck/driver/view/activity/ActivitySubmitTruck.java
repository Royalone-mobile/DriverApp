package com.sawatruck.driver.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bruce.pickerview.popwindow.DatePickerPopWin;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.Colour;
import com.sawatruck.driver.entities.Truck;
import com.sawatruck.driver.entities.TruckBodyType;
import com.sawatruck.driver.entities.TruckBrand;
import com.sawatruck.driver.entities.TruckClass;
import com.sawatruck.driver.entities.TruckType;
import com.sawatruck.driver.entities.User;
import com.sawatruck.driver.utils.HttpHelper;
import com.sawatruck.driver.utils.HttpResponseListener;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.Logger;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.utils.Notice;
import com.sawatruck.driver.utils.Serializer;
import com.sawatruck.driver.utils.StringUtil;
import com.sawatruck.driver.utils.UserManager;
import com.squareup.picasso.Picasso;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class ActivitySubmitTruck extends BaseActivity {
    @Bind(R.id.btn_rate) View btnSubmit;
    @Bind(R.id.chk_insurance) CheckBox chkInsurance;
    @Bind(R.id.edit_model) EditText editModel;
    @Bind(R.id.edit_owner_name) EditText editOwnerName;
    @Bind(R.id.edit_owner_phone_number) EditText editOwnerPhone;
    @Bind(R.id.edit_owner_identity) EditText editOwnerIdentity;
    @Bind(R.id.edit_owner_identity_source) EditText editOwnerIdentitySource;
    @Bind(R.id.txt_owner_identity_date) TextView txtOwnerIdentityDate;


    @Bind(R.id.edit_production_year) EditText editProductionYear;
    @Bind(R.id.edit_pallet_number) EditText editPaletteNumber;


    @Bind(R.id.spinner_company) BetterSpinner spinnerCompany;
    @Bind(R.id.spinner_color) BetterSpinner spinnerColor;
    //@Bind(R.id.spinner_vehicle_body) BetterSpinner spinnerBodyType;
    @Bind(R.id.spinner_vehicle_type) BetterSpinner spinnerVehicleType;
    @Bind(R.id.spinner_vehicle_class) BetterSpinner spinnerVehicleClass;
    @Bind(R.id.btn_change_photo) View btnTakePhoto;
    @Bind(R.id.img_truck) ImageView imgTruck;




    private ArrayList<TruckBrand> truckBrands = new ArrayList<>();
    private ArrayList<Colour> colors = new ArrayList<>();
    private ArrayList<TruckClass> truckClasses = new ArrayList<>();

    private static final int PICK_Camera_IMAGE = 2;
    private final int SELECT_IMAGE = 1000;
    private String photoUrl;
    private File destination;
    private int currentBodyTypesPosition = -1;
    private int currentBrandPosition = -1;
    private int currentColorPosition = -1;
    private int currentClassPosition = -1;
    private int currentTruckTypePosition = -1;

    private final ArrayList<String> bodyTypes = new ArrayList<>();
    private final ArrayList<String> truckTypes = new ArrayList<>();
    private final ArrayList<String> brandList = new ArrayList<>();
    private final ArrayList<String> colorNameList = new ArrayList<>();
    private final ArrayList<String> classNameList = new ArrayList<>();

    private int submitMode = 0;

    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.driver_activity_add_truck, null);
        ButterKnife.bind(this, view);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        if (photoUrl == null)
            Logger.error("default photo url is null");
        else
            Logger.error("default photo url is not null");

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LinkedHashMap<String, Object> param = new LinkedHashMap<>();

                if (photoUrl == null) {
                    Logger.error("photo url is null");
                    submitTruck("");

                    return;
                } else {
                    try {
                        Logger.error("photo url is"  + photoUrl);
                        File file = new File(photoUrl);
                        if (file.exists())
                            param.put("img", file);
                        else {
                            Notice.show(getString(R.string.error_file_not_exist));
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                btnSubmit.setEnabled(false);
                HttpHelper.makeMultipartPostRequest(Constant.UPLOAD_PHOTO_API, param, new HttpResponseListener() {
                    @Override
                    public void OnSuccess(Object response) {
                        String strRet = (String) response;
                        try {
                            JSONObject jsonObject = new JSONObject(strRet);
                            final String photoPath = jsonObject.getString("PhotoPath");

                            Logger.error("Photo Path");
                            Logger.error(photoPath);
                            Handler mainHandler = new Handler(Looper.getMainLooper());
                            Runnable myRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    btnSubmit.setEnabled(true);
                                    submitTruck(photoPath);
                                }
                            };
                            mainHandler.post(myRunnable);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            btnSubmit.setEnabled(true);
                        }
                    }

                    @Override
                    public void OnFailure(Object error) {
                        String strRet = (String) error;
                        Misc.showResponseMessage( strRet);
                        btnSubmit.setEnabled(true);
                    }
                });
            }
        });

        imgTruck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertSelectImageType();
            }
        });
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertSelectImageType();
            }
        });
        ArrayList<String> list = new ArrayList();
        ArrayAdapter adapter = new ArrayAdapter(
                context,
                R.layout.spinner_item,
                list);
        adapter.setDropDownViewResource(R.layout.spinner_select);
        //spinnerBodyType.setAdapter(adapter);
        spinnerVehicleType.setAdapter(adapter);
        spinnerVehicleClass.setAdapter(adapter);
        spinnerCompany.setAdapter(adapter);

        txtOwnerIdentityDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Date now = new Date();
                    SimpleDateFormat convertFormat = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String strDate = "";
                    if(txtOwnerIdentityDate.getText().length() == 0) {
                        strDate = convertFormat.format(now);
                    }
                    else if(txtOwnerIdentityDate.getText().length() >= 0){
                        try {
                            Date date = parseFormat.parse(txtOwnerIdentityDate.getText().toString());
                            strDate = convertFormat.format(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    DatePickerPopWin pickerPopWin = new DatePickerPopWin.Builder(ActivitySubmitTruck.this, new DatePickerPopWin.OnDatePickedListener() {
                        @Override
                        public void onDatePickCompleted(int year, int month, int day, String dateDesc) {
                            txtOwnerIdentityDate.setText(String.format("%d-%d-%d" , year, month, day));
                        }
                    }).textConfirm(getString(R.string.btn_confirm)) //text of confirm button
                            .textCancel("")
                            .btnTextSize(16) // button text size
                            .viewTextSize(25) // pick view text size
                            .colorCancel(Color.parseColor("#999999")) //color of cancel button
                            .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                            .minYear(1950) //min year in loop
                            .maxYear(2030) // max year in loop
                            .showDayMonthYear(true) // shows like dd mm yyyy (default is false)
                            .dateChose(strDate)
                            .build();
                    pickerPopWin.showPopWin(ActivitySubmitTruck.this);
            }
        });


        submitMode =  getIntent().getIntExtra(Constant.INTENT_SUBMIT_TRUCK,0);
        Logger.error("submitMode=" + submitMode);

        getTruckBrands();
        getColours();
        getAllTruckClasses();
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        submitMode =  getIntent().getIntExtra(Constant.INTENT_SUBMIT_TRUCK,0);
        Logger.error("submitMode=" + submitMode);

        if(submitMode ==1)
            setAppTitle(getString(R.string.title_add_truck));
        else {
            setAppTitle(getString(R.string.title_edit_truck));

            String strTruck = getIntent().getStringExtra("truck_info");
            Truck truck = Serializer.getInstance().deserializeTruck(strTruck);
            editModel.setText(truck.getModel());
            editOwnerName.setText(truck.getOwnerName());
            editOwnerPhone.setText(truck.getOwnerPhone());
            editOwnerIdentity.setText(truck.getOwnerIdentity());
            editOwnerIdentitySource.setText(truck.getOwnerIdentitySource());
            txtOwnerIdentityDate.setText(truck.getOwnerIdentityDate());

            editPaletteNumber.setText(truck.getPaletteNumber());
            editProductionYear.setText(String.valueOf(truck.getProductionYear()));
            BaseApplication.getPicasso().load(truck.getPhoto()).into(imgTruck);

            //spinnerBodyType.setText(truck.getTruckBodyworkType());
            spinnerColor.setText(truck.getColor());
            spinnerCompany.setText(truck.getVehicleBrand());
            spinnerVehicleClass.setText(truck.getVehicleClassName());
            spinnerVehicleType.setText(truck.getTruckType());

            chkInsurance.setChecked(truck.isInsurance());
        }



        showNavHome(false);


//        spinnerBodyType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                currentBodyTypesPosition = position;
//            }
//        });
        spinnerColor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentColorPosition = position;
            }
        });

        spinnerVehicleType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentTruckTypePosition = position;
            }
        });
        spinnerCompany.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentBrandPosition = position;
            }
        });

        spinnerVehicleClass.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FillTruckType(position);
            }
        });
    }

    private void FillTruckType(int position)
    {
        currentClassPosition = position;
        if (truckClasses.size() == 0) return;
        TruckClass truckClass = truckClasses.get(position);


        bodyTypes.clear();
        truckTypes.clear();

        for (TruckBodyType bodyType : truckClass.getBodytypes()) {
            bodyTypes.add(bodyType.getName());
        }

        ArrayAdapter adapter = new ArrayAdapter(
                context,
                R.layout.spinner_item,
                bodyTypes);
        adapter.setDropDownViewResource(R.layout.spinner_select);
        //spinnerBodyType.setAdapter(adapter);


        for (TruckType truckType : truckClass.getTypes()) {
            truckTypes.add(truckType.getName());
        }

        ArrayAdapter truckTypesAdapter = new ArrayAdapter(
                context,
                R.layout.spinner_item,
                truckTypes);
        adapter.setDropDownViewResource(R.layout.spinner_select);
        spinnerVehicleType.setAdapter(truckTypesAdapter);

        if(submitMode == 2) {
            for(int i =0 ; i <  truckClass.getTypes().size(); i++ ) {
                TruckType truckType =   truckClass.getTypes().get(i);

                if(truckType.getName().equals(spinnerVehicleType.getText().toString())) {
                    currentTruckTypePosition = i;
                }
            }
        }

//                if(submitMode == 2) {
//                    for(int i =0 ; i <  truckClass.getBodytypes().size(); i++ ) {
//                        TruckBodyType truckBodyType =  truckClass.getBodytypes().get(i);
//
//                        if(truckBodyType.getName().equals(spinnerBodyType.getText().toString())) {
//                            Logger.error("currentClassPositin" + i);
//                            currentBodyTypesPosition = i;
//                        }
//                    }
//                }
    }

    private void getTruckBrands() {
        HttpUtil httpUtil = new HttpUtil();
        User user = UserManager.with(context).getCurrentUser();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());
        httpUtil.get(Constant.GET_TRUCK_BRAND_API, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String paramString = new String(responseBody);
                paramString = StringUtil.escapeString(paramString);

                truckBrands = new ArrayList<>();

                try {
                    JSONArray jsonArray = new JSONArray(paramString);
                    for (int j = 0; j < jsonArray.length(); j++) {
                        TruckBrand truckBrand = BaseApplication.getGson().fromJson(jsonArray.get(j).toString(), TruckBrand.class);
                        truckBrands.add(truckBrand);
                        brandList.add(truckBrand.getName());
                    }


                    ArrayAdapter adapter = new ArrayAdapter(
                            context,
                            R.layout.spinner_item,
                            brandList);
                    adapter.setDropDownViewResource(R.layout.spinner_select);
                    spinnerCompany.setAdapter(adapter);



                    if(submitMode == 2) {
                        for(int i =0 ; i < truckBrands.size(); i++ ) {
                            TruckBrand truckBrand = truckBrands.get(i);

                            if(truckBrand.getName().equals(spinnerCompany.getText().toString())) {
                                Logger.error("currentClassPositin" + i);
                                currentBrandPosition = i;
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    private void getColours() {

        Logger.error("getColours submitMode=" + submitMode);

        HttpUtil httpUtil = new HttpUtil();
        User user = UserManager.with(context).getCurrentUser();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());
        httpUtil.get(Constant.GET_COLOR_API, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String paramString = new String(responseBody);
                paramString = StringUtil.escapeString(paramString);

                colors = new ArrayList<>();
                try {
                    JSONArray jsonArray = new JSONArray(paramString);
                    for (int j = 0; j < jsonArray.length(); j++) {
                        Colour colour = BaseApplication.getGson().fromJson(jsonArray.get(j).toString(), Colour.class);
                        colors.add(colour);
                        colorNameList.add(colour.getName());
                    }

                    ArrayAdapter adapter = new ArrayAdapter(
                            context,
                            R.layout.spinner_item,
                            colorNameList);
                    adapter.setDropDownViewResource(R.layout.spinner_select);
                    spinnerColor.setAdapter(adapter);

                    Logger.error("submitMode=" + submitMode);

                    String strTruck = getIntent().getStringExtra("truck_info");
                    Truck truck = Serializer.getInstance().deserializeTruck(strTruck);

                    if(submitMode == 2) {
                        for(int i =0 ; i < colors.size(); i++ ) {
                            Colour colour = colors.get(i);

                            if(colour.getColorID().equals(String.valueOf(truck.getColorID()))) {
                                spinnerColor.setText(colour.getName());
                                currentColorPosition = i;
                            }
                        }
                    }

                    Logger.error("currentColorPosition=" + currentColorPosition);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    private void getAllTruckClasses() {
        HttpUtil httpUtil = new HttpUtil();
        User user = UserManager.with(context).getCurrentUser();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());
        httpUtil.get(Constant.GET_TRUCK_CLASSES_API, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String paramString = new String(responseBody);
                paramString = StringUtil.escapeString(paramString);

                truckClasses = new ArrayList<>();

                try {
                    JSONArray jsonArray = new JSONArray(paramString);
                    for (int j = 0; j < jsonArray.length(); j++) {
                        TruckClass truckClass = BaseApplication.getGson().fromJson(jsonArray.get(j).toString(), TruckClass.class);
                        truckClasses.add(truckClass);
                        classNameList.add(truckClass.getName());
                    }

                    ArrayAdapter adapter = new ArrayAdapter(
                            context,
                            R.layout.spinner_item,
                            classNameList);
                    adapter.setDropDownViewResource(R.layout.spinner_select);
                    spinnerVehicleClass.setAdapter(adapter);

                    if(truckClasses.size()==1) {
                        currentClassPosition = 0;
                        FillTruckType(0);
                    }
                    if(submitMode == 2) {
                        for(int i =0 ; i < truckClasses.size(); i++ ) {
                            TruckClass truckClass = truckClasses.get(i);

                            if(truckClass.getName().equals(spinnerVehicleClass.getText().toString())) {
                                Logger.error("currentClassPositin" + i);
                                currentClassPosition = i;


                                if(submitMode == 2) {
                                    for(int ii =0 ; ii <  truckClass.getTypes().size(); ii++ ) {
                                        TruckType truckType =   truckClass.getTypes().get(ii);

                                        if(truckType.getName().equals(spinnerVehicleType.getText().toString())) {
                                            currentTruckTypePosition = ii;
                                        }
                                    }
                                }

                                /*if(submitMode == 2) {
                                    for(int iii =0 ; iii <  truckClass.getBodytypes().size(); iii++ ) {
                                        TruckBodyType truckBodyType =  truckClass.getBodytypes().get(iii);

                                        if(truckBodyType.getName().equals(spinnerBodyType.getText().toString())) {
                                            Logger.error("currentClassPositin" + iii);
                                            currentBodyTypesPosition = iii;
                                        }
                                    }
                                }*/

                            }
                        }
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }


    private void submitTruck(String photoPath) {
        RequestParams params = new RequestParams();

        //if (currentClassPosition == -1 || currentBodyTypesPosition == -1 || currentColorPosition == -1 || currentBrandPosition == -1 || currentTruckTypePosition == -1)
        if (currentClassPosition == -1 || currentColorPosition == -1 || currentBrandPosition == -1 || currentTruckTypePosition == -1)
            return;

        TruckClass truckClass = truckClasses.get(currentClassPosition);

        Colour color = colors.get(currentColorPosition);
        TruckBrand truckBrand = truckBrands.get(currentBrandPosition);

        if (editPaletteNumber.getText().toString().length() == 0) {
            editPaletteNumber.requestFocus();
            editPaletteNumber.setError(getString(R.string.error_enter_pallet_number));
            return;
        }

        if (editProductionYear.getText().toString().length() == 0) {
            editProductionYear.requestFocus();
            editProductionYear.setError(getString(R.string.error_enter_production_year));
            return;
        }

        if (editModel.getText().toString().length() == 0) {
            editModel.requestFocus();
            editModel.setError(getString(R.string.error_enter_model));
            return;
        }

        if (editOwnerName.getText().toString().length() == 0) {
            editOwnerName.requestFocus();
            editOwnerName.setError(getString(R.string.error_enter_owner_name));
            return;
        }

        if (editOwnerPhone.getText().toString().length() == 0) {
            editOwnerPhone.requestFocus();
            editOwnerPhone.setError(getString(R.string.error_enter_owner_phone));
            return;
        }

        if (editOwnerIdentity.getText().toString().length() == 0) {
            editOwnerIdentity.requestFocus();
            editOwnerIdentity.setError(getString(R.string.error_enter_owner_identity));
            return;
        }
        if (editOwnerIdentitySource.getText().toString().length() == 0) {
            editOwnerIdentitySource.requestFocus();
            editOwnerIdentitySource.setError(getString(R.string.error_enter_owner_identity_source));
            return;
        }

        if (txtOwnerIdentityDate.getText().toString().length() == 0) {
            Notice.show(getString(R.string.error_enter_identity_date));
            return;
        }

        SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = parseFormat.parse(txtOwnerIdentityDate.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        params.put("ColorMultiLingualID", String.valueOf(color.getColorID()));
        params.put("VehicleBrandID", String.valueOf(truckBrand.getCompanyID()));
        params.put("VehicleClassID", String.valueOf(truckClass.getClassID()));
        params.put("ProductionYear", editProductionYear.getText().toString());
        //params.put("TruckBodyworkTypeID", truckClass.getBodytypes().get(currentBodyTypesPosition).getID());
        params.put("TruckTypeID", truckClass.getTypes().get(currentTruckTypePosition).getID());
        params.put("Model", editModel.getText().toString());

        params.put("PaletteNumber", editPaletteNumber.getText().toString());
        params.put("OwnerName", editOwnerName.getText().toString());
        params.put("OwnerPhone", editOwnerPhone.getText().toString());
        params.put("OwnerIdentity", editOwnerIdentity.getText().toString());
        params.put("OwnerIdentitySource", editOwnerIdentitySource.getText().toString());
        params.put("OwnerIdentityDate", txtOwnerIdentityDate.getText().toString());
        params.put("Insurance", String.valueOf(chkInsurance.isChecked()));
        params.put("ImageURL", photoPath);

        HttpUtil httpUtil = new HttpUtil();

        User user = UserManager.with(this).getCurrentUser();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());

        btnSubmit.setEnabled(false);

        Logger.error(params.toString());
        int submitMode =  getIntent().getIntExtra(Constant.INTENT_SUBMIT_TRUCK,0);

        if(submitMode == 1)
            httpUtil.post(Constant.ADD_TRUCK_API, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Misc.showResponseMessage(responseBody);
                    ActivitySubmitTruck.this.finish();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Misc.showResponseMessage(responseBody);
                }
                @Override
                public void onFinish(){
                    btnSubmit.setEnabled(true);
                }
            });
        else if(submitMode == 2){
            String truckId = getIntent().getStringExtra("truck_id");
            params.put("ID", truckId);
            httpUtil.put(Constant.EDIT_TRUCK_API.concat("/").concat(truckId), params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Misc.showResponseMessage(responseBody);
                    btnSubmit.setEnabled(true);
                    ActivitySubmitTruck.this.finish();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Misc.showResponseMessage(responseBody);
                    btnSubmit.setEnabled(true);
                }
            });
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


    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
    }

    private void alertSelectImageType() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View viewSelLang = getLayoutInflater().inflate(R.layout.dialog_get_image, null);
        final RadioButton radioCamera = (RadioButton) viewSelLang.findViewById(R.id.radio_camera);

        final Button btnOk = (Button) viewSelLang.findViewById(R.id.btn_ok);
        final Button btnCancel = (Button) viewSelLang.findViewById(R.id.btn_cancel);

        builder.setView(viewSelLang);

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
                if (radioCamera.isChecked())
                    openCamera();
                else
                    openGallery();
                alertDialog.dismiss();
            }
        });
    }

    private void openCamera() {
        Date nowTime = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH-mm-ss");
        String strTime = timeFormatter.format(nowTime);
        String strDate = dateFormatter.format(nowTime);
        String name= strDate+"-"+strTime;
        destination = new File(Environment.getExternalStorageDirectory(), name + ".jpg");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Uri destinationURI = FileProvider.getUriForFile(
                context,
                context.getApplicationContext()
                        .getPackageName() + ".provider", destination);


        intent.putExtra(MediaStore.EXTRA_OUTPUT, destinationURI );
        startActivityForResult(intent, PICK_Camera_IMAGE);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_IMAGE)
                if (data != null) {
                    photoUrl = Misc.getRealPathFromURI(data.getData());
                    try {
                        File file = new File(photoUrl);
                        if (file.exists())
                            Picasso.with(context).load(file).into(imgTruck);
                    } catch (Exception e) {
                            e.printStackTrace();
                    }
                }
            if (requestCode == PICK_Camera_IMAGE) {
                photoUrl = destination.getAbsolutePath();
                try {
                        File file = new File(photoUrl);
                        if (file.exists()) Picasso.with(context).load(file).into(imgTruck);
                } catch (Exception e) {
                        e.printStackTrace();
                }
            }
        }
    }
}
