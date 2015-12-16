package ru.valle.tickets.ui;

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

import java.util.ArrayList;

public class Dump {

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

    public Dump() {
        Pages = new ArrayList<byte[]>();
        Counters = new ArrayList<byte[]>();
        AndTechList = new ArrayList<String>();
        LastBlockValidPages = 4;
        SIGNisEmpty = true;
        VERSIONisEmpty = true;
        LastBlockVerifyed = false;
        //VersionInfo = new byte[8];
        //SIGN = new byte[32];
    }

    public int size() {
        return this.Pages.size();
    }

    public byte[] getPage(int n) {
        return this.Pages.get(n);
    }

    public void addPage(byte[] page) {
        this.Pages.add(page);
    }

    public void setSIGNisEmpty(boolean SIGNisEmpty) {
        this.SIGNisEmpty = SIGNisEmpty;
    }

    public void setSIGNisEmpty() {
        this.SIGNisEmpty = true;
    }

    public void setVERSIONisEmpty(boolean VERSIONisEmpty) {
        this.VERSIONisEmpty = VERSIONisEmpty;
    }

    public void setVERSIONisEmpty() {
        this.VERSIONisEmpty = true;
    }

    public boolean isSIGNEmpty() {
        return SIGNisEmpty;
    }

    public boolean isVERSIONEmpty() {
        return VERSIONisEmpty;
    }

    public boolean isEmpty(){
        if (this.Pages.size() == 0) {
            return true;
        }
        return false;
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


    public int getLastBlockValidPages() {
        if (!LastBlockVerifyed) validateLastBlockPages();
        return this.LastBlockValidPages;
    }

    public void validateLastBlockPages() {
    /*
    Attempt to exclude wrapped around information from last page.
    WARNING:
    Not strong algorithm. It is possible
    to write id on last page to brake it
    */
        this.LastBlockValidPages = 0; //last block page valid number
        for (int i = 0; i < 4; i++) {
            if (Pages.get( Pages.size() - 4 + i )[0] == Pages.get(0)[0]
                    && Pages.get( Pages.size() - 4 + i )[1] == Pages.get(0)[1]
                    && Pages.get( Pages.size() - 4 + i )[2] == Pages.get(0)[2]
                    && Pages.get( Pages.size() - 4 + i )[3] == Pages.get(0)[3]) {
                break;
            }
            LastBlockValidPages++;
        }
        LastBlockVerifyed = true;
    }

    public byte[] getATQA() {
        return this.ATQA;
    }

    public void setATQA(byte[] ATQA) {
        this.ATQA = ATQA;
    }

    public byte getSAK() {
        return this.SAK;
    }

    public void setSAK(byte SAK) {
        this.SAK = SAK;
    }

    public byte[] getVersionInfo() {
        return this.VersionInfo;
    }

    public void setVersionInfo(byte[] versionInfo) {
        this.VersionInfo = versionInfo;
    }

    public byte[] getSIGN() {
        return this.SIGN;
    }

    public void setSIGN(byte[] SIGN) {
        this.SIGN = SIGN;
    }

    public byte[] getCounter(int i) {
        return this.Counters.get(i);
    }

    public int getCountersNumber() {
        return this.Counters.size();
    }

    public void addCounter(byte[] counter) {
        Counters.add(counter);
    }

    public ArrayList<String> getAndTechList() {
        return AndTechList;
    }

    public void addAndTechList(String andTech) {
        AndTechList.add(andTech);
    }
}
