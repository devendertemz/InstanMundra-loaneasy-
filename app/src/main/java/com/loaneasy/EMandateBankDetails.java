package com.loaneasy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.cardview.widget.CardView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.loaneasy.Model.NetbankingList;
import com.loaneasy.Service.GPSTracker;
import com.loaneasy.network.Api;
import com.loaneasy.network.RetrofitClient;
import com.loaneasy.utils.ConnectionCheck;
import com.loaneasy.utils.UserSharedPreference;
import com.loaneasy.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EMandateBankDetails extends AppCompatActivity {

    private AppCompatSpinner spBankName,spAccType;
    private CardView cardAxis,cardHdfc,cardIcici,cardSbi,cardOthers;
    private ImageView ivCheckAxis,ivCheckHdfc,ivCheckicici,ivCheckSbi,ivCheckOthers;
    private int nSelectedBank, isEmandate=0;

    //private TextView submitUserBankDetails;
    private String bankName = "Select Bank", accountType, TAG= "EMandateBankDetails";
    private Button submitUserBankDetails;
    private EditText accountHolderName,accountNumber,ifscCode;
    UserSharedPreference sharedPreference;

    private boolean updateFlag = false;
    private RadioGroup radioGroup;
    private RadioButton radio_netbanking, radio_debit;
    private ArrayList<NetbankingList> netbankingLists;
    private ArrayList<String> banklist = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emandate);

        Intent intent = getIntent();
        if (intent!= null){

            updateFlag = intent.getExtras().getBoolean("updateBank");
        }

        sharedPreference = new UserSharedPreference(this);

        submitUserBankDetails =(Button) findViewById(R.id.btSubmitBankDetails);
        spBankName = findViewById(R.id.spBankName);
        spAccType = findViewById(R.id.spAccType);
        ivCheckAxis = findViewById(R.id.ivCheckAxis);
        ivCheckHdfc = findViewById(R.id.ivCheckHdfc);
        ivCheckicici = findViewById(R.id.ivCheckicici);
        ivCheckSbi = findViewById(R.id.ivCheckSbi);
        ivCheckOthers = findViewById(R.id.ivCheckOthers);
        cardAxis = findViewById(R.id.cardAxis);
        cardHdfc = findViewById(R.id.cardHdfc);
        cardIcici = findViewById(R.id.cardIcici);
        cardSbi = findViewById(R.id.cardSbi);
        cardOthers = findViewById(R.id.cardOthers);

        accountHolderName = (EditText) findViewById(R.id.etAccountHolderName);
        accountNumber = (EditText) findViewById(R.id.etAccountNumber);
        ifscCode = (EditText) findViewById(R.id.etIfsc);
        radioGroup = (RadioGroup)findViewById(R.id.groupradio);
        radio_netbanking = (RadioButton) findViewById(R.id.radio_netbanking);
        radio_debit = (RadioButton) findViewById(R.id.radio_debit);
        radioGroup.clearCheck();
        radio_netbanking.setChecked(true);

        if(updateFlag)
        {
            getUserBankDetails();
        }

        try{
            getNetBankingList();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Add the Listener to the Submit Button
        radio_netbanking.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                netbankingLists.clear();
                banklist.clear();
                getNetBankingList();
                radioGroup.clearCheck();
                radio_netbanking.setChecked(true);
                radio_debit.setChecked(false);
            }
        });

        radio_debit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                netbankingLists.clear();
                banklist.clear();
                getDebitCardList();
                radioGroup.clearCheck();
                radio_debit.setChecked(true);
                radio_netbanking.setChecked(false);
            }
        });

        submitUserBankDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (new ConnectionCheck(EMandateBankDetails.this).isNetworkAvailable()) {



                    if(validate())
                    {
                        if(updateFlag)
                        {
                            UpdateUserBankDetails();
                        }
                        else
                        {
                            sendUserBankDetails();
                        }

                    }
                }
                else{
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please Check Your Internet Connection", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });


//        ArrayAdapter<CharSequence> bankNameAdapter = new ArrayAdapter<CharSequence>(getApplicationContext(), R.layout.spinner_text, getResources().getStringArray(R.array.sarBankNames));
//        bankNameAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
//        spBankName.setAdapter(bankNameAdapter);
//
//
        spBankName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String getBankName = spBankName.getSelectedItem().toString();
                if(!getBankName.equalsIgnoreCase("Select Bank"));
                {

                    bankName = spBankName.getSelectedItem().toString();

                }

                Log.i("bankName","="+bankName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> accTypeAdapter = new ArrayAdapter<CharSequence>(getApplicationContext(), R.layout.spinner_text, getResources()
                .getStringArray(R.array.sarAccountType));
        accTypeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spAccType.setAdapter(accTypeAdapter);



        spAccType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {



                accountType= spAccType.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        cardAxis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nSelectedBank = 1;
                selectBank();
            }
        });

        cardHdfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nSelectedBank = 2;
                selectBank();
            }
        });

        cardIcici.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nSelectedBank = 3;
                selectBank();
            }
        });

        cardSbi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nSelectedBank = 4;
                selectBank();
            }
        });

        cardOthers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nSelectedBank = 5;
                selectBank();
            }
        });

    }

    private void getNetBankingList() {
        Api api = RetrofitClient.getRetrofit().create(Api.class);

        Call<String> call = api.getAllNetBanking("netbanking");

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.i("Responsestring", response.body().toString());
                //Toast.makeText()
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.i("onSuccess", response.body().toString());

                        String jsonresponse = response.body().toString();
                        spinJSON(jsonresponse);

                    } else {
                        Log.i("onEmptyResponse", "Returned empty response");//Toast.makeText(getContext(),"Nothing returned",Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }

    private void spinJSON(String response){

        try {

            NetbankingList spinnerModel = null;

            JSONObject obj = new JSONObject(response);
            if(obj.optString("msg").equals("success")){

                netbankingLists = new ArrayList<>();
                JSONArray dataArray  = obj.getJSONArray("bank_list");

                for (int i = 0; i < dataArray.length(); i++) {

                    spinnerModel = new NetbankingList();
                    JSONObject dataobj = dataArray.getJSONObject(i);

                    spinnerModel.setBank_name(dataobj.getString("bank_name"));

                    netbankingLists.add(spinnerModel);
                }

                for (int i = 0; i < netbankingLists.size(); i++){
                    banklist.add(netbankingLists.get(i).getBank_name().toString());
                }

                ArrayList<String> banklistdata = new ArrayList<String>();

                banklistdata.add("Axis Bank ENACH Net");
                banklistdata.add("ICICI Bank ENACH Net");
                banklistdata.add("HDFC BANK LTD Net");
                banklistdata.add("STATE BANK OF INDIA ENACH Net");
                banklist.removeAll(banklistdata);

                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(EMandateBankDetails.this, android.R.layout.simple_list_item_1, banklist);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                spBankName.setAdapter(spinnerArrayAdapter);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getDebitCardList() {
        Api api = RetrofitClient.getRetrofit().create(Api.class);

        Call<String> call = api.getAllNetBanking("debitcard");

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.i("Responsestring", response.body().toString());
                //Toast.makeText()
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.i("onSuccess", response.body().toString());

                        String jsonresponse = response.body().toString();
                        debitCardJSON(jsonresponse);

                    } else {
                        Log.i("onEmptyResponse", "Returned empty response");//Toast.makeText(getContext(),"Nothing returned",Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }

    private void debitCardJSON(String response){

        try {

            NetbankingList spinnerModel = null;

            JSONObject obj = new JSONObject(response);
            if(obj.optString("msg").equals("success")){

                netbankingLists = new ArrayList<>();
                JSONArray dataArray  = obj.getJSONArray("bank_list");

                for (int i = 0; i < dataArray.length(); i++) {

                    spinnerModel = new NetbankingList();
                    JSONObject dataobj = dataArray.getJSONObject(i);

                    spinnerModel.setBank_name(dataobj.getString("bank_name"));

                    netbankingLists.add(spinnerModel);
                }

                for (int i = 0; i < netbankingLists.size(); i++){
                    banklist.add(netbankingLists.get(i).getBank_name().toString());
                }

                ArrayList<String> banklistdata = new ArrayList<String>();

                banklistdata.add("Axis Bank ENACH Debit");
                banklistdata.add("ICICI Bank ENACH Debit");
                banklistdata.add("HDFC BANK ENACH Debit");
                banklistdata.add("STATE BANK OF INDIA ENACH Debit");
                banklist.removeAll(banklistdata);

                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(EMandateBankDetails.this, android.R.layout.simple_list_item_1, banklist);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                spBankName.setAdapter(spinnerArrayAdapter);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void sendUserBankDetails() {
        final ProgressDialog progressBar11;
        progressBar11 = new ProgressDialog(EMandateBankDetails.this, R.style.AppCompatProgressDialogStyle);
        progressBar11.setCancelable(false);
        progressBar11.setMessage("Please Wait...");
        progressBar11.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar11.show();


        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject postParam = new JSONObject();
        try {
            postParam.put("user_id", sharedPreference.getUserId());
            postParam.put("bank_name", bankName);
            postParam.put("account_type", accountType);
            postParam.put("account_number", accountNumber.getText().toString().trim());
            postParam.put("account_holder_name", accountHolderName.getText().toString().trim());
            postParam.put("ifsc_code", ifscCode.getText().toString().trim());
            postParam.put("e_mandate", String.valueOf(isEmandate));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Utility.BASE_URL+"/getUserBankDetails", postParam, new com.android.volley.Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                progressBar11.dismiss();
                if(response.optBoolean("status"))
                {
                    Log.i(TAG,"Inside if condition ");

                    Toast.makeText(EMandateBankDetails.this, response.optString("msg"), Toast.LENGTH_SHORT).show();


                    startActivity(new Intent(EMandateBankDetails.this, HomeActivity.class));
                    finish();

                }
                else
                {
                    Toast.makeText(EMandateBankDetails.this, response.optString("msg"), Toast.LENGTH_SHORT).show();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                progressBar11.dismiss();
                Toast.makeText(EMandateBankDetails.this, "Something went wrong, Please try after some time !", Toast.LENGTH_SHORT).show();
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



    private void getUserBankDetails() {
        final ProgressDialog progressBar11;
        progressBar11 = new ProgressDialog(EMandateBankDetails.this, R.style.AppCompatProgressDialogStyle);
        progressBar11.setCancelable(false);
        progressBar11.setMessage("Please Wait...");
        progressBar11.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar11.show();


        RequestQueue queue = Volley.newRequestQueue(this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("user_id", sharedPreference.getUserId());


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Utility.BASE_URL+"/UserBankDetails", new JSONObject(postParam), new com.android.volley.Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                progressBar11.dismiss();
                if(response.optBoolean("status"))
                {

                    JSONObject result = response.optJSONObject("result");
                    accountHolderName.setText(result.optString("account_holder_name"));
                    accountNumber.setText(result.optString("account_number"));
                    ifscCode.setText(result.optString("ifsc_code"));


                }

            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                progressBar11.dismiss();
                Toast.makeText(EMandateBankDetails.this, "Something went wrong, Please try after some time !", Toast.LENGTH_SHORT).show();
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


    private void UpdateUserBankDetails() {
        final ProgressDialog progressBar11;
        progressBar11 = new ProgressDialog(EMandateBankDetails.this, R.style.AppCompatProgressDialogStyle);
        progressBar11.setCancelable(false);
        progressBar11.setMessage("Please Wait...");
        progressBar11.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar11.show();


        RequestQueue queue = Volley.newRequestQueue(this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("user_id", sharedPreference.getUserId());
        postParam.put("bank_name", bankName);
        postParam.put("account_type", accountType);
        postParam.put("account_number", accountNumber.getText().toString().trim());
        postParam.put("account_holder_name", accountHolderName.getText().toString().trim());
        postParam.put("ifsc_code", ifscCode.getText().toString().trim());
        postParam.put("e_mandate", String.valueOf(isEmandate));


        Toast.makeText(getApplicationContext(), sharedPreference.getUserId(), Toast.LENGTH_SHORT).show();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Utility.BASE_URL+"/updateUserBankDetails", new JSONObject(postParam), new com.android.volley.Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                progressBar11.dismiss();
                if(response.optBoolean("status"))
                {


                    Toast.makeText(EMandateBankDetails.this, response.optString("msg"), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EMandateBankDetails.this, HomeActivity.class));
                    finish();

                }
                else
                {
                    Toast.makeText(EMandateBankDetails.this, response.optString("msg"), Toast.LENGTH_SHORT).show();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                progressBar11.dismiss();
                Toast.makeText(EMandateBankDetails.this, "Something went wrong, Please try after some time !", Toast.LENGTH_SHORT).show();
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


    private  boolean validate(){


        if(bankName.equalsIgnoreCase("Select")){
            Toast.makeText(this, "Please select your bank", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(accountHolderName.getText().toString().isEmpty()){
            Toast.makeText(this, "Please enter account holder name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(accountNumber.getText().toString().isEmpty()){
            Toast.makeText(this, "Please enter your account number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(ifscCode.getText().toString().isEmpty()){
            Toast.makeText(this, "Please enter your IFSC code", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(spAccType.getSelectedItem().toString().equalsIgnoreCase("Select Account Type")){

            Toast.makeText(this, "Please select your account type", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(bankName.equalsIgnoreCase("Select Bank"))
        {
            Toast.makeText(this, "Please select your Bank", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
    private void selectBank() {
        switch (nSelectedBank) {
            case 1:
                isEmandate = 1;
                bankName= "Axis Bank";
                ivCheckAxis.setImageResource(R.drawable.ic_checked_svg);
                ivCheckHdfc.setImageResource(R.drawable.ic_circle_svg);
                ivCheckicici.setImageResource(R.drawable.ic_circle_svg);
                ivCheckSbi.setImageResource(R.drawable.ic_circle_svg);
                ivCheckOthers.setImageResource(R.drawable.ic_circle_svg);
                spBankName.setVisibility(View.GONE);
                break;

            case 2:
                isEmandate = 1;
                bankName= "Hdfc Bank";
                ivCheckAxis.setImageResource(R.drawable.ic_circle_svg);
                ivCheckHdfc.setImageResource(R.drawable.ic_checked_svg);
                ivCheckicici.setImageResource(R.drawable.ic_circle_svg);
                ivCheckSbi.setImageResource(R.drawable.ic_circle_svg);
                ivCheckOthers.setImageResource(R.drawable.ic_circle_svg);
                spBankName.setVisibility(View.GONE);
                break;

            case 3:
                isEmandate = 1;
                bankName= "ICICI";
                ivCheckAxis.setImageResource(R.drawable.ic_circle_svg);
                ivCheckHdfc.setImageResource(R.drawable.ic_circle_svg);
                ivCheckicici.setImageResource(R.drawable.ic_checked_svg);
                ivCheckSbi.setImageResource(R.drawable.ic_circle_svg);
                ivCheckOthers.setImageResource(R.drawable.ic_circle_svg);
                spBankName.setVisibility(View.GONE);
                break;

            case 4:
                isEmandate = 1;
                bankName= "SBI";
                ivCheckAxis.setImageResource(R.drawable.ic_circle_svg);
                ivCheckHdfc.setImageResource(R.drawable.ic_circle_svg);
                ivCheckicici.setImageResource(R.drawable.ic_circle_svg);
                ivCheckSbi.setImageResource(R.drawable.ic_checked_svg);
                ivCheckOthers.setImageResource(R.drawable.ic_circle_svg);
                spBankName.setVisibility(View.GONE);
                break;

            case 5:
                isEmandate = 0;
                ivCheckAxis.setImageResource(R.drawable.ic_circle_svg);
                ivCheckHdfc.setImageResource(R.drawable.ic_circle_svg);
                ivCheckicici.setImageResource(R.drawable.ic_circle_svg);
                ivCheckSbi.setImageResource(R.drawable.ic_circle_svg);
                ivCheckOthers.setImageResource(R.drawable.ic_checked_svg);
                spBankName.setVisibility(View.VISIBLE);
                break;
        }
    }

}
