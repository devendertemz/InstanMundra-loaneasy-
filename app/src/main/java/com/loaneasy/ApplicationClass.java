package com.loaneasy;

import android.app.Application;

import com.msg91.sendotpandroid.library.internal.SendOTP;

public class ApplicationClass extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SendOTP.initializeApp(this,"256032ACgwQJWWBewM5c3723a1", "DLT_TE_ID");        //initialization
    }
}
