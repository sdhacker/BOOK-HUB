package com.gmonetix.bookspivot.util;

/**
 * Created by gaura on 9/4/2017.
 */

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

public class Share {
    public static boolean share(final MenuItem item,
                                final Activity activity)
    {
        final Intent intent =
                new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,
                "Check out the android app <app-name> "
                        + "<android zoom url>");
        activity.startActivity(
                Intent.createChooser(intent,    "Share with"));
        return true;
    }
}