package com.example.android.sunshine.app.data;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.AndroidTestCase;
import android.util.Log;

import junit.framework.Assert;

/**
 * Created by my on 05.12.15.
 */
public class TestDbInfo extends AndroidTestCase{
    public void testInfoInDb() {
        SQLiteDatabase db = new WeatherDbHelper(mContext).getReadableDatabase();
        Cursor c = db.query(WeatherContract.WeatherEntry.TABLE_NAME, null, null, null, null, null, null);
        int res = 0;
        if (c.moveToFirst()){
                String s = DatabaseUtils.dumpCursorToString(c);
            Log.d("Show db", s);

        }
    }
}
