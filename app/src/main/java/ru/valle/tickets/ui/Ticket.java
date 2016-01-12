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
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import ru.valle.tickets.R;

public class Ticket {
    // Debug facility
    static final String TAG = "tickets";
    private static final boolean DEBUG_TIME = false;
    private static final DateFormat ddf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private Calendar getNowCalendar() {
        Calendar now = Calendar.getInstance();
        /*
        For debug set now to time
         */
        if (DEBUG_TIME) {
            now.clear();
            //now.set(2016, Calendar.JANUARY, 12, 12, 20);
            now.set(2015, Calendar.DECEMBER, 29, 12, 45);
        }
        return now;
    }

    // Constants definition
    /* Used transport types */
    public static final int TT_UNKNOWN = 0;
    public static final int TT_METRO = 1;
    public static final int TT_GROUND = 2;
    /* Application */
    public static final int A_UNKNOWN = 0;
    public static final int A_METRO = 262;
    public static final int A_GROUND = 264;
    public static final int A_SOCIAL = 266;
    public static final int A_METRO_LIGHT = 270;
    public static final int A_UNIVERSAL = 279;
    /* Type */
    public static final int T_UNKNOWN = 0;
    /* Type old (layout 0x08) */
    public static final int TO_M1 = 120;
    public static final int TO_M2 = 121;
    public static final int TO_M3 = 122;
    public static final int TO_M4 = 123;
    public static final int TO_M5 = 126;
    public static final int TO_M10 = 127;
    public static final int TO_M20 = 128;
    public static final int TO_M60 = 129;
    public static final int TO_BAGGAGE_AND_PASS = 130;
    public static final int TO_BAGGAGE = 131;
    public static final int TO_UL70 = 149;
    public static final int TO_VESB = 150; // Temporary universal social ticket
    /* Type new (layout 0x0d) */
    public static final int TN_G1 = 601;
    public static final int TN_G2 = 602;
    public static final int TN_G3_DRV = 608;
    public static final int TN_G5 = 603;
    public static final int TN_G11 = 604;
    public static final int TN_G20 = 605;
    public static final int TN_G40 = 606;
    public static final int TN_G60 = 607;
    public static final int TN_GB1_DRV = 571;
    public static final int TN_GB2 = 572; // predicted
    public static final int TN_U1_DRV = 410;
    public static final int TN_U1 = 411;
    public static final int TN_U2 = 412;
    public static final int TN_U5 = 413;
    public static final int TN_U11 = 415;
    public static final int TN_U20 = 416;
    public static final int TN_U40 = 417;
    public static final int TN_U60 = 418;
    public static final int TN_90U1 = 421;
    public static final int TN_90U2 = 422;
    public static final int TN_90U5 = 423;
    public static final int TN_90U11 = 424;
    public static final int TN_90U20 = 425;
    public static final int TN_90U40 = 426;
    public static final int TN_90U60 = 427;
    public static final int TN_UL1D = 419;
    /* Ticket class */
    public static final int C_UNKNOWN = 0;
    public static final int C_OLD_METRO = C_UNKNOWN + 1;
    public static final int C_OLD_SPECIAL = C_UNKNOWN + 2;
    public static final int C_GROUND = C_UNKNOWN + 3;
    public static final int C_GROUND_B = C_UNKNOWN +4;
    public static final int C_UNIVERSAL = C_UNKNOWN + 5;
    public static final int C_UNLIM_DAYS = C_UNKNOWN + 6;
    public static final int C_90UNIVERSAL = C_UNKNOWN + 7;

    // Data fields definition
    private ArrayList<Integer> Dump;
    private boolean DumpValid = false;
    private long TicketNumber = 0L;
    private int Layout = 0;
    private int App = A_UNKNOWN;
    private int Type = T_UNKNOWN;
    private int TicketClass = C_UNKNOWN;
    private boolean SellByDriver = false;
    private int IssuedInt = 0;
    private int StartUseBeforeInt = 0;
    private int StartUseTimeInt = 0;
    private Calendar StartUseDayTime = Calendar.getInstance();
    private int ValidDays = 0;
    private int PassesTotal = 0; // -1 is unlimited
    private int PassesLeft = 0;
    private int TripSeqNumber = 0;
    private int LastUsedDateInt = 0;
    private int LastUsedTimeInt = 0;
    private int TimeToNextTrip = 0;
    private int GateEntered = 0;
    private int TransportType = TT_UNKNOWN;
    private int T90MCount = 0;
    private int T90GCount = 0;
    private int T90RelChangeTimeInt = 0;
    private int T90ChangeTimeInt = 0;
    private int T90TripTimeLeftInt = 0;
    private int OTP = 0;
    private int Hash = 0;

    private DateFormat df;

    public Ticket(NFCaDump dump) {

        DumpValid = true;

        df = new SimpleDateFormat("dd.MM.yyyy");

        Dump = new ArrayList<Integer>();
        if (dump.getPagesNumber() - 4 + dump.getLastBlockValidPages() < 12) {
            DumpValid = false;
            return;
        }
        for (int i = 0; i < 12; i++){
            Dump.add(dump.getPageAsInt(i));
        }

        TicketNumber = (((Dump.get(4) & 0xfff) << 20) | (Dump.get(5) >>> 12)) & 0xffffffffL;

        Layout = ((Dump.get(5) >>> 8) & 0xf);

        App = Dump.get(4) >>> 22;

        Type = (Dump.get(4) >>> 12) & 0x3ff;

        detectPassesTotalAndClass();

        PassesLeft = (Dump.get(9) >>> 16) & 0xff;

        if ((Layout != 0x08 && Layout != 0x0d) ||
                App == A_UNKNOWN ||
                Type == T_UNKNOWN ||
                PassesTotal == 0 ||
                PassesTotal < -1 ||
                PassesTotal > 70 ||
                PassesLeft < -1
                ) {
            DumpValid = false;
        }

        TripSeqNumber = PassesTotal - PassesLeft;

        IssuedInt = (Dump.get(8) >>> 16) & 0xffff;

        StartUseBeforeInt = Dump.get(6) >>> 16;

        ValidDays = (Dump.get(8) >>> 8) & 0xff;

        LastUsedDateInt = Dump.get(11) >>> 16;

        LastUsedTimeInt = (Dump.get(11) & 0xfff0) >>> 5;

        GateEntered = Dump.get(9) & 0xffff;

        TransportType = (Dump.get(9) & 0xc0000000) >>> 30;

        if (TicketClass == C_90UNIVERSAL) {

            T90MCount = (Dump.get(9) & 0x20000000) >>> 29;

            T90GCount = (Dump.get(9) & 0x1c000000) >>> 26;

            T90ChangeTimeInt = 0;
            if ((Dump.get(8) & 0xff) != 0 && (Dump.get(8) & 0xff) != 0x80) {
                T90RelChangeTimeInt = Dump.get(8) & 0xff;
// TODO: Need to add date change (around midnight) processing
                T90ChangeTimeInt = T90RelChangeTimeInt * 5 + LastUsedTimeInt;
            }

            T90TripTimeLeftInt = 0;
            if (T90MCount != 0 || T90GCount != 0) {
// TODO: Need to check date change
                if (getCurrentTimeInt() >= LastUsedTimeInt) {
                    T90TripTimeLeftInt = 90 - (getCurrentTimeInt() - LastUsedTimeInt);
                }
                if (T90TripTimeLeftInt < 0) T90TripTimeLeftInt = 0;
            }
        }
        
        if (TicketClass == C_UNLIM_DAYS){
            TripSeqNumber = PassesLeft;
            PassesTotal = -1;
            PassesLeft = -1;
            StartUseTimeInt = (Dump.get(6) & 0xfff0) >>> 5;
            setStartUseDaytime(IssuedInt, StartUseTimeInt);
// TODO: Need to check date change
            TimeToNextTrip = (LastUsedTimeInt + 20) - getCurrentTimeInt();
            //if (TimeToNextTrip < 0) TimeToNextTrip = 0;
        }

        OTP = Dump.get(3);

        Hash = Dump.get(10);

    }

    public String getTicketAsString(Context c) {
        StringBuilder sb = new StringBuilder();

        if (DEBUG_TIME)
            sb.append(String.format("! ! ! App time set to %s\n\n",
                    ddf.format(getNowCalendar().getTime())));

        if (!DumpValid) {
// TODO: Translate message
            sb.append("! ! ! Dump not valid or ticket type unknown\n\n");
        }

        sb.append(Decode.getAppIdDesc(c, App)).append('\n');
        sb.append(Decode.descCardType(c, Type)).append('\n');
        sb.append("\n- - - -\n");

        sb.append(c.getString(R.string.ticket_num)).append(' ');
        sb.append(String.format("%010d", TicketNumber));
        sb.append(" (till ");
        sb.append(getReadableDate(StartUseBeforeInt)).append(")\n");
        if (ValidDays != 0) {
            sb.append(c.getString(R.string.best_in_days)).append(": ");
            sb.append(ValidDays).append('\n');
        }
        if (IssuedInt != 0){
            sb.append("  from ");
            if (TicketClass == C_UNLIM_DAYS){
                sb.append(String.format(" %s %s\n    to  %s %s", 
                        getReadableDate(IssuedInt),
                        getReadableTime(StartUseTimeInt),
                        getReadableDate(IssuedInt+ValidDays),
                        getReadableTime(StartUseTimeInt)));
            } else {
                sb.append(getReadableDate(IssuedInt));
                sb.append(" to ");
                sb.append(getReadableDate(IssuedInt+ValidDays - 1));
            }
            sb.append('\n');
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
            if (isDateInPast(IssuedInt+ValidDays) &&
                    TicketClass != C_UNLIM_DAYS) {
                sb.append("\n\tE X P I R E D\n");
            }
        }
        
        if (TicketClass == C_UNLIM_DAYS) {
            Calendar tmp = (Calendar)StartUseDayTime.clone();
            tmp.add(Calendar.HOUR, 24 * ValidDays);

            if (DEBUG_TIME)
                Log.d(TAG, String.format("Compare: %s\n", ddf.format(tmp.getTime())));

            if (tmp.compareTo(getNowCalendar()) < 0){
                sb.append("\n\tE X P I R E D\n");
            }else if (TimeToNextTrip > 0) {
                sb.append("\n\tW A I T\n");
            }
        }

        sb.append("\n- - - -\n");

        if (PassesLeft != -1){
            sb.append(c.getString(R.string.passes_left)).append(": ");
            sb.append(PassesLeft).append("\n\n");
        }
        
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

                    sb.append(c.getString(R.string.last_trip));
                    if (getTripSeqNumber() > 0) {
                        sb.append(" №");
                        sb.append(getTripSeqNumber());
                    }
                    sb.append(": ");
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
                    if (TicketClass == C_90UNIVERSAL) {
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
                    
                    if (TicketClass == Ticket.C_UNLIM_DAYS &&
                            TimeToNextTrip > 0) {
                        sb.append(String.format("  %d minutes to next trip", TimeToNextTrip));
                        sb.append('\n');
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

    public boolean isTicketFormatValid() { return DumpValid; }

    public long getTicketNumber() { return TicketNumber; }

    public int getTripSeqNumber() { return TripSeqNumber; }

    public int getTicketClass() { return TicketClass; }

    public int getPassesTotal() {
        if (PassesTotal == 0) detectPassesTotalAndClass();
        return PassesTotal;
    }
    public int getPassesLeft(){
        return PassesLeft;
    }
    public int getRelTransportChangeTimeMinutes() {
        return T90RelChangeTimeInt * 5;
    }

    public int getT90ChangeCount() { return T90GCount + T90MCount; }

/* Internal functions */

    private String getOTPasBinaryString() {
        return Integer.toBinaryString(OTP);
    }

    private String getHashAsHexString() {
        return Integer.toHexString(Hash);
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

    private void detectPassesTotalAndClass() {
        switch (Type) {
            case TO_M1:
                PassesTotal = 1;
                TicketClass = C_OLD_METRO;
                break;
            case TO_M2:
                PassesTotal = 2;
                TicketClass = C_OLD_METRO;
                break;
            case TO_M3:
                PassesTotal = 3;
                TicketClass = C_OLD_METRO;
                break;
            case TO_M4:
                PassesTotal = 4;
                TicketClass = C_OLD_METRO;
                break;
            case TO_M5:
                PassesTotal = 5;
                TicketClass = C_OLD_METRO;
                break;
            case TO_M10:
                PassesTotal = 10;
                TicketClass = C_OLD_METRO;
                break;
            case TO_M20:
                PassesTotal = 20;
                TicketClass = C_OLD_METRO;
                break;
            case TO_M60:
                PassesTotal = 60;
                TicketClass = C_OLD_METRO;
                break;
            case TO_BAGGAGE_AND_PASS:
                PassesTotal = 1;
                TicketClass = C_OLD_SPECIAL;
                break;
            case TO_BAGGAGE:
                PassesTotal = 1;
                TicketClass = C_OLD_SPECIAL;
                break;
            case TO_UL70:
                PassesTotal = -1;
                TicketClass = C_OLD_SPECIAL;
                break;
            case TO_VESB:
                PassesTotal = -1;
                TicketClass = C_OLD_SPECIAL;
                break;
            
            case TN_G1:
                PassesTotal = 1;
                TicketClass = C_GROUND;
                break;
            case TN_G2:
                PassesTotal = 2;
                TicketClass = C_GROUND;
                break;
            case TN_G3_DRV:
                PassesTotal = 3;
                TicketClass = C_GROUND;
                SellByDriver = true;
                break;
            case TN_G5:
                PassesTotal = 5;
                TicketClass = C_GROUND;
                break;
            case TN_G11:
                PassesTotal = 11;
                TicketClass = C_GROUND;
                break;
            case TN_G20:
                PassesTotal = 20;
                TicketClass = C_GROUND;
                break;
            case TN_G40:
                PassesTotal = 40;
                TicketClass = C_GROUND;
                break;
            case TN_G60:
                PassesTotal = 60;
                TicketClass = C_GROUND;
                break;
            case TN_GB1_DRV:
                PassesTotal = 1;
                TicketClass = C_GROUND_B;
                SellByDriver = true;
                break;
            case TN_GB2:
                PassesTotal = 2;
                TicketClass = C_GROUND_B;
                break;
            case TN_U1_DRV:
                PassesTotal = 1;
                TicketClass = C_UNIVERSAL;
                SellByDriver = true;
                break;
            case TN_U1:
                TicketClass = C_UNIVERSAL;
                PassesTotal = 1;
                break;
            case TN_U2:
                PassesTotal = 2;
                TicketClass = C_UNIVERSAL;
                break;
            case TN_U5:
                PassesTotal = 5;
                TicketClass = C_UNIVERSAL;
                break;
            case TN_U11:
                PassesTotal = 11;
                TicketClass = C_UNIVERSAL;
                break;
            case TN_U20:
                PassesTotal = 20;
                TicketClass = C_UNIVERSAL;
                break;
            case TN_U40:
                PassesTotal = 40;
                TicketClass = C_UNIVERSAL;
                break;
            case TN_U60:
                PassesTotal = 60;
                TicketClass = C_UNIVERSAL;
                break;
            case TN_UL1D:
                PassesTotal = -1;
                TicketClass = C_UNLIM_DAYS;
                break;
            case TN_90U1:
                PassesTotal = 1;
                TicketClass = C_90UNIVERSAL;
                break;
            case TN_90U2:
                PassesTotal = 2;
                TicketClass = C_90UNIVERSAL;
                break;
            case TN_90U5:
                PassesTotal = 5;
                TicketClass = C_90UNIVERSAL;
                break;
            case TN_90U11:
                PassesTotal = 11;
                TicketClass = C_90UNIVERSAL;
                break;
            case TN_90U20:
                PassesTotal = 20;
                TicketClass = C_90UNIVERSAL;
                break;
            case TN_90U40:
                PassesTotal = 40;
                TicketClass = C_90UNIVERSAL;
                break;
            case TN_90U60:
                PassesTotal = 60;
                TicketClass = C_90UNIVERSAL;
                break;
            default:
                PassesTotal = 0;
                break;
        }
    }

/* Time related internal functions */

    private String getReadableDate(int days) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(1991, Calendar.DECEMBER, 31);
        c.add(Calendar.DATE, days);
        return this.df.format(c.getTime());
    }

    private String getReadableTime(int time){
        return String.format("%02d:%02d",
                time / 60,
                time % 60);
    }

    private int getCurrentTimeInt() {
        /*
        Return minutes since current day midnight
         */
        Calendar now = getNowCalendar();
        return now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE);
    }

    private boolean isDateInPast(int dateInt) {
        Calendar date = Calendar.getInstance();
        date.clear();
        date.set(1991, Calendar.DECEMBER, 31);
        date.add(Calendar.DATE, dateInt);
        if (date.compareTo(getNowCalendar()) <= 0){
            return true;
        }
        return false;
    }
    
    public int getCurrentDateInt() {
// TODO: need to be implemented
        return 0;
    }

    private void setStartUseDaytime(int date, int time){
        StartUseDayTime.clear();
        StartUseDayTime.set(1991, Calendar.DECEMBER, 31);
        StartUseDayTime.add(Calendar.DATE, date);
        StartUseDayTime.add(Calendar.MINUTE, time);
        if (DEBUG_TIME)
            Log.d(TAG,String.format("Set: %s\n",ddf.format(StartUseDayTime.getTime())));
    }
}
