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

public class ChoiceDialog extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener
{
    private ListView listView;

    private Button cancel;
    private Button clear;
    private Button select;

    private List<Integer> selectList;

    private List<Integer> flagList;
    private List<String> nameList;
    private List<Integer> longNameList;

    private ChoiceAdapter adapter;

    private int mode = Main.DISPLAY_MODE;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose);

        // Find views
        listView = (ListView) findViewById(R.id.list);

        cancel = (Button) findViewById(R.id.cancel);
        clear = (Button) findViewById(R.id.clear);
        select = (Button) findViewById(R.id.select);

        // Set the listeners
        if (listView != null)
        {
            listView.setOnItemClickListener(this);
            listView.setOnItemLongClickListener(this);
        }

        if (cancel != null) cancel.setOnClickListener(this);

        if (clear != null) clear.setOnClickListener(this);

        if (select != null) select.setOnClickListener(this);

        selectList = new ArrayList<Integer>();

        // Populate the lists
        flagList = Arrays.asList(Main.CURRENCY_FLAGS);
        nameList = Arrays.asList(Main.CURRENCY_NAMES);
        longNameList = Arrays.asList(Main.CURRENCY_LONGNAMES);

        // Create the adapter
        adapter = new ChoiceAdapter(this, R.layout.choice, flagList, nameList, longNameList, selectList);

        // Set the adapter
        if (listView != null) listView.setAdapter(adapter);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedState)
    {
        List<Integer> list = savedState.getIntegerArrayList(Main.SAVE_SELECT);

        if (list != null)
        {
            // Update the selection list
            for (int index : list)
                selectList.add(index);

            // Disable buttons if empty
            if (selectList.isEmpty())
            {
                if (clear != null) clear.setEnabled(false);
                if (select != null) select.setEnabled(false);
                mode = Main.DISPLAY_MODE;
            }

            // Enable buttons if selection
            else
            {
                if (clear != null) clear.setEnabled(true);
                if (select != null) select.setEnabled(true);
                mode = Main.SELECT_MODE;
            }
        }

        // No saved list
        else
        {
            mode = Main.DISPLAY_MODE;
        }

        // Notify adapter
        adapter.notifyDataSetChanged();
        super.onRestoreInstanceState(savedState);
    }

    // On save

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        // Save the selection list
        outState.putIntegerArrayList(Main.SAVE_SELECT, (ArrayList<Integer>) selectList);
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();

        switch (id)
        {
            // Cancel
            case R.id.cancel:
                setResult(RESULT_CANCELED);
                finish();
                break;

            // Clear
            case R.id.clear:
                if (clear != null) clear.setEnabled(false);
                if (select != null) select.setEnabled(false);
                mode = Main.DISPLAY_MODE;

                // Start a new selection
                selectList.clear();
                adapter.notifyDataSetChanged();
                break;

            // Select
            case R.id.select:
                // Return new currency list in intent
                Intent intent = new Intent();
                intent.putIntegerArrayListExtra(Main.CHOICE, (ArrayList) selectList);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id)
    {
        // Check mode
        switch (mode)
        {
            // Normal
            case Main.DISPLAY_MODE:
                selectList.add(position);
                // Return new currency in intent
                Intent intent = new Intent();
                intent.putIntegerArrayListExtra(Main.CHOICE, (ArrayList) selectList);
                setResult(RESULT_OK, intent);
                finish();
                break;

            // Select
            case Main.SELECT_MODE:
                if (selectList.contains(position)) selectList.remove(selectList.indexOf(position));

                else selectList.add(position);

                if (selectList.isEmpty())
                {
                    if (clear != null) clear.setEnabled(false);
                    if (select != null) select.setEnabled(false);
                    mode = Main.DISPLAY_MODE;
                }

                adapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView parent, View view, int position, long id)
    {
        if (clear != null) clear.setEnabled(true);
        if (select != null) select.setEnabled(true);
        mode = Main.SELECT_MODE;

        // Start a new selection
        selectList.clear();
        selectList.add(position);
        adapter.notifyDataSetChanged();
        return true;
    }
}
