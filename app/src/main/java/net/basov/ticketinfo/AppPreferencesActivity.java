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

import java.util.Arrays;
import java.util.Locale;

public class AppPreferencesActivity extends PreferenceActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        // Set summary of used language
        ListPreference ListPref = (ListPreference) findPreference("appLang");
        ListPref
                //.setSummary(sp.getString("appLang", "Some Default Text"));
                .setSummary(ListPref.getEntry());

        // Set summary of used search directories
        Preference dirsPref = findPreference(getString(R.string.pk_dumps_directories));
        dirsPref.setSummary(autoDumpsMustBe());
        dirsPref.setDefaultValue(autoDumpsMustBe());
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
            pref.setSummary(autoDumpsMustBe());
            pref.setDefaultValue(autoDumpsMustBe());
        }
        defSharedPref.edit().putBoolean(getString(R.string.pk_pref_changed), true).apply();

    }

    private String autoDumpsMustBe() {
        String searchDirKey = getString(R.string.pk_dumps_directories);
        String autoDumpDirectory = getString(R.string.autodump_directory);

        SharedPreferences defSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String cur_dir_search = defSharedPref.getString(
                        searchDirKey,
                        autoDumpDirectory
        );
        if( ! Arrays.asList(cur_dir_search.split(";")).contains(autoDumpDirectory)) {
            cur_dir_search = autoDumpDirectory + ";" + cur_dir_search;
            SharedPreferences.Editor editor = defSharedPref.edit();
            editor.putString(searchDirKey, cur_dir_search);
            editor.commit();
            findPreference(searchDirKey)
                    .setDefaultValue(cur_dir_search);
            recreate();
        }

        return cur_dir_search;
    }


}


