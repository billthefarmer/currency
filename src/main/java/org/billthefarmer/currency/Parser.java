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

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

// Parser class

public class Parser {
    private Map<String, Double> map;
    private String date;

    // Create parser
    private XMLReader createParser() {
        // Create the map and add value for Euro
        map = new HashMap<String, Double>();
        map.put("EUR", 1.0);

        try {
            // Get a parser
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();

            // Get a reader
            XMLReader reader = parser.getXMLReader();
            Handler handler = new Handler();
            reader.setContentHandler(handler);

            return reader;
        } catch (Exception e) {
            map.clear();
        }

        return null;
    }

    // Get map
    public Map<String, Double> getMap() {
        return map;
    }

    // Get date
    public String getDate() {
        return date;
    }

    // Start parser for a url
    public boolean startParser(String s) {
        // Get a reader
        XMLReader reader = createParser();

        if (reader != null) {
            // Read the xml from the url
            try {
                URL url = new URL(s);
                InputStream stream = url.openStream();
                reader.parse(new InputSource(stream));
                return true;
            } catch (Exception e) {
                map.clear();
            }
        }

        return false;
    }

    // Start parser from a resource
    public boolean startParser(Context context, int id) {
        Resources resources = context.getResources();
        // Get a reader
        XMLReader reader = createParser();

        if (reader != null) {
            // Read the xml from the resources
            try {
                InputStream stream = resources.openRawResource(id);
                reader.parse(new InputSource(stream));
                return true;
            } catch (Exception e) {
                map.clear();
            }
        }

        return false;
    }

    // Handler class
    private class Handler extends DefaultHandler {

        @Override
        public void startElement(String uri, String localName, String qName,
                                 Attributes attributes) throws SAXException {
            String name = "EUR";
            double rate = 1.0;

            if (localName.equals("Cube")) {
                for (int i = 0; i < attributes.getLength(); i++) {
                    // Get the date
                    if (attributes.getLocalName(i).equals("time"))
                        date = attributes.getValue(i);

                        // Get the currency name
                    else if (attributes.getLocalName(i).equals("currency")) {
                        name = attributes.getValue(i);
                    }

                    // Get the currency rate
                    else if (attributes.getLocalName(i).equals("rate")) {
                        try {
                            rate = Double.parseDouble(attributes.getValue(i));
                        } catch (Exception e) {
                            rate = 1.0;
                        }

                        // Add new currency to the map
                        map.put(name, rate);
                    }
                }
            }
        }
    }
}
