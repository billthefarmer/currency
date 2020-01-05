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
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

// ChartActivity class
public class ChartActivity extends Activity
    implements Singleton.TaskCallbacks
{
    public static final String TAG = "ChartActivity";

    public static final String INVERT = "invert";
    public static final String RANGE = "range";

    public static final String ECB_QUARTER_URL =
        "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist-90d.xml";
    public static final String ECB_HIST_URL =
        "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist.xml";

    public static final long MSEC_DAY = 1000 * 60 * 60 * 24;

    public static final int WEEK = 7;
    public static final int MONTH = 30;
    public static final int QUARTER = 90;
    public static final int YEAR = 365;
    public static final int YEARS = 1825;
    public static final int MAX = Integer.MAX_VALUE;

    private Singleton instance;
    private TextView customView;
    private LineChart chart;

    private Map<String, Map<String, Double>> histMap;

    private List<Entry> entryList;
    private LineDataSet dataSet;
    private LineData lineData;

    private boolean wifi = true;
    private boolean roaming = false;
    private boolean fill = true;

    private int firstIndex;
    private int secondIndex;

    private String firstName;
    private String secondName;

    private boolean invert;
    private int range = Integer.MAX_VALUE;

    // On create
    @Override
    @SuppressWarnings("deprecation")
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Get preferences
        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(this);

        boolean theme = preferences.getBoolean(Main.PREF_DARK, true);

        if (!theme)
            setTheme(R.style.AppLightTheme);

        setContentView(R.layout.chart);

        if (savedInstanceState != null)
        {
            invert = savedInstanceState.getBoolean(INVERT);
            range = savedInstanceState.getInt(RANGE);
        }

        List<Integer> list;
        // Check singleton instance
        if (instance == null)
        {
            // Get singleton instance
            instance = Singleton.getInstance(this);

            // Get the intent for the parameters
            Intent intent = getIntent();
            list = intent.getIntegerArrayListExtra(Main.CHART_LIST);
        }

        else
        {
            // Get list from singleton instance
            list = instance.getList();
        }

        // Iterate through the list to get the last two
        if (list != null)
        {
            for (int index : list)
            {
                firstIndex = secondIndex;
                secondIndex = index;
            }
        }

        // Reverse currency indices
        if (invert)
        {
            int index = firstIndex;
            firstIndex = secondIndex;
            secondIndex = index;
        }

        // Look up the names
        firstName = Main.CURRENCY_NAMES[firstIndex];
        secondName = Main.CURRENCY_NAMES[secondIndex];

        // Enable back navigation on action bar
        ActionBar actionBar = getActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);

            // Set custom view
            actionBar.setCustomView(R.layout.text);
            actionBar.setDisplayShowCustomEnabled(true);

            // Get custom view
            customView = (TextView) actionBar.getCustomView();
        }

        // Get chart
        chart = findViewById(R.id.chart);

        // Get text and colour
        Resources resources = getResources();
        String updating = getString(R.string.updating);
        int dark = resources.getColor(android.R.color.secondary_text_dark);

        // Set chart parameters
        if (chart != null)
        {
            // Set the no data text and colour, only seen once
            chart.setNoDataText(updating);
            chart.setNoDataTextColor(dark);

            // Set auto scaling
            chart.setAutoScaleMinMaxEnabled(true);
            chart.setKeepPositionOnRotation(true);

            // Set date formatter for x axis and colour
            XAxis xAxis = chart.getXAxis();
            xAxis.setValueFormatter(new DateAxisValueFormatter());
            xAxis.setGranularity(1f);
            xAxis.setTextColor(dark);

            // Set y axis colour
            YAxis leftAxis = chart.getAxisLeft();
            leftAxis.setTextColor(dark);

            YAxis rightAxis = chart.getAxisRight();
            // Set y axis colour
            rightAxis.setTextColor(dark);

            // No legend - only one dataset
            Legend legend = chart.getLegend();
            legend.setEnabled(false);

            // No description
            Description description = chart.getDescription();
            description.setEnabled(false);
        }

        // Set listener
        if (customView != null)
            // onClick
            customView.setOnClickListener(v ->
        {
            if (histMap != null)
                onInvertClick();
        });

        // Check singleton instance
        if (instance != null && instance.isParsing())
        {
            // Generate the label
            if (customView != null)
                customView.setText(updating);
        }

        else
        {
            // Generate the label
            String label = secondName + "/" + firstName;
            if (customView != null)
                customView.setText(label);
        }
    }

    // On resume
    @Override
    @SuppressWarnings("deprecation")
    protected void onResume()
    {
        super.onResume();

        // Get preferences
        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(this);

        wifi = preferences.getBoolean(Main.PREF_WIFI, true);
        roaming = preferences.getBoolean(Main.PREF_ROAMING, false);
        fill = preferences.getBoolean(Main.PREF_FILL, true);

        // Connect callbacks
        instance = Singleton.getInstance(this);

        // Check singleton instance
        if (instance != null)
            histMap = instance.getMap();

        // Check retained data
        if (histMap != null)
        {
            SimpleDateFormat dateParser =
                new SimpleDateFormat(Main.DATE_FORMAT, Locale.getDefault());
            Resources resources = getResources();

            // Create the entry list
            entryList = new ArrayList<>();

            // Iterate through the dates
            for (String key : histMap.keySet())
            {
                float day;

                // Parse the date and turn it into a day number
                try
                {
                    Date date = dateParser.parse(key);
                    day = date.getTime() / MSEC_DAY;
                }

                // Ignore invalid dates
                catch (Exception e)
                {
                    continue;
                }

                // Get the map for each date
                Map<String, Double> entryMap = histMap.get(key);
                float value;

                // Get the value for each date
                try
                {
                    double first = entryMap.get(firstName);
                    double second = entryMap.get(secondName);
                    value = (float) (first / second);
                }

                // Ignore missing values
                catch (Exception e)
                {
                    continue;
                }

                // Add the entry to the list
                entryList.add(0, new Entry(day, value));
            }

            // Get the colour
            int bright = resources.getColor(android.R.color.holo_blue_bright);
            int dark = resources.getColor(android.R.color.holo_blue_dark);

            // Check the chart
            if (chart != null)
            {
                // Create the dataset
                dataSet = new LineDataSet(entryList, secondName);

                // Set dataset parameters and colour
                dataSet.setDrawCircles(false);
                dataSet.setDrawValues(false);
                dataSet.setColor(bright);

                // Check preference
                if (fill)
                {
                    dataSet.setFillColor(dark);
                    dataSet.setDrawFilled(true);
                }

                // Add the data to the chart and refresh
                lineData = new LineData(dataSet);
                chart.setData(lineData);
                chart.invalidate();

                // Get todays date
                Date today = new Date();
                // Get the start date
                long start = (today.getTime() / MSEC_DAY) - range;

                // Reset chart
                chart.fitScreen();
                // Set the range
                chart.setVisibleXRangeMaximum(range);
                chart.moveViewToX(start);
            }

            // Update menu
            invalidateOptionsMenu();

            // Don't do an online update
            return;
        }

        // Check connectivity before update
        ConnectivityManager manager =
            (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        // Check connection
        if (info == null || !info.isConnected())
        {
            showToast(R.string.no_connection);
            return;
        }

        // Check wifi
        if (wifi && info.getType() != ConnectivityManager.TYPE_WIFI)
        {
            showToast(R.string.no_wifi);
            return;
        }

        // Check roaming
        if (!roaming && info.isRoaming())
        {
            showToast(R.string.roaming);
            return;
        }

        // Schedule the update
        if (instance != null)
            instance.startParseTask(ECB_QUARTER_URL);
    }

    // On pause
    @Override
    protected void onPause()
    {
        super.onPause();

        // Store the indices and historical data in the singleton
        // instance
        if (instance != null)
        {
            List<Integer> list = new ArrayList<>();
            list.add(firstIndex);
            list.add(secondIndex);
            instance.setList(list);
            instance.setMap(histMap);
        }

        // Disconnect callbacks
        instance = Singleton.getInstance(null);
    }

    // onSaveInstanceState
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putBoolean(INVERT, invert);
        outState.putInt(RANGE, range);
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

    // onPrepareOptionsMenu
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        // Invert
        menu.findItem(R.id.action_invert).setEnabled(histMap != null);

        // Range
        switch (range)
        {
        case WEEK:
            menu.findItem(R.id.action_week).setChecked(true);
            break;

        case MONTH:
            menu.findItem(R.id.action_month).setChecked(true);
            break;

        case QUARTER:
            menu.findItem(R.id.action_quarter).setChecked(true);
            break;

        case YEAR:
            menu.findItem(R.id.action_year).setChecked(true);
            break;

        case YEARS:
            menu.findItem(R.id.action_years).setChecked(true);
            break;

        case MAX:
            menu.findItem(R.id.action_max).setChecked(true);
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
        // Home
        case android.R.id.home:
            finish();
            break;

        // Invert chart
        case R.id.action_invert:
            return onInvertClick();

        // New chart
        case R.id.action_new_chart:
            return onNewClick();

        // Refresh chart
        case R.id.action_refresh:
            return onRefreshClick(ECB_QUARTER_URL);

        // Refresh with historical data
        case R.id.action_hist:
            return onRefreshClick(ECB_HIST_URL);

        // Week
        case R.id.action_week:
            return onWeekClick(item);

        // Month
        case R.id.action_month:
            return onMonthClick(item);

        // Quarter
        case R.id.action_quarter:
            return onQuarterClick(item);

        // Year
        case R.id.action_year:
            return onYearClick(item);

        // Years
        case R.id.action_years:
            return onYearsClick(item);

        // Max
        case R.id.action_max:
            return onMaxClick(item);

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

        // Get updating text
        String updating = getString(R.string.updating);

        // Set flag
        invert = !invert;

        // Reverse currency indices
        int index = firstIndex;
        firstIndex = secondIndex;
        secondIndex = index;

        // Update names
        firstName = Main.CURRENCY_NAMES[firstIndex];
        secondName = Main.CURRENCY_NAMES[secondIndex];

        // Set custom text to updating, since this may take a few secs
        if (customView != null)
            customView.setText(updating);

        // Clear the entry list
        entryList.clear();

        // Iterate through the dates
        for (String key : histMap.keySet())
        {
            float day = 0;

            // Parse the date and turn it into a day number
            try
            {
                Date date = dateParser.parse(key);
                day = date.getTime() / MSEC_DAY;
            }

            // Ignore invalid dates
            catch (Exception e)
            {
                continue;
            }

            // Get the map for each date
            Map<String, Double> entryMap = histMap.get(key);
            float value;

            // Get the value for each date
            try
            {
                double first = entryMap.get(firstName);
                double second = entryMap.get(secondName);
                value = (float) (first / second);
            }

            // Ignore missing values
            catch (Exception e)
            {
                continue;
            }

            // Add the entry to the list
            entryList.add(0, new Entry(day, value));
        }

        // Check the chart
        if (chart != null)
        {
            // Add the data to the chart and refresh
            dataSet.setValues(entryList);
            lineData.notifyDataChanged();
            chart.notifyDataSetChanged();
            chart.invalidate();

            // Get todays date
            Date today = new Date();
            // Get the start date
            long start = (today.getTime() / MSEC_DAY) - range;

            // Reset chart
            chart.fitScreen();
            // Set the range
            chart.setVisibleXRangeMaximum(range);
            chart.moveViewToX(start);
        }

        // Restore the custom view to the current currencies
        String label = secondName + "/" + firstName;
        if (customView != null)
            customView.setText(label);

        return true;
    }

    // On new click
    private boolean onNewClick()
    {
        // Start the choice dialog
        Intent intent = new Intent(this, ChoiceDialog.class);
        startActivityForResult(intent, 0);

        return true;
    }

    // On refresh click
    @SuppressWarnings("deprecation")
    private boolean onRefreshClick(String url)
    {
        // Check connectivity before update
        ConnectivityManager manager =
            (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        // Check connection
        if (info == null || !info.isConnected())
        {
            showToast(R.string.no_connection);
            return false;
        }

        // Check wifi
        if (wifi && info.getType() != ConnectivityManager.TYPE_WIFI)
        {
            showToast(R.string.no_wifi);
            return false;
        }

        // Check roaming
        if (!roaming && info.isRoaming())
        {
            showToast(R.string.roaming);
            return false;
        }

        // Get updating text
        String updating = getString(R.string.updating);

        // Set custom text to updating, since this may take a few secs
        if (customView != null)
            customView.setText(updating);

        // Schedule the update
        if (instance != null)
            instance.startParseTask(url);

        return true;
    }

    // onWeekClick
    private boolean onWeekClick(MenuItem item)
    {
        range = WEEK;
        item.setChecked(true);
        updateRange();

        return true;
    }

    // onMonthClick
    private boolean onMonthClick(MenuItem item)
    {
        range = MONTH;
        item.setChecked(true);
        updateRange();

        return true;
    }

    // onQuarterClick
    private boolean onQuarterClick(MenuItem item)
    {
        range = QUARTER;
        item.setChecked(true);
        updateRange();

        return true;
    }

    // onYearClick
    private boolean onYearClick(MenuItem item)
    {
        range = YEAR;
        item.setChecked(true);
        updateRange();

        return true;
    }

    // onYearsClick
    private boolean onYearsClick(MenuItem item)
    {
        range = YEARS;
        item.setChecked(true);
        updateRange();

        return true;
    }

    // onMaxClick
    private boolean onMaxClick(MenuItem item)
    {
        range = MAX;
        item.setChecked(true);
        updateRange();

        return true;
    }

    // updateRange
    private void updateRange()
    {
        // Check the chart
        if (chart != null)
        {
            // Get todays date
            Date today = new Date();
            // Get the start date
            long start = (today.getTime() / MSEC_DAY) - range;

            // Reset chart
            chart.fitScreen();
            // Set the range
            chart.setVisibleXRangeMaximum(range);
            chart.moveViewToX(start);
        }
    }

    // On activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data)
    {
        // Do nothing if cancelled
        if (resultCode != RESULT_OK)
            return;

        SimpleDateFormat dateParser =
            new SimpleDateFormat(Main.DATE_FORMAT, Locale.getDefault());

        // Get updating text
        String updating = getString(R.string.updating);

        // Get index list from intent
        List<Integer> selectList = data.getIntegerArrayListExtra(Main.CHOICE);

        // Iterate through the list to get the last two
        for (int index : selectList)
        {
            firstIndex = secondIndex;
            secondIndex = index;
        }

        // Update names
        firstName = Main.CURRENCY_NAMES[firstIndex];
        secondName = Main.CURRENCY_NAMES[secondIndex];

        // Set custom text to updating, since this may take a few secs
        if (customView != null)
            customView.setText(updating);

        // Clear the entry list
        entryList.clear();

        // Iterate through the dates
        for (String key : histMap.keySet())
        {
            float day;

            // Parse the date and turn it into a day number
            try
            {
                Date date = dateParser.parse(key);
                day = date.getTime() / MSEC_DAY;
            }

            // Ignore invalid dates
            catch (Exception e)
            {
                continue;
            }

            // Get the map for each date
            Map<String, Double> entryMap = histMap.get(key);
            float value;

            // Get the value for each date
            try
            {
                double first = entryMap.get(firstName);
                double second = entryMap.get(secondName);
                value = (float) (first / second);
            }

            // Ignore missing values
            catch (Exception e)
            {
                continue;
            }

            // Add the entry to the list
            entryList.add(0, new Entry(day, value));
        }

        // Check the chart
        if (chart != null)
        {
            // Add the data to the chart and refresh
            dataSet.setValues(entryList);
            lineData.notifyDataChanged();
            chart.notifyDataSetChanged();
            chart.invalidate();

            // Get todays date
            Date today = new Date();
            // Get the start date
            long start = (today.getTime() / MSEC_DAY) - range;

            // Reset chart
            chart.fitScreen();
            // Set the range
            chart.setVisibleXRangeMaximum(range);
            chart.moveViewToX(start);
        }

        // Restore the custom view to the current currencies
        String label = secondName + "/" + firstName;
        if (customView != null)
            customView.setText(label);
    }

    // Ignoring the date as not used
    @Override
    public void onProgressUpdate(String... date)
    {
    }

    // The system calls this to perform work in the UI thread and
    // delivers the result from doInBackground()
    @Override
    @SuppressWarnings("deprecation")
    public void onPostExecute(Map<String, Map<String, Double>> map)
    {
        // Check map
        if (!map.isEmpty())
        {
            // Save map
            histMap = map;

            SimpleDateFormat dateParser =
                new SimpleDateFormat(Main.DATE_FORMAT, Locale.getDefault());
            Resources resources = getResources();

            // Create a new entry list
            entryList = new ArrayList<>();

            // Iterate through the dates
            for (String key : map.keySet())
            {
                float day;

                // Parse the date and turn it into a day number
                try
                {
                    Date date = dateParser.parse(key);
                    day = date.getTime() / MSEC_DAY;
                }

                // Ignore invalid dates
                catch (Exception e)
                {
                    continue;
                }

                // Get the map for each date
                Map<String, Double> entryMap = map.get(key);
                float value;

                // Get the value for each date
                try
                {
                    double first = entryMap.get(firstName);
                    double second = entryMap.get(secondName);
                    value = (float) (first / second);
                }

                // Ignore missing values
                catch (Exception e)
                {
                    continue;
                }

                // Add the entry to the list
                entryList.add(0, new Entry(day, value));
            }

            // Get the colour
            int bright = resources.getColor(android.R.color.holo_blue_bright);
            int dark = resources.getColor(android.R.color.holo_blue_dark);

            // Check the chart
            if (chart != null)
            {
                // Create the dataset
                dataSet = new LineDataSet(entryList, secondName);

                // Set dataset parameters and colour
                dataSet.setDrawCircles(false);
                dataSet.setDrawValues(false);
                dataSet.setColor(bright);

                // Check preference
                if (fill)
                {
                    dataSet.setFillColor(dark);
                    dataSet.setDrawFilled(true);
                }

                // Update chart
                lineData = new LineData(dataSet);
                chart.setData(lineData);
                chart.invalidate();

                // Get todays date
                Date today = new Date();
                // Get the start date
                long start = (today.getTime() / MSEC_DAY) - range;

                // Reset chart
                chart.fitScreen();
                // Set the range
                chart.setVisibleXRangeMaximum(range);
                chart.moveViewToX(start);
            }

            // Update menu
            invalidateOptionsMenu();
        }
        else
        {
            // Show failed
            showToast(R.string.update_failed);
        }

        // Restore the custom view to the current currencies
        String label = secondName + "/" + firstName;
        if (customView != null)
            customView.setText(label);
    }

    // Show toast
    void showToast(int id, Object... args)
    {
        // Get text from resources
        String text = getString(id);
        showToast(text, args);
    }

    // Show toast
    void showToast(String format, Object... args)
    {
        String text = String.format(format, args);
        showToast(text);
    }

    // Show toast
    void showToast(String text)
    {
        // Make a new toast
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    // DateAxisValueFormatter class
    private class DateAxisValueFormatter extends ValueFormatter
    {
        DateFormat dateFormat;

        // Constructor
        private DateAxisValueFormatter()
        {
            dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        }

        // Get formatted value
        @Override
        public String getFormattedValue(float value)
        {
            // "value" represents the position of the label on the
            // axis (x or y). Create a date from the day number and
            // format it.
            Date date = new Date(Math.round(value) * MSEC_DAY);
            return dateFormat.format(date);
        }
    }
}
