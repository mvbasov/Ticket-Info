/**
 * The MIT License (MIT)

 Copyright (c) 2017 Mikhail Basov

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 */

package net.basov.ticketinfo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import java.io.File;

public class WebViewJSCallback {

    private final static String EMA = "ticket-info-dump";
    private final static String EMA_DOM = "basov.net";

    private Context mContext;

    WebViewJSCallback(Context c) {
        mContext = c;
    }

    @JavascriptInterface
    public void launchPreferences() {
        // TODO: Move settings to apropriate place
        Intent i = new Intent(mContext, AppPreferencesActivity.class);
        mContext.startActivity(i);
    }

    @JavascriptInterface
    public void sendDump(String fileName) {
        if (fileName != null && fileName.length() != 0) {
            String filename = "AutoDumps/" + fileName;
            File filelocation = new File(mContext.getExternalFilesDir(null).getAbsolutePath(), filename);
            Uri path = Uri.fromFile(filelocation);
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{EMA + "@" + EMA_DOM});
            i.putExtra(Intent.EXTRA_SUBJECT, "Ticket-Info dump: " + fileName);
            i.putExtra(Intent.EXTRA_TEXT, mContext.getString(R.string.ema_text));
            i.putExtra(Intent.EXTRA_STREAM, path);
            try {
                mContext.startActivity(Intent.createChooser(i, mContext.getString(R.string.send_dump_by_email)));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(mContext, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
