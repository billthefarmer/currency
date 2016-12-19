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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Choice dialog

public class ChoiceDialog extends Activity
    implements View.OnClickListener, AdapterView.OnItemClickListener,
	       AdapterView.OnItemLongClickListener
{
    private ListView listView;
    private Button cancel;
    private Button clear;
    private Button select;

    private ArrayList<Integer> selectList;

    private List<Integer> flagList;
    private List<String> nameList;
    private List<Integer> longNameList;

    private ChoiceAdapter adapter;

    private int mode = Main.NORMAL_MODE;

    // On create

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose);

 	listView = (ListView)findViewById(R.id.list);

	cancel = (Button)findViewById(R.id.cancel);
	clear = (Button)findViewById(R.id.clear);
	select = (Button)findViewById(R.id.select);

	// Set the listeners
	if (listView != null)
	{
	    listView.setOnItemClickListener(this);
	    listView.setOnItemLongClickListener(this);
	}

	if (cancel != null)
	    cancel.setOnClickListener(this);

	if (clear != null)
	    clear.setOnClickListener(this);

	if (select != null)
	    select.setOnClickListener(this);

	selectList = new ArrayList<Integer>();

	// Populate the lists
	flagList = Arrays.asList(Main.CURRENCY_FLAGS);
	nameList = Arrays.asList(Main.CURRENCY_NAMES);
	longNameList = Arrays.asList(Main.CURRENCY_LONGNAMES);

	// Create the adapter
	adapter = new ChoiceAdapter(this, R.layout.choice, flagList,
				    nameList, longNameList);

	// Set the adapter
	if (listView != null)
	    listView.setAdapter(adapter);
    }

    // On click

    @Override
    public void onClick(View v)
    {
	int id = v.getId();

	switch(id)
	{
	    // Cancel
	case R.id.cancel:
	    setResult(RESULT_CANCELED);
	    finish();
	    break;

	    // Clear
	case R.id.clear:
	    mode = Main.NORMAL_MODE;

	    // Clear exising selection
	    for (int index: selectList)
	    {
		View view = listView.getChildAt(index);
		view.setBackgroundResource(0);
	    }

	    // Start a new one
	    selectList.clear();
	    break;

	    // Select
	case R.id.select:
	    // Return new currency list in intent
	    Intent intent = new Intent();
	    intent.putIntegerArrayListExtra(Main.CHOICE, selectList);
	    setResult(RESULT_OK, intent);
	    finish();
	    break;
	}
    }

    // On item click

    @Override
    public void onItemClick(AdapterView parent, View view,
			    int position, long id)
    {
	// Check mode
	switch (mode)
	{
	    // Normal
	case Main.NORMAL_MODE:
	    selectList.add(position);
	    // Return new currency in intent
	    Intent intent = new Intent();
	    intent.putIntegerArrayListExtra(Main.CHOICE, selectList);
	    setResult(RESULT_OK, intent);
	    finish();
	    break;

	    // Select
	case Main.SELECT_MODE:
	    selectList.add(position);
	    view.setBackgroundResource(android.R.color.holo_blue_dark);
	    break;
	}
    }

    // On item long click

    @Override
    public boolean onItemLongClick(AdapterView parent, View view,
				   int position, long id)
    {
	mode = Main.SELECT_MODE;

	// Clear exising selection
	for (int index: selectList)
	{
	    View v = listView.getChildAt(index);
	    v.setBackgroundResource(0);
	}

	// Start a new one
	selectList.clear();
	selectList.add(position);
	view.setBackgroundResource(android.R.color.holo_blue_dark);
	return true;
    }
}
