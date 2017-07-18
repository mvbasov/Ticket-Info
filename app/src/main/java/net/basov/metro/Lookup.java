/**
 * The MIT License (MIT)

 Copyright (c) 2015,2016 Mikhail Basov

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
 
package net.basov.metro;

import java.util.Stack;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Class with static methods to get Moscow metro (underground, mcc and monorail)
 * stations information. SAX based.
 *
 * Created by mvb on 11/06/16.
 */


public class Lookup {

    public static String findDBts(String dataFileURI) {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			DefaultHandlerToSearch SearchHandlerForTS = new DefaultHandlerToSearch() {

				public void startElement(String uri, String localName, String qName,
										 Attributes attributes) throws SAXException {
					if (qName.equalsIgnoreCase("metro")) {
							result = attributes.getValue("ts");
						throw new MySAXTerminationException();
					}
				}
			};

			try {
				saxParser.parse(
					dataFileURI,
					SearchHandlerForTS
				);
			} catch (MySAXTerminationException done) {}
            return SearchHandlerForTS.result;
		} catch (Exception e) {
			e.printStackTrace();
		}
        return "";

    }

	public static String findDBprovider(String dataFileURI) {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			DefaultHandlerToSearch SearchHandlerForTS = new DefaultHandlerToSearch() {

				public void startElement(String uri, String localName, String qName,
										 Attributes attributes) throws SAXException {
					if (qName.equalsIgnoreCase("metro")) {
						result = attributes.getValue("provider");
						throw new MySAXTerminationException();
					}
				}
			};

			try {
				saxParser.parse(
						dataFileURI,
						SearchHandlerForTS
				);
			} catch (MySAXTerminationException done) {}
			return SearchHandlerForTS.result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";

	}

	public static String findStationById(String sId, String dataFileURI) {
        if (sId.length() > 0 && sId != null)
		    return findStationById(sId.substring(0, sId.length() - 1), sId.substring(sId.length() - 1), dataFileURI);
        else
            return "";
	}

    public static String findStationById(String sId, String eId, final String dataFileURI) {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			DefaultHandlerToSearch SearchHandlerForStationById = new DefaultHandlerToSearch() {

				boolean here;
				boolean done;
				boolean entMatch;
				private Stack<String> elementStack = new Stack<String>();
				String lineNeed = "";
				String stationInfo = "";

				public void startElement(String uri, String localName, String qName,
										 Attributes attributes) throws SAXException {
					this.elementStack.push(qName);
					if (qName.equalsIgnoreCase("st")
							&& attributes.getValue("id").equals(patternOne)) {
						String isUniq = attributes.getValue("uniq");
						if (isUniq != null && isUniq.equalsIgnoreCase("no")) {
							lineNeed += findLineById(attributes.getValue("ln"), dataFileURI);
						}
						here = true;
					}
					if (here
							&& qName.equals("e")
							&& attributes.getValue("id").equals(patternTwo)) {
						entMatch = true;
						stationInfo += findEntranceInfoById(attributes.getValue("et"), dataFileURI);
					} else {
						entMatch = false;
					}

					if (done) {
						if (lineNeed.length() > 0) result += "[" + lineNeed + "]";
						if (stationInfo.length() > 0) result += " (" + stationInfo + ")";
						throw new MySAXTerminationException();
					}
				}

				public void endElement(String uri, String localName,
									   String qName) throws SAXException {
					if (here && elementStack.peek().equals("st")) done = true;
					this.elementStack.pop();
				}

				public void characters(char ch[], int start, int length) throws SAXException {
					String value = new String(ch, start, length).trim();
					if (here && elementStack.peek().equals("n")) {
                        result = value;
                    }
					if (
                            here
                            && elementStack.peek().equals("e")
                            && patternTwo != null
                            && entMatch
                            && value != null
                            ) {

						if (stationInfo.length() > 0) {
							stationInfo += ". " + value;
						} else {
							stationInfo += value;
						}
                    }
				}
			};

			try {
                SearchHandlerForStationById.patternOne = sId;
				SearchHandlerForStationById.patternTwo = eId;
				saxParser.parse(
					dataFileURI,
					SearchHandlerForStationById
				);
			} catch (MySAXTerminationException done) {}
            return SearchHandlerForStationById.result;
		} catch (Exception e) {
			e.printStackTrace();
		}
        return "";

    }

	public static String findLineById(String lId, String dataFileURI) {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			DefaultHandlerToSearch SearchHandlerForLineById = new DefaultHandlerToSearch() {

				boolean here;
				boolean done;
				private Stack<String> elementStack = new Stack<String>();

				public void startElement(String uri, String localName, String qName,
										 Attributes attributes) throws SAXException {
					this.elementStack.push(qName);
					if (qName.equalsIgnoreCase("ln")
							&& attributes.getValue("id").equals(patternOne)) {
						here = true;
					}

					if (done)
						throw new MySAXTerminationException();
				}

				public void endElement(String uri, String localName,
									   String qName) throws SAXException {
					this.elementStack.pop();
					if (here) done = true;
				}

				public void characters(char ch[], int start, int length) throws SAXException {
					String value = new String(ch, start, length).trim();
					if (here && elementStack.peek().equals("ln")) {
                        result = value;
                    }
				}
			};

			try {
                SearchHandlerForLineById.patternOne = lId;
				saxParser.parse(
					dataFileURI,
					SearchHandlerForLineById
				);
			} catch (MySAXTerminationException done) {}
            return SearchHandlerForLineById.result;
		} catch (Exception e) {
			e.printStackTrace();
		}
        return "";
    }

    public static String findStationIdByTsId(String tsId, String dataFileURI) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            DefaultHandlerToSearch SearchHandlerForStationIdByTs = new DefaultHandlerToSearch() {

                boolean done;
                private Stack<String> elementStack = new Stack<String>();

                public void startElement(String uri, String localName, String qName,
                                         Attributes attributes) throws SAXException {
                    this.elementStack.push(qName);
                    if (qName.equalsIgnoreCase("ts")
                            && attributes.getValue("id").equals(patternOne)) {
                        result = attributes.getValue("st");
                        if(attributes.getValue("e")!=null)
                            result += attributes.getValue("e");
                        else
                            result += "0";

                        done = true;
                    }

                    if (done)
                        throw new MySAXTerminationException();
                }
            };
            try {
                SearchHandlerForStationIdByTs.patternOne = tsId;
                saxParser.parse(
                        dataFileURI,
                        SearchHandlerForStationIdByTs
                );
            } catch (MySAXTerminationException done) {}
            return SearchHandlerForStationIdByTs.result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String findStationInfoByTsId(String tsId, String dataFileURI) {
        return findStationById(findStationIdByTsId(tsId, dataFileURI), dataFileURI);
    }

	public static String findEntranceInfoById(String eT, String dataFileURI) {
        try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			DefaultHandlerToSearch SearchHandlerForEntranceInfo = new DefaultHandlerToSearch() {
				boolean here;
				boolean done;
				private Stack<String> elementStack = new Stack<String>();

				public void startElement(String uri, String localName, String qName,
										 Attributes attributes) throws SAXException {
					this.elementStack.push(qName);
					if (qName.equalsIgnoreCase("et")
						&& attributes.getValue("id").equals(patternOne)) {
						here = true;
					}

					if (done) {
						throw new MySAXTerminationException();
                    }
				}

				public void endElement(String uri, String localName,
									   String qName) throws SAXException {
					this.elementStack.pop();
					if (here) done = true;
				}

				public void characters(char ch[], int start, int length) throws SAXException {
					String value = new String(ch, start, length).trim();
					if (here && elementStack.peek().equals("et") && length > 0) {
                        result = value;
                    }
                    if (result == null) result = "";
				}
			};
			try {
                SearchHandlerForEntranceInfo.patternOne = eT;
				saxParser.parse(
					dataFileURI,
					SearchHandlerForEntranceInfo
				);
			} catch (MySAXTerminationException done) {}
            return SearchHandlerForEntranceInfo.result;
		} catch (Exception e) {
			e.printStackTrace();
		}
        return "";
    }

}

class MySAXTerminationException extends SAXException {}

class DefaultHandlerToSearch extends DefaultHandler {
	public String patternOne;
	public String patternTwo;
	public String result = "";
}


