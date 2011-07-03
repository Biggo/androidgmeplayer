package com.biggo.AndroidGMEPlayer.PlayerLibs;

import com.biggo.AndroidGMEPlayer.Track;

public interface ITrackInfo {
	
	public Track getTrack();
	public boolean isValidFile();
	public boolean hasTracks();
}
