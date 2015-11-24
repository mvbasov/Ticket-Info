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

import android.content.Context;
import ru.valle.tickets.R;

public class Decode
{
    public static String getStationName(int turnstile){
        switch (turnstile) {
            case 1230:
            case 2290:
                return "Парк культуры (радиальная)";
            case 1228:
            case 2211:
                return "Авиамоторная";
            case 2194:
                return "Юго-Западная";
            case 2233:
                return "Спортивная";
            default:
                return "Has no information";
        }
    }

    public static String descCardType(Context c, int ct) {
        switch (ct) {
            case 120:
                return "1 " + Lang.getNounCase(1, R.array.trip_cases, c);
            case 121:
                return "2 " + Lang.getNounCase(2, R.array.trip_cases, c);
            case 122:  // metro
            case 608:  // ground
                return "3 " + Lang.getNounCase(3, R.array.trip_cases, c);
            case 123:
                return "4 " + Lang.getNounCase(4, R.array.trip_cases, c);
            case 126:
                return "5 " + Lang.getNounCase(5, R.array.trip_cases, c);
            case 127:
                return "10 " + Lang.getNounCase(10, R.array.trip_cases, c);
            case 128:
                return "20 " + Lang.getNounCase(20, R.array.trip_cases, c);
            case 129:
                return "60 " + Lang.getNounCase(60, R.array.trip_cases, c);
            case 410:
            case 411:
                return "Universal, 1 " + Lang.getNounCase(1, R.array.trip_cases, c);
            case 412:
                return "Universal, 2 " + Lang.getNounCase(2, R.array.trip_cases, c);
            case 413:
                return "Universal, 5 " + Lang.getNounCase(5, R.array.trip_cases, c);
            case 415:
                return "Universal, 11 " + Lang.getNounCase(11, R.array.trip_cases, c);
            case 418:
                return "Universal, 60 " + Lang.getNounCase(60, R.array.trip_cases, c);
            case 421:
                return "90 minutes, 1 " + Lang.getNounCase(1, R.array.trip_cases, c);
            case 130:
                return c.getString(R.string.baggage_and_pass);
            case 131:
                return c.getString(R.string.baggage_only);
            case 149:
                return c.getString(R.string.universal_ultralight_70);
            case 150:
                return c.getString(R.string.vesb);
            default:
                return c.getString(R.string.unknown_ticket_category) + ": " + ct;

        }
    }


    public static String getAppIdDesc(Context c, int id){
        switch (id) {
            case 262:
                return c.getString(R.string.ticket_main_type_mosmetro);
            case 264:
                return c.getString(R.string.ticket_main_type_mosground);
            case 266:
                return c.getString(R.string.ticket_main_type_mossocial);
            case 270:
                return c.getString(R.string.ticket_main_type_mosmetrolight);
            case 279:
                return c.getString(R.string.ticket_main_type_mosuniversal);
            default:
                return c.getString(R.string.ticket_main_type_unknown) + ": " + id;
        }
    }
}

