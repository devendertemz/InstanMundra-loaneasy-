package com.loaneasy.Home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.loaneasy.BankDetailsActivity;
import com.loaneasy.R;
import com.loaneasy.utils.ConnectionCheck;
import com.loaneasy.utils.UserSharedPreference;
import com.loaneasy.utils.Utility;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class ApplyForLoan extends Fragment {


    private Button btApplyForLoan;
    private SeekBar selectLoanAmount;
    private TextView tvAmountSelected, rcTotalInterest,rcProcFee,rcGst,rcDisbAmount,rcRepayAmount,tvPlatfromCharges;
    private Boolean isAllowed = true;

    private DecimalFormat formatter = new DecimalFormat("#,##,###");

    private Double mEmiRatePerMonth = 0.03;
    private double platformCharges;
    private String TAG="ApplyForLoan";
    private UserSharedPreference sharedPreference;

    String[] PERMISSIONS = { Manifest.permission.READ_CONTACTS,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    int PERMISSION_ALL = 1;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        //String strtext = getArguments().getString("loan_status");
        //Log.i("RSR","="+strtext);
        //Toast.makeText(getContext(), strtext, Toast.LENGTH_SHORT).show();

        return inflater.inflate(R.layout.apply_loan, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        sharedPreference = new UserSharedPreference(getContext());

        tvAmountSelected = view.findViewById(R.id.tvAmountSelected);
        rcTotalInterest = view.findViewById(R.id.rcTotalInterest);
        rcProcFee = view.findViewById(R.id.rcProcFee);
        rcGst = view.findViewById(R.id.rcGst);
        rcDisbAmount = view.findViewById(R.id.rcDisbAmount);
        rcRepayAmount = view.findViewById(R.id.rcRepayAmount);
        tvPlatfromCharges = view.findViewById(R.id.tvPlatfromCharges);


        btApplyForLoan = view.findViewById(R.id.btApplyForLoan);
        selectLoanAmount  = view.findViewById(R.id.seekBarLoanAmount);

        selectLoanAmount.setMax(30);
        selectLoanAmount.setProgress(3);
        selectLoanAmount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {

                int amt = Integer.parseInt(tvAmountSelected.getText().toString().replace(",", ""));
                if (amt <4000)
                {
                    selectLoanAmount.setMax(8);
                }
                else
                {
                    selectLoanAmount.setMax(30);
                }

                if (progress > 2) {
                    tvAmountSelected.setText(String.valueOf(formatter.format(progress * 1000)));



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



        btApplyForLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!hasPermissions(ApplyForLoan.this, PERMISSIONS)) {

                    ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);

                } else {

                    if (allValidated()) {

                        if (new ConnectionCheck(getActivity()).isNetworkAvailable()) {

                            getActiveLoan();

                            /*Intent intent = new Intent(getActivity(), BankDetailsActivity.class);
                            intent.putExtra("loan_amount", tvAmountSelected.getText().toString());
                            intent.putExtra("disbursed_amount", rcDisbAmount.getText().toString());
                            intent.putExtra("days_returning", "30");
                            intent.putExtra("processing_fee", rcProcFee.getText().toString());
                            intent.putExtra("total_interest", rcTotalInterest.getText().toString());
                            intent.putExtra("repay_amount", rcRepayAmount.getText().toString());
                            startActivity(intent);*/
                        }
                    }
                }

            }

        });


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

    private boolean allValidated() {

        if (!isAllowed) {
            showDialogLoan();
            return false;
        }

        if (rcRepayAmount.getText().toString().equalsIgnoreCase("--")) {
            Toast.makeText(getActivity(), "Please select amount", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (rcTotalInterest.getText().toString().equalsIgnoreCase("--")) {
            Toast.makeText(getActivity(), "Please select days", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    public void showDialogLoan() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_loan);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView tvOkDialLoan = dialog.findViewById(R.id.tvOkDialLoan);

        tvOkDialLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private boolean hasPermissions(ApplyForLoan context, String[] permissions) {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }



    private void getActiveLoan(){

        final ProgressDialog progressBar;
        progressBar = new ProgressDialog(getContext(), R.style.AppCompatProgressDialogStyle);
        progressBar.setCancelable(false);
        progressBar.setMessage("Please Wait...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();


        RequestQueue queue = Volley.newRequestQueue(getContext());
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

                    Toast.makeText(getContext(), response.optString("msg"), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //updateDocumentDialog();

                    Intent intent = new Intent(getContext(), BankDetailsActivity.class);
                    intent.putExtra("loan_amount", tvAmountSelected.getText().toString());
                    intent.putExtra("disbursed_amount", rcDisbAmount.getText().toString());
                    //intent.putExtra("days_returning", tvDays.getText().toString());
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



}
