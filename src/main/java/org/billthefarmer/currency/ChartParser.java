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
import java.util.Date;
import java.util.Hashtable;
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
    private Map<Date, Map<String, Double>> table;
    private String time;

    // Create parser
    private XMLReader createParser()
    {
	table = new Hashtable<Date, Map<String, Double>>();

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
	    table.clear();
	}
		
	return null;
    }

    // Get table
    public Map<String, Double> getTable()
    {
	return table;
    }

    // Get time
    public String getTime()
    {
	return time;
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
		table.clear();
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
		table.clear();
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
			time = attributes.getValue(i);
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

			// add new element to the table
			table.put(name, rate);
		    }
		}
	    }
	}
    }
}
