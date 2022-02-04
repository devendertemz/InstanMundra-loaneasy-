package com.loaneasy.NewAuthentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.irozon.sneaker.Sneaker;
import com.loaneasy.R;
import com.loaneasy.ViewPresenter.ForgetPasswordPresenter;
import com.loaneasy.ViewPresenter.ModelReq.ForgetPasswordSendOTPRequest;
import com.rjesture.startupkit.AppTools;

import okhttp3.ResponseBody;


public class ForgetPasswordActivity extends AppCompatActivity implements ForgetPasswordPresenter.UserLoginView {

    TextView btPhoneNoSubmit, tvVerifyTextRed;
    EditText etPhoneNumber;

    ForgetPasswordPresenter presenter;
    String number;


    Intent intent;
    String key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        btPhoneNoSubmit = findViewById(R.id.btPhoneNoSubmit);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        tvVerifyTextRed = findViewById(R.id.tvVerifyTextRed);


        intent = getIntent();
        if (intent != null) {
            key = intent.getStringExtra("key");

            if (key.equalsIgnoreCase("Signup")) {
                tvVerifyTextRed.setText("Enter below details to  \n SignUp");


            } else if (key.equalsIgnoreCase("Forget")) {

            } else if (key.equalsIgnoreCase("LoginWithOTP")) {
                tvVerifyTextRed.setText("Enter below details to  \n Login with otp");

            }


        }

        presenter = new ForgetPasswordPresenter(this);


        btPhoneNoSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                number = etPhoneNumber.getText().toString().trim();


                if (number.length() != 10) {
                    Sneaker.with(ForgetPasswordActivity.this)
                            .setTitle("Enter a valid Number")
                            .setMessage("")
                            .sneakError();
                } else {


                    if (key.equalsIgnoreCase("Signup")) {

                        presenter.sendRegistrationOtp(ForgetPasswordActivity.this, new ForgetPasswordSendOTPRequest(number));


                    } else if (key.equalsIgnoreCase("Forget")) {
                        presenter.ForgetPasswordSendOTP(ForgetPasswordActivity.this, new ForgetPasswordSendOTPRequest(number));
                    } else if (key.equalsIgnoreCase("LoginWithOTP")) {

                        presenter.ForgetPasswordSendOTP(ForgetPasswordActivity.this, new ForgetPasswordSendOTPRequest(number));
                    }


                }



/*

                startActivity(new Intent(ForgetPasswordActivity.this,UpdatePasswordActivity.class));
                finish();
*/
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
    public void onUserLoginSuccess(ResponseBody response, String status, String msg, String message) {


        if (message.equalsIgnoreCase("ok")) {

            if (status.equalsIgnoreCase("true")) {


                if (key.equalsIgnoreCase("LoginWithOTP"))
                {

                    Toast.makeText(ForgetPasswordActivity.this, msg + "", Toast.LENGTH_SHORT).show();
                    Intent in = new Intent(ForgetPasswordActivity.this, VerfiyOtpActivity.class);

                    in.putExtra("key", key);
                    in.putExtra("number", number);
                    startActivity(in);
                }else {


                    Toast.makeText(ForgetPasswordActivity.this, msg + "", Toast.LENGTH_SHORT).show();
                    Intent in = new Intent(ForgetPasswordActivity.this, VerfiyOtpActivity.class);

                    in.putExtra("key", key);
                    in.putExtra("number", number);
                    startActivity(in);


                }
                        /*
                startActivity(new Intent(ForgetPasswordActivity.this,UpdatePasswordActivity.class));
                finish();*/


            } else {
                key="Signupp";
                presenter.sendRegistrationOtp(ForgetPasswordActivity.this, new ForgetPasswordSendOTPRequest(number));

                Sneaker.with(this)
                        .setTitle(msg)
                        .setMessage("")
                        .sneakError();

            }
        }
    }

    @Override
    public void sendRegistrationOtpSucess(ResponseBody response, String status, String msg, String messsage) {


        if (messsage.equalsIgnoreCase("ok")) {
            if (status.equalsIgnoreCase("true")) {

                Toast.makeText(ForgetPasswordActivity.this, msg + "", Toast.LENGTH_SHORT).show();
                Intent in = new Intent(ForgetPasswordActivity.this, VerfiyOtpActivity.class);
                in.putExtra("number", number);
                in.putExtra("key", key);

                startActivity(in);
                        /*
                startActivity(new Intent(ForgetPasswordActivity.this,UpdatePasswordActivity.class));
                finish();*/


            } else {
                Sneaker.with(this)
                        .setTitle(msg)
                        .setMessage("")
                        .sneakError();

            }
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