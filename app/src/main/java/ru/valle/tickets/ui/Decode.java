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
            case 10568:
                return "Проспект Мира (радиальная)";
            case 11531:
                return "Царицыно";
            case 13482:
            case 13354:
            case 13488:
                return "Тропарёво";
            case 13001:
            case 13442:
            case 13083:
            case 10781:
                return "Пролетарская";
            case 13321:
            case 12877:
            case 13405:
            case 12854:
                return "Крестьянская застава";
            case 12988:
                return "Марксистская";
            case 12519:
                return "Алтуфьево";
            case 10394:
            case 10396:
            case 13347:
            case 10632:
            case 13372:
            case 11109:
                return "Коньково";
            case 11501:
                return "Киевская";
            case 13033:
                return "Проспект Вернадского";
            case 12493:
            case 11556:
                return "Деловой центер";
            case 12254:
            case 12036:
                return "Фрунзенская";
            case 12473:
                return "Савёловская";
            case 1230:
            case 2290:
                return "Парк культуры (радиальная)";
            case 11715:
            case 11482:
                return "Текстильщики";
            case 12958:
            case 1228:
            case 2211:
                return "Авиамоторная";
            case 2194:
                return "Юго-Западная";
            case 2233:
                return "Спортивная";
            default:
                return "";
        }
    }

    public static String descCardType(Context c, int ct) {
        switch (ct) {
            // Old layout 0x08
            case 120:
                return "1 " + Lang.getNounCase(1, R.array.trip_cases, c);
            case 121:
                return "2 " + Lang.getNounCase(2, R.array.trip_cases, c);
            case 122:
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
            case 130:
                return c.getString(R.string.baggage_and_pass);
            case 131:
                return c.getString(R.string.baggage_only);
            case 149:
                return c.getString(R.string.universal_ultralight_70);
            case 150:
                return c.getString(R.string.vesb);

            // Ney layout 0x0d
            case 601: // !!!unconfirmed!!! 1 passes, ground, sell by driver (0002277252)
                return "(cg)" + "1 " + Lang.getNounCase(1, R.array.trip_cases, c)+" "+c.getString(R.string.sell_by_driver);
            case 602: // 2 passes ground (0001585643, with paper check)
                return "2 " + Lang.getNounCase(2, R.array.trip_cases, c);
            case 608: // 3 passes, ground, sell by driver (0010197214)
                return "3 " + Lang.getNounCase(3, R.array.trip_cases, c)+" "+c.getString(R.string.sell_by_driver);
            case 603: // 5 passes ground (0000060635)
                return "5 " + Lang.getNounCase(5, R.array.trip_cases, c);
            case 607: // 60 passes, ground (0001374174)
                return "60 " + Lang.getNounCase(60, R.array.trip_cases, c);
            case 410: // 1 pass, universal, sell by ground driver (0020905097)
                return c.getString(R.string.universal)+", 1 " + Lang.getNounCase(1, R.array.trip_cases, c)+" "+c.getString(R.string.sell_by_driver);
            case 411: // 1 passes, universal (2462677850)
                return c.getString(R.string.universal) + ", 1 " + Lang.getNounCase(1, R.array.trip_cases, c);
            case 412: // !!!unconfirmed!!!2 passes, universal (2507009879)
                return "(cg)" + c.getString(R.string.universal) + ", 2 " + Lang.getNounCase(2, R.array.trip_cases, c);
            case 413: // 5 passes, universal (2462677851)
                return c.getString(R.string.universal) + ", 5 " + Lang.getNounCase(5, R.array.trip_cases, c);
            case 415: // 11 passes, universal (2458927306)
                return c.getString(R.string.universal) + ", 11 " + Lang.getNounCase(11, R.array.trip_cases, c);
            case 417: // 40 passes, universal
                return c.getString(R.string.universal) + ", 40 " + Lang.getNounCase(40, R.array.trip_cases, c);
            case 418: // 60 passes, universal
                return c.getString(R.string.universal) + ", 60 " + Lang.getNounCase(60, R.array.trip_cases, c);
            case 421: // 1 pass, 90 minutes, universal
                return "(cg)" + "90 minutes, " + "1 " + Lang.getNounCase(1, R.array.trip_cases, c);
            case 422: // 2 passes, 90 minutes, universal (1016237832)
                return "90 minutes, " + "2 " + Lang.getNounCase(1, R.array.trip_cases, c);

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

