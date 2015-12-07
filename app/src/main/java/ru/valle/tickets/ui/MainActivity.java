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
import android.util.Log;
import android.widget.TextView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import ru.valle.tickets.R;
import android.text.method.*;

public final class MainActivity extends Activity {

    static final String TAG = "tickets";
    private TextView text;
    private NfcAdapter adapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] filters;
    private String[][] techLists;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        text = (TextView) findViewById(R.id.body);
        text.setMovementMethod(new ScrollingMovementMethod());
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            this.setTitle(getResources().getString(R.string.app_name)+" "+pInfo.versionName);
        } catch (Throwable th) {
            Log.e(TAG, "get package info error", th);
        }

        df = new SimpleDateFormat("dd.MM.yyyy");
        onNewIntent(getIntent());
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
        techLists = new String[][]{new String[]{NfcA.class.getName()}};
    }

    @Override
    public void onResume() {
        super.onResume();
        df = new SimpleDateFormat("dd.MM.yyyy");
        adapter.enableForegroundDispatch(this, pendingIntent, filters, techLists);
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
                new AsyncTask<NfcA, Void, String>() {

                    @Override
                    protected String doInBackground(NfcA... paramss) {
                        try {
                            nfca.connect();

                            byte[] atqa = nfca.getAtqa();
                            byte sak = (byte)nfca.getSak();

                            byte[] cmd0 = { 0x30, (byte) 0};
                            byte[] pages0bytes = nfca.transceive(cmd0);
                            byte[] cmd4 = { 0x30, (byte) 4};
                            byte[] pages4bytes = nfca.transceive(cmd4);
                            byte[] cmd8 = { 0x30, (byte) 8};
                            byte[] pages8bytes = nfca.transceive(cmd8);
                            byte[] cmd12 = { 0x30, (byte) 12};
                            byte[] pages12bytes = nfca.transceive(cmd12);

                            nfca.close();
                            return decodeUltralight(pages0bytes, pages4bytes, pages8bytes, pages12bytes, atqa, sak, techList);
                        } catch (Throwable th) {
                            return getString(R.string.ticket_read_error);
                        }
                    }

                    @Override
                    protected void onPostExecute(String result) {
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

    public String decodeUltralight(byte[] pages0byte, byte[] pages4byte, byte[] pages8byte, byte[] pages12byte, byte[] atqa, byte sak, String[] techList) {
        String prefix = "android.nfc.tech.";
        int[] pages0 = toIntPages(pages0byte);
        int[] pages4 = toIntPages(pages4byte);
        int[] pages8 = toIntPages(pages8byte);
        int[] pages12 = toIntPages(pages12byte);
        int[] p = {
          pages0[0], pages0[1], pages0[2], pages0[3],
          pages4[0], pages4[1], pages4[2], pages4[3],
          pages8[0], pages8[1], pages8[2], pages8[3],
          pages12[0], pages12[1], pages12[2], pages12[3]
        };
        StringBuilder sb = new StringBuilder();
        sb.append(Decode.getAppIdDesc(this, p[4] >>> 22)).append('\n');
        sb.append(Decode.descCardType(this, (p[4] >>> 12) & 0x3ff)).append('\n');
        sb.append("- - - -\n");
        int mask12 = 0;
        for (int i = 0; i < 12; i++) {
            mask12 <<= 1;
            mask12 |= 1;
        }
        int cardLayout = ((p[5] >> 8) & 0xf);

        sb.append(getString(R.string.ticket_num)).append(' ').append(String.format("%010d", ( ((p[4] & mask12) << 20) | (p[5] >>> 12)) &  0xffffffffL)).append('\n');
        sb.append(getString(R.string.issued)).append(": ").append(getReadableDate(((p[8] >>> 16) - 1) & 0xffff)).append('\n');
        sb.append(getString(R.string.ticket_blank_best_before)).append(": ").append(getReadableDate(p[6] >>> 16)).append('\n');
        sb.append(getString(R.string.best_in_days)).append(": ").append((p[8] >>> 8) & 0xff).append('\n');
        sb.append("- - - -\n");
        sb.append(getString(R.string.passes_left)).append(": ").append((p[9] >>> 16) & 0xff).append('\n');

        switch (cardLayout) {
            case 8:
                if((p[9] & 0xffff) != 0){
                    sb.append(getString(R.string.station_last_enter)).append(": ").append(getGateDesc(p[9] & 0xffff)).append('\n');
                }
                sb.append("Layuot 8 (0x08).").append('\n');
                break;
            case 13:
                if((p[9] & 0xffff) != 0){
                    sb.append(getString(R.string.last_enter_date)).append(": \n").append(getReadableDate((p[11] >>> 16) - 1)).append(" ");
                    sb.append(getString(R.string.at)).append(String.format(" %02d:%02d, ", ((p[11] & 0xfff0) >>> 5)/60, ((p[11] & 0xfff0) >>> 5) % 60));
                    sb.append(getString(R.string.station_last_enter)).append(" ").append(getGateDesc(p[9] & 0xffff)).append('\n');
                }
                sb.append("Layuot 13 (0x0d).").append('\n');
                break;

            default:
                sb.append(getString(R.string.unknown_layout)).append(": ").append(cardLayout).append('\n');
                break;
        }

        byte mf_code = (byte)((p[0] & 0xff000000L) >> 24);
        int int_byte = (int)((p[2] & 0x00ff0000L) >> 16);
        sb.append("- - - -\n");
        sb.append(getString(R.string.ticket_hash)).append(": ").append(Integer.toHexString(p[10])).append('\n');
        sb.append(getString(R.string.otp)).append(": ").append(Integer.toBinaryString(p[3])).append('\n');
        sb.append("UID: ").append(String.format("%08x %08x\n", p[0],p[1]));
        sb.append(String.format("BCC0: %02x, BCC1: %02x\n", (p[0] & 0xffL),(p[2] & 0xff000000L) >> 24));
        sb.append("Manufacturer internal byte: ").append(String.format("%02x\n", int_byte));
        sb.append(String.format("ATQA: %02x %02x\n", atqa[1], atqa[0]));
        sb.append(String.format("SAK: %02x\n", sak));

        sb.append("Andriod technologies: \n   ");
        for (int i = 0; i < techList.length; i++){
            if (i != 0){
                sb.append(", ");
            }
            sb.append(techList[i].substring(prefix.length()));
        }
        sb.append('\n');

        sb.append("Chip manufacturer: ");
        switch (mf_code){
            case 0x04:
                sb.append("NXP Semiconductors (Philips) Germany\n");
                break;
            case 0x34:
                sb.append("JSC Micron Russia\n");
                sb.append("Chip (probably): ");
                switch (int_byte) {
                    case 0x0b:
                        sb.append("MIK64PTAS(MIK640D) (80 bytes)");
                        break;
                    case 0xe0:
                        sb.append("?MIK1312ED?(K5016XC1M1H4) (164 bytes)");
                        break;
                    default:
                        sb.append("Unknown");
                }
                sb.append('\n');
                break;
            default:
                sb.append("Unknown\n");
                break;
        }

        return sb.toString();
    }
    private DateFormat df;

    private String getReadableDate(int days) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(1992, 0, 1);
        c.add(Calendar.DATE, days);
        return df.format(c.getTime());
    }

    private String getGateDesc(int id) {
				String SN=Lang.tarnliterate(Decode.getStationName(id));
				if ( SN.length() != 0 ) {
            return "№" + id +" " +getString(R.string.station) + " " + Lang.tarnliterate(Decode.getStationName(id));
				} else {
						return "№" + id ;
				}
    }

    private static int[] toIntPages(byte[] pagesBytes) {
        int[] pages = new int[pagesBytes.length / 4];
        for (int pp = 0; pp < pages.length; pp++) {
            int page = 0;
            for (int i = 0; i < 4; i++) {
                page <<= 8;
                page |= pagesBytes[pp * 4 + i] & 0xff;
            }
            pages[pp] = page;
        }
        return pages;
    }

}
