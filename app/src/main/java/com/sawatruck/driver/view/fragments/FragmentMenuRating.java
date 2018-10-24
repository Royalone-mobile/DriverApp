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


public class FragmentMenuRating extends BaseFragment {
    public static final String TAG = FragmentMenuRating.class.getSimpleName();

    FragmentRatingReceived tab1;
    FragmentRatingGiven tab2;

    @Bind(R.id.pager) ViewPager pager;
    @Bind(R.id.tabs) PagerSlidingTabStrip tabs;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sliding_tab, container, false);
        ButterKnife.bind(this,view);

        BaseActivity baseActivity = (BaseActivity)getActivity();
        baseActivity.setAppTitle(getResources().getString(R.string.sidemenu_myrating));
        baseActivity.showOptions(false);

        RatingPagerAdapter ratingPagerAdapter = new RatingPagerAdapter(getChildFragmentManager());
        pager.setAdapter(ratingPagerAdapter);
        tabs.setViewPager(pager);
        pager.setCurrentItem(0);

        try
        {
            Typeface myFont = Typeface.createFromAsset(getContext().getAssets(), "font/MyriadPro-Regular.otf");
            tabs.setTypeface(myFont,0);
        }
        catch (Exception e)
        {
        }

        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
                setTabTextColor(position);
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

        setTabTextColor(0);
        return view;
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

    public void setCurrentTab(int tabPosition){
        pager.setCurrentItem(tabPosition);
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }


    public class RatingPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {getResources().getString(R.string.tab_my_ratings),
                getResources().getString(R.string.tab_ratings)};

        RatingPagerAdapter(FragmentManager fm) {
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
                    tab1 = FragmentRatingReceived.getInstance(position);
                }
                tabFragment = tab1;
            } else if (position == 1) {
                if (tab2 == null) {
                    tab2 = FragmentRatingGiven.getInstance(position);
                }
                tabFragment = tab2;
            }
            return tabFragment;
        }
    }
}
