package com.digitalmirror.magicmirror;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.digitalmirror.magicmirror.services.beacon.BeaconScannerService;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.appevents.AppEventsLogger;

public class MagicMirrorApplication extends Application {

    private static final String TAG = MagicMirrorApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "App started up");

        FacebookSdk.sdkInitialize(this);
        AppEventsLogger.activateApp(this);
        startService(new Intent(this, BeaconScannerService.class));
    }


}
