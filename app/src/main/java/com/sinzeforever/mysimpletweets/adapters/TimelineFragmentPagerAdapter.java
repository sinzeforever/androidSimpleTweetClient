package com.sinzeforever.mysimpletweets.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentManager;
import com.sinzeforever.mysimpletweets.fragments.HomeTimelineFragment;
import com.sinzeforever.mysimpletweets.fragments.MentionTimelineFragment;

/**
 * Created by sinze on 8/17/15.
 */
public class TimelineFragmentPagerAdapter extends FragmentPagerAdapter {
    private String tabTitles[] = new String[] { "HOME", "MENTIONS" };
    private Fragment fragments[] = new Fragment[] {HomeTimelineFragment.newInstance(), MentionTimelineFragment.newInstance()};
    public TimelineFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
