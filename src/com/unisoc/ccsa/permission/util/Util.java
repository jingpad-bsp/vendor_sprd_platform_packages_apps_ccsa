package com.unisoc.ccsa.permission.util;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;

import com.unisoc.ccsa.Log;
import com.unisoc.ccsa.R;
import com.unisoc.ccsa.permission.provider.ProviderInfo;

public class Util {
    private static final String TAG = "Util";

    public static final String INTENT_KEY_PERMISSION_NAME = "permissionName";
    public static final String INTENT_KEY_APP_COUNT = "appCount";
    public static final String INTENT_KEY_PERMISSION_ID = "permissionID";

    public static final String INTENT_KEY_APP_UID = "applicationUID";
    public static final String INTENT_KEY_APP_PACKAGENAME = "packageName";
    public static final String INTENT_KEY_APP_PERM_COUNT = "permissionCount";
    public static final String INTENT_KEY_APP_NAME = "applicationName";

    public static final String MONITORED_SHARED_PREFERENCES_NAME = "ifMonitoredPreferences";
    public static final String DELETE_LOG_IN_CHOSEN_TIME_PREFERENCES_NAME = "DeleteLogInChosenTimePreferences";
    public static final String THE_CHOSEN_TIME = "TheChosenTime";

    public static final int THE_CHOSEN_TIME_ONE_DAY = R.id.one_day;
    public static final int THE_CHOSEN_TIME_ONE_WEEK = R.id.one_week;
    public static final int THE_CHOSEN_TIME_ONE_MONTH = R.id.one_month;

    public static final int PERM_STATUS_ALLOW = 0;
    public static final int PERM_STATUS_REFUSE = 1;
    public static final int PERM_STATUS_TIP = 2;

    public static Drawable getAppIcon(Context context, String packageName) {
        Drawable icon = null;
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            icon = applicationInfo.loadIcon(context.getPackageManager());
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return context.getResources().getDrawable(R.mipmap.ic_launcher);
        }
        return icon;
    }

    public static String getAppName(Context context, String packageName) {
        String name = null;
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            name = applicationInfo.loadLabel(context.getPackageManager()).toString();
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return name;
    }

    //SPRD:ADD get the logInfo@{
    public static List<PermDoEntity> getLogInfoByPkgName(Context context, String pkgName) {
        if (context != null && pkgName != null) {
            List<PermDoEntity> mList = new ArrayList<PermDoEntity>();
            DoLogTable mDoLog = new DoLogTable(context);
            mList = mDoLog.getlogInfoByPkgName(pkgName);

            return mList;
        }
        return null;
    }

    public static ArrayList<PermNameInfo> sortPermission(ArrayList<PermNameInfo> noSortList) {
        ArrayList<PermNameInfo> list = new ArrayList<PermNameInfo>(noSortList);
        int size = noSortList.size();
        for (int i = 0; i < size; i++) {
            for (int j = i; j < size; j++) {
                if (Integer.parseInt(list.get(i).mPermID) > Integer.parseInt(list.get(j).mPermID)) {
                    PermNameInfo permNameInfoi = list.get(i);
                    PermNameInfo permNameInfoj = list.get(j);
                    list.set(i, permNameInfoj);
                    list.set(j, permNameInfoi);
                }
            }

        }
        for (int i = 0; i < list.size(); i++) {
            Log.i(TAG, "[Util] " + list.get(i).mPermName);
        }
        return list;

    }

    public static void insertCallLog(Context context, ContentValues cv) {
        if (context != null) {
            context.getContentResolver().insert(ProviderInfo.CONTENT_URI, cv);
            Log.i(TAG, "insert into callLog.");
        }
    }
    //@}
}