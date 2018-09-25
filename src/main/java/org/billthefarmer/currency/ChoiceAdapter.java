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

import java.util.List;

// ChoiceAdapter class
public class ChoiceAdapter extends BaseAdapter
{
    private LayoutInflater inflater;

    private List<Integer> flags;
    private List<Integer> longNames;
    private List<Integer> selection;
    private List<String> names;
    private List<String> symbols;

    private int resource;

    // Constructor
    public ChoiceAdapter(Context context, int resource, List<Integer> flags,
                         List<String> names, List<Integer> longNames,
                         List<Integer> selection)
    {
        inflater = LayoutInflater.from(context);

        // Save all the parameters
        this.resource = resource;
        this.flags = flags;
        this.names = names;
        this.longNames = longNames;
        this.selection = selection;
    }

    @Override
    public int getCount()
    {
        return names.size();
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    // Create a new View for each item referenced by the adapter
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ImageView flag;
        TextView name;
        TextView longName;

        // Create a new view
        if (convertView == null)
            convertView = inflater.inflate(resource, parent, false);

        // Find the views
        flag = convertView.findViewById(R.id.flag);
        name = convertView.findViewById(R.id.name);
        longName = convertView.findViewById(R.id.long_name);

        // Update the views
        if (flag != null)
            flag.setImageResource(flags.get(position));

        if (name != null)
            name.setText(names.get(position));

        if (longName != null)
            longName.setText(longNames.get(position));

        // Highlight if selected
        if (selection.contains(position))
            convertView.setBackgroundResource(android.R.color.holo_blue_dark);

        // Clear highlight
        else
            convertView.setBackgroundResource(0);

        return convertView;
    }
}
