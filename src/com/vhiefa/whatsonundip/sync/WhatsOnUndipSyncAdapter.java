package com.vhiefa.whatsonundip.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
//import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.vhiefa.whatsonundip.MainActivity;
import com.vhiefa.whatsonundip.R;
import com.vhiefa.whatsonundip.Utility;
import com.vhiefa.whatsonundip.data.EventContract;
import com.vhiefa.whatsonundip.data.EventContract.EventEntry;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class WhatsOnUndipSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = WhatsOnUndipSyncAdapter.class.getSimpleName();

    // Interval at which to sync with the event, in milliseconds.
    public static final int SYNC_INTERVAL = 60 * 60 * 3; //3 hours
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    public WhatsOnUndipSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");
        // Getting the zipcode to send to the API
     // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String eventJsonStr = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            URL url = new URL("http://spreadsheets.google.com/feeds/list/1Yh6MxmVd0-pB_SmX2Y62TxnAPIZotEGHzrYT-6BDSqk/od6/public/values?alt=json");

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            eventJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        // Now we have a String representing the complete forecast in JSON Format.
        // Fortunately parsing is easy:  constructor takes the JSON string and converts it
        // into an Object hierarchy for us.

        // Event information.  Each day's event info is an element of the "list" array.
    	final String OWM_FEED = "feed"; 
        final String OWM_ENTRY = "entry"; 
        final String OWM_EVENT_ID = "id";
        final String OWM_TITLE = "gsx$namaacara";
        final String OWM_DATE = "gsx$tanggalacara";
        final String OWM_VENUE = "gsx$tempatacara";
        final String OWM_DESCRIPTION = "gsx$deskripsi";
        final String OWM_CATEGORY = "gsx$kategori";
        final String OWM_ORGANIZER = "gsx$penyelenggara";

        try {
            JSONObject eventJson = new JSONObject(eventJsonStr);
            JSONObject feedJson = eventJson.getJSONObject(OWM_FEED);
            JSONArray eventResult = feedJson.getJSONArray(OWM_ENTRY);

            // Get and insert the new weather information into the database
            Vector<ContentValues> cVVector = new Vector<ContentValues>(eventResult.length());

            for(int i = 0; i < eventResult.length(); i++) {
            	// These are the values that will be collected.
            	

                String event_id, title, date, venue, description, category, organizer;

                // Get the JSON object representing the day
                JSONObject dayEvent = eventResult.getJSONObject(i);
                
                JSONObject idObj = dayEvent.getJSONObject(OWM_EVENT_ID);
                event_id = idObj.getString("$t");
               
                JSONObject titleObj = dayEvent.getJSONObject(OWM_TITLE);
                title = titleObj.getString("$t");
                
                JSONObject dateObj = dayEvent.getJSONObject(OWM_DATE); 
                date = dateObj.getString("$t");
                
                String[] timef=date.split("/");  // mm/dd/yyyy
                            
                int day = Integer.parseInt(timef[1]);
    	        int month = Integer.parseInt(timef[0])-1;
    	        int year = Integer.parseInt(timef[2])-1900;
    	        
    	        Date tanggal = new Date(year, month, day);
                           
                
                JSONObject venueObj = dayEvent.getJSONObject(OWM_VENUE);
                venue = venueObj.getString("$t");
                
                JSONObject descriptionObj = dayEvent.getJSONObject(OWM_DESCRIPTION);
                description = descriptionObj.getString("$t");
                
                JSONObject categoryObj = dayEvent.getJSONObject(OWM_CATEGORY);
                category = categoryObj.getString("$t");
                
                JSONObject  organizerObj = dayEvent.getJSONObject(OWM_ORGANIZER);
                organizer = organizerObj.getString("$t");

                ContentValues eventValues = new ContentValues();
                

                eventValues.put(EventEntry.COLUMN_EVENT_ID, event_id);
                eventValues.put(EventEntry.COLUMN_TITLE, title);
                eventValues.put(EventEntry.COLUMN_DATE,EventContract.getDbDateString(tanggal));
                eventValues.put(EventEntry.COLUMN_VENUE, venue);
                eventValues.put(EventEntry.COLUMN_DESCRIPTION, description);
                eventValues.put(EventEntry.COLUMN_CATEGORY, category);
                eventValues.put(EventEntry.COLUMN_ORGANIZER, organizer);


                cVVector.add(eventValues);
            }
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                getContext().getContentResolver().bulkInsert(EventEntry.CONTENT_URI, cvArray);
                
                //delete yesterday's event
                Calendar cal = Calendar.getInstance(); //Get's a calendar object with the current time.
                cal.add(Calendar.DATE, -1); //Signifies yesterday's date
                String yesterdayDate = EventContract.getDbDateString(cal.getTime());
                getContext().getContentResolver().delete(EventEntry.CONTENT_URI,
                		EventEntry.COLUMN_DATE + " <= ?",
                        new String[] {yesterdayDate});
                
               // notifyEvent(); //karena erorr
            }
            Log.d(LOG_TAG, "FetchWeatherTask Complete. " + cVVector.size() + " Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the forecast.
        return;
    }
    
    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
     /*   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).build();
            ContentResolver.requestSync(request);
        } else { */
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
       // }
    } 

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            onAccountCreated(newAccount, context);

        }
        return newAccount;
    }
    
    
    private static void onAccountCreated(Account newAccount, Context context) {
        
         //Since we've created an account
         
        WhatsOnUndipSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        
         // Without calling setSyncAutomatically, our periodic sync will not be enabled.
         
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

       
         //Finally, let's do a sync to get things started
         
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    } 

}