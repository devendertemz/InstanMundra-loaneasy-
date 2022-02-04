package com.loaneasy.new_user_details;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.loaneasy.network.ApiRequest;
import com.loaneasy.R;
import com.loaneasy.Service.GPSTracker;
import com.loaneasy.utils.ConnectionCheck;
import com.loaneasy.utils.UserSharedPreference;
import com.loaneasy.utils.Utility;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class UserProfessionalDetails extends AppCompatActivity implements View.OnClickListener{

    private EditText companyName, companyAddress, totalSalary;
    private TextView pickReference1,pickReference2, referenceName1,referenceName2;
    private AppCompatSpinner sp_workingYears;
    private String getWorkingYears = "", getReferenceName1, getReferenceName2,getReferencePhone1, getReferencePhone2,
            TAG = "UserProfessionalDetails" ,version;
    private Button submitUserDetails;

    private String name, email, gender, houseType, pinCode, district, state, dob, address, panNo, aadharNo;
    final int PICK_CONTACT1=1;
    final int PICK_CONTACT2=2;

    UserSharedPreference sharedPreference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_professional_details);

        sharedPreference = new UserSharedPreference(this);

        Intent intent = getIntent();
        if (intent!= null) {

            name = intent.getStringExtra("userName");
            email = intent.getStringExtra("userEmail");
            gender = intent.getStringExtra("gender");
            houseType = intent.getStringExtra("houseType");
            pinCode = intent.getStringExtra("pincode");
            district = intent.getStringExtra("district");
            state = intent.getStringExtra("state");
            dob = intent.getStringExtra("dob");
            address = intent.getStringExtra("userAddress");
            panNo = intent.getStringExtra("pan");
            aadharNo = intent.getStringExtra("aadhar");

        }


        try {
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
            Log.i("appVersion",version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        companyName = findViewById(R.id.etCompanyName);
        companyAddress = findViewById(R.id.etCompanyAddress);
        totalSalary = findViewById(R.id.etSalary);


        pickReference1 = findViewById(R.id.tvPickReference1);
        pickReference1.setOnClickListener(this);
        pickReference2 =  findViewById(R.id.tvPickReference2);
        pickReference2.setOnClickListener(this);
        referenceName1 = findViewById(R.id.tvReferenceName1);
        referenceName1.setOnClickListener(this);
        referenceName2 = findViewById(R.id.tvReferenceName2);
        referenceName2.setOnClickListener(this);

        submitUserDetails = findViewById(R.id.btSubmitUserDetails);
        submitUserDetails.setOnClickListener(this);

        sp_workingYears = findViewById(R.id.spWorkingYears);

        String[] workingYears = {"Working years", "1","2","3","4","5-7","7-10","Above 10", };
        ArrayAdapter<CharSequence> workingYearsAdapter = new ArrayAdapter<CharSequence>(UserProfessionalDetails.this, R.layout.spinner_text, workingYears);
        workingYearsAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        sp_workingYears.setAdapter(workingYearsAdapter);
        sp_workingYears.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                //getGender = spGender.getSelectedItem().toString();
                //sharedPreference.setGender(spGender.getSelectedItemPosition());
                getWorkingYears = sp_workingYears.getSelectedItem().toString();


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


    private boolean allValidated() {

        if (companyName.getText().toString().trim().isEmpty()) {
            Toast.makeText(UserProfessionalDetails.this, "Please enter your company name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (companyAddress.getText().toString().trim().isEmpty()) {
            Toast.makeText(UserProfessionalDetails.this, "Please enter your company address", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Integer.parseInt(totalSalary.getText().toString().trim()) < 25000) {
            Toast.makeText(UserProfessionalDetails.this, "Salary must be greater than Rs.25,000", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (getWorkingYears.equalsIgnoreCase("Working years")) {
            Toast.makeText(UserProfessionalDetails.this, "Please select working years in current company", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (referenceName1.getText().toString().isEmpty()) {
            Toast.makeText(UserProfessionalDetails.this, "Please select your first reference", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (referenceName2.getText().toString().isEmpty()) {
            Toast.makeText(UserProfessionalDetails.this, "Please select your Second reference", Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;

    }


    private String getContactName(final String phoneNumber, Context context)
    {
        Uri uri=Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName="";
        Cursor cursor=context.getContentResolver().query(uri,projection,null,null,null);

        if (cursor != null) {
            if(cursor.moveToFirst()) {
                contactName=cursor.getString(0);
            }
            cursor.close();
        }

        return contactName;
    }



    private void sendUserDetails() {

        final ProgressDialog progressBar;
        progressBar = new ProgressDialog(UserProfessionalDetails.this, R.style.AppCompatProgressDialogStyle);
        progressBar.setCancelable(false);
        progressBar.setMessage("Please Wait...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();


        GPSTracker gps = new GPSTracker(getApplicationContext());
        double latitude = gps.getLatitude();
        double longitude= gps.getLongitude();

        String fName = "";
        String lName = "";
        if (name.contains(" ")) {
            fName = name.substring(0, name.lastIndexOf(" "));
            lName = name.substring(name.lastIndexOf(" ") + 1, name.length());
        } else
            fName = name;


        RequestQueue queue = Volley.newRequestQueue(this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("user_id", sharedPreference.getUserId());
        postParam.put("first_name", fName);
        postParam.put("last_name", lName);
        postParam.put("phone_no", sharedPreference.getUserPhoneNo());
        postParam.put("profile_completed", "1");
        postParam.put("gender", gender);
        postParam.put("d_o_b", dob);
        postParam.put("official_mail", email);
        postParam.put("emp_type", "");
        postParam.put("take_home_salary", totalSalary.getText().toString().trim());
        postParam.put("address_state", state);
        postParam.put("address_city", district);
        postParam.put("local_address", address);
        postParam.put("pin_code",pinCode);
        postParam.put("latitude", String.valueOf(latitude));
        postParam.put("longitude", String.valueOf(longitude));
        postParam.put("pan_card_no", panNo);
        postParam.put("aadhar_card_no", aadharNo);
        postParam.put("social_media_type", sharedPreference.getSocialMediaType());
        postParam.put("social_name", sharedPreference.getUserSocialName());
        postParam.put("social_id", sharedPreference.getSocialMediaId());
        postParam.put("social_email", sharedPreference.getUserEmail());
        postParam.put("social_profile_pic", sharedPreference.getProfilePic());
        postParam.put("marital_status", "N.A.");
        postParam.put("house_type", houseType);
        postParam.put("staying_years", "");
        postParam.put("salary_mode", "");
        postParam.put("working_years", getWorkingYears);
        postParam.put("current_loan","");
        postParam.put("existing_loan", "N.A.");
        postParam.put("app_version", version);
        postParam.put("current_loan_emi", "");
        postParam.put("company_name", companyName.getText().toString().trim());
        postParam.put("company_address",companyAddress.getText().toString().trim());
        postParam.put("hr_email", "");
        postParam.put("hr_phone", "");
        postParam.put("user_area", sharedPreference.getUserArea());
        postParam.put("primary_reference_name",getReferenceName1);
        postParam.put("primary_reference_phone",getReferencePhone1);
        postParam.put("secondary_reference_name", getReferenceName2);
        postParam.put("secondary_reference_phone", getReferencePhone2);


        //Log.i("Obj","="+postParam);


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Utility.BASE_URL+"/newUserDetails", new JSONObject(postParam), new com.android.volley.Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                progressBar.dismiss();
                if(response.optBoolean("status"))
                {
                    Toast.makeText(UserProfessionalDetails.this, response.optString("msg"), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), UploadUserFiles.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Toast.makeText(UserProfessionalDetails.this, response.optString("msg"), Toast.LENGTH_SHORT).show();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.dismiss();
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Log.i("VolleyError","="+error);
                Log.i("VolleyError","="+error.getMessage());
                //progressBar11.dismiss();
                Toast.makeText(UserProfessionalDetails.this, "Something went wrong, Please try again !", Toast.LENGTH_SHORT).show();
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

    }

    private void sendDataToSubmitDetails() {

        final ProgressDialog progressBar;
        progressBar = new ProgressDialog(UserProfessionalDetails.this, R.style.AppCompatProgressDialogStyle);
        progressBar.setCancelable(false);
        progressBar.setMessage("Please Wait...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();


        RestAdapter adapter = new RestAdapter.Builder().setEndpoint(Utility.BASE_URL).setLogLevel(RestAdapter.LogLevel.FULL).build();
        ApiRequest api = adapter.create(ApiRequest.class);

        String userId = sharedPreference.getUserId();
        String phoneNo = sharedPreference.getUserPhoneNo();
        String socialMediaId = sharedPreference.getSocialMediaId();
        String socialEmail = sharedPreference.getUserEmail();
        String socialMediaType = sharedPreference.getSocialMediaType();
        String socialName = sharedPreference.getUserSocialName();
        String socialProfilePic = sharedPreference.getProfilePic();
        String location = sharedPreference.getLocation();

        String fName = "";
        String lName = "";
        if (name.contains(" ")) {
            fName = name.substring(0, name.lastIndexOf(" "));
            lName = name.substring(name.lastIndexOf(" ") + 1, name.length());
        } else
            fName = name;


        GPSTracker gps = new GPSTracker(getApplicationContext());
        double latitude = gps.getLatitude();
        double longitude= gps.getLongitude();

        Log.d("TAG111", "UID--" + userId);
        Log.d("TAG111", "LOCATION--" + location);
        api.userDetails2(userId, fName, lName, phoneNo,
                gender, dob, email, "", totalSalary.getText().toString().trim(), state,
                district, address,"1", pinCode, String.valueOf(latitude),String.valueOf(longitude),version,
                panNo, aadharNo, socialMediaType, socialName, socialMediaId, socialEmail, socialProfilePic, "",
                houseType, "", "", getWorkingYears, "", "",
                "", companyName.getText().toString().trim(), companyAddress.getText().toString().trim()
                , new Callback<Response>() {
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

                            Log.i("RESULT", "---->" + output);

                            JSONObject jsonObject = new JSONObject(output);


                            //boolean status  = jsonObject.optBoolean("status");


                            if(jsonObject.optBoolean("status"))
                            {
                                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), UploadUserFiles.class);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "something went wrong", Toast.LENGTH_SHORT).show();
                            }

                           /* String profileCompleted = jsonObject.optString("profile_completed");
                            if (profileCompleted.equals("1")) {
                                sharedPreference.setSignFlag(3);
                                startActivity(new Intent(UserProfessionalDetails.this, BankDetailsNewActivity.class));
                                finish();
                            }*/

                           /* startActivity(new Intent(getActivity(), HomeActivity.class).putExtra("city", getCity));
                            finish();*/

                        } catch (Exception e) {
                            e.printStackTrace();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            // Check for the request code, we might be usign multiple startActivityForReslut
            switch (requestCode) {

                case PICK_CONTACT1:
                    Cursor cursor = null;
                    try {

                        Uri uri = data.getData();
                        cursor = getContentResolver().query(uri, null, null, null, null);
                        cursor.moveToFirst();
                        int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        getReferencePhone1 = cursor.getString(phoneIndex);
                        getReferenceName1 = getContactName(getReferencePhone1, getApplicationContext());

                        referenceName1.setText(getReferenceName1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;



                case PICK_CONTACT2:

                    Cursor cursor2 = null;
                    try {

                        Uri uri2 = data.getData();
                        cursor2 = getContentResolver().query(uri2, null, null, null, null);
                        cursor2.moveToFirst();
                        int  phoneIndex = cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        getReferencePhone2 = cursor2.getString(phoneIndex);
                        getReferenceName2 = getContactName(getReferencePhone2, getApplicationContext());
                        referenceName2.setText(getReferenceName2);
                        //textView2.setText(phoneNo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        } else {
            Log.e("UserProfessionalDetails", "Failed to pick contact");
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.tvPickReference1:

                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(contactPickerIntent, PICK_CONTACT1);

                break;


            case R.id.tvPickReference2:

                Intent contactPickerIntent2 = new Intent(Intent.ACTION_PICK,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(contactPickerIntent2, PICK_CONTACT2);
                break;

            case R.id.tvReferenceName1:
                Intent contactPickerIntent11 = new Intent(Intent.ACTION_PICK,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(contactPickerIntent11, PICK_CONTACT1);
                break;

            case R.id.tvReferenceName2:
                Intent contactPickerIntent22 = new Intent(Intent.ACTION_PICK,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(contactPickerIntent22, PICK_CONTACT2);
                break;

            case R.id.btSubmitUserDetails:
                if (new ConnectionCheck(UserProfessionalDetails.this).isNetworkAvailable()) {
                    if (allValidated())
                    {
                        sendUserDetails();
                        //sendDataToSubmitDetails();
                    }
                }
                else
                {
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please Check Your Internet Connection", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                break;
        }
    }
}
