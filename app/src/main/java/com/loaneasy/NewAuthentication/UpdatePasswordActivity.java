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
import com.loaneasy.ViewPresenter.ModalRepo.LoginRepo;
import com.loaneasy.ViewPresenter.ModelReq.UpdatePasswordRequest;
import com.loaneasy.ViewPresenter.updatePasswordPresenter;
import com.rjesture.startupkit.AppTools;

import okhttp3.ResponseBody;

public class UpdatePasswordActivity extends AppCompatActivity implements updatePasswordPresenter.UserLoginView {


    TextView UpdatePassword;
    EditText etPassword, etconfirmPassword;
    String Number, passwordd, Cpasswordd;


    Intent intent;
    updatePasswordPresenter presenter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        intent = getIntent();
        if (intent != null) {
            Number = intent.getStringExtra("number");

        }

        UpdatePassword = findViewById(R.id.UpdatePassword);

        etconfirmPassword = findViewById(R.id.etconfirmPassword);
        etPassword = findViewById(R.id.etPassword);

        UpdatePassword = findViewById(R.id.UpdatePassword);
        presenter=new updatePasswordPresenter(this);

        UpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                passwordd = etPassword.getText().toString().trim();
                Cpasswordd = etconfirmPassword.getText().toString();

                if (passwordd.length() < 6) {
                    Sneaker.with(UpdatePasswordActivity.this)
                            .setTitle("The password must be at least 6 characters!")
                            .setMessage("")
                            .sneakError();


                } else if (!passwordd.equals(Cpasswordd)) {

                    Sneaker.with(UpdatePasswordActivity.this)
                            .setTitle("Confirm password not match")
                            .setMessage("")
                            .sneakError();

                } else {
                    presenter.ForgetPasswordSendOTP(UpdatePasswordActivity.this,new UpdatePasswordRequest(Number,passwordd));


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
    public void onUserLoginSuccess(ResponseBody response, String status, String msg, String message) {


        if (message.equalsIgnoreCase("ok")) {
            if (status.equalsIgnoreCase("true")) {

                Toast.makeText(UpdatePasswordActivity.this, msg + "", Toast.LENGTH_SHORT).show();
                Intent in = new Intent(UpdatePasswordActivity.this, LoginActivity.class);
                startActivity(in);
                finish();
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