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

    // Constants definition
    final int MAX_PAGES = 64;

    final byte AC_UNKNOWN = 0;
    final byte FACTORY_LOCKED = AC_UNKNOWN + 1;
    final byte READ_ONLY = AC_UNKNOWN + 2;
    final byte PARTIAL_WRITE = AC_UNKNOWN + 3;
    final byte WRITE = AC_UNKNOWN + 4;
    final byte OTP = AC_UNKNOWN + 5;
    final byte AUTH_REQUIRE = AC_UNKNOWN + 6;
    final byte SPECIAL = AC_UNKNOWN + 7;

    final byte IC_UNKNOWN = 0;
    final byte IC_MF0ICU1 = IC_UNKNOWN + 1;
    final byte IC_MF0ULx1 = IC_UNKNOWN + 2;
    final byte IC_MIK640D = IC_UNKNOWN + 3;
    final byte IC_MIK1312ED = IC_UNKNOWN + 4;

    static final String TAG = "tickets";
    private TextView text;
    private NfcAdapter adapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] filters;
    private String[][] techList;
    private DateFormat df;
    private byte ICType;

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


                            ArrayList<byte[]> readBlocks = new ArrayList<byte[]>();

                            nfca.connect();

                            byte[] atqa = nfca.getAtqa();
                            byte sak = (byte) nfca.getSak();

                            for (int i = 0; i < (MAX_PAGES/4) + 1; i++) {
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
                                    readBlocks.add(nfca.transceive(cmd));
                                    if ( readBlocks.get(readBlocks.size()-1).length != 16 ) {
                                        //Remove wrong block and finish
                                        readBlocks.remove(readBlocks.size()-1);
                                        break;
                                    }

/*
// TODO: delete this debug code from here:
                                    for (byte b : readBlocks.get(readBlocks.size() - 1)) {
                                        sbx.append(String.format("%02x, ", b & 0xff));
                                    StringBuilder sbx = new StringBuilder();
                                    }
                                    sbx.setLength(sbx.length() - 2); // remove last comma
                                    Log.d(TAG, String.format("%02x: %s\n", i, sbx));
// TODO: to here.
*/

                                } catch (IOException ignored0) {
                                    break; // this 4 pages block totally out of band
                                }
                            }

/*
                            If try to read Mifare 1K on devices without support this tag type 0 blocks returned
                            If try to read using NfcA library Mifare 1K on devices with support only one block with one byte returned
*/
                            if (readBlocks.isEmpty()) return getString(R.string.unsupported_tag_type);

                            nfca.close();
/*
                            Read answer to GET_VERSION command.
                            This code mast be wrapped around by connect/close.
                            In other case it lead strange side effects.
                            It found experimentally.
*/
                            nfca.connect();
                            byte[] hw_ver = new byte[8];
                            hw_ver[0] = (byte) 0x77;
                            // Because 1-st byte of normal frame must be 0x00
                            // I use 0x77(initial value) and 0x99(unsuccessful) as flags
                            if ( (readBlocks.get(0)[0] == (byte)0x04 && readBlocks.size() == 5) ||          // MF0ULx1 (80 bytes)
                                    (readBlocks.get(0)[0] == (byte)0x34 && readBlocks.size() == 11)  ) {    // MIK1312ED (164 bytes)
                                try {
                                    byte[] cmd_ver = {0x60};
                                    hw_ver = nfca.transceive(cmd_ver);
                                } catch (IOException ignored1) {
                                    hw_ver[0] = (byte) 0x99; // set unsuccessful flag
                                }
                            }

                            nfca.close();

/*
                            Attempt to exclude wrapped around information from last page.
                            WARNING:
                            Not strong algorithm. It is possible
                            to write id on last page to brake it
*/
                            int lastBlockValidPages = 0; //last block page valid number
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
        sb.append(getString(R.string.issued)).append(": ").append(getReadableDate(((p[8] >>> 16)) & 0xffff)).append('\n');
        sb.append(getString(R.string.start_use_before)).append(": ").append(getReadableDate(p[6] >>> 16)).append('\n');
        sb.append(getString(R.string.best_in_days)).append(": ").append((p[8] >>> 8) & 0xff).append('\n');
        sb.append("- - - -\n");
        sb.append(getString(R.string.passes_left)).append(": ").append((p[9] >>> 16) & 0xff).append('\n');

        int cardLayout = ((p[5] >>> 8) & 0xf);
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
                    sb.append(getString(R.string.last_enter_date)).append(": \n  ").append(getReadableDate((p[11] >>> 16))).append(" ");
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

        byte mf_code = (byte)((p[0] & 0xff000000L) >>> 24);
        int int_byte = (int)((p[2] & 0x00ff0000L) >>> 16);

        sb.append(String.format("App ID: %d\n", p[4] >>> 22));
        sb.append(String.format("Type: %d\n",(p[4] >>> 12) & 0x3ff));

        sb.append(getString(R.string.ticket_hash)).append(": ").append(Integer.toHexString(p[10])).append('\n');
        sb.append(getString(R.string.otp)).append(": ").append(Integer.toBinaryString(p[3])).append('\n');
        sb.append(String.format("4 byte pages read: %d (total %d bytes)\n", (readBlocks.size() - 1) * 4 + lastBlockValidPages, ((readBlocks.size() - 1) * 4 + lastBlockValidPages) * 4));
        sb.append("UID: ").append(String.format("%08x %08x\n", p[0], p[1]));
        sb.append(String.format("BCC0: %02x, BCC1: %02x", (p[0] & 0xffL), (p[2] & 0xff000000L) >> 24));

        byte UID_BCC0_CRC = (byte)0x88; // CT (Cascade Tag) [value 88h] as defined in ISO/IEC 14443-3 Type A
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
                sb.append("Chip: ");
                if (readBlocks.size() == 4) {
                    ICType = IC_MF0ICU1;
                    sb.append("(probably)MF0ICU1 (64 bytes)");
                } else if (hw_ver[0] == (byte)0x00 &&
                        hw_ver[1] == (byte)0x04 &&
                        hw_ver[2] == (byte)0x03 &&
                        hw_ver[4] == (byte)0x01){
                    ICType = IC_MF0ULx1;
                    sb.append("MF0ULx1 (80 bytes)");
/*
                     according to data sheet byte 3 need to be:
                     - 0x01 (17pF version MF0L(1|2)1)
                     - 0x02 (50pF version MF0LH(1|2)1)
                     but all tickets I've seen had 0x03 value in this byte
                     I don't know what does it mead and don't use this
                     byte for identification.
*/
                } else {
                    sb.append("Unknown\n");
                }
                sb.append('\n');
                break;
            case 0x34:
                sb.append("JSC Micron Russia\n");
                sb.append("Chip: ");
                if (readBlocks.size() == 5) {
                    ICType = IC_MIK640D;
                    sb.append("(probably) MIK64PTAS(MIK640D) (80 bytes)");
                } else if (hw_ver[0] == (byte)0x00 &&
                        hw_ver[1] == (byte)0x34 &&
                        hw_ver[2] == (byte)0x21 &&
                        hw_ver[3] == (byte)0x01 &&
                        hw_ver[4] == (byte)0x01 &&
                        hw_ver[5] == (byte)0x00){
                    ICType = IC_MIK1312ED;
                    sb.append("MIK1312ED(К5016ВГ4Н4 aka K5016XC1M1H4) (164 bytes)");
                } else {
                    sb.append("Unknown");
                }
                sb.append('\n');
                break;
            default:
                sb.append("Unknown\n");
                break;
        }

        byte[] lPageAccess = new byte[MAX_PAGES];
        for (int i = 0; i < MAX_PAGES-1; i++){
            lPageAccess[1] = AC_UNKNOWN;
        };
        lPageAccess[0] = FACTORY_LOCKED;
        lPageAccess[1] = FACTORY_LOCKED;
        lPageAccess[2] = (p[2] & 0x90) != 0 ? READ_ONLY : PARTIAL_WRITE;
        lPageAccess[3] = OTP;
        lPageAccess[4] = ((p[2] & 0x1000) | (p[2] & 0x0200)) != 0 ? READ_ONLY : WRITE;
        lPageAccess[5] = ((p[2] & 0x2000) | (p[2] & 0x0200)) != 0 ? READ_ONLY : WRITE;
        lPageAccess[6] = ((p[2] & 0x4000) | (p[2] & 0x0200)) != 0 ? READ_ONLY : WRITE;
        lPageAccess[7] = ((p[2] & 0x8000) | (p[2] & 0x0200)) != 0 ? READ_ONLY : WRITE;
        lPageAccess[8] = ((p[2] & 0x0001) | (p[2] & 0x0200)) != 0 ? READ_ONLY : WRITE;
        lPageAccess[9] = ((p[2] & 0x0002) | (p[2] & 0x0200)) != 0 ? READ_ONLY : WRITE;
        lPageAccess[10] = ((p[2] & 0x0004) | (p[2] & 0x0400)) != 0 ? READ_ONLY : WRITE;
        lPageAccess[11] = ((p[2] & 0x0008) | (p[2] & 0x0400)) != 0 ? READ_ONLY : WRITE;
        lPageAccess[12] = ((p[2] & 0x0010) | (p[2] & 0x0400)) != 0 ? READ_ONLY : WRITE;
        lPageAccess[13] = ((p[2] & 0x0020) | (p[2] & 0x0400)) != 0 ? READ_ONLY : WRITE;
        lPageAccess[14] = ((p[2] & 0x0040) | (p[2] & 0x0400)) != 0 ? READ_ONLY : WRITE;
        lPageAccess[15] = ((p[2] & 0x0080) | (p[2] & 0x0400)) != 0 ? READ_ONLY : WRITE;

        switch (ICType) {
            case IC_MF0ULx1:
                lPageAccess[16] = SPECIAL;
                lPageAccess[17] = SPECIAL;
                lPageAccess[18] = SPECIAL;
                lPageAccess[19] = SPECIAL;
                if (readBlocks.get((int)16/(int)4)[3] != (byte)0xff) {
                    for (int i = (int)(readBlocks.get((int)16/(int)4)[3] & 0x0ffL); i < MAX_PAGES - 1; i++){
                        lPageAccess[i] = AUTH_REQUIRE;
                    }
                }
                break;
            case IC_MIK640D:
                lPageAccess[16] = OTP;
                lPageAccess[17] = OTP;
                lPageAccess[18] = OTP;
                lPageAccess[19] = OTP;
                break;
            case IC_MIK1312ED:
                lPageAccess[16] = ((readBlocks.get((int)36/(int)4)[0] & 0x01) | (readBlocks.get((int)36/(int)4)[2] & 0x01)) != 0 ? READ_ONLY : WRITE;
                lPageAccess[17] = ((readBlocks.get((int)36/(int)4)[0] & 0x01) | (readBlocks.get((int)36/(int)4)[2] & 0x01)) != 0 ? READ_ONLY : WRITE;
                lPageAccess[18] = ((readBlocks.get((int)36/(int)4)[0] & 0x02) | (readBlocks.get((int)36/(int)4)[2] & 0x01)) != 0 ? READ_ONLY : WRITE;
                lPageAccess[19] = ((readBlocks.get((int)36/(int)4)[0] & 0x02) | (readBlocks.get((int)36/(int)4)[2] & 0x01)) != 0 ? READ_ONLY : WRITE;
                lPageAccess[20] = ((readBlocks.get((int)36/(int)4)[0] & 0x04) | (readBlocks.get((int)36/(int)4)[2] & 0x02)) != 0 ? READ_ONLY : WRITE;
                lPageAccess[21] = ((readBlocks.get((int)36/(int)4)[0] & 0x04) | (readBlocks.get((int)36/(int)4)[2] & 0x02)) != 0 ? READ_ONLY : WRITE;
                lPageAccess[22] = ((readBlocks.get((int)36/(int)4)[0] & 0x08) | (readBlocks.get((int)36/(int)4)[2] & 0x02)) != 0 ? READ_ONLY : WRITE;
                lPageAccess[23] = ((readBlocks.get((int)36/(int)4)[0] & 0x08) | (readBlocks.get((int)36/(int)4)[2] & 0x02)) != 0 ? READ_ONLY : WRITE;
                lPageAccess[24] = ((readBlocks.get((int)36/(int)4)[0] & 0x10) | (readBlocks.get((int)36/(int)4)[2] & 0x04)) != 0 ? READ_ONLY : WRITE;
                lPageAccess[25] = ((readBlocks.get((int)36/(int)4)[0] & 0x10) | (readBlocks.get((int)36/(int)4)[2] & 0x04)) != 0 ? READ_ONLY : WRITE;
                lPageAccess[26] = ((readBlocks.get((int)36/(int)4)[0] & 0x20) | (readBlocks.get((int)36/(int)4)[2] & 0x04)) != 0 ? READ_ONLY : WRITE;
                lPageAccess[27] = ((readBlocks.get((int)36/(int)4)[0] & 0x20) | (readBlocks.get((int)36/(int)4)[2] & 0x04)) != 0 ? READ_ONLY : WRITE;
                lPageAccess[28] = ((readBlocks.get((int)36/(int)4)[0] & 0x40) | (readBlocks.get((int)36/(int)4)[2] & 0x08)) != 0 ? READ_ONLY : WRITE;
                lPageAccess[29] = ((readBlocks.get((int)36/(int)4)[0] & 0x40) | (readBlocks.get((int)36/(int)4)[2] & 0x08)) != 0 ? READ_ONLY : WRITE;
                lPageAccess[30] = ((readBlocks.get((int)36/(int)4)[0] & 0x80) | (readBlocks.get((int)36/(int)4)[2] & 0x08)) != 0 ? READ_ONLY : WRITE;
                lPageAccess[31] = ((readBlocks.get((int)36/(int)4)[0] & 0x80) | (readBlocks.get((int)36/(int)4)[2] & 0x08)) != 0 ? READ_ONLY : WRITE;
                lPageAccess[32] = ((readBlocks.get((int)36/(int)4)[1] & 0x01) | (readBlocks.get((int)36/(int)4)[2] & 0x10)) != 0 ? READ_ONLY : WRITE;
                lPageAccess[33] = ((readBlocks.get((int)36/(int)4)[1] & 0x01) | (readBlocks.get((int)36/(int)4)[2] & 0x10)) != 0 ? READ_ONLY : WRITE;
                lPageAccess[34] = ((readBlocks.get((int)36/(int)4)[1] & 0x02) | (readBlocks.get((int)36/(int)4)[2] & 0x10)) != 0 ? READ_ONLY : WRITE;
                lPageAccess[35] = ((readBlocks.get((int)36/(int)4)[1] & 0x02) | (readBlocks.get((int)36/(int)4)[2] & 0x10)) != 0 ? READ_ONLY : WRITE;
                lPageAccess[36] = SPECIAL;
                lPageAccess[37] = SPECIAL;
                lPageAccess[38] = SPECIAL;
                lPageAccess[39] = SPECIAL;
                lPageAccess[40] = SPECIAL;
                if (readBlocks.get((int)37/(int)4)[3] != (byte)0xff) {
                    for (int i = (int)(readBlocks.get((int)37/(int)4)[3] & 0x0ffL); i < MAX_PAGES - 1; i++){
                        lPageAccess[(byte)i] = AUTH_REQUIRE;
                    }
                }
                break;
            default:
                break;
        }


        sb.append("- - - Dump: - - -\n");
        // print all read blocks except last
        for (int l=0;l<readBlocks.size()-1;l++){
            for (int k=0;k<4;k++){
                sb.append(String.format("%02x:%s: ",l*4+k, getAccessAsString(lPageAccess[l*4+k])));
                for (int m=0;m<4;m++){
                    sb.append(String.format("%02x ",readBlocks.get(l)[k*4+m]));
                }
                sb.append("\n");
            }
        }
        // print only valid pages of last block
        for (int i=0; i<lastBlockValidPages; i++ ) {
            sb.append(String.format("%02x:%s: ", (readBlocks.size()-1) * 4 + i, getAccessAsString(lPageAccess[(readBlocks.size()-1) * 4 + i])));
            for (int m = 0; m < 4; m++) {
                sb.append(String.format("%02x ", readBlocks.get(readBlocks.size() - 1)[i*4+m]));
            }
            sb.append("\n");
        }
        // warning, because fuzzy algorithm used
        if (lastBlockValidPages != 4)
            sb.append(String.format("---\n[!]Last block valid pages: %d\n", lastBlockValidPages));
        sb.append("- - - Dump legend: - - -\n");
        sb.append(":u: - unknown\n");
        sb.append(":f: - factory locked\n");
        sb.append(":r: - read only\n");
        sb.append(":p: - partially writable\n");
        sb.append(":o: - One Time Programming (OTP)\n");
        sb.append(":w: - writable\n");
        sb.append(":a: - authentication require for write\n");
        sb.append(":s: - special\n");

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
        String SN=Lang.tarnliterate(Decode.getStationName(id));
        if ( SN.length() != 0 ) {
            return "№" + id +"\n  " +getString(R.string.station) + " " + Lang.tarnliterate(Decode.getStationName(id));
        } else {
            return "№" + id ;
        }
    }

    private String getAccessAsString(byte ac){
        switch (ac){
            case AC_UNKNOWN:
                return "u";
            case FACTORY_LOCKED:
                return "f";
            case READ_ONLY:
                return "r";
            case PARTIAL_WRITE:
                return "p";
            case WRITE:
                return "w";
            case OTP:
                return "o";
            case SPECIAL:
                return "s";
            case AUTH_REQUIRE:
                return "a";
            default:
                return "n";
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
