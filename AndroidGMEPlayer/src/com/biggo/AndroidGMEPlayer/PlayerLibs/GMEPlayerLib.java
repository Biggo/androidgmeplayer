package com.biggo.AndroidGMEPlayer.PlayerLibs;

import android.media.AudioTrack;

public class GMEPlayerLib implements IPlayerLib {
	static {
		System.loadLibrary("gme_player");
	}
	public boolean init(String filename, int track) {
		return initFile(filename, track, 44100);
	}
	
	private AudioTrack audio;
	public void setAudio(AudioTrack audio) {
		this.audio = audio;		
	}	

	public int play() {
		int result = -1;
		if (audio != null)
		{
			int size = 1024;
			short[] buf = getSample(size);
			if(buf.length > 0)
			{
				result = audio.write(buf, 0, buf.length);
			}
			
		}
		return result;
	}

	public boolean seek(int position) {
		boolean success = false;
		int time = (int) getTime();
		if(position > time)
		{
			success = seekForward(position - time);
		}
		else
		{				
			long msec = position;
			long sec = msec / 1000;
			msec -= sec * 1000;
			long bytes = (sec * 44100 + msec * 44100 / 1000) * 2;
			success = skip(bytes);			
		}
		return success;
	}

	public void setFade(int fadeStart, int length) {
		setFade((long) fadeStart,  (long)length);		
	}

	public boolean stop() {
		return cleanup();
	}
	
	public native String getLastError();
	
	
    public native boolean initFile(String filename, int track, int sampleRate);
    public native long getTime();
    private native short[] getSample(long size);
    public native boolean cleanup();
    public native long getSampleRate();
    public native boolean startTrack(int track);
    public native int getTrack();
    public native int getChannel();
    public native String[] getChannelNames();
    public native boolean seekTo(long msec);
    public boolean seekForward(long msec)
    {
		long sec = msec / 1000;
		msec -= sec * 1000;
		long bytes = (sec * 44100 + msec * 44100 / 1000) * 2;
    	return skip(bytes);
    }
	private native boolean skip(long n);
	public native boolean isTrackEnded();
	private native void setFade( long start_msec, long length_msec);
	public native void setIgnoreSilence( boolean disable);
	public native void setTempo(double tempo);
	public native void muteChannel(int index, boolean mute);
	public native void muteChannels( int mask );
	public native void setGain( double gain);	
	public native void setEqualizer(double treble, long bass);

	


}