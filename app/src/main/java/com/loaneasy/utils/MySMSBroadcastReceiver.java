package com.loaneasy.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ravindra on 07-Jan-19.
 */
public class MySMSBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

             Log.i("rsr","="+status.getStatusCode());
            switch(status.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    // Get SMS message contents

                    String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);

                    Pattern pattern = Pattern.compile("(\\d{5})");

                    //   \d is for a digit
                    //   {} is the number of digits here 4.
                    Matcher matcher = pattern.matcher(message);
                    String val = "";
                    if (matcher.find()) {
                        val = matcher.group(1);
                        Log.i("OTP","="+val);


                       /* Intent otpIntent = new Intent(context, VerifyOtp.class);
                        otpIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        otpIntent.putExtra("otp",val);
                        context.startActivity(otpIntent);*/


                        Intent i = new Intent("MySMSBroadcastReceiver");
                        i.putExtra("otp", val);
                        context.sendBroadcast(i);

                    }



                    // Extract one-time code from the message and complete verification
                    // by sending the code back to your server.
                    break;
                case CommonStatusCodes.TIMEOUT:
                    // Waiting for SMS timed out (5 minutes)
                    //Toast.makeText(context, "Waiting for SMS timed out", Toast.LENGTH_SHORT).show();
                    break;


            }
        }




    }




}