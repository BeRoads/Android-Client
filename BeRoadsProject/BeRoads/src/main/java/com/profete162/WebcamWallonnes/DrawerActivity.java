package com.profete162.WebcamWallonnes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.profete162.WebcamWallonnes.Adapter.MenuAdapter;
import com.profete162.WebcamWallonnes.Utils.GPS;
import com.profete162.WebcamWallonnes.Utils.NumberedFragment;
import com.profete162.WebcamWallonnes.Utils.NumberedListFragment;

import java.util.concurrent.atomic.AtomicInteger;

public class DrawerActivity extends FragmentActivity {

    public static final String TAG = "WazaBe";
    int count=0;
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_APP_VERSION = "appVersion";
    public static final String PROPERTY_ON_SERVER_EXPIRATION_TIME =
            "onServerExpirationTimeMs";
    /**
     * Default lifespan (7 days) of a reservation until it is considered expired.
     */
    public static final long REGISTRATION_EXPIRY_TIME_MS = DateUtils.DAY_IN_MILLIS * 7;
    public FrameLayout detail_frame;
    public DrawerLayout mDrawerLayout;
    int color = R.color.holo_blue_dark;
    //Fragment fragment;
    boolean show;
    Fragment f;
    Location loc;
    /**
     * Substitute you own sender ID here.
     */
    String SENDER_ID = "570622304067";
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    String regid;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private ListView mDrawerList;
    private String[] mMenuTitles;
    private int icon;
    private Menu menu;
    private MyMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        loc = GPS.getLastLoc(this);
        setContentView(R.layout.activity_main);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mTitle = this.getString(R.string.app_name);

        getSupportFragmentManager().addOnBackStackChangedListener(getListener());

        if (mDrawerLayout != null) {
            mDrawerToggle = new ActionBarDrawerToggle(
                    this,                  /* host Activity */
                    mDrawerLayout,         /* DrawerLayout object */
                    R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                    R.string.app_name,  /* "open drawer" description */
                    R.string.app_name  /* "close drawer" description */
            ) {

                /** Called when a drawer has settled in a completely closed state. */
                public void onDrawerClosed(View view) {
                    getActionBar().setTitle(mTitle);
                    getActionBar().setIcon(icon);
                    //setColor(false);
                }

                /** Called when a drawer has settled in a completely open state. */
                public void onDrawerOpened(View drawerView) {
                    count++;
                    if(count>5){
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=dQw4w9WgXcQ"));
                        startActivity(browserIntent);
                        count=0;
                    }
                    getActionBar().setTitle(getString(R.string.app_name));
                    getActionBar().setIcon(R.drawable.ic_launcher);
                    if (PreferenceManager.getDefaultSharedPreferences(DrawerActivity.this).getBoolean("tuto", true))
                        try {
                            f.getView().findViewById(R.id.tuto).setVisibility(View.GONE);
                            PreferenceManager.getDefaultSharedPreferences(DrawerActivity.this).edit().putBoolean("tuto", false).commit();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    //setColor(true);
                }
            };

            // Set the drawer toggle as the DrawerListener
            mDrawerLayout.setDrawerListener(mDrawerToggle);
            //if(this.getResources().getBoolean(R.bool.lockDrawer))
            //   mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);

        }

        getActionBar().setDisplayHomeAsUpEnabled(mDrawerLayout != null);
        getActionBar().setHomeButtonEnabled(mDrawerLayout != null);

        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        if (mDrawerList != null) {
            mMenuTitles = getResources().getStringArray(R.array.menuItems);
            // Set the adapter for the list view
            Button b = new Button(this);
            b.setText("GCM");
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(DrawerActivity.this, NotifConfigActivity.class));

                }
            });
           // mDrawerList.addFooterView(b);
            mDrawerList.setAdapter(new MenuAdapter(this,
                    R.layout.row_menu, mMenuTitles));


            // Set the list's click listener
            mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
            mDrawerList.setBackgroundColor(getResources().getColor(R.color.greyMenu));
            selectItem(0, false);
            setColor(false);
        } else {
            f = new MyMapFragment();
            // Insert the fragment by replacing any existing fragment
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.replace(R.id.content_frame, f);
            ft.commit();
            if (loc != null)
                ((MyMapFragment) f).doStuff();
            detail_frame = (FrameLayout) findViewById(R.id.detail_frame);
        }

        if (loc == null) {
            // Acquire a reference to the system Location Manager
            final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

// Define a listener that responds to location updates
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    // Called when a new location is found by the network location provider.
                    locationManager.removeUpdates(this);

                    if (loc == null) {
                        DrawerActivity.this.loc = location;
                        Log.e("", "LOCVATION" + f);

                        if (f instanceof NumberedFragment)
                            ((NumberedFragment) f).updateLoc(location);

                        if (f instanceof NumberedListFragment)
                            ((NumberedListFragment) f).updateLoc(location);

                        if (f instanceof MyMapFragment)
                            ((MyMapFragment) f).updateLoc(location);
                    }
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            };

// Register the listener with the Location Manager to receive location updates

            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, locationListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }


    }

    private FragmentManager.OnBackStackChangedListener getListener() {
        FragmentManager.OnBackStackChangedListener result = new FragmentManager.OnBackStackChangedListener() {
            public void onBackStackChanged() {

                FragmentManager manager = getSupportFragmentManager();

                if (manager != null) {
                    int pos = 0;
                    f = manager.findFragmentById(R.id.content_frame);
                    if (f instanceof MyMapFragment) {
                        DrawerActivity.this.mapFragment = (MyMapFragment) f;
                        show = true;
                    } else
                        show = false;

                    Log.e("", "STACK: " + mapFragment);

                    if (show)
                        showAction();
                    else
                        hideAction();

                    if (f instanceof NumberedFragment)
                        pos = ((NumberedFragment) f).POSITION;

                    if (f instanceof NumberedListFragment)
                        pos = ((NumberedListFragment) f).POSITION;

                    mTitle = DrawerActivity.this.getResources().getStringArray(R.array.menuItems)[pos];

                    switch (pos) {
                        case 1:
                            color = R.color.holo_green_dark;
                            break;
                        case 2:
                            color = R.color.holo_yellow_dark;
                            break;
                        case 3:
                            color = R.color.holo_red_dark;
                            break;
                        case 4:
                            color = R.color.holo_grey_dark;
                            try {
                                mTitle = mTitle + " (v" + getPackageManager().getPackageInfo(getPackageName(), 0).versionName + ")";
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                            break;
                        default:
                            color = R.color.holo_blue_dark;
                            break;
                    }


                    updateActionBar(pos);
                }
            }
        };


        return result;
    }

    private void setColor(boolean black) {
        int newColor = black ? this.getResources().getColor(R.color.holo_grey_dark) : this.getResources().getColor(color);

        Drawable colorDrawable = new ColorDrawable(newColor);
        Drawable bottomDrawable = getResources().getDrawable(
                R.drawable.actionbar_bottom);
        LayerDrawable ld = new LayerDrawable(new Drawable[]{colorDrawable,
                bottomDrawable});

        getActionBar().setBackgroundDrawable(ld);

        // http://stackoverflow.com/questions/11002691/actionbar-setbackgrounddrawable-nulling-background-from-thread-handler
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayShowTitleEnabled(true);
    }

    /**
     * Swaps fragments in the main content view
     */
    public void selectItem(int position, boolean add) {
        // Create a new fragment and specify the planet to show based on position
        switch (position) {
            case 1:
                color = R.color.holo_green_dark;
                f = new CamFragment();
                setProgressBarIndeterminateVisibility(false);
                break;
            case 2:
                color = R.color.holo_yellow_dark;
                setProgressBarIndeterminateVisibility(true);
                f = new RadarFragment();
                break;
            case 3:
                color = R.color.holo_red_dark;
                setProgressBarIndeterminateVisibility(true);
                f = new MyMapFragment();
                break;
            case 4:
                color = R.color.holo_grey_dark;
                setProgressBarIndeterminateVisibility(false);
                f = new AboutFragment();
                break;
            default:
                color = R.color.holo_blue_dark;
                setProgressBarIndeterminateVisibility(true);
                f = new TrafficFragment();
                break;
        }
        mTitle = DrawerActivity.this.getResources().getStringArray(R.array.menuItems)[position];
        updateActionBar(position);

        // Insert the fragment by replacing any existing fragment
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.replace(R.id.content_frame, f);
        if (add)
            ft.addToBackStack("CVE");
        ft.commit();

        // Highlight the selected item, update the title, and close the drawer
        // mDrawerList.setItemChecked(position, true);

        if (mDrawerLayout != null)
            mDrawerLayout.closeDrawer(mDrawerList);
    }

    public void updateActionBar(int position) {

        icon = getResources().getIdentifier("ic_w_" + this.getResources().getStringArray(R.array.menuIcons)[position], "drawable"
                , this.getPackageName());
        getActionBar().setIcon(icon);
        getActionBar().setTitle(mTitle);
        //setTitle(mMenuTitles[position]);
        setColor(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(Menu.NONE, 0, Menu.NONE, "Refresh").setIcon(R.drawable.ic_menu_refresh).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(Menu.NONE, 1, Menu.NONE, "Traffic").setIcon(R.drawable.ic_menu_mapmode).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        this.menu = menu;
        if (show)
            showAction();
        else
            hideAction();

        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        if (mDrawerToggle != null)
            mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDrawerToggle != null)
            mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case 0:
                try {
                    mapFragment.doStuff();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            case 1:
                try {
                    mapFragment.toggleTraffic();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            case android.R.id.home:
                if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) {
                    return true;
                }
                return super.onOptionsItemSelected(item);
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    public void hideAction() {
        if (menu != null) {
            try {
                menu.findItem(0).setVisible(false);
                menu.findItem(1).setVisible(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void showAction() {
        if (menu != null) {
            try {
                menu.findItem(0).setVisible(true);
                menu.findItem(1).setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            count=0;
            selectItem(position, true);
        }

    }


}
