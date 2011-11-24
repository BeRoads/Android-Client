package com.profete162.WebcamWallonnes.adapter;

import com.profete162.WebcamWallonnes.SortMap.ImageDisplayer;

public interface ImageMapReceivedCallback
{
	// Called when an image is rendered
	public void onImageReceived(ImageDisplayer displayer);
}