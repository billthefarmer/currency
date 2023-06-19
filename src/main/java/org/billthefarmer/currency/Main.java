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
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

// Main class
public class Main extends Activity
    implements EditText.OnEditorActionListener,
    AdapterView.OnItemClickListener,
    AdapterView.OnItemLongClickListener,
    View.OnClickListener, TextWatcher,
    Data.TaskCallbacks
{
    // Initial active currency name
    public static final String DEFAULT_CURRENCY = "EUR";

    // Initial currency name list
    public static final String CURRENCY_LIST[] =
    {
        "USD", "GBP", "CAD", "AUD"
    };

    public static final class Currency
    {
        public final String name;
        public final String symbol;
        public final int longname;
        public final int flag;

        private Currency(String name, String symbol, int longname, int flag)
        {
            this.name = name;
            this.symbol = symbol;
            this.longname = longname;
            this.flag = flag;
        }
    }

    public static final Currency CURRENCIES[] =
    {
        new Currency("AUD", "$",  R.string.long_aud, R.drawable.flag_aud),
        new Currency("BGN", "лв", R.string.long_bgn, R.drawable.flag_bgn),
        new Currency("BRL", "R$", R.string.long_brl, R.drawable.flag_brl),
        new Currency("CAD", "$",  R.string.long_cad, R.drawable.flag_cad),
        new Currency("CHF", "",   R.string.long_chf, R.drawable.flag_chf),
        new Currency("CNY", "¥",  R.string.long_cny, R.drawable.flag_cny),
        new Currency("CZK", "Kč", R.string.long_czk, R.drawable.flag_czk),
        new Currency("DKK", "kr", R.string.long_dkk, R.drawable.flag_dkk),
        new Currency("EUR", "€",  R.string.long_eur, R.drawable.flag_eur),
        new Currency("EXT", "",   R.string.long_ext, R.drawable.flag_ext),
        new Currency("GBP", "£",  R.string.long_gbp, R.drawable.flag_gbp),
        new Currency("HKD", "$",  R.string.long_hkd, R.drawable.flag_hkd),
        new Currency("HRK", "kn", R.string.long_hrk, R.drawable.flag_hrk),
        new Currency("HUF", "Ft", R.string.long_huf, R.drawable.flag_huf),
        new Currency("IDR", "Rp", R.string.long_idr, R.drawable.flag_idr),
        new Currency("ILS", "₪",  R.string.long_ils, R.drawable.flag_ils),
        new Currency("INR", "₹",  R.string.long_inr, R.drawable.flag_inr),
        new Currency("ISK", "kr", R.string.long_isk, R.drawable.flag_isk),
        new Currency("JPY", "¥",  R.string.long_jpy, R.drawable.flag_jpy),
        new Currency("KRW", "₩",  R.string.long_krw, R.drawable.flag_kpw),
        new Currency("MXN", "$",  R.string.long_mxn, R.drawable.flag_mxn),
        new Currency("MYR", "RM", R.string.long_myr, R.drawable.flag_myr),
        new Currency("NOK", "kr", R.string.long_nok, R.drawable.flag_nok),
        new Currency("NZD", "$",  R.string.long_nzd, R.drawable.flag_nzd),
        new Currency("PHP", "₱",  R.string.long_php, R.drawable.flag_php),
        new Currency("PLN", "zł", R.string.long_pln, R.drawable.flag_pln),
        new Currency("RON", "lei",R.string.long_ron, R.drawable.flag_ron),
        new Currency("RUB", "₽",  R.string.long_rub, R.drawable.flag_rub),
        new Currency("SEK", "kr", R.string.long_sek, R.drawable.flag_sek),
        new Currency("SGD", "$",  R.string.long_sgd, R.drawable.flag_sgd),
        new Currency("THB", "฿",  R.string.long_thb, R.drawable.flag_thb),
        new Currency("TRY", "₺",  R.string.long_try, R.drawable.flag_try),
        new Currency("USD", "$",  R.string.long_usd, R.drawable.flag_usd),
        new Currency("ZAR", "R",  R.string.long_zar, R.drawable.flag_zar),
    };

    public static int currencyIndex(String name)
    {
        for (int i = 0; i < CURRENCIES.length; i++)
            if (CURRENCIES[i].name.equals(name))
                return i;

        return -1;
    }

    public static final String TAG = "Currency";

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String PREF_MAP = "pref_map";
    public static final String PREF_DATE = "pref_date";
    public static final String PREF_NAMES = "pref_names";
    public static final String PREF_INDEX = "pref_index";
    public static final String PREF_VALUE = "pref_value";
    public static final String PREF_VALUES = "pref_values";
    public static final String PREF_EXTRA = "pref_extra";

    public static final String PREF_WIFI = "pref_wifi";
    public static final String PREF_ROAMING = "pref_roaming";
    public static final String PREF_DIGITS = "pref_digits";
    public static final String PREF_ENTRY = "pref_entry";
    public static final String PREF_FILL = "pref_fill";
    public static final String PREF_THEME = "pref_theme";
    public static final String PREF_ABOUT = "pref_about";

    public static final String CHART_LIST = "chart_list";
    public static final String SAVE_SELECT = "save_select";

    public static final String ECB_DAILY_URL =
        "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";

    public final static String CHOICE = "choice";
    public final static String WIDGET = "widget://";

    public static final int DISPLAY_MODE = 0;
    public static final int SELECT_MODE = 1;

    public static final int LIGHT  = 0;
    public static final int DARK   = 1;
    public static final int SYSTEM = 2;

    private int mode = DISPLAY_MODE;

    private boolean wifi = true;
    private boolean roaming = false;
    private boolean select = true;

    private int theme = 0;
    private int digits = 3;

    private int currentIndex = 0;
    private int widgetEntry = 0;

    private double currentValue = 1.0;
    private double convertValue = 1.0;
    private double extraValue = 1.0;

    private String date;

    private ImageView flagView;
    private TextView nameView;
    private TextView symbolView;
    private EditText editView;
    private TextView longNameView;
    private TextView dateView;
    private TextView statusView;

    private Data data;

    private List<Integer> flagList;
    private List<String> nameList;
    private List<String> symbolList;
    private List<String> valueList;
    private List<Integer> longNameList;

    private List<Integer> selectList;
    private Map<String, Double> valueMap;

    private CurrencyAdapter adapter;

    private Resources resources;

    // On create
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Get preferences
        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(this);

        theme = Integer.parseInt(preferences.getString(PREF_THEME, "0"));

        Configuration config = getResources().getConfiguration();
        int night = config.uiMode & Configuration.UI_MODE_NIGHT_MASK;

        switch (theme)
        {
        case LIGHT:
            setTheme(R.style.AppLightTheme);
            break;

        case DARK:
            setTheme(R.style.AppTheme);
            break;

        case SYSTEM:
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

        setContentView(R.layout.main);

        // Get data instance
        data = Data.getInstance(this);

        // Find views
        flagView = findViewById(R.id.flag);
        nameView = findViewById(R.id.name);
        symbolView = findViewById(R.id.symbol);
        editView = findViewById(R.id.edit);
        longNameView = findViewById(R.id.long_name);
        dateView = findViewById(R.id.date);
        statusView = findViewById(R.id.status);
        ListView listView = findViewById(R.id.list);

        // Set the click listeners, just for the text selection logic
        if (flagView != null)
            flagView.setOnClickListener(this);

        if (nameView != null)
            nameView.setOnClickListener(this);

        if (symbolView != null)
            symbolView.setOnClickListener(this);

        if (longNameView != null)
            longNameView.setOnClickListener(this);

        // Set the listeners for the value field
        if (editView != null)
        {
            editView.addTextChangedListener(this);
            editView.setOnEditorActionListener(this);
            editView.setOnClickListener(this);
        }

        // Set the listeners for the list view
        if (listView != null)
        {
            listView.setOnItemClickListener(this);
            listView.setOnItemLongClickListener(this);
        }

        // Create lists
        flagList = new ArrayList<>();
        nameList = new ArrayList<>();
        symbolList = new ArrayList<>();
        valueList = new ArrayList<>();
        longNameList = new ArrayList<>();

        // Check data instance
        if (data != null)
            selectList = data.getList();

        // Check select list
        if (selectList == null)
            selectList = new ArrayList<>();

        // Set mode
        if (selectList.isEmpty())
            mode = Main.DISPLAY_MODE;

        else
            mode = Main.SELECT_MODE;

        // Create the adapter
        adapter = new CurrencyAdapter(this, R.layout.item, flagList, nameList,
                                      symbolList, valueList, longNameList,
                                      selectList);
        // Set the list view adapter
        if (listView != null)
            listView.setAdapter(adapter);
    }

    // On resume
    @Override
    @SuppressWarnings("deprecation")
    protected void onResume()
    {
        super.onResume();

        // Get resources
        resources = getResources();

        // Get preferences
        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(this);

        int last = theme;

        theme = Integer.parseInt(preferences.getString(PREF_THEME, "0"));

        wifi = preferences.getBoolean(PREF_WIFI, true);
        roaming = preferences.getBoolean(PREF_ROAMING, false);
        digits = Integer.parseInt(preferences.getString(PREF_DIGITS, "3"));

        if (last != theme && Build.VERSION.SDK_INT != Build.VERSION_CODES.M)
            recreate();

        // Get current currency
        currentIndex = preferences.getInt
            (PREF_INDEX, currencyIndex(DEFAULT_CURRENCY));

        // Get widget entry
        widgetEntry = Integer.parseInt(preferences.getString(PREF_ENTRY, "0"));

        // Get current value
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits(digits);
        numberFormat.setMaximumFractionDigits(digits);

        String value = preferences.getString(PREF_VALUE, "1.0");

        // Parse string value
        try
        {
            currentValue = Double.parseDouble(value);
        }
        catch (Exception ex)
        {
            currentValue = 1.0;
        }

        // Get extra value
        String extra = preferences.getString(PREF_EXTRA, "1.0");

        // Parse extra value
        try
        {
            extraValue = Double.parseDouble(extra);
        }
        catch (Exception ex)
        {
            extraValue = 1.0;
        }

        // Get the date and format it for display
        date = preferences.getString(PREF_DATE, "");
        String format = resources.getString(R.string.updated);
        String updated = String.format(Locale.getDefault(), format, date);

        // Check the date view
        if (dateView != null)
            dateView.setText(updated);

        // Set current currency flag and names
        if (flagView != null)
            flagView.setImageResource(CURRENCIES[currentIndex].flag);
        if (nameView != null)
            nameView.setText(CURRENCIES[currentIndex].name);
        if (symbolView != null)
            symbolView.setText(CURRENCIES[currentIndex].symbol);
        if (longNameView != null)
            longNameView.setText(CURRENCIES[currentIndex].longname);

        // Set current value
        numberFormat.setGroupingUsed(false);
        value = numberFormat.format(currentValue);
        if (editView != null)
            editView.setText(value);

        // Connect callbacks
        data = Data.getInstance(this);

        // Check data instance
        if (data != null)
            // Get the saved value map
            valueMap = data.getMap();

        // Check retained data
        if (valueMap == null)
        {
            // Get saved currency rates
            String mapJSON = preferences.getString(PREF_MAP, null);

            // Check saved rates
            if (mapJSON != null)
            {
                // Create the value map from a JSON object
                try
                {
                    // Create the JSON object
                    JSONObject mapObject = new JSONObject(mapJSON);
                    valueMap = new HashMap<>();

                    // Use an iterator for the JSON object
                    Iterator<String> keys = mapObject.keys();
                    while (keys.hasNext())
                    {
                        String key = keys.next();
                        valueMap.put(key, mapObject.getDouble(key));
                    }
                }
                catch (Exception e)
                {
                }
            }

            // Get old rates from resources
            else
            {
                // Get a parser
                Parser parser = new Parser();

                // Start the parser
                parser.startParser(this, R.raw.eurofxref_daily);

                SimpleDateFormat dateParser =
                    new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
                DateFormat dateFormat =
                    DateFormat.getDateInstance(DateFormat.MEDIUM);

                // Get the date from the parser
                String latest = parser.getDate();

                // Format the date for display
                if (latest != null)
                {
                    try
                    {
                        Date update = dateParser.parse(latest);
                        date = dateFormat.format(update);
                    }
                    catch (Exception e)
                    {
                    }

                    // Show the formatted date
                    format = resources.getString(R.string.updated);
                    updated = String.format(Locale.getDefault(), format, date);
                    if (dateView != null)
                        dateView.setText(updated);
                }

                else if (statusView != null)
                    statusView.setText(R.string.failed);

                valueMap = parser.getMap();
                valueMap.put("EUR", 1.0);
                valueMap.put("EXT", extraValue);
            }
        }

        // Get saved currency lists
        String namesJSON = preferences.getString(PREF_NAMES, null);
        String valuesJSON = preferences.getString(PREF_VALUES, null);

        // Check saved name list
        if (namesJSON != null)
        {
            try
            {
                // Update name list from JSON array
                JSONArray namesArray = new JSONArray(namesJSON);
                nameList.clear();
                for (int i = 0; !namesArray.isNull(i); i++)
                    nameList.add(namesArray.getString(i));
            }
            catch (Exception e)
            {
            }
        }

        // Use the default list
        else
        {
            nameList.addAll(Arrays.asList(CURRENCY_LIST));
        }

        // Get the saved value list
        if (valuesJSON != null)
        {
            try
            {
                // Update value list from JSON array
                JSONArray valuesArray = new JSONArray(valuesJSON);
                valueList.clear();
                for (int i = 0; !valuesArray.isNull(i); i++)
                    valueList.add(valuesArray.getString(i));
            }
            catch (Exception e)
            {
            }
        }

        // Calculate value list
        else
        {
            valueList.clear();

            // Format each value
            numberFormat.setGroupingUsed(true);
            for (String name : nameList)
            {
                try
                {
                    Double v = valueMap.get(name);
                    valueList.add(numberFormat.format(v));
                }

                catch (Exception e)
                {
                    valueList.add(numberFormat.format(Double.NaN));
                }
            }
        }

        // Get the current conversion rate
        convertValue = valueMap.containsKey(CURRENCIES[currentIndex].name)?
            valueMap.get(CURRENCIES[currentIndex].name): Double.NaN;

        // Recalculate all the values
        valueList.clear();
        numberFormat.setGroupingUsed(true);
        for (String name : nameList)
        {
            try
            {
                Double v = (currentValue / convertValue) *
                    valueMap.get(name);

                valueList.add(numberFormat.format(v));
            }

            catch (Exception e)
            {
                valueList.add(numberFormat.format(Double.NaN));
            }
        }

        // Clear lists
        if (flagList != null)
            flagList.clear();
        if (symbolList != null)
            symbolList.clear();
        if (longNameList != null)
            longNameList.clear();

        // Populate the lists
        for (String name : nameList)
        {
            int index = currencyIndex(name);

            if (flagList != null)
                flagList.add(CURRENCIES[index].flag);
            if (symbolList != null)
                symbolList.add(CURRENCIES[index].symbol);
            if (longNameList != null)
                longNameList.add(CURRENCIES[index].longname);
        }

        // Update the adapter
        adapter.notifyDataSetChanged();

        // Check data instance
        if (data != null)
        {
            // Check retained data
            if (data.getMap() != null)
            {
                valueMap.put("EUR", 1.0);
                valueMap.put("EXT", extraValue);

                // Don't update
                return;
            }
        }

        // Check connectivity before update
        ConnectivityManager manager =
            (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        // Check connected
        if (info == null || !info.isConnected())
        {
            if (statusView != null)
                statusView.setText(R.string.no_connection);
            return;
        }

        // Check wifi
        if (wifi && info.getType() != ConnectivityManager.TYPE_WIFI)
        {
            if (statusView != null)
                statusView.setText(R.string.no_wifi);
            return;
        }

        // Check roaming
        if (!roaming && info.isRoaming())
        {
            if (statusView != null)
                statusView.setText(R.string.roaming);
            return;
        }

        // Schedule update
        if (statusView != null)
            statusView.setText(R.string.updating);

        // Start the task
        if (data != null)
            data.startParseTask(ECB_DAILY_URL);
    }

    // On pause
    @Override
    protected void onPause()
    {
        super.onPause();

        // Update widgets
        updateWidgets();

        // Get preferences
        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(this);

        // Get editor
        SharedPreferences.Editor editor = preferences.edit();

        // Get entries
        JSONObject valueObject = new JSONObject(valueMap);
        JSONArray nameArray = new JSONArray(nameList);
        JSONArray valueArray = new JSONArray(valueList);

        // Update preferences
        editor.putString(PREF_MAP, valueObject.toString());
        editor.putString(PREF_NAMES, nameArray.toString());
        editor.putString(PREF_VALUES, valueArray.toString());

        editor.putInt(PREF_INDEX, currentIndex);
        editor.putString(PREF_ENTRY, Integer.toString(widgetEntry));

        String value = Double.toString(currentValue);
        editor.putString(PREF_VALUE, value);
        String extra = Double.toString(extraValue);
        editor.putString(PREF_EXTRA, extra);
        editor.putString(PREF_DATE, date);
        editor.apply();

        // Save the select list and value map in the data instance
        if (data != null)
        {
            data.setList(selectList);
            data.setMap(valueMap);
        }

        // Disconnect callbacks
        data = Data.getInstance(null);
    }

    // On create options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it
        // is present.
        MenuInflater inflater = getMenuInflater();

        // Check mode
        switch (mode)
        {
        case DISPLAY_MODE:
            inflater.inflate(R.menu.main, menu);
            break;

        case SELECT_MODE:
            inflater.inflate(R.menu.select, menu);
            break;
        }

        return true;
    }

    // On options item selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Get id
        int id = item.getItemId();
        switch (id)
        {
        // Add
        case R.id.action_add:
            return onAddClick();

        // Refresh
        case R.id.action_refresh:
            return onRefreshClick();

        // Update
        case R.id.action_update:
            return onUpdateClick();

        // Help
        case R.id.action_help:
            return onHelpClick();

        // Settings
        case R.id.action_settings:
            return onSettingsClick();

        // Clear
        case R.id.action_clear:
            return onClearClick();

        // Remove
        case R.id.action_remove:
            return onRemoveClick();

        // Chart
        case R.id.action_chart:
            return onChartClick();

        // Copy
        case R.id.action_copy:
            return onCopyClick();
        }

        return false;
    }

    // updateWidgets
    private void updateWidgets()
    {
        // Set digits
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits(digits);
        numberFormat.setMaximumFractionDigits(digits);
        numberFormat.setGroupingUsed(true);

        String value = numberFormat.format(currentValue);

        // Get preferences
        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(this);

        // Get manager
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        ComponentName provider = new
            ComponentName(this, CurrencyWidgetProvider.class);

        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(provider);
        for (int appWidgetId: appWidgetIds)
        {
            widgetEntry = preferences.getInt(String.valueOf(appWidgetId),
                                             widgetEntry);
            if (widgetEntry >= nameList.size())
                widgetEntry = 0;

            String entryName = nameList.get(widgetEntry);
            String entryValue = valueList.get(widgetEntry);
            int entryIndex = currencyIndex(entryName);
            String longName = getString(CURRENCIES[entryIndex].longname);

            // Create an Intent to configure widget
            Intent config = new Intent(this, CurrencyWidgetConfigure.class);
            config.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            // This bit of jiggery hackery is to force the system to
            // keep a different intent for each widget
            Uri uri = Uri.parse(WIDGET + String.valueOf(appWidgetId));
            config.setData(uri);
            //noinspection InlinedApi
            PendingIntent configIntent =
                PendingIntent.getActivity(this, 0, config,
                                          PendingIntent.FLAG_UPDATE_CURRENT |
                                          PendingIntent.FLAG_IMMUTABLE);
            // Create an Intent to launch Currency
            Intent intent = new Intent(this, Main.class);
            //noinspection InlinedApi
            PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, intent,
                                          PendingIntent.FLAG_UPDATE_CURRENT |
                                          PendingIntent.FLAG_IMMUTABLE);
            // Get the layout for the widget
            RemoteViews views = new
                RemoteViews(getPackageName(), R.layout.widget);

            // Attach an on-click listener to the view.
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);
            views.setOnClickPendingIntent(R.id.config, configIntent);

            views.setTextViewText(R.id.current_name,
                                  CURRENCIES[currentIndex].name);
            views.setTextViewText(R.id.current_symbol,
                                  CURRENCIES[currentIndex].symbol);
            views.setTextViewText(R.id.current_value, value);

            views.setImageViewResource(R.id.flag, CURRENCIES[entryIndex].flag);
            views.setTextViewText(R.id.name, entryName);
            views.setTextViewText(R.id.symbol, CURRENCIES[entryIndex].symbol);
            views.setTextViewText(R.id.value, entryValue);
            views.setTextViewText(R.id.long_name, longName);

            // Tell the AppWidgetManager to perform an update on the
            // current app widget.
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    // On add click
    private boolean onAddClick()
    {
        // Start the choice dialog
        Intent intent = new Intent(this, ChoiceDialog.class);
        startActivityForResult(intent, 0);

        return true;
    }

    // On clear click
    private boolean onClearClick()
    {
        // Clear the list and update the adapter
        selectList.clear();
        adapter.notifyDataSetChanged();

        // Restore the menu
        mode = DISPLAY_MODE;
        invalidateOptionsMenu();
        return true;
    }

    // On copy click
    private boolean onCopyClick()
    {
        ClipboardManager clipboard =
            (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits(digits);
        numberFormat.setMaximumFractionDigits(digits);

        // Copy value to clip
        String clip = null;
        for (int i : selectList)
        {
            try
            {
                numberFormat.setGroupingUsed(true);
                Number number = numberFormat.parse(valueList.get(i));
                Double value = number.doubleValue();

                // Remove grouping from value
                numberFormat.setGroupingUsed(false);
                clip = numberFormat.format(value);
            }

            catch (Exception e)
            {
            }
        }

        // Copy clip to clipboard
        clipboard.setPrimaryClip(ClipData.newPlainText("Currency", clip));

        // Clear selection
        selectList.clear();
        adapter.notifyDataSetChanged();

        // Restore menu
        mode = DISPLAY_MODE;
        invalidateOptionsMenu();
        return true;
    }

    // On remove click
    private boolean onRemoveClick()
    {
        List<String> removeList = new ArrayList<>();

        // Create a list of currency names to remove
        for (int i : selectList)
            removeList.add(nameList.get(i));

        for (String name : removeList)
        {
            // Look up name
            int i = nameList.indexOf(name);

            // Remove from the lists
            flagList.remove(i);
            nameList.remove(i);
            symbolList.remove(i);
            valueList.remove(i);
            longNameList.remove(i);
        }

        // Clear list and update adapter
        selectList.clear();
        adapter.notifyDataSetChanged();

        // Restore menu
        mode = DISPLAY_MODE;
        invalidateOptionsMenu();

        return true;
    }

    // On chart click
    private boolean onChartClick()
    {
        Intent intent = new Intent(this, ChartActivity.class);
        List<Integer> list = new ArrayList<>();

        // Add the current index
        list.add(currentIndex);

        // Add the select list to the list
        for (int index : selectList)
        {
            String name = nameList.get(index);
            list.add(currencyIndex(name));
        }

        // Put the list
        intent.putIntegerArrayListExtra(CHART_LIST,
                                        (ArrayList<Integer>) list);

        // Start chart activity
        startActivity(intent);

        // Clear select list and update adapter
        selectList.clear();
        adapter.notifyDataSetChanged();

        // Restore menu
        mode = DISPLAY_MODE;
        invalidateOptionsMenu();

        return true;
    }

    // On refresh click
    @SuppressWarnings("deprecation")
    private boolean onRefreshClick()
    {
        // Check connectivity before refresh
        ConnectivityManager manager =
            (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        // Check connected
        if (info == null || !info.isConnected())
        {
            if (statusView != null)
                statusView.setText(R.string.no_connection);
            return false;
        }

        // Check wifi
        if (wifi && info.getType() != ConnectivityManager.TYPE_WIFI)
        {
            if (statusView != null)
                statusView.setText(R.string.no_wifi);
            return false;
        }

        // Check roaming
        if (!roaming && info.isRoaming())
        {
            if (statusView != null)
                statusView.setText(R.string.roaming);
            return false;
        }

        // Schedule update
        if (statusView != null)
            statusView.setText(R.string.updating);

        // Start the task
        if (data != null)
            data.startParseTask(ECB_DAILY_URL);

        return true;
    }

    // On update click
    private boolean onUpdateClick()
    {
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits(digits);
        numberFormat.setMaximumFractionDigits(digits);

        NumberFormat englishFormat = NumberFormat.getInstance(Locale.ENGLISH);

        String extra = numberFormat.format(extraValue);

        // Open dialog
        updateDialog(R.string.update_extra, extra, R.string.decimal,
                    (dialog, id) ->
        {
            switch (id)
            {
            case DialogInterface.BUTTON_POSITIVE:
                EditText text =
                    ((Dialog) dialog).findViewById(R.id.value);
                String value = text.getText().toString();

                // Ignore empty string
                if (value.isEmpty())
                    return;

                // Try default locale
                try
                {
                    Number number = numberFormat.parse(value);
                    extraValue = number.doubleValue();
                }

                catch (Exception e)
                {
                    // Try English locale
                    try
                    {
                        Number number = englishFormat.parse(value);
                        extraValue = number.doubleValue();
                    }

                    catch (Exception ex)
                    {
                        extraValue = 1.0;
                    }
                }

                // Update display
                valueMap.put("EXT", extraValue);
                convertValue = valueMap.containsKey
                    (CURRENCIES[currentIndex].name)?
                    valueMap.get(CURRENCIES[currentIndex].name): Double.NaN;
                Editable editable = editView.getEditableText();
                afterTextChanged(editable);
            }
        });

        return true;
    }

    // updateDialog
    private void updateDialog(int title, String value, int hint,
                              DialogInterface.OnClickListener listener)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        // Add the buttons
        builder.setPositiveButton(R.string.ok, listener);
        builder.setNegativeButton(R.string.cancel, listener);

        // Create edit text
        Context context = builder.getContext();
        LayoutInflater inflater = (LayoutInflater)
            context.getSystemService(LAYOUT_INFLATER_SERVICE);
        EditText text = (EditText) inflater.inflate(R.layout.value, null);
        text.setText(value);

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.setView(text, 40, 0, 40, 0);
        dialog.show();
    }

    // On help click
    private boolean onHelpClick()
    {
        // Start help activity
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);

        return true;
    }

    // On settings click
    private boolean onSettingsClick()
    {
        // Start settings activity
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);

        return true;
    }

    // On click
    public void onClick(View view)
    {
        int id = view.getId();

        switch (id)
        {
        // Value field
        case R.id.edit:
            if (select)
            {
                // Forces select all
                view.clearFocus();
                view.requestFocus();
            }

            // Do it only once
            select = false;
            break;

        // Any other view
        default:
            // Clear value field selection
            if (editView != null)
                editView.setSelection(0);
            select = true;
        }
    }

    // After text changed
    @Override
    public void afterTextChanged(Editable editable)
    {
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits(digits);
        numberFormat.setMaximumFractionDigits(digits);

        NumberFormat englishFormat = NumberFormat.getInstance(Locale.ENGLISH);

        String n = editable.toString();
        if (n.length() > 0)
        {
            // Parse current value
            try
            {
                Number number = numberFormat.parse(n);
                currentValue = number.doubleValue();
            }

            catch (Exception e)
            {
                // Try English locale
                try
                {
                    Number number = englishFormat.parse(n);
                    currentValue = number.doubleValue();
                }

                // Do nothing on exception
                catch (Exception ex)
                {
                    return;
                }
            }
        }

        // Recalculate all the values
        valueList.clear();
        numberFormat.setGroupingUsed(true);
        for (String name : nameList)
        {
            try
            {
                Double value = (currentValue / convertValue) *
                    valueMap.get(name);

                valueList.add(numberFormat.format(value));
            }

            catch (Exception e)
            {
                valueList.add(numberFormat.format(Double.NaN));
            }
        }

        // Notify the adapter
        adapter.notifyDataSetChanged();
    }

    // Not used
    @Override
    public void beforeTextChanged(CharSequence s, int start,
                                  int count, int after) {}

    // Not used
    @Override
    public void onTextChanged(CharSequence s, int start,
                              int before, int count) {}

    // On editor action
    @Override
    public boolean onEditorAction(TextView view, int actionId, KeyEvent event)
    {
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits(digits);
        numberFormat.setMaximumFractionDigits(digits);

        NumberFormat englishFormat = NumberFormat.getInstance(Locale.ENGLISH);

        switch (actionId)
        {
        case EditorInfo.IME_ACTION_DONE:

            // Parse current value
            String n = view.getText().toString();
            if (n.length() > 0)
            {
                try
                {
                    Number number = numberFormat.parse(n);
                    currentValue = number.doubleValue();
                }

                catch (Exception e)
                {
                    // Try English locale
                    try
                    {
                        Number number = englishFormat.parse(n);
                        currentValue = number.doubleValue();
                    }

                    // Set to one on exception
                    catch (Exception ex)
                    {
                        currentValue = 1.0;
                        view.setText(R.string.num_one);
                    }
                }
            }

            // Reformat the value field
            numberFormat.setGroupingUsed(false);
            String s = numberFormat.format(currentValue);
            view.setText(s);

            // Recalculate all the values
            valueList.clear();
            numberFormat.setGroupingUsed(true);
            for (String name : nameList)
            {
                try
                {
                    Double value = (currentValue / convertValue) *
                        valueMap.get(name);

                    valueList.add(numberFormat.format(value));
                }

                catch (Exception e)
                {
                    valueList.add(numberFormat.format(Double.NaN));
                }
            }

            // Notify the adapter
            adapter.notifyDataSetChanged();

            return false; // Or the keypad won't go away
        }

        return false;
    }

    // On item click
    @Override
    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id)
    {
        String value;
        int oldIndex;
        double oldValue;

        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits(digits);
        numberFormat.setMaximumFractionDigits(digits);

        // Check mode
        switch (mode)
        {
        // Display mode - replace the current currency
        case DISPLAY_MODE:
            // Save the current values
            oldIndex = currentIndex;
            oldValue = currentValue;

            // Set the current currency from the list
            currentIndex = currencyIndex(nameList.get(position));

            convertValue = valueMap.containsKey(CURRENCIES[currentIndex].name)?
                valueMap.get(CURRENCIES[currentIndex].name): Double.NaN;

            if (flagView != null)
                flagView.setImageResource(CURRENCIES[currentIndex].flag);
            if (nameView != null)
                nameView.setText(CURRENCIES[currentIndex].name);
            if (symbolView != null)
                symbolView.setText(CURRENCIES[currentIndex].symbol);
            if (longNameView != null)
                longNameView.setText(CURRENCIES[currentIndex].longname);

            // Remove the selected currency from the lists
            flagList.remove(position);
            nameList.remove(position);
            symbolList.remove(position);
            valueList.remove(position);
            longNameList.remove(position);

            // Add the old current currency to the start of the list
            flagList.add(0, CURRENCIES[oldIndex].flag);
            nameList.add(0, CURRENCIES[oldIndex].name);
            symbolList.add(0, CURRENCIES[oldIndex].symbol);
            longNameList.add(0, CURRENCIES[oldIndex].longname);

            numberFormat.setGroupingUsed(true);
            value = numberFormat.format(oldValue);
            valueList.add(0, value);

            numberFormat.setGroupingUsed(false);
            value = numberFormat.format(currentValue);

            if (editView != null)
            {
                editView.setText(value);

                // Forces select all
                editView.clearFocus();
                editView.requestFocus();

                // Do it only once
                select = false;
            }

            // Get preferences
            SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this);

            // Get editor
            SharedPreferences.Editor editor = preferences.edit();

            // Get entries
            JSONArray nameArray = new JSONArray(nameList);
            JSONArray valueArray = new JSONArray(valueList);

            // Update preferences
            editor.putString(PREF_NAMES, nameArray.toString());
            editor.putString(PREF_VALUES, valueArray.toString());
            editor.putInt(PREF_INDEX, currentIndex);
            value = Double.toString(currentValue);
            editor.putString(PREF_VALUE, value);
            editor.apply();

            // Notify the adapter
            adapter.notifyDataSetChanged();
            break;

        // Select mode - toggle selection
        case SELECT_MODE:
            // Select mode - add or remove from list
            if (selectList.contains(position))
                selectList.remove(selectList.indexOf(position));

            else
                selectList.add(position);

            // Reset mode if list empty
            if (selectList.isEmpty())
            {
                mode = DISPLAY_MODE;
                invalidateOptionsMenu();
            }

            // Notify the adapter
            adapter.notifyDataSetChanged();
            break;
        }
    }

    // On item long click
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id)
    {
        // Switch to select mode, update menu
        mode = SELECT_MODE;
        invalidateOptionsMenu();

        // Clear the list and add the new selection
        selectList.clear();
        selectList.add(position);

        // Notify the adapter
        adapter.notifyDataSetChanged();
        return true;
    }

    // On activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data)
    {
        // Do nothing if cancelled
        if (resultCode != RESULT_OK)
            return;

        // Get index list from intent
        List<Integer> indexList = data.getIntegerArrayListExtra(CHOICE);
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits(digits);
        numberFormat.setMaximumFractionDigits(digits);
        numberFormat.setGroupingUsed(true);

        // Add currencies from list
        for (int index : indexList)
        {
            // Don't add duplicates or currencies not available
            if ((currentIndex == index) ||
                nameList.contains(CURRENCIES[index].name) ||
                !valueMap.containsKey(CURRENCIES[index].name))
                continue;

            flagList.add(CURRENCIES[index].flag);
            nameList.add(CURRENCIES[index].name);
            symbolList.add(CURRENCIES[index].symbol);
            longNameList.add(CURRENCIES[index].longname);

            Double value = 1.0;

            try
            {
                value = (currentValue / convertValue) *
                    valueMap.get(CURRENCIES[index].name);
            }

            catch (Exception e) {}

            valueList.add(numberFormat.format(value));
        }

        // Get preferences
        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(this);

        // Get editor
        SharedPreferences.Editor editor = preferences.edit();

        // Get entries
        JSONArray nameArray = new JSONArray(nameList);
        JSONArray valueArray = new JSONArray(valueList);

        // Update preferences
        editor.putString(PREF_NAMES, nameArray.toString());
        editor.putString(PREF_VALUES, valueArray.toString());
        editor.apply();

        adapter.notifyDataSetChanged();
    }

    // On progress update
    @Override
    public void onProgressUpdate(String... dates)
    {
        SimpleDateFormat dateParser =
            new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        DateFormat dateFormat =
            DateFormat.getDateInstance(DateFormat.MEDIUM);

        // Format the date for display
        if (dates[0] != null)
        {
            try
            {
                Date update = dateParser.parse(dates[0]);
                this.date = dateFormat.format(update);
            }

            catch (Exception e)
            {
            }

            String format = resources.getString(R.string.updated);
            String updated = String.format(Locale.getDefault(),
                                           format, this.date);
            if (dateView != null)
                dateView.setText(updated);
        }
        else if (statusView != null)
            statusView.setText(R.string.failed);
    }

    // The system calls this to perform work in the UI thread and
    // delivers the result from doInBackground()
    @Override
    public void onPostExecute(Map<String, Double> map)
    {
        // Check the map
        if (!map.isEmpty())
        {
            valueMap = map;
            valueMap.put("EUR", 1.0);
            valueMap.put("EXT", extraValue);

            // Empty the value list
            valueList.clear();

            // Get the convert value
            convertValue = valueMap.containsKey(CURRENCIES[currentIndex].name)?
                valueMap.get(CURRENCIES[currentIndex].name): Double.NaN;

            // Populate a new value list
            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setMinimumFractionDigits(digits);
            numberFormat.setMaximumFractionDigits(digits);
            numberFormat.setGroupingUsed(true);
            for (String name : nameList)
            {
                try
                {
                    Double value = (currentValue / convertValue) *
                        valueMap.get(name);

                    valueList.add(numberFormat.format(value));
                }

                catch (Exception e)
                {
                    valueList.add(numberFormat.format(Double.NaN));
                }
            }

            // Get preferences
            SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this);

            // Get editor
            SharedPreferences.Editor editor = preferences.edit();

            // Get entries
            JSONObject valueObject = new JSONObject(valueMap);
            JSONArray nameArray = new JSONArray(nameList);
            JSONArray valueArray = new JSONArray(valueList);

            // Update preferences
            editor.putString(PREF_MAP, valueObject.toString());
            editor.putString(PREF_NAMES, nameArray.toString());
            editor.putString(PREF_VALUES, valueArray.toString());

            editor.putString(PREF_DATE, date);
            editor.apply();

            statusView.setText(R.string.ok);
            adapter.notifyDataSetChanged();
        }

        // Notify failed
        else if (statusView != null)
            statusView.setText(R.string.failed);
    }
}
