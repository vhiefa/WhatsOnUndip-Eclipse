package com.vhiefa.whatsonundip.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by Afifatul Mukaroh
 */
public class EventProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private EventDbHelper mOpenHelper;

    private static final int EVENT = 100;
    private static final int EVENT_AND_ID = 101;


    private static final SQLiteQueryBuilder sEventQueryBuilder;

    static {
        sEventQueryBuilder = new SQLiteQueryBuilder();
        sEventQueryBuilder.setTables(
                EventContract.EventEntry.TABLE_NAME
        );
    }

    
    
    private static final String sEvent =
            EventContract.EventEntry.TABLE_NAME;

    private static final String sEventWithStartDateSelection =
            EventContract.EventEntry.TABLE_NAME+
                    "." + EventContract.EventEntry.COLUMN_DATE + " >= ? ";

    private static final String sEventAndId =
            EventContract.EventEntry.TABLE_NAME+
                    "." + EventContract.EventEntry._ID + " = ? ";
    
    private Cursor getEvent(Uri uri, String[] projection, String sortOrder) {
        String startDate = EventContract.EventEntry.getStartDateFromUri(uri);

        String[] selectionArgs;
        String selection;

        if (startDate == null) {
            selection = sEvent;
           selectionArgs = null;
        } else {
            selectionArgs = new String[]{startDate};
            selection = sEventWithStartDateSelection;
        }

        return sEventQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    } 

    
    private Cursor getEventAndDate(
            Uri uri, String[] projection, String sortOrder) {
        String id = EventContract.EventEntry.getIdFromUri(uri);

        return sEventQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sEventAndId,
                new String[]{id},
                null,
                null,
                sortOrder
        );
    }
    
    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = EventContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, EventContract.PATH_EVENT, EVENT);
        matcher.addURI(authority, EventContract.PATH_EVENT + "/*", EVENT_AND_ID);


        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new EventDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,  String sortOrder) {

        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
        	// "EVENT/*"
	       case EVENT_AND_ID:
	        {
	            retCursor = getEventAndDate(uri, projection, sortOrder);
	            break;
	        } 
            // "EVENT"
            case EVENT: {
            	
            	retCursor = getEvent(uri, projection, sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
        	case EVENT_AND_ID:
        		return EventContract.EventEntry.CONTENT_ITEM_TYPE;  
            case EVENT:
                return EventContract.EventEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case EVENT: {
                long _id = db.insert(EventContract.EventEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = EventContract.EventEntry.buildEventUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case EVENT:
                rowsDeleted = db.delete(
                        EventContract.EventEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case EVENT:
                rowsUpdated = db.update(EventContract.EventEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case EVENT:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(EventContract.EventEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
