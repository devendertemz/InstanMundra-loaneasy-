package com.loaneasy.user_details;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;


import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.loaneasy.HomeActivity;
import com.loaneasy.OpenFrontCamera;
import com.loaneasy.R;
import com.loaneasy.utils.ConnectionCheck;
import com.loaneasy.utils.UserSharedPreference;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit.mime.TypedFile;

public class BankDetailsFragment extends Fragment {

    public final int RC_FILE_PICKER_PERM = 321;
    private final static int CAMERA_PIC_REQUEST1 = 0;
    AppCompatSpinner spAccType, spBankName;
    TextView tvUploadStatmnt, tvUploadSlip, tv_toolbar_title/*, tvTotalAmount, tvHandlingCharge, tvTotalInterest, tvLoanAmount*/;
    EditText etBankName;
    Button btSubmitDetails;
    View viewBanknameline;
    String getAccType, getBankName;
    int nFlagDoc;
    ArrayList<String> alFilePath = new ArrayList<>();
    TypedFile typedFileBankStatmnt, typedFileSalarySlip;
    Toolbar mToolbar;
    UserSharedPreference sharedPreference;
    String loanAmount, returnDays, returnDate, totalInterest;

    public BankDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_bank_details, container, false);
        sharedPreference = new UserSharedPreference(getActivity());
        spAccType = view.findViewById(R.id.spAccType);
        spBankName = view.findViewById(R.id.spBankName);
        tvUploadStatmnt = view.findViewById(R.id.tvUploadStatmnt);
        tvUploadSlip = view.findViewById(R.id.tvUploadSlip);
        tv_toolbar_title = view.findViewById(R.id.tv_toolbar_title);
        etBankName = view.findViewById(R.id.etBankName);
        btSubmitDetails = view.findViewById(R.id.btSubmitDetails);
        viewBanknameline = view.findViewById(R.id.viewBanknameline);
       /* tvTotalAmount = view.findViewById(R.id.tvTotalAmount);
        tvHandlingCharge = view.findViewById(R.id.tvHandlingCharge);
        tvTotalInterest = view.findViewById(R.id.tvTotalInterest);
        tvLoanAmount = view.findViewById(R.id.tvLoanAmount);*/

        DecimalFormat formatter = new DecimalFormat("#,##,###");
        UserDetailsActivity activity = (UserDetailsActivity) getActivity();
        activity.getStatus(3);

     /*   tvLoanAmount.setText(formatter.format(Integer.parseInt(*//*loanAmount*//*"15000")));
        tvTotalInterest.setText(""+formatter.format(Integer.parseInt(*//*totalInterest*//*"1000")));
        tvHandlingCharge.setText("200");

        tvTotalAmount.setText(""+formatter.format((Integer.parseInt(loanAmount)+Integer.parseInt(totalInterest)+ 200)));*/

        ArrayAdapter<CharSequence> accTypeAdapter = new ArrayAdapter<CharSequence>(getActivity(), R.layout.spinner_text, getResources()
                .getStringArray(R.array.sarAccountType));
        accTypeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spAccType.setAdapter(accTypeAdapter);

        spAccType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                getAccType = spAccType.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<CharSequence> bankNameAdapter = new ArrayAdapter<CharSequence>(getActivity(), R.layout.spinner_text, getResources().getStringArray(R.array.sarBankNames));
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        tvUploadStatmnt.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
               /* if (ActivityCompat.checkSelfPermission(getActivity(), FilePickerConst.PERMISSIONS_FILE_PICKER) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{FilePickerConst.PERMISSIONS_FILE_PICKER}, RC_FILE_PICKER_PERM);

                } else {
                    nFlagDoc = 1;
                    FilePickerBuilder.getInstance().enableImagePicker(false)
                            .setMaxCount(1)
                            .setSelectedFiles(alFilePath)
                            .setActivityTheme(R.style.AppTheme)
                            .pickFile(getActivity());
                }*/
            }
        });

        tvUploadSlip.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
               /* if (ActivityCompat.checkSelfPermission(getActivity(), FilePickerConst.PERMISSIONS_FILE_PICKER) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{FilePickerConst.PERMISSIONS_FILE_PICKER}, RC_FILE_PICKER_PERM);

                } else {
                    if (!alFilePath.isEmpty()) {
                        nFlagDoc = 2;
                        FilePickerBuilder.getInstance().enableImagePicker(false)
                                .setMaxCount(1)
                                .setSelectedFiles(alFilePath)
                                .setActivityTheme(R.style.AppTheme)
                                .pickFile(getActivity());
                    }
                    else
                        Toast.makeText(getActivity(), "please attach bank statement first", Toast.LENGTH_SHORT).show();
                }*/
            }
        });

        btSubmitDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new ConnectionCheck(getActivity()).isNetworkAvailable()) {

                /*String sAccType = spAccType.getSelectedItem().toString();
                String sBankName = "";

                if (!spBankName.getSelectedItem().toString().equalsIgnoreCase("others")){
                    sBankName = spBankName.getSelectedItem().toString();
                }
                else
                    sBankName = etBankName.getText().toString().trim();

                if (alFilePath.size() == 2) {
                    typedFileBankStatmnt = new TypedFile("multipart/form-data", new File(alFilePath.get(0)));
                    typedFileSalarySlip = new TypedFile("multipart/form-data", new File(alFilePath.get(1)));
                }
                if (allValidated(sAccType, sBankName))
                    sendDataApplyLoan(sAccType, sBankName);*/

                    startActivity(new Intent(getActivity(), HomeActivity.class));
                    getActivity().finish();

                }else{
                    Snackbar snackbar = Snackbar.make(view.findViewById(android.R.id.content), "Please Check Your Internet Connection", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }

            }
        });
        
        return view;
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
