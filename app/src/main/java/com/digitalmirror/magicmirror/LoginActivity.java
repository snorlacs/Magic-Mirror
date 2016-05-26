package com.digitalmirror.magicmirror;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.digitalmirror.magicmirror.models.User;
import com.digitalmirror.magicmirror.services.LocationService;
import com.digitalmirror.magicmirror.services.UserService;
import com.digitalmirror.magicmirror.utils.LocationUtil;
import com.digitalmirror.magicmirror.utils.Preferences;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.digitalmirror.magicmirror.utils.Preferences.Keys.FIRST_NAME;
import static com.digitalmirror.magicmirror.utils.Preferences.Keys.GENDER;
import static com.digitalmirror.magicmirror.utils.Preferences.Keys.USER_ID;

public class LoginActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final String TAG = "LoginActivity";
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private RelativeLayout profileContent;
    private String firstName;
    private String gender;

    private String userId;
    private ProgressDialog progress;
    private Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        runtimePermissionsForLocationServices();
        enableBluetooth();
        enableLocationServicesForAndroidM();

        preferences = new Preferences(getApplicationContext());
        loadPreferences();
        trackAccessToken();
        trackProfile();
        accessTokenTracker.startTracking();
        profileTracker.startTracking();
        progress = new ProgressDialog(this);
        loginWithFacebook();

    }

    private void enableBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);;
        }
    }

    private void runtimePermissionsForLocationServices() {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH}, PERMISSION_REQUEST_COARSE_LOCATION);
        }
    }

    private void enableLocationServicesForAndroidM() {
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
            if(new LocationUtil().isLocationEnabled(getApplicationContext())) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Location Services Not Active");
                builder.setMessage("Please enable Location Services and GPS");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });
                Dialog alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        savePreferences();
//        accessTokenTracker.stopTracking();
//        profileTracker.stopTracking();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Profile profile = Profile.getCurrentProfile();
        displayProfileContents(profile);
    }

    @Override
    public void onBackPressed() {
        savePreferences();
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        loadPreferences();
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
                    RelativeLayout profileLayout = (RelativeLayout) findViewById(R.id.profile_content);
                    profileLayout.setVisibility(View.INVISIBLE);
                    clearUserBeaconRelation();
                    clearPreferences();
                }
            }
        };
    }

    private void clearUserBeaconRelation() {

        new LocationService().logoutUser(userId, new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i("Delete:", "success");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.i("Failure", t.toString());
            }
        });
    }

    private void clearPreferences() {
       firstName = null;
       gender = null;
       userId = null;
       savePreferences();
    }

    private void loadPreferences() {
        firstName = preferences.get(FIRST_NAME);
        gender = preferences.get(GENDER);
        userId = preferences.get(USER_ID);
    }

    private void registerUser(String lastName, String base64String) {
        if(firstName == null)
        {
            Profile profile = Profile.getCurrentProfile();
            firstName = profile.getFirstName();
        }

        User user = new User(userId, firstName, lastName, gender, base64String);
        new UserService().registerUser(user, new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                savePreferences();
                Log.i("Response", response.toString());

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.i("Failure", t.toString());
        }
        });
    }

    private void displayProfileContents(Profile profile) {
        if (profile != null) {
            profileContent = (RelativeLayout) findViewById(R.id.profile_content);
            RelativeLayout welcomeContent = (RelativeLayout) findViewById(R.id.welcome_content);
            TextView greetings = (TextView) findViewById(R.id.greet);
            greetings.setText("Hello " + firstName);
            welcomeContent.setVisibility(View.INVISIBLE);
            profileContent.setVisibility(View.VISIBLE);
            Picasso.with(this).load(profile.getProfilePictureUri(200, 200)).into(target);

        }
    }

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            ImageView imageView = (ImageView) findViewById(R.id.profile_image);
            imageView.setImageBitmap(bitmap);
            if(userId == null) {
                userId = Profile.getCurrentProfile().getId();
                String lastName = Profile.getCurrentProfile().getLastName();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                String base64String = Profile.getCurrentProfile().getProfilePictureUri(400,400).toString();
                registerUser(lastName, base64String);
            }
            progress.dismiss();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };

    private void loginWithFacebook() {
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);
        facebookCallback();
    }

    private void facebookCallback() {
        LoginButton facebookSignInButton = (LoginButton) findViewById(R.id.login_button);
        facebookSignInButton.setReadPermissions("public_profile");

        facebookSignInButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                progress.setTitle("Loading");
                progress.show();
                AccessToken accessToken = loginResult.getAccessToken();
                setFacebookData(loginResult);
                enableBluetooth();
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

    private void savePreferences() {
        preferences.store(FIRST_NAME, firstName);
        preferences.store(Preferences.Keys.GENDER, gender);
        preferences.store(Preferences.Keys.USER_ID, userId);
    }
}

