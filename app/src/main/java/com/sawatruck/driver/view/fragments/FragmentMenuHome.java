package com.sawatruck.driver.view.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.Load;
import com.sawatruck.driver.utils.Logger;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.utils.UserManager;
import com.sawatruck.driver.view.activity.ActivitySearchLoad;
import com.sawatruck.driver.view.activity.BaseActivity;
import com.sawatruck.driver.view.activity.MainActivity;
import com.sawatruck.driver.view.design.CustomTextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by royal on 8/20/2017.
 */


public class FragmentMenuHome extends BaseFragment implements View.OnClickListener {
    public static final String TAG = FragmentMenuHome.class.getSimpleName();
    @Bind(R.id.btn_post_adds) View btnPostAdds;
    @Bind(R.id.pager) ViewPager pager;
    @Bind(R.id.tabs) PagerSlidingTabStrip tabs;
    @Bind(R.id.btn_search_loads) CustomTextView searchView;

    FragmentHomeMapView tab1;
    FragmentHomeListView tab2;
    static FragmentMenuHome _instance;
    public View getPostAddButton(){
        return btnPostAdds;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this,view);
        btnPostAdds.setOnClickListener(this);
        searchView.setOnClickListener(this);


        _instance = this;
        BaseActivity baseActivity = (BaseActivity)getActivity();
        baseActivity.setAppTitle(getResources().getString(R.string.app_name));
        baseActivity.showOptions(true);

        Drawable drawable = getContext().getResources().getDrawable(R.drawable.ico_search);
        drawable.setBounds(0,0,30,30);
        searchView.setCompoundDrawables(null,null,drawable,null);

        HomePagerAdapter ratingPagerAdapter = new HomePagerAdapter(getChildFragmentManager());
        pager.setAdapter(ratingPagerAdapter);
        tabs.setViewPager(pager);
        pager.setCurrentItem(0);


//        if(getActivity().getIntent().getBooleanExtra("post_ad_from_splash", false)) {
//            MainActivity mainActivity = (MainActivity) getActivity();
//            FragmentPostAd fragmentPostLoad = new FragmentPostAd();
//            mainActivity.inflateFragmentWithTag(fragmentPostLoad, FragmentPostAd.TAG);
//        }
//        if(getActivity().getIntent().getBooleanExtra("search_load_from_splash", false)) {
//            Intent intent = new Intent(getActivity(), ActivitySearchLoad.class);
//            startActivity(intent);
//        }

        try
        {
            Typeface myFont = Typeface.createFromAsset(getContext().getAssets(), "font/MyriadPro-Regular.otf");
            tabs.setTypeface(myFont,0);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                LinearLayout mTabsLinearLayout = ((LinearLayout) tabs.getChildAt(0));
                for (int i = 0; i < mTabsLinearLayout.getChildCount(); i++) {
                    TextView tv = (TextView) mTabsLinearLayout.getChildAt(i);
                    if(i == position){
                        tv.setTextColor(Misc.getColorFromResource(R.color.colorDarkOrange));
                    } else {
                        tv.setTextColor(Misc.getColorFromResource(R.color.colorLightGray));
                    }
                }

            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });

        setUpTabStrip();
        return view;
    }

    public void setUpTabStrip() {
        LinearLayout mTabsLinearLayout = ((LinearLayout) tabs.getChildAt(0));
        for (int i = 0; i < mTabsLinearLayout.getChildCount(); i++) {
            TextView tv = (TextView) mTabsLinearLayout.getChildAt(i);

            if(i == 0){
                tv.setTextColor(Misc.getColorFromResource(R.color.colorDarkOrange));
            } else {
                tv.setTextColor(Misc.getColorFromResource(R.color.colorLightGray));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        int id =v.getId();
        Intent intent;
        switch (id) {
            case R.id.btn_post_adds:
                int userType = UserManager.with(BaseApplication.getContext()).getUserType();
                if(userType == 0) {
                    MainActivity mainActivity = (MainActivity)getActivity();
                    mainActivity.showSlidingMenu();
                }
                else {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    FragmentPostAd fragmentPostLoad = new FragmentPostAd();
                    mainActivity.inflateFragmentWithTag(fragmentPostLoad, FragmentPostAd.TAG);
                }
                break;
            case R.id.btn_search:
                intent = new Intent(BaseApplication.getContext(), ActivitySearchLoad.class);
                startActivity(intent);
                break;
            case R.id.btn_search_loads:
                intent = new Intent(BaseApplication.getContext(), ActivitySearchLoad.class);
                startActivity(intent);
                break;
        }
    }


    public class HomePagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {getResources().getString(R.string.tab_mapview), getResources().getString(R.string.tab_listview)};

        HomePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment tabFragment = null;
            if (position == 0) {
                if (tab1 == null) {
                    tab1 = FragmentHomeMapView.getInstance(position);
                }
                tabFragment = tab1;
            } else if (position == 1) {
                if (tab2 == null) {
                    tab2 = FragmentHomeListView.getInstance(position);
                }
                tabFragment = tab2;
            }
            return tabFragment;
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        _instance = null;
    }
}


