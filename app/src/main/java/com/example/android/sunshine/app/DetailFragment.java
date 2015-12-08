package com.example.android.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;

/**
 * Created by my on 04.12.15.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = DetailFragment.class.getSimpleName();

    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_WEATHER_HUMIDITY = 5;
    static final int COL_WEATHER_WIND = 6;
    static final int COL_WEATHER_WIND_DIRECTION = 7;
    static final int COL_WEATHER_PRESSURE = 8;
    static final int COL_WEATHER_CONDITION_ID = 9;

    private static final String[] DETAIL_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)

            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
    };

    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    private static final int DETAILS_LOADER = 1;
    private String shareForecast;
    private ShareActionProvider mShareActionProvider;
    private TextView dateTv;
    private TextView dayTv;
    private TextView pressureTv;
    private TextView lowTempTv;
    private TextView windTv;
    private TextView humidityTv;
    private TextView highTempTv;
    private TextView descrTv;
    private ImageView weatherImage;

    public DetailFragment() {

    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        getLoaderManager().restartLoader(DETAILS_LOADER, args, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAILS_LOADER, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        weatherImage = (ImageView) rootView.findViewById(R.id.detail_imageView);
        dateTv = (TextView) rootView.findViewById(R.id.detail_dateTv);
        dayTv = (TextView) rootView.findViewById(R.id.detail_dayTv);
        humidityTv = (TextView) rootView.findViewById(R.id.detail_humidityTv);
        lowTempTv = (TextView) rootView.findViewById(R.id.detail_lowTempTv);
        highTempTv = (TextView) rootView.findViewById(R.id.detail_maxTempTv);
        windTv = (TextView) rootView.findViewById(R.id.detail_windTv);
        pressureTv = (TextView) rootView.findViewById(R.id.detail_pressureTv);
        descrTv = (TextView) rootView.findViewById(R.id.detail_descrTv);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        if (shareForecast != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareForecast + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

//    public void onLocationChanged(){
//        updateWeather();
//        getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
//    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        Intent intent = getActivity().getIntent();
//        if (intent == null || intent.getData() == null){
//            Uri singleWeatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
//                    Utility.getPreferredLocation(getActivity()),
//                    System.currentTimeMillis());
//            if (null != args){
//                singleWeatherUri = Uri.parse(args.getString("forecast"));
//            }
//            return  new CursorLoader(getActivity(), singleWeatherUri, DETAIL_COLUMNS, null, null, null);
//        } else {
//            return new CursorLoader(getActivity(), intent.getData(), DETAIL_COLUMNS, null, null, null);
//        }

        Log.v(LOG_TAG, "In onCreateLoader");
        Intent intent = getActivity().getIntent();
        if (intent == null || intent.getData() == null) {
            return null;
        }

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                intent.getData(),
                DETAIL_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data != null && data.moveToFirst()) {
            int imgResId = Utility.getArtResourceForWeatherCondition(data.getInt(COL_WEATHER_CONDITION_ID));
            String date = Utility.getFormattedMonthDay(getActivity(), data.getLong(COL_WEATHER_DATE));
            String day = Utility.getDayName(getActivity(), data.getLong(COL_WEATHER_DATE));
            String maxTemp = Utility.formatTemperature(
                    getActivity(),
                    data.getDouble(COL_WEATHER_MAX_TEMP),
                    Utility.isMetric(getActivity()));

            String minTemp = Utility.formatTemperature(
                    getActivity(),
                    data.getDouble(COL_WEATHER_MIN_TEMP),
                    Utility.isMetric(getActivity()));

            String humidity = getActivity().getString(R.string.format_humidity, data.getInt(COL_WEATHER_HUMIDITY));

            String wind = Utility.formatWind(
                    getActivity(),
                    data.getDouble(COL_WEATHER_WIND),
                    data.getDouble(COL_WEATHER_WIND_DIRECTION));

            String pressure = getActivity().getString(R.string.format_pressure, data.getDouble(COL_WEATHER_PRESSURE));
            String descr = data.getString(COL_WEATHER_DESC);

            if (imgResId != -1) weatherImage.setImageResource(imgResId);
            dateTv.setText(date);
            dayTv.setText(day);
            highTempTv.setText(maxTemp);
            lowTempTv.setText(minTemp);
            descrTv.setText(descr);
            humidityTv.setText(humidity);
            windTv.setText(wind);
            pressureTv.setText(pressure);

            shareForecast = String.format("%s - %s - %s/%s", date, descr, maxTemp, minTemp);
        }

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
