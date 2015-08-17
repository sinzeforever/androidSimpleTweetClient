package com.sinzeforever.mysimpletweets.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sinzeforever.mysimpletweets.R;
import com.sinzeforever.mysimpletweets.models.TwitterApplication;
import com.sinzeforever.mysimpletweets.models.TwitterClient;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by sinze on 8/7/15.
 */
public class PostDialog extends DialogFragment {
    TextView tvTweetBtn;
    TextView tvTextCount;
    EditText etPostContent;
    TwitterClient client;
    Activity activity;
    int textCount = 0;
    final int TEXT_COUNT_LIMIT = 140;
    public static PostDialog newInstance(Activity activity) {
        PostDialog fragment = new PostDialog();
        fragment.activity = activity;
        fragment.client = TwitterApplication.getRestClient();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(LayoutParams.WRAP_CONTENT, 1300);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.post_dialog, container);

        // set up dialog window
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        // set up tweet button event listener etPostContent.getText().toString()
        etPostContent = (EditText) view.findViewById(R.id.etPostContent);
        tvTextCount = (TextView) view.findViewById(R.id.tvTextCount);
        tvTweetBtn = (TextView) view.findViewById(R.id.tvTweetBtn);
        tvTweetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etPostContent.getText().toString().length() > 0) {
                    client.postNewTweet(new JsonHttpResponseHandler() {
                        // SUCCESS
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.d("my", "post content succeed");
                            Toast.makeText(activity, "Tweet Successfully", Toast.LENGTH_SHORT).show();
                            // ((TimelineActivity) activity).rePopulateTimeline();
                            dismiss();
                        }

                        // FAILURE
                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.d("my", "post content failed");
                        }
                    }, etPostContent.getText().toString());
                }
            }
        });
        etPostContent.setFilters( new InputFilter[] {new InputFilter.LengthFilter(TEXT_COUNT_LIMIT)});
        etPostContent.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){
                textCount = etPostContent.getText().length();
                tvTextCount.setText("(" + String.valueOf(textCount) + "/" + TEXT_COUNT_LIMIT + ")");
            }
        });

        return view;
    }
}
