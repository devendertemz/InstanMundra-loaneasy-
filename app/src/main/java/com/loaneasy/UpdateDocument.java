package com.loaneasy;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.loaneasy.utils.AndroidMultiPartEntity;
import com.loaneasy.utils.ConnectionCheck;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

public class UpdateDocument extends AppCompatActivity implements View.OnClickListener {

    private TextView tvUploadStatement, tvUploadSlip1, tvUploadSlip2, tvUploadSlip3, tv_toolbar_title;

    private Button btSubmitDetails;

    private int nFlagDoc;

    private File fileBankStatmnt, fileSalSlip1, fileSalSlip2, fileSalSlip3;

    private String getBankStatementPin="N.A.", loanId;

    private EditText bankStatementPin;

    private final int CAMERA_REQUEST_CODE = 100;
    public final int nRESULT_LOAD_IMAGE_PHOTO = 3;
    public final int REQUEST_IMAGE_CAPTURE = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_document);


        Intent intent = getIntent();
        if (intent.getExtras() != null) {

            loanId = intent.getStringExtra("loan_id");
            Log.i("loan Id","="+loanId);
        }


        tvUploadStatement = findViewById(R.id.tvUploadStatmnt);
        tvUploadSlip1 = findViewById(R.id.tvUploadSlip1);
        tvUploadSlip2 = findViewById(R.id.tvUploadSlip2);
        tvUploadSlip3 = findViewById(R.id.tvUploadSlip3);

        bankStatementPin = findViewById(R.id.etBankStatementPin);

        tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        btSubmitDetails = findViewById(R.id.btSubmitDetails);

        tvUploadStatement.setOnClickListener(this);
        tvUploadSlip1.setOnClickListener(this);
        tvUploadSlip2.setOnClickListener(this);
        tvUploadSlip3.setOnClickListener(this);
        btSubmitDetails.setOnClickListener(this);




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

    private void dialogForDocs() {
        LayoutInflater inflater = LayoutInflater.from(UpdateDocument.this);
        View dialogView = inflater.inflate(R.layout.dialog_for_docs, new LinearLayout(UpdateDocument.this), false);
        final AlertDialog dialog = new AlertDialog.Builder(UpdateDocument.this)
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

                    if (ContextCompat.checkSelfPermission(UpdateDocument.this, Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_DENIED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            ActivityCompat.requestPermissions(UpdateDocument.this, new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                        }

                    }else {
                        startCamera();
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

                    Intent intent = new Intent(UpdateDocument.this, NormalFilePickActivity.class);
                    intent.putExtra(Constant.MAX_NUMBER, 1);
                    intent.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"pdf"});
                    startActivityForResult(intent, Constant.REQUEST_CODE_PICK_FILE);
                    dialog.dismiss();

                }
            });


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



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_CANCELED) {
            Log.i("switch", "=" + nFlagDoc);
            switch (nFlagDoc) {

                case 1:


                    if (data != null) {
                        ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                        if (list.size() != 0) {
                            String path = list.get(0).getPath();
                            fileBankStatmnt = new File(list.get(0).getPath());
                            tvUploadStatement.setText(path.substring(path.lastIndexOf("/") + 1, path.length()));
                            tvUploadStatement.setTextColor(getResources().getColor(R.color.textGreen));
                        }
                    }
                    break;

                case 2:
                    if (requestCode == REQUEST_IMAGE_CAPTURE) {
                        Bundle extras = data.getExtras();
                        if (resultCode == RESULT_OK && extras != null) {
                            Bitmap imageBitmap = null;
                            imageBitmap = (Bitmap) extras.get("data");
                           /* Glide.with(this).clear(ivSalSlip1);
                            ivSalSlip1.setImageBitmap(imageBitmap);*/
                            ///////
                            fileSalSlip1 = new File(UpdateDocument.this.getCacheDir(), "IMSAL1_" + new Random().nextInt(500) + ".png");

                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                            byte[] bitmapdata = bos.toByteArray();

                            FileOutputStream fos = null;
                            try {
                                fileSalSlip1.createNewFile();
                                fos = new FileOutputStream(fileSalSlip1);
                                fos.write(bitmapdata);
                                fos.flush();
                                fos.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            String sFileNam = fileSalSlip1.getPath();
                            tvUploadSlip1.setText("Salary slip 1 : \n " + sFileNam.substring(sFileNam.lastIndexOf("/") + 1, sFileNam.length()));
                            tvUploadSlip1.setTextColor(getResources().getColor(R.color.textGreen));

                        } else {
                            Toast.makeText(this, "Not able to capture Image", Toast.LENGTH_SHORT).show();
                        }
                    } else if (requestCode == nRESULT_LOAD_IMAGE_PHOTO) {
                        InputStream is;
                        Uri uriEmpFront;
                        is = null;
                        if (data != null) {
                            uriEmpFront = data.getData();
                            if (uriEmpFront.getAuthority() != null) {

                                try {
                                    is = UpdateDocument.this.getContentResolver().openInputStream(uriEmpFront);
                                    Bitmap photo = BitmapFactory.decodeStream(is);
                                   /* Glide.with(this).clear(ivSalSlip1);
                                    ivSalSlip1.setImageBitmap(photo);*/
                                    fileSalSlip1 = new File(UpdateDocument.this.getCacheDir(), "IMSAL1_" + new Random().nextInt(500) + ".png");
                                    fileSalSlip1.createNewFile();

                                    Bitmap bitmap = photo;
                                    ByteArrayOutputStream bos = new ByteArrayOutputStream();

                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                                    byte[] bitmapdata = bos.toByteArray();

                                    FileOutputStream fos = new FileOutputStream(fileSalSlip1);
                                    fos.write(bitmapdata);
                                    fos.flush();
                                    fos.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                tvUploadSlip1.setText("Salary slip 1 : \n " + "Uploaded");
                                tvUploadSlip1.setTextColor(getResources().getColor(R.color.textGreen));
                            }
                        }
                    } else {
                       /* if (alFilePath.size() > 0)
                            alFilePath.remove(0);
                        alFilePath.addAll(0, data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                        String sFileNam = alFilePath.get(0).substring((alFilePath.get(0).lastIndexOf("/")) + 1, alFilePath.get(0).length());

                        fileSalSlip1 = new File(alFilePath.get(0));
                        tvUploadSlip1.setText("Salary slip 1 : \n " + sFileNam);
                        tvUploadSlip1.setTextColor(getResources().getColor(R.color.textGreen));*/

                        if (data != null) {
                            ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                            if (list.size() != 0) {
                                String path = list.get(0).getPath();
                                fileSalSlip1 = new File(list.get(0).getPath());
                                tvUploadSlip1.setText(path.substring(path.lastIndexOf("/") + 1, path.length()));
                                tvUploadSlip1.setTextColor(getResources().getColor(R.color.textGreen));
                            }
                        }

                    }
                    break;

                case 3:
                    if (requestCode == REQUEST_IMAGE_CAPTURE) {
                        Bundle extras = data.getExtras();
                        if (resultCode == RESULT_OK && extras != null) {
                            Bitmap imageBitmap = null;
                            imageBitmap = (Bitmap) extras.get("data");
                           /* Glide.with(this).clear(ivSalSlip1);
                            ivSalSlip1.setImageBitmap(imageBitmap);*/
                            ///////
                            fileSalSlip2 = new File(UpdateDocument.this.getCacheDir(), "IMSAL2_" + new Random().nextInt(500) + ".png");

                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                            byte[] bitmapdata = bos.toByteArray();

                            FileOutputStream fos = null;
                            try {
                                fileSalSlip2.createNewFile();
                                fos = new FileOutputStream(fileSalSlip2);
                                fos.write(bitmapdata);
                                fos.flush();
                                fos.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            String sFileNam = fileSalSlip2.getPath();
                            tvUploadSlip2.setText("Salary slip 2 : \n " + sFileNam.substring(sFileNam.lastIndexOf("/") + 1, sFileNam.length()));
                            tvUploadSlip2.setTextColor(getResources().getColor(R.color.textGreen));

                        } else {
                            Toast.makeText(this, "Not able to capture Image", Toast.LENGTH_SHORT).show();
                        }
                    } else if (requestCode == nRESULT_LOAD_IMAGE_PHOTO) {
                        InputStream is;
                        Uri uriEmpFront;
                        is = null;
                        if (data != null) {
                            uriEmpFront = data.getData();
                            if (uriEmpFront.getAuthority() != null) {
                                try {
                                    is = UpdateDocument.this.getContentResolver().openInputStream(uriEmpFront);
                                    Bitmap photo = BitmapFactory.decodeStream(is);
                                   /* Glide.with(this).clear(ivSalSlip1);
                                    ivSalSlip1.setImageBitmap(photo);*/
                                    fileSalSlip2 = new File(UpdateDocument.this.getCacheDir(), "IMSAL2_" + new Random().nextInt(500) + ".png");
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
                                tvUploadSlip2.setTextColor(getResources().getColor(R.color.textGreen));
                            }
                        }
                    } else {

                        /*if (alFilePath.size() > 0)
                            alFilePath.remove(0);
                        alFilePath.addAll(0, data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                        String sFileNam = alFilePath.get(0).substring((alFilePath.get(0).lastIndexOf("/")) + 1, alFilePath.get(0).length());

                        fileSalSlip2 = new File(alFilePath.get(0));
                        tvUploadSlip2.setText("Salary slip 2 : \n " + sFileNam);
                        tvUploadSlip2.setTextColor(getResources().getColor(R.color.textGreen));*/

                        if (data != null) {
                            ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                            if (list.size() != 0) {
                                String path = list.get(0).getPath();
                                fileSalSlip2 = new File(list.get(0).getPath());
                                tvUploadSlip2.setText(path.substring(path.lastIndexOf("/") + 1, path.length()));
                                tvUploadSlip2.setTextColor(getResources().getColor(R.color.textGreen));
                            }
                        }

                    }
                    break;

                case 4:
                    if (requestCode == REQUEST_IMAGE_CAPTURE) {
                        Bundle extras = data.getExtras();
                        if (resultCode == RESULT_OK && extras != null) {
                            Bitmap imageBitmap = null;
                            imageBitmap = (Bitmap) extras.get("data");
                           /* Glide.with(this).clear(ivSalSlip1);
                            ivSalSlip1.setImageBitmap(imageBitmap);*/
                            ///////
                            fileSalSlip3 = new File(UpdateDocument.this.getCacheDir(), "IMSAL3_" + new Random().nextInt(500) + ".png");

                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                            byte[] bitmapdata = bos.toByteArray();

                            FileOutputStream fos = null;
                            try {
                                fileSalSlip3.createNewFile();
                                fos = new FileOutputStream(fileSalSlip3);
                                fos.write(bitmapdata);
                                fos.flush();
                                fos.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            String sFileNam = fileSalSlip3.getPath();
                            tvUploadSlip3.setText("Salary slip 3 : \n " + sFileNam.substring(sFileNam.lastIndexOf("/") + 1, sFileNam.length()));
                            tvUploadSlip3.setTextColor(getResources().getColor(R.color.textGreen));

                        } else {
                            Toast.makeText(this, "Not able to capture Image", Toast.LENGTH_SHORT).show();
                        }
                    } else if (requestCode == nRESULT_LOAD_IMAGE_PHOTO) {
                        InputStream is;
                        Uri uriEmpFront;
                        is = null;
                        if (data != null) {
                            uriEmpFront = data.getData();
                            if (uriEmpFront.getAuthority() != null) {
                                try {
                                    is = UpdateDocument.this.getContentResolver().openInputStream(uriEmpFront);
                                    Bitmap photo = BitmapFactory.decodeStream(is);
                                   /* Glide.with(this).clear(ivSalSlip1);
                                    ivSalSlip1.setImageBitmap(photo);*/
                                    fileSalSlip3 = new File(UpdateDocument.this.getCacheDir(), "IMSAL3_" + new Random().nextInt(500) + ".png");
                                    fileSalSlip3.createNewFile();

                                    Bitmap bitmap = photo;
                                    ByteArrayOutputStream bos = new ByteArrayOutputStream();

                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                                    byte[] bitmapdata = bos.toByteArray();

                                    FileOutputStream fos = new FileOutputStream(fileSalSlip3);
                                    fos.write(bitmapdata);
                                    fos.flush();
                                    fos.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                tvUploadSlip3.setText("Salary slip 3 : \n " + "Uploaded");
                                tvUploadSlip3.setTextColor(getResources().getColor(R.color.textGreen));
                            }
                        }
                    } else {

                        if (data != null) {
                            ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                            if (list.size() != 0) {
                                String path = list.get(0).getPath();
                                fileSalSlip3 = new File(list.get(0).getPath());
                                tvUploadSlip3.setText(path.substring(path.lastIndexOf("/") + 1, path.length()));
                                tvUploadSlip3.setTextColor(getResources().getColor(R.color.textGreen));
                            }
                        }

                    }
                    break;
            }

        }

    }





    @SuppressLint("StaticFieldLeak")
    private class uploadUserSalaryFiles extends AsyncTask<Void, Integer, String> {

        ProgressDialog progressBarAsn = new ProgressDialog(UpdateDocument.this, R.style.AppCompatProgressDialogStyle);
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
                    Toast.makeText(UpdateDocument.this, jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(UpdateDocument.this, HomeActivity.class);
                    startActivity(intent);
                    finish();

                }
                else
                {
                    Toast.makeText(UpdateDocument.this, jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
                }
            }



            super.onPostExecute(result);



        }

    }




    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.tvUploadStatmnt:

                nFlagDoc = 1;
                Intent intent4 = new Intent(UpdateDocument.this, NormalFilePickActivity.class);
                intent4.putExtra(Constant.MAX_NUMBER, 1);
                intent4.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"pdf"});
                startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);

                break;

            case R.id.tvUploadSlip1:

                nFlagDoc = 2;
                dialogForDocs();
                break;

            case R.id.tvUploadSlip2:
                nFlagDoc = 3;
                dialogForDocs();
                break;

            case R.id.tvUploadSlip3:
                nFlagDoc = 4;
                dialogForDocs();
                break;

            case R.id.btSubmitDetails:

                if (new ConnectionCheck(UpdateDocument.this).isNetworkAvailable()) {

                    if (allValidated()){

                        getBankStatementPin = bankStatementPin.getText().toString().trim();

                        new uploadUserSalaryFiles().execute();
                    }

                } else {
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please Check Your Internet Connection", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }

                break;
        }
    }
}
