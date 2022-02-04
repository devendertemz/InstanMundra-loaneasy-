package com.loaneasy.NewAuthentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.irozon.sneaker.Sneaker;
import com.loaneasy.R;
import com.loaneasy.ViewPresenter.ModelReq.SignUpBody;
import com.loaneasy.ViewPresenter.ModelReq.UpdatePasswordRequest;
import com.loaneasy.ViewPresenter.NewUserSignUpPresenter;
import com.loaneasy.ViewPresenter.updatePasswordPresenter;
import com.rjesture.startupkit.AppTools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import customfonts.EditText_Roboto_Light;
import customfonts.MyTextView_Roboto_Medium;
import okhttp3.ResponseBody;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, NewUserSignUpPresenter.NewUserSignUpView {
    EditText ed_fullName, ed_Email, ed_mobile, ed_Password, ed_C_Password;
    String fullName, Email, mobile, Password, C_Password;
    MyTextView_Roboto_Medium btPhoneNoSubmit;
    NewUserSignUpPresenter presenter;
    Intent intent;
    String Number;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);




        ed_fullName = findViewById(R.id.ed_fullName);
        ed_Email = findViewById(R.id.ed_Email);
        ed_mobile = findViewById(R.id.ed_mobile);
        ed_Password = findViewById(R.id.ed_Password);
        ed_C_Password = findViewById(R.id.ed_C_Password);
        btPhoneNoSubmit = findViewById(R.id.btPhoneNoSubmit);


        intent = getIntent();
        if (intent != null) {
            Number = intent.getStringExtra("number");
            ed_mobile.setText(Number);
        }


        presenter=new NewUserSignUpPresenter(this);

        btPhoneNoSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btPhoneNoSubmit:

                SignUp();
                break;
        }
    }

    private void SignUp() {
        fullName = ed_fullName.getText().toString();
        Email = ed_Email.getText().toString();
        mobile = ed_mobile.getText().toString();
        Password = ed_Password.getText().toString();
        C_Password = ed_C_Password.getText().toString();

        if (fullName.isEmpty() || Email.isEmpty() || mobile.isEmpty() ||Password.isEmpty() || C_Password.isEmpty()){
            Sneaker.with(this)
                    .setTitle("All Filed is required")
                    .setMessage("")
                    .sneakWarning();
        } else if (Password.length() < 6) {
            Sneaker.with(SignUpActivity.this)
                    .setTitle("The password must be at least 6 characters!")
                    .setMessage("")
                    .sneakError();


        } else if (!Password.equals(C_Password)) {
            Sneaker.with(SignUpActivity.this)
                    .setTitle("Confirm password not match")
                    .setMessage("")
                    .sneakError();

        } else {

            SignUpBody signUpBody = new SignUpBody(fullName,Email,mobile,Password);

            presenter.NewUserSignUp(SignUpActivity.this ,signUpBody);

        }



    }

    @Override
    public void onNewUserSignUpError(String message) {
        Sneaker.with(this)
                .setTitle(message)
                .setMessage("")
                .sneakError();
    }

    @Override
    public void onNewUserSignUpSuccess(ResponseBody responseBody, String message) {
        String response=null,status = null,msg = null;

        if (message.equalsIgnoreCase("ok")) {

            try {
                response = responseBody.string();
                JSONObject jsonObject = new JSONObject(response);
                status = jsonObject.getString("status");
                msg = jsonObject.getString("msg");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            if (status.equalsIgnoreCase("true")) {

                Toast.makeText(SignUpActivity.this, msg + "", Toast.LENGTH_SHORT).show();
                Intent in = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(in);
                finish();



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
    public void onNewUserSignUpFailure(Throwable t) {
        Sneaker.with(this)
                .setTitle(t.getLocalizedMessage())
                .setMessage("")
                .sneakError();
    }
}