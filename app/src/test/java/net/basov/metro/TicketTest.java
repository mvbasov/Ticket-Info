package net.basov.metro;

import java.util.ArrayList;

import junit.framework.TestCase;

import net.basov.nfc.NFCaDump;

import org.junit.Before;
import org.junit.Test;

//import org.junit.Test;

//import static org.junit.Assert.*;

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
    int expectedEntranceEntered;
    int expectedClass;
    int expectedLayout;
    int expectedPassesTotal;
    int expectedPassesLeft;
    int expectedApp;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        ArrayList<String> content = new ArrayList<String>();

/**
 * 0083381452 (till 28.05.2017)
 * Universal, 1 pass
 * Valid 5 days (from 16.09.2016 to 20.09.2016)
 * Trip N1 16.09.2016 at 00:32
 * Gate 137 (Ground)
 * Passes left 0
 *
 * Layout 13, AppId 279, Type 410
 *
        expectedTicketNumber = 83381452L;
        expectedClass = Ticket.C_UNIVERSAL;
        expectedPassesTotal = 1;
        expectedValidDays = 5;
        expectedTripSeqNumber = 1;
        expectedGateEntered = 137;
        expectedPassesLeft = 0;
        expectedLayout = 13;
        expectedApp = 279;
        expectedType = 410;
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
 */

/**
 * 0001983509
 * Unlimited, 3 days
 * Valid 3 days (from 09.09.2016 11:02 to 12.09.2016 11:02)
 * Trip N15 16.09.2016 at 11:01
 * Gate 2281 (Metro)
 *
 * Layout 10, AppId 279, Type 435
 *
 */
        expectedTicketNumber = 1983509L;
        expectedClass = Ticket.C_UNLIM_DAYS;
        expectedPassesTotal = -1;
        expectedValidDays = 3;
        expectedTripSeqNumber = 15;
        expectedEntranceEntered = 2281;
        expectedPassesLeft = -1;
        expectedLayout = 10;
        expectedApp = 279;
        expectedType = 435;
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

    @Test
    public void testGetValidDays() throws Exception {
        assertEquals(expectedValidDays, ticket.getValidDays());
    }

    @Test
    public void testGetTicketNumber() throws Exception {
        assertEquals(expectedTicketNumber, ticket.getTicketNumber());
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
}