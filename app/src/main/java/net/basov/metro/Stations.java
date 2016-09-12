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
            case 0x08e9:
                return "Тропарёво (первый)";
            case 0x08ea:
                return "Тропарёво (второй)";
            case 0x00bf:
                return "Юго-Западная (северный)";
            case 0x04a7:
                return "Тверская";
            case 0x0480:
                return "Пролетарская (западный?)";
            default:
                return "";
        }
    }
}
