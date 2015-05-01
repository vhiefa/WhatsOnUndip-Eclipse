package com.vhiefa.whatsonundip;

import com.vhiefa.whatsonundip.sync.WhatsOnUndipSyncAdapter;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


/**
 * Created by Afifatul Mukaroh
 */
public class MainActivity extends ActionBarActivity implements EventFragment.Callback  {

    private boolean mTwoPane;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if (findViewById(R.id.event_detail_container)!=null){
        	mTwoPane = true;
        	
        	if (savedInstanceState == null){
        		getSupportFragmentManager().beginTransaction()
        		.replace(R.id.event_detail_container, new DetailFragment())
        		.commit();
        	} else {
        		mTwoPane = false;
        	}
        }
        
        WhatsOnUndipSyncAdapter.initializeSyncAdapter(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_submit) {
            Intent i = null;
			i = new Intent(MainActivity.this, SubmitEventActivity.class);
			startActivity(i);
            return true;
        
        }
        
        if (id == R.id.action_about) {
            Intent i = null;
			i = new Intent(MainActivity.this, AboutActivity.class);
			startActivity(i);
            return true;
        
        }
        
        return super.onOptionsItemSelected(item);
    }


	@Override
	public void onItemSelected(String id) {
		if (mTwoPane){
			// In two-pane mode, show the detail view in this activity by
            // adding or replacing the dtail fragment using a
            // fragment transaction
			Bundle args = new Bundle ();
			args.putString(DetailActivity.ID_KEY, id);
			
			DetailFragment fragment = new DetailFragment();
			fragment.setArguments(args);
			
			getSupportFragmentManager().beginTransaction()
				.replace(R.id.event_detail_container, fragment)
				.commit();
		} else {
			Intent intent = new Intent(this, DetailActivity.class)
				.putExtra(DetailActivity.ID_KEY, id);
			startActivity(intent);
		}
		
	}


}