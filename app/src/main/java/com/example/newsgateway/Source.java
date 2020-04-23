package com.example.newsgateway;

import java.io.Serializable;

public class Source implements Serializable
{
    String name;
    String category;
    String id;

    Source(String name, String category, String id)
    {
        this.name = name;
        this.category = category;
        this.id = id;
    }

    public String getCategory()
    {
        return category;
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }
}
