package com.digitalmirror.magicmirror;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digitalmirror.magicmirror.model.User;
import com.digitalmirror.magicmirror.services.UserService;
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

public class LoginActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private LinearLayout profileContent;
    private String firstName;
    private String gender;

    private String userId;
    private ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadPreferences();
        trackAccessToken();
        trackProfile();
        accessTokenTracker.startTracking();
        profileTracker.startTracking();
        progress = new ProgressDialog(this);
        loginWithFacebook();
    }

    @Override
    protected void onStop() {
        super.onStop();
        savePreferences();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
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
                    progress.setTitle("Loading");
                    progress.show();
                    profileContent.setVisibility(View.INVISIBLE);
                    progress.dismiss();
                }
            }
        };
    }

    private void loadPreferences() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        firstName = sharedPreferences.getString("firstName", null);
        gender = sharedPreferences.getString("gender", null);
        userId = sharedPreferences.getString("userId", null);
    }

    private void registerUser(String lastName, String base64String) {
        User user = new User(userId, firstName, lastName, gender, base64String);
        new UserService().registerUser(user, new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
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
            profileContent = (LinearLayout) findViewById(R.id.profile_content);
            TextView greetings = (TextView) findViewById(R.id.greet);
            greetings.setText("Hello " + firstName);
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
                String base64String = Base64.encodeToString(byteArray,Base64.DEFAULT);
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
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("firstName", firstName);
        editor.putString("gender", gender);
        editor.putString("userId", userId);
        editor.commit();
    }
}

