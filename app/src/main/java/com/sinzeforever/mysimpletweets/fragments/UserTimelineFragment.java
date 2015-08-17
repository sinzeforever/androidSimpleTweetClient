package com.sinzeforever.mysimpletweets.fragments;

import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sinzeforever.mysimpletweets.libs.Util;
import com.sinzeforever.mysimpletweets.models.Tweet;
import com.sinzeforever.mysimpletweets.models.TwitterApplication;
import com.sinzeforever.mysimpletweets.models.TwitterUser;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserTimelineFragment extends TimelineFragment {
    TwitterUser user;
    public static UserTimelineFragment newInstance(TwitterUser user) {
        UserTimelineFragment fragment = new UserTimelineFragment();
        fragment.client = TwitterApplication.getRestClient();
        fragment.user = user;
        return fragment;
    }

    @Override
    public void populateTimeline() {
        if (Util.isOnline(getActivity()) == true) {
            // is online, get data from net
            client.getUserTimeline(new JsonHttpResponseHandler() {
                // SUCCESS
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    // parse data and save into adapter
                    handleUserTimelineResponse(response);
                }

                // FAILURE
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("my", "get user timeline API failed, " + errorResponse.toString());
                    // since api fail, get data from db
                    // populateTimelineFromDB();
                }
            }, user.getScreenName());
        } else {
            //  Log.d("my", "use db");
            // populateTimelineFromDB();
        }
    }

    @Override
    public void rePopulateTimeline() {
        if (Util.isOnline(getActivity()) == false) {
            // if not online
            return;
        }
        client.getFilter().resetUserTimeline();
        client.getUserTimeline(new JsonHttpResponseHandler() {
            // SUCCESS
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("my", response.toString());
                // reset
                tweetsArrayAdapter.clear();
                // parse data and save into adapter
                handleUserTimelineResponse(response);
                // move the scroll position to the top
                lvTweets.setSelection(0);
                // stop refreshing
                swipeRefreshLayout.setRefreshing(false);
            }

            // FAILURE
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("my", "re-get user timeline API failed" + errorResponse.toString());
                // since api fail, get data from db
                // populateTimelineFromDB();
            }
        }, user.getScreenName());
    }

    protected void handleUserTimelineResponse(JSONArray response) {
        // create tweets
        ArrayList<Tweet> newTweets = Tweet.createFromJSONArray(response);
        // update adapter
        tweetsArrayAdapter.addAll(newTweets);
        // update filter for pagination
        client.getFilter().updateUserMaxId(newTweets);
        // save new tweets
        // saveAllTweetsToDB(newTweets);
    }
}