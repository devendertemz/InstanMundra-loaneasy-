package com.loaneasy.Rtrofit;

import com.loaneasy.ViewPresenter.ModalRepo.LoginRepo;
import com.loaneasy.ViewPresenter.ModalRepo.OTPVerifyRepo;
import com.loaneasy.ViewPresenter.ModelReq.ForgetPasswordSendOTPRequest;
import com.loaneasy.ViewPresenter.ModelReq.LoginRequest;
import com.loaneasy.ViewPresenter.ModelReq.SignUpBody;
import com.loaneasy.ViewPresenter.ModelReq.UpdatePasswordRequest;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit.client.Response;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;




import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public interface UserService {


    /**
     * File Name: UserService.java
     * Description: This file contains classes and functions for the Api.
     *
     * @author Devender
     * Date Created: 20/07/2021
     * Date Released:
     * Created by Devender Singh
     */
/*

    @FormUrlEncoded

    @POST("/admin/API/UserLogin")
    Call<LoginRepo> userlogin(
            @Field("user_email") String user_email,
            @Field("password") String password

    );
*/

    @POST("/admin/API/UserLogin")
    Call<LoginRepo> userlogin(
            @Body LoginRequest loginRequest
    );

    @POST("/admin/API/UserSignUp")
    Call<ResponseBody> NewuserSignUp(
            @Body SignUpBody loginRequest
    );

    @POST("/admin/API/loginWithOtp")
    Call<ResponseBody> ForgetPasswordSendOTPRequest(
            @Body ForgetPasswordSendOTPRequest loginRequest
    );


    @POST("/admin/API/sendRegistrationOtp")
    Call<ResponseBody> sendRegistrationOtp(
            @Body ForgetPasswordSendOTPRequest loginRequest
    );


    @FormUrlEncoded
    @POST("/admin/API/verifyOTP")
    Call<OTPVerifyRepo> verifyOTP(
            @Field("phone_no") String phone_no,
            @Field("otp") String otp

    );

    @FormUrlEncoded
    @POST("/admin/API/verifyOTP")
    Call<ResponseBody> UserRegisterverifyOTP(
            @Field("phone_no") String phone_no,
            @Field("otp") String otp

    );

    @POST("/admin/API/saveNewPassword")
    Call<ResponseBody> saveNewPassword(
            @Body UpdatePasswordRequest updatePasswordRequest
    );



}
