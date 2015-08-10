package com.sinzeforever.mysimpletweets.models;
import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
/**
 * Created by sinze on 8/7/15.
 */
public class Tweet {
    private String text;
    private String createdAt;
    private ArrayList<String> imageUrls;
    private long id;
    private long replyId = 0;
    private TwitterUser user;

    public String getText() {
        return text;
    }

    public long getId() {
        return id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public TwitterUser getUser() {
        return user;
    }

    public long getReplyId() { return replyId;}

    public ArrayList<String> getImageUrls() { return imageUrls; }

    public static Tweet createFromJSON(JSONObject json) {
        Tweet tweet = new Tweet();
        try {
            tweet.text = json.getString("text");
            tweet.id = json.getLong("id");
            tweet.createdAt = json.getString("created_at");
            // check reply id
            String replyStr = json.getString("in_reply_to_user_id");
            tweet.replyId = (replyStr != null && !replyStr.isEmpty() && !replyStr.equals("null") ?  Long.valueOf(replyStr) : 0);
            tweet.user = new TwitterUser(json.getJSONObject("user"));
            // parse tweet image url
            tweet.imageUrls = new ArrayList<String>();
            JSONObject entities = json.getJSONObject("entities");
            if (entities.has("media")) {
                JSONArray mediaArray = entities.getJSONArray("media");
                for(int i = 0; i < mediaArray.length(); i++) {
                    JSONObject media = mediaArray.getJSONObject(i);
                    tweet.imageUrls.add(media.getString("media_url"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweet;
    }

    public static Tweet createFromCursor(Cursor cursor) {
        Tweet tweet = new Tweet();
        TwitterUser twitterUser = new TwitterUser();
        tweet.id = cursor.getLong(0);
        tweet.text = cursor.getString(1);
        twitterUser.setName(cursor.getString(2));
        twitterUser.setLocation(cursor.getString(3));
        tweet.createdAt = cursor.getString(4);
        tweet.user = twitterUser;

        return tweet;
    }

    public static ArrayList<Tweet> createFromJSONArray(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<Tweet>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject tweetJSON = jsonArray.getJSONObject(i);
                Tweet tweet = Tweet.createFromJSON(tweetJSON);
                if (tweet != null && tweet.getReplyId() <= 0) {
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return tweets;
    }
    public String getPostTimeRaw() {
        return createdAt;
    }


    public String getPostTimeText() {
        final String TWITTER_DATE_FORMAT="EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(TWITTER_DATE_FORMAT, Locale.ENGLISH);
        sf.setLenient(true);
        Date createDate = new Date();
        try {
            createDate = sf.parse(createdAt) ;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // get current timeframe
        Calendar calendar = Calendar.getInstance();
        java.util.Date now = calendar.getTime();
        java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());

        // get the time distance
        Long tmpTime = (currentTimestamp.getTime() -  createDate.getTime()) / 1000;
        int day = (int)(tmpTime / 86400);
        int hour = (int) ((tmpTime % 86400) / 3600);
        int minute = (int) ((tmpTime % 3600) / 60);
        int sec = (int) (tmpTime % 60);

        // string format
        if (day > 0) {
            return day + "d" + hour + "h";
        } else if (hour > 0) {
            return hour + "h" + minute + "m";
        } else if (minute > 0) {
            return minute + "m";
        } else {
            return sec + "s";
        }
    }
}
/*
{
    "coordinates": null,
    "truncated": false,
    "created_at": "Tue Aug 28 21:16:23 +0000 2012",
    "favorited": false,
    "id_str": "240558470661799936",
    "in_reply_to_user_id_str": null,
    "entities": {
        "urls": [],
        "hashtags": [],
        "user_mentions": []
    },
    "text": "just another test",
    "contributors": null,
    "id": 240558470661799936,
    "retweet_count": 0,
    "in_reply_to_status_id_str": null,
    "geo": null,
    "retweeted": false,
    "in_reply_to_user_id": null,
    "place": null,
    "source": "<a href="//realitytechnicians.com\"" rel="\"nofollow\"">OAuth Dancer Reborn</a>",
    "user": {
        "name": "OAuth Dancer",
        "profile_sidebar_fill_color": "DDEEF6",
        "profile_background_tile": true,
        "profile_sidebar_border_color": "C0DEED",
        "profile_image_url": "http://a0.twimg.com/profile_images/730275945/oauth-dancer_normal.jpg",
        "created_at": "Wed Mar 03 19:37:35 +0000 2010",
        "location": "San Francisco, CA",
        "follow_request_sent": false,
        "id_str": "119476949",
        "is_translator": false,
        "profile_link_color": "0084B4",
        "entities": {
            "url": {
                "urls": [
                    {
                        "expanded_url": null,
                        "url": "http://bit.ly/oauth-dancer",
                        "indices": [0, 26],
                        "display_url": null
                    }
                ]
            },
            "description": null
        },
        "default_profile": false,
        "url": "http://bit.ly/oauth-dancer",
        "contributors_enabled": false,
        "favourites_count": 7,
        "utc_offset": null,
        "profile_image_url_https": "https://si0.twimg.com/profile_images/730275945/oauth-dancer_normal.jpg",
        "id": 119476949,
        "listed_count": 1,
        "profile_use_background_image": true,
        "profile_text_color": "333333",
        "followers_count": 28,
        "lang": "en",
        "protected": false,
        "geo_enabled": true,
        "notifications": false,
        "description": "",
        "profile_background_color": "C0DEED",
        "verified": false,
        "time_zone": null,
        "profile_background_image_url_https": "https://si0.twimg.com/profile_background_images/80151733/oauth-dance.png",
        "statuses_count": 166,
        "profile_background_image_url": "http://a0.twimg.com/profile_background_images/80151733/oauth-dance.png",
        "default_profile_image": false,
        "friends_count": 14,
        "following": false,
        "show_all_inline_media": false,
        "screen_name": "oauth_dancer"
    },
    "in_reply_to_screen_name": null,
    "in_reply_to_status_id": null
},
*/