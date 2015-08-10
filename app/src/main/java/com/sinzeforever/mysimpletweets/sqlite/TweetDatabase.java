package com.sinzeforever.mysimpletweets.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sinzeforever.mysimpletweets.models.Tweet;

import java.util.ArrayList;

/**
 * Created by sinze on 8/8/15.
 */
public class TweetDatabase extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "twiiterDatabase";
    private static final String TABLE_TWEET = "table_tweet";
    private static final String KEY_ID = "id";
    private static final String KEY_BODY = "body";
    private static final String KEY_USERNAME = "user_name";
    private static final String KEY_USERLOCATION = "user_location";
    private static final String KEY_POST_TIME = "post_time";

    public TweetDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TODO_TABLE = "CREATE TABLE " + TABLE_TWEET + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_BODY + " TEXT,"
                + KEY_USERLOCATION + " TEXT," + KEY_USERNAME + " TEXT,"
                + KEY_POST_TIME + " TEXT)";
        db.execSQL(CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Wipe older tables if existed
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TWEET);
            // Create tables again
            onCreate(db);
        }
    }

    public void insertTweet(Tweet tweet) {
        SQLiteDatabase db = this.getWritableDatabase();
        // put values
        ContentValues values = new ContentValues();
        values.put(KEY_ID, tweet.getId());
        values.put(KEY_BODY, tweet.getText());
        values.put(KEY_USERNAME, tweet.getUser().getName());
        values.put(KEY_USERLOCATION, tweet.getUser().getLocation());
        values.put(KEY_POST_TIME, tweet.getPostTimeRaw());
        // insert db
        db.insertOrThrow(TABLE_TWEET, null, values);
        // Closing database connection
        db.close();
    }

    public Tweet getTweet(int id) {
        // Open database for reading
        SQLiteDatabase db = this.getReadableDatabase();
        // Construct and execute query
        Cursor cursor = db.query(TABLE_TWEET,  // TABLE
                new String[] { KEY_ID, KEY_BODY, KEY_USERNAME, KEY_USERLOCATION, KEY_POST_TIME}, // SELECT
                KEY_ID + "= ?", new String[] { String.valueOf(id) },  // WHERE, ARGS
                null, null, "id DESC", null); // GROUP BY, HAVING, ORDER BY, LIMIT
        if (cursor != null)
            cursor.moveToFirst();
        // Load result into model object
        Tweet tweet = Tweet.createFromCursor(cursor);

        // Close the cursor
        if (cursor != null)
            cursor.close();

        return tweet;
    }

    public ArrayList<Tweet> getAllTweets() {
        ArrayList<Tweet> tweets = new ArrayList<Tweet>();
        // Select All Query

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + KEY_ID + ", " + KEY_BODY + ", " + KEY_USERNAME + ", " + KEY_USERLOCATION + ", " + KEY_POST_TIME + " FROM " + TABLE_TWEET;

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Tweet tweet = Tweet.createFromCursor(cursor);
                tweets.add(tweet);
            } while (cursor.moveToNext());
        }
        // Close the cursor
        if (cursor != null)
            cursor.close();

        // return todo list
        return tweets;
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(TABLE_TWEET, "1", null);
        db.close();
    }
}
