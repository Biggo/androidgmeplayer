package com.biggo.AndroidGMEPlayer.PlayerLibs;

import com.biggo.AndroidGMEPlayer.Track;

public class SexypsfTrackInfo implements ITrackInfo {
	
	private String filename = "";
	private String path = "";
	private int type = -1;
	private Track thisTrack= null;
	
	public SexypsfTrackInfo(String path, String filename, int type)
	{
		this.path = path;
		this.filename = filename;
		this.type = type;
	}

	public Track getTrack() {
		thisTrack = new Track();
		thisTrack.setPath(path);
		thisTrack.setFilename(filename);
		thisTrack.setTrackNum(0);
		thisTrack.setTrackCount(1);
		thisTrack.setSong(filename);
		thisTrack.setGame(filename);
		thisTrack.setSystem("Playstation");
		thisTrack.setAuthor("");
		thisTrack.setCopyright("");
		thisTrack.setComment("");
		thisTrack.setDumper("");
		thisTrack.setType(type);
		thisTrack.setTrackLength(900000);
		thisTrack.setIntroLength(0);
		thisTrack.setLoopLength(0);
		return thisTrack;
	}

	public boolean isValidFile() {
		return true;
	}
	
	public boolean hasTracks()
	{
		return thisTrack == null;
	}
}
