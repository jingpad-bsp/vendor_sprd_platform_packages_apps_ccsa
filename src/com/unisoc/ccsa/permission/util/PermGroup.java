package com.unisoc.ccsa.permission.util;

import java.util.ArrayList;

public class PermGroup {
    public String mPermGroupNanme;

    public ArrayList<PermNameInfo> mPermGroupItem = new ArrayList<PermNameInfo>();

    public PermGroup(String permGroupName) {
        mPermGroupNanme = permGroupName;
    }

    public String getPermGroupName() {
        return mPermGroupNanme;
    }

    public void addPermItem(PermNameInfo permItem) {
        mPermGroupItem.add(permItem);
    }

    public String getPermItem(int position) {
        if (position == 0) {
            return mPermGroupNanme;
        } else {
            return mPermGroupItem.get(position - 1).mPermName;
        }
    }

    public Object getItem(int position) {
        if (position == 0) {
            return mPermGroupNanme;
        } else {
            return mPermGroupItem.get(position - 1);
        }
    }

    public PermNameInfo getPermNameInfo(int position) {
        return mPermGroupItem.get(position - 1);
    }

    public int getPermItemCount() {
        return mPermGroupItem.size() + 1;
    }

}
