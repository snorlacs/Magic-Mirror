package com.digitalmirror.magicmirror.gateways;

import com.digitalmirror.magicmirror.model.BeaconLocation;
import com.digitalmirror.magicmirror.model.User;

import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.Call;
import retrofit2.http.Query;

public interface UserServiceGateway {
    @POST("/users")
    Call<User> regiserUser(@Body User user);
}
