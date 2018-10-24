package com.sawatruck.driver.view.activity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.LoadType;
import com.sawatruck.driver.entities.TruckClass;
import com.sawatruck.driver.entities.TruckType;
import com.sawatruck.driver.entities.User;
import com.sawatruck.driver.utils.AppSettings;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.StringUtil;
import com.sawatruck.driver.utils.UserManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by royal on 8/16/2017.
 */

public class ActivitySearchFilter extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.spin_loadType) Spinner spinnerLoadType;
    @Bind(R.id.spin_truckClass) Spinner spinnerTruckClass;
    @Bind(R.id.spin_truckType) Spinner spinnerTruckType;
    @Bind(R.id.btn_applyfilter) Button btnApplyFilter;

    ArrayList<TruckClass> truckClasses = new ArrayList<>();
    ArrayList<LoadType> loadTypes = new ArrayList<>();
    ArrayList<TruckType> truckTypes = new ArrayList<>();

    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_searchfilter,null);
        ButterKnife.bind(this, view);
        btnApplyFilter.setOnClickListener(this);



        initView();
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        setAppTitle(getString(R.string.title_search_filter));
        showNavHome(false);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_applyfilter:
                applySearch();
                break;
        }
    }

    public void applySearch(){
        try{
            if(truckClasses.size()>spinnerTruckClass.getSelectedItemPosition()){
                TruckClass truckClass =  truckClasses.get(spinnerTruckClass.getSelectedItemPosition());
                AppSettings.with(context).setTruckClass(String.valueOf(truckClass.getClassID()));
            }


            if(loadTypes.size()>spinnerLoadType.getSelectedItemPosition()){
                LoadType loadType =  loadTypes.get(spinnerLoadType.getSelectedItemPosition());
                AppSettings.with(context).setLoadType(String.valueOf(loadType.getLoadTypeID()));
            }

            if(truckTypes.size()>spinnerTruckType.getSelectedItemPosition()){
                TruckType truckType =  truckTypes.get(spinnerTruckType.getSelectedItemPosition());
                AppSettings.with(context).setTruckType(String.valueOf(truckType.getID()));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finish();
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

    public void initView(){
        getAllTruckClasses();
        getAllTruckTypes();
        getAllLoadTypes();
    }


    public void getAllLoadTypes(){
        HttpUtil httpUtil = new HttpUtil();
        User user  = UserManager.with(context).getCurrentUser();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());

        httpUtil.get(Constant.GET_LOAD_TYPES_API, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String paramString = new String(responseBody);
                paramString = StringUtil.escapeString(paramString);

                loadTypes = new ArrayList<>();
                ArrayList<String> loadTypesList = new ArrayList<>();


                int firstSelection = 0;

                try {
                    JSONArray jsonArray = new JSONArray(paramString);
                    for(int j=0; j<jsonArray.length(); j++) {
                        LoadType loadType = BaseApplication.getGson().fromJson(jsonArray.get(j).toString(), LoadType.class);
                        loadTypes.add(loadType);
                        loadTypesList.add(loadType.getName());

                        if( String.valueOf(loadType.getLoadTypeID()).equals(AppSettings.with(context).getLoadType()))
                            firstSelection  =  j;
                    }

                    ArrayAdapter adapter = new ArrayAdapter(
                            context,
                            R.layout.spinner_item,
                            loadTypesList);
                    adapter.setDropDownViewResource(R.layout.spinner_select);
                    spinnerLoadType.setAdapter(adapter);
                    if(loadTypes.size()>0)
                        spinnerLoadType.setSelection(firstSelection);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });

    }


    public void getAllTruckTypes(){
        HttpUtil httpUtil = new HttpUtil();
        User user  = UserManager.with(context).getCurrentUser();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());
        httpUtil.get(Constant.GET_TRUCK_TYPE_API, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String paramString = new String(responseBody);
                paramString = StringUtil.escapeString(paramString);

                truckTypes = new ArrayList<>();
                ArrayList<String> truckTypesList = new ArrayList<>();

                int firstSelection = 0;
                try {
                    JSONArray jsonArray = new JSONArray(paramString);
                    for(int j=0; j<jsonArray.length(); j++) {
                        TruckType truckType = BaseApplication.getGson().fromJson(jsonArray.get(j).toString(), TruckType.class);
                        truckTypes.add(truckType);
                        truckTypesList.add(truckType.getName());

                        if( String.valueOf(truckType.getID()).equals(AppSettings.with(context).getTruckType()))
                            firstSelection  =  j;

                    }

                    ArrayAdapter adapter = new ArrayAdapter(
                            context,
                            R.layout.spinner_item,
                            truckTypesList);
                    adapter.setDropDownViewResource(R.layout.spinner_select);
                    spinnerTruckType.setAdapter(adapter);
                    if(truckTypes.size()>0)
                        spinnerTruckType.setSelection(firstSelection);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }


    private void getAllTruckClasses(){
        HttpUtil httpUtil = new HttpUtil();
        User user  = UserManager.with(context).getCurrentUser();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());
        httpUtil.get(Constant.GET_TRUCK_CLASSES_API, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String paramString = new String(responseBody);
                paramString = StringUtil.escapeString(paramString);

                truckClasses = new ArrayList<>();
                ArrayList<String> classNameList = new ArrayList<>();
                int firstSelection = 0;
                try {
                    JSONArray jsonArray = new JSONArray(paramString);
                    for(int j=0; j<jsonArray.length(); j++) {
                        TruckClass truckClass = BaseApplication.getGson().fromJson(jsonArray.get(j).toString(), TruckClass.class);
                        truckClasses.add(truckClass);
                        classNameList.add(truckClass.getName());

                        if( String.valueOf(truckClass.getClassID()).equals(AppSettings.with(context).getTruckClass()))
                            firstSelection  =  j;

                    }

                    ArrayAdapter adapter = new ArrayAdapter(
                            context,
                            R.layout.spinner_item,
                            classNameList);
                    adapter.setDropDownViewResource(R.layout.spinner_select);
                    spinnerTruckClass.setAdapter(adapter);
                    if(classNameList.size()>0)
                        spinnerTruckClass.setSelection(firstSelection);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }
}
