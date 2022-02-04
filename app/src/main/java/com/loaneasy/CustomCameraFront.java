package com.loaneasy;

import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class CustomCameraFront extends AppCompatActivity {

    private Camera mCamera;
    private CameraPreview mPreview;
    public static final int MEDIA_TYPE_IMAGE = 1;


    private String TAG = "CustomCameraFront";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);


        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        Button captureFrontPhoto = findViewById(R.id.button_capture);
        captureFrontPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get an image from the camera
                mCamera.takePicture(null, null, mPicture);
            }
        });
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        Log.d("No of cameras", Camera.getNumberOfCameras() + "");
        for (int camNo = 0; camNo < Camera.getNumberOfCameras(); camNo++) {
            Camera.CameraInfo camInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(camNo, camInfo);

            if (camInfo.facing == (Camera.CameraInfo.CAMERA_FACING_FRONT)) {
                c = Camera.open(camNo);
                c.setDisplayOrientation(90);
            }
        }
        return c; // returns null if camera is unavailable
    }


    private  String  getCutsomPath(String file_path)
    {

        String path = "file://"+file_path;

        // String path = "file:///storage/emulated/0/Pictures/2019_09_27/Cart_IMG_20190927_174122.jpg"

        try {
            URL url = new URL (path);

            try {
                Uri uri = Uri.parse(url.toURI().toString());

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //String filePath  = path;
        return path;
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);

            if (pictureFile == null){
                Log.d(TAG, "Error creating media file, check storage permissions");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }


            Uri  fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
            Log.i("fileUri","="+fileUri);

            Intent i = new Intent();
            i.putExtra("uri", fileUri.toString());
            setResult(-1, i);
            finish();



        }
    };



    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }



    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "InstantMudra");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("InstantMudra", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
}
