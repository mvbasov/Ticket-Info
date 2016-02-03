/**
 * The MIT License (MIT)

 Copyright (c) 2015 Mikhail Basov

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

package net.basov.nfc;

import android.nfc.tech.NfcA;

import java.io.IOException;
import java.util.ArrayList;

public class NFCaDump {
    /*
    Operate with NFC A technology cards.

    !!! Attention !!!
    The following functions (doesn't support by all cards):
        readVERSION(NfcA);
        readSIGN(NfcA);
        readCounters(NfcA);
     need to be wrapped around by separate nfca.connect()/nfca.close()
     and placed after (supported by all cards):
        readATQA(NfcA);
        readSAK(NfcA);
        readPages(NfcA); (need to be last, because finished by exception)
     because read this information from card which doesn't support it block normal
     operation without reconnect.
     */

    // Constants definition
    public static final int MAX_PAGES = 64;

    public static final byte AC_UNKNOWN = 0;
    public static final byte AC_FACTORY_LOCKED = AC_UNKNOWN + 1;
    public static final byte AC_READ_ONLY = AC_UNKNOWN + 2;
    public static final byte AC_PARTIAL_WRITE = AC_UNKNOWN + 3;
    public static final byte AC_WRITE = AC_UNKNOWN + 4;
    public static final byte AC_OTP = AC_UNKNOWN + 5;
    public static final byte AC_AUTH_REQUIRE = AC_UNKNOWN + 6;
    public static final byte AC_INTERAL_USE = AC_UNKNOWN + 7;
    public static final byte AC_SPECIAL = AC_UNKNOWN + 8;

    public static final byte IC_UNKNOWN = 0;
    public static final byte IC_MF0ICU1 = IC_UNKNOWN + 1;
    public static final byte IC_MF0UL11 = IC_UNKNOWN + 2;
    public static final byte IC_MF0UL21 = IC_UNKNOWN + 3;
    public static final byte IC_MIK640D = IC_UNKNOWN + 4;
    public static final byte IC_MIK1312ED = IC_UNKNOWN + 5;

    public static final byte CMD_GET_VERSION = (byte)0x60;
    public static final byte CMD_READ_SIGN = (byte)0x3C;
    public static final byte CMD_READ_CNT = (byte)0x39;
    public static final byte CMD_INCR_CNT = (byte)0xA5;

    // Data fields definition
    private ArrayList<byte[]> Pages;
    private int LastBlockValidPages;
    private boolean LastBlockVerifyed;
    private byte[] ATQA;
    private byte SAK;
    private byte[] VersionInfo;
    private byte[] SIGN;
    private ArrayList<byte[]> Counters;
    private ArrayList<String> AndTechList;
    private boolean SIGNisEmpty;
    private boolean VERSIONisEmpty;
    private byte IC_Type;
    private byte[] PagesAccess;

    public NFCaDump() {
        Pages = new ArrayList<byte[]>();
        Counters = new ArrayList<byte[]>();
        AndTechList = new ArrayList<String>();
        LastBlockValidPages = 4;
        SIGNisEmpty = true;
        VERSIONisEmpty = true;
        LastBlockVerifyed = false;
        //VersionInfo = new byte[8];
        //SIGN = new byte[32];
        IC_Type = IC_UNKNOWN;
        PagesAccess = new byte[MAX_PAGES];
        for (int i = 0; i < MAX_PAGES - 1; i++) {
            PagesAccess[i] = AC_UNKNOWN;
        }
    }

/* Operate with ATQA and SAK */

    public void readATQA(NfcA nfca) {
        try {
            if (!nfca.isConnected()) nfca.connect();
            setATQA(nfca.getAtqa());
        } catch (IOException ignored) {}
    }

    public void setATQA(byte[] ATQA) {
        this.ATQA = ATQA;
    }

    public byte[] getATQA() {
        return this.ATQA;
    }

    public void readSAK(NfcA nfca) {
        try {
            if (!nfca.isConnected()) nfca.connect();
            setSAK((byte) nfca.getSak());
        } catch (IOException ignored) {}
    }

    public void setSAK(byte SAK) {
        this.SAK = SAK;
    }

    public byte getSAK() {
        return this.SAK;
    }

/* Dump pages operation functions */

    public void readPages(NfcA nfca) {
        for (int i = 0; i < (NFCaDump.MAX_PAGES/4) + 1; i++) {
            byte[] cmd = {0x30, (byte) (i * 4)};
            try {
                if (!nfca.isConnected()) nfca.connect();
/*
                Read 4 pages (4 bytes each) block.
                Throw IOException if index of 1-st page out of band.
                Wrap around p0 if try to read more then exists pages.
                According to manufacturer data sheets read 4 pages of 4 bytes,
                i.e. exactly 16 bytes in any case.
                But, on some devices (Sony Xperia Z1 with Android 5.1.1, for example)
                last block with only one byte generated.
                Other devices (Samsung Galaxy S IV with Andpoid 5.0.1, for example)
                lead manufacturer description.
*/
                byte[] answer = nfca.transceive(cmd);
                if (answer.length != 16) {
                    break;  //   Only 16 bytes blocks are valid
                }
                addPagesBlock( answer );
            } catch (IOException ignored) {
                break; // this 4 pages block totally out of band
            }
        }
    }

    public void addPage(byte[] page) {
        this.Pages.add(page);
    }

    public void addPagesBlock(byte[] block) {
    /*
    According to manufacturer data sheets read 4 pages of 4 bytes,
    i.e. exactly 16 bytes in any case.
    But, on some devices (Sony Xperia Z1 with Android 5.1.1, for example)
    last block with only one byte generated.
    Other devices (Samsung Galaxy S IV with Andpoid 5.0.1, for example)
    lead manufacturer description.
    */
        if (block.length == 16) {
            for (int i = 0; i < 4; i++) {
                this.addPage(new byte[]{
                        block[i * 4],
                        block[i * 4 + 1],
                        block[i * 4 + 2],
                        block[i * 4 + 3]});
            }
        }
    }

    public int getPagesNumber() {
        return this.Pages.size();
    }

    public boolean isPagesEmpty(){
        if (this.Pages.size() == 0) {
            return true;
        }
        return false;
    }

    public byte[] getPage(int n) {
        return this.Pages.get(n);
    }

    public int getPageAsInt(int n) {
        int page = 0;
        for (int i = 0; i < 4; i++) {
            page <<= 8;
            page |= getPage(n)[i] & 0xff;
        }
        return page;
    }

    private void validateLastBlockPages() {
    /*
    Attempt to exclude wrapped around information from last page.
    WARNING:
    Not strong algorithm. It is possible
    to write id on last page to brake it
    */
        this.LastBlockValidPages = 0; //last block page valid number
        if (Pages.size() > 4) {
            for (int i = 0; i < 4; i++) {
                if (Pages.get(Pages.size() - 4 + i)[0] == Pages.get(0)[0]
                        && Pages.get(Pages.size() - 4 + i)[1] == Pages.get(0)[1]
                        && Pages.get(Pages.size() - 4 + i)[2] == Pages.get(0)[2]
                        && Pages.get(Pages.size() - 4 + i)[3] == Pages.get(0)[3]) {
                    break;
                }
                LastBlockValidPages++;
            }
        } else {
            LastBlockValidPages = 4;
        }
        LastBlockVerifyed = true;
    }

    public int getLastBlockValidPages() {
        if (!LastBlockVerifyed) validateLastBlockPages();
        return this.LastBlockValidPages;
    }

/* Pages access condition detection and show functions */

    private void detectPagesAccess() {

        PagesAccess[0] = AC_FACTORY_LOCKED;
        PagesAccess[1] = AC_FACTORY_LOCKED;
        PagesAccess[2] = (getPage(2)[1] & 0x90) != 0 ? AC_READ_ONLY : AC_PARTIAL_WRITE;
        PagesAccess[3] = AC_OTP;
        PagesAccess[4] = ((getPage(2)[2] & 0x10) | (getPage(2)[2] & 0x02)) != 0 ? AC_READ_ONLY : AC_WRITE;
        PagesAccess[5] = ((getPage(2)[2] & 0x20) | (getPage(2)[2] & 0x02)) != 0 ? AC_READ_ONLY : AC_WRITE;
        PagesAccess[6] = ((getPage(2)[2] & 0x40) | (getPage(2)[2] & 0x02)) != 0 ? AC_READ_ONLY : AC_WRITE;
        PagesAccess[7] = ((getPage(2)[2] & 0x80) | (getPage(2)[2] & 0x02)) != 0 ? AC_READ_ONLY : AC_WRITE;
        PagesAccess[8] = ((getPage(2)[3] & 0x01) | (getPage(2)[2] & 0x02)) != 0 ? AC_READ_ONLY : AC_WRITE;
        PagesAccess[9] = ((getPage(2)[3] & 0x02) | (getPage(2)[2] & 0x02)) != 0 ? AC_READ_ONLY : AC_WRITE;
        PagesAccess[10] = ((getPage(2)[3] & 0x04) | (getPage(2)[2] & 0x04)) != 0 ? AC_READ_ONLY : AC_WRITE;
        PagesAccess[11] = ((getPage(2)[3] & 0x08) | (getPage(2)[2] & 0x04)) != 0 ? AC_READ_ONLY : AC_WRITE;
        PagesAccess[12] = ((getPage(2)[3] & 0x10) | (getPage(2)[2] & 0x04)) != 0 ? AC_READ_ONLY : AC_WRITE;
        PagesAccess[13] = ((getPage(2)[3] & 0x20) | (getPage(2)[2] & 0x04)) != 0 ? AC_READ_ONLY : AC_WRITE;
        PagesAccess[14] = ((getPage(2)[3] & 0x40) | (getPage(2)[2] & 0x04)) != 0 ? AC_READ_ONLY : AC_WRITE;
        PagesAccess[15] = ((getPage(2)[3] & 0x80) | (getPage(2)[2] & 0x04)) != 0 ? AC_READ_ONLY : AC_WRITE;

        switch (IC_Type) {
            case IC_MF0UL11:
                PagesAccess[16] = AC_SPECIAL;
                PagesAccess[17] = AC_SPECIAL;
                PagesAccess[18] = AC_SPECIAL;
                PagesAccess[19] = AC_SPECIAL;
                if (getPage(16)[3] != (byte)0xff) {
                    for (int i = (int)(getPage(16)[3] & 0x0ffL); i < MAX_PAGES - 1; i++) {
                        PagesAccess[i] = AC_AUTH_REQUIRE;
                    }
                }
                break;
            case IC_MIK640D:
                PagesAccess[16] = AC_OTP;
                PagesAccess[17] = AC_OTP;
                PagesAccess[18] = AC_OTP;
                PagesAccess[19] = AC_INTERAL_USE;
                break;
            case IC_MIK1312ED:
            case IC_MF0UL21:
                PagesAccess[16] = ((getPage(36)[0] & 0x01) | (getPage(36)[2] & 0x01)) != 0 ? AC_READ_ONLY : AC_WRITE;
                PagesAccess[17] = ((getPage(36)[0] & 0x01) | (getPage(36)[2] & 0x01)) != 0 ? AC_READ_ONLY : AC_WRITE;
                PagesAccess[18] = ((getPage(36)[0] & 0x02) | (getPage(36)[2] & 0x01)) != 0 ? AC_READ_ONLY : AC_WRITE;
                PagesAccess[19] = ((getPage(36)[0] & 0x02) | (getPage(36)[2] & 0x01)) != 0 ? AC_READ_ONLY : AC_WRITE;
                PagesAccess[20] = ((getPage(36)[0] & 0x04) | (getPage(36)[2] & 0x02)) != 0 ? AC_READ_ONLY : AC_WRITE;
                PagesAccess[21] = ((getPage(36)[0] & 0x04) | (getPage(36)[2] & 0x02)) != 0 ? AC_READ_ONLY : AC_WRITE;
                PagesAccess[22] = ((getPage(36)[0] & 0x08) | (getPage(36)[2] & 0x02)) != 0 ? AC_READ_ONLY : AC_WRITE;
                PagesAccess[23] = ((getPage(36)[0] & 0x08) | (getPage(36)[2] & 0x02)) != 0 ? AC_READ_ONLY : AC_WRITE;
                PagesAccess[24] = ((getPage(36)[0] & 0x10) | (getPage(36)[2] & 0x04)) != 0 ? AC_READ_ONLY : AC_WRITE;
                PagesAccess[25] = ((getPage(36)[0] & 0x10) | (getPage(36)[2] & 0x04)) != 0 ? AC_READ_ONLY : AC_WRITE;
                PagesAccess[26] = ((getPage(36)[0] & 0x20) | (getPage(36)[2] & 0x04)) != 0 ? AC_READ_ONLY : AC_WRITE;
                PagesAccess[27] = ((getPage(36)[0] & 0x20) | (getPage(36)[2] & 0x04)) != 0 ? AC_READ_ONLY : AC_WRITE;
                PagesAccess[28] = ((getPage(36)[0] & 0x40) | (getPage(36)[2] & 0x08)) != 0 ? AC_READ_ONLY : AC_WRITE;
                PagesAccess[29] = ((getPage(36)[0] & 0x40) | (getPage(36)[2] & 0x08)) != 0 ? AC_READ_ONLY : AC_WRITE;
                PagesAccess[30] = ((getPage(36)[0] & 0x80) | (getPage(36)[2] & 0x08)) != 0 ? AC_READ_ONLY : AC_WRITE;
                PagesAccess[31] = ((getPage(36)[0] & 0x80) | (getPage(36)[2] & 0x08)) != 0 ? AC_READ_ONLY : AC_WRITE;
                PagesAccess[32] = ((getPage(36)[1] & 0x01) | (getPage(36)[2] & 0x10)) != 0 ? AC_READ_ONLY : AC_WRITE;
                PagesAccess[33] = ((getPage(36)[1] & 0x01) | (getPage(36)[2] & 0x10)) != 0 ? AC_READ_ONLY : AC_WRITE;
                PagesAccess[34] = ((getPage(36)[1] & 0x02) | (getPage(36)[2] & 0x10)) != 0 ? AC_READ_ONLY : AC_WRITE;
                PagesAccess[35] = ((getPage(36)[1] & 0x02) | (getPage(36)[2] & 0x10)) != 0 ? AC_READ_ONLY : AC_WRITE;
                PagesAccess[36] = AC_SPECIAL;
                PagesAccess[37] = AC_SPECIAL;
                PagesAccess[38] = AC_SPECIAL;
                PagesAccess[39] = AC_SPECIAL;
                PagesAccess[40] = AC_SPECIAL;
                if (getPage(37)[3] != (byte)0xff) {
                    for (int i = (int)(getPage(37)[3] & 0x0ffL); i < MAX_PAGES - 1; i++) {
                        PagesAccess[(byte)i] = AC_AUTH_REQUIRE;
                    }
                }
                break;
            default:
                break;
        }
    }

    public byte getPageAssess(int page) {
        if (PagesAccess[0] == AC_UNKNOWN) detectPagesAccess();
        return PagesAccess[page];
    }

    private String getAccessAsString(byte ac) {
        switch (ac) {
            case AC_UNKNOWN:
                return "u";
            case AC_FACTORY_LOCKED:
                return "f";
            case AC_READ_ONLY:
                return "r";
            case AC_PARTIAL_WRITE:
                return "p";
            case AC_WRITE:
                return "w";
            case AC_OTP:
                return "o";
            case AC_SPECIAL:
                return "s";
            case AC_INTERAL_USE:
                return "i";
            case AC_AUTH_REQUIRE:
                return "a";
            default:
                return "n";
        }
    }

/* IC detection functions */

    private void detectIC_Type() {
        /*
        This algorithm operate only with full accesable cards
        If authentication required to read some pages it doesn't operate
        beacuse based on number of sucessfuly read pages.
        I don't want to read VERSION and SIGN on every card because it slow
        card reading.
         */
        switch (getPage(0)[0]) {
            case 0x04:
                if (getPagesNumber() == 20 &&
                        !isVERSIONEmpty()) {
                    if (getVersionInfo()[0] == (byte) 0x00 &&
                            getVersionInfo()[1] == (byte) 0x04 &&
                            getVersionInfo()[2] == (byte) 0x03 &&
                            getVersionInfo()[4] == (byte) 0x01 &&
                            getVersionInfo()[5] == (byte) 0x00 &&
                            getVersionInfo()[6] == (byte) 0x0b) {
/*
                        according to data sheet byte 3 need to be:
                        - 0x01 (17pF version MF0L(1|2)1)
                        - 0x02 (50pF version MF0LH(1|2)1)
                        but all tickets I've seen had 0x03 value in this byte
                        I don't know what does it mead and don't use this
                        byte for identification.
*/
                        IC_Type = IC_MF0UL11;
                    }
                } else if (getPagesNumber() == 44 &&
                        !isVERSIONEmpty()) {
                    if (getVersionInfo()[0] == (byte) 0x00 &&
                            getVersionInfo()[1] == (byte) 0x04 &&
                            getVersionInfo()[2] == (byte) 0x03 &&
                            getVersionInfo()[4] == (byte) 0x01 &&
                            getVersionInfo()[5] == (byte) 0x00 &&
                            getVersionInfo()[6] == (byte) 0x0e) {
                        IC_Type = IC_MF0UL21;
                    }
                } else if (getPagesNumber() == 16) {
                    IC_Type = IC_MF0ICU1;
                } else {
                    IC_Type = IC_UNKNOWN;
                }
                break;
            case 0x34:
                if (getPagesNumber() == 44 &&
                        !isVERSIONEmpty()) {
                    if (getVersionInfo()[0] == (byte) 0x00 &&
                            getVersionInfo()[1] == (byte) 0x34 &&
                            getVersionInfo()[2] == (byte) 0x21 &&
                            getVersionInfo()[3] == (byte) 0x01 &&
                            getVersionInfo()[4] == (byte) 0x01 &&
                            getVersionInfo()[5] == (byte) 0x00) {
                        IC_Type = IC_MIK1312ED;
                    }
                } else if (getPagesNumber() == 20) {
                        IC_Type = IC_MIK640D;
                } else {
                    IC_Type = IC_UNKNOWN;
                }
                break;
            default:
                IC_Type = IC_UNKNOWN;
                break;
        }

    }

    public byte getIC_Type(){
        if (IC_Type == IC_UNKNOWN) detectIC_Type();
        return IC_Type;
    }

    public String getIC_TypeAsString() {
        switch (getIC_Type()) {
            case IC_MF0ICU1:
                return "(probably)MF0ICU1 (64 bytes) [Mifare Ultralight]";
            case IC_MF0UL11:
                return "MF0UL(H)11 (80 bytes) [Mifare Ultralight EV1]";
            case IC_MF0UL21:
                return "MF0UL(H)21 (164 bytes) [Mifare Ultralight EV1]";
            case IC_MIK640D:
                return "(probably) MIK64PTAS(MIK640D) (80 bytes)";
            case IC_MIK1312ED:
                return "MIK1312ED(К5016ВГ4Н4 aka K5016XC1M1H4) (164 bytes)";
            default:
                return "Unknown";
        }
    }

/* UID CRC check */

    public boolean UID_CRC_Check() {

        // CT (Cascade Tag) [value 88h] as defined in ISO/IEC 14443-3 Type A
        byte UID_BCC0_CRC = (byte)0x88;
        UID_BCC0_CRC ^= getPage(0)[0];
        UID_BCC0_CRC ^= getPage(0)[1];
        UID_BCC0_CRC ^= getPage(0)[2];
        UID_BCC0_CRC ^= getPage(0)[3]; //The BCC0 itself, if ok result is 0

        byte UID_BCC1_CRC = (byte)0x00;
        UID_BCC1_CRC ^= getPage(1)[0];
        UID_BCC1_CRC ^= getPage(1)[1];
        UID_BCC1_CRC ^= getPage(1)[2];
        UID_BCC1_CRC ^= getPage(1)[3];
        UID_BCC1_CRC ^= getPage(2)[0]; //The BCC1 itself, if ok result is 0

        if (UID_BCC0_CRC == 0 && UID_BCC1_CRC == 0) {
            return true;
        }
        return false;
    }

/* Display manufacturer name */

    public String getManufName() {
        switch (Pages.get(0)[0]){
            case 0x04:
                return "NXP Semiconductors (Philips) Germany";
            case 0x34:
                return "JSC Micron Russia";
            default:
                return "Unknown";
        }
    }

/* IC manufacturer signature operation functions */

    public void readSIGN(NfcA nfca) {
        setSIGNisEmpty();
        try {
            byte[] cmd_read_sign = {
                    NFCaDump.CMD_READ_SIGN,
                    (byte)0x00}; // according to data sheet
            if (!nfca.isConnected()) nfca.connect();
            setSIGN(nfca.transceive(cmd_read_sign));
            setSIGNisEmpty(false);

        } catch (IOException ignored) {
            setSIGNisEmpty();
        }

    }

    public void setSIGNisEmpty(boolean SIGNisEmpty) {
        this.SIGNisEmpty = SIGNisEmpty;
    }

    public void setSIGNisEmpty() {
        this.SIGNisEmpty = true;
    }

    public boolean isSIGNEmpty() {
        return SIGNisEmpty;
    }

    public void setSIGN(byte[] SIGN) {
        this.SIGN = SIGN;
    }

    public byte[] getSIGN() {
        return this.SIGN;
    }

/* IC hardware version get and display functions */

    public void readVERSION(NfcA nfca) {
        setVERSIONisEmpty();
        try {
            byte[] cmd_ver = {NFCaDump.CMD_GET_VERSION};
            if (!nfca.isConnected()) nfca.connect();
            setVersionInfo(nfca.transceive(cmd_ver));
            setVERSIONisEmpty(false);
        } catch (IOException ignored) {
            setVERSIONisEmpty();
        }

    }

    public void setVERSIONisEmpty(boolean VERSIONisEmpty) {
        this.VERSIONisEmpty = VERSIONisEmpty;
    }

    public void setVERSIONisEmpty() {
        this.VERSIONisEmpty = true;
    }

    public boolean isVERSIONEmpty() {
        return VERSIONisEmpty;
    }

    public void setVersionInfo(byte[] versionInfo) {
        this.VersionInfo = versionInfo;
    }

    public byte[] getVersionInfo() {
        return this.VersionInfo;
    }

/* Internal one way counters read and display functions */

    public void readCounters(NfcA nfca) {
        try {
            byte[] cmd_read_cnt = {
                    NFCaDump.CMD_READ_CNT,
                    (byte)0x00 };
            if (!nfca.isConnected()) nfca.connect();
            for (int i = 0; i < 3; i++) {
                cmd_read_cnt[1] = (byte) i;
                addCounter(nfca.transceive(cmd_read_cnt));
            }
        } catch (IOException ignored) { }

    }

    public void addCounter(byte[] counter) {
        this.Counters.add(counter);
    }

    public int getCountersNumber() {
        return this.Counters.size();
    }

    public byte[] getCounter(int i) {
        return this.Counters.get(i);
    }

    public void incCounter(NfcA nfca, int cnt, byte[] inc) {
/*
    nfca - NFC A connection
    cnt - increment counter number
    incr - LSB 1-st, 4-th byte ignored but need

    Doesn't need to dump functionality.

    Increment to 0 (safe):
        byte[] inc = {
                (byte)0x00, // LSB
                (byte)0x00,
                (byte)0x00, // MSB
                (byte)0x00 // ignored
                };
        d.incCounter(nfca, 0, inc);

    Increment to 321(hex):
        byte[] inc = {
                (byte)0x01, // LSB
                (byte)0x02,
                (byte)0x03, // MSB
                (byte)0x00 // ignored
                };
        d.incCounter(nfca, 0, inc);

    WARNING! Counters are one way!
*/
        try {
            byte[] cmd_incr_cnt = {
                    CMD_INCR_CNT,
                    (byte)cnt,  // increment counter 0
                    // LSB 1-st, 4-th byte need but ignored.
                    // 0 increment is valid but has no effect.
                    inc[0], inc[1], inc[2], inc[3]};
            if (!nfca.isConnected()) nfca.connect();
            nfca.transceive(cmd_incr_cnt);
        } catch (IOException ignored) {}
    }

/* Operate with Android detected technologies list */

    public void addAndTechList(String andTech) {
        this.AndTechList.add(andTech);
    }

    public ArrayList<String> getAndTechList() {
        return this.AndTechList;
    }

/* Display dump an IC tech . information */

    public String getUIDCheckAsString() {
        StringBuilder sb = new StringBuilder();
        sb.append("UID: ").append(String.format("%08x %08x\n", getPageAsInt(0), getPageAsInt(1)));
        sb.append(String.format("  BCC0: %02x, BCC1: %02x", getPage(0)[3], getPage(2)[0]));

        if (UID_CRC_Check()) {
            sb.append(" (CRC OK)");
        } else {
            sb.append(" (CRC not OK)");
        }
        sb.append("\n");

        return sb.toString();
    }

    public String getMemoryInfoAsString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("4 bytes pages read: %d (total %d bytes)\n",
                getPagesNumber() - 4 + getLastBlockValidPages(),
                (getPagesNumber() - 4 + getLastBlockValidPages()) * 4));

        return sb.toString();
    }

    public String getIC_InfoAsString() {

        StringBuilder sb = new StringBuilder();

        sb.append(String.format("ATQA: %02x %02x\n",
                getATQA()[1], // in reverse order according to ISO/IEC 14443-3 Type A
                getATQA()[0]));
        sb.append(String.format("SAK: %02x\n", getSAK()));

        if (!isVERSIONEmpty()) {
            sb.append("GET_VERSION:\n");
            sb.append("  ");
            for (int i = 0; i < getVersionInfo().length; i++) {
                sb.append(String.format("%02x ", getVersionInfo()[i]));
            }
            sb.append("\n");
        }

        if (getCountersNumber() > 0) {
            sb.append("Counters(hex):\n");
            for (int i = 0; i < getCountersNumber(); i++) {
                sb.append(String.format("  %01x: ", i));
                // LSB is 1-st
                for (int j = (getCountersNumber() - 1); j >= 0; j--) {
                    sb.append(String.format("%02x", getCounter(i)[j]));
                }
                sb.append("\n");
            }
        }

        if (!isSIGNEmpty()) {
            sb.append("READ_SIG:\n  ");
            for (int i = 0; i < getSIGN().length; i++) {
                sb.append(String.format("%02x", getSIGN()[i]));
                if ((i + 1) % 16 == 0 && (i + 1) != 32) sb.append("\n  ");
            }
            sb.append("\n");
        }

        String prefix = "android.nfc.tech.";
        sb.append("Android technologies: \n   ");
        for (int i = 0; i < getAndTechList().size(); i++) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append(getAndTechList().get(i).substring(prefix.length()));
        }
        sb.append('\n');

        return sb.toString();
    }

    public String getDetectedICTypeAsString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Chip manufacturer: ");
        sb.append(getManufName()).append("\n");
        sb.append("Chip: ");
        sb.append(getIC_TypeAsString()).append("\n");

        return sb.toString();
    }

    public String getDumpAsDetailedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("- - - Dump: - - -\n");
        for (int i=0; i < getPagesNumber() - ( 4 - getLastBlockValidPages() ); i++){
            sb.append(String.format("%02x:%s: ", i, getAccessAsString(getPageAssess(i))));
            for (int j=0; j < 4; j++){
                sb.append(String.format("%02x ", getPage(i)[j]));
            }
            sb.append("\n");
        }
        // warning, because fuzzy algorithm used
        if (getLastBlockValidPages() != 4)
            sb.append(String.format("---\n[!]Last block valid pages: %d\n",
                    getLastBlockValidPages()));
        sb.append("- - - Dump legend: - - -\n");
        sb.append(":u: - unknown\n");
        sb.append(":f: - factory locked\n");
        sb.append(":r: - read only\n");
        sb.append(":p: - partially writable\n");
        sb.append(":o: - One Time Programming (OTP)\n");
        sb.append(":w: - writable\n");
        sb.append(":a: - authentication require for write\n");
        sb.append(":s: - special\n");
        sb.append(":i: - reserved for internal use\n");

        return sb.toString();
    }

    public String getDumpAsSimpleString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getPagesNumber(); i++) {
            if (i == getPagesNumber() - (4 - getLastBlockValidPages())) {
                sb.append("-?-\n");
            }
            sb.append(String.format("%08x\n", getPageAsInt(i)));
        }


        return sb.toString();
    }
}
