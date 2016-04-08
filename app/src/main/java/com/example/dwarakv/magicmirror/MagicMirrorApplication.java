package com.example.dwarakv.magicmirror;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

public class MagicMirrorApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(this);
    }
}
