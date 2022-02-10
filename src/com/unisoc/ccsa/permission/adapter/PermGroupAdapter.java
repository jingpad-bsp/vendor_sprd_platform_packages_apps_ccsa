package com.unisoc.ccsa.permission.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.unisoc.ccsa.R;
import com.unisoc.ccsa.permission.util.PermGroup;
import com.unisoc.ccsa.permission.util.PermNameInfo;

import java.util.ArrayList;


public class PermGroupAdapter extends BaseAdapter implements IrefreshView {

    private LayoutInflater mLayoutInflater;
    private Context mContext;

    private ArrayList<PermGroup> mPermGroups;

    public static final int TYPE_COUNT = 2; //TYPE_GROUP TYPE_ITEM
    public static final int TYPE_GROUP = 0;
    public static final int TYPE_ITEM = 1;

    public PermGroupAdapter(Context context, ArrayList<PermGroup> permGroups) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        if (permGroups == null) {
            mPermGroups = new ArrayList<PermGroup>();
        } else {
            mPermGroups = permGroups;
        }
    }

    @Override
    public int getCount() {
        int count = 0;
        if (mPermGroups != null) {
            for (PermGroup group : mPermGroups) {
                count += group.getPermItemCount();
            }
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        if (mPermGroups == null || position < 0 || position > getCount()) {
            return null;
        }

        int categroyFirstIndex = 0;

        for (PermGroup group : mPermGroups) {
            int size = group.getPermItemCount();
            int categoryIndex = position - categroyFirstIndex;
            if (categoryIndex < size) {
                return group.getItem(categoryIndex);
            }

            categroyFirstIndex += size;
        }
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        if (mPermGroups == null || position < 0 || position > getCount()) {
            return TYPE_ITEM;
        }

        int categroyFirstIndex = 0;

        for (PermGroup group : mPermGroups) {
            int size = group.getPermItemCount();
            int categoryIndex = position - categroyFirstIndex;
            if (categoryIndex == 0) {
                return TYPE_GROUP;
            }

            categroyFirstIndex += size;
        }

        return TYPE_ITEM;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int itemViewType = getItemViewType(position);
        ViewHolder viewHolder;
        switch (itemViewType) {
            case TYPE_GROUP:
                if (convertView == null) {
                    convertView = mLayoutInflater.inflate(
                            R.layout.ps_perm_group_header, null);

                }
                TextView permGroup = (TextView) convertView
                        .findViewById(R.id.group_header);
                permGroup.setText((String) getItem(position));
                break;
            case TYPE_ITEM:
                if (convertView == null) {
                    viewHolder = new ViewHolder();
                    convertView = mLayoutInflater.inflate(
                            R.layout.ps_perm_group_list_item, null);

                    viewHolder.mPermName = (TextView) convertView
                            .findViewById(R.id.perm_name);
                    viewHolder.mPermCount = (TextView) convertView
                            .findViewById(R.id.perm_name_summary);

                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }

                viewHolder.mPermName
                        .setText(((PermNameInfo) getItem(position)).mPermName);
                /*
                 * if(!ifMonitored()){
                 * viewHolder.mPermName.setTextColor(R.color.gray); }
                 */
                String count = mContext.getString(R.string.perm_des,
                        ((PermNameInfo) getItem(position)).mPermCount);
                if (count != null) {
                    viewHolder.mPermCount.setText(count);
                }
                break;
        }

        return convertView;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) != TYPE_GROUP;
    }

    static class ViewHolder {
        TextView mPermName;
        TextView mPermCount;
    }

    @Override
    public void refresh() {
        mPermGroups = new PermNameInfo(mContext).getPermGroups();
        notifyDataSetChanged();
    }

    public void getData() {
        if (mPermGroups == null || mPermGroups.size() == 0) {
            mPermGroups = new PermNameInfo(mContext).getPermGroups();
            notifyDataSetChanged();
        }
    }

    public void clearData() {
        if (mPermGroups != null)
            mPermGroups.clear();
    }
}