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

package net.basov.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ru.valle.tickets.ui.MainActivity;
import net.basov.nfc.NFCaDump;
import net.basov.metro.Ticket;

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
                dName.append(String.format("-%dd",ticket.getValidDays()));
                dName.append(String.format("-%03d",ticket.getTripSeqNumber()));
            } else {
				if (ticket.getType() == ticket.TO_VESB) {
					dName.append(String.format("-su"));
                    dName.append(String.format("-%04d", ticket.getTripSeqNumber()));
				} else {
                    dName.append(String.format("-%02d", ticket.getPassesTotal()));
                    dName.append(String.format("-%02d", ticket.getTripSeqNumber()));
                    if (ticket.getTicketClass() == Ticket.C_90UNIVERSAL) {
                        dName.append(String.format(".%02d",ticket.getRelTransportChangeTimeMinutes()));
                        dName.append(String.format(".%1d",ticket.getT90ChangeCount()));
                    }
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
	
	public static boolean ReadDump(NFCaDump dump, String fileName) {
		ArrayList<String> file_content = new ArrayList<String>();
		try {
			BufferedReader input = new BufferedReader(new FileReader(fileName));
			if (!input.ready()) {
				throw new IOException();
			}
			
			String line = "";
					
			while ((line = input.readLine()) != null) {
				file_content.add(line);
			}
			input.close();

		} catch (IOException e) {
			System.out.println(e);
			return false;
        }
		dump.setReadFrom(NFCaDump.READ_FROM_FILE);
		ParseDump(dump, file_content);
		return true;
	}
	
	// Parser states
	final static int PS_UNKNOWN = 0;
	final static int PS_DUMP = PS_UNKNOWN + 1;
	final static int PS_FAKE_PAGES = PS_UNKNOWN + 2;
	final static int PS_IC_INFO = PS_UNKNOWN + 3;
	final static int PS_IC_DECODE = PS_UNKNOWN + 4;
	final static int PS_REMARK = PS_UNKNOWN + 5;
	
	public static void ParseDump(NFCaDump dump, List<String> content) {
		int parserState = PS_DUMP;

		for (String line : content) {
			if(line.matches("-\\?-")){
				parserState = PS_FAKE_PAGES;
				continue;
			}
			if(line.matches("\\+\\+\\+")){
				parserState = PS_IC_INFO;
				continue;
			}
			if(line.matches("===")){
				parserState = PS_IC_DECODE;
				continue;
			}
			if(line.matches("---")){
				parserState = PS_REMARK;
				continue;
			}
			switch(parserState){
				case PS_FAKE_PAGES:
					//TODO: Check is it need to set dump.LastBlockValidPages (now private) here
					if (!ReadVerifyStoreDumpPage(dump, line)){
						parserState = PS_UNKNOWN;
					}
					//dump.appendRemark("F: :" + line + ":\n");
					break;
				case PS_DUMP:
					if (!ReadVerifyStoreDumpPage(dump, line)){
						parserState = PS_UNKNOWN;
					} 
					break;
				case PS_REMARK:
					dump.appendRemark(line+"\n");
					break;
				case PS_UNKNOWN:
					dump.appendRemark("U: :" + line + ":\n");
					break;
				//TODO: Reading of these parts of dump will be implemented
				case PS_IC_INFO:
				case PS_IC_DECODE:
					break;
				default:
			}
		}
	}
	
	private static Boolean ReadVerifyStoreDumpPage(NFCaDump dump, String line){
		Boolean rc = false;
		line.trim();
		line.replace(" ", "");
		if (line.matches("-?[0-9a-fA-F]+") && line.length() == 8){
			dump.addPage(hexStringToByteArray(line));
			rc = true;
		} else {
			dump.appendRemark("E: :" + line + ":\n");
			rc = false;
		}
		return rc;
	}

	public static byte[] hexStringToByteArray(String s) {
		// Grabed from http://stackoverflow.com/a/18714790
		int len = s.length();
		byte[] data = new byte[len/2];

		for(int i = 0; i < len; i+=2){
			data[i/2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
		}

		return data;
	}
	
}
