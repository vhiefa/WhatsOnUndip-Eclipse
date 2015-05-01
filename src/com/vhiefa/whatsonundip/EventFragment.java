package com.vhiefa.whatsonundip;

import java.util.Date;

import com.vhiefa.whatsonundip.data.EventContract;
import com.vhiefa.whatsonundip.data.EventContract.EventEntry;
import com.vhiefa.whatsonundip.sync.WhatsOnUndipSyncAdapter;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
/**
 * Created by Afifatul Mukaroh
 */
public class EventFragment extends Fragment implements LoaderCallbacks<Cursor> {

    private EventAdapter mEventAdapter;

    private static final int EVENT_LOADER = 0;

    // For the event view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] EVENT_COLUMNS = {
            EventEntry.TABLE_NAME + "." + EventEntry._ID,
            EventEntry.COLUMN_EVENT_ID,
            EventEntry.COLUMN_TITLE,
            EventEntry.COLUMN_DATE,
            EventEntry.COLUMN_VENUE,
            EventEntry.COLUMN_DESCRIPTION,
            EventEntry.COLUMN_CATEGORY,
            EventEntry.COLUMN_ORGANIZER
    };


    // These indices are tied to EVENT_COLUMNS.  If EVENT_COLUMNS changes, these
    // must change.
    public static final int COL_ID = 0;
    public static final int COL_EVENT_ID = 1;
    public static final int COL_EVENT_TITLE = 2;
    public static final int COL_EVENT_DATE = 3;
    public static final int COL_EVENT_VENUE = 4;
    public static final int COL_EVENT_DESCRIPTION = 5;
    public static final int COL_EVENT_CATEGORY = 6;
    public static final int COL_EVENT_ORGANIZER = 7;


    public EventFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.eventfragment, menu);
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateEvent();
            return true;
        }
        return super.onOptionsItemSelected(item);
    } 

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_event);
        
        mEventAdapter = new EventAdapter (getActivity(),null, 0);
        
        listView.setAdapter(mEventAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = mEventAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                	((Callback)getActivity())
                	      .onItemSelected(cursor.getString(COL_ID));
                }
            }
        });
        

        return rootView;
    }
    
    public interface Callback{
    	public void onItemSelected(String id);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(EVENT_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public void updateEvent() {
    	// WhatsOnUndipSyncAdapter.syncImmediately(getActivity());
        new FetchEventTask(getActivity()).execute();
    }

    @Override
    public void onResume() {
        super.onResume();
            getLoaderManager().restartLoader(EVENT_LOADER, null, this);
    }
    
    @Override
    public void onStart() {
        super.onStart();
        updateEvent();
    }
    


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.
        // To only show current and future dates, get the String representation for today,
        // and filter the query to return event only for dates after or including today.
    	
        // Only return data after today.
        String startDate = EventContract.getDbDateString(new Date());

        // Sort order:  Ascending, by date.
        String sortOrder = EventEntry.COLUMN_DATE + " ASC";

        Uri eventUri = EventEntry.buildEventWithStartDate(startDate);
        Log.v("buildEventWithStartDate", eventUri.toString());
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                eventUri,
                EVENT_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mEventAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mEventAdapter.swapCursor(null);
    }
    

}