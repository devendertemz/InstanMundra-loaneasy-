package com.loaneasy.DrawerItems;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.loaneasy.ICICIActivity;
import com.loaneasy.network.ApiRequest;
import com.loaneasy.RazorPayCheckout;
import com.loaneasy.R;
import com.loaneasy.utils.ConnectionCheck;
import com.loaneasy.utils.UserSharedPreference;
import com.loaneasy.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ActiveLoansActivity extends AppCompatActivity {

    boolean flagCheck = false;
    private TextView tvLoanID, tvLoanAmount, tvDisbursedAmount, tvLoanDate, tvTenure, tvInterest, tvProcFee, tvRepayAmount, tvLoanDuedate, tvStatus,
            textview, repayAmountView, DueDateView, tvReturnPayment, returnPayment, tvFineAmount, fineAmount,
            tvChequeBounce, chequeBounce, tvReceivedPayment, receivedPayment;

    private CardView cvActiveLoan, cvNoLoan;
    UserSharedPreference sharedPreference;
    private Button payOnline;

    private String loanRepayAmount, loanId,s_no;

    private LinearLayout loanRepay, loanDue, returnPaymentContainer, fineAmountContainer, chequeBounceContainer,
            receivedPaymentContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_loans);
        sharedPreference = new UserSharedPreference(this);

        payOnline = (Button) findViewById(R.id.btPayOnline);

        cvActiveLoan = findViewById(R.id.cvActiveLoan);
        cvNoLoan = findViewById(R.id.cvNoLoan);
        textview = findViewById(R.id.textview);

        repayAmountView = findViewById(R.id.tvLoanRepaymentAmount);
        DueDateView = findViewById(R.id.tvLoanDueDate);
        returnPaymentContainer = findViewById(R.id.lvReturnPayment);

        receivedPaymentContainer = findViewById(R.id.lvReceivedPayment);

        tvReturnPayment = findViewById(R.id.tvReturnPaymentView);
        returnPayment = findViewById(R.id.tvReturnPayment);

        fineAmountContainer = findViewById(R.id.lyFineAmount);
        tvFineAmount = findViewById(R.id.tvFineAmount);
        fineAmount = findViewById(R.id.FineAmount);

        chequeBounceContainer = findViewById(R.id.lyChequeBounce);
        tvChequeBounce = findViewById(R.id.tvChequeBounce);
        chequeBounce = findViewById(R.id.ChequeBounce);

        loanRepay = findViewById(R.id.lvLoanRepay);
        loanDue = findViewById(R.id.lvLoanDueDate);

        tvReceivedPayment = findViewById(R.id.tvReceivedPaymentView);
        receivedPayment = findViewById(R.id.tvReceivedPayment);


        tvLoanID = findViewById(R.id.tvLoanID);
        tvLoanAmount = findViewById(R.id.tvLoanAmount);
        tvDisbursedAmount = findViewById(R.id.tvDisbursedAmount);
        tvLoanDate = findViewById(R.id.tvLoanDate);
        tvTenure = findViewById(R.id.tvTenure);
        tvInterest = findViewById(R.id.tvInterest);
        tvProcFee = findViewById(R.id.tvProcFee);
        tvRepayAmount = findViewById(R.id.tvRepayAmount);
        tvLoanDuedate = findViewById(R.id.tvLoanDuedate);
        tvStatus = findViewById(R.id.tvStatus);


        payOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showEditDialog();


                //Toast.makeText(ActiveLoansActivity.this, "Currently service is not available. Sorry for inconvenience", Toast.LENGTH_SHORT).show();
            }

        });


        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("flagCheck")) {

            Log.i("flagCheck", "=" + getIntent().getExtras().getBoolean("flagCheck"));
            cvActiveLoan.setVisibility(View.VISIBLE);
            textview.setVisibility(View.VISIBLE);
            cvNoLoan.setVisibility(View.GONE);
            payOnline.setVisibility(View.GONE);

            fineAmountContainer.setVisibility(View.GONE);
            tvFineAmount.setVisibility(View.GONE);
            fineAmount.setVisibility(View.GONE);

            String amt = getIntent().getStringExtra("loanAmnt");
            String intrst = getIntent().getStringExtra("interest");


            String totalFineAmount = getIntent().getStringExtra("fineAMount");
            int value = Integer.parseInt(amt) + Integer.parseInt(intrst) + Integer.parseInt(totalFineAmount);
            String chkBounce = getIntent().getStringExtra("chequeBounce");
            Log.i("bounce", "=" + chkBounce);
            int bounceCount = Integer.parseInt(chkBounce);

            int BounceAmount = bounceCount * 500;
            double Gst = BounceAmount * 0.18;

            double totalBounceAmt = BounceAmount + Gst;
            Log.i("total_bounce", "=" + totalBounceAmt);
            String loanStatus = getIntent().getStringExtra("applicationStatus");
            tvStatus.setText(loanStatus);
            Log.i("rr", "outside if");
            returnPaymentContainer.setVisibility(View.GONE);
            tvReturnPayment.setVisibility(View.GONE);
            returnPayment.setVisibility(View.GONE);

            if (loanStatus.equalsIgnoreCase("Disbursed")) {

                double finalAmount = value + totalBounceAmt;

                DecimalFormat format = new DecimalFormat("0.#");
                String finalAmtFormat = format.format(finalAmount);
                Log.i("finalAmount", "=" + finalAmtFormat);

                loanRepay.setVisibility(View.VISIBLE);
                loanDue.setVisibility(View.VISIBLE);
                tvRepayAmount.setVisibility(View.VISIBLE);
                tvLoanDuedate.setVisibility(View.VISIBLE);
                repayAmountView.setVisibility(View.VISIBLE);
                DueDateView.setVisibility(View.VISIBLE);

                fineAmountContainer.setVisibility(View.VISIBLE);
                tvFineAmount.setVisibility(View.VISIBLE);
                fineAmount.setVisibility(View.VISIBLE);


                chequeBounceContainer.setVisibility(View.VISIBLE);
                tvChequeBounce.setVisibility(View.VISIBLE);
                chequeBounce.setVisibility(View.VISIBLE);


                tvRepayAmount.setText(finalAmtFormat);
                tvLoanDuedate.setText(formatDate(getIntent().getStringExtra("dueDate")));
                chequeBounce.setText(String.valueOf(totalBounceAmt));
                fineAmount.setText(String.valueOf(totalFineAmount));
            } else {
                fineAmountContainer.setVisibility(View.GONE);
                tvFineAmount.setVisibility(View.GONE);
                fineAmount.setVisibility(View.GONE);

                chequeBounceContainer.setVisibility(View.GONE);
                tvChequeBounce.setVisibility(View.GONE);
                chequeBounce.setVisibility(View.GONE);

                loanRepay.setVisibility(View.GONE);
                loanDue.setVisibility(View.GONE);
                tvRepayAmount.setVisibility(View.GONE);
                tvLoanDuedate.setVisibility(View.GONE);
                repayAmountView.setVisibility(View.GONE);
                DueDateView.setVisibility(View.GONE);


            }

            flagCheck = getIntent().getExtras().getBoolean("flagCheck");
            tvLoanID.setText(getIntent().getStringExtra("loanId"));
            tvLoanAmount.setText(getIntent().getStringExtra("loanAmnt"));
            tvDisbursedAmount.setText(getIntent().getStringExtra("disbursedAmnt"));
            tvLoanDate.setText(getIntent().getStringExtra("loanAvailedDate"));
            tvTenure.setText(getIntent().getStringExtra("tenure"));
            tvInterest.setText(getIntent().getStringExtra("interest"));
            tvProcFee.setText(getIntent().getStringExtra("processingFee"));
            //tvRepayAmount.setText(getIntent().getStringExtra("repayAmount"));


        }


        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("completed")) {
            Log.i("completed", "-->");
            cvActiveLoan.setVisibility(View.VISIBLE);
            textview.setVisibility(View.VISIBLE);
            cvNoLoan.setVisibility(View.GONE);

            fineAmountContainer.setVisibility(View.GONE);
            tvFineAmount.setVisibility(View.GONE);
            fineAmount.setVisibility(View.GONE);

            chequeBounceContainer.setVisibility(View.GONE);
            tvChequeBounce.setVisibility(View.GONE);
            chequeBounce.setVisibility(View.GONE);

            payOnline.setVisibility(View.GONE);

            loanRepay.setVisibility(View.VISIBLE);
            loanDue.setVisibility(View.VISIBLE);
            tvRepayAmount.setVisibility(View.VISIBLE);
            tvLoanDuedate.setVisibility(View.VISIBLE);
            repayAmountView.setVisibility(View.VISIBLE);
            DueDateView.setVisibility(View.VISIBLE);
            returnPaymentContainer.setVisibility(View.VISIBLE);
            tvReturnPayment.setVisibility(View.VISIBLE);
            returnPayment.setVisibility(View.VISIBLE);


            String totalFineAmount = getIntent().getStringExtra("fineAMount");
            String amt = getIntent().getStringExtra("loanAmnt");
            String intrst = getIntent().getStringExtra("interest");

            int value = Integer.parseInt(amt) + Integer.parseInt(intrst) + Integer.parseInt(totalFineAmount);

            String chkBounce = getIntent().getStringExtra("chequeBounce");
            int bounceCount = Integer.parseInt(chkBounce);
            int BounceAmount = bounceCount * 500;
            double Gst = BounceAmount * 0.18;
            double totalBounceAmt = BounceAmount + Gst;
            double finalAmount = value + totalBounceAmt;
            DecimalFormat format = new DecimalFormat("0.#");
            String finalAmtFormat = format.format(finalAmount);


            String loanStatus = getIntent().getStringExtra("applicationStatus");
            tvStatus.setText(loanStatus);
            flagCheck = getIntent().getExtras().getBoolean("completed");
            tvLoanID.setText(getIntent().getStringExtra("loanId"));
            tvLoanAmount.setText(getIntent().getStringExtra("loanAmnt"));
            tvDisbursedAmount.setText(getIntent().getStringExtra("disbursedAmnt"));
            tvLoanDate.setText(getIntent().getStringExtra("loanAvailedDate"));
            tvTenure.setText(getIntent().getStringExtra("tenure"));
            tvInterest.setText(getIntent().getStringExtra("interest"));
            tvProcFee.setText(getIntent().getStringExtra("processingFee"));
            tvRepayAmount.setText(finalAmtFormat);


            tvLoanDuedate.setText(formatDate(getIntent().getStringExtra("dueDate")));
            returnPayment.setText(formatDate(getIntent().getStringExtra("returnPayment")));

            fineAmount.setText(String.valueOf(totalFineAmount));


        }

        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("paynow")) {
            Log.i("paynow", "-->");

            payOnline.setVisibility(View.VISIBLE);
            cvActiveLoan.setVisibility(View.VISIBLE);
            textview.setVisibility(View.VISIBLE);
            cvNoLoan.setVisibility(View.GONE);

            chequeBounceContainer.setVisibility(View.VISIBLE);
            tvChequeBounce.setVisibility(View.VISIBLE);
            chequeBounce.setVisibility(View.VISIBLE);

            String chkBounce = getIntent().getStringExtra("chequeBounce");
            int bounceCount = Integer.parseInt(chkBounce);

            int BounceAmount = bounceCount * 500;
            double Gst = BounceAmount * 0.18;
            //DecimalFormat format = new DecimalFormat("0.#");

            double totalBounceAmt = BounceAmount + Gst;
            //System.out.println(format.format(totalBounceAmt));


            loanRepay.setVisibility(View.VISIBLE);
            loanDue.setVisibility(View.VISIBLE);
            tvRepayAmount.setVisibility(View.VISIBLE);
            tvLoanDuedate.setVisibility(View.VISIBLE);
            repayAmountView.setVisibility(View.VISIBLE);
            DueDateView.setVisibility(View.VISIBLE);
            returnPaymentContainer.setVisibility(View.VISIBLE);
            tvReturnPayment.setVisibility(View.VISIBLE);
            returnPayment.setVisibility(View.VISIBLE);


            String amt = getIntent().getStringExtra("loanAmnt");
            String intrst = getIntent().getStringExtra("interest");
            String fineAmount = getIntent().getStringExtra("fineAMount");
            Log.i("fine", "=" + fineAmount);

            int fineAmt = Integer.parseInt(amt) + Integer.parseInt(intrst) + Integer.parseInt(fineAmount);

            double totalRepay = fineAmt + totalBounceAmt;
            Log.i("totalRepay", "=" + totalRepay);

            //Toast.makeText(this, "Repay="+totalRepay, Toast.LENGTH_SHORT).show();

            //String finalAmt = format.format(totalRepay);
            //Log.i("final amount","="+finalAmt);
            loanRepayAmount = String.valueOf(totalRepay);
            loanId = getIntent().getStringExtra("loanId");
            s_no = getIntent().getStringExtra(s_no);
            String loanStatus = getIntent().getStringExtra("applicationStatus");
            tvStatus.setText(loanStatus);
            flagCheck = getIntent().getExtras().getBoolean("completed");
            tvLoanID.setText(getIntent().getStringExtra("loanId"));
            tvLoanAmount.setText(getIntent().getStringExtra("loanAmnt"));
            tvDisbursedAmount.setText(getIntent().getStringExtra("disbursedAmnt"));
            tvLoanDate.setText(getIntent().getStringExtra("loanAvailedDate"));
            tvTenure.setText(getIntent().getStringExtra("tenure"));
            tvInterest.setText(getIntent().getStringExtra("interest"));
            tvProcFee.setText(getIntent().getStringExtra("processingFee"));
            tvRepayAmount.setText(String.valueOf(totalRepay));
            chequeBounce.setText(String.valueOf(totalBounceAmt));
            tvLoanDuedate.setText(formatDate(getIntent().getStringExtra("dueDate")));
            returnPayment.setText(getIntent().getStringExtra("returnPayment"));


        }


        if (!flagCheck) {

            getActiveLoans(sharedPreference.getUserId());
        }

        findViewById(R.id.tvTakeLoan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private String formatDate(String dateString) {
        try {
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
            Date d = sd.parse(dateString);
            sd = new SimpleDateFormat("dd-MM-yyyy");
            return sd.format(d);
        } catch (ParseException e) {
        }
        return "";
    }


    private void getActiveLoans(String userId) {
        final ProgressDialog progressBar;
        progressBar = new ProgressDialog(ActiveLoansActivity.this, R.style.AppCompatProgressDialogStyle);
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

                    Log.i("TAG111", "--getLoanList" + output);
                    if (output.contains("No Data Found")) {
                        cvNoLoan.setVisibility(View.VISIBLE);
                        cvActiveLoan.setVisibility(View.GONE);
                        textview.setVisibility(View.GONE);
                        //Toast.makeText(ActiveLoansActivity.this, output, Toast.LENGTH_SHORT).show();
                    } else {
                        cvActiveLoan.setVisibility(View.VISIBLE);
                        textview.setVisibility(View.VISIBLE);
                        cvNoLoan.setVisibility(View.GONE);
                        JSONArray array = new JSONArray(output);


                        JSONObject object = array.getJSONObject(0);
                        tvLoanID.setText(object.optString("order_id"));
                        s_no=object.optString("s_no");
                        tvLoanAmount.setText(object.optString("loan_amount"));
                        tvDisbursedAmount.setText(object.optString("disbursed_amount"));
                        tvLoanDate.setText(object.optString("date_created"));
                        tvTenure.setText(object.optString("days_returning"));
                        tvInterest.setText(object.optString("total_interest"));
                        tvProcFee.setText(object.optString("processing_fee"));
                        String amt = object.optString("loan_amount");
                        String intrst = object.optString("total_interest");
                        String fine_amount = object.optString("fine_amount");
                        String bounce = object.optString("cheque_bounce_count");
                        String receiveAmt = object.optString("return_amount");


                        int value = Integer.parseInt(amt) + Integer.parseInt(intrst) + Integer.parseInt(fine_amount);
                        Log.i("value", "---->" + value);

                        Log.i("bounce", "---->" + bounce);

                        int BounceAmount = Integer.parseInt(bounce) * 500;
                        double Gst = BounceAmount * 0.18;
                        double totalBounceAmt = BounceAmount + Gst;

                        double finalAmount = value + totalBounceAmt;

                        DecimalFormat format = new DecimalFormat("0.#");
                        String finalAmtFormat = format.format(finalAmount);


                        //tvRepayAmount.setText(String.valueOf(value));
                        //tvLoanDuedate.setText(object.optString("repayment_date"));
                        //tvStatus.setText(object.optString("application_status"));


                        String loanStatus = object.optString("application_status");
                        tvStatus.setText(loanStatus);
                        Log.i("rr", "status=" + loanStatus);
                        if (loanStatus.equalsIgnoreCase("Disbursed")) {

                            int fine = Integer.parseInt(fine_amount);
                            Log.i("fineAMt", "=" + fine);
                            if (fine > 0) {
                                fineAmountContainer.setVisibility(View.VISIBLE);
                                tvFineAmount.setVisibility(View.VISIBLE);
                                fineAmount.setVisibility(View.VISIBLE);


                                receivedPaymentContainer.setVisibility(View.VISIBLE);
                                receivedPayment.setVisibility(View.VISIBLE);
                                fineAmount.setVisibility(View.VISIBLE);

                                fineAmount.setText(String.valueOf(fine));

                                receivedPayment.setText(receiveAmt);

                            } else {
                                fineAmountContainer.setVisibility(View.GONE);
                                tvFineAmount.setVisibility(View.GONE);
                                fineAmount.setVisibility(View.GONE);
                            }


                            returnPaymentContainer.setVisibility(View.GONE);
                            tvReturnPayment.setVisibility(View.GONE);
                            returnPayment.setVisibility(View.GONE);
                            Log.i("rr", "inside if");
                            loanRepay.setVisibility(View.VISIBLE);
                            loanDue.setVisibility(View.VISIBLE);
                            tvRepayAmount.setVisibility(View.VISIBLE);
                            tvLoanDuedate.setVisibility(View.VISIBLE);
                            repayAmountView.setVisibility(View.VISIBLE);
                            DueDateView.setVisibility(View.VISIBLE);
                            tvRepayAmount.setText(finalAmtFormat);
                            chequeBounce.setText(String.valueOf(totalBounceAmt));
                            tvLoanDuedate.setText(formatDate(object.optString("repayment_date")));
                            returnPayment.setText(formatDate(object.optString("returnpayment_date")));
                        } else {
                            Log.i("rr", "inside else");
                            loanRepay.setVisibility(View.GONE);
                            loanDue.setVisibility(View.GONE);
                            tvRepayAmount.setVisibility(View.GONE);
                            tvLoanDuedate.setVisibility(View.GONE);
                            repayAmountView.setVisibility(View.GONE);
                            DueDateView.setVisibility(View.GONE);
                            fineAmountContainer.setVisibility(View.GONE);
                            tvFineAmount.setVisibility(View.GONE);
                            fineAmount.setVisibility(View.GONE);

                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ActiveLoansActivity.this, "Something went wrong !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("failure", "---->>" + error.getMessage());
                progressBar.dismiss();
                Toast.makeText(ActiveLoansActivity.this, "Something went wrong !", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void showEditDialog() {
        final Dialog dialog = new Dialog(ActiveLoansActivity.this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.cancel);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ImageView close = dialog.findViewById(R.id.close);
        TextView ICICI = dialog.findViewById(R.id.ICICI);
        TextView Razorpay = dialog.findViewById(R.id.Razorpay);


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });
        Razorpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                Intent intent = new Intent(getApplicationContext(), RazorPayCheckout.class);
                intent.putExtra("loan_amount", loanRepayAmount);
                intent.putExtra("loan_id", loanId);
                startActivity(intent);

            }
        });

        ICICI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                double doubleNumber = Double.valueOf(loanRepayAmount);
                int amount = (int) doubleNumber;
               /* System.out.println("Double Number: " + doubleNumber);
                System.out.println("Integer Part: " + amount);
                System.out.println("Decimal Part: " + (doubleNumber - amount));



         */
                //Toast.makeText(ActiveLoansActivity.this, "Work On Progres", Toast.LENGTH_SHORT).show();

                //https://instantmudra.com
               /// String url="https://uat.instantmudra.com/admin/icici-upi-form/user_id/"+sharedPreference.getUserId()+"/loan_id/"+s_no+"/amount/"+amount;
                String url="https://instantmudra.com/admin/icici-upi-form/user_id/"+sharedPreference.getUserId()+"/loan_id/"+s_no+"/amount/"+amount;
                Intent intent = new Intent(getApplicationContext(), ICICIActivity.class);
                intent.putExtra("url",url );

                startActivity(intent);




                dialog.dismiss();

            }
        });


        dialog.show();
    }

}
