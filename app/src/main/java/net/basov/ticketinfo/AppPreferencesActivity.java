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

        ListPreference ListPref = (ListPreference) findPreference("appLang");
        ListPref
                //.setSummary(sp.getString("appLang", "Some Default Text"));
                .setSummary(ListPref.getEntry());

        Preference dir_pref = (Preference) findPreference(getString(R.string.pk_dumps_directories));
        SharedPreferences defSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        dir_pref.setSummary(defSharedPref.getString(getString(R.string.pk_dumps_directories),""));
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
        SharedPreferences defSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (pref instanceof ListPreference) {
            ListPreference etp = (ListPreference) pref;
            pref.setSummary(etp.getEntry());
            String appLangPref = defSharedPref.getString(
                    getString(R.string.pk_app_lang),
                    getString(R.string.pref_lang_def)
            );
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

        String pref_key_search_dir = getString(R.string.pk_dumps_directories);

        if (pref_key_search_dir.equals(key)) {
            pref.setSummary(defSharedPref.getString(pref_key_search_dir,""));
        }
        defSharedPref.edit().putBoolean(getString(R.string.pk_pref_changed), true).apply();

    }


}


