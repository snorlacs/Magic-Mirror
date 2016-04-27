package com.digitalmirror.magicmirror.gateways;

import com.digitalmirror.magicmirror.models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserServiceGateway {
    @POST("/users")
    Call<User> regiserUser(@Body User user);
}
