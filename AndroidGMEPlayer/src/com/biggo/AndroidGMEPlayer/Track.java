package com.biggo.AndroidGMEPlayer;

public class Track {
	
	public static final int TYPE_AY = 0; 
	public static final int TYPE_GBS = 1; 
	public static final int TYPE_GYM = 2; 
	public static final int TYPE_HES = 3; 
	public static final int TYPE_KSS = 4; 
	public static final int TYPE_MP3 = 5; 
	public static final int TYPE_NSF = 6; 
	public static final int TYPE_SAP = 7; 
	public static final int TYPE_SPC = 8; 
	public static final int TYPE_UNKNOWN = 9; 
	public static final int TYPE_VGM = 10; 
	public static final int TYPE_NSFE = 11; 
	
	private String path = "";
	private String filename = "";
	private int trackNum = -1;
	private int trackCount = -1;
	private String song = "";
	private String game = "";
	private String system = "";
	private String author = "";
	private String copyright = "";
	private String comment = "";
	private String dumper = "";
	private int type = -1;
	private int rowID = -1;	
	private int tracklength = -1;
	private int introlength = -1;
	private int looplength = -1;

//	public Track(String path, String filename, int trackNum, int trackCount, String song, String game, String system, String author, String copyright, String comment, String dumper, int type)
//	{
//		this.path = path;
//		this.filename = filename;
//		this.trackNum = trackNum;
//		this.trackCount = trackCount;
//		this.song = song;
//		this.game = game;
//		this.system = system;
//		this.author = author;
//		this.copyright = copyright;
//		this.comment = comment;
//		this.dumper = dumper;		
//		this.type = type;
//	}
//	
//	public Track()
//	{
//		this("", "", 0, 0, "", "", "", "", "", "", "", Track.TYPE_UNKNOWN);
//	}
	
	public String getPath()
	{
		return this.path;
	}
	
	public void setPath(String path)
	{
		this.path = path;
	}
	
	public String getFilename()
	{
		return this.filename;
	}
	
	public void setFilename(String filename)
	{
		this.filename = filename;
	}
	
	public int getTrackNum()
	{
		return this.trackNum;
	}
	
	public void setTrackNum(int trackNum)
	{
		this.trackNum = trackNum;
	}
	
	public int getTrackCount()
	{
		return this.trackCount;
	}
	
	public void setTrackCount(int trackCount)
	{
		this.trackCount = trackCount;
	}
	
	public String getSong()
	{
		return this.song;
	}
	
	public void setSong(String song)
	{
		this.song = song;
	}
	
	public String getGame()
	{
		return this.game;
	}
	
	public void setGame(String game)
	{
		this.game = game;
	}
	
	public String getSystem()
	{
		return this.system;
	}
	
	public void setSystem(String system)
	{
		this.system = system;
	}
	
	public String getAuthor()
	{
		return this.author;
	}
	
	public void setAuthor(String author)
	{
		this.author = author;
	}
	
	public String getCopyright()
	{
		return this.copyright;
	}
	
	public void setCopyright(String copyright)
	{
		this.copyright = copyright;
	}
	
	public String getComment()
	{
		return this.comment;
	}
	
	public void setComment(String comment)
	{
		this.comment = comment;
	}
	
	public String getDumper()
	{
		return this.dumper;
	}
	
	public void setDumper(String dumper)
	{
		this.dumper = dumper;
	}
	
	public int getType()
	{
		return this.type;
	}
	
	public void setType(int type)
	{
		this.type = type;
	}
	
	public String toString()
	{
		if( !(game.equalsIgnoreCase("") || song.equalsIgnoreCase("")) )
		{
			return game + " - " + song;
		}
		else
		{
			return  this.filename + " Track: " + this.trackNum + "/" + this.trackCount;
		}
	}
	
	public int getRowID() {
		return rowID;
	}

	public void setRowID(int rowID) {
		this.rowID = rowID;
	}

	public int getTrackLength() {
		return tracklength;
	}

	public void setTrackLength(int tracklength) {
		this.tracklength = tracklength;
	}

	public int getIntroLength() {
		return introlength;
	}

	public void setIntroLength(int introlength) {
		this.introlength = introlength;
	}

	public int getLoopLength() {
		return looplength;
	}

	public void setLoopLength(int looplength) {
		this.looplength = looplength;
	}
	
	

}
