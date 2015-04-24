package com.vhiefa.whatsonundip;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vhiefa.whatsonundip.data.EventContract.EventEntry;
import com.vhiefa.whatsonundip.data.EventContract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Vector;

public class FetchEventTask extends AsyncTask<Void, Void, Void> {

    private final String LOG_TAG = FetchEventTask.class.getSimpleName();
    private final Context mContext;

    public FetchEventTask(Context context) {
        mContext = context;
    }
    
    // brings our database to an empty state
    public void deleteAllRecords() {
        mContext.getContentResolver().delete(
                EventContract.EventEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                EventContract.EventEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        cursor.close();
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private void getEventDataFromJson(String eventJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.

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
        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            mContext.getContentResolver().bulkInsert(EventEntry.CONTENT_URI, cvArray);
            
        }
    }

    @Override
    protected Void doInBackground(Void... params) {

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
            URL url = new URL("https://spreadsheets.google.com/feeds/list/0AszGUUE4pwmQdDg1LTZoa0xDVGktbDNrM1V4amhNRXc/od6/public/values?alt=json");

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
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
                return null;
            }
            eventJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
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
        
        try {
            getEventDataFromJson(eventJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
     }

}
