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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CurrencyAdapter extends BaseAdapter
{
    private LayoutInflater inflater;

    private int flags[];

    private String names[];
    private String symbols[];
    private String values[];

    public CurrencyAdapter(Context context, int flags[], String names[],
			   String symbols[], String values[])
    {
	inflater = LayoutInflater.from(context);

	this.flags = flags;
	this.symbols = symbols;
	this.names = names;
	this.values = values;
    }

    public int getCount()
    {
        return names.length;
    }

    public Object getItem(int position)
    {
        return null;
    }

    public long getItemId(int position)
    {
        return 0;
    }

    // create a new View for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ImageView flag;
	TextView name;
	TextView symbol;
	TextView value;

        if (convertView == null)
	    convertView = inflater.inflate(R.layout.item, parent, false);

	flag = (ImageView)convertView.findViewById(R.id.flag);
	name = (TextView)convertView.findViewById(R.id.name);
	symbol = (TextView)convertView.findViewById(R.id.symbol);
	value = (TextView)convertView.findViewById(R.id.value);

	if (flag != null)
	    flag.setImageResource(flags[position]);

	if (name != null)
	    name.setText(names[position]);

	if (symbol != null)
	    symbol.setText(symbols[position]);

	if (value != null)
	    value.setText(values[position]);

        return convertView;
    }
}
