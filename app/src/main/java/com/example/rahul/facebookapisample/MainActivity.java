package com.example.rahul.facebookapisample;



import io.fabric.sdk.android.Fabric;
import android.os.AsyncTask;
import com.facebook.AccessToken;
import com.facebook.HttpMethod;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.example.rahul.facebookapisample.R;
import android.widget.Button;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;

/* Twitter Imports*/
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import android.os.AsyncTask;

/*  Facebook Imports */
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import java.net.URL;
import android.widget.ImageView;
import android.widget.TextView;
import  java.util.Arrays;
import com.facebook.CallbackManager;
import android.util.Log;
import android.widget.Toast;
import com.facebook.GraphRequest;
import org.json.JSONObject;
import com.facebook.GraphResponse;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements DownloadProfileAsyncTask.DisplayBitMapAfterDownloading {

    private LoginButton loginButton;


    private TwitterLoginButton twitterLoginButton;

    public static final String TAG = "HikeItLikeIt";

    private static String TWITTER_CONSUMER_KEY= "o81rdLmjYgv6XxQlQAmbeJwqB";
    private static String TWITTER_CONSUMER_VALUE = "IFKNiLSDdZ9caz8jCjIv35wytyDEBOKkhIOjUnot39AZnKbbRz";

// customize the login button in the onCreateView method

   CallbackManager callbackManager;

    TextView loginTextView;

    public void onGettingBitMapFromURLConnection(DownloadProfileAsyncTask taskInstance){

            Bitmap profilePicture = taskInstance.bitMap;
            Log.d(MainActivity.TAG, "Finished  making the bit map");
            ImageView imageView = (ImageView)findViewById(R.id.imageView);
            imageView.setImageBitmap(profilePicture);
            Log.d(MainActivity.TAG, "Set  the image view image");
    }

    private ArrayList<String> facebookReadPermissions;

    private void  setUpFacebookLogin(){
        loginButton = (LoginButton)this.findViewById(R.id.fb_loginButton);
        facebookReadPermissions = new ArrayList<String>();
        facebookReadPermissions.add("email");
        facebookReadPermissions.add("user_friends");
        facebookReadPermissions.add("user_birthday");
        facebookReadPermissions.add("user_location");
        facebookReadPermissions.add("public_profile");

        loginButton.setReadPermissions(facebookReadPermissions);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                loginTextView.setText("Successful login!");
                makeUserInfoRequest(loginResult);
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Canceled by the user", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "There was an error", Toast.LENGTH_LONG).show();
                loginTextView.setText(error.toString());
            }
        });

    }

   private String urlString = "";

    private void obtainFacebookUserProfilePicture(String userId) {
        GraphRequest requestForProfilePicture = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + userId + "/picture",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        Log.d(MainActivity.TAG, "profile pictyre = " + response.toString());
                        JSONObject  responseObject = response.getJSONObject();
                        try{
                            JSONObject data = responseObject.getJSONObject("data");
                            Log.d(MainActivity.TAG, "url = " + data.getString("url"));
                            String modifiedUrl =  getUrlString(data.getString("url"));
                            Log.d(MainActivity.TAG, "Mofified url = " + modifiedUrl);
                            new DownloadProfileAsyncTask(modifiedUrl, MainActivity.this).execute();
                        }
                        catch(org.json.JSONException ex){
                            Log.d(MainActivity.TAG, "Could not retrieve the vaoue at key");
                            Log.d(MainActivity.TAG, ex.toString());
                        }
                    }
                }
        );
        Bundle infoParameters = new Bundle();
        infoParameters.putString("redirect", "0");
        infoParameters.putString("type", "large");
        requestForProfilePicture.setParameters(infoParameters);
        requestForProfilePicture.executeAsync();    // execute the request.
    }



    private String getUrlString(String urlToFormat){
        Log.d(MainActivity.TAG, "The new url = " + urlToFormat.replace("\\", ""));
        return urlToFormat.replace("\\","");
    }

    private void makeUserInfoRequest(LoginResult result){
        GraphRequest  graphRequest = GraphRequest.newMeRequest(
                result.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback(){
            //. make a request for basic user info that is publicly available.
            @Override
            public void onCompleted(JSONObject object, GraphResponse response){
                Log.d(MainActivity.TAG, object.toString());
                Log.d(MainActivity.TAG, response.toString());
                try{
                    Log.d(MainActivity.TAG,  object.getString("email"));
                    String userId = object.getString("id");
                    Log.d(MainActivity.TAG, "user id = " + userId);
             obtainFacebookUserProfilePicture(userId);

                }

                catch(org.json.JSONException ex){
                 Log.d(MainActivity.TAG, "Unable to parse the JSON");
                }
            }
        });
        Bundle  infoParameters = new Bundle();
        infoParameters.putString("fields", "id,first_name,last_name,email,gender,birthday,picture");
        graphRequest.setParameters(infoParameters);
        graphRequest.executeAsync();    // execute the request.
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.main_layout);

        Button btn = (Button)findViewById(R.id.displayButton);
        btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                String url = " https://scontent.xx.fbcdn.net/hprofile-xft1/v/t1.0-1/p200x200/1918918_10203676244945406_3428997713542616786_n.jpg?_nc_eui=ARjodtiHAuJYaGIn5iGzlFiZEeYYrYnMaePx5_fZXse_HtLXIsZqIw&oh=4453d4bc8fc0d13f77b9e6c639a7155f&oe=578216CB";
                new DownloadProfileAsyncTask(url, MainActivity.this).execute();
            }
        });

        loginTextView = (TextView)findViewById(R.id.logTxtView);
        callbackManager = CallbackManager.Factory.create();
        setUpFacebookLogin();
        TwitterAuthConfig  authConfig = new TwitterAuthConfig(TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_VALUE);
        Fabric.with(this, new Twitter(authConfig));
        Log.d(MainActivity.TAG, "Initialized Twitter");
        twitterLoginButton = (TwitterLoginButton)this.findViewById(R.id.tw_loginButton);
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Toast.makeText(getApplicationContext(), "Sign in was successsful!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(getApplicationContext(), "Sign in  failed miserably!", Toast.LENGTH_LONG).show();
            }
        });


        //       setUpTwitterLogin();
    }

@Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data){
     super.onActivityResult(requestCode, resultCode, data);
     callbackManager.onActivityResult(requestCode, resultCode, data);
     twitterLoginButton.onActivityResult(requestCode, resultCode,data);
    }

}
