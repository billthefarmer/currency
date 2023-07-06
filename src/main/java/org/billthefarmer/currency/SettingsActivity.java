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
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;

// SettingsActivity class
@SuppressWarnings("deprecation")
public class SettingsActivity extends Activity
{
    // On create
    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Get preferences
        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(this);

        int theme = Integer.parseInt(preferences.getString(Main.PREF_THEME, "0"));

        Configuration config = getResources().getConfiguration();
        int night = config.uiMode & Configuration.UI_MODE_NIGHT_MASK;

        switch (theme)
        {
        case Main.LIGHT:
            setTheme(R.style.AppLightTheme);
            break;

        case Main.DARK:
            setTheme(R.style.AppTheme);
            break;

        case Main.SYSTEM:
            switch (night)
            {
            case Configuration.UI_MODE_NIGHT_NO:
                setTheme(R.style.AppLightTheme);
                break;

            case Configuration.UI_MODE_NIGHT_YES:
                setTheme(R.style.AppTheme);
                break;
            }
            break;
        }

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
        .replace(android.R.id.content, new SettingsFragment())
        .commit();

        // Enable back navigation on action bar
        ActionBar actionBar = getActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.settings);
        }
    }

    // On options item selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Switch on item id
        switch (item.getItemId())
        {
        // Home, finish
        case android.R.id.home:
            finish();
            break;

        default:

            return false;
        }

        return true;
    }
}
