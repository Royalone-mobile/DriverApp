package com.sawatruck.driver.view.fragments;

import android.graphics.Typeface;
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
import com.sawatruck.driver.R;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.view.activity.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by royal on 8/19/2017.
 */


public class FragmentMenuBids extends BaseFragment {
    public static final String TAG = FragmentMenuBids.class.getSimpleName();

    FragmentBidBooked tab1;
    FragmentBidAccepted tab2;
    FragmentBidActive tab3;
    public static int Initial_TAB = 0;
    @Bind(R.id.pager) ViewPager pager;
    @Bind(R.id.tabs) PagerSlidingTabStrip tabs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sliding_tab, container, false);
        ButterKnife.bind(this,view);


        BaseActivity baseActivity = (BaseActivity)getActivity();
        baseActivity.setAppTitle(getResources().getString(R.string.sidemenu_mybids));
        baseActivity.showOptions(false);

        BidPagerAdapter bidPagerAdapter = new BidPagerAdapter(getChildFragmentManager());
        pager.setAdapter(bidPagerAdapter);
        tabs.setViewPager(pager);
        tabs.setOnPageChangeListener(viewPageListener);
        pager.setCurrentItem(Initial_TAB);

        try
        {
            Typeface myFont = Typeface.createFromAsset(getContext().getAssets(), "font/MyriadPro-Regular.otf");
            tabs.setTypeface(myFont,0);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        setTabTextColor(Initial_TAB);


        Initial_TAB = 0;
        return view;
    }

    private ViewPager.OnPageChangeListener viewPageListener = new ViewPager.OnPageChangeListener(){

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            setTabTextColor(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public void setCurrentTab(int tabPosition){
        pager.setCurrentItem(tabPosition);
    }

    public void setTabTextColor(int tabPosition) {

        // Set first tab selected
        LinearLayout mTabsLinearLayout = ((LinearLayout) tabs.getChildAt(0));
        for (int i = 0; i < mTabsLinearLayout.getChildCount(); i++) {
            TextView tv = (TextView) mTabsLinearLayout.getChildAt(i);

            if(i == tabPosition){
                tv.setTextColor(Misc.getColorFromResource(R.color.colorDarkOrange));
            } else {
                tv.setTextColor(Misc.getColorFromResource(R.color.colorLightGray));
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }


    public class BidPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {getResources().getString(R.string.tab_booked),
                getResources().getString(R.string.tab_accepted), getResources().getString(R.string.tab_opened)};

        BidPagerAdapter(FragmentManager fm) {
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
                    tab1 = FragmentBidBooked.getInstance(position);
                }
                tabFragment = tab1;
            } else if (position == 1) {
                if (tab2 == null) {
                    tab2 = FragmentBidAccepted.getInstance(position);
                }
                tabFragment = tab2;
            } else if (position ==2){
                if (tab3 == null) {
                    tab3 = FragmentBidActive.getInstance(position);
                }
                tabFragment = tab3;
            }
            return tabFragment;
        }
    }
}
