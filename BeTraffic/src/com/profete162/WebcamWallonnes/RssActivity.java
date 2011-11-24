package com.profete162.WebcamWallonnes;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.profete162.WebcamWallonnes.Rss.DownloadRssTask;
import com.profete162.WebcamWallonnes.Rss.RSSFeed;

public class RssActivity extends ListActivity {

	protected static final String TAG = "ActivityRss";
	private RSSFeed myRssFeed = null;	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rss);		
		new DownloadRssTask(this).execute();

	}


	public RSSFeed getRssFeed(){
		return myRssFeed;
	}
	
	public void setRssFeed(RSSFeed rssFeed){
		this.myRssFeed=rssFeed;		
	}


	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
		alertbox.setTitle(myRssFeed.getItem(position).getTitle());
		alertbox.setMessage(myRssFeed.getItem(position).getDescription().replace(myRssFeed.getItem(position).getTitle(), ""));			
		alertbox.setNeutralButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
					}
				});
		alertbox.show();
	}

}