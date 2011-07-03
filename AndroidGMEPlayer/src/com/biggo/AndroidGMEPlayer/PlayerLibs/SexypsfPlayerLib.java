package com.biggo.AndroidGMEPlayer.PlayerLibs;

import android.media.AudioTrack;

public class SexypsfPlayerLib implements IPlayerLib{
	
	public boolean stop() {
		sexypsfstop();
		return true;
	}

	private boolean trackEnded = false;
	
	public String getLastError() {
		return null;
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
			byte[] buf = new byte[size];
			sexypsfpause(false);
			int length = sexypsfputaudiodata(buf, size);
			sexypsfpause(true);
			trackEnded = length != size;
			if(length > 0)
			{
				result = audio.write(buf, 0, buf.length);
			}
			
		}
		return result;
	}

	public boolean init(String filename, int track) {
		if( sexypsfopen(filename))
		{
			sexypsfplay();
			return true;
		}
		else
			return false;
		
	}

	public boolean isTrackEnded() {
		return trackEnded;
	}

	public boolean seek(int msec) {
		sexypsfseek(msec, 0);
		return true;
	}

	private boolean seekForward(long msec) {
		sexypsfseek((int) msec, 1);
		return true;
	}

	   /** this is used to load the 'sexypsf' library on application
     * startup.
     */
   static {
       System.loadLibrary("sexypsf");
   }
   
   /** The native function implemented by sexypsf.
    * It's used to open a psf file.
    */
   public static native boolean sexypsfopen(String filename);
   
   /** Native function to play a opened psf file */
   public static native void sexypsfplay();
   
   /** Native function to pause the playback */
   public static native void sexypsfpause(boolean pause);

   /** Native function to seek the playback */
   public static native void sexypsfseek(int seek, int mode);

   /** Native function to stop a the playback */
   public static native void sexypsfstop();
   
   /** Native function to get the audio data from sexypsf */
   public static native int sexypsfputaudiodata(byte[] arr, int size);

public void setFade(int fadeStart, int length) {	
}

}
