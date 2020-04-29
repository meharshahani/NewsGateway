package com.example.newsgateway;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity
{
    private Menu main_menu = null;
    private Map<String, ArrayList<Source>> sourcesMap = new TreeMap<>();;

    private String chosenCategory = "";

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    private ListView drawerListView;
    private List<Source> drawerItemList = new ArrayList<>();
    private Map<String,Integer> colorMap = new HashMap<>();

    public static final int BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT = 1;

    private List<Fragment> fragments;
    private PageViewerAdapter pageAdapter;
    private ViewPager pager;

    private NewsReceiver newsReceiver;
    private List<Article> articles;

    private ImageView background;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setCustomActionBar(this);
        createLeftMenu();

        newsReceiver = new NewsReceiver();
        fragments = new ArrayList<>();
        pageAdapter = new PageViewerAdapter(getSupportFragmentManager());
        pager = findViewById(R.id.pager);
        pager.setAdapter(pageAdapter);

        background = findViewById(R.id.background);
    }

    public static void setCustomActionBar(AppCompatActivity activity)
    {
        TextView title = new TextView(activity);
        title.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));

        title.setText(activity.getString(R.string.app_name));
        title.setTextColor(Color.BLACK);
        title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(22);
        activity.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        activity.getSupportActionBar().setCustomView(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        main_menu = menu;

        if(!sourcesMap.isEmpty())
            updateRightMenu(this.sourcesMap);
        else
            {
            new NewsSourceDownloader(this).execute();
        }
        return super.onCreateOptionsMenu(menu);
    }
    private void updateRightMenu(Map<String, ArrayList<Source>> sourcesMap)
    {
        int[] color = {Color.BLACK,Color.DKGRAY,Color.LTGRAY,Color.RED,Color.GREEN,Color.BLUE,Color.YELLOW,Color.CYAN,Color.MAGENTA};
        for(String key: sourcesMap.keySet())
            main_menu.add(key);

        for (int i = 0; i < main_menu.size();++i)
        {
            MenuItem menuItem = main_menu.getItem(i);
//            if(menuItem.getItemId()==R.id.menuAbout ||
//                    actionBarDrawerToggle.onOptionsItemSelected(menuItem) ||
//                    menuItem.getItemId()==R.id.app_bar_search)
                //continue;

            colorMap.put(menuItem.getTitle().toString(), color[i%color.length]);
            SpannableString spanString = new SpannableString(menuItem.getTitle().toString());
            spanString.setSpan(new ForegroundColorSpan(color[i%color.length]), 0, spanString.length(), 0);
            menuItem.setTitle(spanString);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if (actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;

//        if (item.getItemId() == R.id.menuAbout)
//        {
//            Intent intent = new Intent(this, AboutActivity.class);
//            startActivity(intent);
//            return true;
//        }
        chosenCategory = item.getTitle().toString();
        updateLeftMenu(chosenCategory);
        drawerLayout.openDrawer(drawerListView);

        return true;
    }

    private void createLeftMenu()
    {
        drawerLayout = findViewById(R.id.drawerLayout);
        drawerListView = findViewById(R.id.drawerList);

        SourceAdapter sourceAdapter =
                new SourceAdapter(this, R.layout.drawer_layout, drawerItemList, colorMap);
        drawerListView.setAdapter(sourceAdapter);

        drawerListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onLeftMenuItemClicked(position);
            }
        });
        actionBarDrawerToggle = new ActionBarDrawerToggle
                (
                        this,
                        drawerLayout,
                        R.string.drawer_open,
                        R.string.drawer_close
                );
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }
    private void updateLeftMenu(String category)
    {
        if (category == null || category.isEmpty()) return;

        drawerItemList.clear();
        drawerItemList.addAll(sourcesMap.get(category));
        ((SourceAdapter) drawerListView.getAdapter()).notifyDataSetChanged();
    }
    private void onLeftMenuItemClicked(int position)
    {
        background.setVisibility(View.GONE);

        TextView title = (TextView) getSupportActionBar().getCustomView();
        title.setText(drawerItemList.get(position).getName());
        Intent intent = new Intent(getString(R.string.INTENT_SERVICE));
        intent.putExtra(Intent.ACTION_ATTACH_DATA, drawerItemList.get(position));
        sendBroadcast(intent);

        drawerLayout.closeDrawer(drawerListView);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

//    public void searchBar(MenuItem item)
//    {
//        SearchView searchView = findViewById(R.id.app_bar_search);
//    }

    public void onPostSourceDownload(Map<String, ArrayList<Source>> sourcesMap)
    {
        updateTreeMap(sourcesMap);
        updateRightMenu(sourcesMap);
    }
    private void updateTreeMap(Map<String, ArrayList<Source>> sourcesMap)
    {
        this.sourcesMap.clear();
        this.sourcesMap.putAll(sourcesMap);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState)
    {
        outState.putString(getString(R.string.NEWS_CATEGORY), chosenCategory);
        outState.putString("MEDIA_SOURCE",
                ((TextView) getSupportActionBar().getCustomView()).getText().toString());
        outState.putInt("PAGER_INDEX",pager.getCurrentItem());

        outState.putSerializable(getString(R.string.HASHMAP), (TreeMap)sourcesMap);
        outState.putSerializable("ARTICLES", (ArrayList)articles);
        outState.putSerializable("COLOR_MAP", (HashMap)colorMap);

        super.onSaveInstanceState(outState);
    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        chosenCategory = savedInstanceState.getString(getString(R.string.NEWS_CATEGORY));
        ((TextView) getSupportActionBar().getCustomView()).
                setText(savedInstanceState.getString("MEDIA_SOURCE"));

        int pagerIndex = savedInstanceState.getInt("PAGER_INDEX");

        updateTreeMap((Map<String, ArrayList<Source>>)savedInstanceState.getSerializable(getString(R.string.HASHMAP)));
        List<Article> articles = (ArrayList)savedInstanceState.getSerializable("ARTICLES");

        colorMap.clear();
        colorMap.putAll((HashMap)savedInstanceState.getSerializable("COLOR_MAP"));
        updateViewPager(articles, pagerIndex);

        if(articles != null)
            background.setVisibility(View.GONE);
        if(!chosenCategory.isEmpty())
            updateLeftMenu(chosenCategory);
    }

    @Override
    protected void onResume()
    {

        IntentFilter intentFilter = new IntentFilter(getString(R.string.INTENT_TO_MAIN));
        registerReceiver(newsReceiver, intentFilter);

        Intent intent = new Intent(MainActivity.this, NewsService.class);
        startService(intent);
        super.onResume();
    }
    @Override
    protected void onStop()
    {
        unregisterReceiver(newsReceiver);

        Intent intent = new Intent(MainActivity.this, NewsService.class);
        stopService(intent);

        super.onStop();
    }

    class NewsReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (action == null || !action.equals(getString(R.string.INTENT_TO_MAIN))) {return;}

            if (intent.hasExtra(Intent.ACTION_ATTACH_DATA))
            {
                List<Article> articles = (ArrayList) intent.getSerializableExtra(Intent.ACTION_ATTACH_DATA);
                updateViewPager(articles);
            }
        }
    }

    private class PageViewerAdapter extends FragmentStatePagerAdapter
    {
        private long baseId = 0;

        PageViewerAdapter(FragmentManager fm)
        {
           // super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            super(fm);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount()
        {
            return fragments.size();
        }
        void notifyChangeInPosition(int n)
        {
            baseId += getCount() + n;
        }
    }

    private void updateViewPager(List<Article> articles) {
        updateViewPager(articles, 0);
    }

    private void updateViewPager(List<Article> articles, int currentItem)
    {
        if(articles == null)
            return;

        this.articles = articles;
        fragments.clear();
        for (int i = 0; i < articles.size(); i++)
        {
            fragments.add(ArticleFragment.newInstance(articles.get(i), i+1, articles.size()));
        }

        pageAdapter.notifyDataSetChanged();
        pager.setCurrentItem(currentItem);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
}
