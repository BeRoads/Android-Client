package com.profete162.WebcamWallonnes.Rss;

import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.profete162.WebcamWallonnes.R;
import com.profete162.WebcamWallonnes.RssActivity;
import com.profete162.WebcamWallonnes.adapter.RSSAdapter;

public class DownloadRssTask extends AsyncTask<URL, Integer, Long> {

	private RSSFeed myRssFeed;
	private Context context;
	private LayoutInflater layoutInflater;
	private RssActivity trafAct;

	public DownloadRssTask(RssActivity trafAct) {
		this.trafAct = trafAct;
		context = trafAct;
		myRssFeed = trafAct.getRssFeed();
		layoutInflater = trafAct.getLayoutInflater();

	}

	protected Long doInBackground(URL... params) {
		updateData();
		return null;
	}

	protected void onPostExecute(Long result) {
		TextView feedEmpty = (TextView) trafAct
		.findViewById(R.id.nocon);
		if (myRssFeed != null) {
			if (myRssFeed.getList().size() > 0) {
				RSSAdapter adapter = new RSSAdapter(context,
						R.layout.row_rss, myRssFeed.getList(), layoutInflater,
						myRssFeed);
				trafAct.setListAdapter(adapter);
				trafAct.setRssFeed(myRssFeed);
			} 
		} else {
			feedEmpty.setVisibility(View.VISIBLE);
			feedEmpty.setText(trafAct.getString(R.string.txt_connection));
		}
	}

	private void updateData() {

		try {
			/*
			 * getting rss feed from the railtime.be website
			 */
			URL rssUrl = new URL(
					"http://trafiroutes.wallonie.be/trafiroutes/Evenements_FR.rss");

			RSSDocument rssDoc = new RSSDocument(rssUrl);
			myRssFeed = rssDoc.getRSSFeed();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
