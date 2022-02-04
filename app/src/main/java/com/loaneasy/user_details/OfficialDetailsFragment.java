package com.loaneasy.user_details;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.loaneasy.Beans.UserPointsBeans;
import com.loaneasy.R;
import com.loaneasy.utils.ConnectionCheck;
import com.loaneasy.utils.UserSharedPreference;

import org.json.JSONArray;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
public class OfficialDetailsFragment extends Fragment {

    Pattern pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");

    private AppCompatSpinner spEmpType, spSalary, spSalaryMode, spWorkingYearOrg;
    private EditText etPanCard, etAadharCard, etCompanyName, etCompanyAddress, ettakeHomeSalary;
    private String getFirstName, getLastName, getEmail, getGender, getDob, getMaritalStatus, getHouseType,
            getState, getCity, getPinCode, getLocalAddress, getEmpType, getSalary, getSalaryMode, getWorkingYears,
            getCurrentLoan, getUriSelfie;

    private ImageView ivAdharFront, ivAdharBack, ivPan;
    int cameraAction;

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    private AppCompatCheckBox checkBox;
    JSONArray data = null;
    Button btSubmitAllDetails;
    public TextView empTypeText, takeHomeSalary, salaryModeText, workingYearsText, currentLoanText, existingLoanTex;

    ArrayAdapter<String> employeeTypeAdapter, salaryModeAdapter, workingYearAdapter;
    ArrayList<UserPointsBeans> alWorkingYear;
    ArrayList<String> alEmpType, alSalaryMode;
    ProgressDialog progressBar;

    UserSharedPreference sharedPreference;

    public OfficialDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for getActivity() fragment
        final View view = inflater.inflate(R.layout.fragment_official_details, container, false);

        UserDetailsActivity activity = (UserDetailsActivity) getActivity();
        activity.getStatus(2);

        sharedPreference = new UserSharedPreference(getActivity());
        Bundle bundle = getArguments();
        if (bundle!=null) {
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
            getUriSelfie = bundle.getString("uriSelfie");
        }
        checkBox = view.findViewById(R.id.cbTermsCondition);

        empTypeText = view.findViewById(R.id.tvEmpTypeText);
        takeHomeSalary = view.findViewById(R.id.tvTakeHomeSalaryText);
        salaryModeText = view.findViewById(R.id.tvSalaryModeText);
        workingYearsText = view.findViewById(R.id.tvWorkingYearsText);
        currentLoanText = view.findViewById(R.id.tvCurrentLoanText);
        btSubmitAllDetails = view.findViewById(R.id.btSubmitAllDetails);

        spEmpType = view.findViewById(R.id.spEmpType);
        spSalaryMode = view.findViewById(R.id.spSalaryMode);
        ettakeHomeSalary = view.findViewById(R.id.etTakeHomeSalary);
        //spSalary = view.findViewById(R.id.spSalary);
        spWorkingYearOrg = view.findViewById(R.id.spWorkingYearOrg);

        alWorkingYear = new ArrayList<UserPointsBeans>();
        alEmpType = new ArrayList<>();
        alSalaryMode = new ArrayList<>();

        alEmpType.add("Select");
        alSalaryMode.add("Select");

        etAadharCard = view.findViewById(R.id.etAadharCard);
        etPanCard = view.findViewById(R.id.etPanCard);
        etCompanyName = view.findViewById(R.id.etCompanyName);
        etCompanyAddress = view.findViewById(R.id.etCompanyAddress);

        ivAdharFront = view.findViewById(R.id.ivAdharFront);
        ivAdharBack = view.findViewById(R.id.ivAdharBack);
        ivPan = view.findViewById(R.id.ivPan);
        ivAdharFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraAction = 1;
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });

        ivAdharBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraAction = 2;
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });

        ivPan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraAction = 3;
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });


        btSubmitAllDetails.setEnabled(false);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked) {
                    btSubmitAllDetails.setEnabled(true);
                } else
                    btSubmitAllDetails.setEnabled(false);
            }
        });

        //////////////
        for (int j = 0; j < PersonalDetailsFragment.alSpinnersData.size(); j++) {
            if (PersonalDetailsFragment.alSpinnersData.get(j).getCategory().equalsIgnoreCase("Employment Type")) {
                alEmpType.add(PersonalDetailsFragment.alSpinnersData.get(j).getSub_category());
            }

            if (PersonalDetailsFragment.alSpinnersData.get(j).getCategory().equalsIgnoreCase("Salary Mode")) {
                alSalaryMode.add(PersonalDetailsFragment.alSpinnersData.get(j).getSub_category());
            }

            if (PersonalDetailsFragment.alSpinnersData.get(j).getCategory().equalsIgnoreCase("Working Years")) {
                alWorkingYear.add(PersonalDetailsFragment.alSpinnersData.get(j));
            }
        }

        employeeTypeAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text, alEmpType);
        employeeTypeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spEmpType.setAdapter(employeeTypeAdapter);

        spEmpType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (!spEmpType.getSelectedItem().toString().equalsIgnoreCase("select")) {
                    for (int j = 0; j < PersonalDetailsFragment.alSpinnersData.size(); j++) {
                        if (PersonalDetailsFragment.alSpinnersData.get(j).getSub_category().equalsIgnoreCase(spEmpType.getSelectedItem().toString())) {
                            getEmpType = PersonalDetailsFragment.alSpinnersData.get(j).getInfo_id();
                        }
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        salaryModeAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text, alSalaryMode);
        salaryModeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spSalaryMode.setAdapter(salaryModeAdapter);

        spSalaryMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (!spSalaryMode.getSelectedItem().toString().equalsIgnoreCase("select")) {
                    for (int j = 0; j < PersonalDetailsFragment.alSpinnersData.size(); j++) {
                        if (PersonalDetailsFragment.alSpinnersData.get(j).getSub_category().equalsIgnoreCase(spSalaryMode.getSelectedItem().toString())) {
                            getSalaryMode = PersonalDetailsFragment.alSpinnersData.get(j).getInfo_id();
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        String[] sarWorkYears = new String[]{"Select", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        workingYearAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text, sarWorkYears);
        workingYearAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spWorkingYearOrg.setAdapter(workingYearAdapter);
        spWorkingYearOrg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (!spWorkingYearOrg.getSelectedItem().toString().equalsIgnoreCase("select")) {
                    if (Integer.parseInt(spWorkingYearOrg.getSelectedItem().toString()) <= 2) {
                        getWorkingYears = alWorkingYear.get(0).getInfo_id();
                    } else if (Integer.parseInt(spWorkingYearOrg.getSelectedItem().toString()) >= 3 &&
                            Integer.parseInt(spWorkingYearOrg.getSelectedItem().toString()) <= 5) {
                        getWorkingYears = alWorkingYear.get(1).getInfo_id();
                    } else if (Integer.parseInt(spWorkingYearOrg.getSelectedItem().toString()) >= 6 &&
                            Integer.parseInt(spWorkingYearOrg.getSelectedItem().toString()) <= 10) {
                        getWorkingYears = alWorkingYear.get(2).getInfo_id();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ////////////////


       /* String[] takeHomeSalary = {"30000 - 50000", "51000 - 70000", "71000 - above"};
        final ArrayAdapter<CharSequence> salaryAdapter = new ArrayAdapter<CharSequence>(getActivity(), R.layout.spinner_text, takeHomeSalary);
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
        });*/

        btSubmitAllDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new ConnectionCheck(getActivity()).isNetworkAvailable()) {
                    if (allValidated()){}
                       // sendDataToSubmitDetails();
                } else {
                    Snackbar snackbar = Snackbar.make(view.findViewById(android.R.id.content), "Please Check Your Internet Connection", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
        return view;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new
                        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(getActivity(), "camera permission denied", Toast.LENGTH_LONG).show();
            }

        }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {

            switch (cameraAction) {
                case 1: Bitmap photoFront = (Bitmap) data.getExtras().get("data");
                    ivAdharFront.setImageBitmap(photoFront);
                    break;
                case 2: Bitmap photoBack = (Bitmap) data.getExtras().get("data");
                    ivAdharBack.setImageBitmap(photoBack);
                    break;
                case 3: Bitmap photoPan = (Bitmap) data.getExtras().get("data");
                    ivPan.setImageBitmap(photoPan);
                    break;
            }
        }
    }

    private boolean allValidated() {

        if (etCompanyName.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), "Please enter your company name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etCompanyAddress.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(), "Please enter your company address", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!validatePanCard(etPanCard.getText().toString().trim()))
            return false;

        if (etAadharCard.getText().toString().trim().isEmpty() || etAadharCard.getText().toString().trim().length() < 12) {
            Toast.makeText(getActivity(), "Please enter aadhar card number", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (spEmpType.getSelectedItem().toString().equalsIgnoreCase("select")) {
            Toast.makeText(getActivity(), "Please select Employment type", Toast.LENGTH_SHORT).show();
            return false;
        }

       /* if (spSalaryMode.getSelectedItem().toString().equalsIgnoreCase("select")) {
            Toast.makeText(getActivity(), "Please select salary mode", Toast.LENGTH_SHORT).show();
            return false;
        }*/

        if (spWorkingYearOrg.getSelectedItem().toString().equalsIgnoreCase("select")) {
            Toast.makeText(getActivity(), "Please select working years", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

   /* private FragmentManager.OnBackStackChangedListener getListener() {
        FragmentManager.OnBackStackChangedListener result = new FragmentManager.OnBackStackChangedListener() {
            public void onBackStackChanged() {
                FragmentManager manager = getFragmentManager();

                if (manager != null) {
                    if (manager.getBackStackEntryCount() >= 1) {

                    }
                }
            }
        };

        return result;
    }*/

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


    /*private void sendDataToSubmitDetails() {
        progressBar = new ProgressDialog(getActivity(), R.style.AppCompatProgressDialogStyle);
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
        sharedPreference.setUserCity(getCity);

        Log.d("TAG111", "UID--" + userId);
        Log.d("TAG111", "LOCATION--" + location);
        api.userDetails(userId, getFirstName, getLastName, phoneNo,
                getGender, getDob, getEmail, getEmpType, getSalary, getState,
                getCity, getLocalAddress, getPinCode, ""*//*location.substring(0, location.indexOf(","))*//*, ""*//*location.substring(location.indexOf(",")+1, location.length())*//*,
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
                            if (profileCompleted.equals("1")) {
                                sharedPreference.setSignFlag(3);
                                UserDetailsActivity activity = (UserDetailsActivity) getActivity();
                                activity.showBankDetailsFrag();
                            }

                           *//* startActivity(new Intent(getActivity(), HomeActivity.class).putExtra("city", getCity));
                            finish();*//*

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.i("failure", "---->>" + error.getMessage());
                        progressBar.dismiss();
                        Toast.makeText(getActivity(), "Internal Server Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }*/

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
