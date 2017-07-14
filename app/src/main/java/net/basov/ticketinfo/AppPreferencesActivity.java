package net.basov.ticketinfo;

/**
 * Created by mvb on 6/22/17.
 */

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import java.util.Locale;

public class AppPreferencesActivity extends PreferenceActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        ListPreference ListPref = (ListPreference) findPreference("appLang");
        ListPref
                //.setSummary(sp.getString("appLang", "Some Default Text"));
                .setSummary(ListPref.getEntry());
    }

    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        Preference pref = findPreference(key);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (pref instanceof ListPreference) {
            ListPreference etp = (ListPreference) pref;
            pref.setSummary(etp.getEntry());
            String appLangPref = sharedPref.getString("appLang", "en");
            if ("appLang".equals(key)) {
                switch (appLangPref) {
                    case "ru":
                    case "en":
                        Locale locale = new Locale(appLangPref);
                        Locale.setDefault(locale);
                        Configuration config = new Configuration();
                        config.locale = locale;
                        getBaseContext().getResources().updateConfiguration(config,
                                getBaseContext().getResources().getDisplayMetrics());
                        break;
                    default:
                        break;
                }
                recreate();
            }
        }
        sharedPref.edit().putBoolean("changed", true).apply();

    }


}


