package com.sawatruck.driver.view.activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.R;
import com.sawatruck.driver.utils.AppSettings;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.view.design.CustomTextView;

import java.util.List;


public abstract class BaseActivity extends AppCompatActivity {
    static String TAG = BaseActivity.class.getSimpleName();
    protected Context context;
    private Toolbar toolbar;
    private static boolean active = false;
    public static boolean toggleState = false;  //false -- home true -- back
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        initLayout();
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        initToolbar();
        Misc.applyLocale(this);
    }


    /**
     * init layout
     */
    private void initLayout() {
        View v = getContentView();
        setContentView(v);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void initToolbar()
    {
        try {
            toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
            toolbar.setNavigationIcon(R.drawable.ico_home);
            CustomTextView toolbarTitle = (CustomTextView) toolbar.findViewById(R.id.toolbar_title);

            toolbarTitle.setBold(true);

            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void showNavHome(boolean bHome) {
        if(toolbar==null) return;
        if(bHome) {
            toolbar.setNavigationIcon(R.drawable.ico_home);
            toggleState = true;
        }
        else {
            if(AppSettings.with(BaseApplication.getContext()).getLangCode().equals(getString(R.string.locale_arabic))){
                toolbar.setNavigationIcon(R.drawable.ico_back_ar);
            }
            else if(AppSettings.with(BaseApplication.getContext()).getLangCode().equals(getString(R.string.locale_english))){
                toolbar.setNavigationIcon(R.drawable.ico_back);
            }

            toggleState = false;
        }
    }

    public void showOptions(boolean bOption){
        if(toolbar==null) return;
        ImageView btnNotification  = (ImageView)findViewById(R.id.btn_notification);
        ImageView btnSearchFilter  = (ImageView)findViewById(R.id.btn_searchfilter);
        CustomTextView txtNotificationCount = (CustomTextView ) findViewById(R.id.txt_notification_count);

        ImageView btnAddAd  = (ImageView)findViewById(R.id.btn_add_ad);
        if(bOption) {
            btnNotification.setVisibility(View.VISIBLE);
            btnSearchFilter.setVisibility(View.VISIBLE);
            txtNotificationCount.setVisibility(View.VISIBLE);
            btnAddAd.setVisibility(View.GONE);
        }
        else {
            btnNotification.setVisibility(View.GONE);
            btnSearchFilter.setVisibility(View.GONE);
            txtNotificationCount.setVisibility(View.GONE);
            btnAddAd.setVisibility(View.GONE);
        }
    }

    public void showAddAd(){
        if(toolbar==null) return;
        ImageView btnAddAd  = (ImageView)findViewById(R.id.btn_add_ad);
        btnAddAd.setVisibility(View.VISIBLE);
    }
    public void setAppTitle(String title){
        try {
            toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
            CustomTextView txtTitle = (CustomTextView) toolbar.findViewById(R.id.toolbar_title);
            txtTitle.setText(title);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected abstract View getContentView();

    private void changeFragment(int id, Fragment fragment, String tag) {
        if (tag == null)
            getSupportFragmentManager().beginTransaction().replace(id, fragment).commit();
        else {
            getSupportFragmentManager().beginTransaction().replace(id, fragment, tag).commit();

        }
    }

    private void changeFragment(int container, Fragment fragment) {
        changeFragment(container, fragment, null);
    }

    private void clearFragments(@Nullable String tag) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> allFragments = fragmentManager.getFragments();
        if (allFragments != null && allFragments.size() > 0) {

            for (Fragment frag : allFragments) {
                if (frag != null && (tag == null || (frag.getTag() != null && frag.getTag().equals(tag)))) {
                    fragmentManager.beginTransaction().remove(frag).commit();
                }
            }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy(){

        super.onDestroy();
        active = false;
    }

    public void setFullScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    public void onStart(){
        super.onStart();
        active = true;
    }
    @Override
    public void onStop(){
        super.onStop();

    }
}
