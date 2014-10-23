package com.profete162.WebcamWallonnes;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.profete162.WebcamWallonnes.Adapter.SectionedAdapter;
import com.profete162.WebcamWallonnes.Adapter.WebcamAdapter;
import com.profete162.WebcamWallonnes.Utils.DataBaseHelper;
import com.profete162.WebcamWallonnes.Utils.NumberedFragment;
import com.profete162.WebcamWallonnes.Utils.Utils;
import com.profete162.WebcamWallonnes.models.Webcam;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CamFragment extends NumberedFragment {

    DataBaseHelper mDbHelper;
    String lan = "fr";
    boolean scrolled;

    public void hideTuto() {
        getView().findViewById(R.id.tuto).setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        POSITION = 1;
        View v = inflater.inflate(R.layout.fragment_cam, null);
        ((TextView) v.findViewById(R.id.tText)).setText(R.string.tutoTabs);
        return v;
    }

    public void updateToLoc(Location location) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(location);
        ViewPager myPager = (ViewPager) getView().findViewById(R.id.pager);
        myPager.setAdapter(adapter);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(CamFragment.this.getActivity());
        scrolled = prefs.getBoolean("camScrolled", false);
        if ((!scrolled))
            getView().findViewById(R.id.tuto).setVisibility(View.VISIBLE);

        myPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int position) {
                SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(CamFragment.this.getActivity()).edit();
                if (!scrolled) {
                    e.putBoolean("camScrolled", true);
                    hideTuto();
                }
                e.putInt("scrollPosition", position);
                e.commit();
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        myPager.setCurrentItem(prefs.getInt("scrollPosition", 0));

        lan = getString(R.string.lan);
        // Bind the widget to the adapter
        //PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) this.getView().findViewById(R.id.tabs);
        //tabs.setViewPager(myPager);

    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Location l = ((DrawerActivity) this.getActivity()).loc;
        updateToLoc(l);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDbHelper != null)
            mDbHelper.close();
    }

    public Dialog displayDialog(final String url, String name) {
        final Dialog dialog = new Dialog(CamFragment.this
                .getActivity(), R.style.full_screen_dialog) {
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);

                ImageView view = new ImageView(CamFragment.this
                        .getActivity());

                Picasso.with(CamFragment.this.getActivity())
                        .load(url)
                        .into(view);

                view.setLayoutParams(new LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT));
                // view.setBackgroundResource(R.drawable.search_bg_shadow);
                final Dialog d = this;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        d.dismiss();
                    }
                });

                setContentView(view);
                getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT);
            }
        };
        dialog.setTitle(name);
        return dialog;

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    class ViewPagerAdapter extends PagerAdapter {

        Location loc;

        public ViewPagerAdapter(Location loc) {
            mDbHelper = new DataBaseHelper(getActivity());
            mDbHelper.openDataBase(DataBaseHelper.DB_NAME_WEBCAM);
            this.loc = loc;
        }

        public int getCount() {
            return 3;
        }

        public Object instantiateItem(ViewGroup container, int position) {
            final SectionedAdapter sectionAdapter;
            final ListView list = new ListView(getActivity());
            list.setSelector(R.drawable.listselector_green);
            //list.setDrawSelectorOnTop(true);
            ArrayList<Webcam> myList = new ArrayList<Webcam>();
            final Cursor webcamFavCursor = mDbHelper.fetchAllFavWebcam();
            for (webcamFavCursor.moveToFirst(); !webcamFavCursor.isAfterLast(); webcamFavCursor.moveToNext()) {
                myList.add(new Webcam(
                        webcamFavCursor.getInt(webcamFavCursor.getColumnIndex("_id")),
                        webcamFavCursor.getString(webcamFavCursor.getColumnIndex("city")),
                        Utils.getDistance(loc.getLatitude(), loc.getLongitude(), webcamFavCursor.getDouble(webcamFavCursor.getColumnIndex("lat")), webcamFavCursor.getDouble(webcamFavCursor.getColumnIndex("lng"))),
                        webcamFavCursor.getString(webcamFavCursor.getColumnIndex("img")),
                        webcamFavCursor.getDouble(webcamFavCursor.getColumnIndex("lat")),
                        webcamFavCursor.getDouble(webcamFavCursor.getColumnIndex("lng")),
                        webcamFavCursor.getInt(webcamFavCursor.getColumnIndex("starred")) == 1));
            }
            webcamFavCursor.close();

            sectionAdapter = new SectionedAdapter() {
                @Override
                protected View getHeaderView(String caption, int index,
                                             View convertView, ViewGroup parent) {
                    TextView result = (TextView) convertView;

                    if (convertView == null) {
                        result = (TextView) getActivity().getLayoutInflater()
                                .inflate(R.layout.header, null);
                    }

                    result.setText(caption);

                    return (result);
                }
            };

            sectionAdapter.addSection(CamFragment.this.getResources().getString(R.string.starred), new WebcamAdapter(
                    getActivity(), R.layout.row_cam,
                    myList));


            switch (position) {
                case 0:
                    try {
                        final Cursor webcamCursor = mDbHelper.fetchAllNonFavWebcam();

                        ArrayList<Webcam> mySecondList = new ArrayList<Webcam>();

                        for (webcamCursor.moveToFirst(); !webcamCursor.isAfterLast(); webcamCursor.moveToNext()) {
                            // The Cursor is now set to the right position
                            mySecondList.add(new Webcam(
                                    webcamCursor.getInt(webcamCursor.getColumnIndex("_id")),
                                    webcamCursor.getString(webcamCursor.getColumnIndex("city")),
                                    Utils.getDistance(loc.getLatitude(), loc.getLongitude(), webcamCursor.getDouble(webcamCursor.getColumnIndex("lat")), webcamCursor.getDouble(webcamCursor.getColumnIndex("lng"))),
                                    webcamCursor.getString(webcamCursor.getColumnIndex("img")),
                                    webcamCursor.getDouble(webcamCursor.getColumnIndex("lat")),
                                    webcamCursor.getDouble(webcamCursor.getColumnIndex("lng")),
                                    webcamCursor.getInt(webcamCursor.getColumnIndex("starred")) == 1));
                        }
                        //webcamCursor.close();
                        //final WebcamAdapter adapter = new WebcamAdapter(getActivity(),
                        //        R.layout.row_cam, myList);

                        sectionAdapter.addSection(getString(R.string.orderAlphabet), new WebcamAdapter(
                                getActivity(), R.layout.row_cam,
                                mySecondList));

                        list.setAdapter(sectionAdapter);
                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg) {
                                try {
                                    Webcam item = (Webcam) sectionAdapter.getItem(position);
                                    Dialog dialog = displayDialog(item.img, item.city);
                                    dialog.show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    try {

                        Cursor temp = mDbHelper.fetchAllZones(lan);
                        ArrayList<String> items = new ArrayList<String>();
                        for (temp.moveToFirst(); !temp.isAfterLast(); temp
                                .moveToNext()) {
                            items.add(temp.getString(temp
                                    .getColumnIndex("zone_" + lan)));
                        }
                        temp.close();

                        for (String aZone : items) {
                            ArrayList<Webcam> myTempList = new ArrayList<Webcam>();
                            Cursor webcamCursor = mDbHelper.fetchAllWebcam(aZone, lan);
                            for (webcamCursor.moveToFirst(); !webcamCursor.isAfterLast(); webcamCursor.moveToNext()) {
                                // The Cursor is now set to the right position
                                myTempList.add(new Webcam(
                                        webcamCursor.getInt(webcamCursor.getColumnIndex("_id")),
                                        webcamCursor.getString(webcamCursor.getColumnIndex("city")),
                                        Utils.getDistance(loc.getLatitude(), loc.getLongitude(), webcamCursor.getDouble(webcamCursor.getColumnIndex("lat")), webcamCursor.getDouble(webcamCursor.getColumnIndex("lng"))),
                                        webcamCursor.getString(webcamCursor.getColumnIndex("img")),
                                        webcamCursor.getDouble(webcamCursor.getColumnIndex("lat")),
                                        webcamCursor.getDouble(webcamCursor.getColumnIndex("lng")),
                                        webcamCursor.getInt(webcamCursor.getColumnIndex("starred")) == 1));
                            }
                            webcamCursor.close();
                            sectionAdapter.addSection(aZone, new WebcamAdapter(
                                    getActivity(), R.layout.row_cam,
                                    myTempList));
                        }

                        list.setAdapter(sectionAdapter);
                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                                SectionedAdapter myAdapter = (SectionedAdapter) list.getAdapter();
                                Webcam w = (Webcam) myAdapter.getItem(position);
                                //Log.e("","***"+w.city);

                                try {
                                    Dialog dialog = displayDialog(w.img, w.city);
                                    dialog.show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    try {
                        Cursor webcamCursor = mDbHelper.fetchAllNonFavWebcam();
                        ArrayList<Webcam> mySecondList = new ArrayList<Webcam>();
                        for (webcamCursor.moveToFirst(); !webcamCursor.isAfterLast(); webcamCursor.moveToNext()) {
                            // The Cursor is now set to the right position

                            mySecondList.add(new Webcam(
                                    webcamCursor.getInt(webcamCursor.getColumnIndex("_id")),
                                    webcamCursor.getString(webcamCursor.getColumnIndex("city")),
                                    Utils.getDistance(loc.getLatitude(), loc.getLongitude(), webcamCursor.getDouble(webcamCursor.getColumnIndex("lat")), webcamCursor.getDouble(webcamCursor.getColumnIndex("lng"))),
                                    webcamCursor.getString(webcamCursor.getColumnIndex("img")),
                                    webcamCursor.getDouble(webcamCursor.getColumnIndex("lat")),
                                    webcamCursor.getDouble(webcamCursor.getColumnIndex("lng")),
                                    webcamCursor.getInt(webcamCursor.getColumnIndex("starred")) == 1));
                        }
                        webcamCursor.close();
                        Collections.sort(mySecondList, new CustomComparator());

                        //myList.addAll(mySecondList);

                        sectionAdapter.addSection(getString(R.string.orderDist), new WebcamAdapter(
                                getActivity(), R.layout.row_cam,
                                mySecondList));


                        list.setAdapter(sectionAdapter);
                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                                try {
                                    Webcam w = (Webcam) adapter.getItemAtPosition((int) arg);
                                    Dialog dialog = displayDialog(w.img, w.city);
                                    dialog.show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

            }

            list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                               int position, long arg3) {

                    Webcam item = (Webcam) ((SectionedAdapter) arg0.getAdapter()).getItem(position);
                    //Toast.makeText(CamFragment.this.getActivity(), item.city, Toast.LENGTH_LONG).show();

                    if (item.starred)
                        mDbHelper.resetFavorite(item);
                    else
                        mDbHelper.setFavorite(item);

                    // FragmentTransaction ft = CamFragment.this.getActivity().getSupportFragmentManager().beginTransaction();
                    // ft.remove(CamFragment.this);
                    //ft.addToBackStack(null);
                    //ft.commit();
                    ((DrawerActivity) CamFragment.this.getActivity()).selectItem(1, true);

                    return false;
                }
            });


            container.addView(list);
            return list;
        }

        @Override
        public void destroyItem(ViewGroup container,
                                int position, Object object) {
            if (object instanceof View) {
                container.removeView((View) object);
            }
        }

        @Override
        public boolean isViewFromObject(View view,
                                        Object object) {
            return view.equals(object);
        }

        @Override
        public Parcelable saveState() {
            return null;
        }


    }

    public class CustomComparator implements Comparator<Webcam> {
        @Override
        public int compare(Webcam o1, Webcam o2) {
            if (o1.distance == o2.distance)
                return 0;
            else if (o1.distance > o2.distance)
                return 1;
            else return -1;
        }
    }
}

