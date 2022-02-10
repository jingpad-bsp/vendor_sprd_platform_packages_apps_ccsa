package com.unisoc.ccsa.permission;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.unisoc.ccsa.R;

public class SwitchDialog extends Dialog {
    private TextView tvBtnYes, tvBtnCancel, tvTitle, tvContent;
    private RelativeLayout mContainer;
    private View mView;

    public SwitchDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        setContentView(R.layout.ps_switch_dialog);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mContainer = (RelativeLayout) this.findViewById(R.id.rl_container);
        tvBtnYes = (TextView) this.findViewById(R.id.tv_btn_yes);
        tvBtnCancel = (TextView) this.findViewById(R.id.tv_btn_cancel);
        tvTitle = (TextView) this.findViewById(R.id.tv_switch_dialog_title);
        tvContent = (TextView) this.findViewById(R.id.tv_switch_dialog_content);
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);
    }

    public void setPositiveButtonListener(View.OnClickListener listener) {
        if (tvBtnYes == null) {
            tvBtnYes = (TextView) this.findViewById(R.id.tv_btn_yes);
        }
        if (listener != null) {
            tvBtnYes.setOnClickListener(listener);
        } else {
            tvBtnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    SwitchDialog.this.dismiss();
                }
            });
        }
    }

    public void setNegativeButtonListener(View.OnClickListener listener) {
        if (tvBtnCancel == null) {
            tvBtnCancel = (TextView) this.findViewById(R.id.tv_btn_cancel);
        }
        if (listener != null) {
            tvBtnCancel.setOnClickListener(listener);
        } else {
            tvBtnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    SwitchDialog.this.dismiss();
                }
            });
        }
    }

    public void setTitle(String title) {
        if (tvTitle == null) {
            tvTitle = (TextView) this.findViewById(R.id.tv_switch_dialog_title);
        }
        tvTitle.setText(title);
    }

    public void setContent(String content) {
        if (tvContent == null) {
            tvContent = (TextView) this.findViewById(R.id.tv_switch_dialog_content);
        }
        tvContent.setText(content);
    }

    public void setPositiveButtionText(String s) {
        if (tvBtnYes == null) {
            tvBtnYes = (TextView) this.findViewById(R.id.tv_btn_yes);
        }
        tvBtnYes.setText(s);
    }

    public void setNegativeButtonText(String s) {
        if (tvBtnCancel == null) {
            tvBtnCancel = (TextView) this.findViewById(R.id.tv_btn_cancel);
        }
        tvBtnCancel.setText(s);
    }

    public void setDialogheightSize(int size) {
        if (mContainer == null) {
            mContainer = (RelativeLayout) findViewById(R.id.rl_container);
        }
        LayoutParams params = mContainer.getLayoutParams();
        params.height = size;
        mContainer.setLayoutParams(params);
    }

    public View getView() {
        if (mView == null) {
            if (tvContent == null) {
                tvContent = (TextView) this.findViewById(R.id.tv_switch_dialog_content);
            }
            return tvContent;
        }
        return mView;
    }

    public void setView(View v) {
        if (v != null) {
            if (mContainer == null) {
                mContainer = (RelativeLayout) findViewById(R.id.rl_container);
            }
            tvContent.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.BELOW, R.id.iv_line);
            mContainer.addView(v, params);
            mView = v;
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
    }
}
