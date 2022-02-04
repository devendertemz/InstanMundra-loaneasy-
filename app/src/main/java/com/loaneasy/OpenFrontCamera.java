package com.loaneasy;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.loaneasy.user_details.UserDetailsActivity;
import com.loaneasy.user_details_new.OfficialDetailsActivity;
import com.loaneasy.user_details_new.PersonalDetailsActivity;
import com.loaneasy.utils.UserSharedPreference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class OpenFrontCamera extends AppCompatActivity {

    private Button btProceed,openCamera;
    private ImageView imageView, openFrontCamera;
    private final static int CAMERA_PIC_REQUEST1 = 0;
    Context con;

    private Uri uriFilePath;

    private String currentPhotoPath;

    static final int REQUEST_TAKE_PHOTO = 1;

    private String firstName, lastName, officialEmail, gender, dob, currentLoan, houseType, stayingYears, etState, etCity,
            etPinCode, etLocalAddress, emi_amount,etReferenceName1, etReferencePhone1,etReferenceName2, etReferencePhone2;
    Uri uriSelfie;
    UserSharedPreference sharedPreference;
    Bitmap bitmapFrontCam;

    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opne_front_camera);
        sharedPreference = new UserSharedPreference(this);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        if (savedInstanceState != null) {
            if (uriFilePath == null && savedInstanceState.getString("uri_file_path") != null) {
                uriFilePath = Uri.parse(savedInstanceState.getString("uri_file_path"));
            }
        }



        btProceed = (Button) findViewById(R.id.btProceed);
        openCamera = (Button) findViewById(R.id.btOpenCamera);
        openFrontCamera = (ImageView) findViewById(R.id.imageView1);
        imageView = (ImageView)this.findViewById(R.id.imageView1);

        Intent intent = getIntent();
        if (intent!= null){
            firstName = intent.getStringExtra("firstName");
            lastName = intent.getStringExtra("lastName");
            officialEmail = intent.getStringExtra("officialEmail");
            gender = intent.getStringExtra("gender");
            dob = intent.getStringExtra("dob");
            currentLoan = intent.getStringExtra("currentLoan");
            houseType = intent.getStringExtra("houseType");
            stayingYears = intent.getStringExtra("stayingYears");
            etState = intent.getStringExtra("etState");
            etCity = intent.getStringExtra("etCity");
            etPinCode = intent.getStringExtra("etPinCode");
            etLocalAddress = intent.getStringExtra("etLocalAddress");
            emi_amount = intent.getStringExtra("emi_amount");

            etReferenceName1 = intent.getStringExtra("etReferenceName1");
            etReferencePhone1 = intent.getStringExtra("etReferencePhone1");
            etReferenceName2 = intent.getStringExtra("etReferenceName2");
            etReferencePhone2 = intent.getStringExtra("etReferencePhone2");


        }

        openFrontCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* if (getFrontCameraId() == -1) {
                    Toast.makeText(getApplicationContext(),
                            "Front Camera Not Detected", Toast.LENGTH_SHORT).show();
                } else {
                    Intent cameraIntent = new Intent();
                    cameraIntent.setClass(getApplicationContext(), CustomCameraFront.class);
                    startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST1);

                    // startActivity(new
                    // Intent(MainActivity.this,CameraActivity.class));
                }*/

                dispatchTakePictureIntent();
            }
        });

        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dispatchTakePictureIntent();

               /* if (getFrontCameraId() == -1) {
                    Toast.makeText(getApplicationContext(),
                            "Front Camera Not Detected", Toast.LENGTH_SHORT).show();
                } else {
                    Intent cameraIntent = new Intent();
                    cameraIntent.setClass(getApplicationContext(), CustomCameraFront.class);
                    startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST1);

                    // startActivity(new
                    // Intent(MainActivity.this,CameraActivity.class));
                }*/


            }
        });

        btProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (uriFilePath != null && !uriFilePath.equals(Uri.EMPTY)) {
                    Intent intent = new Intent(OpenFrontCamera.this, OfficialDetailsActivity.class);
                    intent.putExtra("firstName", firstName);
                    intent.putExtra("lastName", lastName);
                    intent.putExtra("officialEmail", officialEmail);
                    intent.putExtra("gender", gender);
                    intent.putExtra("dob", dob);
                    intent.putExtra("currentLoan", currentLoan);
                    intent.putExtra("houseType", houseType);
                    intent.putExtra("stayingYears", stayingYears);
                    intent.putExtra("etState", etState);
                    intent.putExtra("etCity", etCity);
                    intent.putExtra("etPinCode", etPinCode);
                    intent.putExtra("etLocalAddress", etLocalAddress);
                    intent.putExtra("uriSelfie", uriFilePath.toString());
                    //intent.putExtra("selfie_path", imagePath);
                    intent.putExtra("emi_amount", emi_amount);

                    intent.putExtra("etReferenceName1", etReferenceName1);
                    intent.putExtra("etReferencePhone1", etReferencePhone1);
                    intent.putExtra("etReferenceName2",  etReferenceName2);
                    intent.putExtra("etReferencePhone2",  etReferencePhone2);

                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please take your photo to continue", Toast.LENGTH_SHORT).show();
                }

               /* if(allValidated()) {

                }*/
            }
        });


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (uriFilePath != null)
            outState.putString("uri_file_path", uriFilePath.toString());
        super.onSaveInstanceState(outState);
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

   /* private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                uriFilePath = FileProvider.getUriForFile(this, "com.loaneasy.fileprovider", photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriFilePath);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
                takePictureIntent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
                takePictureIntent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                Log.i("uri_from_selfie","=="+uriFilePath);
            }
        }
    }*/





    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            imagePath = uriFilePath.toString();

            imageView.setImageURI(null);
            imageView.setImageURI(uriFilePath);

        }
    }



    public boolean allValidated(){

        if (uriSelfie == null){

            Toast.makeText(getApplicationContext(), "Please take your photo to continue", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    int getFrontCameraId() {
        Camera.CameraInfo ci = new Camera.CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, ci);
            if (ci.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
                return i;
        }
        return -1; // No front-facing camera found
    }

    public int getRotationDegree() {
        int degree = 0;

        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                degree = info.orientation;

                return degree;
            }
        }

        return degree;
    }



    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_PIC_REQUEST1) {
            if (resultCode == RESULT_OK) {

                try {

                    imagePath = data.getStringExtra("uri");
                    Uri uriData = Uri.parse(imagePath);
                    //bitmapFrontCam = (Bitmap) data.getParcelableExtra("BitmapImage");

                    //imageView.setImageURI(uriData);

                    bitmapFrontCam = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriData);


                    Matrix matrix = new Matrix();
                    matrix.postRotate(270);
                    Bitmap rotatedBitmap = Bitmap.createBitmap(bitmapFrontCam, 0, 0, bitmapFrontCam.getWidth(), bitmapFrontCam.getHeight(), matrix, true);

                    imageView.setImageBitmap(rotatedBitmap);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT)
                    .show();
        }
    }*/



}
