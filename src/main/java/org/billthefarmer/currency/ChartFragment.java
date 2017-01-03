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
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;
import java.util.Map;

// ChartFragment class

public class ChartFragment extends Fragment
{
    public static final String TAG = "ChartFragment";
    // Data objects we want to retain
    private Map<String, Map<String,Double>> map;
    private List<Integer> list;
    private TaskCallbacks callbacks;

    private int first;
    private int second;

    private boolean parsing;

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

    // Set first
    public void setFirst(int first)
    {
	this.first = first;
    }

    // Get first
    public int getFirst()
    {
	return first;
    }

    // Set list
    public void setList(List<Integer> list)
    {
	this.list = list;
    }

    // Get list
    public List<Integer> getList()
    {
	return list;
    }

    // Set map
    public void setMap(Map<String, Map<String,Double>> map)
    {
        this.map = map;
    }

    // Get map
    public Map<String, Map<String,Double>> getMap()
    {
        return map;
    }

    // Is parsing
    public boolean isParsing()
    {
	return parsing;
    }

    // Start parse task
    protected void startParseTask(String url)
    {
	ParseTask parseTask = new ParseTask();
	parseTask.execute(url);

	parsing = true;
    }

    // ParseTask class
    protected class ParseTask
	extends AsyncTask<String, String, Map<String, Map<String, Double>>>
    {
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
	    parsing = false;

	    if (callbacks != null)
		callbacks.onPostExecute(map);
	}
    }

    // TaskCallbacks interface
    interface TaskCallbacks
    {
	void onProgressUpdate(String... date);
	void onPostExecute(Map<String, Map<String,Double>> map);
    }
}
