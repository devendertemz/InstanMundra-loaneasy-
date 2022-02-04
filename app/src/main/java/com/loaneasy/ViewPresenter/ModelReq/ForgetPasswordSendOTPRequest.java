package com.loaneasy.ViewPresenter.ModelReq;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ForgetPasswordSendOTPRequest {




    @SerializedName("phone_no")
    @Expose
    private String phone_no;


    @SerializedName("email")
    @Expose
    private String email;



    public ForgetPasswordSendOTPRequest(String phone_no, String email) {
        this.phone_no = phone_no;
        this.email = email;
    }

    public ForgetPasswordSendOTPRequest(String phone_no) {
        this.phone_no = phone_no;
    }
}
