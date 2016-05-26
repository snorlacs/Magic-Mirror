package com.digitalmirror.magicmirror.services.beacon;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.digitalmirror.magicmirror.models.UserBeaconLocation;
import com.digitalmirror.magicmirror.services.LocationService;
import com.digitalmirror.magicmirror.utils.Preferences;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import static com.digitalmirror.magicmirror.utils.Preferences.Keys.USER_ID;

public class BeaconHandler {
    private static final String IBEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    private static final String TAG = BeaconHandler.class.getSimpleName();

    private LocationService locationService;
    private Preferences preferences;

    public BeaconHandler(LocationService locationService, Preferences preferences) {
        this.locationService = locationService;
        this.preferences = preferences;
    }

    public BeaconHandler(Context context) {
        this(new LocationService(), new Preferences(context));
    }

    public void handleBeaconsInRange(Collection<Beacon> foundBeacons) {
        if (foundBeacons.isEmpty()) {
            return;
        }

        ArrayList<Beacon> sortedBeacons = sortByDistance(foundBeacons);
        Beacon nearestBeacon = sortedBeacons.get(0);
        String uuId = nearestBeacon.getId1().toString();
        long majorId = Long.parseLong(String.valueOf(nearestBeacon.getId2()));
        long minorId = Long.parseLong(String.valueOf(nearestBeacon.getId3()));

        Log.d(TAG, "Found a nearestBeacon! uuid: " + uuId +
                " and major id: " + majorId +
                " and minor id: " + minorId +
                " approximately " + nearestBeacon.getDistance() + " meters away.");
        if (preferences.get(USER_ID) != null) {
            UserBeaconLocation userBeaconlocation = new UserBeaconLocation(preferences.get(USER_ID), uuId, majorId, minorId);
            locationService.postLocation(userBeaconlocation);
        } else {
            Log.d(TAG, "Beacon registration waiting for user registration");
        }

    }

    public String getLayout() {
        return IBEACON_LAYOUT;
    }

    @NonNull
    public ArrayList<Beacon> sortByDistance(Collection<Beacon> foundBeacons) {
        ArrayList<Beacon> beacons = new ArrayList<>(foundBeacons);
        Collections.sort(beacons, new Comparator<Beacon>() {
            @Override
            public int compare(Beacon lhs, Beacon rhs) {
                return (int) (lhs.getDistance() - rhs.getDistance());
            }
        });

        return beacons;
    }
}
