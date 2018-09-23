# ![Logo](src/main/res/drawable-mdpi/ic_launcher.png) Currency [![Build Status](https://travis-ci.org/billthefarmer/currency.svg?branch=master)](https://travis-ci.org/billthefarmer/currency) [![Available on F-Droid](https://f-droid.org/wiki/images/c/ca/F-Droid-button_available-on_smaller.png)](https://f-droid.org/repository/browse/?fdid=org.billthefarmer.currency)

Android currency conversion. The app is available on [F-Droid](https://f-droid.org/repository/browse/?fdid=org.billthefarmer.currency) and [here](https://github.com/billthefarmer/currency/releases).

## Pull requests
I have had a number of pull requests on this app of mainly cosmetic
changes, possibly due to a
[Reddit](https://www.reddit.com/r/androiddev/comments/5lqdvw/do_you_want_to_contribute_to_an_open_source_app)
post about contributing to open source apps. Please feel free to
contribute bug fixes, enhancement requests, translations, etc, or fork
your own version, but not cosmetic updates to modernise the 'look', or
tidy up the code etc. I quite like the app icons I am using and am
reluctant to change them.

## Extra currencies
I have had a number of requests for extra currencies, some offering
possible data sources. I have decided that trying to add more
currencies from disparate sources makes this app far too
complicated. It was designed around the current and historical data
freely and reliably available from the
[European Central Bank](http://www.ecb.europa.eu/stats/exchange/eurofxref/html/index.en.html)
and to redesign it to deal with currencies that have historical data,
currencies that don't, data sources that might disappear and other
potential pitfalls that I haven't found yet makes it far too complex.

More ~currencies~, cryptocurrencies and lots of other conversions are in [Equate](https://github.com/EvanRespaut/Equate).

![](https://raw.githubusercontent.com/billthefarmer/billthefarmer.github.io/master/images/currency/currency.png) ![](https://raw.githubusercontent.com/billthefarmer/billthefarmer.github.io/master/images/currency/choose.png)

![](https://raw.githubusercontent.com/billthefarmer/billthefarmer.github.io/master/images/currency/settings.png) ![](https://raw.githubusercontent.com/billthefarmer/billthefarmer.github.io/master/images/currency/about.png)

![](https://raw.githubusercontent.com/billthefarmer/billthefarmer.github.io/master/images/currency/chart.png)

![](https://raw.githubusercontent.com/billthefarmer/billthefarmer.github.io/master/images/currency/hist.png)

 * Currency rates from the [European Central Bank](http://www.ecb.europa.eu/stats/exchange/eurofxref/html/index.en.html)
 * 32 international currencies
 * Currency rates updated daily
 * Last rate update retained for use offline
 * Ukrainian, German and French translation

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
  WiFi' or 'Roaming' according to the update settings. It may show
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
  currency. If more than one currency is selected the chart will be of
  the last two selected currencies.
* **Copy** selection value to clipboard. Only one value will be copied.

## Chart
The display shows an interactive chart of the last 90 days of
historical rates from the
[ECB](http://www.ecb.europa.eu/stats/exchange/eurofxref/html/index.en.html"). The
chart responds to two finger pinch and expand gestures and will scroll
once expanded. The toolbar icons from left to right are:
* **Invert** the chart.
* **New chart** The scrollable list of currencies will pop up. Select
  one or two currencies from the list as above to replace the current
  currencies in the chart.
* **Update** the chart.
* **Historical** The chart will be updated with nearly eighteen years
  of historical data currently dating from 1 April 1999. Not all the
  currencies go back this far. **Caution** - this will take a while,
  depending on the connection, and will download more than 6Mb of
  data.

## Settings
### Update
* **WiFi** Update while connected on WiFi only
* **Roaming** Update while roaming

### Selection
* **Selection** Select all current currency value text when touched.

### Numbers
* **Fraction digits** Select the number of digits to display after the
  decimal point. A popup list of options will be displayed.

### Chart
* **Fill** Fill the chart trace.

### Theme
* **Dark** Use dark theme

### About
* **About** Display the version, copyright and licence.
