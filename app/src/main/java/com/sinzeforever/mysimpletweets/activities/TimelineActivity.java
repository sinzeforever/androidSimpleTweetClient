package com.sinzeforever.mysimpletweets.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sinzeforever.mysimpletweets.R;
import com.sinzeforever.mysimpletweets.adapters.TimelineFragmentPagerAdapter;
import com.sinzeforever.mysimpletweets.fragments.PostDialog;
import com.sinzeforever.mysimpletweets.fragments.TimelineFragment;
import com.sinzeforever.mysimpletweets.libs.Util;
import com.sinzeforever.mysimpletweets.models.TwitterClient;
import com.sinzeforever.mysimpletweets.models.TwitterUser;
import com.sinzeforever.mysimpletweets.sqlite.TweetDatabase;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

public class TimelineActivity extends ActionBarActivity implements TimelineFragment.OnFragmentInteractionListener{

    private final int POST_DIALOG_CODE = 98;
    private TwitterClient client;
    private TweetDatabase db;
    private boolean resetDB = true;
    private TwitterUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        // set action bar
        setUpActionBar();
        // set up db
        // setUpDatabase();

        // set up pager for home timeline and mention timeline
        setUpPager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // handle networking change
        TextView tvWarning = (TextView) findViewById(R.id.tvWarning);
        if (Util.isOnline(this)) {
            tvWarning.setVisibility(View.GONE);
        } else {
            // show error msg
            tvWarning.setVisibility(View.VISIBLE);
        }
    }

    private  void setUpPager() {
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new TimelineFragmentPagerAdapter(getSupportFragmentManager()));

        // Give the PagerSlidingTabStrip the ViewPager
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(viewPager);
        // setList(MentionTimelineFragment.newInstance());
    }

    private void setUpActionBar() {
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.twitterBlue)));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText("Twitter Home");
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

/*
    public void populateTimelineFromDB() {
        Log.d("my", "Get tweets from DB");
        ArrayList<Tweet> dbTweets = db.getAllTweets();
        tweetsArrayAdapter.addAll(dbTweets);
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
*/

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

    public void viewUserProfile(TwitterUser user) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    // set list fragment
    /*
    private void setList(Fragment pageFragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flMainWindow, pageFragment);
        fragmentTransaction.commit();
    }
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction() {
        // do nothing
        return;
    }
}
