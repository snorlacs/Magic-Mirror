package com.digitalmirror.magicmirror.utils;

import android.bluetooth.BluetoothAdapter;

public class BluetoothUtil {

    public boolean enableBluetooth() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!defaultAdapter.isEnabled()) {
            defaultAdapter.enable();
        }
        return true;
    }
}
