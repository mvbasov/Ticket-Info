package net.basov.ticketinfo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import java.io.File;

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

    @JavascriptInterface
    public void sendDump(String fileName) {
        if (fileName.length() != 0 && fileName != null) {
            String filename = "AutoDumps/" + fileName;
            File filelocation = new File(mContext.getExternalFilesDir(null).getAbsolutePath(), filename);
            Uri path = Uri.fromFile(filelocation);
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{"ticket-info-dump@basov.net"});
            i.putExtra(Intent.EXTRA_SUBJECT, "Ticket-Info dump: "+fileName);
            i.putExtra(Intent.EXTRA_TEXT, "Put your comment here if you wish.");
            i.putExtra(Intent.EXTRA_STREAM, path);
            try {
                mContext.startActivity(Intent.createChooser(i, "Send dump by E-Mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(mContext, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
