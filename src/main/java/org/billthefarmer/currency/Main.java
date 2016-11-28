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

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.Arrays;
import java.util.List;

public class Main extends Activity
{

    // Initial currency name list
    public static final String currency_list[] =
    {
	"EUR", "USD", "GBP", "CAD", "AUD"
    };

    // Current available currencies name
    public static final String currency_name[] =
    {
	"EUR", "USD", "JPY", "BGN",
	"CZK", "DKK", "GBP", "HUF",
	"LTL", "LVL", "PLN", "RON",
	"SEK", "CHF", "NOK", "HRK",
	"RUB", "TRY", "AUD", "BRL",
	"CAD", "CNY", "HKD", "IDR",
	"ILS", "INR", "KRW", "MXN",
	"MYR", "NZD", "PHP", "SGD",
	"THB", "ZAR"
    };

    public static final String currency_symbol[] =
    {
	"€",  "$",  "¥",  null,
	null, null, "£",  null,
	null, null, null, null,
	null, null, "$",  null,
	"$",  null, "$",  null,
	null, null, null, null,
	null, "$",  null, "$",
	null, null
    };

    // Current available currencies long name
    public static final Integer currency_longname[] =
    {
	R.string.EUR, R.string.USD, R.string.JPY, R.string.BGN,
	R.string.CZK, R.string.DKK, R.string.GBP, R.string.HUF,
	R.string.LTL, R.string.LVL, R.string.PLN, R.string.RON,
	R.string.SEK, R.string.CHF, R.string.NOK, R.string.HRK,
	R.string.RUB, R.string.TRY, R.string.AUD, R.string.BRL,
	R.string.CAD, R.string.CNY, R.string.HKD, R.string.IDR,
	R.string.ILS, R.string.INR, R.string.KRW, R.string.MXN,
	R.string.MYR, R.string.NZD, R.string.PHP, R.string.SGD,
	R.string.THB, R.string.ZAR
    };
	
    // Current available currencies icon
    public static final Integer currency_icon[] =
    { 
	R.drawable.flag_eur, R.drawable.flag_usd, R.drawable.flag_jpy,
	R.drawable.flag_bgn, R.drawable.flag_czk, R.drawable.flag_dkk,
	R.drawable.flag_gbp, R.drawable.flag_huf, R.drawable.flag_ltl,
	R.drawable.flag_lvl, R.drawable.flag_pln, R.drawable.flag_ron,
	R.drawable.flag_sek, R.drawable.flag_chf, R.drawable.flag_nok,
	R.drawable.flag_hrk, R.drawable.flag_rub, R.drawable.flag_try,
	R.drawable.flag_aud, R.drawable.flag_brl, R.drawable.flag_cad,
	R.drawable.flag_cny, R.drawable.flag_hkd, R.drawable.flag_idr,
	R.drawable.flag_ils, R.drawable.flag_inr, R.drawable.flag_kpw,
	R.drawable.flag_mxn, R.drawable.flag_myr, R.drawable.flag_nzd,
	R.drawable.flag_php, R.drawable.flag_sgd, R.drawable.flag_thb,
	R.drawable.flag_zar
    };

    private ImageView flag;
    private TextView symbol;
    private EditText edit;
    private ListView list;

    private List<String> names;

    // On create

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

	flag = (ImageView)findViewById(R.id.flag);
	symbol = (TextView)findViewById(R.id.symbol);
	edit = (EditText)findViewById(R.id.edit);
	list = (ListView)findViewById(R.id.list);

	names = Arrays.asList(currency_name);
    }

    // On create options menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
	// Inflate the menu; this adds items to the action bar if it
	// is present.

	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.main, menu);

	return true;
    }
}
