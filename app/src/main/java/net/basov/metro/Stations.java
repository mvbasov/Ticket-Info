package net.basov.metro;

/**
 * The MIT License (MIT)

 Copyright (c) 2016 Mikhail Basov

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


 * Created by mvb on 9/12/16.
 */
public class Stations {
    public static String getStationByStationId(int stationId){
        switch (stationId) {
//            case 0x08e9: //(0001983509-3d-015)
            case 2281: //(0001983509-3d-015)
                return "Тропарёво (первый)";
//            case 0x08ea: //(0001983509-3d-014)
            case 2282: //(0001983509-3d-014)
                return "Тропарёво (второй)";
//            case 0x00bf: //(0001983509-3d-013)
            case 191: //(0001983509-3d-013)
                return "Юго-Западная (северный)";
//            case 0x00a2: //(2580244002-20-19)
            case 162: //(2580244002-20-19)
                return "Ленинские горы (южный)";
//            case 0x0098: //(2579056354-02-02)
            case 152: //(2579056354-02-02)
                return "Спортивная (южный)";
//            case 0x04a7: //(0001983509-3d-012)
            case 1191: //(0001983509-3d-012)
                return "Тверская";
//            case 0x047f: //(2580244002-20-17)
            case 1151: //(2580244002-20-17)
                return "Пролетарская (западный)";
//            case 0x0480: //(new-2575358437-01-01)
            case 1152: //(new-2575358437-01-01)
                return "Пролетарская (восточный)";
//            case 0x05fb: //(2580244002-20-16)
            case 1531: //(2580244002-20-16)
                return "Крестьянская застава";
//            case 0x04c5: //?(2577096376-60-25)
            case 1221: //?(2577096376-60-25)
                return "Беговая (вход со стороны платформы)";
//            case 0x0425: //?(2580635469-60-02)
            case 1061: //?(2580635469-60-02)
                return "Коньково (северный)";
//            case 0x0426: //(2580244002-20-18)
            case 1062: //(2580244002-20-18)
                return "Коньково (южный)";
//            case 0x0318: //?(2577405528-60-09)
            case 792: //?(2577405528-60-09)
                return "Новогиреево(?)";
            default:
                return "";
        }
    }
}
