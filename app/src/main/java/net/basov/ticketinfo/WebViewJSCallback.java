package net.basov.ticketinfo;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

public class WebViewJSCallback {

    Context mContext;

    WebViewJSCallback(Context c) {
        mContext = c;
    }

    @JavascriptInterface
    public void launchPreferences() {
        // TODO: Move settings to apropriate place
        Intent i = new Intent(mContext, AppPreferencesActivity.class);
        mContext.startActivity(i);
    }
}
