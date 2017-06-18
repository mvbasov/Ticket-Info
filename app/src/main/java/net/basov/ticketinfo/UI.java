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
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.basov.metro.Ticket;
import net.basov.nfc.NFCaDump;
import net.basov.util.StringTools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

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
        visibilityMap.put("w_msg", "vw_msg");
        visibilityMap.put("t_trips_left", "vt_trips_left");
        visibilityMap.put("t_trip_seq_number", "vt_trip");
        visibilityMap.put("t_station", "vt_station");
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
            header_json.put(field, StringTools.escapeCharsForJSON(content));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setDump(String content) {
        try {
            dump_json.put("d_content", StringTools.escapeCharsForJSON(content));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setTicket(String field, String content) {
        try {
            ticket_json.put(field, StringTools.escapeCharsForJSON(content));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (visibilityMap.containsKey(field) && (content.length() > 0))
            setTicketVisibility(visibilityMap.get(field));

    }

    public void setIC(String field, String content) {
        try {
            ic_json.put(field, StringTools.escapeCharsForJSON(content));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (visibilityMap.containsKey(field))
            setICVisibility(visibilityMap.get(field));

    }

    public void setWelcome(String field, String message) {
        try {
            welcome_json.put(field, StringTools.escapeCharsForJSON(message));
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
            // TODO: remove debug
            //Log.d("vvvvWelcome",welcome_json.getJSONObject("visibility").toString());
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
            // TODO: remove debug
            //Log.d("vvvvWelcome",welcome_json.getJSONObject("visibility").toString());
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
            // TODO: remove debug
            //Log.d("vvvvWelcome",welcome_json.getJSONObject("visibility").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void displayWelcome(final WebView wv) {
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(wv, url);

                view.evaluateJavascript("javascript:jreplace('" + welcome_json.toString() +"')",null);
                // TODO: remove debug
                //Log.d("tttttt",welcome_json.toString());
                wv.clearCache(true);
            }
        });
        wv.loadUrl("file:///android_asset/start.html");
    }

    public void displayUI(final WebView wv) {
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(wv, url);

                view.evaluateJavascript("javascript:jreplace('" + header_json.toString() +"')", null);
                view.evaluateJavascript("javascript:jreplace('" + ticket_json.toString() +"')", null);
                view.evaluateJavascript("javascript:jreplace('" + ic_json.toString() +"')",null);
                view.evaluateJavascript("javascript:jreplace('" + dump_json.toString() +"')",null);

                wv.clearCache(true);
                //TODO: remove debug
                //Log.d("hhhh", header_json.toString());
                //Log.d("tttt", ticket_json.toString());
                //Log.d("iiii", ic_json.toString());
            }
        });
        wv.loadUrl("file:///android_asset/webview_ui.html");
    }

    public void displayTicketInfo(NFCaDump d, Ticket t, WebView wv, Context c) {
        this.setTicketHeader("h_state", t.getTicketStateAsHTML());
        this.setTicketHeader("h_number", t.getTicketNumberAsHTML());
        this.setTicket("t_desc", Decode.descCardType(c, t.getTicketType()));
        if (t.getPassesLeft() != 0)
            this.setTicket("t_trips_left", t.getPassesLeftAsHTML());
        if ((t.getGateEntered() != 0) || (t.getEntranceEntered() != 0)) {
            this.setTicket("t_trip_seq_number", t.getTripSeqNumbetAsHTML());
            if (t.getGateEntered() != 0) {
                this.setTicket("t_station_id", t.getGateEnteredAsHTML());
                this.setTicket("t_station", t.getTurnstileDescAsHTML(c));
            } else if (t.getEntranceEntered() !=0) {
                this.setTicket("t_station_id", t.getEntrancrEnteredAsHTML());
                this.setTicket("t_station", t.getStationDescAsHTML(c));
            }
            this.setTicket("t_transport_type", t.getTransportTypeAsHTML(c));
        }
        this.setTicket("t_layout", t.getTicketLayoutAsHTML());
        this.setTicket("t_app_id", t.getTicketAppIDAsHTML());
        this.setTicket("t_type_id", t.getTicketTypeAsHTML());
        this.setTicket("t_hash", t.getHashAsHexString());
        this.setTicket("t_number", t.getTicketNumberAsHTML());
        this.setTicket("t_ic_uid", d.getUIDAsHTML());
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
