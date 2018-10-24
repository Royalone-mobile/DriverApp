package com.sawatruck.driver.view.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.bruce.pickerview.popwindow.DatePickerPopWin;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.User;
import com.sawatruck.driver.entities.UserInfo;
import com.sawatruck.driver.utils.HttpHelper;
import com.sawatruck.driver.utils.HttpResponseListener;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.Logger;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.utils.NetUtil;
import com.sawatruck.driver.utils.Notice;
import com.sawatruck.driver.utils.StringUtil;
import com.sawatruck.driver.utils.UserManager;
import com.sawatruck.driver.view.design.CircularImage;
import com.sawatruck.driver.view.design.CustomEditText;
import com.sawatruck.driver.view.design.CustomTextView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import io.ghyeok.stickyswitch.widget.StickySwitch;

public class ActivityEditProfile extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.img_avatar)CircularImage imgAvatar;
    @Bind(R.id.edit_firstname) CustomEditText editFirstName;
    @Bind(R.id.edit_lastname) CustomEditText editLastName;
    @Bind(R.id.edit_company_name) CustomEditText editCompanyName;
    @Bind(R.id.edit_mobile_phone) CustomEditText editMobilePhone;
    @Bind(R.id.edit_bio) CustomEditText editBio;
    @Bind(R.id.edit_email) CustomEditText editEmail;
    @Bind(R.id.btn_save) CustomTextView btnSave;
    @Bind(R.id.btn_change_photo) View btnChangePhoto;
    @Bind(R.id.txt_birthday) CustomTextView txtBirthday;
    @Bind(R.id.switch_gender) StickySwitch switchGender;
    private static final int PICK_Camera_IMAGE = 2;
    private final int SELECT_IMAGE = 1000;
    private static String photoUrl;
    private File destination;
    private boolean bFromPhone  = false;

    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_editprofile,null);
        ButterKnife.bind(this, view);


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        btnChangePhoto.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        txtBirthday.setOnClickListener(this);

        initView();
        return view;
    }


    public void getProfile(){
        HttpUtil httpUtil = HttpUtil.getInstance();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION,         UserManager.with(this).getCurrentUser().getToken());

        HttpUtil.getInstance().get(Constant.USER_VIEWPROFILE_API, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String paramString = new String(responseBody);
                paramString = StringUtil.escapeString(paramString);
                try {
                    JSONObject jsonObject = new JSONObject(paramString);
                    JSONObject contentObject =  jsonObject.getJSONObject("Object");
                    UserInfo userInfo = BaseApplication.getGson().fromJson(contentObject.toString(), UserInfo.class);
                    txtBirthday.setText(userInfo.getBirthDay());
                    editBio.setText(userInfo.getDetails());

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

    private void initView(){
        try {
            User user = UserManager.with(this).getCurrentUser();
            editEmail.setText(user.getEmail());
            String firstName = user.getFullName().substring(0, user.getFullName().lastIndexOf(" "));
            String lastName = user.getFullName().substring(user.getFullName().lastIndexOf(" "), user.getFullName().length());
            editFirstName.setText(firstName);
            editLastName.setText(lastName);
            BaseApplication.getPicasso().load(user.getPhotoUrl()).placeholder(R.drawable.ico_user).fit().into(imgAvatar);
            photoUrl = user.getPhotoUrl();
            if (user.isGender())
                switchGender.setDirection(StickySwitch.Direction.LEFT);
            else
                switchGender.setDirection(StickySwitch.Direction.RIGHT);
            editMobilePhone.setText(user.getPhone());

            getProfile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeBirthday(){
        Date now = new Date();
        Locale locale = new Locale(Locale.ENGLISH.getLanguage());
        SimpleDateFormat convertFormat = new SimpleDateFormat("yyyy-MM-dd",locale);
        SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd",locale);
        String strDate = "";
        if(txtBirthday.getText().length() == 0) {
            strDate = convertFormat.format(now);
        }
        else if(txtBirthday.getText().length() >= 0){
            try {
                Date date = parseFormat.parse(txtBirthday.getText().toString());
                strDate = convertFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        DatePickerPopWin pickerPopWin = new DatePickerPopWin.Builder(this, new DatePickerPopWin.OnDatePickedListener() {
            @Override
            public void onDatePickCompleted(int year, int month, int day, String dateDesc) {
                java.text.NumberFormat mNumberFormat= java.text.NumberFormat.getIntegerInstance(Locale.US);
                mNumberFormat.setGroupingUsed(false);
                txtBirthday.setText(String.format("%s-%s-%s", mNumberFormat.format(year), mNumberFormat.format(month),mNumberFormat.format(day)));
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
        pickerPopWin.showPopWin(this);
    }
    @Override
    public void onResume(){
        super.onResume();
        setAppTitle(getResources().getString(R.string.title_edit_profile));
        showNavHome(false);
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_change_photo:
                alertSelectImageType();
                break;
            case R.id.btn_save:
                changeProfile();
                break;
            case R.id.txt_birthday:
                changeBirthday();
                break;
        }
    }


    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),SELECT_IMAGE);
    }

    private void alertSelectImageType(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View viewSelLang = getLayoutInflater().inflate(R.layout.dialog_get_image,null);
        final RadioButton radioCamera = (RadioButton)viewSelLang.findViewById(R.id.radio_camera);

        final Button btnOk = (Button)viewSelLang.findViewById(R.id.btn_ok);
        final Button btnCancel = (Button)viewSelLang.findViewById(R.id.btn_cancel);

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
                if(radioCamera.isChecked())
                    openCamera();
                else
                    openGallery();
                alertDialog.dismiss();
            }
        });
    }

    private void openCamera(){
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
        if (resultCode == Activity.RESULT_OK){
            try {
                if(requestCode == SELECT_IMAGE)
                    if (data != null) {
                        bFromPhone = true;
                        photoUrl = Misc.getRealPathFromURI(data.getData());
                        File file = new File(photoUrl);
                        if (file.exists())
                            Picasso.with(context).load(file).into(imgAvatar);
                    }
                if(requestCode == PICK_Camera_IMAGE)
                    photoUrl = destination.getAbsolutePath();
                    bFromPhone = true;
                    File file = new File(photoUrl);
                    if (file.exists())
                        Picasso.with(context).load(file).into(imgAvatar);

            }
            catch (Exception e){
                e.printStackTrace();
            }
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


    private void changeProfile(){
        if(editEmail.getText().toString().length() == 0){
            editEmail.setError(getString(R.string.error_enter_email));
            editEmail.requestFocus();
            return;
        }

        if(editFirstName.getText().toString().length() == 0){
            editFirstName.setError(getString(R.string.error_enter_firstname));
            editFirstName.requestFocus();
            return;
        }

        if(editLastName.getText().toString().length() == 0){
            editLastName.setError(getString(R.string.error_enter_lastname));
            editLastName.requestFocus();
            return;
        }

        if(editMobilePhone.getText().toString().length() == 0 ){
            editMobilePhone.setError(getString(R.string.error_enter_phonenumber));
            editMobilePhone.requestFocus();
            return;
        }

        if(txtBirthday.getText().toString().length() == 0){
            Notice.show(getString(R.string.error_enter_birthday));
            return;
        }

        final LinkedHashMap<String, Object> param = new LinkedHashMap<String, Object>();

        if(photoUrl==null) {
            Notice.show(getString(R.string.error_add_profile_photo));
            return;
        }
        else{
            if(bFromPhone) {
                try {
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
            else {
                uploadProfile(photoUrl);
                btnSave.setEnabled(false);
                return;
            }
        }

        btnSave.setEnabled(false);
        HttpHelper.makeMultipartPostRequest(Constant.UPLOAD_USER_PHOTO_API, param, new HttpResponseListener() {

            @Override
            public void OnSuccess(Object response) {
                String strRet = (String)response;
                try {
                    JSONObject jsonObject = new JSONObject(strRet);
                    final String photoPath = jsonObject.getString("PhotoPath");


                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            uploadProfile(photoPath);
                        }
                    };
                    mainHandler.post(myRunnable);

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnSave.setEnabled(true);
                        }
                    });

                }
            }

            @Override
            public void OnFailure(Object error) {
                try {
                    String strRet = (String) error;
                    Misc.showResponseMessage(strRet);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnSave.setEnabled(true);
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void uploadProfile(String photoPath){
        User user  = UserManager.with(this).getCurrentUser();
        final User newUser = user;
        newUser.setEmail(editEmail.getText().toString());
        newUser.setFullName(editFirstName.getText().toString() + " " + editLastName.getText().toString());
        newUser.setPhotoUrl(photoPath);
        RequestParams params = new RequestParams();
        params.put("ImageUrl",photoPath);
        params.put("ID", user.getUserId());
        if(switchGender.getDirection()== StickySwitch.Direction.LEFT) {
            params.put("Gender", true);
            newUser.setGender(true);
        }
        else if(switchGender.getDirection()== StickySwitch.Direction.RIGHT) {
            params.put("Gender", false);
            newUser.setGender(false);
        }
        params.put("Email", editEmail.getText().toString());
        params.put("CountryID", Misc.getCountryDialCode());
        params.put("NationalityCountryID", Misc.getCountryDialCode());
        params.put("MobileCountryID", Misc.getCountryDialCode());
        params.put("Birthday", txtBirthday.getText().toString());
        params.put("FirstName", editFirstName.getText().toString());
        params.put("LastName", editLastName.getText().toString());
        params.put("PhoneNumber", editMobilePhone.getText().toString());
        params.put("Details", editBio.getText().toString());

        HttpUtil httpUtil = HttpUtil.getInstance();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());

        httpUtil.put(Constant.USER_EDITPROFILE_API + "/" + user.getUserId(), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Misc.showResponseMessage(responseBody);
                    UserManager.with(context).setCurrentUser(newUser);
                    ActivityEditProfile.this.finish();
                    MainActivity.updateNavigationBar();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    Misc.showResponseMessage(responseBody);
                    btnSave.setEnabled(true);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}