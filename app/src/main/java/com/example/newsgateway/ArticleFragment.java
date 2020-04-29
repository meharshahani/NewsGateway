package com.example.newsgateway;

import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.io.File.separator;

public class ArticleFragment extends Fragment
{
    private static final String TAG = "Frag_Article";
    private String article_url;
    private String index;

    public ArticleFragment(){};

    public static ArticleFragment newInstance(Article article, int index, int max)
    {
        ArticleFragment articleFragment = new ArticleFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("OBJECT" , article);

        //setting the page number
        String indexString = String.format("%d of %d", index, max);
        bundle.putString("INDEX", indexString);

        articleFragment.setArguments(bundle);
        return articleFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View fragmentView = inflater.inflate(R.layout.fragment_layout, container, false);
        Bundle args = getArguments();
        if(args == null)
            return null;

        final Article article = (Article) args.getSerializable("OBJECT");
        if(article == null)
            return  null;

        this.article_url = article.url;

        this.index = args.getString("INDEX");

        LinearLayout linearLayout = fragmentView.findViewById(R.id.frag_linear_layout);
        TextView textView;

        if(article.title != null && !article.title.isEmpty())
        {
            textView = new TextView(getContext());
            textView.setTextColor(Color.BLACK);
            textView.setText(article.title);
            textView.setTextSize(25);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setTypeface(Typeface.DEFAULT);
            textView.setPadding(0,0,0,5);
            setOnClick(textView);
            linearLayout.addView(textView);
        }
        if(article.publishedAt != null && !article.publishedAt.isEmpty())
        {
            String parsedDate = article.publishedAt;
            try
            {
                Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXX").parse(article.publishedAt);
                parsedDate = date.toString();
            }
            catch(ParseException e)
            {
                e.printStackTrace();
            }
            textView = new TextView(getContext());
            textView.setTextColor(Color.BLACK);
            textView.setText(parsedDate);
            textView.setTextSize(15);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            textView.setPadding(0,12,0,0);
            linearLayout.addView(textView);
        }
        if(article.authorName != null && !article.authorName.isEmpty())
        {
            textView = new TextView(getContext());
            textView.setPadding(0,8,0,0);
            textView.setTextColor(Color.BLACK);
            textView.setText(article.authorName);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            textView.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
            linearLayout.addView(textView);
        }
        {
            ImageView seperator = new ImageView(getContext());
            seperator.setPadding(0,8,0,0);
            seperator.setImageDrawable(getContext().getDrawable(R.drawable.separator));
            linearLayout.addView(seperator);
        }
        if(article.urlToImage != null && !article.urlToImage.isEmpty())
        {
            Picasso picasso = new Picasso.Builder(getContext()).build();
            ImageView imageView = new ImageView(getContext());
            imageView.setPadding(0,28,0,0);

            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int screenHeight = size.y;

            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int)(screenHeight*0.30)));

            picasso.load(article.urlToImage).error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);
            setOnClick(imageView);
            linearLayout.addView(imageView);
        }
        if(article.description != null && !article.description.isEmpty())
        {
            textView = new TextView(getContext());
            textView.setPadding(0,12,0,0);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(18);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            setOnClick(textView);

            if(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
            {
                textView.setText(Html.fromHtml(article.description, Html.FROM_HTML_MODE_LEGACY));
            }
            else
            {
                textView.setText(Html.fromHtml(article.description));
            }
            linearLayout.addView(textView);
        }

        if(args.getString("INDEX") != null)
        {
            TextView pagerIndex = fragmentView.findViewById(R.id.pageNumber);
            pagerIndex.setText(args.getString("INDEX"));
        }
        return fragmentView;
    }

    private void setOnClick(View view)
    {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(article_url));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
    }
}
