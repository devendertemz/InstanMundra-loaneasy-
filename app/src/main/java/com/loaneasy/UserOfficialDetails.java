package com.loaneasy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatSpinner;

import com.google.android.material.snackbar.Snackbar;
import com.loaneasy.Beans.UserPointsBeans;
import com.loaneasy.utils.ConnectionCheck;
import com.loaneasy.utils.UserSharedPreference;
import com.loaneasy.utils.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UserOfficialDetails extends AppCompatActivity {

    Pattern pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");

    private AppCompatSpinner spEmpType, spSalary, spSalaryMode, spWorkingYearOrg;
    private EditText etPanCard, etAadharCard, etCompanyName, etCompanyAddress;
    private String getFirstName, getLastName, getEmail, getGender, getDob, getMaritalStatus, getHouseType,
            getState, getCity, getPinCode, getLocalAddress, getEmpType, getSalary, getSalaryMode, getWorkingYears,
            getCurrentLoan;

    AppCompatCheckBox checkBox;
    JSONArray data = null;
    Button btSubmitAllDetails;
    public TextView empTypeText, takeHomeSalary, salaryModeText, workingYearsText, currentLoanText, existingLoanTex;

    ArrayAdapter<String> employeeTypeAdapter, salaryModeAdapter, workingYearAdapter;
    ArrayList<UserPointsBeans> alWorkingYear;
    ArrayList<String> alEmpType, alSalaryMode;
    ProgressDialog progressBar;

    UserSharedPreference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_official_details);

        sharedPreference = new UserSharedPreference(this);
        Bundle bundle = getIntent().getExtras();
        getFirstName = bundle.getString("firstName");
        getLastName = bundle.getString("lastName");
        getEmail = bundle.getString("officialEmail");
        getGender = bundle.getString("gender");
        getDob = bundle.getString("dob");
        getCurrentLoan = bundle.getString("currentLoan");
        getHouseType = bundle.getString("houseType");
        getState = bundle.getString("etState");
        getCity = bundle.getString("etCity");
        getPinCode = bundle.getString("etPinCode");
        getLocalAddress = bundle.getString("etLocalAddress");

        checkBox = findViewById(R.id.cbTermsCondition);

        empTypeText = findViewById(R.id.tvEmpTypeText);
        takeHomeSalary = findViewById(R.id.tvTakeHomeSalaryText);
        salaryModeText = findViewById(R.id.tvSalaryModeText);
        workingYearsText = findViewById(R.id.tvWorkingYearsText);
        currentLoanText = findViewById(R.id.tvCurrentLoanText);
        btSubmitAllDetails = findViewById(R.id.btSubmitAllDetails);

        spEmpType = findViewById(R.id.spEmpType);
        spSalaryMode = findViewById(R.id.spSalaryMode);
        spSalary = findViewById(R.id.spSalary);
        spWorkingYearOrg = findViewById(R.id.spWorkingYearOrg);

        alWorkingYear = new ArrayList<UserPointsBeans>();
        alEmpType = new ArrayList<>();
        alSalaryMode = new ArrayList<>();

        alEmpType.add("Select");
        alSalaryMode.add("Select");

       /* try {
            if (!userPoints.isEmpty())
                data = new JSONArray(userPoints);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (data != null) {
            empTypeList = new ArrayList<UserPointsBeans>();
            salaryModeList = new ArrayList<UserPointsBeans>();
            existingLoanTypeList = new ArrayList<UserPointsBeans>();

            for (int i = 0; i < data.length(); i++) {
                JSONObject dataObject = null;
                try {
                    dataObject = data.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                UserPointsBeans userPointsBeans = new UserPointsBeans(dataObject.optString("s_no"),
                        dataObject.optString("category"),
                        dataObject.optString("sub_category"), dataObject.optString("points"));

                if (dataObject.optString("category").equalsIgnoreCase("Employment Type")) {
                    //pointsList = new ArrayList<UserPointsBeans>();
                    empTypeList.add(userPointsBeans);


                } else if (dataObject.optString("category").equalsIgnoreCase("Salary Mode")) {
                    //pointsList = new ArrayList<UserPointsBeans>();
                    salaryModeList.add(userPointsBeans);

                } else if (dataObject.optString("category").equalsIgnoreCase("Existing Loan")) {
                    //pointsList = new ArrayList<UserPointsBeans>();
                    existingLoanTypeList.add(userPointsBeans);

                }
            }

            employeeAdapter = new ArrayAdapter<UserPointsBeans>(getApplicationContext(), R.layout.spinner_text, empTypeList);
            employeeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
            spEmpType.setAdapter(employeeAdapter);

            salaryModeAdapter = new ArrayAdapter<UserPointsBeans>(getApplicationContext(), R.layout.spinner_text, salaryModeList);
            salaryModeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
            spSalaryMode.setAdapter(salaryModeAdapter);

            existingLoanAdapter = new ArrayAdapter<UserPointsBeans>(getApplicationContext(), R.layout.spinner_text, existingLoanTypeList);
            existingLoanAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
            spExistLoanStatus.setAdapter(existingLoanAdapter);
        }*/

        etAadharCard = findViewById(R.id.etAadharCard);
        etPanCard = findViewById(R.id.etPanCard);
        etCompanyName = findViewById(R.id.etCompanyName);
        etCompanyAddress = findViewById(R.id.etCompanyAddress);

        btSubmitAllDetails.setEnabled(false);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked) {
                    btSubmitAllDetails.setEnabled(true);
                }else
                    btSubmitAllDetails.setEnabled(false);
            }
        });
        
        //////////////
        for (int j = 0; j < UserInformation.alSpinnersData.size(); j++) {
            if (UserInformation.alSpinnersData.get(j).getCategory().equalsIgnoreCase("Employment Type")) {
                alEmpType.add(UserInformation.alSpinnersData.get(j).getSub_category());
            }

            if (UserInformation.alSpinnersData.get(j).getCategory().equalsIgnoreCase("Salary Mode")) {
                alSalaryMode.add(UserInformation.alSpinnersData.get(j).getSub_category());
            }

            if (UserInformation.alSpinnersData.get(j).getCategory().equalsIgnoreCase("Working Years")) {
                alWorkingYear.add(UserInformation.alSpinnersData.get(j));
            }
        }

        employeeTypeAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_text, alEmpType);
        employeeTypeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spEmpType.setAdapter(employeeTypeAdapter);
        
        spEmpType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (!spEmpType.getSelectedItem().toString().equalsIgnoreCase("select")){
                    for (int j = 0; j < UserInformation.alSpinnersData.size(); j++) {
                        if (UserInformation.alSpinnersData.get(j).getSub_category().equalsIgnoreCase(spEmpType.getSelectedItem().toString())) {
                            getEmpType = UserInformation.alSpinnersData.get(j).getInfo_id();
                        }
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        salaryModeAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_text, alSalaryMode);
        salaryModeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spSalaryMode.setAdapter(salaryModeAdapter);

        spSalaryMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (!spSalaryMode.getSelectedItem().toString().equalsIgnoreCase("select")){
                    for (int j = 0; j < UserInformation.alSpinnersData.size(); j++) {
                        if (UserInformation.alSpinnersData.get(j).getSub_category().equalsIgnoreCase(spSalaryMode.getSelectedItem().toString())) {
                            getSalaryMode = UserInformation.alSpinnersData.get(j).getInfo_id();
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        String[] sarWorkYears = new String[]{"Select", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        workingYearAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_text, sarWorkYears);
        workingYearAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spWorkingYearOrg.setAdapter(workingYearAdapter);
        spWorkingYearOrg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (!spWorkingYearOrg.getSelectedItem().toString().equalsIgnoreCase("select")) {
                    if (Integer.parseInt(spWorkingYearOrg.getSelectedItem().toString()) <= 2) {
                        getWorkingYears = alWorkingYear.get(0).getInfo_id();
                    } else if (Integer.parseInt(spWorkingYearOrg.getSelectedItem().toString()) >= 3 &&
                            Integer.parseInt(spWorkingYearOrg.getSelectedItem().toString()) <= 5 ) {
                        getWorkingYears = alWorkingYear.get(1).getInfo_id();
                    } else if (Integer.parseInt(spWorkingYearOrg.getSelectedItem().toString()) >= 6 &&
                            Integer.parseInt(spWorkingYearOrg.getSelectedItem().toString()) <= 10 ) {
                        getWorkingYears = alWorkingYear.get(2).getInfo_id();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        String[] takeHomeSalary = {"30000 - 50000", "51000 - 70000", "71000 - above"};
        final ArrayAdapter<CharSequence> salaryAdapter = new ArrayAdapter<CharSequence>(getApplicationContext(), R.layout.spinner_text, takeHomeSalary);
        salaryAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spSalary.setAdapter(salaryAdapter);

        spSalary.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    getSalary = spSalary.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btSubmitAllDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new ConnectionCheck(UserOfficialDetails.this).isNetworkAvailable()) {
                    if (allValidated()){}
                      //  sendDataToSubmitDetails();
                }
                else{
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please Check Your Internet Connection", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });

    }


    private boolean allValidated() {

        if (etCompanyName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter your company name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etCompanyAddress.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter your company address", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!validatePanCard(etPanCard.getText().toString().trim()))
            return false;

        if (etAadharCard.getText().toString().trim().isEmpty() || etAadharCard.getText().toString().trim().length()<12) {
            Toast.makeText(this, "Please enter aadhar card number", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (spEmpType.getSelectedItem().toString().equalsIgnoreCase("select")) {
            Toast.makeText(this, "Please select Employment type", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (spSalaryMode.getSelectedItem().toString().equalsIgnoreCase("select")) {
            Toast.makeText(this, "Please select salary mode", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (spWorkingYearOrg.getSelectedItem().toString().equalsIgnoreCase("select")) {
            Toast.makeText(this, "Please select working years", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public boolean validatePanCard(String panCard) {

        if (!isValidPan(panCard)) {
            setErrorInputLayout(etPanCard, getString(R.string.pancard_not_valid));
            return false;
        } else {
            return true;
        }
    }

    public void setErrorInputLayout(TextView editText, String msg) {
        editText.setError(msg);
        editText.requestFocus();
    }

    public boolean isValidPan(String panCard) {
        Matcher matcher = pattern.matcher(panCard);
        return matcher.matches();

    }


   /* private void sendDataToSubmitDetails() {
        progressBar = new ProgressDialog(UserOfficialDetails.this, R.style.AppCompatProgressDialogStyle);
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
        String socialName = sharedPreference.getUserName();
        String socialProfilePic = sharedPreference.getProfilePic();
        String location = sharedPreference.getLocation();
        sharedPreference.setUserCity(getCity);

        Log.d("TAG111","UID--"+userId);
        Log.d("TAG111","LOCATION--"+location);
        api.userDetails(userId, getFirstName, getLastName, phoneNo,
                getGender, getDob, getEmail, getEmpType, getSalary, getState,
                getCity, getLocalAddress, getPinCode, location.substring(0, location.indexOf(",")), location.substring(location.indexOf(",")+1, location.length()),
                etPanCard.getText().toString().trim(), etAadharCard.getText().toString().toLowerCase(), socialMediaType,
                socialName, socialMediaId, socialEmail, socialProfilePic, "", getHouseType, getSalaryMode,
                getWorkingYears, getCurrentLoan, "", etCompanyName.getText().toString().trim(),
                etCompanyAddress.getText().toString().trim()
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

                            JSONObject jsonObject = new JSONObject(output);
                            Log.i("TAG111", "-----responseuserDetails------>" + output);

                            String profileCompleted = jsonObject.optString("profile_completed");
                            if (profileCompleted.equals("1")){
                                sharedPreference.setSignFlag(3);
                            }

                            startActivity(new Intent(getApplicationContext(), HomeActivity.class).putExtra("city", getCity));
                            finish();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.i("failure", "---->>" + error);
                        Log.i("failure", "---->>" + error.getMessage());
                        progressBar.dismiss();
                        Toast.makeText(getApplicationContext(), "Internal Server Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }*/
}
