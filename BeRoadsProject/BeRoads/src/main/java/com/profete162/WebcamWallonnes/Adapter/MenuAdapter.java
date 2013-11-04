package com.profete162.WebcamWallonnes.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.profete162.WebcamWallonnes.R;


public class MenuAdapter extends ArrayAdapter<String> {

    String[] list;
    String[] icons;
    Context mContext;

    public MenuAdapter(Context context, int textViewResourceId, String[] objects) {

        super(context, textViewResourceId, objects);
        this.icons = context.getResources().getStringArray(R.array.menuIcons);
        this.mContext = context;
        list = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {

            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();  // we get a reference to the activity
            row = inflater.inflate(R.layout.row_menu, parent, false);
        }

        TextView tTitle = (TextView) row.findViewById(R.id.tTitle);
        ImageView icon = (ImageView) row.findViewById(R.id.icon);

        tTitle.setText(list[position]);
        icon.setImageResource(mContext.getResources().getIdentifier("ic_c_"+icons[position], "drawable"
                , mContext.getPackageName()));

        return row;

    }
}