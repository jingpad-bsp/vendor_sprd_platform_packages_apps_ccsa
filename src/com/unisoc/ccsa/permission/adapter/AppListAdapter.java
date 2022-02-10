package com.unisoc.ccsa.permission.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.unisoc.ccsa.Log;
import com.unisoc.ccsa.R;
import com.unisoc.ccsa.permission.util.AppPermInfo;

import java.util.ArrayList;


public class AppListAdapter extends BaseAdapter implements IrefreshView {

    private static final String TAG = "AppListAdapter";
    private Context mContext;
    private ArrayList<AppPermInfo.AppInfo> mAppInfos;

    public AppListAdapter(Context context, ArrayList<AppPermInfo.AppInfo> appInfos) {
        mContext = context;
        if (appInfos == null) {
            mAppInfos = new ArrayList<AppPermInfo.AppInfo>();
        } else {
            mAppInfos = appInfos;
        }

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
    public void refresh() {
        mAppInfos = new AppPermInfo(mContext).getAppInfo();
        notifyDataSetChanged();
        Log.d(TAG, "AppListAdapter->refresh:");
    }

    public void getData() {
        if (mAppInfos == null || mAppInfos.size() == 0) {
            mAppInfos = new AppPermInfo(mContext).getAppInfo();
        }
        notifyDataSetChanged();
    }

    public void clearDate() {
        if (mAppInfos != null)
            mAppInfos.clear();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.ps_app_list_item, null);
            viewHolder.appIcon = (ImageView) convertView
                    .findViewById(R.id.app_icon);
            viewHolder.appTitle = (TextView) convertView
                    .findViewById(R.id.app_title);
            viewHolder.appComment = (TextView) convertView
                    .findViewById(R.id.app_comment);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.appIcon
                .setBackgroundDrawable(mAppInfos.get(position).mAppIcon);
        viewHolder.appTitle.setText(mAppInfos.get(position).mAppName);
        String format = mContext.getString(R.string.app_perm_format);
        viewHolder.appComment.setText(String.format(format,
                mAppInfos.get(position).mAppPermID.size()));

        return convertView;
    }

    static class ViewHolder {
        ImageView appIcon;
        TextView appTitle;
        TextView appComment;
        ImageView appDetail;
    }
}
