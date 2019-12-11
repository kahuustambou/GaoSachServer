package com.example.gaosachserver.Remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IGeoCoordinates {

    @GET("maps/api/geocode/json")
    Call<String> getGeoCode(@Query("Địa chỉ") String address);

    @GET("maps/api/direction/json")
    Call<String> getGeoCode(@Query("Nguồn gốc") String origin, @Query("Nơi đến") String destination);


    void getDirections();
}
