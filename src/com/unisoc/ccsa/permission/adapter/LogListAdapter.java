package com.unisoc.ccsa.permission.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.unisoc.ccsa.Log;
import com.unisoc.ccsa.R;
import com.unisoc.ccsa.permission.util.DoLogTable;
import com.unisoc.ccsa.permission.util.LogEntity;
import com.unisoc.ccsa.permission.util.PermDoEntity;
import com.unisoc.ccsa.permission.util.Util;

public class LogListAdapter extends BaseExpandableListAdapter implements IrefreshView {
    private static final String TAG = "LogListAdapter";
    private LayoutInflater mInflater;
    private Context mContext;
    private List<LogEntity> mLogList;
    private Resources mResources;

    public static final int DATE = 0;
    public static final int TIME = 1;

    public LogListAdapter(Context context, List<LogEntity> list) {
        this.mInflater = LayoutInflater.from(context);
        this.mLogList = list;
        this.mContext = context;
        this.mResources = context.getResources();
    }

    public LogListAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mResources = context.getResources();
    }

    @Override
    public PermDoEntity getChild(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        if (mLogList != null) {
            return mLogList.get(groupPosition).getPermDo().get(childPosition);
        }
        return null;
    }

    //////////////////the follow is child////////////////
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean arg2, View convertView,
                             ViewGroup group) {
        // TODO Auto-generated method stub
        ChildHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.ps_log_content_item, null);
            holder = new ChildHolder();
            holder.date = (TextView) convertView.findViewById(R.id.tv_log_content_item_date);
            holder.time = (TextView) convertView.findViewById(R.id.tv_log_content_item_time);
            holder.event = (TextView) convertView.findViewById(R.id.tv_log_content_item_do);
            convertView.setTag(holder);
        } else {
            holder = (ChildHolder) convertView.getTag();
        }
        PermDoEntity child = getChild(groupPosition, childPosition);
        holder.date.setText(transformDate(child.getDate(), DATE));
        holder.time.setText(transformDate(child.getDate(), TIME));
        switch (child.getmDo()) {
            case Util.PERM_STATUS_ALLOW:
                holder.event.setText(String.format(mContext.getString(R.string.choose_refuse), child.getPermName()));
                break;
            case Util.PERM_STATUS_REFUSE:
                holder.event.setText(String.format(mContext.getString(R.string.choose_allow), child.getPermName()));
                break;
            case Util.PERM_STATUS_TIP:
                holder.event.setText(String.format(mContext.getString(R.string.choose_tip), child.getPermName()));
                break;
            default:
                Log.w(TAG,"invalid status");
                break;
        }
        return convertView;
    }

    class ChildHolder {
        TextView date, time, event;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // TODO Auto-generated method stub
        if (mLogList != null) {
            return mLogList.get(groupPosition).getPermDo().size();
        }
        return 0;
    }

    ////////////////////////the follow is group////////////////////////
    @Override
    public LogEntity getGroup(int groupPosition) {
        // TODO Auto-generated method stub
        if (mLogList != null) {
            return mLogList.get(groupPosition);
        }
        return null;
    }

    @Override
    public int getGroupCount() {
        // TODO Auto-generated method stub
        if (mLogList != null) {
            return mLogList.size();
        }
        return 0;
    }

    @Override
    public long getGroupId(int groupPosition) {
        // TODO Auto-generated method stub
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup arg3) {
        // TODO Auto-generated method stub
        GroupHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.ps_log_app_item, null);
            holder = new GroupHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.iv_log_app_item_appIcon);
            holder.appName = (TextView) convertView.findViewById(R.id.tv_log_app_item_appName);
            holder.arrow = (ImageView) convertView.findViewById(R.id.iv_log_app_item_arrow);
            convertView.setTag(holder);
        } else {
            holder = (GroupHolder) convertView.getTag();
        }
        LogEntity group = getGroup(groupPosition);
        Drawable icon = group.getIcon();
        if (icon == null) {
            icon = Util.getAppIcon(mContext, group.getPackageName());
            group.setIcon(icon);
        }
        holder.icon.setImageDrawable(icon);
        holder.appName.setText(group.getmAppName());
        if (isExpanded) {
            holder.arrow.setImageResource(R.drawable.arrow_down);
        } else {
            holder.arrow.setImageResource(R.drawable.arrow);
        }
        return convertView;
    }

    class GroupHolder {
        ImageView icon, arrow;
        TextView appName;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void refresh() {
        // TODO Auto-generated method stub
        //getLogList
        getLog();
    }

    public void getLog() {
        new LogTask().execute();
    }

    private class LogTask extends AsyncTask<Void, Void, List<LogEntity>> {

        @Override
        protected List<LogEntity> doInBackground(Void... voids) {
            List<LogEntity> list = new ArrayList<LogEntity>();
            DoLogTable mDoLog = new DoLogTable(mContext);
            list = mDoLog.getPkgNameDistinct();
            if (list != null && list.size() > 0) {
                Log.d(TAG, "[LogListAdapter] " + list.get(0).getPackageName() + " " + list.get(0).getmAppName());
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<LogEntity> result) {
            super.onPostExecute(result);
            mLogList = result;
            notifyDataSetChanged();
        }
    }

    private String transformDate(String date, int type) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date time = format.parse(date);
            switch (type) {
                case DATE:
                    SimpleDateFormat formateDate = new SimpleDateFormat("MM-dd");
                    String currentDate = formateDate.format(new Date());
                    String returnDate = formateDate.format(time);
                    if (currentDate.equals(returnDate)) {
                        return mContext.getString(R.string.today);
                    }
                    return returnDate;
                case TIME:
                    SimpleDateFormat formateTime = new SimpleDateFormat("HH:mm");
                    return formateTime.format(time);
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return mContext.getString(R.string.wrong_date);
        }

        return mContext.getString(R.string.wrong_date);
    }
}
