////////////////////////////////////////////////////////////////////////////////
//
//  Currency - An android currency converter.
//
//  Copyright (C) 2016	Bill Farmer
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
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

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

// CurrencyWidgetDispatch
public class CurrencyWidgetDispatch extends Activity
{
    public static final String TAG = "CurrencyWidgetDispatch";

    // On create
    @Override
    @SuppressWarnings("deprecation")
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Get preferences
        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(this);

        boolean wifi = preferences.getBoolean(Main.PREF_WIFI, true);
        boolean roaming = preferences.getBoolean(Main.PREF_ROAMING, false);

        // Check connectivity before update
        ConnectivityManager manager =
            (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        // Check connected
        if (info == null || !info.isConnected())
        {
            finish();
            return;
        }

        // Check wifi
        if (wifi && info.getType() != ConnectivityManager.TYPE_WIFI)
        {
            finish();
            return;
        }

        // Check roaming
        if (!roaming && info.isRoaming())
        {
            finish();
            return;
        }

        // Start update
        Intent update = new Intent(this, CurrencyWidgetUpdate.class);
        startService(update);

        if (BuildConfig.DEBUG)
            Log.d(TAG, "Update " + update);

        finish();
    }
}
