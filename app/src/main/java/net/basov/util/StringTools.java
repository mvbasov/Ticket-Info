package net.basov.util;

/**
 * Created by mvb on 6/18/17.
 */

public class StringTools {

    public static String escapeCharsForJSON(String src) {
        return src
                .replace("'", "&#39;")
                .replace("\"", "&#34;");
    }

}
