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
            case 13490: // [14] (left) (0001029499-1d-01)
            case 13507: // [15] (2-nd from left) (0001029499-1d-04)
            case 13496: // [16] (3-rd from left) (2487157655-60-49)
            case 13489: // [21] (right, baggage) (2523074756-02-01)
                return "Тропарёво (южный)";
            case 13354: // [6] (left, baggage) (2464659182-05-02)
            case 11226: // [7] (2-nd from left) (1013862735-01-01.00.1)
            case 13482: // [8] (3-rd from left) (2462677850-01-01)
            case 13506: // [9] (4-th from left) (2487157655-60-47)
            case 13487: // [10] (4-th from right) (2458927306-11-08)
            case 13499: // [11] (3-rd from right) (2487157655-60-45)
            case 12907: // [12] (2-nd from right) (2464659182-05-04)
            case 13488: // [13] (right) (2516440644-40-01)
                return "Тропарёво (северный)"; // 2016-01-06 complete
            //case 13442: // [9] (left) (2487157655-60-46)
            case 13001:
            case 10781: // [10] (2-nd from left) (2458927306-11-06)
            case 13148: // [11] (3-rd from left) (2487157655-60-48)
            case 10763: // [12] (4-th from left) (0001029499-1d-003)
            case 13442: // [13] (4-th from right) (2518437516-20-01)
            case 10925: // [14] 3-rd from right) (2458927306-11-09)
            case 13083: // [15] (2-nd from right) (2462677851-05-04)
            case 13173: // [16] (right, baggage) (2517927572-05-05)
                return "Пролетарская (восточный)";
            case 11771: // [8] (right) (2523074756-02-02)
            case 12905: // [1] (left, baggage) (1016237832-02-01.10.3)
                return "Пролетарская (западный)";
            case 12877: // [1] (left) (2458927306-11-01)
            case 13321: // [2] (2-nd from left) (2462677851-05-02)(2462677851-05-03)
            case 13405: // [3] (3-rd from left) (2458927306-11-04)
            case 12386: // [4] (4-th from left) (2469438311-60-47)
            case 13090: // [5] (5-th from left) (1016363888-05-01.45.3)
            case 12854: // [6] (right, before baggage) (1016237832-02-02.00.1)
            case 11440: // [] (right, baggage) (2464659182-05-05)
                return "Крестьянская застава"; // 2015-12-24 complete
            case 12988: // [7] (left) (2487157655-60-44)
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
            case 12493: // [1] (left, baggage)(2462677851-05-05)
            case 11556: // [2] (2-nd from left) (2458927306-11-02)
                return "Деловой центер";
            case 12254:
            case 12036:
                return "Фрунзенская";
            case 12473: // [?] (2-nd from left) (2458927306-11-03)
                return "Савёловская";
            case 11715:
            case 11482:
                return "Текстильщики";
            case 12024: // [13] (right) (0001029499-1d-002)
                return "Лубянка (западный)";
            case 12193: // [14] (left) (2458927306-11-07)
                return "Лубянка (восточный)";
            case 11760: // [?] (2516440644-40-22)
                return "Беляево (южный)";
            
            case 1230: // from original code
            case 2290: // from original code
                return "Парк культуры (радиальная)";
            case 12958: // [1] (left) (2458927306-11-05)
            case 1228: // from original code
            case 2211: // from original code
                return "Авиамоторная";
            case 2194: // from original code
                return "Юго-Западная";
            case 2233: // from original code
                return "Спортивная";

            default:
                return "";
        }
    }

    public static String descCardType(Context c, int ct) {
        switch (ct) {
            // Old layout 0x08
            case Ticket.TO_M1:
                return "1 " + Lang.getNounCase(1, R.array.trip_cases, c);
            case Ticket.TO_M2:
                return "2 " + Lang.getNounCase(2, R.array.trip_cases, c);
            case Ticket.TO_M3:
                return "3 " + Lang.getNounCase(3, R.array.trip_cases, c);
            case Ticket.TO_M4:
                return "4 " + Lang.getNounCase(4, R.array.trip_cases, c);
            case Ticket.TO_M5:
                return "5 " + Lang.getNounCase(5, R.array.trip_cases, c);
            case Ticket.TO_M10:
                return "10 " + Lang.getNounCase(10, R.array.trip_cases, c);
            case Ticket.TO_M20:
                return "20 " + Lang.getNounCase(20, R.array.trip_cases, c);
            case Ticket.TO_M60:
                return "60 " + Lang.getNounCase(60, R.array.trip_cases, c);
            case Ticket.TO_BAGGAGE_AND_PASS:
                return c.getString(R.string.baggage_and_pass);
            case Ticket.TO_BAGGAGE:
                return c.getString(R.string.baggage_only);
            case Ticket.TO_UL70:
                return c.getString(R.string.universal_ultralight_70);
            case Ticket.TO_VESB:
                return c.getString(R.string.vesb);

            // New layout 0x0d
            case Ticket.TN_G1: // 1 passes, ground (0002277252)(0002550204, with paper check)
                return "1 " + Lang.getNounCase(1, R.array.trip_cases, c);
            case Ticket.TN_G2: // 2 passes ground (0001585643, with paper check)
                return "2 " + Lang.getNounCase(2, R.array.trip_cases, c);
            case Ticket.TN_G3_DRV: // 3 passes, ground, sell by driver (0010197214)
                return "3 " + Lang.getNounCase(3, R.array.trip_cases, c)+" "+c.getString(R.string.sell_by_driver);
            case Ticket.TN_G5: // 5 passes ground (0000060635)(0002550205, with paper check)
                return "5 " + Lang.getNounCase(5, R.array.trip_cases, c);
            case Ticket.TN_G11: // 11 passes ground (0002551460, with paper check)
                return "11 " + Lang.getNounCase(5, R.array.trip_cases, c);
            case Ticket.TN_G20: // 20 passes, ground (0002275051)(0002688466, with paper check)
                return "20 " + Lang.getNounCase(60, R.array.trip_cases, c);
            case Ticket.TN_G40: // 40 passes, ground (0002551487, with paper check)
                return "40 " + Lang.getNounCase(60, R.array.trip_cases, c);
            case Ticket.TN_G60: // 60 passes, ground (0000108646, with paper check)
                return "60 " + Lang.getNounCase(60, R.array.trip_cases, c);
// TODO: Translate message
            case Ticket.TN_GB1_DRV: // 1 pass, ground, Zone B (0000021180 script on ticket)
                return "Zone B, " + "1 " + Lang.getNounCase(1, R.array.trip_cases, c)+" "+c.getString(R.string.sell_by_driver);
            case Ticket.TN_GB2: // 2 pass, ground, Zone B (predicted)
                return "(p) Zone B, " + "2 " + Lang.getNounCase(2, R.array.trip_cases, c);
            case Ticket.TN_U1_DRV: // 1 pass, universal, sell by ground driver (0020905097)
                return c.getString(R.string.universal)+", 1 " + Lang.getNounCase(1, R.array.trip_cases, c)+" "+c.getString(R.string.sell_by_driver);
            case Ticket.TN_U1: // 1 passes, universal (2462677850, with paper check)
                return c.getString(R.string.universal) + ", 1 " + Lang.getNounCase(1, R.array.trip_cases, c);
            case Ticket.TN_U2: // 2 passes, universal (2523074756, with paper check)
                return c.getString(R.string.universal) + ", 2 " + Lang.getNounCase(2, R.array.trip_cases, c);
            case Ticket.TN_U5: // 5 passes, universal (2462677851, with paper check)
                return c.getString(R.string.universal) + ", 5 " + Lang.getNounCase(5, R.array.trip_cases, c);
            case Ticket.TN_U11: // 11 passes, universal (2458927306, with paper check)
                return c.getString(R.string.universal) + ", 11 " + Lang.getNounCase(11, R.array.trip_cases, c);
            case Ticket.TN_U20: // 20 passes, universal (2518437516, with paper check)
                return c.getString(R.string.universal) + ", 20 " + Lang.getNounCase(11, R.array.trip_cases, c);
            case Ticket.TN_U40: // 40 passes, universal (2516440644, with paper check)
                return c.getString(R.string.universal) + ", 40 " + Lang.getNounCase(40, R.array.trip_cases, c);
            case Ticket.TN_U60: // 60 passes, universal (2478069296, confirmed lly)
                return c.getString(R.string.universal) + ", 60 " + Lang.getNounCase(60, R.array.trip_cases, c);
// TODO: Translate messages
            case Ticket.TN_UL1D:
                return "Universal, 1 day, unlimited passes, 20 minutes between passes";
            case Ticket.TN_90U1: // 1 pass, 90 minutes, universal (1013862735, with paper check)
                return "90 minutes, " + "1 " + Lang.getNounCase(1, R.array.trip_cases, c);
            case Ticket.TN_90U2: // 2 passes, 90 minutes, universal (1016237832, with paper check)
                return "90 minutes, " + "2 " + Lang.getNounCase(1, R.array.trip_cases, c);
            case Ticket.TN_90U5: // 5 passes, 90 minutes, universal (1016363888, with paper check)
                return "90 minutes, " + "5 " + Lang.getNounCase(1, R.array.trip_cases, c);
            case Ticket.TN_90U11: // 11 passes, 90 minutes, universal (1016235763, with paper sheck)
                return "90 minutes, " + "11 " + Lang.getNounCase(1, R.array.trip_cases, c);
            case Ticket.TN_90U20: // 20 passes, 90 minutes, universal (1016043594, with paper check)
                return "90 minutes, " + "20 " + Lang.getNounCase(1, R.array.trip_cases, c);
            case Ticket.TN_90U40: // 40 passes, 90 minutes, universal (1016043595, with paper check)
                return "90 minutes, " + "40 " + Lang.getNounCase(1, R.array.trip_cases, c);
            case Ticket.TN_90U60: // 60 passes, 90 minutes, universal (1015907198, confirmed Max)
                return "90 minutes, " + "60 " + Lang.getNounCase(1, R.array.trip_cases, c);


            default:
                return c.getString(R.string.unknown_ticket_category) + ": " + ct;
        }
    }


    public static String getAppIdDesc(Context c, int id){
        switch (id) {
            case Ticket.A_METRO:
                return c.getString(R.string.ticket_main_type_mosmetro);
            case Ticket.A_GROUND:
                return c.getString(R.string.ticket_main_type_mosground);
            case Ticket.A_SOCIAL:
                return c.getString(R.string.ticket_main_type_mossocial);
            case Ticket.A_METRO_LIGHT:
                return c.getString(R.string.ticket_main_type_mosmetrolight);
            case Ticket.A_UNIVERSAL:
                return c.getString(R.string.ticket_main_type_mosuniversal);
            default:
                return c.getString(R.string.ticket_main_type_unknown) + ": " + id;
        }
    }
}

