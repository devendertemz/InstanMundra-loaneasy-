package com.loaneasy.ViewPresenter.ModelReq;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdatePasswordRequest {


    @SerializedName("phone_no")
    @Expose
    private String phone_no;
    @SerializedName("password")
    @Expose
    private String password;

    public UpdatePasswordRequest(String phone_no, String password) {
        this.phone_no = phone_no;
        this.password = password;
    }
}
