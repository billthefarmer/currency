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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.NumberFormat;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Date;
import java.util.List;
import java.util.Map;

// ChartActivity class

public class ChartActivity extends Activity
{
    private LineChart chart;

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

	chart = (LineChart) findViewById(R.id.chart);
    }

    // On resume

    @Override
    protected void onResume()
    {
	super.onResume();
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

	default:
	    return false;
	}

	return true;
    }

    // ParseTask class

    private class ParseTask
	extends AsyncTask<String, Void, Map<String, Double>>
    {
	Context context;
	String latest;

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
		publishProgress((Void)null);

	    return parser.getTable();
	}

	@Override
	protected void onProgressUpdate(Void... data)
	{
	}

	// The system calls this to perform work in the UI thread and
	// delivers the result from doInBackground()
	@Override
	protected void onPostExecute(Map<String, Double> table)
	{
	}
    }
}
