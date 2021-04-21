package com.neucore.neusdk_demo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.neucore.neusdk_demo.R;


public class TransferAdminsDialog extends Dialog {

    private CheckButtonCancelOnclick buttonCancleListener;
    private CheckButtonCancelVerOnclick buttonCancleVerListener;
    private CheckButtonSureOnclick buttonSureListener;

    public TransferAdminsDialog(Context context) {
        super(context);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.dialog_transfer_admins);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView dialog_disconnect_6421_ver = (TextView) findViewById(R.id.dialog_disconnect_6421_ver);
        TextView dialog_disconnect_cancel = (TextView) findViewById(R.id.dialog_disconnect_cancel);
        TextView dialog_disconnect_sure = (TextView) findViewById(R.id.dialog_disconnect_sure);
        dialog_disconnect_6421_ver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonCancleVerListener.onClick(v);
            }
        });
        dialog_disconnect_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonCancleListener.onClick(v);
            }
        });

        dialog_disconnect_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSureListener.onClick(v);
            }
        });

    }

    //确定回调
    public interface CheckButtonCancelVerOnclick {
        void onClick(View view);
    }

    public void setButtonCancelVerOnClick(CheckButtonCancelVerOnclick buttonCancleVerListener) {
        this.buttonCancleVerListener = buttonCancleVerListener;
    }

    //确定回调
    public interface CheckButtonCancelOnclick {
        void onClick(View view);
    }

    public void setButtonCancelOnClick(CheckButtonCancelOnclick buttonCancleListener) {
        this.buttonCancleListener = buttonCancleListener;
    }

    public interface CheckButtonSureOnclick {
        void onClick(View view);
    }

    public void setButtonSureOnClick(CheckButtonSureOnclick buttonSureListener) {
        this.buttonSureListener = buttonSureListener;
    }


    public void finish() {
        dismiss();
    }





}
