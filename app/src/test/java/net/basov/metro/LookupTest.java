package net.basov.metro;

import junit.framework.Assert;

import org.junit.Test;

import java.io.InputStream;

public class LookupTest {

    @Test
    public void testStationById() throws Exception {
        Assert.assertEquals(
                "Какая то",
                Lookup.getStationById("1", getInputStream())
        );
        Assert.assertEquals(
                "Tакая то",
                Lookup.getStationById("11", getInputStream())
        );
    }

    @Test
    public void testStationByIdWithEntr() throws Exception {
        Assert.assertEquals(
                "Какая то (Верхний)",
                Lookup.getStationById("1", "1", getInputStream())
        );
        Assert.assertEquals(
                "Какая то (Нижний)",
                Lookup.getStationById("1", "2", getInputStream())
        );
    }

    @Test
    public void testStationNotUniq() throws Exception {
        Assert.assertEquals(
                "Никакая [Каковская]",
                Lookup.getStationById("2", getInputStream())
        );
        Assert.assertEquals(
                "Никакая [Никаковская]",
                Lookup.getStationById("3", getInputStream())
        );
    }

    @Test
    public void testStationWithEntTypeAndRem() throws Exception {
        Assert.assertEquals(
                "Tакая то (Левый. До дому)",
                Lookup.getStationById("11", "1", getInputStream())
        );
        Assert.assertEquals(
                "Tакая то (Правый. До хаты)",
                Lookup.getStationById("11", "2", getInputStream())
        );
    }

    @Test
    public void testEmptyStation() throws Exception {
        Assert.assertEquals(
                "",
                Lookup.getStationById("5", getInputStream())
        );
    }

    @Test
    public void testStationIdByTurnstileId() throws Exception {
        Assert.assertEquals(
                "1",
                Lookup.getStationIdByTurnstailId("1101",getInputStream())
        );
        Assert.assertEquals(
                "2",
                Lookup.getStationIdByTurnstailId("1102",getInputStream())
        );
        Assert.assertEquals(
                "3",
                Lookup.getStationIdByTurnstailId("1103",getInputStream())
        );
        Assert.assertEquals(
                "11",
                Lookup.getStationIdByTurnstailId("1111",getInputStream())
        );
        Assert.assertEquals(
                "",
                Lookup.getStationIdByTurnstailId("9999",getInputStream())
        );

    }

    @Test
    public void testStationNameByTurnstileId() throws Exception {
        Assert.assertEquals(
                "Какая то",
                Lookup.getStationNameByTurnstileId("1101", getInputStream(), getInputStream())
        );
        Assert.assertEquals(
                "Никакая [Каковская]",
                Lookup.getStationNameByTurnstileId("1102", getInputStream(), getInputStream())
        );
        Assert.assertEquals(
                "Никакая [Никаковская]",
                Lookup.getStationNameByTurnstileId("1103", getInputStream(), getInputStream())
        );
        Assert.assertEquals(
                "Tакая то",
                Lookup.getStationNameByTurnstileId("1111", getInputStream(), getInputStream())
        );
        Assert.assertEquals(
                "",
                Lookup.getStationNameByTurnstileId("9999", getInputStream(), getInputStream())
        );

    }

    /**
     * Read file app/src/test/resources/metro.xml
     * @return InputStream
     */
   private InputStream getInputStream() {
        return  this.getClass().getClassLoader().getResourceAsStream("metro.xml");
    }

}