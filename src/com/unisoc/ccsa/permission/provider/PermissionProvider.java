package com.unisoc.ccsa.permission.provider;

import java.io.File;

import com.unisoc.ccsa.permission.PermissionSettingsFragment;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class PermissionProvider extends ContentProvider {

    private DatabaseHelper mOpenHelper; //create databaseHelper
    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        return true;
    }

    private SQLiteDatabase getDatabase() {
        File file = getContext().getDatabasePath("permission.db");
        if (!file.exists()) {
            db = null;
            mOpenHelper = null;
            mOpenHelper = new DatabaseHelper(getContext());
        }
        if (mOpenHelper == null) {
            mOpenHelper = new DatabaseHelper(getContext());
        }
        if (db == null) {
            db = mOpenHelper.getWritableDatabase();
        }
        return db;

    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int n = 0;
        Cursor c;
        switch (ProviderInfo.uriMatcher.match(uri)) {
            case ProviderInfo.ITEM:
                break;
            case ProviderInfo.ITEM_UID:
                selection = ProviderInfo.UID + " = " + uri.getPathSegments().get(1);
                break;
            case ProviderInfo.ITEM_PERMISSIONID:
                selection = ProviderInfo.PERMISSIONID + " = " + uri.getPathSegments().get(1);
                break;
            case ProviderInfo.ITEM_PERMISSIONCOUNT:
                selection = "0 = 0  group by (" + ProviderInfo.UID + ")";
                break;
            case ProviderInfo.ITEM_APPCOUNT:
                selection = "0 = 0  group by (" + ProviderInfo.PERMISSIONID + ")";
                break;

            case ProviderInfo.LOG:
                n = ProviderInfo.LOG;
                break;
            case ProviderInfo.LOG_PKGNAME_DISTINCT:
                n = ProviderInfo.LOG_PKGNAME_DISTINCT;
                break;
            case ProviderInfo.ITEM_CONTROL:
                n = ProviderInfo.ITEM_CONTROL;
                break;
            default:
                throw new IllegalArgumentException("Unkonw URI" + uri);
        }
        if (n == 0) {
            c = getDatabase().query(ProviderInfo.PERMISSION_TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        }
        //SPRD:ADD @{
        else if (ProviderInfo.LOG == n) {
            c = getDatabase().query(ProviderInfo.TABLE_LOG, projection, selection, selectionArgs, null, null, sortOrder);
        } else if (ProviderInfo.LOG_PKGNAME_DISTINCT == n) {
            c = getDatabase().query(ProviderInfo.TABLE_LOG, projection, selection, selectionArgs, null, null, sortOrder);
        } else if (ProviderInfo.ITEM_CONTROL == n) {
            c = getDatabase().query(ProviderInfo.CONTROL_TABLE, projection, selection, selectionArgs, null, null, null);
        }
        //@}
        else {
            c = null;
        }
        return c;
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long row = 0;
        switch (ProviderInfo.uriMatcher.match(uri)) {
            case ProviderInfo.ITEM:
                row = getDatabase().insert(ProviderInfo.PERMISSION_TABLE_NAME, null, values);
                break;

            //SPRD:ADD @{
            case ProviderInfo.LOG:
                row = getDatabase().insert(ProviderInfo.TABLE_LOG, null, values);
                break;
            //@}
        }
        if (row > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int n = 0;
        int result;
        switch (ProviderInfo.uriMatcher.match(uri)) {
            case ProviderInfo.ITEM:
                break;
            case ProviderInfo.ITEM_UID:
                selection = ProviderInfo.UID + " = " + uri.getPathSegments().get(1);
                break;
            //SPRD:ADD @{
            case ProviderInfo.LOG:
                n = ProviderInfo.LOG;
                break;
            //@}
            default:
                throw new IllegalArgumentException("Unkonw URI" + uri);
        }
        if (n == 0) {
            result = getDatabase().delete(ProviderInfo.PERMISSION_TABLE_NAME, selection, selectionArgs);
        }
        //SPRD:ADD @{
        else if (ProviderInfo.LOG == n) {
            result = getDatabase().delete(ProviderInfo.TABLE_LOG, selection, selectionArgs);
        }
        //@}
        else {
            result = 0;
        }
        if (result > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return result;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;
        switch (ProviderInfo.uriMatcher.match(uri)) {
            case ProviderInfo.ITEM:
                count = getDatabase().update(ProviderInfo.PERMISSION_TABLE_NAME, values, selection, selectionArgs);
                break;
            case ProviderInfo.ITEM_UID:
                selection = ProviderInfo.UID + " = " + uri.getPathSegments().get(1);
                count = getDatabase().update(ProviderInfo.PERMISSION_TABLE_NAME, values, selection, selectionArgs);
                break;

            case ProviderInfo.ITEM_CONTROL:
                count = getDatabase().update(ProviderInfo.CONTROL_TABLE, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unkonw URI" + uri);

        }
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;

    }


}