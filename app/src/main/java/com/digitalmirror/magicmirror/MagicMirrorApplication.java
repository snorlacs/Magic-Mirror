package com.digitalmirror.magicmirror;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;

public class MagicMirrorApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(this);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);
    }
}
