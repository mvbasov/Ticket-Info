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


 * Created by mvb on 2/3/16.
 */

package net.basov.metro;

public class Turnstiles {

    public static String getStationByTurnstile(int turnstile){
        switch (turnstile) {
            case 10568:
                return "Проспект Мира (радиальная)";
            case 11531:
                return "Царицыно";
            case 13490: // [14] (left) (0001029499-1d-01)
            case 13507: // [15] (2-nd from left) (0001029499-1d-004)
            case 13496: // [16] (3-rd from left) (2487157655-60-49)
            case 13483: // [17] (4-th from left) (0001192751-3d-006)
            case 13477: // [18] (4-th from right) (0001192751-3d-010)
            case 13486: // [19] (3-rd from right) (0001192740-7d-001)
            case 13502: // [20] (2-nd from right) (0001192751-3d-001)
            case 13489: // [21] (right, baggage) (2523074756-02-01)
                return "Тропарёво (южный)"; // 2016-01-25 complete
            case 13354: // [6] (left, baggage) (2464659182-05-02)
            case 11226: // [7] (2-nd from left) (1013862735-01-01.00.1)
            case 13482: // [8] (3-rd from left) (2462677850-01-01)
            case 13506: // [9] (4-th from left) (2487157655-60-47)
            case 13487: // [10] (4-th from right) (2458927306-11-08)
            case 13499: // [11] (3-rd from right) (2487157655-60-45)
            case 12907: // [12] (2-nd from right) (2464659182-05-04)
            case 13488: // [13] (right) (2516440644-40-01)
                return "Тропарёво (северный)"; // 2016-01-06 complete
            case 13175: // [9] (left) (0001192751-3d-003)
            case 10754: // [10] (2-nd from left) (000119240-7d-015)
            case 13148: // [11] (3-rd from left) (2487157655-60-48)
            case 10763: // [12] (4-th from left) (0001029499-1d-003)
            case 13442: // [13] (4-th from right) (2518437516-20-01)
            case 10925: // [14] 3-rd from right) (2458927306-11-09)
            case 13083: // [15] (2-nd from right) (2462677851-05-04)
            case 13173: // [16] (right, baggage) (2517927572-05-05)
                return "Пролетарская (восточный)"; // 2016-01-20 complete
            case 12905: // [1] (left, baggage) (1016237832-02-01.10.3)
            case 13001: // [2] (2-nd from left) (1014908460-02-02.00.1)
            case 10787: // [3] (3-rd from left) (2375517573-20-02)
            case 12485: // [4] (4-th from left) (2375517573-20-04)
            case 12938: // [5] (4-th from right) (2375517573-20-11)
            case 13129: // [6] (3-rd from right) (2375517573-20-09)
            case 13145: // [7] (2-nd from right) (2375517573-20-07)
            case 11771: // [8] (right) (2523074756-02-02)
                return "Пролетарская (западный)"; // 2016-02-25 complete
            case 12537: // [1?] (left) (0001192740-7d-009)
            case 10781: // [2?] (2-nd from left) (0001192740-7d-014)
            case 11912: // [3?] (3-rd from left) (245892730-11-11)
            case 11139: // [4?] (4-th from left) (0001192613-3d-007)
            case 10382: // [5?] (5-th from left) (2487157644-60-53)
            case 10586: // [6?] (2-nd from right) (2487157655-60-51)
            case 11824: // [7?] (right, baggage) (0001192740-7d-012)   
                return "Крестьянская застава"; // 2016-02-10 complete
            case 12988: // [7] (left) (2487157655-60-44)
            case 12977: // [] (right, next after 13) (0001192740-7d-002)
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
            case 12215: // [2] (2-nd from left) (2378235665-20-02)
                return "Проспект Вернадского (южный)";              
            case 12493: // [1] (left, baggage) (2462677851-05-05)
            case 11556: // [2] (2-nd from left) (2458927306-11-02)
                return "Деловой центер";
            case 12254:
            case 12036:
                return "Фрунзенская";
            case 11211: // [11] (left in new block) (2375517573-20-03)
            case 12587: // [14] (right in new block, baggage) (2375517573-20-06)
            case 12473: // [2] (2-nd from left in old block) (2458927306-11-03)
            case 12147: // [10] (right in new block) (2378235665-20-15)
                return "Савёловская";
            case 11715:
            case 11482:
                return "Текстильщики";
            case 12227: // [1] (left) (0001192751-3d-004)
            case 13317: // [2] (2-nd from left) (0001192751-3d-007)
            case 12057: // [3] (3-td from left) (0001192751-3d-011)
            case 11952: // [4] (4-th from left) (0001192740-7d-003)
            case 13135: // [5] (5-th from left) (0001192613-3d-002)
            case 11967: // [6] (6-th from left) (0001192613-3d-006)
            case 10716: // [7] (7-th from left) (script on case)
            case 12099: // [12] (2-nd from right) (0001192751-3d-002)
            case 12024: // [13] (right) (0001029499-1d-002)
                return "Лубянка (южный) (Детский мир)";
            case 12193: // [14] (left) (2458927306-11-07)
            case 11922: // [15] (2-nd from left) (0001192740-7d-008)
            case 13322: // [16] (3-rd from left) (0001192740-7d-011)
            case 13276: // [18] (3-rd from right) (0001192613-3d-004)
            case 13323: // [19] (2-nd from right) (0001192740-7d-016)
            case 11965: // [20] (right) (0001192740-7d-005)
                return "Лубянка (северный)";
            case 11955: // [7] (2-nd from right) (2378235665-20-04)
            case 11839: // [8] (right) (2378235665-20-03)
                return "Чистые пруды";
            case 11760: // [?] (2516440644-40-22)
                return "Беляево (южный)";
            case 13539: // [] (left, baggage) (0001192751-3d-005)
                return "Румянцево (южный)";
            case 13558: // [] (left, baggage) (2522701976-02-01)
            case 13554: // [] (right, 11-th from left, baggage) (2522710198-02-01)
                return "Саларьево";
            case 11379: // [] (left, baggage) (0001192751-3d-009)
                return "Павелецкая (северный)";
            case 13492: // [] (left, baggage)
                return "Парк культуры (кольцевая, радиальная)";
            case 12958: // [1] (left) (2458927306-11-05)
                return "Авиамоторная";
            case 12265: // [1] (left) (2487157655-60-50)
            case 12067: // [7] (2-nd from right) (script on case)
            case 12059: // [8] (right) (0001192613-3d-005)
                return "Юго-Западная (северный)";
            case 12200: // [9] (left) (2458927306-11-10)
            case 12023: // [15] (2-nd from right) (scribe on case)
            case 10675: // [16] (right) (1014908460-02-01.05.2)
                return "Юго-Западная (южный)";
			case 10814: // [2] (2-nd from left) (2378235665-20-17)
			case 11223: // [3?] (3-rd from left) (0006981689-su-0005)
				return "Арбатская (арбатско-покровской)";

            default:
                return "";
        }
    }

}
