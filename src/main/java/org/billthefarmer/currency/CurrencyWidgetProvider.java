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

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.DateFormat;
import java.text.NumberFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

// CurrencyWidgetProvider
@SuppressWarnings("deprecation")
public class CurrencyWidgetProvider extends AppWidgetProvider
{
    public static final String TAG = "CurrencyWidgetProvider";

    private boolean updateDone;

    // onReceive
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (BuildConfig.DEBUG)
            Log.d(TAG, "onReceive " + intent);

        updateDone = intent.getBooleanExtra
            (CurrencyWidgetUpdate.EXTRA_UPDATE_DONE, false);

        super.onReceive(context, intent);
    }

    // onUpdate
    @Override
    @SuppressWarnings("deprecation")
    public void onUpdate(Context context,
                         AppWidgetManager appWidgetManager,
                         int[] appWidgetIds)
    {
        if (BuildConfig.DEBUG)
            Log.d(TAG, "onUpdate " + context);

        // Get preferences
        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(context);

        // Get digits
        int digits = Integer.parseInt
            (preferences.getString(Main.PREF_DIGITS, "3"));

        // Set digits
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits(digits);
        numberFormat.setMaximumFractionDigits(digits);
        numberFormat.setGroupingUsed(true);

        // Get saved currency rates
        String mapJSON = preferences.getString(Main.PREF_MAP, null);
        Map<String, Double> valueMap = new HashMap<String, Double>();

        // Check saved rates
        if (mapJSON != null)
        {
            // Create the value map from a JSON object
            try
            {
                // Create the JSON object
                JSONObject mapObject = new JSONObject(mapJSON);

                // Use an iterator for the JSON object
                Iterator<String> keys = mapObject.keys();
                while (keys.hasNext())
                {
                    String key = keys.next();
                    valueMap.put(key, mapObject.getDouble(key));
                }
            }

            catch (Exception e) {}
        }

        // Get saved currency lists
        String namesJSON = preferences.getString(Main.PREF_NAMES, null);
        String valuesJSON = preferences.getString(Main.PREF_VALUES, null);
        List<String> nameList = new ArrayList<String>();
        List<String> valueList = new ArrayList<String>();

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

        // Get the saved value list
        if (valuesJSON != null)
        {
            try
            {
                // Update value list from JSON array
                JSONArray valuesArray = new JSONArray(valuesJSON);
                for (int i = 0; !valuesArray.isNull(i); i++)
                    valueList.add(valuesArray.getString(i));
            }

            catch (Exception e) {}
        }

        // Calculate value list
        else
        {
            // Format each value
            numberFormat.setGroupingUsed(true);
            for (String name : nameList)
            {
                Double v = valueMap.get(name);
                String value = numberFormat.format((v != null)? v: 0.0);

                valueList.add(value);
            }
        }

        // Get current currency
        int currentIndex = preferences.getInt(Main.PREF_INDEX, 0);

        String value = preferences.getString(Main.PREF_VALUE, "1.0");
        String currentValue = "1.0";
        try
        {
            double v = Double.parseDouble(value);
            currentValue = numberFormat.format(v);
        }

        catch (Exception e)
        {
            currentValue = "1.0";
        }

        for (int appWidgetId: appWidgetIds)
        {
            int widgetEntry = Integer.parseInt
                (preferences.getString(Main.PREF_ENTRY, "0"));

            widgetEntry = preferences.getInt(String.valueOf(appWidgetId),
                                             widgetEntry);

            if (widgetEntry >= nameList.size())
                widgetEntry = 0;

            if (BuildConfig.DEBUG)
                Log.d(TAG, "Id " + appWidgetId + ", " + widgetEntry);

            String entryName = nameList.get(widgetEntry);
            String entryValue = valueList.get(widgetEntry);
            int entryIndex = Main.currencyIndex(entryName);
            String longName = context.getString
                (Main.CURRENCIES[entryIndex].longname);

            // Create an Intent to refresh widgets
            Intent refresh = new Intent(context, CurrencyWidgetUpdate.class);
            //noinspection InlinedApi
            PendingIntent refreshIntent =
                PendingIntent.getService(context, 0, refresh,
                                         PendingIntent.FLAG_UPDATE_CURRENT |
                                         PendingIntent.FLAG_IMMUTABLE);
            // Create an Intent to configure widget
            Intent config = new Intent(context, CurrencyWidgetConfigure.class);
            config.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            // This bit of jiggery hackery is to force the system to
            // keep a different intent for each widget
            Uri uri = Uri.parse(Main.WIDGET + String.valueOf(appWidgetId));
            config.setData(uri);
            //noinspection InlinedApi
            PendingIntent configIntent =
                PendingIntent.getActivity(context, 0, config,
                                          PendingIntent.FLAG_UPDATE_CURRENT |
                                          PendingIntent.FLAG_IMMUTABLE);
            // Create an Intent to launch Currency
            Intent intent = new Intent(context, Main.class);
            //noinspection InlinedApi
            PendingIntent pendingIntent =
                PendingIntent.getActivity(context, 0, intent,
                                          PendingIntent.FLAG_UPDATE_CURRENT |
                                          PendingIntent.FLAG_IMMUTABLE);
            // Get the layout for the widget
            RemoteViews views = new
                RemoteViews(context.getPackageName(), R.layout.widget);

            // Attach an on-click listener to the view.
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);
            views.setOnClickPendingIntent(R.id.refresh, refreshIntent);
            views.setOnClickPendingIntent(R.id.config, configIntent);

            views.setTextViewText(R.id.current_name,
                                  Main.CURRENCIES[currentIndex].name);
            views.setTextViewText(R.id.current_symbol,
                                  Main.CURRENCIES[currentIndex].symbol);
            views.setTextViewText(R.id.current_value, currentValue);

            views.setImageViewResource(R.id.flag,
                                       Main.CURRENCIES[entryIndex].flag);
            views.setTextViewText(R.id.name, entryName);
            views.setTextViewText(R.id.symbol,
                                  Main.CURRENCIES[entryIndex].symbol);
            views.setTextViewText(R.id.value, entryValue);
            views.setTextViewText(R.id.long_name, longName);

            // Tell the AppWidgetManager to perform an update on the
            // current app widget.
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

        // Update done
        if (updateDone)
            return;

        boolean wifi = preferences.getBoolean(Main.PREF_WIFI, true);
        boolean roaming = preferences.getBoolean(Main.PREF_ROAMING, false);

        // Check connectivity before update
        ConnectivityManager manager = (ConnectivityManager)
            context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        // Check connected
        if (info == null || !info.isConnected())
            return;

        // Check wifi
        if (wifi && info.getType() != ConnectivityManager.TYPE_WIFI)
            return;

        // Check roaming
        if (!roaming && info.isRoaming())
            return;

        // Start update service, won't work on android 10+
        try
        {
            Intent update = new Intent(context, CurrencyWidgetUpdate.class);
            context.startService(update);

            if (BuildConfig.DEBUG)
                Log.d(TAG, "Update " + update);
        }

        catch (Exception e)
        {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "Update " + e);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds)
    {
        // Get preferences
        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(context);

        // Get editor
        SharedPreferences.Editor editor = preferences.edit();
        for (int appWidgetId: appWidgetIds)
            editor.remove(String.valueOf(appWidgetId));

        editor.apply();
    }
}
