package com.gentledevs.chatmodule.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.view.Window;

import com.gentledevs.chatmodule.R;

/**
 * Created by Yura Stetsyc on 1/19/17.
 */

public class MessageOptionsDialog extends AppCompatDialog {

    private View.OnClickListener mCopyListener;
    private View.OnClickListener mForwardListener;

    private boolean mIsCopyVisible;

    public MessageOptionsDialog(Context context, boolean isCopyVisible) {
        super(context);
        mIsCopyVisible = isCopyVisible;
    }

    public MessageOptionsDialog(Context context, int theme, boolean isCopyVisible) {
        super(context, theme);
        mIsCopyVisible = isCopyVisible;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_message_options);
        View copyTxt = findViewById(R.id.copy_txt);
        copyTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCopyListener.onClick(v);
                MessageOptionsDialog.this.dismiss();
            }
        });
        if (mIsCopyVisible) copyTxt.setVisibility(View.VISIBLE);
        else copyTxt.setVisibility(View.GONE);
        findViewById(R.id.forward_txt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mForwardListener.onClick(v);
                MessageOptionsDialog.this.dismiss();
            }
        });
    }

    public void setOnCopyListener(View.OnClickListener listener) {
        mCopyListener = listener;
    }

    public void setOnForwardListener(View.OnClickListener listener) {
        mForwardListener = listener;
    }

}
