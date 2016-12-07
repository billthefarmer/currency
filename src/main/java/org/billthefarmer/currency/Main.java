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

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

// Main class

public class Main extends Activity
    implements EditText.OnEditorActionListener,
	       AdapterView.OnItemClickListener,
	       AdapterView.OnItemLongClickListener
{
    // Initial currency name list
    public static final String CURRENCY_LIST[] =
    {
	"USD", "GBP", "CAD", "AUD"
    };

    // Currency names
    public static final String CURRENCY_NAME[] =
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
    public static final String CURRENCY_SYMBOL[] =
    {
	"€", "$", "¥", " ",
	" ", " ", "£", " ",
	" ", " ", " ", " ",
	" ", " ", " ", " ",
	"$", " ", "$", " ",
	"$", " ", " ", " ",
	" ", " ", " ", "$",
	" ", "$", " ", " "
    };

    // Currency long names
    public static final Integer CURRENCY_LONGNAME[] =
    {
	R.string.EUR, R.string.USD, R.string.JPY, R.string.BGN,
	R.string.CZK, R.string.DKK, R.string.GBP, R.string.HUF,
	R.string.PLN, R.string.RON, R.string.SEK, R.string.CHF,
	R.string.NOK, R.string.HRK, R.string.RUB, R.string.TRY,
	R.string.AUD, R.string.BRL, R.string.CAD, R.string.CNY,
	R.string.HKD, R.string.IDR, R.string.ILS, R.string.INR,
	R.string.KRW, R.string.MXN, R.string.MYR, R.string.NZD,
	R.string.PHP, R.string.SGD, R.string.THB, R.string.ZAR
    };
	
    // Currency flags
    public static final Integer CURRENCY_FLAG[] =
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

    public static final String PREF_MAP = "pref_map";
    public static final String PREF_TIME = "pref_time";
    public static final String PREF_NAMES = "pref_names";
    public static final String PREF_INDEX = "pref_index";
    public static final String PREF_VALUE = "pref_value";
    public static final String PREF_VALUES = "pref_values";

    public static final String PREF_WIFI = "pref_wifi";
    public static final String PREF_ROAMING = "pref_roamning";
    public static final String PREF_DIGITS = "pref_digits";
    public static final String PREF_ABOUT = "pref_about";

    public static final String ECB_URL =
	"http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";

    protected final static String CHOICE = "choice";

    public static final int NORMAL_MODE = 0;
    public static final int SELECT_MODE = 1;

    private int mode = NORMAL_MODE;

    private boolean wifi = true;
    private boolean roaming = false;
    private int digits = 3;

    private int currentIndex = 0;
    private double currentValue = 1.0;
    private double convertValue = 1.0;
    private String time;

    private ImageView flagView;
    private TextView nameView;
    private TextView symbolView;
    private EditText editView;
    private TextView longNameView;
    private TextView timeView;
    private TextView statusView;
    private ListView listView;

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

	flagView = (ImageView)findViewById(R.id.flag);
	nameView = (TextView)findViewById(R.id.name);
	symbolView = (TextView)findViewById(R.id.symbol);
	editView = (EditText)findViewById(R.id.edit);
	longNameView = (TextView)findViewById(R.id.long_name);
	timeView = (TextView)findViewById(R.id.time);
	statusView = (TextView)findViewById(R.id.status);
	listView = (ListView)findViewById(R.id.list);

	if (editView != null)
	    editView.setOnEditorActionListener(this);

	if (listView != null)
	{
	    listView.setOnItemClickListener(this);
	    listView.setOnItemLongClickListener(this);
	}

	currencyNameList = Arrays.asList(CURRENCY_NAME);

	flagList = new ArrayList<Integer>();
	symbolList = new ArrayList<String>();
	valueList = new ArrayList<String>();
	longNameList = new ArrayList<Integer>();

	selectList = new ArrayList<Integer>();
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
	digits = Integer.parseInt(preferences.getString(PREF_DIGITS, "3"));

	currentIndex = preferences.getInt(PREF_INDEX, 0);
	currentValue = preferences.getFloat(PREF_VALUE, 1.0f);
	time = preferences.getString(PREF_TIME, "");
	String format = resources.getString(R.string.updated);
	String updated = String.format(format, time);
	timeView.setText(updated);

	// Set current currency
	flagView.setImageResource(CURRENCY_FLAG[currentIndex]);
	nameView.setText(CURRENCY_NAME[currentIndex]);
	symbolView.setText(CURRENCY_SYMBOL[currentIndex]);
	longNameView.setText(CURRENCY_LONGNAME[currentIndex]);

	NumberFormat numberFormat = NumberFormat.getInstance();
	numberFormat.setMinimumFractionDigits(digits);
	numberFormat.setMaximumFractionDigits(digits);
	String value = numberFormat.format(currentValue);
	// String value = String.format("%1.3f", currentValue);
	editView.setText(value);

	// Get saved currency rates and list
	String mapJSON = preferences.getString(PREF_MAP, null);
	String namesJSON = preferences.getString(PREF_NAMES, null);
	String valuesJSON = preferences.getString(PREF_VALUES, null);

	// Check saved rates
	if (mapJSON != null)
	{
	    try
	    {
		JSONObject mapObject = new JSONObject(mapJSON);
		valueMap = new Hashtable<String, Double>();
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
	    Parser parser = new Parser();
	    parser.startParser(this, R.raw.eurofxref_daily);

	    time = parser.getTime();

	    if (time != null)
	    {
		format = resources.getString(R.string.updated);
		updated = String.format(format, time);
		timeView.setText(updated);
	    }

	    else
		statusView.setText(R.string.failed);

	    valueMap = parser.getTable();
	}

	// Check saved name list
	if (namesJSON != null)
	{
	    try
	    {
		JSONArray namesArray = new JSONArray(namesJSON);
		nameList = new ArrayList<String>();
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
	    nameList = new ArrayList<String>(Arrays.asList(CURRENCY_LIST));
	}

	// Get the saved value list
	if (valuesJSON != null)
	{
	    try
	    {
		JSONArray valuesArray = new JSONArray(valuesJSON);
		valueList = new ArrayList<String>();

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
	    numberFormat = NumberFormat.getInstance();
	    numberFormat.setMinimumFractionDigits(digits);
	    numberFormat.setMaximumFractionDigits(digits);
	    for (String s: nameList)
	    {
		Double v = valueMap.get(s);
		value = numberFormat.format(v);
		// value = String.format("%1.3f", v);

		valueList.add(value);
	    }
	}

	// Get the current conversion rate
	convertValue = valueMap.get(CURRENCY_NAME[currentIndex]);

	// Populate the lists
	for (String name: nameList)
	{
	    int index = currencyNameList.indexOf(name);

	    flagList.add(CURRENCY_FLAG[index]);
	    symbolList.add(CURRENCY_SYMBOL[index]);
	    longNameList.add(CURRENCY_LONGNAME[index]);
	}

	// Create the adapter
	adapter = new CurrencyAdapter(this, R.layout.item, flagList, nameList,
				      symbolList, valueList, longNameList);

	// Set the list view adapter
	if (listView != null)
	    listView.setAdapter(adapter);

	// Check connectivity before update
	ConnectivityManager manager =
	    (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
	NetworkInfo info = manager.getActiveNetworkInfo();

	// Update online
	if (info == null || !info.isConnected())
	{
	    statusView.setText(R.string.no_connection);
	    return;
	}

	if (wifi && info.getType() != ConnectivityManager.TYPE_WIFI)
	{
	    statusView.setText(R.string.no_wifi);
	    return;
	}

	if (!roaming && info.isRoaming())
	{
	    statusView.setText(R.string.roaming);
	    return;
	}

	statusView.setText(R.string.updating);
	ParseTask parseTask = new ParseTask(this);
	parseTask.execute(ECB_URL);
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
	editor.putFloat(PREF_VALUE, (float)currentValue);
	editor.putString(PREF_TIME, time);
	editor.apply();
    }

    // On create options menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
	// Inflate the menu; this adds items to the action bar if it
	// is present.

	MenuInflater inflater = getMenuInflater();

	switch (mode)
	{
	case NORMAL_MODE:
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
	case R.id.action_done:
	    return onDoneClick();

	case R.id.action_add:
	    return onAddClick();

	case R.id.action_remove:
	    return onRemoveClick();

	case R.id.action_refresh:
	    return onRefreshClick();

	    // Settings

	case R.id.action_settings:
	    return onSettingsClick();
	}

	return false;
    }

    // on done click

    private boolean onDoneClick()
    {
	mode = NORMAL_MODE;
	invalidateOptionsMenu();

	for (int i: selectList)
	{
	    View v = listView.getChildAt(i);
	    v.setBackgroundResource(0);
	}

	selectList.clear();

	return true;
    }

    // On add click

    private boolean onAddClick()
    {
	Intent intent = new Intent(this, ChoiceDialog.class);
	startActivityForResult(intent, 0);

	return true;
    }

    // On remove click

    private boolean onRemoveClick()
    {
	List<String> removeList = new ArrayList<String>();

	for (int i: selectList)
	{
	    removeList.add(nameList.get(i));

	    View v = listView.getChildAt(i);
	    v.setBackgroundResource(0);
	}

	for (String s: removeList)
	{
	    int i = nameList.indexOf(s);

	    flagList.remove(i);
	    nameList.remove(i);
	    symbolList.remove(i);
	    valueList.remove(i);
	    longNameList.remove(i);
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

	selectList.clear();

	adapter.notifyDataSetChanged();

	mode = NORMAL_MODE;
	invalidateOptionsMenu();

 	return true;
    }

    // On resfresh click

    private boolean onRefreshClick()
    {
	// Check connectivity before refresh
	ConnectivityManager manager =
	    (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
	NetworkInfo info = manager.getActiveNetworkInfo();

	// Update online
	if (info == null || !info.isConnected())
	{
	    statusView.setText(R.string.no_connection);
	    return false;
	}

	if (wifi && info.getType() != ConnectivityManager.TYPE_WIFI)
	{
	    statusView.setText(R.string.no_wifi);
	    return false;
	}

	if (!roaming && info.isRoaming())
	{
	    statusView.setText(R.string.roaming);
	    return false;
	}

	statusView.setText(R.string.updating);
	ParseTask parseTask = new ParseTask(this);
	parseTask.execute(ECB_URL);
	return true;
    }

    // On settings click

    private boolean onSettingsClick()
    {
	Intent intent = new Intent(this, SettingsActivity.class);
	startActivity(intent);

	return true;
    }

    // On editor action

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
    {
	NumberFormat numberFormat = NumberFormat.getInstance();
	numberFormat.setMinimumFractionDigits(digits);
	numberFormat.setMaximumFractionDigits(digits);

	switch (actionId)
	{
        case  EditorInfo.IME_ACTION_DONE:

	    try
	    {
	    	Number number = numberFormat.parse(v.getText().toString());
	    	currentValue = number.doubleValue();
	    }

	    catch (Exception e)
	    {
		e.printStackTrace();
		currentValue = 1.0;
	    }

	    valueList.clear();
	    for (String name: nameList)
	    {
		Double value = (currentValue / convertValue) *
		    valueMap.get(name);

		String s = numberFormat.format(value);
		// String s = String.format("%1.3f", value);
		valueList.add(s);
	    }

	    // Get preferences
	    SharedPreferences preferences =
		PreferenceManager.getDefaultSharedPreferences(this);

	    // Get editor
	    SharedPreferences.Editor editor = preferences.edit();

	    JSONArray valueArray = new JSONArray(valueList);

	    // Update preferences
	    editor.putString(PREF_VALUES, valueArray.toString());
	    editor.putFloat(PREF_VALUE, (float)currentValue);
	    editor.apply();

	    adapter.notifyDataSetChanged();

	    return false;
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

	switch (mode)
	{
	case NORMAL_MODE:
	    oldIndex = currentIndex;
	    oldValue = currentValue;

	    currentIndex = currencyNameList.indexOf(nameList.get(position));

	    currentValue = (oldValue / convertValue) *
		valueMap.get(CURRENCY_NAME[currentIndex]);

	    convertValue = valueMap.get(CURRENCY_NAME[currentIndex]);

	    value = numberFormat.format(currentValue);
	    // value = String.format("%1.3f", currentValue);
	    editView.setText(value);

	    flagView.setImageResource(CURRENCY_FLAG[currentIndex]);
	    nameView.setText(CURRENCY_NAME[currentIndex]);
	    symbolView.setText(CURRENCY_SYMBOL[currentIndex]);
	    longNameView.setText(CURRENCY_LONGNAME[currentIndex]);

	    flagList.remove(position);
	    nameList.remove(position);
	    symbolList.remove(position);
	    valueList.remove(position);
	    longNameList.remove(position);

	    flagList.add(0, CURRENCY_FLAG[oldIndex]);
	    nameList.add(0, CURRENCY_NAME[oldIndex]);
	    symbolList.add(0, CURRENCY_SYMBOL[oldIndex]);
	    longNameList.add(0, CURRENCY_LONGNAME[oldIndex]);

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

	    adapter.notifyDataSetChanged();
	    break;

	case SELECT_MODE:
	    selectList.add(position);
	    view.setBackgroundResource(android.R.color.holo_blue_dark);
	    break;
	}
    }

    // On item long click

    @Override
    public boolean onItemLongClick(AdapterView parent, View view,
				   int position, long id)
    {
	mode = SELECT_MODE;
	invalidateOptionsMenu();

	for (int i: selectList)
	{
	    View v = parent.getChildAt(i);
	    v.setBackgroundResource(0);
	}

	selectList.clear();
	selectList.add(position);

	view.setBackgroundResource(android.R.color.holo_blue_dark);

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

	// Get index from intent
	int index = data.getIntExtra(CHOICE, 0);

	flagList.add(CURRENCY_FLAG[index]);
	nameList.add(CURRENCY_NAME[index]);
	symbolList.add(CURRENCY_SYMBOL[index]);
	longNameList.add(CURRENCY_LONGNAME[index]);

	Double value = (currentValue / convertValue) *
	    valueMap.get(CURRENCY_NAME[index]);

	NumberFormat numberFormat = NumberFormat.getInstance();
	numberFormat.setMinimumFractionDigits(digits);
	numberFormat.setMaximumFractionDigits(digits);
	String s = numberFormat.format(value);
	// String s = String.format("%1.3f", value);

	valueList.add(s);

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

    // ParseTask class

    private class ParseTask
	extends AsyncTask<String, String, Map<String, Double>>
    {
	Context context;
	String time;

	private ParseTask(Context context)
	{
	    this.context = context;
	}

	// The system calls this to perform work in a worker thread
	// and delivers it the parameters given to AsyncTask.execute()
	@Override
	protected Map doInBackground(String... urls)
	{
	    Parser parser = new Parser();

	    if (parser.startParser(urls[0]) == true)
		publishProgress(parser.getTime());

	    return parser.getTable();
	}

	@Override
	protected void onProgressUpdate(String... time)
	{
	    if (time[0] != null)
	    {
		String format = resources.getString(R.string.updated);
		String updated = String.format(format, time[0]);

		timeView.setText(updated);

		this.time = time[0];
	    }

	    else
		statusView.setText(R.string.failed);
	}

	// The system calls this to perform work in the UI thread and
	// delivers the result from doInBackground()
	@Override
	protected void onPostExecute(Map<String, Double> table)
	{
	    if (!table.isEmpty())
	    {
		valueMap = table;

		valueList.clear();

		convertValue = valueMap.get(CURRENCY_NAME[currentIndex]);

		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setMinimumFractionDigits(digits);
		numberFormat.setMaximumFractionDigits(digits);
		for (String name: nameList)
		{
		    int index = currencyNameList.indexOf(name);

		    Double value = (currentValue / convertValue) *
			valueMap.get(name);

		    String s = numberFormat.format(value);
		    // String s = String.format("%1.3f", value);

		    valueList.add(s);
		}

		// Get preferences
		SharedPreferences preferences =
		    PreferenceManager.getDefaultSharedPreferences(context);

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

		editor.putString(PREF_TIME, time);
		editor.apply();

		time = this.time;

		statusView.setText(R.string.ok);
		adapter.notifyDataSetChanged();
	    }

	    else
		statusView.setText(R.string.failed);		
	}
    }
}
