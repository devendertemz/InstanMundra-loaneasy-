package com.loaneasy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.loaneasy.network.ApiRequest;
import com.loaneasy.utils.UserSharedPreference;
import com.loaneasy.utils.Utility;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DoneActivity extends AppCompatActivity {

    UserSharedPreference sharedPreference;
    private TextView tvAmount, tvOrderId, tvPayableAmount, tvRepayAmount, orderMessage;
    private String TAG = "DoneActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);
        sharedPreference = new UserSharedPreference(this);

        tvOrderId = findViewById(R.id.tvOrderId);
        tvAmount = findViewById(R.id.tvAmount);
        tvPayableAmount = findViewById(R.id.tvPayableAmount);
        tvRepayAmount = findViewById(R.id.tvRepayAmount);
        orderMessage = findViewById(R.id.tvOrderMessage);

        String sOrderId="", sAmount = "", sPayableAmount = "", sRepayAmount = "", appliedDays="";

        DecimalFormat formatter = new DecimalFormat("#,##,###");

        if (getIntent().getExtras() != null) {
            sOrderId = getIntent().getStringExtra("orderid");
            sAmount = getIntent().getStringExtra("loanAmount");
            sPayableAmount = getIntent().getStringExtra("payableAmount");
            appliedDays = getIntent().getStringExtra("days_returning");
            sRepayAmount = getIntent().getStringExtra("repayAmount");

            tvOrderId.setText(sOrderId);
            tvAmount.setText("Rs. "+sAmount);
            tvPayableAmount.setText("Rs. "+sPayableAmount);
            tvRepayAmount.setText("Rs. "+sRepayAmount);
        }

        //sendDataConfirmSMS(sharedPreference.getUserPhoneNo(), sOrderId);

        orderMessage.setText("Please save your loan application reference number : " + sOrderId);
        sendLoanIdUniqueId(sOrderId );


        findViewById(R.id.tvDone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }



    private void sendLoanIdUniqueId(String loanId) {
       /* final ProgressDialog progressBar11;
        progressBar11 = new ProgressDialog(OfficialDetailsActivity.this);
        progressBar11.setCancelable(false);
        progressBar11.setMessage("Please Wait...");
        progressBar11.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar11.show();*/




        RequestQueue queue = Volley.newRequestQueue(this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("user_id", sharedPreference.getUserId());
        postParam.put("order_id", loanId);


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Utility.BASE_URL+"/sendConfirmWithUniqueId", new JSONObject(postParam), new com.android.volley.Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                //progressBar11.dismiss();
               /* if(response.optBoolean("status"))
                {

                }
                else
                {
                    Toast.makeText(DoneActivity.this, response.optString("msg"), Toast.LENGTH_SHORT).show();
                }*/
            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Log.i("VolleyError","="+error);
                Log.i("VolleyError","="+error.getMessage());
                //progressBar11.dismiss();
                Toast.makeText(DoneActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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

    public void sendDataConfirmSMS(String phone, String orderid){

        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(Utility.BASE_URL).build();

        ApiRequest registrationApiRequest = restAdapter.create(ApiRequest.class);


        registrationApiRequest.sendingConfirmationSMS(phone, orderid, new Callback<Response>() {
            @Override
            public void success(Response result, Response response) {
                BufferedReader bufferedReader = null;
                String output = "";

                try {
                    Log.d("TAG111","--ConfirmationSMS_response-->"+output);
                    bufferedReader = new BufferedReader(new InputStreamReader(result.getBody().in())) ;
                    output = bufferedReader.readLine();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("TAG111","--error_ConfirmationSMS-->"+error.getLocalizedMessage());
            }
        });
    }
}
