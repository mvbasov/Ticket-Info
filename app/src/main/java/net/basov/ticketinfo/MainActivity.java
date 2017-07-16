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

/**
 * Created by mvb on 6/15/17.
 * New version of UI based on WebView
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.widget.EditText;
import android.widget.Toast;

import net.basov.metro.Ticket;
import net.basov.nfc.NFCaDump;
import net.basov.util.FileIO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {

    static final String TAG = "tickets";
    static final Integer NFC_DIALOG_REQUEST_CODE = 653;

    private WebView mainUI_WV;
    private NfcAdapter adapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] filters;
    private List<String> d_file_content;
    private String d_remark;
    private String d_auto_file_name;
    private String d_real_file_name;
    private NFCaDump d;
    private String[][] techList;
    private String app_title;
    private String currentLang;

    private UI ui;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String appLangPref = sharedPref.getString("appLang", "en");
        switch (appLangPref) {
            case "ru":
            case "en":
                Locale locale = new Locale(appLangPref);
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config,
                        getBaseContext().getResources().getDisplayMetrics());
                break;
            default:
                break;
        }
        currentLang = appLangPref;

        d = new NFCaDump();
        ui = new UI();

        setContentView(R.layout.webview_ui);
        mainUI_WV = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = mainUI_WV.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        /* Enable JavaScript */
        webSettings.setJavaScriptEnabled(true);
        mainUI_WV.addJavascriptInterface(new WebViewJSCallback(this), "Android");
        /* Show external page in browser */
        mainUI_WV.setWebViewClient(new MyWebViewClient());
        /* Handle JavaScript prompt doalog */
        mainUI_WV.setWebChromeClient(new myWebChromeClient());

        /* Enable chome remote debuging for WebView (Ctrl-Shift-I) */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE))
            { WebView.setWebContentsDebuggingEnabled(true); }
        }


        try {

            /*
             * AIDE has limited support of gradle.
             * If program compiled by AndroidStudio R.string.git_describe is set by gradle.
             * If program compiled by AIDE this resource doesn't exist.
             */
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            int git_describe_id = getResources().getIdentifier("git_describe", "string", getPackageName());
            if (git_describe_id == 0)
                app_title = getResources().getString(R.string.app_name)
                        + " "
                        + pInfo.versionName
                        + "-AIDE";
            else {
                String git_describe = getResources().getString(git_describe_id);
                if (!git_describe.isEmpty())
                    app_title = getResources().getString(R.string.app_name)
                            + " "
                            + git_describe;
                else
                    app_title = getResources().getString(R.string.app_name)
                            + " "
                            + pInfo.versionName;
            }
            ui.displayUI(app_title, d_file_content, d_auto_file_name, d_real_file_name, d_remark, mainUI_WV);

        } catch (Throwable th) {
            Log.e(TAG, "get package info error", th);
        }

        onNewIntent(getIntent());

/*
  Check is NFC adapter present and switched on
  */
        adapter = NfcAdapter.getDefaultAdapter(this);
        if ((adapter != null) && !adapter.isEnabled()) {

            AlertDialog.Builder nfcEnableDialog = new AlertDialog.Builder(MainActivity.this);
            nfcEnableDialog.setTitle(R.string.nfc_enable_dialog_title);
            nfcEnableDialog.setMessage(getString(R.string.nfc_enable_dialog));
            nfcEnableDialog.setPositiveButton(R.string.nfc_dialog_go_btn, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                startActivityForResult(intent, NFC_DIALOG_REQUEST_CODE);
                }

            });

            nfcEnableDialog.setNegativeButton(R.string.nfc_dialog_cancel_btn, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            nfcEnableDialog.show();

        }

        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        try {
            filter.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            Log.e(TAG, "Add data type fail", e);
        }
        filters = new IntentFilter[]{filter};
        techList = new String[][]{new String[]{NfcA.class.getName()}};

    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.enableForegroundDispatch(this, pendingIntent, filters, techList);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String resumeLang = sharedPref.getString("appLang", "en");
        if (!currentLang.equals(resumeLang)) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
//TODO: remove debug
//            Toast.makeText(
//                    MainActivity.this,
//                    "Main activity recreated because language changed",
//                    Toast.LENGTH_SHORT
//            ).show();
        }
        if (sharedPref.getBoolean("changed", true)) {
            sharedPref.edit().putBoolean("changed", false).apply();
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (adapter != null)
            adapter.disableForegroundDispatch(this);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        currentLang = sharedPref.getString("appLang", "en");

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        //outState.putString("app_lang", sharedPref.getString("appLang", "en"));
        outState.putString("app_title", app_title);
        outState.putStringArrayList("d_file_content", (ArrayList<String>) d_file_content);
        outState.putString("d_auto_file_name", d_auto_file_name);
        outState.putString("d_real_file_name", d_real_file_name);
        outState.putString("d_remark", d_remark);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        d_file_content = savedInstanceState.getStringArrayList("d_file_content");
        d_auto_file_name = savedInstanceState.getString("d_auto_file_name");
        d_real_file_name = savedInstanceState.getString("d_real_file_name");
        d_remark = savedInstanceState.getString("d_remark");
        app_title = savedInstanceState.getString("app_title");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NFC_DIALOG_REQUEST_CODE) {
            /* Process NFC enable dialog */
            ui.displayWelcomeByNFC(mainUI_WV);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        /* NFC tag read event */
        if (intent != null && intent.getAction().equals(NfcAdapter.ACTION_TECH_DISCOVERED)) {
            try {
                Bundle extras = intent.getExtras();
                Tag tag = (Tag) extras.get(NfcAdapter.EXTRA_TAG);

                final String[] techList = tag.getTechList();

                // TODO: remove debug toast
//                Toast.makeText(
//                        this,
//                        "Chip UID: "
//                                +byteArrayToHexString(tag.getId()),
//                        Toast.LENGTH_LONG
//                ).show();

                final NfcA nfca = NfcA.get(tag);

                // Reading new tag. Clean previous content.
                d_file_content = null;
                d_remark = null;
                d_auto_file_name = null;
                d_real_file_name = null;

                ui.setWelcome("w_msg", getString(R.string.ticket_is_reading));
                ui.displayWelcomeScreen(mainUI_WV);

                new AsyncTask<NfcA, Void, NFCaDump>() {

                    @Override
                    protected NFCaDump doInBackground(NfcA... paramss) {
                        try {
                            d = new NFCaDump();

                            nfca.connect();

                            d.setReadFrom(NFCaDump.READ_FROM_NFC);
                            d.readATQA(nfca);
                            d.readSAK(nfca);
                            d.readPages(nfca);

                            nfca.close();
/*
                            If try to read Mifare 1K on devices without support this tag type 0 blocks returned
                            If try to read using NfcA library Mifare 1K on devices with support only one block with one byte returned
*/
                            if (d.isPagesEmpty())
                                return null;

                            if ((d.getPage(0)[0] == (byte)0x04 && d.getPagesNumber() == 20) ||     // MF0ULx1 (80 bytes)
                                    (d.getPage(0)[0] == (byte)0x34 && d.getPagesNumber() == 44)) {  // MIK1312ED (164 bytes)

                                nfca.connect();
                                // Separate connect()/close() block of calls
                                // which supported not by all cards

                                d.readVERSION(nfca);
                                d.readCounters(nfca);
                                d.readSIGN(nfca);

                                nfca.close();
                            }

                            for (String aTechList : techList) {
                                d.addAndTechList(aTechList);
                            }
                            return d;
                        } catch (IOException ie) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(NFCaDump dump) {
                        if (dump != null) {
                            ui.dataClean();

                            d_file_content = new ArrayList<String>();
                            for (String line : NFCaDump.getDumpAsString(dump).split("\\r?\\n"))
                                d_file_content.add(line);
                            d_auto_file_name = Ticket.createAutoDumpFileName(new Ticket(dump));

                            if (dump.getPagesNumber() < 12) {
                                // TODO: display somthig interesting
                            } else {
                                if (FileIO.writeAutoDump(dump, MainActivity.this)) {
                                    Toast.makeText(
                                            MainActivity.this,
                                            "Dump saved.",
                                            Toast.LENGTH_LONG
                                            ).show();
                                } else {
                                    NFCaDump d_tmp = new NFCaDump();
                                    try {
                                        String storage =
                                                FileIO.getFilesDir(MainActivity.this)
                                                .getAbsolutePath();
                                        String fName = storage +
                                                "/" +
                                                d_auto_file_name +
                                                Ticket.FILE_EXT;
                                        if (FileIO.ReadDump(d_tmp, fName)) {
                                            dump.setRemark(d_tmp.getRemark());
                                            d_remark = d_tmp.getRemark();
                                            //d_real_file_name = t.getRealFileName();
                                        }
                                    } catch (NullPointerException e) {
                                        // TODO: display somthig interesting
                                    }
                                }
                            }
                            // TODO: remove debug
                            //Toast.makeText(MainActivity.this,"Read IC Intent",Toast.LENGTH_SHORT).show();

                            ui.displayUI(app_title, d_file_content, d_auto_file_name, d_real_file_name, d_remark, mainUI_WV);

                        } else {
                            ui.setWelcome("w_msg", getString(R.string.ticket_read_error));
                            ui.displayWelcomeScreen(mainUI_WV);
                            Log.e(TAG, "dump err");
                        }
                    }
                }.execute(nfca);

            } catch (Throwable th) {
                ui.setWelcome("w_msg", getString(R.string.ticket_read_error));
                ui.displayWelcomeScreen(mainUI_WV);
                Log.e(TAG, "read err", th);
            }
        /* Dump shared by another application */
        } else if((intent.getAction().equals(Intent.ACTION_SEND)
                || intent.getAction().equals(Intent.ACTION_VIEW))
                && intent.getType().startsWith("text/")) {

            Uri rcvUri = null;

            if (intent.getAction().equals(Intent.ACTION_SEND)) {
                rcvUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            } else if (intent.getAction().equals(Intent.ACTION_VIEW)) {
                rcvUri = intent.getData();
            }

            if (rcvUri != null) {
                FileIO.ReadDump(d, rcvUri.getPath());
                if (d.getReadFrom() == NFCaDump.READ_FROM_FILE) {

                    d_file_content = new ArrayList<String>();
                    for (String line : NFCaDump.getDumpAsString(d).split("\\r?\\n"))
                        d_file_content.add(line);

                    d_remark = d.getRemark();

                    int pathLength =
                            FileIO.getFilesDir(MainActivity.this)
                                    .getAbsolutePath().length() + 1;
                    d_real_file_name = rcvUri.getPath().substring(pathLength);

                    d_auto_file_name = Ticket.createAutoDumpFileName(new Ticket(d));

                    if (d.getPagesNumber() < 12) {
                        // TODO: display something interesting
                    }

                    // TODO: remove debug
                    //Toast.makeText(MainActivity.this,"Read file Intent",Toast.LENGTH_SHORT).show();
                    ui.displayUI(app_title, d_file_content, d_auto_file_name, d_real_file_name, d_remark, mainUI_WV);

                }
            }
        } else if (intent.getAction().equals("net.basov.ticketinfo.LAUNCH_HELP")) {

            /* Handle BACK button */
            mainUI_WV.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if(event.getAction() == KeyEvent.ACTION_DOWN) {
                        WebView webView = (WebView) v;
                        switch(keyCode) {
                            case KeyEvent.KEYCODE_BACK:
                                if(webView.canGoBack()) {
                                    //webView.goBack();
                                    ui.displayUI(app_title, d_file_content, d_auto_file_name, d_real_file_name, d_remark, mainUI_WV);
                                    return true;
                                }
                                break;
                        }
                    }
                    return false;
                }
            });

            ui.displayHelpScreen(mainUI_WV);

        } else {
            /* Other intent ??? */
            // TODO: remove debug
            //Toast.makeText(MainActivity.this,"Other Intent",Toast.LENGTH_SHORT).show();
            ui.displayUI(app_title, d_file_content, d_auto_file_name, d_real_file_name, d_remark, mainUI_WV);
        }
    }

    /**
     * Handle JavaScript prompt() dialogue
     */
    private class myWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsPrompt(
                WebView view,
                String url,
                String message,
                String defaultValue,
                final JsPromptResult result) {

            final EditText input = new EditText(MainActivity.this);

            new AlertDialog.Builder(MainActivity.this)
                    .setView(input)
                    .setTitle(R.string.add_remark_to_dump)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    String value = input.getText().toString();
                                    result.confirm(value);
                                }
                            })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    result.cancel();
                                }
                            })
                    .setOnCancelListener(
                            new DialogInterface.OnCancelListener() {
                                public void onCancel(DialogInterface dialog) {
                                    result.cancel();
                                }
                            })
                    .show();

            return true;
        }

    }

}

