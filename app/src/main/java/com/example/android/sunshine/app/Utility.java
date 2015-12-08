package com.example.android.sunshine.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by my on 25.11.15.
 */
public class Utility {
    private final static String LOG_TAG = Utility.class.getSimpleName();


    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_location_key),
                context.getString(R.string.pref_location_default));
    }

    public static boolean isMetric(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_units_key),
                context.getString(R.string.pref_units_metric))
                .equals(context.getString(R.string.pref_units_metric));
    }

    static String formatTemperature(Context context, double temperature, boolean isMetric) {
        double temp;
        if ( !isMetric ) {
            temp = 9*temperature/5+32;
        } else {
            temp = temperature;
        }


        return context.getString(R.string.format_temperature, temp);
    }

    static String formatToFriendlyDate(Context context, long dateInMillis){
        SimpleDateFormat dateFormat = (SimpleDateFormat) DateFormat.getDateInstance();
        Time time = new Time();
        time.setToNow();

        int dayFromDb = Time.getJulianDay(dateInMillis, time.gmtoff);
        int currDay = Time.getJulianDay(System.currentTimeMillis(), time.gmtoff);

        if (dayFromDb == currDay){
            dateFormat.applyLocalizedPattern("MMMM dd");
            return context.getString(
                    R.string.format_date,
                    context.getString(R.string.today),
                    getFormattedMonthDay(context, dateInMillis));

        } else if (dayFromDb == currDay + 1){
            return context.getString(R.string.tomorrow);
        } else if (dayFromDb < currDay + 7){
            return getDayName(context, dateInMillis);
        } else {
            dateFormat.applyLocalizedPattern("EEE MMM dd");
            return dateFormat.format(dateInMillis);
        }
    }

    public static String getDayName(Context context, long dateInMillis){
        Time time = new Time();
        time.setToNow();
        Date localDate = new Date(dateInMillis);
        int dayFromDb = Time.getJulianDay(dateInMillis, time.gmtoff);
        int currDay = Time.getJulianDay(System.currentTimeMillis(), time.gmtoff);

        if (dayFromDb == currDay){
            return context.getString(R.string.today);
        } else if (dayFromDb == currDay + 1){
            return context.getString(R.string.tomorrow);
        } else {
            return new SimpleDateFormat("EEEE").format(localDate);
        }
    }

    public static String getFormattedMonthDay(Context context, long dateInMillis){
        SimpleDateFormat monthDayFormat = new SimpleDateFormat("MMMM dd");
        Date localDate = new Date(dateInMillis);
        String monthDayString = monthDayFormat.format(localDate);
        return monthDayString;
    }

    public static String formatWind(Context context, double speed, double degrees){
        int windFormatId;

        if (Utility.isMetric(context)) {
            windFormatId = R.string.wind_metric;
        } else {
            windFormatId = R.string.wind_imperial;
            speed *= 0.621371192237;
        }

        String direction = "";
        if (degrees >= 337.5 || degrees < 22.5) {
            direction = "N";
        } else if (degrees >= 22.5 && degrees < 67.5) {
            direction = "NE";
        } else if (degrees >= 67.5 && degrees < 112.5) {
            direction = "E";
        } else if (degrees >= 112.5 && degrees < 157.5) {
            direction = "SE";
        } else if (degrees >= 157.5 && degrees < 202.5) {
            direction = "S";
        } else if (degrees >= 202.5 && degrees < 247.5) {
            direction = "SW";
        } else if (degrees >= 247.5 && degrees < 292.5) {
            direction = "W";
        } else if (degrees >= 292.5 && degrees < 337.5) {
            direction = "NW";
        }
        return context.getString(windFormatId, speed, direction);
    }

    /**
     * Helper method to provide the icon resource id according to the weather condition id returned
     * by the OpenWeatherMap call.
     * @param conditionId from OpenWeatherMap API response
     * @return resource id for the corresponding icon. -1 if no relation is found.
     */
    public static int getIconResourceForWeatherCondition(int conditionId) {
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (conditionId >= 200 && conditionId <= 232) {
            return R.drawable.ic_storm;
        } else if (conditionId >= 300 && conditionId <= 321) {
            return R.drawable.ic_light_rain;
        } else if (conditionId >= 500 && conditionId <= 504) {
            return R.drawable.ic_rain;
        } else if (conditionId == 511) {
            return R.drawable.ic_snow;
        } else if (conditionId >= 520 && conditionId <= 531) {
            return R.drawable.ic_rain;
        } else if (conditionId >= 600 && conditionId <= 622) {
            return R.drawable.ic_snow;
        } else if (conditionId >= 701 && conditionId <= 761) {
            return R.drawable.ic_fog;
        } else if (conditionId == 761 || conditionId == 781) {
            return R.drawable.ic_storm;
        } else if (conditionId == 800) {
            return R.drawable.ic_clear;
        } else if (conditionId == 801) {
            return R.drawable.ic_light_clouds;
        } else if (conditionId >= 802 && conditionId <= 804) {
            return R.drawable.ic_cloudy;
        }
        return -1;
    }

    /**
     * Helper method to provide the art resource id according to the weather condition id returned
     * by the OpenWeatherMap call.
     * @param conditionId from OpenWeatherMap API response
     * @return resource id for the corresponding image. -1 if no relation is found.
     */
    public static int getArtResourceForWeatherCondition(int conditionId) {
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (conditionId >= 200 && conditionId <= 232) {
            return R.drawable.art_storm;
        } else if (conditionId >= 300 && conditionId <= 321) {
            return R.drawable.art_light_rain;
        } else if (conditionId >= 500 && conditionId <= 504) {
            return R.drawable.art_rain;
        } else if (conditionId == 511) {
            return R.drawable.art_snow;
        } else if (conditionId >= 520 && conditionId <= 531) {
            return R.drawable.art_rain;
        } else if (conditionId >= 600 && conditionId <= 622) {
            return R.drawable.art_snow;
        } else if (conditionId >= 701 && conditionId <= 761) {
            return R.drawable.art_fog;
        } else if (conditionId == 761 || conditionId == 781) {
            return R.drawable.art_storm;
        } else if (conditionId == 800) {
            return R.drawable.art_clear;
        } else if (conditionId == 801) {
            return R.drawable.art_light_clouds;
        } else if (conditionId >= 802 && conditionId <= 804) {
            return R.drawable.art_clouds;
        }
        return -1;
    }
}
