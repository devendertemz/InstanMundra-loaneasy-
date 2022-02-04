package com.loaneasy.new_user_details;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.loaneasy.EMandateBankDetails;
import com.loaneasy.R;
import com.loaneasy.user_details_new.OfficialDetailsActivity;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import id.zelory.compressor.Compressor;

public class UploadUserFiles extends AppCompatActivity  implements View.OnClickListener {


    private  final int CAMERA_REQUEST = 1888;
    private  final int MY_CAMERA_PERMISSION_CODE = 100;
    public final int nRESULT_LOAD_IMAGE_PHOTO = 3;
    public final int REQUEST_IMAGE_CAPTURE = 200;
    String[] PERMISSIONS = { Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };
    int PERMISSION_ALL = 1;
    private final int CAMERA_REQUEST_CODE = 100;
    private UserSharedPreference sharedPreference;
    private Uri uriFilePath;
    private ArrayList<String> alFilePath ;
    private File fileAadharFront, fileAadharBack, filePanCard, fileEmployeeIdCard, fileUserProfilePic;
    final int REQUEST_TAKE_PHOTO = 1;
    private int cameraAction;
    private ImageView userProfile, employeeId, aadharFront, aadharBack, panCard;
    private Button uploadFiles;

    private String getUriSelfie ="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_user_files);

        sharedPreference = new UserSharedPreference(this);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        alFilePath = new ArrayList<>();

        if (savedInstanceState != null) {
            if (uriFilePath == null && savedInstanceState.getString("uri_file_path") != null) {
                uriFilePath = Uri.parse(savedInstanceState.getString("uri_file_path"));
            }
        }



        userProfile = findViewById(R.id.ivUserSelfie);
        employeeId = findViewById(R.id.ivEmployeeId);
        aadharFront = findViewById(R.id.ivAdharFront);
        aadharBack = findViewById(R.id.ivAdharBack);
        panCard = findViewById(R.id.ivPanCard);
        uploadFiles = findViewById(R.id.btUploadUserFiles);

        userProfile.setOnClickListener(this);
        employeeId.setOnClickListener(this);
        aadharFront.setOnClickListener(this);
        aadharBack.setOnClickListener(this);
        panCard.setOnClickListener(this);
        uploadFiles.setOnClickListener(this);

        if (!hasPermissions(this, PERMISSIONS)) {

            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

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
                Toast.makeText(UploadUserFiles.this, "camera permission denied", Toast.LENGTH_LONG).show();
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


    private void openGallery(int nDifPhoto) {

        if (nDifPhoto == 3) {
            Intent mIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(mIntent, nRESULT_LOAD_IMAGE_PHOTO);
        }
    }

    private void dialogForDocs() {
        LayoutInflater inflater = LayoutInflater.from(UploadUserFiles.this);
        View dialogView = inflater.inflate(R.layout.dialog_for_docs, new LinearLayout(UploadUserFiles.this), false);
        final AlertDialog dialog = new AlertDialog.Builder(UploadUserFiles.this)
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

                    if (ContextCompat.checkSelfPermission(UploadUserFiles.this, Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_DENIED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            ActivityCompat.requestPermissions(UploadUserFiles.this, new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
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
                        Toast.makeText(UploadUserFiles.this, "PLease take a Picture or Upload from Gallery", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Intent intent= new Intent(UploadUserFiles.this, NormalFilePickActivity.class);
                        intent.putExtra(Constant.MAX_NUMBER, 1);
                        intent.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"pdf"});
                        startActivityForResult(intent, Constant.REQUEST_CODE_PICK_FILE);
                        dialog.dismiss();
                    }

                }
            });


        }
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


    private String getRealPathFromURI(Uri contentURI, AppCompatActivity context) {
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


    private boolean allValidated() {

        if (fileUserProfilePic == null || !fileUserProfilePic.exists()) {
            Toast.makeText(UploadUserFiles.this, "Please take your selfie", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (fileEmployeeIdCard == null || !fileEmployeeIdCard.exists()) {
            Toast.makeText(UploadUserFiles.this, "Please take a picture of your employment ID card", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (fileAadharFront == null || !fileAadharFront.exists()) {
            Toast.makeText(UploadUserFiles.this, "Please take a picture of Aadhar front", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (fileAadharBack == null || !fileAadharBack.exists()) {
            Toast.makeText(UploadUserFiles.this, "Please take a picture of Aadhar back", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (filePanCard == null || !filePanCard.exists()) {
            Toast.makeText(UploadUserFiles.this, "Please take a picture of pan card", Toast.LENGTH_SHORT).show();
            return false;
        }



        return true;
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (uriFilePath != null)
            outState.putString("uri_file_path", uriFilePath.toString());
        super.onSaveInstanceState(outState);
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


                            aadharFront.setImageURI(null);
                            aadharFront.setImageURI(uriFilePath);
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

                            aadharFront.setImageURI(null);
                            aadharFront.setImageURI(uriEmpFront);

                            Log.i("galleryUri", "==" + uriEmpFront);


                            String uriFromPath = getRealPathFromURI(uriEmpFront, UploadUserFiles.this);
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
                                aadharFront.setImageResource(R.drawable.ic_pdf_svg);

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



                            aadharBack.setImageURI(null);
                            aadharBack.setImageURI(uriFilePath);

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
                            aadharBack.setImageURI(null);
                            aadharBack.setImageURI(uriEmpFront);

                            String uriFromPath = getRealPathFromURI(uriEmpFront, UploadUserFiles.this);
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
                                aadharBack.setImageResource(R.drawable.ic_pdf_svg);

                            }
                        }


                    }
                    break;
                case 3:
                    if (requestCode == REQUEST_IMAGE_CAPTURE) {
                        //Bundle extras = data.getExtras();
                        if (resultCode == RESULT_OK) {

                            panCard.setImageURI(null);
                            panCard.setImageURI(uriFilePath);
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
                            panCard.setImageURI(null);
                            panCard.setImageURI(uriEmpFront);
                            String uriFromPath = getRealPathFromURI(uriEmpFront, UploadUserFiles.this);
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
                                panCard.setImageResource(R.drawable.ic_pdf_svg);

                            }
                        }


                    }
                    break;

                case 4:

                    if (requestCode == REQUEST_IMAGE_CAPTURE) {
                        //Bundle extras = data.getExtras();
                        if (resultCode == RESULT_OK) {

                            employeeId.setImageURI(null);
                            employeeId.setImageURI(uriFilePath);
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
                            employeeId.setImageURI(null);
                            employeeId.setImageURI(uriEmpFront);
                            String uriFromPath = getRealPathFromURI(uriEmpFront, UploadUserFiles.this);
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
                        employeeId.setImageResource(R.drawable.ic_pdf_svg);


                    }
                    break;
            }
        }

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            getUriSelfie = uriFilePath.toString();

            userProfile.setImageURI(null);
            userProfile.setImageURI(uriFilePath);

            Uri uriData = Uri.parse(getUriSelfie);
            fileUserProfilePic = new File(uriData.getPath());

            Log.i("selfieFile","="+fileUserProfilePic.getPath());

            try {
                fileUserProfilePic = new Compressor(getApplicationContext())
                        .setMaxWidth(640)
                        .setMaxHeight(480)
                        .setQuality(30)
                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES).getAbsolutePath())
                        .compressToFile(fileUserProfilePic);
            } catch (IOException e) {
                e.printStackTrace();
            }



        }
    }



    private void dispatchTakePictureIntent() {

        PackageManager packageManager = getApplication().getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            File mainDirectory = new File(Environment.getExternalStorageDirectory(), "InstantMudra/tmp");
            if (!mainDirectory.exists()) {
                mainDirectory.mkdirs();
            }

            Calendar calendar = Calendar.getInstance();
            File mediaFile = new File(mainDirectory.getPath() + File.separator
                    + "IMG_" + sharedPreference.getUserId() + "_" + calendar.getTimeInMillis() + ".jpg");

            uriFilePath = Uri.fromFile(mediaFile);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFilePath);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
            intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
            intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
            startActivityForResult(intent, REQUEST_TAKE_PHOTO);

        }
    }



    @SuppressLint("StaticFieldLeak")
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {

        ProgressDialog progressBarAsn = new ProgressDialog(UploadUserFiles.this, R.style.AppCompatProgressDialogStyle);
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



                entity.addPart("user_id", new StringBody(sharedPreference.getUserId() ));
                //entity.addPart("profile_pic", new FileBody( new File(Uri.parse(getUriSelfie).getPath())));
                entity.addPart("profile_pic", new FileBody(fileUserProfilePic));
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

                    Toast.makeText(UploadUserFiles.this, jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
                    JSONObject resultObject = jsonObject.optJSONObject("result");
                    if (resultObject.optString("profile_completed").equalsIgnoreCase("1")) {
                        sharedPreference.setSignFlag(3);

                        Intent openBankDetails = new Intent(getApplicationContext(), EMandateBankDetails.class);
                        openBankDetails.putExtra("updateBank", false);
                        startActivity(openBankDetails);
                        finish();
                    }

               /* else
                {
                    Toast.makeText(UploadUserFiles.this, jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
                }*/
            }



            super.onPostExecute(result);



        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.ivUserSelfie:
                dispatchTakePictureIntent();
                break;
            case R.id.ivEmployeeId:
                cameraAction = 4;
                dialogForDocs();
                break;
            case R.id.ivAdharFront:
                cameraAction = 1;
                dialogForDocs();

                break;
            case R.id.ivAdharBack:
                cameraAction = 2;
                dialogForDocs();

                break;
            case R.id.ivPanCard:
                cameraAction = 3;
                dialogForDocs();
                break;

            case R.id.btUploadUserFiles:
                if (new ConnectionCheck(UploadUserFiles.this).isNetworkAvailable()) {

                    if (allValidated())
                    {
                        new UploadFileToServer().execute();
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
