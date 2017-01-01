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
import android.app.Fragment;
import android.os.Bundle;
import android.content.Context;
import android.os.AsyncTask;

import java.util.Map;

// DataFragment class

public class DataFragment extends Fragment
{
    // Data objects we want to retain
    private Map<String, Map<String,Double>> data;
    private Map<String,Double> map;
    private TaskCallbacks callbacks;

    // This method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Retain this fragment
        setRetainInstance(true);
    }

    // Hold a reference to the parent Activity so we can report the
    // task's current progress and results. The Android framework will
    // pass us a reference to the newly created Activity after each
    // configuration change.
    @Override
    public void onAttach(Activity activity)
    {
	super.onAttach(activity);
	if (activity instanceof ChartActivity)
	    callbacks = (TaskCallbacks)activity;
    }

    // Set the callback to null so we don't accidentally leak the
    // Activity instance.
    @Override
    public void onDetach()
    {
	super.onDetach();
	callbacks = null;
    }

    // Set data
    public void setData(Map<String, Map<String,Double>> data)
    {
        this.data = data;
    }

    // Set map
    public void setMap(Map<String,Double> map)
    {
        this.map = map;
    }

    // Get data
    public Map<String, Map<String,Double>> getData()
    {
        return data;
    }

    // Get map
    public Map<String,Double> getMap()
    {
        return map;
    }

    // Start parse task
    protected void startParseTask(String url)
    {
	ParseTask parseTask = new ParseTask();
	parseTask.execute(url);
    }

    // ParseTask class
    protected class ParseTask
	extends AsyncTask<String, String, Map<String, Map<String, Double>>>
    {
	Context context;

	// Constructor
	protected ParseTask()
	{
	}

	// The system calls this to perform work in a worker thread
	// and delivers it the parameters given to AsyncTask.execute()
	@Override
	    protected Map doInBackground(String... urls)
	{
	    // Get a parser
	    ChartParser parser = new ChartParser();

	    // Start the parser and report progress with the date
	    if (parser.startParser(urls[0]) == true)
		publishProgress(parser.getDate());

	    // Return the map
	    return parser.getMap();
	}

	// Ignoring the date as not used
	@Override
	    protected void onProgressUpdate(String... date)
	{
	    if (callbacks != null)
		callbacks.onProgressUpdate(date);
	}

	// The system calls this to perform work in the UI thread and
	// delivers the result from doInBackground()
	@Override
	    protected void onPostExecute(Map<String, Map<String,Double>> map)
	{
	    if (callbacks != null)
		callbacks.onPostExecute(map);
	}
    }

    // TaskCallbacks interface
    interface TaskCallbacks
    {
	// void onPreExecute();
	void onProgressUpdate(String... date);
	// void onCancelled();
	void onPostExecute(Map<String, Map<String,Double>> map);
    }
}
