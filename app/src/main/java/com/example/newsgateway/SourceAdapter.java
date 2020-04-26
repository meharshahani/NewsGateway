package com.example.newsgateway;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.IntegerRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Map;

public class SourceAdapter extends ArrayAdapter<Source>
{
    private List<Source> sources;
    private Map<String, Integer> map;

    SourceAdapter(@NonNull Context context, int layoutID, List<Source> sources, Map<String, Integer> map)
    {
        super(context, layoutID, sources);
        this.sources = sources;
        this.map = map;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_layout,parent,false);
        String sourceName = sources.get(position).getName();
        TextView item = convertView.findViewById(R.id.drawerItem);
        item.setText(sourceName);
        item.setTextColor(map.get(sources.get(position).getCategory()));

        return convertView;
    }
}
