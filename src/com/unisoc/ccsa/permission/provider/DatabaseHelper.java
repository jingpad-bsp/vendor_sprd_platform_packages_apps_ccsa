package com.unisoc.ccsa.permission.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String CREATE_TABLE = "CREATE TABLE "
            + ProviderInfo.PERMISSION_TABLE_NAME
            + "(" + ProviderInfo.ID + " INTEGER PRIMARY KEY,"
            + ProviderInfo.SIGN + " TEXT,"
            + ProviderInfo.UID + " INTEGER,"
            + ProviderInfo.PACKAGENAME + " TEXT,"
            + ProviderInfo.PERMISSIONID + "  INTEGER,"
            + ProviderInfo.PERMISSION + "  INTEGER,"
            + ProviderInfo.ASSIST + "  TEXT"
            + "); ";


    //SPRD:ADD @{
    private static final String CREATE_TABLE_LOG = "CREATE TABLE "
            + ProviderInfo.TABLE_LOG
            + "(" + ProviderInfo.LOG_ID + " INTEGER PRIMARY KEY,"
            + ProviderInfo.LOG_PKGNAME + " TEXT,"
            + ProviderInfo.LOG_PERMID + " TEXT,"
            + ProviderInfo.LOG_STRATEGY + " INTEGER,"
            // + ProviderInfo.LOG_TIME+" TimeStamp NOT NULL DEFAULT (datetime ('now','localtime'))"CURRENT_TIMESTAMP
            + ProviderInfo.LOG_TIME + " TimeStamp NOT NULL DEFAULT (datetime (CURRENT_TIMESTAMP,'localtime'))"
            + "); ";
    //@}

    private static final String TABLE_CONTROL = "CREATE TABLE "
            + ProviderInfo.CONTROL_TABLE
            + "(" + ProviderInfo.ControlColumns._ID + " INTEGER PRIMARY KEY,"
            + ProviderInfo.ControlColumns.SERVICE_NAME + " TEXT,"
            + ProviderInfo.ControlColumns.PERMISSION + " INTEGER"
            + ");";

    //create database
    DatabaseHelper(Context context) {
        super(context, ProviderInfo.DATABASE_NAME, null, ProviderInfo.DATABASE_VERSION);
    }

    //create table
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE);
        db.execSQL(TABLE_CONTROL);
        //SPRD:ADD @{
        db.execSQL(CREATE_TABLE_LOG);
        //@}
        String insertMe = "INSERT INTO control (service_name, permission) VALUES ('security',0)";
        db.execSQL(insertMe);
    }

    //update table
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS permission");
        db.execSQL("DROP TABLE IF EXISTS self_start");
        //SPRD:ADD @{
        db.execSQL("DROP TABLE IF EXISTS log");
        //@}
        db.execSQL("DROP TABLE IF EXISTS control");
        onCreate(db);
    }


}
