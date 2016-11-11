package net.basov.metro;

import junit.framework.Assert;

import org.junit.Test;

import java.io.InputStream;

public class LookupTest {

    @Test
    public void testStationById() throws Exception {
        Assert.assertEquals(
                "Какая то",
                Lookup.findStationById("10", getDataFileURI())
        );
        Assert.assertEquals(
                "Tакая то",
                Lookup.findStationById("110", getDataFileURI())
        );
    }
    @Test
    public void testStationByIdWithEntr() throws Exception {
        Assert.assertEquals(
                "Какая то (Верхний)",
                Lookup.findStationById("1", "1", getDataFileURI())
        );
        Assert.assertEquals(
                "Какая то (Нижний)",
                Lookup.findStationById("1", "2", getDataFileURI())
        );
    }
    @Test
    public void testStationNotUniq() throws Exception {
        Assert.assertEquals(
                "Никакая[Каковская]",
                Lookup.findStationById("20", getDataFileURI())
        );
        Assert.assertEquals(
                "Никакая[Никаковская]",
                Lookup.findStationById("30", getDataFileURI())
        );
    }
    @Test
    public void testStationWithEntTypeAndRem() throws Exception {
        Assert.assertEquals(
                "Tакая то (Левый. До дому)",
                Lookup.findStationById("11", "1", getDataFileURI())
        );
        Assert.assertEquals(
                "Tакая то (Левый. До дому)",
                Lookup.findStationById("111", getDataFileURI())
        );
        Assert.assertEquals(
                "Tакая то (Правый. До хаты)",
                Lookup.findStationById("11", "2", getDataFileURI())
        );
        Assert.assertEquals(
                "Tакая то (Правый. До хаты)",
                Lookup.findStationById("112", getDataFileURI())
        );
    }
    @Test
    public void testEmptyStation() throws Exception {
        Assert.assertEquals(
                "",
                Lookup.findStationById("50", getDataFileURI())
        );
    }
    @Test
    public void testStationIdByTurnstileId() throws Exception {
        Assert.assertEquals(
                "11",
                Lookup.findStationIdByTsId("1101",getDataFileURI())
        );
        Assert.assertEquals(
                "20",
                Lookup.findStationIdByTsId("1102",getDataFileURI())
        );
        Assert.assertEquals(
                "30",
                Lookup.findStationIdByTsId("1103",getDataFileURI())
        );
        Assert.assertEquals(
                "112",
                Lookup.findStationIdByTsId("1111",getDataFileURI())
        );
        Assert.assertEquals(
                "",
                Lookup.findStationIdByTsId("9999",getDataFileURI())
        );

    }

    @Test
    public void testStationNameByTurnstileId() throws Exception {
        Assert.assertEquals(
                "Какая то (Верхний)",
                Lookup.findStationInfoByTsId("1101", getDataFileURI())
        );
        Assert.assertEquals(
                "Никакая[Каковская]",
                Lookup.findStationInfoByTsId("1102", getDataFileURI())
        );
        Assert.assertEquals(
                "Никакая[Никаковская]",
                Lookup.findStationInfoByTsId("1103", getDataFileURI())
        );
        Assert.assertEquals(
                "Tакая то (Правый. До хаты)",
                Lookup.findStationInfoByTsId("1111", getDataFileURI())
        );
        Assert.assertEquals(
                "",
                Lookup.findStationInfoByTsId("9999", getDataFileURI())
        );

    }

    /**
     * Read file app/src/test/resources/metro.xml
     * @return InputStream
     */
    private InputStream getInputStream() {
        return  this.getClass().getClassLoader().getResourceAsStream("metro.xml");
    }
    private String getDataFileURI() {
        return String.valueOf(this.getClass().getClassLoader().getResource("metro.xml"));
    }

}