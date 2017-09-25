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
import android.content.res.Configuration;
import android.content.res.Resources;
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

import net.basov.metro.Lookup;
import net.basov.metro.Ticket;
import net.basov.nfc.NFCaDump;
import net.basov.util.AppDetails;
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
    private String db_ts;
    private String db_provider;

    private UI ui;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Set default preferences at first run and after preferences version upgrade */
        SharedPreferences defSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = defSharedPref.edit();
        switch (defSharedPref.getInt(getString(R.string.pk_pref_version), 0)) {
            case 0: // initial
                editor.putInt(getString(R.string.pk_pref_version), 3);
                editor.putBoolean(getString(R.string.pk_transliterate_flag), false);
                editor.putString(getString(R.string.pk_app_lang), "default");
                editor.putBoolean(getString(R.string.pk_pref_changed), false);
                editor.putBoolean(getString(R.string.pk_send_platform_info), true);
                editor.putBoolean(getString(R.string.pk_use_view_directory), false);
                editor.putString(getString(R.string.pk_dumps_directories), getString(R.string.autodump_directory));
                editor.commit();
                break;
            case 1: // upgrade from v1 to v2
                editor.putInt(getString(R.string.pk_pref_version), 3);
                editor.putBoolean(getString(R.string.pk_use_view_directory), false);
                editor.putString(getString(R.string.pk_dumps_directories), getString(R.string.autodump_directory));
                editor.commit();
                break;
            case 2: // upgrade from v2 to v3
                editor.putInt(getString(R.string.pk_pref_version), 3);
                editor.putString(getString(R.string.pk_dumps_directories), getString(R.string.autodump_directory));
                editor.commit();
            default:
                break;
        }

        // TODO: Remove dirty hack after creating nornmal config for this.
        //editor.putString(getString(R.string.pk_dumps_directories), "AutoDumps;Garbage;NonMetro");
        //editor.commit();




        /* Set application language according to preferences */
        String appLangPref = defSharedPref.getString(
                getString(R.string.pk_app_lang),
                getString(R.string.pref_lang_def)
        );
        Locale locale;
        Configuration config = new Configuration();
        switch (appLangPref) {
            case "ru":
            case "en":
                locale = new Locale(appLangPref);
                Locale.setDefault(locale);
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config,
                        getBaseContext().getResources().getDisplayMetrics());
                break;
            case "default":
                locale = new Locale(Resources.getSystem().getConfiguration().locale.getLanguage());
                Locale.setDefault(locale);
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config,
                        getBaseContext().getResources().getDisplayMetrics());
            default:
                break;
        }

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

        app_title = getResources().getString(R.string.app_name);

        try {
            app_title += " " + AppDetails.getAppName(this);
        } catch (Throwable th) {
            Log.e(TAG, "get package info error", th);
        }

        /* Update DB if need */
        if (Ticket.updateDB(MainActivity.this))
            Toast.makeText(MainActivity.this, "Metro station DB updated.", Toast.LENGTH_SHORT).show();

        /* Get actual DB version information */
        db_ts = Lookup.findDBts(Ticket.getDataFileURIasString(MainActivity.this));
        db_provider = Lookup.findDBprovider(Ticket.getDataFileURIasString(MainActivity.this));

        ui.displayUI(app_title, db_ts, db_provider, d_file_content, d_auto_file_name, d_real_file_name, d_remark, mainUI_WV);

        onNewIntent(getIntent());

        /* Check is NFC adapter present and switched on */
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

        /* Recreate activity if language preferense changed */
        SharedPreferences defSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (defSharedPref.getBoolean(getString(R.string.pk_pref_changed), true)) {
            defSharedPref.edit().putBoolean(getString(R.string.pk_pref_changed), false).apply();
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
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("app_title", app_title);
        outState.putString("db_ts", db_ts);
        outState.putString("db_provider", db_provider);
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
        db_ts = savedInstanceState.getString("db_ts");
        db_provider = savedInstanceState.getString("db_provider");

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
        if (intent == null || intent.getAction() == null){
            ui.setWelcome("w_msg", MainActivity.this.getString(R.string.ticket_read_error)+"(null Intent or getAction())");
            ui.displayWelcomeScreen(mainUI_WV);
        }
        else if (intent != null && intent.getAction().equals(NfcAdapter.ACTION_TECH_DISCOVERED)) {
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
                            String d_file_name = Ticket.createDumpFileName(new Ticket(dump));
                            d_auto_file_name = Ticket.createAutoDumpFileName(new Ticket(dump));

                            if (dump.getPagesNumber() < 12) {
                                // TODO: display somthig interesting
                            } else {

                                SharedPreferences defSharedPref =
                                        PreferenceManager
                                                .getDefaultSharedPreferences(MainActivity.this);

                                String[] dumpsPaths = defSharedPref.getString(
                                                        getString(R.string.pk_dumps_directories),
                                                        getString(R.string.autodump_directory)
                                                ).split(";");

                                String fName = FileIO.findSavedDump(
                                        d_file_name + Ticket.FILE_EXT,
                                        dumpsPaths,
                                        MainActivity.this);

                                if (fName != null) {
                                    NFCaDump d_tmp = new NFCaDump();
                                    try {
                                        String storage =
                                                FileIO.getFilesDir(MainActivity.this)
                                                        .getAbsolutePath();
                                        if (FileIO.ReadDump(d_tmp, storage + "/" + fName)) {
                                            dump.setRemark(d_tmp.getRemark());
                                            d_remark = d_tmp.getRemark();
                                            d_real_file_name = fName;
                                        }
                                    } catch (NullPointerException e) {
                                        // TODO: display somthig interesting
                                    }
                                } else {
                                    FileIO.writeAutoDump(dump, MainActivity.this);
                                    Toast.makeText(
                                            MainActivity.this,
                                            "Dump saved.",
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            }
                            // TODO: remove debug
                            //Toast.makeText(MainActivity.this,"Read IC Intent",Toast.LENGTH_SHORT).show();

                            ui.displayUI(app_title, db_ts, db_provider, d_file_content, d_auto_file_name, d_real_file_name, d_remark, mainUI_WV);

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
                    ui.displayUI(app_title, db_ts, db_provider, d_file_content, d_auto_file_name, d_real_file_name, d_remark, mainUI_WV);

                }
            }
        } else if (intent.getAction().equals(MainActivity.this.getPackageName()+".LAUNCH_HELP")) {

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
                                    ui.displayUI(app_title, db_ts, db_provider, d_file_content, d_auto_file_name, d_real_file_name, d_remark, mainUI_WV);
                                    return true;
                                }
                                break;
                        }
                    }
                    return false;
                }
            });

            ui.displayHelpScreen(app_title, mainUI_WV);

        } else {
            /* Other intent ??? */
            // TODO: remove debug
            //Toast.makeText(MainActivity.this,"Other Intent",Toast.LENGTH_SHORT).show();
            ui.displayUI(app_title, db_ts, db_provider, d_file_content, d_auto_file_name, d_real_file_name, d_remark, mainUI_WV);
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

