package com.profete162.WebcamWallonnes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.profete162.WebcamWallonnes.Utils.NumberedFragment;

public class AboutFragment extends NumberedFragment {


    @Override
    public void onResume() {
        POSITION=4;
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, null);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setPeopleToView(R.id.people1, "Android: Waza_Be", R.drawable.ic_p_wazabe, "Your Android apps creator <a href=\"mailto:christophe.versieux@gmail.com\">christophe.versieux@gmail.com</a>", "https://plus.google.com/108315424589085456181/posts");
        setPeopleToView(R.id.people2, "Web: Quentin Kaiser", R.drawable.ic_p_qkaiser, "Student in computer sciences @ UCL . Hacker and python addict", "http://www.quentinkaiser.be");
        setPeopleToView(R.id.people3, "Design: julien Winant", R.drawable.ic_p_harkor, "Je r&eacute;alise des interfaces interactives modernes et efficaces", "http://harkor.be/");
        setPeopleToView(R.id.people4, "iPhone: Lionel Schinckus", R.drawable.ic_p_lio, "iOS developer", "https://twitter.com/ValCapri");


    }

    private void setPeopleToView(int layout, String title, int img, String desc, final String link) {
        View view = this.getView().findViewById(layout);

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!link.contentEquals(""))
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(link)));
            }
        });

        ImageView iv = (ImageView) view.findViewById(R.id.iv);
        iv.setImageResource(img);

        TextView tvName = (TextView) view.findViewById(R.id.tName);
        tvName.setText(title);

        TextView tDesc = (TextView) view.findViewById(R.id.tDesc);
        tDesc.setText(Html.fromHtml(desc));
    }


}

