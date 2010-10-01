package com.biggo.AndroidGMEPlayer.PlayerLibs;

import com.biggo.AndroidGMEPlayer.Track;

public class GMETrackInfo implements ITrackInfo {
	static {
		System.loadLibrary("gme_trackinfo");
	}
	
	private String filename = "";
	private String path = "";
	private int type = -1;
	private int track = 0;
	private int count = -1;
	
	public GMETrackInfo(String path, String filename, int type)
	{
		this.path = path;
		this.filename = filename;
		this.type = type;
		String[] fileInfo = getFileInfo(path, track);		
		count = (fileInfo != null)? Integer.parseInt(fileInfo[0]) : -1;
	}

	public Track getTrack() {
		if(count != -1 && track < count)
		{
			String[] fileInfo = getFileInfo(path, track);			
			Track newTrack = new Track();
			newTrack.setPath(path);
			newTrack.setFilename(filename);
			newTrack.setTrackNum(track);
			newTrack.setTrackCount(count);
			newTrack.setSong(fileInfo[4]);
			newTrack.setGame(fileInfo[5]);
			newTrack.setSystem(fileInfo[6]);
			newTrack.setAuthor(fileInfo[7]);
			newTrack.setCopyright(fileInfo[8]);
			newTrack.setComment(fileInfo[9]);
			newTrack.setDumper(fileInfo[10]);
			newTrack.setType(type);
			newTrack.setTrackLength(Integer.parseInt(fileInfo[1]));
			newTrack.setIntroLength(Integer.parseInt(fileInfo[2]));
			newTrack.setLoopLength(Integer.parseInt(fileInfo[3]));
			track++;
			return newTrack;
		}
		else
			return null;
	}
	
	private native String[] getFileInfo(String filename, int track);
	public native String getLastError();
	
	public boolean isValidFile()
	{
		return count > -1;
	}
}
