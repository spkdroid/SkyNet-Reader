package com.news.skynet.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.news.skynet.R;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.news.skynet.R;
import com.news.skynet.adapter.AppController;
import com.news.skynet.adapter.Newsfeedadapter;
import com.news.skynet.bean.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * NewsFeed.java
 * <p>
 * A placeholder fragment containing the news feed as a simple list.
 * <p>
 * The fragment consume the url as a constructer and will generate the feeds as a simple list.
 */
@SuppressLint("ValidFragment")
public class NewsFeed extends Fragment implements AdapterView.OnItemClickListener {

    private static final String TAG = "FEED";
    private static String url;
    private ProgressDialog pDialog;
    private List<News> newsfeedList = new ArrayList<News>();
    private ListView listView;
    private Newsfeedadapter adapter;
    SwipeRefreshLayout swipeContainer;
    private TextView msg;

    public NewsFeed(String s) {
        this.url = s;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainer);

        listView = (ListView) rootView.findViewById(R.id.partylist);

        msg = (TextView) rootView.findViewById(R.id.emptymsg);
        msg.setVisibility(View.INVISIBLE);
        adapter = new Newsfeedadapter(getActivity(), newsfeedList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        // when the items in the list are empty then a warning message is shown in the e screen
        if (adapter.isEmpty()) {
            msg.setVisibility(View.VISIBLE);
        }

        pDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();


        // method for producing refresh by swiping the listview
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // call the send request method to call the url
                sendRequest(url);
                swipeContainer.setRefreshing(false);
            }
        });

        // setting color scheme for the swipes
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        sendRequest(url);
        return rootView;
    }

    /***
     * Send request method use the url and fetch the infromation from the server. This is an reusable compenet.
     *
     * @param url
     */

    private void sendRequest(String url) {
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Log.d(TAG, response.toString());
                        hidePDialog();

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                // reading the information from the json object and storing it adding to the list.
                                JSONObject obj = response.getJSONObject(i);
                                News newsfeed = new News();
                                newsfeed.setNewsTitle(obj.getString("title"));
                                newsfeed.setImage(obj.getString("temp"));
                                newsfeed.setNewsLine(obj.getString("description"));
                                newsfeed.setUrl(obj.getString("link"));
                                newsfeed.setDate(obj.getString("date").substring(5, 16));
                                // adding movie to movies array
                                newsfeedList.add(newsfeed);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        adapter.notifyDataSetChanged();
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();
            }


        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq);
    }


    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        final String lert_description = ((TextView) view.findViewById(R.id.url)).getText().toString();
        Intent i = new Intent(Intent.ACTION_VIEW,
                Uri.parse(lert_description));
        startActivity(i);

    }
}
