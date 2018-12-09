package com.example.louisbertin.pomodoro;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.view.MenuItem;

import com.kizitonwose.colorpreference.ColorPreference;


public class SettingsActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // load settings fragment
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();
    }

    public static class MainPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);

            // save color preference
            ColorPreference colorPreference = (ColorPreference) findPreference("key_color");
            saveColorPickerValue(colorPreference, getActivity());

            // notification preference change listener
            bindPreferenceSummaryToValue(findPreference(getString(R.string.key_pomodoro_cycle)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.key_pom_end_ringtone)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.key_time_pom)));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }


    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();
            //Log.d(TAG, "onPreferenceChange: string"+stringValue);

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(listPreference.getEntries()[index]);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.

                Ringtone ringtone = RingtoneManager.getRingtone(
                        preference.getContext(), Uri.parse(stringValue));

                if (ringtone == null) {
                    // Clear the summary if there was a lookup error.
                    preference.setSummary(R.string.summary_choose_ringtone);
                } else {
                    // Set the summary to reflect the new ringtone display
                    // name.
                    String name = ringtone.getTitle(preference.getContext());
                    preference.setSummary(name);
                }

            }else {
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    private static void saveColorPickerValue(ColorPreference colorPreference, final Activity activity) {
        colorPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override public boolean onPreferenceChange(Preference preference, Object newValue) {
                if ("key_color".equals(preference.getKey())) {
                    String newDefaultColor = Integer.toHexString((int) newValue).substring(2);

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("button_color", newDefaultColor);
                    editor.commit();
                }
                return true;
            }
        });
    }

}