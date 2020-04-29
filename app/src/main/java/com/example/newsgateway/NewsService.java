package com.example.newsgateway;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class NewsService extends Service
{
   // private boolean running = false;
    ArrayList<Article> articlesList = new ArrayList<Article>();
    ServiceReceiver serviceReceiver;

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        serviceReceiver = new ServiceReceiver();
        IntentFilter intentFilter = new IntentFilter(getApplicationContext().getString(R.string.INTENT_SERVICE));
        registerReceiver(serviceReceiver, intentFilter);

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy()
    {
        unregisterReceiver(serviceReceiver);
        super.onDestroy();
    }

    public void setArticlesList(List<Article> articlesList)
    {
        if(articlesList == null)
            return;
        Intent intent = new Intent(getApplicationContext().getString(R.string.INTENT_MAIN));
        intent.putExtra(Intent.ACTION_ATTACH_DATA,(ArrayList) articlesList);
        sendBroadcast(intent);
    }

    class ServiceReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if(action == null || !action.equals(getString(R.string.INTENT_SERVICE)))
                return;

            Source source;
            if(intent.hasExtra(Intent.ACTION_ATTACH_DATA))
            {
                source = (Source)intent.getSerializableExtra(Intent.ACTION_ATTACH_DATA);
                new NewsArticleDownloader(NewsService.this, source.getId(), context).execute();
            }
        }
    }
}
