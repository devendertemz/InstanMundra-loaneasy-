package com.loaneasy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.loaneasy.DrawerItems.PrivacyPolicy;
import com.loaneasy.NewAuthentication.LoginActivity;
import com.loaneasy.login_signup.PhoneNoActivity;
import com.loaneasy.login_signup.SocialMediaLogin;
import com.loaneasy.new_user_details.UserProfile;
import com.loaneasy.utils.PrefManager;
import com.loaneasy.utils.UserSharedPreference;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import java.io.IOException;


public class Splash extends AppCompatActivity {

    private PrefManager prefManager;
    int SPLASH_LENGTH = 3000;
    UserSharedPreference sharedPreference;
    private int DAYS_FOR_FLEXIBLE_UPDATE = 15;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        //FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        sharedPreference = new UserSharedPreference(this);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        forceUpdate();
        runSplash();


      /*  AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());

        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();*/



    }


    public void runSplash(){

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                prefManager = new PrefManager(getApplicationContext());
                Toast.makeText(Splash.this, sharedPreference.getSignFlag() +"", Toast.LENGTH_SHORT).show();

                if (prefManager.isFirstTimeLaunch()) {
                    Intent privacyActivity = new Intent(getApplicationContext(), PrivacyPolicy.class);
                    privacyActivity.putExtra("termsButton", true);
                    startActivity(privacyActivity);
                    finish();
                }


                else {

                    if (sharedPreference.getSignFlag() == 1){
                        startActivity(new Intent(getApplicationContext(), SocialMediaLogin.class));
                        finish();
                    }
                    else if (sharedPreference.getSignFlag() == 2){
                        startActivity(new Intent(getApplicationContext(), UserProfile.class));
                        finish();
                    }
                    else if (sharedPreference.getSignFlag() == 3){
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        finish();
                    }
                    else {

                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
/*
                        startActivity(new Intent(getApplicationContext(), PhoneNoActivity.class));*/
                        finish();
                    }


                  /*  startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    finish();*/




                   /* Intent intent = new Intent(Splash.this, DoneActivity.class);
                    intent.putExtra("loanAmount", "5000");
                    intent.putExtra("payableAmount", "5150");
                    //intent.putExtra("days_returning", returnDays);
                    intent.putExtra("days_returning", "30");
                    intent.putExtra("orderid", "IM-PL-54542");
                    intent.putExtra("repayAmount","5150" );
                    startActivity(intent);
                    finish();*/




                }


            }
        }, SPLASH_LENGTH);
    }


    public void forceUpdate(){
        PackageManager packageManager = this.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo =  packageManager.getPackageInfo(getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String currentVersion = packageInfo.versionName;
        Log.i("current_version","---"+currentVersion);
        new ForceUpdateAsync(currentVersion,Splash.this).execute();
    }



    @SuppressLint("StaticFieldLeak")
    public class ForceUpdateAsync extends AsyncTask<String, String, JSONObject>{

        private String latestVersion;
        private String currentVersion;
        private Context context;

        public ForceUpdateAsync(String currentVersion, Context context){
            this.currentVersion = currentVersion;
            this.context = context;
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            try {
                latestVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + context.getPackageName()+ "&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div.hAyfc:nth-child(4) > span:nth-child(2) > div:nth-child(1) > span:nth-child(1)")
                        .first()
                        .ownText();
                Log.i("latest_version","---"+latestVersion);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return new JSONObject();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if(latestVersion!=null){
                if(!currentVersion.equalsIgnoreCase(latestVersion)){
                     //Toast.makeText(context,"update is available.",Toast.LENGTH_LONG).show();

                    showForceUpdateDialog();

                    /*if(!(context instanceof Splash)) {
                        if(!((Activity)context).isFinishing()){
                            showForceUpdateDialog();
                        }
                    }*/
                }
                else
                {
                      runSplash();
                }
            }
            super.onPostExecute(jsonObject);
        }


        private void showForceUpdateDialog(){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(context,
                    R.style.AlertDialogCustom));

            alertDialogBuilder.setTitle(context.getString(R.string.youAreNotUpdatedTitle));
            alertDialogBuilder.setMessage(context.getString(R.string.youAreNotUpdatedMessage) + " " + latestVersion + context.getString(R.string.youAreNotUpdatedMessage1));
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName())));
                    dialog.cancel();
                }
            });
            alertDialogBuilder.show();
        }
    }


}
