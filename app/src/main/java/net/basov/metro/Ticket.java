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
    private static final boolean DEBUG_TIME = false;

    public static final DateFormat DDF = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    // Constants definition

    /* Used transport types */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TT_UNKNOWN, TT_GROUND, TT_METRO})
    public @interface PassTransportType {}
    public static final int TT_UNKNOWN = 0;
    public static final int TT_METRO = 1;
    public static final int TT_GROUND = 2;

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
            TO_M1, TO_M2, TO_M3, TO_M4, TO_M5, TO_M10, TO_M20, TO_M20, TO_M60,
            TO_BAGGAGE_AND_PASS, TO_BAGGAGE, TO_UL70,
            TO_VESB,
            TN_U1, TN_U1_DRV, TN_U2, TN_U5, TN_U11, TN_90U20, TN_U40, TN_U60,
            TN_90U1, TN_90U1_G, TN_90U2, TN_90U2_G, TN_90U5, TN_90U11, TN_90U20, TN_90U60,
            TN_UL1D, TN_UL3D, TN_UL7D,
            TN_G1, TN_GB1_DRV, TN_G2, TN_G3_DRV, TN_G5, TN_G11, TN_G20, TN_G40, TN_G60,
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
    private ArrayList<Integer> mDump;

    /**
     * Name of the ticket.
     * Usefull for test datatsets.
     */
    private String mName;
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
    private Calendar mStartUse = null;

    /**
     * Ticket issue date
     */
    private Calendar mIssued = null;

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
    private int mGateEntered = 0;

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
     * One time programming bit counter
     * Used for control number of passes.
     */
    private int mOTP = 0;
    /**
     * mHash (encrypted checksum) of variable ticket block.
     */
    private int mHash = 0;

    private final static DateFormat DF = new SimpleDateFormat("dd.MM.yyyy");
    private final static DateFormat TF = new SimpleDateFormat("HH:mm");
    private final static DateFormat DTF = new SimpleDateFormat("dd.MM.yyyy HH:mm");

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

    public static String createDumpFileName(Ticket ticket) {

        StringBuilder dName = new StringBuilder();

        dName.append(String.format("%010d", ticket.getTicketNumber()));
        if (ticket.isTicketFormatValid()) {
            if (ticket.getTicketClass() == Ticket.C_UNLIM_DAYS ){
                dName.append(String.format("-%dd",ticket.getValidDays()));
                dName.append(String.format("-%03d",ticket.getTripSeqNumber()));
            } else if (ticket.getTicketType() == Ticket.TO_VESB) {
                    dName.append("-su");
                    dName.append(String.format("-%04d", ticket.getTripSeqNumber()));
            } else {
                dName.append(String.format("-%02d", ticket.getPassesTotal()));
                dName.append(String.format("-%02d", ticket.getTripSeqNumber()));
                if (ticket.getTicketClass() == Ticket.C_90UNIVERSAL) {
                    if (ticket.getLayout() == 0x0d) {
                        dName.append(String.format(".%02d", ticket.getRelTransportChangeTimeMinutes()));
                        dName.append(String.format(".%1d", ticket.getT90ChangeCount()));
                    } else if (ticket.getLayout() == 0x0a) {
                        dName.append(String.format(".%02d", ticket.getRelTransportChangeTimeMinutes()));
                    }
                }
            }

        } else {
            dName.append("-xx-xx");
        }

        return dName.toString();
    }

    /**
     * Process mDump field and generate other fields content
     */
    private void processTicket() {

        mOTP = mDump.get(3);

        setTicketNumber((((mDump.get(4) & 0xfff) << 20) | (mDump.get(5) >>> 12)) & 0xffffffffL);

        setLayout(((mDump.get(5) >>> 8) & 0xf));

        mApp = mDump.get(4) >>> 22;

        //noinspection WrongConstant
        setTicketType((mDump.get(4) >>> 12) & 0x3ff);

        setTypeRelatedInfo();

        /**
         * Temporary variable to extract values from dump pages
         */
        int tmp;

        switch (getLayout()) {
            case 0x08:
            case 0x0d:
                setValidDays((mDump.get(8) >>> 8) & 0xff);
                mPassesLeft = (mDump.get(9) >>> 16) & 0xff;
                tmp = (mDump.get(8) >>> 16) & 0xffff;
                if ( tmp != 0) {
                    mIssued = Calendar.getInstance();
                    mIssued.clear();
                    mIssued.set(1991, Calendar.DECEMBER, 31);
                    mIssued.add(Calendar.DATE, tmp);
                }
                tmp = (mDump.get(6) >>> 16) & 0xffff;
                if (tmp != 0) {
                    mStartUseBefore = Calendar.getInstance();
                    mStartUseBefore.clear();
                    mStartUseBefore.set(1991, Calendar.DECEMBER, 31);
                    mStartUseBefore.add(Calendar.DATE, tmp);
                }
                tmp = (mDump.get(6) & 0xfff0) >>> 5;
                if (tmp != 0 ) {
                    mStartUse = Calendar.getInstance();
                    mStartUse.clear();
                    mStartUse.set(1991, Calendar.DECEMBER, 31);
                    mStartUse.add(Calendar.MINUTE, tmp);
                }

                mGateEntered = mDump.get(9) & 0xffff;
                tmp = (mDump.get(11) >>> 16) & 0xffff;
                if (tmp != 0) {
                    mTripStart = Calendar.getInstance();
                    mTripStart.clear();
                    mTripStart.set(1991, Calendar.DECEMBER, 31);
                    mTripStart.add(Calendar.DAY_OF_MONTH, tmp);
                    mTripStart.add(Calendar.MINUTE, (mDump.get(11) & 0xfff0) >>> 5);
                    //noinspection WrongConstant
                    setPassTransportType((mDump.get(9) & 0xc0000000) >>> 30);
                }
                if (getTicketClass() == C_90UNIVERSAL) {
                    if ((mDump.get(8) & 0xff) != 0 && (mDump.get(8) & 0xff) != 0x80) {
                        mT90RelChangeTime = (mDump.get(8) & 0xff) * 5;
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
                setValidDays(((mDump.get(6) >>> 1) & 0x7ffff) / (24 * 60));
                mPassesLeft = (mDump.get(8) >>> 24) & 0xff;
                mIssued = Calendar.getInstance();
                mIssued.clear();
                mIssued.set(2015, Calendar.DECEMBER, 31);
                mIssued.add(Calendar.DAY_OF_MONTH, (mDump.get(6) >>> 20) & 0xfff);
                mIssued.add(Calendar.MINUTE,((mDump.get(6) >>> 1) & 0x7ffff) % (24 * 60));
                mEntranceEntered = (mDump.get(8) >>> 8) & 0xffff;

                tmp = (mDump.get(7) >>> 13) & 0x7ffff;
                if (tmp != 0) {
                    // Base date (mIssued) used with zero time. Set it.
                    mTripStart = getBaseDate((Calendar) mIssued.clone());
                    mTripStart.add(Calendar.MINUTE, tmp);
                    //noinspection WrongConstant
                    setPassTransportType(mDump.get(7) & 0x3);
                }
                if (getTicketClass() == C_90UNIVERSAL) {
                    mT90MCount = (mDump.get(8) >>> 6) & 0x01;
                    mT90RelChangeTime = (mDump.get(7) >>> 2) & 0x3ff;
                    mT90ChangeTime = (Calendar) mTripStart.clone();
                    mT90ChangeTime.add(Calendar.MINUTE, mT90RelChangeTime);

                    if (getTimeToCompare().after(mTripStart)){
                        mT90TripTimeLeft = (int)( 90 -
							((getTimeToCompare().getTimeInMillis()
                                        - mTripStart.getTimeInMillis())
                                        /(1000L * 60)));
                    }
                    if (mT90TripTimeLeft < 0) mT90TripTimeLeft = 0;
                } else if (getTicketClass() == C_UNLIM_DAYS) {
                    mStartUse = (Calendar) mIssued.clone();
                    mStartUse.add(Calendar.MINUTE, getValidDays() * 24 * 60);
                    setTypeRelatedInfo();

// TODO: Check. Is it right place to to make day limited tickets time correct.
// TODO: May be better way to do this at display time
                    mIssued.add(Calendar.MINUTE, -1);
                }

                break;
        }

        if (getTicketClass() == C_UNLIM_DAYS){
            setTripSeqNumber(getPassesLeft());
            mPassesLeft = -1;
            if(getTripSeqNumber() == 0) setTypeRelatedInfo();
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
        } else {
            setTripSeqNumber(mPassesTotal - getPassesLeft());
        }

		if (getTicketType() == TO_VESB) {
			setTripSeqNumber((mDump.get(9) >>> 16) & 0xfff);
			mPassesLeft = -1;
		}

        mHash = mDump.get(10);

    }

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

        if (DEBUG_TIME)
            sb.append(String.format("! ! ! Application time set to %s\n\n",
									DDF.format(getTimeToCompare().getTime())));

        if (!mTicketFormatValid) {
// TODO: Translate message
            sb.append("! ! ! mDump not valid or ticket type unknown\n\n");
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
            tmpCal = (Calendar) mIssued.clone();
            tmpCal.add(Calendar.HOUR, 24 * getValidDays());

            if (DEBUG_TIME)
                Log.d(TAG, String.format("Compare: %s\n", DDF.format(tmpCal.getTime())));

            if (mIssued == null ) {
                if (mStartUseBefore.after(tmpCal)) {
                    sb.append("\n\tE X P I R E D\n");
                }
            } else {
                if (tmpCal.compareTo(getTimeToCompare()) < 0) {
                    sb.append("\n\tE X P I R E D\n");
                } else if (mTimeToNextTrip > 0) {
                    sb.append("\n\tW A I T\n");
                }
            }
            if (tmpCal.after(getTimeToCompare())
                    && getTripSeqNumber() == 0)
                sb.append("\n\tN E V E R  U S E D");
            if (mStartUse != null) {
                sb.append(String.format("\n\tStart use up to: %s",
                        DTF.format(mStartUse.getTime())));
            }

        }

        sb.append("\n- - - -\n");

        if (getPassesLeft() != -1){
            sb.append(c.getString(R.string.passes_left)).append(": ");
            sb.append(getPassesLeft()).append("\n\n");
        }
        
        switch (getLayout()) {
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
                sb.append("Layout 8 (0x8).").append('\n');
                break;
            case 13:
                if (getGateEntered() != 0) {

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
                    sb.append(getGateDesc(c, getGateEntered()));
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

        sb.append(String.format("App ID: %$1d (0x%$103x), ", mApp));
        sb.append(String.format("Ticket type: %$1d (0x%$103x)\n", getTicketType()));

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
            case TO_M1:
            case TN_U1_DRV:
            case TN_UL3D:
            case TN_U2:
// TODO: Need to add all other types

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


    public void setTripSeqNumber(int tripSeqNumber) {
        if (tripSeqNumber < 0 || tripSeqNumber > 255)
            addParserError("Wrong trip seq number");
        mTripSeqNumber = tripSeqNumber;
    }

    public int getTripSeqNumber() { return mTripSeqNumber; }

    public void setGateEntered(int gateEntered) {
        mGateEntered = gateEntered;
    }

    /**
     * This trip current entered gate
     * @return {@link Ticket#mGateEntered} gate id
     */
    public int getGateEntered() {
        return mGateEntered;
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
                addParserError(" Wrong ticket class,");
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

    /**
     *
     * @return Last change time in minutes related to trip start
     */
    public int getRelTransportChangeTimeMinutes() { return mT90RelChangeTime; }

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
        this.mValidDays = validDays;
    }

    /**
     *
     * @return How many days ticket valid from issue date
     */
    public int getValidDays() { return mValidDays; }

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
    private String getHashAsHexString() {
        return Integer.toHexString(mHash);
    }

    /**
     *
     * @param c context
     * @param id gate id
     * @return Gate description as string
     */
    private String getGateDesc(Context c, int id) {
        String trType ="";
        switch (getPassTransportType()) {
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
        String SN = Lang.transliterate(Turnstiles.getStationByTurnstile(id));
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
        switch (getPassTransportType()) {
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
        
        String StationName = Lang.transliterate(Stations.getStationByStationId(id));

        if (StationName.length() != 0) {
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
                if (getValidDays() == 0) setValidDays(1);
                mWhereSell = WS_METRO;
                break;
            case TN_UL3D:
                mPassesTotal = -1;
                setTicketClass(C_UNLIM_DAYS);
                if (getValidDays() == 0) setValidDays(3);
                mWhereSell = WS_METRO;
                break;
            case TN_UL7D:
                mPassesTotal = -1;
                setTicketClass(C_UNLIM_DAYS);
                if (getValidDays() == 0) setValidDays(7);
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
     * @param mTimeToCompare
     */
    public void setTimeToCompare(Calendar mTimeToCompare) {
        this.mTimeToCompare = mTimeToCompare;
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

}
