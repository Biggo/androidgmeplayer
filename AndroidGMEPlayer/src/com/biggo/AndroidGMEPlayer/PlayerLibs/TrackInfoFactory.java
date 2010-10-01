package com.biggo.AndroidGMEPlayer.PlayerLibs;

import com.biggo.AndroidGMEPlayer.Track;

public class TrackInfoFactory {

	public static ITrackInfo getTrackInfo(String path, String filename)
	{		
		if(filename.endsWith(".nsf"))
		{
			return new GMETrackInfo(path, filename, Track.TYPE_NSF);
		}
		else if(filename.endsWith(".nsfe"))
		{
			return new GMETrackInfo(path, filename, Track.TYPE_NSFE);
		}
		else if(filename.endsWith(".spc"))
			return new GMETrackInfo(path, filename, Track.TYPE_SPC);
		else if(filename.endsWith(".gbs"))
		{
			return new GMETrackInfo(path, filename, Track.TYPE_GBS);
		}
		else if(filename.endsWith("vgz")|| filename.endsWith(".vgm"))
			return new GMETrackInfo(path, filename, Track.TYPE_VGM);
		else if(filename.endsWith(".ay"))
		{
			return new GMETrackInfo(path, filename, Track.TYPE_AY);
		}
		else if(filename.endsWith(".hes"))
		{
			return new GMETrackInfo(path, filename, Track.TYPE_HES);
		}
		else if(filename.endsWith(".kss"))
		{
			return new GMETrackInfo(path, filename, Track.TYPE_KSS);
		}
		else if(filename.endsWith(".sap"))
		{
			return new GMETrackInfo(path, filename, Track.TYPE_SAP);
		}
		else
			return null;
		

	}
}
