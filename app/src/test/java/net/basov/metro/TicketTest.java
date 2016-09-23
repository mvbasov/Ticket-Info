package net.basov.metro;

import java.util.ArrayList;
import java.util.Calendar;

import net.basov.nfc.NFCaDump;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by mvb on 9/23/16.
 */
public class TicketTest extends TestCase {

    NFCaDump dump = new NFCaDump();
    Ticket ticket;

    int expectedValidDays;
    long expectedTicketNumber;
    int expectedType;
    int expectedTripSeqNumber;
    int expectedGateEntered;
    int expectedTransportType;
    int expectedEntranceEntered;
    int expectedClass;
    int expectedLayout;
    int expectedPassesTotal;
    int expectedPassesLeft;
    int expectedApp;
    Calendar expectedStartUseBefore;
    Calendar expectedIssued;
    Calendar expectedTripStart;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        //TDS_0xd_U1();
        //TDS_0xa_3D();
        TDS_0xa_U2();

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
    private void TDS_0xd_U1(){

        expectedTicketNumber = 83381452L;
        expectedClass = Ticket.C_UNIVERSAL;
        expectedPassesTotal = 1;
        expectedValidDays = 5;
        expectedTripSeqNumber = 1;
        expectedGateEntered = 137;
        expectedTransportType = Ticket.TT_GROUND;
        expectedPassesLeft = 0;
        expectedLayout = 13;
        expectedApp = 279;
        expectedType = 410;
        expectedIssued = Calendar.getInstance();
        expectedIssued.clear();
        expectedIssued.set(2016, Calendar.SEPTEMBER, 16, 00, 00);
        expectedTripStart = Calendar.getInstance();
        expectedTripStart.clear();
        expectedTripStart.set(2016, Calendar.SEPTEMBER, 16, 00, 32);
        expectedStartUseBefore = Calendar.getInstance();
        expectedStartUseBefore.clear();
        expectedStartUseBefore.set(2017, Calendar.MAY, 28, 00, 00);

        ArrayList<String> content = new ArrayList<String>();
        content.add("343d6ced");
        content.add("9111ef86");
        content.add("e900f000");
        content.add("fffffffc");
        content.add("45d9a04f");
        content.add("84cccd00");
        content.add("24400000");
        content.add("24400000");
        content.add("23420500");
        content.add("80000089");
        content.add("5862c95b");
        content.add("23420400");
        content.add("23420500");
        content.add("80000089");
        content.add("5862c95b");
        content.add("23420400");
        NFCaDump.parseDump(dump, content);
        ticket = new Ticket(dump);

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
    private void TDS_0xa_3D() {

        expectedTicketNumber = 1983509L;
        expectedClass = Ticket.C_UNLIM_DAYS;
        expectedPassesTotal = -1;
        expectedValidDays = 3;
        expectedTripSeqNumber = 15;
        expectedEntranceEntered = 2281;
        expectedTransportType = Ticket.TT_METRO;
        expectedPassesLeft = -1;
        expectedLayout = 10;
        expectedApp = 279;
        expectedType = 435;
        expectedIssued = Calendar.getInstance();
        expectedIssued.clear();
        expectedIssued.set(2016, Calendar.SEPTEMBER, 9, 11, 02, 00);
        expectedTripStart = Calendar.getInstance();
        expectedTripStart.clear();
        expectedTripStart.set(2016, Calendar.SEPTEMBER, 12, 11, 01, 00);

        ArrayList<String> content = new ArrayList<String>();
        content.add("34e793c8");
        content.add("817ff576");
        content.add("7de07008");
        content.add("00000000");
        content.add("45db3001");
        content.add("e4415a00");
        content.add("0fd026ee");
        content.add("026ea001");
        content.add("0f08e940");
        content.add("00000000");
        content.add("5ef67e1b");
        content.add("0fd026ee");
        content.add("026ea001");
        content.add("0f08e940");
        content.add("00000000");
        content.add("5ef67e1b");
        NFCaDump.parseDump(dump, content);
        ticket = new Ticket(dump);
    }

    /**
     * 2533158286<br/>
     * Universal, 2 pass<br/>
     * Valid 5 days (from 08.09.2016 to 12.09.2016)<br/>
     * Trip N2 16.09.2016 at 00:32<br/>
     * Gate 2281 (Metro)<br/>
     * Passes left 0<br/>
     *<br/>
     * Layout 13, AppId 279, Type 410<br/>
     *
     */
    private void TDS_0xa_U2(){

        expectedTicketNumber = 2533158286L;
        expectedClass = Ticket.C_UNIVERSAL;
        expectedPassesTotal = 2;
        expectedValidDays = 5;
        expectedTripSeqNumber = 2;
        expectedEntranceEntered = 2281;
        expectedTransportType = Ticket.TT_METRO;
        expectedPassesLeft = 0;
        expectedLayout = 10;
        expectedApp = 279;
        expectedType = 412;
        expectedIssued = Calendar.getInstance();
        expectedIssued.clear();
        expectedIssued.set(2016, Calendar.SEPTEMBER, 8, 00, 00);
        expectedTripStart = Calendar.getInstance();
        expectedTripStart.clear();
        expectedTripStart.set(2016, Calendar.SEPTEMBER, 8, 11, 17);

        ArrayList<String> content = new ArrayList<String>();
        content.add("34c06d11");
        content.add("4137f776");
        content.add("f7e07008");
        content.add("fffffffc");
        content.add("45d9c96f");
        content.add("ced8ea00");
        content.add("0fc03840");
        content.add("0054a001");
        content.add("0008e940");
        content.add("00000000");
        content.add("b82de5ab");
        content.add("0fc03840");
        content.add("0054a001");
        content.add("0008e940");
        content.add("00000000");
        content.add("b82de5ab");

        NFCaDump.parseDump(dump, content);
        ticket = new Ticket(dump);

    }

    @Test
    public void testGetValidDays() throws Exception {
        assertEquals(expectedValidDays, ticket.getValidDays());
    }

    @Test
    public void testGetTicketNumber() throws Exception {
        assertEquals(expectedTicketNumber, ticket.getTicketNumber());
    }

    @Test
    public void testGetStartUseBefore() throws Exception {
        if (ticket.getStartUseBefore() != null) {
            assertEquals(
                    "Expected: " +
                            Ticket.df.format(expectedStartUseBefore.getTime()) +
                            " Result: " +
                            Ticket.df.format(ticket.getStartUseBefore().getTime()),
                    expectedStartUseBefore, ticket.getStartUseBefore());
        }
    }

    @Test
    public void testGetType() throws Exception {
        assertEquals(expectedType, ticket.getType());
    }

    @Test
    public void testGetTripSeqNumber() throws Exception {
        assertEquals(expectedTripSeqNumber, ticket.getTripSeqNumber());
    }

    @Test
    public void testGetTicketClass() throws Exception {
        assertEquals(expectedClass, ticket.getTicketClass());
    }

    @Test
    public void testGetLayout() throws Exception {
        assertEquals(expectedLayout, ticket.getLayout());
    }

    @Test
    public void testGetPassesTotal() throws Exception {
        assertEquals(expectedPassesTotal, ticket.getPassesTotal());
    }

    @Test
    public void testGetPassesLeft() throws Exception {
        assertEquals(expectedPassesLeft, ticket.getPassesLeft());
    }

    @Test
    public void testGetGateEntered() throws Exception {
        assertEquals(expectedGateEntered, ticket.getGateEntered());
    }

    @Test
    public void testGetEntranceEntered() throws Exception {
        assertEquals(expectedEntranceEntered, ticket.getEntranceEntered());
    }

    @Test
    public void testGetIssued() throws Exception {
        assertEquals(
                "Expected: " +
                    Ticket.dtf.format(expectedIssued.getTime()) +
                " Result: " +
                    Ticket.dtf.format(ticket.getIssued().getTime()),
                expectedIssued, ticket.getIssued());
    }

    @Test
    public void testGetTripStart() throws Exception {
        assertEquals(
                "Expected: " +
                        Ticket.dtf.format(expectedTripStart.getTime()) +
                " Result: " +
                        Ticket.dtf.format(ticket.getTripStart().getTime()),
                expectedTripStart, ticket.getTripStart());
    }

    @Test
    public void testGetTransportType() throws Exception {
        assertEquals(expectedTransportType, ticket.getTransportType());
    }
}
