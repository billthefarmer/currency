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

import java.io.InputStream;
import java.net.URL;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

// Parser class
public class ChartParser
{
    private Map<String, Map<String, Double>> map;
    private Map<String, Double> entry;
    private String date;

    // Create parser
    private XMLReader createParser()
    {
	map = new LinkedHashMap<String, Map<String, Double>>();
	date = "1970-01-01";

	try
	{
	    SAXParserFactory factory = SAXParserFactory.newInstance();
	    SAXParser parser = factory.newSAXParser();

	    XMLReader reader = parser.getXMLReader();
	    Handler handler = new Handler();
	    reader.setContentHandler(handler);

	    return reader;
	}

	catch (Exception e)
	{
	    map.clear();
	}

	return null;
    }

    // Get data
    public String getDate()
    {
	return date;
    }

    // Get map
    public Map<String, Map<String, Double>> getMap()
    {
	return map;
    }

    // Start parser
    public boolean startParser(String s)
    {
	XMLReader reader = createParser();

	if(reader != null)
	{
	    try
	    {
		URL url = new URL(s);
		InputStream stream = url.openStream();
		reader.parse(new InputSource(stream));
		return true;
	    }

	    catch (Exception e)
	    {
		map.clear();
	    }
	}

	return false;
    }

    // Start parser
    public boolean startParser(Context context, int id)
    {
	Resources resources = context.getResources();
	XMLReader reader = createParser();

	if(reader != null)
	{
	    try
	    {
		InputStream stream = resources.openRawResource(id);
		reader.parse(new InputSource(stream));
		return true;
	    }

	    catch (Exception e)
	    {
		map.clear();
	    }
	}

	return false;
    }

    // Handler class
    private class Handler extends DefaultHandler
    {
	// Start element
	@Override
	public void startElement(String uri, String localName, String qName,
				 Attributes attributes) throws SAXException
	{
	    String name = "EUR";
	    double rate = 1.0;

	    if(localName == "Cube")
	    {
		for (int i = 0; i < attributes.getLength(); i++)
		{
		    if (attributes.getLocalName(i) == "time")
		    {
			String time = attributes.getValue(i);

			if (time.compareTo(date) > 0)
			    date = time;

			entry = new HashMap<String, Double>();
			entry.put("EUR", 1.0);
			map.put(time, entry);
		    }

		    else if (attributes.getLocalName(i) == "currency")
		    {
			name = attributes.getValue(i);
		    }

		    else if (attributes.getLocalName(i) == "rate")
		    {
			try
			{
			    rate = Double.parseDouble(attributes.getValue(i));
			}

			catch (Exception e)
			{
			    rate = 1.0;
			}

			// add new element to the entry
			entry.put(name, rate);
		    }
		}
	    }
	}
    }
}
