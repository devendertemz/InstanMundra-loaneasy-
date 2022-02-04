package com.loaneasy.ViewPresenter.ModelReq;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginRequest {




    @SerializedName("user_email")
    @Expose
    private String userEmail;
    @SerializedName("password")
    @Expose
    private String password;

    public LoginRequest(String userEmail, String password) {
        this.userEmail = userEmail;
        this.password = password;
    }
}
