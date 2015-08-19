package com.sinzeforever.mysimpletweets.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.sinzeforever.mysimpletweets.R;
import com.sinzeforever.mysimpletweets.adapters.TweetsArrayAdapter;
import com.sinzeforever.mysimpletweets.libs.EndlessScrollListener;
import com.sinzeforever.mysimpletweets.libs.Util;
import com.sinzeforever.mysimpletweets.models.Tweet;
import com.sinzeforever.mysimpletweets.models.TwitterClient;

import java.util.ArrayList;

public class TimelineFragment extends Fragment {
    protected TweetsArrayAdapter tweetsArrayAdapter;
    protected TwitterClient client;
    protected OnFragmentInteractionListener mListener;
    protected ArrayList<Tweet> tweets;
    protected ListView lvTweets;
    protected View rootView;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected ProgressBar progressBarFooter;
    private  LayoutInflater inflater;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpTweetAdapter();
        setUpSwipeAndRefreshLayout();
        //set up progress bar
        setUpProgressBar();
        rePopulateTimeline();
    }

    protected void setUpProgressBar() {
        // Inflate the footer
        View footer = inflater.inflate(
                R.layout.footer_progress, null);
        // Find the progressbar within footer
        progressBarFooter = (ProgressBar)
                footer.findViewById(R.id.pbFooterLoading);
        // Add footer to ListView before setting adapter
        lvTweets.addFooterView(footer);
    }

    protected void showProgressBar() {
        progressBarFooter.setVisibility(View.VISIBLE);
    }

    protected void hideProgressBar() {
        progressBarFooter.setVisibility(View.GONE);
    }

    private void setUpTweetAdapter() {
        // find list view
        lvTweets = (ListView) rootView.findViewById(R.id.lvTweets);
        // create array
        tweets = new ArrayList<>();
        // construct the adapter to from data source
        tweetsArrayAdapter = new TweetsArrayAdapter(getActivity(), tweets);
        // set adapter
        lvTweets.setAdapter(tweetsArrayAdapter);
        // set infinite scroll events
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (Util.isOnline(getActivity()) == true) {
                    populateTimeline();
                }
            }
        });
    }

    public void populateTimeline() {}

    public void rePopulateTimeline() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setUpSwipeAndRefreshLayout() {
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.slTimeline);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Util.isOnline(getActivity())) {
                    rePopulateTimeline();
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        rootView = inflater.inflate(R.layout.fragment_timeline, container, false);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction();
    }

}
