package com.profete162.WebcamWallonnes.Utils;

import android.location.Location;
import androidx.fragment.app.Fragment;

import com.profete162.WebcamWallonnes.CamFragment;

/**
 * Created by 201601 on 21/06/13.
 */
public class NumberedFragment extends Fragment {

   public  int POSITION;
    public int getNum() {
        return POSITION;
    }

    public void updateLoc(Location location) {
        if (this instanceof CamFragment)
            ((CamFragment) this).updateToLoc(location);
    }
}
