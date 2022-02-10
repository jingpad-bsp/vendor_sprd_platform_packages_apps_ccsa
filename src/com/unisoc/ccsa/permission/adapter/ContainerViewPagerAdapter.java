package com.unisoc.ccsa.permission.adapter;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

public class ContainerViewPagerAdapter extends PagerAdapter {

    private List<View> mListView;

    public ContainerViewPagerAdapter(List<View> listView) {
        mListView = listView;
    }

    @Override
    public int getCount() {
        return mListView.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mListView.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mListView.get(position));

        return mListView.get(position);
    }

}