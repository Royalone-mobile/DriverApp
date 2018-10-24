package com.sawatruck.driver.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.sawatruck.driver.controller.NearbyLocation;
import com.sawatruck.driver.entities.AddressDetail;
import com.sawatruck.driver.entities.PayPalPayment;

import java.util.ArrayList;

public class DBController {

    private DBHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;

    private static DBController _instance;
    static {
        _instance = new DBController();
    }

    public static DBController getInstance(){
        return _instance;
    }

    public DBController open(Context context) throws SQLException {
        this.context = context;
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public NearbyLocation getSavedLocationByLatLng(LatLng latLng){
        String query = "SELECT * FROM " + DBHelper.TABLE_SAVED_LOCATION +" WHERE latitude='" + latLng.latitude + "' AND longitude='" +latLng.longitude + "'";
        NearbyLocation nearbyLocation = new NearbyLocation();
        Cursor cursor = database.rawQuery(query,null);

        if (cursor != null) {
            cursor.moveToFirst();

            if(cursor.moveToFirst()) {

                nearbyLocation.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitude")));
                nearbyLocation.setLongitude(cursor.getDouble(cursor.getColumnIndex("longitude")));
                nearbyLocation.setName(cursor.getString(cursor.getColumnIndex("name")));
                nearbyLocation.setVincity(cursor.getString(cursor.getColumnIndex("vinicity")));

                return nearbyLocation;
            }
        }
        return null;
    }

    public void closeDB(){
        dbHelper.close();
    }

    public void insertSearch(LoadSearchDAO loadSearch){
        ContentValues contentValue = new ContentValues();
        contentValue.put("load_date",loadSearch.getLoadDate());
        contentValue.put("load_location", loadSearch.getLoadLocation());
        contentValue.put("delivery_date", loadSearch.getDeliveryDate());
        contentValue.put("delivery_location", loadSearch.getDeliveryLocation());
        contentValue.put("load_address", loadSearch.getLoadAddress());
        contentValue.put("delivery_address", loadSearch.getDeliveryAddress());

        contentValue.put("search_name", loadSearch.getSearchName());

        database.insert(DBHelper.TABLE_SEARCH_LOAD, null, contentValue);
    }


    public void insertSavedLocation(NearbyLocation nearbyLocation){
        ContentValues contentValue = new ContentValues();
        contentValue.put("latitude", nearbyLocation.getLatitude());
        contentValue.put("longitude", nearbyLocation.getLongitude());
        contentValue.put("name", nearbyLocation.getName());
        contentValue.put("vinicity", nearbyLocation.getVincity());
        database.insert(DBHelper.TABLE_SAVED_LOCATION, null, contentValue);
    }

    public void deleteSavedLocation(NearbyLocation nearbyLocation) {
        database.execSQL("delete from "+DBHelper.TABLE_SAVED_LOCATION +" where latitude='"+nearbyLocation.getLatitude()+"' AND longitude='" + nearbyLocation.getLongitude()+ "' " );
    }

    public void insertRecentLocation(NearbyLocation nearbyLocation){
        ContentValues contentValue = new ContentValues();
        contentValue.put("latitude", nearbyLocation.getLatitude());
        contentValue.put("longitude", nearbyLocation.getLongitude());
        contentValue.put("name", nearbyLocation.getName());
        contentValue.put("vinicity", nearbyLocation.getVincity());
        database.insert(DBHelper.TABLE_RECENT_LOCATION, null, contentValue);
    }

    public void deleteRecentLocation(NearbyLocation nearbyLocation) {
        database.execSQL("delete from "+DBHelper.TABLE_RECENT_LOCATION +" where latitude='"+nearbyLocation.getLatitude()+"' AND longitude='" + nearbyLocation.getLongitude()+ "' " );
    }

    public ArrayList<NearbyLocation> getRecentLocations() {
        ArrayList<NearbyLocation> nearbyLocations = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + DBHelper.TABLE_RECENT_LOCATION;

        Cursor cursor = database.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do{
                    NearbyLocation nearbyLocation = new NearbyLocation();
                    nearbyLocation.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitude")));
                    nearbyLocation.setLongitude(cursor.getDouble(cursor.getColumnIndex("longitude")));
                    nearbyLocation.setName(cursor.getString(cursor.getColumnIndex("name")));
                    nearbyLocation.setVincity(cursor.getString(cursor.getColumnIndex("vinicity")));
                    nearbyLocations.add(nearbyLocation);
                }while (cursor.moveToNext());
            }


        }catch (Exception e)
        {
            Log.e("ErrorMessage : ", e.getMessage());
        }
        return nearbyLocations;
    }

    public ArrayList<LoadSearchDAO> getSearchs() {
        ArrayList<LoadSearchDAO> searchList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + DBHelper.TABLE_SEARCH_LOAD;

        Cursor cursor = database.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do{
                    LoadSearchDAO searchDAO = new LoadSearchDAO();
                    searchDAO.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    searchDAO.setLoadDate(cursor.getString(cursor.getColumnIndex("load_date")));
                    searchDAO.setLoadLocation(cursor.getString(cursor.getColumnIndex("load_location")));
                    searchDAO.setDeliveryDate(cursor.getString(cursor.getColumnIndex("delivery_date")));
                    searchDAO.setDeliveryLocation(cursor.getString(cursor.getColumnIndex("delivery_location")));
                    searchDAO.setSearchName(cursor.getString(cursor.getColumnIndex("search_name")));
                    searchDAO.setLoadAddress(cursor.getString(cursor.getColumnIndex("load_address")));
                    searchDAO.setDeliveryAddress(cursor.getString(cursor.getColumnIndex("delivery_address")));
                    searchList.add(searchDAO);
                }while (cursor.moveToNext());
            }


        }catch (Exception e)
        {
            Log.e("ErrorMessage : ", e.getMessage());
        }
        return searchList;
    }

    public void deleteSearch(LoadSearchDAO loadSearchDAO) {
        database.execSQL("delete from "+ DBHelper.TABLE_SEARCH_LOAD + " where id='"+ loadSearchDAO.getId()+ "'");
    }

    public ArrayList<NearbyLocation> getSavedLocations() {
        ArrayList<NearbyLocation> nearbyLocations = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + DBHelper.TABLE_SAVED_LOCATION;

        Cursor cursor = database.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do{
                    NearbyLocation nearbyLocation = new NearbyLocation();
                    nearbyLocation.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitude")));
                    nearbyLocation.setLongitude(cursor.getDouble(cursor.getColumnIndex("longitude")));
                    nearbyLocation.setName(cursor.getString(cursor.getColumnIndex("name")));
                    nearbyLocation.setVincity(cursor.getString(cursor.getColumnIndex("vinicity")));
                    nearbyLocations.add(nearbyLocation);
                }while (cursor.moveToNext());
            }


        }catch (Exception e)
        {
            Log.e("ErrorMessage : ", e.getMessage());
        }
        return nearbyLocations;
    }


    public NearbyLocation getRecentLocationByLatLng(LatLng latLng) {
        String query = "SELECT * FROM " + DBHelper.TABLE_RECENT_LOCATION +" WHERE latitude='" + latLng.latitude + "' AND longitude='" +latLng.longitude + "'";
        NearbyLocation nearbyLocation = new NearbyLocation();
        Cursor cursor = database.rawQuery(query,null);

        if (cursor != null) {
            cursor.moveToFirst();

            if(cursor.moveToFirst()) {

                nearbyLocation.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitude")));
                nearbyLocation.setLongitude(cursor.getDouble(cursor.getColumnIndex("longitude")));
                nearbyLocation.setName(cursor.getString(cursor.getColumnIndex("name")));
                nearbyLocation.setVincity(cursor.getString(cursor.getColumnIndex("vinicity")));

                return nearbyLocation;
            }
        }
        return null;
    }


    public void insertPayPalPayment(PayPalPayment paypalPayment){
        ContentValues contentValue = new ContentValues();
        contentValue.put("PayerID",paypalPayment.getPayerID());
        contentValue.put("amount", paypalPayment.getAmount());
        database.insert(DBHelper.TABLE_PAYPAL_PAYMENT, null, contentValue);
    }

    public void deletePayPalPayment(PayPalPayment paypalPayment) {
        database.execSQL("delete  from "+DBHelper.TABLE_PAYPAL_PAYMENT +" where PayerID='"+paypalPayment.getPayerID()+"' AND amount='" + paypalPayment.getAmount() + "' " );
    }

    public ArrayList<PayPalPayment> getPaypalPayments() {
        ArrayList<PayPalPayment> paypalPayments = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + DBHelper.TABLE_PAYPAL_PAYMENT;

        Cursor cursor = database.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do{
                    PayPalPayment paypalPayment = new PayPalPayment();
                    paypalPayment.setAmount(cursor.getString(cursor.getColumnIndex("amount")));
                    paypalPayment.setPayerID(cursor.getString(cursor.getColumnIndex("PayerID")));
                    paypalPayments.add(paypalPayment);
                }while (cursor.moveToNext());
            }


        }catch (Exception e) {
            Log.e("ErrorMessage : ", e.getMessage());
        }
        return paypalPayments;
    }

}
