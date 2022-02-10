package com.unisoc.ccsa.permission.util;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import android.Manifest.permission;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.unisoc.ccsa.Log;
import com.unisoc.ccsa.permission.provider.ProviderInfo;


public class AppPermInfo {

    private static final String TAG = "AppPermInfo";

    public class AppInfo {
        public String mAppName;
        public String mSign;
        public Drawable mAppIcon;
        public int mAppUid;
        public String mPackageName;
        public int mAllow;
        public int mPermID;

        public ArrayList<String> mAppPermission;
        public ArrayList<String> mAppPermID;
    }


    public class AutoStartInfo {
        public String appName;
        public String appSign;
        public int appUid;
        public int appAllow;
        public String appPackageName;
    }

    public ArrayList<AppPermInfo> mApplicationInfos;

    public ArrayList<String> mAppPermission;

    private Context mContext;

    public ArrayList<AppInfo> mAppInfos;

    private ContentResolver mContentResolver;

    public ArrayList<AutoStartInfo> mAutoStartInfos;

    public AppPermInfo(Context context) {
        mContext = context;
        mContentResolver = context.getContentResolver();
    }

    public ArrayList<AppInfo> getAppInfo() {
        List<PackageInfo> packages = mContext.getPackageManager().getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        mAppInfos = new ArrayList<AppInfo>();
        for (PackageInfo packageInfo : packages) {
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                AppInfo appInfo = new AppInfo();

                appInfo.mAppPermission = getAppPerms(packageInfo.packageName);
                appInfo.mAppPermID = getAppPermID(packageInfo.packageName);
                if (appInfo.mAppPermission.size() > 0) {
                    appInfo.mAppName = packageInfo.applicationInfo.loadLabel(mContext.getPackageManager()).toString();
                    appInfo.mPackageName = packageInfo.packageName;
                    appInfo.mAppIcon = packageInfo.applicationInfo.loadIcon(mContext.getPackageManager());
                    appInfo.mAppUid = packageInfo.applicationInfo.uid;
                    appInfo.mSign = getSignature(packageInfo.packageName);
                    mAppInfos.add(appInfo);
                }

            }
        }

        return mAppInfos;
    }

    public ArrayList<AutoStartInfo> getAllInstallApp() {
        ArrayList<AutoStartInfo> autoStartInfos = new ArrayList<AutoStartInfo>();
        List<PackageInfo> packages = mContext.getPackageManager().getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (PackageInfo info : packages) {
            if ((info.applicationInfo.flags &
                    ApplicationInfo.FLAG_SYSTEM) == 0) {
                AutoStartInfo autoStartInfo = new AutoStartInfo();
                autoStartInfo.appSign = getSignature(info.packageName);
                autoStartInfo.appUid = info.applicationInfo.uid;
                autoStartInfo.appAllow = 0;
                autoStartInfo.appPackageName = info.packageName;
                autoStartInfos.add(autoStartInfo);
            }
        }

        return autoStartInfos;
    }

    public AppInfo getPackageInfoByPacName(String packageName) {
        AppInfo appInfo = null;

        Cursor cursor = mContentResolver.query(ProviderInfo.CONTENT_URI,
                new String[]{ProviderInfo.UID,
                        ProviderInfo.PERMISSIONID,
                        ProviderInfo.PERMISSION,
                        ProviderInfo.SIGN},
                ProviderInfo.PACKAGENAME + "=" + "'" + packageName + "'", null, null);

        if (cursor != null && cursor.getCount() != 0) {
            appInfo = new AppInfo();
            appInfo.mPackageName = packageName;
            appInfo.mAppPermID = new ArrayList<String>();
            appInfo.mAppPermission = new ArrayList<String>();

            appInfo.mAppPermID.clear();
            appInfo.mAppPermission.clear();

            while (cursor.moveToNext()) {
                appInfo.mAppUid = cursor.getInt(cursor.getColumnIndex(ProviderInfo.UID));
                appInfo.mSign = cursor.getString(cursor.getColumnIndex(ProviderInfo.SIGN));
                appInfo.mAppPermID.add(String.valueOf(cursor.getInt(cursor.getColumnIndex(ProviderInfo.PERMISSIONID))));
                appInfo.mAppPermission.add(String.valueOf(cursor.getInt(cursor.getColumnIndex(ProviderInfo.PERMISSION))));
            }

            cursor.close();
        }
        return appInfo;
    }

    public AppInfo findAppFromPackageName(String packageName) throws NameNotFoundException {
        ApplicationInfo appliInfo = mContext.getPackageManager().getApplicationInfo(packageName, 0);
        Log.d(TAG, "packageName!!:" + appliInfo.packageName);
        Log.d(TAG, "uid!!:" + appliInfo.uid);
        Log.d(TAG, "sign!!:" + getSignature(appliInfo.packageName));
        ArrayList<String> permId = getAppPermID(packageName);
        Log.d(TAG, "!!!!:" + permId.size());

        AppInfo appInfo = new AppInfo();
        appInfo.mPackageName = appliInfo.packageName;
        appInfo.mAppUid = appliInfo.uid;
        appInfo.mSign = getSignature(appliInfo.packageName);
        appInfo.mAppPermID = getAppPermID(packageName);
        return appInfo;
    }

    public ArrayList<AppInfo> getAppInfoFromDB(int permID) {
        ArrayList<AppInfo> appInfos = new ArrayList<AppInfo>();

        Uri uri = ContentUris.withAppendedId(ProviderInfo.CONTENT_URI_PERMISSIONID, permID);

        Cursor cursor = mContentResolver.query(uri, new String[]{ProviderInfo.PACKAGENAME, ProviderInfo.PERMISSION,
                ProviderInfo.PERMISSIONID}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                AppInfo appInfo = new AppInfo();
                String packageName = cursor.getString(cursor.getColumnIndex(ProviderInfo.PACKAGENAME));
                int allow = cursor.getInt(cursor.getColumnIndex(ProviderInfo.PERMISSION));
                int permId = cursor.getInt(cursor.getColumnIndex(ProviderInfo.PERMISSIONID));
                try {
                    ApplicationInfo applicationInfo = mContext.getPackageManager().getApplicationInfo(packageName,
                            PackageManager.GET_UNINSTALLED_PACKAGES);
                    appInfo.mAppIcon = applicationInfo.loadIcon(mContext.getPackageManager());
                    appInfo.mAppName = applicationInfo.loadLabel(mContext.getPackageManager()).toString();
                    appInfo.mAllow = allow;
                    appInfo.mAppUid = applicationInfo.uid;
                    //SPRD:ADD @{
                    appInfo.mPackageName = packageName;
                    //@}

                    appInfos.add(appInfo);
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }

            }
            cursor.close();
        }

        return appInfos;
    }

    public ArrayList<String> getPackagesFromDB() {
        String lastName = "";
        ArrayList<String> list = new ArrayList<String>();

        Cursor cursor = mContentResolver.query(ProviderInfo.CONTENT_URI,
                new String[]{ProviderInfo.PACKAGENAME}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(ProviderInfo.PACKAGENAME));

                if (!lastName.equals(name)) {
                    Log.d(TAG, "app package name=" + name);
                    list.add(name);
                    lastName = name;
                }
            }
            cursor.close();
        }

        return list;
    }

    public boolean isPackageExist(String name) {
        boolean isExist = false;
        Cursor c = mContentResolver.query(ProviderInfo.CONTENT_URI,
                new String[]{ProviderInfo.PACKAGENAME, ProviderInfo.PERMISSIONID},
                ProviderInfo.PACKAGENAME + "='" + name + "'", null, null);
        if (c != null) {
            if (c.moveToNext()) {
                isExist = true;
            }
            c.close();
        }

        return isExist;
    }

    private String getSignature(String packageName) {
        String signString = null;
        try {
            PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            Signature[] signatures = packageInfo.signatures;
            Signature signature = signatures[0];
            return parseSign(signature.toByteArray());
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return signString;
    }

    private String parseSign(byte[] signature) {
        String signString = null;
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(signature));
            signString = cert.getPublicKey().toString();

        } catch (CertificateException e) {
            e.printStackTrace();
        }

        return signString;
    }

    private ArrayList<String> getAppPerms(String packageName) {
        ArrayList<String> permList = new ArrayList<String>();

        try {
            PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            if (packageInfo.requestedPermissions != null && packageInfo.requestedPermissions.length > 0) {
                for (String s : packageInfo.requestedPermissions) {
                    if (s != null && PermType.PERMTYPE.containsKey(s)) {
                        permList.add(s);
                    }
                }
            }

        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return permList;
    }

    private ArrayList<String> getAppPermID(String packageName) {
        ArrayList<String> permIDList = new ArrayList<String>();
        List<Integer> policyIDList = PermType.getPolicyIDFromDB();
        try {
            PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            if (packageInfo.requestedPermissions != null) {
                for (String s : packageInfo.requestedPermissions) {
                    if (s != null) {
                        if (PermType.PERMTYPE.containsKey(s)) {
                            int policy_id = Integer.parseInt(getAppPermIDByPerm(s));
                            Log.d(TAG, "getAppPermID, policy_id: " + policy_id);
                            if (policyIDList.contains(policy_id) && !permIDList.contains(getAppPermIDByPerm(s))) {
                                permIDList.add(getAppPermIDByPerm(s));
                                Log.d(TAG, "package: " + packageName + " permID: " + getAppPermIDByPerm(s));
                            }
                        }
                    }
                }
            }

            if (PermType.isValidPermission(PermType.PERM_READ_MMS) && !isContainInvalidPermID(permIDList, PermType.PERM_READ_MMS)) {
                permIDList.add(PermType.PERM_READ_MMS);
            }
            if (PermType.isValidPermission(PermType.PERM_SEND_MMS) && !isContainInvalidPermID(permIDList, PermType.PERM_SEND_MMS)) {
                permIDList.add(PermType.PERM_SEND_MMS);
            }
            if (PermType.isValidPermission(PermType.PERM_WRITE_MMS) && !isContainInvalidPermID(permIDList, PermType.PERM_WRITE_MMS)) {
                permIDList.add(PermType.PERM_WRITE_MMS);
            }
            if (PermType.isValidPermission(PermType.PERM_BLUETOOTH) && !isContainInvalidPermID(permIDList, PermType.PERM_BLUETOOTH)) {
                permIDList.add(PermType.PERM_BLUETOOTH);
            }
            if (PermType.isValidPermission(PermType.PERM_SEND_EMAIL) && !isContainInvalidPermID(permIDList, PermType.PERM_SEND_EMAIL)) {
                permIDList.add(PermType.PERM_SEND_EMAIL);
            }
            if (PermType.isValidPermission(PermType.PERM_READ_PROTECTED_FILES) && !isContainInvalidPermID(permIDList, PermType.PERM_READ_PROTECTED_FILES)) {
                permIDList.add(PermType.PERM_READ_PROTECTED_FILES);
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return permIDList;
    }

    private boolean isContainInvalidPermID(ArrayList<String> arr, String permID) {
        return arr.contains(permID);
    }

    private String getAppPermIDByPerm(String perm) {

        if (perm.equals(permission.CALL_PHONE) || perm.equals(permission.CALL_PRIVILEGED)) {
            return PermType.PERMTYPE.get(permission.CALL_PHONE);
        }
        if (perm.equals(permission.ACCESS_COARSE_LOCATION) || perm.equals(permission.ACCESS_FINE_LOCATION)) {
            return PermType.PERMTYPE.get(permission.ACCESS_COARSE_LOCATION);
        }

        if (perm.equals(permission.READ_SMS) || perm.equals(permission.RECEIVE_MMS)
                || perm.equals(permission.RECEIVE_WAP_PUSH)) {
            return PermType.PERMTYPE.get(permission.READ_SMS);
        }

        return PermType.PERMTYPE.get(perm);
    }

    public String getAppPermNameByID(String permID) {
        return mContext.getString(PermType.PERMNAME.get(permID));
    }

    public String getAppPermNameByPerm(String perm) {
        return mContext.getString(PermType.PERMNAME.get(PermType.PERMTYPE.get(perm)));
    }
}
