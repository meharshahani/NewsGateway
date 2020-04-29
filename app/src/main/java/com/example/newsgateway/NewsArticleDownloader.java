package com.example.newsgateway;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NewsArticleDownloader extends AsyncTask<Void,Void,List<Article>>
{

    private NewsService newsService;
    private String id;
    private Context context;

    public NewsArticleDownloader(NewsService newsService, String id, Context context)
    {
        this.newsService = newsService;
        this.id = id;
        this.context= context;
    }

    @Override
    protected List<Article> doInBackground(Void... voids)
    {
        JSONObject jsonObject = getArticles();
        if(jsonObject == null)
            return null;
        return parseArticles(jsonObject);
    }

    @Override
    protected void onPostExecute(List<Article> articles)
    {
        if(articles != null)
            newsService.setArticlesList(articles);
    }

    private JSONObject getArticles()
    {
        String url_ = String.format("%s%s%s%s", context.getString(R.string.START_URL),
                id, context.getString(R.string.END_URL), context.getString(R.string.API_KEY));

        JSONObject jsonObject = null;
        String urlToUse = Uri.parse(url_).toString();

        try
        {
            URL url =  new URL(urlToUse);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            if(httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                throw (new Exception());
            }

            httpURLConnection.setRequestMethod("GET");
            InputStream is = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));

            String line;
            StringBuilder sb = new StringBuilder();

            while((line = bufferedReader.readLine()) != null)
            {
                sb.append(line).append('\n');
            }
            jsonObject = new JSONObject(sb.toString());
            return jsonObject;
        }
        catch (Exception e) {
            e.printStackTrace();
            return jsonObject;
        }
    }

    private List<Article> parseArticles(JSONObject jsonObject)
    {
        List<Article> articles = new ArrayList<>();

        try
        {
            if(jsonObject.has("articles"))
            {
                JSONArray articlesArray = jsonObject.getJSONArray("articles");
                for(int i = 0; i<articlesArray.length(); ++i)
                {
                    JSONObject articleObject = articlesArray.getJSONObject(i);

                    Article article = new Article(
                            articleObject.getString("author"),
                            articleObject.getString("title"),
                            articleObject.getString("description"),
                            articleObject.getString("url"),
                            articleObject.getString("urlToImage"),
                            articleObject.getString("publishedAt")
                    );
                    articles.add(article);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            return articles;
        }
    }
}












