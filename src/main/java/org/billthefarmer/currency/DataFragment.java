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

import android.app.Fragment;
import android.os.Bundle;

import java.util.Map;

public class DataFragment extends Fragment
{
    // data object we want to retain
    private Map<String, Map<String,Double>> data;
    private Map<String,Double> map;

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    public void setData(Map<String, Map<String,Double>> data)
    {
        this.data = data;
    }

    public void setMap(Map<String,Double> map)
    {
        this.map = map;
    }

    public Map<String, Map<String,Double>> getData()
    {
        return data;
    }

    public Map<String,Double> getMap()
    {
        return map;
    }
}
