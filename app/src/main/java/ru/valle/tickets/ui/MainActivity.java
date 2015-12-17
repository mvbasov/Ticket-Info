/**
 * The MIT License (MIT)

 Copyright (c) 2015 Mikhail Basov
 Copyright (c) 2013 Valentin Konovalov

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

package ru.valle.tickets.ui;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.content.pm.PackageInfo;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import ru.valle.tickets.R;

public final class MainActivity extends Activity {

    static final String TAG = "tickets";

    private TextView text;
    private NfcAdapter adapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] filters;
    private String[][] techList;
    //private DateFormat df;
    private Context c;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        c = this;
        setContentView(R.layout.main);
        text = (TextView) findViewById(R.id.body);
        text.setMovementMethod(new ScrollingMovementMethod());
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            this.setTitle(getResources().getString(R.string.app_name) + " " + pInfo.versionName);
        } catch (Throwable th) {
            Log.e(TAG, "get package info error", th);
        }
// TODO: Do I need get/set date format here?
        //df = new SimpleDateFormat("dd.MM.yyyy");
        onNewIntent(getIntent());
// TODO: Check NFC is switched on  
        adapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        try {
            filter.addDataType("*/*");
        } catch (MalformedMimeTypeException e) {
            Log.e(TAG, "Add data type fail", e);
        }
        filters = new IntentFilter[]{filter};
        techList = new String[][]{new String[]{NfcA.class.getName()}};
    }

    @Override
    public void onResume() {
        super.onResume();
// TODO: Do I need get/set date format here?
        //df = new SimpleDateFormat("dd.MM.yyyy");
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

                final NfcA nfca = NfcA.get(tag);
                text.setText(getString(R.string.ticket_is_reading));
                AsyncTask<NfcA, Void, NFCaDump> execute = new AsyncTask<NfcA, Void, NFCaDump>() {

                    @Override
                    protected NFCaDump doInBackground(NfcA... paramss) {
                        try {

                            NFCaDump d = new NFCaDump();

                            nfca.connect();

                            d.setATQA(nfca.getAtqa());
                            d.setSAK((byte) nfca.getSak());

                            for (int i = 0; i < (NFCaDump.MAX_PAGES/4) + 1; i++) {
                                byte[] cmd = {0x30, (byte) (i * 4)};
                                try {
/*
                                    Read 4 pages (4 bytes each) block.
                                    Throw IOException if index of 1-st page out of band.
                                    Wrap around p0 if try to read more then exists pages.
                                    According to manufacturer data sheets read 4 pages of 4 bytes,
                                    i.e. exactly 16 bytes in any case.
                                    But, on some devices (Sony Xperia Z1 with Android 5.1.1, for example)
                                    last block with only one byte generated.
                                    Other devices (Samsung Galaxy S IV with Andpoid 5.0.1, for example)
                                    lead manufacturer description.
*/
                                    byte[] answer = nfca.transceive(cmd);
                                    if (answer.length != 16) {
                                        break;  //   Only 16 bytes blocks are valid
                                    }
                                    d.addPagesBlock( answer );
                                } catch (IOException ignored0) {
                                    break; // this 4 pages block totally out of band
                                }
                            }

/*
                            If try to read Mifare 1K on devices without support this tag type 0 blocks returned
                            If try to read using NfcA library Mifare 1K on devices with support only one block with one byte returned
*/
                            if (d.isPagesEmpty())
                                return null;

                            nfca.close();
/*
                            Read answer to GET_VERSION command.
                            This code mast be wrapped around by connect/close.
                            In other case it lead strange side effects.
                            It found experimentally.
                            If you wish to get more precesion card detection
                            you need to remove memory size check around
                            GET_VERSION(0x60), READ_SIGN(0x3C) and READ_CNT(0x39)
                            commands and little bite rewrite detectIC_Type().
*/
                            nfca.connect();
                            d.setVERSIONisEmpty();
                            if ((d.getPage(0)[0] == (byte)0x04 && d.getPagesNumber() == 20) ||     // MF0ULx1 (80 bytes)
                                    (d.getPage(0)[0] == (byte)0x34 && d.getPagesNumber() == 44)) {  // MIK1312ED (164 bytes)
                                try {
                                    byte[] cmd_ver = {NFCaDump.CMD_GET_VERSION};
                                    d.setVersionInfo(nfca.transceive(cmd_ver));
                                    d.setVERSIONisEmpty(false);
                                } catch (IOException ignored1) {
                                    d.setVERSIONisEmpty();
                                }
                                try {
                                    byte[] cmd_read_cnt = {
                                            NFCaDump.CMD_READ_CNT,
                                            (byte)0x00 };
                                    for (int i = 0; i < 3; i++) {
                                        cmd_read_cnt[1] = (byte)i;
                                        d.addCounter(nfca.transceive(cmd_read_cnt));
                                    }
                                } catch (IOException ignored2) { }
/* TODO: delete this debug code from here: -BEGIN- [Increment counter]
                                // The following code need only for test counter increment functionality.
                                // Doesn't need to dump functionality.
                                // WARNING! Counters are one way!
                                try {
                                        byte[] cmd_incr_cnt = {
                                            NFCaDump.CMD_INCR_CNT,
                                            (byte)0x00,  // increment counter 0
                                            // LSB 1-st, 4-th byte need but ignored.
                                            // 0 increment is valid but has no effect.
                                            (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
                                            
                                    nfca.transceive(cmd_incr_cnt);
                                } catch (IOException ignored3) {}
TODO: to here. -END- [Increment counter]*/
                                d.setSIGNisEmpty();
                                try {
                                    byte[] cmd_read_sign = {
                                        NFCaDump.CMD_READ_SIGN,
                                        (byte)0x00}; // according to data sheet
                                    d.setSIGN(nfca.transceive(cmd_read_sign));
                                    d.setSIGNisEmpty(false);

                                } catch (IOException ignored4) {
                                    d.setSIGNisEmpty();
                                }
                            }

                            nfca.close();

                            for (int i = 0; i < techList.length; i++) {
                                d.addAndTechList(techList[i]);
                            }


                            return d;
                        } catch (IOException ie) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(NFCaDump dump) {
                        if (dump != null) {
// TODO: Write continuous (optional) log
                            Ticket t = new Ticket(dump);
                            StringBuilder sb = new StringBuilder();

                            if (dump.getPagesNumber() < 12) {

                                sb.append("!!! Card read partially.\n");
                                sb.append("!!! Decoding ticket information impossible.\n");
                                sb.append("!!! Try to read again.\n");
                                sb.append("- - - -\n");

                            } else {
                                sb.append(t.getTicketAsString(c));
                            }

                            sb.append(dump.getIC_InfoAsString());
                            sb.append(dump.getDumpAsString());
                            text.setText(sb.toString());

                        } else {
                            text.setText(getString(R.string.ticket_read_error));
                        }
                    }
                }.execute(nfca);

            } catch (Throwable th) {
                text.setText(getString(R.string.ticket_read_error));
                Log.e(TAG, "read err", th);
            }
        } else {
            text.setText(getString(R.string.ticket_disclaimer));
        }
    }
}
