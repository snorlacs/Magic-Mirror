package com.digitalmirror.magicmirror.services;

import com.digitalmirror.magicmirror.BuildConfig;
import com.digitalmirror.magicmirror.gateways.UserServiceGateway;
import com.digitalmirror.magicmirror.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class UserService {

    public void registerUser(User user, Callback<User> callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.USER_SERVICE_BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();


        UserServiceGateway userServiceGateway = retrofit.create(UserServiceGateway.class);
        Call<User> call = userServiceGateway.regiserUser(user);
        call.enqueue(callback);
    }


}
