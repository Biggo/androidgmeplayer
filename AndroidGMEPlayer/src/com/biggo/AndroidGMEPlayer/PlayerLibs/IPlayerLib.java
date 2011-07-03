package com.biggo.AndroidGMEPlayer.PlayerLibs;

import android.media.AudioTrack;

public interface IPlayerLib {
		
	public int play();
	public boolean stop();
	public boolean seek(int position);	
	public String getLastError();	
    public boolean init(String filename, int track);
	public boolean isTrackEnded();	
	public void setAudio(AudioTrack audio);
	public void setFade(int fadeStart, int length);
    
    /*public long getTime();
    public short[] getSampleShort(long size);
    public byte[] getSampleByte(long size);
    public boolean cleanup();
    public long getSampleRate();
    public boolean startTrack(int track);
    public int getTrack();
    public int getChannel();
    public String[] getChannelNames();
    public boolean seek(long msec);	
	public boolean seekForward(long msec);
	public boolean isTrackEnded();
	public void setFade( long start_msec, long length_msec);
	public void setIgnoreSilence( boolean disable);
	//public Track getTrackInfo();
	public void setTempo(double tempo);
	public void muteChannel(int index, boolean mute);
	public void muteChannels( int mask );
	public void setGain( double gain);	
	public void setEqualizer(double treble, long bass);	
	
	public boolean isBytes();*/
	

}
