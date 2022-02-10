package com.unisoc.ccsa.permission.util;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;

public class LogEntity {
    private Drawable mIcon;
    private String mAppName;
    private String mPackageName;
    private List<PermDoEntity> mPermDo;

    public String getmAppName() {
        return mAppName;
    }

    public void setAppName(String mAppName) {
        this.mAppName = mAppName;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public void setPackageName(String mPackageName) {
        this.mPackageName = mPackageName;
    }

    public List<PermDoEntity> getPermDo() {
        if (mPermDo != null) {
            return mPermDo;
        }
        return new ArrayList<PermDoEntity>();
    }

    public void setPermDo(List<PermDoEntity> mPermDo) {
        this.mPermDo = mPermDo;
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public void setIcon(Drawable mIcon) {
        this.mIcon = mIcon;
    }

    public int getLogCount() {
        if (mPermDo != null) {
            return mPermDo.size();
        }
        return 0;
    }

}
