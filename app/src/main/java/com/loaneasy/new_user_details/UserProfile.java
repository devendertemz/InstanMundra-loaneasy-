package com.loaneasy.new_user_details;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.material.snackbar.Snackbar;
import com.loaneasy.network.ApiRequest;
import com.loaneasy.R;
import com.loaneasy.utils.ConnectionCheck;
import com.loaneasy.utils.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UserProfile extends AppCompatActivity implements View.OnClickListener {


    private TextView dateOfBirth;
    private AppCompatSpinner sp_gender, sp_houseType;
    private EditText fullName, userEmail,pinCode, userAddress, panNo, aadharNo;
    private Button nextPersonalDetails;

    private String getGender = "", getHouseType = "", getDob ="", getDistrict ="", getState = "";
    private Pattern pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");
    protected final int REQUEST_CHECK_SETTINGS = 0x1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_personal_details);

        displayLocationSettingsRequest(getApplicationContext());

        fullName = findViewById(R.id.etFullName);
        userEmail = findViewById(R.id.etUserEmail);
        pinCode = findViewById(R.id.etPinCode);
        userAddress = findViewById(R.id.etUserAddress);
        panNo = findViewById(R.id.etPanNo);
        aadharNo = findViewById(R.id.etAadharNo);


        pinCode.addTextChangedListener(new MyTextWatcher(pinCode));



        dateOfBirth = findViewById(R.id.tvDateOfBirth);
        dateOfBirth.setOnClickListener(this);
        nextPersonalDetails = findViewById(R.id.btNextPersonalDetails);
        nextPersonalDetails.setOnClickListener(this);


        sp_gender = findViewById(R.id.spGender);
        sp_houseType = findViewById(R.id.spTypeHouse);



        String[] division = {"Select", "Male", "Female"};
        ArrayAdapter<CharSequence> divisionAdapter = new ArrayAdapter<CharSequence>(UserProfile.this, R.layout.spinner_text, division);
        divisionAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        sp_gender.setAdapter(divisionAdapter);
        sp_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                getGender =  sp_gender.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        String[] houseTypes = {"Select", "Own","Rented"};
        ArrayAdapter<CharSequence> houseAdapter = new ArrayAdapter<CharSequence>(UserProfile.this, R.layout.spinner_text, houseTypes);
        divisionAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        sp_houseType.setAdapter(houseAdapter);
        sp_houseType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                //getGender = spGender.getSelectedItem().toString();
                //sharedPreference.setGender(spGender.getSelectedItemPosition());
                getHouseType = sp_houseType.getSelectedItem().toString();
                if(getHouseType.equalsIgnoreCase("Own"))
                {
                    getHouseType = "1";
                }
                else if(getHouseType.equalsIgnoreCase("Rented"))
                {
                    getHouseType = "3";
                }
                else if(getHouseType.equalsIgnoreCase("Select"))
                {
                    getHouseType = "Select";
                }
                //getHouseType = sp_houseType.getSelectedItem().toString();

                Log.i("getHouseType","="+getHouseType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.etPinCode:
                    if (pinCode.getText().toString().trim().length()==6) {
                        sendDataForPincodeDetails(pinCode.getText().toString().trim());
                        //sharedPreference.setPincode(etPinCode.getText().toString().trim());
                    }
                    break;
            }
        }
    }

    private void sendDataForPincodeDetails(String pinCode) {

        final RestAdapter adapter = new RestAdapter.Builder().setEndpoint(Utility.PINCODE_URL + pinCode).build();
        ApiRequest api = adapter.create(ApiRequest.class);

        api.getPincodeDetails(new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                BufferedReader reader = null;
                String output = "";

                try {

                    //Initializing buffered reader
                    reader = new BufferedReader(new InputStreamReader(response.getBody().in()));

                    //Reading the output in the string
                    output = reader.readLine();
                    Log.d("TAG111", "---" + output);

                    JSONObject object = new JSONObject(output);
                    if (object.getString("Status").equalsIgnoreCase("success")){
                        JSONArray array = object.getJSONArray("PostOffice");
                        JSONObject object1 = array.getJSONObject(0);
                        String sState = object1.getString("State");
                        String sDistrict = object1.getString("District");


                        getState = sState;
                        getDistrict = sDistrict;
                      /*  if(sState.equalsIgnoreCase("Delhi") || sState.equalsIgnoreCase("Uttar Pradesh") || sState.equalsIgnoreCase("Haryana"))
                        {
                            //Eligible for loan
                        }
                        else
                        {
                            AreaDialog();
                        }*/


                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("TAG111", "--error-->>" + error.getMessage());
            }
        });
    }


    private void AreaDialog() {

        final Dialog dialogAge = new Dialog(UserProfile.this);
        dialogAge.setCancelable(false);
        dialogAge.setContentView(R.layout.dialog_area);
        dialogAge.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView tvOkDialAge = dialogAge.findViewById(R.id.tvOkDialAge);


        tvOkDialAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAge.dismiss();
                finish();
                System.exit(0);

            }
        });

        dialogAge.show();
    }


    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i("TAG", "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
                        Log.i("rr","------>");
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(UserProfile.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("TAG", "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i("rsr","------>");
                        break;

                    case  LocationSettingsStatusCodes.CANCELED:
                        Toast.makeText(UserProfile.this, "User Cancelled Location", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private boolean allValidated() {

        if (fullName.getText().toString().trim().isEmpty()) {
            Toast.makeText(UserProfile.this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (userEmail.getText().toString().trim().isEmpty()) {
            Toast.makeText(UserProfile.this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (pinCode.getText().toString().trim().isEmpty()) {
            Toast.makeText(UserProfile.this, "Please enter pin code", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (userAddress.getText().toString().trim().isEmpty()) {
            Toast.makeText(UserProfile.this, "Please enter your address", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!validatePanCard(panNo.getText().toString().trim())){

            Toast.makeText(UserProfile.this, "Please enter Valid PAN Number", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (aadharNo.getText().toString().trim().isEmpty()) {
            Toast.makeText(UserProfile.this, "Please enter valid aadhar no", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (getGender.equalsIgnoreCase("Select")) {
            Toast.makeText(UserProfile.this, "Please select gender", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (getHouseType.equalsIgnoreCase("Select")) {
            Toast.makeText(UserProfile.this, "Please select house type", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(getDob.isEmpty())
        {
            Toast.makeText(UserProfile.this, "Please select your Date of birth", Toast.LENGTH_SHORT).show();
            return false;
        }




        return  true;

    }



    public boolean validatePanCard(String panCard) {

        if (!isValidPan(panCard)) {
            setErrorInputLayout(panNo, getString(R.string.pancard_not_valid));
            return false;
        } else {
            return true;
        }
    }

    private void setErrorInputLayout(TextView editText, String msg) {
        editText.setError(msg);
        editText.requestFocus();
    }

    private boolean isValidPan(String panCard) {
        Matcher matcher = pattern.matcher(panCard);
        return matcher.matches();

    }


    private void getAge(int year, int month, int day){
        int age;

        final Calendar calenderToday = Calendar.getInstance();
        int currentYear = calenderToday.get(Calendar.YEAR);
        int currentMonth = 1 + calenderToday.get(Calendar.MONTH);
        int todayDay = calenderToday.get(Calendar.DAY_OF_MONTH);

        age = currentYear - year;

        if(month > currentMonth) {
            --age;
        } else if(month == currentMonth) {
            if(day > todayDay){
                --age;
            }
        }
        if (age < 18){
            showDialogAge();
        }
    }

    private void showDialogAge() {

        final Dialog dialogAge = new Dialog(UserProfile.this);
        dialogAge.setCancelable(false);
        dialogAge.setContentView(R.layout.dialog_age);
        dialogAge.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView tvOkDialAge = dialogAge.findViewById(R.id.tvOkDialAge);

        tvOkDialAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAge.dismiss();
            }
        });

        dialogAge.show();
    }




    @Override
    public void onClick(View v) {

            switch (v.getId())
            {
                case R.id.tvDateOfBirth:
                    final Calendar calendar = Calendar.getInstance();
                    int mYear = calendar.get(Calendar.YEAR);
                    int mMonth = calendar.get(Calendar.MONTH);
                    int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(UserProfile.this, R.style.DatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                            String sMonth = String.valueOf(month+1);
                            String sDay = String.valueOf(dayOfMonth);

                            if (sDay.length()==1){
                                sDay = "0"+sDay;
                            }
                            if (sMonth.length()==1){
                                sMonth = "0"+sMonth;
                            }



                            getDob = sDay + "-" + sMonth + "-" + year;
                            dateOfBirth.setText(getDob);
                            //sharedPreference.setDob(getDob);
                            getAge(year, month + 1, dayOfMonth);
                        }
                    }, mYear, mMonth, mDay);
                    datePickerDialog.show();


                break;

                case R.id.btNextPersonalDetails:

                    if (new ConnectionCheck(UserProfile.this).isNetworkAvailable()) {

                        if (allValidated())
                        {
                            Intent userProfessionalDetails = new Intent(getApplicationContext(), UserProfessionalDetails.class);
                            userProfessionalDetails.putExtra("userName", fullName.getText().toString().trim());
                            userProfessionalDetails.putExtra("userEmail", userEmail.getText().toString().trim());
                            userProfessionalDetails.putExtra("gender", getGender);
                            userProfessionalDetails.putExtra("houseType", getHouseType);
                            userProfessionalDetails.putExtra("pincode", pinCode.getText().toString().trim());
                            userProfessionalDetails.putExtra("district", getDistrict);
                            userProfessionalDetails.putExtra("state", getState);
                            userProfessionalDetails.putExtra("dob", getDob);
                            userProfessionalDetails.putExtra("userAddress", userAddress.getText().toString().trim());
                            userProfessionalDetails.putExtra("pan", panNo.getText().toString().trim());
                            userProfessionalDetails.putExtra("aadhar", aadharNo.getText().toString().trim());
                            startActivity(userProfessionalDetails);


                        }
                    }
                    else {
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please Check Your Internet Connection", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }

                    break;
            }
    }
}
