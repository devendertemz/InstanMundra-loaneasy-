package com.loaneasy.user_details;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;

import com.loaneasy.network.ApiRequest;
import com.loaneasy.Beans.UserPointsBeans;
import com.loaneasy.R;
import com.loaneasy.utils.UserSharedPreference;
import com.loaneasy.utils.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PersonalDetailsFragment extends Fragment {

    private AppCompatSpinner spGender, employmentType, salary, spCurrentLoan, spHouseType;
    TextView dob, skip, tvGenderText, tvDobText, tvMaritalStatusText, tvHouseTypeText, tvNameWlcm;
    TextView submitUserDetails;
    private EditText etFirstName, etLastName, etOfficialEmail, etState, etCity, etPinCode, etLocalAddress, etLoanEmi, panCardNo, aadharCardNo;
    private String getGender, getDob, getCurrentLoan, getHouseType;
    ScrollView scrollView;

    ArrayAdapter<String> currentLoanAdapter, houseTypeAdapter;
    public ArrayList<UserPointsBeans> alCurrentLoan;
    ArrayList<String> alHouseTypeList;
    UserSharedPreference sharedPreference;
    public static ArrayList<UserPointsBeans> alSpinnersData;
    String[] sarCurrentloan;
    LinearLayout lyLoanEmi;

    public PersonalDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for getActivity() fragment
        View view = inflater.inflate(R.layout.fragment_personal_details, container, false);

        sharedPreference = new UserSharedPreference(getActivity());
        alSpinnersData = new ArrayList<>();
        alCurrentLoan = new ArrayList<>();
        alHouseTypeList = new ArrayList<>();
        alHouseTypeList.add("Select");

        spGender = view.findViewById(R.id.spGender);
        spHouseType = view.findViewById(R.id.spHouseType);
        spCurrentLoan = view.findViewById(R.id.spCurrentLoan);

        etLoanEmi = view.findViewById(R.id.etLoanEmi);
        lyLoanEmi = view.findViewById(R.id.lyLoanEmi);
        tvNameWlcm = view.findViewById(R.id.tvNameWlcm);
        tvGenderText = view.findViewById(R.id.tvGenderText);
        tvDobText = view.findViewById(R.id.tvDobText);
        tvHouseTypeText = view.findViewById(R.id.tvHouseTypeText);
        getPoints();

        UserDetailsActivity activity = (UserDetailsActivity) getActivity();
        activity.getStatus(1);

        String userName = sharedPreference.getUserSocialName();
        String email = sharedPreference.getUserEmail();
        String fname = userName.substring(0, userName.lastIndexOf(" "));
        String lname = userName.substring(userName.lastIndexOf(" "), userName.length());

        tvNameWlcm.setText("Welcome, "+fname);

        etFirstName =  view.findViewById(R.id.etFirstName);
        etLastName =  view.findViewById(R.id.etLastName);
        etOfficialEmail =  view.findViewById(R.id.etOfficialEmail);
        etState =  view.findViewById(R.id.etState);
        etCity =  view.findViewById(R.id.etCity);
        etPinCode =  view.findViewById(R.id.etPinCode);
        etLocalAddress =  view.findViewById(R.id.etLocalAddress);
       /* etOfficialEmail.setText(email);
        etFirstName.setText(fname);
        etLastName.setText(lname);*/

        etPinCode.addTextChangedListener(new MyTextWatcher(etPinCode));


        submitUserDetails = view.findViewById(R.id.btSubmitUserInfo);

        String[] division = {"Male", "Female"};
        ArrayAdapter<CharSequence> divisionAdapter = new ArrayAdapter<CharSequence>(getActivity(), R.layout.spinner_text, division);
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

        sarCurrentloan = new String[]{"select", "0", "1", "2", "3", "4", "5"};
        currentLoanAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text, sarCurrentloan);
        currentLoanAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spCurrentLoan.setAdapter(currentLoanAdapter);

        dob = view.findViewById(R.id.tvDob);
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.DatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
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
                    /*UserDetailsActivity activity = (UserDetailsActivity) getActivity();
                    activity.startOfficialDetailFrag(etFirstName.getText().toString().trim(), etLastName.getText().toString().trim(),
                            etOfficialEmail.getText().toString().trim(), getGender, getDob, getCurrentLoan, getHouseType,
                            etState.getText().toString().trim(),etCity.getText().toString().trim(),
                            etPinCode.getText().toString().trim(), etLocalAddress.getText().toString().trim());*/


                    UserDetailsActivity activity = (UserDetailsActivity) getActivity();
                    activity.startFrontCam(etFirstName.getText().toString().trim(), etLastName.getText().toString().trim(),
                            etOfficialEmail.getText().toString().trim(), getGender, getDob, getCurrentLoan, getHouseType,
                            etState.getText().toString().trim(),etCity.getText().toString().trim(),
                            etPinCode.getText().toString().trim(), etLocalAddress.getText().toString().trim());

                }

            }
        });
        return view;
    }

    private void spinnersOnClick() {

        spCurrentLoan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (!spCurrentLoan.getSelectedItem().toString().equalsIgnoreCase("select")) {
                    Log.i("rsr","-->"+alCurrentLoan.size());
                    if (spCurrentLoan.getSelectedItem().toString().equals("0")) {
                        getCurrentLoan = alCurrentLoan.get(0).getInfo_id();
                    } else if (spCurrentLoan.getSelectedItem().toString().equals("1") || spCurrentLoan.getSelectedItem().toString().equals("2")) {

                        getCurrentLoan = alCurrentLoan.get(2).getInfo_id();

                    } else if (Integer.parseInt(spCurrentLoan.getSelectedItem().toString()) >= 3) {
                        getCurrentLoan = alCurrentLoan.get(2).getInfo_id();
                    }
                    if (!spCurrentLoan.getSelectedItem().toString().equalsIgnoreCase("0")){
                        lyLoanEmi.setVisibility(View.VISIBLE);
                    }else
                        lyLoanEmi.setVisibility(View.GONE);

                }
                else {
                    lyLoanEmi.setVisibility(View.GONE);
                }
                //Toast.makeText(getActivity(), spCurrentLoan.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();


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
    }

    private void getPoints() {
        final ProgressDialog progressBar;
        progressBar = new ProgressDialog(getActivity(), R.style.AppCompatProgressDialogStyle);
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
                    //Toast.makeText(getActivity(), jsonArray.toString(), Toast.LENGTH_SHORT).show();

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

                    houseTypeAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text, alHouseTypeList);
                    houseTypeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
                    spHouseType.setAdapter(houseTypeAdapter);

                    spinnersOnClick();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("TAG111", "--getUserPoints error-->>" + error.getMessage());
                progressBar.dismiss();
                Toast.makeText(getActivity(), "Internal Server Error", Toast.LENGTH_SHORT).show();
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

        if (!validateEmail(etOfficialEmail.getText().toString().trim()))
            return false;

        if (etFirstName.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), "Please enter first name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etLastName.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), "Please enter last name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (getDob == null) {
            Toast.makeText(getActivity(), "Please Select Date of Birth", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (spCurrentLoan.getSelectedItem().toString().equalsIgnoreCase("select")) {
            Toast.makeText(getActivity(), "Please select current loan", Toast.LENGTH_SHORT).show();
            return false;
        }


        if (spHouseType.getSelectedItem().toString().equalsIgnoreCase("select")) {
            Toast.makeText(getActivity(), "Please Select House Type", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etPinCode.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), "Please enter pin code", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etCity.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), "Please enter your city name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etState.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), "Please enter your state name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etLocalAddress.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), "Please enter your address", Toast.LENGTH_SHORT).show();
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
                    if (etPinCode.getText().toString().trim().length()==6)
                        sendDataForPincodeDetails(etPinCode.getText().toString().trim());
                    break;
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
