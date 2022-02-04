package com.loaneasy.DrawerItems;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.snackbar.Snackbar;
import com.loaneasy.network.ApiRequest;
import com.loaneasy.Beans.UserPointsBeans;
import com.loaneasy.R;
import com.loaneasy.utils.ConnectionCheck;
import com.loaneasy.utils.UserSharedPreference;
import com.loaneasy.utils.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ProfileActivity extends AppCompatActivity {

    public final int nRESULT_LOAD_IMAGE_PHOTO = 3;
    public final int REQUEST_IMAGE_CAPTURE = 200;
    public final int RequestPermissionCode = 1;
    EditText etPinCode, etCity, etState, etLocalAddress, etText;
    Button btSubmit;
    ImageView ivBack;
    UserSharedPreference sharedPreference;
    private CardView frameNoProfile;
    ScrollView frameProfile;
    TextView tvDOB, tvName, tvMobile, tvEmail, tvOk, tvCancel;
    public ArrayList<UserPointsBeans> alSpinnersData, alCurrentLoan, alWorkingYear;
    ArrayList<String> alHouseTypeList, alEmpType, alSalaryMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreference = new UserSharedPreference(this);
        setContentView(R.layout.activity_profile);

        tvName = (TextView) findViewById(R.id.tvName);
        tvDOB =(TextView) findViewById(R.id.tvDOB);
        tvEmail =(TextView) findViewById(R.id.tvEmail);
        tvMobile =(TextView) findViewById(R.id.tvMobile);
        etPinCode = findViewById(R.id.etProfilePinCode);
        etCity = findViewById(R.id.etCity);
        etState = findViewById(R.id.etState);
        etLocalAddress = findViewById(R.id.etLocalAddress);
        btSubmit = findViewById(R.id.btSubmit);
        ivBack = findViewById(R.id.ivBack);
        frameNoProfile = findViewById(R.id.frameNoProfile);
        frameProfile = findViewById(R.id.frameProfile);

        alSpinnersData = new ArrayList<>();
        alCurrentLoan = new ArrayList<>();
        alWorkingYear = new ArrayList<>();
        alEmpType = new ArrayList<>();
        alSalaryMode = new ArrayList<>();
        alHouseTypeList = new ArrayList<>();
        alHouseTypeList = new ArrayList<>();
        alHouseTypeList.add("Select");
        alEmpType.add("Select");
        alSalaryMode.add("Select");

        etPinCode.clearFocus();
        if (new ConnectionCheck(ProfileActivity.this).isNetworkAvailable()) {
            sendDataGetUserDetails(sharedPreference.getUserId());
        } else {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please Check Your Internet Connection", Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog(1);
            }
        });

        tvEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog(2);
            }
        });


        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (new ConnectionCheck(ProfileActivity.this).isNetworkAvailable()) {
                    String empType = "";
                    String takeHomeSal = "";
                    String pinCode = etPinCode.getText().toString().trim();
                    String city = etCity.getText().toString().trim();
                    String state = etState.getText().toString().trim();
                    String localAddress = etLocalAddress.getText().toString().trim();
                    if (allValidated()) {

                        sendDataUpdateUserDetails("", "", "", tvEmail.getText().toString(), "", "", state, city,
                                localAddress, "", "", pinCode, "", "", "", "");
                    }
                } else {
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please Check Your Internet Connection", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });


        etPinCode.addTextChangedListener(new MyTextWatcher(etPinCode));
    }

    public void showEditDialog(final int flag) {
        final Dialog dialog = new Dialog(ProfileActivity.this);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_edit);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        etText = dialog.findViewById(R.id.etText);
        tvOk = dialog.findViewById(R.id.tvOk);
        tvCancel = dialog.findViewById(R.id.tvCancel);

        if (flag == 1) {
            etText.setHint("Enter phone no");
            etText.setInputType(InputType.TYPE_CLASS_NUMBER);
            etText.setFilters(new InputFilter[]{
                    new InputFilter.LengthFilter(10)
            });
        } else {
            etText.setHint("Enter email");
            etText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            etText.setFilters(new InputFilter[]{
                    new InputFilter.LengthFilter(30)
            });
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
                if (new ConnectionCheck(ProfileActivity.this).isNetworkAvailable()) {
                    String sText = etText.getText().toString().trim();
                    if (flag == 1) {
                        if (sText.length() == 10) {
                            tvMobile.setText(sText);
                            dialog.dismiss();

                        } else
                            Toast.makeText(ProfileActivity.this, "Please enter correct mobile number", Toast.LENGTH_SHORT).show();
                    } else if (flag == 2) {
                        if (validateEmail(sText)) {
                            tvEmail.setText(sText);
                            dialog.dismiss();

                        }
                    }
                } else
                    Toast.makeText(ProfileActivity.this, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();


            }
        });

        dialog.show();
    }

    private boolean allValidated() {

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


    public boolean validateEmail(String emaill) {

        if (!isValidEmail(emaill)) {
            setErrorInputLayout(etText, getString(R.string.email_not_valid));
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


    private void dialogForCamera() {
        LayoutInflater inflater = LayoutInflater.from(ProfileActivity.this);
        View dialogView = inflater.inflate(R.layout.dialog_for_camera, new LinearLayout(ProfileActivity.this), false);
        final AlertDialog dialog = new AlertDialog.Builder(ProfileActivity.this)
                .setView(dialogView)
                .create();

        DisplayMetrics metrics = new DisplayMetrics();
        int nWidth = metrics.widthPixels;
        int nHeight = metrics.heightPixels;
        dialog.getWindow().setLayout(nWidth, nHeight);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        TextView tv_camera, tv_gallery;

        tv_camera = dialog.findViewById(R.id.tv_camera);
        tv_gallery = dialog.findViewById(R.id.tv_gallery);

        if (tv_camera != null) {
            tv_camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startCamera();
                    dialog.dismiss();
                }
            });
        }

        if (tv_gallery != null) {
            tv_gallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    openGallery(nRESULT_LOAD_IMAGE_PHOTO);
                    dialog.dismiss();
                }
            });
        }
    }

    private void startCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void openGallery(int nDifPhoto) {

        if (nDifPhoto == 3) {
            Intent mIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(mIntent, nRESULT_LOAD_IMAGE_PHOTO);
        }
    }


    private void getPoints() {

        RestAdapter adapter = new RestAdapter.Builder().setEndpoint(Utility.BASE_URL).build();
        ApiRequest api = adapter.create(ApiRequest.class);

        api.getUserPoints(new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                BufferedReader reader = null;
                String output = "";

                try {

                    //Initializing buffered reader
                    reader = new BufferedReader(new InputStreamReader(response.getBody().in()));

                    //Reading the output in the string
                    output = reader.readLine();

                    JSONArray jsonArray = new JSONArray(output);
                    Log.i("TAG111", "----getUserPoints------->" + jsonArray);
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

                        if (alSpinnersData.get(j).getCategory().equalsIgnoreCase("Employment Type")) {
                            alEmpType.add(alSpinnersData.get(j).getSub_category());
                        }

                        if (alSpinnersData.get(j).getCategory().equalsIgnoreCase("Salary Mode")) {
                            alSalaryMode.add(alSpinnersData.get(j).getSub_category());
                        }

                        if (alSpinnersData.get(j).getCategory().equalsIgnoreCase("Working Years")) {
                            alWorkingYear.add(alSpinnersData.get(j));
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("TAG111", "--getUserPoints error-->>" + error.getMessage());
                Toast.makeText(getApplicationContext(), "Internal Server Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendDataGetUserDetails(String userId) {

        final ProgressDialog progressBar;
        progressBar = new ProgressDialog(ProfileActivity.this, R.style.AppCompatProgressDialogStyle);
        progressBar.setCancelable(false);
        progressBar.setMessage("Please Wait...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();

        RestAdapter adapter = new RestAdapter.Builder().setEndpoint(Utility.BASE_URL).setLogLevel(RestAdapter.LogLevel.FULL).build();
        ApiRequest api = adapter.create(ApiRequest.class);

        api.getUserDetails(userId, new Callback<Response>() {
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
                    Log.d("TAG111", "--getUserDetails_Response-->" + output);

                    if (!output.contains("not found")) {
                        //frameProfile.setVisibility(View.VISIBLE);
                        JSONObject object = new JSONObject(output);
                        tvName.setText(object.optString("first_name") + " " + object.optString("last_name"));
                        tvEmail.setText(object.optString("official_mail"));
                        etPinCode.setText(object.optString("pin_code"));
                        etCity.setText(object.optString("address_city"));
                        etState.setText(object.optString("address_state"));
                        etLocalAddress.setText(object.optString("local_address"));
                        tvDOB.setText(object.optString("d_o_b"));
                        tvMobile.setText(object.optString("phone_no"));
                        // spEmpType.setSelection(accTypeAdapter.getPosition(object.optString("emp_type")));


                    } else if (output.contains("not found")) {
                        //frameNoProfile.setVisibility(View.VISIBLE);
                    /*    tvCreateProfile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(ProfileActivity.this, UserInformation.class));
                                finish();
                            }
                        });*/
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("failure", "---->>" + error.getMessage());
                progressBar.dismiss();
                Toast.makeText(getApplicationContext(), "Internal Server Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    ///for update

    private void sendDataUpdateUserDetails(String profileUrl, final String fname, final String lname, String officialEmail, String empType, String takeHomeSal, String state,
                                           final String city, String localAddress, String companyName, String companyAddress, String pinCode,
                                           String salaryMode, String workingYears, String currentLoan, String houseType) {

        final ProgressDialog progressBar;
        progressBar = new ProgressDialog(ProfileActivity.this, R.style.AppCompatProgressDialogStyle);
        progressBar.setCancelable(false);
        progressBar.setMessage("Please Wait...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();

        RestAdapter adapter = new RestAdapter.Builder().setEndpoint(Utility.BASE_URL).setLogLevel(RestAdapter.LogLevel.FULL).build();
        ApiRequest api = adapter.create(ApiRequest.class);


        api.updateUserProfile(sharedPreference.getUserId(), profileUrl, fname, lname, officialEmail, empType, takeHomeSal,
                state, city, localAddress, companyName, companyAddress, pinCode, salaryMode, workingYears, currentLoan,
                houseType, new Callback<Response>() {
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

                            sharedPreference.setUserSocialName(fname + " " + lname);
                            sharedPreference.setUserCity(city);
                            Log.d("TAG111", "---updateUserProfileNoPic response---->" + output);
                            Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.i("TAG111", "--updateUserProfileNoPic Error-->>" + error.getMessage());
                        progressBar.dismiss();
                        if (error.getMessage().equalsIgnoreCase("timeout"))
                            Toast.makeText(getApplicationContext(), "Connection timeout, please try again", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getApplicationContext(), "Internal Server Error", Toast.LENGTH_SHORT).show();
                    }
                });
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
                    if (object.getString("Status").equalsIgnoreCase("success")) {
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
                Log.i("TAG111", "--pincodeerror-->>" + error.getMessage());
                Toast.makeText(getApplicationContext(), "Internal Server Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

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

                case R.id.etProfilePinCode:
                    if (etPinCode.getText().toString().trim().length() == 6)
                        sendDataForPincodeDetails(etPinCode.getText().toString().trim());
                    break;
            }
        }
    }
}
