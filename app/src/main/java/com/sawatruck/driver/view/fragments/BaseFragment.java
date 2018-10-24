package com.sawatruck.driver.view.fragments;

/**
 * Created by royal on 8/19/2017.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import com.sawatruck.driver.BaseApplication;


public class BaseFragment extends Fragment {

    protected Activity mMainActivity;
    private ProgressBar _progressBar;
    protected LayoutInflater mLayoutInflater;

    public void showProgressDialog() {
        if (_progressBar != null) {
            return;
        }
    }

    public void dismissProgressDialog() {

        if (_progressBar != null) {
            try {
                _progressBar.setVisibility(View.GONE);
            } catch (IllegalArgumentException e) {
            }
            _progressBar = null;
        }
    }

    @SuppressWarnings("unused")
    public boolean backAllowed() {
        return true;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mMainActivity =  activity;
        mLayoutInflater = LayoutInflater.from(mMainActivity);
    }

    @SuppressWarnings("unused")
    public void hideKeyboard() {
        View view = mMainActivity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) mMainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @SuppressWarnings("unused")
    public void showInfoDialog(String title, String description) {
        showInfoDialog(title, description, null);
    }

    public void showInfoDialog(String title, String description, DialogInterface.OnClickListener okListener) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(mMainActivity);
        builderSingle.setTitle(title);
        builderSingle.setMessage(description);

        if (okListener == null) {
            okListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            };
        } else {
            builderSingle.setCancelable(false);
        }

        builderSingle.setPositiveButton("OK", okListener);
        builderSingle.show();
    }

    @SuppressWarnings("unused")
    protected boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) BaseApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
