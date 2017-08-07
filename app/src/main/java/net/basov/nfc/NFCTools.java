package net.basov.nfc;

import android.content.Context;
import android.content.pm.FeatureInfo;

/**
 * Created by mvb on 8/7/17.
 */

public class NFCTools {

    /*
     * Sorce from https://stackoverflow.com/a/41226511
     */
    public static boolean deviceSupportsMifareClassic(Context mContext) {
        FeatureInfo[] info = mContext. getPackageManager().getSystemAvailableFeatures();
        for (FeatureInfo i : info) {
            String name = i.name;
            if (name != null && name.equals("com.nxp.mifare")) {
                return true;
            }
        }
        return false;
    }


}
