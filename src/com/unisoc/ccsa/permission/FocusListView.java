package com.unisoc.ccsa.permission;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import com.unisoc.ccsa.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.unisoc.ccsa.permission.adapter.PermGroupAdapter;
import com.unisoc.ccsa.permission.util.IFocusListener;

public class FocusListView extends ListView {
    private final String TAG = "FocusListView";

    private ListAdapter adapter;
    private int tmpSelection = 0;

    private IFocusListener focusListener;


    public FocusListView(Context context) {
        super(context);

    }

    public FocusListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public FocusListView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public void setAdapter(ListAdapter adapter) {
        this.adapter = adapter;
        super.setAdapter(adapter);
    }

    public void setFocusListener(IFocusListener focusListener) {
        this.focusListener = focusListener;
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction,
                                  Rect previouslyFocusedRect) {
        if (gainFocus) {
            super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
            if (null != getSelectedView()) {
                int nextItemPos = getNextItemPos(tmpSelection, true);
                Log.d(TAG, "[onFocusChanged]gain Focus setSelection " + nextItemPos);
                setSelection(nextItemPos);
            }
        } else {
            tmpSelection = getSelectedItemPosition();
            Log.d(TAG, "[onFocusChanged]lose Focus at " + tmpSelection);
            super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        }
    }

    @Override
    public void setSelection(int position) {
        super.setSelection(position);
    }

    public int getNextItemPos(int fromPosition, boolean isDownDirection) {
        int itemViewType;
        int destPosition = -1;
        int i = 0;
        if (isDownDirection) {
            for (i = fromPosition; i <= adapter.getCount() - 1; i++) {
                itemViewType = adapter.getItemViewType(i);
                if (itemViewType == PermGroupAdapter.TYPE_ITEM) {
                    destPosition = i;
                    break;
                }
            }
        } else {
            for (i = fromPosition; i >= 0; i--) {
                itemViewType = adapter.getItemViewType(i);
                if (itemViewType == PermGroupAdapter.TYPE_ITEM) {
                    destPosition = i;
                    break;
                }
            }
            if (i <= -1) {
                Log.d(TAG, "[getNextItemPos]now at group1");
                focusListener.requestFocus();
            }
        }

        return destPosition;
    }

    /*
     * Resolve about item focus, such as some group item cannot focus and other content item can focus.
     *  Tips:
     *  1) if want to setSelection, you need to call super.onKeyDown 1st, and then setSelection. Otherwise it is invalid.
     *  2) getSelectedItemPosition will return current position if you call this method before super.onKeyDown,
     *     it will return next item position if call getSelectedItemPosition after super.onKeyDown.
     *  3) if call setSelection in onKeyUp, it will focus next item 1st and then change focus to your specified, maybe expect user experience.
     *     so resolve it in onKeyDown is better.
     * @see android.widget.ListView#onKeyDown(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean ret = super.onKeyDown(keyCode, event);
        View view = getSelectedView();
        int pos = getSelectedItemPosition();
        int itemViewType = adapter.getItemViewType(pos);
        if (null != view) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (itemViewType == PermGroupAdapter.TYPE_GROUP) {
                        int nextItemPos = getNextItemPos(pos, true);
                        if (nextItemPos >= 0 && nextItemPos < adapter.getCount()) {
                            setSelection(nextItemPos);
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (itemViewType == PermGroupAdapter.TYPE_GROUP) {
                        int nextItemPos = getNextItemPos(pos, false);
                        if (nextItemPos >= 0 && nextItemPos < adapter.getCount()) {
                            setSelection(nextItemPos);
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        return ret;
    }

}
