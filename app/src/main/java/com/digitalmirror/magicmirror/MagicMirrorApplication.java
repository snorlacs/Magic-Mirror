package com.digitalmirror.magicmirror;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.digitalmirror.magicmirror.model.BeaconLocation;
import com.digitalmirror.magicmirror.services.LocationService;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MagicMirrorApplication extends Application implements BootstrapNotifier {

    private static final String TAG = "MagicMirrorApplication";
    private RegionBootstrap regionBootstrap;
    private BeaconManager beaconManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "App started up");
        FacebookSdk.sdkInitialize(this);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

        Region region = new Region("mirrorRegion", null, null, null);
        regionBootstrap = new RegionBootstrap(this, region);
    }


    @Override
    public void didEnterRegion(Region region) {

        Log.d(TAG, "Got a didEnterRegion call");

        BeaconLocation beaconLocation = new BeaconLocation("uuId", "majorId", "minorId");


        new LocationService().postLocation(beaconLocation, new Callback<BeaconLocation>() {
            @Override
            public void onResponse(Call<BeaconLocation> call, Response<BeaconLocation> response) {

            }

            @Override
            public void onFailure(Call<BeaconLocation> call, Throwable t) {

            }
        });


        regionBootstrap.disable();
        Intent intent = new Intent(this, MonitoringActivity.class);
        // IMPORTANT: in the AndroidManifest.xml definition of this activity, you must set android:launchMode="singleInstance" or you will get two instances
        // created when a user launches the activity manually and it gets launched from here.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }

    @Override
    public void didExitRegion(Region region) {

    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {

    }
}
