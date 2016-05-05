package com.digitalmirror.magicmirror;

import android.app.Application;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import com.digitalmirror.magicmirror.services.beacon.BeaconScannerService;
import com.digitalmirror.magicmirror.utils.BluetoothUtil;
import com.digitalmirror.magicmirror.utils.LocationUtil;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;

public class MagicMirrorApplication extends Application {

    private static final String TAG = MagicMirrorApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "App started up");

        FacebookSdk.sdkInitialize(this);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);

        if (new BluetoothUtil().enableBluetooth()) {
            startService(new Intent(this, BeaconScannerService.class));
        }
    }


}
