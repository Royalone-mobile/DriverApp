package com.sawatruck.driver.repository;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by royal on 9/1/2017.
 */

public class DBHelper extends SQLiteOpenHelper{
    private static final String DB_NAME="sawatruck_driver.db";
    private static final int DB_VERSION = 1;
    public static final String TABLE_SEARCH_LOAD = "sawatruck_search_load";
    public static final String TABLE_SAVED_LOCATION = "sawatruck_saved_locations";
    public static final String TABLE_RECENT_LOCATION = "sawatruck_recent_locations";

    public static final String TABLE_PAYPAL_PAYMENT = "sawatruck_paypal_payment";

    public static final String CREATE_TABLE_PAYPAL_PAYMENT = "CREATE TABLE IF NOT EXISTS " + TABLE_PAYPAL_PAYMENT + " ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + "PayerID TEXT, "
            + "amount TEXT "
            + ");";


    public static final String CREATE_TABLE_SEARCH = "CREATE TABLE IF NOT EXISTS " + TABLE_SEARCH_LOAD + " ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + "load_date TEXT, "
            + "load_location TEXT, "
            + "delivery_date TEXT, "
            + "delivery_location TEXT,"
            + "delivery_address TEXT,"
            + "load_address TEXT,"
            + "search_name TEXT "
            + ");";

    public static final String CREATE_TABLE_RECENT_LOCATION = "CREATE TABLE IF NOT EXISTS " + TABLE_RECENT_LOCATION + " ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + "latitude REAL, "
            + "longitude REAL, "
            + "name TEXT, "
            + "vinicity TEXT"
            + ");";

    public static final String CREATE_TABLE_SAVED_LOCATION = "CREATE TABLE IF NOT EXISTS " + TABLE_SAVED_LOCATION + " ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + "latitude REAL, "
            + "longitude REAL, "
            + "name TEXT, "
            + "vinicity TEXT"
            + ");";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SEARCH);
        db.execSQL(CREATE_TABLE_SAVED_LOCATION);
        db.execSQL(CREATE_TABLE_RECENT_LOCATION);
        db.execSQL(CREATE_TABLE_PAYPAL_PAYMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEARCH_LOAD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECENT_LOCATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVED_LOCATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAYPAL_PAYMENT);
        onCreate(db);
    }
}
