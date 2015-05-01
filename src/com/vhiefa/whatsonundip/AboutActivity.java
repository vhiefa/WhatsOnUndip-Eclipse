package com.vhiefa.whatsonundip;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

/**
 * Created by Afifatul Mukaroh
 */
public class AboutActivity extends PreferenceActivity
		implements Preference.OnPreferenceChangeListener {
	
boolean mBindingPreference;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	// Add 'general' preferences, defined in the XML file
	addPreferencesFromResource(R.xml.pref_general);

	}

	@Override
	public boolean onPreferenceChange(Preference arg0, Object arg1) {
		// TODO Auto-generated method stub
		return false;
	}

}
