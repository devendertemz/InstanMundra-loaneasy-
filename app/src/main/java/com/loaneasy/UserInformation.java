package com.loaneasy;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import com.loaneasy.Beans.UserPointsBeans;
import com.loaneasy.network.ApiRequest;
import com.loaneasy.utils.UserSharedPreference;
import com.loaneasy.utils.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UserInformation extends AppCompatActivity {

    private AppCompatSpinner spGender, employmentType, salary, spCurrentLoan, spHouseType;
    TextView dob, skip, tvGenderText, tvDobText, tvMaritalStatusText, tvHouseTypeText, tvSkip, tvNameWlcm,
            submitUserDetails, tvFirstName, tvLastName, tvOfficialEmail;
    private EditText etState, etCity, etPinCode, etLocalAddress, panCardNo, aadharCardNo;
    private String getGender, getDob, getCurrentLoan, getHouseType;
    ScrollView scrollView;

    ArrayAdapter<String> currentLoanAdapter, houseTypeAdapter;
    ArrayList<UserPointsBeans> alCurrentLoan;
    ArrayList<String> alHouseTypeList;
    UserSharedPreference sharedPreference;
    public static ArrayList<UserPointsBeans> alSpinnersData;
    String[] sarCurrentloan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);
        sharedPreference = new UserSharedPreference(this);
        alSpinnersData = new ArrayList<>();
        alCurrentLoan = new ArrayList<>();
        alHouseTypeList = new ArrayList<>();
        alHouseTypeList.add("Select");

        spGender = findViewById(R.id.spGender);
        spHouseType = findViewById(R.id.spHouseType);
        spCurrentLoan = findViewById(R.id.spCurrentLoan);

        tvSkip = findViewById(R.id.tvSkip);
        tvNameWlcm = findViewById(R.id.tvNameWlcm);
        tvGenderText = findViewById(R.id.tvGenderText);
        tvDobText = findViewById(R.id.tvDobText);
        tvHouseTypeText = findViewById(R.id.tvHouseTypeText);
        getPoints();

        String userName = sharedPreference.getUserSocialName();
        String email = sharedPreference.getUserEmail();
        String fname = userName.substring(0, userName.lastIndexOf(" "));
        String lname = userName.substring(userName.lastIndexOf(" "), userName.length());

        tvNameWlcm.setText("Welcome, "+fname);

        tvFirstName =  findViewById(R.id.etFirstName);
        tvLastName =  findViewById(R.id.etLastName);
        tvOfficialEmail =  findViewById(R.id.etOfficialEmail);
        etState =  findViewById(R.id.etState);
        etCity =  findViewById(R.id.etCity);
        etPinCode =  findViewById(R.id.etPinCode);
        etLocalAddress =  findViewById(R.id.etLocalAddress);
        tvOfficialEmail.setText(email);
        tvFirstName.setText(fname);
        tvLastName.setText(lname);

        etPinCode.addTextChangedListener(new MyTextWatcher(etPinCode));

        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserInformation.this, HomeActivity.class));
                finish();
            }
        });
        
        submitUserDetails = findViewById(R.id.btSubmitUserInfo);

        String[] division = {"Male", "Female"};
        ArrayAdapter<CharSequence> divisionAdapter = new ArrayAdapter<CharSequence>(getApplicationContext(), R.layout.spinner_text, division);
        divisionAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spGender.setAdapter(divisionAdapter);

        spGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                getGender = spGender.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sarCurrentloan = new String[]{"Select", "0", "1", "2", "3", "4", "5"};
        currentLoanAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_text, sarCurrentloan);
        currentLoanAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spCurrentLoan.setAdapter(currentLoanAdapter);
        spCurrentLoan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (!spCurrentLoan.getSelectedItem().toString().equalsIgnoreCase("select")) {
                    if (spCurrentLoan.getSelectedItem().toString().equals("0")) {
                        getCurrentLoan = alCurrentLoan.get(0).getInfo_id();
                    } else if (spCurrentLoan.getSelectedItem().toString().equals("1") ||
                            spCurrentLoan.getSelectedItem().toString().equals("2")) {
                        getCurrentLoan = alCurrentLoan.get(1).getInfo_id();
                    } else if (Integer.parseInt(spCurrentLoan.getSelectedItem().toString()) >= 3) {
                        getCurrentLoan = alCurrentLoan.get(2).getInfo_id();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spHouseType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!spHouseType.getSelectedItem().toString().equalsIgnoreCase("select")){
                    for (int j = 0; j < alSpinnersData.size(); j++) {
                        if (alSpinnersData.get(j).getSub_category().equalsIgnoreCase(spHouseType.getSelectedItem().toString())) {
                            getHouseType = alSpinnersData.get(j).getInfo_id();
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        dob = findViewById(R.id.tvDob);
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(UserInformation.this, R.style.DatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dob.setText(dayOfMonth + "-" + (month + 1) + "-" + year);

                        //assignmentDate = year+"-"+ (month+1) +"-"+dayOfMonth;
                        getDob = dayOfMonth + "-" + (month + 1) + "-" + year;

                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        submitUserDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (allValidated()) {
                    Intent intent = new Intent(getApplicationContext(), UserOfficialDetails.class);
                    intent.putExtra("firstName", tvFirstName.getText().toString().trim());
                    intent.putExtra("lastName", tvLastName.getText().toString().trim());
                    intent.putExtra("officialEmail", tvOfficialEmail.getText().toString().trim());
                    intent.putExtra("gender", getGender);
                    intent.putExtra("dob", getDob);
                    intent.putExtra("currentLoan", getCurrentLoan);
                    intent.putExtra("houseType", getHouseType);
                    intent.putExtra("etState", etState.getText().toString().trim());
                    intent.putExtra("etCity", etCity.getText().toString().trim());
                    intent.putExtra("etPinCode", etPinCode.getText().toString().trim());
                    intent.putExtra("etLocalAddress", etLocalAddress.getText().toString().trim());
                    startActivity(intent);
                }

            }


        });
    }

    private void getPoints() {
        final ProgressDialog progressBar;
        progressBar = new ProgressDialog(UserInformation.this);
        progressBar.setCancelable(false);
        progressBar.setMessage("Please Wait...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();

        RestAdapter adapter = new RestAdapter.Builder().setEndpoint(Utility.BASE_URL).build();
        ApiRequest api = adapter.create(ApiRequest.class);

        api.getUserPoints(new Callback<Response>() {
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

                    JSONArray jsonArray = new JSONArray(output);
                    Log.i("response", "----------->" + jsonArray);
                    //Toast.makeText(UserInformation.this, jsonArray.toString(), Toast.LENGTH_SHORT).show();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.optJSONObject(i);

                        alSpinnersData.add(new UserPointsBeans(jsonObject.optString("info_id"),
                                                                            jsonObject.optString("category"),
                                                                            jsonObject.optString("sub_category"),
                                                                            jsonObject.optString("points")));
                    }

                    for (int j = 0; j < alSpinnersData.size(); j++) {
                        if (alSpinnersData.get(j).getCategory().equalsIgnoreCase("Current Loan")) {
                            alCurrentLoan.add(alSpinnersData.get(j));
                        }

                        if (alSpinnersData.get(j).getCategory().equalsIgnoreCase("House Type")) {
                            alHouseTypeList.add(alSpinnersData.get(j).getSub_category());
                        }
                    }

                    houseTypeAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_text, alHouseTypeList);
                    houseTypeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
                    spHouseType.setAdapter(houseTypeAdapter);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("TAG111", "--getUserPoints error-->>" + error.getMessage());
                progressBar.dismiss();
                Toast.makeText(getApplicationContext(), "Internal Server Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //for pincode details
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

                        etState.setText(sState);
                        etCity.setText(sDistrict);
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

    private boolean allValidated() {

        if (!validateEmail(tvOfficialEmail.getText().toString().trim()))
            return false;

        if (tvFirstName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter first name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (tvLastName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter last name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (getDob == null) {
            Toast.makeText(this, "Please Select Date of Birth", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (spCurrentLoan.getSelectedItem().toString().equalsIgnoreCase("select")) {
            Toast.makeText(this, "Please select current loan", Toast.LENGTH_SHORT).show();
            return false;
        }


        if (spHouseType.getSelectedItem().toString().equalsIgnoreCase("select")) {
            Toast.makeText(this, "Please Select House Type", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etPinCode.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter pin code", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etCity.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter your city name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etState.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter your state name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etLocalAddress.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter your address", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    ///for email validation
    public boolean validateEmail(String emaill) {

        if (!isValidEmail(emaill)) {
            setErrorInputLayout(tvOfficialEmail, getString(R.string.email_not_valid));
            return false;
        } else {
            return true;
        }
    }

    public void setErrorInputLayout(TextView editText, String msg) {
        editText.setError(msg);
        editText.requestFocus();
    }

    public boolean isValidEmail(String strEmail) {
        return strEmail != null && android.util.Patterns.EMAIL_ADDRESS.matcher(strEmail).matches();
    }


   /* private boolean validatePanCard() {
        if (panCardNo.getText().toString().trim().isEmpty()) {
            inputLayoutPancard.setError("Please Enter Pan Card No");
            requestFocusBottom(panCardNo);
            return false;
        } else {
            inputLayoutPancard.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateAadharCard() {
        if (aadharCardNo.getText().toString().trim().isEmpty()) {
            inputLayoutAadhar.setError("Please Enter Aadhar Card No");
            requestFocusBottom(aadharCardNo);
            return false;
        } else {
            inputLayoutAadhar.setErrorEnabled(false);
        }

        return true;
    }*/


    public class MyTextWatcher implements TextWatcher {

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
                        if (etPinCode.getText().toString().trim().length()==6)
                            sendDataForPincodeDetails(etPinCode.getText().toString().trim());
                    break;
            }
        }
    }


}
