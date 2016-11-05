package net.basov.metro;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.InputStream;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Class with static methods to get Moscow metro (underground, mcc and monorail)
 * stations information.
 *
 * Created by mvb on 10/13/16.
 */
public class Lookup {
    /**
     * Wrapper to old Stations class
     *
     * @param stationId
     * @param inputStream
     * @return Station name and discription
     */
    public static String getStationByIdFromTicket(int stationId,  InputStream inputStream) {
        return getStationById(
                (stationId / 10) + "",
                (stationId % 10) + "",
                inputStream
                );
    }


    /**
     * Lookup to asset file metro xml and find station name by station ID
     *
     * @param sId Station ID
     * @param inputStream Context to get resources from asset
     * @return Station name with line if not uniq or empty string if not exists
     */
    public static String getStationById(String sId, InputStream inputStream) {
        return getStationById(sId, null, inputStream);
    }

    /**
     * Lookup to asset file metro xml and find station entrance information
     * by station and entrance ID
     *
     * @param sId Station ID
     * @param eId Entrance ID
     * @param inputStream Context to get resources from asset
     * @return Station entance description with comment if exist.
     */
    public static String getStationById(String sId, String eId, InputStream inputStream) {
        String ret = "";
        Boolean notUniq = false;
        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();
        try {
            Element metro = (Element) xPath.evaluate(
                    "/metro",
                    new InputSource(inputStream),
                    XPathConstants.NODE
            );

            NodeList stName = (NodeList) xPath.evaluate(
                    String.format(
                            "/metro/sts/st[@id='%s']/n/text()", sId
                    ),
                    metro,
                    XPathConstants.NODESET
            );
            if (stName.getLength() == 0) return ret;
            ret += stName.item(0).getTextContent();

            NodeList stLine = (NodeList) xPath.evaluate(
                    String.format(
                            "/metro/sts/st[@id='%s']/@ln", sId
                    ),
                    metro,
                    XPathConstants.NODESET
            );
            String lnIdx = stLine.item(0).getTextContent();

            NodeList stUniq = (NodeList) xPath.evaluate(
                    String.format(
                            "/metro/sts/st[@id='%s']/@uniq", sId
                    ),
                    metro,
                    XPathConstants.NODESET
            );
            if ( stUniq.getLength() > 0 )
                notUniq = stUniq.item(0).getTextContent().equalsIgnoreCase("no");

            if (notUniq) {
                NodeList lines = (NodeList) xPath.evaluate(
                        String.format(
                                "/metro/lns/ln[@id='%s']/text()", lnIdx
                        ),
                        metro,
                        XPathConstants.NODESET
                );
                ret += " [" + lines.item(0).getTextContent() + "]";
            }

            if (eId != null) {
                String eInfo = "";
                NodeList eType = (NodeList) xPath.evaluate(
                        String.format(
                                "/metro/sts/st[@id='%s']/e[@id='%s']/@et", sId, eId
                        ),
                        metro,
                        XPathConstants.NODESET
                );
                String eTypeId;
                if (eType.getLength() > 0) {
                    eTypeId = eType.item(0).getTextContent();
                    NodeList eTypeString = (NodeList) xPath.evaluate(
                            String.format(
                                    "/metro/ets/et[@id='%s']/text()", eTypeId
                            ),
                            metro,
                            XPathConstants.NODESET
                    );
                    if (eTypeString.getLength() > 0)
                        eInfo += eTypeString.item(0).getTextContent();

                }
                NodeList eNote = (NodeList) xPath.evaluate(
                        String.format(
                                "/metro/sts/st[@id='%s']/e[@id='%s']/text()", sId, eId
                        ),
                        metro,
                        XPathConstants.NODESET
                );

                if (eNote.getLength() > 0) {
					if (eInfo.length() > 0) eInfo +=". ";
                    eInfo += eNote.item(0).getTextContent();
				}
				if (eInfo.length() > 0)					
                	ret += "(" + eInfo + ")";

            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
        }
        return ret;
    }


    /**
     * Get station ID by turnstile ID
     * @param tId Turnstile ID
     * @param inputStream Context to get resources from asset
     * @return Station ID
     */
    public static String getStationIdByTurnstailId(String tId, InputStream inputStream) {
        String ret = "";
        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();
        try {
            NodeList stId = (NodeList) xPath.evaluate(
                    String.format(
                            "/metro/tss/ts[@id='%s']/@st", tId
                    ),
                    new InputSource(inputStream),
                    XPathConstants.NODESET
            );
            if (stId.getLength() == 0) return "";
            ret += stId.item(0).getTextContent();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Get station name by turnstile ID
     * @param tId Turnstile ID
     * @param inputStream1 Context to get resources from asset
     * @return Station name as {@link Lookup#getStationById(String, InputStream)}
     */
    public static String getStationNameByTurnstileId(String tId, InputStream inputStream1, InputStream inputStream2) {
// TODO: It is very ugly :( Need to find way to reuse InputStream
        return getStationById(
                getStationIdByTurnstailId(tId, inputStream1),
                inputStream2
        );
    }
}
