package com.loaneasy.NewAuthentication;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.irozon.sneaker.Sneaker;
import com.loaneasy.HomeActivity;
import com.loaneasy.R;
import com.loaneasy.ViewPresenter.ModalRepo.LoginRepo;
import com.loaneasy.ViewPresenter.ModalRepo.OTPVerifyRepo;
import com.loaneasy.ViewPresenter.ModelReq.LoginRequest;
import com.loaneasy.ViewPresenter.OTPVerifiyPresenter;
import com.loaneasy.new_user_details.UserProfile;
import com.loaneasy.utils.UserSharedPreference;
import com.rjesture.startupkit.AppTools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;

public class VerfiyOtpActivity extends AppCompatActivity implements OTPVerifiyPresenter.UserLoginView {


    TextView getOtpCall, btSubmitOtp;
    EditText etPin;
    Intent intent;
    String Number, key;
    OTPVerifiyPresenter presenter;
    UserSharedPreference sharedPreference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        getOtpCall = findViewById(R.id.getOtpCall);
        getOtpCall.setVisibility(View.INVISIBLE);
        btSubmitOtp = findViewById(R.id.btSubmitOtp);
        etPin = findViewById(R.id.etPin);
        presenter = new OTPVerifiyPresenter(this);
        sharedPreference = new UserSharedPreference(this);
        intent = getIntent();
        if (intent != null) {
            Number = intent.getStringExtra("number");
            key = intent.getStringExtra("key");
            Log.e("keyyyy", Number + key);


        }

        btSubmitOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etPin.getText().toString().trim().length() != 5) {
                    Sneaker.with(VerfiyOtpActivity.this)
                            .setTitle("Enter a Valid OTP")
                            .setMessage("")
                            .sneakError();
                } else {

                    if (key.equalsIgnoreCase("Signup")) {
                        presenter.UserRegisterverifyOTP(VerfiyOtpActivity.this, Number, etPin.getText().toString());

                    } else if (key.equalsIgnoreCase("Signupp")) {
                        presenter.UserRegisterverifyOTP(VerfiyOtpActivity.this, Number, etPin.getText().toString());

                    } else {

                        presenter.ForgetPasswordSendOTP(VerfiyOtpActivity.this, Number, etPin.getText().toString());

                    }

                }

            }
        });


    }

    @Override
    public void onUserLoginError(String message) {
        Sneaker.with(this)
                .setTitle(message)
                .setMessage("")
                .sneakError();
    }

    @Override
    public void onUserLoginSuccess(OTPVerifyRepo response, String message) {
        Log.e("responseee", response.getResult().toString());


        if (message.equalsIgnoreCase("ok")) {
            if (response.getStatus() == true) {

               /* if (key.equalsIgnoreCase("Signup")) {


                    Toast.makeText(VerfiyOtpActivity.this, response.getMsg() + "", Toast.LENGTH_SHORT).show();

                    Intent in = new Intent(VerfiyOtpActivity.this, SignUpActivity.class);
                    in.putExtra("number", Number);

                    startActivity(in);
                    finish();

                } else */
                if (key.equalsIgnoreCase("Forget")) {

                    Toast.makeText(VerfiyOtpActivity.this, response.getMsg() + "", Toast.LENGTH_SHORT).show();

                    Intent in = new Intent(VerfiyOtpActivity.this, UpdatePasswordActivity.class);
                    in.putExtra("number", Number);

                    startActivity(in);
                    finish();
                } else if (key.equalsIgnoreCase("LoginWithOTP")) {


                    if (response.getResult().getProfileCompleted().equals("1")) {


                        sharedPreference.setSignFlag(3);
                        sharedPreference.setUserId(response.getResult().getUserId());

                        sharedPreference.setUserPhoneNo(response.getResult().getPhoneNo());
                        //    sharedPreference.setLocation(response.getResult().getLocation());
                        sharedPreference.setUserSocialName("" + response.getResult().getFullName());

                        // sharedPreference.setUserCity(response.getResult().getLocation());
                        sharedPreference.setUserEmail(response.getResult().getEmail());

                      /*  Intent in = new Intent(VerfiyOtpActivity.this, SocialMediaLoginn.class);
                        startActivity(in);
                        finish();*/


                        Intent in = new Intent(VerfiyOtpActivity.this, HomeActivity.class);
                        startActivity(in);
                        finish();


                     /*   String profilePic = "";
                        if (!response.getResult()..optString("profile_pic").isEmpty())
                            sharedPreference.setProfilePic(result.optString("profile_pic"));
                        else
                            sharedPreference.setProfilePic(result.optString("social_profile_pic"));
                */

                    } else {


                        sharedPreference.setSignFlag(2);
                        sharedPreference.setUserSocialName(response.getResult().getFullName());
                        sharedPreference.setUserId(response.getResult().getUserId());
                        sharedPreference.setUserPhoneNo(response.getResult().getPhoneNo());
                        //sharedPreference.setLocation(response.getResult().getLocation());
                        sharedPreference.setUserEmail(response.getResult().getEmail());
                        //  Log.d("TAG111", "uid---" + sharedPreference.getUserId());
//                        Toast.makeText(VerfiyOtpActivity.this, response.getMsg() + "", Toast.LENGTH_SHORT).show();


                     /*   Intent in = new Intent(VerfiyOtpActivity.this, SocialMediaLoginn.class);
                        startActivity(in);
                        finish();
*/

                        Intent in = new Intent(VerfiyOtpActivity.this, UserProfile.class);
                        startActivity(in);
                        finish();
                    }




                    /*


                    Toast.makeText(VerfiyOtpActivity.this, response.getMsg() + "", Toast.LENGTH_SHORT).show();

                    Intent in = new Intent(VerfiyOtpActivity.this, HomeActivity.class);
                    startActivity(in);

//                    sharedPreference.setExistCustomerFlag(1);
                    sharedPreference.setSignFlag(3);

                    sharedPreference.setUserSocialName(response.getResult().getFullName());
                    sharedPreference.setUserId(response.getResult().getUserId());
                    sharedPreference.setUserPhoneNo(response.getResult().getPhoneNo());
                    //sharedPreference.setLocation(response.getResult().getLocation());
                    sharedPreference.setUserEmail(response.getResult().getEmail());
                    Log.d("TAG111","uid---"+sharedPreference.getUserId());


                    finish();
*/
                }


//                Toast.makeText(LoginActivity.this, response.getResult().getUserId()+"", Toast.LENGTH_SHORT).show();

            } else {


                Sneaker.with(this)
                        .setTitle("Enter correct OTP")
                        .setMessage("")
                        .sneakError();


                //              Toast.makeText(LoginActivity.this, response.getStatus()+"", Toast.LENGTH_SHORT).show();
            }
        }


    }

    @Override
    public void onUserLoginSuccess(ResponseBody response, String message) {
        if (message.equalsIgnoreCase("ok")) {

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(response.string());
                if (jsonObject.getBoolean("status") == true) {

                    if (key.equalsIgnoreCase("Signupp")) {
                        Intent in = new Intent(VerfiyOtpActivity.this, SocialMediaLoginn.class);

                        in.putExtra("number", Number);
                        startActivity(in);
                        finish();


                        //presenter.UserRegisterverifyOTP(VerfiyOtpActivity.this, Number, etPin.getText().toString());

                    } else {

                        Intent in = new Intent(VerfiyOtpActivity.this, SignUpActivity.class);
                        in.putExtra("number", Number);
                        startActivity(in);
                        finish();
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ;
        }

    }

    @Override
    public void showHideProgress(boolean isShow) {
        if (isShow) {
            AppTools.showRequestDialog(this);


        } else {
            AppTools.hideDialog();

        }
    }

    @Override
    public void onUserLoginFailure(Throwable t) {
        Sneaker.with(this)
                .setTitle(t.getLocalizedMessage())
                .setMessage("")
                .sneakError();
    }


}