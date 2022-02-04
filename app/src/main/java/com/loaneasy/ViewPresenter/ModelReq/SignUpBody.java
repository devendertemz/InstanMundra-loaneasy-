package com.loaneasy.ViewPresenter.ModelReq;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SignUpBody {
    @SerializedName("full_name")
    @Expose
    public String fullName;
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("phone_no")
    @Expose
    public String phoneNo;
    @SerializedName("password")
    @Expose
    public String password;

    /**
     * No args constructor for use in serialization
     *
     */
    public SignUpBody() {
    }

    /**
     *
     * @param password
     * @param fullName
     * @param email
     * @param phoneNo
     */
    public SignUpBody(String fullName, String email, String phoneNo, String password) {
        super();
        this.fullName = fullName;
        this.email = email;
        this.phoneNo = phoneNo;
        this.password = password;
    }

}