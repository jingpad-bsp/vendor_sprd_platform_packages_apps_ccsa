package com.unisoc.ccsa.permission.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.unisoc.ccsa.R;
import com.unisoc.ccsa.permission.util.AppPermInfo;

import java.util.ArrayList;


public class PermDetailAdapter extends BaseAdapter {

    private ArrayList<AppPermInfo.AppInfo> mAppInfos;
    private Context mContext;

    public PermDetailAdapter(Context context, ArrayList<AppPermInfo.AppInfo> appPermInfos) {
        mContext = context;
        mAppInfos = appPermInfos;
    }

    @Override
    public int getCount() {
        return mAppInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return mAppInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.ps_list_item, null);

            viewHolder.appIcon = (ImageView) convertView.findViewById(R.id.app_icon);
            viewHolder.appName = (TextView) convertView.findViewById(R.id.title);

            viewHolder.allowText = (TextView) convertView.findViewById(R.id.app_perm);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.appIcon.setBackgroundDrawable(mAppInfos.get(position).mAppIcon);
        viewHolder.appName.setText(mAppInfos.get(position).mAppName);


        if (mAppInfos.get(position).mAllow == 0) {
            viewHolder.allowText.setText(mContext.getString(R.string.allow));
        } else if (mAppInfos.get(position).mAllow == 1) {
            viewHolder.allowText.setText(mContext.getString(R.string.deny));
        } else if (mAppInfos.get(position).mAllow == 2) {
            viewHolder.allowText.setText(mContext.getString(R.string.tip));
        }

        return convertView;
    }

    static class ViewHolder {
        ImageView appIcon;
        TextView appName;
        TextView appSummary;
        TextView allowText;
    }

}