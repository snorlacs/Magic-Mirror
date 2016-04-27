package com.digitalmirror.magicmirror.gateways;

import com.digitalmirror.magicmirror.models.UserBeaconLocation;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LocationServiceGateway {

    @POST("/location")
    Call<UserBeaconLocation> postLocation(@Body UserBeaconLocation userBeaconLocation);
}
