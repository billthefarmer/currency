# ![Logo](src/main/res/drawable-mdpi/ic_launcher.png) Currency [![Build Status](https://travis-ci.org/billthefarmer/currency.svg?branch=master)](https://travis-ci.org/billthefarmer/currency)

Android currency conversion. The app is available on [F-Droid](https://f-droid.org/repository/browse/?fdid=org.billthefarmer.currency) and [here](https://github.com/billthefarmer/currency/releases).

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
the numeric keypad to update the value field.  The whole value field
is selected by default when touched so it will be overwritten by
typing in a new value. To deselect the text, touch an adjacent area,
like the currency flag. This behaviour can be disabled in the settings
below.

### Toolbar

The icons in the toolbar from left to right are:

* **Add** a currency to the list. A scrollable list of currencies will
   pop up. Touch an entry to add it or touch the **Cancel** button
   below the list. Long touch an entry to select it. Once one currency
   is selected others may be added or removed from the selection by
   touching them. Another long touch on another currency will clear
   the list and select the new currency. Touch the **Clear** button to
   clear the selection. Touch the **Select** button to add the
   selection. The entries will be added to the list in the order
   selected.

* **Update** Get the day's currency rates from the
  [ECB](http://www.ecb.europa.eu/stats/exchange/eurofxref/html/index.en.html). The
  date shown on the left above the currency list will be updated if
  new rates are available. It may show the previous day because the
  rates are updated at around 14:15 CET. The status display on the
  right above the currency list will show 'OK', 'No Connection', 'No
  WiFi' or 'Roaming' according to the update settings. If may show
  'Failed' if the connection times out or fails to connect.

* **Help** Display help text.
* **Settings** Display the settings screen.

### Edit currency list
Touch a currency entry in the list to make it current. The old current
currency will move to the top of the list. Long touch a currency entry
to select it. Once one currency is selected others may be added or
removed from the selection by touching them. Another long touch on
another currency will clear the list and select the new currency. The
icons in the toolbar will change to:

* **Clear** the selection.
* **Remove** the selected currencies.
* **Chart** Display a chart of selected currencies. If one is
  selected, the chart will be of the current currency and the selected
  currency. If two currencies are selected the chart will be of those
  currencies.
* **Copy** selection value to clipboard. Only one value will be copied.

## Chart

The display shows an interactive chart of the last 90 days of
historical rates from the
[ECB](http://www.ecb.europa.eu/stats/exchange/eurofxref/html/index.en.html"). The
chart responds to two finger pinch and expand gestures and will scroll
once expanded. The toolbar icons from left to right are:

* **Invert** the chart.
* **Update** the chart.
* **Eighteen years** The chart will be updated with nearly eighteen
  years of historical data currently dating from 1
  April 1999. **Caution** - this will take a while, depending on the
  connection, and will download more than 6Mb of data.

## Settings

### Update

* **WiFi** Update while connected on WiFi only
* **Roaming** Update while roaming

### Selection

* **Selection** Select all current currency value text when touched.
  
### Numbers
 
* **Fraction digits** Select the number of digits to display after the
  decimal point. A popup list of options will be displayed.
	
### About

* **About** Display the version, copyright and licence.
