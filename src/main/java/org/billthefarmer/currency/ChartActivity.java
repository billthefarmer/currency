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
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultFillFormatter;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.NumberFormat;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

// ChartActivity class

public class ChartActivity extends Activity
{
    public static final String TAG = "Chart";
    public static final String DATA_TAG = "data";

    public static final String ECB_QUARTER_URL =
	"http://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist-90d.xml";
    public static final String ECB_HIST_URL =
	"http://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist.xml";

    public static final long MSEC_DAY = 1000 * 60 * 60 * 24;

    private DataFragment dataFragment;

    private TextView currentView;

    private LineChart chart;

    private Map<String, Map<String,Double>> histMap;

    private List<Entry> entryList;
    private LineDataSet dataSet;
    private LineData lineData;

    private boolean wifi = true;
    private boolean roaming = false;

    private int current;
    private int currency;

    private String currentName;
    private String currencyName;

    // On create

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart);

        // Find the retained fragment on activity restarts
        FragmentManager fm = getFragmentManager();
        dataFragment = (DataFragment) fm.findFragmentByTag(DATA_TAG);

        // Create the fragment the first time
        if (dataFragment == null)
	{
            // add the fragment
            dataFragment = new DataFragment();
            fm.beginTransaction().add(dataFragment, DATA_TAG).commit();
        }

	// Enable back navigation on action bar
	ActionBar actionBar = getActionBar();
	if (actionBar != null)
	{
	    actionBar.setDisplayHomeAsUpEnabled(true);

	    // Set custom view
	    actionBar.setCustomView(R.layout.text);
	    actionBar.setDisplayShowCustomEnabled(true);

	    currentView = (TextView)actionBar.getCustomView();
	}

	chart = (LineChart)findViewById(R.id.chart);

	Resources resources = getResources();

	int dark = resources.getColor(android.R.color.secondary_text_dark);
	String updating = resources.getString(R.string.updating);

	chart.setNoDataText(updating);
	chart.setNoDataTextColor(dark);

	chart.setAutoScaleMinMaxEnabled(true);
	chart.setKeepPositionOnRotation(true);

	XAxis xAxis = chart.getXAxis();
	xAxis.setValueFormatter(new dateAxisValueFormatter());
	xAxis.setGranularity(1f);
	xAxis.setTextColor(dark);

	YAxis leftAxis = chart.getAxisLeft();
	leftAxis.setTextColor(dark);

	YAxis rightAxis = chart.getAxisRight();
	rightAxis.setTextColor(dark);

	Legend legend = chart.getLegend();
	legend.setEnabled(false);

	Description description = chart.getDescription();
	description.setEnabled(false);

	Intent intent = getIntent();

	current = intent.getIntExtra(Main.CHART_CURRENT, 0);
	currency = intent.getIntExtra(Main.CHART_CURRENCY, 0);

	currentName = Main.CURRENCY_NAMES[current];
	currencyName = Main.CURRENCY_NAMES[currency];

	String label = currencyName + "/" + currentName;
	currentView.setText(label);
    }

    // On resume

    @Override
    protected void onResume()
    {
	super.onResume();

	// Get preferences
	SharedPreferences preferences =
 	    PreferenceManager.getDefaultSharedPreferences(this);

	wifi = preferences.getBoolean(Main.PREF_WIFI, true);
	roaming = preferences.getBoolean(Main.PREF_ROAMING, false);

	// Get data fragment
	if (dataFragment != null)
	    histMap = dataFragment.getData();

	// Check retained data
	if (histMap != null)
	{
	    SimpleDateFormat dateParser =
		new SimpleDateFormat(Main.DATE_FORMAT, Locale.getDefault());
	    Resources resources = getResources();

	    entryList = new ArrayList<Entry>();

	    for (String key: histMap.keySet())
	    {
		float day = 0;

		try
		{
		    Date date = dateParser.parse(key);
		    day = date.getTime() / MSEC_DAY;
		}

		catch (Exception e) {}

		Map<String, Double> entryMap = histMap.get(key);

		double current = entryMap.get(currentName);
		double currency = entryMap.get(currencyName);

		float value = (float)(current / currency);

		entryList.add(0, new Entry(day, value));
	    }

	    int bright = resources.getColor(android.R.color.holo_blue_bright);

	    dataSet = new LineDataSet(entryList, currencyName);

	    dataSet.setDrawCircles(false);
	    dataSet.setDrawValues(false);
	    dataSet.setColor(bright);

	    lineData = new LineData(dataSet);

	    chart.setData(lineData);
	    chart.invalidate();
	    return;
	}

	// Check connectivity before update
	ConnectivityManager manager =
	    (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
	NetworkInfo info = manager.getActiveNetworkInfo();

	// Update online
	if (info == null || !info.isConnected())
	    return;

	if (wifi && info.getType() != ConnectivityManager.TYPE_WIFI)
	    return;

	if (!roaming && info.isRoaming())
	    return;

	ParseTask parseTask = new ParseTask(this);
	parseTask.execute(ECB_QUARTER_URL);
    }

    // On pause

    @Override
    protected void onPause()
    {
	super.onPause();
    }

    // On destroy

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        // store the data in the fragment
        dataFragment.setData(histMap);
    }

    // On create options menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
	// Inflate the menu; this adds items to the action bar if it
	// is present.

	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.chart, menu);

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
	    // Home

	case android.R.id.home:
	    finish();
	    break;

	case R.id.action_invert:
	    return onInvertClick();

	case R.id.action_refresh:
	    return onRefreshClick(ECB_QUARTER_URL);

	case R.id.action_years:
	    return onRefreshClick(ECB_HIST_URL);

	default:
	    return false;
	}

	return true;
    }

    // on invert click
    private boolean onInvertClick()
    {
	SimpleDateFormat dateParser =
	    new SimpleDateFormat(Main.DATE_FORMAT, Locale.getDefault());
	Resources resources = getResources();

	int index = current;
	current = currency;
	currency = index;

	currentName = Main.CURRENCY_NAMES[current];
	currencyName = Main.CURRENCY_NAMES[currency];

	String label = currencyName + "/" + currentName;
	currentView.setText(label);

	entryList.clear();

	for (String key: histMap.keySet())
	{
	    float day = 0;

	    try
	    {
		Date date = dateParser.parse(key);
		day = date.getTime() / MSEC_DAY;
	    }

	    catch (Exception e) {}

	    Map<String, Double> entryMap = histMap.get(key);

	    double current = entryMap.get(currentName);
	    double currency = entryMap.get(currencyName);

	    float value = (float)(current / currency);

	    entryList.add(0, new Entry(day, value));
	}

	dataSet.setValues(entryList);
	lineData.notifyDataChanged();
	chart.notifyDataSetChanged();
	chart.invalidate();

	return true;
    }

    // on refresh click
    private boolean onRefreshClick(String url)
    {
	// Check connectivity before update
	ConnectivityManager manager =
	    (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
	NetworkInfo info = manager.getActiveNetworkInfo();

	// Update online
	if (info == null || !info.isConnected())
	    return false;

	if (wifi && info.getType() != ConnectivityManager.TYPE_WIFI)
	    return false;

	if (!roaming && info.isRoaming())
	    return false;

	Resources resources = getResources();
	String updating = resources.getString(R.string.updating);

	currentView.setText(updating);

	ParseTask parseTask = new ParseTask(this);
	parseTask.execute(url);

	return true;
    }

    // ParseTask class

    private class ParseTask
	extends AsyncTask<String, String, Map<String, Map<String, Double>>>
    {
	Context context;

	private ParseTask(Context context)
	{
	    this.context = context;
	}

	// The system calls this to perform work in a worker thread
	// and delivers it the parameters given to AsyncTask.execute()
	@Override
	protected Map doInBackground(String... urls)
	{
	    ChartParser parser = new ChartParser();

	    if (parser.startParser(urls[0]) == true)
		publishProgress(parser.getDate());

	    return parser.getMap();
	}

	@Override
	protected void onProgressUpdate(String... date) {}

	// The system calls this to perform work in the UI thread and
	// delivers the result from doInBackground()
	@Override
	protected void onPostExecute(Map<String, Map<String,Double>> map)
	{
	    // Check map
	    if (map == null)
		return;

	    // Save map
	    histMap = map;

	    SimpleDateFormat dateParser =
		new SimpleDateFormat(Main.DATE_FORMAT, Locale.getDefault());
	    Resources resources = context.getResources();

	    entryList = new ArrayList<Entry>();

	    for (String key: map.keySet())
	    {
		float day = 0;

		try
		{
		    Date date = dateParser.parse(key);
		    day = date.getTime() / MSEC_DAY;
		}

		catch (Exception e) {}

		Map<String, Double> entryMap = map.get(key);

		double current = entryMap.get(currentName);
		double currency = entryMap.get(currencyName);

		float value = (float)(current / currency);

		entryList.add(0, new Entry(day, value));
	    }

	    int bright = resources.getColor(android.R.color.holo_blue_bright);
	    int dark = resources.getColor(android.R.color.secondary_text_dark);

	    dataSet = new LineDataSet(entryList, currencyName);

	    dataSet.setDrawCircles(false);
	    dataSet.setDrawValues(false);
	    dataSet.setColor(bright);

	    lineData = new LineData(dataSet);

	    String label = currencyName + "/" + currentName;
	    currentView.setText(label);

	    chart.setData(lineData);
	    chart.invalidate();
	}
    }

    private class dateAxisValueFormatter implements IAxisValueFormatter
    {
	DateFormat dateFormat;

	private dateAxisValueFormatter()
	{
	    dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
	}

	@Override
	public String getFormattedValue(float value, AxisBase axis)
	{
	    // "value" represents the position of the label on the axis (x or y)
	    Date date = new Date((int)value * MSEC_DAY);
	    return dateFormat.format(date);
	}
    }
}
