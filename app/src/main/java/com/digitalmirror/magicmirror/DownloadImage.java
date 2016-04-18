package com.digitalmirror.magicmirror;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.digitalmirror.magicmirror.model.User;
import com.digitalmirror.magicmirror.services.UserService;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownloadImage extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    private final String facebookId;
    private final String firstName;
    private final String lastName;
    private final String gender;

    public DownloadImage(ImageView bmImage,String facebookId, String firstName, String lastName, String gender) {
        this.bmImage = bmImage;
        this.facebookId = facebookId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmImage.setImageBitmap(result);
        result.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        String base64String = Base64.encodeToString(byteArray,Base64.DEFAULT);
        User user = new User(facebookId,firstName,lastName,gender,base64String);
        new UserService().registerUser(user, new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }
}
