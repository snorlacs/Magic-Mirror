package com.digitalmirror.magicmirror.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class Preferences {

    private final SharedPreferences sharedPreferences;

    public Preferences(Context context) {
        sharedPreferences = context.getSharedPreferences("magic_mirror", MODE_PRIVATE);
    }

    public void store(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String get(String key) {
        return sharedPreferences.getString(key, null);
    }

    public interface Keys {
        String FIRST_NAME = "firstName";
        String GENDER = "gender";
        String USER_ID = "userId";
    }
}
