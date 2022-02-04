package com.loaneasy.DrawerItems;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.loaneasy.network.ApiRequest;
import com.loaneasy.Beans.AppliedLoansBean;
import com.loaneasy.R;
import com.loaneasy.utils.ConnectionCheck;
import com.loaneasy.utils.UserSharedPreference;
import com.loaneasy.utils.Utility;
import com.loaneasy.utils.VerticalSpaceItemDecoration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.facebook.FacebookSdk.getApplicationContext;

public class PayNowFragment extends Fragment {

    RecyclerView rvPayNow;
    PayNowAdapter payNowAdapter;
    public ArrayList<AppliedLoansBean> alPayNow;
    UserSharedPreference sharedPreference;
    CardView cvNoLoan;

    public PayNowFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pay_now, container, false);
        sharedPreference = new UserSharedPreference(getActivity());
        alPayNow = new ArrayList<>();
        rvPayNow = view.findViewById(R.id.rvPayNow);
        cvNoLoan = view.findViewById(R.id.cvNoLoan);
        //ivBackToolbar = view.findViewById(R.id.ivBackToolbar);

        payNowAdapter = new PayNowAdapter(getActivity());
        rvPayNow.setAdapter(payNowAdapter);
        rvPayNow.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvPayNow.addItemDecoration(new VerticalSpaceItemDecoration(22));

        if (new ConnectionCheck(getActivity()).isNetworkAvailable())
            sendDataGetUserDetails(sharedPreference.getUserId());
        else{
            Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Please Check Your Internet Connection", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
        return view;
    }

    public class PayNowAdapter extends RecyclerView.Adapter<PayNowAdapter.ViewHolder> {

        private Context context;
        ProgressDialog progressDialog;

        public PayNowAdapter(Context context) {
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_pay_now, null, false);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutView.setLayoutParams(lp);

            // return new RecyclerViewHolder(rootView);
            return new ViewHolder(layoutView);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.tvLoanAmount.setText(alPayNow.get(position).getLoanAmnt());
            holder.tvRepayDate.setText("Applied Date :  "+ alPayNow.get(position).getLoanAvailedDate());
            holder.tvDueDate.setText("Due Date :  "+alPayNow.get(position).getRepayDate());

            if ((alPayNow.get(position).getApplicationStatus().equalsIgnoreCase("verify"))){
                holder.tvStatusAppl.setText("Applied");
            }else if ((alPayNow.get(position).getApplicationStatus().equalsIgnoreCase("pending"))){
                holder.tvStatusAppl.setText("Verified");
            }else if ((alPayNow.get(position).getApplicationStatus().equalsIgnoreCase("approved"))){
                holder.tvStatusAppl.setText("Processing");
            }
            else if (alPayNow.get(position).getApplicationStatus().equalsIgnoreCase("sanction") &&
                    alPayNow.get(position).getRepayment_status().equalsIgnoreCase("0") &&
                    alPayNow.get(position).getUpcoming_payment().equalsIgnoreCase("1")){
                holder.tvStatusAppl.setText("Disbursed");
            }else
                holder.tvStatusAppl.setText(alPayNow.get(position).getApplicationStatus());
            if (!alPayNow.get(position).getApplicationStatus().isEmpty()) {
                if (alPayNow.get(position).getApplicationStatus().equalsIgnoreCase("verify"))
                    holder.ivStatusAppl.setImageResource(R.drawable.ic_red_dot);
                else
                    holder.ivStatusAppl.setImageResource(R.drawable.ic_green_dot);
            }

            holder.tvView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /*AppliedLoansActivity appliedLoansActivity = (AppliedLoansActivity) getActivity();
                    appliedLoansActivity.openLoanDetailsFrag(alCompletedLoans.get(position).getLoanAmount(), alCompletedLoans.get(position).getApplStatus(),
                            alCompletedLoans.get(position).getRepayDate(), alCompletedLoans.get(position).getDateCreated(), alCompletedLoans.get(position).getDaysReturning()
                                    ,alCompletedLoans.get(position).getOrderId());*/

                    Intent intent = new Intent(getActivity(), ActiveLoansActivity.class);
                    intent.putExtra("paynow", true);
                    intent.putExtra("loanId", alPayNow.get(position).getLoanId());
                    intent.putExtra("loanAmnt", alPayNow.get(position).getLoanAmnt());
                    intent.putExtra("disbursedAmnt", alPayNow.get(position).getDisbursedAmnt());
                    intent.putExtra("loanAvailedDate", alPayNow.get(position).getLoanAvailedDate());
                    intent.putExtra("tenure", alPayNow.get(position).getTenure());
                    intent.putExtra("interest", alPayNow.get(position).getInterest());
                    intent.putExtra("processingFee", alPayNow.get(position).getProcessingFee());
                    intent.putExtra("repayAmount", alPayNow.get(position).getRepayAmount());
                    intent.putExtra("dueDate", alPayNow.get(position).getDueDate());
                    intent.putExtra("applicationStatus", holder.tvStatusAppl.getText().toString());
                    intent.putExtra("fineAMount", alPayNow.get(position).getFineAMount());
                    intent.putExtra("chequeBounce", alPayNow.get(position).getChequeBounceAount());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return alPayNow.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            //ImageView itemType;
            TextView tvLoanAmount, tvRepayDate, tvStatusAppl, tvView, tvDueDate;
            ImageView ivStatusAppl;

            public ViewHolder(View itemView) {
                super(itemView);

                tvLoanAmount = itemView.findViewById(R.id.tvLoanAmount);
                tvRepayDate = itemView.findViewById(R.id.tvRepayDate);
                tvStatusAppl = itemView.findViewById(R.id.tvStatusAppl);
                ivStatusAppl = itemView.findViewById(R.id.ivStatusAppl);
                tvView = itemView.findViewById(R.id.tvView);
                tvDueDate = itemView.findViewById(R.id.tvDueDate);
            }
        }
    }



    private void sendDataGetUserDetails(String userId) {

        final ProgressDialog progressBar;
        progressBar = new ProgressDialog(getActivity(), R.style.AppCompatProgressDialogStyle);
        progressBar.setCancelable(false);
        progressBar.setMessage("Please Wait...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();

        RestAdapter adapter = new RestAdapter.Builder().setEndpoint(Utility.BASE_URL).build();
        ApiRequest api = adapter.create(ApiRequest.class);

        api.getAplliedLoans(userId, new Callback<Response>() {
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

                    Log.i("Tag111", "--getAplliedLoans_Response-->>" + output);
                    if (output.contains("not applied")){
                        Toast.makeText(getActivity(), output, Toast.LENGTH_SHORT).show();
                        rvPayNow.setVisibility(View.GONE);
                        cvNoLoan.setVisibility(View.VISIBLE);
                    }
                    else {
                        JSONArray array = new JSONArray(output);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            String applicationStatus = object.optString("application_status");
                            String repayment_status = object.optString("repayment_status");
                            String upcoming_payment = object.optString("upcoming_payment");
                            if (!applicationStatus.equalsIgnoreCase("completed") ) {
                                String loanId = object.optString("order_id");
                                String loanAmnt = object.optString("loan_amount");
                                String disbursedAmnt = object.optString("disbursed_amount");
                                String loanAvailedDate = object.optString("date_created");
                                String repayment_date = object.optString("repayment_date");
                                String tenure = object.optString("days_returning");
                                String interest = object.optString("total_interest");
                                String processingFee = object.optString("processing_fee");
                                String repayAmount = "0"; //object.optString("processing_fee");
                                String dueDate = object.optString("repayment_date");
                                String fineAmount = object.optString("fine_amount");
                                String chequeBounce = object.optString("cheque_bounce_count");
                                alPayNow.add(new AppliedLoansBean(loanId, loanAmnt, disbursedAmnt, loanAvailedDate,
                                        repayment_date, tenure, interest, processingFee, repayAmount, dueDate, applicationStatus,
                                        repayment_status, upcoming_payment, fineAmount,chequeBounce));
                            }
                        }
                        payNowAdapter.notifyDataSetChanged();

                        if (alPayNow.isEmpty()){
                            rvPayNow.setVisibility(View.GONE);
                            cvNoLoan.setVisibility(View.VISIBLE);
                        }else {
                            rvPayNow.setVisibility(View.VISIBLE);
                            cvNoLoan.setVisibility(View.GONE);
                        }




                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Something went wrong !", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("failure", "---->>" + error.getMessage());
                progressBar.dismiss();
                if (error.getMessage().equalsIgnoreCase("timeout"))
                    Toast.makeText(getApplicationContext(), "Connection timeout, please try again", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), "Internal Server Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
