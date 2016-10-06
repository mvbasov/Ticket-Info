package net.basov.metro;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Data sets for TicketTest
 * Created by mvb on 9/24/16.
 */
public class TicketTestData {

    public static ArrayList<Ticket> getDataSets() {
        ArrayList<Ticket> TDSArray = new ArrayList<Ticket>();
        TDSArray.add(TDS_0xd_U1());
        TDSArray.add(TDS_0xa_3D());
        TDSArray.add(TDS_0xa_U2());
        TDSArray.add(TDS_0xa_3D_unused());
        TDSArray.add(TDS_0xa_3D_trip_3());
        TDSArray.add(TDS_0xa_90M2());
        return TDSArray;
    }

    private static Calendar getCldr(int year, int month, int day, int hour, int minute) {
        Calendar rCal = Calendar.getInstance();
        rCal.clear();
        rCal.set(year, month, day, hour, minute, 0);
        return rCal;
    }

    /**
     * 0083381452 (till 28.05.2017)<br/>
     * Universal, 1 pass<br/>
     * Valid 5 days (from 16.09.2016 to 20.09.2016)<br/>
     * Trip N1 16.09.2016 at 00:32<br/>
     * Gate 137 (Ground)<br/>
     * Passes left 0<br/>
     *<br/>
     * Layout 13, AppId 279, Type 410<br/>
     *
     */
    public static Ticket TDS_0xd_U1() {

        Ticket TDS = new Ticket();

        TDS.setName("Layout 0xd, Universal 1 pass");
        TDS.setTicketNumber(83381452L);
        TDS.setTicketClass(Ticket.C_UNIVERSAL);
        TDS.setPassesTotal(1);
        TDS.setValidDays(5);
        TDS.setTripSeqNumber(1);
        TDS.setGateEntered(137);
        TDS.setPassTransportType(Ticket.TT_GROUND);
        TDS.setPassesLeft(0);
        TDS.setLayout(0xd);
        TDS.setApp(Ticket.A_UNIVERSAL);
        TDS.setTicketType(Ticket.TN_U1_DRV);
        TDS.setIssued(getCldr(2016, Calendar.SEPTEMBER, 16, 0, 0));
        TDS.setTripStart(getCldr(2016, Calendar.SEPTEMBER, 16, 0, 32));
        TDS.setStartUseBefore(getCldr(2017, Calendar.MAY, 28, 0, 0));

        ArrayList<Integer> content = new ArrayList<Integer>();
        content.add(0x343d6ced);
        content.add(0x9111ef86);
        content.add(0xe900f000);
        content.add(0xfffffffc);
        content.add(0x45d9a04f);
        content.add(0x84cccd00);
        content.add(0x24400000);
        content.add(0x24400000);
        content.add(0x23420500);
        content.add(0x80000089);
        content.add(0x5862c95b);
        content.add(0x23420400);
        content.add(0x23420500);
        content.add(0x80000089);
        content.add(0x5862c95b);
        content.add(0x23420400);
        TDS.setDump(content);

        return TDS;
    }

    /**
     * 0001983509<br/>
     * Unlimited, 3 days<br/>
     * Valid 3 days (from 09.09.2016 11:02 to 12.09.2016 11:02)<br/>
     * Trip N15 12.09.2016 at 11:01<br/>
     * Station entrance ID 2281 (Metro)<br/>
     *<br/>
     * Layout 10, AppId 279, Type 435<br/>
     *
     */
    public static Ticket TDS_0xa_3D() {

        Ticket TDS = new Ticket();

        TDS.setName("Layout 0xa, 3 days limited, trip 15");
        TDS.setTicketNumber(1983509L);
        TDS.setTicketClass(Ticket.C_UNLIM_DAYS);
        TDS.setPassesTotal(-1);
        TDS.setValidDays(3);
        TDS.setTripSeqNumber(15);
        TDS.setEntranceEntered(2281);
        TDS.setPassTransportType(Ticket.TT_METRO);
        TDS.setPassesLeft(-1);
        TDS.setLayout(10);
        TDS.setApp(Ticket.A_UNIVERSAL);
        TDS.setTicketType(Ticket.TN_UL3D);
        TDS.setIssued(getCldr(2016, Calendar.SEPTEMBER, 9, 0, 0));
        TDS.setFirstUseTime(11 * 60 + 3);
        TDS.setTripStart(getCldr(2016, Calendar.SEPTEMBER, 12, 11, 1));
        ArrayList<Integer> content = new ArrayList<Integer>();
        content.add(0x34e793c8);
        content.add(0x817ff576);
        content.add(0x7de07008);
        content.add(0x00000000);
        content.add(0x45db3001);
        content.add(0xe4415a00);
        content.add(0x0fd026ee);
        content.add(0x026ea001);
        content.add(0x0f08e940);
        content.add(0x00000000);
        content.add(0x5ef67e1b);
        content.add(0x0fd026ee);
        content.add(0x026ea001);
        content.add(0x0f08e940);
        content.add(0x00000000);
        content.add(0x5ef67e1b);
        TDS.setDump(content);

        return TDS;
    }

    /**
     * 2533158286<br/>
     * Universal, 2 pass<br/>
     * Valid 5 days (from 08.09.2016 to 12.09.2016)<br/>
     * Trip N2 08.09.2016 at 11:17<br/>
     * Gate 2281 (Metro)<br/>
     * Passes left 0<br/>
     *<br/>
     * Layout 10, AppId 279, Type 412<br/>
     *
     */
    public static Ticket TDS_0xa_U2(){

        Ticket TDS = new Ticket();

        TDS.setName("Layout 0xa, Universal 2 passes");
        TDS.setTicketNumber(2533158286L);
        TDS.setTicketClass(Ticket.C_UNIVERSAL);
        TDS.setPassesTotal(2);
        TDS.setValidDays(5);
        TDS.setTripSeqNumber(2);
        TDS.setEntranceEntered(2281);
        TDS.setPassTransportType(Ticket.TT_METRO);
        TDS.setPassesLeft(0);
        TDS.setLayout(10);
        TDS.setApp(Ticket.A_UNIVERSAL);
        TDS.setTicketType(Ticket.TN_U2);
        TDS.setIssued(getCldr(2016, Calendar.SEPTEMBER, 8, 0, 0));
        TDS.setTripStart(getCldr(2016, Calendar.SEPTEMBER, 8, 11, 17));

        ArrayList<Integer> content = new ArrayList<Integer>();
        content.add(0x34c06d11);
        content.add(0x4137f776);
        content.add(0xf7e07008);
        content.add(0xfffffffc);
        content.add(0x45d9c96f);
        content.add(0xced8ea00);
        content.add(0x0fc03840);
        content.add(0x0054a001);
        content.add(0x0008e940);
        content.add(0x00000000);
        content.add(0xb82de5ab);
        content.add(0x0fc03840);
        content.add(0x0054a001);
        content.add(0x0008e940);
        content.add(0x00000000);
        content.add(0xb82de5ab);
        TDS.setDump(content);

        return TDS;
    }

    /**
     * 0001964479<br/>
     * Unlimited, 3 days<br/>
     * Valid 3 days (from 26.09.2016 00:00 to 05.10.2016 23:59)<br/>
     * Never used<br/>
     *<br/>
     * Layout 10, AppId 279, Type 435<br/>
     *
     */
    public static Ticket TDS_0xa_3D_unused(){

        Ticket TDS = new Ticket();

        TDS.setName("Layout 0xa, 3 days limited, never used");
        TDS.setTicketNumber(1964479L);
        TDS.setTicketClass(Ticket.C_UNLIM_DAYS);
        TDS.setPassesTotal(-1);
        TDS.setValidDays(3);
        TDS.setPassesLeft(-1);
        TDS.setLayout(10);
        TDS.setApp(Ticket.A_UNIVERSAL);
        TDS.setTicketType(Ticket.TN_UL3D);
        // TODO: Check this ticket issue date
        TDS.setIssued(getCldr(2016, Calendar.SEPTEMBER, 26, 0, 0));

        ArrayList<Integer> content = new ArrayList<Integer>();
        content.add(0x34e780db); //P0
        content.add(0x592b1276); //P1
        content.add(0x16e03000); //P2
        content.add(0x00000000); //P3
        content.add(0x45db3001); //P4
        content.add(0xdf9bfa00); //P5
        content.add(0x10e07081); //P6
        content.add(0x00000000); //P7
        content.add(0x00000000); //P8
        content.add(0x00000000); //P9
        content.add(0x401541dd); //P10
        content.add(0x10e07081); //P11
        content.add(0x00000000); //P12
        content.add(0x00000000); //P13
        content.add(0x00000000); //P14
        content.add(0x401541dd); //P15
        TDS.setDump(content);

        return TDS;
    }

    /**
     * 0001964479<br/>
     * Unlimited, 3 days<br/>
     * Valid 3 days (from 26.09.2016 00:00 to 05.10.2016 23:59)<br/>
     * Trip N3 04.10.2016 at 23:25<br/>
     * Station entrance ID 1181 (Metro)<br/>
     *<br/>
     * Layout 10, AppId 279, Type 435<br/>
     *
     */
    public static Ticket TDS_0xa_3D_trip_3(){

        Ticket TDS = new Ticket();

        TDS.setName("Layout 0xa, 3 days limited, trip 3");
        TDS.setTicketNumber(1964479L);
        TDS.setTicketClass(Ticket.C_UNLIM_DAYS);
        TDS.setPassesTotal(-1);
        TDS.setValidDays(3);
        TDS.setTripSeqNumber(3);
        TDS.setPassTransportType(Ticket.TT_METRO);
        TDS.setEntranceEntered(1181);
        TDS.setPassesLeft(-1);
        TDS.setLayout(10);
        TDS.setApp(Ticket.A_UNIVERSAL);
        TDS.setTicketType(Ticket.TN_UL3D);
        // TODO: Check this ticket issue date
        TDS.setIssued(getCldr(2016, Calendar.SEPTEMBER, 26, 0, 0));
        TDS.setFirstUseTime(12 * 60 + 42);
        TDS.setTripStart(getCldr(2016, Calendar.OCTOBER, 4, 23, 25));

        ArrayList<Integer> content = new ArrayList<Integer>();
        content.add(0x34e780db); //P0
        content.add(0x592b1276); //P1
        content.add(0x16e07008); //P2
        content.add(0x00000000); //P3
        content.add(0x45db3001); //P4
        content.add(0xdf9bfa00); //P5
        content.add(0x10e081b4); //P6
        content.add(0x064fa001); //P7
        content.add(0x03049d40); //P8
        content.add(0x00000000); //P9
        content.add(0xb5087400); //P10
        content.add(0x10e081b4); //P11
        content.add(0x064fa001); //P12
        content.add(0x03049d40); //P13
        content.add(0x00000000); //P14
        content.add(0xb5087400); //P15
        TDS.setDump(content);

        return TDS;
    }

    /**
     * 1017786847<br/>
     * Universal, 30 minutes, 2 passes<br/>
     * Valid 5 days (from 02.10.2016 00:00 to 07.10.2016 23:59)<br/>
     * Trip N2 02.10.2016 at 14:05<br/>
     * Station entrance ID 991 (Metro)<br/>
     *<br/>
     * Layout 10, AppId 279, Type 438<br/>
     *
     */
    public static Ticket TDS_0xa_90M2(){

        Ticket TDS = new Ticket();

        TDS.setName("Layout 0xa, Universal 90 minutes, trip 2");
        TDS.setTicketNumber(1017786847L);
        TDS.setTicketClass(Ticket.C_90UNIVERSAL);
        TDS.setPassesTotal(2);
        TDS.setValidDays(5);
        TDS.setTripSeqNumber(2);
        TDS.setT90MCount(1);
        TDS.setPassTransportType(Ticket.TT_METRO);
        TDS.setEntranceEntered(991);
        TDS.setPassesLeft(0);
        TDS.setLayout(10);
        TDS.setApp(Ticket.A_UNIVERSAL);
        TDS.setTicketType(Ticket.TN_90U2_G);
        TDS.setIssued(getCldr(2016, Calendar.OCTOBER, 2, 0, 0));
        TDS.setTripStart(getCldr(2016, Calendar.OCTOBER, 2, 14, 5));
        TDS.setT90ChangeTime(getCldr(2016, Calendar.OCTOBER, 2, 14, 16));

        ArrayList<Integer> content = new ArrayList<Integer>();
        content.add(0x04084ace); //P0
        content.add(0x3a773f85); //P1
        content.add(0xf7487008); //P2
        content.add(0xfffffffc); //P3
        content.add(0x45db63ca); //P4
        content.add(0xa31dfa00); //P5
        content.add(0x11403840); //P6
        content.add(0x0069a02d); //P7
        content.add(0x0003df40); //P8
        content.add(0x00000000); //P9
        content.add(0xee8922e9); //P10
        content.add(0x11403840); //P11
        content.add(0x0069a02d); //P12
        content.add(0x0003df40); //P13
        content.add(0x00000000); //P14
        content.add(0xee8922e9); //P15
        TDS.setDump(content);

        return TDS;
    }
}
