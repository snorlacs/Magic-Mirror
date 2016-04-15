package com.digitalmirror.magicmirror.services;

import com.digitalmirror.magicmirror.gateways.LocationServiceGateway;
import com.digitalmirror.magicmirror.model.BeaconLocation;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class LocationService {

    public void postLocation(BeaconLocation beaconLocation, Callback<BeaconLocation> callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.mlab.com/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();


//        https://api.mlab.com/api/1/databases/mirrortest/collections/location?apiKey=tOzF18DiN6loXnndQzNIPiCNncJxS83P

        LocationServiceGateway serviceGateway = retrofit.create(LocationServiceGateway.class);
        Call<BeaconLocation> call = serviceGateway.postLocation(beaconLocation, "tOzF18DiN6loXnndQzNIPiCNncJxS83P");
        call.enqueue(callback);
    }
}
