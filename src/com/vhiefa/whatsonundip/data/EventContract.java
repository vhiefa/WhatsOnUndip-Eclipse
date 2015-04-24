package com.vhiefa.whatsonundip.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Afifatul Mukaroh
 */
public class EventContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website. A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.vhiefa.whatsonundip";
    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.vhiefa.whatsonundip/event/ is a valid path for
    // looking at event data. content://com.vhiefa.whatsonundip/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not. Don't be that dev, reader. Don't be that dev.
    public static final String PATH_EVENT = "event";

    // Format used for storing dates in the database.  ALso used for converting those strings
    // back into date objects for comparison/processing.
    public static final String DATE_FORMAT = "yyyyMMdd"; 
    
    /**
     * Converts Date class to a string representation, used for easy comparison and database lookup.
     * @param date The input date
     * @return a DB-friendly representation of the date, using the format defined in DATE_FORMAT.
     */
    public static String getDbDateString(Date date){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }
    


    /**
     * Converts a dateText to a long Unix time representation
     * @param dateText the input date string
     * @return the Date object
     */
    public static Date getDateFromDb(String dateText) {
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            return dbDateFormat.parse(dateText);
        } catch ( ParseException e ) {
            e.printStackTrace();
            return null;
        }
    }


    /* Inner class that defines the table contents of the event table */
    public static final class EventEntry implements BaseColumns {

        private static final String LOG_TAG = EventEntry.class.getSimpleName();

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_EVENT).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_EVENT;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_EVENT;

        public static final String TABLE_NAME = "event";

        public static final String COLUMN_EVENT_ID = "event_id";
        public static final String COLUMN_TITLE = "judul";
        public static final String COLUMN_DATE = "tanggal";
        public static final String COLUMN_VENUE = "tempat";
        public static final String COLUMN_DESCRIPTION = "deskripsi";
        public static final String COLUMN_CATEGORY = "kategori";
        public static final String COLUMN_ORGANIZER = "penyelenggara";
        

        public static Uri buildEventUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        
        public static Uri buildEventWithStartDate(String startDate) {
            return CONTENT_URI.buildUpon().appendQueryParameter(COLUMN_DATE, startDate).build();
        }

        
        public static Uri buildEventWithId(String id) {
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }
        
        
        public static String getIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
        public static String getStartDateFromUri(Uri uri) {
            return uri.getQueryParameter(COLUMN_DATE);
        }

    }
}
