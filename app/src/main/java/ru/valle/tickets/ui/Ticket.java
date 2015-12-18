package ru.valle.tickets.ui;

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

import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import ru.valle.tickets.R;

public class Ticket {

    // Data fields definition
    ArrayList<Integer> Dump;
    long Number = 0L;
    int Layout = 0;
    int App = 0;
    int Type = 0;
    int IssuedInt = 0;
    int StartUseBeforeInt = 0;
    int ValidDays = 0;
    int PassesTotal = 0;
    int PassesLeft = 0;
    int LastUsedDateInt = 0;
    int LastUsedTimeInt = 0;
    int GateEntered = 0;
    int OTP = 0;
    int Hash = 0;

    private DateFormat df;

    public Ticket(NFCaDump dump) {

        Dump = new ArrayList<Integer>();
// TODO: Think about which is allowed minimum of pages to decode.
        int max = dump.getPagesNumber() > 16 ? 16:dump.getPagesNumber();
        for (int i = 0; i < max; i++){
            Dump.add(dump.getPageAsInt(i));
        }

        Number = (((Dump.get(4) & 0xfff) << 20) | (Dump.get(5) >>> 12)) & 0xffffffffL;

        Layout = ((Dump.get(5) >>> 8) & 0xf);

        App = Dump.get(4) >>> 22;

        Type = (Dump.get(4) >>> 12) & 0x3ff;

        IssuedInt = (Dump.get(8) >>> 16) & 0xffff;

        StartUseBeforeInt = Dump.get(6) >>> 16;

        ValidDays = (Dump.get(8) >>> 8) & 0xff;

        PassesLeft = (Dump.get(9) >>> 16) & 0xff;

        LastUsedDateInt = Dump.get(11) >>> 16;

        LastUsedTimeInt = (Dump.get(11) & 0xfff0) >>> 5;

        GateEntered = Dump.get(9) & 0xffff;

        OTP = Dump.get(3);
        Hash = Dump.get(10);

        df = new SimpleDateFormat("dd.MM.yyyy");

    }

    public String getTicketAsString(Context c) {
        StringBuilder sb = new StringBuilder();

        sb.append(Decode.getAppIdDesc(c, App)).append('\n');
        sb.append(Decode.descCardType(c, Type)).append('\n');
        sb.append("- - - -\n");

        sb.append(c.getString(R.string.ticket_num)).append(' ');
        sb.append(String.format("%010d\n", Number));
        sb.append(c.getString(R.string.issued)).append(": ");
        sb.append(getReadableDate(IssuedInt)).append('\n');
        sb.append(c.getString(R.string.start_use_before)).append(": ");
        sb.append(getReadableDate(StartUseBeforeInt)).append('\n');
        sb.append(c.getString(R.string.best_in_days)).append(": ");
        sb.append(ValidDays).append('\n');
        sb.append("- - - -\n");
        sb.append(c.getString(R.string.passes_left)).append(": ");
        sb.append(PassesLeft).append('\n');

        switch (Layout) {
            case 8:
                if ( GateEntered != 0) {
                    sb.append(c.getString(R.string.station_last_enter)).append(": ");
                    sb.append(getGateDesc(c, GateEntered)).append('\n');
                }
                sb.append("- - - -\n");
                sb.append("Layuot 8 (0x8).").append('\n');
                break;
            case 13:
                if (GateEntered != 0) {
                    sb.append(c.getString(R.string.last_enter_date)).append(": \n  ");
                    sb.append(getReadableDate(LastUsedDateInt)).append(" ");
                    sb.append(c.getString(R.string.at)).append(getReadableLastUsedTime());
                    sb.append(c.getString(R.string.station_last_enter)).append(" ");
                    sb.append(getGateDesc(c, GateEntered)).append('\n');
                }
                sb.append("- - - -\n");
                sb.append("Layuot 13 (0xd).").append('\n');
                break;

            default:
                sb.append(c.getString(R.string.unknown_layout)).append(": ");
                sb.append(Layout).append('\n');
                break;
        }

        sb.append(String.format("App ID: %d (0x%03x), ", App, App));
        sb.append(String.format("Type: %d (0x%03x)\n", Type, Type));

        sb.append(c.getString(R.string.ticket_hash)).append(": ");
        sb.append(getHashAsHexString()).append('\n');
        sb.append(c.getString(R.string.otp)).append(": ");
        sb.append(getOTPasBinaryString()).append('\n');

        return sb.toString();
    }

    public String getReadableLastUsedTime(){
        return String.format(" %02d:%02d,\n  ",
                LastUsedTimeInt / 60,
                LastUsedTimeInt % 60);
    }

    public String getOTPasBinaryString() {
        return Integer.toBinaryString(OTP);
    }

    public String getHashAsHexString() {
        return Integer.toHexString(Hash);
    }

    private String getReadableDate(int days) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(1992, 0, 0);
        c.add(Calendar.DATE, days);
        return df.format(c.getTime());
    }

    private String getGateDesc(Context c, int id) {
        String SN = Lang.tarnliterate(Decode.getStationName(id));
        if (SN.length() != 0) {
            return "№" + id + "\n  " +
                    c.getString(R.string.station) + " " +
                    Lang.tarnliterate(Decode.getStationName(id));
        } else {
            return "№" + id;
        }
    }
}
