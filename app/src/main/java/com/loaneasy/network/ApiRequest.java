package com.loaneasy.network;


import java.util.HashMap;
import java.util.Map;

import javax.security.auth.callback.Callback;


import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.mime.TypedFile;
import retrofit2.Call;

public interface ApiRequest {

    @FormUrlEncoded
    @POST("/loginUser")
    void login(@Field("phone_no") String phoneNo,
               @Field("location") String location,
               retrofit.Callback<Response> callback);

    @FormUrlEncoded
    @POST("/updateFcmToken")
    void updateFcmToken(@Field("user_id") String user_id,
                        @Field("fcm_token") String fcm_token,
                        retrofit.Callback<Response> callback);

    @FormUrlEncoded
    @POST("/laonEligibility")
    void loanEligibility(@Field("user_id") String user_id,
                         retrofit.Callback<Response> callback);

    @FormUrlEncoded
    @POST("/getActiveLoan")
    void getActiveLoan(@Field("user_id") String user_id,
                       retrofit.Callback<Response> callback);

    @FormUrlEncoded
    @POST("/sendingSMS")
    void sendingSMS(@Field("phone_no") String phone_no,
                    @Field("otp") String otp,
                    retrofit.Callback<Response> callback);

    @Multipart
    @POST("/userDetails")
    void userDetails(@Part("user_id") String user_id,
                     @Part("first_name") String first_name,
                     @Part("last_name") String last_name,
                     @Part("phone_no") String phoneNo,
                     @Part("gender") String gender,
                     @Part("d_o_b") String d_o_b,
                     @Part("official_mail") String official_mail,
                     @Part("emp_type") String emp_type,
                     @Part("take_home_salary") String take_home_salary,
                     @Part("address_state") String address_state,
                     @Part("address_city") String address_city,
                     @Part("local_address") String local_address,
                     @Part("pin_code") String pin_code,
                     @Part("latitude") String latitude,
                     @Part("longitude") String longitude,
                     @Part("pan_card_no") String pan_card_no,
                     @Part("aadhar_card_no") String aadhar_card_no,
                     @Part("social_media_type") String socail_media_type,
                     @Part("social_name") String socail_name,
                     @Part("social_id") String socail_id,
                     @Part("social_email") String socail_email,
                     @Part("social_profile_pic") String socail_profile_pic,
                     @Part("marital_status") String marital_status,
                     @Part("house_type") String house_type,
                     @Part("staying_years") String staying_years,
                     @Part("salary_mode") String salary_mode,
                     @Part("working_years") String working_years,
                     @Part("current_loan") String current_loan,
                     @Part("existing_loan") String existing_loan,
                     @Part("profile_pic") TypedFile profile_pic,
                     @Part("employer_id_card") TypedFile employer_id_card,
                     @Part("aadhar_front") TypedFile aadhar_front,
                     @Part("aadhar_back") TypedFile aadhar_back,
                     @Part("pan_card_photo") TypedFile pan_card_photo,
                     @Part("current_loan_emi") String current_loan_emi,
                     @Part("company_name") String company_name,
                     @Part("company_address") String company_add,
                     retrofit.Callback<Response> callback);


    @Multipart
    @POST("/do_add_userdetails")
    void userDetails2(@Part("user_id") String user_id,
                      @Part("first_name") String first_name,
                      @Part("last_name") String last_name,
                      @Part("phone_no") String phoneNo,
                      @Part("gender") String gender,
                      @Part("d_o_b") String d_o_b,
                      @Part("official_mail") String official_mail,
                      @Part("emp_type") String emp_type,
                      @Part("take_home_salary") String take_home_salary,
                      @Part("address_state") String address_state,
                      @Part("address_city") String address_city,
                      @Part("local_address") String local_address,
                      @Part("profile_completed") String profile_completed,
                      @Part("pin_code") String pin_code,
                      @Part("latitude") String latitude,
                      @Part("longitude") String longitude,
                      @Part("app_version") String app_version,
                      @Part("pan_card_no") String pan_card_no,
                      @Part("aadhar_card_no") String aadhar_card_no,
                      @Part("social_media_type") String socail_media_type,
                      @Part("social_name") String socail_name,
                      @Part("social_id") String socail_id,
                      @Part("social_email") String socail_email,
                      @Part("social_profile_pic") String socail_profile_pic,
                      @Part("marital_status") String marital_status,
                      @Part("house_type") String house_type,
                      @Part("staying_years") String staying_years,
                      @Part("salary_mode") String salary_mode,
                      @Part("working_years") String working_years,
                      @Part("current_loan") String current_loan,
                      @Part("existing_loan") String existing_loan,
                      @Part("current_loan_emi") String current_loan_emi,
                      @Part("company_name") String company_name,
                      @Part("company_address") String company_add,
                      retrofit.Callback<Response> callback);


    @Multipart
    @POST("/do_apply_loan")
    void applyLoan2(@Part("user_id") String user_id,
                    @Part("loan_amount") String loan_amount,
                    @Part("disbursed_amount") String disbursed_amount,
                    @Part("processing_fee") String processing_fee,
                    @Part("platform_charges") String platform_charges,
                    @Part("total_interest") String total_interest,
                    @Part("days_returning") String days_returning,
                    @Part("bank_statement") TypedFile bank_statement,
                    @Part("bank_statement_pin") String bank_statement_pin,
                    @Part("salary_slip1") TypedFile salary_slip1,
                    @Part("salary_slip2") TypedFile salary_slip2,
                    @Part("salary_slip3") TypedFile salary_slip3,
                    retrofit.Callback<Response> callback);


    @Multipart
    @POST("/updateUserProfile")
    void updateUserProfile(@Part("user_id") String user_id,
                           @Part("profile_pic") TypedFile profile_pic,
                           @Part("first_name") String fname,
                           @Part("last_name") String lname,
                           @Part("official_mail") String mail,
                           @Part("emp_type") String emp_type,
                           @Part("take_home_salary") String take_home_salary,
                           @Part("address_state") String address_state,
                           @Part("address_city") String address_city,
                           @Part("local_address") String local_address,
                           @Part("company_name") String company_name,
                           @Part("company_address") String company_address,
                           @Part("pin_code") String pin_code,
                           @Part("salary_mode") String salary_mode,
                           @Part("working_years") String working_years,
                           @Part("current_loan") String current_loan,
                           @Part("house_type") String house_type,
                           retrofit.Callback<Response> callback);

    @Multipart
    @POST("/uploadUserFiles")
    void uploadUserFile(@Part("user_id") String user_id,
                        @Part("profile_pic") TypedFile profile_pic,
                        @Part("employer_id_card") TypedFile employer_id_card,
                        @Part("aadhar_front") TypedFile aadhar_front,
                        @Part("aadhar_back") TypedFile aadhar_back,
                        @Part("pan_card_photo") TypedFile pan_card_photo,
                        retrofit.Callback<Response> callback);


    @FormUrlEncoded
    @POST("/updateUserProfile")
    void updateUserProfile(@Field("user_id") String user_id,
                           @Field("profile_pic") String profile_pic,
                           @Field("first_name") String fname,
                           @Field("last_name") String lname,
                           @Field("official_mail") String mail,
                           @Field("emp_type") String emp_type,
                           @Field("take_home_salary") String take_home_salary,
                           @Field("address_state") String address_state,
                           @Field("address_city") String address_city,
                           @Field("local_address") String local_address,
                           @Field("company_name") String company_name,
                           @Field("company_address") String company_address,
                           @Field("pin_code") String pin_code,
                           @Field("salary_mode") String salary_mode,
                           @Field("working_years") String working_years,
                           @Field("current_loan") String current_loan,
                           @Field("house_type") String house_type,
                           retrofit.Callback<Response> callback);

    @Multipart
    @POST("/applyLoan")
    void applyLoan(@Part("user_id") String user_id,
                   @Part("loan_amount") String loan_amount,
                   @Part("disbursed_amount") String disbursed_amount,
                   @Part("days_returning") String days_returning,
                   @Part("repayment_date") String repayment_date,
                   @Part("account_type") String account_type,
                   @Part("bank_name") String bank_name,
                   @Part("acct_no") String acct_no,
                   @Part("ifsc_code") String ifsc_code,
                   @Part("bank_statement") TypedFile bank_statmnt,
                   @Part("salary_slip1") TypedFile salary_slip1,
                   @Part("salary_slip2") TypedFile salary_slip2,
                   @Part("salary_slip3") TypedFile salary_slip3,
                   @Part("document_dependency") String document_dependency,
                   @Part("total_interest") String total_interest,
                   @Part("processing_fee") String processing_fee,
                   retrofit.Callback<Response> callback);

    @FormUrlEncoded
    @POST("/applyLoan")
    void applyLoanWihtoutDocs(@Field("user_id") String user_id,
                              @Field("loan_amount") String loan_amount,
                              @Field("disbursed_amount") String disbursed_amount,
                              @Field("days_returning") String days_returning,
                              @Field("repayment_date") String repayment_date,
                              @Field("account_type") String account_type,
                              @Field("bank_name") String bank_name,
                              @Field("acct_no") String acct_no,
                              @Field("ifsc_code") String ifsc_code,
                              @Field("bank_statement") String bank_statmnt,
                              @Field("salary_slip1") String salary_slip1,
                              @Field("salary_slip2") String salary_slip2,
                              @Field("salary_slip3") String salary_slip3,
                              @Field("document_dependency") String document_dependency,
                              @Field("total_interest") String total_interest,
                              @Field("processing_fee") String processing_fee,
                              retrofit.Callback<Response> callback);

    @FormUrlEncoded
    @POST("/getContactDetails")
    void contactDetails(@Field("contact_list") String contact_list,
                        retrofit.Callback<Response> callback);

    @FormUrlEncoded
    @POST("/getAplliedLoans")
    void getAplliedLoans(@Field("user_id") String user_id,
                         retrofit.Callback<Response> callback);

    @FormUrlEncoded
    @POST("/getUserDetails")
    void getUserDetails(@Field("user_id") String user_id,
                        retrofit.Callback<Response> callback);

    @GET("/getUserPoints")
    void getUserPoints(retrofit.Callback<Response> callback);

    @GET("/")
    void getPincodeDetails(retrofit.Callback<Response> callback);

    @FormUrlEncoded
    @POST("/sendingConfirmationSMS")
    void sendingConfirmationSMS(@Field("phone_no") String phone_no,
                                @Field("order_id") String order_id,
                                retrofit.Callback<Response> callback);


    @FormUrlEncoded
    @POST("/loginUser2")
    void loginUser2(@Field("phone_no") String phone,
                    retrofit.Callback<Response> callback);

    @FormUrlEncoded
    @POST("/verifyOTP")
    void verifyOTP(@Field("phone_no") String phoneNo,
                   @Field("otp") String location,
                   retrofit.Callback<Response> callback);



    @FormUrlEncoded
    @POST("admin/API/UserLogin")
    void UserLogin(@Field("user_email") String user_email,
                   @Field("password") String password,
                   retrofit.Callback<Response> callback);







}
