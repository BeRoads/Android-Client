package com.profete162.WebcamWallonnes.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.profete162.WebcamWallonnes.Utils.Webcam;
import com.profete162.WebcamWallonnes.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class WebcamAdapter extends ArrayAdapter<Webcam> {

    ArrayList<Webcam> list;
    String[] icons;
    Context mContext;

    public WebcamAdapter(Context context, int textViewResourceId, ArrayList<Webcam> objects) {

        super(context, textViewResourceId, objects);
        this.icons = context.getResources().getStringArray(R.array.menuIcons);
        this.mContext = context;
        list = objects;
    }

    boolean empty=false;

    @Override
    public int getCount() {
        if (list.size()!=0)
            return list.size();
        else{
            empty = true;
            return 1;
        }


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (empty) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();  // we get a reference to the activity
            View tuto =  inflater.inflate(R.layout.row_tuto, parent, false);
            ((TextView)tuto.findViewById(R.id.tText)).setText(R.string.tutoFav);
            return tuto;
        }

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();  // we get a reference to the activity
            row = inflater.inflate(R.layout.row_cam, parent, false);
        }

        TextView tTitle = (TextView) row.findViewById(R.id.tTitle);
        tTitle.setText(list.get(position).city);

        TextView tDist = (TextView) row.findViewById(R.id.tDist);
        int iDistance = ((int) list.get(position).distance) / 100;
        tDist.setText((double) iDistance / 10 + "km");

        //UrlImageViewHelper.setUrlDrawable((ImageView) row.findViewById(R.id.iv), list.get(position).img, null, 20 * DateUtils.MINUTE_IN_MILLIS);

        ImageView iv=(ImageView) row.findViewById(R.id.iv);

        Picasso.with(row.getContext())
                .load( list.get(position).img)
                .resize(75, 75).placeholder(null)
                .centerCrop()
                .into(iv);

        return row;

    }
}