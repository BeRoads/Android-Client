package com.profete162.WebcamWallonnes;

import android.os.Bundle;
import android.support.v4.app.FragmentMapActivity;
import android.support.v4.app.ListFragment;

public class MapActivity extends FragmentMapActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

	}

	public static class WeeklyWeatherFragment extends ListFragment {

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

		}

	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
