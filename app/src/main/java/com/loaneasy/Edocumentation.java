package com.loaneasy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
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
import com.gspl.leegalitysdk.Leegality;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.paynimo.android.payment.PaymentActivity.REQUEST_CODE;

public class Edocumentation extends AppCompatActivity {


    private String TAG = "Edocumentation";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        //getEDocument();


        Intent intent = new Intent(getApplicationContext(), Leegality.class);
        intent.putExtra("url", "https://sandbox.leegality.com/sign/9b8fcad2-e41c-48c1-97d4-33872fc883ad");
        startActivityForResult(intent, REQUEST_CODE);

    }


    private void getEDocument(){

        /*final ProgressDialog progressBar;
        progressBar = new ProgressDialog(getApplicationContext(), R.style.AppCompatProgressDialogStyle);
        progressBar.setCancelable(false);
        progressBar.setMessage("Please Wait...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();*/

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                "https://sandbox.leegality.com/api/v2.1/sign/request?documentId=4CZNG7E",
                null, new com.android.volley.Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                Log.d(TAG, "IMUDRA"+response.toString());
                //progressBar.dismiss();

                try{
                //JSONArray docRequests = response.optJSONArray("requests");
                JSONObject data = response.optJSONObject("data");

                Log.i("data","="+data);
                JSONArray jsonArray = data.optJSONArray("requests");
                Log.i("requests","="+jsonArray);
                JSONObject docRequest = jsonArray.getJSONObject(0);
                Log.i("docRequest","="+docRequest);

                String signUrl = docRequest.optString("signUrl");
                Log.i("signUrl","="+signUrl);


                    Intent intent = new Intent(getApplicationContext(), Leegality.class);
                    intent.putExtra("url", signUrl);
                    startActivityForResult(intent, REQUEST_CODE);




                } catch (Exception e){
                    e.printStackTrace();
                }


            }
        }, new com.android.volley.Response.ErrorListener()

        {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "PrestaError: " + error.getMessage());
                //progressBar.dismiss();
                Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }

        })

        {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                String credentials = "MSXDHTDHUJ83SAYHPMGRI3MH6GTECSYW:MSXDHTDHUJ83SAYHPMGRI3MH6GTECSYW";
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

                headers.put("X-Auth-Token", "aPAsoGcy93urxG8tv7ljz9lSYip01jRF");
                return headers;
            }

        };

        jsonObjReq.setTag(TAG);
        // Adding request to request queue
        queue.add(jsonObjReq);



    }



}
