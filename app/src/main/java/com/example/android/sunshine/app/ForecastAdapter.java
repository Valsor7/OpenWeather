package com.example.android.sunshine.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;

/**
 * Created by my on 25.11.15.
 */
public class ForecastAdapter extends CursorAdapter {



    private static final String LOG_TAG = ForecastAdapter.class.getSimpleName().toString();

    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    static final int COL_COORD_LAT = 7;
    static final int COL_COORD_LONG = 8;

    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;

    private Context mContext;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
    }

    public static class ViewHolder {
        ImageView iconView;
        TextView lowTempTv;
        TextView dateTv;
        TextView descrTv;
        TextView highTempTv;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateTv = (TextView) view.findViewById(R.id.list_item_date_textview);
            descrTv = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highTempTv = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowTempTv = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }



    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;

        switch (viewType) {
            case VIEW_TYPE_TODAY: {
                layoutId = R.layout.list_item_forecast_today;
                break;
            }
            case VIEW_TYPE_FUTURE_DAY: {
                layoutId = R.layout.list_item_forecast;
                break;
            }
        }

        View listItemView = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder viewHolder = new ViewHolder(listItemView);
        listItemView.setTag(viewHolder);

        return listItemView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        int conditionId = cursor.getInt(COL_WEATHER_CONDITION_ID);
        int imgResId = -1;
        if (cursor.getPosition() == 0){
            imgResId = Utility.getArtResourceForWeatherCondition(conditionId);
        } else if (cursor.getPosition() > 0){
            imgResId = Utility.getIconResourceForWeatherCondition(conditionId);
        }

        String strDate = Utility.formatToFriendlyDate(context, cursor.getLong(COL_WEATHER_DATE));
        String strHighTemp = Utility.formatTemperature(
                context,
                cursor.getDouble(COL_WEATHER_MAX_TEMP),
                Utility.isMetric(context));
        String strLowTemp = Utility.formatTemperature(
                context,
                cursor.getDouble(COL_WEATHER_MIN_TEMP),
                Utility.isMetric(context));
        String strDescr = cursor.getString(COL_WEATHER_DESC);

        if (imgResId != -1) viewHolder.iconView.setImageResource(imgResId);

        viewHolder.dateTv.setText(strDate);
        viewHolder.descrTv.setText(strDescr);
        viewHolder.highTempTv.setText(strHighTemp);
        viewHolder.lowTempTv.setText(strLowTemp);
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }
}
