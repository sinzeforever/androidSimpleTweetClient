package com.sinzeforever.mysimpletweets.libs;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by sinze on 8/9/15.
 */
public class Util {
    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
