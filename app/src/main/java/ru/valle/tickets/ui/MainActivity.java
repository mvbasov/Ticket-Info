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

package ru.valle.tickets.ui;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import net.basov.metro.Ticket;
import net.basov.nfc.NFCaDump;
import net.basov.util.FileIO;

import java.io.IOException;
import java.util.ArrayList;

import ru.valle.tickets.R;

public final class MainActivity extends Activity {

    static final String TAG = "tickets";

    private TextView text;
    private NfcAdapter adapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] filters;
	private NFCaDump d;
    private String[][] techList;
    private static Context c;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        c = this;
		d = new NFCaDump();
        setContentView(R.layout.main);
        text = (TextView) findViewById(R.id.body);
        try {

            /**
             * AIDE has limited support of gradle.
             * If program compiled by AndroidStudio R.string.git_describe is set by gradle.
             * If program compiled by AIDE this resource doesn't exist.
             */
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            int git_describe_id = getResources().getIdentifier("git_describe", "string", getPackageName());
            String title;
            if (git_describe_id == 0)
                title = getResources().getString(R.string.app_name)
                        + " "
                        + pInfo.versionName
                        + "-AIDE";
            else {
                String git_describe = getResources().getString(git_describe_id);
                if (!git_describe.isEmpty())
                    title = getResources().getString(R.string.app_name)
                            + " "
                            + git_describe;
                else
                    title = getResources().getString(R.string.app_name)
                            + " "
                            + pInfo.versionName;
            }
            this.setTitle(title);

        } catch (Throwable th) {
            Log.e(TAG, "get package info error", th);
        }
		
        onNewIntent(getIntent());
// TODO: Check NFC is switched on  
        adapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        try {
            filter.addDataType("*/*");
        } catch (MalformedMimeTypeException e) {
            Log.e(TAG, "Add data type fail", e);
        }
        filters = new IntentFilter[]{filter};
        techList = new String[][]{new String[]{NfcA.class.getName()}};
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.enableForegroundDispatch(this, pendingIntent, filters, techList);
    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.disableForegroundDispatch(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_ic_info:
                Toast.makeText(this, "IC info", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_dump:
                Toast.makeText(this, "Dump", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_debug:
                Toast.makeText(this, "Debug", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (intent != null && intent.getAction().equals(NfcAdapter.ACTION_TECH_DISCOVERED)) {
            try {
                Bundle extras = intent.getExtras();
                Tag tag = (Tag) extras.get(NfcAdapter.EXTRA_TAG);

                final String[] techList = tag.getTechList();

                final NfcA nfca = NfcA.get(tag);
                text.setText(getString(R.string.ticket_is_reading));
                new AsyncTask<NfcA, Void, NFCaDump>() {

                    @Override
                    protected NFCaDump doInBackground(NfcA... paramss) {
                        try {
							d = new NFCaDump();

                            nfca.connect();

							d.setReadFrom(NFCaDump.READ_FROM_NFC);
                            d.readATQA(nfca);
                            d.readSAK(nfca);
                            d.readPages(nfca);

                            nfca.close();
/*
                            If try to read Mifare 1K on devices without support this tag type 0 blocks returned
                            If try to read using NfcA library Mifare 1K on devices with support only one block with one byte returned
*/
                            if (d.isPagesEmpty())
                                return null;

                            if ((d.getPage(0)[0] == (byte)0x04 && d.getPagesNumber() == 20) ||     // MF0ULx1 (80 bytes)
                                    (d.getPage(0)[0] == (byte)0x34 && d.getPagesNumber() == 44)) {  // MIK1312ED (164 bytes)

                                nfca.connect();
                                // Separate connect()/close() block of calls
                                // which supported not by all cards

                                d.readVERSION(nfca);
                                d.readCounters(nfca);
                                d.readSIGN(nfca);

                                nfca.close();
                            }

                            for (String aTechList : techList) {
                                d.addAndTechList(aTechList);
                            }
                            return d;
                        } catch (IOException ie) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(NFCaDump dump) {
                        if (dump != null) {
                            Ticket t = new Ticket(dump);
                            StringBuilder sb = new StringBuilder();

                            if (dump.getPagesNumber() < 12) {

                                sb.append("!!! Card read partially.\n");
                                sb.append("!!! Decoding ticket information impossible.\n");
                                sb.append("!!! Try to read again.\n");
                                sb.append("- - - -\n");

                            } else {
                                if (FileIO.writeAutoDump(dump)) {
                                    sb.append("Dump saved\n");
                                    Toast toast = Toast.makeText(c, "Dump saved.", Toast.LENGTH_LONG);
                                    toast.show();
                                    sb.append("\n- - - -\n");
                                } else {
                                    try {
                                        NFCaDump d_tmp = new NFCaDump();
                                        String storage = MainActivity.getAppContext()
                                                .getExternalFilesDir(null)
                                                .getAbsolutePath();
                                        String fName = storage +
                                                "/AutoDumps/" +
                                                Ticket.createDumpFileName(t) +
                                                ".txt";
                                        if (FileIO.ReadDump(d_tmp, fName)) {
                                            sb.append("Existing dump comment:\n");
                                            sb.append(d_tmp.getRemark());
                                            sb.append("\n- - - -\n");
                                        }
                                    } catch (NullPointerException e) {
                                        sb.append("Dump exist but not readable\n");
                                    }
                                }
                                sb.append(t.getTicketAsString(c));
                            }

                            sb.append(dump.getMemoryInfoAsString());
                            sb.append(dump.getUIDCheckAsString());
                            sb.append(dump.getIC_InfoAsString());
                            sb.append(dump.getDetectedICTypeAsString());
                            sb.append(dump.getDumpAsDetailedString());
                            text.setText(sb.toString());

                        } else {
                            text.setText(getString(R.string.ticket_read_error));
                        }
                    }
                }.execute(nfca);

            } catch (Throwable th) {
                text.setText(getString(R.string.ticket_read_error));
                Log.e(TAG, "read err", th);
            }
        } else if((intent.getAction().equals(Intent.ACTION_SEND)
				|| intent.getAction().equals(Intent.ACTION_VIEW))
				&& intent.getType().startsWith("text/")){
					
			Uri rcvUri = null;
			
			if (intent.getAction().equals(Intent.ACTION_SEND)) {
				rcvUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
			} else if (intent.getAction().equals(Intent.ACTION_VIEW)){
				rcvUri = intent.getData();
			}
			
			if (rcvUri != null) {
				FileIO.ReadDump(d, rcvUri.getPath());
				if (d.getReadFrom()==NFCaDump.READ_FROM_FILE) {
					StringBuilder sb = new StringBuilder();
					Ticket t;
					if (d.getDDD() != null) {
						ArrayList<Integer> tmpDump = new ArrayList<Integer>();

						for (int i = 0; i < 12; i++) {
							tmpDump.add(d.getPageAsInt(i));
						}
						
						t = new Ticket(tmpDump, d.getDDD());
                        t.setDDDRem(d.getDDDRem());
					} else {
						t = new Ticket(d);
					}
					if (d.getRemark().length() != 0){
						sb.append("File: ");
                        sb.append(rcvUri.getLastPathSegment());
                        sb.append("\n");
						sb.append(d.getRemark());
						sb.append("\n- - - -\n");
					}
					if (d.getPagesNumber() < 12) {
						sb.append("!!! Dump partial.\n");
						sb.append("!!! Decoding ticket information impossible.\n");
						sb.append("- - - -\n");
					} else {
						sb.append(t.getTicketAsString(c));
					}
					sb.append(d.getMemoryInfoAsString());
					sb.append(d.getUIDCheckAsString());
					sb.append(d.getIC_InfoAsString());
					sb.append(d.getDetectedICTypeAsString());
					sb.append(d.getDumpAsDetailedString());
					text.setText(sb.toString());
				}
			}
		} else {
			text.setText(getString(R.string.ticket_disclaimer));
		}
    }

    public static Context getAppContext() {
        return c;
    }
}
