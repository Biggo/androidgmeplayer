package com.biggo.AndroidGMEPlayer.PlayerLibs;

public interface IPlayerLib {
	public String getLastError();	
    public boolean init(String filename, int track, int sampleRate);
    public long getTime();
    public short[] getSample(long size);
    public boolean cleanup();
    public long getSampleRate();
    public boolean startTrack(int track);
    public int getTrack();
    public int getChannel();
    public String[] getChannelNames();
    public boolean seek(long msec);	
	public boolean skip(long n);
	public boolean isTrackEnded();
	public void setFade( long start_msec, long length_msec);
	public void setIgnoreSilence( boolean disable);
	//public Track getTrackInfo();
	public void setTempo(double tempo);
	public void muteChannel(int index, boolean mute);
	public void muteChannels( int mask );
	public void setGain( double gain);	
	public void setEqualizer(double treble, long bass);	

}
