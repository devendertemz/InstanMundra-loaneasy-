package com.loaneasy.user_details_new;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.loaneasy.HomeActivity;
import com.loaneasy.R;
import com.loaneasy.utils.ConnectionCheck;
import com.loaneasy.utils.UserSharedPreference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;



public class BankDetailsNewActivity extends AppCompatActivity {

    public final int RC_FILE_PICKER_PERM = 321;
    //AppCompatSpinner spAccType, spBankName;
    TextView tvUploadStatmnt, tvUploadSlip1, tvUploadSlip2, tvUploadSlip3, tv_toolbar_title, tvSkip, tvSubmitDetails/*, tvTotalAmount, tvHandlingCharge, tvTotalInterest, tvLoanAmount*/;
    EditText etBankName, etAccnNo, etIfsc;
    View viewBanknameline;
    String getAccType, getBankName;
    int nFlagDoc;
    ArrayList<String> alFilePathStatmnt;
    ArrayList<String> alFilePathSalSlip;
    UserSharedPreference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_details_new);

        sharedPreference = new UserSharedPreference(BankDetailsNewActivity.this);
        alFilePathStatmnt = new ArrayList<>();
        alFilePathSalSlip = new ArrayList<>();
        /*spAccType = findViewById(R.id.spAccType);
        spBankName = findViewById(R.id.spBankName);*/
        tvUploadStatmnt = findViewById(R.id.tvUploadStatmnt);
        tvUploadSlip1 = findViewById(R.id.tvUploadSlip1);
        tvUploadSlip2 = findViewById(R.id.tvUploadSlip2);
        tvUploadSlip3 = findViewById(R.id.tvUploadSlip3);
        tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        tvSkip = findViewById(R.id.tvSkip);
        etBankName = findViewById(R.id.etBankName);
        /*etAccnNo = findViewById(R.id.etAccnNo);
        etIfsc = findViewById(R.id.etIfsc);*/
        tvSubmitDetails = findViewById(R.id.btSubmitDetails);
        viewBanknameline = findViewById(R.id.viewBanknameline);

        String sStatement = sharedPreference.getBankStatPath();
        String sSlip1 = sharedPreference.getSalSlip1Path();
        String sSlip2 = sharedPreference.getSalSlip2Path();
        String sSlip3 = sharedPreference.getSalSlip3Path();

        if (!sStatement.isEmpty()){
           if (new File(sStatement).exists()){

               alFilePathStatmnt.add(sStatement);
               String sFileName = sStatement.substring((sStatement.lastIndexOf("/")) + 1, sStatement.length());
               tvUploadStatmnt.setText("Bank statement : \n" + sFileName);
               tvUploadStatmnt.setTextColor(getResources().getColor(R.color.dot_dark_screen3));
           }
       }
       if (!sSlip1.isEmpty()){
           if (new File(sSlip1).exists()){

               alFilePathSalSlip.add(sSlip1);
               String sFileName = sSlip1.substring((sSlip1.lastIndexOf("/")) + 1, sSlip1.length());
               tvUploadSlip1.setText("Bank slip : \n" + sFileName);
               tvUploadSlip1.setTextColor(getResources().getColor(R.color.dot_dark_screen3));
           }
       }

       if (!sSlip2.isEmpty()){
           if (new File(sSlip2).exists()){

               alFilePathSalSlip.add(sSlip2);
               String sFileName = sSlip2.substring((sSlip2.lastIndexOf("/")) + 1, sSlip2.length());
               tvUploadSlip2.setText("Bank slip : \n" + sFileName);
               tvUploadSlip2.setTextColor(getResources().getColor(R.color.dot_dark_screen3));
           }
       }

       if (!sSlip3.isEmpty()){
           if (new File(sSlip3).exists()){

               alFilePathSalSlip.add(sSlip3);
               String sFileName = sSlip3.substring((sSlip3.lastIndexOf("/")) + 1, sSlip3.length());
               tvUploadSlip3.setText("Bank slip : \n" + sFileName);
               tvUploadSlip3.setTextColor(getResources().getColor(R.color.dot_dark_screen3));
           }
       }

       if (!sharedPreference.getAccnNo().isEmpty()){
           etAccnNo.setText(sharedPreference.getAccnNo());
       }
       if (!sharedPreference.getIfsc().isEmpty()){
           etIfsc.setText(sharedPreference.getIfsc());
       }



        /*   tvLoanAmount.setText(formatter.format(Integer.parseInt(*//*loanAmount*//*"15000")));
        tvTotalInterest.setText(""+formatter.format(Integer.parseInt(*//*totalInterest*//*"1000")));
        tvHandlingCharge.setText("200");

        tvTotalAmount.setText(""+formatter.format((Integer.parseInt(loanAmount)+Integer.parseInt(totalInterest)+ 200)));*/

        //etAccnNo.setText(sharedPreference.getAccnNo());
        //etIfsc.setText(sharedPreference.getIfsc());

       /* ArrayAdapter<CharSequence> accTypeAdapter = new ArrayAdapter<CharSequence>(BankDetailsNewActivity.this, R.layout.spinner_text, getResources()
                .getStringArray(R.array.sarAccountType));
        accTypeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spAccType.setAdapter(accTypeAdapter);

        spAccType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                getAccType = spAccType.getSelectedItem().toString();
                sharedPreference.setAccType(spAccType.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<CharSequence> bankNameAdapter = new ArrayAdapter<CharSequence>(BankDetailsNewActivity.this, R.layout.spinner_text, getResources().getStringArray(R.array.sarBankNames));
        bankNameAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spBankName.setAdapter(bankNameAdapter);

        spBankName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (spBankName.getSelectedItem().toString().equalsIgnoreCase("others")) {
                    etBankName.setVisibility(View.VISIBLE);
                    viewBanknameline.setVisibility(View.VISIBLE);
                } else {
                    if (etBankName.getVisibility() == View.VISIBLE) {
                        etBankName.setVisibility(View.GONE);
                        viewBanknameline.setVisibility(View.GONE);
                        getBankName = spBankName.getSelectedItem().toString();
                    } else
                        getBankName = spBankName.getSelectedItem().toString();
                }
                sharedPreference.setBankName(spBankName.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/

        tvUploadStatmnt.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                /*if (ActivityCompat.checkSelfPermission(BankDetailsNewActivity.this, FilePickerConst.PERMISSIONS_FILE_PICKER) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{FilePickerConst.PERMISSIONS_FILE_PICKER}, RC_FILE_PICKER_PERM);

                } else {
                    nFlagDoc = 1;
                    FilePickerBuilder.getInstance().enableImagePicker(false)
                            .setMaxCount(1)
                            .setSelectedFiles(alFilePathStatmnt)
                            .setActivityTheme(R.style.AppTheme)
                            .pickFile(BankDetailsNewActivity.this);
                }*/
            }
        });

        tvUploadSlip1.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                /*if (ActivityCompat.checkSelfPermission(BankDetailsNewActivity.this, FilePickerConst.PERMISSIONS_FILE_PICKER) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{FilePickerConst.PERMISSIONS_FILE_PICKER}, RC_FILE_PICKER_PERM);

                } else {
                    nFlagDoc = 2;
                    FilePickerBuilder.getInstance().enableImagePicker(false)
                            .setMaxCount(1)
                            .setSelectedFiles(alFilePathSalSlip)
                            .setActivityTheme(R.style.AppTheme)
                            .pickFile(BankDetailsNewActivity.this);
                }*/
            }
        });
        tvUploadSlip2.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
               /* if (ActivityCompat.checkSelfPermission(BankDetailsNewActivity.this, FilePickerConst.PERMISSIONS_FILE_PICKER) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{FilePickerConst.PERMISSIONS_FILE_PICKER}, RC_FILE_PICKER_PERM);

                } else {
                    nFlagDoc = 3;
                    FilePickerBuilder.getInstance().enableImagePicker(false)
                            .setMaxCount(1)
                            .setSelectedFiles(alFilePathSalSlip)
                            .setActivityTheme(R.style.AppTheme)
                            .pickFile(BankDetailsNewActivity.this);
                }*/
            }
        });

        tvUploadSlip3.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
               /* if (ActivityCompat.checkSelfPermission(BankDetailsNewActivity.this, FilePickerConst.PERMISSIONS_FILE_PICKER) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{FilePickerConst.PERMISSIONS_FILE_PICKER}, RC_FILE_PICKER_PERM);

                } else {
                    nFlagDoc = 4;
                    FilePickerBuilder.getInstance().enableImagePicker(false)
                            .setMaxCount(1)
                            .setSelectedFiles(alFilePathSalSlip)
                            .setActivityTheme(R.style.AppTheme)
                            .pickFile(BankDetailsNewActivity.this);
                }*/
            }
        });

        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BankDetailsNewActivity.this, HomeActivity.class));
                finish();
            }
        });

        tvSubmitDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new ConnectionCheck(BankDetailsNewActivity.this).isNetworkAvailable()) {

                   // String sAccType = spAccType.getSelectedItem().toString();
               /* String sBankName = "";

                if (!spBankName.getSelectedItem().toString().equalsIgnoreCase("others")){
                    sBankName = spBankName.getSelectedItem().toString();
                }
                else*/
                    //String sBankName = spBankName.getSelectedItem().toString();
                   /* String sAccNo = etAccnNo.getText().toString().trim();
                    String sIfsc = etIfsc.getText().toString().trim();*/

                   /* if (alFilePath.size() == 2) {
                        typedFileBankStatmnt = new TypedFile("multipart/form-data", new File(alFilePath.get(0)));
                        typedFileSalarySlip = new TypedFile("multipart/form-data", new File(alFilePath.get(1)));
                    }*/

                    //      sendDataApplyLoan(sAccType, sBankName);
                    if (allValidated()) {
                        Intent intent = new Intent(BankDetailsNewActivity.this, HomeActivity.class);
                        //intent.putExtra("bank_name", sBankName);
                        //intent.putExtra("acc_type", sAccType);
                        //intent.putExtra("acc_num", sAccNo);
                        //intent.putExtra("ifsc", sIfsc);
                        intent.putExtra("bank_statement", alFilePathStatmnt.get(0));
                        intent.putExtra("salary_slip1", alFilePathSalSlip.get(0));
                        intent.putExtra("salary_slip2", alFilePathSalSlip.get(1));
                        intent.putExtra("salary_slip3", alFilePathSalSlip.get(2));
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please Check Your Internet Connection", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }

            }
        });

       /* etAccnNo.addTextChangedListener(new MyTextWatcher(etAccnNo));
        etIfsc.addTextChangedListener(new MyTextWatcher(etIfsc));*/

       /* if (sharedPreference.getAccType() != 0){
            spAccType.setSelection(sharedPreference.getAccType());
        }
        if (sharedPreference.getBankName() != 0){
            spBankName.setSelection(sharedPreference.getBankName());
        }*/
    }

    private boolean allValidated() {

        /*if (spAccType.getSelectedItemPosition() == 0) {
            Toast.makeText(BankDetailsNewActivity.this, "Please select bank account type", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (spBankName.getSelectedItemPosition() == 0) {
            Toast.makeText(BankDetailsNewActivity.this, "Please select bank name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (sAccNo.isEmpty()) {
            Toast.makeText(BankDetailsNewActivity.this, "Please enter bank account number", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (sIfsc.isEmpty()) {
            Toast.makeText(BankDetailsNewActivity.this, "Please enter your bank IFSC number", Toast.LENGTH_SHORT).show();
            return false;
        }
        */

        if (alFilePathStatmnt.isEmpty()) {
            Toast.makeText(BankDetailsNewActivity.this, "Please attach bank statement", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (alFilePathSalSlip.size() != 3) {
            Toast.makeText(BankDetailsNewActivity.this, "Please attach your current salary slips", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void copyFile(File src, String fileName) throws IOException {

        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File myDir = new File(root + "/IM");
        myDir.mkdirs();
        String fname = "Image-" + fileName;
        File dst = new File(myDir, fname);

        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
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

                /*case R.id.etAccnNo:
                    sharedPreference.setAccnNo(etAccnNo.getText().toString().trim());
                    break;

                case R.id.etIfsc:
                    sharedPreference.setIfsc(etIfsc.getText().toString().trim());
                    break;*/


            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            /*case FilePickerConst.REQUEST_CODE_DOC:
                if (resultCode == Activity.RESULT_OK && data != null) {

                    if (nFlagDoc == 1) {
                        if (alFilePathStatmnt.size() > 0)
                            alFilePathStatmnt.clear();

                        String sStatement = String.valueOf(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS)).replace("[", "").replace("]", "");
                        alFilePathStatmnt.add(sStatement);
                        String sFileName = sStatement.substring((sStatement.lastIndexOf("/")) + 1, sStatement.length());
                        tvUploadStatmnt.setText("Bank statement : \n" + sFileName);
                        tvUploadStatmnt.setTextColor(getResources().getColor(R.color.dot_dark_screen3));

                        sharedPreference.setBankStatPath(sStatement);
                    } else if (nFlagDoc == 2) {

                        for (int i = 0; i < alFilePathSalSlip.size(); i++) {
                            if (alFilePathSalSlip.get(i).contains(tvUploadSlip1.getText().toString().replace("Salary slip : \n", ""))) {
                                alFilePathSalSlip.remove(i);
                            }
                        }
                        String sSlip1 = String.valueOf(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS)).replace("[", "").replace("]", "");
                        alFilePathSalSlip.add(sSlip1);
                        String sFileName = sSlip1.substring((sSlip1.lastIndexOf("/")) + 1, sSlip1.length());
                        tvUploadSlip1.setText("Salary slip : \n" + sFileName);
                        tvUploadSlip1.setTextColor(getResources().getColor(R.color.dot_dark_screen3));

                        sharedPreference.setSalSlip1Path(sSlip1);
                    } else if (nFlagDoc == 3) {

                        for (int i = 0; i < alFilePathSalSlip.size(); i++) {
                            if (alFilePathSalSlip.get(i).contains(tvUploadSlip2.getText().toString().replace("Salary slip : \n", ""))) {
                                alFilePathSalSlip.remove(i);
                            }
                        }
                        String sSlip2 = String.valueOf(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS)).replace("[", "").replace("]", "");
                        alFilePathSalSlip.add(sSlip2);
                        String sFileName = sSlip2.substring((sSlip2.lastIndexOf("/")) + 1, sSlip2.length());
                        tvUploadSlip2.setText("Salary slip : \n" + sFileName);
                        tvUploadSlip2.setTextColor(getResources().getColor(R.color.dot_dark_screen3));

                        sharedPreference.setSalSlip2Path(sSlip2);
                    } else if (nFlagDoc == 4) {

                        for (int i=0; i<alFilePathSalSlip.size(); i++){
                            if (alFilePathSalSlip.get(i).contains(tvUploadSlip3.getText().toString().replace("Salary slip : \n",""))){
                                alFilePathSalSlip.remove(i);
                            }
                        }
                        String sSlip3 = String.valueOf(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS)).replace("[", "").replace("]", "");
                        alFilePathSalSlip.add(sSlip3);
                        String sFileName = sSlip3.substring((sSlip3.lastIndexOf("/")) + 1, sSlip3.length());
                        tvUploadSlip3.setText("Salary slip : \n" + sFileName);
                        tvUploadSlip3.setTextColor(getResources().getColor(R.color.dot_dark_screen3));

                        sharedPreference.setSalSlip3Path(sSlip3);
                    }
                    Log.d("TAG111", "-alFilePath->" + alFilePathSalSlip.toString());

                }
                break;*/
        }
    }
}
