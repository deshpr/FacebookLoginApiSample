package com.example.rahul.facebookapisample;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.net.URL;

/**
 * Created by Rahul on 3/18/2016.
 */
import android.graphics.Bitmap;
import android.view.Display;


public class DownloadProfileAsyncTask extends AsyncTask<Void, Void, Void> {

    private String urlString;
    public Bitmap bitMap;
    public DisplayBitMapAfterDownloading callBack;

    public interface DisplayBitMapAfterDownloading {
        public void onGettingBitMapFromURLConnection(DownloadProfileAsyncTask taskInstance);
    }



    public DownloadProfileAsyncTask(String  urlString, DisplayBitMapAfterDownloading asyncCallback){
        super();
        this.urlString = urlString;
        this.callBack = asyncCallback;
    }

    protected void onPreExecute(){

    }

    private  android.graphics.Bitmap makeBitmapFromString(String urlString){

        android.graphics.Bitmap profileImage = null;
        try{
            URL imageUrl = new URL(urlString);
            profileImage = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
            Log.d(MainActivity.TAG, profileImage.toString());
        }
        catch(java.net.MalformedURLException ex){
            Log.d(MainActivity.TAG, ex.toString());
        }
        catch(java.io.IOException ex){
            Log.d(MainActivity.TAG, ex.toString());
        }
        return profileImage;
    }

        @Override
    protected Void doInBackground(Void... params){
        android.graphics.Bitmap resultingImage = makeBitmapFromString(this.urlString);
        Log.d(MainActivity.TAG, "resulting image = " + resultingImage.toString());
            this.bitMap = resultingImage;
        return null;
    }


    @Override
    protected void onPostExecute(Void result){
        super.onPostExecute(result);
        if(this.callBack!=null){
            this.callBack.onGettingBitMapFromURLConnection(this);
        }
    }
}
