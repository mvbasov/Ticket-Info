package net.basov.ticketinfo;

/**
 * Created by mvb on 6/15/17.
 * New version of UI based on WebView
 */
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.Toast;

import net.basov.metro.Ticket;
import net.basov.nfc.NFCaDump;
import net.basov.util.FileIO;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends Activity {

    static final String TAG = "tickets";

    private WebView mainUI_WV;
    private NfcAdapter adapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] filters;
    private NFCaDump d;
    private String[][] techList;
    private static Context c;
    private String welcome_json;
    private String msg_json;
    private String ticket_json;
    private String ticket_visibility_json;
    private String ic_json;
    private String ic_visibility_json;
    private String header_json;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        c = this;
        d = new NFCaDump();
        setContentView(R.layout.webview_ui);
        mainUI_WV = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = mainUI_WV.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        try {

            /**
             * AIDE has limited support of gradle.
             * If program compiled by AndroidStudio R.string.git_describe is set by gradle.
             * If program compiled by AIDE this resource doesn't exist.
             */
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            int git_describe_id = getResources().getIdentifier("git_describe", "string", getPackageName());
            String title;
            if (git_describe_id == 0)
                title = getResources().getString(R.string.app_name)
                        + " "
                        + pInfo.versionName
                        + "-AIDE";
            else {
                String git_describe = getResources().getString(git_describe_id);
                if (!git_describe.isEmpty())
                    title = getResources().getString(R.string.app_name)
                            + " "
                            + git_describe;
                else
                    title = getResources().getString(R.string.app_name)
                            + " "
                            + pInfo.versionName;
            }
            welcome_json = "{ " +
                "\"w_header\":\"" + title + "\"" +
                "}";
            fillUI(mainUI_WV, "start.html", welcome_json);

        } catch (Throwable th) {
            Log.e(TAG, "get package info error", th);
        }

        onNewIntent(getIntent());
// TODO: Check NFC is switched on
        adapter = NfcAdapter.getDefaultAdapter(this);
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

//        header_json = "{"
//            +"\"h_number\":\"0003123881\","
//            +"\"h_state\":\"<font color=\\\\\"violet\\\\\">DebugAPP</font>\""
//            +"}";
//
//        ticket_json = "{"
//            +"\"t_valid_days\":\"1\","
//            +"\"t_from_datetime\":\"07.06.2017 16:03\","
//            +"\"t_to_datetime\":\"08.06.2017 16:03\","
//            +"\"t_start_use\":\"08.06.2017 at 00:00\","
//            +"\"t_ic_uid\":\"0123456789abcdef\""
//            +"}";
//
//        ticket_visibility_json = "{"
//            +"\"vt_note\":null,"
//            +"\"vt_trip\":null,"
//            +"\"vt_station\":null"
//            +"}";
//
//        ic_json = "{"
//            +"\"i_manufacturer\":\"JSC Micron Russia\","
//            +"\"i_ic_std_bytes\":\"164\","
//            +"\"i_names\":\""
//            +"  MIK1312ED\\\\n"
//            +"  aka К5016ВГ4Н4\\\\n"
//            +"  aka K5016XC1M1H4\","
//            +"\"i_read_pages\":\"41\","
//            +"\"i_read_bytes\":\"164\","
//            +"\"i_read_sig\":\""
//            +"  00000000000000000000000000000000\\\\n"
//            +"  00000000000000000000000000000000\","
//            +"\"i_tech\":\"nfcA\""
//            +"}";
//
//        ic_visibility_json = "{"
//                +"\"vi_get_version\":null,"
//                +"\"vi_read_sig\":null,"
//                +"\"vi_counters\":null"
//                +"}";
//
//        mainUI_WV.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(mainUI_WV, url);
//
//                view.loadUrl("javascript:jreplace('" + header_json +"')");
//
//                view.loadUrl("javascript:jreplace('" + ticket_json +"')");
//                view.loadUrl("javascript:jvisible('" + ticket_visibility_json +"')");
//
//                view.loadUrl("javascript:jreplace('" + ic_json +"')");
//                view.loadUrl("javascript:jvisible('" + ic_visibility_json +"')");
//
//                mainUI_WV.clearCache(true);
//            }
//        });
//
//        mainUI_WV.loadUrl("file:///android_asset/webview_ui.html");
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.enableForegroundDispatch(this, pendingIntent, filters, techList);
    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.disableForegroundDispatch(this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (intent != null && intent.getAction().equals(NfcAdapter.ACTION_TECH_DISCOVERED)) {
            try {
                Bundle extras = intent.getExtras();
                Tag tag = (Tag) extras.get(NfcAdapter.EXTRA_TAG);

                final String[] techList = tag.getTechList();

                // TODO: remove debug toast
                Toast.makeText(
                        this,
                        "Chip UID: "
                                +byteArrayToHexString(tag.getId()),
                        Toast.LENGTH_LONG
                ).show();

                final NfcA nfca = NfcA.get(tag);
                msg_json = "{" +
                        "\"error_msg\":\""+getString(R.string.ticket_is_reading)+"\"," +
                        "\"visibility\":[" +
                        "\"error_msg\"" +
                        "]" +
                        "}";
                fillUI(mainUI_WV, "start.html", msg_json);




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
                            Ticket t = new Ticket(dump);
                            StringBuilder sb = new StringBuilder();

                            if (dump.getPagesNumber() < 12) {

                                sb.append("!!! Card read partially.\n");
                                sb.append("!!! Decoding ticket information impossible.\n");
                                sb.append("!!! Try to read again.\n");
                                sb.append("- - - -\n");

                            } else {
                                if (FileIO.writeAutoDump(dump)) {
                                    sb.append("Dump saved\n");
                                    Toast toast = Toast.makeText(c, "Dump saved.", Toast.LENGTH_LONG);
                                    toast.show();
                                    sb.append("\n- - - -\n");
                                } else {
                                    try {
                                        NFCaDump d_tmp = new NFCaDump();
                                        String storage = MainActivity.getAppContext()
                                                .getExternalFilesDir(null)
                                                .getAbsolutePath();
                                        String fName = storage +
                                                "/AutoDumps/" +
                                                Ticket.createDumpFileName(t) +
                                                ".txt";
                                        if (FileIO.ReadDump(d_tmp, fName)) {
                                            sb.append("Existing dump comment:\n");
                                            sb.append(d_tmp.getRemark());
                                            sb.append("\n- - - -\n");
                                        }
                                    } catch (NullPointerException e) {
                                        sb.append("Dump exist but not readable\n");
                                    }
                                }
                                sb.append(t.getTicketAsString(c));
                            }

                            sb.append(dump.getMemoryInfoAsString());
                            sb.append(dump.getUIDCheckAsString());
                            sb.append(dump.getIC_InfoAsString());
                            sb.append(dump.getDetectedICTypeAsString());
                            sb.append(dump.getDumpAsDetailedString());
                            // REDESIGN
                            //text.setText(sb.toString());

                        } else {
                            msg_json = "{" +
                                    "\"error_msg\":\""+getString(R.string.ticket_read_error)+"\"," +
                                    "\"visibility\":[" +
                                    "\"error_msg\"" +
                                    "]" +
                                    "}";
                            fillUI(mainUI_WV, "start.html", msg_json);
                            Log.e(TAG, "dump err");
                        }
                    }
                }.execute(nfca);

            } catch (Throwable th) {
                msg_json = "{" +
                        "\"error_msg\":\""+getString(R.string.ticket_read_error)+"\"," +
                        "\"visibility\":[" +
                        "\"error_msg\"" +
                        "]" +
                        "}";
                fillUI(mainUI_WV, "start.html", msg_json);
                Log.e(TAG, "read err", th);
            }
        } else if((intent.getAction().equals(Intent.ACTION_SEND)
                || intent.getAction().equals(Intent.ACTION_VIEW))
                && intent.getType().startsWith("text/")){

            Uri rcvUri = null;

            if (intent.getAction().equals(Intent.ACTION_SEND)) {
                rcvUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            } else if (intent.getAction().equals(Intent.ACTION_VIEW)){
                rcvUri = intent.getData();
            }

            if (rcvUri != null) {
                FileIO.ReadDump(d, rcvUri.getPath());
                if (d.getReadFrom()==NFCaDump.READ_FROM_FILE) {
                    StringBuilder sb = new StringBuilder();
                    Ticket t;
                    if (d.getDDD() != null) {
                        ArrayList<Integer> tmpDump = new ArrayList<Integer>();

                        for (int i = 0; i < 12; i++) {
                            tmpDump.add(d.getPageAsInt(i));
                        }

                        t = new Ticket(tmpDump, d.getDDD());
                        t.setDDDRem(d.getDDDRem());
                    } else {
                        t = new Ticket(d);
                    }
                    if (d.getRemark().length() != 0){
                        sb.append("File: ");
                        sb.append(rcvUri.getLastPathSegment());
                        sb.append("\n");
                        sb.append(d.getRemark());
                        sb.append("\n- - - -\n");
                    }
                    if (d.getPagesNumber() < 12) {
                        sb.append("!!! Dump partial.\n");
                        sb.append("!!! Decoding ticket information impossible.\n");
                        sb.append("- - - -\n");
                    } else {
                        sb.append(t.getTicketAsString(c));
                    }
                    sb.append(d.getMemoryInfoAsString());
                    sb.append(d.getUIDCheckAsString());
                    sb.append(d.getIC_InfoAsString());
                    sb.append(d.getDetectedICTypeAsString());
                    sb.append(d.getDumpAsDetailedString());
                    // REDESIGN
                    //text.setText(sb.toString());
                }
            }
        } else {
            msg_json = "{" +
                    "\"error_msg\":\""+getString(R.string.ticket_disclaimer)+"\"," +
                    "\"visibility\":[" +
                    "\"error_msg\"" +
                    "]" +
                    "}";
            fillUI(mainUI_WV, "start.html", msg_json);
        }
    }
    public static Context getAppContext() {
        return c;
    }

    // TODO: move to more logicaly sutable place. Introduced to debug UID print.
    //http://stackoverflow.com/a/13006907
    public static String byteArrayToHexString(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public static void fillUI(final WebView wv, String page, final String json) {
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(wv, url);

                view.loadUrl("javascript:jreplace('" + json +"')");

                wv.clearCache(true);
            }
        });
        wv.loadUrl("file:///android_asset/"+page);
    }
}
