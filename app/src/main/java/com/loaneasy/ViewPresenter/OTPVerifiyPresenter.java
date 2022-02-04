package com.loaneasy.ViewPresenter;


import android.content.Context;

import com.loaneasy.Rtrofit.ApiClientt;
import com.loaneasy.ViewPresenter.ModalRepo.OTPVerifyRepo;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OTPVerifiyPresenter {

    private UserLoginView view;

    public OTPVerifiyPresenter(UserLoginView view) {
        this.view = view;
    }


    public void ForgetPasswordSendOTP(Context context, String number, String otp) {
        Call<OTPVerifyRepo> loginCall = ApiClientt.getApi(context).verifyOTP(number, otp);

        //    Call<OTPVerifyRepo> loginCall = ApiManager.getApi(context).GetCart( "38","47,48");
        view.showHideProgress(true);

        loginCall.enqueue(new Callback<OTPVerifyRepo>() {
            @Override
            public void onResponse(Call<OTPVerifyRepo> call, Response<OTPVerifyRepo> response) {
                view.showHideProgress(false);


                if (response.isSuccessful() && response.body() != null && response.code() == 200) {
                    try {


                        view.onUserLoginSuccess(response.body(), response.message());

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else if (response.code() == 500) {
                    try {
                        String errorStr = response.errorBody().string();
                        JSONObject jsonObject = new JSONObject(errorStr);
                        JSONObject jsonObject1 = jsonObject.getJSONObject("message");

                        view.onUserLoginError(jsonObject1.getString("error"));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        view.onUserLoginError(String.valueOf(response.code()));
                    }

                } else if (response.code() == 401) {
                    try {
                        String errorStr = response.errorBody().string();
                        JSONObject jsonObject = new JSONObject(errorStr);
                        JSONObject jsonObject1 = jsonObject.getJSONObject("message");

                        view.onUserLoginError(jsonObject1.getString("error"));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        view.onUserLoginError(String.valueOf(response.code()));
                    }
                }
            }

            @Override
            public void onFailure(Call<OTPVerifyRepo> call, Throwable t) {
                view.onUserLoginFailure(t);
                view.showHideProgress(false);

            }
        });

    }
    public void UserRegisterverifyOTP(Context context, String number, String otp) {
        Call<ResponseBody> loginCall = ApiClientt.getApi(context).UserRegisterverifyOTP(number, otp);

        //    Call<OTPVerifyRepo> loginCall = ApiManager.getApi(context).GetCart( "38","47,48");
        view.showHideProgress(true);

        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                view.showHideProgress(false);


                if (response.isSuccessful() && response.body() != null && response.code() == 200) {
                    try {


                        view.onUserLoginSuccess(response.body(), response.message());

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else if (response.code() == 500) {
                    try {
                        String errorStr = response.errorBody().string();
                        JSONObject jsonObject = new JSONObject(errorStr);
                        JSONObject jsonObject1 = jsonObject.getJSONObject("message");

                        view.onUserLoginError(jsonObject1.getString("error"));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        view.onUserLoginError(String.valueOf(response.code()));
                    }

                } else if (response.code() == 401) {
                    try {
                        String errorStr = response.errorBody().string();
                        JSONObject jsonObject = new JSONObject(errorStr);
                        JSONObject jsonObject1 = jsonObject.getJSONObject("message");

                        view.onUserLoginError(jsonObject1.getString("error"));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        view.onUserLoginError(String.valueOf(response.code()));
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

        void onUserLoginError(String message);

        void onUserLoginSuccess(OTPVerifyRepo response, String message);
        void onUserLoginSuccess(ResponseBody response, String message);


        void showHideProgress(boolean isShow);

        void onUserLoginFailure(Throwable t);
    }
}
