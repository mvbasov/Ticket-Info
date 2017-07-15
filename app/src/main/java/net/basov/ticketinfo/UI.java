/**
 * The MIT License (MIT)

 Copyright (c) 2017 Mikhail Basov

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

package net.basov.ticketinfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.webkit.WebView;

import net.basov.metro.Ticket;
import net.basov.nfc.NFCaDump;
import net.basov.util.TextTools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import ru.valle.tickets.ui.Decode;

/*
* Created by mvb on 6/17/17.
*/
public class UI {

    private final static String VISIBILITY = "visibility";

    private JSONObject welcome_json;
    private JSONObject header_json;
    private JSONObject ticket_json;
    private JSONObject ic_json;
    private JSONObject dump_json;

    private static final HashMap<String, String> visibilityMap;
    static
    {
        visibilityMap = new HashMap<String, String>();
        visibilityMap.put("w_debug", "vw_debug");
        visibilityMap.put("w_msg", "vw_msg");
        visibilityMap.put("t_debug", "tv_debug");
        visibilityMap.put("t_from_datetime", "vt_from_to_datetime");
        visibilityMap.put("t_to_datetime", "vt_from_to_datetime");
        visibilityMap.put("t_from_date", "vt_from_to_date");
        visibilityMap.put("t_to_date", "vt_from_to_date");
        visibilityMap.put("t_start_use_before", "vt_start_use_before");
        visibilityMap.put("t_start_use_till", "vt_start_use_till");
        visibilityMap.put("t_real_file_name", "vt_real_file_name");
        visibilityMap.put("t_file_name", "vt_file_note");
        visibilityMap.put("t_note_text", "vt_note");
        visibilityMap.put("t_trip_start_date", "vt_trip_time");
        visibilityMap.put("t_trip_start_time", "vt_trip_time");
        visibilityMap.put("t_trips_left", "vt_trips_left");
        visibilityMap.put("t_trip_seq_number", "vt_trip");
        visibilityMap.put("t_station", "vt_station");
        visibilityMap.put("t_90m_header_fake", "vt_90m_header");
        visibilityMap.put("t_90m_details", "vt_90m_details");
        visibilityMap.put("t_dump_crc16", "vt_dump_crc16");
        visibilityMap.put("t_parser_error", "vt_parser_error");
        visibilityMap.put("i_get_version", "vi_get_version");
        visibilityMap.put("i_counters", "vi_counters");
        visibilityMap.put("i_read_sig", "vi_read_sig");
    }

    /**
     * Only create object.
     * Fields doesn't automatically filed.
     */
    public UI() {
        this.dataClean();
    }

    public void dataClean () {
        welcome_json = new JSONObject();
        header_json = new JSONObject();
        ticket_json = new JSONObject();
        ic_json = new JSONObject();
        dump_json = new JSONObject();
    }

    public void setTicketHeader(String field, String content) {
        try {
            header_json.put(field, TextTools.escapeCharsForJSON(content));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setDump(String content) {
        try {
            dump_json.put("d_content", TextTools.escapeCharsForJSON(content));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setTicket(String field, String content) {
        try {
            ticket_json.put(field, TextTools.escapeCharsForJSON(content));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (visibilityMap.containsKey(field) && (content.length() > 0))
            setTicketVisibility(visibilityMap.get(field));

    }

    public void setIC(String field, String content) {
        try {
            ic_json.put(field, TextTools.escapeCharsForJSON(content));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (visibilityMap.containsKey(field))
            setICVisibility(visibilityMap.get(field));

    }

    public void setWelcome(String field, String message) {
        try {
            welcome_json.put(field, TextTools.escapeCharsForJSON(message));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (visibilityMap.containsKey(field))
            setWelcomeVisibility(visibilityMap.get(field));
    }

    private void setWelcomeVisibility(String name) {
        try {
            if (welcome_json.has(VISIBILITY)) {
                if (! welcome_json.getJSONObject(VISIBILITY).has(name)) {
                    welcome_json.getJSONObject(VISIBILITY).put(name,"");
                }
            } else {
                JSONObject vv = new JSONObject();
                welcome_json.put(VISIBILITY, vv);
                welcome_json.getJSONObject(VISIBILITY).put(name,"");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setTicketVisibility(String name) {
        try {
            if (ticket_json.has(VISIBILITY)) {
                if (! ticket_json.getJSONObject(VISIBILITY).has(name)) {
                    ticket_json.getJSONObject(VISIBILITY).put(name,"");
                }
            } else {
                JSONObject vv = new JSONObject();
                ticket_json.put(VISIBILITY, vv);
                ticket_json.getJSONObject(VISIBILITY).put(name,"");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setICVisibility(String name) {
        try {
            if (ic_json.has(VISIBILITY)) {
                if (! ic_json.getJSONObject(VISIBILITY).has(name)) {
                    ic_json.getJSONObject(VISIBILITY).put(name,"");
                }
            } else {
                JSONObject vv = new JSONObject();
                ic_json.put(VISIBILITY, vv);
                ic_json.getJSONObject(VISIBILITY).put(name,"");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void displayWelcomeByNFC(WebView wv) {
        Context c = wv.getContext();
        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(c);
        //TODO: remove debug
//        setWelcome("w_debug",
//                FileIO.getFilesDir(wv.getContext()).getAbsolutePath()
//        );
        if (adapter == null || ! adapter.isEnabled()){
            setWelcome("w_msg",
                    "<font color=\"darkred\">"
                    + c.getString(R.string.welcome_without_nfc)
                    + "<font>"
                );
        } else {
            setWelcome("w_msg",
                    "<font color=\"darkgreen\">"
                    + c.getString(R.string.welcome_with_nfc)
                    + "<font>"
            );
        }
        displayWelcome(wv);
    }

    public void displayHelp(final  WebView wv) {
        wv.setWebViewClient(new MyWebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(wv, url);
                //wv.clearCache(true);
            }
        });
        Context c = wv.getContext();
        wv.loadUrl("file:///android_asset/" + c.getString(R.string.help_ui_file));

    }

    public void displayWelcome(final WebView wv) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(wv.getContext());

        setWelcome("s_lang", sharedPref.getString("appLang", "en"));
        if (sharedPref.getBoolean("transliterateFlag", false)) {
            //setWelcome("s_translit", wv.getContext().getString(R.string.yes));
            //setWelcome("s_translit", "&#x2611;");
            setWelcome("s_translit", "<input type=\"checkbox\" disabled=\"disabled\" checked=\"checked\">");

        } else {
            //setWelcome("s_translit", wv.getContext().getString(R.string.no));
            //setWelcome("s_translit", "&#x2610;");
            setWelcome("s_translit", "<input type=\"checkbox\" disabled=\"disabled\">");
        }
        if (sharedPref.getBoolean("sendPlatformInfo", false)) {
            //setWelcome("s_sendinfo", wv.getContext().getString(R.string.yes));
            //setWelcome("s_sendinfo", "&#x2611;");
            setWelcome("s_sendinfo", "<input type=\"checkbox\" disabled=\"disabled\" checked=\"checked\">");
        } else {
            //setWelcome("s_sendinfo", wv.getContext().getString(R.string.no));
            //setWelcome("s_sendinfo", "&#x2610;");
            setWelcome("s_sendinfo", "<input type=\"checkbox\" disabled=\"disabled\">");
        }
        wv.setWebViewClient(new MyWebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(wv, url);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    view.evaluateJavascript("javascript:jreplace('" + welcome_json.toString() +"')",null);
                } else {
                    view.loadUrl("javascript:jreplace('" + welcome_json.toString() +"')",null);
                }
                wv.clearCache(true);
            }
        });
        Context c = wv.getContext();
        wv.loadUrl("file:///android_asset/" + c.getString(R.string.welcome_ui_file));
    }

    public void displayUI(final WebView wv) {
        wv.setWebViewClient(new MyWebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(wv, url);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    view.evaluateJavascript("javascript:jreplace('" + header_json.toString() + "')", null);
                    view.evaluateJavascript("javascript:jreplace('" + ticket_json.toString() + "')", null);
                    view.evaluateJavascript("javascript:jreplace('" + ic_json.toString() + "')", null);
                    view.evaluateJavascript("javascript:jreplace('" + dump_json.toString() + "')", null);
                } else {
                    view.loadUrl("javascript:jreplace('" + header_json.toString() + "')");
                    view.loadUrl("javascript:jreplace('" + ticket_json.toString() + "')");
                    view.loadUrl("javascript:jreplace('" + ic_json.toString() + "')");
                    view.loadUrl("javascript:jreplace('" + dump_json.toString() + "')");
                }
                wv.clearCache(true);
                wv.clearHistory();
                //TODO: remove debug
                //Log.d("hhhh", header_json.toString());
                //Log.d("tttt", ticket_json.toString());
                //Log.d("iiii", ic_json.toString());
            }
        });
        Context c = wv.getContext();
        wv.loadUrl("file:///android_asset/" + c.getString(R.string.ticket_ui_file));
    }

    public void displayTicketInfo(String appTitle, List<String> dumpContent, String fileName, String realFileName, String remark, WebView wv){
        NFCaDump dmp = new NFCaDump();
        Ticket ticket;
        if (dumpContent != null) {
            NFCaDump.parseDump(dmp, dumpContent);
            if (remark != null && remark.length() != 0)
                dmp.setRemark(remark);
            if (dmp.getDDD() != null) {
                ArrayList<Integer> tmpDump = new ArrayList<Integer>();

                for (int i = 0; i < 12; i++) {
                    tmpDump.add(dmp.getPageAsInt(i));
                }

                ticket = new Ticket(tmpDump, dmp.getDDD());
                ticket.setDDDRem(dmp.getDDDRem());
            } else {
                ticket = new Ticket(dmp);
            }

            ticket.setFileName(fileName);
            ticket.setRealFileName(realFileName);
            displayTicketInfo(dmp, ticket, wv);
        } else {
            setWelcome("w_header", appTitle);
            displayWelcomeByNFC(wv);
        }
    }

    public void displayTicketInfo(NFCaDump d, Ticket t, WebView wv) {
        Context c = wv.getContext();
        if (t.getTicketState() == Ticket.TS_UNKNOWN)
            t.detectTicketState();
        this.setTicketHeader("h_state", t.getTicketStateAsHTML(c));
        this.setTicketHeader("h_number", t.getTicketNumberAsString());
        if (t.isDebugTimeSet()) {
            String DDDrem;
            if (d.getDDDRem() != null) {
                DDDrem =
                        "<pre style=\"white-space: pre-wrap\">"
                        + d.getDDDRem()
                        + "</pre>";
            } else {
                DDDrem = "";
            }
            this.setTicket("t_debug",
                    "<font color=\"Violet\">Debug time is: "
                    + Ticket.DTF.format(t.getTimeToCompare().getTime())
                    + "</font>"
                    + DDDrem
            );
        }
        this.setTicket("t_desc", Decode.descCardType(c, t.getTicketType(), t.getTicketTypeVersion()));
        if (t.getValidDays() != 0) {
            this.setTicket("t_valid_days",t.getValidDaysAsString());
        }

        if (t.getIssued() != null) {
            if (t.getTicketClass() == Ticket.C_UNLIM_DAYS){
                if (t.getTripSeqNumber() != 0 && t.getUseTillDate() != null) {
                    Calendar toCal = (Calendar) t.getUseTillDate().clone();
                    toCal.add(Calendar.MINUTE, t.getFirstUseTime());
                    Calendar fromCal = (Calendar) toCal.clone();
                    fromCal.add(Calendar.DATE, -1 * t.getValidDays());
                    this.setTicket("t_from_datetime",
                            String.format("%s", Ticket.DTF.format(fromCal.getTime())));
                    this.setTicket("t_to_datetime",
                            String.format("%s", Ticket.DTF.format(toCal.getTime())));
                } else if (t.getStartUseTill() != null) {
                    this.setTicket("t_start_use_till",
                            Ticket.DTF.format(t.getStartUseTill().getTime())
                    );
                }

            } else {
                Calendar toCal = (Calendar) t.getIssued().clone();
                toCal.add(Calendar.DATE, t.getValidDays());
                this.setTicket("t_from_date",
                        String.format("%s",Ticket.DF.format(t.getIssued().getTime())));
                this.setTicket("t_to_date",
                        String.format("%s",Ticket.DF.format(toCal.getTime())));
            }
        }

        if (t.getTicketClass() != Ticket.C_UNLIM_DAYS) {
            if (t.getStartUseBefore() != null) {
                if (t.getIssued() != null) {
                    Calendar tmpCal = (Calendar) t.getIssued().clone();
                    tmpCal.add(Calendar.DATE, t.getValidDays());
                    if (t.getStartUseBefore().after(tmpCal))
                        this.setTicket("t_start_use_before", t.getStartUseBeforeAsString());
                } else
                    this.setTicket("t_start_use_before", t.getStartUseBeforeAsString());
            }
        }

        if (t.getStartUseTill() != null && t.getTicketClass() == Ticket.C_UNLIM_DAYS) {
            this.setTicket("t_start_use_till",
                    Ticket.DTF.format(t.getStartUseTill().getTime())
            );
        }

        if (t.getPassesLeft() > 0)
            this.setTicket("t_trips_left", t.getPassesLeftAsString());
        if ((t.getTurnstileEntered() != 0) || (t.getEntranceEntered() != 0)) {
            this.setTicket("t_trip_seq_number", t.getTripSeqNumbetAsString());
            if (t.getTripStart() != null) {
                this.setTicket("t_trip_start_date",
                        Ticket.DF.format(t.getTripStart().getTime()));
                this.setTicket("t_trip_start_time",
                        Ticket.TF.format(t.getTripStart().getTime()));
            }
            if (t.getTurnstileEntered() != 0) {
                this.setTicket("t_station_id", t.getTurnstileEnteredAsString());
                this.setTicket("t_station", t.getTurnstileDescAsHTML(c));
            } else if (t.getEntranceEntered() !=0) {
                this.setTicket("t_station_id", t.getEntranceEnteredAsString());
                this.setTicket("t_station", t.getStationDescAsHTML(c));
            }
            this.setTicket("t_transport_type", t.getTransportTypeAsHTML(c));
        }
        // TODO: move to web interface
        if (t.getTicketClass() == Ticket.C_90UNIVERSAL) {
            this.setTicket("t_90m_header_fake","  ");
            StringBuilder sb = new StringBuilder();
            if (t.getT90TripTimeLeft() > 0) {
                sb.append("  ");
                sb.append(c.getString(R.string.t90m_trip_time_left));
                sb.append(": ");
                sb.append(t.getReadableTime(t.getT90TripTimeLeft()));
            } else {
                sb.append("  ");
                sb.append(c.getString(R.string.t90m_trip_time_finished));
            }
            sb.append('\n');
            sb.append("  ");
            sb.append(c.getString(R.string.t90m_metro_trip_is));
            sb.append(" ");
            if (t.getT90MCount() > 0) {
                sb.append(c.getString(R.string.t90m_metro_used));
            } else {
                sb.append(c.getString(R.string.t90m_metro_trip_possible));
            }
            sb.append('\n');
            switch (t.getLayout()){
                case 13:
                    sb.append("  ");
                    sb.append(c.getString(R.string.t90m_ground_count));
                    sb.append(": ");
                    sb.append(t.getT90GCount());
                    sb.append('\n');
                    break;
                case 10:
                    break;
            }
            if (t.getT90RelChangeTime() != 0) {
                sb.append("  ");
                sb.append(c.getString(R.string.t90m_change_time));
                sb.append(": ");
                sb.append(Ticket.TF.format(t.getT90ChangeTime().getTime()));
                sb.append(String.format(" (%02d min)", t.getT90RelChangeTime()));
                sb.append('\n');
            }
            this.setTicket("t_90m_details", sb.toString());
        }
        this.setTicket("t_file_name", t.getFileName()+Ticket.FILE_EXT);
        if (t.getRealFileName() != null && !t.getRealFileName().equals(t.getFileName()+Ticket.FILE_EXT))
            this.setTicket("t_real_file_name", t.getRealFileName());
        if (d.getRemark().length() > 0)
            this.setTicket("t_note_text", d.getRemark());
        this.setTicket("t_layout", t.getTicketLayoutAsString());
        this.setTicket("t_app_id", t.getTicketAppIDAsString());
        this.setTicket("t_type_id", t.getTicketTypeAsString());
        this.setTicket("t_hash", t.getHashAsHexString());
        this.setTicket("t_number", t.getTicketNumberAsString());
        this.setTicket("t_ic_uid", d.getUIDAsString());
        if(!t.isTicketFormatValid())
            this.setTicket("t_dump_crc16", Ticket.getDumpCRC16AsHexString(t.getDump()));
        if (t.getParserError() != null && t.getParserError().length() != 0)
            this.setTicket(
                    "t_parser_error",
                    "<font color=\"red\">Parser error:\n  "
                    + t.getParserError()
                    +"</font>"
            );
        this.setTicket("i_manufacturer", d.getManufacturerAsHTML());
        this.setTicket("i_chip_names", d.getChipNamesAsHTML());
        this.setTicket("i_std_bytes", d.getChipCapacityAsHTML());
        this.setTicket("i_read_pages", d.getPagesReadAsHTML());
        this.setTicket("i_read_bytes", d.getBytesReadAsHTML());
        this.setTicket("i_uid_hi", d.getUIDHiasHTML());
        this.setTicket("i_uid_lo", d.getUIDLoasHTML());
        this.setTicket("i_bcc0", d.getBCC0AsHTML());
        this.setTicket("i_bcc1", d.getBCC1AsHTML());
        this.setTicket("i_crc_status", d.getUIDCRCStatusAsHTML());
        this.setTicket("i_otp", d.getOTPAsHTML());
        if (d.isSAKNotEmpty())
            this.setIC("i_sak",d.getSAKAsHTML());
        if (d.isATQANotEmpty())
            this.setIC("i_atqa",d.getATQAAsHTML());
        if (d.isVERSIONNotEmpty())
            this.setIC("i_get_version", d.getVERSIONAsHTML());
        if (d.isCountersNotEmpty())
            this.setIC("i_counters", d.getCountersAsHTML());
        if (d.isSIGNNotEmpty())
            this.setIC("i_read_sig", d.getSIGNAsHTML());
        if (d.isAtechListNotEmpty())
            this.setIC("i_tech",d.getATechAsHTML());
        this.setDump(d.getDumpAsHTMLString());
        this.displayUI(wv);
    }
}
