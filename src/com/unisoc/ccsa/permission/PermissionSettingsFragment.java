package com.unisoc.ccsa.permission;


import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.graphics.Matrix;
import android.os.Bundle;
import android.app.Fragment;
import android.os.IBinder;
import android.util.DisplayMetrics;
import com.unisoc.ccsa.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.unisoc.ccsa.R;
import com.unisoc.ccsa.permission.adapter.AppListAdapter;
import com.unisoc.ccsa.permission.adapter.ContainerViewPagerAdapter;
import com.unisoc.ccsa.permission.adapter.LogListAdapter;
import com.unisoc.ccsa.permission.adapter.PermGroupAdapter;
import com.unisoc.ccsa.permission.provider.ProviderInfo;
import com.unisoc.ccsa.permission.service.DataUpdateService;
import com.unisoc.ccsa.permission.util.AppPermInfo;
import com.unisoc.ccsa.permission.util.DoLogTable;
import com.unisoc.ccsa.permission.util.PermNameInfo;
import com.unisoc.ccsa.permission.util.SystemPropertiesProxy;
import com.unisoc.ccsa.permission.util.Util;

import java.io.File;
import java.util.ArrayList;


public class PermissionSettingsFragment extends Fragment {
    private static final String TAG = "PermissionSettings";
    private static PermissionSettingsFragment mPermissionSettingsFragment = null;
    private ColorStateList mTitleTextColorBlack;
    private ColorStateList mTitleTextColorBlue;
    private ColorStateList mTitleTextColorDark;
    private ViewPager mViewPager;
    private TextView mPermTextView, mAppTextView, mLogTextView;
    private FocusListView mPermList;
    private ListView mAppList;
    private ExpandableListView mExpandableListView;
    private View mPermView, mAppView, mLogView;
    private ArrayList<View> mViews;

    private static final int PAGE_PERM = 0;
    private static final int PAGE_APP = 1;
    private static final int PAGE_BOOT = 2;

    private ImageView mImageView;
    private int mOffset;
    private int mCurrentIndex;
    private int mTabWidth;

    public ProgressDialog mProgressDialog = null;
    private LogListAdapter mLogAdapter;
    private PermGroupAdapter mPermGroupAdapter;
    private AppListAdapter mAppListAdapter;
    public static boolean isCreateDB = false;
    private CheckBox mLogCheckBox;
    private View mView;

    private DataUpdateService.DataUpdateBinder mService = null;

    private View.OnClickListener mOnTabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.permission:
                    mCurrentIndex = PAGE_PERM;
                    break;
                case R.id.application:
                    mCurrentIndex = PAGE_APP;
                    break;
                case R.id.log:
                    mCurrentIndex = PAGE_BOOT;
                    break;
            }
            setSwitchBarTextColor();
            mViewPager.setCurrentItem(mCurrentIndex);
        }
    };

    public PermissionSettingsFragment() {
        // Required empty public constructor
    }

    public static PermissionSettingsFragment getInstance() {
        if (mPermissionSettingsFragment == null) {
            mPermissionSettingsFragment = new PermissionSettingsFragment();
        }
        return mPermissionSettingsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(getActivity(), DataUpdateService.class);
        getActivity().startService(intent);
        getActivity().bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_permisson_settings, container, false);
        initView();
        updateMonitorState(1);
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        File file = getActivity().getDatabasePath(ProviderInfo.DATABASE_NAME);
        if (file.exists()) {
            mPermGroupAdapter.getData();
            mAppListAdapter.getData();
            mLogAdapter.getLog();
        } else {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setTitle(getString(R.string.title_init_appinfo));
            mProgressDialog.setMessage(getString(R.string.init_appinfo));
            mProgressDialog.setCancelable(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.show();
            Intent intent = new Intent(getActivity(), DataUpdateService.class);
            getActivity().startService(intent);
        }
    }

    @Override
    public void onDestroy() {
        DoLogTable.releaseRefreshLog();
        getActivity().unbindService(mServiceConnection);
        super.onDestroy();

    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService = (DataUpdateService.DataUpdateBinder) iBinder;
            mService.addDataChangeListener(mOnDataChangeListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService.removeDataChangeListener(mOnDataChangeListener);
        }
    };

    private DataUpdateService.OnDataChangeListener mOnDataChangeListener = new DataUpdateService.OnDataChangeListener() {
        @Override
        public void onDataChange() {
            Log.d(TAG, "onDataChange");
            if (mPermGroupAdapter != null) {
                mPermGroupAdapter.refresh();
            }
            if (mAppListAdapter != null) {
                mAppListAdapter.refresh();
            }
            if (mLogAdapter != null) {
                mLogAdapter.refresh();
            }
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        }
    };

    private void initView() {
        mTitleTextColorBlack = getResources().getColorStateList(R.color.black);
        mTitleTextColorBlue = getResources().getColorStateList(
                R.color.title_selected_color);
        mTitleTextColorDark = getResources().getColorStateList(
                R.color.Function_off_color);
        initImageView();
        mTabWidth = mOffset * 2;
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        initPermView(layoutInflater);
        initAppView(layoutInflater);
        initLogView(layoutInflater);
        initViewPager();
        initSelectBar();
    }

    private void initSelectBar() {
        mPermTextView = mView.findViewById(R.id.permission);
        mPermTextView.setOnClickListener(mOnTabClickListener);
        mAppTextView = mView.findViewById(R.id.application);
        mAppTextView.setOnClickListener(mOnTabClickListener);
        mLogTextView = mView.findViewById(R.id.log);
        mLogTextView.setOnClickListener(mOnTabClickListener);
        setSwitchBarTextColor();
    }

    private void initViewPager() {
        mViews = new ArrayList<View>();
        mViews.add(mPermView);
        mViews.add(mAppView);
        mViews.add(mLogView);
        mViewPager = mView.findViewById(R.id.view_container);
        mViewPager.setAdapter(new ContainerViewPagerAdapter(mViews));
        mViewPager.setCurrentItem(PAGE_PERM);
        mCurrentIndex = PAGE_PERM;
        mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }

    private void initPermView(LayoutInflater layoutInflater) {
        mPermView = layoutInflater.inflate(R.layout.ps_perm_group_list, null);
        /* Modify for Keyboard featurephone. {@ */
        mPermList = (FocusListView) mPermView.findViewById(R.id.perm_list);
        /* @} */
        mPermList.setOnItemClickListener(mPermItemClickListener);
        mPermGroupAdapter = new PermGroupAdapter(getActivity(), null);
        mPermList.setAdapter(mPermGroupAdapter);

    }


    private void initLogView(LayoutInflater layoutInflater) {
        mLogView = layoutInflater.inflate(R.layout.ps_log_view, null);

        boolean isChecked = "!@#$%^&*".equals(SystemPropertiesProxy.get("persist.sys.secure.debuglog"));
        mLogCheckBox = (CheckBox) mLogView.findViewById(R.id.ck_log_on_off);
        mLogCheckBox.setChecked(isChecked);
        mLogCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean checked) {
                if (checked) {
                    SystemPropertiesProxy.set("persist.sys.secure.debuglog", "!@#$%^&*");
                } else {
                    SystemPropertiesProxy.set("persist.sys.secure.debuglog", "!");
                }
            }
        });

        mExpandableListView = (ExpandableListView) mLogView.findViewById(R.id.elv_log_view);
        mExpandableListView.setGroupIndicator(null);
        mLogAdapter = new LogListAdapter(getActivity());

        DoLogTable.setLogViewRefresh(mLogAdapter);
        mExpandableListView.setAdapter(mLogAdapter);
    }

    private void initAppView(LayoutInflater layoutInflater) {
        mAppView = layoutInflater.inflate(R.layout.ps_app_list, null);
        mAppList = (ListView) mAppView.findViewById(R.id.lv_app_list);
        mAppList.setOnItemClickListener(mAppListClickListener);
        mAppListAdapter = new AppListAdapter(getActivity(), null);
        mAppList.setAdapter(mAppListAdapter);
    }

    private void initImageView() {
        mImageView = mView.findViewById(R.id.line);
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        ViewGroup.LayoutParams lp = mImageView.getLayoutParams();
        lp.width = (screenWidth / 3);
        mImageView.setLayoutParams(lp);
        mOffset = (screenWidth / 3) / 2;
        Matrix matrix = new Matrix();
        matrix.postTranslate(mOffset, 0);
        mImageView.setImageMatrix(matrix);
    }

    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageSelected(int position) {
            // TODO Auto-generated method stub
            Animation animation = new TranslateAnimation(mCurrentIndex
                    * mTabWidth, position * mTabWidth, 0, 0);
            mCurrentIndex = position;
            animation.setFillAfter(true);
            animation.setDuration(300);
            mImageView.startAnimation(animation);
            if (mCurrentIndex == PAGE_BOOT) {
                mLogAdapter.getLog();
            }
            setSwitchBarTextColor();
        }

    }

    public void setSwitchBarTextColor() {
        switch (mCurrentIndex) {
            case PAGE_APP:
                mAppTextView.setTextColor(mTitleTextColorBlue);
                mPermTextView.setTextColor(mTitleTextColorBlack);
                mLogTextView.setTextColor(mTitleTextColorBlack);
                break;
            case PAGE_PERM:
                mAppTextView.setTextColor(mTitleTextColorBlack);
                mPermTextView.setTextColor(mTitleTextColorBlue);
                mLogTextView.setTextColor(mTitleTextColorBlack);
                break;
            case PAGE_BOOT:
                mAppTextView.setTextColor(mTitleTextColorBlack);
                mPermTextView.setTextColor(mTitleTextColorBlack);
                mLogTextView.setTextColor(mTitleTextColorBlue);
                break;
        }
    }

    private AdapterView.OnItemClickListener mPermItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            if (mPermGroupAdapter.getItemViewType(position) == PermGroupAdapter.TYPE_ITEM) {
                PermNameInfo permNameInfo = (PermNameInfo) mPermGroupAdapter.getItem(position);
                Intent intent = new Intent();
                intent.putExtra(Util.INTENT_KEY_PERMISSION_ID, permNameInfo.mPermID);
                intent.putExtra(Util.INTENT_KEY_PERMISSION_NAME,
                        permNameInfo.mPermName);
                intent.setClass(getActivity(), PermDetailActivity.class);
                startActivity(intent);
            }
        }
    };

    private AdapterView.OnItemClickListener mAppListClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Intent intent = new Intent();
            AppPermInfo.AppInfo app = (AppPermInfo.AppInfo) mAppList.getAdapter().getItem(position);
            intent.putExtra(Util.INTENT_KEY_APP_UID, app.mAppUid);
            intent.putExtra(Util.INTENT_KEY_APP_NAME, app.mAppName);
            intent.setClass(getActivity(),
                    AppPermEditorActivity.class);

            startActivity(intent);
        }
    };

    private void updateMonitorState(int state) {
        ContentValues values = new ContentValues(1);
        values.put(ProviderInfo.ControlColumns.PERMISSION, state);
        getActivity().getContentResolver().update(
                ProviderInfo.ControlColumns.CONTENT_URI, values, null, null);
    }

}
