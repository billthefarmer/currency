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
import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

// Main class
public class Main extends Activity
    implements EditText.OnEditorActionListener,
    AdapterView.OnItemClickListener,
    AdapterView.OnItemLongClickListener,
    View.OnClickListener, TextWatcher,
    Data.TaskCallbacks
{
    // Initial currency name list
    public static final String CURRENCY_LIST[] =
    {
        "USD", "GBP", "CAD", "AUD"
    };

    // Currency names
    public static final String CURRENCY_NAMES[] =
    {
        "EUR", "USD", "JPY", "BGN",
        "CZK", "DKK", "GBP", "HUF",
        "PLN", "RON", "SEK", "CHF",
        "NOK", "HRK", "RUB", "TRY",
        "AUD", "BRL", "CAD", "CNY",
        "HKD", "IDR", "ILS", "INR",
        "KRW", "MXN", "MYR", "NZD",
        "PHP", "SGD", "THB", "ZAR"
    };

    // Currency symbols
    public static final String CURRENCY_SYMBOLS[] =
    {
        "€", "$", "¥", "лв",
        "Kč", "kr", "£", "Ft",
        "zł", "lei", "kr", "",
        "kr", "kn", "₽", "₺",
        "$", "R$", "$", "¥",
        "$", "Rp", "₪", "₹",
        "₩", "$", "RM", "$",
        "₱", "$", "฿", "S"
    };

    // Currency long names
    public static final Integer CURRENCY_LONGNAMES[] =
    {
        R.string.long_eur, R.string.long_usd, R.string.long_jpy,
        R.string.long_bgn, R.string.long_czk, R.string.long_dkk,
        R.string.long_gbp, R.string.long_huf, R.string.long_pln,
        R.string.long_ron, R.string.long_sek, R.string.long_chf,
        R.string.long_nok, R.string.long_hrk, R.string.long_rub,
        R.string.long_try, R.string.long_aud, R.string.long_brl,
        R.string.long_cad, R.string.long_cny, R.string.long_hkd,
        R.string.long_idr, R.string.long_ils, R.string.long_inr,
        R.string.long_krw, R.string.long_mxn, R.string.long_myr,
        R.string.long_nzd, R.string.long_php, R.string.long_sgd,
        R.string.long_thb, R.string.long_zar
    };

    // Currency flags
    public static final Integer CURRENCY_FLAGS[] =
    {
        R.drawable.flag_eur, R.drawable.flag_usd, R.drawable.flag_jpy,
        R.drawable.flag_bgn, R.drawable.flag_czk, R.drawable.flag_dkk,
        R.drawable.flag_gbp, R.drawable.flag_huf, R.drawable.flag_pln,
        R.drawable.flag_ron, R.drawable.flag_sek, R.drawable.flag_chf,
        R.drawable.flag_nok, R.drawable.flag_hrk, R.drawable.flag_rub,
        R.drawable.flag_try, R.drawable.flag_aud, R.drawable.flag_brl,
        R.drawable.flag_cad, R.drawable.flag_cny, R.drawable.flag_hkd,
        R.drawable.flag_idr, R.drawable.flag_ils, R.drawable.flag_inr,
        R.drawable.flag_kpw, R.drawable.flag_mxn, R.drawable.flag_myr,
        R.drawable.flag_nzd, R.drawable.flag_php, R.drawable.flag_sgd,
        R.drawable.flag_thb, R.drawable.flag_zar
    };

    public static final String TAG = "Main";

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String PREF_MAP = "pref_map";
    public static final String PREF_DATE = "pref_date";
    public static final String PREF_NAMES = "pref_names";
    public static final String PREF_INDEX = "pref_index";
    public static final String PREF_VALUE = "pref_value";
    public static final String PREF_VALUES = "pref_values";

    public static final String PREF_WIFI = "pref_wifi";
    public static final String PREF_ROAMING = "pref_roaming";
    public static final String PREF_SELECT = "pref_select";
    public static final String PREF_DIGITS = "pref_digits";
    public static final String PREF_FILL = "pref_fill";
    public static final String PREF_ABOUT = "pref_about";

    public static final String CHART_LIST = "chart_list";
    public static final String SAVE_SELECT = "save_select";

    public static final String ECB_DAILY_URL =
        "http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";

    protected final static String CHOICE = "choice";

    public static final int DISPLAY_MODE = 0;
    public static final int SELECT_MODE = 1;

    private int mode = DISPLAY_MODE;

    private boolean wifi = true;
    private boolean roaming = false;
    private boolean selectAll = true;
    private boolean select = true;
    private int digits = 3;

    private int currentIndex = 0;
    private double currentValue = 1.0;
    private double convertValue = 1.0;
    private String date;

    private ImageView flagView;
    private TextView nameView;
    private TextView symbolView;
    private EditText editView;
    private TextView longNameView;
    private TextView dateView;
    private TextView statusView;
    private ListView listView;

    private Data instance;

    private List<String> currencyNameList;

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
        setContentView(R.layout.main);

        // Get data instance
        instance = Data.getInstance(this);

        // Find views
        flagView = (ImageView)findViewById(R.id.flag);
        nameView = (TextView)findViewById(R.id.name);
        symbolView = (TextView)findViewById(R.id.symbol);
        editView = (EditText)findViewById(R.id.edit);
        longNameView = (TextView)findViewById(R.id.long_name);
        dateView = (TextView)findViewById(R.id.date);
        statusView = (TextView)findViewById(R.id.status);
        listView = (ListView)findViewById(R.id.list);

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

        // Create currency name list
        currencyNameList = Arrays.asList(CURRENCY_NAMES);

        // Create lists
        flagList = new ArrayList<Integer>();
        nameList = new ArrayList<String>();
        symbolList = new ArrayList<String>();
        valueList = new ArrayList<String>();
        longNameList = new ArrayList<Integer>();

        // Check instance
        if (instance != null)
            selectList = instance.getList();

        // Check select list
        if (selectList == null)
            selectList = new ArrayList<Integer>();

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
    protected void onResume()
    {
        super.onResume();

        // Get resources
        resources = getResources();

        // Get preferences
        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(this);

        wifi = preferences.getBoolean(PREF_WIFI, true);
        roaming = preferences.getBoolean(PREF_ROAMING, false);
        selectAll = preferences.getBoolean(PREF_SELECT, true);
        digits = Integer.parseInt(preferences.getString(PREF_DIGITS, "3"));

        // Get current currency
        currentIndex = preferences.getInt(PREF_INDEX, 0);

        // Get current value
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits(digits);
        numberFormat.setMaximumFractionDigits(digits);
        try
        {
            String value = preferences.getString(PREF_VALUE, "1.0");
            Number number = numberFormat.parse(value);
            currentValue = number.doubleValue();
        }

        catch (Exception e)
        {
            currentValue = 1.0;
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
            flagView.setImageResource(CURRENCY_FLAGS[currentIndex]);
        if (nameView != null)
            nameView.setText(CURRENCY_NAMES[currentIndex]);
        if (symbolView != null)
            symbolView.setText(CURRENCY_SYMBOLS[currentIndex]);
        if (longNameView != null)
            longNameView.setText(CURRENCY_LONGNAMES[currentIndex]);

        // Set current value
        numberFormat.setGroupingUsed(false);
        String value = numberFormat.format(currentValue);
        if (editView != null)
            editView.setText(value);

        // Connect callbacks
        instance = Data.getInstance(this);

        // Check data instance
        if (instance != null)
            // Get the saved value map
            valueMap = instance.getMap();

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
                    valueMap = new HashMap<String, Double>();

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
                    e.printStackTrace();
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
                        e.printStackTrace();
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
                e.printStackTrace();
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
                e.printStackTrace();
            }
        }

        // Calculate value list
        else
        {
            valueList.clear();

            // Format each value
            for (String name : nameList)
            {
                Double v = valueMap.get(name);
                value = numberFormat.format(v);
                // value = String.format("%1.3f", v);

                valueList.add(value);
            }
        }

        // Get the current conversion rate
        convertValue = valueMap.get(CURRENCY_NAMES[currentIndex]);

        // Recalculate all the values
        valueList.clear();
        for (String name : nameList)
        {
            Double v = (currentValue / convertValue) *
                valueMap.get(name);

            String s = numberFormat.format(v);
            valueList.add(s);
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
            int index = currencyNameList.indexOf(name);

            if (flagList != null)
                flagList.add(CURRENCY_FLAGS[index]);
            if (symbolList != null)
                symbolList.add(CURRENCY_SYMBOLS[index]);
            if (longNameList != null)
                longNameList.add(CURRENCY_LONGNAMES[index]);
        }

        // Update the adapter
        adapter.notifyDataSetChanged();

        // Check data instance
        if (instance != null)
        {
            // Check retained data
            if (instance.getMap() != null)
                // Don't update
                return;
        }

        // Check connectivity before update
        ConnectivityManager manager =
            (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
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
        if (instance != null)
            instance.startParseTask(ECB_DAILY_URL);;
    }

    // On pause
    @Override
    protected void onPause()
    {
        super.onPause();

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

        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits(digits);
        numberFormat.setMaximumFractionDigits(digits);
        numberFormat.setGroupingUsed(false);
        String value = numberFormat.format(currentValue);
        editor.putString(PREF_VALUE, value);
        editor.putString(PREF_DATE, date);
        editor.apply();

        // Save the select list and value map in the data instance
        if (instance != null)
        {
            instance.setList(selectList);
            instance.setMap(valueMap);
        }

        // Disconnect callbacks
        instance = Data.getInstance(null);
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
            (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);

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

            catch (Exception e) {}
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
        List<String> removeList = new ArrayList<String>();

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
        List<Integer> list = new ArrayList<Integer>();

        // Add the current index
        list.add(currentIndex);

        // Add the select list to the list
        for (int index : selectList)
        {
            String name = nameList.get(index);
            list.add(currencyNameList.indexOf(name));
        }

        // Put the list
        intent.putIntegerArrayListExtra(CHART_LIST, (ArrayList)list);

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
    private boolean onRefreshClick()
    {
        // Check connectivity before refresh
        ConnectivityManager manager =
            (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
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
        if (instance != null)
            instance.startParseTask(ECB_DAILY_URL);

        return true;
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
            if (selectAll && select)
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

        // Parse current value
        try
        {
            String n = editable.toString();
            if (n.length() > 0)
            {
                Number number = numberFormat.parse(n);
                currentValue = number.doubleValue();
            }
        }

        // Do nothing on exception
        catch (Exception e)
        {
            return;
        }

        // Recalculate all the values
        valueList.clear();
        for (String name : nameList)
        {
            Double value = (currentValue / convertValue) *
                           valueMap.get(name);

            String s = numberFormat.format(value);
            valueList.add(s);
        }

        // Notify the adapter
        adapter.notifyDataSetChanged();
    }

    // Not used
    @Override
    public void beforeTextChanged (CharSequence s, int start,
                                   int count,  int after) {}
    // Not used
    @Override
    public void onTextChanged (CharSequence s, int start,
                               int before, int count) {}

    // On editor action
    @Override
    public boolean onEditorAction(TextView view, int actionId, KeyEvent event)
    {
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits(digits);
        numberFormat.setMaximumFractionDigits(digits);

        switch (actionId)
        {
        case  EditorInfo.IME_ACTION_DONE:

            // Parse current value
            try
            {
                String n = view.getText().toString();
                if (n.length() > 0)
                {
                    Number number = numberFormat.parse(n);
                    currentValue = number.doubleValue();
                }
            }

            // Set to one on exception
            catch (Exception e)
            {
                currentValue = 1.0;
                view.setText(R.string.num_one);
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
                Double value = (currentValue / convertValue) *
                               valueMap.get(name);

                s = numberFormat.format(value);
                valueList.add(s);
            }

            // Notify the adapter
            adapter.notifyDataSetChanged();

            return false; // Or the keypad won't go away
        }

        return false;
    }

    // On item click
    @Override
    public void onItemClick(AdapterView parent, View view,
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
            currentIndex = currencyNameList.indexOf(nameList.get(position));

            currentValue = (oldValue / convertValue) *
                           valueMap.get(CURRENCY_NAMES[currentIndex]);

            convertValue = valueMap.get(CURRENCY_NAMES[currentIndex]);

            numberFormat.setGroupingUsed(false);
            value = numberFormat.format(currentValue);
            // value = String.format("%1.3f", currentValue);
            if (editView != null)
                editView.setText(value);

            if (flagView != null)
                flagView.setImageResource(CURRENCY_FLAGS[currentIndex]);
            if (nameView != null)
                nameView.setText(CURRENCY_NAMES[currentIndex]);
            if (symbolView != null)
                symbolView.setText(CURRENCY_SYMBOLS[currentIndex]);
            if (longNameView != null)
                longNameView.setText(CURRENCY_LONGNAMES[currentIndex]);

            // Remove the selected currency from the lists
            flagList.remove(position);
            nameList.remove(position);
            symbolList.remove(position);
            valueList.remove(position);
            longNameList.remove(position);

            // Add the old current currency to the start of the list
            flagList.add(0, CURRENCY_FLAGS[oldIndex]);
            nameList.add(0, CURRENCY_NAMES[oldIndex]);
            symbolList.add(0, CURRENCY_SYMBOLS[oldIndex]);
            longNameList.add(0, CURRENCY_LONGNAMES[oldIndex]);

            numberFormat.setGroupingUsed(true);
            value = numberFormat.format(oldValue);
            // value = String.format("%1.3f", oldValue);
            valueList.add(0, value);

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
            editor.putFloat(PREF_VALUE, (float)currentValue);
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
    public boolean onItemLongClick(AdapterView parent, View view,
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

        // Add currencies from list
        for (int index : indexList)
        {
            // Don't add duplicates
            if (nameList.contains(CURRENCY_NAMES[index]))
                continue;

            flagList.add(CURRENCY_FLAGS[index]);
            nameList.add(CURRENCY_NAMES[index]);
            symbolList.add(CURRENCY_SYMBOLS[index]);
            longNameList.add(CURRENCY_LONGNAMES[index]);

            Double value = (currentValue / convertValue) *
                           valueMap.get(CURRENCY_NAMES[index]);

            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setMinimumFractionDigits(digits);
            numberFormat.setMaximumFractionDigits(digits);
            String s = numberFormat.format(value);

            valueList.add(s);
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
    public void onProgressUpdate(String... date)
    {
        SimpleDateFormat dateParser =
            new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        DateFormat dateFormat =
            DateFormat.getDateInstance(DateFormat.MEDIUM);

        // Format the date for display
        if (date[0] != null)
        {
            try
            {
                Date update = dateParser.parse(date[0]);
                this.date = dateFormat.format(update);
            }

            catch (Exception e)
            {
                e.printStackTrace();
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

            // Empty the value list
            valueList.clear();

            // Get the convert value
            convertValue = valueMap.get(CURRENCY_NAMES[currentIndex]);

            // Populate a new value list
            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setMinimumFractionDigits(digits);
            numberFormat.setMaximumFractionDigits(digits);
            for (String name : nameList)
            {
                int index = currencyNameList.indexOf(name);

                Double value = (currentValue / convertValue) *
                               valueMap.get(name);

                String s = numberFormat.format(value);

                valueList.add(s);
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
