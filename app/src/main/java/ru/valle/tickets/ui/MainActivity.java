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

    // Constants definition
    static final int MAX_PAGES = 64;

    static final byte AC_UNKNOWN = 0;
    static final byte AC_FACTORY_LOCKED = AC_UNKNOWN + 1;
    static final byte AC_READ_ONLY = AC_UNKNOWN + 2;
    static final byte AC_PARTIAL_WRITE = AC_UNKNOWN + 3;
    static final byte AC_WRITE = AC_UNKNOWN + 4;
    static final byte AC_OTP = AC_UNKNOWN + 5;
    static final byte AC_AUTH_REQUIRE = AC_UNKNOWN + 6;
    static final byte AC_INTERAL_USE = AC_UNKNOWN + 7;
    static final byte AC_SPECIAL = AC_UNKNOWN + 8;

    static final byte IC_UNKNOWN = 0;
    static final byte IC_MF0ICU1 = IC_UNKNOWN + 1;
    static final byte IC_MF0ULx1 = IC_UNKNOWN + 2;
    static final byte IC_MIK640D = IC_UNKNOWN + 3;
    static final byte IC_MIK1312ED = IC_UNKNOWN + 4;

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
                            if (dump.isEmpty())
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
                            if ((dump.getPage(0)[0] == (byte)0x04 && dump.size() == 20) ||     // MF0ULx1 (80 bytes)
                                    (dump.getPage(0)[0] == (byte)0x34 && dump.size() == 44)) {  // MIK1312ED (164 bytes)
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
        String prefix = "android.nfc.tech.";

        if (dump.size() < 16) {
            Log.d(TAG, "Tag read partial");
            return "Tag read partial. Try again.";
        }

        int[] p = new int[16];
        for (int i = 0; i < 16; i++) {
            p[i] = toIntPages(dump.getPage(i))[0];
        }

        StringBuilder sb = new StringBuilder();
        sb.append(Decode.getAppIdDesc(this, p[4] >>> 22)).append('\n');
        sb.append(Decode.descCardType(this, (p[4] >>> 12) & 0x3ff)).append('\n');
        sb.append("- - - -\n");

        int mask12 = 0;
        for (int i = 0; i < 12; i++) {
            mask12 <<= 1;
            mask12 |= 1;
        }

        sb.append(getString(R.string.ticket_num)).append(' ').append(String.format("%010d", (((p[4] & mask12) << 20) | (p[5] >>> 12)) & 0xffffffffL)).append('\n');
        sb.append(getString(R.string.issued)).append(": ").append(getReadableDate((p[8] >>> 16) & 0xffff)).append('\n');
        sb.append(getString(R.string.start_use_before)).append(": ").append(getReadableDate(p[6] >>> 16)).append('\n');
        sb.append(getString(R.string.best_in_days)).append(": ").append((p[8] >>> 8) & 0xff).append('\n');
        sb.append("- - - -\n");
        sb.append(getString(R.string.passes_left)).append(": ").append((p[9] >>> 16) & 0xff).append('\n');

        int cardLayout = ((p[5] >>> 8) & 0xf);
        switch (cardLayout) {
            case 8:
                if ((p[9] & 0xffff) != 0) {
                    sb.append(getString(R.string.station_last_enter)).append(": ").append(getGateDesc(p[9] & 0xffff)).append('\n');
                }
                sb.append("- - - -\n");
                sb.append("Layuot 8 (0x08).").append('\n');
                break;
            case 13:
                if ((p[9] & 0xffff) != 0) {
                    sb.append(getString(R.string.last_enter_date)).append(": \n  ").append(getReadableDate((p[11] >>> 16))).append(" ");
                    sb.append(getString(R.string.at)).append(String.format(" %02d:%02d,\n  ", ((p[11] & 0xfff0) >>> 5) / 60, ((p[11] & 0xfff0) >>> 5) % 60));
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

        sb.append(String.format("App ID: %d (0x%03x), ", p[4] >>> 22, p[4] >>> 22));
        sb.append(String.format("Type: %d (0x%03x)\n", (p[4] >>> 12) & 0x3ff, (p[4] >>> 12) & 0x3ff));

        sb.append(getString(R.string.ticket_hash)).append(": ").append(Integer.toHexString(p[10])).append('\n');
        sb.append(getString(R.string.otp)).append(": ").append(Integer.toBinaryString(p[3])).append('\n');
        sb.append(String.format("4 bytes pages read: %d (total %d bytes)\n", dump.size() - 4 + dump.getLastBlockValidPages(), (dump.size() - 4 + dump.getLastBlockValidPages()) * 4));
        sb.append("UID: ").append(String.format("%08x %08x\n", p[0], p[1]));
        sb.append(String.format("  BCC0: %02x, BCC1: %02x", (p[0] & 0xffL), (p[2] & 0xff000000L) >> 24));

        byte UID_BCC0_CRC = (byte)0x88; // CT (Cascade Tag) [value 88h] as defined in ISO/IEC 14443-3 Type A
        UID_BCC0_CRC ^= dump.getPage(0)[0];
        UID_BCC0_CRC ^= dump.getPage(0)[1];
        UID_BCC0_CRC ^= dump.getPage(0)[2];
        UID_BCC0_CRC ^= dump.getPage(0)[3]; //The BCC0 itself, if ok result is 0

        byte UID_BCC1_CRC = (byte)0x00;
        UID_BCC1_CRC ^= dump.getPage(1)[0];
        UID_BCC1_CRC ^= dump.getPage(1)[1];
        UID_BCC1_CRC ^= dump.getPage(1)[2];
        UID_BCC1_CRC ^= dump.getPage(1)[3];
        UID_BCC1_CRC ^= dump.getPage(2)[0]; //The BCC1 itself, if ok result is 0

        if (UID_BCC0_CRC == 0 && UID_BCC1_CRC == 0) {
            sb.append(" (CRC OK)\n");
        } else {
            sb.append(" (CRC not OK)\n");
        }

        sb.append("Manufacturer internal byte: ").append(String.format("%02x\n", int_byte));
        sb.append(String.format("ATQA: %02x %02x\n", dump.getATQA()[1], dump.getATQA()[0]));
        sb.append(String.format("SAK: %02x\n", dump.getSAK()));

        if (!dump.isVERSIONEmpty()) {
            sb.append("GET_VERSION: ");
            for (int i = 0; i < dump.getVersionInfo().length; i++) {
                sb.append(String.format("%02x ", dump.getVersionInfo()[i]));
            }
            sb.append("\n");
        }

        if (dump.getCountersNumber() > 0) {
            sb.append("Counters(hex):\n");
            for (int i = 0; i < dump.getCountersNumber(); i++) {
                sb.append(String.format("  %01x: ", i));
                // LSB is 1-st
                for (int j = (dump.getCountersNumber() - 1); j >= 0; j--) {
                    sb.append(String.format("%02x", dump.getCounter(i)[j]));
                }
                sb.append("\n");
            }
        }

        if (!dump.isSIGNEmpty()) {
            sb.append("READ_SIG:\n  ");
            for (int i = 0; i < dump.getSIGN().length; i++) {
                sb.append(String.format("%02x", dump.getSIGN()[i]));
                if ((i + 1) % 16 == 0 && (i + 1) != 32) sb.append("\n  ");
            }
            sb.append("\n");
        }

        sb.append("Android technologies: \n   ");
        for (int i = 0; i < dump.getAndTechList().size(); i++) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append(dump.getAndTechList().get(i).substring(prefix.length()));
        }
        sb.append('\n');

        sb.append("Chip manufacturer: ");
        switch (mf_code) {
            case 0x04:
                sb.append("NXP Semiconductors (Philips) Germany\n");
                sb.append("Chip: ");
                if (dump.size() == 16) {
                    ICType = IC_MF0ICU1;
                    sb.append("(probably)MF0ICU1 (64 bytes)");
                } else if (dump.getVersionInfo()[0] == (byte) 0x00 &&
                        dump.getVersionInfo()[1] == (byte) 0x04 &&
                        dump.getVersionInfo()[2] == (byte) 0x03 &&
                        dump.getVersionInfo()[4] == (byte) 0x01) {
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
                    ICType = IC_UNKNOWN;
                    sb.append("Unknown\n");
                }
                sb.append('\n');
                break;
            case 0x34:
                sb.append("JSC Micron Russia\n");
                sb.append("Chip: ");
                if (dump.size() == 20) {
                    ICType = IC_MIK640D;
                    sb.append("(probably) MIK64PTAS(MIK640D) (80 bytes)");
                } else if (dump.getVersionInfo()[0] == (byte)0x00 &&
                        dump.getVersionInfo()[1] == (byte)0x34 &&
                        dump.getVersionInfo()[2] == (byte)0x21 &&
                        dump.getVersionInfo()[3] == (byte)0x01 &&
                        dump.getVersionInfo()[4] == (byte)0x01 &&
                        dump.getVersionInfo()[5] == (byte)0x00) {
                    ICType = IC_MIK1312ED;
                    sb.append("MIK1312ED(К5016ВГ4Н4 aka K5016XC1M1H4) (164 bytes)");
                } else {
                    ICType = IC_UNKNOWN;
                    sb.append("Unknown");
                }
                sb.append('\n');
                break;
            default:
                sb.append("Unknown\n");
                break;
        }

        byte[] pageAccess = new byte[MAX_PAGES];
        for (int i = 0; i < MAX_PAGES - 1; i++) {
            pageAccess[1] = AC_UNKNOWN;
        }
        pageAccess[0] = AC_FACTORY_LOCKED;
        pageAccess[1] = AC_FACTORY_LOCKED;
        pageAccess[2] = (p[2] & 0x90) != 0 ? AC_READ_ONLY : AC_PARTIAL_WRITE;
        pageAccess[3] = AC_OTP;
        pageAccess[4] = ((p[2] & 0x1000) | (p[2] & 0x0200)) != 0 ? AC_READ_ONLY : AC_WRITE;
        pageAccess[5] = ((p[2] & 0x2000) | (p[2] & 0x0200)) != 0 ? AC_READ_ONLY : AC_WRITE;
        pageAccess[6] = ((p[2] & 0x4000) | (p[2] & 0x0200)) != 0 ? AC_READ_ONLY : AC_WRITE;
        pageAccess[7] = ((p[2] & 0x8000) | (p[2] & 0x0200)) != 0 ? AC_READ_ONLY : AC_WRITE;
        pageAccess[8] = ((p[2] & 0x0001) | (p[2] & 0x0200)) != 0 ? AC_READ_ONLY : AC_WRITE;
        pageAccess[9] = ((p[2] & 0x0002) | (p[2] & 0x0200)) != 0 ? AC_READ_ONLY : AC_WRITE;
        pageAccess[10] = ((p[2] & 0x0004) | (p[2] & 0x0400)) != 0 ? AC_READ_ONLY : AC_WRITE;
        pageAccess[11] = ((p[2] & 0x0008) | (p[2] & 0x0400)) != 0 ? AC_READ_ONLY : AC_WRITE;
        pageAccess[12] = ((p[2] & 0x0010) | (p[2] & 0x0400)) != 0 ? AC_READ_ONLY : AC_WRITE;
        pageAccess[13] = ((p[2] & 0x0020) | (p[2] & 0x0400)) != 0 ? AC_READ_ONLY : AC_WRITE;
        pageAccess[14] = ((p[2] & 0x0040) | (p[2] & 0x0400)) != 0 ? AC_READ_ONLY : AC_WRITE;
        pageAccess[15] = ((p[2] & 0x0080) | (p[2] & 0x0400)) != 0 ? AC_READ_ONLY : AC_WRITE;

        switch (ICType) {
            case IC_MF0ULx1:
                pageAccess[16] = AC_SPECIAL;
                pageAccess[17] = AC_SPECIAL;
                pageAccess[18] = AC_SPECIAL;
                pageAccess[19] = AC_SPECIAL;
                if (dump.getPage(16)[3] != (byte)0xff) {
                    for (int i = (int)(dump.getPage(16)[3] & 0x0ffL); i < MAX_PAGES - 1; i++) {
                        pageAccess[i] = AC_AUTH_REQUIRE;
                    }
                }
                break;
            case IC_MIK640D:
                pageAccess[16] = AC_OTP;
                pageAccess[17] = AC_OTP;
                pageAccess[18] = AC_OTP;
                pageAccess[19] = AC_INTERAL_USE;
                break;
            case IC_MIK1312ED:
                pageAccess[16] = ((dump.getPage(36)[0] & 0x01) | (dump.getPage(36)[2] & 0x01)) != 0 ? AC_READ_ONLY : AC_WRITE;
                pageAccess[17] = ((dump.getPage(36)[0] & 0x01) | (dump.getPage(36)[2] & 0x01)) != 0 ? AC_READ_ONLY : AC_WRITE;
                pageAccess[18] = ((dump.getPage(36)[0] & 0x02) | (dump.getPage(36)[2] & 0x01)) != 0 ? AC_READ_ONLY : AC_WRITE;
                pageAccess[19] = ((dump.getPage(36)[0] & 0x02) | (dump.getPage(36)[2] & 0x01)) != 0 ? AC_READ_ONLY : AC_WRITE;
                pageAccess[20] = ((dump.getPage(36)[0] & 0x04) | (dump.getPage(36)[2] & 0x02)) != 0 ? AC_READ_ONLY : AC_WRITE;
                pageAccess[21] = ((dump.getPage(36)[0] & 0x04) | (dump.getPage(36)[2] & 0x02)) != 0 ? AC_READ_ONLY : AC_WRITE;
                pageAccess[22] = ((dump.getPage(36)[0] & 0x08) | (dump.getPage(36)[2] & 0x02)) != 0 ? AC_READ_ONLY : AC_WRITE;
                pageAccess[23] = ((dump.getPage(36)[0] & 0x08) | (dump.getPage(36)[2] & 0x02)) != 0 ? AC_READ_ONLY : AC_WRITE;
                pageAccess[24] = ((dump.getPage(36)[0] & 0x10) | (dump.getPage(36)[2] & 0x04)) != 0 ? AC_READ_ONLY : AC_WRITE;
                pageAccess[25] = ((dump.getPage(36)[0] & 0x10) | (dump.getPage(36)[2] & 0x04)) != 0 ? AC_READ_ONLY : AC_WRITE;
                pageAccess[26] = ((dump.getPage(36)[0] & 0x20) | (dump.getPage(36)[2] & 0x04)) != 0 ? AC_READ_ONLY : AC_WRITE;
                pageAccess[27] = ((dump.getPage(36)[0] & 0x20) | (dump.getPage(36)[2] & 0x04)) != 0 ? AC_READ_ONLY : AC_WRITE;
                pageAccess[28] = ((dump.getPage(36)[0] & 0x40) | (dump.getPage(36)[2] & 0x08)) != 0 ? AC_READ_ONLY : AC_WRITE;
                pageAccess[29] = ((dump.getPage(36)[0] & 0x40) | (dump.getPage(36)[2] & 0x08)) != 0 ? AC_READ_ONLY : AC_WRITE;
                pageAccess[30] = ((dump.getPage(36)[0] & 0x80) | (dump.getPage(36)[2] & 0x08)) != 0 ? AC_READ_ONLY : AC_WRITE;
                pageAccess[31] = ((dump.getPage(36)[0] & 0x80) | (dump.getPage(36)[2] & 0x08)) != 0 ? AC_READ_ONLY : AC_WRITE;
                pageAccess[32] = ((dump.getPage(36)[1] & 0x01) | (dump.getPage(36)[2] & 0x10)) != 0 ? AC_READ_ONLY : AC_WRITE;
                pageAccess[33] = ((dump.getPage(36)[1] & 0x01) | (dump.getPage(36)[2] & 0x10)) != 0 ? AC_READ_ONLY : AC_WRITE;
                pageAccess[34] = ((dump.getPage(36)[1] & 0x02) | (dump.getPage(36)[2] & 0x10)) != 0 ? AC_READ_ONLY : AC_WRITE;
                pageAccess[35] = ((dump.getPage(36)[1] & 0x02) | (dump.getPage(36)[2] & 0x10)) != 0 ? AC_READ_ONLY : AC_WRITE;
                pageAccess[36] = AC_SPECIAL;
                pageAccess[37] = AC_SPECIAL;
                pageAccess[38] = AC_SPECIAL;
                pageAccess[39] = AC_SPECIAL;
                pageAccess[40] = AC_SPECIAL;
                if (dump.getPage(37)[3] != (byte)0xff) {
                    for (int i = (int)(dump.getPage(37)[3] & 0x0ffL); i < MAX_PAGES - 1; i++) {
                        pageAccess[(byte)i] = AC_AUTH_REQUIRE;
                    }
                }
                break;
            default:
                break;
        }

        sb.append("- - - Dump: - - -\n");
        for (int i=0; i < dump.size() - ( 4 - dump.getLastBlockValidPages() ); i++){
            sb.append(String.format("%02x:%s: ", i, getAccessAsString(pageAccess[i])));
            for (int j=0; j < 4; j++){
                sb.append(String.format("%02x ", dump.getPage(i)[j]));
            }
            sb.append("\n");
        }
        // warning, because fuzzy algorithm used
        if (dump.getLastBlockValidPages() != 4)
            sb.append(String.format("---\n[!]Last block valid pages: %d\n", dump.getLastBlockValidPages()));
        sb.append("- - - Dump legend: - - -\n");
        sb.append(":u: - unknown\n");
        sb.append(":f: - factory locked\n");
        sb.append(":r: - read only\n");
        sb.append(":p: - partially writable\n");
        sb.append(":o: - One Time Programming (OTP)\n");
        sb.append(":w: - writable\n");
        sb.append(":a: - authentication require for write\n");
        sb.append(":s: - special\n");
        sb.append(":i: - reserved for internal use\n");

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

    private String getAccessAsString(byte ac) {
        switch (ac) {
            case AC_UNKNOWN:
                return "u";
            case AC_FACTORY_LOCKED:
                return "f";
            case AC_READ_ONLY:
                return "r";
            case AC_PARTIAL_WRITE:
                return "p";
            case AC_WRITE:
                return "w";
            case AC_OTP:
                return "o";
            case AC_SPECIAL:
                return "s";
            case AC_INTERAL_USE:
                return "i";
            case AC_AUTH_REQUIRE:
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
