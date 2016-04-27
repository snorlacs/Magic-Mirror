package com.digitalmirror.magicmirror.services;

import android.util.Log;

import com.digitalmirror.magicmirror.BuildConfig;
import com.digitalmirror.magicmirror.gateways.LocationServiceGateway;
import com.digitalmirror.magicmirror.models.UserBeaconLocation;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class LocationService {

    private static final String TAG = LocationService.class.getSimpleName();

    public void postLocation(UserBeaconLocation userUserBeaconLocation) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.LOCATION_SERVICE_BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        LocationServiceGateway serviceGateway = retrofit.create(LocationServiceGateway.class);
        Call<UserBeaconLocation> call = serviceGateway.postLocation(userUserBeaconLocation);
        call.enqueue(new Callback<UserBeaconLocation>() {
            @Override
            public void onResponse(Call<UserBeaconLocation> call, Response<UserBeaconLocation> response) {
                Log.i(TAG, "Got response from location service. Status Code: " + response.code());
            }

            @Override
            public void onFailure(Call<UserBeaconLocation> call, Throwable t) {
                Log.e(TAG, "Error while sending user location to location service", t);
            }
        });
    }
}
