////////////////////////////////////////////////////////////////////////////////
//
//  Currency - An android currency converter.
//
//  Copyright (C) 2016	Bill Farmer
//
//  This program is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Bill Farmer	 william j farmer [at] yahoo [dot] co [dot] uk.
//
///////////////////////////////////////////////////////////////////////////////

package org.billthefarmer.currency;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource

        addPreferencesFromResource(R.xml.preferences);

        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());

        preferences.registerOnSharedPreferenceChangeListener(this);

        ListPreference preference =
                (ListPreference) findPreference(Main.PREF_DIGITS);

        // Set summary to be the user-description for the selected value

        preference.setSummary(preference.getEntry());

        // Get about summary

        Preference about = findPreference(Main.PREF_ABOUT);
        String sum = (String) about.getSummary();

        // Set version in text view

        String s = String.format(sum, BuildConfig.VERSION_NAME);
        about.setSummary(s);
    }

    // On preference tree click

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
                                         Preference preference) {
        boolean result =
                super.onPreferenceTreeClick(preferenceScreen, preference);

        // Set home as up
        if (preference instanceof PreferenceScreen) {
            Dialog dialog = ((PreferenceScreen) preference).getDialog();
            ActionBar actionBar = dialog.getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        return result;
    }

    // On shared preference changed

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences,
                                          String key) {
        if (key.equals(Main.PREF_DIGITS)) {
            ListPreference preference = (ListPreference) findPreference(key);

            // Set summary to be the user-description for the selected value
            preference.setSummary(preference.getEntry());
        }
    }
}
