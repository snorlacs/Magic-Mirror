package com.digitalmirror.magicmirror.gateways;

import com.digitalmirror.magicmirror.model.BeaconLocation;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface LocationServiceGateway {
    @POST("api/1/databases/mirrortest/collections/location")
    Call<BeaconLocation> postLocation(@Body BeaconLocation beaconLocation, @Query("apiKey") String key);

//    Call<List<User>> groupList(@Path("id") int groupId, @Query("sort") String sort);
}
