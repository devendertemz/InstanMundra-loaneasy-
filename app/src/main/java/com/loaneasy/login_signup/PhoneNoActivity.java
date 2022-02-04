package com.loaneasy.login_signup;

import android.Manifest;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.material.snackbar.Snackbar;
import com.loaneasy.R;
import com.loaneasy.utils.ConnectionCheck;
import com.loaneasy.utils.UserSharedPreference;


public class PhoneNoActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private TextView submitPhoneNo, tvOk;
    private EditText etPhoneNumber;

    private int PERMISSION_ALL = 1;
    private String[] PERMISSIONS = { Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.READ_CONTACTS,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    protected  final int REQUEST_CHECK_SETTINGS = 0x1;
    private String TAG = "PhoneNoActivity", userArea="N.A.";
    UserSharedPreference sharedPreference;
    private AppCompatSpinner spCity;
    private Dialog dialogOffer;


    private final int RESOLVE_HINT = 1011;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreference = new UserSharedPreference(this);
        if (!hasPermissions(this, PERMISSIONS)) {

            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        setContentView(R.layout.activity_phoneno);

        displayLocationSettingsRequest(getApplicationContext());

        requestHint();




        spCity = (AppCompatSpinner) findViewById(R.id.spinnerLocation);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);

       /* InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etPhoneNumber, 0);*/


        submitPhoneNo = findViewById(R.id.btPhoneNoSubmit);

        submitPhoneNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /*if (spCity.getSelectedItemPosition() == 0)
                    Toast.makeText(PhoneNoActivity.this, "Please select your city", Toast.LENGTH_SHORT).show();
                else if (spCity.getSelectedItemPosition() != 5){

                    startActivity(new Intent(PhoneNoActivity.this, SocialMediaLogin.class));
                }*/

                if (new ConnectionCheck(getApplicationContext()).isNetworkAvailable())
                {
                    if (!hasPermissions(PhoneNoActivity.this, PERMISSIONS)) {

                        ActivityCompat.requestPermissions(PhoneNoActivity.this, PERMISSIONS, PERMISSION_ALL);
                    }
                    else
                    {
                            if(validate())
                            {

                                Intent intent = new Intent(getApplicationContext(), VerifyOtp.class);
                                Bundle bundle = new Bundle();
                                Log.i("area","="+userArea);
                                bundle.putString("phone_no", etPhoneNumber.getText().toString().trim());
                                bundle.putString("userArea", userArea);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();
                            }
                    }
                }
                else
                {
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please Check Your Internet Connection", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }

            }
        });


        ArrayAdapter<CharSequence> accTypeAdapter = new ArrayAdapter<CharSequence>(getApplicationContext(), R.layout.spinner_text, getResources()
                .getStringArray(R.array.sarYourCity));
        accTypeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spCity.setAdapter(accTypeAdapter);

        spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                userArea = spCity.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void requestHint() {

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.CREDENTIALS_API)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) PhoneNoActivity.this)
                .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) PhoneNoActivity.this)
                .build();
        googleApiClient.connect();


        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();

        PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(
                googleApiClient, hintRequest);
        try {
            startIntentSenderForResult(intent.getIntentSender(),
                    RESOLVE_HINT, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }


    private boolean validate()
    {


        if(etPhoneNumber.getText().toString().isEmpty())
        {
            Toast.makeText(this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (etPhoneNumber.getText().length() <10)
        {
            Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }



    private void showCityDialog() {
        dialogOffer = new Dialog(PhoneNoActivity.this);
        dialogOffer.setCancelable(false);
        dialogOffer.setContentView(R.layout.dialog_yourcity);
        dialogOffer.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvOk = dialogOffer.findViewById(R.id.tvOk);

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogOffer.dismiss();
                //System.exit(0);
            }
        });

        dialogOffer.show();
    }

    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
                        Log.i("rr", "------>");
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(PhoneNoActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i("rsr", "------>");
                        break;

                    case LocationSettingsStatusCodes.CANCELED:
                        Toast.makeText(PhoneNoActivity.this, "User Cancelled Location", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }


    private boolean hasPermissions(PhoneNoActivity context, String[] permissions) {

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                Log.i("number","="+credential.getId());

                String contactNumber = credential.getId();


                if(contactNumber.startsWith("+"))
                {
                    if(contactNumber.length()==13)
                    {
                        contactNumber=contactNumber.substring(3);
                        etPhoneNumber.setText(contactNumber);
                    }
                    else if(contactNumber.length()==14)
                    {
                        contactNumber=contactNumber.substring(4);
                        etPhoneNumber.setText(contactNumber);
                    }

                }


            }
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

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
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}
