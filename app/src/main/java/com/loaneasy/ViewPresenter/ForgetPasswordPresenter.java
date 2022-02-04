package com.loaneasy.ViewPresenter;


import android.content.Context;

import com.loaneasy.Rtrofit.ApiClientt;
import com.loaneasy.ViewPresenter.ModelReq.ForgetPasswordSendOTPRequest;
import com.loaneasy.ViewPresenter.ModelReq.LoginRequest;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ForgetPasswordPresenter {

    private UserLoginView view;

    public ForgetPasswordPresenter(UserLoginView view) {
        this.view = view;
    }


    public void ForgetPasswordSendOTP(Context context, ForgetPasswordSendOTPRequest loginRequest) {
        Call<ResponseBody> loginCall = ApiClientt.getApi(context).ForgetPasswordSendOTPRequest(loginRequest);

        //    Call<ResponseBody> loginCall = ApiManager.getApi(context).GetCart( "38","47,48");
        view.showHideProgress(true);

        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                view.showHideProgress(false);



                if (response.isSuccessful() && response.body() != null && response.code() == 200) {
                    try {

                        JSONObject jsonObject = new JSONObject(response.body().string());

                        view.onUserLoginSuccess(response.body(), jsonObject.getString("status"),jsonObject.getString("msg"), response.message());


                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else if (response.code() == 500) {
                    try {
                        String errorStr = response.errorBody().string();
                        JSONObject jsonObject = new JSONObject(errorStr);
                        JSONObject jsonObject1 = jsonObject.getJSONObject("message");

                        view.onUserLoginError( jsonObject1.getString("error"));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        view.onUserLoginError( String.valueOf(response.code()));
                    }

                } else if (response.code() == 401) {
                    try {
                        String errorStr = response.errorBody().string();
                        JSONObject jsonObject = new JSONObject(errorStr);
                        JSONObject jsonObject1 = jsonObject.getJSONObject("message");

                        view.onUserLoginError( jsonObject1.getString("error"));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        view.onUserLoginError( String.valueOf(response.code()));
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                view.onUserLoginFailure(t);
                view.showHideProgress(false);

            }
        });

    }
    public void sendRegistrationOtp(Context context, ForgetPasswordSendOTPRequest loginRequest) {
        Call<ResponseBody> loginCall = ApiClientt.getApi(context).sendRegistrationOtp(loginRequest);

        //    Call<ResponseBody> loginCall = ApiManager.getApi(context).GetCart( "38","47,48");
        view.showHideProgress(true);

        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                view.showHideProgress(false);



                if (response.isSuccessful() && response.body() != null && response.code() == 200) {
                    try {

                        JSONObject jsonObject = new JSONObject(response.body().string());

                        view.sendRegistrationOtpSucess(response.body(), jsonObject.getString("status"),jsonObject.getString("msg"), response.message());


                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else if (response.code() == 500) {
                    try {
                        String errorStr = response.errorBody().string();
                        JSONObject jsonObject = new JSONObject(errorStr);
                        JSONObject jsonObject1 = jsonObject.getJSONObject("message");

                        view.onUserLoginError( jsonObject1.getString("error"));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        view.onUserLoginError( String.valueOf(response.code()));
                    }

                } else if (response.code() == 401) {
                    try {
                        String errorStr = response.errorBody().string();
                        JSONObject jsonObject = new JSONObject(errorStr);
                        JSONObject jsonObject1 = jsonObject.getJSONObject("message");

                        view.onUserLoginError( jsonObject1.getString("error"));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        view.onUserLoginError( String.valueOf(response.code()));
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                view.onUserLoginFailure(t);
                view.showHideProgress(false);

            }
        });

    }



    public interface UserLoginView {

        void onUserLoginError( String message);

        void onUserLoginSuccess(ResponseBody response, String status,String msg,String messsage);
        void sendRegistrationOtpSucess(ResponseBody response, String status,String msg,String messsage);


        void showHideProgress(boolean isShow);

        void onUserLoginFailure(Throwable t);
    }
}
