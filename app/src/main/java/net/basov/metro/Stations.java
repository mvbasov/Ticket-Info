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

/**
 * Information about station codes from very old, magnetic metro tickets:
 * http://www.metro.ru/stations/codes/
 */

public class Stations {
    public static String getStationByStationId(int stationId){
        String desc = "";
        int entrance = stationId%10;
        switch (stationId/10) {

//TODO: Need to check stations codes
//            desc += "Парк Победы (Арбатско-Покровская)";
//            desc += "Парк Победы (Калининская)";
/*
            case 165:
                desc += "Парк Победы (Калининская)";
                break;
*/

//TODO: Need to check stations codes
//            desc += "Каширская (Замоскворецкая)";
//            desc += "Каширская (Каховская)";
/*
            case 25:
                desc += "Каширская (Каховская)";
                break;
*/


/**
 * [01] Сокольническая линия
 */
            case 1:
                desc += "Бульвар Рокоссовского (Сокольническая)";
                break;
            case 2:
                desc += "Черкизовская";
                break;
            case 3:
                desc += "Преображенская площадь";
                break;
            case 4:
                desc += "Сокольники";
                break;
            case 5:
                desc += "Красносельская";
                break;
            case 6:
                desc += "Комсомольская (Сокольническая)";
                break;
            case 7:
                desc += "Красные ворота";
                break;
            case 8:
                desc += "Чистые пруды";
                break;
            case 9:
                desc += "Лубянка";
                break;
            case 10:
                desc += "Охотный ряд";
                break;
            case 11:
                desc += "Библиотека им. Ленина";
                break;
            case 12:
                desc += "Кропоткинская";
                break;
            case 13:
                desc += "Парк культуры (Сокольническая)";
                break;
            case 14:
                desc += "Фрунзенская";
                break;
            case 15:
                desc += "Спортивная";
                switch (entrance) {
                    case 2: //(0001983509-3d-012)
                        desc += " (южный)";
                        break;
                    default:
                        break;
                }
                break;
            case 16:
                desc += "Воробьёвы горы";
                switch (entrance) {
                    case 2: //(2580244002-20-19)
                        desc += " (южный)";
                        break;
                    default:
                        break;
                }
                break;
            case 17:
                desc += "Университет";
                break;
            case 18:
                desc += "Проспект Вернадского";
                break;
            case 19:
                desc += "Юго-Западная";
                switch (entrance) {
                    case 1: //(0001983509-3d-013)
                        desc += " (северный)";
                        break;
                    default:
                        break;
                }
                break;
            case 228:
                desc +=  "Тропарёво";
                switch (entrance) {
                    case 1: //(0001983509-3d-015)
                        desc += " (первый)";
                        break;
                    case 2: //(0001983509-3d-014)
                        desc += " (второй)";
                        break;
                    default:
                        break;
                }
                break;
            case 229:
                desc +=  "Румянцево?";
                switch (entrance) {
                    case 1:
                        desc += " (северный)";
                        break;
                    case 2:
                        desc += " (южный)";
                        break;
                    default:
                        break;
                }
                break;
            case 230:
                desc +=  "Саларьево";
                switch (entrance) {
                    case 1: //(0001964482-1d-002)
                        desc += "";
                        break;
                    default:
                        break;
                }
                break;

/**
 * [02] Замоскворецкая линия
 */
            case 20:
                desc += "Красногвардейская (Замоскворецкая)";
                break;
            case 21:
                desc += "Домодедовская";
                break;
            case 22:
                desc += "Орехово";
                break;
            case 23:
                desc += "Царицыно";
                break;
            case 24:
                desc += "Кантемировская";
                break;
            case 25:
                desc += "Каширская (Замоскворецкая)";
                break;
            case 28:
                desc += "Коломенская";
                break;
            case 29:
                desc += "Автозаводская (Замоскворецкая)";
                break;
            case 30:
                desc += "Павелецкая (Замоскворецкая)";
                break;
            case 31:
                desc += "Новокузнецкая (Замоскворецкая)";
                break;
            case 32:
                desc += "Театральная";
                break;
            case 33:
                desc += "Тверская";
                break;
            case 34:
                desc += "Маяковская";
                break;
            case 35:
                desc += "Белорусская (Замоскворецкая)";
                break;
            case 36:
                desc += "Динамо";
                break;
            case 37:
                desc += "Аэропорт";
                break;
            case 38:
                desc += "Сокол";
                break;
            case 39:
                desc += "Войковская";
                break;
            case 40:
                desc += "Водный стадион";
                break;
            case 41:
                desc += "Речной вокзал";
                break;

/**
 * [11а] Каховская линия
 */
            case 26:
                desc += "Каховская (Каховская)";
                break;
            case 27:
                desc += "Варшавская (Каховская)";
                break;
/**
 * [03] Арбатско-Покровская линия
 */
            case 211:
                desc += "Строгино";
                break;
            case 65:
                desc += "Молодежная";
                break;
            case 66:
                desc += "Крылатское";
                break;
            case 210:
                desc += "Кунцевская (Арбатско-Покровская)";
                break;
            case 165:
                desc += "Парк Победы (Арбатско-Покровская)";
                break;
            case 42:
                desc += "Киевская (Арбатско-Покровская)";
                break;
            case 43:
                desc += "Смоленская (Арбатско-Покровская)";
                break;
            case 44:
                desc += "Арбатская (Арбатско-Покровская)";
                break;
            case 45:
                desc += "Площадь Революции";
                break;
            case 46:
                desc += "Курская (Арбатско-Покровская)";
                break;
            case 47:
                desc += "Бауманская";
                break;
            case 48:
                desc += "Электрозаводская";
                break;
            case 49:
                desc += "Семеновская";
                break;
            case 50:
                desc += "Партизанская";
                break;
            case 51:
                desc += "Измайловская";
                break;
            case 52:
                desc += "Первомайская";
                break;
            case 53:
                desc += "Щелковская";
                break;

/**
 * [04] Филевская линия
 */
            case 188:
                desc += "Выставочная";
                break;
            case 189:
                desc += "Международная";
                break;
            case 54:
                desc += "Александровский сад";
                break;
            case 55:
                desc += "Арбатская (Филевская)";
                break;
            case 56:
                desc += "Смоленская (Филевская)";
                break;
            case 57:
                desc += "Киевская (Филевская)";
                break;
            case 58:
                desc += "Студенческая";
                break;
            case 59:
                desc += "Кутузовская (Филевская)";
                break;
            case 60:
                desc += "Фили";
                break;
            case 61:
                desc += "Багратионовская";
                break;
            case 62:
                desc += "Филевский парк";
                break;
            case 63:
                desc += "Пионерская";
                break;
            case 64:
                desc += "Кунцевская (Филевская)";
                break;
/**
 * [05] Кольцевая линия
 */
            case 67:
                desc += "Белорусская (Кольцевая)";
                break;
            case 68:
                desc += "Новослободская";
                break;
            case 69:
                desc += "Проспект Мира (Кольцевая)";
                break;
            case 70:
                desc += "Комсомольская (Кольцевая)";
                break;
            case 71:
                desc += "Курская (Кольцевая)";
                break;
            case 72:
                desc += "Таганская (Кольцевая)";
                break;
            case 73:
                desc += "Павелецкая (Кольцевая)";
                break;
            case 74:
                desc += "Добрынинская";
                break;
            case 75:
                desc += "Октябрьская (Кольцевая)";
                break;
            case 76:
                desc += "Парк культуры (Кольцевая)";
                break;
            case 77:
                desc += "Киевская (Кольцевая)";
                break;
            case 78:
                desc += "Краснопресненская";
                break;
/**
 * [08] Калининская линия
 */
            case 186:
                desc += "Новокосино";
                break;
            case 79:
                desc += "Новогиреево";
                switch (entrance) {
                    case 2: //?(2577405528-60-09)
                        desc += " (?)";
                        break;
                    default:
                        break;
                }
                break;
            case 80:
                desc += "Перово";
                break;
            case 81:
                desc += "Шоссе Энтузиастов (Калининская)";
                break;
            case 82:
                desc += "Авиамоторная";
                break;
            case 83:
                desc += "Площадь Ильича";
                break;
            case 84:
                desc += "Марксистская";
                break;
            case 85:
                desc += "Третьяковская (Калининская)";
                break;
/**
 * [06] Калужско-Рижская линия
 */
            case 86:
                desc += "Медведково";
                break;
            case 87:
                desc += "Бабушкинская";
                break;
            case 88:
                desc += "Свиблово";
                break;
            case 89:
                desc += "Ботанический сад (Калужско-Рижская)";
                break;
            case 90:
                desc += "ВДНХ";
                break;
            case 91:
                desc += "Алексеевская";
                break;
            case 92:
                desc += "Рижская";
                break;
            case 93:
                desc += "Проспект Мира (Калужско-Рижская)";
                break;
            case 94:
                desc += "Сухаревская";
                break;
            case 95:
                desc += "Тургеневская";
                break;
            case 96:
                desc += "Китай-город (Калужско-Рижская)";
                break;
            case 97:
                desc += "Третьяковская (Калужско-Рижская)";
                break;
            case 98:
                desc += "Октябрьская (Калужско-Рижская)";
                break;
            case 99:
                desc += "Шаболовская";
                break;
            case 100:
                desc += "Ленинский проспект";
                break;
            case 101:
                desc += "Академическая";
                break;
            case 102:
                desc += "Профсоюзная";
                break;
            case 103:
                desc += "Новые Черемушки";
                break;
            case 104:
                desc += "Калужская";
                break;
            case 105:
                desc += "Беляево";
                break;
            case 106:
                desc += "Коньково";
                switch (entrance) {
                    case 1: //(2580635469-60-02)
                        desc += " (северный)";
                        break;
                    case 2: //(2580244002-20-18)
                        desc += " (южный)";
                        break;
                    default:
                        break;
                }
                break;
            case 107:
                desc += "Теплый Стан";
                break;
            case 108:
                desc += "Ясенево";
                break;
            case 109:
                desc += "Новоясеневская";
                break;

/**
 * [07] Таганско-Краснопресненская линия
 */
            case 110:
                desc += "Выхино";
                break;
            case 111:
                desc += "Рязанский проспект";
                break;
            case 112:
                desc += "Кузьминки";
                break;
            case 113:
                desc += "Текстильщики";
                break;
            case 114:
                desc += "Волгоградский проспект";
                break;
            case 115:
                desc += "Пролетарская";
                switch (entrance) {
                    case 1: //(2580244002-20-17)
                        desc += " (западный)";
                        break;
                    case 2: //(new-2575358437-01-01)
                        desc += " (восточный)";
                        break;
                    default:
                        break;
                }
                break;
            case 116:
                desc += "Таганская (Таганско-Краснопресненская)";
                break;
            case 117:
                desc += "Китай-город (Таганско-Краснопресненская)";
                break;
            case 118:
                desc += "Кузнецкий мост";
                break;
            case 119:
                desc += "Пушкинская";
                switch (entrance) {
                    case 1: //(0001983509-3d-012)
                        desc += " (к Известиям)";
                        break;
                    default:
                        break;
                }
                break;
            case 120:
                desc += "Баррикадная";
                break;
            case 121:
                desc += "Улица 1905 года";
                break;
            case 122:
                desc += "Беговая";
                switch (entrance) {
                    case 1: //(2577096376-60-25)
                        desc += " (вход со стороны платформы)";
                        break;
                    default:
                        break;
                }
                break;
            case 123:
                desc += "Полежаевская";
                break;
            case 124:
                desc += "Октябрьское поле";
                break;
            case 125:
                desc += "Щукинская";
                break;
            case 126:
                desc += "Тушинская";
                break;
            case 127:
                desc += "Сходненская";
                break;
            case 128:
                desc += "Планерная";
                break;

/**
 * [09] Серпуховско-Тимирязевская линия
 */
            case 129:
                desc += "Алтуфьево";
                break;
            case 130:
                desc += "Бибирево";
                break;
            case 131:
                desc += "Отрадное";
                break;
            case 132:
                desc += "Владыкино (Серпуховско-Тимирязевская)";
                break;
            case 133:
                desc += "Петровско-Разумовская (Серпуховско-Тимирязевская)";
                break;
            case 134:
                desc += "Тимирязевская (Серпуховско-Тимирязевская)";
                break;
            case 135:
                desc += "Дмитровская";
                break;
            case 136:
                desc += "Савеловская";
                break;
            case 137:
                desc += "Менделеевская";
                break;
            case 138:
                desc += "Цветной бульвар";
                break;
            case 139:
                desc += "Чеховская";
                break;
            case 140:
                desc += "Боровицкая";
                break;
            case 141:
                desc += "Полянка";
                break;
            case 142:
                desc += "Серпуховская";
                break;
            case 143:
                desc += "Тульская";
                break;
            case 144:
                desc += "Нагатинская";
                break;
            case 145:
                desc += "Нагорная";
                break;
            case 146:
                desc += "Нахимовский проспект";
                break;
            case 147:
                desc += "Севастопольская";
                break;
            case 148:
                desc += "Чертановская";
                break;
            case 149:
                desc += "Южная";
                break;
            case 150:
                desc += "Пражская";
                break;
            case 162:
                desc += "Улица Академика Янгеля";
                break;
            case 163:
                desc += "Аннино";
                break;
            case 164:
                desc += "Бульвар Дмитрия Донского";
                break;
/**
 * [10] Люблинско-Дмитровская линия
 */
            case 166:
                desc += "Трубная (Люблинско-Дмитровская)";
                break;
            case 151:
                desc += "Чкаловская";
                break;
            case 152:
                desc += "Римская";
                break;
            case 153: //(2580244002-20-16) (single entrance)
                desc += "Крестьянская застава";
                break;
            case 161:
                desc += "Дубровка (Люблинско-Дмитровская)";
                break;
            case 154:
                desc += "Кожуховская";
                break;
            case 155:
                desc += "Печатники";
                break;
            case 156:
                desc += "Волжская";
                break;
            case 157:
                desc += "Люблино";
                break;
            case 158:
                desc += "Братиславская";
                break;
            case 159:
                desc += "Марьино";
                break;

/**
 * [12] Бутовская линия
 */
            case 191:
                desc += "Улица Старокачаловская";
                break;
            case 192:
                desc += "Улица Скобелевская";
                break;
            case 193:
                desc += "Бульвар адмирала Ушакова";
                break;
            case 194:
                desc += "Улица Горчакова";
                break;
            case 195:
                desc += "Бунинская аллея";
                break;

/**
 *  Московское Центральное Кольцо
 */
            case 540:
                desc += "Лужники";
                break;

            default:
                break;
        }
        return desc;
    }
}
