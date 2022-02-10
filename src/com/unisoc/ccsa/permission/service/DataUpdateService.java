package com.unisoc.ccsa.permission.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import com.unisoc.ccsa.Log;
import com.unisoc.ccsa.permission.provider.ProviderInfo;
import com.unisoc.ccsa.permission.util.AppPermInfo;
import com.unisoc.ccsa.permission.util.Util;
import java.util.ArrayList;

public class DataUpdateService extends Service {

    private static final String TAG = "DataUpdateService";

    private static final int TYPE_UPDATE = 1;
    private static final int TYPE_INSTALL = 2;
    private static final int TYPE_UNINSTALL = 3;

    private AppPermInfo mAppPermInfo;
    private ArrayList<OnDataChangeListener> mOnDataChangeListeners = new ArrayList<OnDataChangeListener>();

    @Override
    public IBinder onBind(Intent intent) {
        return new DataUpdateBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAppPermInfo = new AppPermInfo(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");

        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new UpdateTask().execute(String.valueOf(TYPE_UPDATE), null);
        return START_STICKY;
    }

    private synchronized boolean update(int type, String packageName) {
        boolean update = false;

        switch (type) {
            case TYPE_UPDATE:
                ArrayList<AppPermInfo.AppInfo> apps = mAppPermInfo.getAppInfo();
                Log.i(TAG, "appPermissionInfos size=" + apps.size());

                ArrayList<String> list = mAppPermInfo.getPackagesFromDB();

                if (list.size() > apps.size()) {
                    for (String name : list) {
                        boolean isExist = true;
                        try {
                            mAppPermInfo.findAppFromPackageName(name);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                            isExist = false;
                        }
                        if (!isExist) {
                            remove(name);
                            update = true;
                        }
                    }
                } else {
                    for (AppPermInfo.AppInfo appInfo : apps) {

                        if (!mAppPermInfo.isPackageExist(appInfo.mPackageName)) {
                            for (String s : appInfo.mAppPermID) {
                                add(s, appInfo);
                            }
                            update = true;
                        }
                    }
                }
                break;
            case TYPE_INSTALL:
                AppPermInfo.AppInfo appInfo = null;
                try {
                    appInfo = mAppPermInfo.findAppFromPackageName(packageName);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    break;
                }

                if (appInfo.mAppPermID.size() > 0) {
                    for (String s : appInfo.mAppPermID) {
                        add(s, appInfo);
                    }
                    update = true;
                }

                break;
            case TYPE_UNINSTALL:
                if (mAppPermInfo.getPackageInfoByPacName(packageName) != null) {
                    remove(packageName);
                    update = true;
                }
                break;
            default:
                break;
        }

        return update;
    }

    private void add(String permission, AppPermInfo.AppInfo appInfo) {
        Log.d(TAG, "add package:" + appInfo.mPackageName + " permission:" + permission);
        ContentValues values = new ContentValues();
        values.put(ProviderInfo.SIGN, appInfo.mSign);
        values.put(ProviderInfo.UID, appInfo.mAppUid);
        values.put(ProviderInfo.PERMISSIONID, Integer.parseInt(permission));
        values.put(ProviderInfo.PERMISSION, Util.PERM_STATUS_TIP);
        values.put(ProviderInfo.PACKAGENAME, appInfo.mPackageName);

        getContentResolver().insert(ProviderInfo.CONTENT_URI, values);
    }

    private void remove(String packageName) {
        Log.d(TAG, "remove package:" + packageName);
        getContentResolver().delete(ProviderInfo.CONTENT_URI_LOG, ProviderInfo.LOG_PKGNAME + "= " + "'" + packageName + "'", null);
        getContentResolver().delete(ProviderInfo.CONTENT_URI, ProviderInfo.PACKAGENAME + "= " + "'" + packageName + "'", null);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private class UpdateTask extends AsyncTask<String, Integer, Boolean> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            return update(Integer.parseInt(strings[0]), strings[1]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                for (OnDataChangeListener listener : mOnDataChangeListeners) {
                    listener.onDataChange();
                }
            }
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int type = -1;

            if (action.equals(Intent.ACTION_PACKAGE_ADDED)) {
                type = TYPE_INSTALL;
            } else if (action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
                type = TYPE_UNINSTALL;
            } else {
                Log.e(TAG, "invalid action:" + action);
                return;
            }

            String name = intent.getData().getSchemeSpecificPart();
            new UpdateTask().execute(String.valueOf(type), name);
        }
    };

    public void addDataChangeListener(OnDataChangeListener listener) {
        if (!mOnDataChangeListeners.contains(listener)) {
            mOnDataChangeListeners.add(listener);
        }
    }

    public void removeDataChangeListener(OnDataChangeListener listener) {
        if (mOnDataChangeListeners.contains(listener)) {
            mOnDataChangeListeners.remove(listener);
        }
    }

    public class DataUpdateBinder extends Binder {

        private DataUpdateService mService;

        public DataUpdateBinder() {
            mService = DataUpdateService.this;
        }

        public void addDataChangeListener(OnDataChangeListener listener) {
            mService.addDataChangeListener(listener);
        }

        public void removeDataChangeListener(OnDataChangeListener listener) {
            mService.removeDataChangeListener(listener);
        }

    }

    public interface OnDataChangeListener {
        void onDataChange();
    }
}