package com.sinzeforever.mysimpletweets.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.sinzeforever.mysimpletweets.R;
import com.sinzeforever.mysimpletweets.activities.DetailDialog;
import com.sinzeforever.mysimpletweets.libs.Util;
import com.sinzeforever.mysimpletweets.models.Tweet;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

/**
 * Created by sinze on 8/7/15.
 */
public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {
    Context context;
    public TweetsArrayAdapter(Context context, List<Tweet> objects) {
        super(context, 0, objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Tweet tweet = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.tweet_item, parent, false);
        }

        // image
        ImageView ivUserIcon = (ImageView) convertView.findViewById(R.id.ivUserIcon);
        if (tweet.getUser().getProfileImageUrl() != null) {
            // insert the image using picasso
            Transformation transformation = new RoundedTransformationBuilder()
                    .borderWidthDp(1)
                    .borderColor(Color.parseColor("#f0f0f0"))
                    .cornerRadiusDp(30)
                    .oval(false)
                    .build();
            Picasso.with(getContext())
                    .load(tweet.getUser().getProfileImageUrl())
                    .placeholder(R.drawable.ic_user)
                    .error(R.drawable.ic_user)
                    .fit()
                    .transform(transformation)
                    .into(ivUserIcon);
        } else {
            ivUserIcon.setImageResource(R.drawable.ic_user);
        }

        // Text Body
        TextView tvText = (TextView) convertView.findViewById(R.id.tvText);
        tvText.setText(tweet.getText());

        // User Name
        TextView tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
        tvUserName.setText(tweet.getUser().getName());

        // set post time
        TextView tvPostTime = (TextView) convertView.findViewById(R.id.tvPostTime);
        tvPostTime.setText(tweet.getPostTimeText());

        // set click
        final RelativeLayout rlTweetCard = (RelativeLayout) convertView.findViewById(R.id.rlTweetCard);
        rlTweetCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onClickTweet(tweet);
            }
        });

        return convertView;
    }

    public void onClickTweet(Tweet tweet) {
        if (Util.isOnline(context)) {
            DetailDialog detailDialog = DetailDialog.newInstance(context, tweet);
            detailDialog.show(((Activity) context).getFragmentManager(), "tweeet detail");
        }
    }
}
