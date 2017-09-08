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
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import net.basov.metro.Lookup;
import net.basov.metro.Ticket;
import net.basov.nfc.NFCTools;
import net.basov.util.FileIO;
import net.basov.util.AppDetails;

import java.io.File;

public class WebViewJSCallback {
	
	public static final String TAG = "WebViewJSCallback";

    private final static String EMA = "ticket-info-dump";
    private final static String EMA_DOM = "basov.net";

    private Context mContext;

    WebViewJSCallback(Context c) {
        mContext = c;
    }

    @JavascriptInterface
    public void launchPreferences() {
        Intent i = new Intent(mContext, AppPreferencesActivity.class);
        mContext.startActivity(i);
    }

    @JavascriptInterface
    public void launchHelp() {
        Intent i = new Intent();
        i.setAction(mContext.getPackageName()+".LAUNCH_HELP");
        mContext.startActivity(i);
    }

    @JavascriptInterface
    public void launchFileManager() {
        Intent intent = new Intent();

        SharedPreferences defSharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        if (defSharedPref.getBoolean(mContext.getString(R.string.pk_use_view_directory), false)) {
            /* For ES File Manager and X-plore File Manager */
            intent.setAction("org.openintents.action.VIEW_DIRECTORY");
        } else {
            /* For OI File Manager */
            intent.setAction("android.intent.action.VIEW");
        }

        Uri uri = Uri.parse("file://" + FileIO.getFilesDir(mContext).getAbsolutePath() + "/AutoDumps/");
        intent.setData(uri);
        mContext.startActivity(Intent.createChooser(intent, "Open/Share saved dumps"));
        //mContext.startActivity(intent);
    }

    @JavascriptInterface
    public void sendDump(String fileName, String parserErrors) {
        SharedPreferences defSharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);

        if (fileName != null && fileName.length() != 0) {
            File fileLocation = new File(FileIO.getFilesDir(mContext).getAbsolutePath(), fileName);
            Uri path = Uri.fromFile(fileLocation);
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{EMA + "@" + EMA_DOM});
            String appInfo = mContext.getResources().getString(R.string.app_name);
            try {
                appInfo += " " + AppDetails.getAppName(mContext);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            i.putExtra(Intent.EXTRA_SUBJECT, appInfo + ". Dump: " + fileName);
            String emaText = mContext.getString(R.string.ema_text);
            String emaInfo = "";
            if (parserErrors != null && parserErrors.length() != 0) {
                emaInfo += "--- Parser errors ---\n";
                emaInfo += parserErrors;
                emaInfo += "\n--- End of parse errors ---\n";
            }
            if (defSharedPref.getBoolean(mContext.getString(R.string.pk_send_platform_info), true)) {
                emaInfo += "--- Platform information ---\n";
                emaInfo += " Manufacturer: " + Build.MANUFACTURER + "\n";
                emaInfo += " Device: " + android.os.Build.DEVICE + "\n";
                emaInfo += " Model (and Product): " + android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")\n";
                emaInfo += " Mifare Classic support: ";
                if (NFCTools.deviceSupportsMifareClassic(mContext))
                    emaInfo +="yes\n";
                else
                    emaInfo +="no\n";
                emaInfo += " OS API Level: " + android.os.Build.VERSION.SDK_INT + "\n";
                emaInfo += " Android version : " + android.os.Build.VERSION.RELEASE + "\n";
                emaInfo += " Build.DISPLAY: " + android.os.Build.DISPLAY + "\n";
				String cmVersion = AppDetails.getSystemProperty("ro.cm.version");
				if (cmVersion.length() != 0)
					emaInfo += " CyanogenMod version: " + cmVersion + "\n";
                emaInfo += "--- End of platform information ---\n";
                emaInfo += "--- Application information ---\n";
                emaInfo += appInfo + "\n";
                String DFPath = Ticket.getDataFileURIasString(mContext);
                emaInfo += " Data file URI: " + DFPath + "\n";
                emaInfo += " DB timestamp: " + Lookup.findDBts(DFPath) + "\n";
                emaInfo += " DB provider: " + Lookup.findDBprovider(DFPath) + "\n";
                emaInfo += "--- End of Application information ---\n";
            }
            if (emaInfo.length() != 0)
                emaText += "\n\n--- Don't edit after this line, please ---\n" + emaInfo;
            i.putExtra(Intent.EXTRA_TEXT, emaText);
            i.putExtra(Intent.EXTRA_STREAM, path);
            try {
                mContext.startActivity(Intent.createChooser(i, mContext.getString(R.string.send_dump_by_email)));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(mContext, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @JavascriptInterface
    public void appendRemark(String fileName, String remark) {
        File file = new File(FileIO.getFilesDir(mContext).getAbsolutePath(), fileName);
        if (FileIO.appendRemarkToDump(file, remark))
            Toast.makeText(mContext, "Remark added to dump file.", Toast.LENGTH_SHORT).show();
    }

}
