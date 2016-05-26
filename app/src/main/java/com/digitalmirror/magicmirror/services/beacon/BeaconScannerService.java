package com.digitalmirror.magicmirror.services.beacon;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;


public class BeaconScannerService extends Service
        implements BeaconConsumer, MonitorNotifier, RangeNotifier {

    private static final String TAG = BeaconScannerService.class.getSimpleName();
    private BeaconManager beaconManager;
    private BeaconHandler beaconHandler;
    private final IBinder mBinder = new LocalBinder();
    Collection<Beacon> beacons;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        if (beaconManager != null) {
            beaconManager.unbind(this);
        }

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isCompatible()) {
            Log.i(TAG, "Android OS version is below 4.3 or device doesn't have BLE, service not starting up!");
            stopSelf();
            return START_NOT_STICKY;
        }

        if (beaconHandler == null) {
            beaconHandler = new BeaconHandler(this);
        }

        beaconManager = BeaconManager.getInstanceForApplication(this);

        if (beaconManager != null && !beaconManager.isBound(this)) {
            beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(beaconHandler.getLayout()));
            BeaconManager.setAndroidLScanningDisabled(true);
            beaconManager.setForegroundScanPeriod(1000);
            beaconManager.setForegroundBetweenScanPeriod(500);
            beaconManager.setBackgroundMode(false);
            beaconManager.setBackgroundScanPeriod(2000);
            beaconManager.setBackgroundBetweenScanPeriod(500);
            beaconManager.bind(this);

            Log.i(TAG, "Beacon Scanner Service started up");
        }
        return START_STICKY;
    }

    private boolean isCompatible() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) &&
                (getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE));
    }

    @Override
    public void onBeaconServiceConnect() {
        Identifier uuid = Identifier.parse("B9407F30-F5F8-466E-AFF9-25556B57FE6D");
        Region region = new Region("airportRegion", uuid, null, null);

        beaconManager.setMonitorNotifier(this);
        beaconManager.setRangeNotifier(this);

        try {
            beaconManager.startMonitoringBeaconsInRegion(region);
        } catch (RemoteException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public void didEnterRegion(Region region) {
        try {
            beaconManager.startRangingBeaconsInRegion(region);
            Log.d(TAG, "Starting ranging beacons");
        } catch (RemoteException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public void didExitRegion(Region region) {
    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        Log.d(TAG, "Got beacons in range, size: " + beacons.size());
        this.beacons = beacons;
        beaconHandler.handleBeaconsInRange(beacons);

    }

    public Beacon getNearestBeacon() {
        if (beaconHandler == null) {
            beaconHandler = new BeaconHandler(this);
        }
        if(beacons!=null) {
            ArrayList<Beacon> sortedBeacons = beaconHandler.sortByDistance(beacons);
            return sortedBeacons.get(0);
        }
        return null;
    }

    public class LocalBinder extends Binder {
        public BeaconScannerService getService() {
            return BeaconScannerService.this;
        }
    }


}