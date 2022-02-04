package com.loaneasy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.loaneasy.utils.UserSharedPreference;
import com.loaneasy.utils.Utility;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ravindra on 27-Apr-19.
 */
public class RazorPayCheckout extends AppCompatActivity implements PaymentResultListener {


    String sAmount, sAddrId, loanId, transactionId;
    String TAG = "RazorPayCheckout";
    UserSharedPreference sharedPreference;
    ProgressDialog progressBar;
    private HashMap hashMap;
    String totalCartValue, addressId;
    private int nStatusCode;
    private double totalAMount;
    //private String url = "https://instantmudra.com/admin/login/repayment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        sharedPreference = new UserSharedPreference(this);

        if (getIntent().getExtras() != null) {
            //double loanAmount = Integer.parseInt(getIntent().getExtras().getString("loan_amount"));
            double loanAmount = Double.parseDouble(getIntent().getExtras().getString("loan_amount"));


            Log.i("loanAMount","----"+loanAmount);
            double onlineCharges = (loanAmount *2.5)/100;
            Log.i("onlineCharges","----"+onlineCharges);
            totalAMount = loanAmount + onlineCharges;
            Log.i("totalAmount","----"+totalAMount);
            double amountInPaisa = totalAMount * 100;



            sAmount = String.valueOf(Math.round(amountInPaisa));
            Log.i("AmountInPaise",sAmount);
           // sAmount = "" + (Double.parseDouble(getIntent().getExtras().getString("loan_amount")) * 100);
            //sAddrId = getIntent().getExtras().getString("addrId");
            loanId = getIntent().getExtras().getString("loan_id");
            //callRazorPay("qbsbcavchavGhth");
        }
        startPayment();
    }





    public void startPayment() {
        /*
          You need to pass current activity in order to let Razorpay create RazorPayCheckout
         */
        final AppCompatActivity activity = this;

        final Checkout co = new Checkout();

        try {

            JSONObject options = new JSONObject();
            options.put("name", "Instant Mudra");
            options.put("description", "Loan Repay");
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", sAmount);
            options.put("payment_capture", true);
            Log.i("userEmail","="+sharedPreference.getUserEmail());
            JSONObject preFill = new JSONObject();
            preFill.put("email", sharedPreference.getUserEmail());
            preFill.put("contact", sharedPreference.getUserPhoneNo());

            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String paymentId) {
        try {
            Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, "Order placed successfully " + s, Toast.LENGTH_SHORT).show();
            //getCartValue();
            //callRazorPay(s);
            Log.i("payId","---"+paymentId);
            transactionId = paymentId;


            //sendUserPaymentDetails();


        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentSuccess", e);
        }
    }

    @Override
    public void onPaymentError(int i, String error) {
        try {
            //Toast.makeText(this, "Payment failed: " + i + " " + error, Toast.LENGTH_SHORT).show();
            Log.i("PaymentError",error);
            //Toast.makeText(this, "Payment failed", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Log.e(TAG, "PaymentException", e);
        }
    }




    private void sendUserPaymentDetails() {
        final ProgressDialog progressBar11;
        progressBar11 = new ProgressDialog(RazorPayCheckout.this, R.style.AppCompatProgressDialogStyle);
        progressBar11.setCancelable(false);
        progressBar11.setMessage("Please Wait...");
        progressBar11.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar11.show();



        RequestQueue queue = Volley.newRequestQueue(this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("order_id", loanId);
        postParam.put("amount", String.valueOf(totalAMount));
        postParam.put("payment_id", transactionId);




        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Utility.BASE_URL, new JSONObject(postParam), new com.android.volley.Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                progressBar11.dismiss();

                startActivity(new Intent(RazorPayCheckout.this, HomeActivity.class));
                finish();
                /*if(response.optString("Status").equalsIgnoreCase("success"))
                {
                    Log.i(TAG,"Inside if condition");

                    Toast.makeText(RazorPayCheckout.this, response.optString("Status"), Toast.LENGTH_SHORT).show();


                    startActivity(new Intent(RazorPayCheckout.this, HomeActivity.class));
                    finish();

                }
                *//*else
                {
                    Toast.makeText(RazorPayCheckout.this, response.optString("msg"), Toast.LENGTH_SHORT).show();
                }*/
            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                progressBar11.dismiss();
                Toast.makeText(RazorPayCheckout.this, "Something went wrong, Please try after some time !", Toast.LENGTH_SHORT).show();
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


    private void getCartValue() {
        final ProgressDialog progressBar;
        progressBar = new ProgressDialog(this, R.style.AppCompatProgressDialogStyle);
        progressBar.setCancelable(false);
        progressBar.setMessage("Please Wait...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Utility.BASE_URL + "/cart", null, new com.android.volley.Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                //Log.d(TAG, "--addtocart_response-->" + response.toString());
                progressBar.dismiss();

                totalCartValue = response.optString("amount_payable");
                //getAddress();
            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "addingCartError: " + error.getMessage());
                if (error != null) {
                    NetworkResponse networkResponse = error.networkResponse;
                    int in = networkResponse.statusCode;
                    Toast.makeText(RazorPayCheckout.this, "" + in, Toast.LENGTH_SHORT).show();
                }
                progressBar.dismiss();
                Toast.makeText(RazorPayCheckout.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }

        }) {

            @Override
            protected com.android.volley.Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                int i = response.statusCode;
                Toast.makeText(RazorPayCheckout.this, "" + i, Toast.LENGTH_SHORT).show();
                return super.parseNetworkResponse(response);
            }

        };

        jsonObjReq.setTag(TAG);
        // Adding request to request queue
        queue.add(jsonObjReq);

    }



    private void getAddress() {
        final ProgressDialog progressBar;
        progressBar = new ProgressDialog(this, R.style.AppCompatProgressDialogStyle);
        progressBar.setCancelable(false);
        progressBar.setMessage("Please Wait...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utility.BASE_URL + "/cart",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);
                        progressBar.dismiss();

                        try {
                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.optJSONObject(i);
                                addressId = object.optString("id");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "" + error.getMessage());
                progressBar.dismiss();
                if (error != null) {
                    NetworkResponse networkResponse = error.networkResponse;
                    int in = networkResponse.statusCode;
                    if (in == 402)
                        Toast.makeText(RazorPayCheckout.this, "Product already present in your cart", Toast.LENGTH_SHORT).show();
                    else if (in == 400)
                        Toast.makeText(RazorPayCheckout.this, "Product is currently Out of stock!", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(RazorPayCheckout.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        }) {

            @Override
            public byte[] getBody() throws AuthFailureError {
                return null;
            }
        };

        stringRequest.setTag(TAG);
        // Adding request to request queue
        queue.add(stringRequest);

    }

    ///////razorPay
    private void callRazorPay(String payId) {
        final ProgressDialog progressBar;
        progressBar = new ProgressDialog(RazorPayCheckout.this, R.style.AppCompatProgressDialogStyle);
        progressBar.setCancelable(false);
        progressBar.setMessage("Please Wait...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();

        RequestQueue queue = Volley.newRequestQueue(RazorPayCheckout.this);

        final JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("pay_id", payId);
            jsonObject.put("address_id", sAddrId);
            jsonObject.put("amount", sAmount);
            jsonObject.put("type", "buyProducts");
            jsonObject.put("selection", new JSONArray());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                Utility.BASE_URL + "/razorPay", new com.android.volley.Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "--razorPay_response-->" + response.toString());
                progressBar.dismiss();

                if (nStatusCode == 200) {
                    finish();
                    Toast.makeText(RazorPayCheckout.this, "Order placed successfully", Toast.LENGTH_SHORT).show();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.dismiss();
                Log.e(TAG, "razorPayError: " + error.getMessage());
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    int in = networkResponse.statusCode;
                    Log.e(TAG, "razorPayError_code: " + in);
                }
                Toast.makeText(RazorPayCheckout.this, "Something went wrong !", Toast.LENGTH_SHORT).show();

            }

        }) {

            @Override
            public byte[] getBody() throws AuthFailureError {
                return jsonObject.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            protected com.android.volley.Response<String> parseNetworkResponse(NetworkResponse response) {
                if (response != null) {
                    nStatusCode = response.statusCode;
                }
                return super.parseNetworkResponse(response);
            }


        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                180000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        jsonObjReq.setTag(TAG);
        // Adding request to request queue
        queue.add(jsonObjReq);

    }
}
