package com.example.newsgateway;

import java.io.Serializable;

public class Article implements Serializable
{
    private String authorName;
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private String publishedAt;

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
