package com.loaneasy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.loaneasy.utils.Utility;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoanCalculator extends AppCompatActivity {

    private SeekBar amount,days;
    TextView setAmount,setDays, rePaymentDate;
    Button applyForLoan;
    String getAmount, retuningDays, retuningDate, profileCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laon_calculator);



        setAmount = (TextView) findViewById(R.id.tvAmount);
        setDays = (TextView) findViewById(R.id.tvSetDays);
        rePaymentDate = (TextView) findViewById(R.id.tvRePayment);
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        retuningDate = format.format(today);
        rePaymentDate.setText("Repayment Date "+retuningDate);

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("ProfileRegister", MODE_PRIVATE);
        profileCompleted = prefs.getString("profileCompleted", null);

        applyForLoan = (Button) findViewById(R.id.btConfirmLoan);
        applyForLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*ConfirmDialog alert = new ConfirmDialog();
                alert.showDialog(LoanCalculator.this, "Error de conexión al servidor");*/

               // Toast.makeText(LoanCalculator.this, "Loan Applied Successfully", Toast.LENGTH_SHORT).show();

              /*  if (profileCompleted !=null)
                {
                    apply_loan();
                }
                else
                {

                }*/

                //apply_loan();

            }
        });

        amount = (SeekBar) findViewById(R.id.PRICEseekBarID);
        amount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
               // Log.i("rr","----------->"+progress);
                setAmount.setText(String.valueOf(progress)+"\u20B9");
                getAmount = String.valueOf(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        days = (SeekBar) findViewById(R.id.sbDays);
        days.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int daysCount, boolean b) {
                setDays.setText(String.valueOf(daysCount)+" Days");

                retuningDays = String.valueOf(daysCount);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

   /* private void apply_loan()
    {
        final ProgressDialog progressBar;
        progressBar = new ProgressDialog(LoanCalculator.this);
        progressBar.setCancelable( true );
        progressBar.setMessage( "Please Wait..." );
        progressBar.setProgressStyle( ProgressDialog.STYLE_SPINNER );
        progressBar.setProgress( 0 );
        progressBar.setMax( 1000 );
        progressBar.show();

        RestAdapter adapter = new RestAdapter.Builder().setEndpoint(Utility.BASE_URL).build();
        ApiRequest api = adapter.create(ApiRequest.class);

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
        String userId = prefs.getString("userId", null);

        api.applyLoan(userId,getAmount, retuningDays,retuningDate, new Callback<Response>() {
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

                    //JSONObject jsonObject = new JSONObject(output);
                    Log.i("response","----------->"+output);
                    Toast.makeText(LoanCalculator.this, "Loan Applied successfully", Toast.LENGTH_SHORT).show();



                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);




                    *//*queryMap2 = new HashMap();
                    queryMap2.put("mobileNo",getMobileNo.getText().toString().trim());
                    queryMap2.put("fcmToken", MyFirebaseInstanceIDService.fcmToken);

                    send_fcm_token();*//*



                } catch (Exception e)
                {
                    e.printStackTrace();
                }


            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("failure","---->>"+error);
                Log.i("failure","---->>"+error.getMessage());
                progressBar.dismiss();
                Toast.makeText(getApplicationContext(), "Internal Server Error", Toast.LENGTH_SHORT).show();
            }
        });
    }*/


}
