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

public class AppliedLoansFragment extends Fragment {

    private RecyclerView rvAppliedLoans;
    LoansAdapter loansAdapter;
    public ArrayList<AppliedLoansBean> alAppliedLoans;
    //ImageView ivBackToolbar;
    UserSharedPreference sharedPreference;
    private CardView cvNoActLoan;

    public AppliedLoansFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_applied_loans, container, false);

        sharedPreference = new UserSharedPreference(getActivity());
        alAppliedLoans = new ArrayList<>();

        rvAppliedLoans = view.findViewById(R.id.rvAppliedLoans);
        cvNoActLoan = view.findViewById(R.id.cvNoActLoan);
        //ivBackToolbar = view.findViewById(R.id.ivBackToolbar);

        loansAdapter = new LoansAdapter(getActivity());
        rvAppliedLoans.setAdapter(loansAdapter);
        rvAppliedLoans.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvAppliedLoans.addItemDecoration(new VerticalSpaceItemDecoration(22));

        if (new ConnectionCheck(getActivity()).isNetworkAvailable())
            sendDataGetUserDetails(sharedPreference.getUserId());
        else{
            Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Please Check Your Internet Connection", Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        return view;
    }

    public class LoansAdapter extends RecyclerView.Adapter<LoansAdapter.ViewHolder> {

        private Context context;
        ProgressDialog progressDialog;

        public LoansAdapter(Context context) {
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_loans, null, false);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutView.setLayoutParams(lp);

            // return new RecyclerViewHolder(rootView);
            return new ViewHolder(layoutView);
        }

        @Override
        public void onBindViewHolder(final LoansAdapter.ViewHolder holder, final int position) {
            holder.tvLoanAmount.setText(alAppliedLoans.get(position).getLoanAmnt());
            holder.tvRepayDate.setText("Applied Date :  "+ alAppliedLoans.get(position).getLoanAvailedDate());
            if ((alAppliedLoans.get(position).getApplicationStatus().equalsIgnoreCase("verify"))){
                holder.tvStatusAppl.setText("Applied");
            }else if ((alAppliedLoans.get(position).getApplicationStatus().equalsIgnoreCase("pending"))){
                holder.tvStatusAppl.setText("Verified");
            }else if (alAppliedLoans.get(position).getApplicationStatus().equalsIgnoreCase("approved") ||
                    alAppliedLoans.get(position).getApplicationStatus().equalsIgnoreCase("hold") ||
                    alAppliedLoans.get(position).getApplicationStatus().equalsIgnoreCase("processing")){
                holder.tvStatusAppl.setText("Processing");
            }else if (alAppliedLoans.get(position).getApplicationStatus().equalsIgnoreCase("sanction") &&
                    alAppliedLoans.get(position).getRepayment_status().equalsIgnoreCase("0") &&
                    alAppliedLoans.get(position).getUpcoming_payment().equalsIgnoreCase("1")){
                holder.tvStatusAppl.setText("Disbursed");
            }else
                holder.tvStatusAppl.setText(alAppliedLoans.get(position).getApplicationStatus());

            if (!alAppliedLoans.get(position).getApplicationStatus().isEmpty()) {
                if (alAppliedLoans.get(position).getApplicationStatus().equalsIgnoreCase("Rejected"))
                    holder.ivStatusAppl.setImageResource(R.drawable.ic_red_dot);
                else
                    holder.ivStatusAppl.setImageResource(R.drawable.ic_green_dot);
            }

            holder.tvView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getActivity(), ActiveLoansActivity.class);
                    intent.putExtra("flagCheck", true);
                    intent.putExtra("loanId", alAppliedLoans.get(position).getLoanId());
                    intent.putExtra("loanAmnt", alAppliedLoans.get(position).getLoanAmnt());
                    intent.putExtra("disbursedAmnt", alAppliedLoans.get(position).getDisbursedAmnt());
                    intent.putExtra("loanAvailedDate", alAppliedLoans.get(position).getLoanAvailedDate());
                    intent.putExtra("tenure", alAppliedLoans.get(position).getTenure());
                    intent.putExtra("interest", alAppliedLoans.get(position).getInterest());
                    intent.putExtra("processingFee", alAppliedLoans.get(position).getProcessingFee());
                    intent.putExtra("repayAmount", alAppliedLoans.get(position).getRepayAmount());
                    intent.putExtra("dueDate", alAppliedLoans.get(position).getDueDate());
                    intent.putExtra("applicationStatus", holder.tvStatusAppl.getText().toString());
                    intent.putExtra("fineAMount", alAppliedLoans.get(position).getFineAMount());
                    intent.putExtra("chequeBounce", alAppliedLoans.get(position).getChequeBounceAount());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return alAppliedLoans.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            //ImageView itemType;
            TextView tvLoanAmount, tvRepayDate, tvStatusAppl, tvView;
            ImageView ivStatusAppl;

            public ViewHolder(View itemView) {
                super(itemView);

                tvLoanAmount = itemView.findViewById(R.id.tvLoanAmount);
                tvRepayDate = itemView.findViewById(R.id.tvRepayDate);
                tvStatusAppl = itemView.findViewById(R.id.tvStatusAppl);
                ivStatusAppl = itemView.findViewById(R.id.ivStatusAppl);
                tvView = itemView.findViewById(R.id.tvView);
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

                    Log.i("TAG111", "--getAplliedLoans_Response-->>" + output);
                    if (output.contains("not applied")){
                        Toast.makeText(getActivity(), output, Toast.LENGTH_SHORT).show();
                        rvAppliedLoans.setVisibility(View.GONE);
                        cvNoActLoan.setVisibility(View.VISIBLE);
                    }
                    else {
                        JSONArray array = new JSONArray(output);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
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
                            String applicationStatus = object.optString("application_status");
                            String repayment_status = object.optString("repayment_status");
                            String upcoming_payment = object.optString("upcoming_payment");
                            String fineAmount = object.optString("fine_amount");
                            String chequeBounce = object.optString("cheque_bounce_count");
                            alAppliedLoans.add(new AppliedLoansBean(loanId, loanAmnt, disbursedAmnt, loanAvailedDate,
                                    repayment_date,tenure, interest, processingFee, repayAmount, dueDate, applicationStatus,
                                    repayment_status, upcoming_payment,fineAmount, chequeBounce));
                        }
                        loansAdapter.notifyDataSetChanged();

                        if (alAppliedLoans.isEmpty()){
                            rvAppliedLoans.setVisibility(View.GONE);
                            cvNoActLoan.setVisibility(View.VISIBLE);
                        }else {
                            rvAppliedLoans.setVisibility(View.VISIBLE);
                            cvNoActLoan.setVisibility(View.GONE);
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
