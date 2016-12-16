# ![Logo](src/main/res/drawable-mdpi/ic_launcher.png) Currency [![Build Status](https://travis-ci.org/billthefarmer/currency.svg?branch=master)](https://travis-ci.org/billthefarmer/currency)

Android currency conversion. The app is avalable on [F-Droid](https://f-droid.org/repository/browse/?fdid=org.billthefarmer.currency) and [here](https://github.com/billthefarmer/currency/releases).

![](https://raw.githubusercontent.com/billthefarmer/billthefarmer.github.io/master/images/currency/currency.png) ![](https://raw.githubusercontent.com/billthefarmer/billthefarmer.github.io/master/images/currency/choose.png)

![](https://raw.githubusercontent.com/billthefarmer/billthefarmer.github.io/master/images/currency/settings.png) ![](https://raw.githubusercontent.com/billthefarmer/billthefarmer.github.io/master/images/currency/about.png)

 * Currency rates from the [European Central Bank](http://www.ecb.europa.eu/stats/exchange/eurofxref/html/index.en.html)
 * 32 international currencies
 * Currency rates updated daily
 * Last rate update retained for use offline

## Usage
### Edit

Touch the current currency value field to edit the value. The display
will be updated dynamically as you type. Touch the **Done** button on
the numeric keypad to update the value field. The values shown against
each currency in the display will be updated.

### Toolbar

The icons in the toolbar from left to right are:

* **Add** a currency to the list. A scrollable list of currencies will
   pop up. Touch an entry to add it or touch the **Cancel** button
   below the list. Long touch an entry to select it. Once one currency
   is selected others may be added to the selection by touching
   them. Another long touch on another currency will clear the list
   and select the new currency. Touch the **Clear** button to clear
   the selection. Touch the **Select** button to add the
   selection. The entries will be added to the list in the order
   selected.

* **Update** Get the day's currency rates from the ECB. The date shown
  on the left above the currency list will be updated. It may show the
  previous day because the rates are updated at around 14:15 CET. The
  status display on the right above the currency list will show 'OK',
  'No Connection', 'No WiFi' or 'Roaming' according to the update
  settings.

* **Help** Display help text.
* **Settings** Display the settings screen.

### Edit currency list
Touch a currency entry in the list to make it current. The old current
currency will move to the top of the list. Long touch a currency entry
to select it. Once one currency is selected others may be added to the
selection by touching them. Another long touch on another currency
will clear the list and select the new currency. The icons in the
toolbar will change to:

* **Clear** Clear the selection.
* **Gopy** Copy selection value. Only one value will be copied.
* **Remove** the selected currencies.

## Settings

### Update

* **WiFi** Update while connected on WiFi only
* **Roaming** Update while roaming
 
### Numbers
 
* **Fraction digits** Select the number of digits to display after the
  decimal point. A popup list of options will be displayed.
	
### About

* **About** Display the version, copyright and licence.
