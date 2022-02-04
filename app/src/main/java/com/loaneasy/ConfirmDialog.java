package com.loaneasy;

import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Ravindra on 03-Apr-18.
 */

public class ConfirmDialog {

    public void showDialog(AppCompatActivity activity, String msg){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_confirm);



        Button dialogButton = (Button) dialog.findViewById(R.id.btConfirmDialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}
