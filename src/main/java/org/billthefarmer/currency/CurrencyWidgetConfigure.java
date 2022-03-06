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
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;

// CurrencyWidgetConfigure
public class CurrencyWidgetConfigure extends Activity
{
    // On create
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Get preferences
        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(this);

        boolean dark = preferences.getBoolean(Main.PREF_DARK, true);

        if (!dark)
            setTheme(R.style.DialogLightTheme);

        setContentView(R.layout.config);

        // Find views
        ListView listView = findViewById(R.id.list);
        Button cancel = findViewById(R.id.cancel);

        // Set the listeners
        if (listView != null)
            listView.setOnItemClickListener((parent, view, position, id) ->
        {
            Intent intent = getIntent();
            int appWidgetId =
                intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                                   AppWidgetManager.INVALID_APPWIDGET_ID);
            // Get editor
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(String.valueOf(appWidgetId), position);
            editor.apply();

            AppWidgetManager appWidgetManager =
                AppWidgetManager.getInstance(this);
            CurrencyWidgetProvider provider = new CurrencyWidgetProvider();
            int appWidgetIds[] = {appWidgetId};
            provider.onUpdate(this, appWidgetManager, appWidgetIds);

            Intent result = new Intent();
            result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            setResult(RESULT_OK, result);
            finish();
        });

        if (cancel != null)
            cancel.setOnClickListener((v) ->
        {
            setResult(RESULT_CANCELED);
            finish();
        });


        // Get saved currency list
        String namesJSON = preferences.getString(Main.PREF_NAMES, null);
        List<String> nameList = new ArrayList<String>();

        // Check saved name list
        if (namesJSON != null)
        {
            try
            {
                // Update name list from JSON array
                JSONArray namesArray = new JSONArray(namesJSON);
                for (int i = 0; !namesArray.isNull(i); i++)
                    nameList.add(namesArray.getString(i));
            }

            catch (Exception e) {}
        }

        // Use the default list
        else
        {
            nameList.addAll(Arrays.asList(Main.CURRENCY_LIST));
        }

        // Create currency name list
        List<String> currencyNameList = Arrays.asList(Main.CURRENCY_NAMES);

        // Populate the lists
        List<Integer> flagList = new ArrayList<Integer>();
        List<Integer> longNameList = new ArrayList<Integer>();
        List<Integer> selectList = new ArrayList<Integer>();
        for (String name: nameList)
        {
            int index = currencyNameList.indexOf(name);
            flagList.add(Main.CURRENCY_FLAGS[index]);
            longNameList.add(Main.CURRENCY_LONGNAMES[index]);
        }

        // Create the adapter
        ChoiceAdapter adapter = new ChoiceAdapter(this, R.layout.choice,
                                                  flagList, nameList,
                                                  longNameList, selectList);
        // Set the adapter
        if (listView != null)
            listView.setAdapter(adapter);
    }
}
