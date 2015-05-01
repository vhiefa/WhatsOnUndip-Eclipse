package com.vhiefa.whatsonundip;

import java.util.GregorianCalendar;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
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

import com.vhiefa.whatsonundip.data.EventContract;
import com.vhiefa.whatsonundip.data.EventContract.EventEntry;


/**
 * Created by Afifatul Mukaroh
 */
public class DetailFragment extends Fragment implements LoaderCallbacks<Cursor> {

        private static final String LOG_TAG = DetailFragment.class.getSimpleName();
        private static final String event_SHARE_HASHTAG = "\n\n#WhatsOnUndipApp ease you to find or share events on Undip\nFind the app on Playstore!";
       // public static final String ID_KEY = "event_id";
        private ShareActionProvider mShareActionProvider;
        private String mIdEvent, mEvent, mEventName, mEventPlace, mEventDesc, mEventDate;
        private static final int DETAIL_LOADER = 0;

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
        
        private ImageView mIconView;
        private TextView mDateView;
        private TextView mTitleView;
        private TextView mVenueView;
        private TextView mDescriptionView;
        private TextView mCategoryView;
        private TextView mOrganizerView;


        public DetailFragment() {
            setHasOptionsMenu(true);
        }

       @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	   
    	   Bundle arguments = getArguments();
           if (arguments != null) {
               mIdEvent = arguments.getString(DetailActivity.ID_KEY);
           }
    	   
    	   
    	   View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
    	   mIconView = (ImageView) rootView.findViewById(R.id.detail_icon);
           mDateView = (TextView) rootView.findViewById(R.id.detail_date_textview);
           mVenueView = (TextView) rootView.findViewById(R.id.detail_venue_textview);
           mDescriptionView = (TextView) rootView.findViewById(R.id.detail_description_textview);
           mCategoryView = (TextView) rootView.findViewById(R.id.detail_category_textview);
           mOrganizerView = (TextView) rootView.findViewById(R.id.detail_organizer_textview);
           mTitleView = (TextView) rootView.findViewById(R.id.detail_title_textview);
           return rootView;
           
        }
       
       @Override
       public void onResume() {
           super.onResume();
           
              Bundle arguments = getArguments();
              if (arguments !=null && arguments.containsKey(DetailActivity.ID_KEY)){
               	getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
               }

       }
       
        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            Log.v(LOG_TAG, "in onCreateOptionsMenu");
            // Inflate the menu; this adds items to the action bar if it is present.
            inflater.inflate(R.menu.detailfragment, menu);

            // Retrieve the share menu item
            MenuItem menuItem = menu.findItem(R.id.action_share);

            // Get the provider and hold onto it to set/change the share intent.
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

            // If onLoadFinished happens before this, we can go ahead and set the share intent now.
            if (mEvent != null) {
                mShareActionProvider.setShareIntent(createShareEventIntent());
            }
            
            
            
        }

        private Intent createShareEventIntent() {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, mEvent + event_SHARE_HASHTAG);
            return shareIntent;
        }
        


       @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();            
            if ((id == R.id.action_addToCalendar) && (mEvent != null)) {
	           	 Intent calIntent = new Intent(Intent.ACTION_INSERT); 
	           	calIntent.setType("vnd.android.cursor.item/event");    
	           	calIntent.putExtra(Events.TITLE, mEventName); 
	           	calIntent.putExtra(Events.EVENT_LOCATION, mEventPlace); 
	           	calIntent.putExtra(Events.DESCRIPTION, mEventDesc); 
	        	           	
	           	String[] dateArray = mEventDate.split("");
	           	String sYear = dateArray[1]+dateArray[2]+dateArray[3]+dateArray[4];
	           	String sMonth = dateArray[5]+dateArray[6];
	           	String sDay = dateArray[7]+dateArray[8];
	           	
	           	int year = Integer.parseInt(sYear);
	           	int month = Integer.parseInt(sMonth);
	           	int day = Integer.parseInt(sDay);
	           	
	           	
	           	
	           	GregorianCalendar calDate = new GregorianCalendar(year, month-1, day);
	           	calIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true); 
	           	calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calDate.getTimeInMillis()); 
	           	calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,  calDate.getTimeInMillis()); 
	           	 
	           	startActivity(calIntent);
                return true;
            }
            return super.onOptionsItemSelected(item);
        } 
        
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            
            Bundle arguments = getArguments();
            if (arguments !=null && arguments.containsKey(DetailActivity.ID_KEY)){
             	getLoaderManager().initLoader(DETAIL_LOADER, null, this);
             }
            
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Log.v(LOG_TAG, "In onCreateLoader");
            
            // Sort order:  Ascending, by id.
            String sortOrder = EventContract.EventEntry._ID + " ASC";

            Uri eventUri = EventContract.EventEntry.buildEventWithId(mIdEvent);
            Log.v(LOG_TAG, eventUri.toString());

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
            Log.v(LOG_TAG, "In onLoadFinished");
            //if (!data.moveToFirst()) { return; }
            if (data != null && data.moveToFirst()) {
            	
	            String date = data.getString(data.getColumnIndex(EventEntry.COLUMN_DATE));
	            mDateView .setText("Date : "+Utility.formatDate(date));
	
	            String description =data.getString(data.getColumnIndex(EventEntry.COLUMN_DESCRIPTION));
	            mDescriptionView.setText("Description : \n"+description);
	            
	            String title = data.getString(data.getColumnIndex(EventEntry.COLUMN_TITLE));
	            mTitleView.setText(title);
	            
	            String venue =  data.getString(data.getColumnIndex(EventEntry.COLUMN_VENUE));
	            mVenueView.setText("Place : "+venue);
	            
	            String organizer =  data.getString(data.getColumnIndex(EventEntry.COLUMN_ORGANIZER));
	            mOrganizerView.setText("Organizer : "+organizer);
	            
	            String category =  data.getString(data.getColumnIndex(EventEntry.COLUMN_CATEGORY));
	            mCategoryView .setText(category); 
	
	
	            mIconView.setImageResource(Utility.getIconResourceForEventCategory(category));
	
	
	            // We still need this for the share intent
	            mEvent = String.format("%s \n\nTanggal : %s \nTempat : %s  \nDeskripsi : \n %s", title, Utility.formatDate(date), venue, description);
	            mEventName = title;
	            mEventPlace = venue;
	            mEventDate = date;
	            mEventDesc = description;         
	
	            Log.v(LOG_TAG, "event String: " + mEvent);
	
	            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
	            if (mShareActionProvider != null) {
	                mShareActionProvider.setShareIntent(createShareEventIntent());
	            }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) { }
    }