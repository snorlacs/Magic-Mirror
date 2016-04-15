package com.digitalmirror.magicmirror;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.digitalmirror.magicmirror.magicmirror.BuildConfig;
import com.digitalmirror.magicmirror.magicmirror.R;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;
import org.json.JSONException;
import org.json.JSONObject;

import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

public class LoginActivity extends AppCompatActivity implements BootstrapNotifier{

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private LinearLayout profileContent;
    private String firstName;
    private String gender;

    private RegionBootstrap regionBootstrap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadPreferences();

        trackAccessToken();
        trackProfile();
        accessTokenTracker.startTracking();
        profileTracker.startTracking();
        loginWithFacebook();
        checkForUpdates();
    }

    private void trackProfile() {
        profileTracker = new ProfileTracker() {

            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                displayProfileContents(currentProfile);
            }
        };
    }

    private void trackAccessToken() {
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    profileContent.setVisibility(View.INVISIBLE);
                }
            }
        };
    }

    private void loadPreferences() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        firstName = sharedPreferences.getString("firstName",null);
        gender = sharedPreferences.getString("gender",null);
    }

    private void displayProfileContents(Profile profile) {
        if (profile != null) {
            profileContent = (LinearLayout) findViewById(R.id.profile_content);
            TextView greetings = (TextView) findViewById(R.id.greet);
            greetings.setText("Hello " + firstName);
            System.out.println(gender);
            new DownloadImage((ImageView) findViewById(R.id.profile_image)).execute(profile.getProfilePictureUri(200, 200).toString());
            profileContent.setVisibility(View.VISIBLE);
        }
    }

    private void loginWithFacebook() {
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login);
        facebookCallback();
    }

    private void facebookCallback() {
        LoginButton facebookSignInButton = (LoginButton) findViewById(R.id.login_button);
        facebookSignInButton.setReadPermissions("user_friends");

        facebookSignInButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                setFacebookData(loginResult);
                Toast.makeText(getApplicationContext(), "Logging in...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }


    private void setFacebookData(final LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i("response", response.toString());
                        try {
                            firstName = response.getJSONObject().getString("first_name");
                            gender = response.getJSONObject().getString("gender");
                            displayProfileContents(Profile.getCurrentProfile());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,gender");
        request.setParameters(parameters);
        request.executeAsync();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onBackPressed() {
        savePreferences();
        super.onBackPressed();
    }

    private void savePreferences() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor =  sharedPreferences.edit();
        editor.putString("firstName",firstName);
        editor.putString("gender", gender);
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Profile profile = Profile.getCurrentProfile();
        displayProfileContents(profile);
        checkForCrashes();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Region region = new Region("mirrorRegion", Identifier.parse("b9407f30-f5f8-466e-aff9-25556b57fe6d"), Identifier.parse("21425"), null);
        regionBootstrap = new RegionBootstrap(this, region);
        unregisterManagers();
    }

    protected void onStop() {
        super.onStop();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterManagers();
    }

    private void checkForCrashes() {
        CrashManager.register(this);
    }

    private void checkForUpdates() {
        UpdateManager.register(this);
    }

    private void unregisterManagers() {
        UpdateManager.unregister();
    }


    @Override
    public void didEnterRegion(Region region) {
        System.out.println("coming..");
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Important:  make sure to add android:launchMode="singleInstance" in the manifest
        // to keep multiple copies of this activity from getting created if the user has
        // already manually launched the app.
        this.startActivity(intent);
    }

    @Override
    public void didExitRegion(Region region) {

    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {

    }
}

