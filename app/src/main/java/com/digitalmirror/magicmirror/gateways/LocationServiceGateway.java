package com.digitalmirror.magicmirror.gateways;

import com.digitalmirror.magicmirror.models.User;
import com.digitalmirror.magicmirror.models.UserBeaconLocation;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface LocationServiceGateway {

    @POST("/location")
    Call<UserBeaconLocation> postLocation(@Body UserBeaconLocation userBeaconLocation);

    @DELETE("/location/{id}")
    Call<Void> logoutUser(@Path("id") String userId);
}
