package com.loaneasy.user_details_new;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.loaneasy.network.ApiRequest;
import com.loaneasy.Beans.UserPointsBeans;
import com.loaneasy.EMandateBankDetails;
import com.loaneasy.R;
import com.loaneasy.Service.GPSTracker;
import com.loaneasy.utils.AndroidMultiPartEntity;
import com.loaneasy.utils.ConnectionCheck;
import com.loaneasy.utils.UserSharedPreference;
import com.loaneasy.utils.Utility;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.NormalFile;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import id.zelory.compressor.Compressor;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;


public class OfficialDetailsActivity extends AppCompatActivity {

    public final int RC_FILE_PICKER_PERM = 321;
    private final int CAMERA_REQUEST_CODE = 100;
    public final int nRESULT_LOAD_IMAGE_PHOTO = 3;
    public final int REQUEST_IMAGE_CAPTURE = 200;
    ArrayList<String> alFilePath ;

    private int MY_SOCKET_TIMEOUT_MS=5000;

    Pattern pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");
    String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};
    int PERMISSION_ALL = 1;

    private final String  TAG = "OfficialDetailsActivity";

    private AppCompatSpinner spEmpType, spSalary, spSalaryMode, spWorkingYearOrg,spAddressProof;
    private EditText etPanCard, etAadharCard, etCompanyName, etCompanyAddress, ettakeHomeSalary, hrPhone,hrEmail;
    private String getFirstName, getLastName, getEmail, getGender, getDob, getHouseType, getStayingYears,
            getState, getCity, getPinCode, getLocalAddress, getEmpType, getSalaryMode, getWorkingYears,
            getCurrentLoan, getUriSelfie, getEmi_amount,version,addressProofType,
            etReferenceName1, etReferencePhone1,etReferenceName2, etReferencePhone2;

    private ImageView ivAdharFront, ivAdharBack, ivPan, ivEmpID;
    private File fileAadharFront, fileAadharBack, filePanCard, fileEmployeeIdCard, fileUserProfilePic;
    int cameraAction;
    TypedFile tfAdharFront, tfAdharBack, tfPanCard, tfProfilePic, tfEmpId;

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    private Uri uriFilePath;
    private String selfieImagePath;
    private AppCompatCheckBox checkBox;
    Button btSubmitAllDetails;
    public TextView empTypeText, takeHomeSalary, salaryModeText, workingYearsText, currentLoanText, btSubmitUserInfo, tvAddressType;

    ArrayAdapter<String>  workingYearAdapter,addressTypeAdapter;
    ArrayList<UserPointsBeans> alWorkingYear;
    ArrayList<String> alEmpType, alSalaryMode;
    ProgressDialog progressBar;

    UserSharedPreference sharedPreference;
    ArrayList<UserPointsBeans> empTypeList, salaryModeList;
    ArrayAdapter<UserPointsBeans>  empTypeAdapter, salaryModeTypeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official_details);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        alFilePath = new ArrayList<>();
        
        if (savedInstanceState != null) {
            if (uriFilePath == null && savedInstanceState.getString("uri_file_path") != null) {
                uriFilePath = Uri.parse(savedInstanceState.getString("uri_file_path"));
            }
        }


        getPoints();

        if (!hasPermissions(this, PERMISSIONS)) {

            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
            
        try {
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
            Log.i("appVersion",version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        sharedPreference = new UserSharedPreference(this);
        Intent intent = getIntent();
        if (intent != null) {
            getFirstName = intent.getStringExtra("firstName");
            getLastName = intent.getStringExtra("lastName");
            getEmail = intent.getStringExtra("officialEmail");
            getGender = intent.getStringExtra("gender");
            getDob = intent.getStringExtra("dob");
            getCurrentLoan = intent.getStringExtra("currentLoan");
            getHouseType = intent.getStringExtra("houseType");
            getStayingYears = intent.getStringExtra("stayingYears");
            getState = intent.getStringExtra("etState");
            getCity = intent.getStringExtra("etCity");
            getPinCode = intent.getStringExtra("etPinCode");
            getLocalAddress = intent.getStringExtra("etLocalAddress");
            getUriSelfie = intent.getStringExtra("uriSelfie");
            //selfieImagePath = intent.getStringExtra("selfie_path");
            getEmi_amount = intent.getStringExtra("emi_amount");


            Log.i("getUriSelfie","="+getUriSelfie);

            etReferenceName1 = intent.getStringExtra("etReferenceName1");
            etReferencePhone1 = intent.getStringExtra("etReferencePhone1");
            etReferenceName2 = intent.getStringExtra("etReferenceName2");
            etReferencePhone2 = intent.getStringExtra("etReferencePhone2");


        }

        checkBox = findViewById(R.id.cbTermsCondition);
        btSubmitUserInfo = findViewById(R.id.btSubmitUserInfo);
        tvAddressType = findViewById(R.id.tvAddressType);
        empTypeText = findViewById(R.id.tvEmpTypeText);
        takeHomeSalary = findViewById(R.id.tvTakeHomeSalaryText);
        salaryModeText = findViewById(R.id.tvSalaryModeText);
        workingYearsText = findViewById(R.id.tvWorkingYearsText);
        currentLoanText = findViewById(R.id.tvCurrentLoanText);
        btSubmitAllDetails = (Button) findViewById(R.id.btSubmitAllDetails);
        spAddressProof = findViewById(R.id.spAddressProof);
        spEmpType = findViewById(R.id.spEmpType);
        spSalaryMode = findViewById(R.id.spSalaryMode);
        ettakeHomeSalary = findViewById(R.id.etTakeHomeSalary);
        //spSalary = view.findViewById(R.id.spSalary);
        spWorkingYearOrg = findViewById(R.id.spWorkingYearOrg);

        hrPhone = findViewById(R.id.etHrPhoneNumber);
        hrEmail = findViewById(R.id.etHrEmail);




        alWorkingYear = new ArrayList<>();
        alEmpType = new ArrayList<>();
        alSalaryMode = new ArrayList<>();

        alEmpType.add("Select");
        alSalaryMode.add("Select");

        etAadharCard = findViewById(R.id.etAadharCard);
        etPanCard = findViewById(R.id.etPanCard);
        etCompanyName = findViewById(R.id.etCompanyName);
        etCompanyAddress = findViewById(R.id.etCompanyAddress);

        ivAdharFront = findViewById(R.id.ivAdharFront);
        ivAdharBack = findViewById(R.id.ivAdharBack);
        ivPan = findViewById(R.id.ivPan);
        ivEmpID = findViewById(R.id.ivEmpID);

        etCompanyName.setText(sharedPreference.getCompanyName());
        etCompanyAddress.setText(sharedPreference.getCompanyAddr());
        ettakeHomeSalary.setText(sharedPreference.getSalary());
        etAadharCard.setText(sharedPreference.getAadhar());
        etPanCard.setText(sharedPreference.getPan());


       /* if (!sharedPreference.getEmpIdURI().isEmpty()) {
            ivEmpID.setImageBitmap(BitmapFactory.decodeFile(sharedPreference.getEmpIdURI()));
            tfEmpId = new TypedFile("multipart/form-data", new File(sharedPreference.getEmpIdURI()));

        }

        if (!sharedPreference.getAdharFrntURI().isEmpty()) {
            ivAdharFront.setImageBitmap(BitmapFactory.decodeFile(sharedPreference.getAdharFrntURI()));
            tfAdharFront = new TypedFile("multipart/form-data", new File(sharedPreference.getAdharFrntURI()));
        }

        if (!sharedPreference.getAdharBackURI().isEmpty()) {
            ivAdharBack.setImageBitmap(BitmapFactory.decodeFile(sharedPreference.getAdharBackURI()));
            tfAdharBack = new TypedFile("multipart/form-data", new File(sharedPreference.getAdharBackURI()));
        }

        if (!sharedPreference.getPanURI().isEmpty()) {
            ivPan.setImageBitmap(BitmapFactory.decodeFile(sharedPreference.getPanURI()));
            tfPanCard = new TypedFile("multipart/form-data", new File(sharedPreference.getPanURI()));
        }*/

        ivAdharFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    cameraAction = 1;
                    dialogForDocs();

            }
        });

        ivAdharBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    cameraAction = 2;
                    dialogForDocs();
            }
        });

        ivPan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    cameraAction = 3;
                    dialogForDocs();
            }
        });

        ivEmpID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    cameraAction = 4;
                    dialogForDocs();
            }
        });

        btSubmitUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(OfficialDetailsActivity.this, BankDetailsNewActivity.class));


                if (new ConnectionCheck(OfficialDetailsActivity.this).isNetworkAvailable()) {
                    if (!hasPermissions(OfficialDetailsActivity.this, PERMISSIONS)) {

                        ActivityCompat.requestPermissions(OfficialDetailsActivity.this, PERMISSIONS, PERMISSION_ALL);
                    } else {
                        if (allValidated())
                            //sendDataToSubmitDetails();
                            //new UploadFileToServer().execute();
                            //
                            makeJsonObjReq();


                    }
                    //startActivity(new Intent(OfficialDetailsActivity.this, BankDetailsNewActivity.class));
                } else {
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please Check Your Internet Connection", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });


        spEmpType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                UserPointsBeans selectedItem = (UserPointsBeans) spEmpType.getSelectedItem();
                String salaryMode = spEmpType.getSelectedItem().toString();
                if(!salaryMode.equalsIgnoreCase("select"))
                {
                    getEmpType = selectedItem.getInfo_id().toString();
                    //sharedPreference.setModeSalary(Integer.parseInt(getSalaryMode));
                }



            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        /*salaryModeAdapter = new ArrayAdapter<String>(OfficialDetailsActivity.this, R.layout.spinner_text, alSalaryMode);
        salaryModeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spSalaryMode.setAdapter(salaryModeAdapter);*/

        spSalaryMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                UserPointsBeans selectedItem = (UserPointsBeans) spSalaryMode.getSelectedItem();
                //getHouseType = selectedItem.getPoints();
                //Toast.makeText(OfficialDetailsActivity.this, selectedItem.getSub_category(), Toast.LENGTH_SHORT).show();
               /* if (!spSalaryMode.getSelectedItem().toString().equalsIgnoreCase("select")) {
                    for (int j = 0; j < PersonalDetailsActivity.alSpinnersData.size(); j++) {
                        if (PersonalDetailsActivity.alSpinnersData.get(j).getSub_category().equalsIgnoreCase(spSalaryMode.getSelectedItem().toString())) {
                            getSalaryMode = PersonalDetailsActivity.alSpinnersData.get(j).getInfo_id();
                            sharedPreference.setModeSalary(Integer.parseInt(getSalaryMode));
                        }
                    }
                }*/

                String salaryMode = spSalaryMode.getSelectedItem().toString();
                //Toast.makeText(OfficialDetailsActivity.this, salaryMode, Toast.LENGTH_SHORT).show();
                if(!salaryMode.equalsIgnoreCase("select"))
                {
                    getSalaryMode = selectedItem.getInfo_id().toString();
                    //sharedPreference.setModeSalary(Integer.parseInt(getSalaryMode));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        String[] sarWorkYears = new String[]{"Select", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        workingYearAdapter = new ArrayAdapter<String>(OfficialDetailsActivity.this, R.layout.spinner_text, sarWorkYears);
        workingYearAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spWorkingYearOrg.setAdapter(workingYearAdapter);
        spWorkingYearOrg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (!spWorkingYearOrg.getSelectedItem().toString().equalsIgnoreCase("select")) {
                    if (Integer.parseInt(spWorkingYearOrg.getSelectedItem().toString()) <= 2) {
                        getWorkingYears = "7";
                    } else if (Integer.parseInt(spWorkingYearOrg.getSelectedItem().toString()) >= 3 &&
                            Integer.parseInt(spWorkingYearOrg.getSelectedItem().toString()) <= 5) {
                        getWorkingYears = "8";
                    } else if (Integer.parseInt(spWorkingYearOrg.getSelectedItem().toString()) >= 6 &&
                            Integer.parseInt(spWorkingYearOrg.getSelectedItem().toString()) <= 10) {
                        getWorkingYears = "9";
                    }


                    //sharedPreference.setWorkYear(spWorkingYearOrg.getSelectedItemPosition());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        String[] arrAddressType= new String[]{"select", "Voter Id Card", "Aadhar Card", "Passport", "Driving License"};
        addressTypeAdapter = new ArrayAdapter<String>(OfficialDetailsActivity.this, R.layout.spinner_text, arrAddressType);
        addressTypeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spAddressProof.setAdapter(addressTypeAdapter);
        spAddressProof.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (!spAddressProof.getSelectedItem().toString().equalsIgnoreCase("select")) {

                    addressProofType= spAddressProof.getSelectedItem().toString();
                    etAadharCard.setHint("Please Enter "+addressProofType+" Number");
                    tvAddressType.setText("Upload photo of "+addressProofType);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




        etCompanyName.addTextChangedListener(new MyTextWatcher(etCompanyName));
        etCompanyAddress.addTextChangedListener(new MyTextWatcher(etCompanyAddress));
        ettakeHomeSalary.addTextChangedListener(new MyTextWatcher(ettakeHomeSalary));
        etAadharCard.addTextChangedListener(new MyTextWatcher(etAadharCard));
        etPanCard.addTextChangedListener(new MyTextWatcher(etPanCard));

        if (sharedPreference.getWorkYear()!=0){
            spWorkingYearOrg.setSelection(sharedPreference.getWorkYear());
        }

        /*for (int i=0; i<PersonalDetailsActivity.alSpinnersData.size(); i++){
            if (String.valueOf(sharedPreference.getEmpType()).equalsIgnoreCase(PersonalDetailsActivity.alSpinnersData.get(i).getInfo_id())) {
               String sSelected = PersonalDetailsActivity.alSpinnersData.get(i).getSub_category();
               spEmpType.setSelection(employeeTypeAdapter.getPosition(sSelected));
            } else if (String.valueOf(sharedPreference.getModeSalary()).equalsIgnoreCase(PersonalDetailsActivity.alSpinnersData.get(i).getInfo_id())) {
                String sSelected = PersonalDetailsActivity.alSpinnersData.get(i).getSub_category();
                spSalaryMode.setSelection(salaryModeAdapter.getPosition(sSelected));
            }
        }*/
    }




    private void capturePhoto(){
        PackageManager packageManager = getApplication().getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            File mainDirectory = new File(Environment.getExternalStorageDirectory(), "InstantMudra/tmp");
            if (!mainDirectory.exists())
            {
                mainDirectory.mkdirs();
            }

            Calendar calendar = Calendar.getInstance();
            File mediaFile = new File(mainDirectory.getPath() + File.separator
                    +"IMG_" +sharedPreference.getUserId()+"_"+ calendar.getTimeInMillis() + ".jpg");

            uriFilePath  = Uri.fromFile(mediaFile);
            //uriFilePath = Uri.fromFile(new File(mainDirectory, "IMG_" + calendar.getTimeInMillis()));

            //uriFilePath = FileProvider.getUriForFile(BankDetailsActivity.this, BuildConfig.APPLICATION_ID ,mediaFile);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFilePath);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);



        }
        else
        {
            Toast.makeText(this, "You device don,t have camera", Toast.LENGTH_SHORT).show();
        }
    }



    private void dialogForDocs() {
        LayoutInflater inflater = LayoutInflater.from(OfficialDetailsActivity.this);
        View dialogView = inflater.inflate(R.layout.dialog_for_docs, new LinearLayout(OfficialDetailsActivity.this), false);
        final AlertDialog dialog = new AlertDialog.Builder(OfficialDetailsActivity.this)
                .setView(dialogView)
                .create();

        DisplayMetrics metrics = new DisplayMetrics();
        int nWidth = metrics.widthPixels;
        int nHeight = metrics.heightPixels;
        dialog.getWindow().setLayout(nWidth, nHeight);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        final TextView tv_camera, tv_gallery, tv_pdf;

        tv_camera = dialog.findViewById(R.id.tv_camera);
        tv_gallery = dialog.findViewById(R.id.tv_gallery);
        tv_pdf = dialog.findViewById(R.id.tv_pdf);

        if (tv_camera != null) {
            tv_camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (ContextCompat.checkSelfPermission(OfficialDetailsActivity.this, Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_DENIED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            ActivityCompat.requestPermissions(OfficialDetailsActivity.this, new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                        }

                    }else {


                        //startCamera();
                        capturePhoto();
                        dialog.dismiss();
                    }


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

        if (tv_pdf != null) {
            tv_pdf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if(cameraAction == 4)
                    {
                        Toast.makeText(OfficialDetailsActivity.this, "PLease take a Picture or Upload from Gallery", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Intent intent= new Intent(OfficialDetailsActivity.this, NormalFilePickActivity.class);
                        intent.putExtra(Constant.MAX_NUMBER, 1);
                        intent.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"pdf"});
                        startActivityForResult(intent, Constant.REQUEST_CODE_PICK_FILE);
                        dialog.dismiss();
                    }

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

    public String getRealPathFromURI(Uri contentURI, AppCompatActivity context) {
        String[] projection = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = context.managedQuery(contentURI, projection, null,
                null, null);
        if (cursor == null)
            return null;
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        if (cursor.moveToFirst()) {
            String s = cursor.getString(column_index);
            // cursor.close();
            return s;
        }
        // cursor.close();
        return null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (uriFilePath != null)
            outState.putString("uri_file_path", uriFilePath.toString());
        super.onSaveInstanceState(outState);
    }

    private void getPoints() {
        final ProgressDialog progressBar;
        progressBar = new ProgressDialog(OfficialDetailsActivity.this, R.style.AppCompatProgressDialogStyle);
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

                //progressBar.dismiss();
                if(progressBar.isShowing())
                {
                    progressBar.dismiss();
                }
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

                    empTypeList = new ArrayList<UserPointsBeans>();
                    salaryModeList = new ArrayList<UserPointsBeans>();
                    UserPointsBeans userPointsBeans1 = new UserPointsBeans("0","Employment Type","Select","0");
                    empTypeList.add(userPointsBeans1);
                    salaryModeList.add(userPointsBeans1);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.optJSONObject(i);

                        UserPointsBeans userPointsBeans = new UserPointsBeans(jsonObject.optString("info_id"),
                                jsonObject.optString("category"),
                                jsonObject.optString("sub_category"), jsonObject.optString("points"));
                        //pointsList.add(userPointsBeans);


                        if (jsonObject.optString("category").equalsIgnoreCase("Employment Type")) {
                            //pointsList = new ArrayList<UserPointsBeans>();
                            empTypeList.add(userPointsBeans);

                        }
                        if (jsonObject.optString("category").equalsIgnoreCase("Salary Mode")) {
                            //pointsList = new ArrayList<UserPointsBeans>();
                            salaryModeList.add(userPointsBeans);

                        }
                   }

                    empTypeAdapter = new ArrayAdapter<UserPointsBeans>(getApplicationContext(), R.layout.spinner_text, empTypeList);
                    empTypeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
                    spEmpType.setAdapter(empTypeAdapter);

                    salaryModeTypeAdapter = new ArrayAdapter<UserPointsBeans>(getApplicationContext(), R.layout.spinner_text, salaryModeList);
                    salaryModeTypeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
                    spSalaryMode.setAdapter(salaryModeTypeAdapter);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("TAG111", "--getUserPoints error-->>" + error.getMessage());
                progressBar.dismiss();
                Toast.makeText(OfficialDetailsActivity.this, "Internal Server Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean hasPermissions(Context context, String[] permissions) {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new
                        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(OfficialDetailsActivity.this, "camera permission denied", Toast.LENGTH_LONG).show();
            }

        }

        // login_btn.setOnClickListener( null );
        // register_btn.setOnClickListener( null );
        if (permissions.length == 0) {
            return;
        }

        boolean allPermissionsGranted = true;
        if (grantResults.length > 0) {
            for (int grantResult : grantResults) {

                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
        }


        if (!allPermissionsGranted) {
            boolean somePermissionsForeverDenied = false;
            for (String permission : permissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    //denied
                    Toast.makeText(getApplicationContext(), "Please allow required permission", Toast.LENGTH_LONG).show();
                    Log.e("denied", permission);

                } else {
                    if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                        //allowed

                        Log.e("allowed", permission);


                    } else {
                        //set to never ask again

                        //Toast.makeText( getApplicationContext(),"set to never ask again",Toast.LENGTH_LONG ).show();
                        Log.e("set to never ask again", permission);
                        somePermissionsForeverDenied = true;
                    }
                }
            }


            if (somePermissionsForeverDenied) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Permissions Required")
                        .setMessage("You have forcefully denied some of the required permissions " +
                                "for this action. Please open settings, go to permissions and allow them.")
                        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.fromParts("package", getPackageName(), null));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // System.runFinalizersOnExit(true);
                                //android.os.Process.killProcess(android.os.Process.myPid());
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();

            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_CANCELED) {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            switch (cameraAction) {
                case 1:

                    if (requestCode == REQUEST_IMAGE_CAPTURE) {
                        //Bundle extras = data.getExtras();
                        if (resultCode == RESULT_OK) {


                            ivAdharFront.setImageURI(null);
                            ivAdharFront.setImageURI(uriFilePath);
                            File AadharFront = new File(uriFilePath.getPath());
                            try {
                                fileAadharFront = new Compressor(this)
                                        .setMaxWidth(640)
                                        .setMaxHeight(480)
                                        .setQuality(30)
                                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                                Environment.DIRECTORY_PICTURES).getAbsolutePath())
                                        .compressToFile(AadharFront);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(this, "Not able to capture Image", Toast.LENGTH_SHORT).show();
                        }
                    } else if (requestCode == nRESULT_LOAD_IMAGE_PHOTO) {

                        if (data != null) {


                            Uri uriEmpFront = data.getData();

                            ivAdharFront.setImageURI(null);
                            ivAdharFront.setImageURI(uriEmpFront);

                            Log.i("galleryUri", "==" + uriEmpFront);


                            String uriFromPath = getRealPathFromURI(uriEmpFront, OfficialDetailsActivity.this);
                            File AadharFront = new File(uriFromPath);
                            try {
                                fileAadharFront = new Compressor(this)
                                        .setMaxWidth(640)
                                        .setMaxHeight(480)
                                        .setQuality(30)
                                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                                Environment.DIRECTORY_PICTURES).getAbsolutePath())
                                        .compressToFile(AadharFront);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        }
                    } else {

                        if (data != null) {
                            ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                            if (list.size() != 0) {
                                String path = list.get(0).getPath();


                                fileAadharFront = new File(list.get(0).getPath());
                                Uri uri = Uri.fromFile(new File(path));
                                ivAdharFront.setImageResource(R.drawable.ic_pdf_svg);

                                MediaScannerConnection.scanFile(this,
                                        new String[]{fileAadharFront.getPath()}, null,
                                        new MediaScannerConnection.OnScanCompletedListener() {
                                            public void onScanCompleted(String path, Uri uri) {
                                                Log.i("onScanCompleted", "=" + uri);
                                            }
                                        });
                                Log.i("pdfFilePath", "==" + list.get(0).getPath());
                                Log.i("uri", "==" + uri);
                            }
                        }


                    }
                    break;


                case 2:
                    if (requestCode == REQUEST_IMAGE_CAPTURE) {
                        //Bundle extras = data.getExtras();
                        if (resultCode == RESULT_OK) {



                            ivAdharBack.setImageURI(null);
                            ivAdharBack.setImageURI(uriFilePath);

                            File AadharBack = new File(uriFilePath.getPath());
                            try {
                                fileAadharBack = new Compressor(this)
                                        .setMaxWidth(640)
                                        .setMaxHeight(480)
                                        .setQuality(30)
                                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                                Environment.DIRECTORY_PICTURES).getAbsolutePath())
                                        .compressToFile(AadharBack);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        } else {
                            Toast.makeText(this, "Not able to capture Image", Toast.LENGTH_SHORT).show();
                        }
                    } else if (requestCode == nRESULT_LOAD_IMAGE_PHOTO) {

                        if (data != null) {


                            Uri uriEmpFront = data.getData();
                            ivAdharBack.setImageURI(null);
                            ivAdharBack.setImageURI(uriEmpFront);

                            String uriFromPath = getRealPathFromURI(uriEmpFront, OfficialDetailsActivity.this);
                            File AadharBack = new File(uriFromPath);
                            try {
                                fileAadharBack = new Compressor(this)
                                        .setMaxWidth(640)
                                        .setMaxHeight(480)
                                        .setQuality(30)
                                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                                Environment.DIRECTORY_PICTURES).getAbsolutePath())
                                        .compressToFile(AadharBack);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        }
                    } else {

                        if (data != null) {
                            ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                            if (list.size() != 0) {
                                String path = list.get(0).getPath();
                                fileAadharBack = new File(list.get(0).getPath());
                                ivAdharBack.setImageResource(R.drawable.ic_pdf_svg);

                            }
                        }


                    }
                    break;
                case 3:
                    if (requestCode == REQUEST_IMAGE_CAPTURE) {
                        //Bundle extras = data.getExtras();
                        if (resultCode == RESULT_OK) {

                            ivPan.setImageURI(null);
                            ivPan.setImageURI(uriFilePath);
                            File PanCard = new File(uriFilePath.getPath());
                            try {
                                filePanCard = new Compressor(this)
                                        .setMaxWidth(640)
                                        .setMaxHeight(480)
                                        .setQuality(30)
                                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                                Environment.DIRECTORY_PICTURES).getAbsolutePath())
                                        .compressToFile(PanCard);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        } else {
                            Toast.makeText(this, "Not able to capture Image", Toast.LENGTH_SHORT).show();
                        }
                    } else if (requestCode == nRESULT_LOAD_IMAGE_PHOTO) {


                        if (data != null) {

                            Uri uriEmpFront = data.getData();
                            ivPan.setImageURI(null);
                            ivPan.setImageURI(uriEmpFront);
                            String uriFromPath = getRealPathFromURI(uriEmpFront, OfficialDetailsActivity.this);
                            File PanCard = new File(uriFromPath);
                            try {
                                filePanCard = new Compressor(this)
                                        .setMaxWidth(640)
                                        .setMaxHeight(480)
                                        .setQuality(30)
                                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                                Environment.DIRECTORY_PICTURES).getAbsolutePath())
                                        .compressToFile(PanCard);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        }
                    } else {

                        if (data != null) {
                            ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                            if (list.size() != 0) {
                                String path = list.get(0).getPath();
                                filePanCard = new File(list.get(0).getPath());
                                ivPan.setImageResource(R.drawable.ic_pdf_svg);

                            }
                        }


                    }
                    break;

                case 4:

                    if (requestCode == REQUEST_IMAGE_CAPTURE) {
                        //Bundle extras = data.getExtras();
                        if (resultCode == RESULT_OK) {

                            ivEmpID.setImageURI(null);
                            ivEmpID.setImageURI(uriFilePath);
                            File EmployeeCard = new File(uriFilePath.getPath());
                            try {
                                fileEmployeeIdCard = new Compressor(this)
                                        .setMaxWidth(640)
                                        .setMaxHeight(480)
                                        .setQuality(30)
                                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                                Environment.DIRECTORY_PICTURES).getAbsolutePath())
                                        .compressToFile(EmployeeCard);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(this, "Not able to capture Image", Toast.LENGTH_SHORT).show();
                        }
                    } else if (requestCode == nRESULT_LOAD_IMAGE_PHOTO) {
                       /* InputStream is;
                        Uri uriEmpFront;
                        is = null;*/

                        if (data != null) {


                            Uri uriEmpFront = data.getData();
                            ivEmpID.setImageURI(null);
                            ivEmpID.setImageURI(uriEmpFront);
                            String uriFromPath = getRealPathFromURI(uriEmpFront, OfficialDetailsActivity.this);
                            File EmpCard = new File(uriFromPath);
                            try {
                                fileEmployeeIdCard = new Compressor(this)
                                        .setMaxWidth(640)
                                        .setMaxHeight(480)
                                        .setQuality(30)
                                        .setCompressFormat(Bitmap.CompressFormat.PNG)
                                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                                Environment.DIRECTORY_PICTURES).getAbsolutePath())
                                        .compressToFile(EmpCard);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        if (alFilePath.size() > 0)
                            alFilePath.remove(0);
                        //alFilePath.addAll(0, data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                        String sFileNam = alFilePath.get(0).substring((alFilePath.get(0).lastIndexOf("/")) + 1, alFilePath.get(0).length());

                        fileEmployeeIdCard = new File(alFilePath.get(0));
                        ivEmpID.setImageResource(R.drawable.ic_pdf_svg);


                    }
                    break;
            }
        }
    }


    private boolean allValidated() {

        if (spEmpType.getSelectedItem().toString().equalsIgnoreCase("select")) {
            Toast.makeText(OfficialDetailsActivity.this, "Please select Employment type", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (ettakeHomeSalary.getText().toString().trim().isEmpty()) {
            Toast.makeText(OfficialDetailsActivity.this, "Please enter your salary", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Integer.parseInt(ettakeHomeSalary.getText().toString().trim()) < 25000) {
            Toast.makeText(OfficialDetailsActivity.this, "Salary must be greater than Rs.25,000", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (spSalaryMode.getSelectedItem().toString().equalsIgnoreCase("select")) {
            Toast.makeText(OfficialDetailsActivity.this, "Please select salary mode", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (spWorkingYearOrg.getSelectedItem().toString().equalsIgnoreCase("select")) {
            Toast.makeText(OfficialDetailsActivity.this, "Please select working years", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (fileEmployeeIdCard == null || !fileEmployeeIdCard.exists()) {
            Toast.makeText(OfficialDetailsActivity.this, "Please take a picture of your employment ID card", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (fileAadharFront == null || !fileAadharFront.exists()) {
            Toast.makeText(OfficialDetailsActivity.this, "Please take a picture of "+addressProofType+ "front", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (fileAadharBack == null || !fileAadharBack.exists()) {
            Toast.makeText(OfficialDetailsActivity.this, "Please take a picture of "+addressProofType+ " back", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (filePanCard == null || !filePanCard.exists()) {
            Toast.makeText(OfficialDetailsActivity.this, "Please take a picture of pan card", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etCompanyName.getText().toString().trim().isEmpty()) {
            Toast.makeText(OfficialDetailsActivity.this, "Please enter your company name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etCompanyAddress.getText().toString().trim().isEmpty()) {
            Toast.makeText(OfficialDetailsActivity.this, "Please enter your company address", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!validatePanCard(etPanCard.getText().toString().trim())){

            Toast.makeText(OfficialDetailsActivity.this, "Please enter Address Proof Number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(etAadharCard.getText().toString().isEmpty())
        {
            Toast.makeText(OfficialDetailsActivity.this, "Please enter your "+addressProofType+" number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (hrEmail.getText().toString().isEmpty()){
            Toast.makeText(OfficialDetailsActivity.this, "Please enter your company HR email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (hrPhone.getText().toString().isEmpty()){
            Toast.makeText(OfficialDetailsActivity.this, "Please enter your company HR phone number", Toast.LENGTH_SHORT).show();
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

                case R.id.etCompanyName:
                    sharedPreference.setCompanyName(etCompanyName.getText().toString().trim());
                    break;

                case R.id.etCompanyAddress:
                    sharedPreference.setCompanyAddr(etCompanyAddress.getText().toString().trim());
                    break;

                case R.id.etTakeHomeSalary:
                    sharedPreference.setSalary(ettakeHomeSalary.getText().toString().trim());
                    break;

                case R.id.etAadharCard:
                    sharedPreference.setAadhar(etAadharCard.getText().toString().trim());
                    break;

                case R.id.etPanCard:
                    sharedPreference.setPan(etPanCard.getText().toString().trim());
                    break;

            }
        }
    }




    private void makeJsonObjReq() {


        GPSTracker gps = new GPSTracker(getApplicationContext());
        double latitude = gps.getLatitude();
        double longitude= gps.getLongitude();


        RequestQueue queue = Volley.newRequestQueue(this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("user_id", sharedPreference.getUserId());
        postParam.put("first_name", getFirstName);
        postParam.put("last_name", getLastName);
        postParam.put("phone_no", sharedPreference.getUserPhoneNo());
        postParam.put("profile_completed", "1");
        postParam.put("gender", getGender);
        postParam.put("d_o_b", getDob);
        postParam.put("official_mail", getEmail);
        postParam.put("emp_type", getEmpType);
        postParam.put("take_home_salary", ettakeHomeSalary.getText().toString().trim());
        postParam.put("address_state", getState);
        postParam.put("address_city", getCity);
        postParam.put("local_address", getLocalAddress);
        postParam.put("pin_code",getPinCode);
        postParam.put("latitude", String.valueOf(latitude));
        postParam.put("longitude", String.valueOf(longitude));
        postParam.put("pan_card_no", etPanCard.getText().toString().trim());
        postParam.put("aadhar_card_no", etAadharCard.getText().toString());
        postParam.put("social_media_type", sharedPreference.getSocialMediaType());
        postParam.put("social_name", sharedPreference.getUserSocialName());
        postParam.put("social_id", sharedPreference.getSocialMediaId());
        postParam.put("social_email", sharedPreference.getUserEmail());
        postParam.put("social_profile_pic", sharedPreference.getProfilePic());
        postParam.put("marital_status", "N.A.");
        postParam.put("house_type", getHouseType);
        postParam.put("staying_years", getStayingYears);
        postParam.put("salary_mode", getSalaryMode);
        postParam.put("working_years", getWorkingYears);
        postParam.put("current_loan",getCurrentLoan);
        postParam.put("existing_loan", "N.A.");
        postParam.put("app_version", version);
        postParam.put("current_loan_emi", getEmi_amount);
        postParam.put("company_name", etCompanyName.getText().toString().trim());
        postParam.put("company_address",etCompanyAddress.getText().toString().trim());
        postParam.put("hr_email", hrEmail.getText().toString().trim());
        postParam.put("hr_phone", hrPhone.getText().toString().trim());
        postParam.put("user_area", sharedPreference.getUserArea());
        postParam.put("primary_reference_name",etReferenceName1);
        postParam.put("primary_reference_phone",etReferencePhone1);
        postParam.put("secondary_reference_name", etReferenceName2);
        postParam.put("secondary_reference_phone", etReferencePhone2);


        Log.i("Obj","="+postParam);


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Utility.BASE_URL+"/newUserDetails", new JSONObject(postParam), new com.android.volley.Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        //progressBar11.dismiss();
                        if(response.optBoolean("status"))
                        {
                            Log.i(TAG,"Inside if condition ");
                            sharedPreference.setUserEmail(getEmail);

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    new UploadFileToServer().execute();
                                }
                            }, 100);
                            //new UploadFileToServer().execute();
                        }
                        else
                        {
                            Toast.makeText(OfficialDetailsActivity.this, response.optString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Log.i("VolleyError","="+error);
                Log.i("VolleyError","="+error.getMessage());
                //progressBar11.dismiss();
                Toast.makeText(OfficialDetailsActivity.this, "Something went wrong, Please try after some time !", Toast.LENGTH_SHORT).show();
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



        // Cancelling request
    /* if (queue!= null) {
    queue.cancelAll(TAG);
    } */

    }


   /* @SuppressLint("StaticFieldLeak")*/
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {

        ProgressDialog progressBarAsn = new ProgressDialog(OfficialDetailsActivity.this, R.style.AppCompatProgressDialogStyle);
        long totalSize = 0;


        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBarAsn.setCancelable(false);
            progressBarAsn.setTitle("Uploading Documents...");
            progressBarAsn.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressBarAsn.show();
            progressBarAsn.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {

            // updating progress bar value
            progressBarAsn.setProgress(progress[0]);

        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Utility.BASE_URL+"/uploadUserFiles");

            try {

                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));

                            }
                        });

                Uri uriData = Uri.parse(getUriSelfie);


                File selfieFile = new File(uriData.getPath());

                Log.i("selfieFile","="+selfieFile.getPath());

                try {
                    selfieFile = new Compressor(getApplicationContext())
                            .setMaxWidth(640)
                            .setMaxHeight(480)
                            .setQuality(30)
                            .setCompressFormat(Bitmap.CompressFormat.JPEG)
                            .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES).getAbsolutePath())
                            .compressToFile(selfieFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                entity.addPart("user_id", new StringBody(sharedPreference.getUserId() ));
                //entity.addPart("profile_pic", new FileBody( new File(Uri.parse(getUriSelfie).getPath())));
                entity.addPart("profile_pic", new FileBody(selfieFile));
                entity.addPart("employer_id_card", new FileBody(fileEmployeeIdCard));
                entity.addPart("aadhar_front", new FileBody(fileAadharFront));
                entity.addPart("aadhar_back", new FileBody(fileAadharBack));
                entity.addPart("pan_card_photo", new FileBody(filePanCard));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);



                // Making server call
                // Log.i("rsr","-------->"+httppost.toString());
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                Log.i("rsr","------>"+statusCode);
                if (statusCode == 200) {
                    // Server response

                    if(progressBarAsn.isShowing())
                    {
                        progressBarAsn.dismiss();
                    }

                    responseString = EntityUtils.toString(r_entity);


                    Log.i("rsr","----->"+response);

                   /* JSONObject jsonObject = new JSONObject(responseString);
                    String profileCompleted = jsonObject.optString("profile_completed");
                    if (profileCompleted.equalsIgnoreCase("1")) {
                        sharedPreference.setSignFlag(3);
                        startActivity(new Intent(OfficialDetailsActivity.this, BankDetailsNewActivity.class));
                        finish();
                    }*/

                    JSONObject jsonObject = new JSONObject(responseString);
                    Log.i("Object","----->"+jsonObject);


                } else {

                    if(progressBarAsn.isShowing())
                    {
                        progressBarAsn.dismiss();
                    }
                    responseString = "Error occurred! Http Status Code: " + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            } catch (JSONException e){
                responseString = e.toString();
            }



            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("TAG", "Response from server: " + result);
            if(progressBarAsn.isShowing())
            {
                progressBarAsn.dismiss();
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;


            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(jsonObject != null)
            {
                if(jsonObject.optBoolean("status"))
                {
                    Toast.makeText(OfficialDetailsActivity.this, jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
                    JSONObject resultObject = jsonObject.optJSONObject("result");
                    if (resultObject.optString("profile_completed").equalsIgnoreCase("1")) {
                        sharedPreference.setSignFlag(3);

                        Intent openBankDetails = new Intent(getApplicationContext(), EMandateBankDetails.class);
                        openBankDetails.putExtra("updateBank", false);
                        startActivity(openBankDetails);
                    }
                }
                else
                {
                    Toast.makeText(OfficialDetailsActivity.this, jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
                }
            }



            super.onPostExecute(result);



        }

    }

    private void sendDataToSubmitDetails() {
        progressBar = new ProgressDialog(OfficialDetailsActivity.this, R.style.AppCompatProgressDialogStyle);
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

        tfProfilePic = new TypedFile("multipart/form-data", new File(Uri.parse(getUriSelfie).getPath()));
        GPSTracker gps = new GPSTracker(getApplicationContext());
        double latitude = gps.getLatitude();
        double longitude= gps.getLongitude();

        Log.d("TAG111", "UID--" + userId);
        Log.d("TAG111", "LOCATION--" + location);
        api.userDetails(userId, getFirstName, getLastName, phoneNo,
                getGender, getDob, getEmail, getEmpType, ettakeHomeSalary.getText().toString().trim(), getState,
                getCity, getLocalAddress, getPinCode, ""+latitude/*location.substring(0, location.indexOf(","))*/,
                ""+longitude/*location.substring(location.indexOf(",")+1, location.length())*/,
                etPanCard.getText().toString().trim(), etAadharCard.getText().toString().toLowerCase(), socialMediaType,
                socialName, socialMediaId, socialEmail, socialProfilePic, "", getHouseType, getStayingYears, getSalaryMode,
                getWorkingYears, getCurrentLoan, "", tfProfilePic, tfEmpId,
                tfAdharFront, tfAdharBack, tfPanCard, getEmi_amount, etCompanyName.getText().toString().trim(), etCompanyAddress.getText().toString().trim()
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
                                startActivity(new Intent(OfficialDetailsActivity.this, BankDetailsNewActivity.class));
                                finish();
                            }

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








}
