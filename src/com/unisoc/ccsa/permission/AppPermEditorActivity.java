package com.unisoc.ccsa.permission;


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
import android.widget.TextView;

import com.unisoc.ccsa.R;
import com.unisoc.ccsa.permission.adapter.AppPermEditorAdapter;
import com.unisoc.ccsa.permission.provider.ProviderInfo;
import com.unisoc.ccsa.permission.util.DoLogTable;
import com.unisoc.ccsa.permission.util.IFocusListener;
import com.unisoc.ccsa.permission.util.PermGroup;
import com.unisoc.ccsa.permission.util.PermNameInfo;
import com.unisoc.ccsa.permission.util.Util;

import java.util.ArrayList;


/* Modify for Keyboard featurephone. {@ */
public class AppPermEditorActivity extends Activity implements IFocusListener {
    /* @} */

    private int mUid;

    /* Modify for Keyboard featurephone. {@ */
    private FocusListView mListView;
    private ImageView mIvBack;
    /* @} */

    private String[] mPermDes;
    private String[] mPermValue;

    private ContentResolver mResolver;

    private PermNameInfo mPermNameInfo;
    private AppPermEditorAdapter mAdapter;

    //SPRD:ADD@{
    private DoLogTable mDoLog;
    //@}

    private TextView mAppName;
    private String mName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ps_app_perm_editor);
        Intent intent = getIntent();

        mUid = intent.getExtras().getInt(Util.INTENT_KEY_APP_UID);
        mName = intent.getStringExtra(Util.INTENT_KEY_APP_NAME);

        mPermDes = getResources().getStringArray(R.array.perm_des);
        mPermValue = getResources().getStringArray(R.array.perm_value);
        mResolver = getContentResolver();
        //SPRD:ADD@{
        mDoLog = new DoLogTable(AppPermEditorActivity.this);
        //@}

        initView();
    }

    private void initView() {
        mListView = (FocusListView) findViewById(R.id.perm_list);
        mAppName = (TextView) findViewById(R.id.tv_app_perm_editor_appName);
        mIvBack = (ImageView) findViewById(R.id.iv_app_perm_editor_back);
        mIvBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                AppPermEditorActivity.this.finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        mPermNameInfo = new PermNameInfo(this);

        ArrayList<PermGroup> permGroups = mPermNameInfo.getAppPermGroup(mUid);
        mAdapter = new AppPermEditorAdapter(this, permGroups);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mEditorClickListener);
        /* Modify for Keyboard featurephone. {@ */
        mListView.setFocusListener(this);
        /* @} */
        mAppName.setText(mName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private OnItemClickListener mEditorClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mAdapter.getItemViewType(position) == AppPermEditorAdapter.TYPE_ITEM) {
                final PermNameInfo permInfo = (PermNameInfo) mAdapter.getItem(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(AppPermEditorActivity.this);
                builder.setItems(mPermDes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        update(permInfo.mAppUid,
                                Integer.parseInt(permInfo.mPermID),
                                Integer.parseInt(mPermValue[which]));
                        updateListView();
                    }
                });
                Dialog dialog = builder.create();
                dialog.show();
            }
        }

    };

    private void update(int uid, int permId, int perm) {
        ContentValues values = new ContentValues(1);
        values.put(ProviderInfo.PERMISSION, perm);
        mResolver.update(ProviderInfo.CONTENT_URI, values, ProviderInfo.UID + "=" + uid + " and " +
                ProviderInfo.PERMISSIONID + "=" + permId, null);
    }

    private void updateListView() {
        ArrayList<PermGroup> permGroups = mPermNameInfo.getAppPermGroup(mUid);
        mListView.setAdapter(new AppPermEditorAdapter(this, permGroups));
    }

    /* Modify for Keyboard featurephone. {@ */
    @Override
    public void requestFocus() {
        mIvBack.requestFocus();
    }
    /* @} */
}