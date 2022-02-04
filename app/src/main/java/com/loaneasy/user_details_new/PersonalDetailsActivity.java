package com.loaneasy.user_details_new;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


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
import com.loaneasy.network.ApiRequest;
import com.loaneasy.Beans.UserPointsBeans;
import com.loaneasy.OpenFrontCamera;
import com.loaneasy.R;
import com.loaneasy.utils.ConnectionCheck;
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

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class PersonalDetailsActivity extends AppCompatActivity {


    static final int PICK_CONTACT=1;
    int  flagPhoneNoPick = 0;
    private String phoneNo;
    private AppCompatSpinner spGender, spCurrentLoan, spHouseType;
    TextView dob, tvGenderText, tvDobText, tvHouseTypeText, tvNameWlcm ,
            tvOk, tvCancel, tvOkDialAge, btSubmitUserInfo;
    private EditText etFirstName, etLastName, etOfficialEmail, etState, etCity, etPinCode,
            etLocalAddress, etLoanEmi, etText, etReferenceName1, etReferencePhone1,etReferenceName2, etReferencePhone2;
    private String getGender, getDob, getCurrentLoan, getHouseType, TAG= "PhoneNoActivity";
    ArrayAdapter<String> currentLoanAdapter, houseTypeAdapter;
    public ArrayList<UserPointsBeans> alCurrentLoan;
    ArrayList<String> alHouseTypeList;
    UserSharedPreference sharedPreference;
    public static ArrayList<UserPointsBeans> alSpinnersData;
    String[] sarCurrentloan;
    LinearLayout lyLoanEmi;
    String loanAmount="0", stayingYears;
    Dialog dialog, dialogAge;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    ArrayAdapter<UserPointsBeans>  houseAdapter;
    ArrayList<UserPointsBeans> houseTypeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);

        getPoints();

        //  displayLocationSettingsRequest(getApplicationContext());

        sharedPreference = new UserSharedPreference(PersonalDetailsActivity.this);

        Log.i("getArea","="+sharedPreference.getUserArea());

        alSpinnersData = new ArrayList<>();
        alCurrentLoan = new ArrayList<>();
        alHouseTypeList = new ArrayList<>();
        alHouseTypeList.add("Select");

        spGender = findViewById(R.id.spnGender);
        spHouseType = findViewById(R.id.spnHouseType);
        spCurrentLoan = findViewById(R.id.spnCurrentLoan);

        etLoanEmi = findViewById(R.id.etLoanEmi);
        lyLoanEmi = findViewById(R.id.lyLoanEmi);
        tvNameWlcm = findViewById(R.id.tvNameWlcm);
        tvGenderText = findViewById(R.id.tvGenderText);
        tvDobText = findViewById(R.id.tvDobText);
        tvHouseTypeText = findViewById(R.id.tvHouseTypeText);
        etFirstName =  findViewById(R.id.etFirstName);
        etLastName =  findViewById(R.id.etLastName);
        etOfficialEmail =  findViewById(R.id.etOfficialEmail);
        etState =  findViewById(R.id.etState);
        etCity =  findViewById(R.id.etCity);
        etPinCode =  findViewById(R.id.etPinCode);
        etLocalAddress =  findViewById(R.id.etLocalAddress);
        etLoanEmi =  findViewById(R.id.etLoanEmi);
        btSubmitUserInfo = findViewById(R.id.btSubmitUserInfo);


        etReferenceName1= findViewById(R.id.etReferenceName1);
        etReferencePhone1=findViewById(R.id.etReferencePhone1);
        etReferenceName2= findViewById(R.id.etReferenceName2);
        etReferencePhone2 = findViewById(R.id.etReferencePhone2);




        etReferencePhone1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {


                if(b == true && etReferencePhone1.getText().toString().isEmpty()){
                    flagPhoneNoPick = 1;
                    getPhoneNoFromContacts();
                    // etReferencePhone1.setText(phoneNo);
                }
            }
        });

        etReferencePhone2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if(b == true  && etReferencePhone2.getText().toString().isEmpty()){
                    flagPhoneNoPick =2;
                    getPhoneNoFromContacts();
                    // etReferencePhone2.setText(phoneNo);
                }
            }
        });






        String userName = sharedPreference.getUserSocialName();
        if (!userName.isEmpty()){
            String fname = userName.substring(0, userName.lastIndexOf(" "));
            String lname = userName.substring(userName.lastIndexOf(" "), userName.length());
            //tvNameWlcm.setText("Welcome, "+fname);
        }
        //etOfficialEmail.setText(sharedPreference.getEmail());
        /*etPinCode.setText(sharedPreference.getPincode());
        etCity.setText(sharedPreference.getCity());
        etState.setText(sharedPreference.getState());*/


        String[] division = {"Male", "Female"};
        ArrayAdapter<CharSequence> divisionAdapter = new ArrayAdapter<CharSequence>(PersonalDetailsActivity.this, R.layout.spinner_text, division);
        divisionAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spGender.setAdapter(divisionAdapter);

        spGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                getGender = spGender.getSelectedItem().toString();
                sharedPreference.setGender(spGender.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sarCurrentloan = new String[]{"select", "0", "1", "2", "3", "4", "5"};
        currentLoanAdapter = new ArrayAdapter<String>(PersonalDetailsActivity.this, R.layout.spinner_text, sarCurrentloan);
        currentLoanAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spCurrentLoan.setAdapter(currentLoanAdapter);


        spCurrentLoan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String currentLoan = spCurrentLoan.getSelectedItem().toString();

                if(!currentLoan.equalsIgnoreCase("select"))
                {
                    if(currentLoan.equalsIgnoreCase("0"))
                    {

                        getCurrentLoan="13";

                    }
                    else if(currentLoan.equalsIgnoreCase("1"))
                    {
                        getCurrentLoan="14";
                        showEditDialog(1);

                    }
                    else if(currentLoan.equalsIgnoreCase("2"))
                    {
                        getCurrentLoan="15";
                        showEditDialog(1);

                    }
                    else
                    {
                        getHouseType = "2";
                        showEditDialog(1);
                    }
                }

                //Toast.makeText(PersonalDetailsActivity.this, getHouseType, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        spHouseType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                UserPointsBeans selectedItem = (UserPointsBeans) spHouseType.getSelectedItem();
                getHouseType = selectedItem.getInfo_id();
                Log.i("house_type","="+getHouseType);
                if(!spHouseType.getSelectedItem().toString().equalsIgnoreCase("select"))
                {
                    showEditDialog(2);
                }


                //Toast.makeText(PersonalDetailsActivity.this, selectedItem.getPoints(), Toast.LENGTH_SHORT).show();
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

                DatePickerDialog datePickerDialog = new DatePickerDialog(PersonalDetailsActivity.this, R.style.DatePickerDialogTheme,
                        new DatePickerDialog.OnDateSetListener() {
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

                        dob.setText(sDay + "-" + sMonth + "-" + year);

                        getDob = sDay + "-" + sMonth + "-" + year;
                        sharedPreference.setDob(getDob);
                        getAge(year, month + 1, dayOfMonth);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        btSubmitUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (allValidated()) {


                    if(etState.getText().toString().equalsIgnoreCase("Delhi") || etState.getText().toString().equalsIgnoreCase("Uttar Pradesh") || etState.getText().toString().equalsIgnoreCase("Haryana"))
                    {
                        Intent intent = new Intent(PersonalDetailsActivity.this, OpenFrontCamera.class);
                        intent.putExtra("firstName", etFirstName.getText().toString().trim());
                        intent.putExtra("lastName", etLastName.getText().toString().trim());
                        intent.putExtra("officialEmail", etOfficialEmail.getText().toString().trim());
                        intent.putExtra("gender", getGender);
                        intent.putExtra("dob", getDob);
                        intent.putExtra("currentLoan", getCurrentLoan);
                        intent.putExtra("houseType", getHouseType);
                        intent.putExtra("stayingYears", stayingYears);
                        intent.putExtra("etState", etState.getText().toString().trim());
                        intent.putExtra("etCity", etCity.getText().toString().trim());
                        intent.putExtra("etPinCode", etPinCode.getText().toString().trim());
                        intent.putExtra("etLocalAddress",  etLocalAddress.getText().toString().trim());
                        intent.putExtra("emi_amount",  loanAmount);
                        intent.putExtra("etReferenceName1", etReferenceName1.getText().toString().trim());
                        intent.putExtra("etReferencePhone1", etReferencePhone1.getText().toString().trim());
                        intent.putExtra("etReferenceName2",  etReferenceName2.getText().toString().trim());
                        intent.putExtra("etReferencePhone2",  etReferencePhone2.getText().toString().trim());
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        AreaDialog();
                    }


                }

            }
        });

        etPinCode.addTextChangedListener(new MyTextWatcher(etPinCode));
        etFirstName.addTextChangedListener(new MyTextWatcher(etFirstName));
        etLastName.addTextChangedListener(new MyTextWatcher(etLastName));
        etOfficialEmail.addTextChangedListener(new MyTextWatcher(etOfficialEmail));
        etCity.addTextChangedListener(new MyTextWatcher(etCity));
        etState.addTextChangedListener(new MyTextWatcher(etState));
        etLocalAddress.addTextChangedListener(new MyTextWatcher(etLocalAddress));

        spGender.setSelection(sharedPreference.getGender());


    }

    private  void getPhoneNoFromContacts() {



        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);

        //code


    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        //super.onActivityResult(reqCode, resultCode, data);
        // String cNumber = "";

        if(resultCode == RESULT_OK)
        {
            switch(reqCode)
            {
                case PICK_CONTACT:
                    contactPick(data);
                    break;
            }
        }
        else{
            Toast.makeText(this,"Failed to pick Contact",Toast.LENGTH_SHORT).show();
        }



        //return cNumber;
    }

    private void contactPick(Intent data) {

        Cursor cursor =null;
        try{

            Uri uri = data.getData();
            cursor = getContentResolver().query(uri,null,null,null,null);
            cursor.moveToFirst();
            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            phoneNo = cursor.getString(phoneIndex);
            // phoneNo = phoneNo.replaceFirst("(\\d{3})(\\d{3})(\\d+)", "($1) $2-$3");
            phoneNo = formatPhoneNo(phoneNo);
            Log.e("phonemofunc",phoneNo);

            if(flagPhoneNoPick == 1){
                etReferencePhone1.setText(phoneNo);
                etReferencePhone1.setSelection(etReferencePhone1.getText().length());
            }
            else if(flagPhoneNoPick == 2){
                etReferencePhone2.setText(phoneNo);
                etReferencePhone1.setSelection(etReferencePhone2.getText().length());
            }





        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private String formatPhoneNo(String s) {
        s = s.replaceAll("[^\\d.]", "");
        return  s;

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
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
                        Log.i("rr","------>");
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(PersonalDetailsActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i("rsr","------>");
                        break;

                    case  LocationSettingsStatusCodes.CANCELED:
                        Toast.makeText(PersonalDetailsActivity.this, "User Cancelled Location", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
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

    public void showDialogAge() {

        dialogAge = new Dialog(PersonalDetailsActivity.this);
        dialogAge.setCancelable(false);
        dialogAge.setContentView(R.layout.dialog_age);
        dialogAge.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        tvOkDialAge = dialogAge.findViewById(R.id.tvOkDialAge);

        tvOkDialAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAge.dismiss();
            }
        });

        dialogAge.show();
    }


    public void AreaDialog() {

        dialogAge = new Dialog(PersonalDetailsActivity.this);
        dialogAge.setCancelable(false);
        dialogAge.setContentView(R.layout.dialog_area);
        dialogAge.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        tvOkDialAge = dialogAge.findViewById(R.id.tvOkDialAge);


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




    public void showEditDialog(final int flag) {
        dialog = new Dialog(PersonalDetailsActivity.this);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_edit);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        etText = dialog.findViewById(R.id.etText);
        tvOk = dialog.findViewById(R.id.tvOk);
        tvCancel = dialog.findViewById(R.id.tvCancel);

        if (flag == 1) {
            etText.setHint("Enter EMI Amount");
            etText.setInputType(InputType.TYPE_CLASS_NUMBER);

        } else {
            etText.setHint("staying year(s)");
            etText.setInputType(InputType.TYPE_CLASS_NUMBER);

        }

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (new ConnectionCheck(PersonalDetailsActivity.this).isNetworkAvailable()) {
                    String sText = etText.getText().toString().trim();
                    if (flag == 1) {
                        if (!sText.isEmpty()) {
                            loanAmount = sText;
                            dialog.dismiss();
                            View softKey = getCurrentFocus();
                            if (softKey != null) {
                                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(softKey.getWindowToken(), 0);
                            }
                        } else
                            Toast.makeText(PersonalDetailsActivity.this, "Please enter your total EMI amount", Toast.LENGTH_SHORT).show();
                    } else if (flag == 2) {
                        if (!sText.isEmpty()) {
                            stayingYears = sText;
                            dialog.dismiss();
                            View softKey = getCurrentFocus();
                            if (softKey != null) {
                                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(softKey.getWindowToken(), 0);
                            }
                        } else
                            Toast.makeText(PersonalDetailsActivity.this, "Please enter number of years you stayed in house", Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(PersonalDetailsActivity.this, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();


            }
        });

        dialog.show();
    }

    private void spinnersOnClick() {

        if (sharedPreference.getCurrLoan()!=0){
            spCurrentLoan.setSelection(sharedPreference.getCurrLoan());
        }

        spCurrentLoan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (!spCurrentLoan.getSelectedItem().toString().equalsIgnoreCase("select")) {
                    Log.i("rsr","-->"+alCurrentLoan.size());
                    if (spCurrentLoan.getSelectedItem().toString().equals("0")) {
                        getCurrentLoan = "13";
                    } else if (spCurrentLoan.getSelectedItem().toString().equals("1") || spCurrentLoan.getSelectedItem().toString().equals("2")) {

                        getCurrentLoan = "14";

                    } else if (Integer.parseInt(spCurrentLoan.getSelectedItem().toString()) >= 3) {
                        getCurrentLoan = "15";
                    }
                    if (!spCurrentLoan.getSelectedItem().toString().equalsIgnoreCase("0")){
                        //lyLoanEmi.setVisibility(VISIBLE);
                        showEditDialog(1);
                    }/*else
                        lyLoanEmi.setVisibility(GONE);*/

                }
                else {
                    lyLoanEmi.setVisibility(GONE);
                }

                sharedPreference.setCurrLoan(spCurrentLoan.getSelectedItemPosition());


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        String sSelected;
        for (int i=0; i<alSpinnersData.size(); i++){
            if (String.valueOf(sharedPreference.getHouseType()).equalsIgnoreCase(alSpinnersData.get(i).getInfo_id())) {
                sSelected = alSpinnersData.get(i).getSub_category();
                spHouseType.setSelection(houseTypeAdapter.getPosition(sSelected));
            }
        }

    }

    private void getPoints() {
        final ProgressDialog progressBar;
        progressBar = new ProgressDialog(PersonalDetailsActivity.this, R.style.AppCompatProgressDialogStyle);
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
                    //Toast.makeText(PersonalDetailsActivity.this, jsonArray.toString(), Toast.LENGTH_SHORT).show();

                   /* for (int i = 0; i < jsonArray.length(); i++) {
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

                    houseTypeAdapter = new ArrayAdapter<String>(PersonalDetailsActivity.this, R.layout.spinner_text, alHouseTypeList);
                    houseTypeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
                    spHouseType.setAdapter(houseTypeAdapter);

                    spinnersOnClick();*/

                    houseTypeList = new ArrayList<UserPointsBeans>();
                    UserPointsBeans userPointsBeans1 = new UserPointsBeans("0","House Type","Select","0");
                    houseTypeList.add(userPointsBeans1);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.optJSONObject(i);

                        UserPointsBeans userPointsBeans = new UserPointsBeans(jsonObject.optString("info_id"),
                                jsonObject.optString("category"),
                                jsonObject.optString("sub_category"), jsonObject.optString("points"));
                        //pointsList.add(userPointsBeans);


                        if (jsonObject.optString("category").equalsIgnoreCase("House Type")) {
                            //pointsList = new ArrayList<UserPointsBeans>();
                            houseTypeList.add(userPointsBeans);

                        }


                    }

                    houseAdapter = new ArrayAdapter<UserPointsBeans>(getApplicationContext(), R.layout.spinner_text, houseTypeList);
                    houseAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
                    spHouseType.setAdapter(houseAdapter);



                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("TAG111", "--getUserPoints error-->>" + error.getMessage());
                progressBar.dismiss();
                Toast.makeText(PersonalDetailsActivity.this, "Internal Server Error", Toast.LENGTH_SHORT).show();
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
                        if(sState.equalsIgnoreCase("Delhi") || sState.equalsIgnoreCase("Uttar Pradesh") || sState.equalsIgnoreCase("Haryana"))
                        {
                            //Eligible for loan
                        }
                        else
                        {
                            AreaDialog();
                        }


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

        if (etFirstName.getText().toString().trim().isEmpty()) {
            Toast.makeText(PersonalDetailsActivity.this, "Please enter first name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etLastName.getText().toString().trim().isEmpty()) {
            Toast.makeText(PersonalDetailsActivity.this, "Please enter last name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!validateEmail(etOfficialEmail.getText().toString().trim()))
            return false;

        if (getDob == null) {
            Toast.makeText(PersonalDetailsActivity.this, "Please Select Date of Birth", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (spCurrentLoan.getSelectedItem().toString().equalsIgnoreCase("select")) {
            Toast.makeText(PersonalDetailsActivity.this, "Please select current loan", Toast.LENGTH_SHORT).show();
            return false;
        }


        if (spHouseType.getSelectedItem().toString().equalsIgnoreCase("select")) {
            Toast.makeText(PersonalDetailsActivity.this, "Please Select House Type", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etPinCode.getText().toString().trim().isEmpty()) {
            Toast.makeText(PersonalDetailsActivity.this, "Please enter pin code", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etCity.getText().toString().trim().isEmpty()) {
            Toast.makeText(PersonalDetailsActivity.this, "Please enter your city name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etState.getText().toString().trim().isEmpty()) {
            Toast.makeText(PersonalDetailsActivity.this, "Please enter your state name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etLocalAddress.getText().toString().trim().isEmpty()) {
            Toast.makeText(PersonalDetailsActivity.this, "Please enter your address", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (lyLoanEmi.getVisibility() == VISIBLE && etLoanEmi.getText().toString().trim().isEmpty()){
            Toast.makeText(PersonalDetailsActivity.this, "Please enter EMI amount", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etReferenceName1.getText().toString().trim().isEmpty()) {
            Toast.makeText(PersonalDetailsActivity.this, "Please enter your primary reference name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etReferencePhone1.getText().toString().trim().isEmpty()) {
            Toast.makeText(PersonalDetailsActivity.this, "Please enter your primary reference phone number", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etReferenceName2.getText().toString().trim().isEmpty()) {
            Toast.makeText(PersonalDetailsActivity.this, "Please enter your secondary reference name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etReferencePhone2.getText().toString().trim().isEmpty()){
            Toast.makeText(PersonalDetailsActivity.this, "Please enter your secondary reference phone number", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    ///for email validation
    public boolean validateEmail(String emaill) {

        if (!isValidEmail(emaill)) {
            setErrorInputLayout(etOfficialEmail, getString(R.string.email_not_valid));
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
                    if (etPinCode.getText().toString().trim().length()==6) {
                        sendDataForPincodeDetails(etPinCode.getText().toString().trim());
                        //sharedPreference.setPincode(etPinCode.getText().toString().trim());
                    }
                    break;

                case R.id.etFirstName:
                    String name  = etFirstName.getText().toString().trim()+" "+etLastName.getText().toString().trim();
                    sharedPreference.setUserName(name);
                    break;

                case R.id.etLastName:
                    sharedPreference.setUserName(etFirstName.getText().toString().trim()+" "+etLastName.getText().toString().trim());
                    break;

                case R.id.etOfficialEmail:
                    sharedPreference.setEmail(etOfficialEmail.getText().toString().trim());
                    break;

                case R.id.etCity:
                    sharedPreference.setCity(etCity.getText().toString().trim());
                    break;

                case R.id.etState:
                    sharedPreference.setState(etState.getText().toString().trim());
                    break;

                case R.id.etLocalAddress:
                    sharedPreference.setAddress(etLocalAddress.getText().toString().trim());
                    break;

            }
        }
    }
}