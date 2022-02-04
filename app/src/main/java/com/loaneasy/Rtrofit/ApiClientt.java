package com.loaneasy.Rtrofit;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClientt {

    private static final String BASE_URl = "https://instantmudra.com";

    private static Retrofit retrofit = null;
    private static OkHttpClient okHttpClient;
    private static int REQUEST_TIMEOUT = 30;
   // private static String token;

    public static Retrofit getRetrofit(Context context) {
       // token = SharedPrefManager.getInstance(context).GetAccessToken();
        //   System.out.println("tk " + token+" user "+MyPreferences.getInstance(context).getString(PrefConf.KEY_USER_ID, ""));
        if (okHttpClient == null)
            initOkHttp(context);

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }

    private static void initOkHttp(Context context) {

        OkHttpClient.Builder httpClient = new OkHttpClient().newBuilder()
                .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        httpClient.addInterceptor(interceptor);

        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json");

                    //.addHeader("Authorization", "Bearer " +token);

            Request request = requestBuilder.build();
            return chain.proceed(request);
        });
        okHttpClient = httpClient.build();
    }

    public static UserService getApi(Context context) {
        return ApiClientt.getRetrofit(context).create(UserService.class);
    }
}