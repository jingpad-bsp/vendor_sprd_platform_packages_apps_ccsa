package com.unisoc.ccsa.permission.util;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.drawable.Drawable;

public class AppPermDetail {

    public String mAppName;
    public Drawable mAppIcon;

    public ArrayList<AppPermDetail> mAppPermDetails;

    private Context mContext;
    private ContentResolver mContentResolver;

    public AppPermDetail(Context context) {
        mContext = context;
        mContentResolver = context.getContentResolver();
    }

    public AppPermDetail() {

    }

    public ArrayList<AppPermDetail> getAppPermDetails() {
        ArrayList<AppPermDetail> appPermDetails = new ArrayList<AppPermDetail>();

        return appPermDetails;
    }
}