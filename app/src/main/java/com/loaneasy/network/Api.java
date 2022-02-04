package com.loaneasy.network;



import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Api {

    @GET("admin/index.php/API/getBankbyType/")
    Call<String> getAllNetBanking(@Query("type") String typeNetbanking);



}
