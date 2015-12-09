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
import java.util.ArrayList;
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
                            nfca.connect();

                            byte[] hw_ver = new byte[8];
                            try {
                                byte[] cmd_ver = { 0x60 };
                                hw_ver = nfca.transceive(cmd_ver);
                            } catch (IOException ignored1) {
                                hw_ver[0] = (byte)0x99;
                            }
                            nfca.close();

                            nfca.connect();
                            byte[] atqa = nfca.getAtqa();
                            byte sak = (byte) nfca.getSak();
                            nfca.close();

                            ArrayList<byte[]> readBlocks = new ArrayList<byte[]>();
                            nfca.connect();
                            for (int i = 0; i < 32; i++) {
                                byte[] cmd = {0x30, (byte) (i * 4)};
                                try {
                                    //read 4 pages (4 bytes each) block
                                    //catch IOException if index of page out of band
                                    //wrap around p0 if try to read more then exists pages
                                    readBlocks.add(nfca.transceive(cmd));

//                                    StringBuilder sbx = new StringBuilder();
//                                    for (byte b : readBlocks.get(readBlocks.size() - 1)) {
//                                        sbx.append(String.format("%02x, ", b & 0xff));
//                                    }
//                                    sbx.setLength(sbx.length() - 2);
//                                    Log.d(TAG, String.format("%02x: %s\n", i, sbx));

                                } catch (IOException ignored0) {
                                    break; // this 4 pages block totally out of band
                                }
                            }
                            nfca.close();

                            /*
                            If try to read Mifare 1K on devices without support this tag type 0 blocks returned
                            If try to read using NfcA library Mifare 1K on devices with support only one block with one byte returned
                             */
                            if (readBlocks.isEmpty() || readBlocks.get(0).length == 1) return getString(R.string.unsupported_tag_type);

                            /*
                            On some devices (Sony Xperia Z1 with Android 5.1.1, for example)
                            last block with only one byte generated.
                            On other devices (Smsung Galaxy S IV with Andpoid 5.0.1, for example)
                            it is not true.
                            The following code remove it unnessesary block
                             */
                            if (readBlocks.get(readBlocks.size() - 1).length == 1) {
                                readBlocks.remove(readBlocks.size() - 1);
                            }

                            /*
                            Attempt to exclude wrapped around information
                            from last page.
                            WARNING:
                            Not strong algorithm. It is possible
                            to write id on last page to brake it
                             */
                            int lastBlockValidPages = 0; //last block page valid number
// TODO: Sometime it fails with ArrayIndexOutOfBoundsException: length=1; index=4
                            for (int i = 0; i < 4; i++) {
                                if (readBlocks.get(readBlocks.size() - 1)[i * 4] == readBlocks.get(0)[0]
                                        && readBlocks.get(readBlocks.size() - 1)[i * 4 + 1] == readBlocks.get(0)[1]
                                        && readBlocks.get(readBlocks.size() - 1)[i * 4 + 2] == readBlocks.get(0)[2]
                                        && readBlocks.get(readBlocks.size() - 1)[i * 4 + 3] == readBlocks.get(0)[3]) {
                                    break;
                                }
                                lastBlockValidPages++;
                            }
                            return decodeUltralight(readBlocks, lastBlockValidPages, atqa, sak, hw_ver, techList);
                        } catch (IOException ie) {
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

    public String decodeUltralight(ArrayList<byte []> readBlocks, int lastBlockValidPages, byte[] atqa, byte sak, byte[] hw_ver, String[] techList) {
        String prefix = "android.nfc.tech.";

        if (readBlocks.size() < 4){
            Log.d(TAG,"Tag read partial");
            return "Tag read partial. Try again.";
        }
        int[] pages0 = toIntPages(readBlocks.get(0));
        int[] pages4 = toIntPages(readBlocks.get(1));
        int[] pages8 = toIntPages(readBlocks.get(2));
        int[] pages12 = toIntPages(readBlocks.get(3));

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

        sb.append(getString(R.string.ticket_num)).append(' ').append(String.format("%010d", ( ((p[4] & mask12) << 20) | (p[5] >>> 12)) &  0xffffffffL)).append('\n');
        sb.append(getString(R.string.issued)).append(": ").append(getReadableDate(((p[8] >>> 16) - 1) & 0xffff)).append('\n');
        sb.append(getString(R.string.ticket_blank_best_before)).append(": ").append(getReadableDate(p[6] >>> 16)).append('\n');
        sb.append(getString(R.string.best_in_days)).append(": ").append((p[8] >>> 8) & 0xff).append('\n');
        sb.append("- - - -\n");
        sb.append(getString(R.string.passes_left)).append(": ").append((p[9] >>> 16) & 0xff).append('\n');

        int cardLayout = ((p[5] >> 8) & 0xf);
        switch (cardLayout) {
            case 8:
                if((p[9] & 0xffff) != 0){
                    sb.append(getString(R.string.station_last_enter)).append(": ").append(getGateDesc(p[9] & 0xffff)).append('\n');
                }
                sb.append("- - - -\n");
                sb.append("Layuot 8 (0x08).").append('\n');
                break;
            case 13:
                if((p[9] & 0xffff) != 0){
                    sb.append(getString(R.string.last_enter_date)).append(": \n  ").append(getReadableDate((p[11] >>> 16) - 1)).append(" ");
                    sb.append(getString(R.string.at)).append(String.format(" %02d:%02d,\n  ", ((p[11] & 0xfff0) >>> 5)/60, ((p[11] & 0xfff0) >>> 5) % 60));
                    sb.append(getString(R.string.station_last_enter)).append(" ").append(getGateDesc(p[9] & 0xffff)).append('\n');
                }
                sb.append("- - - -\n");
                sb.append("Layuot 13 (0x0d).").append('\n');
                break;

            default:
                sb.append(getString(R.string.unknown_layout)).append(": ").append(cardLayout).append('\n');
                break;
        }

        byte mf_code = (byte)((p[0] & 0xff000000L) >> 24);
        int int_byte = (int)((p[2] & 0x00ff0000L) >> 16);

        sb.append(getString(R.string.ticket_hash)).append(": ").append(Integer.toHexString(p[10])).append('\n');
        sb.append(getString(R.string.otp)).append(": ").append(Integer.toBinaryString(p[3])).append('\n');
        sb.append(String.format("4 byte pages read: %d (total %d bytes)\n", (readBlocks.size() - 1) * 4 + lastBlockValidPages, ((readBlocks.size() - 1) * 4 + lastBlockValidPages) * 4));
        sb.append("UID: ").append(String.format("%08x %08x\n", p[0], p[1]));
        sb.append(String.format("BCC0: %02x, BCC1: %02x", (p[0] & 0xffL), (p[2] & 0xff000000L) >> 24));

        byte UID_BCC0_CRC = (byte)0x88; // CT byte, allwayse 0x88 in my case
        UID_BCC0_CRC ^= readBlocks.get(0)[0];
        UID_BCC0_CRC ^= readBlocks.get(0)[1];
        UID_BCC0_CRC ^= readBlocks.get(0)[2];
        UID_BCC0_CRC ^= readBlocks.get(0)[3]; //The BCC0 itself, if ok result is 0

        byte UID_BCC1_CRC = (byte)0x00;
        UID_BCC1_CRC ^= readBlocks.get(0)[4];
        UID_BCC1_CRC ^= readBlocks.get(0)[5];
        UID_BCC1_CRC ^= readBlocks.get(0)[6];
        UID_BCC1_CRC ^= readBlocks.get(0)[7];
        UID_BCC1_CRC ^= readBlocks.get(0)[8]; //The BCC1 itself, if ok result is 0

        if (UID_BCC0_CRC == 0 && UID_BCC1_CRC == 0){
            sb.append(" (CRC OK)\n");
        } else {
            sb.append(" (CRC not OK)\n");
        }

        sb.append(String.format("Check BCC0: %02x, BCC1: %02x\n", UID_BCC0_CRC, UID_BCC1_CRC));
        sb.append("Manufacturer internal byte: ").append(String.format("%02x\n", int_byte));
        sb.append(String.format("ATQA: %02x %02x\n", atqa[1], atqa[0]));
        sb.append(String.format("SAK: %02x\n", sak));

        if ( hw_ver[0] == 0x00 ){
            sb.append("GET_VERSION: ");
            for (int i=0; i<hw_ver.length;i++){
                sb.append(String.format("%02x ",hw_ver[i]));
            }
            sb.append("\n");
        }

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
                        sb.append("MIK1312ED(К5016ВГ4Н4 aka K5016XC1M1H4) (164 bytes)");
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

        sb.append("- - - Dump: - - -\n");
        // print all read blocks except last
        for (int l=0;l<readBlocks.size()-1;l++){
            for (int k=0;k<4;k++){
                sb.append(String.format("%02x: ",l*4+k));
                for (int m=0;m<4;m++){
                    sb.append(String.format("%02x ",readBlocks.get(l)[k*4+m]));
                }
                sb.append("\n");
            }
        }
        // print only valid pages of last block
        for (int i=0; i<lastBlockValidPages; i++ ) {
            sb.append(String.format("%02x: ", ((readBlocks.size()-1) * 4 + i)));
            for (int m = 0; m < 4; m++) {
                sb.append(String.format("%02x ", readBlocks.get(readBlocks.size() - 1)[i*4+m]));
            }
            sb.append("\n");
        }
        // warning, because fuzzy algorithm used
        if (lastBlockValidPages != 4)
            sb.append(String.format("---\n[!]Last block valid pages: %d\n", lastBlockValidPages));

        return sb.toString();
    }

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
                    return "№" + id +"\n  " +getString(R.string.station) + " " + Lang.tarnliterate(Decode.getStationName(id));
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
