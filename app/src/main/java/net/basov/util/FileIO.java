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
import java.util.ArrayList;

import ru.valle.tickets.ui.MainActivity;
import net.basov.nfc.NFCaDump;

public class FileIO {

    static final String TAG = "tickets";

    public static boolean writeAutoDump(NFCaDump dump) {
        File fileW;
        boolean rc = false;

 		String dumpFileName = NFCaDump.createDumpFileName(dump);
		String dumpContent = NFCaDump.createDumpContent(dump);

        FileOutputStream outputStream;
        File sdcard = MainActivity.getAppContext().getExternalFilesDir(null);
        String fNamePrefix = "AutoDumps/";
        String fName;
        try {
            fName = "/" + fNamePrefix + "/" + dumpFileName;
            fileW = new File(sdcard, fName + ".txt");
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
	
	public static boolean ReadDump(NFCaDump dump, String fileName) {
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
			return false;
        }
		dump.setReadFrom(NFCaDump.READ_FROM_FILE);
		NFCaDump.parseDump(dump, file_content);
		return true;
	}
}
