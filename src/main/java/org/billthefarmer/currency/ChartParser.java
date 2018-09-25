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

import android.content.Context;
import android.content.res.Resources;
import android.util.Xml;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

// Parser class
public class ChartParser {
    private Map<String, Map<String, Double>> map;
    private Map<String, Double> entry;
    // Oldest possible date
    private String date = "1970-01-01";

    // Get map
    public Map<String, Map<String, Double>> getMap() {
        return map;
    }

    // Get date
    public String getDate() {
        return date;
    }

    // Start parser for a url
    public boolean startParser(String s) {
        // Create the map
        map = new LinkedHashMap<>();

        // Read the xml from the url
        try {
            URL url = new URL(s);
            InputStream stream = url.openStream();
            Handler handler = new Handler();
            Xml.parse(stream, Xml.Encoding.UTF_8, handler);
            return true;
        } catch (Exception e) {
            map.clear();
        }

        return false;
    }

    // Start parser from resources
    public boolean startParser(Context context, int id) {
        // Create the map
        map = new LinkedHashMap<>();

        Resources resources = context.getResources();

        // Read the xml from the resources
        try {
            InputStream stream = resources.openRawResource(id);
            Handler handler = new Handler();
            Xml.parse(stream, Xml.Encoding.UTF_8, handler);
            return true;
        } catch (Exception e) {
            map.clear();
        }

        return false;
    }

    // Handler class
    private class Handler extends DefaultHandler {
        // Start element
        @Override
        public void startElement(String uri, String localName, String qName,
                                 Attributes attributes) {
            String name = "EUR";
            double rate;

            if (localName.equals("Cube")) {
                for (int i = 0; i < attributes.getLength(); i++) {
                    // Get the date
                    switch (attributes.getLocalName(i)) {
                        case "time":
                            String time = attributes.getValue(i);

                            // Check if more recent
                            if (time.compareTo(date) > 0)
                                date = time;

                            // Create a map for this date
                            entry = new HashMap<>();
                            // Add euro to the entry
                            entry.put("EUR", 1.0);
                            // Add the entry to the map
                            map.put(time, entry);
                            break;

                        // Get the currency name
                        case "currency":
                            name = attributes.getValue(i);
                            break;

                        // Get the currency rate
                        case "rate":
                            try {
                                rate = Double.parseDouble(attributes.getValue(i));
                            } catch (Exception e) {
                                rate = 1.0;
                            }

                            // add new element to the entry
                            entry.put(name, rate);
                            break;
                    }
                }
            }
        }
    }
}
