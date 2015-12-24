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
    // Constants definition
    /* Used transport types */
    public static final int TT_UNKNOWN = 0;
    public static final int TT_METRO = TT_UNKNOWN + 1;
    public static final int TT_GROUND = TT_UNKNOWN + 2;
    /* Application */
    public static final int A_UNKNOWN = 0;
    public static final int A_METRO = 262;
    public static final int A_GROUND = 264;
    public static final int A_SOCIAL = 266;
    public static final int A_METRO_LIGHT = 270;
    public static final int A_UNIVERSAL = 279;
    /* Type */
    public static final int T_UNKNOWN = 0;
    
    // Data fields definition
    ArrayList<Integer> Dump;
    long Number = 0L;
    int Layout = 0;
    int App = A_UNKNOWN;
    int Type = T_UNKNOWN;
    int IssuedInt = 0;
    int StartUseBeforeInt = 0;
    int ValidDays = 0;
    int PassesTotal = 0;
    int PassesLeft = 0;
    int LastUsedDateInt = 0;
    int LastUsedTimeInt = 0;
    int GateEntered = 0;
    int TransportType = TT_UNKNOWN;
    int T90MCount = 0;
    int T90GCount = 0;
    int T90ChangeTimeInt = 0;
    int T90TripTimeLeftInt = 0;
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
        
        TransportType = (Dump.get(9) & 0xc0000000) >>> 30;

        T90MCount = (Dump.get(9) & 0x20000000) >>> 29;

        T90GCount = (Dump.get(9) & 0x1c000000) >>> 26;

        T90ChangeTimeInt = 0;
        if ((Dump.get(8) & 0xff) != 0) {
// TODO: Need to add date change (around midnight) processing
            T90ChangeTimeInt = (Dump.get(8) & 0xff) * 5 + LastUsedTimeInt;
        }

        T90TripTimeLeftInt = 0;
        if (T90MCount != 0 || T90GCount != 0) {
// TODO: Need to check date change
            if (getCurrentTimeInt() >= LastUsedTimeInt) {
                T90TripTimeLeftInt = 90 - getCurrentTimeInt() - LastUsedTimeInt;
            }
            if (T90TripTimeLeftInt < 0) T90TripTimeLeftInt = 0;
        }

        OTP = Dump.get(3);

        Hash = Dump.get(10);

        df = new SimpleDateFormat("dd.MM.yyyy");

    }

    public String getTicketAsString(Context c) {
        StringBuilder sb = new StringBuilder();

        sb.append(Decode.getAppIdDesc(c, App)).append('\n');
        sb.append(Decode.descCardType(c, Type)).append('\n');
        sb.append("\n- - - -\n");

        sb.append(c.getString(R.string.ticket_num)).append(' ');
        sb.append(String.format("%010d", Number));
        sb.append(" (");
        sb.append(getReadableDate(StartUseBeforeInt)).append(")\n");
        if (ValidDays != 0) {
            sb.append(c.getString(R.string.best_in_days)).append(": ");
            sb.append(ValidDays).append('\n');
        }
        if (IssuedInt != 0){
            //sb.append(c.getString(R.string.valid)).append(": ");
            sb.append("  ");
            sb.append(getReadableDate(IssuedInt)).append(" - ");
            sb.append(getReadableDate(IssuedInt+ValidDays)).append("\n");
        } else {
            sb.append(c.getString(R.string.start_use_before)).append(": ");
            sb.append(getReadableDate(StartUseBeforeInt)).append('\n');
        }
        
// TODO: Translate messages
        if (PassesLeft == 0) {
            sb.append("\n\tE M P T Y\n");
        } else if (IssuedInt == 0 ) { 
            if (isDateInPast(StartUseBeforeInt)) {
                sb.append("\n\tE X P I R E D\n");
            }
        } else {
            if (isDateInPast(IssuedInt+ValidDays)) {
                sb.append("\n\tE X P I R E D\n");
            }
        }
        sb.append("\n- - - -\n");
        
        sb.append(c.getString(R.string.passes_left)).append(": ");
        sb.append(PassesLeft).append('\n');

        switch (Layout) {
            case 8:
                if ( GateEntered != 0) {
                    sb.append(c.getString(R.string.station_last_enter)).append(": ");
                    sb.append(getGateDesc(c, GateEntered)).append('\n');
                }
                sb.append("\n- - - -\n");
                sb.append("Layuot 8 (0x8).").append('\n');
                break;
            case 13:
                if (GateEntered != 0) {

                    sb.append(c.getString(R.string.last_trip)).append(":\n  ");
                    sb.append(getReadableDate(LastUsedDateInt)).append(" ");
                    sb.append(c.getString(R.string.at)).append(" ");
                    sb.append(getReadableTime(LastUsedTimeInt));
                    sb.append(",\n  ");
                    sb.append(c.getString(R.string.station_last_enter)).append(" ");
                    sb.append(getGateDesc(c, GateEntered));
                    sb.append("\n");
// TODO: Translate messages
                    switch (TransportType) {
                        case TT_METRO:
                            sb.append("  (Metro)");
                            break;
                        case TT_GROUND:
                            sb.append("  (Ground)");
                            break;
                        case TT_UNKNOWN:
                            sb.append("  (Unknown)");
                            break;
                        default:
                            sb.append("  (!!! Internal error !!!)");
                            break;
                    }
                    sb.append('\n');

// TODO: Translate messages
                    if (T90MCount != 0 || T90GCount != 0) {
                       sb.append("90 minutes trip details:\n");
                       if (T90TripTimeLeftInt > 0) {
                            sb.append("  Time left: ");
                            sb.append(getReadableTime(T90TripTimeLeftInt)).append('\n');
                        } else {
                            sb.append("  Trip time ended\n");
                        }
                        sb.append("  Metro  count: ");
                        sb.append(T90MCount);
                        if (T90MCount > 0) {
                            sb.append(" (no more allowed)");
                        }
                        sb.append('\n');
                        sb.append("  Ground count: ");
                        sb.append(T90GCount).append('\n');
                        sb.append("  Change  time: ");
                        sb.append(getReadableTime(T90ChangeTimeInt)).append('\n');
                    }

                }
                sb.append("\n- - - -\n");
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

    private int getCurrentTimeInt() {
        /*
        Return minutes since current day midnight
         */
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.HOUR) * 60 + now.get(Calendar.MINUTE);
    }

    public String getReadableTime(int time){
        return String.format("%02d:%02d",
                time / 60,
                time % 60);
    }

    public String getOTPasBinaryString() {
        return Integer.toBinaryString(OTP);
    }

    public String getHashAsHexString() {
        return Integer.toHexString(Hash);
    }
    
    private boolean isDateInPast(int dateInt) {
        Calendar date = Calendar.getInstance();
        date.clear();
        date.set(1991, Calendar.DECEMBER, 31);
        date.add(Calendar.DATE, dateInt);
        if (date.compareTo(Calendar.getInstance()) <= 0){
            return true;
        }
        return false;
    }

    private String getReadableDate(int days) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(1991, Calendar.DECEMBER, 31);
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
