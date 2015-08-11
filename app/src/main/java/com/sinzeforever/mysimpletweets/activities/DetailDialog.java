package com.sinzeforever.mysimpletweets.activities;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.sinzeforever.mysimpletweets.R;
import com.sinzeforever.mysimpletweets.models.Tweet;
import com.sinzeforever.mysimpletweets.models.TwitterApplication;
import com.sinzeforever.mysimpletweets.models.TwitterClient;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by sinze on 8/8/15.
 */
public class DetailDialog extends DialogFragment {
    Context context;
    Tweet tweet;
    TextView tvUserName;
    TextView tvText;
    TextView tvPostTime;
    TextView tvUserLocation;
    TextView tvReplyBtn;
    EditText etReply;
    ImageView ivUserIcon;
    TwitterClient client;

    public static DetailDialog newInstance(Context context, Tweet tweet) {
        DetailDialog fragment = new DetailDialog();
        fragment.context = context;
        fragment.tweet = tweet;
        fragment.client = TwitterApplication.getRestClient();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // set up dialog window
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        View view = inflater.inflate(R.layout.detail_dialog, container);
        tvText = (TextView) view.findViewById(R.id.tvDetailText);
        tvUserName = (TextView) view.findViewById(R.id.tvDetailUserName);
        tvPostTime = (TextView) view.findViewById(R.id.tvDetailPostTime);
        ivUserIcon = (ImageView) view.findViewById(R.id.ivDetailUserIcon);
        tvUserLocation = (TextView) view.findViewById(R.id.tvDetailUserLocation);
        etReply = (EditText) view.findViewById(R.id.etReply);
        tvReplyBtn = (TextView) view.findViewById(R.id.tvReplayBtn);
                // set user icon
        // insert the image using picasso
        Transformation transformation = new RoundedTransformationBuilder()
                .borderWidthDp(1)
                .borderColor(Color.parseColor("#f0f0f0"))
                .cornerRadiusDp(30)
                .oval(false)
                .build();
        Picasso.with(context)
                .load(tweet.getUser().getProfileImageUrl())
                .placeholder(R.drawable.ic_user)
                .error(R.drawable.ic_user)
                .fit()
                .transform(transformation)
                .into(ivUserIcon);

        // set text body
        tvText.setText(tweet.getText());
        // set user name
        tvUserName.setText(tweet.getUser().getName());
        // set user location
        if (tweet.getUser().getLocation().length() > 0) {
            tvUserLocation.setText("@" + tweet.getUser().getLocation());
        } else {
            tvUserLocation.setVisibility(View.GONE);
        }
        // set post time
        tvPostTime.setText(tweet.getPostTimeRaw());

        // set up reply input
        tvReplyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etReply.getText().toString().length() > 0) {
                    String replyString = "@" + tweet.getUser().getName() + " " + etReply.getText().toString();
                    client.postReplyTweet(new JsonHttpResponseHandler() {
                        // SUCCESS
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.d("my", "reply succeed");
                            Toast.makeText(context, "Tweet Successfully", Toast.LENGTH_SHORT).show();
                            ((TimelineActivity) context).rePopulateTimeline();
                            dismiss();
                        }

                        // FAILURE
                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Log.d("my", "reply failed");
                        }
                    }, replyString, tweet.getId());
                }
            }
        });


        // set up media images
        if (tweet.getImageUrls() != null) {
            Log.d("my", "tweet url count   " + tweet.getImageUrls().size());
            for (int i = 0; i < tweet.getImageUrls().size(); i++) {
                ImageView ivTweetImg = new ImageView(context);
                ivTweetImg.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                ivTweetImg.setBackgroundColor(Color.BLACK);
                ivTweetImg.setAdjustViewBounds(true);
                ivTweetImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
                Log.d("my", "tweet url: " + tweet.getImageUrls());
                Picasso.with(context)
                        .load(tweet.getImageUrls().get(i))
                        .into(ivTweetImg);

                // insert into images layout
                ViewGroup llTweetImgWrapper = (ViewGroup) view.findViewById(R.id.llTweetImgWrapper);
                llTweetImgWrapper.addView(ivTweetImg);
            }
        }
        return view;
    }
}
