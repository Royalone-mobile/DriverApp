package com.sawatruck.driver.view.activity;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.Truck;
import com.sawatruck.driver.utils.Serializer;
import com.sawatruck.driver.view.design.CustomTextView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by royal on 8/22/2017.
 */


public class ActivityTruckDetail extends BaseActivity {
    @Bind(R.id.txt_model) CustomTextView txtModel;
    @Bind(R.id.txt_vehicle_brand) CustomTextView txtVehicleBrand;
    @Bind(R.id.txt_production_year) CustomTextView txtProductionYear;
    @Bind(R.id.txt_color) CustomTextView txtColor;
    @Bind(R.id.txt_palette_number) CustomTextView txtPaletteNumber;
    @Bind(R.id.chk_insurance) CheckBox chkInsurnace;
    @Bind(R.id.txt_vehicle_class) CustomTextView txtVehicleClass;
    @Bind(R.id.txt_bodywork_type) CustomTextView txtBodyworkType;
    @Bind(R.id.txt_vehicle_type) CustomTextView txtVehicleType;
    @Bind(R.id.txt_owner_name) CustomTextView txtOwnerName;
    @Bind(R.id.txt_owner_identity) CustomTextView txtIdentity;
    @Bind(R.id.txt_owner_identity_source) CustomTextView txtIdentitySource;
    @Bind(R.id.txt_owner_identity_date) CustomTextView txtIdentityDate;
    @Bind(R.id.txt_owner_phone) CustomTextView txtOwnerPhone;

    @Bind(R.id.img_truckphoto) ImageView imgTruckPhoto;
    @Bind(R.id.toolbar_edit) TextView btnEditTruck;

    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.driver_activity_mytruck_details, null);
        ButterKnife.bind(this, view);
        btnEditTruck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityTruckDetail.this, ActivitySubmitTruck.class);

                String strTruck = getIntent().getStringExtra("truck_info");
                Truck truck = Serializer.getInstance().deserializeTruck(strTruck);

                intent.putExtra(Constant.INTENT_SUBMIT_TRUCK, 2);
                intent.putExtra("truck_id", truck.getID());
                intent.putExtra("truck_info", strTruck);

                startActivity(intent);
                finish();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        showNavHome(false);
        initView();
    }

    public void initView() {
        try {
            Intent intent = getIntent();
            String strTruck = intent.getStringExtra("truck_info");
            Truck truck = Serializer.getInstance().deserializeTruck(strTruck);
            txtModel.setText(truck.getModel());
            txtVehicleBrand.setText(truck.getVehicleBrand());
            txtProductionYear.setText(String.valueOf(truck.getProductionYear()));
            txtColor.setText(truck.getColor());
            txtPaletteNumber.setText(truck.getPaletteNumber());
            txtVehicleClass.setText(truck.getVehicleClassName());
            txtBodyworkType.setText(truck.getTruckBodyworkType());
            txtVehicleType.setText(truck.getTruckType());
            txtOwnerName.setText(truck.getOwnerName());
            txtIdentity.setText(truck.getOwnerIdentity());
            txtIdentitySource.setText(truck.getOwnerIdentitySource());
            txtIdentityDate.setText(truck.getOwnerIdentityDate());
            txtOwnerPhone.setText(truck.getOwnerPhone());
            chkInsurnace.setChecked(truck.isInsurance());
            setAppTitle(getString(R.string.title_truck) + " " + truck.getPaletteNumber());
            BaseApplication.getPicasso().load(truck.getPhoto()).placeholder(R.drawable.ico_truck).into(imgTruckPhoto);
        } catch (Exception e) {
            e.printStackTrace();
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
}
