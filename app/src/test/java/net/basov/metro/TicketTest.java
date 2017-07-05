package net.basov.metro;

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

    private Ticket expectedTicket;
    private Ticket realTicket;

    @Parameters(name="{index}: {0}")
    public static Iterable<?> data() {
        return TicketTestData.getDataSets();
    }

    public TicketTest(Ticket TDS) {
        this.expectedTicket = TDS;
        this.realTicket = new Ticket(
                TDS.getDump(),
                TDS.getTimeToCompare()
        );
    }

    @Test
    public void testTestDataSet() throws Exception {
        if (!expectedTicket.isTicketFormatValid())
            Assert.assertEquals(
                    expectedTicket.getParserError(),
                    true,
                    expectedTicket.isTicketFormatValid()
            );
        Assert.assertEquals(null, realTicket.getParserError());
    }

    @Test
    public void testGetValidDays() throws Exception {
        Assert.assertEquals(
                this.expectedTicket.getValidDays(),
                this.realTicket.getValidDays()
        );
    }

    @Test
    public void testGetTicketNumber() throws Exception {
        Assert.assertEquals(
                this.expectedTicket.getTicketNumber(),
                this.realTicket.getTicketNumber()
        );
    }

    @Test
    public void testGetStartUseBefore() throws Exception {
        if (this.expectedTicket.getStartUseBefore() != null) {
            Assert.assertEquals(
                    "Expected StartUseBefore: " +
                            Ticket.DDF.format(this.expectedTicket.getStartUseBefore().getTime()) +
                            " Result: " +
                            Ticket.DDF.format(this.realTicket.getStartUseBefore().getTime()),
                    this.expectedTicket.getStartUseBefore(),
                    this.realTicket.getStartUseBefore()
            );
        } else {
            if (realTicket.getStartUseBefore() != null) {
                Assert.assertEquals(
                        "Expected StartUseBefore: null" +
                                " Result: " +
                                Ticket.DDF.format(this.realTicket.getStartUseBefore().getTime()),
                        null,
                        this.realTicket.getStartUseBefore());
            }
        }
    }

    @Test
    public void testGetStartUseTill() throws Exception {
        if (this.expectedTicket.getStartUseTill() != null) {
            Assert.assertEquals(
                    "Expected StartUseTill: " +
                            Ticket.DDF.format(this.expectedTicket.getStartUseTill().getTime()) +
                            " Result: " +
                            Ticket.DDF.format(this.realTicket.getStartUseTill().getTime()),
                    this.expectedTicket.getStartUseTill(),
                    this.realTicket.getStartUseTill()
            );
        } else {
            if (realTicket.getStartUseTill() != null) {
                Assert.assertEquals(
                        "Expected StartUseTill: null" +
                                " Result: " +
                                Ticket.DDF.format(this.realTicket.getStartUseTill().getTime()),
                        null,
                        this.realTicket.getStartUseTill());
            }
        }
    }

    @Test
    public void testGetType() throws Exception {
        Assert.assertEquals(
                this.expectedTicket.getTicketType(),
                this.realTicket.getTicketType()
        );
    }

    @Test
    public void testGetTripSeqNumber() throws Exception {
        Assert.assertEquals(
                this.expectedTicket.getTripSeqNumber(),
                this.realTicket.getTripSeqNumber()
        );
    }

    @Test
    public void testGetTicketClass() throws Exception {
        Assert.assertEquals(
                this.expectedTicket.getTicketClass(),
                this.realTicket.getTicketClass()
        );
    }

    @Test
    public void testGetLayout() throws Exception {
        Assert.assertEquals(
                this.expectedTicket.getLayout(),
                this.realTicket.getLayout()
        );
    }

    @Test
    public void testGetPassesTotal() throws Exception {
        Assert.assertEquals(
                this.expectedTicket.getPassesTotal(),
                this.realTicket.getPassesTotal()
        );
    }

    @Test
    public void testGetPassesLeft() throws Exception {
        Assert.assertEquals(
                this.expectedTicket.getPassesLeft(),
                this.realTicket.getPassesLeft()
        );
    }

    @Test
    public void testGetTurnstileEntered() throws Exception {
        Assert.assertEquals(
                this.expectedTicket.getTurnstileEntered(),
                this.realTicket.getTurnstileEntered()
        );
    }

    @Test
    public void testGetEntranceEntered() throws Exception {
        Assert.assertEquals(
                this.expectedTicket.getEntranceEntered(),
                this.realTicket.getEntranceEntered()
        );
    }

    @Test
    public void testGetIssued() throws Exception {
        if (expectedTicket.getIssued() != null) {
            Assert.assertEquals(
                    "Expected Issued: " +
                            Ticket.DDF.format(this.expectedTicket.getIssued().getTime()) +
                            " Result: " +
                            Ticket.DDF.format(this.realTicket.getIssued().getTime()),
                    this.expectedTicket.getIssued(),
                    this.realTicket.getIssued()
            );
        } else {
            if (realTicket.getIssued() != null) {
                Assert.assertEquals(
                        "Expected Issued: null" +
                                " Result: " +
                                Ticket.DDF.format(this.realTicket.getIssued().getTime()),
                        null,
                        this.realTicket.getIssued());
            }
        }
    }

    @Test
    public void testGetTripStart() throws Exception {
        if (expectedTicket.getTripStart() != null) {
            Assert.assertEquals(
                    "Expected TripStartTime: " +
                            Ticket.DDF.format(this.expectedTicket.getTripStart().getTime()) +
                            " Result: " +
                            Ticket.DDF.format(this.realTicket.getTripStart().getTime()),
                    this.expectedTicket.getTripStart(),
                    this.realTicket.getTripStart()
            );
        } else {
            if (realTicket.getTripStart() != null) {
                Assert.assertEquals(
                        "Expected TripStartTime: null" +
                                " Result: " +
                                Ticket.DDF.format(this.realTicket.getTripStart().getTime()),
                        null,
                        this.realTicket.getTripStart());
            }
        }
    }

    @Test
    public void testGetPassTransportType() throws Exception {
        Assert.assertEquals(
                this.expectedTicket.getPassTransportType(),
                this.realTicket.getPassTransportType()
        );
    }

    @Test
    public void testGetPassMetroType() throws Exception {
        Assert.assertEquals(
                this.expectedTicket.getPassMetroType(),
                this.realTicket.getPassMetroType()
        );
    }

    @Test
    public void testGetApp() throws Exception {
        Assert.assertEquals(
                this.expectedTicket.getApp(),
                this.realTicket.getApp()
        );
    }

    @Test
    public void testGetT90ChangeTime() throws Exception {
        Assert.assertEquals(
                this.expectedTicket.getT90ChangeTime(),
                this.realTicket.getT90ChangeTime()
        );

    }

    @Test
    public void testGetT90MCount() throws Exception {
        Assert.assertEquals(
                this.expectedTicket.getT90MCount(),
                this.realTicket.getT90MCount()
        );
    }

    @Test
    public void testGetFirstUseTime() throws Exception {
        Assert.assertEquals(
                this.expectedTicket.getFirstUseTime(),
                this.realTicket.getFirstUseTime()
        );
    }

    @Test
    public void testGetTimeToNextTrip() throws Exception {
        Assert.assertEquals(
                this.expectedTicket.getTimeToNextTrip(),
                this.realTicket.getTimeToNextTrip()
        );

    }

    @Test
    public void testGetTimeToCompare() throws Exception {
        Assert.assertEquals(
                "Expected: " +
                        Ticket.DDF.format(this.expectedTicket.getTimeToCompare().getTime()) +
                        " Result: " +
                        Ticket.DDF.format(this.realTicket.getTimeToCompare().getTime()),
                this.expectedTicket.getTimeToCompare(),
                this.realTicket.getTimeToCompare()
        );
    }

    @Test
    public void testCreateDumpFileName() throws Exception {
        Assert.assertEquals(
                Ticket.createAutoDumpFileName(this.expectedTicket),
                Ticket.createAutoDumpFileName(this.realTicket)
        );
    }
}
