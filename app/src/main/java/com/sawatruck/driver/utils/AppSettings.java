package com.sawatruck.driver.utils;

import android.content.Context;
import android.support.annotation.NonNull;


/**
 * Created by royalone on 2017-01-06.
 */

public class AppSettings {

    private static AppSettings instance = null;
    Context mContext = null;


    private AppSettings(@NonNull Context context) {
        if (mContext == null) {
            mContext = context;
        }
    }

    /**
     * @param context
     * @return Returns a 'Prefs' instance
     */

    public static AppSettings with(@NonNull Context context) {
        if (instance == null) {
            instance = new AppSettings(context);
        }
        return instance;
    }

    public void setUser(String user){
        Prefs.with(mContext).write("pref_user",user);
    }

    public void setBalance(String balance){
        Prefs.with(mContext).write("pref_user_balance",balance);
    }

    public String getUser(){
        return Prefs.with(mContext).read("pref_user");
    }

    public void setUserType(int userType){
        Prefs.with(mContext).writeInt("pref_user_type",userType);
    }
    public int getUserType(){
        return Prefs.with(mContext).readInt("pref_user_type");
    }

    public void setRegionCode(String regionCode){
        Prefs.with(mContext).write("pref_region_code", regionCode);
    }
    public String getRegionCode(){
        return Prefs.with(mContext).read("pref_region_code");
    }

    public String getLangCode() {
        return Prefs.with(mContext).read("pref_locale");
    }
    public void setLangCode(String localeCode){
        Prefs.with(mContext).write("pref_locale", localeCode);
    }

    public void setNotificationSetting(boolean notificationSetting) {
        Prefs.with(mContext).writeBoolean("pref_notification", notificationSetting);
    }

    public boolean getNotificationSetting(){
        return Prefs.with(mContext).readBoolean("pref_notification", true);
    }

    public String getLoadType(){
        return Prefs.with(mContext).read("pref_load_type");
    }

    public void setLoadType(String loadType){
        Prefs.with(mContext).write("pref_load_type",loadType);
    }

    public String getTruckType(){
        return Prefs.with(mContext).read("pref_truck_type");
    }

    public void setTruckType(String truckType){
        Prefs.with(mContext).write("pref_truck_type",truckType);
    }

    public String getTruckClass(){
        return Prefs.with(mContext).read("pref_truck_class");
    }

    public void setTruckClass(String truckClass){
        Prefs.with(mContext).write("pref_truck_class",truckClass);
    }

    public void setCurrentLat(double currentLat) {
        Prefs.with(mContext).writeDouble("pref_latitude",currentLat);
    }

    public void setCurrentLng(double currentLng) {
        Prefs.with(mContext).writeDouble("pref_longitude",currentLng);
    }


    public Double getCurrentLat() {
        return Prefs.with(mContext).readDouble("pref_latitude");
    }

    public Double getCurrentLng() {
        return Prefs.with(mContext).readDouble("pref_longitude");
    }

    public void setFirebaseToken(String refreshedToken) {
        Prefs.with(mContext).write("pref_firebase_token",refreshedToken);
    }

    public String getFirebaseToken() {
        return Prefs.with(mContext).read("pref_firebase_token");
    }



    public void setCurrentCity(String city) {
        Prefs.with(mContext).write("pref_current_city", city);
    }

    public String getCurrentCity(){
        return Prefs.with(mContext).read("pref_current_city");
    }

    public void setNotificationCount(int count) {
        Prefs.with(mContext).writeInt("pref_notification_count", count);
    }

    public int getNotificationCount() {
        return Prefs.with(mContext).readInt("pref_notification_count", 0 );
    }

    public void setLanguageDefined(boolean languageDefined) {
        Prefs.with(mContext).writeBoolean("pref_language_defined", languageDefined);
    }

    public boolean getLanguageDefined() {
        return Prefs.with(mContext).readBoolean("pref_language_defined", false);
    }
}
