package net.basov.metro;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Data sets for TicketTest
 * Created by mvb on 9/24/16.
 */
public class TicketTestData {

    public static ArrayList<TicketTestDataSet> getDataSets() {
        ArrayList<TicketTestDataSet> TDSArray = new ArrayList<TicketTestDataSet>();
        TDSArray.add(TDS_0xd_U1());
        TDSArray.add(TDS_0xa_3D());
        TDSArray.add(TDS_0xa_U2());
// TODO: Make test data set for 90 minutes, new layout
//        TDSArray.add(TDS_0xa_90M2());
        return TDSArray;
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
    public static TicketTestDataSet TDS_0xd_U1() {

        TicketTestDataSet TDS = new TicketTestDataSet();

        TDS.setName("Layout 0xd, Universal 1 pass");
        TDS.setExpectedTicketNumber(83381452L);
        TDS.setExpectedClass(Ticket.C_UNIVERSAL);
        TDS.setExpectedPassesTotal(1);
        TDS.setExpectedValidDays(5);
        TDS.setExpectedTripSeqNumber(1);
        TDS.setExpectedGateEntered(137);
        TDS.setExpectedTransportType(Ticket.TT_GROUND);
        TDS.setExpectedPassesLeft(0);
        TDS.setExpectedLayout(0xd);
        TDS.setExpectedApp(Ticket.A_UNIVERSAL);
        TDS.setExpectedType(Ticket.TN_U1_DRV);
        Calendar expIssued = Calendar.getInstance();
        expIssued.clear();
        expIssued.set(2016, Calendar.SEPTEMBER, 16, 0, 0);
        TDS.setExpectedIssued(expIssued);
        Calendar expTripStart = Calendar.getInstance();
        expTripStart.clear();
        expTripStart.set(2016, Calendar.SEPTEMBER, 16, 0, 32);
        TDS.setExpectedTripStart(expTripStart);
        Calendar expStartUseBefore = Calendar.getInstance();
        expStartUseBefore.clear();
        expStartUseBefore.set(2017, Calendar.MAY, 28, 0, 0);
        TDS.setExpectedStartUseBefore(expStartUseBefore);

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
        TDS.setDumpContent(content);

        return TDS;
    }

    /**
     * 0001983509<br/>
     * Unlimited, 3 days<br/>
     * Valid 3 days (from 09.09.2016 11:02 to 12.09.2016 11:02)<br/>
     * Trip N15 12.09.2016 at 11:01<br/>
     * Gate 2281 (Metro)<br/>
     *<br/>
     * Layout 10, AppId 279, Type 435<br/>
     *
     */
    public static TicketTestDataSet TDS_0xa_3D() {

        TicketTestDataSet TDS = new TicketTestDataSet();

        TDS.setName("Layout 0xa, Unlimited 3 days");
        TDS.setExpectedTicketNumber(1983509L);
        TDS.setExpectedClass(Ticket.C_UNLIM_DAYS);
        TDS.setExpectedPassesTotal(-1);
        TDS.setExpectedValidDays(3);
        TDS.setExpectedTripSeqNumber(15);
        TDS.setExpectedEntranceEntered(2281);
        TDS.setExpectedTransportType(Ticket.TT_METRO);
        TDS.setExpectedPassesLeft(-1);
        TDS.setExpectedLayout(10);
        TDS.setExpectedApp(Ticket.A_UNIVERSAL);
        TDS.setExpectedType(Ticket.TN_UL3D);
        Calendar expIssued = Calendar.getInstance();
        expIssued.clear();
        expIssued.set(2016, Calendar.SEPTEMBER, 9, 11, 2, 0);
        TDS.setExpectedIssued(expIssued);
        Calendar expTripStart = Calendar.getInstance();
        expTripStart.clear();
        expTripStart.set(2016, Calendar.SEPTEMBER, 12, 11, 1, 0);
        TDS.setExpectedTripStart(expTripStart);

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
        TDS.setDumpContent(content);

        return TDS;
    }

    /**
     * 2533158286<br/>
     * Universal, 2 pass<br/>
     * Valid 5 days (from 08.09.2016 to 12.09.2016)<br/>
     * Trip N2 16.09.2016 at 00:32<br/>
     * Gate 2281 (Metro)<br/>
     * Passes left 0<br/>
     *<br/>
     * Layout 10, AppId 279, Type 412<br/>
     *
     */
    public static TicketTestDataSet TDS_0xa_U2(){

        TicketTestDataSet TDS = new TicketTestDataSet();

        TDS.setName("Layout 0xa, Universal 2 passes");
        TDS.setExpectedTicketNumber(2533158286L);
        TDS.setExpectedClass(Ticket.C_UNIVERSAL);
        TDS.setExpectedPassesTotal(2);
        TDS.setExpectedValidDays(5);
        TDS.setExpectedTripSeqNumber(2);
        TDS.setExpectedEntranceEntered(2281);
        TDS.setExpectedTransportType(Ticket.TT_METRO);
        TDS.setExpectedPassesLeft(0);
        TDS.setExpectedLayout(10);
        TDS.setExpectedApp(Ticket.A_UNIVERSAL);
        TDS.setExpectedType(Ticket.TN_U2);
        Calendar expIssued = Calendar.getInstance();
        expIssued.clear();
        expIssued.set(2016, Calendar.SEPTEMBER, 8, 0, 0);
        TDS.setExpectedIssued(expIssued);
        Calendar expTripStart = Calendar.getInstance();
        expTripStart.clear();
        expTripStart.set(2016, Calendar.SEPTEMBER, 8, 11, 17);
        TDS.setExpectedTripStart(expTripStart);

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
        TDS.setDumpContent(content);

        return TDS;
    }
}
