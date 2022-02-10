package com.unisoc.ccsa.permission;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.unisoc.ccsa.R;
import com.unisoc.ccsa.permission.adapter.PermDetailAdapter;
import com.unisoc.ccsa.permission.provider.ProviderInfo;
import com.unisoc.ccsa.permission.util.AppPermInfo;
import com.unisoc.ccsa.permission.util.DoLogTable;
import com.unisoc.ccsa.permission.util.Util;

public class PermDetailActivity extends Activity {

    private static final String TAG = "PermDetailActivity";

    private ListView mListView;

    private String mPermID;

    private ContentResolver mResolver;
    private String[] mPermDes;
    private String[] mPermValue;
    private PermDetailAdapter mAdapter;
    private AppPermInfo mAppPermInfo;
    //SPRD:ADD@{
    private DoLogTable mDoLog;
    //@}

    private TextView mPermName;
    private String mPermissionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ps_app_perm_list);
        //SPRD:ADD@{
        mDoLog = new DoLogTable(this);
        //@}
        Intent intent = getIntent();
        mPermID = intent.getExtras().getString(Util.INTENT_KEY_PERMISSION_ID);
        mPermissionName = intent.getStringExtra(Util.INTENT_KEY_PERMISSION_NAME);
        mResolver = getContentResolver();
        mPermDes = getResources().getStringArray(R.array.perm_des);
        mPermValue = getResources().getStringArray(R.array.perm_value);
        initView();
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.app_list);

        ImageView ivBack = (ImageView) findViewById(R.id.iv_app_perm_list_back);
        ivBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                PermDetailActivity.this.finish();
            }
        });
        mPermName = (TextView) findViewById(R.id.tv_app_perm_list_name);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAppPermInfo = new AppPermInfo(this);
        ArrayList<AppPermInfo.AppInfo> appInfos = mAppPermInfo.getAppInfoFromDB(Integer.parseInt(mPermID));
        mAdapter = new PermDetailAdapter(this, appInfos);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mDetailItemClickListener);
        mPermName.setText(mPermissionName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private OnItemClickListener mDetailItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final AppPermInfo.AppInfo appInfo = (AppPermInfo.AppInfo) mAdapter.getItem(position);
            AlertDialog.Builder builder = new AlertDialog.Builder(PermDetailActivity.this);
            builder.setItems(mPermDes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    update(appInfo.mAppUid, Integer.parseInt(mPermID), Integer.parseInt(mPermValue[which]));
                    updateListView();
                }
            });

            Dialog dialog = builder.create();
            dialog.show();

        }
    };

    private void update(int uid, int permId, int perm) {
        ContentValues values = new ContentValues(1);
        values.put(ProviderInfo.PERMISSION, perm);
        mResolver.update(ProviderInfo.CONTENT_URI, values,
                ProviderInfo.UID + "=" + uid + " and " + ProviderInfo.PERMISSIONID + "=" + permId, null);
    }

    private void updateListView() {
        ArrayList<AppPermInfo.AppInfo> appInfos = mAppPermInfo.getAppInfoFromDB(Integer.parseInt(mPermID));
        mAdapter = new PermDetailAdapter(this, appInfos);
        mListView.setAdapter(mAdapter);
    }
}