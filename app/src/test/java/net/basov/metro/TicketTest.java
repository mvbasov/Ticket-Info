package net.basov.metro;

import java.util.Calendar;

import org.junit.Assert;
import org.junit.runners.Parameterized;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;

/**
 * Junit 4 tests for net.basov.metro.Ticket class
 * Created by mvb on 9/23/16.
 */
@RunWith(value = Parameterized.class)
public class TicketTest {

    private Ticket ticket;
    private String error;
    private int expectedValidDays;
    private long expectedTicketNumber;
    private int expectedType;
    private int expectedTripSeqNumber;
    private int expectedGateEntered;
    private int expectedTransportType;
    private int expectedEntranceEntered;
    private int expectedClass;
    private int expectedLayout;
    private int expectedPassesTotal;
    private int expectedPassesLeft;
    private int expectedApp;
    private Calendar expectedStartUseBefore;
    private Calendar expectedIssued;
    private Calendar expectedTripStart;

    @Parameters(name="{index}: {0}")
    public static Iterable<?> data() {
        return TicketTestData.getDataSets();
    }

    public TicketTest(TicketTestDataSet TDS) {
        this.error = TDS.getError();
        this.ticket = TDS.getTicket();
        this.expectedValidDays = TDS.getExpectedValidDays();
        this.expectedTicketNumber = TDS.getExpectedTicketNumber();
        this.expectedType = TDS.getExpectedType();
        this.expectedTripSeqNumber = TDS.getExpectedTripSeqNumber();
        this.expectedGateEntered = TDS.getExpectedGateEntered();
        this.expectedTransportType = TDS.getExpectedTransportType();
        this.expectedEntranceEntered = TDS.getExpectedEntranceEntered();
        this.expectedClass = TDS.getExpectedClass();
        this.expectedLayout = TDS.getExpectedLayout();
        this.expectedPassesTotal = TDS.getExpectedPassesTotal();
        this.expectedPassesLeft = TDS.getExpectedPassesLeft();
        this.expectedApp = TDS.getExpectedApp();
        this.expectedStartUseBefore = TDS.getExpectedStartUseBefore();
        this.expectedIssued = TDS.getExpectedIssued();
        this.expectedTripStart = TDS.getExpectedTripStart();
    }

    @Test
    public void testTestDataSet() throws Exception {
        Assert.assertEquals(null, this.error);
    }

    @Test
    public void testGetValidDays() throws Exception {
        Assert.assertEquals(this.expectedValidDays, this.ticket.getValidDays());
    }

    @Test
    public void testGetTicketNumber() throws Exception {
        Assert.assertEquals(this.expectedTicketNumber, this.ticket.getTicketNumber());
    }

    @Test
    public void testGetStartUseBefore() throws Exception {
        if (this.expectedStartUseBefore != null) {
            Assert.assertEquals(
                    "Expected: " +
                            Ticket.DDF.format(this.expectedStartUseBefore.getTime()) +
                            " Result: " +
                            Ticket.DDF.format(this.ticket.getStartUseBefore().getTime()),
                    this.expectedStartUseBefore, this.ticket.getStartUseBefore());
        } else {
            Assert.assertEquals(null, this.ticket.getStartUseBefore());
        }
    }

    @Test
    public void testGetType() throws Exception {
        Assert.assertEquals(this.expectedType, this.ticket.getTicketType());
    }

    @Test
    public void testGetTripSeqNumber() throws Exception {
        Assert.assertEquals(this.expectedTripSeqNumber, this.ticket.getTripSeqNumber());
    }

    @Test
    public void testGetTicketClass() throws Exception {
        Assert.assertEquals(this.expectedClass, this.ticket.getTicketClass());
    }

    @Test
    public void testGetLayout() throws Exception {
        Assert.assertEquals(this.expectedLayout, this.ticket.getLayout());
    }

    @Test
    public void testGetPassesTotal() throws Exception {
        Assert.assertEquals(this.expectedPassesTotal, this.ticket.getPassesTotal());
    }

    @Test
    public void testGetPassesLeft() throws Exception {
        Assert.assertEquals(this.expectedPassesLeft, this.ticket.getPassesLeft());
    }

    @Test
    public void testGetGateEntered() throws Exception {
        Assert.assertEquals(this.expectedGateEntered, this.ticket.getGateEntered());
    }

    @Test
    public void testGetEntranceEntered() throws Exception {
        Assert.assertEquals(this.expectedEntranceEntered, this.ticket.getEntranceEntered());
    }

    @Test
    public void testGetIssued() throws Exception {
        Assert.assertEquals(
                "Expected: " +
                    Ticket.DDF.format(this.expectedIssued.getTime()) +
                " Result: " +
                    Ticket.DDF.format(this.ticket.getIssued().getTime()),
                this.expectedIssued, this.ticket.getIssued());
    }

    @Test
    public void testGetTripStart() throws Exception {
        Assert.assertEquals(
                "Expected: " +
                        Ticket.DDF.format(this.expectedTripStart.getTime()) +
                " Result: " +
                        Ticket.DDF.format(this.ticket.getTripStart().getTime()),
                this.expectedTripStart, this.ticket.getTripStart());
    }

    @Test
    public void testGetTransportType() throws Exception {
        Assert.assertEquals(this.expectedTransportType, this.ticket.getTransportType());
    }

    @Test
    public void testGetApp() throws Exception {
        Assert.assertEquals(this.expectedApp, this.ticket.getApp());
    }
}
