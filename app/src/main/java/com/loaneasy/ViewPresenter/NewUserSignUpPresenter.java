package com.loaneasy.ViewPresenter;


import android.content.Context;

import com.loaneasy.Rtrofit.ApiClientt;
import com.loaneasy.ViewPresenter.ModelReq.SignUpBody;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NewUserSignUpPresenter {

    private NewUserSignUpView view;

    public NewUserSignUpPresenter(NewUserSignUpView view) {
        this.view = view;
    }


    public void NewUserSignUp(Context context, SignUpBody loginRequest) {
        Call<ResponseBody> loginCall = ApiClientt.getApi(context).NewuserSignUp(loginRequest);

        //    Call<ResponseBody> loginCall = ApiManager.getApi(context).GetCart( "38","47,48");
        view.showHideProgress(true);

        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                view.showHideProgress(false);



                if (response.isSuccessful() && response.body() != null && response.code() == 200) {
                    try {

                        view.onNewUserSignUpSuccess(response.body(), response.message());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else if (response.code() == 500) {
                    try {
                        String errorStr = response.errorBody().string();
                        JSONObject jsonObject = new JSONObject(errorStr);
                        JSONObject jsonObject1 = jsonObject.getJSONObject("message");

                        view.onNewUserSignUpError( jsonObject1.getString("error"));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        view.onNewUserSignUpError( String.valueOf(response.code()));
                    }

                } else if (response.code() == 401) {
                    try {
                        String errorStr = response.errorBody().string();
                        JSONObject jsonObject = new JSONObject(errorStr);
                        JSONObject jsonObject1 = jsonObject.getJSONObject("message");

                        view.onNewUserSignUpError( jsonObject1.getString("error"));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        view.onNewUserSignUpError( String.valueOf(response.code()));
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                view.onNewUserSignUpFailure(t);
                view.showHideProgress(false);

            }
        });

    }

  

    public interface NewUserSignUpView {

        void onNewUserSignUpError( String message);

        void onNewUserSignUpSuccess(ResponseBody responseBody, String message);

        void showHideProgress(boolean isShow);

        void onNewUserSignUpFailure(Throwable t);
    }
}
