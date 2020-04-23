package com.example.newsgateway;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NewsSourceDownloader extends AsyncTask<Void, Void, Void>
{
    private MainActivity mainActivity;
    //private String header = "https://newsapi.org/v2/sources?language=en&country=us&category=&apiKey=";
    private String ApiUrl =  "https://newsapi.org/v2/sources?language=en&country=us&category=&apiKey=" + "19084f873f6647ce84cc3790330842f0";

    // = String.format("%s%s", header, mainActivity.getApplicationContext().getString(R.string.API_KEY));

    //private String api_key = "19084f873f6647ce84cc3790330842f0";

     private static String TAG = "AsyncActivity";

     StringBuilder sb = new StringBuilder();
      JSONObject jsonObject;

//    NewsSourceDownloader(MainActivity ma)
//    {
//        mainActivity = ma;
//        ApiUrl = String.format("%s%s", header,
//                mainActivity.getApplicationContext().getString(R.string.API_KEY));
//    }

    //make the connection to the API
    @Override
    protected Void doInBackground(Void... voids)
    {
        //convert string url to uri format
        Uri uri = Uri.parse(ApiUrl);
        String urlToUse = uri.toString();

        Log.d(TAG, "doInBackground: " + urlToUse);
        //StringBuilder sb = new StringBuilder();

        try
        {
            //make the uri into a URL format that the browser understands
            URL url = new URL(urlToUse);

            //connection object to connect to the API
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            if (con.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                return null;
            }
            //set the method to retrieve the data from the api
            con.setRequestMethod("GET");

            //input stream will get the data
            InputStream inputStream = con.getInputStream();

            //buffered  reader will read the data from the source
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while((line=bufferedReader.readLine()) != null)
            {
                sb.append(line).append('\n');
            }
            jsonObject = new JSONObject(sb.toString());
            Log.d(TAG, "doInBackground: " + jsonObject.toString());
        }
        catch (Exception e)
        {
            Log.d(TAG, "doInBackground: ", e);
            return null;
        }
        return null;
    }
}
