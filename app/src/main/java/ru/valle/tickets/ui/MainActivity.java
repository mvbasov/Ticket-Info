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
    private DateFormat df;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        text = (TextView) findViewById(R.id.body);
        text.setMovementMethod(new ScrollingMovementMethod());
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            this.setTitle(getResources().getString(R.string.app_name) + " " + pInfo.versionName);
        } catch (Throwable th) {
            Log.e(TAG, "get package info error", th);
        }

        df = new SimpleDateFormat("dd.MM.yyyy");
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
        df = new SimpleDateFormat("dd.MM.yyyy");
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
                AsyncTask<NfcA, Void, String> execute = new AsyncTask<NfcA, Void, String>() {

                    @Override
                    protected String doInBackground(NfcA... paramss) {
                        try {

                            Dump dump = new Dump();

                            nfca.connect();

                            dump.setATQA(nfca.getAtqa());
                            dump.setSAK((byte) nfca.getSak());

                            for (int i = 0; i < (Dump.MAX_PAGES/4) + 1; i++) {
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
                                    dump.addPagesBlock( answer );
                                } catch (IOException ignored0) {
                                    break; // this 4 pages block totally out of band
                                }
                            }

/*
                            If try to read Mifare 1K on devices without support this tag type 0 blocks returned
                            If try to read using NfcA library Mifare 1K on devices with support only one block with one byte returned
*/
                            if (dump.isPagesEmpty())
                                return getString(R.string.unsupported_tag_type);

                            nfca.close();
/*
                            Read answer to GET_VERSION command.
                            This code mast be wrapped around by connect/close.
                            In other case it lead strange side effects.
                            It found experimentally.
*/
                            nfca.connect();
                            dump.setVERSIONisEmpty();
                            if ((dump.getPage(0)[0] == (byte)0x04 && dump.getPagesNumber() == 20) ||     // MF0ULx1 (80 bytes)
                                    (dump.getPage(0)[0] == (byte)0x34 && dump.getPagesNumber() == 44)) {  // MIK1312ED (164 bytes)
                                try {
                                    byte[] cmd_ver = {0x60};
                                    dump.setVersionInfo(nfca.transceive(cmd_ver));
                                    dump.setVERSIONisEmpty(false);
                                } catch (IOException ignored1) {
                                    dump.setVERSIONisEmpty();
                                }
                                try {
                                    byte[] cmd_read_cnt = {(byte)0x39, (byte)0x00 };
                                    for (int i = 0; i < 3; i++) {
                                        cmd_read_cnt[1] = (byte)i;
                                        dump.addCounter(nfca.transceive(cmd_read_cnt));
                                    }
                                } catch (IOException ignored2) { }
/* TODO: delete this debug code from here: -BEGIN- [Increment counter]
                                // The following code need only for test counter increment functionality.
                                // Doesn't need to dump functionality.
                                // WARNING! Counters are one way!
                                try {
                                        byte[] cmd_incr_cnt = {
                                            (byte)0xa5, 
                                            (byte)0x00,  // increment counter 0
                                            // LSB 1-st, 4-th byte need but ignored.
                                            // 0 increment is valid but has no effect.
                                            (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
                                            
                                    nfca.transceive(cmd_incr_cnt);
                                } catch (IOException ignored3) {}
TODO: to here. -END- [Increment counter]*/
                                dump.setSIGNisEmpty();
                                try {
                                    byte[] cmd_read_sign = {
                                        (byte)0x3c,
                                        (byte)0x00};
                                    dump.setSIGN(nfca.transceive(cmd_read_sign));
                                    dump.setSIGNisEmpty(false);

                                } catch (IOException ignored4) {
                                    dump.setSIGNisEmpty();
                                }
                            }

                            nfca.close();

                            for (int i = 0; i < techList.length; i++) {
                                dump.addAndTechList(techList[i]);
                            }


                            return decodeUltralight(dump);
                        } catch (IOException ie) {
                            return getString(R.string.ticket_read_error);
                        }
                    }

                    @Override
                    protected void onPostExecute(String result) {
// TODO: Write continuous (optional) log
                        text.setText(result);
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

    public String decodeUltralight(Dump dump) {

        StringBuilder sb = new StringBuilder();

        if (dump.getPagesNumber() < 12) {

            sb.append("!!! Card read partially.\n");
            sb.append("!!! Decoding ticket information impossible.\n");
            sb.append("!!! Try to read again.\n");
            sb.append("- - - -\n");

        } else {

            sb.append(Decode.getAppIdDesc(this, dump.getPageAsInt(4) >>> 22)).append('\n');
            sb.append(Decode.descCardType(this, (dump.getPageAsInt(4) >>> 12) & 0x3ff)).append('\n');
            sb.append("- - - -\n");

            int mask12 = 0;
            for (int i = 0; i < 12; i++) {
                mask12 <<= 1;
                mask12 |= 1;
            }

            sb.append(getString(R.string.ticket_num)).append(' ');
            sb.append(String.format("%010d\n",
                    (((dump.getPageAsInt(4) & mask12) << 20) | (dump.getPageAsInt(5) >>> 12)) & 0xffffffffL));
            sb.append(getString(R.string.issued)).append(": ");
            sb.append(getReadableDate((dump.getPageAsInt(8) >>> 16) & 0xffff)).append('\n');
            sb.append(getString(R.string.start_use_before)).append(": ");
            sb.append(getReadableDate(dump.getPageAsInt(6) >>> 16)).append('\n');
            sb.append(getString(R.string.best_in_days)).append(": ");
            sb.append((dump.getPageAsInt(8) >>> 8) & 0xff).append('\n');
            sb.append("- - - -\n");
            sb.append(getString(R.string.passes_left)).append(": ");
            sb.append((dump.getPageAsInt(9) >>> 16) & 0xff).append('\n');

            int cardLayout = dump.getPage(5)[2] & 0xF;
            switch (cardLayout) {
                case 8:
                    if ((dump.getPageAsInt(9) & 0xffff) != 0) {
                        sb.append(getString(R.string.station_last_enter)).append(": ");
                        sb.append(getGateDesc(dump.getPageAsInt(9) & 0xffff)).append('\n');
                    }
                    sb.append("- - - -\n");
                    sb.append("Layuot 8 (0x8).").append('\n');
                    break;
                case 13:
                    if ((dump.getPageAsInt(9) & 0xffff) != 0) {
                        sb.append(getString(R.string.last_enter_date)).append(": \n  ");
                        sb.append(getReadableDate((dump.getPageAsInt(11) >>> 16))).append(" ");
                        sb.append(getString(R.string.at)).append(String.format(" %02d:%02d,\n  ",
                                ((dump.getPageAsInt(11) & 0xfff0) >>> 5) / 60,
                                ((dump.getPageAsInt(11) & 0xfff0) >>> 5) % 60));
                        sb.append(getString(R.string.station_last_enter)).append(" ");
                        sb.append(getGateDesc(dump.getPageAsInt(9) & 0xffff)).append('\n');
                    }
                    sb.append("- - - -\n");
                    sb.append("Layuot 13 (0xd).").append('\n');
                    break;

                default:
                    sb.append(getString(R.string.unknown_layout)).append(": ").append(cardLayout).append('\n');
                    break;
            }

            sb.append(String.format("App ID: %d (0x%03x), ",
                    dump.getPageAsInt(4) >>> 22,
                    dump.getPageAsInt(4) >>> 22));
            sb.append(String.format("Type: %d (0x%03x)\n",
                    (dump.getPageAsInt(4) >>> 12) & 0x3ff,
                    (dump.getPageAsInt(4) >>> 12) & 0x3ff));

            sb.append(getString(R.string.ticket_hash)).append(": ");
            sb.append(Integer.toHexString(dump.getPageAsInt(10))).append('\n');
            sb.append(getString(R.string.otp)).append(": ");
            sb.append(Integer.toBinaryString(dump.getPageAsInt(3))).append('\n');
        }

        sb.append(dump.getIC_InfoAsString());
        sb.append(dump.getDumpAsString());

        return sb.toString();
    }

    private String getReadableDate(int days) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(1992, 0, 0);
        c.add(Calendar.DATE, days);
        return df.format(c.getTime());
    }

    private String getGateDesc(int id) {
        String SN = Lang.tarnliterate(Decode.getStationName(id));
        if (SN.length() != 0) {
            return "№" + id + "\n  " + getString(R.string.station) + " " + Lang.tarnliterate(Decode.getStationName(id));
        } else {
            return "№" + id;
        }
    }

}
