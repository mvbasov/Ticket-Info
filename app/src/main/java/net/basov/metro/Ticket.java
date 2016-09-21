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

package net.basov.metro;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import net.basov.nfc.NFCaDump;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import ru.valle.tickets.R;
import ru.valle.tickets.ui.Decode;
import ru.valle.tickets.ui.Lang;

public class Ticket {
    // Debug facility
    static final String TAG = "tickets";
    private static final boolean DEBUG_TIME = false;
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

    private static final DateFormat ddf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

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
    public static final int TN_G1 = 601; // 1 passes, ground (0002277252)(0002550204, with paper check)
    public static final int TN_G2 = 602; // 2 passes ground (0001585643, with paper check)
    public static final int TN_G3_DRV = 608; // 3 passes, ground, sell by driver (0010197214)
    public static final int TN_G5 = 603; // 5 passes ground (0000060635)(0002550205, with paper check)
    public static final int TN_G11 = 604; // 11 passes ground (0002551460, with paper check)
    public static final int TN_G20 = 605; // 20 passes, ground (0002275051)(0002688466, with paper check)
    public static final int TN_G40 = 606; // 40 passes, ground (0002551487, with paper check)
    public static final int TN_G60 = 607; // 60 passes, ground (0000108646, with paper check)
    public static final int TN_GB1_DRV = 571; // 1 pass, ground, Zone B (0000021180 script on ticket)
    public static final int TN_GB2 = 572; // predicted
    public static final int TN_GAB1 = 581; // 1 pass, zone A and B (0000197461)
    public static final int TN_U1_DRV = 410; // 1 pass, universal, sell by ground driver (0020905097)
    public static final int TN_U1 = 411; // 1 passes, universal (2462677850, with paper check)
    public static final int TN_U2 = 412; // 2 passes, universal (2523074756, with paper check)
    public static final int TN_U5 = 413; // 5 passes, universal (2462677851, with paper check)
    public static final int TN_U11 = 415; // 11 passes, universal (2458927306, with paper check)
    public static final int TN_U20 = 416; // 20 passes, universal (2518437516, with paper check)
    public static final int TN_U40 = 417; // 40 passes, universal (2516440644, with paper check)
    public static final int TN_U60 = 418; // 60 passes, universal (2478069296, confirmed lly)
    public static final int TN_90U1_G = 421; // 1 pass, 90 minutes, universal, sell on ground (1013862735, with paper check)
    public static final int TN_90U1 = 437; // 1 passs, 90 minutes, universal, sell in metro (1016236236, with paper check)
    public static final int TN_90U2_G = 438; // 2 passes, 90 minutes, universal, sell on ground (1014908560, with paper check)
    public static final int TN_90U2 = 422; // 2 passes, 90 minutes, universal, sell in metro (1016237832, with paper check)
    public static final int TN_90U5 = 423; // 5 passes, 90 minutes, universal (1016363888, with paper check)
    public static final int TN_90U11 = 424; // 11 passes, 90 minutes, universal (1016235763, with paper sheck)
    public static final int TN_90U20 = 425; // 20 passes, 90 minutes, universal (1016043594, with paper check)
    public static final int TN_90U40 = 426; // 40 passes, 90 minutes, universal (1016043595, with paper check)
    public static final int TN_90U60 = 427; // 60 passes, 90 minutes, universal (1015907198, confirmed Max)
    public static final int TN_UL1D = 419; // 1 days, unlimited passes, 20 minutes between passes (0001029499, with paper check)
    public static final int TN_UL3D = 435; // 3 days, unlimited passes, 20 minutes between passes (0001192751, with paper check)
    public static final int TN_UL7D = 436; // 7 days, unlimited passes, 20 minutes between passes (0001192740, with paper check)
    /* Ticket class */
    /**
     * Unknown ticket class
     */
    public static final int C_UNKNOWN = 0;
    /**
     * Old (layout 0x08) metro ticket
     */
    public static final int C_OLD_METRO = C_UNKNOWN + 1;
    /**
     * Old (layout 0x08) special ticket
     */
    public static final int C_OLD_SPECIAL = C_UNKNOWN + 2;
    /**
     * Ticket for ground transport (bus, tramway, trolleybus)
     */
    public static final int C_GROUND = C_UNKNOWN + 3;
    /**
     * Ticket for ground transport of Moscow Region
     */
    public static final int C_GROUND_B = C_UNKNOWN + 4;
    /**
     * Ticket for ground transport of Moscow and Moscow Region
     */
    public static final int C_GROUND_AB = C_UNKNOWN + 5;
    /**
     * Universal ticket for ground and underground Moscow transport
     * Limited by passes.
     */
    public static final int C_UNIVERSAL = C_UNKNOWN + 6;
    /**
     * Universal ticket for ground and underground Moscow transport
     * Limited by day of use. Unlimited passes but 20 minutes between passes
     */
    public static final int C_UNLIM_DAYS = C_UNKNOWN + 7;
    /**
     * Universal ticket for ground and underground Moscow transport
     * Limited by trips.
     * Trip can include one undeground and unlmited amount ground passes during 90 minutes
     */
    public static final int C_90UNIVERSAL = C_UNKNOWN + 8;
    /* Where selll */
    /**
     * Ticket sell point unknown
     */
    public static final int WS_UNKNOWN = 0;
    /**
     * Ticket sell in Metro (underground)
     */
    public static final int WS_METRO = 1;
    /**
     * Ticket sell by ground sell point
     */
    public static final int WS_GROUND = WS_METRO >>> 1;
    /**
     * Ticket sell by ground transport driver
     */
    public static final int WS_DRIVER = WS_METRO >>> 2;
    
    // Data fields definition
    /**
     * Ticket chip content.
     * ArrayList of pages.
     * Each page (4 bytes) represented as Integer
     */
    private ArrayList<Integer> Dump;
    /**
     * Is dump valid
     */
    private boolean DumpValid = false;
    /**
     * Number printed on ticket
     */
    private long TicketNumber = 0L;
    /**
     * Ticket layout
     */
    private int Layout = 0;
    private int App = A_UNKNOWN;
    private int Type = T_UNKNOWN;
    private int TicketClass = C_UNKNOWN;
    /**
     * Where ticket was sell. Possible values:
     * <ul>
     *     <li>{@link Ticket#WS_METRO}</li>
     *     <li>{@link Ticket#WS_GROUND}</li>
     *     <li>{@link Ticket#WS_DRIVER}</li>
     * </ul>
     */
    private int WhereSell = WS_UNKNOWN;
    //private int IssuedInt = 0;
    //private int StartUseBeforeInt = 0;
    //private int StartUseTimeInt = 0;
    /**
     * Start use date time.
     */
    private Calendar StartUse = null;
    /**
     * Ticket issue date
     */
    private Calendar Issued = null;
    /**
     * Ticket blank "Use before" date
     */
    private Calendar StartUseBefore = null;
    //private Calendar StartUseDayTime = Calendar.getInstance();
    /**
     * Ticket valid days from the begin of {@link Ticket#Issued}
     */
    private int ValidDays = 0;
    /**
     * Number of allowed passes at the begin of ticket use.
     * Set to -1 for tickets limeted by day of use.
     */
    private int PassesTotal = 0;
    /**
     * Passes left
     */
    private int PassesLeft = 0;
    /**
     * Pass (or trip for 90 minutes tickets) sequence number
     */
    private int TripSeqNumber = 0;
    //private int LastUsedDateInt = 0;
    //private int LastUsedTimeInt = 0;
    /**
     * When current trip start
     */
    private Calendar TripStart = null;
    /**
     * Time to next pass (in minutes) for day limited tickets
     */
    private int TimeToNextTrip = 0;
    /**
     * Last entered gate id.
     * This value valid only for 0x08 and 0x0d layouts.
     * One underground station has several turnstiles (gates) in each entrance
     */
    private int GateEntered = 0;
    /**
     * Last entered station entrance id.
     * One station may have one or several entrance with different id
     */
    private int EntranceEntered = 0;
    /**
     * Transport type. Possible values:
     * <li>
     *     <li>{@link Ticket#TT_UNKNOWN}</li>
     *     <li>{@link Ticket#TT_METRO}</li>
     *     <li>{@link Ticket#TT_GROUND}</li>
     * </li>
     */
    private int TransportType = TT_UNKNOWN;
    /**
     * Metro pass counter during current trip.
     * Looks like a flag, because it can only be 0 or 1
     * Valid only for 90 minutes ticket.
     */
    private int T90MCount = 0;
    /**
     * Ground pass counter during current trip.
     * Counter from 1 to 7, then 1 again.
     * Valid only for 90 minutes ticket only with 0x0d layout.
     */
    private int T90GCount = 0;
    //private int T90RelChangeTime = 0;
    //private int T90ChangeTimeInt = 0;
    //private int T90TripTimeLeftInt = 0;
    /**
     * Last transport change time during current 90 minutes trip.
     * In minutes from trip start.
     * Valid only for 90 minutes ticket.
     */
    private int T90RelChangeTime = 0;
    /**
     * Minutes left to end of 90 minutes trip.
     * Valid only for 90 minutes ticket.
     */
    private int T90TripTimeLeft = 0;
    /**
     * Time of last transport change during 90 minutes trip
     * Valid only for 90 minutes ticket.
     */
    private Calendar T90ChangeTime = null;
    /**
     * One time programming bit counter
     * Used for control number of passes.
     */
    private int OTP = 0;
    /**
     * Hash (crypted checksum) of variable ticket block.
     */
    private int Hash = 0;

    private DateFormat df;
    private DateFormat tf;
    private DateFormat dtf;

    /**
     * Class to store and represent Moscow transportation system ticket
     * @param dump {@link NFCaDump}
     */
    public Ticket(NFCaDump dump) {

        DumpValid = true;

        df = new SimpleDateFormat("dd.MM.yyyy");
        tf = new SimpleDateFormat("HH:mm");
        dtf = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        Dump = new ArrayList<Integer>();
        if (dump.getPagesNumber() - 4 + dump.getLastBlockValidPages() < 12) {
            DumpValid = false;
            return;
        }
        for (int i = 0; i < 12; i++){
            Dump.add(dump.getPageAsInt(i));
        }

        OTP = Dump.get(3);

        TicketNumber = (((Dump.get(4) & 0xfff) << 20) | (Dump.get(5) >>> 12)) & 0xffffffffL;

        Layout = ((Dump.get(5) >>> 8) & 0xf);

        App = Dump.get(4) >>> 22;

        Type = (Dump.get(4) >>> 12) & 0x3ff;

        getTypeRelatedInfo();

        /**
         * Temporary variable to extract
         */
        int tmp = 0;

        switch (Layout) {
            case 0x08:
            case 0x0d:
                ValidDays = (Dump.get(8) >>> 8) & 0xff;
                PassesLeft = (Dump.get(9) >>> 16) & 0xff;
                //TODO: TIME FORMAT CHANGE
                //IssuedInt = (Dump.get(8) >>> 16) & 0xffff;
                tmp = (Dump.get(8) >>> 16) & 0xffff;
                if ( tmp != 0) {
                    Issued = Calendar.getInstance();
                    Issued.set(1991, Calendar.DECEMBER, 31);
                    Issued.add(Calendar.DATE, tmp);
                }
                //TODO: TIME FORMAT CHANGE
                //StartUseBeforeInt = (Dump.get(6) >>> 16) & 0xffff;
                //StartUseTimeInt = (Dump.get(6) & 0xfff0) >>> 5;
                tmp = (Dump.get(6) >>> 16) & 0xffff;
                if (tmp != 0) {
                    StartUseBefore = Calendar.getInstance();
                    StartUseBefore.set(1991, Calendar.DECEMBER, 31);
                    StartUseBefore.add(Calendar.DATE, tmp);
                }
                tmp = (Dump.get(6) & 0xfff0) >>> 5;
                if (tmp != 0 ) {
                    StartUse = Calendar.getInstance();
                    StartUse.set(1991, Calendar.DECEMBER, 31);
                    StartUse.add(Calendar.MINUTE, tmp);
                }

                GateEntered = Dump.get(9) & 0xffff;
                //TODO: TIME FORMAT CHANGE
                //LastUsedDateInt = (Dump.get(11) >>> 16) & 0xffff;
                //LastUsedTimeInt = (Dump.get(11) & 0xfff0) >>> 5;
                tmp = (Dump.get(11) >>> 16) & 0xffff;
                if (tmp != 0) {
                    TripStart = Calendar.getInstance();
                    TripStart.set(1991, Calendar.DECEMBER, 31);
                    TripStart.add(Calendar.DATE, tmp);
                    TripStart.add(Calendar.MINUTE, (Dump.get(11) & 0xfff0) >>> 5);
                    TransportType = (Dump.get(9) & 0xc0000000) >>> 30;
                }
                if (TicketClass == C_90UNIVERSAL) {
                    if ((Dump.get(8) & 0xff) != 0 && (Dump.get(8) & 0xff) != 0x80) {
                        T90RelChangeTime = (Dump.get(8) & 0xff) * 5;
                        //TODO: TIME FORMAT CHANGE
                        //T90ChangeTimeInt = T90RelChangeTime + LastUsedTimeInt;
                        T90ChangeTime = (Calendar) TripStart.clone();
                        T90ChangeTime.add(Calendar.MINUTE, T90RelChangeTime);
                    }

                    T90MCount = (Dump.get(9) & 0x20000000) >>> 29;
                    T90GCount = (Dump.get(9) & 0x1c000000) >>> 26;
                    T90TripTimeLeft = 0;
                    if (T90MCount != 0 || T90GCount != 0) {
                        //TODO: TIME FORMAT CHANGE
                        //if (getCurrentTimeInt() >= LastUsedTimeInt) {
                        //    T90TripTimeLeft = 90 - (getCurrentTimeInt() - LastUsedTimeInt);
                        //}
                        if (getNowCalendar().after(TripStart)){
                            T90TripTimeLeft = (int)( 90 -
                                    ((getNowCalendar().getTimeInMillis()
                                            - TripStart.getTimeInMillis())
                                    /(1000L * 60)));
                        }
                        if (T90TripTimeLeft < 0) T90TripTimeLeft = 0;
                    }
                }

                break;
            case 0x0a:
                ValidDays = ((Dump.get(6) >>> 1) & 0x7ffff) / (24 * 60);
                PassesLeft = (Dump.get(8) >>> 24) & 0xff;
                //TODO: TIME FORMAT CHANGE
                // New layout date store format the same as in old layout but
                // base date chenged from 01.01.1991 to 01.01.2016
                // Difference between base dates is 8766 days.
                //IssuedInt = ((Dump.get(6) >>> 20) & 0xfff) + 8766;
                //StartUseTimeInt = ((Dump.get(6) & 0xfffff) >>> 1) % (24 * 60) - 1;
                Issued = Calendar.getInstance();
                Issued.set(1991, Calendar.DECEMBER, 31);
                // New layout date store format the same as in old layout but
                // base date chenged from 01.01.1991 to 01.01.2016
                // Difference between base dates is 8766 days.
                Issued.add(Calendar.DATE, 8766);
                Issued.add(Calendar.DATE, (Dump.get(6) >>> 20) & 0xfff);
                Issued.add(Calendar.MINUTE,((Dump.get(6) & 0xfffff) >>> 1) % (24 * 60) - 1);
                EntranceEntered = (Dump.get(8) >>> 8) & 0xffff;
                //TODO: TIME FORMAT CHANGE
                //LastUsedDateInt = IssuedInt + (((Dump.get(7) >>> 13) & 0x7ffff) / (24 * 60)) ;
                //LastUsedTimeInt = ((Dump.get(7) >>> 13) & 0x7ffff) % (24 * 60);
                TripStart = (Calendar) Issued.clone();
                TripStart.add(Calendar.MINUTE, (Dump.get(7) >>> 13) & 0x7ffff);
                TransportType = Dump.get(7) & 0x3;
                if (TicketClass == C_90UNIVERSAL) {
                    T90MCount = (Dump.get(8) >>> 6) & 0x01;
                    T90RelChangeTime = (Dump.get(7) >>> 2) & 0x3ff;
                    //TODO: TIME FORMAT CHANGE
                    //T90ChangeTimeInt = LastUsedTimeInt + T90RelChangeTime;
                    T90ChangeTime = (Calendar) TripStart.clone();
                    T90ChangeTime.add(Calendar.MINUTE, T90RelChangeTime);

                    //TODO: TIME FORMAT CHANGE
                    //T90TripTimeLeftInt = 0;
                    //if (getCurrentTimeInt() >= LastUsedTimeInt && LastUsedTimeInt != 0) {
                    //    T90TripTimeLeftInt = 90 - (getCurrentTimeInt() - LastUsedTimeInt);
                    //}
                    if (getNowCalendar().after(TripStart)){
                        T90TripTimeLeft = (int)( 90 -
                                ((getNowCalendar().getTimeInMillis()
                                        - TripStart.getTimeInMillis())
                                        /(1000L * 60)));
                    }
                    if (T90TripTimeLeft < 0) T90TripTimeLeft = 0;
                }
                break;
        }

        if ((Layout != 0x08 && Layout != 0x0d && Layout != 0x0a) ||
                App == A_UNKNOWN ||
                Type == T_UNKNOWN ||
                PassesTotal == 0 ||
                PassesTotal < -1 ||
                PassesTotal > 70 ||
                getPassesLeft() < -1
                ) {
            DumpValid = false;
        }

        TripSeqNumber = PassesTotal - getPassesLeft();

        if (TicketClass == C_UNLIM_DAYS){
            TripSeqNumber = getPassesLeft();
            PassesLeft = -1;
            //TODO: TIME FORMAT CHANGE
            //setStartUseDaytime(IssuedInt, StartUseTimeInt);
            //TimeToNextTrip = (LastUsedTimeInt + 21) - getCurrentTimeInt();
            Calendar tmpCal = (Calendar) TripStart.clone();
            tmpCal.add(Calendar.MINUTE, 21);
            TimeToNextTrip = (int)((tmpCal.getTimeInMillis()
                    - getNowCalendar().getTimeInMillis())
                    /(1000L * 60));
            if (TimeToNextTrip < 0) TimeToNextTrip = 0;
        }

		if (Type == TO_VESB) {
			TripSeqNumber = (Dump.get(9) >>> 16) & 0xfff;
			PassesLeft = -1;
		}

        Hash = Dump.get(10);

    }

    /**
     * Get ticket string representation
     * @param c context
     * @return string with ticket string representation
     */
    public String getTicketAsString(Context c) {
        StringBuilder sb = new StringBuilder();
        Calendar tmpCal = null;

        if (DEBUG_TIME)
            sb.append(String.format("! ! ! App time set to %s\n\n",
                    ddf.format(getNowCalendar().getTime())));

        if (!DumpValid) {
// TODO: Translate message
            sb.append("! ! ! Dump not valid or ticket type unknown\n\n");
        }

        //sb.append(Decode.getAppIdDesc(c, App)).append('\n');
        sb.append(Decode.descCardType(c, Type)).append('\n');
        sb.append("\n- - - -\n");

        sb.append(c.getString(R.string.ticket_num)).append(' ');
        sb.append(String.format("%010d", TicketNumber));
        //TODO: TIME FORMAT CHANGE
        //if (StartUseBeforeInt != 0) {
        //    sb.append(" (till ");
        //    sb.append(getReadableDate(StartUseBeforeInt)).append(")");
        //}
        if (StartUseBefore != null) {
            sb.append(" (till ");
            sb.append(this.df.format(StartUseBefore.getTime()));
            sb.append(")");
        }
        sb.append("\n");
        if (ValidDays != 0) {
            sb.append(c.getString(R.string.best_in_days)).append(": ");
            sb.append(ValidDays).append('\n');
        }
        //TODO: TIME FORMAT CHANGE
        //if (IssuedInt != 0) {
        //    sb.append("  from ");
        //    if (TicketClass == C_UNLIM_DAYS){
        //        sb.append(String.format(" %s %s\n    to  %s %s",
        //                getReadableDate(IssuedInt),
        //                getReadableTime(StartUseTimeInt),
        //                getReadableDate(IssuedInt+ValidDays),
        //                getReadableTime(StartUseTimeInt)));
        //    } else {
        //        sb.append(getReadableDate(IssuedInt));
        //        sb.append(" to ");
        //        sb.append(getReadableDate(IssuedInt+ValidDays - 1));
        //    }
        //} else if (StartUseBeforeInt != 0) {
        //    sb.append(c.getString(R.string.start_use_before)).append(": ");
        //    sb.append(getReadableDate(StartUseBeforeInt));
        //}
        if (Issued != null) {
            sb.append("  from ");
            tmpCal = (Calendar)Issued.clone();
            if (TicketClass == C_UNLIM_DAYS){
                tmpCal.add(Calendar.DATE, ValidDays);
                sb.append(String.format(" %s\n    to  %s",
                        this.dtf.format(Issued.getTime()),
                        this.dtf.format(tmpCal.getTime()))
                );
            } else {
                tmpCal.add(Calendar.DATE, ValidDays - 1);
                sb.append(String.format(" %s to %s",
                        this.df.format(Issued.getTime()),
                        this.df.format(tmpCal.getTime()))
                );
            }
        } else if (StartUseBefore != null) {
            sb.append(c.getString(R.string.start_use_before)).append(": ");
            sb.append(String.format("%s",
                    this.df.format(StartUseBefore.getTime()))
            );
        }


        sb.append('\n');

// TODO: Translate messages
// TODO: TIME FORMAT CHANGE
//        if (getPassesLeft() == 0) {
//            sb.append("\n\tE M P T Y\n");
//        } else if (IssuedInt == 0 ) {
//            if (isDateInPast(StartUseBeforeInt)) {
//                sb.append("\n\tE X P I R E D\n");
//            }
//        } else {
//            if (isDateInPast(IssuedInt+ValidDays) &&
//                    TicketClass != C_UNLIM_DAYS) {
//                sb.append("\n\tE X P I R E D\n");
//            }
//        }
        if (getPassesLeft() == 0) {
            sb.append("\n\tE M P T Y\n");
        } else if (Issued == null ) {
            if (StartUseBefore.before(getNowCalendar())) {
                sb.append("\n\tE X P I R E D\n");
            }
        } else {
            tmpCal = (Calendar)Issued.clone();
            tmpCal.add(Calendar.DATE, ValidDays);
            if (tmpCal.before(getNowCalendar()) &&
                    TicketClass != C_UNLIM_DAYS) {
                sb.append("\n\tE X P I R E D\n");
            }
        }

// TODO: TIME FORMAT CHANGE
//        if (TicketClass == C_UNLIM_DAYS) {
//            Calendar tmp = (Calendar)StartUseDayTime.clone();
//            tmp.add(Calendar.HOUR, 24 * ValidDays);
//
//            if (DEBUG_TIME)
//                Log.d(TAG, String.format("Compare: %s\n", ddf.format(tmp.getTime())));
//
//            if (IssuedInt == 0 ) {
//                if (isDateInPast(StartUseBeforeInt)) {
//                    sb.append("\n\tE X P I R E D\n");
//                }
//            } else {
//                if (tmp.compareTo(getNowCalendar()) < 0) {
//                    sb.append("\n\tE X P I R E D\n");
//                } else if (TimeToNextTrip > 0) {
//                    sb.append("\n\tW A I T\n");
//                }
//            }
//        }
        if (TicketClass == C_UNLIM_DAYS) {
            tmpCal = (Calendar)StartUse.clone();
            tmpCal.add(Calendar.HOUR, 24 * ValidDays);

            if (DEBUG_TIME)
                Log.d(TAG, String.format("Compare: %s\n", ddf.format(tmpCal.getTime())));

            if (Issued == null ) {
                if (StartUseBefore.after(tmpCal)) {
                    sb.append("\n\tE X P I R E D\n");
                }
            } else {
                if (tmpCal.compareTo(getNowCalendar()) < 0) {
                    sb.append("\n\tE X P I R E D\n");
                } else if (TimeToNextTrip > 0) {
                    sb.append("\n\tW A I T\n");
                }
            }
        }

        sb.append("\n- - . - -\n");

        if (getPassesLeft() != -1){
            sb.append(c.getString(R.string.passes_left)).append(": ");
            sb.append(getPassesLeft()).append("\n\n");
        }
        
        switch (Layout) {
            case 8:
                if ( GateEntered != 0) {
					sb.append(c.getString(R.string.last_trip));
                    if (getTripSeqNumber() > 0) {
                        sb.append(" №");
                        sb.append(getTripSeqNumber());
                    }
                    sb.append(":\n  ");
           
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
// TODO: TIME FORMAT CHANGE
//                    sb.append(getReadableDate(LastUsedDateInt)).append(" ");
//                    sb.append(c.getString(R.string.at)).append(" ");
//                    sb.append(getReadableTime(LastUsedTimeInt));
                    sb.append(this.df.format(TripStart.getTime())).append(" ");
                    sb.append(c.getString(R.string.at)).append(" ");
                    sb.append(this.tf.format(TripStart.getTime()));
                    sb.append(",\n  ");
                    sb.append(c.getString(R.string.station_last_enter)).append(" ");
                    sb.append(getGateDesc(c, GateEntered));
                    sb.append('\n');

// TODO: Translate messages
                    if (TicketClass == C_90UNIVERSAL) {
                       sb.append("90 minutes trip details:\n");
// TODO: TIME FORMAT CHANGE
//                       if (T90TripTimeLeftInt > 0) {
//                            sb.append("  Time left: ");
//                            sb.append(getReadableTime(T90TripTimeLeftInt)).append('\n');
//                        } else {
//                            sb.append("  Trip time ended\n");
//                        }
                       if (T90TripTimeLeft > 0) {
                            sb.append("  Time left: ");
                            sb.append(getReadableTime(T90TripTimeLeft)).append('\n');
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
// TODO: TIME FORMAT CHANGE
//                        sb.append(getReadableTime(T90ChangeTimeInt)).append('\n');
                        sb.append(this.tf.format(T90ChangeTime.getTime())).append('\n');
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
            case 10:
                if (EntranceEntered != 0) {
                    sb.append(c.getString(R.string.last_trip));
                    if (getTripSeqNumber() > 0) {
                        sb.append(" №");
                        sb.append(getTripSeqNumber());
                    }
                    sb.append(": ");
// TODO: TIME FORMAT CHANGE
//                    sb.append(getReadableDate(LastUsedDateInt)).append(" ");
//                    sb.append(c.getString(R.string.at)).append(" ");
//                    sb.append(getReadableTime(LastUsedTimeInt));
                    sb.append(this.df.format(TripStart.getTime())).append(" ");
                    sb.append(c.getString(R.string.at)).append(" ");
                    sb.append(this.tf.format(TripStart.getTime()));
                    sb.append(",\n  ");
                    sb.append(getStationDesc(c, EntranceEntered));
                    sb.append('\n');

                    if (TicketClass == C_90UNIVERSAL) {
                        sb.append("90 minutes trip details:\n");
// TODO: TIME FORMAT CHANGE
//                        if (T90TripTimeLeftInt > 0) {
//                            sb.append("  Time left: ");
//                            sb.append(getReadableTime(T90TripTimeLeftInt)).append('\n');
//                        } else {
//                            sb.append("  Trip time ended\n");
//                        }
                        if (T90TripTimeLeft > 0) {
                            sb.append("  Time left: ");
                            sb.append(getReadableTime(T90TripTimeLeft)).append('\n');
                        } else {
                            sb.append("  Trip time ended\n");
                        }
                        sb.append("  Metro trip");
                        sb.append(T90MCount);
                        if (T90MCount > 0) {
                            sb.append(" is already done");
                        } else {
                            sb.append(" available");
                        }

                        sb.append('\n');
                        //sb.append("  Ground count: ");
                        //sb.append(T90GCount).append('\n');
                        sb.append("  Change  time: ");
// TODO: TIME FORMAT CHANGE
//                        sb.append(getReadableTime(T90ChangeTimeInt));
                        sb.append(this.tf.format(T90ChangeTime.getTime()));
						sb.append(String.format(" (%02d min)", T90RelChangeTime));
						sb.append('\n');
						//TODO: next line to debug only
						//sb.append(String.format("  %03x, %03x\n",T90ChangeTimeInt, T90RelChangeTime));
                    }

                    if (TicketClass == Ticket.C_UNLIM_DAYS &&
                            TimeToNextTrip > 0) {
                        sb.append(String.format("  %d minutes to next trip", TimeToNextTrip));
                        sb.append('\n');
                    }
                }
                sb.append("\n- - - -\n");
                sb.append("Layuot 10 (0xa).").append('\n');
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

    /**
     *
     * @return {@link Ticket#TransportType}
     */
	public int getType() { return Type; }

    public boolean isTicketFormatValid() { return DumpValid; }

    public long getTicketNumber() { return TicketNumber; }

    public int getTripSeqNumber() { return TripSeqNumber; }

    public int getTicketClass() { return TicketClass; }
	
	public int getLayout() { return Layout; }

    /**
     * Get amount of passes
      * @return
     */
    public int getPassesTotal() {
        if (PassesTotal == 0) getTypeRelatedInfo();
        return PassesTotal;
    }

    /**
     *
     * @return how many passes left
     */
    public int getPassesLeft() { return PassesLeft; }

    /**
     *
     * @return Last change time in minutes related to trip start
     */
    public int getRelTransportChangeTimeMinutes() { return T90RelChangeTime; }

    /**
     *
     * @return for 90 minutes ticket type return amount of transport changes during current trip
     */
    public int getT90ChangeCount() { return T90GCount + T90MCount; }

    /**
     *
     * @return How many days ticket valid from issue date
     */
    public int getValidDays() { return ValidDays; }

/* Internal functions */

    /**
     *
     * @return One Time Programming bitwise field as string
     */
    private String getOTPasBinaryString() {
        return Integer.toBinaryString(OTP);
    }

    /**
     *
     * @return get hash (crypted checksum) as hex string
     */
    private String getHashAsHexString() {
        return Integer.toHexString(Hash);
    }

    /**
     *
     * @param c context
     * @param id gate id
     * @return Gate description as string
     */
    private String getGateDesc(Context c, int id) {
        String trType ="";
        switch (TransportType) {
            case TT_METRO:
                trType +=c.getString(R.string.tt_metro);
                break;
            case TT_GROUND:
                trType += c.getString(R.string.tt_ground);
                break;
            case TT_UNKNOWN:
                trType += c.getString(R.string.tt_unknown);
                break;
            default:
                trType += "!!! Internal error !!!";
                break;
        }
        String SN = Lang.tarnliterate(Turnstiles.getStationByTurnstile(id));
        String gateNumType = "№" + id + " (" + trType + ")";
        if (SN.length() != 0) {
            return gateNumType + '\n' +
                    "  " + c.getString(R.string.station) + " " +
                    SN;
        } else {
            return gateNumType;
        }
    }

    /**
     *
     * @param c context
     * @param id station entrance id
     * @return station entrance description
     */
    private String getStationDesc(Context c, int id) {
        StringBuilder sb = new StringBuilder();
        String TransportType ="";
        switch (this.TransportType) {
            case TT_METRO:
                TransportType +=c.getString(R.string.tt_metro);
                break;
            case TT_GROUND:
                TransportType += c.getString(R.string.tt_ground);
                break;
            case TT_UNKNOWN:
                TransportType += c.getString(R.string.tt_unknown);
                break;
            default:
                TransportType += "!!! Internal error !!!";
                break;
        }
        
        String StationName = Lang.tarnliterate(Stations.getStationByStationId(id));

        if (StationName.length() != 0) {
            sb.append("  " + c.getString(R.string.station) + " " + StationName + '\n');
        }
        
        sb.append(String.format("    id: %1$d [0x%1$04x] (%2$s)", id, TransportType));

        return sb.toString();

    }

    /**
     * Set ticket type related class data:
     * <ul>
     *      <li>{@link Ticket#TicketClass}</li>
     *      <li>{@link Ticket#PassesTotal}</li>
     *      <li>{@link Ticket#WhereSell} (for several types)</li>
     *      <li>{@link Ticket#ValidDays} (for day limited tickets)</li>
     * </ul>
     */
    private void getTypeRelatedInfo() {
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
                WhereSell = WS_GROUND;
                break;
            case TN_G2:
                PassesTotal = 2;
                TicketClass = C_GROUND;
                WhereSell = WS_GROUND;            
                break;
            case TN_G3_DRV:
                PassesTotal = 3;
                TicketClass = C_GROUND;
                WhereSell = WS_DRIVER;
                break;
            case TN_G5:
                PassesTotal = 5;
                TicketClass = C_GROUND;
                WhereSell = WS_GROUND;
                break;
            case TN_G11:
                PassesTotal = 11;
                TicketClass = C_GROUND;
                WhereSell = WS_GROUND;
                break;
            case TN_G20:
                PassesTotal = 20;
                TicketClass = C_GROUND;
                WhereSell = WS_GROUND;
                break;
            case TN_G40:
                PassesTotal = 40;
                TicketClass = C_GROUND;
                WhereSell = WS_GROUND;
                break;
            case TN_G60:
                PassesTotal = 60;
                TicketClass = C_GROUND;
                WhereSell = WS_GROUND;
                break;
            case TN_GB1_DRV:
                PassesTotal = 1;
                TicketClass = C_GROUND_B;
                WhereSell = WS_DRIVER;
                break;
            case TN_GB2:
                PassesTotal = 2;
                TicketClass = C_GROUND_B;
                WhereSell = WS_GROUND;
                break;
            case TN_GAB1:
                PassesTotal = 1;
                TicketClass = C_GROUND_AB;
                WhereSell = WS_DRIVER;
                break;
            case TN_U1_DRV:
                PassesTotal = 1;
                TicketClass = C_UNIVERSAL;
                WhereSell = WS_DRIVER;
                break;
            case TN_U1:
                TicketClass = C_UNIVERSAL;
                PassesTotal = 1;
                WhereSell = WS_METRO;
                break;
            case TN_U2:
                PassesTotal = 2;
                TicketClass = C_UNIVERSAL;
                WhereSell = WS_METRO;
                break;
            case TN_U5:
                PassesTotal = 5;
                TicketClass = C_UNIVERSAL;
                WhereSell = WS_METRO;
                break;
            case TN_U11:
                PassesTotal = 11;
                TicketClass = C_UNIVERSAL;
                WhereSell = WS_METRO;
                break;
            case TN_U20:
                PassesTotal = 20;
                TicketClass = C_UNIVERSAL;
// TODO: As code example. Need to check.     
                WhereSell = WS_GROUND;
                WhereSell |= WS_METRO;
                break;
            case TN_U40:
                PassesTotal = 40;
                TicketClass = C_UNIVERSAL;
                WhereSell = WS_METRO;
                break;
            case TN_U60:
                PassesTotal = 60;
                TicketClass = C_UNIVERSAL;
                WhereSell = WS_METRO;
                break;
            case TN_UL1D:
                PassesTotal = -1;
                TicketClass = C_UNLIM_DAYS;
                if (ValidDays == 0) ValidDays = 1;
                WhereSell = WS_METRO;
                break;
            case TN_UL3D:
                PassesTotal = -1;
                TicketClass = C_UNLIM_DAYS;
                if (ValidDays == 0) ValidDays = 3;
                WhereSell = WS_METRO;
                break;
            case TN_UL7D:
                PassesTotal = -1;
                TicketClass = C_UNLIM_DAYS;
                if (ValidDays == 0) ValidDays = 7;
                WhereSell = WS_METRO;
                break;
            case TN_90U1_G:
                PassesTotal = 1;
                TicketClass = C_90UNIVERSAL;            
                WhereSell = WS_GROUND;
                break;
            case TN_90U1:
                PassesTotal = 1;
                TicketClass = C_90UNIVERSAL;
                WhereSell = WS_METRO;
                break;
            case TN_90U2_G:
                PassesTotal = 2;
                TicketClass = C_90UNIVERSAL;
                WhereSell = WS_GROUND;
                break;
            case TN_90U2:
                PassesTotal = 2;
                TicketClass = C_90UNIVERSAL;
                WhereSell = WS_METRO;
                break;
            case TN_90U5:
                PassesTotal = 5;
                TicketClass = C_90UNIVERSAL;
                WhereSell = WS_METRO;
                break;
            case TN_90U11:
                PassesTotal = 11;
                TicketClass = C_90UNIVERSAL;
                WhereSell = WS_METRO;
                break;
            case TN_90U20:
                PassesTotal = 20;
                TicketClass = C_90UNIVERSAL;
                WhereSell = WS_METRO;
                break;
            case TN_90U40:
                PassesTotal = 40;
                TicketClass = C_90UNIVERSAL;
                WhereSell = WS_METRO;
                break;
            case TN_90U60:
                PassesTotal = 60;
                TicketClass = C_90UNIVERSAL;
                WhereSell = WS_METRO;
                break;
            default:
                PassesTotal = 0;
                break;
        }
    }

/* Time related internal functions */

    /**
     *
     * @param days days from base date
     * @return date string representation
     */
    private String getReadableDate(int days) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(1991, Calendar.DECEMBER, 31);
        c.add(Calendar.DATE, days);
        return this.df.format(c.getTime());
    }

    /**
     *
     * @param time minutes from midnight
     * @return Hours and minutes in readable form
     */
    private String getReadableTime(int time){
        return String.format("%02d:%02d",
                time / 60,
                time % 60);
    }

    /**
     *
     * @return minutes since current day midnight
     */
    private int getCurrentTimeInt() {
        Calendar now = getNowCalendar();
        return now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE);
    }

    /**
     * Is date in past related to now
     * @param dateInt date provided as number of days from base date
     * @return
     */
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

    /**
     * Set Calendar object StartUseDayTime from provided parameters
     * @param date date provided as number of days from base date
     * @param time time in minutes ftom date day start
     */
//    private void setStartUseDaytime(int date, int time){
//        StartUseDayTime.clear();
//        StartUseDayTime.set(1991, Calendar.DECEMBER, 31);
//        StartUseDayTime.add(Calendar.DATE, date);
//        StartUseDayTime.add(Calendar.MINUTE, time);
//        if (DEBUG_TIME)
//            Log.d(TAG,String.format("Set: %s\n",ddf.format(StartUseDayTime.getTime())));
//    }
}
