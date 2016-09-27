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
import java.util.Locale;

public class Lang {

    private static final String rusAlpha = "абвгдеёжзиыйклмнопрстуфхцчшщьэюя";
    private static final String rusAlphaUpper = "АБВГДЕЁЖЗИЫЙКЛМНОПРСТУФХЦЧШЩЬЭЮЯ";
    private static final String[] transAlpha = {"a", "b", "v", "g", "d", "e", "yo", "g", "z", "i", "y", "i",
            "k", "l", "m", "n", "o", "p", "r", "s", "t", "u",
            "f", "h", "tz", "ch", "sh", "sh", "'", "e", "yu", "ya"};

    public static String getNounCase(int n, int arrayID, Context c) {
        n = Math.abs(n);
        String[] cases = c.getResources().getStringArray(arrayID);
        String lang = Locale.getDefault().getLanguage();
        int form = n != 1 ? 2 : 0;
        if ("ru".equals(lang) || "ua".equals(lang) || "be".equals(lang)) {
            form = (n % 10 == 1 && n % 100 != 11 ? 0 : (n % 10) >= 2 && (n % 10) <= 4 && (n % 100 < 10 || n % 100 >= 20) ? 1 : 2);
        } else if ("tr".equals(lang)) {
            form = n > 1 ? 2 : 0;
        }
        return cases[form];
    }

    public static String transliterate(String string) {
        String lang = Locale.getDefault().getLanguage();
        if ("ru".equals(lang) || "ua".equals(lang) || "be".equals(lang)) {
            return string;
        } else {
            StringBuilder sb = new StringBuilder(string.length() * 3 / 2);
            for (int i = 0; i < string.length(); i++) {
                char ch = string.charAt(i);
                int ind = rusAlpha.indexOf(ch);
                if (ind >= 0) {
                    sb.append(transAlpha[ind]);
                } else if ((ind = rusAlphaUpper.indexOf(ch)) >= 0) {
                    String trans = transAlpha[ind];
                    sb.append(Character.toUpperCase(trans.charAt(0)));
                    if (trans.length() > 1) {
                        sb.append(trans.substring(1));
                    }
                } else {
                    sb.append(ch);
                }
            }
            return sb.toString();
        }
    }
}
