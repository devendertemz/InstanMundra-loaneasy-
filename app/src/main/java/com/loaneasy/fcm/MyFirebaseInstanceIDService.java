package com.loaneasy.fcm;

import android.util.Log;

import com.loaneasy.network.ApiRequest;
import com.loaneasy.utils.UserSharedPreference;
import com.loaneasy.utils.Utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MyFirebaseInstanceIDService {

    UserSharedPreference sharedPreference;
    private String token,TAG="MyFirebaseInstanceIDService";

    /*@Override
    public void onTokenRefresh() {

        sharedPreference = new UserSharedPreference(this);
        //Getting registration token
        String fcmToken = FirebaseInstanceId.getInstance().getToken();
        sharedPreference.setFCMId(fcmToken);

        token = FirebaseInstanceId.getInstance().getToken();

        sendRegistrationToServer(token);

        //Displaying token on logcat
        Log.d("fcmToken", "----->" + fcmToken);
        //sharedPreference.setFCMId(fcmToken);

    }*/


    /*@Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        Log.i("fcmToken","="+token);
        sharedPreference.setFCMId(token);
        sendRegistrationToServer(token);
    }*/

    private void sendRegistrationToServer(String token) {
        //You can implement this method to store the token on your server
        //Not required for current project

        if(!sharedPreference.getUserId().isEmpty())
        {
            RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(Utility.BASE_URL).build();
            ApiRequest updateFcm = restAdapter.create(ApiRequest.class);
            updateFcm.updateFcmToken(sharedPreference.getUserId(), token, new Callback<Response>() {
                @Override
                public void success(Response result, Response response) {
                    BufferedReader bufferedReader = null;
                    String output = "";


                    try {
                        bufferedReader = new BufferedReader(new InputStreamReader(result.getBody().in()));
                        output = bufferedReader.readLine();
                        //progressDialog.dismiss();
                        Log.i(TAG, "Updated Fcm Token--->" + output);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void failure(RetrofitError error) {
                    //progressDialog.dismiss();

                    Log.e(TAG, "Error UpdateFcmToken-->" + error.getLocalizedMessage());
                }
            });
        }



    }

}
