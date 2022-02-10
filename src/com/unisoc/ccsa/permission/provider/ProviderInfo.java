package com.unisoc.ccsa.permission.provider;

import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

public class ProviderInfo {
    /* tables colum */
    public static final String ID = "_id";
    public static final String SIGN = "sign";
    public static final String UID = "uid";
    public static final String PACKAGENAME = "package_name";
    public static final String PERMISSIONID = "permission_id";
    public static final String PERMISSION = "permission";
    public static final String ASSIST = "assist";
    public static final String COUNT = "count(*) count ";

    public static final String ID2 = "_id";
    public static final String SIGN2 = "sign";
    public static final String UID2 = "uid";
    public static final String PERMISSION2 = "permission";
    public static final String PACKAGENAME2 = "package_name";

    //SPRD:ADD @{
    public static final String LOG_ID = "_id";
    public static final String LOG_PKGNAME = "pkgName";
    public static final String LOG_PERMID = "permName";
    public static final String LOG_STRATEGY = "permStrategy";
    public static final String LOG_TIME = "time";
    //@}

    public static class ControlColumns implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/control");
        public static final String SERVICE_NAME = "service_name";
        public static final String PERMISSION = "permission";
    }

    /* database name */
    public static String DATABASE_NAME = "permission.db";
    public static int DATABASE_VERSION = 6;
    public static String PERMISSION_TABLE_NAME = "permission";

    //SPRD:ADD @{
    public static String TABLE_LOG = "log";
    //@}
    public static String CONTROL_TABLE = "control";

    public static final UriMatcher uriMatcher;
    public static final int ITEM = 1;
    public static final int ITEM_UID = 2;
    public static final int ITEM_PERMISSIONID = 3;
    public static final int ITEM_PERMISSIONCOUNT = 4;
    public static final int ITEM_APPCOUNT = 5;

    //SPRD:ADD @{
    public static final int LOG = 8;
    public static final int LOG_PKGNAME_DISTINCT = 22;
    //@}
    public static final int ITEM_CONTROL = 18;

    public static final String AUTHORITY = "com.unisoc.ccsa.permission.provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/permission");
    public static final Uri CONTENT_URI_UID = Uri.parse("content://" + AUTHORITY + "/permissionUID");
    public static final Uri CONTENT_URI_PERMISSIONID = Uri.parse("content://" + AUTHORITY + "/permissionID");
    public static final Uri CONTENT_URI_PERMISSIONCOUNT = Uri.parse("content://" + AUTHORITY + "/permissionCount");
    public static final Uri CONTENT_URI_APPCOUNT = Uri.parse("content://" + AUTHORITY + "/appCount");


    //SPRD:ADD@{
    public static final Uri CONTENT_URI_LOG = Uri.parse("content://" + AUTHORITY + "/log");
    public static final Uri CONTENT_URI_LOG_PKGNAME_DISTINCT = Uri.parse("content://" + AUTHORITY + "/log_pkgName");

    //@}
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "permission", ITEM);
        uriMatcher.addURI(AUTHORITY, "permissionUID/#", ITEM_UID);
        uriMatcher.addURI(AUTHORITY, "permissionID/#", ITEM_PERMISSIONID);
        uriMatcher.addURI(AUTHORITY, "permissionCount", ITEM_PERMISSIONCOUNT);
        uriMatcher.addURI(AUTHORITY, "appCount", ITEM_APPCOUNT);


        //SPRD:ADD@{
        uriMatcher.addURI(AUTHORITY, "log", LOG);
        uriMatcher.addURI(AUTHORITY, "log_pkgName", LOG_PKGNAME_DISTINCT);
        //@}
        uriMatcher.addURI(AUTHORITY, "control", ITEM_CONTROL);
    }


}
