package com.loaneasy;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import id.zelory.compressor.Compressor;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;


public class BankDetailsActivity extends AppCompatActivity {

    public final int RC_FILE_PICKER_PERM = 321;
    private final int CAMERA_REQUEST_CODE = 100;
    public final int nRESULT_LOAD_IMAGE_PHOTO = 3;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public final int REQUEST_IMAGE_CAPTURE = 200;
    //AppCompatSpinner spAccType, spBankName;
    private TextView tvUploadStatmnt, tvUploadSlip1, tvUploadSlip2, tvUploadSlip3, tv_toolbar_title, tvTotalAmount, tvHandlingCharge, tvTotalInterest, tvLoanAmount;
    EditText etBankName, bankStatementPin;
    private Button btSubmitDetails, submitLater;
    View viewBanknameline;
    String getAccType, getBankName;
    private int nFlagDoc;
    ArrayList<String> alFilePath = new ArrayList<>();
    TypedFile typedFileBankStatmnt, typedFileSalarySlip;
    private Toolbar mToolbar;
    UserSharedPreference sharedPreference;
    private String loanAmount, returnDays, disbursedAmount, totalInterest, processingFees,repayAmount,
            TAG="BankDetailsActivity", getBankStatementPin="N.A.", loanId, loan_id,platform_charges;
    File fileBankStatmnt, fileSalSlip1, fileSalSlip2, fileSalSlip3;

    private TypedFile tpBankStatement ,tpSalarySlip1, tpSalarySlip2, tpSalarySlip3;

    private  boolean ifDocumentUploaded = false;
    private Uri uriFilePath;
    private static final int CAMERA_REQUEST = 1888;
    private static final int FILE_SELECT_CODE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_details);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


        sharedPreference = new UserSharedPreference(this);

        Intent intent = getIntent();
        if (intent.getExtras() != null) {


            //loan_id = intent.getStringExtra("loan_id");
            loanAmount = intent.getStringExtra("loan_amount");
            returnDays = intent.getStringExtra("days_returning");
            disbursedAmount = intent.getStringExtra("disbursed_amount");
            totalInterest = intent.getStringExtra("total_interest");
            processingFees = intent.getStringExtra("processing_fee");
            repayAmount = intent.getStringExtra("repay_amount");
            platform_charges = intent.getStringExtra("platform_charges");
            Log.i("platform_charges","="+platform_charges);

        }


        //spAccType = findViewById(R.id.spAccType);
        //spBankName = findViewById(R.id.spBankName);
        tvUploadStatmnt = (TextView) findViewById(R.id.tvUploadStatmnt);
        tvUploadSlip1 = findViewById(R.id.tvUploadSlip1);
        tvUploadSlip2 = findViewById(R.id.tvUploadSlip2);
        tvUploadSlip3 = findViewById(R.id.tvUploadSlip3);

        tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        etBankName = findViewById(R.id.etBankName);
        bankStatementPin = findViewById(R.id.etBankStatementPin);
        btSubmitDetails = findViewById(R.id.btSubmitDetails);
        submitLater = findViewById(R.id.btSubmitLater);

        viewBanknameline = findViewById(R.id.viewBanknameline);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvHandlingCharge = findViewById(R.id.tvHandlingCharge);
        tvTotalInterest = findViewById(R.id.tvTotalInterest);
        tvLoanAmount = findViewById(R.id.tvLoanAmount);

        DecimalFormat formatter = new DecimalFormat("#,##,###");

        tvLoanAmount.setText(formatter.format(Integer.parseInt(/*loanAmount*/"15000")));
        tvTotalInterest.setText("" + formatter.format(Integer.parseInt(/*totalInterest*/"1000")));
        tvHandlingCharge.setText("200");

        //tvTotalAmount.setText(""+formatter.format((Integer.parseInt(loanAmount)+Integer.parseInt(totalInterest)+ 200)));

        /*findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });*/

        mToolbar = (Toolbar) findViewById(R.id.toolbarBankDetails);
        setSupportActionBar(mToolbar);



        tvUploadStatmnt.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                nFlagDoc = 1;

                Intent intent4 = new Intent(BankDetailsActivity.this, NormalFilePickActivity.class);
                intent4.putExtra(Constant.MAX_NUMBER, 1);
                intent4.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"pdf"});
                startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);
            }
        });

        tvUploadSlip1.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                nFlagDoc = 2;
                dialogForDocs();
            }
        });

        tvUploadSlip2.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {


                nFlagDoc = 3;
                dialogForDocs();
            }
        });

        tvUploadSlip3.setOnClickListener( new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                /*if (ActivityCompat.checkSelfPermission(BankDetailsActivity.this, FilePickerConst.PERMISSIONS_FILE_PICKER) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{FilePickerConst.PERMISSIONS_FILE_PICKER}, RC_FILE_PICKER_PERM);

                } else {
                    nFlagDoc = 4;
                    dialogForDocs();
                }*/
                nFlagDoc = 4;
                dialogForDocs();
            }
        });


        /*****Applying loan with all documents ....*****/
        btSubmitDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new ConnectionCheck(BankDetailsActivity.this).isNetworkAvailable()) {

                 if (allValidated()){

                     //ifDocumentUploaded = true;

                     //applyForLoan();

                     //new uploadUserSalaryFiles().execute();

                     applyLoan2();


                 }

                } else {
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please Check Your Internet Connection", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }

            }
        });


        /*****Applying loan with without documents ....*****/
        submitLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationForLoan();
            }
        });
    }

    /*@Override
    public void onBackPressed() {
        Toast.makeText(this, "Please complete your loan application", Toast.LENGTH_SHORT).show();
    }*/



    private void applyLoan2(){

        final ProgressDialog progressBar;
        progressBar = new ProgressDialog(BankDetailsActivity.this, R.style.AppCompatProgressDialogStyle);
        progressBar.setCancelable(false);
        progressBar.setMessage("Please Wait...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();


        RestAdapter adapter = new RestAdapter.Builder().setEndpoint(Utility.BASE_URL).setLogLevel(RestAdapter.LogLevel.FULL).build();
        ApiRequest api = adapter.create(ApiRequest.class);

        String userId = sharedPreference.getUserId();




        api.applyLoan2(userId, loanAmount.replace(",", ""), disbursedAmount.replace(",", ""),
                 processingFees, platform_charges,totalInterest,returnDays,
                tpBankStatement, getBankStatementPin,tpSalarySlip1, tpSalarySlip2,tpSalarySlip3,


                new Callback<Response>() {
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

                            //JSONObject jsonObject = new JSONObject(output);
                            Log.i("RESULT", "---->" + output);

                            JSONObject result = new JSONObject(output);


                            if(result.optBoolean("status"))
                            {
                                //String loanId = result.optString("data");

                                Intent intent = new Intent(BankDetailsActivity.this, DoneActivity.class);
                                intent.putExtra("loanAmount", loanAmount);
                                intent.putExtra("payableAmount", disbursedAmount);
                                //intent.putExtra("days_returning", returnDays);
                                intent.putExtra("days_returning", "30");
                                intent.putExtra("orderid", result.optString("data"));
                                intent.putExtra("repayAmount",repayAmount );
                                startActivity(intent);
                                finish();

                            }
                            else
                            {
                                Toast.makeText(BankDetailsActivity.this, result.optString("msg"), Toast.LENGTH_SHORT).show();
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



    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }


    private void applyForLoan() {
        final ProgressDialog progressBar11;
        progressBar11 = new ProgressDialog(BankDetailsActivity.this, R.style.AppCompatProgressDialogStyle);
        progressBar11.setCancelable(false);
        progressBar11.setMessage("Please Wait...");
        progressBar11.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar11.show();


        RequestQueue queue = Volley.newRequestQueue(this);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("user_id", sharedPreference.getUserId());
        postParam.put("loan_amount", loanAmount.replace(",", ""));
        postParam.put("disbursed_amount", disbursedAmount.replace(",", ""));
        postParam.put("days_returning", returnDays);
        postParam.put("total_interest", totalInterest);
        postParam.put("processing_fee", processingFees.replace(",", ""));


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Utility.BASE_URL+"/applyLoan2", new JSONObject(postParam), new com.android.volley.Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                progressBar11.dismiss();
                if(response.optBoolean("status"))
                {
                    Log.i(TAG,"Inside if condition ");

                    loanId = response.optString("loan_id");

                    getBankStatementPin = bankStatementPin.getText().toString().trim();
                    if(ifDocumentUploaded)
                    {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                new uploadUserSalaryFiles().execute();
                            }
                        }, 120);

                    }
                    else
                    {
                        Intent intent = new Intent(BankDetailsActivity.this, DoneActivity.class);
                        intent.putExtra("loanAmount", loanAmount);
                        intent.putExtra("payableAmount", disbursedAmount);
                        intent.putExtra("days_returning", returnDays);
                        intent.putExtra("orderid", loanId);
                        intent.putExtra("repayAmount",repayAmount );
                        startActivity(intent);
                        finish();
                    }
                }
                else
                {
                    Toast.makeText(BankDetailsActivity.this, response.optString("msg"), Toast.LENGTH_SHORT).show();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                progressBar11.dismiss();
                Toast.makeText(BankDetailsActivity.this, "Something went wrong, Please try after some time !", Toast.LENGTH_SHORT).show();
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

    void confirmationForLoan(){

        new AlertDialog.Builder(this)
                .setTitle("Submit Later")
                .setMessage("Do you confirm to upload document later ?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        ifDocumentUploaded = false;

                        new uploadUserSalaryFiles().execute();
                    }})
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }


    private void dialogForDocs() {

        LayoutInflater inflater = LayoutInflater.from(BankDetailsActivity.this);
        View dialogView = inflater.inflate(R.layout.dialog_for_docs, new LinearLayout(BankDetailsActivity.this), false);
        final AlertDialog dialog = new AlertDialog.Builder(BankDetailsActivity.this)
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

                    if (ContextCompat.checkSelfPermission(BankDetailsActivity.this, Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_DENIED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            ActivityCompat.requestPermissions(BankDetailsActivity.this, new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                        }

                    }else {


                        dialog.dismiss();
                        capturePhoto();
                        //startCamera();
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
                   /* if (ActivityCompat.checkSelfPermission(BankDetailsActivity.this, FilePickerConst.PERMISSIONS_FILE_PICKER) != PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{FilePickerConst.PERMISSIONS_FILE_PICKER}, RC_FILE_PICKER_PERM);
                        }

                    } else {
                        FilePickerBuilder.getInstance().enableDocSupport(false)
                                .setMaxCount(1)
                                .setSelectedFiles(alFilePath)
                                .setActivityTheme(R.style.AppTheme)
                                .pickFile(BankDetailsActivity.this);
                        dialog.dismiss();
                    }*/



                    Intent intent4 = new Intent(BankDetailsActivity.this, NormalFilePickActivity.class);
                    intent4.putExtra(Constant.MAX_NUMBER, 1);
                    intent4.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"pdf"});
                    startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);
                    dialog.dismiss();

                }
            });


        }
    }




    private  File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "InstantMudra/tmp");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("TAG", "Oops! Failed create "
                        + "InstantMudra/tmp" + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;




        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    +"IMG_" +sharedPreference.getUserId()+"_"+ timeStamp + ".jpg");
        }  else {
            return null;
        }

        return mediaFile;
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

    private void openGallery(int nDifPhoto) {

        if (nDifPhoto == 3) {
            Intent mIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(mIntent, nRESULT_LOAD_IMAGE_PHOTO);
        }
    }

    private boolean allValidated() {

        if (fileBankStatmnt == null || !fileBankStatmnt.exists()) {
            Toast.makeText(this, "Please select your bank statement", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (fileSalSlip1 == null || !fileSalSlip1.exists()) {
            Toast.makeText(this, "Please select salary slip 1", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (fileSalSlip2 == null || !fileSalSlip2.exists()) {
            Toast.makeText(this, "Please select salary slip 2", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (fileSalSlip3 == null || !fileSalSlip3.exists()) {
            Toast.makeText(this, "Please select salary slip 3", Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_CANCELED) {
            Log.i("switch", "=" + nFlagDoc);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            switch (nFlagDoc) {

                case 1:

                    if (data != null) {
                        ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                        if (list.size() != 0) {
                            String path = list.get(0).getPath();
                            fileBankStatmnt = new File(list.get(0).getPath());
                            tpBankStatement =  new TypedFile("multipart/form-data", fileBankStatmnt);
                            tvUploadStatmnt.setText(path.substring(path.lastIndexOf("/") + 1, path.length()));
                            tvUploadStatmnt.setTextColor(getResources().getColor(R.color.textGreen));
                        }
                    }
                    break;

                case 2:
                    if (requestCode == REQUEST_IMAGE_CAPTURE) {
                        //Bundle extras = data.getExtras();
                        if (resultCode == RESULT_OK) {


                            File fileSlip1 = new File(uriFilePath.getPath());
                            Log.i("uri", "==" + fileSlip1.getPath());
                            try {
                                fileSalSlip1 = new Compressor(this)
                                        .setMaxWidth(640)
                                        .setMaxHeight(480)
                                        .setQuality(30)
                                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                                Environment.DIRECTORY_PICTURES).getAbsolutePath())
                                        .compressToFile(fileSlip1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                            tpSalarySlip1 =  new TypedFile("multipart/form-data", fileSalSlip1);

                            String sFileNam = fileSlip1.getPath();
                            tvUploadSlip1.setText("Salary slip 1 : \n " + sFileNam.substring(sFileNam.lastIndexOf("/") + 1, sFileNam.length()));
                            tvUploadSlip1.setTextColor(getResources().getColor(R.color.textGreen));


                        } else {
                            Toast.makeText(this, "Not able to capture Image", Toast.LENGTH_SHORT).show();
                        }


                    } else if (requestCode == nRESULT_LOAD_IMAGE_PHOTO) {
                        //InputStream is= null;
                        //Uri uriEmpFront;
                        if (data != null) {


                            Uri uriEmpFront = data.getData();
                            String uriFromPath = getRealPathFromURI(uriEmpFront, BankDetailsActivity.this);
                            Log.i("gal_uri", "==" + uriFromPath);
                            File fileSlip1 = new File(uriFromPath);
                            try {
                                fileSalSlip1 = new Compressor(this)
                                        .setMaxWidth(640)
                                        .setMaxHeight(480)
                                        .setQuality(30)
                                        .setCompressFormat(Bitmap.CompressFormat.PNG)
                                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                                Environment.DIRECTORY_PICTURES).getAbsolutePath())
                                        .compressToFile(fileSlip1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            tpSalarySlip1 =  new TypedFile("multipart/form-data", fileSalSlip1);
                            String sFileNam = fileSalSlip1.getPath();
                            tvUploadSlip1.setText("Salary slip 1 : \n " + sFileNam.substring(sFileNam.lastIndexOf("/") + 1, sFileNam.length()));
                            tvUploadSlip1.setTextColor(getResources().getColor(R.color.textGreen));

                           /* if (uriEmpFront.getAuthority() != null) {



                                File fileSlip1 = new File(uriEmpFront.getPath());
                                try {
                                    fileSalSlip1 = new Compressor(this)
                                            .setMaxWidth(640)
                                            .setMaxHeight(480)
                                            .setQuality(30)
                                            .setCompressFormat(Bitmap.CompressFormat.PNG)
                                            .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                                    Environment.DIRECTORY_PICTURES).getAbsolutePath())
                                            .compressToFile(fileSlip1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                String sFileNam = fileSalSlip1.getPath();
                                tvUploadSlip1.setText("Salary slip 1 : \n " + sFileNam.substring(sFileNam.lastIndexOf("/") + 1, sFileNam.length()));
                                tvUploadSlip1.setTextColor(getResources().getColor(R.color.textGreen));
                            }*/
                        }
                    } else {


                        if (data != null) {
                            ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                            if (list.size() != 0) {
                                String path = list.get(0).getPath();
                                fileSalSlip1 = new File(list.get(0).getPath());
                                tpSalarySlip1 =  new TypedFile("multipart/form-data", fileSalSlip1);
                                tvUploadSlip1.setText(path.substring(path.lastIndexOf("/") + 1, path.length()));
                                tvUploadSlip1.setTextColor(getResources().getColor(R.color.textGreen));
                            }
                        }

                    }
                    break;

                case 3:
                    if (requestCode == REQUEST_IMAGE_CAPTURE) {
                        //Bundle extras = data.getExtras();
                        if (resultCode == RESULT_OK) {


                            File fileSlip2 = new File(uriFilePath.getPath());
                            try {
                                fileSalSlip2 = new Compressor(this)
                                        .setMaxWidth(640)
                                        .setMaxHeight(480)
                                        .setQuality(30)
                                        .setCompressFormat(Bitmap.CompressFormat.PNG)
                                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                                Environment.DIRECTORY_PICTURES).getAbsolutePath())
                                        .compressToFile(fileSlip2);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            tpSalarySlip2 =  new TypedFile("multipart/form-data", fileSalSlip2);
                            String sFileNam = fileSlip2.getPath();
                            tvUploadSlip2.setText("Salary slip 2 : \n " + sFileNam.substring(sFileNam.lastIndexOf("/") + 1, sFileNam.length()));
                            tvUploadSlip2.setTextColor(getResources().getColor(R.color.textGreen));

                        } else {
                            Toast.makeText(this, "Not able to capture Image", Toast.LENGTH_SHORT).show();
                        }
                    } else if (requestCode == nRESULT_LOAD_IMAGE_PHOTO) {
                        /*InputStream is;
                        Uri uriEmpFront;
                        is = null;*/
                        if (data != null) {


                            Uri uriEmpFront = data.getData();
                            String uriFromPath = getRealPathFromURI(uriEmpFront, BankDetailsActivity.this);
                            Log.i("gal_uri", "==" + uriFromPath);
                            File fileSlip2 = new File(uriFromPath);
                            try {
                                fileSalSlip2 = new Compressor(this)
                                        .setMaxWidth(640)
                                        .setMaxHeight(480)
                                        .setQuality(30)
                                        .setCompressFormat(Bitmap.CompressFormat.PNG)
                                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                                Environment.DIRECTORY_PICTURES).getAbsolutePath())
                                        .compressToFile(fileSlip2);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            tpSalarySlip2 =  new TypedFile("multipart/form-data", fileSalSlip2);
                            String sFileNam = fileSalSlip2.getPath();
                            tvUploadSlip2.setText("Salary slip 2 : \n " + sFileNam.substring(sFileNam.lastIndexOf("/") + 1, sFileNam.length()));
                            tvUploadSlip2.setTextColor(getResources().getColor(R.color.textGreen));



                            /*if (uriEmpFront.getAuthority() != null) {
                             *//* try {
                                    is = BankDetailsActivity.this.getContentResolver().openInputStream(uriEmpFront);
                                    Bitmap photo = BitmapFactory.decodeStream(is);
                                   *//**//* Glide.with(this).clear(ivSalSlip1);
                                    ivSalSlip1.setImageBitmap(photo);*//**//*
                                    fileSalSlip2 = new File(BankDetailsActivity.this.getCacheDir(), "IMSAL2_" + new Random().nextInt(500) + ".png");
                                    fileSalSlip2.createNewFile();

                                    Bitmap bitmap = photo;
                                    ByteArrayOutputStream bos = new ByteArrayOutputStream();

                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                                    byte[] bitmapdata = bos.toByteArray();

                                    FileOutputStream fos = new FileOutputStream(fileSalSlip2);
                                    fos.write(bitmapdata);
                                    fos.flush();
                                    fos.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                tvUploadSlip2.setText("Salary slip 2 : \n " + "Uploaded");
                                tvUploadSlip2.setTextColor(getResources().getColor(R.color.textGreen));*//*

                             *//*File fileSlip2 = new File(uriEmpFront.getPath());
                                try {
                                    fileSalSlip2 = new Compressor(this)
                                            .setMaxWidth(640)
                                            .setMaxHeight(480)
                                            .setQuality(30)
                                            .setCompressFormat(Bitmap.CompressFormat.PNG)
                                            .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                                    Environment.DIRECTORY_PICTURES).getAbsolutePath())
                                            .compressToFile(fileSlip2);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                String sFileNam = fileSalSlip2.getPath();
                                tvUploadSlip2.setText("Salary slip 2 : \n " + sFileNam.substring(sFileNam.lastIndexOf("/") + 1, sFileNam.length()));
                                tvUploadSlip2.setTextColor(getResources().getColor(R.color.textGreen));*//*

                                fileSalSlip3 = new File(uriFilePath.getPath());
                                String sFileNam = fileSalSlip3.getPath();
                                tvUploadSlip3.setText("Salary slip 3 : \n " +sFileNam.substring(sFileNam.lastIndexOf("/")+1, sFileNam.length()));
                                tvUploadSlip3.setTextColor(getResources().getColor(R.color.textGreen));
                            }*/
                        }
                    } else {

                        if (data != null) {
                            ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                            if (list.size() != 0) {
                                String path = list.get(0).getPath();
                                fileSalSlip2 = new File(list.get(0).getPath());
                                tpSalarySlip2 =  new TypedFile("multipart/form-data", fileSalSlip2);
                                tvUploadSlip2.setText(path.substring(path.lastIndexOf("/") + 1, path.length()));
                                tvUploadSlip2.setTextColor(getResources().getColor(R.color.textGreen));
                            }
                        }

                    }
                    break;

                case 4:
                    if (requestCode == REQUEST_IMAGE_CAPTURE) {
                        //Bundle extras = data.getExtras();
                        if (resultCode == RESULT_OK) {

                            File fileSlip3 = new File(uriFilePath.getPath());
                            try {
                                fileSalSlip3 = new Compressor(this)
                                        .setMaxWidth(640)
                                        .setMaxHeight(480)
                                        .setQuality(30)
                                        .setCompressFormat(Bitmap.CompressFormat.PNG)
                                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                                Environment.DIRECTORY_PICTURES).getAbsolutePath())
                                        .compressToFile(fileSlip3);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            tpSalarySlip3 =  new TypedFile("multipart/form-data", fileSalSlip3);
                            String sFileNam = fileSlip3.getPath();
                            tvUploadSlip3.setText("Salary slip 3 : \n " + sFileNam.substring(sFileNam.lastIndexOf("/") + 1, sFileNam.length()));
                            tvUploadSlip3.setTextColor(getResources().getColor(R.color.textGreen));

                        } else {
                            Toast.makeText(this, "Not able to capture Image", Toast.LENGTH_SHORT).show();
                        }
                    } else if (requestCode == nRESULT_LOAD_IMAGE_PHOTO) {
                       /* InputStream is;
                        Uri uriEmpFront;
                        is = null;*/
                        if (data != null) {

                            Uri uriEmpFront = data.getData();
                            String uriFromPath = getRealPathFromURI(uriEmpFront, BankDetailsActivity.this);
                            File fileSlip3 = new File(uriFromPath);
                            try {
                                fileSalSlip3 = new Compressor(this)
                                        .setMaxWidth(640)
                                        .setMaxHeight(480)
                                        .setQuality(30)
                                        .setCompressFormat(Bitmap.CompressFormat.PNG)
                                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                                Environment.DIRECTORY_PICTURES).getAbsolutePath())
                                        .compressToFile(fileSlip3);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            tpSalarySlip3 =  new TypedFile("multipart/form-data", fileSalSlip3);
                            String sFileNam = fileSalSlip3.getPath();
                            tvUploadSlip3.setText("Salary slip 3 : \n " + sFileNam.substring(sFileNam.lastIndexOf("/") + 1, sFileNam.length()));
                            tvUploadSlip3.setTextColor(getResources().getColor(R.color.textGreen));

                        }
                    } else {

                        if (data != null) {
                            ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                            if (list.size() != 0) {
                                String path = list.get(0).getPath();
                                fileSalSlip3 = new File(list.get(0).getPath());
                                tpSalarySlip3 =  new TypedFile("multipart/form-data", fileSalSlip3);
                                tvUploadSlip3.setText(path.substring(path.lastIndexOf("/") + 1, path.length()));
                                tvUploadSlip3.setTextColor(getResources().getColor(R.color.textGreen));
                            }
                        }

                    }
                    break;


                case FILE_SELECT_CODE:

                    if (resultCode == RESULT_OK) {
                        // Get the Uri of the selected file
                        Uri uri = data.getData();
                        Log.d(TAG, "File Uri: " + uri.toString());
                        Log.d(TAG, "File Uri Path: " + uri.getPath());

                        //String filePath =  getRealPathFromURI(uri, BankDetailsActivity.this);

                        try {
                            fileBankStatmnt = new File(new URI(uri.toString()));
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }

                        tpBankStatement =  new TypedFile("multipart/form-data", fileBankStatmnt);
                        tvUploadStatmnt.setText(getFileName(uri));

                        Log.i("path","="+fileBankStatmnt.exists());

                        tvUploadStatmnt.setTextColor(getResources().getColor(R.color.textGreen));

                    }
                    break;
            }

        }


    }





    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (uriFilePath != null)
            outState.putString("uri_file_path", uriFilePath.toString());
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
    }



    @SuppressLint("StaticFieldLeak")
    private class uploadUserSalaryFiles extends AsyncTask<Void, Integer, String> {

        ProgressDialog progressBarAsn = new ProgressDialog(BankDetailsActivity.this, R.style.AppCompatProgressDialogStyle);
        long totalSize = 0;


        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBarAsn.setCancelable(false);
            progressBarAsn.setTitle("Uploading Files...");
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
            HttpPost httppost = new HttpPost(Utility.BASE_URL+"/updateUserSalaryFiles");


            try {

                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));

                            }
                        });


                entity.addPart("order_id", new StringBody(loanId ));
                entity.addPart("bank_statement_pin", new StringBody(getBankStatementPin));
                entity.addPart("bank_statement", new FileBody(fileBankStatmnt));
                entity.addPart("salary_slip1", new FileBody(fileSalSlip1));
                entity.addPart("salary_slip2", new FileBody(fileSalSlip2));
                entity.addPart("salary_slip3", new FileBody(fileSalSlip3));


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
                    //Toast.makeText(BankDetailsActivity.this, jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(BankDetailsActivity.this, DoneActivity.class);
                    intent.putExtra("loanAmount", loanAmount);
                    intent.putExtra("payableAmount", disbursedAmount);
                    intent.putExtra("days_returning", returnDays);
                    intent.putExtra("orderid", loanId);
                    intent.putExtra("repayAmount",repayAmount );
                    startActivity(intent);
                    finish();

                }
                else
                {
                    Toast.makeText(BankDetailsActivity.this, jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(BankDetailsActivity.this, DoneActivity.class);
                    intent.putExtra("loanAmount", loanAmount);
                    intent.putExtra("payableAmount", disbursedAmount);
                    intent.putExtra("days_returning", returnDays);
                    intent.putExtra("orderid", loanId);
                    intent.putExtra("repayAmount",repayAmount );
                    startActivity(intent);
                    finish();
                }
            }


            super.onPostExecute(result);

        }

    }


}
