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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
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
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

// ChartActivity class

public class ChartActivity extends Activity
{
    public static final String TAG = "Chart";

    public static final String ECB_QUARTER_URL =
	"http://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist-90d.xml";
    public static final String ECB_HIST_URL =
	"http://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist.xml";

    public static final long MSEC_DAY = 1000 * 60 * 60 * 24;

    private TextView dateView;
    private TextView statusView;
    private TextView currentView;

    private LineChart chart;

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

	// Enable back navigation on action bar
	ActionBar actionBar = getActionBar();
	if (actionBar != null)
	    actionBar.setDisplayHomeAsUpEnabled(true);

	dateView = (TextView)findViewById(R.id.date);
	statusView = (TextView)findViewById(R.id.status);
	currentView = (TextView)findViewById(R.id.current);

	chart = (LineChart) findViewById(R.id.chart);

	Intent intent = getIntent();

	current = intent.getIntExtra(Main.CHART_CURRENT, 0);
	currency = intent.getIntExtra(Main.CHART_CURRENCY, 0);

	currentName = Main.CURRENCY_NAMES[current];
	currencyName = Main.CURRENCY_NAMES[currency];

	String label = currencyName + " / " + currentName;
	currentView.setText(label);
    }

    // On resume

    @Override
    protected void onResume()
    {
	super.onResume();

	// Don't refresh if list populated
	if (entryList != null && !entryList.isEmpty())
	    return;

	// Get preferences
	SharedPreferences preferences =
 	    PreferenceManager.getDefaultSharedPreferences(this);

	wifi = preferences.getBoolean(Main.PREF_WIFI, true);
	roaming = preferences.getBoolean(Main.PREF_ROAMING, false);

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
	parseTask.execute(ECB_QUARTER_URL);
    }

    // On pause

    @Override
    protected void onPause()
    {
	super.onPause();
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

	case R.id.action_refresh:
	    return onRefreshClick(ECB_QUARTER_URL);

	case R.id.action_years:
	    return onRefreshClick(ECB_HIST_URL);

	default:
	    return false;
	}

	return true;
    }

    // on refresh click
    private boolean onRefreshClick(String url)
    {
	// Get preferences
	SharedPreferences preferences =
 	    PreferenceManager.getDefaultSharedPreferences(this);

	wifi = preferences.getBoolean(Main.PREF_WIFI, true);
	roaming = preferences.getBoolean(Main.PREF_ROAMING, false);

	// Check connectivity before update
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
		publishProgress(parser.getLatest());

	    return parser.getTable();
	}

	@Override
	protected void onProgressUpdate(String... date)
	{
	    SimpleDateFormat dateParser =
		new SimpleDateFormat(Main.DATE_FORMAT, Locale.getDefault());
	    DateFormat dateFormat =
		DateFormat.getDateInstance(DateFormat.MEDIUM);

	    Date update = new Date();
	    String latest = dateFormat.format(update);

	    Resources resources = context.getResources();

	    String format = resources.getString(R.string.updated);
	    String updated = String.format(Locale.getDefault(),
					   format, latest);
	    dateView.setText(updated);
	    statusView.setText(R.string.ok);
	}

	// The system calls this to perform work in the UI thread and
	// delivers the result from doInBackground()
	@Override
	protected void onPostExecute(Map<String, Map<String,Double>> table)
	{
	    SimpleDateFormat dateParser =
		new SimpleDateFormat(Main.DATE_FORMAT, Locale.getDefault());
	    Resources resources = context.getResources();

	    entryList = new ArrayList<Entry>();

	    for (String key: table.keySet())
	    {
		float day = 0;

		try
		{
		    Date date = dateParser.parse(key);
		    day = date.getTime() / MSEC_DAY;
		}

		catch (Exception e) {}

		Map<String, Double> entryMap = table.get(key);

		double current = entryMap.get(currentName);
		double currency = entryMap.get(currencyName);

		float value = (float)(current / currency);

		entryList.add(0, new Entry(day, value));
	    }

	    int dark = resources.getColor(android.R.color.secondary_text_dark);
	    int bright = resources.getColor(android.R.color.holo_blue_bright);

	    dataSet = new LineDataSet(entryList, currencyName);

	    dataSet.setDrawCircles(false);
	    dataSet.setDrawValues(false);
	    dataSet.setColor(bright);

	    lineData = new LineData(dataSet);

	    // chart.setDrawBorders(true);
	    // chart.setBorderColor(dark);

	    chart.setAutoScaleMinMaxEnabled(true);
	    chart.setKeepPositionOnRotation(true);
	    chart.setDescription(null);

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
