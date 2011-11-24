package com.profete162.WebcamWallonnes.Twitter;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Window;

import com.profete162.WebcamWallonnes.R;

public class TwitterSettingsActivity extends PreferenceActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.activity_twitter_preferences);
		

	}


	
}
