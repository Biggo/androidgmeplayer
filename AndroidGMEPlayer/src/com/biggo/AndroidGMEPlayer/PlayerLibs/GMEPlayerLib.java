package com.biggo.AndroidGMEPlayer.PlayerLibs;

public class GMEPlayerLib implements IPlayerLib {
	static {
		System.loadLibrary("gme_player");
	}
	
		public native String getLastError();	
	    public native boolean init(String filename, int track, int sampleRate);
	    public native long getTime();
	    public native short[] getSample(long size);
	    public native boolean cleanup();
	    public native long getSampleRate();
	    public native boolean startTrack(int track);
	    public native int getTrack();
	    public native int getChannel();
	    public native String[] getChannelNames();
	    public native boolean seek(long msec);	
		public native boolean skip(long n);
		public native boolean isTrackEnded();
		public native void setFade( long start_msec, long length_msec);
		public native void setIgnoreSilence( boolean disable);
//		private native String[] getCurrentTrackInfo();
//		public Track getTrackInfo()
//		{
//			String[] fileInfo = getCurrentTrackInfo();
//			Track newTrack = new Track();
//			newTrack
//			Track newTrack = new Track(path,
//					filename, 
//					track, 
//					count, 
//					fileInfo[4], 
//					fileInfo[5], 
//					fileInfo[6], 
//					fileInfo[7], 
//					fileInfo[8], 
//					fileInfo[9], 
//					fileInfo[10], 
//					type);
//			return newTrack;
//		}
		public native void setTempo(double tempo);
		public native void muteChannel(int index, boolean mute);
		public native void muteChannels( int mask );
		public native void setGain( double gain);	
		public native void setEqualizer(double treble, long bass);		


	}