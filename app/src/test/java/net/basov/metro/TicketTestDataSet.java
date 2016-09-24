package net.basov.metro;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Class to store data set for TicketTest
 * Created by mvb on 9/24/16.
 */
public class TicketTestDataSet {

    String mName;
    String mError;

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

    ArrayList<Integer> dumpContent = new ArrayList<Integer>();
    Ticket mTicket;

    public String toString() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getError() { return mError; }

    public int getExpectedValidDays() {
        return expectedValidDays;
    }

    public void setExpectedValidDays(int expectedValidDays) {
        if (expectedValidDays < 0 || expectedValidDays > 120) {
            mError += " Valid days";
        }
        this.expectedValidDays = expectedValidDays;
    }

    public long getExpectedTicketNumber() {
        return expectedTicketNumber;
    }

    public void setExpectedTicketNumber(long expectedTicketNumber) {
        if (expectedTicketNumber <= 0L || expectedTicketNumber > 0xffffffffL) {
            mError += " Ticket number ";
        }
        this.expectedTicketNumber = expectedTicketNumber;
    }

    public int getExpectedType() {
        return expectedType;
    }

    public void setExpectedType(int expectedType) {
        switch (expectedType) {
            case Ticket.TO_M1:
            case Ticket.TN_U1_DRV:
            case Ticket.TN_UL3D:
            case Ticket.TN_U2:
// TODO: Need to add all other types

                break;
            default:
                mError += " Vrong type";
                break;
        }
        this.expectedType = expectedType;
    }

    public int getExpectedTripSeqNumber() {
        return expectedTripSeqNumber;
    }

    public void setExpectedTripSeqNumber(int expectedTripSeqNumber) {
        if (expectedTripSeqNumber < 0 || expectedTripSeqNumber > 255) {
            mError += " Trip sequence number";
        }
        this.expectedTripSeqNumber = expectedTripSeqNumber;
    }

    public int getExpectedGateEntered() {
        return expectedGateEntered;
    }

    public void setExpectedGateEntered(int expectedGateEntered) {
        this.expectedGateEntered = expectedGateEntered;
    }

    public int getExpectedTransportType() {
        return expectedTransportType;
    }

    public void setExpectedTransportType(int expectedTransportType) {
        switch (expectedTransportType) {
            case Ticket.TT_GROUND:
            case Ticket.TT_METRO:
                break;
            default:
                mError += " Transport type";
                break;
        }
        this.expectedTransportType = expectedTransportType;
    }

    public int getExpectedEntranceEntered() {
        return expectedEntranceEntered;
    }

    public void setExpectedEntranceEntered(int expectedEntranceEntered) {
        this.expectedEntranceEntered = expectedEntranceEntered;
    }

    public int getExpectedClass() {
        return expectedClass;
    }

    public void setExpectedClass(int expectedClass) {
        switch (expectedClass) {
            case Ticket.C_90UNIVERSAL:
            case Ticket.C_UNIVERSAL:
            case Ticket.C_UNLIM_DAYS:
// TODO: Need to add all other classes
                break;
            default:
                mError += " Vrong class";
                break;
        }
        this.expectedClass = expectedClass;
    }

    public int getExpectedLayout() {
        return expectedLayout;
    }

    public void setExpectedLayout(int expectedLayout) {
        switch (expectedLayout) {
            case 0x08:
            case 0x0d:
            case 0x0a:
                break;
            default:
                mError += " Vrong layout";
        }
        this.expectedLayout = expectedLayout;
    }

    public int getExpectedPassesTotal() {
        return expectedPassesTotal;
    }

    public void setExpectedPassesTotal(int expectedPassesTotal) {
        this.expectedPassesTotal = expectedPassesTotal;
    }

    public int getExpectedPassesLeft() {
        return expectedPassesLeft;
    }

    public void setExpectedPassesLeft(int expectedPassesLeft) {
// TODO: Add parameter check
        this.expectedPassesLeft = expectedPassesLeft;
    }

    public int getExpectedApp() {
        return expectedApp;
    }

    public void setExpectedApp(@Ticket.Application int expectedApp) {
// TODO: Add parameter check
        this.expectedApp = expectedApp;
    }

    public Calendar getExpectedStartUseBefore() {
        return expectedStartUseBefore;
    }

    public void setExpectedStartUseBefore(Calendar expectedStartUseBefore) {
// TODO: Add parameter check
        this.expectedStartUseBefore = expectedStartUseBefore;
    }

    public Calendar getExpectedIssued() {
        return expectedIssued;
    }

    public void setExpectedIssued(Calendar expectedIssued) {
// TODO: Add parameter check
        this.expectedIssued = expectedIssued;
    }

    public Calendar getExpectedTripStart() {
        return expectedTripStart;
    }

    public void setExpectedTripStart(Calendar expectedTripStart) {
// TODO: Add parameter check
        this.expectedTripStart = expectedTripStart;
    }

    public ArrayList<Integer> getDumpContent() {
        return dumpContent;
    }

    public void setDumpContent(ArrayList<Integer> dumpContent) {
        this.dumpContent = dumpContent;
//        NFCaDump.parseDump(dump, this.dumpContent);
        mTicket = new Ticket(dumpContent);
    }

    public Ticket getTicket() {
        return mTicket;
    }
}
