package com.loaneasy.Home;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.loaneasy.R;
import com.loaneasy.RegisterMandate;
import com.loaneasy.utils.UserSharedPreference;
import com.loaneasy.utils.Utility;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;


public class LoanStatus extends Fragment implements View.OnClickListener {


    private View view_order_placed,view_order_confirmed,view_order_processed,view_order_pickup,con_divider,ready_divider,placed_divider;
    private ImageView img_orderconfirmed,orderprocessed,orderpickup;
    private TextView textorderpickup,text_confirmed,textorderprocessed, loanReferenceId;


    private String statusCode,orderId;

    private boolean flag = false;
    private FrameLayout enashLayout;

    private Button eNash;

    private UserSharedPreference sharedPreference;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


         statusCode = getArguments().getString("loan_status");
         orderId = getArguments().getString("order_id");

         Log.i("RSR","="+statusCode);

        //Toast.makeText(getContext(), statusCode, Toast.LENGTH_SHORT).show();

        return inflater.inflate(R.layout.loan_status, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        sharedPreference = new UserSharedPreference(context);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //enashLayout = view.findViewById(R.id.layout_enash);
        eNash = view.findViewById(R.id.btENash);
        eNash.setOnClickListener(this);

        view_order_placed=view.findViewById(R.id.view_order_placed);
        view_order_confirmed=view.findViewById(R.id.view_order_confirmed);
        view_order_processed=view.findViewById(R.id.view_order_processed);
        view_order_pickup=view.findViewById(R.id.view_order_pickup);
        placed_divider=view.findViewById(R.id.placed_divider);
        con_divider=view.findViewById(R.id.con_divider);
        ready_divider=view.findViewById(R.id.ready_divider);

        textorderpickup=view.findViewById(R.id.textorderpickup);
        text_confirmed=view.findViewById(R.id.text_confirmed);
        textorderprocessed=view.findViewById(R.id.textorderprocessed);

        img_orderconfirmed=view.findViewById(R.id.img_orderconfirmed);
        orderprocessed=view.findViewById(R.id.orderprocessed);
        orderpickup=view.findViewById(R.id.orderpickup);

        loanReferenceId = view.findViewById(R.id.tvLoanReferenceId);
        loanReferenceId.setText("Loan Reference Id : "+orderId);


        getLoanStatus(statusCode);

        getENashRequest();


    }


    private void getLoanStatus(String loanStatus) {

        switch (loanStatus) {
            case "Verify": {
                float alfa = (float) 0.5;
                setStatusVerify(alfa);
                break;
            }
            case "Pending": {
                float alfa = (float) 1;
                setStatusApplied(alfa);
                break;
            }
            case "Processing": {
                float alfa = (float) 1;
                setStatusProcessing(alfa);
                break;
            }
            case "Hold": {
                float alfa = (float) 1;
                setStatusProcessing(alfa);
                break;
            }

            case "Approved": {
                float alfa = (float) 1;
                setStatusApproved(alfa);
                break;
            }

            case "Sanction":{
                float alfa = (float) 1;
                setStatusSanction(alfa);
                break;
            }

            case "Payment":{
                float alfa = (float) 1;
                setStatusSanction(alfa);
                break;
            }


        }
    }


    private void setStatusVerify(float alfa) {
        float myf= (float) 0.5;
        view_order_placed.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        view_order_confirmed.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        orderprocessed.setAlpha(alfa);

        view_order_processed.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        con_divider.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        placed_divider.setAlpha(alfa);

        img_orderconfirmed.setAlpha(alfa);
        placed_divider.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        text_confirmed.setAlpha(alfa);
        textorderprocessed.setAlpha(alfa);
        view_order_pickup.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        ready_divider.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        orderpickup.setAlpha(alfa);

        textorderpickup.setAlpha(myf);


    }


    private void setStatusApplied(float alfa) {
        float myf= (float) 0.5;
        view_order_placed.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        view_order_confirmed.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        orderprocessed.setAlpha(myf);
        view_order_processed.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        con_divider.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        placed_divider.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        img_orderconfirmed.setAlpha(alfa);

        text_confirmed.setAlpha(alfa);
        textorderprocessed.setAlpha(myf);
        view_order_pickup.setAlpha(myf);
        ready_divider.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        orderpickup.setAlpha(myf);
        view_order_pickup.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        textorderpickup.setAlpha(myf);
    }



    private void setStatusApproved(float alfa) {
        float myf= (float) 0.5;
        view_order_placed.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        view_order_confirmed.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        orderprocessed.setAlpha(alfa);

        view_order_processed.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        con_divider.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        placed_divider.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        img_orderconfirmed.setAlpha(alfa);

        text_confirmed.setAlpha(alfa);
        textorderprocessed.setAlpha(alfa);
        view_order_pickup.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        ready_divider.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        textorderpickup.setAlpha(myf);
        orderpickup.setAlpha(myf);

    }


    private void setStatusProcessing(float alfa){

        float myf= (float) 0.5;
        view_order_placed.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        view_order_confirmed.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        orderprocessed.setAlpha(alfa);
        view_order_processed.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        con_divider.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        placed_divider.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        img_orderconfirmed.setAlpha(alfa);

        text_confirmed.setAlpha(alfa);
        textorderprocessed.setAlpha(alfa);
        view_order_pickup.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        ready_divider.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        textorderpickup.setAlpha(myf);
        orderpickup.setAlpha(myf);
    }


    private void setStatusSanction(float alfa) {

        view_order_placed.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        view_order_confirmed.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        orderprocessed.setAlpha(alfa);
        view_order_processed.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        con_divider.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));

        img_orderconfirmed.setAlpha(alfa);
        placed_divider.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        text_confirmed.setAlpha(alfa);
        textorderprocessed.setAlpha(alfa);
        view_order_pickup.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        ready_divider.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        textorderpickup.setAlpha(alfa);
        orderpickup.setAlpha(alfa);

    }



    private void getENashRequest(){


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
                Utility.BASE_URL+"/eNashRequest", new JSONObject(postParam), new com.android.volley.Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("CurrentLoan", response.toString());
                progressBar.dismiss();

                Log.i("Result","---"+response);
                flag = response.optBoolean("status");

                if (response.optBoolean("status"))
                {
                    eNash.setVisibility(View.VISIBLE);
                    //enashLayout.setVisibility(View.VISIBLE);
                }
                else
                {
                    eNash.setVisibility(View.GONE);
                    //enashLayout.setVisibility(View.GONE);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("CurrentLoan", "Error:" + error.getMessage());
                progressBar.dismiss();

                //eNash.setVisibility(View.GONE);
                enashLayout.setVisibility(View.GONE);
                //Toast.makeText(HomeActivity.this, "Something went wrong, Please try after some time !", Toast.LENGTH_SHORT).show();
            }

        }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

        };

        jsonObjReq.setTag("CurrentLoan");
        queue.add(jsonObjReq);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.btENash:

                startActivity(new Intent(getContext(), RegisterMandate.class));
                break;
        }
    }
}
