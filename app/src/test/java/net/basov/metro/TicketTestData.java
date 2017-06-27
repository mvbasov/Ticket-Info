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
        TDSArray.add(TDS_0x8_VESB_0());
        TDSArray.add(TDS_0x8_VESB_9());
        TDSArray.add(TDS_0x8_M1_1());
        TDSArray.add(TDS_0x8_M2_1());
        TDSArray.add(TDS_0x8_M5_4());
        TDSArray.add(TDS_0x8_M10_8());
        TDSArray.add(TDS_0x8_M20_20());
        TDSArray.add(TDS_0x8_M60_59());
        TDSArray.add(TDS_0xd_7D_0());
        TDSArray.add(TDS_0xd_7D_1());
        TDSArray.add(TDS_0xd_U1_1());
        TDSArray.add(TDS_0xa_3D_0());
        TDSArray.add(TDS_0xa_3D_3());
        TDSArray.add(TDS_0xa_3D_15());
        TDSArray.add(TDS_0xa_U2_2());
        TDSArray.add(TDS_0xa_U60_54_2c());
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
     * 0006981689<br/>
     * VESB<br/>
     * Valid 30 days from 1-st pass<br/>
     * Real Issued 31.03.2016<br/>
     * Valid 30 days (from 31.03.2016 to 17.12.2011)<br/>
     *<br/>
     * Layout 8, AppId 279, Type 150<br/>
     *
     */
    public static Ticket TDS_0x8_VESB_0() {

        Ticket TDS = new Ticket();

        TDS.setName("Layout 0x8, VESB, unused");
        TDS.setTicketNumber(6981689L);
        TDS.setTicketClass(Ticket.C_OLD_SPECIAL);
        TDS.setPassesTotal(-1);
        TDS.setPassTransportType(Ticket.TT_METRO);
        TDS.setPassMetroType(Ticket.MT_METRO);
        TDS.setPassesLeft(-1);
        TDS.setLayout(0x8);
        TDS.setApp(Ticket.A_METRO);
        TDS.setTicketType(Ticket.TO_VESB);

        ArrayList<Integer> content = new ArrayList<Integer>();
        content.add(0x04d58ed7); //P0
        content.add(0xa2aa4180); //P1
        content.add(0xc948f088); //P2
        content.add(0x00000000); //P3
        content.add(0x41896006); //P4
        content.add(0xa8839823); //P5
        content.add(0x74000000); //P6
        content.add(0x00000000); //P7
        content.add(0x00000080); //P8
        content.add(0x00000000); //P9
        content.add(0x662eb555); //P10
        content.add(0x00000000); //P11
        content.add(0x00000080); //P12
        content.add(0x00000000); //P13
        content.add(0x662eb555); //P14
        TDS.setDump(content);

        return TDS;
    }

    /**
     * 0006981689<br/>
     * VESB<br/>
     * Valid 30 days from 1-st pass<br/>
     * Real Issued 31.03.2016<br/>
     * 1-st pass 01.05.2016 (represents as issued)<br/>
     * Trip N9
     *<br/>
     * Layout 8, AppId 279, Type 150<br/>
     *
     */
    public static Ticket TDS_0x8_VESB_9() {

        Ticket TDS = new Ticket();

        TDS.setName("Layout 0x8, VESB, pass 9");
        TDS.setTicketNumber(6981689L);
        TDS.setTicketClass(Ticket.C_OLD_SPECIAL);
        TDS.setPassesTotal(-1);
        TDS.setValidDays(30);
        TDS.setTripSeqNumber(9);
        TDS.setTurnstileEntered(10673);
        TDS.setPassTransportType(Ticket.TT_METRO);
        TDS.setPassMetroType(Ticket.MT_METRO);
        TDS.setPassesLeft(-1);
        TDS.setLayout(0x8);
        TDS.setApp(Ticket.A_METRO);
        TDS.setTicketType(Ticket.TO_VESB);
        TDS.setIssued(getCldr(2016, Calendar.MAY, 1, 0, 0));
        TDS.setStartUseBefore(getCldr(2016, Calendar.MAY, 31, 0, 0));

        ArrayList<Integer> content = new ArrayList<Integer>();
        content.add(0x04d58ed7); //P0
        content.add(0xa2aa4180); //P0
        content.add(0xc948f099); //P0
        content.add(0x00000000); //P0
        content.add(0x41896006); //P0
        content.add(0xa8839823); //P0
        content.add(0x74000000); //P0
        content.add(0x00000000); //P0
        content.add(0x22b81e00); //P0
        content.add(0x000929b1); //P0
        content.add(0xce9818eb); //P0
        content.add(0x00000000); //P0
        content.add(0x22b81e00); //P0
        content.add(0x000929b1); //P0
        content.add(0xce9818eb); //P0
        content.add(0x00000000); //P0
        TDS.setDump(content);

        return TDS;
    }

    /**
     * 1080692610<br/>
     * Old metro, 1 pass<br/>
     * Valid 65 days (from 13.10.2011 to 17.12.2011)<br/>
     * Trip N1<br/>
     * Gate 2011 (Metro)<br/>
     * Passes left 0<br/>
     *<br/>
     * Layout 8, AppId 279, Type 410<br/>
     *
     */
    public static Ticket TDS_0x8_M1_1() {

        Ticket TDS = new Ticket();

        TDS.setName("Layout 0x8, Metro 1 pass");
        TDS.setTicketNumber(1080692610L);
        TDS.setTicketClass(Ticket.C_OLD_METRO);
        TDS.setPassesTotal(1);
        TDS.setValidDays(65);
        TDS.setTripSeqNumber(1);
        TDS.setTurnstileEntered(2011);
        TDS.setPassTransportType(Ticket.TT_METRO);
        TDS.setPassMetroType(Ticket.MT_METRO);
        TDS.setPassesLeft(0);
        TDS.setLayout(0x8);
        TDS.setApp(Ticket.A_METRO);
        TDS.setTicketType(Ticket.TO_M1);
        TDS.setIssued(getCldr(2011, Calendar.OCTOBER, 13, 0, 0));
        TDS.setStartUseBefore(getCldr(2011, Calendar.DECEMBER, 17, 0, 0));

        ArrayList<Integer> content = new ArrayList<Integer>();
        content.add(0x0443a36c);
        content.add(0x12172782);
        content.add(0xa048f8ff);
        content.add(0xfffffffc);
        content.add(0x41878406);
        content.add(0xa0f8281c);
        content.add(0x7a000000);
        content.add(0x00000000);
        content.add(0x1c3a4100);
        content.add(0x000007db);
        content.add(0xd7f9ffa6);
        content.add(0x00000000);
        content.add(0x1c3a4100);
        content.add(0x000007db);
        content.add(0xd7f9ffa6);
        content.add(0x00000000);
        TDS.setDump(content);

        return TDS;
    }

    /**
     * 1146014286<br/>
     * Old metro, 2 passes, 1-st pass<br/>
     * Valid 5 days (from 08.01.2012 up to 13.01.2012)<br/>
     * Trip N1<br/>
     * Gate 2003 (Metro)<br/>
     * Passes left 1<br/>
     *<br/>
     * Layout 8, AppId 279, Type 121<br/>
     *
     */
    public static Ticket TDS_0x8_M2_1() {

        Ticket TDS = new Ticket();

        TDS.setName("Layout 0x8, Metro 2 passes, pass 1");
        TDS.setTicketNumber(1146014286L);
        TDS.setTicketClass(Ticket.C_OLD_METRO);
        TDS.setPassesTotal(2);
        TDS.setValidDays(5);
        TDS.setTripSeqNumber(1);
        TDS.setTurnstileEntered(2003);
        TDS.setPassTransportType(Ticket.TT_METRO);
        TDS.setPassMetroType(Ticket.MT_METRO);
        TDS.setPassesLeft(1);
        TDS.setLayout(0x8);
        TDS.setApp(Ticket.A_METRO);
        TDS.setTicketType(Ticket.TO_M2);
        TDS.setIssued(getCldr(2012, Calendar.JANUARY, 8, 0, 0));
        TDS.setStartUseBefore(getCldr(2012, Calendar.JANUARY, 13, 0, 0));

        ArrayList<Integer> content = new ArrayList<Integer>();
        content.add(0x04198015);
        content.add(0x6a462982);
        content.add(0x8748f099);
        content.add(0x0001fffc);
        content.add(0x41879444);
        content.add(0xeca4e81c);
        content.add(0x95000000);
        content.add(0x00000000);
        content.add(0x1c910500);
        content.add(0x000107d3);
        content.add(0x360ddc56);
        content.add(0x00000000);
        content.add(0x1c910500);
        content.add(0x000107d3);
        content.add(0x360ddc56);
        content.add(0x00000000);
        TDS.setDump(content);

        return TDS;
    }

    /**
     * 1062999288<br/>
     * Old metro, 5 passes, 4-th pass<br/>
     * Valid 45 days (from 26.09.2011 up to 10.11.2011)<br/>
     * Trip N4<br/>
     * Gate 1501 (Metro)<br/>
     * Passes left 1<br/>
     *<br/>
     * Layout 8, AppId 279, Type 126<br/>
     *
     */
    public static Ticket TDS_0x8_M5_4() {

        Ticket TDS = new Ticket();

        TDS.setName("Layout 0x8, Metro 5 passes, pass 4");
        TDS.setTicketNumber(1062999288L);
        TDS.setTicketClass(Ticket.C_OLD_METRO);
        TDS.setPassesTotal(5);
        TDS.setValidDays(45);
        TDS.setTripSeqNumber(4);
        TDS.setTurnstileEntered(1501);
        TDS.setPassTransportType(Ticket.TT_METRO);
        TDS.setPassMetroType(Ticket.MT_METRO);
        TDS.setPassesLeft(1);
        TDS.setLayout(0x8);
        TDS.setApp(Ticket.A_METRO);
        TDS.setTicketType(Ticket.TO_M5);
        TDS.setIssued(getCldr(2011, Calendar.SEPTEMBER, 26, 0, 0));
        TDS.setStartUseBefore(getCldr(2011, Calendar.NOVEMBER, 10, 0, 0));

        ArrayList<Integer> content = new ArrayList<Integer>();
        content.add(0x04ce97d5);
        content.add(0x62842882);
        content.add(0x4c48f099);
        content.add(0x03fffffc);
        content.add(0x4187e3f5);
        content.add(0xc14f881c);
        content.add(0x55000000);
        content.add(0x00000000);
        content.add(0x1c292d00);
        content.add(0x000105dd);
        content.add(0xd7b3a839);
        content.add(0x00000000);
        content.add(0x1c292d00);
        content.add(0x000105dd);
        content.add(0xd7b3a839);
        content.add(0x00000000);
        TDS.setDump(content);

        return TDS;
    }

    /**
     * 1022067563<br/>
     * Old metro, 10 passes, 8-th pass<br/>
     * Valid 45 days (from 06.08.2011 up to 20.09.2011)<br/>
     * Trip N8<br/>
     * Gate 609 (Metro)<br/>
     * Passes left 2<br/>
     *<br/>
     * Layout 8, AppId 279, Type 127<br/>
     *
     */
    public static Ticket TDS_0x8_M10_8() {

        Ticket TDS = new Ticket();

        TDS.setName("Layout 0x8, Metro 10 passes, pass 8");
        TDS.setTicketNumber(1022067563L);
        TDS.setTicketClass(Ticket.C_OLD_METRO);
        TDS.setPassesTotal(10);
        TDS.setValidDays(45);
        TDS.setTripSeqNumber(8);
        TDS.setTurnstileEntered(609);
        TDS.setPassTransportType(Ticket.TT_METRO);
        TDS.setPassMetroType(Ticket.MT_METRO);
        TDS.setPassesLeft(2);
        TDS.setLayout(0x8);
        TDS.setApp(Ticket.A_METRO);
        TDS.setTicketType(Ticket.TO_M10);
        TDS.setIssued(getCldr(2011, Calendar.AUGUST, 6, 0, 0));
        TDS.setStartUseBefore(getCldr(2011, Calendar.SEPTEMBER, 20, 0, 0));

        ArrayList<Integer> content = new ArrayList<Integer>();
        content.add(0x0496dac0);
        content.add(0xb2a62782);
        content.add(0xb148f099);
        content.add(0x03fffffc);
        content.add(0x4187f3ce);
        content.add(0xb836b81c);
        content.add(0x22000000);
        content.add(0x00000000);
        content.add(0x1bf62d00);
        content.add(0x00020261);
        content.add(0x051ab9ea);
        content.add(0x00000000);
        content.add(0x1bf62d00);
        content.add(0x00020261);
        content.add(0x051ab9ea);
        content.add(0x00000000);
        TDS.setDump(content);

        return TDS;
    }

    /**
     * 1424959134<br/>
     * Old metro, 20 passes, 20-th pass<br/>
     * Valid 45 days (from 22.08.2011 up to 06.10.2011)<br/>
     * Trip N20<br/>
     * Gate 2194 (Metro)<br/>
     * Passes left 0<br/>
     *<br/>
     * Layout 8, AppId 279, Type 128<br/>
     *
     */
    public static Ticket TDS_0x8_M20_20() {

        Ticket TDS = new Ticket();

        TDS.setName("Layout 0x8, Metro 20 passes, pass 20");
        TDS.setTicketNumber(1424959134L);
        TDS.setTicketClass(Ticket.C_OLD_METRO);
        TDS.setPassesTotal(20);
        TDS.setValidDays(45);
        TDS.setTripSeqNumber(20);
        TDS.setTurnstileEntered(2194);
        TDS.setPassTransportType(Ticket.TT_METRO);
        TDS.setPassMetroType(Ticket.MT_METRO);
        TDS.setPassesLeft(0);
        TDS.setLayout(0x8);
        TDS.setApp(Ticket.A_METRO);
        TDS.setTicketType(Ticket.TO_M20);
        TDS.setIssued(getCldr(2011, Calendar.AUGUST, 22, 0, 0));
        TDS.setStartUseBefore(getCldr(2011, Calendar.OCTOBER, 6, 0, 0));

        ArrayList<Integer> content = new ArrayList<Integer>();
        content.add(0x04619c71);
        content.add(0xb2fd2682);
        content.add(0xeb48f8ff);
        content.add(0xfffffffc);
        content.add(0x4188054e);
        content.add(0xf269e81c);
        content.add(0x32000000);
        content.add(0x00000000);
        content.add(0x1c062d00);
        content.add(0x00000892);
        content.add(0x74710f24);
        content.add(0x00000000);
        content.add(0x1c062d00);
        content.add(0x00000892);
        content.add(0x74710f24);
        content.add(0x00000000);
        TDS.setDump(content);

        return TDS;
    }

    /**
     * 0416338383<br/>
     * Old metro, 60 passes, 59-th pass<br/>
     * Valid 45 days (from 08.12.2011 up to 22.01.2012)<br/>
     * Trip N59<br/>
     * Gate 1596 (Metro)<br/>
     * Passes left 1<br/>
     *<br/>
     * Layout 8, AppId 279, Type 128<br/>
     *
     */
    public static Ticket TDS_0x8_M60_59() {

        Ticket TDS = new Ticket();

        TDS.setName("Layout 0x8, Metro 60 passes, pass 59");
        TDS.setTicketNumber(416338383L);
        TDS.setTicketClass(Ticket.C_OLD_METRO);
        TDS.setPassesTotal(60);
        TDS.setValidDays(45);
        TDS.setTripSeqNumber(59);
        TDS.setTurnstileEntered(1596);
        TDS.setPassTransportType(Ticket.TT_METRO);
        TDS.setPassMetroType(Ticket.MT_METRO);
        TDS.setPassesLeft(1);
        TDS.setLayout(0x8);
        TDS.setApp(Ticket.A_METRO);
        TDS.setTicketType(Ticket.TO_M60);
        TDS.setIssued(getCldr(2011, Calendar.DECEMBER, 8, 0, 0));
        TDS.setStartUseBefore(getCldr(2012, Calendar.JANUARY, 22, 0, 0));

        ArrayList<Integer> content = new ArrayList<Integer>();
        content.add(0x04188713);
        content.add(0x8a442882);
        content.add(0x6448f099);
        content.add(0x7ffffffc);
        content.add(0x4188118d);
        content.add(0x0d1cf81c);
        content.add(0x9e000000);
        content.add(0x00000000);
        content.add(0x1c722d00);
        content.add(0x0001063c);
        content.add(0x467a2922);
        content.add(0x00000000);
        content.add(0x1c722d00);
        content.add(0x0001063c);
        content.add(0x467a2922);
        content.add(0x00000000);
        TDS.setDump(content);

        return TDS;
    }

    /**
     * 0001192740<br/>
     * Unlimited, 3 days<br/>
     * Valid 7 days from 1-st pass.
     * FROM CHECK:<br/>
     * Sell 25.01.2016 13:20:50</br>
     * Start use till 03.02.2016<br/>
     * Never used<br/>
     *<br/>
     * Layout 10, AppId 279, Type 435<br/>
     *
     */
    public static Ticket TDS_0xd_7D_0(){

        Ticket TDS = new Ticket();

        TDS.setName("Layout 0xd, 7 days limited, never used");
        TDS.setTicketNumber(1192740L);
        TDS.setTicketClass(Ticket.C_UNLIM_DAYS);
        TDS.setPassesTotal(-1);
        TDS.setValidDays(7);
        TDS.setPassesLeft(-1);
        TDS.setLayout(13);
        TDS.setApp(Ticket.A_UNIVERSAL);
        TDS.setTicketType(Ticket.TN_UL7D);
        TDS.setStartUseBefore(getCldr(2016, Calendar.FEBRUARY, 3, 0, 0)); // TODO: Nafig?
        TDS.setStartUseTill(getCldr(2016, Calendar.FEBRUARY, 3, 23, 59));

        ArrayList<Integer> content = new ArrayList<Integer>();
        content.add(0x3493a689); //P0
        content.add(0x717dfb66); //P1
        content.add(0x91e03000); //P2
        content.add(0x00000000); //P3
        content.add(0x45db4001); //P4
        content.add(0x23324d00); //P5
        content.add(0x22600000); //P6
        content.add(0x22600000); //P7
        content.add(0x00000080); //P8
        content.add(0x00000000); //P9
        content.add(0x1992f11f); //P10
        content.add(0x00000000); //P11
        content.add(0x00000080); //P12
        content.add(0x00000000); //P13
        content.add(0x1992f11f); //P14
        content.add(0x00000000); //P15
        TDS.setDump(content);

        return TDS;
    }

    /**
     * 0001192740<br/>
     * Unlimited, 3 days<br/>
     * Valid 7 days from 1-st pass.
     * FROM CHECK:<br/>
     * Sell 25.01.2016 13:20:50</br>
     * Start use till 03.02.2016<br/>
     * Trip N1 <br/>
     *<br/>
     * Layout 10, AppId 279, Type 435<br/>
     *
     */
    public static Ticket TDS_0xd_7D_1(){

        Ticket TDS = new Ticket();

        TDS.setName("Layout 0xd, 7 days limited, pass 1");
        TDS.setTicketNumber(1192740L);
        TDS.setTicketClass(Ticket.C_UNLIM_DAYS);
        TDS.setPassesTotal(-1);
        TDS.setValidDays(7);
        TDS.setPassesLeft(-1);
        TDS.setTripSeqNumber(1);
        TDS.setLayout(13);
        TDS.setApp(Ticket.A_UNIVERSAL);
        TDS.setTicketType(Ticket.TN_UL7D);
        TDS.setPassTransportType(Ticket.TT_METRO);
        TDS.setPassMetroType(Ticket.MT_METRO);
        TDS.setIssued(getCldr(2016, Calendar.JANUARY, 25, 0, 0));
        TDS.setFirstUseTime(13 * 60 + 21);
        TDS.setTurnstileEntered(13486);
        TDS.setStartUseBefore(getCldr(2016, Calendar.FEBRUARY, 3, 0, 0)); // TODO: Nafig?
        TDS.setTripStart(getCldr(2016, Calendar.JANUARY, 25, 13, 21));

        ArrayList<Integer> content = new ArrayList<Integer>();
        content.add(0x493a689);
        content.add(0x717dfb66);
        content.add(0x91e0f000);
        content.add(0x00000000);
        content.add(0x45db4001);
        content.add(0x23324d00);
        content.add(0x22606420);
        content.add(0x22606420);
        content.add(0x22570700);
        content.add(0x400134ae);
        content.add(0x358a8246);
        content.add(0x22576428);
        content.add(0x22570700);
        content.add(0x400134ae);
        content.add(0x358a8246);
        content.add(0x22576428);
        TDS.setDump(content);

        return TDS;
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
    public static Ticket TDS_0xd_U1_1() {

        Ticket TDS = new Ticket();

        TDS.setName("Layout 0xd, Universal 1 pass, pass 1 (ground)");
        TDS.setTicketNumber(83381452L);
        TDS.setTicketClass(Ticket.C_UNIVERSAL);
        TDS.setPassesTotal(1);
        TDS.setValidDays(5);
        TDS.setTripSeqNumber(1);
        TDS.setTurnstileEntered(137);
        TDS.setPassTransportType(Ticket.TT_GROUND);
        TDS.setPassMetroType(Ticket.MT_UNKNOWN);
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
     * 0001964479<br/>
     * Unlimited, 3 days<br/>
     * Valid 3 days<br/>
     * Start use till 05.10.2016
     * Never used<br/>
     *<br/>
     * Layout 10, AppId 279, Type 435<br/>
     *
     */
    public static Ticket TDS_0xa_3D_0(){

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
        TDS.setIssued(getCldr(2016, Calendar.SEPTEMBER, 26, 0, 0));
        TDS.setStartUseTill(getCldr(2016, Calendar.OCTOBER, 5, 23, 59));

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
     * Valid 3 days (from 04.10.2016 12:42 to 07.10.2016 12:42)<br/>
     * Trip N3 04.10.2016 at 23:25<br/>
     * Station entrance ID 1181 (Metro)<br/>
     *<br/>
     * Layout 10, AppId 279, Type 435<br/>
     *
     */
    public static Ticket TDS_0xa_3D_3(){

        Ticket TDS = new Ticket();

        TDS.setName("Layout 0xa, 3 days limited, trip 3; Check time to next trip");
        TDS.setTicketNumber(1964479L);
        TDS.setTicketClass(Ticket.C_UNLIM_DAYS);
        TDS.setPassesTotal(-1);
        TDS.setValidDays(3);
        TDS.setTripSeqNumber(3);
        TDS.setPassTransportType(Ticket.TT_METRO);
        TDS.setPassMetroType(Ticket.MT_METRO);
        TDS.setEntranceEntered(1181);
        TDS.setPassesLeft(-1);
        TDS.setLayout(10);
        TDS.setApp(Ticket.A_UNIVERSAL);
        TDS.setTicketType(Ticket.TN_UL3D);
        TDS.setIssued(getCldr(2016, Calendar.SEPTEMBER, 26, 0, 0));
        TDS.setFirstUseTime(12 * 60 + 42);
        TDS.setTripStart(getCldr(2016, Calendar.OCTOBER, 4, 23, 25));
        // Time related part of test
        TDS.setTimeToCompare(getCldr(2016, Calendar.OCTOBER, 4, 23, 27));
        TDS.setTimeToNextTrip(19);

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
     * 0001983509<br/>
     * Unlimited, 3 days<br/>
     * Valid 3 days (from 09.09.2016 11:02 to 12.09.2016 11:02)<br/>
     * Trip N15 12.09.2016 at 11:01<br/>
     * Station entrance ID 2281 (Metro)<br/>
     *<br/>
     * Layout 10, AppId 279, Type 435<br/>
     *
     */
    public static Ticket TDS_0xa_3D_15() {

        Ticket TDS = new Ticket();

        TDS.setName("Layout 0xa, 3 days limited, trip 15");
        TDS.setTicketNumber(1983509L);
        TDS.setTicketClass(Ticket.C_UNLIM_DAYS);
        TDS.setPassesTotal(-1);
        TDS.setValidDays(3);
        TDS.setTripSeqNumber(15);
        TDS.setEntranceEntered(2281);
        TDS.setPassTransportType(Ticket.TT_METRO);
        TDS.setPassMetroType(Ticket.MT_METRO);
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
    public static Ticket TDS_0xa_U2_2(){

        Ticket TDS = new Ticket();

        TDS.setName("Layout 0xa, Universal 2 passes, pass 2");
        TDS.setTicketNumber(2533158286L);
        TDS.setTicketClass(Ticket.C_UNIVERSAL);
        TDS.setPassesTotal(2);
        TDS.setValidDays(5);
        TDS.setTripSeqNumber(2);
        TDS.setEntranceEntered(2281);
        TDS.setPassTransportType(Ticket.TT_METRO);
        TDS.setPassMetroType(Ticket.MT_METRO);
        TDS.setPassesLeft(0);
        TDS.setLayout(10);
        TDS.setApp(Ticket.A_UNIVERSAL);
        TDS.setTicketType(Ticket.TN_U2);
        TDS.setIssued(getCldr(2016, Calendar.SEPTEMBER, 8, 0, 0));
        TDS.setTripStart(getCldr(2016, Calendar.SEPTEMBER, 8, 11, 17));

        ArrayList<Integer> content = new ArrayList<Integer>();
        content.add(0x34c06d11); //P0
        content.add(0x4137f776); //P1
        content.add(0xf7e07008); //P2
        content.add(0xfffffffc); //P3
        content.add(0x45d9c96f); //P4
        content.add(0xced8ea00); //P5
        content.add(0x0fc03840); //P6
        content.add(0x0054a001); //P7
        content.add(0x0008e940); //P8
        content.add(0x00000000); //P9
        content.add(0xb82de5ab); //P10
        content.add(0x0fc03840); //P11
        content.add(0x0054a001); //P12
        content.add(0x0008e940); //P13
        content.add(0x00000000); //P14
        content.add(0xb82de5ab); //P15
        TDS.setDump(content);

        return TDS;
    }

    /**
     * 2583042010<br/>
     * Universal, 60 passes, pass 54 (MCC)<br/>
     * Valid 90 days (from 17.09.2016 00:00 to 07.10.2016 23:59)<br/>
     * Trip N54 09.10.2016 at 20:38<br/>
     * Station entrance ID 5401 (MСС)<br/>
     *<br/>
     * Layout 10, AppId 279, Type 438<br/>
     *
     */
    public static Ticket TDS_0xa_U60_54_2c(){

        Ticket TDS = new Ticket();

        TDS.setName("Layout 0xa, Universal 60 passes, trip 54, tc 1 mcc");
        TDS.setTicketNumber(2583042010L);
        TDS.setTicketClass(Ticket.C_UNIVERSAL);
        TDS.setPassesTotal(60);
        TDS.setValidDays(90);
        TDS.setTripSeqNumber(54);
        TDS.setPassTransportType(Ticket.TT_METRO);
        TDS.setPassMetroType(Ticket.MT_MCC);
        TDS.addMetroTripTransportHistory(Ticket.MT_METRO);
        TDS.addMetroTripTransportHistory(Ticket.MT_MCC);
        TDS.setEntranceEntered(5401);
        TDS.setPassesLeft(6);
        TDS.setLayout(10);
        TDS.setApp(Ticket.A_UNIVERSAL);
        TDS.setTicketType(Ticket.TN_U60);
        TDS.setIssued(getCldr(2016, Calendar.SEPTEMBER, 17, 0, 0));
        TDS.setTripStart(getCldr(2016, Calendar.OCTOBER, 9, 20, 38));

        ArrayList<Integer> content = new ArrayList<Integer>();
        content.add(0x34c4532b); //P0
        content.add(0x8106e077); //P1
        content.add(0x10007008); //P2
        content.add(0x1ffffffc); //P3
        content.add(0x45da299f); //P4
        content.add(0x617daa00); //P5
        content.add(0x1053f480); //P6
        content.add(0x1012c001); //P7
        content.add(0x06151970); //P8
        content.add(0x00000000); //P9
        content.add(0xce5f14d2); //P10
        content.add(0x1053f480); //P11
        content.add(0x1012c001); //P12
        content.add(0x06151970); //P13
        content.add(0x00000000); //P14
        content.add(0xce5f14d2); //P15
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
        TDS.setPassMetroType(Ticket.MT_METRO);
        TDS.setEntranceEntered(991);
        TDS.setPassesLeft(0);
        TDS.setLayout(10);
        TDS.setApp(Ticket.A_UNIVERSAL);
        TDS.setTicketType(Ticket.TN_90U2_G);
        TDS.setIssued(getCldr(2016, Calendar.OCTOBER, 2, 0, 0));
        TDS.setTripStart(getCldr(2016, Calendar.OCTOBER, 2, 14, 5));
        TDS.setT90ChangeTime(getCldr(2016, Calendar.OCTOBER, 2, 14, 16));
        TDS.setT90RelChangeTime(11);

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
