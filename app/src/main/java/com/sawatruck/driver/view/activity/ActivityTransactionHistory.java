package com.sawatruck.driver.view.activity;

import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.sawatruck.driver.R;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.view.fragments.FragmentAllTransaction;
import com.sawatruck.driver.view.fragments.FragmentInTransaction;
import com.sawatruck.driver.view.fragments.FragmentOutTransaction;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ActivityTransactionHistory extends BaseActivity {
    private FragmentAllTransaction tab1;
    private FragmentInTransaction tab2;
    private FragmentOutTransaction tab3;

    @Bind(R.id.pager) ViewPager pager;
    @Bind(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_transaction_history,null);
        ButterKnife.bind(this, view);

        TransactionPagerAdapter transactionPagerAdapter = new TransactionPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(transactionPagerAdapter);
        tabs.setViewPager(pager);
        pager.setCurrentItem(0);

        try {
            Typeface myFont = Typeface.createFromAsset(getAssets(), "font/MyriadPro-Regular.otf");
            tabs.setTypeface(myFont,0);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
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

    @Override
    public void onResume(){
        super.onResume();
        setAppTitle(getString(R.string.title_transaction_history));
        showNavHome(false);
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



    private void setUpTabStrip() {
        LinearLayout mTabsLinearLayout = ((LinearLayout) tabs.getChildAt(0));
        for (int i = 0; i < mTabsLinearLayout.getChildCount(); i++) {
            TextView tv = (TextView) mTabsLinearLayout.getChildAt(i);
            tv.setHorizontallyScrolling(false);
            if(i == 0){
                tv.setTextColor(Misc.getColorFromResource(R.color.colorDarkOrange));
            } else {
                tv.setTextColor(Misc.getColorFromResource(R.color.colorLightGray));
            }
        }
    }

    public class TransactionPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {getResources().getString(R.string.tab_all),
                getResources().getString(R.string.tab_money_in), getResources().getString(R.string.tab_money_out)};

        TransactionPagerAdapter(FragmentManager fm) {
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
                    tab1 = FragmentAllTransaction.getInstance(position);
                }
                tabFragment = tab1;
            } else if (position == 1) {
                if (tab2 == null) {
                    tab2 = FragmentInTransaction.getInstance(position);
                }
                tabFragment = tab2;
            }

            else if (position == 2) {
                if (tab3 == null) {
                    tab3 = FragmentOutTransaction.getInstance(position);
                }
                tabFragment = tab3;
            }
            return tabFragment;
        }
    }
}
