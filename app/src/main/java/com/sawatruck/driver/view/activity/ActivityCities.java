package com.sawatruck.driver.view.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sawatruck.driver.BaseApplication;
import com.sawatruck.driver.Constant;
import com.sawatruck.driver.R;
import com.sawatruck.driver.entities.AddressDetail;
import com.sawatruck.driver.entities.Cities;
import com.sawatruck.driver.entities.City;
import com.sawatruck.driver.entities.Country;
import com.sawatruck.driver.repository.DBController;
import com.sawatruck.driver.utils.AppSettings;
import com.sawatruck.driver.utils.HttpUtil;
import com.sawatruck.driver.utils.Misc;
import com.sawatruck.driver.utils.Serializer;
import com.sawatruck.driver.utils.StringUtil;
import com.sawatruck.driver.view.adapter.LocationAdapter;
import com.sawatruck.driver.view.adapter.SelectLocationSection;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class ActivityCities extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.rv_locations) RecyclerView rvContainer;
    @Bind(R.id.spinner_city) BetterSpinner spinnerCity;
    @Bind(R.id.searchview) SearchView searchView;
    @Bind(R.id.btn_select_from_map) View btnSelectFromMap;

    private LocationAdapter locationAdapter;
    private LatLng currentLatLng;
    private String currentCityName = "Dubai";
    private static ActivityCities _instance;

    private SectionedRecyclerViewAdapter sectionAdapter;
    SelectLocationSection savedLocationSection, recentLocationSection, nearbyLocationSection, searchResultSection;

    private boolean bShowCity = true;

    @Override
    protected View getContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_cities, null);
        ButterKnife.bind(this, view);


        currentLatLng = new LatLng(AppSettings.with(this).getCurrentLat(), AppSettings.with(this).getCurrentLng());
        currentCityName = AppSettings.with(this).getCurrentCity();


        _instance = this;
        locationAdapter = new LocationAdapter(this);
        sectionAdapter = new SectionedRecyclerViewAdapter();

        savedLocationSection = new SelectLocationSection(this, "Saved Locations");
        recentLocationSection = new SelectLocationSection(this, "Recent Locations");
        nearbyLocationSection = new SelectLocationSection(this, "Nearby Locations");
        searchResultSection = new SelectLocationSection(this, "Search Result");

        DBController dbController = DBController.getInstance().open(this);
        recentLocationSection.setList(dbController.getRecentLocations());
        savedLocationSection.setList(dbController.getSavedLocations());
        dbController.closeDB();

        nearbyLocationSection.setOnAddLocationsListener(new SelectLocationSection.OnAddLocationsListener() {
            @Override
            public void OnAddLocations() {
                sectionAdapter.notifyDataSetChanged();
            }
        });

        searchResultSection.setOnAddLocationsListener(new SelectLocationSection.OnAddLocationsListener() {
            @Override
            public void OnAddLocations() {
                sectionAdapter.notifyDataSetChanged();
            }
        });

        sectionAdapter.addSection(savedLocationSection);
        sectionAdapter.addSection(recentLocationSection);
        sectionAdapter.addSection(nearbyLocationSection);

        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(queryTextListener);

        toggleShowCityLocation();
        initializeSpinner();

        btnSelectFromMap.setOnClickListener(this);
        getAllCountries();
        return view;
    }

    public static void notifyDataChanged() {
        try {
            DBController dbController = DBController.getInstance().open(BaseApplication.getContext());
            _instance.recentLocationSection.setList(dbController.getRecentLocations());
            _instance.savedLocationSection.setList(dbController.getSavedLocations());
            dbController.closeDB();
            if (_instance != null)
                _instance.sectionAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void toggleShowCityLocation() {
        bShowCity = !bShowCity;
        currentCityName = AppSettings.with(context).getCurrentCity();
        spinnerCity.setHint(currentCityName);
        //TODO city => false, details=>true
        if (!bShowCity) {
            searchView.setQueryHint(getString(R.string.search_for_city));
            rvContainer.setAdapter(locationAdapter);
            rvContainer.setLayoutManager(new StickyHeaderLayoutManager());
            btnSelectFromMap.setVisibility(View.GONE);
        } else {
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
            rvContainer.setVerticalScrollBarEnabled(true);
            rvContainer.setLayoutManager(mLayoutManager);
            rvContainer.setAdapter(sectionAdapter);
            searchView.setQueryHint(getString(R.string.search_for_location));
            btnSelectFromMap.setVisibility(View.VISIBLE);

            nearbyLocationSection.initializeAdapter();
            sectionAdapter.notifyDataSetChanged();
            getLocationsFromCity();
        }
    }

    public void searchCity(String keyword) {
        HttpUtil.getInstance().get(Constant.SEARCH_CITY_API + "/" + keyword, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] responseBody) {
                String paramString = new String(responseBody);
                paramString = StringUtil.escapeString(paramString);

                ArrayList<City> cities = new ArrayList<>();

                try {
                    JSONArray jsonArray = new JSONArray(paramString);
                    for (int j = 0; j < jsonArray.length(); j++) {
                        City city = BaseApplication.getGson().fromJson(jsonArray.get(j).toString(), City.class);
                        cities.add(city);
                    }
                    ArrayList<Cities> citiesList = getPackets(cities);
                    locationAdapter.initialize();
                    for (Cities item : citiesList) {
                        LocationAdapter.Section section = new LocationAdapter.Section();
                        section.setCities(item);
                        section.setIndex(locationAdapter.getSections().size());
                        Country country = new Country();
                        country.setCountryName(item.get(0).getCountryName());
                        section.setCountry(country);
                        locationAdapter.appendSection(section);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }

    public ArrayList<Cities> getPackets(ArrayList<City> strCities) {
        ArrayList<Cities> list = new ArrayList<>();
        HashMap<String, Integer> hashMap = new HashMap<>();
        int counter = 0;
        for (City city : strCities) {
            if (hashMap.get(city.getCountryName()) == null) {
                hashMap.put(city.getCountryName(), counter);
                Cities cities = new Cities();
                cities.add(city);
                list.add(cities);
                counter++;
            } else {
                int index = hashMap.get(city.getCountryName());
                Cities cities = list.get(index);
                cities.add(city);
            }
        }
        return list;
    }

    public void searchLocation(String keyword) {
        searchResultSection.initializeAdapter();
        sectionAdapter.notifyDataSetChanged();
        searchResultSection.executeKeyword(currentLatLng.latitude, currentLatLng.longitude,keyword);
    }

    private SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        public boolean onQueryTextChange(String newText) {
            // this is your adapter that will be filtered
            if (!bShowCity) {
                searchCity(newText);
            } else {
                searchResultSection.cancelQuery();
                searchLocation(newText);

                try {
                    if (newText.length() != 0) {
                        sectionAdapter.removeAllSections();
                        sectionAdapter.addSection(searchResultSection);
                        rvContainer.setAdapter(sectionAdapter);
                        btnSelectFromMap.setVisibility(View.GONE);
                    }
                    else {
                        sectionAdapter.removeAllSections();
                        sectionAdapter.addSection(savedLocationSection);
                        sectionAdapter.addSection(recentLocationSection);
                        sectionAdapter.addSection(nearbyLocationSection);
                        rvContainer.setAdapter(sectionAdapter);
                        searchView.setQueryHint(getString(R.string.search_for_location));
                        btnSelectFromMap.setVisibility(View.VISIBLE);

                        nearbyLocationSection.initializeAdapter();
                        sectionAdapter.notifyDataSetChanged();
                        getLocationsFromCity();

                        btnSelectFromMap.setVisibility(View.VISIBLE);
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

        public boolean onQueryTextSubmit(String query) {
            //Here u can get the value "query" which is entered in the search box.
            return true;
        }
    };


    public void initializeSpinner() {
        ArrayList emptyList = new ArrayList<>();
        ArrayAdapter adapter = new ArrayAdapter(
                context,
                R.layout.spinner_item,
                emptyList);
        adapter.setDropDownViewResource(R.layout.spinner_select);
        spinnerCity.setAdapter(adapter);
        spinnerCity.setTag(bShowCity);

        spinnerCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleShowCityLocation();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_select_from_map:
                selectLocationFromMap();
                break;
        }
    }

    private void selectLocationFromMap() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), Constant.LOCATION_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    public void getAllCountries() {
        HttpUtil httpUtil = HttpUtil.getInstance();
        httpUtil.get(Constant.GET_ALL_COUNTRIES_API, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] responseBody) {
                String paramString = new String(responseBody);
                paramString = StringUtil.escapeString(paramString);

                ArrayList<Country> countries = new ArrayList<>();

                try {
                    JSONArray jsonArray = new JSONArray(paramString);
                    for (int j = 0; j < jsonArray.length(); j++) {
                        Country country = BaseApplication.getGson().fromJson(jsonArray.get(j).toString(), Country.class);
                        countries.add(country);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (Country country : countries) {
                    getAllCities(country);
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }

    public void getAllCities(final Country country) {
        HttpUtil httpUtil = HttpUtil.getInstance();
        RequestParams params = new RequestParams();
        params.put("countryDialCode", StringUtil.escapeString(country.getCountryDialCode()));
        httpUtil.get(Constant.GET_CITIES_BY_COUNTRY_API, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] responseBody) {
                String paramString = new String(responseBody);
                paramString = StringUtil.escapeString(paramString);
                ArrayList<City> cities = new ArrayList<>();
                try {
                    JSONArray jsonArray = new JSONArray(paramString);
                    for (int j = 0; j < jsonArray.length(); j++) {
                        City city = BaseApplication.getGson().fromJson(jsonArray.get(j).toString(), City.class);
                        cities.add(city);
                    }

                    LocationAdapter.Section section = new LocationAdapter.Section();
                    section.setCities(cities);
                    section.setCountry(country);

                    section.setIndex(locationAdapter.getSections().size());

                    locationAdapter.appendSection(section);
                    locationAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }

    public void setCurrentCityName(String currentCityName) {
        this.currentCityName = currentCityName;
    }

    public void finishWithParsing(Double latitude, Double longitude) {
        LocationAsyncTask locationAsyncTask = new LocationAsyncTask();
        Location location = new Location("location");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        locationAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,location);
    }

    public LatLng getCurrentLatLng() {
        return currentLatLng;
    }

    public void setCurrentLatLng(LatLng currentLatLng) {
        this.currentLatLng = currentLatLng;
    }

    private class LocationAsyncTask extends AsyncTask<Location, Void, Void> {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(Location... params) {
            Location location = params[0];

            final AddressDetail addressDetail = Misc.getAddressDetail(location.getLatitude(), location.getLongitude());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finishWithResult(addressDetail);
                }
            });
            return null;
        }
    }

    public void getLocationsFromCity() {
        nearbyLocationSection.execute(currentLatLng.latitude, currentLatLng.longitude);
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

    @Override
    public void onResume() {
        super.onResume();

        setAppTitle(getResources().getString(R.string.title_search_location));
        showNavHome(false);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) return;
        if (requestCode == Constant.GET_PLACE_REQUEST_CODE) {
            try {
                String json = data.getStringExtra("address");
                Intent intent = new Intent();
                intent.putExtra("address", json);
                setResult(RESULT_OK, intent);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode == Constant.LOCATION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                finishWithParsing(place.getLatLng().latitude,place.getLatLng().longitude);
            }
        }
    }

    public void finishWithResult(AddressDetail addressDetail) {
        Intent intent = new Intent();
        intent.putExtra("address", Serializer.getInstance().serializeAddressDetail(addressDetail));
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        _instance = null;
    }
}