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
import android.util.Log;

import net.basov.nfc.NFCaDump;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import android.support.annotation.IntDef;

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

    public static final DateFormat ddf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    // Constants definition
    /* Used transport types */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TT_UNKNOWN, TT_GROUND, TT_METRO})
    public @interface PassTransportType {}
    public static final int TT_UNKNOWN = 0;
    public static final int TT_METRO = 1;
    public static final int TT_GROUND = 2;

    /* TicketApp */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({A_UNKNOWN, A_METRO, A_GROUND, A_SOCIAL, A_METRO_LIGHT, A_UNIVERSAL})
    public @interface TicketApp {}
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
    /**
     * Temporary universal social ticket
     */
    public static final int TO_VESB = 150;
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
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({C_UNKNOWN, C_OLD_METRO, C_OLD_SPECIAL, C_GROUND, C_GROUND_B, C_GROUND_AB, C_UNIVERSAL, C_UNLIM_DAYS, C_90UNIVERSAL})
    public @interface TicketClass {}
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
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({WS_UNKNOWN, WS_METRO, WS_GROUND, WS_DRIVER})
    public @interface WhereSell {}
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
    /**
     * TicketApp. Possible values:
     * <ul>
     *     <li>{@link Ticket#A_UNKNOWN}</li>
     *     <li>{@link Ticket#A_UNIVERSAL}</li>
     *     <li>{@link Ticket#A_GROUND}</li>
     *     <li>{@link Ticket#A_METRO}</li>
     *     <li>{@link Ticket#A_SOCIAL}</li>
     *     <li>{@link Ticket#A_METRO_LIGHT}</li>
     * </ul>
     */
    private int App = A_UNKNOWN;
    /**
     * Type. Possible value:
     * <ul>
     *     <li>{@link Ticket#T_UNKNOWN}</li>
     *     <li>{@link Ticket#TO_M1}</li>
     *     <li>{@link Ticket#TO_M2}</li>
     *     <li>{@link Ticket#TO_M3}</li>
     *     <li>{@link Ticket#TO_M4}</li>
     *     <li>{@link Ticket#TO_M5}</li>
     *     <li>{@link Ticket#TO_M10}</li>
     *     <li>{@link Ticket#TO_M20}</li>
     *     <li>{@link Ticket#TO_M60}</li>
     *     <li>{@link Ticket#TO_BAGGAGE}</li>
     *     <li>{@link Ticket#TO_BAGGAGE_AND_PASS}</li>
     *     <li>{@link Ticket#TO_UL70}</li>
     *     <li>{@link Ticket#TO_VESB}</li>
     *     <li>{@link Ticket#TN_90U1}</li>
     *     <li>{@link Ticket#TN_90U1_G}</li>
     *     <li>{@link Ticket#TN_90U2}</li>
     *     <li>{@link Ticket#TN_90U2_G}</li>
     *     <li>{@link Ticket#TN_90U5}</li>
     *     <li>{@link Ticket#TN_90U11}</li>
     *     <li>{@link Ticket#TN_90U20}</li>
     *     <li>{@link Ticket#TN_90U40}</li>
     *     <li>{@link Ticket#TN_90U60}</li>
     *     <li>{@link Ticket#TN_G1}</li>
     *     <li>{@link Ticket#TN_G2}</li>
     *     <li>{@link Ticket#TN_G3_DRV}</li>
     *     <li>{@link Ticket#TN_G5}</li>
     *     <li>{@link Ticket#TN_G20}</li>
     *     <li>{@link Ticket#TN_G40}</li>
     *     <li>{@link Ticket#TN_G60}</li>
     *     <li>{@link Ticket#TN_GAB1}</li>
     *     <li>{@link Ticket#TN_GB1_DRV}</li>
     *     <li>{@link Ticket#TN_GB2}</li>
     *     <li>{@link Ticket#TN_U1}</li>
     *     <li>{@link Ticket#TN_U1_DRV}</li>
     *     <li>{@link Ticket#TN_U2}</li>
     *     <li>{@link Ticket#TN_U5}</li>
     *     <li>{@link Ticket#TN_U11}</li>
     *     <li>{@link Ticket#TN_U20}</li>
     *     <li>{@link Ticket#TN_U40}</li>
     *     <li>{@link Ticket#TN_U60}</li>
     *     <li>{@link Ticket#TN_UL1D}</li>
     *     <li>{@link Ticket#TN_UL3D}</li>
     *     <li>{@link Ticket#TN_UL7D}</li>
     * </ul>
     */
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

    private final static DateFormat df = new SimpleDateFormat("dd.MM.yyyy");;
    private final static DateFormat tf = new SimpleDateFormat("HH:mm");;
    private final static DateFormat dtf = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    public Ticket() {
        Dump = new ArrayList<Integer>();
        DumpValid = true;
    }
// TODO: add constructor to ArrayList<String>
    /**
     * Class to store and represent Moscow transportation system ticket.
     * Dump content filled from NFCaDump
     * @param dump {@link NFCaDump}
     */
    public Ticket(NFCaDump dump) {

        this();

        if (dump.getPagesNumber() - 4 + dump.getLastBlockValidPages() < 12) {
            DumpValid = false;
            return;
        }
        for (int i = 0; i < 12; i++) {
            Dump.add(dump.getPageAsInt(i));
        }

        processTicket();
    }

    /**
     * Class to store and represent Moscow transportation system ticket
     * Dump content filled from ArrayList&lt;Integer>
     * @param dump ArrayList&lt;Integer>
     */
    public Ticket(ArrayList<Integer> dump) {

        this();

        Dump = dump;

        processTicket();
    }

    private void processTicket() {

        OTP = Dump.get(3);

        TicketNumber = (((Dump.get(4) & 0xfff) << 20) | (Dump.get(5) >>> 12)) & 0xffffffffL;

        Layout = ((Dump.get(5) >>> 8) & 0xf);

        App = Dump.get(4) >>> 22;

        Type = (Dump.get(4) >>> 12) & 0x3ff;

        getTypeRelatedInfo();

        /**
         * Temporary variable to extract values from dump pages
         */
        int tmp = 0;

        switch (Layout) {
            case 0x08:
            case 0x0d:
                ValidDays = (Dump.get(8) >>> 8) & 0xff;
                PassesLeft = (Dump.get(9) >>> 16) & 0xff;
                tmp = (Dump.get(8) >>> 16) & 0xffff;
                if ( tmp != 0) {
                    Issued = Calendar.getInstance();
                    Issued.clear();
                    Issued.set(1991, Calendar.DECEMBER, 31);
                    Issued.add(Calendar.DATE, tmp);
                }
                tmp = (Dump.get(6) >>> 16) & 0xffff;
                if (tmp != 0) {
                    StartUseBefore = Calendar.getInstance();
                    StartUseBefore.clear();
                    StartUseBefore.set(1991, Calendar.DECEMBER, 31);
                    StartUseBefore.add(Calendar.DATE, tmp);
                }
                tmp = (Dump.get(6) & 0xfff0) >>> 5;
                if (tmp != 0 ) {
                    StartUse = Calendar.getInstance();
                    StartUse.clear();
                    StartUse.set(1991, Calendar.DECEMBER, 31);
                    StartUse.add(Calendar.MINUTE, tmp);
                }

                GateEntered = Dump.get(9) & 0xffff;
                tmp = (Dump.get(11) >>> 16) & 0xffff;
                if (tmp != 0) {
                    TripStart = Calendar.getInstance();
                    TripStart.clear();
                    TripStart.set(1991, Calendar.DECEMBER, 31);
                    TripStart.add(Calendar.DAY_OF_MONTH, tmp);
                    TripStart.add(Calendar.MINUTE, (Dump.get(11) & 0xfff0) >>> 5);
                    TransportType = (Dump.get(9) & 0xc0000000) >>> 30;
                }
                if (TicketClass == C_90UNIVERSAL) {
                    if ((Dump.get(8) & 0xff) != 0 && (Dump.get(8) & 0xff) != 0x80) {
                        T90RelChangeTime = (Dump.get(8) & 0xff) * 5;
                        T90ChangeTime = (Calendar) TripStart.clone();
                        T90ChangeTime.add(Calendar.MINUTE, T90RelChangeTime);
                    }

                    T90MCount = (Dump.get(9) & 0x20000000) >>> 29;
                    T90GCount = (Dump.get(9) & 0x1c000000) >>> 26;
                    T90TripTimeLeft = 0;
                    if (T90MCount != 0 || T90GCount != 0) {
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
                Issued = Calendar.getInstance();
                Issued.clear();
                Issued.set(2015, Calendar.DECEMBER, 31);
                Issued.add(Calendar.DAY_OF_MONTH, (Dump.get(6) >>> 20) & 0xfff);
                Issued.add(Calendar.MINUTE,((Dump.get(6) >>> 1) & 0x7ffff) % (24 * 60));
                EntranceEntered = (Dump.get(8) >>> 8) & 0xffff;

                // Base date (Issued) used with zerro time. Set it.
                TripStart = getBaseDate((Calendar) Issued.clone());
                TripStart.add(Calendar.MINUTE, (Dump.get(7) >>> 13) & 0x7ffff);
                TransportType = Dump.get(7) & 0x3;
                if (TicketClass == C_90UNIVERSAL) {
                    T90MCount = (Dump.get(8) >>> 6) & 0x01;
                    T90RelChangeTime = (Dump.get(7) >>> 2) & 0x3ff;
                    T90ChangeTime = (Calendar) TripStart.clone();
                    T90ChangeTime.add(Calendar.MINUTE, T90RelChangeTime);

                    if (getNowCalendar().after(TripStart)){
                        T90TripTimeLeft = (int)( 90 -
                                ((getNowCalendar().getTimeInMillis()
                                        - TripStart.getTimeInMillis())
                                        /(1000L * 60)));
                    }
                    if (T90TripTimeLeft < 0) T90TripTimeLeft = 0;
                }

// TODO: Check. Is it right place to to make day limited tickets time correct.
// TODO: May be better way to do this at display time
                if (getTicketClass() == C_UNLIM_DAYS) Issued.add(Calendar.MINUTE, -1);

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
            Calendar NextTrip = (Calendar) TripStart.clone();
            NextTrip.add(Calendar.MINUTE, 21);
            long NextTripInSeconds = NextTrip.getTimeInMillis() / 1000L;
            long NowInSeconds = getNowCalendar().getTimeInMillis() / 1000L;
            if (NextTripInSeconds > NowInSeconds ) {
                TimeToNextTrip = (int) (NextTripInSeconds - NowInSeconds) / 60;
            } else {
                TimeToNextTrip = 0;
            }
        }

		if (Type == TO_VESB) {
			TripSeqNumber = (Dump.get(9) >>> 16) & 0xfff;
			PassesLeft = -1;
		}

        Hash = Dump.get(10);

    }

    /**
     * Get Calendar object which set to start of the day.
     * @param cal calendar object
     * @return calendar object
     */
    public Calendar getBaseDate(Calendar cal) {
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.HOUR, 0);
        return cal;
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
        sb.append(String.format("%010d", getTicketNumber()));
        if (StartUseBefore != null) {
            sb.append(" (till ");
            sb.append(this.df.format(StartUseBefore.getTime()));
            sb.append(")");
        }
        sb.append("\n");
        if (getValidDays() != 0) {
            sb.append(c.getString(R.string.best_in_days)).append(": ");
            sb.append(getValidDays());
            sb.append('\n');
        }
        if (Issued != null) {
            sb.append("  from ");
            tmpCal = (Calendar)Issued.clone();
            if (getTicketClass() == C_UNLIM_DAYS){
                tmpCal.add(Calendar.DATE, getValidDays());
                sb.append(String.format(" %s\n    to  %s",
                        this.dtf.format(Issued.getTime()),
                        this.dtf.format(tmpCal.getTime()))
                );
            } else {
                tmpCal.add(Calendar.DATE, getValidDays() - 1);
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

        if (TicketClass == C_UNLIM_DAYS) {
            tmpCal = (Calendar)Issued.clone();
            tmpCal.add(Calendar.HOUR, 24 * getValidDays());

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

        sb.append("\n- - - -\n");

        if (getPassesLeft() != -1){
            sb.append(c.getString(R.string.passes_left)).append(": ");
            sb.append(getPassesLeft()).append("\n\n");
        }
        
        switch (Layout) {
            case 8:
                if ( getGateEntered() != 0) {
					sb.append(c.getString(R.string.last_trip));
                    if (getTripSeqNumber() > 0) {
                        sb.append(" №");
                        sb.append(getTripSeqNumber());
                    }
                    sb.append(":\n  ");
           
                    sb.append(c.getString(R.string.station_last_enter)).append(": ");
                    sb.append(getGateDesc(c, getGateEntered())).append('\n');
                }
                sb.append("\n- - - -\n");
                sb.append("Layuot 8 (0x8).").append('\n');
                break;
            case 13:
                if (getGateEntered() != 0) {

                    sb.append(c.getString(R.string.last_trip));
                    if (getTripSeqNumber() > 0) {
                        sb.append(" №");
                        sb.append(getTripSeqNumber());
                    }
                    sb.append(": ");
                    sb.append(this.df.format(TripStart.getTime())).append(" ");
                    sb.append(c.getString(R.string.at)).append(" ");
                    sb.append(this.tf.format(TripStart.getTime()));
                    sb.append(",\n  ");
                    sb.append(c.getString(R.string.station_last_enter)).append(" ");
                    sb.append(getGateDesc(c, getGateEntered()));
                    sb.append('\n');

// TODO: Translate messages
                    if (TicketClass == C_90UNIVERSAL) {
                       sb.append("90 minutes trip details:\n");
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
                if (getEntranceEntered() != 0) {
                    sb.append(c.getString(R.string.last_trip));
                    if (getTripSeqNumber() > 0) {
                        sb.append(" №");
                        sb.append(getTripSeqNumber());
                    }
                    sb.append(": ");
                    sb.append(this.df.format(TripStart.getTime())).append(" ");
                    sb.append(c.getString(R.string.at)).append(" ");
                    sb.append(this.tf.format(TripStart.getTime()));
                    sb.append(",\n  ");
                    sb.append(getStationDesc(c, getEntranceEntered()));
                    sb.append('\n');

                    if (TicketClass == C_90UNIVERSAL) {
                        sb.append("90 minutes trip details:\n");
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
                        sb.append("  Change  time: ");
                        sb.append(this.tf.format(T90ChangeTime.getTime()));
						sb.append(String.format(" (%02d min)", T90RelChangeTime));
						sb.append('\n');
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

    /**
     *
     * @return {@link Ticket#App}
     */
    public int getApp() { return App; }

    public boolean isTicketFormatValid() { return DumpValid; }

    public long getTicketNumber() { return TicketNumber; }

    /**
     * Ticket blank must be start used before this date.
     * Usually printed at the right of ticket number
     * @return {@link Ticket#StartUseBefore} calendar object
     */
    public Calendar getStartUseBefore() {
        return StartUseBefore;
    }

    /**
     * Date when ticket was issued.
     * For day limited tickets also has time.
     * @return {@link Ticket#Issued} calendar object represent issue date/time
     */
    public Calendar getIssued() {
        return Issued;
    }

    /**
     * When current trip start
     * @return {@link Ticket#TripStart}
     */
    public Calendar getTripStart() {
        return TripStart;
    }


    public int getTripSeqNumber() { return TripSeqNumber; }

    /**
     * This trip current entered gate
     * @return {@link Ticket#GateEntered} gate id
     */
    public int getGateEntered() {
        return GateEntered;
    }

    /**
     * This trip current Station entrance id or Ground transport validator id
     * @return {@link Ticket#EntranceEntered} entrance id
     */
    public int getEntranceEntered() {
        return EntranceEntered;
    }

    /**
     * Get transport type. Possible values:
     *
     * @return {@link Ticket#TransportType} transport type
     */
    public int getTransportType() {
        return TransportType;
    }

    public int getTicketClass() { return TicketClass; }
	
	public int getLayout() { return Layout; }

    /**
     * How manu passes this ticket issued for.
      * @return Amount of passes on the ticket
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
//    private String getReadableDate(int days) {
//        Calendar c = Calendar.getInstance();
//        c.clear();
//        c.set(1991, Calendar.DECEMBER, 31);
//        c.add(Calendar.DATE, days);
//        return this.df.format(c.getTime());
//    }

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
//    private int getCurrentTimeInt() {
//        Calendar now = getNowCalendar();
//        return now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE);
//    }

    /**
     * Is date in past related to now
     * @param dateInt date provided as number of days from base date
     * @return
     */
//    private boolean isDateInPast(int dateInt) {
//        Calendar date = Calendar.getInstance();
//        date.clear();
//        date.set(1991, Calendar.DECEMBER, 31);
//        date.add(Calendar.DATE, dateInt);
//        if (date.compareTo(getNowCalendar()) <= 0){
//            return true;
//        }
//        return false;
//    }

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
