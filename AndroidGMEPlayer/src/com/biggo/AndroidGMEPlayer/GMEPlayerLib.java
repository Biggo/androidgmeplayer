package com.biggo.AndroidGMEPlayer;

class GMEPlayerLib {
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
		public native String[] getTrackInfo();
		public native void setTempo(double tempo);
		public native void muteChannel(int index, boolean mute);
		public native void muteChannels( int mask );
		public native void setGain( double gain);	
		public native void setEqualizer(double treble, long bass);		
		public native String[] getFileInfo(String filename, int track);
	}