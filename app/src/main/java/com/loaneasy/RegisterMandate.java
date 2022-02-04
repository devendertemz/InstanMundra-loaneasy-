package com.loaneasy;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.loaneasy.utils.UserSharedPreference;
import com.loaneasy.utils.Utility;
import com.paynimo.android.payment.PaymentActivity;
import com.paynimo.android.payment.PaymentModesActivity;
import com.paynimo.android.payment.model.Checkout;
import com.paynimo.android.payment.util.Constant;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Ravindra on 26-Mar-19.
 */
public class RegisterMandate extends AppCompatActivity {

    private String TAG= "RegisterMandate";
    UserSharedPreference sharedPreference;
    private  Map<String, String> mandateDetailsObject;
    private  Dialog dialogOffer;
    private TextView tvOk;
    private boolean  mandateFlag = false;
    private String mandateResponse = "", transactionReference,transactionIdentifier;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        sharedPreference = new UserSharedPreference(this);

        //ifMandate();
        //registerMandate();
        
        getUserLoanDetails("12000");

    }


    private void mandateDialog() {
        dialogOffer = new Dialog(RegisterMandate.this);
        dialogOffer.setCancelable(false);
        dialogOffer.setContentView(R.layout.dialog_yourcity);
        dialogOffer.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvOk = dialogOffer.findViewById(R.id.tvOk);

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogOffer.dismiss();
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        dialogOffer.show();
    }


    private void ifMandate(){


        final ProgressDialog progressBar12;
        progressBar12 = new ProgressDialog(RegisterMandate.this, R.style.AppCompatProgressDialogStyle);
        progressBar12.setCancelable(false);
        progressBar12.setMessage("Please Wait...");
        progressBar12.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar12.show();

        RequestQueue queue = Volley.newRequestQueue(this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("user_id",sharedPreference.getUserId());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Utility.BASE_URL+"/checkEMandateRequest", new JSONObject(postParam), new com.android.volley.Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                progressBar12.dismiss();

                JSONObject result = response.optJSONObject("response");

                Log.i("response","="+result);

                getUserLoanDetails("20000");

                /*if(result.optString("is_ecs_request").equalsIgnoreCase("1"))
                {


                    String ifMandate = result.optString("is_mandate_registered");

                    String sanctionedAmount = result.optString("sanction_amount");

                    Log.i("Sanction Amount",":"+sanctionedAmount);

                    getUserLoanDetails("10000");


                    if(ifMandate.equalsIgnoreCase("0"))
                    {
                        getUserLoanDetails(sanctionedAmount);
                    }
                    else if(ifMandate.equalsIgnoreCase("1")){

                        Toast.makeText(RegisterMandate.this, "You have already registered for ecs mandate, please contact to admin", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        mandateDialog();
                    }


                }
                else
                {
                    mandateDialog();
                    //Toast.makeText(RegisterMandate.this, response.optString("msg"), Toast.LENGTH_SHORT).show();

                }

                 */
            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error:" + error.getMessage());
                progressBar12.dismiss();
                Toast.makeText(RegisterMandate.this, "Something went wrong, Please try after some time !", Toast.LENGTH_SHORT).show();
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



    private void registerMandate(String identifier, String name, String email, String phoneNo, String acctNo, String acctHolder,
                                 String acctType, String ifsc, String sanctionedAmount){

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c);
        Log.i("currentDate","="+formattedDate);

       /* Log.i("rr","="+identifier);
        Log.i("rr","="+name);
        Log.i("rr","="+email);
        Log.i("rr","="+phoneNo);
        Log.i("rr","="+acctNo);
        Log.i("rr","="+acctHolder);
        Log.i("rr","="+acctType);
        Log.i("rr","="+ifsc);*/

        long time= System.currentTimeMillis();
        //String transactionIdentifier = "IMPL"+identifier+String.valueOf(time);
        transactionReference = "IM"+identifier;

        Random r = new Random( System.currentTimeMillis() );
        int id =  ((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
        transactionIdentifier = "IM"+identifier+String.valueOf(id);

        Log.i("rr","="+transactionIdentifier);
        //Log.i("rr","="+transactionReference);

        Checkout checkout = new Checkout();
        checkout.setMerchantIdentifier("L278735");
        checkout.setTransactionIdentifier(transactionIdentifier);
        checkout.setTransactionReference (transactionReference);
        checkout.setTransactionType (PaymentActivity.TRANSACTION_TYPE_SALE);
        checkout.setTransactionSubType (PaymentActivity.PAYMENT_METHOD_CARDS);
        checkout.setTransactionCurrency ("INR");
        checkout.setTransactionAmount ("1.00");
        checkout.setTransactionDateTime (formattedDate);
        checkout.setConsumerIdentifier ("IM"+sharedPreference.getUserId());
        checkout.setConsumerEmailID (email);
        checkout.setConsumerMobileNumber (phoneNo);
        checkout.setConsumerAccountNo (acctNo);

        checkout.addCartItem("FIRST","1.00","0.0", "0.0", "IMPL2019-1014", "ANDROID", "InstantMudra E-MANDATE","www.instantmudra.com");
        checkout.setTransactionAmount ("1.00");
        checkout.setPaymentInstructionAction("Y");
        checkout.setPaymentInstructionType("M");
        checkout.setPaymentInstructionLimit(sanctionedAmount);
        checkout.setPaymentInstructionFrequency("ADHO");
        checkout.setPaymentInstructionStartDateTime(formattedDate);
        checkout.setPaymentInstructionEndDateTime("07-12-2036");
        checkout.setConsumerAccountHolderName(acctHolder);
        checkout.setConsumerAccountType(acctType);
        checkout.setPaymentInstrumentIFSC(ifsc);
        checkout.setPaymentInstructionDebitFlag("Y");

        /*long time= System.currentTimeMillis();
        String transactionIdentifier = "IMPL"+identifier+String.valueOf(time);
        String transactionReference = "IM"+identifier;
        Checkout checkout = new Checkout();
        checkout.setMerchantIdentifier("L278735");
        checkout.setTransactionIdentifier(transactionIdentifier);
        checkout.setTransactionReference (transactionReference);
        checkout.setTransactionType (PaymentActivity.TRANSACTION_TYPE_SALE);
        checkout.setTransactionSubType (PaymentActivity.TRANSACTION_SUBTYPE_DEBIT);
        checkout.setTransactionCurrency ("INR");
        checkout.setTransactionAmount ("1.00");
        checkout.setTransactionDateTime (formattedDate);
        checkout.setConsumerIdentifier (name);
        checkout.setConsumerEmailID (email);
        checkout.setConsumerMobileNumber (phoneNo);
        checkout.setConsumerAccountNo (acctNo);
        checkout.addCartItem("FIRST","1.00","0.0", "0.0", "IMPL2019", "ANDROID", "E-MANDATE","www.instantmudra.com");
        checkout.setTransactionAmount ("1.00");
        checkout.setPaymentInstructionAction("Y");
        checkout.setPaymentInstructionType("F");
        checkout.setPaymentInstructionLimit("1000.00");
        checkout.setPaymentInstructionFrequency("ADHO");
        checkout.setPaymentInstructionStartDateTime(formattedDate);
        checkout.setPaymentInstructionEndDateTime("07-12-2036");
        checkout.setConsumerAccountHolderName(acctHolder);
        checkout.setConsumerAccountType(acctType);
        checkout.setPaymentInstrumentIFSC(ifsc);
        checkout.setPaymentInstructionDebitFlag("Y");*/


        /*Intent authIntent = new Intent(this, PaymentModesActivity.class);
        mandateFlag = false;
        Log.d("Checkout Request Object", checkout.getMerchantRequestPayload().toString());
        authIntent.putExtra(Constant.ARGUMENT_DATA_CHECKOUT, checkout);
        authIntent.putExtra(PaymentActivity.EXTRA_PUBLIC_KEY, "1234-6666-6789-56");
        authIntent.putExtra(PaymentActivity.EXTRA_REQUESTED_PAYMENT_MODE,
                PaymentActivity.PAYMENT_METHOD_DEFAULT);
        startActivityForResult(authIntent, PaymentActivity.REQUEST_CODE);*/


        Intent authIntent = PaymentModesActivity.Factory.getAuthorizationIntent(getApplicationContext(), true);
        Log.d("Checkout Request Object",
                checkout.getMerchantRequestPayload().toString());
        authIntent.putExtra(Constant.ARGUMENT_DATA_CHECKOUT, checkout);
        authIntent.putExtra(PaymentActivity.EXTRA_PUBLIC_KEY, "1234-6666-6789-56");
        authIntent.putExtra(PaymentActivity.EXTRA_REQUESTED_PAYMENT_MODE,
                PaymentActivity.PAYMENT_METHOD_DEFAULT);
        PaymentModesActivity.Settings settings = new PaymentModesActivity.Settings();
        authIntent.putExtra(Constant.ARGUMENT_DATA_SETTING, settings);
        startActivityForResult(authIntent, PaymentActivity.REQUEST_CODE);


    }


    private void getUserLoanDetails(final String sanctionedAmt){


        final ProgressDialog progressBar11;
        progressBar11 = new ProgressDialog(RegisterMandate.this, R.style.AppCompatProgressDialogStyle);
        progressBar11.setCancelable(false);
        progressBar11.setMessage("Please Wait...");
        progressBar11.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar11.show();


        RequestQueue queue = Volley.newRequestQueue(this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("user_id", sharedPreference.getUserId());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Utility.BASE_URL+"/getUserLoanBankDetails", new JSONObject(postParam), new com.android.volley.Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                progressBar11.dismiss();
                if(response.optBoolean("status"))
                {
                    Log.i("Result",":"+response.optJSONObject("result"));

                    JSONObject jsonObject = response.optJSONObject("result");
                    String transactionIdentifier = jsonObject.optString("user_id");
                    String firstName = jsonObject.optString("first_name");
                    String lastName = jsonObject.optString("last_name");
                    String consumerIdentifier = firstName+" "+lastName;
                    String consumerEmailID =  jsonObject.optString("official_mail");
                    String consumerMobileNumber = jsonObject.optString("phone_no");
                    String consumerAccountNo = jsonObject.optString("account_number");
                    String consumerAccountHolderName =jsonObject.optString("account_holder_name");
                    String consumerAccountType = jsonObject.optString("account_type");
                    String paymentInstrumentIFSC = jsonObject.optString("ifsc_code");

                    registerMandate(transactionIdentifier, firstName, consumerEmailID, consumerMobileNumber,consumerAccountNo,
                            consumerAccountHolderName, consumerAccountType, paymentInstrumentIFSC, sanctionedAmt);


                }
                else
                {
                    Toast.makeText(RegisterMandate.this, response.optString("msg"), Toast.LENGTH_SHORT).show();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                progressBar11.dismiss();
                Toast.makeText(RegisterMandate.this, "Something went wrong, Please try after some time !", Toast.LENGTH_SHORT).show();
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


    private void saveUserMandateLogs(){

     /*   final ProgressDialog progressBar11;
        progressBar11 = new ProgressDialog(RegisterMandate.this, R.style.AppCompatProgressDialogStyle);
        progressBar11.setCancelable(false);
        progressBar11.setMessage("Please Wait...");
        progressBar11.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar11.show();*/


        RequestQueue queue = Volley.newRequestQueue(this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("user_id", sharedPreference.getUserId());
        postParam.put("transaction_identifier", transactionIdentifier);
        postParam.put("transaction_reference", transactionReference);


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Utility.BASE_URL+"/saveEmandateLog", new JSONObject(postParam), new com.android.volley.Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                //progressBar11.dismiss();

            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                //progressBar11.dismiss();
                //Toast.makeText(RegisterMandate.this, "Something went wrong, Please try after some time !", Toast.LENGTH_SHORT).show();
            }

        }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "text/html; charset=utf-8");
                return headers;
            }


        };

        jsonObjReq.setTag(TAG);
        // Adding request to request queue
        queue.add(jsonObjReq);

    }

    private void saveMandateDetails(){

        final ProgressDialog progressBar11;
        progressBar11 = new ProgressDialog(RegisterMandate.this, R.style.AppCompatProgressDialogStyle);
        progressBar11.setCancelable(false);
        progressBar11.setMessage("Please Wait...");
        progressBar11.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar11.show();


        RequestQueue queue = Volley.newRequestQueue(this);


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Utility.BASE_URL+"/mandateDetails", new JSONObject(mandateDetailsObject), new com.android.volley.Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                progressBar11.dismiss();
                if(response.optBoolean("status"))
                {
                    Log.i("Mandate",":"+response.optJSONObject("result"));

                    Toast.makeText(RegisterMandate.this, "Congratulations, Mandate Registered ! ", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                    finish();

                }
                else
                {
                    Toast.makeText(RegisterMandate.this, response.optString("msg"), Toast.LENGTH_SHORT).show();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                progressBar11.dismiss();
                Toast.makeText(RegisterMandate.this, "Something went wrong, Please try after some time !", Toast.LENGTH_SHORT).show();
            }

        }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "text/html; charset=utf-8");
                return headers;
            }


        };

        jsonObjReq.setTag(TAG);
        // Adding request to request queue
        queue.add(jsonObjReq);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PaymentActivity.REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == PaymentActivity.RESULT_OK) {
                Log.d(TAG, "Result Code :" + RESULT_OK);
                if (data != null) {

                    try {
                        Checkout checkout_res = (Checkout) data
                                .getSerializableExtra(Constant
                                        .ARGUMENT_DATA_CHECKOUT);
                        Log.d("Checkout Response Obj", checkout_res.getMerchantResponsePayload().toString());


                        String transactionType = checkout_res.
                                getMerchantRequestPayload().getTransaction().getType();
                        String transactionSubType = checkout_res.
                                getMerchantRequestPayload().getTransaction().getSubType();
                        if (transactionType != null && transactionType.equalsIgnoreCase(PaymentActivity.TRANSACTION_TYPE_PREAUTH)
                                && transactionSubType != null && transactionSubType
                                .equalsIgnoreCase(PaymentActivity.TRANSACTION_SUBTYPE_RESERVE)) {
                            // Transaction Completed and Got SUCCESS
                            if (checkout_res.getMerchantResponsePayload()
                                    .getPaymentMethod().getPaymentTransaction()
                                    .getStatusCode().equalsIgnoreCase(PaymentActivity.TRANSACTION_STATUS_PREAUTH_RESERVE_SUCCESS)) {
                                Toast.makeText(getApplicationContext(), "Transaction Status - Success", Toast.LENGTH_SHORT).show();
                                Log.v("TRANSACTION STATUS=>", "SUCCESS");

                                /**
                                 * TRANSACTION STATUS - SUCCESS (status code
                                 * 0200 means success), NOW MERCHANT CAN PERFORM
                                 * ANY OPERATION OVER SUCCESS RESULT
                                 */

                                if (checkout_res.getMerchantResponsePayload()
                                        .getPaymentMethod().getPaymentTransaction().getInstruction().getStatusCode().equalsIgnoreCase("")) {
                                    /**
                                     * SI TRANSACTION STATUS - SUCCESS (status
                                     * code 0200 means success)
                                     */
                                    saveUserMandateLogs();
                                    Log.v("TRANSACTION SI STATUS=>",
                                            "SI Transaction Not Initiated");
                                }

                            } // Transaction Completed and Got FAILURE

                            else {
                                // some error from bank side
                                saveUserMandateLogs();
                                Log.v("TRANSACTION STATUS=>", "FAILURE");
                                Toast.makeText(getApplicationContext(),
                                        "Transaction Status - Failure",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } else {

                            // Transaction Completed and Got SUCCESS
                            if (checkout_res.getMerchantResponsePayload().getPaymentMethod().getPaymentTransaction().getStatusCode().equalsIgnoreCase(
                                    PaymentActivity.TRANSACTION_STATUS_SALES_DEBIT_SUCCESS)) {
                                Toast.makeText(getApplicationContext(), "Transaction Status - Success", Toast.LENGTH_SHORT).show();
                                Log.v("TRANSACTION STATUS=>", "SUCCESS");

                                /**
                                 * TRANSACTION STATUS - SUCCESS (status code
                                 * 0300 means success), NOW MERCHANT CAN PERFORM
                                 * ANY OPERATION OVER SUCCESS RESULT
                                 */

                                if (checkout_res.getMerchantResponsePayload().
                                        getPaymentMethod().getPaymentTransaction().
                                        getInstruction().getStatusCode()
                                        .equalsIgnoreCase("")) {
                                    /**
                                     * SI TRANSACTION STATUS - SUCCESS (status
                                     * code 0300 means success)
                                     */
                                    saveUserMandateLogs();
                                    Log.v("TRANSACTION SI STATUS=>",
                                            "SI Transaction Not Initiated");


                                    mandateDetailsObject = new HashMap<String, String>();

                                    mandateDetailsObject.put("user_id", sharedPreference.getUserId());
                                    mandateDetailsObject.put("status_code", checkout_res
                                            .getMerchantResponsePayload().getPaymentMethod()
                                            .getPaymentTransaction().getStatusCode());
                                    mandateDetailsObject.put("status", checkout_res
                                            .getMerchantResponsePayload().getPaymentMethod()
                                            .getPaymentTransaction().getStatusMessage());
                                    mandateDetailsObject.put("amount", checkout_res
                                            .getMerchantResponsePayload().getPaymentMethod().getPaymentTransaction().getAmount());
                                    mandateDetailsObject.put("date_time", checkout_res.
                                            getMerchantResponsePayload().getPaymentMethod()
                                            .getPaymentTransaction().getDateTime());
                                    mandateDetailsObject.put("merchant_transaction_identifier", checkout_res.getMerchantResponsePayload()
                                            .getMerchantTransactionIdentifier());
                                    mandateDetailsObject.put("identifier", checkout_res
                                            .getMerchantResponsePayload().getPaymentMethod()
                                            .getPaymentTransaction().getIdentifier());
                                    mandateDetailsObject.put("bank_selection_code", checkout_res
                                            .getMerchantResponsePayload().getPaymentMethod()
                                            .getBankSelectionCode());
                                    mandateDetailsObject.put("bank_reference_identifier", checkout_res
                                            .getMerchantResponsePayload().getPaymentMethod()
                                            .getPaymentTransaction().getBankReferenceIdentifier());
                                    mandateDetailsObject.put("si_mandate_id", checkout_res
                                            .getMerchantResponsePayload().getPaymentMethod()
                                            .getPaymentTransaction().getInstruction().getId());
                                    mandateDetailsObject.put("is_mandate_registered", "1");

                                    //Log.i("rr", "---->" + mandateDetailsObject);
                                    mandateFlag = true;



                                    String result = "StatusCode : " + checkout_res
                                            .getMerchantResponsePayload().getPaymentMethod()
                                            .getPaymentTransaction().getStatusCode()
                                            + "\nStatusMessage : " + checkout_res
                                            .getMerchantResponsePayload().getPaymentMethod()
                                            .getPaymentTransaction().getStatusMessage()
                                            + "\nErrorMessage : " + checkout_res
                                            .getMerchantResponsePayload().getPaymentMethod()
                                            .getPaymentTransaction().getErrorMessage()
                                            + "\nAmount : " + checkout_res
                                            .getMerchantResponsePayload().getPaymentMethod().getPaymentTransaction().getAmount()
                                            + "\nDateTime : " + checkout_res.
                                            getMerchantResponsePayload().getPaymentMethod()
                                            .getPaymentTransaction().getDateTime()
                                            + "\nMerchantTransactionIdentifier : "
                                            + checkout_res.getMerchantResponsePayload()
                                            .getMerchantTransactionIdentifier()
                                            + "\nIdentifier : " + checkout_res
                                            .getMerchantResponsePayload().getPaymentMethod()
                                            .getPaymentTransaction().getIdentifier()
                                            + "\nBankSelectionCode : " + checkout_res
                                            .getMerchantResponsePayload().getPaymentMethod()
                                            .getBankSelectionCode()
                                            + "\nBankReferenceIdentifier : " + checkout_res
                                            .getMerchantResponsePayload().getPaymentMethod()
                                            .getPaymentTransaction().getBankReferenceIdentifier()
                                            + "\nRefundIdentifier : " + checkout_res
                                            .getMerchantResponsePayload().getPaymentMethod()
                                            .getPaymentTransaction().getRefundIdentifier()
                                            + "\nBalanceAmount : " + checkout_res
                                            .getMerchantResponsePayload().getPaymentMethod()
                                            .getPaymentTransaction().getBalanceAmount()
                                            + "\nInstrumentAliasName : " + checkout_res
                                            .getMerchantResponsePayload().getPaymentMethod()
                                            .getInstrumentAliasName()
                                            + "\nSI Mandate Id : " + checkout_res
                                            .getMerchantResponsePayload().getPaymentMethod()
                                            .getPaymentTransaction().getInstruction().getId()
                                            + "\nSI Mandate Status : " + checkout_res
                                            .getMerchantResponsePayload().getPaymentMethod()
                                            .getPaymentTransaction().getInstruction().getStatusCode()
                                            + "\nSI Mandate Error Code : " + checkout_res
                                            .getMerchantResponsePayload().getPaymentMethod()
                                            .getPaymentTransaction().getInstruction().getErrorcode();
                                    Log.i("result", ":" + result);

                                    mandateDetailsObject.put("mandate_registered_response", result);
                                    saveMandateDetails();


                                } else if (checkout_res.getMerchantResponsePayload()
                                        .getPaymentMethod().getPaymentTransaction()
                                        .getInstruction()
                                        .getStatusCode().equalsIgnoreCase(
                                                PaymentActivity.TRANSACTION_STATUS_SALES_DEBIT_SUCCESS)) {

                                    /**
                                     * SI TRANSACTION STATUS - SUCCESS (status
                                     * code 0300 means success)
                                     */
                                    Log.v("TRANSACTION SI STATUS=>", "SUCCESS");
                                } else {
                                    /**
                                     * SI TRANSACTION STATUS - Failure (status
                                     * code OTHER THAN 0300 means failure)
                                     */
                                    saveUserMandateLogs();
                                    Log.v("TRANSACTION SI STATUS=>", "FAILURE");
                                }
                                // Transaction Completed and Got FAILURE
                            } else if (checkout_res
                                    .getMerchantResponsePayload().getPaymentMethod().getPaymentTransaction().getStatusCode().equalsIgnoreCase(
                                            PaymentActivity.TRANSACTION_STATUS_DIGITAL_MANDATE_SUCCESS
                                    )) {
                                Toast.makeText(getApplicationContext(), "Transaction Status - Success", Toast.LENGTH_SHORT).show();
                                Log.v("TRANSACTION STATUS=>", "SUCCESS");

                                /**
                                 * TRANSACTION STATUS - SUCCESS (status code
                                 * 0398 means success), NOW MERCHANT CAN PERFORM
                                 * ANY OPERATION OVER SUCCESS RESULT
                                 */

                                if (checkout_res.getMerchantResponsePayload().
                                        getPaymentMethod().getPaymentTransaction().
                                        getInstruction().getId() != null
                                        && !checkout_res
                                        .getMerchantResponsePayload().getPaymentMethod().getPaymentTransaction().getInstruction().getId().isEmpty()) {
                                    /**
                                     * SI TRANSACTION STATUS - SUCCESS (status
                                     * code 0300 means success)
                                     */
                                    Log.v("TRANSACTION SI STATUS=>",
                                            "INITIATED");
                                } else {

                                    /**
                                     * SI TRANSACTION STATUS - Failure (status
                                     * code OTHER THAN 0300 means failure)
                                     */
                                    saveUserMandateLogs();
                                    Log.v("TRANSACTION SI STATUS=>", "FAILURE");

                                }
                            } else {
                                // some error from bank side

                                saveUserMandateLogs();
                                Log.v("TRANSACTION STATUS=>", "FAILURE");
                                Toast.makeText(getApplicationContext(), "Transaction Status - Failure", Toast.LENGTH_SHORT).show();
                            }

                        }
                        String result = "StatusCode : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getPaymentTransaction().getStatusCode()
                                + "\nStatusMessage : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getPaymentTransaction().getStatusMessage()
                                + "\nErrorMessage : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getPaymentTransaction().getErrorMessage()
                                + "\nAmount : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod().getPaymentTransaction().getAmount()
                                + "\nDateTime : " + checkout_res.
                                getMerchantResponsePayload().getPaymentMethod()
                                .getPaymentTransaction().getDateTime()
                                + "\nMerchantTransactionIdentifier : "
                                + checkout_res.getMerchantResponsePayload()
                                .getMerchantTransactionIdentifier()
                                + "\nIdentifier : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getPaymentTransaction().getIdentifier()
                                + "\nBankSelectionCode : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getBankSelectionCode()
                                + "\nBankReferenceIdentifier : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getPaymentTransaction().getBankReferenceIdentifier()
                                + "\nRefundIdentifier : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getPaymentTransaction().getRefundIdentifier()
                                + "\nBalanceAmount : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getPaymentTransaction().getBalanceAmount()
                                + "\nInstrumentAliasName : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getInstrumentAliasName()
                                + "\nSI Mandate Id : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getPaymentTransaction().getInstruction().getId()
                                + "\nSI Mandate Status : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getPaymentTransaction().getInstruction().getStatusCode()
                                + "\nSI Mandate Error Code : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getPaymentTransaction().getInstruction().getErrorcode();

                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            } else if (resultCode == PaymentActivity.RESULT_ERROR) {
                Log.d(TAG, "got an error");

                if (data.hasExtra(PaymentActivity.RETURN_ERROR_CODE) &&
                        data.hasExtra(PaymentActivity.RETURN_ERROR_DESCRIPTION)) {
                    String error_code = (String) data
                            .getStringExtra(PaymentActivity.RETURN_ERROR_CODE);
                    String error_desc = (String) data
                            .getStringExtra(PaymentActivity.RETURN_ERROR_DESCRIPTION);

                    Toast.makeText(getApplicationContext(), " Got error :"
                            + error_code + "--- " + error_desc, Toast.LENGTH_SHORT)
                            .show();
                    Log.d(TAG + " Code=>", error_code);
                    Log.d(TAG + " Desc=>", error_desc);

                }
            } else if (resultCode == PaymentActivity.RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Transaction Aborted by User",
                        Toast.LENGTH_SHORT).show();
                Log.d(TAG, "User pressed back button");

            }
        }

    }




}
