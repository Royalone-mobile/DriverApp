package com.sawatruck.driver.view.activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.paypal.android.sdk.payments.PayPalService;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.controller.LocationUpdateService;
import com.sawatruck.driver.controller.RegionCodeAPI;
import com.sawatruck.driver.controller.UpdateService;
import com.sawatruck.driver.entities.GetToDo;
import com.sawatruck.driver.entities.User;
import com.sawatruck.driver.utils.AppSettings;
import com.sawatruck.driver.utils.GPSTracker;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.Logger;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.utils.NetUtil;
import com.sawatruck.driver.utils.StringUtil;
import com.sawatruck.driver.utils.UserManager;
import com.sawatruck.driver.view.adapter.DrawerAdapter;
import com.sawatruck.driver.view.design.CircularImage;
import com.sawatruck.driver.view.design.CustomTextView;
import com.sawatruck.driver.view.design.DrawerOption;
import com.sawatruck.driver.view.fragments.FragmentMenuAd;
import com.sawatruck.driver.view.fragments.FragmentMenuBalance;
import com.sawatruck.driver.view.fragments.FragmentMenuBids;
import com.sawatruck.driver.view.fragments.FragmentMenuHome;
import com.sawatruck.driver.view.fragments.FragmentMenuInBox;
import com.sawatruck.driver.view.fragments.FragmentMenuRating;
import com.sawatruck.driver.view.fragments.FragmentMenuSetting;
import com.sawatruck.driver.view.fragments.FragmentMenuToDo;
import com.sawatruck.driver.view.fragments.FragmentMenuTruck;
import com.sawatruck.driver.view.fragments.FragmentPostAd;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import io.fabric.sdk.android.Fabric;
import me.leolin.shortcutbadger.ShortcutBadger;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private DrawerOption[] mValues;
    private DrawerAdapter mAdapter;
    private static ListView _DrawerListView;
    private Bundle _savedInstanceState;
    private static MainActivity _instance;
    @Bind(R.id.fragment_container)
    FrameLayout fragmentContainer;
    private FragmentMenuHome homeFragment;
    private FragmentMenuInBox inboxFragment;
    private FragmentMenuBalance myBalanceFragment;
    private FragmentMenuBids myBidsFragment;
    private FragmentMenuAd myAdsFragment;
    private FragmentMenuTruck myTruckFragment;
    private FragmentMenuRating myRatingFragment;
    private FragmentMenuSetting settingFragment;
    private FragmentMenuToDo fragmentMenuToDo;

    private SlidingMenu slidingMenu;
    private View sideMenu;

    private long[] mHits = new long[2];

    public static MainActivity getInstance(){
        return _instance;
    }

    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_main,null);
        ButterKnife.bind(this, view);
        _instance = this;

        Fabric.with(this, new Crashlytics());

        requestCameraPermission();
        requestReadStoragePermission();
        requestLocationPermission();
        requestCallPermission();
        NetUtil.turnGPSOn();

        checkTravels();


        int userType = UserManager.with(this).getUserType();

        if(userType == 0) {
            setupGuestNavigationBar();
            FragmentMenuHome homeFragment = new FragmentMenuHome();
            inflateFragmentWithTag(homeFragment, FragmentMenuHome.TAG);
        }
        else {
            showNavHome(true);
            setupNavigationBar();
            setupFragments();
        }

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, Constant.paypalConfig);
        startService(intent);


        User user = UserManager.with(this).getCurrentUser();
        Logger.error(user.getToken());


        Intent updateServiceIntent = new Intent(this, UpdateService.class);
        startService(updateServiceIntent);

        return view;
    }

    private void fetchGetToDo() {
        HttpUtil httpUtil = HttpUtil.getInstance();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, UserManager.with(context).getCurrentUser().getToken());

        httpUtil.get(Constant.GET_TO_DO_API, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String paramString = new String(responseBody);
                    paramString = StringUtil.escapeString(paramString);
                    JSONArray jsonArray= new JSONArray(paramString);

                    Logger.error("todo size = " + jsonArray.length());
                    mAdapter.getItem(7).setCount(jsonArray.length());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }

    @Override
    public void onDestroy(){
        _instance = null;
        stopService(new Intent(this, PayPalService.class));
        stopService(new Intent(this, UpdateService.class));
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static void installNewVersion(String url) {
        _instance.installApp(url);
    }

    private void installApp(String url) {
        new DownloadNewVersion().execute(url);
    }

    private void setupGuestNavigationBar(){
        slidingMenu = new SlidingMenu(this);

        if(AppSettings.with(BaseApplication.getContext()).getLangCode().equals(getString(R.string.locale_arabic))){
            slidingMenu.setMode(SlidingMenu.RIGHT);
            slidingMenu.setTouchModeAbove(SlidingMenu.RIGHT);
        }
        else if(AppSettings.with(BaseApplication.getContext()).getLangCode().equals(getString(R.string.locale_english))){
            slidingMenu.setMode(SlidingMenu.LEFT);
            slidingMenu.setTouchModeAbove(SlidingMenu.LEFT);
        }


        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        slidingMenu.setFadeEnabled(true);

        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW );
        View sideMenuGuest = getLayoutInflater().inflate(R.layout.sidemenu_guest, null, false);
        CustomTextView btnLogIn = (CustomTextView) sideMenuGuest.findViewById(R.id.btn_login);
        CustomTextView btnCreateAccount = (CustomTextView) sideMenuGuest.findViewById(R.id.btn_create_account);
        CustomTextView txtNoticeGuest = (CustomTextView)sideMenuGuest.findViewById(R.id.txt_notice_guest);
        CustomTextView txtVersion = (CustomTextView)sideMenuGuest.findViewById(R.id.txt_version);

        txtVersion.setText(Misc.getVersionName(BaseApplication.getContext()));

        txtNoticeGuest.setText(Html.fromHtml(getString(R.string.notice_guest)));

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaseApplication.getContext(), ActivitySignIn.class);
                startActivity(intent);
                finish();
            }
        });

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaseApplication.getContext(), ActivitySignUp.class);
                startActivity(intent);
                finish();
            }
        });
        slidingMenu.setMenu(sideMenuGuest);

    }


    public static void updateNavigationBar(){
        if(_instance == null) return;
        CircularImage navAvatar = (CircularImage) _instance.sideMenu.findViewById(R.id.nav_user_avatar);
        CustomTextView navUserName = (CustomTextView) _instance.sideMenu.findViewById(R.id.nav_user_name);
        navUserName.setBold(true);

        User user = UserManager.with(_instance).getCurrentUser();

        try {
            BaseApplication.getPicasso().load(user.getPhotoUrl()).placeholder(R.drawable.ico_user).fit().into(navAvatar);
        }catch (Exception e){
            e.printStackTrace();
        }
        navUserName.setText(user.getFullName());
    }

    private void setupNavigationBar(){
        slidingMenu = new SlidingMenu(this);
        if(AppSettings.with(BaseApplication.getContext()).getLangCode().equals(getString(R.string.locale_arabic))){
            slidingMenu.setMode(SlidingMenu.RIGHT);
            slidingMenu.setTouchModeAbove(SlidingMenu.RIGHT);
        }
        else if(AppSettings.with(BaseApplication.getContext()).getLangCode().equals(getString(R.string.locale_english))){
            slidingMenu.setMode(SlidingMenu.LEFT);
            slidingMenu.setTouchModeAbove(SlidingMenu.LEFT);
        } else if(AppSettings.with(BaseApplication.getContext()).getLangCode().equals(getString(R.string.locale_spanish))) {
            slidingMenu.setMode(SlidingMenu.LEFT);
            slidingMenu.setTouchModeAbove(SlidingMenu.LEFT);
        }
        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        slidingMenu.setFadeEnabled(true);

        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        sideMenu = getLayoutInflater().inflate(R.layout.sidemenu, null, false);
        slidingMenu.setMenu(sideMenu);

        _DrawerListView = (ListView)sideMenu.findViewById(R.id.nav_menulist);
        _DrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onNavItemSelected(position);
            }
        });

        CircularImage navAvatar = (CircularImage) sideMenu.findViewById(R.id.nav_user_avatar);
        CustomTextView navUserName = (CustomTextView) sideMenu.findViewById(R.id.nav_user_name);
        navUserName.setBold(true);

        navAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ActivityViewProfile.class);
                startActivity(intent);
            }
        });
        navUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ActivityViewProfile.class);
                startActivity(intent);
            }
        });

        User user = UserManager.with(this).getCurrentUser();

        try {
            BaseApplication.getPicasso().load(user.getPhotoUrl()).placeholder(R.drawable.ico_user).fit().into(navAvatar);
        }catch (Exception e){
            e.printStackTrace();
        }


        navUserName.setText(user.getFullName());

        mValues = new DrawerOption[]{
                new DrawerOption(getString(R.string.sidemenu_home), R.drawable.ico_sidemenu_home, 0),
                new DrawerOption(getString(R.string.my_bids), R.drawable.ico_sidemenu_loads, 0),
                new DrawerOption(getString(R.string.sidemenu_myads), R.drawable.ico_sidemenu_ad, 0),
                new DrawerOption(getString(R.string.sidemenu_mybalance), R.drawable.ico_dollar, 0),
                new DrawerOption(getString(R.string.sidemenu_inbox), R.drawable.ico_inbox, 0),
                new DrawerOption(getString(R.string.sidemenu_mytruck), R.drawable.ico_sidemenu_truck, 0),
                new DrawerOption(getString(R.string.sidemenu_myrating), R.drawable.ico_rating, 0),
                new DrawerOption(getString(R.string.sidemenu_todo), R.drawable.ico_sidemenu_truck, 0),
                new DrawerOption(getString(R.string.sidemenu_settings), R.drawable.ico_setting, 0),

                new DrawerOption(getString(R.string.sidemenu_logout), R.drawable.ico_logout, 0)
        };
        mAdapter = new DrawerAdapter(this,mValues);
        _DrawerListView.setAdapter(mAdapter);
        _DrawerListView.setItemChecked(0, true);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
    }


    @Override
    public void onBackPressed() {
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();

        if (mHits[0] >= SystemClock.uptimeMillis() - 1000) {
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.putExtra("EXIT", true);
//                startActivity(intent);
            super.onBackPressed();
        }
        else
            Toast.makeText(this, getString(R.string.error_press_again), Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Logger.error("onSaveInstanceState");
        if(homeFragment != null && homeFragment.isVisible())
            getSupportFragmentManager().putFragment(outState, FragmentMenuHome.TAG, homeFragment);
        if(inboxFragment != null && inboxFragment.isVisible())
            getSupportFragmentManager().putFragment(outState, FragmentMenuInBox.TAG, inboxFragment);
        if(myBalanceFragment != null && myBalanceFragment.isVisible())
            getSupportFragmentManager().putFragment(outState, FragmentMenuBalance.TAG, myBalanceFragment);
        if(myBidsFragment != null && myBidsFragment.isVisible())
            getSupportFragmentManager().putFragment(outState, FragmentMenuBids.TAG, myBidsFragment);
        if(myAdsFragment != null && myAdsFragment.isVisible())
            getSupportFragmentManager().putFragment(outState, FragmentMenuAd.TAG, myAdsFragment);
        if(myRatingFragment != null && myRatingFragment.isVisible())
            getSupportFragmentManager().putFragment(outState, FragmentMenuRating.TAG, myRatingFragment);
        if(settingFragment != null && settingFragment.isVisible())
            getSupportFragmentManager().putFragment(outState, FragmentMenuSetting.TAG, settingFragment);
        if(myTruckFragment != null && myTruckFragment.isVisible())
            getSupportFragmentManager().putFragment(outState, FragmentMenuTruck.TAG, myTruckFragment);
        if(fragmentMenuToDo != null && fragmentMenuToDo.isVisible())
            getSupportFragmentManager().putFragment(outState, FragmentMenuToDo.TAG, fragmentMenuToDo);

        super.onSaveInstanceState(outState);
    }

    private void setupFragments(){
        if(fragmentContainer != null) {
            if(_savedInstanceState != null) {
                homeFragment = (FragmentMenuHome) getSupportFragmentManager().getFragment(_savedInstanceState, FragmentMenuHome.TAG);
                inboxFragment = (FragmentMenuInBox) getSupportFragmentManager().getFragment(_savedInstanceState, FragmentMenuInBox.TAG);
                myBalanceFragment = (FragmentMenuBalance) getSupportFragmentManager().getFragment(_savedInstanceState, FragmentMenuBalance.TAG);
                myBidsFragment = (FragmentMenuBids) getSupportFragmentManager().getFragment(_savedInstanceState, FragmentMenuBids.TAG);
                myAdsFragment = (FragmentMenuAd) getSupportFragmentManager().getFragment(_savedInstanceState, FragmentMenuAd.TAG);
                myRatingFragment = (FragmentMenuRating) getSupportFragmentManager().getFragment(_savedInstanceState, FragmentMenuRating.TAG);
                settingFragment = (FragmentMenuSetting) getSupportFragmentManager().getFragment(_savedInstanceState, FragmentMenuSetting.TAG);
                myTruckFragment = (FragmentMenuTruck) getSupportFragmentManager().getFragment(_savedInstanceState, FragmentMenuTruck.TAG);
                fragmentMenuToDo = (FragmentMenuToDo) getSupportFragmentManager().getFragment(_savedInstanceState, FragmentMenuToDo.TAG);
            }

            if(homeFragment == null)
                homeFragment = new FragmentMenuHome();
            if(inboxFragment == null)
                inboxFragment = new FragmentMenuInBox();
            if(myBalanceFragment == null)
                myBalanceFragment = new FragmentMenuBalance();
            if(myBidsFragment == null)
                myBidsFragment = new FragmentMenuBids();
            if(myAdsFragment == null)
                myAdsFragment = new FragmentMenuAd();
            if(myTruckFragment == null)
                myTruckFragment = new FragmentMenuTruck();
            if(myRatingFragment == null)
                myRatingFragment = new FragmentMenuRating();
            if(settingFragment == null)
                settingFragment = new FragmentMenuSetting();
            if(fragmentMenuToDo == null)
                fragmentMenuToDo = new FragmentMenuToDo();

            inflateFragmentWithTag(homeFragment, FragmentMenuHome.TAG);
        }
    }

    public void showSlidingMenu(){
        if (!slidingMenu.isMenuShowing())
            slidingMenu.showMenu(false);
    }

    private void hideSlidingMenu(){
        if(slidingMenu.isMenuShowing())
            slidingMenu.showContent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                if(toggleState) {
                    super.onBackPressed();
                }
                else {
                    showSlidingMenu();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void inflateFragmentWithTag(Fragment fragment,String TAG) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment,TAG);
        transaction.commitAllowingStateLoss();
    }

    public void onNavItemSelected(int position)
    {
        hideSlidingMenu();
        switch(position){
            case 0:
                inflateFragmentWithTag(homeFragment, FragmentMenuHome.TAG);
                break;
            case 1:
                inflateFragmentWithTag(myBidsFragment, FragmentMenuBids.TAG);
                break;
            case 2:
                inflateFragmentWithTag(myAdsFragment, FragmentMenuAd.TAG);
                break;
            case 3:
                inflateFragmentWithTag(myBalanceFragment, FragmentMenuBalance.TAG);
                break;
            case 4:
                inflateFragmentWithTag(inboxFragment, FragmentMenuInBox.TAG);
                break;
            case 5:
                inflateFragmentWithTag(myTruckFragment, FragmentMenuTruck.TAG);
                break;
            case 6:
                inflateFragmentWithTag(myRatingFragment, FragmentMenuRating.TAG);
                break;
            case 7:
                inflateFragmentWithTag(fragmentMenuToDo, FragmentMenuToDo.TAG);
                break;
            case 8:
                inflateFragmentWithTag(settingFragment, FragmentMenuSetting.TAG);
                break;
            case 9:
                alertSignOutDialog();
                break;
        }
    }

    private void alertSignOutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final View viewRateApp = getLayoutInflater().inflate(R.layout.dialog_signout,null);


        final Button btnOk = (Button)viewRateApp.findViewById(R.id.btn_ok);
        final Button btnCancel = (Button)viewRateApp.findViewById(R.id.btn_cancel);

        builder.setView(viewRateApp);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void signOut() {
        User user = new User();
        UserManager.with(this).setUserType(0);
        UserManager.with(this).setCurrentUser(user);
        finish();
        Intent intent = new Intent(this, ActivitySplash.class);
        startActivity(intent);

        NotificationManager mNotificationManager =
                (NotificationManager) BaseApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();

        ShortcutBadger.removeCount(BaseApplication.getContext());
    }

    @Override
    public void onResume(){
        super.onResume();

        fetchGetToDo();
        Toolbar toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.toolbar);
        ImageView btnNotification = (ImageView)toolbar.findViewById(R.id.btn_notification);
        ImageView btnSearchFilter = (ImageView) toolbar.findViewById(R.id.btn_searchfilter);
        ImageView btnAddAd = (ImageView) toolbar.findViewById(R.id.btn_add_ad);

        btnNotification.setOnClickListener(this);
        btnSearchFilter.setOnClickListener(this);
        btnAddAd.setOnClickListener(this);


        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                getNotifications();

                BaseApplication._application.getLocation();

                BaseApplication.getHandler().postAtTime(this, System.currentTimeMillis() + Constant.LOCATION_TRACK_INTERVAL);
                BaseApplication.getHandler().postDelayed(this, Constant.LOCATION_TRACK_INTERVAL);

            }
        });
    }


    @Override
    public void onClick(View v) {
        int id =v.getId();
        Intent intent;
        switch (id) {
            case R.id.btn_notification:
                intent = new Intent(BaseApplication.getContext(), ActivityNotification.class);
                startActivity(intent);
                break;
            case R.id.btn_searchfilter:
                intent = new Intent(BaseApplication.getContext(), ActivitySearchFilter.class);
                startActivity(intent);
                break;
            case R.id.btn_add_ad:
                FragmentPostAd fragmentPostLoad = new FragmentPostAd();
                inflateFragmentWithTag(fragmentPostLoad, FragmentPostAd.TAG);
        }
    }


    public static void applyAddAd(){
        if(getInstance() !=null) {
            getInstance().onNavItemSelected(2);
        }
    }

    public static void applyAddOffer(){
        if(getInstance() !=null) {
            getInstance().onNavItemSelected(1);
        }
    }

    private void requestReadStoragePermission()
    {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            1);

                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            1);
                }
            }
        }

    }

    private void requestCameraPermission() {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CAMERA)) {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},
                            1);

                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},
                            1);
                }
            }
        }

    }

    private void requestLocationPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    11); // Last parameter is requestCode
            return;
        }

    }


    public  boolean requestCallPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }


    ProgressDialog bar;
    class DownloadNewVersion extends AsyncTask<String,Integer,Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            bar = new ProgressDialog(MainActivity.this);
            bar.setCancelable(false);

            bar.setMessage("Downloading...");

            bar.setIndeterminate(true);
            bar.setCanceledOnTouchOutside(false);
            bar.show();

        }

        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);

            bar.setIndeterminate(false);
            bar.setMax(100);
            bar.setProgress(progress[0]);
            String msg = "";
            if(progress[0]>99){

                msg="Finishing... ";

            }else {

                msg="Downloading... "+progress[0]+"%";
            }
            bar.setMessage(msg);

        }
        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            bar.dismiss();

            if(result){

                Toast.makeText(getApplicationContext(),"Update Done",
                        Toast.LENGTH_SHORT).show();

            }else{

                Toast.makeText(getApplicationContext(),"Error: Try Again",
                        Toast.LENGTH_SHORT).show();

            }

        }

        @Override
        protected Boolean doInBackground(String... arg0) {
            Boolean flag = false;

            try {


                URL url = new URL(arg0[0]);

                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setDoOutput(true);
                c.connect();


                String PATH = Environment.getExternalStorageDirectory()+"/Download/";
                File file = new File(PATH);
                file.mkdirs();

                File outputFile = new File(file,"app-debug.apk");

                if(outputFile.exists()){
                    outputFile.delete();
                }

                FileOutputStream fos = new FileOutputStream(outputFile);
                InputStream is = c.getInputStream();

                int total_size = 1431692;//size of apk

                byte[] buffer = new byte[1024];
                int len1 = 0;
                int per = 0;
                int downloaded=0;
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);
                    downloaded +=len1;
                    per = (int) (downloaded * 100 / total_size);
                    publishProgress(per);
                }
                fos.close();
                is.close();

                OpenNewVersion(PATH);

                flag = true;
            } catch (Exception e) {

                flag = false;
            }
            return flag;

        }
    }


    void OpenNewVersion(String location) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(location + "app-debug.apk")),
                "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void checkTravels(){
        HttpUtil httpUtil = HttpUtil.getInstance();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, UserManager.with(context).getCurrentUser().getToken());
        httpUtil.get(Constant.GET_ACTIVE_SESSION_API, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int j, Header[] headers, byte[] responseBody) {
                try {
                    String paramString = new String(responseBody);
                    paramString = StringUtil.escapeString(paramString);
                    JSONObject jsonObject= new JSONObject(paramString);
                    GetToDo getToDo = BaseApplication.getGson().fromJson(jsonObject.toString(), GetToDo.class);

                    int trackingStatus = Integer.valueOf(getToDo.getSessionStatus());
                    Intent intent;
                    if(trackingStatus <3) {
                        startLocationUpdateService(getToDo.getID(), 1 );
                        intent = new Intent(context, ActivityPickup.class);
                        intent.putExtra(Constant.INTENT_TRAVEL_ID, getToDo.getID());
                        intent.putExtra(Constant.INTENT_TRACKING_STATUS, trackingStatus);
                        context.startActivity(intent);
                        return;
                    } else if(trackingStatus < 6){
                        startLocationUpdateService(getToDo.getID(), 2 );
                        intent = new Intent(context, ActivityTravel.class);
                        intent.putExtra(Constant.INTENT_TRAVEL_ID, getToDo.getID());
                        intent.putExtra(Constant.INTENT_TRACKING_STATUS, trackingStatus);
                        context.startActivity(intent);
                        return;
                    }

                    int Status = Integer.valueOf(getToDo.getTravelStatus());
                    Logger.error("Travel Status = " + Status);

                    if(Status  == 2){
                        intent = new Intent(context, ActivityCollectPayment.class);
                        intent.putExtra(Constant.INTENT_TRAVEL_ID, getToDo.getID());
                        context.startActivity(intent);

                        return;
                    }
                    else if(Status == 3){
                        intent = new Intent(context, ActivityRating.class);
                        intent.putExtra(Constant.INTENT_TRAVEL_ID, getToDo.getID());
                        context.startActivity(intent);
                        return;
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int j, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }


    private void startLocationUpdateService(String travelId, int TravelType) {
        try {
            Intent intent = new Intent(BaseApplication.getContext(), LocationUpdateService.class);
            LocationUpdateService.setTravelID(travelId);
            LocationUpdateService.setServiceType(TravelType);
            Logger.error("TravelID =  " + LocationUpdateService.getTravelID());
            PendingIntent pIntent = PendingIntent.getService(this, Constant.TRACKING_REQUEST_CODE, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());
            AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarm.cancel(pIntent);

            LocationUpdateService.isActive = true;
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), Constant.TRACKING_INTERVAL, pIntent);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    private void getNotifications() {
        HttpUtil httpUtil = new HttpUtil();
        User user  = UserManager.with(this).getCurrentUser();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());
        httpUtil.get(Constant.GET_NOTIFICATIONS_UNSEEN_API, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String paramString = new String(responseBody);
                paramString = StringUtil.escapeString(paramString);

                try {
                    JSONArray jsonArray = new JSONArray(paramString);
                    Toolbar toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.toolbar);
                    CustomTextView txtNotificationCount = (CustomTextView) toolbar.findViewById(R.id.txt_notification_count);
                    txtNotificationCount.setText(String.valueOf(jsonArray.length()));

                    AppSettings.with(BaseApplication.getContext()).setNotificationCount(jsonArray.length());
                    ShortcutBadger.applyCount(BaseApplication.getContext(),jsonArray.length());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }

        });
    }
}

