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

import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


//        header_json = "{"
//            +"\"h_number\":\"0003123881\","
//            +"\"h_state\":\"<font color=\\\\\"violet\\\\\">DebugAPP</font>\""
//            +"}";
//
//        ticket_json = "{"
//            +"\"t_valid_days\":\"1\","
//            +"\"t_from_datetime\":\"07.06.2017 16:03\","
//            +"\"t_to_datetime\":\"08.06.2017 16:03\","
//            +"\"t_start_use\":\"08.06.2017 at 00:00\","
//            +"\"t_ic_uid\":\"0123456789abcdef\""
//            +"}";
//
//        ticket_visibility_json = "{"
//            +"\"vt_note\":null,"
//            +"\"vt_trip\":null,"
//            +"\"vt_station\":null"
//            +"}";
//
//        ic_json = "{"
//            +"\"i_manufacturer\":\"JSC Micron Russia\","
//            +"\"i_ic_std_bytes\":\"164\","
//            +"\"i_names\":\""
//            +"  MIK1312ED\\\\n"
//            +"  aka К5016ВГ4Н4\\\\n"
//            +"  aka K5016XC1M1H4\","
//            +"\"i_read_pages\":\"41\","
//            +"\"i_read_bytes\":\"164\","
//            +"\"i_read_sig\":\""
//            +"  00000000000000000000000000000000\\\\n"
//            +"  00000000000000000000000000000000\","
//            +"\"i_tech\":\"nfcA\""
//            +"}";
//
//        ic_visibility_json = "{"
//                +"\"vi_get_version\":null,"
//                +"\"vi_read_sig\":null,"
//                +"\"vi_counters\":null"
//                +"}";
//
//        mainUI_WV.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(mainUI_WV, url);
//
//                view.loadUrl("javascript:jreplace('" + header_json +"')");
//
//                view.loadUrl("javascript:jreplace('" + ticket_json +"')");
//                view.loadUrl("javascript:jvisible('" + ticket_visibility_json +"')");
//
//                view.loadUrl("javascript:jreplace('" + ic_json +"')");
//                view.loadUrl("javascript:jvisible('" + ic_visibility_json +"')");
//
//                mainUI_WV.clearCache(true);
//            }
//        });
//
//        mainUI_WV.loadUrl("file:///android_asset/webview_ui.html");



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
            header_json.put(field, content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setDump(String content) {
        try {
            dump_json.put("d_content", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setTicket(String field, String content) {
        try {
            ticket_json.put(field, content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (visibilityMap.containsKey(field))
            setTicketVisibility(visibilityMap.get(field));

    }

    public void setIC(String field, String content) {
        try {
            ic_json.put(field, content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (visibilityMap.containsKey(field))
            setICVisibility(visibilityMap.get(field));

    }

    public void setWelcome(String field, String message) {
        try {
            welcome_json.put(field, message);
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
                Log.d("iiii", ic_json.toString());
            }
        });
        wv.loadUrl("file:///android_asset/webview_ui.html");
    }

}
