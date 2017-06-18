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

import net.basov.metro.Ticket;

import net.basov.ticketinfo.R;

public class Decode {

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
                return "3 " + Lang.getNounCase(3, R.array.trip_cases, c)+" ("+c.getString(R.string.sell_by_driver)+")";
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
                return "Zone B, " + "1 " + Lang.getNounCase(1, R.array.trip_cases, c)+" ("+c.getString(R.string.sell_by_driver) + ")";
            case Ticket.TN_GB2: // 2 pass, ground, Zone B (predicted)
                return "(p) Zone B, " + "2 " + Lang.getNounCase(2, R.array.trip_cases, c);
            case Ticket.TN_GAB1: // 1 pass, ground, Zone A and B (0000021180 script on ticket)
                return "Zone A and B, " + "1 " + Lang.getNounCase(1, R.array.trip_cases, c)+" ("+c.getString(R.string.sell_by_driver) + ")";
            case Ticket.TN_U1_DRV: // 1 pass, universal, sell by ground driver (0020905097)
                return c.getString(R.string.universal)+", 1 " + Lang.getNounCase(1, R.array.trip_cases, c)+" ("+c.getString(R.string.sell_by_driver) + ")";
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
            case Ticket.TN_UL1D: // 1 days, unlimited passes, 20 minutes between passes on same station (0001029499, with paper check)
                return "Universal, 1 day, unlimited passes";
            case Ticket.TN_UL3D: // 3 days, unlimited passes, 20 minutes between passes on same station (0001192751, with paper check)
                return "Universal, 3 days, unlimited passes";
            case Ticket.TN_UL7D: // 7 days, unlimited passes, 20 minutes between passes on same station (0001192740, with paper check)
                return "Universal, 7 days, unlimited passes";
            case Ticket.TN_90U1_G: // 1 pass, 90 minutes, universal, sell on ground (1013862735, with paper check)
                return "90 minutes, " + "1 " + Lang.getNounCase(1, R.array.trip_cases, c) + " (sell on ground)";
            case Ticket.TN_90U1: // 1 pass, 90 minutes, universal, sell in metro (1016236236, with paper check)
                return "90 minutes, " + "1 " + Lang.getNounCase(1, R.array.trip_cases, c) + " (sell in metro)";
            case Ticket.TN_90U2: // 2 passes, 90 minutes, universal, sell in metro (1016237832, with paper check)
                return "90 minutes, " + "2 " + Lang.getNounCase(1, R.array.trip_cases, c) + " (sell in metro)";
            case Ticket.TN_90U2_G: // 2 passes, 90 minutes, universal, sell on ground (1014908460, with paper check)
                return "90 minutes, " + "2 " + Lang.getNounCase(1, R.array.trip_cases, c) + " (sell on ground)";              
            case Ticket.TN_90U5: // 5 passes, 90 minutes, universal (1016363888, with paper check)
                return "90 minutes, " + "5 " + Lang.getNounCase(1, R.array.trip_cases, c);
            case Ticket.TN_90U11: // 11 passes, 90 minutes, universal (1016235763, with paper check)
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

