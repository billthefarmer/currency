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

import android.os.Handler;
import android.os.Looper;

import java.util.List;
import java.util.Map;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

// Singleton class
@SuppressWarnings("deprecation")
public class Singleton
{
    private static Singleton instance;
    private static Handler handler;
    private static ExecutorService executor;
    private static OnResultListener listener;
    private static boolean parsing;

    private Map<String, Map<String, Double>> map;
    private List<Integer> list;


    // Constructor
    private Singleton()
    {
    }

    // Get instance
    public static Singleton getInstance(OnResultListener listener)
    {
        if (instance == null)
            instance = new Singleton();

        instance.listener = listener;

        // Executor
        executor =  Executors.newSingleThreadExecutor();

        // Handler
        handler = new Handler(Looper.getMainLooper());

        return instance;
    }

    // Get list
    public List<Integer> getList()
    {
        return list;
    }

    // Set list
    public void setList(List<Integer> list)
    {
        this.list = list;
    }

    // Get map
    public Map<String, Map<String, Double>> getMap()
    {
        return map;
    }

    // Set map
    public void setMap(Map<String, Map<String, Double>> map)
    {
        this.map = map;
    }

    // Is parsing
    public static boolean isParsing()
    {
        return parsing;
    }

    // Start parse
    protected static void startParse(String url)
    {
        if (parsing)
            return;

        parsing = true;
        executor.execute(() ->
        {
            // Get a parser
            ChartParser parser = new ChartParser();

            // Start the parser and report the date
            if (!parser.startParser(url))
            {
                parsing = false;
                return;
            }

            parsing = false;
            if (listener != null)
            {
                handler.post(() ->
                {
                    listener.onDateResult(parser.getDate());
                    listener.onDataResult(parser.getMap());
                });
            }
        });
    }

    // OnResultListener interface
    interface OnResultListener
    {
        void onDateResult(String date);
        void onDataResult(Map<String, Map<String, Double>> map);
    }
}
