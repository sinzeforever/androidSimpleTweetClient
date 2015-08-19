package com.sinzeforever.mysimpletweets.fragments;

import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sinzeforever.mysimpletweets.libs.Util;
import com.sinzeforever.mysimpletweets.models.Tweet;
import com.sinzeforever.mysimpletweets.models.TwitterApplication;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeTimelineFragment extends TimelineFragment {

    public static HomeTimelineFragment newInstance() {
        HomeTimelineFragment fragment = new HomeTimelineFragment();
        fragment.client = TwitterApplication.getRestClient();
        return fragment;
    }

    @Override
    public void populateTimeline() {
        showProgressBar();
        if (Util.isOnline(getActivity()) == true) {
            // is online, get data from net
            client.getHomeTimeline(new JsonHttpResponseHandler() {
                // SUCCESS
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    // parse data and save into adapter
                    handleHomeTimelineResponse(response);
                    hideProgressBar();
                }

                // FAILURE
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("my", "get home timeline API failed, " + errorResponse.toString());
                    // since api fail, get data from db
                    // populateTimelineFromDB();
                    hideProgressBar();
                }
            });
        } else {
            hideProgressBar();
        }
    }

    @Override
    public void rePopulateTimeline() {
        if (Util.isOnline(getActivity()) == false) {
            // if not online
            return;
        }
        showProgressBar();
        client.getFilter().resetHomeTimeline();
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            // SUCCESS
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("my", response.toString());
                // reset
                tweetsArrayAdapter.clear();
                // parse data and save into adapter
                handleHomeTimelineResponse(response);
                // move the scroll position to the top
                lvTweets.setSelection(0);
                // stop refreshing
                swipeRefreshLayout.setRefreshing(false);
                hideProgressBar();
            }

            // FAILURE
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("my", "re-get home timeline API failed" + errorResponse.toString());
                // since api fail, get data from db
                // populateTimelineFromDB();
                hideProgressBar();
            }
        });
    }

    protected void handleHomeTimelineResponse(JSONArray response) {
        // create tweets
        ArrayList<Tweet> newTweets = Tweet.createFromJSONArray(response);
        // update adapter
        tweetsArrayAdapter.addAll(newTweets);
        // update filter for pagination
        client.getFilter().updateHomeMaxId(newTweets);
        // save new tweets
        // saveAllTweetsToDB(newTweets);
    }
}