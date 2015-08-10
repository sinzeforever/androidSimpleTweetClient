package com.sinzeforever.mysimpletweets.models;
import java.util.ArrayList;

/**
 * Created by sinze on 8/8/15.
 */
public class TwitterClientFilter {
    long homeMaxId = -1;
    int homeCount = 25;

    public void resetHomeTimeline() {
        homeMaxId = -1;
    }

    public int getHomeCount() {
        return homeCount;
    }

    public long getHomeMaxIdId() {
        return homeMaxId;
    }

    public void updateHomeMaxId(long input) {
        if (homeMaxId > input || homeMaxId < 0) {
            homeMaxId = input;
        }
    }

    // update home max id by tweet lists
    public void updateHomeMaxId(ArrayList<Tweet> tweets) {
        for (int i = 0; i < tweets.size(); i++) {
            updateHomeMaxId(tweets.get(i).getId());
        }
    }
}
