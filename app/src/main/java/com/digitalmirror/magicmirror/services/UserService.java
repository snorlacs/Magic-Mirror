package com.digitalmirror.magicmirror.services;

import com.digitalmirror.magicmirror.gateways.UserServiceGateway;
import com.digitalmirror.magicmirror.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class UserService {

    public void registerUser(User user, Callback<User> callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://52.77.106.73:8080")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();


        UserServiceGateway userServiceGateway = retrofit.create(UserServiceGateway.class);
        Call<User> call = userServiceGateway.regiserUser(user);
        call.enqueue(callback);
    }

}
