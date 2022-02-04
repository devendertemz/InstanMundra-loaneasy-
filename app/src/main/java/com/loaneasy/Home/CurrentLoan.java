package com.loaneasy.Home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.loaneasy.network.ApiRequest;
import com.loaneasy.DrawerItems.ActiveLoansActivity;
import com.loaneasy.R;
import com.loaneasy.utils.UserSharedPreference;
import com.loaneasy.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CurrentLoan extends Fragment implements View.OnClickListener {


    private TextView loanDueDate, loanAmount;

    private UserSharedPreference sharedPreference;

    private String getLoanAmount, getDueDate, loanId,s_no, disBursAmt, loanAvialDate, tenure, interest, processingFees,repayAmount,
            applicationStatus, fineAmt, chequeBounce;
    private Button loanDetails, payNow;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        sharedPreference = new UserSharedPreference(getContext());

        return inflater.inflate(R.layout.current_loan, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);




        loanDueDate = view.findViewById(R.id.tvDueDate);
        loanAmount = view.findViewById(R.id.tvLoanAmount);

        loanDetails = view.findViewById(R.id.btLoanDetails);
        payNow = view.findViewById(R.id.btPayNow);

        loanDetails.setOnClickListener(this);
        payNow.setOnClickListener(this);


        sharedPreference = new UserSharedPreference(getContext());


        getActiveLoans(sharedPreference.getUserId());

      //  getActiveLoans("99893");


    }








    private void getActiveLoans(String userId) {
        final ProgressDialog progressBar;
        progressBar = new ProgressDialog(getContext(), R.style.AppCompatProgressDialogStyle);
        progressBar.setCancelable(false);
        progressBar.setMessage("Please Wait...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();

        RestAdapter adapter = new RestAdapter.Builder().setEndpoint(Utility.BASE_URL).build();
        ApiRequest api = adapter.create(ApiRequest.class);

        api.getActiveLoan(userId, new Callback<Response>() {
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
                    Log.i("RR","="+output);

                    //Toast.makeText(getContext(), output, Toast.LENGTH_SHORT).show();
                    JSONArray jsonArray = new JSONArray(output);
                    JSONObject result = jsonArray.getJSONObject(0);


                    //Log.i("Response","="+result);

                    //Toast.makeText(getContext(), "Response"+result, Toast.LENGTH_SHORT).show();

                    getLoanAmount = result.optString("loan_amount");
                    getDueDate = result.optString("repayment_date");

                    loanId = result.optString("order_id");
                    s_no = result.optString("s_no");
                    disBursAmt = result.optString("disbursed_amount");
                    loanAvialDate = result.optString("date_created");
                    tenure = result.optString("days_returning");
                    interest = result.optString("total_interest");
                    processingFees = result.optString("processing_fee");
                    //repayAmount = result.optString("repayment_date");
                    applicationStatus = result.optString("application_status");
                    fineAmt = result.optString("fine_amount");
                    chequeBounce = result.optString("cheque_bounce_count");



                    loanAmount.setText("Rs. "+getLoanAmount);
                    loanDueDate.setText("Due Date: "+getDueDate);


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Something went wrong !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("failure", "---->>" + error.getMessage());
                progressBar.dismiss();
                Toast.makeText(getContext(), "Something went wrong !", Toast.LENGTH_SHORT).show();
            }
        });
    }




    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.btLoanDetails:
                startActivity(new Intent(getContext(), ActiveLoansActivity.class));
                break;

            case R.id.btPayNow:

                Intent intent = new Intent(getActivity(), ActiveLoansActivity.class);
                intent.putExtra("paynow", true);
                intent.putExtra("loanId", loanId);

                intent.putExtra("s_no", s_no);

                intent.putExtra("loanAmnt", getLoanAmount);
                intent.putExtra("disbursedAmnt", disBursAmt);
                intent.putExtra("loanAvailedDate", loanAvialDate);
                intent.putExtra("tenure", tenure);
                intent.putExtra("interest", interest);
                intent.putExtra("processingFee", processingFees);
                intent.putExtra("repayAmount", repayAmount);
                intent.putExtra("dueDate", getDueDate);
                intent.putExtra("applicationStatus", applicationStatus);
                intent.putExtra("fineAMount", fineAmt);
                intent.putExtra("chequeBounce",chequeBounce );

            /*    intent.putExtra("loanId", "IM-PL-1011");
                intent.putExtra("loanAmnt", "3000");
                intent.putExtra("disbursedAmnt", "2469");
                intent.putExtra("loanAvailedDate", "25-03-2021");
                intent.putExtra("tenure", "15");
                intent.putExtra("interest", "105");
                intent.putExtra("processingFee", "450");
                intent.putExtra("repayAmount", "3105");
                intent.putExtra("dueDate", "05-04-2021");
                intent.putExtra("applicationStatus", "Disbursed");
                intent.putExtra("fineAMount", "450");
                intent.putExtra("chequeBounce","1" );*/



                startActivity(intent);



                break;
        }
    }
}
