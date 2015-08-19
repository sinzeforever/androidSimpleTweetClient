package com.sinzeforever.mysimpletweets.models;

import android.content.Context;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "h8cp86yWpOMECJCieN9choySE";       // Change this
	public static final String REST_CONSUMER_SECRET = "bolRU4WcljBBi5Bfc7225oE1ABO8FVHLx1jMgGlSgZXzp171Gy"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://cpsimpletweets"; // Change this (here and in manifest)

    private TwitterClientFilter filter;

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
        filter = new TwitterClientFilter();
	}

    public TwitterClientFilter getFilter() {
        return filter;
    }

    // End Points Methods
    // Get Home Timeline /statuses/home_timeline.json
    // count = 25
    // since_id=1
    public void getHomeTimeline(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/home_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", filter.getHomeCount());
        params.put("exclude_replies", false);
        Long maxId = filter.getHomeMaxIdId();
        if (maxId > 0) {
            params.put("max_id", maxId);
        }
        // Execute Request
        getClient().get(apiUrl, params, handler);
    }

    // Get Mention Timeline /statuses/mentions_timeline.json
    // count = 25
    // since_id=1
    public void getMentionTimeline(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", filter.getMentionCount());
        params.put("exclude_replies", false);
        Long maxId = filter.getMentionMaxId();
        if (maxId > 0) {
            params.put("max_id", maxId);
        }
        // Execute Request
        getClient().get(apiUrl, params, handler);
    }

    public void getUserTimeline(AsyncHttpResponseHandler handler, String screenName) {
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        RequestParams params = new RequestParams();
        Long maxId = filter.getUserMaxId();
        params.put("count", filter.getUserCount());
        params.put("screen_name", screenName);

        if (maxId > 0) {
            params.put("max_id", maxId);
        }
        // Execute Request
        getClient().get(apiUrl, params, handler);
    }

    public void getUserProfile(AsyncHttpResponseHandler handler) {
        Log.d("my", "call getting user profile api~~~");
        String apiUrl = getApiUrl("account/verify_credentials.json");
        getClient().get(apiUrl, new RequestParams(), handler);
    }

    public void postNewTweet(AsyncHttpResponseHandler handler, String content) {
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", content);
        getClient().post(apiUrl, params, handler);
    }

    public void postReplyTweet(AsyncHttpResponseHandler handler, String content, Long id) {
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", content);
        params.put("in_reply_to_status_id", id);
        getClient().post(apiUrl, params, handler);
    }
}