package com.profete162.WebcamWallonnes;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.profete162.WebcamWallonnes.Utils.NumberedFragment;

public class MyMapFragment extends NumberedFragment {

    ZeMapFragment mMapFragment;
    View view = null;

    public void doStuff() {
        if (mMapFragment != null)
            mMapFragment.doStuff();
    }

    public void toggleTraffic() {
        if (mMapFragment != null)
            mMapFragment.toggleTraffic();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.POSITION = 3;
        // if (this.getChildFragmentManager().findFragmentById(R.id.container) != null)
        //    return inflater.inflate(R.layout.fragment_map, container, false);
        mMapFragment = new ZeMapFragment();
        view = inflater.inflate(R.layout.fragment_map, container, false);
        FragmentTransaction fragmentTransaction = this.getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, mMapFragment);
        fragmentTransaction.commit();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}