package com.vhiefa.whatsonundip;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by Afifatul Mukaroh
 */
public class SubmitEventActivity  extends ActionBarActivity {

	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_submitevent);
       
	        if (savedInstanceState == null) {
	            getSupportFragmentManager().beginTransaction()
	                    .add(R.id.submitevent_container, new SubmitEventFragment())
	                    .commit();
	        }
	    }
	    
	
	
/*	 @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	 @Override
	 public Intent getParentActivityIntent(){
		 return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	 }*/
}

