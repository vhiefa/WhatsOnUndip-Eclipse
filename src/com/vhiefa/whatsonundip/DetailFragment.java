package com.vhiefa.whatsonundip;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
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
     * A placeholder fragment containing a simple view.
     */
public class DetailFragment extends Fragment implements LoaderCallbacks<Cursor> {

        private static final String LOG_TAG = DetailFragment.class.getSimpleName();

        private static final String event_SHARE_HASHTAG = " #whatsOnUndipApp";

        public static final String ID_KEY = "event_id";

        private ShareActionProvider mShareActionProvider;
      //  private String mLocation;
        private String mEvent;
        private String mIdStr;

        private static final int DETAIL_LOADER = 0;

     // For the event view we're showing only a small subset of the stored data.
        // Specify the columns we need.
        private static final String[] EVENT_COLUMNS = {
                // In this case the id needs to be fully qualified with a table name, since
                // the content provider joins the location & weather tables in the background
                // (both have an _id column)
                // On the one hand, that's annoying.  On the other, you can search the weather table
                // using the location set by the user, which is only in the Location table.
                // So the convenience is worth it.
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
        public void onSaveInstanceState(Bundle outState) {
         //  outState.putString(LOCATION_KEY, mLocation);
            super.onSaveInstanceState(outState);
        } 

        @Override
        public void onResume() {
            super.onResume();
          //  if (mLocation != null &&
           //         !mLocation.equals(Utility.getPreferredLocation(getActivity()))) {
               getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
          //  }

        }

       @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            //return inflater.inflate(R.layout.fragment_detail, container, false);
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
        public void onActivityCreated(Bundle savedInstanceState) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
         //   if (savedInstanceState != null) {
          //      mLocation = savedInstanceState.getString(LOCATION_KEY);
          //  } 
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Log.v(LOG_TAG, "In onCreateLoader");
            Intent intent = getActivity().getIntent();
            if (intent == null || !intent.hasExtra(ID_KEY)) {
                return null;
            }
            
            String eventId = getArguments().getString(DetailActivity.ID_KEY);

            // Sort order:  Ascending, by id.
            String sortOrder = EventContract.EventEntry._ID + " ASC";

            Uri eventUri = EventContract.EventEntry.buildEventWithId(eventId);
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
            	
            
            	
            

            String date = Utility.formatDate(
                    data.getString(data.getColumnIndex(EventEntry.COLUMN_DATE)));
            mDateView .setText(date);

            String description =
                    data.getString(data.getColumnIndex(EventEntry.COLUMN_DESCRIPTION));
            mDescriptionView.setText(description);
            
            String title =
                    data.getString(data.getColumnIndex(EventEntry.COLUMN_TITLE));
             mTitleView       .setText(title);
            
            String venue =
                    data.getString(data.getColumnIndex(EventEntry.COLUMN_VENUE));
            mVenueView  .setText(venue);
            
            String organizer =
                    data.getString(data.getColumnIndex(EventEntry.COLUMN_ORGANIZER));
            mOrganizerView      .setText(organizer);
            
            String category =
                    data.getString(data.getColumnIndex(EventEntry.COLUMN_CATEGORY));
           mCategoryView    .setText(category);


           mIconView.setImageResource(Utility.getIconResourceForEventCategory(category));


            // We still need this for the share intent
            mEvent = String.format("%s \n %s \n %s  \n %s", title, date, venue, description);

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