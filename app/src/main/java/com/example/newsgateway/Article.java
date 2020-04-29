package com.example.newsgateway;

import java.io.Serializable;

public class Article implements Serializable
{
   // public boolean title;
    public String authorName;
    public String title;
    public String description;
    public String url;
    public String urlToImage;
    public String publishedAt;

    Article(String authorName, String title, String description, String url, String urlToImage, String publishedAt)
    {
        this.authorName  = authorName;
        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.publishedAt = publishedAt;

    }
}
