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

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import net.basov.metro.Ticket;
import net.basov.nfc.NFCaDump;

public class FileIO {

    static final String TAG = "tickets";

    public static boolean writeAutoDump(NFCaDump dump, Context c) {
        File fileW;
        boolean rc = false;
        Ticket ticket = new Ticket(dump);
 		String dumpFileName = Ticket.createAutoDumpFileName(ticket);
		String dumpContent = NFCaDump.getDumpAsString(dump);

        FileOutputStream outputStream;
        File sdcard;
        try {

            sdcard = c.getExternalFilesDir(null);
            fileW = new File(sdcard, dumpFileName + Ticket.FILE_EXT);
            if (!fileW.getParentFile().exists())
                fileW.getParentFile().mkdirs();
            if (!fileW.exists()) {
                outputStream = new FileOutputStream(fileW);
                outputStream.write(dumpContent.getBytes());
                outputStream.close();
                rc = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rc;
    }
	
	public static ArrayList<String> ReadDump(String fileName) {
		ArrayList<String> file_content = new ArrayList<String>();
		try {
			BufferedReader input = new BufferedReader(new FileReader(fileName));
			if (!input.ready()) {
				throw new IOException();
			}
			
			String line;
					
			while ((line = input.readLine()) != null) {
				file_content.add(line);
			}
			input.close();

		} catch (IOException e) {
			e.printStackTrace();
        }
		return file_content;
	}

    public static boolean ReadDump(NFCaDump dump, String fileName) {
        ArrayList<String> dump_content = ReadDump(fileName);
        if (dump_content.size() !=0) {
            dump.setReadFrom(NFCaDump.READ_FROM_FILE);
            NFCaDump.parseDump(dump, dump_content);
            return true;
        } else {
            return false;
        }
    }

	public static boolean appendRemarkToDump(File f, String rem) {
        boolean rc = false;

        if (f.exists()) {
            FileOutputStream outputRemarkStream = null;
            try {
                outputRemarkStream = new FileOutputStream(f, true);
                outputRemarkStream.write(rem.getBytes());
                outputRemarkStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            rc = true;
        }
        return rc;
    }

    public static File getFilesDir(Context c) {
        File filesDir;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            filesDir = c.getExternalFilesDir(null);
        } else {
            filesDir = c.getFilesDir();
        }
        return filesDir;
    }
}
