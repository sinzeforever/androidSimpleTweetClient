package com.sinzeforever.mysimpletweets.models;
import java.util.ArrayList;

/**
 * Created by sinze on 8/8/15.
 */
public class TwitterClientFilter {
    long homeMaxId = -1;
    long mentionMaxId = -1;
    long userMaxId = -1;
    int homeCount = 25;
    int mentionCount = 25;
    int userCount = 25;

    public long getMentionMaxId() {
        return mentionMaxId;
    }

    public int getMentionCount() {
        return mentionCount;
    }

    public void resetHomeTimeline() {
        homeMaxId = -1;
    }

    public void resetMentionTimeline() {
        mentionMaxId = -1;
    }

    public long getUserMaxId() {
        return userMaxId;
    }

    public int getUserCount() {
        return userCount;
    }

    public long getHomeMaxId() {
        return homeMaxId;
    }

    public void resetUserTimeline() {
        userMaxId = -1;
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
        if (tweets.size() > 0) {
            updateHomeMaxId(tweets.get(tweets.size() - 1).getId());
        }
    }

    public void updateMentionMaxId(long input) {
        if (mentionMaxId > input || mentionMaxId < 0) {
            mentionMaxId = input - 1;
        }
    }

    // update home max id by tweet lists
    public void updateMentionMaxId(ArrayList<Tweet> tweets) {
        if (tweets.size() > 0) {
            updateMentionMaxId(tweets.get(tweets.size() - 1).getId());
        }
    }

    public void updateUserMaxId(long input) {
        if (userMaxId > input || userMaxId < 0) {
            userMaxId = input - 1;
        }
    }

    // update home max id by tweet lists
    public void updateUserMaxId(ArrayList<Tweet> tweets) {
        if (tweets.size() > 0) {
            updateUserMaxId(tweets.get(tweets.size() - 1).getId());
        }
    }
}
