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

    long Number;

    String AppName;
    String TypeName;

    int PassesTotal;
    int PassesLeft;

    Calendar Sell;
    Calendar StartUseBefore;
    int ValidDays;

    Calendar LastUsed;
    int GateEntered;
    String StationEntered;

    private DateFormat df;

    public Ticket(NFCaDump dump) {

        Dump = new ArrayList<Integer>();
// TODO: Think about which is allowed minimum of pages to decode.
        int max = dump.getPagesNumber() > 16 ? 16:dump.getPagesNumber();
        for (int i = 0; i < max; i++){
            Dump.add(dump.getPageAsInt(i));
        }

        Number = 0;
        AppName = "";
        TypeName = "";
        PassesTotal = 0;
        PassesLeft = 0;
        Sell = Calendar.getInstance();
        Sell.clear();
        StartUseBefore = Calendar.getInstance();
        StartUseBefore.clear();
        LastUsed = Calendar.getInstance();
        LastUsed.clear();
        ValidDays = 0;
        GateEntered = 0;
        StationEntered = "";
        df = new SimpleDateFormat("dd.MM.yyyy");

    }

    public String getTicketAsString(Context c) {
        StringBuilder sb = new StringBuilder();

        sb.append(Decode.getAppIdDesc(c, Dump.get(4) >>> 22)).append('\n');
        sb.append(Decode.descCardType(c, (Dump.get(4) >>> 12) & 0x3ff)).append('\n');
        sb.append("- - - -\n");

        int mask12 = 0;
        for (int i = 0; i < 12; i++) {
            mask12 <<= 1;
            mask12 |= 1;
        }

        sb.append(c.getString(R.string.ticket_num)).append(' ');
        sb.append(String.format("%010d\n",
                (((Dump.get(4) & mask12) << 20) | (Dump.get(5) >>> 12)) & 0xffffffffL));
        sb.append(c.getString(R.string.issued)).append(": ");
        sb.append(getReadableDate((Dump.get(8) >>> 16) & 0xffff)).append('\n');
        sb.append(c.getString(R.string.start_use_before)).append(": ");
        sb.append(getReadableDate(Dump.get(6) >>> 16)).append('\n');
        sb.append(c.getString(R.string.best_in_days)).append(": ");
        sb.append((Dump.get(8) >>> 8) & 0xff).append('\n');
        sb.append("- - - -\n");
        sb.append(c.getString(R.string.passes_left)).append(": ");
        sb.append((Dump.get(9) >>> 16) & 0xff).append('\n');

        int cardLayout = ((Dump.get(5) >>> 8) & 0xf);
        switch (cardLayout) {
            case 8:
                if ((Dump.get(9) & 0xffff) != 0) {
                    sb.append(c.getString(R.string.station_last_enter)).append(": ");
                    sb.append(getGateDesc(c, Dump.get(9) & 0xffff)).append('\n');
                }
                sb.append("- - - -\n");
                sb.append("Layuot 8 (0x8).").append('\n');
                break;
            case 13:
                if ((Dump.get(9) & 0xffff) != 0) {
                    sb.append(c.getString(R.string.last_enter_date)).append(": \n  ");
                    sb.append(getReadableDate((Dump.get(11) >>> 16))).append(" ");
                    sb.append(c.getString(R.string.at)).append(String.format(" %02d:%02d,\n  ",
                            ((Dump.get(11) & 0xfff0) >>> 5) / 60,
                            ((Dump.get(11) & 0xfff0) >>> 5) % 60));
                    sb.append(c.getString(R.string.station_last_enter)).append(" ");
                    sb.append(getGateDesc(c, Dump.get(9) & 0xffff)).append('\n');
                }
                sb.append("- - - -\n");
                sb.append("Layuot 13 (0xd).").append('\n');
                break;

            default:
                sb.append(c.getString(R.string.unknown_layout)).append(": ").append(cardLayout).append('\n');
                break;
        }

        sb.append(String.format("App ID: %d (0x%03x), ",
                Dump.get(4) >>> 22,
                Dump.get(4) >>> 22));
        sb.append(String.format("Type: %d (0x%03x)\n",
                (Dump.get(4) >>> 12) & 0x3ff,
                (Dump.get(4) >>> 12) & 0x3ff));

        sb.append(c.getString(R.string.ticket_hash)).append(": ");
        sb.append(Integer.toHexString(Dump.get(10))).append('\n');
        sb.append(c.getString(R.string.otp)).append(": ");
        sb.append(Integer.toBinaryString(Dump.get(3))).append('\n');

        return sb.toString();
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
            return "№" + id + "\n  " + c.getString(R.string.station) + " " + Lang.tarnliterate(Decode.getStationName(id));
        } else {
            return "№" + id;
        }
    }


}
