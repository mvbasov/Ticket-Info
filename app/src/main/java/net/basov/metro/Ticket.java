/**
 * The MIT License (MIT)

 Copyright (c) 2015,2016 Mikhail Basov
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import android.support.annotation.IntDef;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import net.basov.ticketinfo.R;

import ru.valle.tickets.ui.Decode;
import ru.valle.tickets.ui.Lang;

/**
 * Class to store and represent Moscow transportation system ticket.
 * Constructors:
 * <ul>
 *   <li>{@link Ticket#Ticket()} doesn't automatically parse dump</li>
 *   <li>{@link Ticket#Ticket(NFCaDump)} automatically parse provided dump</li>
 *   <li>{@link Ticket#Ticket(ArrayList)} automatically parse provided dump</li>
 * </ul>
 */

public class Ticket {
    // Debug facility
    static final String TAG = "tickets";
    private boolean DEBUG_TIME = false;

    public static final DateFormat DDF = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    // Constants definition

    public static final String FILE_EXT = ".txt";


    /* Used transport types */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TT_UNKNOWN, TT_GROUND, TT_METRO})
    public @interface PassTransportType {}
    public static final int TT_UNKNOWN = 0;
    public static final int TT_METRO = 1;
    public static final int TT_GROUND = 2;

    /* Used metro types */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MT_UNKNOWN, MT_METRO, MT_MONORAIL, MT_MCC})
    public @interface PassMetroType {}
    public static final int MT_UNKNOWN = 0;
    public static final int MT_METRO = 1;
    public static final int MT_MONORAIL = 2;
    public static final int MT_MCC = 3;

    /* Ticket Application */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({A_UNKNOWN, A_METRO, A_GROUND, A_SOCIAL, A_METRO_LIGHT, A_UNIVERSAL})
    public @interface TicketApp {}
    public static final int A_UNKNOWN = 0;
    public static final int A_METRO = 262;
    public static final int A_GROUND = 264;
    public static final int A_SOCIAL = 266;
    public static final int A_METRO_LIGHT = 270;
    public static final int A_UNIVERSAL = 279;

    /* Ticket type */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            T_UNKNOWN,
            TO_M1, TO_M2, TO_M3, TO_M4, TO_M5, TO_M10, TO_M20, TO_M60,
            TO_BAGGAGE_AND_PASS, TO_BAGGAGE, TO_UL70,
            TO_VESB,
            TN_U1, TN_U1_DRV, TN_U2, TN_U5, TN_U11, TN_U20, TN_U40, TN_U60,
            TN_90U1, TN_90U1_G, TN_90U2, TN_90U2_G, TN_90U5, TN_90U11, TN_90U20, TN_90U60,
            TN_UL1D, TN_UL3D, TN_UL7D,
            TN_G1, TN_G2, TN_G3_DRV, TN_G5, TN_G11, TN_G20, TN_G40, TN_G60,
            TN_GB1_DRV, TN_GAB1

    })
    public @interface TicketType {}
    public static final int T_UNKNOWN = 0;
/* Old tickets types (layout 0x8) */
    /**
     * Old metro ticket for 1 pass
     */
    public static final int TO_M1 = 120;
    /**
     * Old metro ticket for 2 passes
     */
    public static final int TO_M2 = 121;
    /**
     * Old metro ticket for 3 passes
     */
    public static final int TO_M3 = 122;
    /**
     * Old metro ticket for 4 passes
     */
    public static final int TO_M4 = 123;
    /**
     * Old metro ticket for 5 passes
     */
    public static final int TO_M5 = 126;
    /**
     * Old metro ticket for 10 passes
     */
    public static final int TO_M10 = 127;
    /**
     * Old metro ticket for 20 passes
     */
    public static final int TO_M20 = 128;
    /**
     * Old metro ticket for 60 passes
     */
    public static final int TO_M60 = 129;
    /**
     * Old metro ticket for baggage and pass
     */
    public static final int TO_BAGGAGE_AND_PASS = 130;
    /**
     * Old metro ticket for baggage only
     */
    public static final int TO_BAGGAGE = 131;
    /**
     * Old metro ticket for 70 (passes|days)?
     */
    public static final int TO_UL70 = 149;
    /**
     * Temporary universal social ticket
     */
    public static final int TO_VESB = 150;

/* New ticket types (layout 0xd and 0xa) */

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
    public static final int TN_90U1 = 437; // 1 pass, 90 minutes, universal, sell in metro (1016236236, with paper check)
    public static final int TN_90U2_G = 438; // 2 passes, 90 minutes, universal, sell on ground (1014908560, with paper check)
    public static final int TN_90U2 = 422; // 2 passes, 90 minutes, universal, sell in metro (1016237832, with paper check)
    public static final int TN_90U5 = 423; // 5 passes, 90 minutes, universal (1016363888, with paper check)
    public static final int TN_90U11 = 424; // 11 passes, 90 minutes, universal (1016235763, with paper check)
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
     * Trip can include one underground and unlimited amount ground passes during 90 minutes
     */
    public static final int C_90UNIVERSAL = C_UNKNOWN + 8;

    /* Where sell */
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
    public static final int WS_GROUND = WS_METRO << 1;
    /**
     * Ticket sell by ground transport driver
     */
    public static final int WS_DRIVER = WS_METRO << 2;
    
    /**
     * Tecket state
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(
        flag = true,
        value = {TS_UNKNOWN, TS_NEVER_USED, TS_READY,TS_IN_TRIP, TS_EMPTY, TS_EXPIRED}
    )
    public @interface TicketState {}
    public static final int TS_UNKNOWN = 0;
    public static final int TS_NEVER_USED = 1;
    public static final int TS_READY = 1 << 1;
    public static final int TS_IN_TRIP = 1 << 2;
    public static final int TS_EMPTY = 1 << 3;
    public static final int TS_EXPIRED = 1 << 4;

    // Data fields definition

    /**
     * Ticket chip content.
     * ArrayList of pages.
     * Each page (4 bytes) represented as Integer
     */
    private ArrayList<Integer> mDump;

    /**
     * Name of the ticket.
     * Usefull for test datatsets.
     */
    private String mName;

    private String mFileName;

    private String mDDDRem;
    
    /**
     * Describe parser error if exists
     */
    private String mParserError;
    /**
     * Date and time to calculate time difference related values.
     * To process real ticket in normal circumstance set to now time.
     * For test and debug can be set to any point of time.
     */
    private Calendar mTimeToCompare = null;
    /**
     * Is dump valid
     */
    private boolean mTicketFormatValid = false;
    /**
     * Number printed on ticket
     */
    private long mTicketNumber = 0L;
    /**
     * Ticket layout
     */
    private int mLayout = 0;
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
    private int mApp = A_UNKNOWN;
    /**
     * Ticket type. Possible value:
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
    private int mTicketType = T_UNKNOWN;

    /**
     * Ticket Class. Possible values:
     * <ul>
     *     <li>{@link Ticket#C_UNKNOWN}</li>
     *     <li>{@link Ticket#C_OLD_SPECIAL}</li>
     *     <li>{@link Ticket#C_OLD_SPECIAL}</li>
     *     <li>{@link Ticket#C_GROUND}</li>
     *     <li>{@link Ticket#C_GROUND_B}</li>
     *     <li>{@link Ticket#C_GROUND_AB}</li>
     *     <li>{@link Ticket#C_UNIVERSAL}</li>
     *     <li>{@link Ticket#C_90UNIVERSAL}</li>
     *     <li>{@link Ticket#C_UNLIM_DAYS}</li>
     * </ul>
     */
    private int mTicketClass = C_UNKNOWN;
    /**
     * Where ticket was sell. Possible values:
     * <ul>
     *     <li>{@link Ticket#WS_METRO}</li>
     *     <li>{@link Ticket#WS_GROUND}</li>
     *     <li>{@link Ticket#WS_DRIVER}</li>
     * </ul>
     */
    private int mWhereSell = WS_UNKNOWN;
    /**
     * Start use till date time.
     * Used for day limited tickets
     */
    private Calendar mStartUseTill = null;
    /**
     * Ticket issue date
     * !!! Only date, without time !!!
     */
    private Calendar mIssued = null;
    /**
     * Time of the day in minutes when ticket 1-st time used.
     * Actual for day limited tickets
     */
    private int mFirstUseTime;
    /**
     * Ticket blank "Use before" date
     */
    private Calendar mStartUseBefore = null;
    /**
     * Ticket valid days from the begin of {@link Ticket#mIssued}
     */
    private int mValidDays = 0;
    /**
     * Number of allowed passes at the begin of ticket use.
     * Set to -1 for tickets limited by day of use.
     */
    private int mPassesTotal = 0;
    /**
     * Passes left
     */
    private int mPassesLeft = 0;
    /**
     * Pass (or trip for 90 minutes tickets) sequence number
     */
    private int mTripSeqNumber = 0;

    /**
     * When current trip start
     */
    private Calendar mTripStart = null;
    /**
     * Time to next pass (in minutes) for day limited tickets
     */
    private int mTimeToNextTrip = 0;
    /**
     * Last entered gate id.
     * This value valid only for 0x08 and 0x0d layouts.
     * One underground station has several turnstiles (gates) in each entrance
     */
    private int mTurnstileEntered = 0;

    /**
     * Last entered station entrance id.
     * One station may have one or several entrance with different id
     */
    private int mEntranceEntered = 0;
    /**
     * Transport type. Possible values:
     * <li>
     *     <li>{@link Ticket#TT_UNKNOWN}</li>
     *     <li>{@link Ticket#TT_METRO}</li>
     *     <li>{@link Ticket#TT_GROUND}</li>
     * </li>
     */
    private int mPassTransportType = TT_UNKNOWN;
    /**
     * Metro type. Possible values:
     * <ul>
     *     <li>{@link Ticket#MT_UNKNOWN}</li>
     *     <li>{@link Ticket#MT_METRO}</li>
     *     <li>{@link Ticket#MT_MONORAIL}</li>
     *     <li>{@link Ticket#MT_MCC}</li>
     * </ul>
     */
    private int mPassMetroType = MT_UNKNOWN;

    private ArrayList<Integer> mMetroTripTransportHistory = new ArrayList<Integer>();
    /**
     * Metro pass counter during current trip.
     * Looks like a flag, because it can only be 0 or 1
     * Valid only for 90 minutes ticket.
     */
    private int mT90MCount = 0;
    /**
     * Ground pass counter during current trip.
     * Counter from 1 to 7, then 1 again.
     * Valid only for 90 minutes ticket only with 0x0d layout.
     */
    private int mT90GCount = 0;
    /**
     * Last transport change time during current 90 minutes trip.
     * In minutes from trip start.
     * Valid only for 90 minutes ticket.
     */
    private int mT90RelChangeTime = 0;
    /**
     * Minutes left to end of 90 minutes trip.
     * Valid only for 90 minutes ticket.
     */
    private int mT90TripTimeLeft = 0;
    /**
     * Time of last transport change during 90 minutes trip
     * Valid only for 90 minutes ticket.
     */
    private Calendar mT90ChangeTime = null;
    /**
     * Current ticket state.
     * Possible values:
     * <li>
     *     <li>{@link Ticket#TS_UNKNOWN}</li>
     *     <li>{@link Ticket#TS_NEVER_USED}</li>
     *     <li>{@link Ticket#TS_EXPIRED}</li>
     *     <li>{@link Ticket#TS_EMPTY}</li>
     *     <li>{@link Ticket#TS_IN_TRIP}</li>
     * </li>
     */
    private int mTicketState = TS_UNKNOWN;
    /**
     * One time programming bit counter
     * Used for control number of passes.
     */
    private int mOTP = 0;
    /**
     * mHash (encrypted checksum) of variable ticket block.
     */
    private int mHash = 0;

    public final static DateFormat DF = new SimpleDateFormat("dd.MM.yyyy");
    public final static DateFormat TF = new SimpleDateFormat("HH:mm");
    public final static DateFormat DTF = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    /**
     * Only create object.
     * Fields doesn't automatically filed.
     */
    public Ticket() {
        mDump = new ArrayList<Integer>();
        mTimeToCompare = Calendar.getInstance();
        setTicketFormatIsValid(true);
    }

    /**
     * mDump content filled from NFCaDump
     * Other fields automatically filled.
     * @param dump {@link NFCaDump}
     */
    public Ticket(NFCaDump dump) {
        this();

        ArrayList<Integer> tmpDump = new ArrayList<Integer>();

        for (int i = 0; i < 12; i++) {
            tmpDump.add(dump.getPageAsInt(i));
        }

        setDump(tmpDump);

        processTicket();
    }

    /**
     * mDump content filled from ArrayList&lt;Integer>
     * Other fields automatically filled.
     * @param dump ArrayList&lt;Integer>
     */
    public Ticket(ArrayList<Integer> dump) {

        this();

        setDump(dump);

        processTicket();
    }

    /**
     * {@link Ticket#mDump} filled from <b>dump</b> parameters<br/>
     * {@link Ticket#mTimeToCompare} filled from <b>timeToCompare</b> parameters<br/>
     * Other fields automatically filled.
     * @param dump ArrayList&lt;Integer>
     * @param timeToCompare
     */
    public Ticket(ArrayList<Integer> dump, Calendar timeToCompare) {

        this();

        setDump(dump);
        setTimeToCompare(timeToCompare);
        DEBUG_TIME = true;
        processTicket();
    }

    public static String createDumpFileName(Ticket ticket) {

        StringBuilder dName = new StringBuilder();
        dName.append("");

        if (ticket.isTicketFormatValid()) {
            dName.append(String.format("%010d", ticket.getTicketNumber()));
            switch (ticket.getTicketClass()){
                case Ticket.C_UNLIM_DAYS:
                    dName.append(String.format("-%dd",ticket.getValidDays()));
                    dName.append(String.format("-%03d",ticket.getTripSeqNumber()));
                    dName.append(getTCsuffix(ticket));
                    break;
                case Ticket.TO_VESB:
                    dName.append("-su");
                    dName.append(String.format("-%04d", ticket.getTripSeqNumber()));
                    break;
                case Ticket.C_90UNIVERSAL:
                    dName.append(String.format("-%02d", ticket.getPassesTotal()));
                    dName.append(String.format("-%02d", ticket.getTripSeqNumber()));                     
                    dName.append(getTCsuffix(ticket));
                    if (ticket.getLayout() == 0x0d) {
                        dName.append(String.format(".%02d", ticket.getRelTransportChangeTimeMinutes()));
                        dName.append(String.format(".%1d", ticket.getT90ChangeCount()));
                    } else if (ticket.getLayout() == 0x0a) {
                        dName.append(String.format(".%02d", ticket.getRelTransportChangeTimeMinutes()));
                    }
                    break;
                default:
                    dName.append(String.format("-%02d", ticket.getPassesTotal()));
                    dName.append(String.format("-%02d", ticket.getTripSeqNumber()));                     
                    dName.append(getTCsuffix(ticket));
                    break;
            }
        } else {
            dName.append("xxxxxxxxxx-xx-xx");
        }

        return dName.toString();
    }

    public void setFileName(String fn) { mFileName = fn; }

    public String getFileName() { return mFileName; }

    /**
     * Process mDump field and generate other fields content
     */
    private void processTicket() {

        mOTP = mDump.get(3);

        setTicketNumber((((mDump.get(4) & 0x00000fff) << 20) | ((mDump.get(5) & 0xfffff000)>>> 12)) & 0xffffffffL);

        setLayout(((mDump.get(5) & 0x00000f00) >>> 8));

        mApp = (mDump.get(4) & 0xfffffc00) >>> 22;

        //noinspection WrongConstant
        setTicketType((mDump.get(4) & 0x003ff000) >>> 12);

        setTypeRelatedInfo();

        /**
         * Temporary variable to extract values from dump pages
         */
        int tmp;

        switch (getLayout()) {
            case 0x08:
            case 0x0d:
                setValidDays((mDump.get(8) & 0x0000ff00) >>> 8);
                mPassesLeft = (mDump.get(9) & 0x00ff0000) >>> 16;
                tmp = (mDump.get(8) & 0xffff0000) >>> 16;
                if ( tmp != 0) {
                    mIssued = Calendar.getInstance();
                    mIssued.clear();
                    mIssued.set(1991, Calendar.DECEMBER, 31);
                    mIssued.add(Calendar.DATE, tmp);
                }
                tmp = (mDump.get(6) & 0xffff0000) >>> 16;
                if (tmp != 0 && mLayout == 0xd) {
                    mStartUseBefore = Calendar.getInstance();
                    mStartUseBefore.clear();
                    mStartUseBefore.set(1991, Calendar.DECEMBER, 31);
                    mStartUseBefore.add(Calendar.DATE, tmp);
                }
                tmp = (mDump.get(6) & 0x0000fff0) >>> 5;
                if (tmp != 0 ) {
// TODO: Check hear. If field contain only minutes frm mIssued
                    mStartUseTill = Calendar.getInstance();
                    mStartUseTill.clear();
                    mStartUseTill.set(1991, Calendar.DECEMBER, 31);
                    mStartUseTill.add(Calendar.MINUTE, tmp);
                    mFirstUseTime = tmp;
                }
                mTurnstileEntered = mDump.get(9) & 0x0000ffff;
                tmp = (mDump.get(11) & 0xffff0000) >>> 16;
                if (tmp != 0) {
                    mTripStart = Calendar.getInstance();
                    mTripStart.clear();
                    mTripStart.set(1991, Calendar.DECEMBER, 31);
                    mTripStart.add(Calendar.DAY_OF_MONTH, tmp);
                    mTripStart.add(Calendar.MINUTE, (mDump.get(11) & 0x0000fff0) >>> 5);
                    if (mLayout == 0xd) {
                        //noinspection WrongConstant
                        setPassTransportType((mDump.get(9) & 0xc0000000) >>> 30);
                        //noinspection WrongConstant
                        setPassMetroType(
                                (mDump.get(11) & 0x00000018) >>> 3
                        );
                        if ((mDump.get(11) & 0x00000001) == 1) {
                            if (getPassMetroType() == MT_METRO) {
                                mMetroTripTransportHistory.add(MT_MONORAIL);
                                mMetroTripTransportHistory.add(MT_METRO);
                            } else {
                                mMetroTripTransportHistory.add(MT_METRO);
                                mMetroTripTransportHistory.add(MT_MONORAIL);                               
                            }
                        }
                    }
                }
                if (mLayout == 0x8) {
                    setPassTransportType(TT_METRO);
                    setPassMetroType(MT_METRO);
                    if (mIssued != null) {
                        mStartUseBefore = (Calendar) mIssued.clone();
                        mStartUseBefore.add(Calendar.DATE, mValidDays);
                    }
                }
                if (getTicketClass() == C_90UNIVERSAL) {
                    tmp = mDump.get(8) & 0x0000007f;
                    if (tmp != 0) {
                        mT90RelChangeTime = tmp * 5;
                        mT90ChangeTime = (Calendar) mTripStart.clone();
                        mT90ChangeTime.add(Calendar.MINUTE, mT90RelChangeTime);
                    }

                    mT90MCount = (mDump.get(9) & 0x20000000) >>> 29;
                    mT90GCount = (mDump.get(9) & 0x1c000000) >>> 26;
                    mT90TripTimeLeft = 0;
                    if (mT90MCount != 0 || mT90GCount != 0) {
                        if (getTimeToCompare().after(mTripStart)){
                            mT90TripTimeLeft = (int)( 90 -
                                ((getTimeToCompare().getTimeInMillis()
                                            - mTripStart.getTimeInMillis())
                                    /(1000L * 60)));
                        }
                        if (mT90TripTimeLeft < 0) mT90TripTimeLeft = 0;
                    }
                }

                break;
            case 0x0a:
                setValidDays(((mDump.get(6) & 0x000ffffe) >>> 1) / (24 * 60));
                mPassesLeft = (mDump.get(8) & 0xff000000) >>> 24;
                mIssued = Calendar.getInstance();
                mIssued.clear();
                mIssued.set(2015, Calendar.DECEMBER, 31);
                mIssued.add(Calendar.DAY_OF_MONTH, (mDump.get(6) & 0x0fff00000) >>> 20);
                if (mTicketClass == C_UNLIM_DAYS && mPassesLeft != 0)
                     // mPassesLeft == 0 is unused flag for day limited tickets
                    mFirstUseTime = ((mDump.get(6) & 0x000ffffe) >>> 1) % (24 * 60);;

                mEntranceEntered = (mDump.get(8) & 0x00ffff00) >>> 8;

                tmp = (mDump.get(7) & 0xffffe000) >>> 13;
                if (tmp != 0) {
                    mTripStart = (Calendar) mIssued.clone();
                    mTripStart.add(Calendar.MINUTE, tmp);
                    //noinspection WrongConstant
                    setPassTransportType(mDump.get(7) & 0x00000003);
                    int metroType = (mDump.get(8) & 0x000000ff);
                    //1-st pass in current trip stored in MSB
                    // 0b11223344
                    // 11 - 1-st pass;
                    // 22 - 2-nd pass (transport change) in current trip
                    // ...
                    for (int i = 0; i < 4; i++) {
                        tmp = (metroType >>> ((3 - i) * 2)) & 0x3;
                        if (tmp != 0)
                            mMetroTripTransportHistory.add(tmp);
                    }
                    if (mMetroTripTransportHistory != null &&
                            !mMetroTripTransportHistory.isEmpty())
                        //noinspection WrongConstant
                        setPassMetroType(
                                mMetroTripTransportHistory.get(
                                        mMetroTripTransportHistory.size() - 1
                                )
                        );
                }
                if (getTicketClass() == C_90UNIVERSAL) {
                    mT90MCount = (mDump.get(8) & 0x00000040) >>> 6;
                    mT90RelChangeTime = (mDump.get(7) & 0x00000ffc) >>> 2;
                    mT90ChangeTime = (Calendar) mTripStart.clone();
                    mT90ChangeTime.add(Calendar.MINUTE, mT90RelChangeTime);

                    if (getTimeToCompare().after(mTripStart)) {
                        mT90TripTimeLeft = (int) (90 -
                                ((getTimeToCompare().getTimeInMillis()
                                        - mTripStart.getTimeInMillis())
                                        / (1000L * 60)));
                    }
                    if (mT90TripTimeLeft < 0) mT90TripTimeLeft = 0;
                } else if (getTicketClass() == C_UNLIM_DAYS) {
                    mStartUseTill = (Calendar) mIssued.clone();
                    mStartUseTill.add(Calendar.MINUTE, getValidDays() * 24 * 60);
                    if(getTripSeqNumber() == 0) setTypeRelatedInfo();
// TODO: Check. Is it right place to to make day limited tickets time correct.
// TODO: May be better way to do this at display time
                    //mIssued.add(Calendar.MINUTE, -1);
                }

                break;
        }

        if (getTicketClass() == C_UNLIM_DAYS){
            setTripSeqNumber(getPassesLeft());
            mPassesLeft = -1;
            if (mTripStart != null) {
                Calendar NextTrip = (Calendar) mTripStart.clone();
                NextTrip.add(Calendar.MINUTE, 21);
                long NextTripInSeconds = NextTrip.getTimeInMillis() / 1000L;
                long NowInSeconds = getTimeToCompare().getTimeInMillis() / 1000L;
                if (NextTripInSeconds > NowInSeconds) {
                    mTimeToNextTrip = (int) (NextTripInSeconds - NowInSeconds) / 60;
                } else {
                    mTimeToNextTrip = 0;
                }
            }
        } else if (getTicketType() != TO_VESB){
            setTripSeqNumber(mPassesTotal - getPassesLeft());
        } else {
            setTripSeqNumber((mDump.get(9) & 0x0fff0000) >>> 16);
            mPassesLeft = -1;
        }

        mHash = mDump.get(10);

    }
    
    public void setDDDRem(String dddRem) { this.mDDDRem = dddRem; }

    /**
     * Set IC dump which ticket based on
     * Not automaticaly parsed
     * @param dump
     */
    public void setDump(ArrayList<Integer> dump) {
        if ( dump.size() < 12 ) {
            setTicketFormatIsValid(false);
            mParserError += " Dump too short";
            return;
        }
        this.mDump = dump;
    }

    /**
     * Get IC dump which ticket based on
     * @return
     */
    public ArrayList<Integer> getDump() {
        return mDump;
    }

    public void setTicketFormatIsValid(boolean isValid) {
        this.mTicketFormatValid = isValid;
    }

    /**
     * Get ticket name {@link Ticket#mName}
     * @return
     */
    public String getName() {
        return mName;
    }

    @Override
    public String toString() {
        return mName;
    }
    /**
     * Set ticket name {@link Ticket#mName}
     * @param name
     */
    public void setName(String name) {
        mName = name;
    }

    /**
     * Get ticket string representation
     * @param c context
     * @return string with ticket string representation
     */
    public String getTicketAsString(Context c) {
        StringBuilder sb = new StringBuilder();
        Calendar tmpCal = null;

        if (DEBUG_TIME) {
            sb.append(String.format("!!! App. time set to %s\n",
                                    DDF.format(getTimeToCompare().getTime())));
            sb.append(String.format("DDD rem: %s\n",
                                    mDDDRem));
            sb.append("\n- - - -\n");            
        }
        
        if (!mTicketFormatValid) {
// TODO: Translate message
            sb.append("!!! mDump not valid or ticket type unknown\n\n");
        }

        sb.append(Decode.descCardType(c, getTicketType())).append('\n');
        sb.append("\n- - - -\n");

        sb.append(c.getString(R.string.ticket_num)).append(' ');
        sb.append(String.format("%010d", getTicketNumber()));
        if (mStartUseBefore != null) {
            sb.append(" (till ");
            sb.append(DF.format(mStartUseBefore.getTime()));
            sb.append(")");
        }
        sb.append("\n");
        if (getValidDays() != 0) {
            sb.append(c.getString(R.string.best_in_days)).append(": ");
            sb.append(getValidDays());
            sb.append('\n');
        }
        if (mIssued != null) {
            sb.append("  from ");
            tmpCal = (Calendar) mIssued.clone();
            if (getTicketClass() == C_UNLIM_DAYS){
                tmpCal.add(Calendar.DATE, getValidDays());
                sb.append(String.format(" %s\n    to  %s",
                        DTF.format(mIssued.getTime()),
                        DTF.format(tmpCal.getTime()))
                );
            } else {
                tmpCal.add(Calendar.DATE, getValidDays() - 1);
                sb.append(String.format(" %s to %s",
                        DF.format(mIssued.getTime()),
                        DF.format(tmpCal.getTime()))
                );
            }
        } else if (mStartUseBefore != null) {
            sb.append(c.getString(R.string.start_use_before)).append(": ");
            sb.append(String.format("%s",
                    DF.format(mStartUseBefore.getTime()))
            );
        }

        sb.append('\n');

// TODO: Translate messages
        if (getPassesLeft() == 0) {
            sb.append("\n\tE M P T Y\n");
        } else if (mIssued == null ) {
            if (mStartUseBefore.before(getTimeToCompare())) {
                sb.append("\n\tE X P I R E D\n");
            }
        } else {
            tmpCal = (Calendar) mIssued.clone();
            tmpCal.add(Calendar.DATE, getValidDays());
            if (tmpCal.before(getTimeToCompare()) &&
                    getTicketClass() != C_UNLIM_DAYS) {
                sb.append("\n\tE X P I R E D\n");
            }
        }

        if (getTicketClass() == C_UNLIM_DAYS) {
            if (mIssued == null ) {
                if (mStartUseBefore.after(tmpCal)) {
                    sb.append("\n\tE X P I R E D\n");
                } else {
                    sb.append("\n\tN E V E R  U S E D\n");
                }
            } else {

                tmpCal = (Calendar) mIssued.clone();
                tmpCal.add(Calendar.HOUR, 24 * getValidDays());

                if (DEBUG_TIME)
                    Log.d(TAG, String.format("Compare: %s\n", DDF.format(tmpCal.getTime())));

                if (tmpCal.compareTo(getTimeToCompare()) < 0) {
                    sb.append("\n\tE X P I R E D\n");
                } else if (mTimeToNextTrip > 0) {
                    sb.append("\n\tW A I T\n");
                }
                if (tmpCal.after(getTimeToCompare())
                        && getTripSeqNumber() == 0)
                    sb.append("\n\tN E V E R  U S E D\n");
            }
            if (mStartUseTill != null) {
                sb.append(String.format("\n\tStart use till: %s",
                        DTF.format(mStartUseTill.getTime())));
            }

        }

        sb.append("\n- - - -\n");

        if (getPassesLeft() != -1){
            sb.append(c.getString(R.string.passes_left)).append(": ");
            sb.append(getPassesLeft()).append("\n\n");
        }
        
        switch (getLayout()) {
            case 8:
                if ( getTurnstileEntered() != 0) {
                    sb.append(c.getString(R.string.last_trip));
                    if (getTripSeqNumber() > 0) {
                        sb.append(" №");
                        sb.append(getTripSeqNumber());
                    }
                    sb.append(":\n  ");

                    sb.append(c.getString(R.string.station_last_enter)).append(": ");
                    sb.append(getTurnstileDesc(c, getTurnstileEntered())).append('\n');
                }
                sb.append("\n- - - -\n");
                sb.append("Layout 8 (0x8).").append('\n');
                break;
            case 13:
                if (getTurnstileEntered() != 0) {

                    sb.append(c.getString(R.string.last_trip));
                    if (getTripSeqNumber() > 0) {
                        sb.append(" №");
                        sb.append(getTripSeqNumber());
                    }
                    sb.append(": ");
                    sb.append(DF.format(mTripStart.getTime())).append(" ");
                    sb.append(c.getString(R.string.at)).append(" ");
                    sb.append(TF.format(mTripStart.getTime()));
                    sb.append(",\n  ");
                    sb.append(c.getString(R.string.station_last_enter)).append(" ");
                    sb.append(getTurnstileDesc(c, getTurnstileEntered()));
                    sb.append('\n');

// TODO: Translate messages
                    if (getTicketClass() == C_90UNIVERSAL) {
                       sb.append("90 minutes trip details:\n");
                       if (mT90TripTimeLeft > 0) {
                            sb.append("  Time left: ");
                            sb.append(getReadableTime(mT90TripTimeLeft)).append('\n');
                        } else {
                            sb.append("  Trip time ended\n");
                        }
                        sb.append("  Metro  count: ");
                        sb.append(mT90MCount);
                        if (mT90MCount > 0) {
                            sb.append(" (no more allowed)");
                        }
                        sb.append('\n');
                        sb.append("  Ground count: ");
                        sb.append(mT90GCount).append('\n');
                        sb.append("  Change  time: ");
                        sb.append(TF.format(mT90ChangeTime.getTime())).append('\n');
                    }
                    
                    if (getTicketClass() == Ticket.C_UNLIM_DAYS &&
                            mTimeToNextTrip > 0) {
                        sb.append(String.format("  %d minutes to next trip", mTimeToNextTrip));
                        sb.append('\n');
                    }

                }
                sb.append("\n- - - -\n");
                sb.append("Layout 13 (0xd).").append('\n');
                break;
            case 10:
                if (getEntranceEntered() != 0) {
                    sb.append(c.getString(R.string.last_trip));
                    if (getTripSeqNumber() > 0) {
                        sb.append(" №");
                        sb.append(getTripSeqNumber());
                    }
                    sb.append(": ");
                    sb.append(DF.format(mTripStart.getTime())).append(" ");
                    sb.append(c.getString(R.string.at)).append(" ");
                    sb.append(TF.format(mTripStart.getTime()));
                    sb.append(",\n  ");
                    sb.append(getStationDesc(c, getEntranceEntered()));
                    sb.append('\n');

                    if (getTicketClass() == C_90UNIVERSAL) {
                        sb.append("90 minutes trip details:\n");
                        if (mT90TripTimeLeft > 0) {
                            sb.append("  Time left: ");
                            sb.append(getReadableTime(mT90TripTimeLeft)).append('\n');
                        } else {
                            sb.append("  Trip time ended\n");
                        }
                        sb.append("  Metro trip");
                        sb.append(mT90MCount);
                        if (mT90MCount > 0) {
                            sb.append(" is already done");
                        } else {
                            sb.append(" available");
                        }

                        sb.append('\n');
                        sb.append("  Change  time: ");
                        sb.append(TF.format(mT90ChangeTime.getTime()));
                        sb.append(String.format(" (%02d min)", mT90RelChangeTime));
                        sb.append('\n');
                    }

                    if (getTicketClass() == Ticket.C_UNLIM_DAYS &&
                            mTimeToNextTrip > 0) {
                        sb.append(String.format("  %d minutes to next trip", mTimeToNextTrip));
                        sb.append('\n');
                    }
                }
                sb.append("\n- - - -\n");
                sb.append("Layout 10 (0xa).").append('\n');
                break;
            default:
                sb.append(c.getString(R.string.unknown_layout)).append(": ");
                sb.append(getLayout()).append('\n');
                break;
        }

        sb.append(String.format("App ID: %1$d (0x%1$03x), ", mApp));
        sb.append(String.format("Ticket type: %1$d (0x%1$03x)\n", getTicketType()));

        sb.append(c.getString(R.string.ticket_hash)).append(": ");
        sb.append(getHashAsHexString()).append('\n');
        sb.append(c.getString(R.string.otp)).append(": ");
        sb.append(getOTPasBinaryString()).append('\n');
        
        return sb.toString();
    }

    private void addParserError(String errorString) {
        if (mParserError != null)
            mParserError += ", ";
        else
            mParserError = "";
        mParserError += errorString;
        setTicketFormatIsValid(false);
    }

    public String getParserError() {return this.mParserError; }

    /**
     * Set number printed on ticket
     * @param ticketNumber
     */
    public void setTicketNumber(long ticketNumber) {
        if (ticketNumber <= 0L || ticketNumber > 0xffffffffL)
                addParserError("Ticket number wrong");
        mTicketNumber = ticketNumber;
    }

    public void setTicketType(@TicketType int ticketType) {
        switch (ticketType) {
            case TO_BAGGAGE:
            case TO_BAGGAGE_AND_PASS:
            case TO_M1:
            case TO_M2:
            case TO_M3:
            case TO_M4:
            case TO_M5:
            case TO_M10:
            case TO_M20:
            case TO_M60:
            case TO_UL70:
            case TO_VESB:
            case TN_90U1:
            case TN_90U1_G:
            case TN_90U2:
            case TN_90U2_G:
            case TN_90U5:
            case TN_90U11:
            case TN_90U20:
            case TN_90U40:
            case TN_90U60:
            case TN_G1:
            case TN_G2:
            case TN_G3_DRV:
            case TN_G5:
            case TN_G11:
            case TN_G20:
            case TN_G40:
            case TN_G60:
            case TN_GAB1:
            case TN_GB1_DRV:
            case TN_GB2:
            case TN_U1:
            case TN_U1_DRV:
            case TN_U2:
            case TN_U5:
            case TN_U11:
            case TN_U20:
            case TN_U40:
            case TN_U60:
            case TN_UL1D:
            case TN_UL3D:
            case TN_UL7D:
                break;
            default:
                addParserError("Wrong type");
                break;
        }
        this.mTicketType = ticketType;
    }

    /**
     * @return {@link Ticket#mTicketType}
     */
	public int getTicketType() { return mTicketType; }

    public void setApp(int app) {
        mApp = app;
    }

    /**
     * @return {@link Ticket#mApp}
     */
    public int getApp() { return mApp; }

    public boolean isTicketFormatValid() { return mTicketFormatValid; }

    public long getTicketNumber() { return mTicketNumber; }

    public void setStartUseBefore(Calendar startUseBefore) {
        mStartUseBefore = startUseBefore;
    }

    /**
     * Ticket blank must be start used before this date.
     * Usually printed at the right of ticket number
     * @return {@link Ticket#mStartUseBefore} calendar object
     */
    public Calendar getStartUseBefore() {
        return mStartUseBefore;
    }

    public void setIssued(Calendar issued) {
        mIssued = issued;
    }

    /**
     * Date when ticket was issued.
     * For day limited tickets also has time.
     * @return {@link Ticket#mIssued} calendar object represent issue date/time
     */
    public Calendar getIssued() {
        return mIssued;
    }

    public int getFirstUseTime() {
        return mFirstUseTime;
    }

    public void setFirstUseTime(int firstUseTime) {
        mFirstUseTime = firstUseTime;
    }

    public void setTripStart(Calendar tripStart) {
        mTripStart = tripStart;
    }

    /**
     * When current trip start
     * @return {@link Ticket#mTripStart}
     */
    public Calendar getTripStart() {
        return mTripStart;
    }

    public int getTimeToNextTrip() {
        return mTimeToNextTrip;
    }

    public void setTimeToNextTrip(int timeToNextTrip) {
        mTimeToNextTrip = timeToNextTrip;
    }

    public void setTripSeqNumber(int tripSeqNumber) {
        if (tripSeqNumber < 0 || tripSeqNumber > 255)
            addParserError("Wrong trip seq number");
        mTripSeqNumber = tripSeqNumber;
    }

    public int getTripSeqNumber() { return mTripSeqNumber; }

    public void setTurnstileEntered(int turnstileEntered) {
        mTurnstileEntered = turnstileEntered;
    }

    /**
     * This trip current entered gate
     * @return {@link Ticket#mTurnstileEntered} gate id
     */
    public int getTurnstileEntered() {
        return mTurnstileEntered;
    }

    public void setEntranceEntered(int entranceEntered) {
        mEntranceEntered = entranceEntered;
    }

    /**
     * This trip current Station entrance id or Ground transport validator id
     * @return {@link Ticket#mEntranceEntered} entrance id
     */
    public int getEntranceEntered() {
        return mEntranceEntered;
    }

    public void setPassTransportType(@PassTransportType int passTransportType) {
        switch (passTransportType) {
            case Ticket.TT_GROUND:
            case Ticket.TT_METRO:
            case Ticket.TT_UNKNOWN:
                break;
            default:
                addParserError("Wrong transport type");
                break;
        }
        this.mPassTransportType = passTransportType;

    }
    /**
     * Get pass transport type. Possible values:
     * @return {@link Ticket#mPassTransportType} transport type
     */
    public int getPassTransportType() {
        return mPassTransportType;
    }

    public int getPassMetroType() {
        return mPassMetroType;
    }

    public void setPassMetroType(@PassMetroType int passMetroType) {
        switch (passMetroType) {
            case Ticket.MT_UNKNOWN:
            case Ticket.MT_METRO:
            case Ticket.MT_MONORAIL:
            case Ticket.MT_MCC:
                break;
            default:
                addParserError("Wrong metro type");
                break;
        }
        this.mPassMetroType = passMetroType;
    }

    public void addMetroTripTransportHistory(@PassMetroType int metroType) {
        switch (metroType) {
            case MT_METRO:
            case MT_MONORAIL:
            case MT_MCC:
                break;
            default:
                addParserError("Wrong metro type");
                break;
        }
        this.mMetroTripTransportHistory.add(metroType);
    }

    public void setTicketClass(@TicketClass int ticketClass) {
        switch (ticketClass) {
            case Ticket.C_UNKNOWN:
            case Ticket.C_OLD_METRO:
            case Ticket.C_OLD_SPECIAL:
            case Ticket.C_GROUND:
            case Ticket.C_GROUND_B:
            case Ticket.C_GROUND_AB:
            case Ticket.C_UNIVERSAL:
            case Ticket.C_UNLIM_DAYS:
            case Ticket.C_90UNIVERSAL:
                break;
            default:
                addParserError("Wrong ticket class");
                break;
        }

        this.mTicketClass = ticketClass;
    }

    public int getTicketClass() { return mTicketClass; }

    public void setLayout(int layout) {
        switch (layout) {
            case 0x08:
            case 0x0d:
            case 0x0a:
                break;
            default:
                addParserError("Wrong layout");
        }

        this.mLayout = layout;
    }

	public int getLayout() { return mLayout; }

    public void setPassesTotal(int passesTotal) {
        mPassesTotal = passesTotal;
    }

    /**
     * How many passes this ticket issued for.
      * @return Amount of passes on the ticket
     */
    public int getPassesTotal() {
        return mPassesTotal;
    }

    public void setPassesLeft(int passesLeft) {
        mPassesLeft = passesLeft;
    }

    /**
     *
     * @return how many passes left
     */
    public int getPassesLeft() { return mPassesLeft; }

    public void setT90RelChangeTime(int t90RelChangeTime) {
        mT90RelChangeTime = t90RelChangeTime;
    }

    /**
     *
     * @return Last change time in minutes related to trip start
     */
    public int getRelTransportChangeTimeMinutes() { return mT90RelChangeTime; }

    public int getT90MCount() {
        return mT90MCount;
    }

    public void setT90MCount(int t90MCount) {
        mT90MCount = t90MCount;
    }

    /**
     *
     * @return for 90 minutes ticket type return amount of transport changes during current trip
     */
    public int getT90ChangeCount() { return mT90GCount + mT90MCount; }

    public void setValidDays(int validDays) {
        if (validDays < 0 || validDays > 120) {
            setTicketFormatIsValid(false);
            addParserError("Valid days number wrong");
        }
        if (this.mValidDays == 0)
        /**
         * if not 0 it is day limited and set and set by
         * {@link Ticket#setTypeRelatedInfo()}
         */

            this.mValidDays = validDays;
    }

    public Calendar getT90ChangeTime() {
        return mT90ChangeTime;
    }

    public void setT90ChangeTime(Calendar t90ChangeTime) {
        mT90ChangeTime = t90ChangeTime;
    }

    /**
     *
     * @return How many days ticket valid from issue date
     */
    public int getValidDays() { return mValidDays; }

    public void detectTicketState() {
        Calendar tmpCal = null;

        setTicketState(TS_READY);
        if (getPassesLeft() == 0) {
            setTicketState(TS_EMPTY);
        } else if (mIssued == null ) {
            if (mStartUseBefore.before(getTimeToCompare())) {
                setTicketState(TS_EXPIRED);
            }
        } else {
            tmpCal = (Calendar) mIssued.clone();
            tmpCal.add(Calendar.DATE, getValidDays());
            if (tmpCal.before(getTimeToCompare()) &&
                    getTicketClass() != C_UNLIM_DAYS) {
                setTicketState(TS_EXPIRED);
            }
            if (getPassesLeft() == getPassesTotal())
                setTicketState(TS_NEVER_USED);
        }


        if (getTicketClass() == C_UNLIM_DAYS) {
            if (mIssued == null ) {
                if (mStartUseBefore.after(tmpCal)) {
                    setTicketState(TS_EXPIRED);
                } else {
                    setTicketState(TS_NEVER_USED);
                }
            } else {

                tmpCal = (Calendar) mIssued.clone();
                tmpCal.add(Calendar.HOUR, 24 * getValidDays());

                if (DEBUG_TIME)
                    Log.d(TAG, String.format("Compare: %s\n", DDF.format(tmpCal.getTime())));

                if (tmpCal.compareTo(getTimeToCompare()) < 0) {
                    setTicketState(TS_EXPIRED);
                } else if (mTimeToNextTrip > 0) {
                    //sb.append("\n\tW A I T\n");
                }
                if (tmpCal.after(getTimeToCompare())
                        && getTripSeqNumber() == 0)
                    setTicketState(TS_NEVER_USED);
            }
        }
    }

    public int getTicketState() { return mTicketState; }

    public void setTicketState(@TicketState int ts) { mTicketState = ts; }

/* Internal functions */

    /**
     *
     * @return One Time Programming bitwise field as string
     */
    private String getOTPasBinaryString() {
        return Integer.toBinaryString(mOTP);
    }

    /**
     *
     * @return get hash (encrypted checksum) as hex string
     */
    public String getHashAsHexString() {
        return Integer.toHexString(mHash);
    }

    /**
     *
     * @param c context
     * @param id gate id
     * @return Gate description as string
     */
    private String getTurnstileDesc(Context c, int id) {
        String trType ="";
        switch (getPassTransportType()) {
            case TT_METRO:
                switch (getPassMetroType()) {
                    case MT_METRO:
                        trType +=c.getString(R.string.mt_metro);
                        break;
                    case MT_MONORAIL:
                        trType +=c.getString(R.string.mt_monorail);
                        break;
                    case MT_MCC:
                        trType +=c.getString(R.string.mt_mcc);
                        break;
                    case MT_UNKNOWN:
                        break;
                    default:
                        trType += c.getString(R.string.mt_unknown);
                        break;
                }

                if (mMetroTripTransportHistory.size() > 1) {
                    trType += ", hist.: ";
                    for (int tt : mMetroTripTransportHistory) {
                        switch (tt) {
                            case MT_METRO:
                                trType += "M";
                                break;
                            case MT_MONORAIL:
                                trType += "R";
                                break;
                            case MT_MCC:
                                trType += "C";
                                break;
                            default:
                            case MT_UNKNOWN:
                                trType += "U";
                                break;
                        }
                    }
                }
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

        String gateNumAndType = "№" + id + " (" + trType + ")";

        String SN = Lang.transliterate(
                Lookup.findStationInfoByTsId(id+"", getDataFileURIasString(c))
        );

        if ((SN.length() != 0 ) && (getPassTransportType() != TT_GROUND)) {
            return gateNumAndType + '\n' +
                    "  " + c.getString(R.string.station) + " " +
                    SN;
        } else {
            return gateNumAndType;
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
        switch (getPassTransportType()) {
            case TT_METRO:
                switch (getPassMetroType()) {
                    case MT_MCC:
                        TransportType +=c.getString(R.string.mt_mcc);
                        break;
                    case MT_METRO:
                        TransportType +=c.getString(R.string.mt_metro);
                        break;
                    case MT_MONORAIL:
                        TransportType +=c.getString(R.string.mt_monorail);
                        break;
                    case MT_UNKNOWN:
                    default:
                        TransportType += c.getString(R.string.tt_unknown);
                        break;
                }
                if (mMetroTripTransportHistory.size() > 1) {
                    TransportType += ", hist.: ";
                    for (int tt : mMetroTripTransportHistory) {
                        switch (tt) {
                            case MT_METRO:
                                TransportType += "M";
                                break;
                            case MT_MONORAIL:
                                TransportType += "R";
                                break;
                            case MT_MCC:
                                TransportType += "C";
                                break;
                            default:
                            case MT_UNKNOWN:
                                TransportType += "U";
                                break;
                        }
                    }
                }
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

        String StationName = Lang.transliterate(
                Lookup.findStationById(id+"", getDataFileURIasString(c))
        );

        if ((StationName.length() != 0) && (getPassTransportType() != TT_GROUND)) {
            sb.append("  ");
            sb.append(c.getString(R.string.station));
            sb.append(" ");
            sb.append(StationName);
            sb.append('\n');
        }
        
        sb.append(String.format("    id: %1$d [0x%1$04x] (%2$s)", id, TransportType));

        return sb.toString();

    }

    /**
     * Set ticket type related class data:
     * <ul>
     *      <li>{@link Ticket#mTicketClass}</li>
     *      <li>{@link Ticket#mPassesTotal}</li>
     *      <li>{@link Ticket#mWhereSell} (for several types)</li>
     *      <li>{@link Ticket#mValidDays} (for day limited tickets)</li>
     * </ul>
     */
    private void setTypeRelatedInfo() {
        switch (getTicketType()) {
            case TO_M1:
                mPassesTotal = 1;
                setTicketClass(C_OLD_METRO);
                break;
            case TO_M2:
                mPassesTotal = 2;
                setTicketClass(C_OLD_METRO);
                break;
            case TO_M3:
                mPassesTotal = 3;
                setTicketClass(C_OLD_METRO);
                break;
            case TO_M4:
                mPassesTotal = 4;
                setTicketClass(C_OLD_METRO);
                break;
            case TO_M5:
                mPassesTotal = 5;
                setTicketClass(C_OLD_METRO);
                break;
            case TO_M10:
                mPassesTotal = 10;
                setTicketClass(C_OLD_METRO);
                break;
            case TO_M20:
                mPassesTotal = 20;
                setTicketClass(C_OLD_METRO);
                break;
            case TO_M60:
                mPassesTotal = 60;
                setTicketClass(C_OLD_METRO);
                break;
            case TO_BAGGAGE_AND_PASS:
                mPassesTotal = 1;
                setTicketClass(C_OLD_SPECIAL);
                break;
            case TO_BAGGAGE:
                mPassesTotal = 1;
                setTicketClass(C_OLD_SPECIAL);
                break;
            case TO_UL70:
                mPassesTotal = -1;
                setTicketClass(C_OLD_SPECIAL);
                break;
            case TO_VESB:
                mPassesTotal = -1;
                setTicketClass(C_OLD_SPECIAL);
                break;
            
            case TN_G1:
                mPassesTotal = 1;
                setTicketClass(C_GROUND);
                mWhereSell = WS_GROUND;
                break;
            case TN_G2:
                mPassesTotal = 2;
                setTicketClass(C_GROUND);
                mWhereSell = WS_GROUND;
                break;
            case TN_G3_DRV:
                mPassesTotal = 3;
                setTicketClass(C_GROUND);
                mWhereSell = WS_DRIVER;
                break;
            case TN_G5:
                mPassesTotal = 5;
                setTicketClass(C_GROUND);
                mWhereSell = WS_GROUND;
                break;
            case TN_G11:
                mPassesTotal = 11;
                setTicketClass(C_GROUND);
                mWhereSell = WS_GROUND;
                break;
            case TN_G20:
                mPassesTotal = 20;
                setTicketClass(C_GROUND);
                mWhereSell = WS_GROUND;
                break;
            case TN_G40:
                mPassesTotal = 40;
                setTicketClass(C_GROUND);
                mWhereSell = WS_GROUND;
                break;
            case TN_G60:
                mPassesTotal = 60;
                setTicketClass(C_GROUND);
                mWhereSell = WS_GROUND;
                break;
            case TN_GB1_DRV:
                mPassesTotal = 1;
                setTicketClass(C_GROUND_B);
                mWhereSell = WS_DRIVER;
                break;
            case TN_GB2:
                mPassesTotal = 2;
                setTicketClass(C_GROUND_B);
                mWhereSell = WS_GROUND;
                break;
            case TN_GAB1:
                mPassesTotal = 1;
                setTicketClass(C_GROUND_AB);
                mWhereSell = WS_DRIVER;
                break;
            case TN_U1_DRV:
                mPassesTotal = 1;
                setTicketClass(C_UNIVERSAL);
                mWhereSell = WS_DRIVER;
                break;
            case TN_U1:
                setTicketClass(C_UNIVERSAL);
                mPassesTotal = 1;
                mWhereSell = WS_METRO;
                break;
            case TN_U2:
                mPassesTotal = 2;
                setTicketClass(C_UNIVERSAL);
                mWhereSell = WS_METRO;
                break;
            case TN_U5:
                mPassesTotal = 5;
                setTicketClass(C_UNIVERSAL);
                mWhereSell = WS_METRO;
                break;
            case TN_U11:
                mPassesTotal = 11;
                setTicketClass(C_UNIVERSAL);
                mWhereSell = WS_METRO;
                break;
            case TN_U20:
                mPassesTotal = 20;
                setTicketClass(C_UNIVERSAL);
// TODO: As code example. Need to check.     
                mWhereSell = WS_GROUND;
                mWhereSell |= WS_METRO;
                break;
            case TN_U40:
                mPassesTotal = 40;
                setTicketClass(C_UNIVERSAL);
                mWhereSell = WS_METRO;
                break;
            case TN_U60:
                mPassesTotal = 60;
                setTicketClass(C_UNIVERSAL);
                mWhereSell = WS_METRO;
                break;
            case TN_UL1D:
                mPassesTotal = -1;
                setTicketClass(C_UNLIM_DAYS);
//                if (getValidDays() == 0) setValidDays(1);
                setValidDays(1);
                mWhereSell = WS_METRO;
                break;
            case TN_UL3D:
                mPassesTotal = -1;
                setTicketClass(C_UNLIM_DAYS);
//                if ((getValidDays() == 0) || (getValidDays() == 10)) setValidDays(3);
                setValidDays(3);
                mWhereSell = WS_METRO;
                break;
            case TN_UL7D:
                mPassesTotal = -1;
                setTicketClass(C_UNLIM_DAYS);
//                if (getValidDays() == 0) setValidDays(7);
                setValidDays(7);
                mWhereSell = WS_METRO;
                break;
            case TN_90U1_G:
                mPassesTotal = 1;
                setTicketClass(C_90UNIVERSAL);
                mWhereSell = WS_GROUND;
                break;
            case TN_90U1:
                mPassesTotal = 1;
                setTicketClass(C_90UNIVERSAL);
                mWhereSell = WS_METRO;
                break;
            case TN_90U2_G:
                mPassesTotal = 2;
                setTicketClass(C_90UNIVERSAL);
                mWhereSell = WS_GROUND;
                break;
            case TN_90U2:
                mPassesTotal = 2;
                setTicketClass(C_90UNIVERSAL);
                mWhereSell = WS_METRO;
                break;
            case TN_90U5:
                mPassesTotal = 5;
                setTicketClass(C_90UNIVERSAL);
                mWhereSell = WS_METRO;
                break;
            case TN_90U11:
                mPassesTotal = 11;
                setTicketClass(C_90UNIVERSAL);
                mWhereSell = WS_METRO;
                break;
            case TN_90U20:
                mPassesTotal = 20;
                setTicketClass(C_90UNIVERSAL);
                mWhereSell = WS_METRO;
                break;
            case TN_90U40:
                mPassesTotal = 40;
                setTicketClass(C_90UNIVERSAL);
                mWhereSell = WS_METRO;
                break;
            case TN_90U60:
                mPassesTotal = 60;
                setTicketClass(C_90UNIVERSAL);
                mWhereSell = WS_METRO;
                break;
            default:
                mPassesTotal = 0;
                break;
        }
    }

/* Time related internal functions */

    /**
     * set {@link Ticket#mTimeToCompare}
     * @param timeToCompare
     */
    public void setTimeToCompare(Calendar timeToCompare) {
        this.mTimeToCompare = timeToCompare;
    }

    /**
     *
     * @return {@link Ticket#mTimeToCompare}
     */
    public Calendar getTimeToCompare() {
        return mTimeToCompare;
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
     * @param time minutes from midnight
     * @return Hours and minutes in readable form
     */
    private String getReadableTime(int time){
        return String.format("%02d:%02d",
                time / 60,
                time % 60);
    }

    /**
     * Get data file URI.
     * Use External storage stored datafile if exists.
     * As default use assets provided file (copy it).
     * @param c application context
     * @return data file URI (as String)
     */
    private String getDataFileURIasString(Context c) {
        String sdcardPath = c.getExternalFilesDir(null).getPath();
        URI dataFileURI = null;
        try {
            dataFileURI = new URI("file://" + sdcardPath + "/" + "metro.xml");
            File metroDataFile = new File(dataFileURI);
            if (!metroDataFile.exists()) {
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = c.getAssets().open("metro.xml");
                    File outFile = new File(dataFileURI);
                    out = new FileOutputStream(outFile);
                    byte[] buffer = new byte[1024];
                    int read;
                    while((read = in.read(buffer)) != -1){
                        out.write(buffer, 0, read);
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                }
                finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return dataFileURI.toString();
    }

    private static String getTCsuffix(Ticket ticket){
        int lastMTTidx = ticket.mMetroTripTransportHistory.size();
        String lmt = "";
        if (lastMTTidx > 1) {
            switch (ticket.mMetroTripTransportHistory.get(lastMTTidx - 1)) {
                case MT_METRO:
                    lmt += "m";
                    break;
                case MT_MONORAIL:
                    lmt += "r";
                    break;
                case MT_MCC:
                    lmt += "c";
                    break;
                default:
                    lmt += "x";
                    break;
            }
            return String.format("_%d%s", lastMTTidx - 1, lmt);
        } else {
            return "";
        }
    }

    /* WebView UI related functions */
    public String getTicketNumberAsHTML() {
        return String.format("%010d", getTicketNumber());
    }

    public String getTicketStateAsHTML() {
        switch (getTicketState()){
            case TS_UNKNOWN:
                return "<font color=\"Violet\">Unknown</font>";
            case TS_NEVER_USED:
                return "<font color=\"Lime\">Newer Used</font>";
            case TS_READY:
                return "<font color=\"Green\">Ready to use</font>";
            case TS_IN_TRIP:
                return "<font color=\"Blue\">In trip</font>";
            case TS_EMPTY:
                return "<font color=\"Red\">Empty</font>";
            case TS_EXPIRED:
                return "<font color=\"Red\">Expired</font>";
            default:
                return "<font color=\"Violet\">???</font>";
        }
    }

    public String getValidDaysAsHTML() { return String.format("%d", getValidDays()); }

    public String getStartUseBeforeASHTML() {
        return String.format("%s",
                DF.format(mStartUseBefore.getTime())
        );
    }

    public String getTicketTypeAsHTML() {
        return String.format("%1$d (0x%1$03x)", getTicketType());
    }

    public String getTicketAppIDAsHTML() {
        return String.format("%1$d (0x%1$03x)", getApp());
    }

    public String getTicketLayoutAsHTML() {
        return String.format("%1$d (0x%1$02x)", getLayout());
    }

    public String getTurnstileEnteredAsHTML() {
        return String.format("%1$d [0x%1$04x]", getTurnstileEntered());

    }

    public String getEntrancrEnteredAsHTML() {
        return String.format("%1$d [0x%1$04x]", getEntranceEntered());

    }

    public String getPassesLeftAsHTML() {
        return String.format("%d", getPassesLeft());
    }

    public String getTripSeqNumbetAsHTML() {
        return String.format("%d", getTripSeqNumber());
    }

    public String getTransportTypeAsHTML(Context c) {
        String TransportType ="";
        switch (getPassTransportType()) {
            case TT_METRO:
                switch (getPassMetroType()) {
                    case MT_MCC:
                        TransportType +=c.getString(R.string.mt_mcc);
                        break;
                    case MT_METRO:
                        TransportType +=c.getString(R.string.mt_metro);
                        break;
                    case MT_MONORAIL:
                        TransportType +=c.getString(R.string.mt_monorail);
                        break;
                    case MT_UNKNOWN:
                    default:
                        TransportType += c.getString(R.string.tt_unknown);
                        break;
                }
                if (mMetroTripTransportHistory.size() > 1) {
                    TransportType += ", hist.: ";
                    for (int tt : mMetroTripTransportHistory) {
                        switch (tt) {
                            case MT_METRO:
                                TransportType += "M";
                                break;
                            case MT_MONORAIL:
                                TransportType += "R";
                                break;
                            case MT_MCC:
                                TransportType += "C";
                                break;
                            default:
                            case MT_UNKNOWN:
                                TransportType += "U";
                                break;
                        }
                    }
                }
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

        return TransportType;
    }

    public String getStationDescAsHTML(Context c) {
        return Lang.transliterate(
                Lookup.findStationById(getEntranceEntered()+"", getDataFileURIasString(c))
        );
//        return
//            Lookup.findStationById(getEntranceEntered()+"", getDataFileURIasString(c));
    }

    public String getTurnstileDescAsHTML(Context c) {
        return Lang.transliterate(
                Lookup.findStationInfoByTsId(getTurnstileEntered()+"", getDataFileURIasString(c))
        );
//        return
//            Lookup.findStationInfoByTsId(getTurnstileEntered()+"", getDataFileURIasString(c));
    }

}
