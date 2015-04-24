package com.vhiefa.whatsonundip.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.vhiefa.whatsonundip.data.EventContract.EventEntry;

/**
 * Created by Afifatul Mukaroh
 */
public class EventDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "whatsonundipevent.db";

    public EventDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold locations. A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude
        // TBD
        final String SQL_CREATE_EVENT_TABLE = "CREATE TABLE " + EventEntry.TABLE_NAME + " (" +
                EventEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                // the ID of the location entry associated with this news data
                EventEntry.COLUMN_EVENT_ID + " TEXT NOT NULL, " +
                EventEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                EventEntry.COLUMN_DATE + " TEXT NOT NULL, " +
                EventEntry.COLUMN_VENUE + " TEXT NOT NULL," +
                EventEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL," +
                EventEntry.COLUMN_CATEGORY + " TEXT NOT NULL," +
                EventEntry.COLUMN_ORGANIZER + " TEXT NOT NULL, "+
                
				"UNIQUE (" + EventEntry.COLUMN_EVENT_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_EVENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL(" DROP TABLE IF EXIST " + EventEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
