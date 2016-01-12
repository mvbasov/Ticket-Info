package ru.valle.tickets.ui;

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

 * Created by mvb on 12/28/15.
 */

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FileIO {

    static final String TAG = "tickets";

    public static boolean writeAutoDump(NFCaDump dump) {
        File fileW = null;
        Ticket ticket = new Ticket(dump);
        StringBuilder dName = new StringBuilder();
        boolean rc = false;

        dName.append(String.format("%010d", ticket.getTicketNumber()));
        if (ticket.isTicketFormatValid()) {
            if (ticket.getTicketClass() == Ticket.C_UNLIM_DAYS){
                dName.append("-ul");
                dName.append(String.format("-%03d",ticket.getTripSeqNumber()));
            } else {
                dName.append(String.format("-%02d", ticket.getPassesTotal()));
                dName.append(String.format("-%02d", ticket.getTripSeqNumber()));
                if (ticket.getTicketClass() == Ticket.C_90UNIVERSAL) {
                    dName.append(String.format(".%02d",ticket.getRelTransportChangeTimeMinutes()));
                    dName.append(String.format(".%1d",ticket.getT90ChangeCount()));
            }
            }
        } else {
            dName.append("-xx-xx");
        }

        //Log.d(TAG, String.format("%s\n", dName));

        StringBuilder dText = new StringBuilder();

        dText.append(dump.getDumpAsSimpleString());

        dText.append("+++\n");
        dText.append(dump.getIC_InfoAsString());

        dText.append("===\n");
        dText.append(dump.getDetectedICTypeAsString());

        dText.append("---\n");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dText.append("DD: ");
        Calendar c = Calendar.getInstance();
        dText.append(df.format(c.getTime()));
        dText.append("\n");

        //Log.d(TAG, String.format("%s\n", dText));

        FileOutputStream outputStream;
        File sdcard = MainActivity.getAppContext().getExternalFilesDir(null);
        String fNamePrefix = "AutoDumps/";
        String fName = "";
        try {
            fName = "/" + fNamePrefix + "/" + dName;
            fileW = new File(sdcard, fName + ".txt");
            if (!fileW.getParentFile().exists())
                fileW.getParentFile().mkdirs();
            if (!fileW.exists()) {
                outputStream = new FileOutputStream(fileW);
                outputStream.write(dText.toString().getBytes());
                outputStream.close();
                rc = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return rc;
    }
}
