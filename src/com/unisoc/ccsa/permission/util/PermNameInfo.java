package com.unisoc.ccsa.permission.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.unisoc.ccsa.Log;
import com.unisoc.ccsa.R;
import com.unisoc.ccsa.permission.provider.ProviderInfo;

public class PermNameInfo {

    private static final String TAG = "PermNameInfo";

    public String mPermName;
    public int mPermCount;
    public String mPermID;
    public int mAppUid;
    public int mPerm;

    private Context mContext;
    private ContentResolver mContentResolver;

    private String[] projects = new String[]{
            ProviderInfo.PERMISSIONID,
            ProviderInfo.UID,
            ProviderInfo.PERMISSION
    };

    public PermNameInfo() {

    }

    public PermNameInfo(Context context) {
        mContext = context;
    }

    public ArrayList<PermNameInfo> getPermNameInfos() {
        ArrayList<PermNameInfo> permName = new ArrayList<PermNameInfo>();

        Set<String> permID = PermType.PERMNAME.keySet();

        Iterator<String> ior = permID.iterator();
        while (ior.hasNext()) {
            PermNameInfo permNameInfo = new PermNameInfo();
            String id = ior.next();
            permNameInfo.mPermID = id;

            permNameInfo.mPermCount = getPermCount(id);
            if (permNameInfo.mPermCount > 0) {
                permNameInfo.mPermName = mContext.getString(PermType.PERMNAME.get(id));
                permName.add(permNameInfo);
            }
        }
        return permName;
    }

    public ArrayList<PermGroup> getPermGroups() {
        ArrayList<PermGroup> appPermGroup = new ArrayList<PermGroup>();

        PermGroup groupCommication = new PermGroup(mContext.getString(R.string.group_commication));
        PermGroup goupSentive = new PermGroup(mContext.getString(R.string.group_senstive));
        PermGroup groupInterface = new PermGroup(mContext.getString(R.string.group_interface));

        Set<String> permID = PermType.PERMNAME.keySet();

        Iterator<String> ior = permID.iterator();

        while (ior.hasNext()) {
            PermNameInfo permNameInfo = new PermNameInfo();
            String id = ior.next();
            if (!PermType.isValidPermission(id)) {
                continue;
            }
            permNameInfo.mPermID = id;
            permNameInfo.mPermCount = getPermCount(id);
            switch (Integer.parseInt(id) / PermType.BASE_NUMBER) {
                case PermType.PERM_GROUP_COMMICATION:
                    permNameInfo.mPermName = mContext.getString(PermType.PERMNAME.get(id));
                    groupCommication.addPermItem(permNameInfo);
                    break;
                case PermType.PERM_GROUP_SENSTIVE:
                    permNameInfo.mPermName = mContext.getString(PermType.PERMNAME.get(id));
                    goupSentive.addPermItem(permNameInfo);
                    break;
                case PermType.PERM_GROUP_INTERFACE:
                    permNameInfo.mPermName = mContext.getString(PermType.PERMNAME.get(id));
                    groupInterface.addPermItem(permNameInfo);
                    break;
                default:
                    Log.e(TAG, "invalid permission type");
                    break;
            }
        }

        appPermGroup.add(groupCommication);
        appPermGroup.add(goupSentive);
        appPermGroup.add(groupInterface);

        ArrayList<PermGroup> permGroups = new ArrayList<PermGroup>();
        for (PermGroup permGroup : appPermGroup) {
            PermGroup newPermGroup = new PermGroup(permGroup.mPermGroupNanme);
            ArrayList<PermNameInfo> list = Util.sortPermission(permGroup.mPermGroupItem);
            for (int i = 0; i < list.size(); i++) {
                newPermGroup.addPermItem(list.get(i));
            }
            permGroups.add(newPermGroup);
        }
        return permGroups;
    }

    public ArrayList<PermNameInfo> getAppPermName(int uid) {
        ArrayList<PermNameInfo> appPermName = new ArrayList<PermNameInfo>();
        mContentResolver = mContext.getContentResolver();
        Uri uri = ContentUris.withAppendedId(ProviderInfo.CONTENT_URI_UID, uid);
        Cursor cursor = mContentResolver.query(uri, projects, null, null, null);
        try {
            while (cursor.moveToNext()) {
                PermNameInfo permNameInfo = new PermNameInfo();
                permNameInfo.mAppUid = cursor.getInt(cursor.getColumnIndex(ProviderInfo.UID));

                if (PermType.PERMNAME.containsKey(cursor.getString(
                    cursor.getColumnIndex(ProviderInfo.PERMISSIONID)))) {
                    permNameInfo.mPermName = mContext.getString(
                        PermType.PERMNAME.get(
                                cursor.getString(
                                        cursor.getColumnIndex(
                                                ProviderInfo.PERMISSIONID))));

            }
                permNameInfo.mPermID = cursor.getString(
                        cursor.getColumnIndex(ProviderInfo.PERMISSIONID));

                appPermName.add(permNameInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }


        return appPermName;

    }


    public ArrayList<PermGroup> getAppPermGroup(int uid) {

        ArrayList<PermGroup> appPermGroup = new ArrayList<PermGroup>();

        PermGroup groupCommication = new PermGroup(mContext.getString(R.string.group_commication));
        PermGroup goupSentive = new PermGroup(mContext.getString(R.string.group_senstive));
        PermGroup groupInterface = new PermGroup(mContext.getString(R.string.group_interface));

        Cursor cursor = null;
        try {
            mContentResolver = mContext.getContentResolver();
            Uri uri = ContentUris.withAppendedId(ProviderInfo.CONTENT_URI_UID, uid);
            cursor = mContentResolver.query(uri, projects, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    PermNameInfo permNameInfo = new PermNameInfo();
                    permNameInfo.mAppUid = cursor.getInt(cursor.getColumnIndex(ProviderInfo.UID));
                    permNameInfo.mPermID = cursor.getString(
                            cursor.getColumnIndex(ProviderInfo.PERMISSIONID));
                    permNameInfo.mPerm = cursor.getInt(cursor.getColumnIndex(ProviderInfo.PERMISSION));

                    switch (Integer.parseInt(permNameInfo.mPermID) / PermType.BASE_NUMBER) {
                        case PermType.PERM_GROUP_COMMICATION:
                            permNameInfo.mPermName = mContext.getString(
                                    PermType.PERMNAME.get(
                                            cursor.getString(
                                                    cursor.getColumnIndex(
                                                            ProviderInfo.PERMISSIONID))));
                            groupCommication.addPermItem(permNameInfo);
                            break;
                        case PermType.PERM_GROUP_SENSTIVE:
                            permNameInfo.mPermName = mContext.getString(
                                    PermType.PERMNAME.get(
                                            cursor.getString(
                                                    cursor.getColumnIndex(
                                                            ProviderInfo.PERMISSIONID))));
                            goupSentive.addPermItem(permNameInfo);

                            break;
                        case PermType.PERM_GROUP_INTERFACE:
                            permNameInfo.mPermName = mContext.getString(
                                    PermType.PERMNAME.get(
                                            cursor.getString(
                                                    cursor.getColumnIndex(
                                                            ProviderInfo.PERMISSIONID))));
                            groupInterface.addPermItem(permNameInfo);

                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        if (groupCommication.getPermItemCount() > 1) {
            appPermGroup.add(groupCommication);
        }
        if (goupSentive.getPermItemCount() > 1) {
            appPermGroup.add(goupSentive);
        }
        if (groupInterface.getPermItemCount() > 1) {
            appPermGroup.add(groupInterface);
        }


        ArrayList<PermGroup> permGroups = new ArrayList<PermGroup>();
        for (PermGroup permGroup : appPermGroup) {
            PermGroup newPermGroup = new PermGroup(permGroup.mPermGroupNanme);
            ArrayList<PermNameInfo> list = Util.sortPermission(permGroup.mPermGroupItem);
            for (int i = 0; i < list.size(); i++) {
                newPermGroup.addPermItem(list.get(i));
            }
            permGroups.add(newPermGroup);
        }

        return permGroups;

    }

    private int getPermCount(String permID) {
        int count = 0;

        mContentResolver = mContext.getContentResolver();
        Uri uri = ContentUris.withAppendedId(ProviderInfo.CONTENT_URI_PERMISSIONID, Integer.parseInt(permID));

        Cursor cursor = mContentResolver.query(uri, null, null, null, null);

        try {
            count = cursor.getCount();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }

    //SPRD:ADD @{
    public String getPkgNameByUid(Context context, int appUid) {
        String mPkgName = null;
        mContentResolver = context.getContentResolver();
        Uri mUri = ContentUris.withAppendedId(ProviderInfo.CONTENT_URI_UID, appUid);
        Cursor mCur = mContentResolver.query(mUri, null, null, null, null);
        try {
            if ((mCur != null) && (mCur.getCount() > 0)) {
                mCur.moveToFirst();
                mPkgName = mCur.getString(mCur.getColumnIndex(ProviderInfo.PACKAGENAME));
            }
        } catch (Exception e) {

        } finally {
            if (mCur != null) {
                mCur.close();
            }
        }
        return mPkgName;
    }
    //@}
}