package com.sinzeforever.mysimpletweets.activities;

import android.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.sinzeforever.mysimpletweets.R;
import com.sinzeforever.mysimpletweets.fragments.TimelineFragment;
import com.sinzeforever.mysimpletweets.fragments.UserTimelineFragment;
import com.sinzeforever.mysimpletweets.models.TwitterClient;
import com.sinzeforever.mysimpletweets.models.TwitterUser;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends ActionBarActivity implements TimelineFragment.OnFragmentInteractionListener{
    TwitterUser user;
    TwitterClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // parse user
        parseUser();

        // set Action bar
        setUpActionBar();

        // get user timeline
        setUpTimeline();

        // set data
        setProfileData();
    }

    private void setUpActionBar() {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.twitterBlue)));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText("@" + user.getScreenName());
    }

    private void parseUser() {
        user = (TwitterUser) getIntent().getSerializableExtra("user");
    }

    private void setProfileData() {
        // user icon
        ImageView ivUserIcon = (ImageView) findViewById(R.id.ivUserIcon);
        Picasso.with(this)
                .load(user.getProfileImageUrl())
                .fit()
                .into(ivUserIcon);
        // user name
        TextView tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvUserName.setText(user.getName());
        // user screen name
        TextView tvUserScreenName = (TextView) findViewById(R.id.tvUserScreenName);
        tvUserScreenName.setText("@" + user.getScreenName());
        // profile bg img
        ImageView ivProfileBgImg = (ImageView) findViewById(R.id.ivProfileBgImg);
        Picasso.with(this)
                .load(user.getProfileBgImageUrl())
                .fit()
                .into(ivProfileBgImg);
        // tweet count
        TextView tvTweetsCount = (TextView) findViewById(R.id.tvTweetsCount);
        tvTweetsCount.setText(String.valueOf(user.getTweetsCount()));
        // following count
        TextView tvFollowingCount = (TextView) findViewById(R.id.tvFollowingsCount);
        tvFollowingCount.setText(String.valueOf(user.getFollowingCount()));
        // follower count
        TextView tvFollowersCount = (TextView) findViewById(R.id.tvFollowersCount);
        tvFollowersCount.setText(String.valueOf(user.getFollowersCount()));
    }

    private void setUpTimeline() {
        if (getSupportActionBar().isShowing() == true) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.flUserTimeline, UserTimelineFragment.newInstance(user));
            fragmentTransaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction() {
        return;
    }
}
