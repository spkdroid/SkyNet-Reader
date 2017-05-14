package com.news.skynet.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.news.skynet.R;
import com.news.skynet.bean.News;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 *  Newsfeedadapter.java
 *
 *
 *  This is used to fetch the individual feed item and bind it to the listview.
 *
 *  The application extends a BaseAdapter which can be used to override a simple listview to show a customized
 *
 *  news list.
 *
 */


public class Newsfeedadapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<News> movieItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public Newsfeedadapter(Activity activity, List<News> movieItems) {
        this.activity = activity;
        this.movieItems = movieItems;
    }

    @Override
    public int getCount() {
        return movieItems.size();
    }

    @Override
    public Object getItem(int location) {
        return movieItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.contact_item, null);
        // when the image is null (null check operation)
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        // linking the XML content
        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.thumbnail);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView genre = (TextView) convertView.findViewById(R.id.newscontent);
        TextView url=(TextView)convertView.findViewById(R.id.url);
        TextView date=(TextView) convertView.findViewById(R.id.Date);

        // set method to fix the information in the news feed.
        News m = movieItems.get(position);
        thumbNail.setImageUrl(m.getImage(), imageLoader);
        title.setText(m.getNewsTitle());
        genre.setText(m.getNewsLine());
        url.setText(m.getUrl());
        date.setText(m.getDate());

        return convertView;
    }
}