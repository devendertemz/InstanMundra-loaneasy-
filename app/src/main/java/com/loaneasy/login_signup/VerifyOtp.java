package com.loaneasy.login_signup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.loaneasy.network.ApiRequest;
import com.loaneasy.HomeActivity;
import com.loaneasy.R;
import com.loaneasy.utils.UserSharedPreference;
import com.loaneasy.utils.Utility;
import com.msg91.sendotpandroid.library.internal.SendOTP;
import com.msg91.sendotpandroid.library.listners.VerificationListener;
import com.msg91.sendotpandroid.library.roots.RetryType;
import com.msg91.sendotpandroid.library.roots.SendOTPConfigBuilder;
import com.msg91.sendotpandroid.library.roots.SendOTPResponseCode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class VerifyOtp extends AppCompatActivity implements VerificationListener {

    private TextView tvSubmitOtp, getOtpOnCall;
    private String phoneNumber,otp,getUserArea,TAG="VerifyOtp", newGeneratedOtp, serverOtp;
    private int randomOtp;
    UserSharedPreference sharedPreference;
    ProgressDialog progressDialog;
    EditText etPin;
    private boolean resendFlag = false;
    private TextView tvResendOtp, tvOtpTimer;
    //private Verification mVerification;

    Handler handler;
    public static final String OTP_REGEX = "[0-9]{1,6}";
    CountDownTimer mCountDownTimer;
    private String fcmToken="N.A.";
    private ProgressDialog pd = null, pd1=null;
    //private boolean reSendOtpFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        setContentView(R.layout.activity_verify_otp);
        sharedPreference = new UserSharedPreference(this);
        //otpTimer = (TextView)findViewById(R.id.tvOtpTimer);


        registerReceiver(broadcastReceiver, new IntentFilter("MySMSBroadcastReceiver"));


      /*  IntentFilter screenStateFilter = new IntentFilter();
        screenStateFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(broadcastReceiver, screenStateFilter);*/

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            getUserArea = bundle.getString("userArea");
            phoneNumber = bundle.getString("phone_no");

            Log.i("area","="+getUserArea);
            //sendOtp22(phoneNumber);

            sendOtpNew(false);

        }



        etPin = findViewById(R.id.etPin);
        tvResendOtp = findViewById(R.id.tvResendOtp);
        tvOtpTimer = findViewById(R.id.tvOtpTimer);
        tvSubmitOtp = findViewById(R.id.btSubmitOtp);
        getOtpOnCall = findViewById(R.id.getOtpCall);

        /*mCountDownTimer = new CountDownTimer(40000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                String sec = "" + millisUntilFinished;
                tvOtpTimer.setVisibility(View.VISIBLE);
                tvOtpTimer.setText("00:" + millisUntilFinished / 1000);
                *//*if (sec.length() == 5) {
                    tvOtpTimer.setText("00:" + millisUntilFinished / 1000);
                } else {
                    tvOtpTimer.setText("00:" + "0" + millisUntilFinished / 1000);
                }*//*


            }

            @Override
            public void onFinish() {

                //Toast.makeText(VerifyOtp.this, "Please enter OTP manually", Toast.LENGTH_SHORT).show();
                if (VerifyOtp.this.pd != null) {
                    VerifyOtp.this.pd.dismiss();
                }
                //tvOtpTimer.setVisibility(View.GONE);
                tvResendOtp.setVisibility(View.VISIBLE);

            }
        }.start();*/



        tvResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //mVerification.resend("voice");
                //resendOTP(phoneNumber);

                //sendOtpNew(true);

                //Toast.makeText(VerifyOtp.this, "clicked", Toast.LENGTH_SHORT).show();

                resendFlag = true;
                tvResendOtp.setVisibility(View.GONE);

                resendOtpFromBackend();


            }
        });



        getOtpOnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VerifyOtp.this, "You will get a call for OTP", Toast.LENGTH_SHORT).show();
                //mVerification.resend("voice");
                SendOTP.getInstance().getTrigger().resend(RetryType.VOICE);


            }
        });

        tvSubmitOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                /*String verifyOtp = etPin.getText().toString().trim();
                Log.i("entered_otp","="+verifyOtp);
                Log.i("entered_otp","="+newGeneratedOtp);*/
               /* if(verifyOtp.equalsIgnoreCase(newGeneratedOtp))
                {
                    //verifyingOTP(phoneNumber, verifyOtp);
                    registerNewUser(phoneNumber);
                }
                else
                {
                    Toast.makeText(VerifyOtp.this, "Wrong OTP", Toast.LENGTH_SHORT).show();
                }*/



                if (resendFlag)
                {
                    if(etPin.getText().toString().trim().equalsIgnoreCase(serverOtp))
                    {
                        registerNewUser(phoneNumber);
                    }
                    else
                    {
                        Toast.makeText(VerifyOtp.this, "Wrong OTP Entered", Toast.LENGTH_SHORT).show();
                    }

                }
                else
                {
                    if(etPin != null)
                    {
                        SendOTP.getInstance().getTrigger().verify(etPin.getText().toString());
                    }
                }







            }
        });

        etPin.addTextChangedListener(new MyTextWatcher(etPin));
    }



    private void callTimer(){


    CountDownTimer mCountDownTimer = new CountDownTimer(40000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                String sec = "" + millisUntilFinished;
                tvOtpTimer.setVisibility(View.VISIBLE);
                tvOtpTimer.setText("00:" + millisUntilFinished / 1000);
                /*if (sec.length() == 5) {
                    tvOtpTimer.setText("00:" + millisUntilFinished / 1000);
                } else {
                    tvOtpTimer.setText("00:" + "0" + millisUntilFinished / 1000);
                }*/


            }

            @Override
            public void onFinish() {

                //Toast.makeText(VerifyOtp.this, "Please enter OTP manually", Toast.LENGTH_SHORT).show();
                if (VerifyOtp.this.pd != null) {
                    VerifyOtp.this.pd.dismiss();
                }


                tvOtpTimer.setVisibility(View.GONE);
                tvResendOtp.setVisibility(View.VISIBLE);

            }
        }.start();

    }


    private void sendOtpNew(boolean otpFlag){

        if(otpFlag)
        {
            otpTimer();
        }

        Random r = new Random( System.currentTimeMillis() );
        int otpGenerated =  ((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
        newGeneratedOtp = String.valueOf(otpGenerated);
        Log.i("otp","===>"+newGeneratedOtp);



        String otpSMS = newGeneratedOtp+"is Your verification code ";

        /*mVerification = SendOtpVerification.createSmsVerification
                (SendOtpVerification
                        .config("91" + phoneNumber)
                        .context(this)
                        .autoVerification(false)
                        .httpsConnection(false)//connection to be use in network calls
                        .expiry("5")//value in minutes
                        .senderId("IMUDRA") //where XXXX is any string
                        .otp(newGeneratedOtp)// Default Otp code if want to add yours
                        .otplength("5") //length of your otp
                        .message(otpSMS)
                        .build(), this);
        mVerification.initiate();*/

        new SendOTPConfigBuilder()
                .setCountryCode(91)
                .setMobileNumber(phoneNumber)
                .setVerifyWithoutOtp(true)//direct verification while connect with mobile network
                .setAutoVerification(VerifyOtp.this)//Auto read otp from Sms And Verify
                .setSenderId("IMUDRA")
                .setMessage("##OTP## is Your verification digits.")
                .setOtpLength(5)
                .setVerificationCallBack(this).build();

        SendOTP.getInstance().getTrigger().initiate();

        callTimer();


        //startRetriever();
    }


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle b = intent.getExtras();

            String smsOtp = b.getString("otp");

            Log.i("otp_get",""+smsOtp);


            if (VerifyOtp.this.pd != null) {
                VerifyOtp.this.pd.dismiss();
            }

            //verifyingOTP(phoneNumber, smsOtp);

            if(smsOtp.equalsIgnoreCase(newGeneratedOtp))
            {
                registerNewUser(phoneNumber);
            }


        }
    };




    private void otpTimer()
    {
        tvOtpTimer.setVisibility(View.VISIBLE);
        tvResendOtp.setVisibility(View.GONE);


        CountDownTimer countDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {


                String sec = "" + millisUntilFinished;
                tvOtpTimer.setVisibility(View.VISIBLE);
                if (sec.length() == 5) {
                    tvOtpTimer.setText("00:" + millisUntilFinished / 1000);
                } else {
                    tvOtpTimer.setText("00:" + "0" + millisUntilFinished / 1000);
                }
            }

            @Override
            public void onFinish() {


                tvOtpTimer.setVisibility(View.GONE);
                tvResendOtp.setVisibility(View.VISIBLE);
            }
        }.start();


    }


    private  void registerNewUser(String phone){

        this.pd1 = ProgressDialog.show(this, "Registering User", "Please wait...", true, false);

        RequestQueue queue = Volley.newRequestQueue(this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("phone_no", phone);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Utility.BASE_URL+"/registerUser2", new JSONObject(postParam), new com.android.volley.Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                //JSONObject jsonObject = new JSONObject(response);
                if(response.optBoolean("status"))
                {
                    JSONObject result = response.optJSONObject("result");
                    sharedPreference.setUserArea(getUserArea);
                    if (result.optString("profile_completed").equals("1")) {
                        sharedPreference.setSignFlag(3);
                        sharedPreference.setUserId(result.optString("user_id"));
                        sharedPreference.setUserPhoneNo(result.optString("phone_no"));
                        sharedPreference.setLocation(result.optString("location"));
                        sharedPreference.setUserSocialName(""+result.optString("first_name")+" "+result.optString("last_name"));
                        sharedPreference.setUserCity(result.optString("address_city"));
                        sharedPreference.setUserEmail(result.optString("official_mail"));
                        String profilePic = "";
                        if (!result.optString("profile_pic").isEmpty())
                            sharedPreference.setProfilePic(result.optString("profile_pic"));
                        else
                            sharedPreference.setProfilePic(result.optString("social_profile_pic"));
                    }
                    else if (result.optString("existing_customer").equals("1")){
                        sharedPreference.setExistCustomerFlag(1);

                        sharedPreference.setUserSocialName(result.optString("name"));
                        sharedPreference.setUserId(result.optString("user_id"));
                        sharedPreference.setUserPhoneNo(result.optString("phone_no"));
                        sharedPreference.setLocation(result.optString("location"));
                        sharedPreference.setUserEmail(result.optString("official_mail"));
                        Log.d("TAG111","uid---"+sharedPreference.getUserId());
                        Log.d("TAG111","FCM token---"+sharedPreference.getFCMId());
                    }
                    else {
                        sharedPreference.setUserId(result.optString("user_id"));
                        sharedPreference.setUserPhoneNo(result.optString("phone_no"));
                        sharedPreference.setLocation(result.optString("location"));
                    }

                    Log.i("fcm_token","shared_prefs"+sharedPreference.getFCMId());
                    sendDataTokenUpdate(sharedPreference.getUserId(), sharedPreference.getFCMId());

                }
                else
                {
                    String serverMsg= response.optString("msg");
                    Toast.makeText(VerifyOtp.this, serverMsg, Toast.LENGTH_SHORT).show();
                }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("TAG111","-exception---"+e.getMessage());
                }


            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error:" + error.getMessage());
                Toast.makeText(VerifyOtp.this, "Internal Server Error!", Toast.LENGTH_SHORT).show();

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


    public void resendOtpFromBackend(){


        final ProgressDialog progressBar;
        progressBar = new ProgressDialog(VerifyOtp.this, R.style.AppCompatProgressDialogStyle);
        progressBar.setCancelable(false);
        progressBar.setMessage("Please Wait...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();


        RequestQueue queue = Volley.newRequestQueue(this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("phone_no", phoneNumber);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Utility.BASE_URL+"/sendOtp", new JSONObject(postParam), new com.android.volley.Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                progressBar.dismiss();
                try {
                    //JSONObject jsonObject = new JSONObject(response);
                    if(response.optBoolean("status"))
                    {
                        serverOtp = response.optString("otp");
                        callTimer();
                    }
                    else
                    {
                        Toast.makeText(VerifyOtp.this, response.optString("msg"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("TAG111","-exception---"+e.getMessage());
                }


            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.dismiss();
                VolleyLog.d(TAG, "Error:" + error.getMessage());
                Toast.makeText(VerifyOtp.this, "Internal Server Error!", Toast.LENGTH_SHORT).show();
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


    public  void startRetriever ()
    {
        SmsRetrieverClient client = SmsRetriever.getClient(this /* context */);

        // Starts SmsRetriever, which waits for ONE matching SMS message until timeout
        // (5 minutes). The matching SMS message will be sent via a Broadcast Intent with
        // action SmsRetriever#SMS_RETRIEVED_ACTION.
        Task<Void> task = client.startSmsRetriever();

        // Listen for success/failure of the start Task. If in a background thread, this
        // can be made blocking using Tasks.await(task, [timeout]);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Successfully started retriever, expect broadcast intent
                //Toast.makeText(PhoneNoActivity.this, "Successfully started retriever", Toast.LENGTH_SHORT).show();

            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Failed to start retriever, inspect Exception for more details
               // Toast.makeText(VerifyOtp.this, "Failed to read OTP", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void sendOtp22(String phone){

        this.pd = ProgressDialog.show(this, "Verifying OTP Automatically", "Please wait...", true, false);

        RequestQueue queue = Volley.newRequestQueue(this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("phone_no", phone);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Utility.BASE_URL+"/registerUser", new JSONObject(postParam), new com.android.volley.Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                //JSONObject jsonObject = new JSONObject(output);
                boolean status = response.optBoolean("status");
                if(status)
                {
                    otp = response.optString("otp");
                    /*******OPT confirmation fom server, start reading sms *******/
                    //startRetriever();
                    //verifcation();
                }
                else
                {
                    Toast.makeText(VerifyOtp.this, "Something went wrong !", Toast.LENGTH_SHORT).show();
                }



            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error:" + error.getMessage());
                Toast.makeText(VerifyOtp.this, "Internal Server Error!", Toast.LENGTH_SHORT).show();

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

    private void resendOTP(String phone)
    {
        final ProgressDialog progressBar;
        progressBar = new ProgressDialog(VerifyOtp.this, R.style.AppCompatProgressDialogStyle);
        progressBar.setCancelable(false);
        progressBar.setMessage("Please Wait...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();

        RequestQueue queue = Volley.newRequestQueue(this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("phone_no", phone);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Utility.BASE_URL+"/reSendOtp", new JSONObject(postParam), new com.android.volley.Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                progressBar.dismiss();

                otp = response.optString("otp");
                //startRetriever();
                //JSONObject jsonObject = new JSONObject(output);
                otpTimer();

            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.dismiss();
                VolleyLog.d(TAG, "Error:" + error.getMessage());
                Toast.makeText(VerifyOtp.this, "Internal Server Error!", Toast.LENGTH_SHORT).show();

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


    /*****Send OTP to phone number   *****/
    public void sendOtp(String phone){

       /* final ProgressDialog progressBar;
        progressBar = new ProgressDialog(VerifyOtp.this, R.style.AppCompatProgressDialogStyle);
        progressBar.setCancelable(false);
        progressBar.setMessage("Loading...");
        progressBar.show();*/

        this.pd = ProgressDialog.show(this, "Verifying OTP Automatically", "Please wait...", true, false);

        Log.i("rr1","---->");
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(Utility.BASE_URL).setLogLevel(RestAdapter.LogLevel.FULL).build();
        ApiRequest registrationApiRequest = restAdapter.create(ApiRequest.class);



        registrationApiRequest.loginUser2(phone, new Callback<Response>() {
            @Override
            public void success(Response result, Response response) {
                BufferedReader bufferedReader = null;
                String output = "";
                //progressDialog.dismiss();
                Log.i("rr2","---->");
                try {

                    bufferedReader = new BufferedReader(new InputStreamReader(result.getBody().in())) ;
                    output = bufferedReader.readLine();
                    //progressBar.dismiss();
                    Log.d("OtpResponse","--gettingSMS_response-->"+output);
                    //Toast.makeText(SmsReader.this, "Waiting for OTP sms, Don't go back.", Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject jsonObject = new JSONObject(output);
                        boolean status = jsonObject.optBoolean("status");
                        if(status)
                        {
                            otp = jsonObject.optString("otp");
                            /*******OPT confirmation fom server, start reading sms *******/
                            //startRetriever();
                        }
                        else
                        {
                            Toast.makeText(VerifyOtp.this, "Something went wrong !", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (IOException e) {
                    Log.d("TAG111","--exception_SMS_response-->"+output);
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                //progressBar.dismiss();

                Toast.makeText(VerifyOtp.this, "Internal Server Error!", Toast.LENGTH_SHORT).show();
                Log.d("error","-->"+error.getLocalizedMessage());
                Log.i("rr3","---->");
            }
        });
    }







  /*  private String getGeneratedFcm(){

        final String[] token = {""};

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        token[0]  = task.getResult().getToken();

                         //token[0] = newToken.toString();


                    }
                });
        return token[0];
    }*/


    ///for fcmToken update
    public void sendDataTokenUpdate(String UId, String token) {

        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(Utility.BASE_URL).build();

        ApiRequest registrationApiRequest = restAdapter.create(ApiRequest.class);

        /*progressDialog = new ProgressDialog(VerifyOtp.this, R.style.AppCompatProgressDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Verifying OTP..");
        progressDialog.show();*/
        registrationApiRequest.updateFcmToken(UId, token, new Callback<Response>() {
            @Override
            public void success(Response result, Response response) {
                BufferedReader bufferedReader = null;
                String output = "";
                //progressDialog.dismiss();

                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(result.getBody().in()));
                    output = bufferedReader.readLine();
                    //progressDialog.dismiss();
                    Log.i("TAG111", "-output_updateFcmToken--->" + output);

                    if (VerifyOtp.this.pd1 != null) {
                        VerifyOtp.this.pd1.dismiss();
                    }
                    if (sharedPreference.getSignFlag() == 3) {
                        startActivity(new Intent(VerifyOtp.this, HomeActivity.class));
                        finish();
                    } else {
                        sharedPreference.setSignFlag(1);
                        Intent intent = new Intent(getApplicationContext(), SocialMediaLogin.class);
                        startActivity(intent);
                        finish();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                //progressDialog.dismiss();
                if (VerifyOtp.this.pd1 != null) {
                    VerifyOtp.this.pd1.dismiss();
                }
                //Toast.makeText(VerifyOtp.this, "Server Error", Toast.LENGTH_SHORT).show();
                Log.e("TAG111", "--error_updateFcmToken-->" + error.getLocalizedMessage());

                if (sharedPreference.getSignFlag() == 3) {
                    startActivity(new Intent(VerifyOtp.this, HomeActivity.class));
                    finish();
                } else {
                    sharedPreference.setSignFlag(1);
                    Intent intent = new Intent(getApplicationContext(), SocialMediaLogin.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }



    @Override
    public void onSendOtpResponse(final SendOTPResponseCode responseCode, final String message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Log.e(TAG, "onSendOtpResponse: " + responseCode.getCode() + "=======" + message);
                if (responseCode == SendOTPResponseCode.DIRECT_VERIFICATION_SUCCESSFUL_FOR_NUMBER || responseCode == SendOTPResponseCode.OTP_VERIFIED) {
                    //Toast.makeText(VerifyOtp.this, "verified successfully", Toast.LENGTH_SHORT).show();
                    registerNewUser(phoneNumber);
                    //otp verified OR direct verified by send otp 2.O
                } else if (responseCode == SendOTPResponseCode.READ_OTP_SUCCESS) {
                    //Auto read otp from sms successfully
                    // you can get otp form message filled
                    if(etPin != null)
                    {
                        etPin.setText(message);
                        SendOTP.getInstance().getTrigger().verify(message);
                    }
                } else if (responseCode == SendOTPResponseCode.SMS_SUCCESSFUL_SEND_TO_NUMBER || responseCode == SendOTPResponseCode.DIRECT_VERIFICATION_FAILED_SMS_SUCCESSFUL_SEND_TO_NUMBER)
                {
                    // Otp send to number successfully
                    //Toast.makeText(VerifyOtp.this, "OTP send successfully", Toast.LENGTH_SHORT).show();
                } else {
                    //exception found
                    Log.e("sms_error",message);
                }
            }
        });


    }



    /***********MSG 91 OTP methods********/
   /* @Override
    public void onInitiated(String response) {
        Log.d(TAG, "Initialized!" + response);
        //OTP successfully resent/sent.
    }

    @Override
    public void onInitiationFailed(Exception exception) {
        Log.e(TAG, "Verification initialization failed: " + exception.getMessage());
        //sending otp failed.
    }

    @Override
    public void onVerified(String response) {
        Log.d(TAG, "Verified!\n" + response);
        //OTP verified successfully.
    }

    @Override
    public void onVerificationFailed(Exception exception) {
        Log.e(TAG, "Verification failed: " + exception.getMessage());
        //OTP  verification failed.
    }
*/


    public class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.etPin:
                    if (etPin.getText().toString().trim().length()==5) {
                        InputMethodManager imm = (InputMethodManager) VerifyOtp.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        //Find the currently focused view, so we can grab the correct window token from it.
                        View view = VerifyOtp.this.getCurrentFocus();
                        //If no view currently has focus, create a new one, just so we can grab a window token from it
                        if (view == null) {
                            view = new View(VerifyOtp.this);
                        }
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    break;


            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(broadcastReceiver);

        SendOTP.getInstance().getTrigger().stop();
    }

}
