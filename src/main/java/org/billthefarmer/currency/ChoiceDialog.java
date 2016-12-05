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
import java.util.Arrays;
import java.util.List;

public class ChoiceDialog extends Activity
    implements AdapterView.OnItemClickListener, View.OnClickListener
{
    private ListView listView;
    private Button button;

    private List<Integer> flagList;
    private List<String> nameList;
    private List<Integer> longNameList;

    private ChoiceAdapter adapter;

    // On create

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose);

	button = (Button)findViewById(R.id.cancel);
 	listView = (ListView)findViewById(R.id.list);

	if (button != null)
	    button.setOnClickListener(this);

	if (listView != null)
	    listView.setOnItemClickListener(this);

	flagList = Arrays.asList(Main.CURRENCY_FLAG);
	nameList = Arrays.asList(Main.CURRENCY_NAME);
	longNameList = Arrays.asList(Main.CURRENCY_LONGNAME);

	adapter = new ChoiceAdapter(this, R.layout.choice, flagList,
				    nameList, longNameList);

	if (listView != null)
	    listView.setAdapter(adapter);
    }

    // On click

    @Override
    public void onClick(View v)
    {
	setResult(RESULT_CANCELED);
	finish();
    }

    // On item click

    @Override
    public void onItemClick(AdapterView parent, View view,
			    int position, long id)
    {
	// Return new currency in intent
	Intent intent = new Intent();
	intent.putExtra(Main.CHOICE,
			position);
	setResult(RESULT_OK, intent);
	finish();
    }
}
