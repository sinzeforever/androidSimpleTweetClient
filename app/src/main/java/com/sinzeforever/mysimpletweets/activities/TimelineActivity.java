package com.sinzeforever.mysimpletweets.activities;

import android.app.ActionBar;
import android.graphics.drawable.ColorDrawable;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sinzeforever.mysimpletweets.R;
import com.sinzeforever.mysimpletweets.adapters.TweetsArrayAdapter;
import com.sinzeforever.mysimpletweets.libs.EndlessScrollListener;
import com.sinzeforever.mysimpletweets.libs.Util;
import com.sinzeforever.mysimpletweets.models.Tweet;
import com.sinzeforever.mysimpletweets.models.TwitterApplication;
import com.sinzeforever.mysimpletweets.models.TwitterClient;
import com.sinzeforever.mysimpletweets.models.TwitterUser;
import com.sinzeforever.mysimpletweets.sqlite.TweetDatabase;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimelineActivity extends ActionBarActivity {

    private final int POST_DIALOG_CODE = 98;
    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter tweetsArrayAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TweetDatabase db;
    private boolean resetDB = true;
    private ListView lvTweets;
    private TwitterUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        // set action bar
        setUpActionBar();
        // set up adapter
        setUpTweetAdapter();
        // set up db
        setUpDatabase();

        client = TwitterApplication.getRestClient(); // singleton client
        // getUserProfile();
        populateTimeline();

        // set up swipe and refresh layout
        setUpSwipeAndRefreshLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // handle networking change
        TextView tvWarning = (TextView) findViewById(R.id.tvWarning);
        if (Util.isOnline(this)) {
            rePopulateTimeline();
            tvWarning.setVisibility(View.GONE);
        } else {
            // show error msg
            tvWarning.setVisibility(View.VISIBLE);
        }
    }

    private void setUpActionBar() {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.twitterBlue)));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
    }

    private void setUpTweetAdapter() {
        // find list view
        lvTweets = (ListView) findViewById(R.id.lvTweets);
        // create array
        tweets = new ArrayList<>();
        // construct the adapter to from data source
        tweetsArrayAdapter = new TweetsArrayAdapter(this, tweets);
        // set adapter
        lvTweets.setAdapter(tweetsArrayAdapter);
        // set infinite scroll events
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (Util.isOnline(getBaseContext()) == true) {
                    populateTimeline();
                }
            }
        });
    }

    private void setUpSwipeAndRefreshLayout() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.slTimeline);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Util.isOnline(getBaseContext())) {
                    rePopulateTimeline();
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void setUpDatabase() {
        db = new TweetDatabase(this);
    }

    // get user profile
    private void getUserProfile() {
        client.getUserProfile(new JsonHttpResponseHandler() {
            // SUCCESS
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("my", response.toString());
            }

            // FAILURE
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("my", "get user profile API failed, " + errorResponse.toString());
            }
        });
    }
    // Send an API request to get the timeline json
    // Fill the listview by creating the tweet objects from the json
    private void populateTimeline() {
        if (Util.isOnline(this) == true) {
            // is online, get data from net
            client.getHomeTimeline(new JsonHttpResponseHandler() {
                // SUCCESS
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    // parse data and save into adapter
                    handleHomeTimelineResponse(response);
                }

                // FAILURE
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("my", "get home timeline API failed, " + errorResponse.toString());
                    // since api fail, get data from db
                    populateTimelineFromDB();
                }
            });
        } else {
            //  Log.d("my", "use db");
            populateTimelineFromDB();
        }
    }

    public void rePopulateTimeline() {
        if (Util.isOnline(this) == false) {
            // if not online
            return ;
        }
        client.getFilter().resetHomeTimeline();
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            // SUCCESS
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("my", response.toString());
                // reset
                tweetsArrayAdapter.clear();
                resetDB();
                // parse data and save into adapter
                handleHomeTimelineResponse(response);
                // move the scroll position to the top
                lvTweets.setSelection(0);
                // stop refreshing
                swipeRefreshLayout.setRefreshing(false);
            }

            // FAILURE
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("my", "re-get home timeline API failed" + errorResponse.toString());
                // since api fail, get data from db
                populateTimelineFromDB();
            }
        });
    }

    public void populateTimelineFromDB() {
        Log.d("my", "Get tweets from DB");
        ArrayList<Tweet> dbTweets = db.getAllTweets();
        tweetsArrayAdapter.addAll(dbTweets);
    }

    public void handleHomeTimelineResponse(JSONArray response) {
        // create tweets
        ArrayList<Tweet> newTweets= Tweet.createFromJSONArray(response);
        // update adapter
        tweetsArrayAdapter.addAll(newTweets);
        // update filter for pagination
        client.getFilter().updateHomeMaxId(newTweets);
        // save new tweets
        saveAllTweetsToDB(newTweets);
    }

    public void resetDB() {
        db.deleteAll();
        resetDB = false;
    }

    public void saveAllTweetsToDB(ArrayList<Tweet> newTweets) {
        resetDB();
        for(int i = 0; i < newTweets.size(); i++) {
            db.insertTweet(newTweets.get(i));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    public void onClickPost(MenuItem mi) {
        if (Util.isOnline(this)) {
            PostDialog postDialog = PostDialog.newInstance(this);
            postDialog.show(getFragmentManager(), "post dialog");
        } else {
            Toast.makeText(this, "Please go online first", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }
}
