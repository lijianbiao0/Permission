package com.hofo.permission;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;


public class DeleteDialog extends Dialog {
    private Context mContext;
    private int[] mListenerId;
    private View mContentView;
    private View.OnClickListener mOnClickListener;

    public DeleteDialog(Context context, int... listenerId) {
        super(context, R.style.PermissionDialogStyle);
        mContext = context;
        mListenerId = listenerId;
        mContentView = View.inflate(mContext, R.layout.dialog_delete, null);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setGravity(Gravity.CENTER);
        initClickListener();
        //点击外面和返回都不能取消对话框
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        setContentView(mContentView);
    }

    /**
     * 初始化单击事件监听器
     * 当单击事件触发后对话框会消失
     */
    private void initClickListener() {
        if (mListenerId != null) {
            for (int id : mListenerId) {
                mContentView.findViewById(id).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                        if (mOnClickListener != null) {
                            mOnClickListener.onClick(v);
                        }
                    }
                });
            }
        }
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public void setText(int id, String msg) {
        View view = mContentView.findViewById(id);
        if (view instanceof TextView) {
            ((TextView) view).setText(msg);
        }
    }
}
