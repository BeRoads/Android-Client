package com.profete162.WebcamWallonnes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.profete162.WebcamWallonnes.Twitter.TwitterSettingsActivity;
import com.profete162.WebcamWallonnes.adapter.TabsAdapter;

public class TwitterActivity extends FragmentActivity {
	// http://search.twitter.com/search.json?q=BETRAINS%20OR%20SNCB%20OR%20NMBS
	ViewPager mViewPager;
	TabsAdapter mTabsAdapter;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_twitter);
		getSupportActionBar().setNavigationMode(
				ActionBar.NAVIGATION_MODE_STANDARD);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("Twitter");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Settings").setIcon(android.R.drawable.ic_menu_preferences)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			Intent i = new Intent(this, TwitterSettingsActivity.class);
			startActivity(i);
			break;

		case android.R.id.home:
			finish();

		default:
			Log.i("", "ID: " + item.getItemId());

		}

		return super.onOptionsItemSelected(item);

	}

	

	
}
