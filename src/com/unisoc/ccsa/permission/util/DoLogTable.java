package com.unisoc.ccsa.permission.util;

import java.util.ArrayList;
import java.util.List;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.unisoc.ccsa.Log;
import com.unisoc.ccsa.permission.adapter.IrefreshView;
import com.unisoc.ccsa.permission.provider.ProviderInfo;

public class DoLogTable {
    private static final String TAG = "DoLogTable";
    private Context mContext = null;
    private static IrefreshView mLogView;

    public DoLogTable(Context context) {
        this.mContext = context;
    }

    public static void setLogViewRefresh(IrefreshView refresh) {
        mLogView = refresh;
    }

    public void refreshLogView() {
        if (mLogView != null) {
            mLogView.refresh();
        }
    }

    public static void releaseRefreshLog() {
        if (mLogView != null) {
            mLogView = null;
        }
    }

    // query all Log info
    public Cursor queryAllLogInfo() {
        Cursor mCur = null;
        String mPkgName = null;
        String mPermID = null;
        int mPermStrategy;
        String mTime;
        try {
            mCur = mContext.getContentResolver().query(
                    ProviderInfo.CONTENT_URI_LOG, null, null, null, null);
            if (mCur != null && mCur.getCount() > 0) {
                for (mCur.moveToFirst(); !mCur.isAfterLast(); mCur.moveToNext()) {
                    mPkgName = mCur.getString(mCur
                            .getColumnIndex(ProviderInfo.LOG_PKGNAME));
                    mPermID = mCur.getString(mCur
                            .getColumnIndex(ProviderInfo.LOG_PERMID));
                    mPermStrategy = mCur.getInt(mCur
                            .getColumnIndex(ProviderInfo.LOG_STRATEGY));
                    mTime = mCur.getString(mCur
                            .getColumnIndex(ProviderInfo.LOG_TIME));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCur != null) {
                mCur.close();
            }
        }
        return mCur;
    }

    // deledte all the logInfo
    public void deleteLogInfo() {
        int mCount = mContext.getContentResolver().delete(
                ProviderInfo.CONTENT_URI_LOG, null, null);
        refreshLogView();
    }

    // insert into the Log_table
    public void insertLogInfo(ContentValues mCV) {
        Uri mUri = null;
        if (mCV != null) {
            mUri = mContext.getContentResolver().insert(ProviderInfo.CONTENT_URI_LOG,
                    mCV);
        }

    }

    //query the logInfo by pkgName
    public List<PermDoEntity> getlogInfoByPkgName(String pkgName) {
        List<PermDoEntity> mList = new ArrayList<PermDoEntity>();
        String[] mProjection = {ProviderInfo.LOG_PERMID, ProviderInfo.LOG_STRATEGY, ProviderInfo.LOG_TIME};
        String mSelection = ProviderInfo.LOG_PKGNAME + "=?";
        String[] mSelectionArgs = {pkgName};
        Cursor mCur = null;
        try {
            mCur = mContext.getContentResolver().query(ProviderInfo.CONTENT_URI_LOG, mProjection, mSelection, mSelectionArgs, null);
            if ((mCur != null) && (mCur.getCount() > 0)) {
                String mPermId;
                String mPermName;
                int mStrategy;
                String mTime;
                Log.d(TAG, "getlogInfoByPkgName");
                for (mCur.moveToFirst(); !mCur.isAfterLast(); mCur.moveToNext()) {
                    PermDoEntity mPermDoEntity = new PermDoEntity();

                    mPermId = mCur.getString(mCur.getColumnIndex(ProviderInfo.LOG_PERMID));
                    if (!PermType.isValidPermission(mPermId)) {
                        Log.d(TAG, "error mPermId:" + mPermId);
                        continue;
                    }
                    mStrategy = mCur.getInt(mCur.getColumnIndex(ProviderInfo.LOG_STRATEGY));
                    mTime = mCur.getString(mCur.getColumnIndex(ProviderInfo.LOG_TIME));
                    mPermName = mContext.getString(PermType.PERMNAME.get(mPermId));

                    mPermDoEntity.setmPermName(mPermName);
                    mPermDoEntity.setmDo(mStrategy);
                    mPermDoEntity.setDate(mTime);

                    mList.add(mPermDoEntity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCur != null) {
                mCur.close();
            }
        }

        return mList;
    }

    //get the pkgName distict
    public List<LogEntity> getPkgNameDistinct() {
        List<LogEntity> mListPkgName = new ArrayList<LogEntity>();
        Cursor mCur = mContext.getContentResolver().query(
                ProviderInfo.CONTENT_URI_LOG_PKGNAME_DISTINCT, new String[]{ProviderInfo.LOG_PKGNAME},
                null, null, null);
        if (mCur != null && mCur.getCount() > 0) {
            List<String> listPkgName = new ArrayList<String>();
            for (mCur.moveToFirst(); !mCur.isAfterLast(); mCur.moveToNext()) {
                String mPkgName = mCur.getString(mCur.getColumnIndex(ProviderInfo.LOG_PKGNAME));
                if (listPkgName.contains(mPkgName)) {
                    continue;
                } else {
                    listPkgName.add(mPkgName);
                    LogEntity mLogEntity = new LogEntity();

                    mLogEntity.setPackageName(mPkgName);
                    String mAppName = Util.getAppName(mContext, mPkgName);
                    mLogEntity.setAppName(mAppName);

                    List<PermDoEntity> mListPermDoEntity = new ArrayList<PermDoEntity>();
                    mListPermDoEntity = getlogInfoByPkgName(mPkgName);

                    mLogEntity.setPermDo(mListPermDoEntity);
                    mLogEntity.setAppName(mAppName);
                    mLogEntity.setPackageName(mPkgName);

                    mListPkgName.add(mLogEntity);
                }
            }
            mCur.close();
        }

        return mListPkgName;
    }
}