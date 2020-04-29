package com.example.newsgateway;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class NewsSourceDownloader extends AsyncTask<Void, Void, Map<String, ArrayList<Source>>>
{

    private MainActivity mainActivity;
    NewsSourceDownloader(MainActivity mainActivity)
      {
          this.mainActivity = mainActivity;
      }

    @Override
    protected Map<String, ArrayList<Source>> doInBackground(Void... voids)
    {
        JSONObject jsonObject = getUrls();
         if(jsonObject == null)
             return null;

         return parseUrls(jsonObject);
    }

    @Override
    protected void onPostExecute(Map<String, ArrayList<Source>> sourcesMap)
    {
        mainActivity.onPostSourceDownload(sourcesMap);
    }

    // get all the data from the api into a JSON object
    private JSONObject getUrls()
    {
        String url = String.format("%s%s", mainActivity.getApplicationContext().getString(R.string.api_url),
                mainActivity.getApplicationContext().getString(R.string.API_KEY));
        JSONObject jsonObject = null;
        String urlToUse = Uri.parse(url).toString();

        try
        {
            URL url_ = new URL(urlToUse);
            HttpURLConnection con = (HttpURLConnection) url_.openConnection();

            if(con.getResponseCode() != HttpURLConnection.HTTP_OK)
                throw(new Exception());

            con.setRequestMethod("GET");

            InputStream inputStream = con.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            StringBuilder sb = new StringBuilder();

            while((line=bufferedReader.readLine()) != null)
            {
                sb.append(line).append('\n');
            }

            jsonObject = new JSONObject(sb.toString());
            return jsonObject;

        }
        catch(Exception e)
        {
            e.printStackTrace();
            return jsonObject;
        }
    }

    //parse the JSON object that you received earlier
    //it is an array of JSON object
    private Map<String, ArrayList<Source>> parseUrls(JSONObject jsonObject)
    {
        //this will store the array of JSON objects
        Map<String, ArrayList<Source>> sourcesMap = new TreeMap<>();
        ArrayList<Source> allSources = new ArrayList<>();
        try
        {
            if(jsonObject.has("sources"))
            {
                JSONArray sourcesArray = jsonObject.getJSONArray("sources");
                for(int i = 0; i < sourcesArray.length(); ++i)
                {
                    //get each json object from the array
                    JSONObject sourceObject = sourcesArray.getJSONObject(i);

                    //look for the fields that we are interested in and check if it is null
                    String name = sourceObject.getString("name");
                    String id = sourceObject.getString("id");
                    String category = sourceObject.getString("category");
                    if("null".equals(id))
                        continue;
                    if("null".equals(name))
                        continue;
                    if("null".equals((category)))
                        continue;

                    //make an object of the Source class
                    Source source = new Source(name,category,id);

                    ArrayList<Source> arrayList = sourcesMap.get(source.getCategory());
                    if(arrayList == null)
                    {
                        arrayList = new ArrayList<>();
                        arrayList.add(source);
                        sourcesMap.put(source.getCategory(), arrayList);
                    }
                    else arrayList.add(source);
                    allSources.add(source);
                }
                sourcesMap.put("all", allSources);
            }

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        finally
        {
            return sourcesMap;
        }

    }
}
