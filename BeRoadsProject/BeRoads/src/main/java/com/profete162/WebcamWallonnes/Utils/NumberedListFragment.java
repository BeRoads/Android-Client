package com.profete162.WebcamWallonnes.Utils;


import android.location.Location;
import android.support.v4.app.ListFragment;

import com.profete162.WebcamWallonnes.CamFragment;
import com.profete162.WebcamWallonnes.RadarFragment;
import com.profete162.WebcamWallonnes.TrafficFragment;

/**
 * Created by 201601 on 21/06/13.
 */
public class NumberedListFragment extends ListFragment {

   public  int POSITION;
    public int getNum() {
        return POSITION;
    }

    public void updateLoc(Location location) {

        if (this instanceof TrafficFragment)
            ((TrafficFragment) this).updateToLoc(location);

        else if (this instanceof RadarFragment)
            ((RadarFragment) this).updateToLoc(location);

    }
}
