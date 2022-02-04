package com.loaneasy;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.CampaignTrackingReceiver;
import com.google.android.gms.analytics.Tracker;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.loaneasy.Drawer.FragmentDrawer;
import com.loaneasy.DrawerItems.ActiveLoansActivity;
import com.loaneasy.DrawerItems.AppliedLoansActivity;
import com.loaneasy.DrawerItems.ContactUsActivity;
import com.loaneasy.DrawerItems.FAQActivity;
import com.loaneasy.DrawerItems.PrivacyPolicy;
import com.loaneasy.DrawerItems.ProfileActivity;
import com.loaneasy.DrawerItems.RefundPolicy;
import com.loaneasy.DrawerItems.TermsConditionsActivity;
import com.loaneasy.Home.ApplyForLoan;
import com.loaneasy.Home.CurrentLoan;
import com.loaneasy.Home.LoanStatus;
import com.loaneasy.NewAuthentication.LoginActivity;
import com.loaneasy.login_signup.PhoneNoActivity;
import com.loaneasy.network.ApiRequest;
import com.loaneasy.utils.ConnectionCheck;
import com.loaneasy.utils.UserSharedPreference;
import com.loaneasy.utils.Utility;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;



public class HomeActivity extends AppCompatActivity implements
        FragmentDrawer.FragmentDrawerListener, NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener {

    FragmentDrawer drawerFragment;
    private Toolbar mToolbar;
    String[] PERMISSIONS = { Manifest.permission.READ_CONTACTS,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    int PERMISSION_ALL = 1;

    //public SeekBar seekbarAmount, seekbarDays;
    public TextView setDays, rePaymentDate, userName, userCity, textLoan, textAmount, tvAmountSelected,
            tvDays, tvOkDialLoan, tvTotalInterest, rcTotalInterest, rcProcFee, rcGst, rcDisbAmount, rcRepayAmount,
            tvLoanEligibility, tvOkDialDoc, tvCancelDialDoc,tvPlatfromCharges;

    //private Button btSubmitAmnt, eNash;

    LinearLayout lyCalEmi1, lyCalEmi2, lyReceipt;
    String retuningDate, sAccType = "", sBankName = "", sSalarySlip1 = "", sSalarySlip2 = "", sSalarySlip3 = "",
            sBankStatement = "", sAccnNo = "", sIfsc = "",TAG="HomeActivity", loanId;
    ImageView iv_userProfile;

    Double mEmiRatePerMonth = 0.03;
    int nLoanEligAmount = -1;

    UserSharedPreference sharedPreference;
    DecimalFormat formatter;
    private DrawerLayout mDrawerLayout;
    private View containerView;
    TypedFile typedFileBankStatmnt, typedFileSalarySlip1, typedFileSalarySlip2, typedFileSalarySlip3;
    Dialog dialog, dialogDoc;
    Boolean isAllowed = true;

    private double platformCharges;


    private Button getStatus;
    private  Dialog dialogUploadDocument;
    private TextView tvOk, tvCancel;
    private boolean flag = false;
    private Tracker mTracker;
    private FrameLayout enashLayout;
    private final Executor backgroundExecutor = Executors.newSingleThreadExecutor();
    private final String prefKey = "checkedInstallReferrer";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sharedPreference = new UserSharedPreference(this);

        //eNash = findViewById(R.id.btENash);

        //enashLayout = findViewById(R.id.layout_enash);

        //getUserCurrentStatus();


        //getENashRequest();

        //getRatingDialog();

        //checkInstallReferrer();

      /*  LoanStatus frg = new LoanStatus();
        FragmentManager fm = this.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.homeFragment, frg);
        ft.commit();*/

        //GoogleAnalytics.getInstance(this).setDryRun(true);
        //forceUpdate();
        //AnalyticsSampleApp application = (AnalyticsSampleApp) getApplication();
        //mTracker = application.getDefaultTracker();
        //mTracker.setScreenName("Home Activity");


      /*  String campaignData = "https://play.google.com/store/apps/details?id=com.loaneasy&referrer=utm_source%3Dcashtap%26utm_medium%3Dapp%26utm_term%3Dloan%26utm_content%3Dinstall_reffer%26utm_campaign%3Dimudra";
        mTracker.send(new HitBuilders.ScreenViewBuilder()
                .setCampaignParamsFromUrl(campaignData)
                .build()
        );*/


        mDrawerLayout = findViewById(R.id.drawer_layout);
        containerView = findViewById(R.id.fragment_navigation_drawer);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Dashboard");

        //dialogDoc = new Dialog(HomeActivity.this);

        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        //iv_userProfile = findViewById(R.id.iv_userProfile);

        userName = findViewById(R.id.tvUserNameHome);
        userCity = findViewById(R.id.tvUserCity);

        //btSubmitAmnt = findViewById(R.id.btSubmitAmnt);
       /* setDays = findViewById(R.id.tvSetDays);
        rePaymentDate = findViewById(R.id.tvRePayment);
        textLoan = findViewById(R.id.tvCalculateLoanText);
        textAmount = findViewById(R.id.tvTextSelectAmount);
        tvAmountSelected = findViewById(R.id.tvAmountSelected);
        tvDays = findViewById(R.id.tvDays);
        tvLoanEligibility = findViewById(R.id.tvLoanEligibility);*/

        // tvTotalInterest = findViewById(R.id.tvTotalInterest);*/

        //lyReceipt = findViewById(R.id.lyReceipt);
       //rcLoanAmount = findViewById(R.id.rcLoanAmount);
       /* rcTotalInterest = findViewById(R.id.rcTotalInterest);
        rcProcFee = findViewById(R.id.rcProcFee);
        rcGst = findViewById(R.id.rcGst);
        rcDisbAmount = findViewById(R.id.rcDisbAmount);
        rcRepayAmount = findViewById(R.id.rcRepayAmount);
        tvPlatfromCharges = findViewById(R.id.tvPlatfromCharges);*/


        Date today = new Date();
        if (!hasPermissions(HomeActivity.this, PERMISSIONS)) {

            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        @SuppressLint("SimpleDateFormat") final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        retuningDate = format.format(today);

        //Glide.with(HomeActivity.this).load(sharedPreference.getProfilePic()).apply(RequestOptions.placeholderOf(R.drawable.user_image).error(R.drawable.user_image)).into(iv_userProfile);

        formatter = new DecimalFormat("#,##,###");

        if (getIntent().getExtras() != null) {
            userCity.setText(getIntent().getStringExtra("city"));
        }




        getUserCurrentStatus();


        /*btSubmitAmnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!hasPermissions(HomeActivity.this, PERMISSIONS)) {

                    ActivityCompat.requestPermissions(HomeActivity.this, PERMISSIONS, PERMISSION_ALL);
                } else {
                    //if (sharedPreference.getSignFlag() == 3) {
                    if (allValidated()) {
                        if (new ConnectionCheck(HomeActivity.this).isNetworkAvailable()) {

                                //String days = tvDays.getText().toString();
                                String days = "30";
                                Calendar calendar = Calendar.getInstance();
                                calendar.add(Calendar.DATE, Integer.parseInt(days));
                                Date returnDate = new Date(calendar.getTimeInMillis());
                                String rDate = format.format(returnDate);

                                //Log.i("returnDate","---->"+rDate);

                            *//*
                            Log.i("rr","="+tvAmountSelected.getText().toString());
                            Log.i("rr","="+rcDisbAmount.getText().toString());
                            Log.i("rr","="+tvDays.getText().toString());
                            Log.i("rr","="+rcProcFee.getText().toString());
                            Log.i("rr","="+rcTotalInterest.getText().toString());*//*


                            getActiveLoan();

                          *//*  Intent intent = new Intent(HomeActivity.this, BankDetailsActivity.class);
                            intent.putExtra("loan_amount", tvAmountSelected.getText().toString());
                            intent.putExtra("disbursed_amount", rcDisbAmount.getText().toString());
                            intent.putExtra("days_returning", tvDays.getText().toString());
                            intent.putExtra("processing_fee", rcProcFee.getText().toString());
                            intent.putExtra("total_interest", rcTotalInterest.getText().toString());
                            intent.putExtra("repay_amount", rcRepayAmount.getText().toString());
                            startActivity(intent);*//*



                            *//*Intent intent = new Intent(HomeActivity.this, BankDetailsActivity.class);
                            intent.putExtra("loan_amount", tvAmountSelected.getText().toString());
                            intent.putExtra("disbursed_amount", rcDisbAmount.getText().toString());
                            intent.putExtra("days_returning", tvDays.getText().toString());
                            intent.putExtra("processing_fee", rcProcFee.getText().toString());
                            intent.putExtra("total_interest", rcTotalInterest.getText().toString());
                            intent.putExtra("repay_amount", rcRepayAmount.getText().toString());
                            startActivity(intent);*//*
                                *//*if (!sAccType.isEmpty()) {
                                    sendDataApplyLoan(rDate, sAccType, sBankName);
                                } else
                                    sendDataApplyLoanWithoutDocs(rDate, sAccType, sBankName);*//*

                        } else {
                            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please Check Your Internet Connection", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                        *//*Intent intent = new Intent(HomeActivity.this, BankDetailsActivity.class);
                        intent.putExtra("loanAmount", tvAmountSelected.getText().toString());
                        intent.putExtra("returnDays", days);
                        intent.putExtra("returnDate", rDate);
                        intent.putExtra("totalInterest", tvTotalInterest.getText().toString());
                        startActivity(intent);*//*

                        *//*String payableAmount = ""+formatter.format((Integer.parseInt(tvAmountSelected.getText().toString().replace(",",""))+
                                                                Integer.parseInt(tvTotalInterest.getText().toString().replace(",",""))+ 200));
                        Intent intent = new Intent(HomeActivity.this, DoneActivity.class);
                        intent.putExtra("orderid", "IM2352142125212");
                        intent.putExtra("loanAmount", *//**//*object.optString("loan_amount")*//**//*tvAmountSelected.getText().toString());
                        intent.putExtra("payableAmount", payableAmount);
                        startActivity(intent);*//*

                    }

                }
            }
        });*/

        /*eNash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), RegisterMandate.class);
                startActivity(intent);
            }
        });*/

       /* tvLoanEligibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new ConnectionCheck(HomeActivity.this).isNetworkAvailable()) {
                    //sendDataGetLoanElig(sharedPreference.getUserId());
                } else {
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please Check Your Internet Connection", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });*/



        /*seekbarAmount = findViewById(R.id.PRICEseekBarID);
        seekbarAmount.setMax(30);
        seekbarAmount.setProgress(3);
        seekbarAmount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if (lyReceipt.getVisibility() == View.GONE) {
                    lyReceipt.setVisibility(View.VISIBLE);
                }
                int amt = Integer.parseInt(tvAmountSelected.getText().toString().replace(",", ""));
                if (amt <4000)
                {
                    seekbarDays.setMax(8);
                }
                else
                {
                    seekbarDays.setMax(23);
                }

                if (progress > 2) {
                    tvAmountSelected.setText(String.valueOf(formatter.format(progress * 1000)));


                  *//*  calculateLoan(Integer.parseInt(tvAmountSelected.getText().toString().replace(",", "")),
                            Integer.parseInt(tvDays.getText().toString()));*//*
                    calculateLoan(Integer.parseInt(tvAmountSelected.getText().toString().replace(",", "")),
                            Integer.parseInt("30"));
                } else {
                    tvAmountSelected.setText(String.valueOf(formatter.format(3000)));

                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        */

        /*seekbarDays = findViewById(R.id.seekbarDays);

        seekbarDays.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if (lyReceipt.getVisibility() == View.GONE) {
                    lyReceipt.setVisibility(View.VISIBLE);
                }

                int amt = Integer.parseInt(tvAmountSelected.getText().toString().replace(",", ""));


                if (amt <4000)
                {
                    seekbarDays.setMax(8);
                }
                else
                {
                    seekbarDays.setMax(23);
                }

                tvDays.setText(String.valueOf(progress + 7));

                *//*calculateLoan(Integer.parseInt(tvAmountSelected.getText().toString().replace(",", "")),
                        Integer.parseInt(tvDays.getText().toString()));*//*
                calculateLoan(Integer.parseInt(tvAmountSelected.getText().toString().replace(",", "")),
                        Integer.parseInt("30"));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
*/
    }



    private void getUserCurrentStatus(){


        final ProgressDialog progressBar;
        progressBar = new ProgressDialog(HomeActivity.this, R.style.AppCompatProgressDialogStyle);
        progressBar.setCancelable(false);
        progressBar.setMessage("Please Wait...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();


        RequestQueue queue = Volley.newRequestQueue(this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("user_id", sharedPreference.getUserId());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Utility.BASE_URL+"/getCurrentStateOfUser", new JSONObject(postParam), new com.android.volley.Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                progressBar.dismiss();

                //Log.i("Result","---"+response);

                if(response.optBoolean("status"))
                {


                    String code = response.optString("code");
                    String loan_status = response.optString("laon_status");
                    String order_id = response.optString("order_id");

                    //Toast.makeText(HomeActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                    if(code.equalsIgnoreCase("1"))
                    {
                        //Toast.makeText(HomeActivity.this, "status = "+code, Toast.LENGTH_SHORT).show();

                        Bundle bundle = new Bundle();
                        bundle.putString("loan_status", loan_status);
                        bundle.putString("order_id",order_id);
                        LoanStatus loanStatus = new LoanStatus();
                        loanStatus.setArguments(bundle);
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.homeFragment, loanStatus);
                        ft.commit();

                    }
                    else if(code.equalsIgnoreCase("2")){

                        CurrentLoan currentLoan = new CurrentLoan();
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.homeFragment, currentLoan);
                        ft.commit();
                    }
                    else if(code.equalsIgnoreCase("3") ){

                       /* Bundle bundle = new Bundle();
                        bundle.putString("loan_status", loan_status);*/
                        ApplyForLoan applyForLoan = new ApplyForLoan();
                        //applyForLoan.setArguments(bundle);
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.homeFragment, applyForLoan);
                        ft.commit();
                    }
                }
                else {
                    ApplyForLoan applyForLoan = new ApplyForLoan();
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.homeFragment, applyForLoan);
                    ft.commit();



                }

            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error:" + error.getMessage());
                //Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.dismiss();

            }

        }) {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

        };

        jsonObjReq.setTag(TAG);
        queue.add(jsonObjReq);

    }


    void checkInstallReferrer() {
        if (getPreferences(MODE_PRIVATE).getBoolean(prefKey, false)) {
            return;
        }

        final InstallReferrerClient referrerClient = InstallReferrerClient.newBuilder(this).build();
        //backgroundExecutor.execute(() -> getInstallReferrerFromClient(referrerClient));

        backgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                getInstallReferrerFromClient(referrerClient);
            }
        });
    }



    void getInstallReferrerFromClient(final InstallReferrerClient referrerClient) {

        referrerClient.startConnection(new InstallReferrerStateListener() {
            @Override
            public void onInstallReferrerSetupFinished(int responseCode) {
                switch (responseCode) {
                    case InstallReferrerClient.InstallReferrerResponse.OK:
                        ReferrerDetails response = null;
                        try {
                            response = referrerClient.getInstallReferrer();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                            return;
                        }
                        final String referrerUrl = response.getInstallReferrer();


                        // TODO: If you're using GTM, call trackInstallReferrerforGTM instead.
                        trackInstallReferrer(referrerUrl);


                        // Only check this once.
                        getPreferences(MODE_PRIVATE).edit().putBoolean(prefKey, true).commit();

                        // End the connection
                        referrerClient.endConnection();

                        break;
                    case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                        // API not available on the current Play Store app.
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                        // Connection couldn't be established.
                        break;
                }
            }

            @Override
            public void onInstallReferrerServiceDisconnected() {

            }
        });
    }


    private void trackInstallReferrer(final String referrerUrl) {
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                CampaignTrackingReceiver receiver = new CampaignTrackingReceiver();
                Intent intent = new Intent("com.android.vending.INSTALL_REFERRER");
                intent.putExtra("referrer", referrerUrl);
                receiver.onReceive(getApplicationContext(), intent);
            }
        });
    }

    //Get values by key
    public String getUtmValues(Context context,String key){
        String val=null;
        try {
            SharedPreferences preferences = context.getSharedPreferences("utm_campaign", Context.MODE_PRIVATE);
            val = preferences.getString(key, "null");
        }catch (Exception e){
            e.printStackTrace();
        }
        return val;
    }



    private void ratingDialog(){

        final Dialog ratingDialog = new Dialog(HomeActivity.this);
        ratingDialog.setCancelable(true);
        ratingDialog.setContentView(R.layout.dialog_rating_review);
        ratingDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView closeDialog = ratingDialog.findViewById(R.id.tvCloseRating);
        TextView ratingButton = ratingDialog.findViewById(R.id.tvRateUsDialog);
        RatingBar ratingBar = ratingDialog.findViewById(R.id.ratingBar);
        ratingBar.setRating(5);


        ratingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent viewIntent =
                            new Intent("android.intent.action.VIEW",
                                    Uri.parse("https://play.google.com/store/apps/details?id=com.loaneasy"));
                    startActivity(viewIntent);
                }catch(Exception e) {
                    Toast.makeText(getApplicationContext(),"Unable to Connect Try Again...",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }
        });


        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratingDialog.dismiss();
            }
        });


        ratingDialog.show();

    }


    private void updateDocumentDialog() {
        dialogUploadDocument = new Dialog(HomeActivity.this);
        dialogUploadDocument.setCancelable(true);
        dialogUploadDocument.setContentView(R.layout.dialog_doc_update);
        dialogUploadDocument.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvOk = dialogUploadDocument.findViewById(R.id.tvOk);
        tvCancel = dialogUploadDocument.findViewById(R.id.tvCancel);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogUploadDocument.dismiss();
            }
        });

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogUploadDocument.dismiss();

                    //if (sharedPreference.getSignFlag() == 3) {

                        if (new ConnectionCheck(HomeActivity.this).isNetworkAvailable()) {
                                applyForLoan();
                        }
                        else
                        {
                            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please Check Your Internet Connection", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }


               /* Intent intent = new Intent(getApplicationContext(), UpdateDocument.class);
                intent.putExtra("loan_id", loanId);
                startActivity(intent);
                finish();*/
            }
        });



        dialogUploadDocument.show();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialogUploadDocument != null) {
            dialogUploadDocument.dismiss();
        }
    }



    private void getENashRequest(){


        final ProgressDialog progressBar;
        progressBar = new ProgressDialog(HomeActivity.this, R.style.AppCompatProgressDialogStyle);
        progressBar.setCancelable(false);
        progressBar.setMessage("Please Wait...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();


        RequestQueue queue = Volley.newRequestQueue(this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("user_id", sharedPreference.getUserId());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Utility.BASE_URL+"/eNashRequest", new JSONObject(postParam), new com.android.volley.Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                progressBar.dismiss();

                Log.i("Result","---"+response);
                flag = response.optBoolean("status");

                if (response.optBoolean("status"))
                {
                    //eNash.setVisibility(View.VISIBLE);
                    enashLayout.setVisibility(View.VISIBLE);
                }
                else
                {
                    //eNash.setVisibility(View.GONE);
                    enashLayout.setVisibility(View.GONE);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error:" + error.getMessage());
                progressBar.dismiss();

                //eNash.setVisibility(View.GONE);
                enashLayout.setVisibility(View.GONE);
                //Toast.makeText(HomeActivity.this, "Something went wrong, Please try after some time !", Toast.LENGTH_SHORT).show();
            }

        }) {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

        };

        jsonObjReq.setTag(TAG);
        queue.add(jsonObjReq);

    }



    private void applyForLoan() {
        final ProgressDialog progressBar11;
        progressBar11 = new ProgressDialog(HomeActivity.this, R.style.AppCompatProgressDialogStyle);
        progressBar11.setCancelable(false);
        progressBar11.setMessage("Please Wait...");
        progressBar11.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar11.show();


        RequestQueue queue = Volley.newRequestQueue(this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("user_id", sharedPreference.getUserId());
        postParam.put("loan_amount", tvAmountSelected.getText().toString().replace(",", ""));
        postParam.put("disbursed_amount", rcDisbAmount.getText().toString().replace(",", ""));
        postParam.put("days_returning", tvDays.getText().toString());
        postParam.put("total_interest", rcTotalInterest.getText().toString().replace(",", ""));
        postParam.put("processing_fee", rcRepayAmount.getText().toString().replace(",", ""));


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Utility.BASE_URL+"/applyLoan2", new JSONObject(postParam), new com.android.volley.Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                progressBar11.dismiss();
                if(response.optBoolean("status"))
                {
                    Log.i(TAG,"Inside if condition ");

                    //loanId = response.optString("loan_id");

                    Intent intent = new Intent(HomeActivity.this, BankDetailsActivity.class);
                    intent.putExtra("loan_id", response.optString("loan_id"));
                    intent.putExtra("loan_amount", tvAmountSelected.getText().toString());
                    intent.putExtra("disbursed_amount", rcDisbAmount.getText().toString());
                    intent.putExtra("days_returning", tvDays.getText().toString());
                    intent.putExtra("processing_fee", rcProcFee.getText().toString());
                    intent.putExtra("total_interest", rcTotalInterest.getText().toString());
                    intent.putExtra("repay_amount", rcRepayAmount.getText().toString());
                    startActivity(intent);

                }
                else
                {
                    Toast.makeText(HomeActivity.this, response.optString("msg"), Toast.LENGTH_SHORT).show();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                progressBar11.dismiss();
                Toast.makeText(HomeActivity.this, "Something went wrong, Please try after some time !", Toast.LENGTH_SHORT).show();
            }

        }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

        };

        jsonObjReq.setTag(TAG);
        // Adding request to request queue
        queue.add(jsonObjReq);

    }


    private void getActiveLoan(){

        final ProgressDialog progressBar;
        progressBar = new ProgressDialog(HomeActivity.this, R.style.AppCompatProgressDialogStyle);
        progressBar.setCancelable(false);
        progressBar.setMessage("Please Wait...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();


        RequestQueue queue = Volley.newRequestQueue(this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("user_id", sharedPreference.getUserId());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Utility.BASE_URL+"/getLoanApplied", new JSONObject(postParam), new com.android.volley.Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                progressBar.dismiss();

                Log.i("Result","---"+response);

                if(response.optBoolean("status"))
                {

                    Toast.makeText(HomeActivity.this, response.optString("msg"), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //updateDocumentDialog();

                    Intent intent = new Intent(HomeActivity.this, BankDetailsActivity.class);
                    intent.putExtra("loan_amount", tvAmountSelected.getText().toString());
                    intent.putExtra("disbursed_amount", rcDisbAmount.getText().toString());
                    intent.putExtra("days_returning", tvDays.getText().toString());
                    //intent.putExtra("days_returning", "30");
                    String platChargs  = String.valueOf(platformCharges);
                    intent.putExtra("platform_charges", platChargs);
                    intent.putExtra("processing_fee", rcProcFee.getText().toString());
                    intent.putExtra("total_interest", rcTotalInterest.getText().toString());
                    intent.putExtra("repay_amount", rcRepayAmount.getText().toString());
                    startActivity(intent);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error:" + error.getMessage());
                progressBar.dismiss();
                //Toast.makeText(HomeActivity.this, "Something went wrong, Please try after some time !", Toast.LENGTH_SHORT).show();
            }

        }) {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

        };

        jsonObjReq.setTag(TAG);
        // Adding request to request queue
        queue.add(jsonObjReq);

        // Cancelling request
    /* if (queue!= null) {
    queue.cancelAll(TAG);
    } */
    }



    private void getRatingDialog(){


        final ProgressDialog progressBar;
        progressBar = new ProgressDialog(HomeActivity.this, R.style.AppCompatProgressDialogStyle);
        progressBar.setCancelable(false);
        progressBar.setMessage("Please Wait...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();


        RequestQueue queue = Volley.newRequestQueue(this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("user_id", sharedPreference.getUserId());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Utility.BASE_URL+"/getLoanCompleted", new JSONObject(postParam), new com.android.volley.Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                progressBar.dismiss();

                Log.i("Response","---"+response);

                if(response.optBoolean("status"))
                {
                    ratingDialog();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error:" + error.getMessage());
                progressBar.dismiss();
                //Toast.makeText(HomeActivity.this, "Something went wrong, Please try after some time !", Toast.LENGTH_SHORT).show();
            }

        }) {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

        };

        jsonObjReq.setTag(TAG);
        // Adding request to request queue
        queue.add(jsonObjReq);
    }


    private boolean allValidated() {

        if (!isAllowed) {
            showDialogLoan();
            return false;
        }

        if (rcRepayAmount.getText().toString().equalsIgnoreCase("--")) {
            Toast.makeText(HomeActivity.this, "Please select amount", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (rcTotalInterest.getText().toString().equalsIgnoreCase("--")) {
            Toast.makeText(HomeActivity.this, "Please select days", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        drawerFragment.setNotifCount();

        if (new ConnectionCheck(HomeActivity.this).isNetworkAvailable()) {
            //sendDataGetLoanElig(sharedPreference.getUserId());
        } else {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please Check Your Internet Connection", Snackbar.LENGTH_LONG);
            snackbar.show();
            String text = "<font color=#f9a82d>Click</font> <font color=#376DD8>here</font> <font color=#f9a82d>to reload</font>";
            tvLoanEligibility.setText(Html.fromHtml(text));
        }

        userName.setText(sharedPreference.getUserSocialName());

        if (!sharedPreference.getCity().isEmpty())
            userCity.setText(" | " + sharedPreference.getCity());
    }




    @SuppressLint("SetTextI18n")
    private void calculateLoan(int amntSelected, int days) {

        if (days < 15)
            days = 15;
        else if (days > 15)
            days = 30;


        double interPerMonth = amntSelected * mEmiRatePerMonth;
        double interPerDay = interPerMonth / 30;
        double totalInterest = interPerDay * days;
        double processingFee = 0;

        platformCharges = amntSelected * .04;
        double gstChargesOnPlatfromFee = platformCharges * .18;

        rcTotalInterest.setText("" + formatter.format((int) Math.round(totalInterest)));
        if(amntSelected <= 4000){
            processingFee = amntSelected * .15;
        }
        else if (amntSelected < 10000) {
            processingFee = 700;
        } else {
            processingFee = amntSelected * .07;
        }

        //processingFee = processingFee;


        DecimalFormat processingFeeRoundOf = new DecimalFormat("0.00");

        rcProcFee.setText("" + processingFeeRoundOf.format(processingFee));
        rcGst.setText(String.valueOf(formatter.format((processingFee+platformCharges) * .18) ));


        double totalDisburseAmount = amntSelected - (Double.parseDouble(rcProcFee.getText().toString()) +
                Double.parseDouble(rcGst.getText().toString().replace(",", "")) + platformCharges);

        double totalRepayAmount = amntSelected + totalInterest;
        rcDisbAmount.setText(String.valueOf(formatter.format(totalDisburseAmount)));
        rcRepayAmount.setText(String.valueOf(formatter.format(totalRepayAmount)));
        tvPlatfromCharges.setText(String.valueOf(formatter.format(platformCharges)));


    }

    @Override
    public void onDrawerItemSelected(View view, int position) {

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        return false;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.lyMyAccount:
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                mDrawerLayout.closeDrawer(containerView);
                break;

            case R.id.lyApplLoans:
                startActivity(new Intent(HomeActivity.this, AppliedLoansActivity.class));
                mDrawerLayout.closeDrawer(containerView);
                break;

            case R.id.lyActiveLoans:
                startActivity(new Intent(HomeActivity.this, ActiveLoansActivity.class));
                mDrawerLayout.closeDrawer(containerView);
                break;

            case R.id.lyNotifs:
                startActivity(new Intent(HomeActivity.this, NotificationsActivity.class));
                mDrawerLayout.closeDrawer(containerView);
                break;

            case R.id.lyTerms:
                startActivity(new Intent(HomeActivity.this, TermsConditionsActivity.class));
                mDrawerLayout.closeDrawer(containerView);
                break;

            case R.id.lyFaq:
                startActivity(new Intent(HomeActivity.this, FAQActivity.class));
                mDrawerLayout.closeDrawer(containerView);
                break;

            case R.id.lycontact_us:
                startActivity(new Intent(HomeActivity.this, ContactUsActivity.class));
                mDrawerLayout.closeDrawer(containerView);
                break;

            case R.id.lyPrivacyPolicy:
                //startActivity(new Intent(HomeActivity.this, PrivacyPolicy.class));
                Intent intentPrivacy = new Intent(getApplicationContext(), PrivacyPolicy.class);
                intentPrivacy.putExtra("termsButton", false);
                startActivity(intentPrivacy);
                mDrawerLayout.closeDrawer(containerView);
                break;

            case R.id.lyLogout:
                logoutAlertDialog();
                break;

            case R.id.lyRefundPolicy:
                startActivity(new Intent(HomeActivity.this, RefundPolicy.class));
                mDrawerLayout.closeDrawer(containerView);
                break;

           /* case R.id.lyEcsMandate:
                startActivity(new Intent(HomeActivity.this, RegisterMandate.class));
                mDrawerLayout.closeDrawer(containerView);
                break;*/

            case R.id.lyBankDetails:
                //startActivity(new Intent(getActivity(), RegisterMandate.class));
                Intent openBankDetails = new Intent(getApplicationContext(), EMandateBankDetails.class);
                openBankDetails.putExtra("updateBank", true);
                startActivity(openBankDetails);
                mDrawerLayout.closeDrawer(containerView);
                break;
        }
    }


    
    private boolean hasPermissions(HomeActivity context, String[] permissions) {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }



    private void logoutAlertDialog(){


        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        sharedPreference.clearPrefs();
                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));

                        finish();

                    /*    sharedPreference.clearPrefs();
                        startActivity(new Intent(HomeActivity.this, PhoneNoActivity.class));
                        finish();*/
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        // login_btn.setOnClickListener( null );
        // register_btn.setOnClickListener( null );
        if (permissions.length == 0) {
            return;
        }

        boolean allPermissionsGranted = true;
        if (grantResults.length > 0) {
            for (int grantResult : grantResults) {

                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
        }


        if (!allPermissionsGranted) {
            boolean somePermissionsForeverDenied = false;
            for (String permission : permissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    //denied
                    Toast.makeText(getApplicationContext(), "Please allow required permission", Toast.LENGTH_LONG).show();
                    Log.e("denied", permission);

                } else {
                    if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                        //allowed

                        Log.e("allowed", permission);


                    } else {
                        //set to never ask again

                        //Toast.makeText( getApplicationContext(),"set to never ask again",Toast.LENGTH_LONG ).show();
                        Log.e("set to never ask again", permission);
                        somePermissionsForeverDenied = true;
                    }
                }
            }


            if (somePermissionsForeverDenied) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Permissions Required")
                        .setMessage("You have forcefully denied some of the required permissions " +
                                "for this action. Please open settings, go to permissions and allow them.")
                        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.fromParts("package", getPackageName(), null));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // System.runFinalizersOnExit(true);
                                //android.os.Process.killProcess(android.os.Process.myPid());
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();

            }

        }
    }

    public void showDialogLoan() {

        dialog = new Dialog(HomeActivity.this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_loan);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        tvOkDialLoan = dialog.findViewById(R.id.tvOkDialLoan);

        tvOkDialLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void showDialogDocument() {

        dialogDoc.setCancelable(false);
        dialogDoc.setContentView(R.layout.dialog_documentcheck);
        dialogDoc.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        tvOkDialDoc = dialogDoc.findViewById(R.id.tvOkDialDoc);
        tvCancelDialDoc = dialogDoc.findViewById(R.id.tvCancelDialDoc);

        tvOkDialDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDoc.dismiss();
            }
        });

        tvCancelDialDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDoc.dismiss();
            }
        });

        dialogDoc.show();
    }

    private void sendDataApplyLoan(String sReturnDate, String sAccType, String sBankName) {

        final ProgressDialog progressBar;
        progressBar = new ProgressDialog(HomeActivity.this, R.style.AppCompatProgressDialogStyle);
        progressBar.setCancelable(false);
        progressBar.setMessage("Please Wait...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();

        RestAdapter adapter = new RestAdapter.Builder().setEndpoint(Utility.BASE_URL).build();
        ApiRequest api = adapter.create(ApiRequest.class);

        typedFileBankStatmnt = new TypedFile("multipart/form-data", new File(sBankStatement));
        typedFileSalarySlip1 = new TypedFile("multipart/form-data", new File(sSalarySlip1));
        typedFileSalarySlip2 = new TypedFile("multipart/form-data", new File(sSalarySlip2));
        typedFileSalarySlip3 = new TypedFile("multipart/form-data", new File(sSalarySlip3));

        api.applyLoan(sharedPreference.getUserId(), tvAmountSelected.getText().toString().replace(",", ""),
                rcDisbAmount.getText().toString().replace(",", ""),
                tvDays.getText().toString(), sReturnDate, sAccType, sBankName,sAccnNo,sIfsc, typedFileBankStatmnt, typedFileSalarySlip1,
                typedFileSalarySlip2, typedFileSalarySlip3, "0", rcTotalInterest.getText().toString(),
                rcProcFee.getText().toString().replace(",", ""), new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        BufferedReader reader = null;
                        String output = "";

                        progressBar.dismiss();
                        try {

                            reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                            output = reader.readLine();

                            Log.d("TAG111", "---applyLoan response---->" + output);

                            JSONObject object = new JSONObject(output);
                            Intent intent = new Intent(HomeActivity.this, DoneActivity.class);
                            intent.putExtra("orderid", object.optString("order_id"));
                            intent.putExtra("loanAmount", object.optString("loan_amount"));
                            intent.putExtra("payableAmount", rcDisbAmount.getText().toString());
                            intent.putExtra("repayAmount", rcRepayAmount.getText().toString());
                            startActivity(intent);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Something went wrong !", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.i("TAG111", "--applyLoan Error-->>" + error.getMessage());
                        progressBar.dismiss();
                        if (error.getMessage().equalsIgnoreCase("timeout"))
                            Toast.makeText(getApplicationContext(), "Connection timeout, please try again", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getApplicationContext(), "Internal Server Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendDataApplyLoanWithoutDocs(String sReturnDate, String sAccType, String sBankName) {

        final ProgressDialog progressBar;
        progressBar = new ProgressDialog(HomeActivity.this, R.style.AppCompatProgressDialogStyle);
        progressBar.setCancelable(false);
        progressBar.setMessage("Please Wait...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();

        RestAdapter adapter = new RestAdapter.Builder().setEndpoint(Utility.BASE_URL).build();
        ApiRequest api = adapter.create(ApiRequest.class);

        api.applyLoanWihtoutDocs(sharedPreference.getUserId(), tvAmountSelected.getText().toString().replace(",", ""), rcDisbAmount.getText().toString().replace(",", ""), tvDays.getText().toString(),
                sReturnDate, sAccType, sBankName,"","", "", "", "", "", "0",
                rcTotalInterest.getText().toString(), rcProcFee.getText().toString().replace(",", ""),
                new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        BufferedReader reader = null;
                        String output = "";

                        progressBar.dismiss();
                        try {

                            reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                            output = reader.readLine();

                            Log.d("TAG111", "---applyLoan response---->" + output);

                            JSONObject object = new JSONObject(output);
                            Intent intent = new Intent(HomeActivity.this, DoneActivity.class);
                            intent.putExtra("orderid", object.optString("order_id"));
                            intent.putExtra("loanAmount", object.optString("loan_amount"));
                            intent.putExtra("payableAmount", rcDisbAmount.getText().toString());
                            intent.putExtra("repayAmount", rcRepayAmount.getText().toString());
                            startActivity(intent);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Something went wrong !", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.i("TAG111", "--applyLoan Error-->>" + error.getMessage());
                        progressBar.dismiss();
                        if (error.getMessage().equalsIgnoreCase("timeout"))
                            Toast.makeText(getApplicationContext(), "Connection timeout, please try again", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getApplicationContext(), "Internal Server Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendDataGetLoanElig(String userId) {

        final ProgressDialog progressBar;
        progressBar = new ProgressDialog(HomeActivity.this, R.style.AppCompatProgressDialogStyle);
        progressBar.setCancelable(false);
        progressBar.setMessage("Please Wait...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();

        RestAdapter adapter = new RestAdapter.Builder().setEndpoint(Utility.BASE_URL).setLogLevel(RestAdapter.LogLevel.FULL).build();
        ApiRequest api = adapter.create(ApiRequest.class);

        api.loanEligibility(userId, new Callback<Response>() {

            @Override
            public void success(Response response, Response response2) {
                BufferedReader reader = null;
                String output = "";

                progressBar.dismiss();
                try {

                    //Initializing buffered reader
                    reader = new BufferedReader(new InputStreamReader(response.getBody().in()));

                    //Reading the output in the string
                    output = reader.readLine();
                    Log.d("TAG111", "--getLoanElig_Response-->" + output);

                    JSONObject object = new JSONObject(output);
                    if (object.optString("status").equalsIgnoreCase("true")) {
                        nLoanEligAmount = Integer.parseInt(object.optString("amount").replace(",", ""));
                        tvLoanEligibility.setText("You are eligible for the loan amount of upto Rs. " + formatter.format(nLoanEligAmount));

                        if ((object.optString("activeLoan").equalsIgnoreCase("1"))) {
                            isAllowed = false;
                            nLoanEligAmount = -1;
                            tvLoanEligibility.setText(R.string.loan_already_applied);
                        }

                        if ((object.optString("document_dependency").equalsIgnoreCase("1"))) {
                            if (!dialogDoc.isShowing())
                                showDialogDocument();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("failure", "---->>" + error.getMessage());
                progressBar.dismiss();
                Toast.makeText(getApplicationContext(), "Internal Server Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
