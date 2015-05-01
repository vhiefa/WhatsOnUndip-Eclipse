package com.vhiefa.whatsonundip;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

/**
 * Created by Afifatul Mukaroh
 */

public class DetailActivity extends ActionBarActivity {
	private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    public static final String ID_KEY = "event_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
        	
        	String id = getIntent().getStringExtra(ID_KEY);

            Log.v(LOG_TAG, "arguments id 1: " + id);

            Bundle arguments = new Bundle();
            arguments.putString(DetailActivity.ID_KEY, id);

            Log.v(LOG_TAG, "arguments id 2: " + id);
        	
        	
            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.event_detail_container, fragment)
                    .commit();
            
        }
    }
    
}