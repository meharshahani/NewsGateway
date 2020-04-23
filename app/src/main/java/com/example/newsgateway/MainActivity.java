package com.example.newsgateway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
    private static final int MENU_A = 100;
   // private static final int MENU_GROUP = 10;
    Button button;
    private static String TAG = "MainActivity";
    private Menu menu;
    private static int menuId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "onClick: Inside Onclick ");
                NewsSourceDownloader asyncObject = new NewsSourceDownloader();
                asyncObject.execute();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        this.menu = menu;
        //create a reference to the item
        //fetch data from the api
        //get the count of the category as an int and let the for loop handle it
        for(int i = 0; i < 5; i++)
        {
            menu.add(Menu.NONE, menuId, 0, "Option " + (i+1));
        }
       return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        //show news of the particular category when the option is selected
        Toast.makeText(this, "You selected an item", Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }


}
