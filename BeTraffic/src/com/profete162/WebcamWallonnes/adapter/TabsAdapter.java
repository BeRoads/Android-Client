package com.profete162.WebcamWallonnes.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ActionBar.Tab;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

public class TabsAdapter extends FragmentPagerAdapter implements
ViewPager.OnPageChangeListener, ActionBar.TabListener {
	private final Context mContext;
	private final ActionBar mActionBar;
	private final ViewPager mViewPager;
	private int mIndex;
	private final ArrayList<String> mTabs = new ArrayList<String>();

	public TabsAdapter(FragmentActivity activity, ActionBar actionBar,
			ViewPager pager) {
		super(activity.getSupportFragmentManager());
		mContext = activity;
		mActionBar = actionBar;
		mViewPager = pager;
		mViewPager.setAdapter(this);
		mViewPager.setOnPageChangeListener(this);
	}

	public void addTab(ActionBar.Tab tab, Class<?> clss, int index) {
		mTabs.add(clss.getName());
		mActionBar.addTab(tab.setTabListener(this));
		notifyDataSetChanged();
		mIndex=index;
	}

	@Override
	public int getCount() {
		return mTabs.size();
	}
	
	public int getIndex() {
		return this.mIndex;
	}

	@Override
	public Fragment getItem(int position) {
		return Fragment.instantiate(mContext, mTabs.get(position), null);
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {
		mActionBar.setSelectedNavigationItem(position);
		if(position==2)
			Toast.makeText(mContext, "TODO", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}
}
