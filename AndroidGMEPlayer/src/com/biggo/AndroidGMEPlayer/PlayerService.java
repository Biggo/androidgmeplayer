package com.biggo.AndroidGMEPlayer;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;


public class PlayerService extends Service {
	
	private boolean isPlaying = false;
	private boolean isLoaded = false;
	
	private final String PLAYER_SETTINGS = "PLAYER_SETTINGS";
	private final String PLAYER_SETTINGS_SAMPLE_RATE = "PLAYER_SETTINGS_SAMPLE_RATE";
	private final String PLAYER_SETTINGS_BUFFER_SIZE = "PLAYER_SETTINGS_BUFFER_SIZE";
	private final String PLAYER_SETTINGS_FADE_LENGTH = "PLAYER_SETTINGS_FADE_LENGTH";
	private final String PLAYER_SETTINGS_TEMPO = "PLAYER_SETTINGS_TEMPO";
	
	public final static String ACTION_NEXT_TRACK = "AndroidGMEPlayer_ACTION_NEXT_TRACK";	
	public final static String ACTION_PREVIOUS_TRACK = "AndroidGMEPlayer_ACTION_PREVIOUS_TRACK";
	public final static String ACTION_CHANGE_TRACK = "AndroidGMEPlayer_ACTION_CHANGE_TRACK";
	
	private float buffer; //ms
	private int sampleRate; //hz
	private int fadeLength; //ms
	private double tempo; //1.0 is normal
	
	private int trackLength = 0;
	private int trackTime = 0;
	private int timeOffset = 0;
		
	private Thread t;
	private GMEPlayerLib gme;
	
	private AudioTrack audio;
	
	final Handler toastHandler = new Handler();


    public class LocalBinder extends Binder {
    	PlayerService getService() {
            return PlayerService.this;
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
    	super.onStart(intent, startId);
    	
    	String action = intent.getAction();
    	if(action.compareTo(PlayerService.ACTION_NEXT_TRACK) == 0)
    	{
    		this.nextTrack();
    	}
    	else if(action.compareTo(PlayerService.ACTION_PREVIOUS_TRACK) == 0)
    	{
    		this.previousTrack();
    	}	
    	else if(action.compareTo(PlayerService.ACTION_CHANGE_TRACK) == 0)
    	{
    		this.changeTrack(intent.getIntExtra("TrackNumber", 0));
    	}	
    }

    @Override
    public void onCreate() {
    	super.onCreate();
    	
    	SharedPreferences settings = this.getSharedPreferences(PLAYER_SETTINGS, 0);
    	setBuffer(settings.getFloat(PLAYER_SETTINGS_BUFFER_SIZE, (float) 500.0));
    	setSampleRate(settings.getInt(PLAYER_SETTINGS_SAMPLE_RATE, 44100));
    	setFadeLength(settings.getInt(PLAYER_SETTINGS_FADE_LENGTH, 1500));
    	setTempo((double)settings.getFloat(PLAYER_SETTINGS_TEMPO, (float) 1.0));    	
    }
    
	@Override
    public void onDestroy() {
		super.onDestroy();
		stop();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    private final IBinder mBinder = new LocalBinder();

    private void init()
	{
		if(gme != null)
		{
			gme.cleanup();
			gme = null;
		}
		gme = new GMEPlayerLib();			

		if(audio != null)
		{
	        audio.flush();
	        audio.release();
		}
		int bufferSize = (int)(sampleRate * (buffer / 1000.0)) * 2;	

		audio = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, AudioFormat.CHANNEL_CONFIGURATION_STEREO, AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM);		
		
		Track track = Library.getCurrentPlaylist().getCurrentTrack();
		if(track != null)
		{
		    isLoaded = gme.init(track.getPath(), track.getTrackNum(), sampleRate);
		    if(isLoaded)
		    {		
		    	String[] trackinfo = gme.getTrackInfo();
	
				int length = Integer.parseInt(trackinfo[1]);
				int intro = Integer.parseInt(trackinfo[2]);
				int loops = Integer.parseInt(trackinfo[3]);
				
				if(length > 0)
					trackLength = length;
				else if(loops > 0)
					trackLength = intro + loops *2;
				else
					trackLength = 150000;
				
				trackTime += fadeLength;
				
				trackTime = 0;
				timeOffset = 0;
				gme.setFade(trackLength - fadeLength, fadeLength);
				gme.setTempo(tempo);
		    }
		    else
		    {
				String error = gme.getLastError();
				if(error != null)
				{
					showError(error);
				}	    	
		    }
		}
		else
		{
			showError("No Track Loaded");
		}
	}
    
    private void updateTrackInfo()
    {
		Intent intent = new Intent();
		intent.setAction(AndroidGMEPlayer.ACTION_UPDATE_TRACK_INFO);
		this.sendBroadcast(intent);
    }
	
	public void play()
	{	
		if (!isLoaded)
        {
			init();
        }
		if(!isPlaying && isLoaded)
		{
			isPlaying = true;
			int playState = audio.getPlayState();
			if(playState == AudioTrack.PLAYSTATE_PAUSED || playState == AudioTrack.PLAYSTATE_STOPPED)
			{
				audio.play();
								
				t = new Thread() {
					@Override public void run() {
						playLoop();
					}
				  };
				t.setPriority(Thread.MAX_PRIORITY);
				t.start();

				updateTrackInfo();
			}
			else
			{
				showError("Failed to start playback.");
			}
		}	
	}
	
	private void playLoop()
	{
		//Looper.prepare();
		long size = (AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_CONFIGURATION_STEREO, AudioFormat.ENCODING_PCM_16BIT));
		
        while (isPlaying)
    	{    		        		
    		short[] buf = gme.getSample(size);
    		String err = gme.getLastError();
    		if(err != null)
    		{
    			showError("Failed to get sample: " + err);
    			stop();
    		}
    		else
    		{
	    		int success = audio.write(buf, 0, (int)size);
	    		if(success < 0)
	    		{
	    			String error = "Failed to write sample to AudioTrack.";
	    			if(success == AudioTrack.ERROR)
	    				error += " A generic error occured.";
	    			else if(success == AudioTrack.ERROR_BAD_VALUE)
	    				error += " Invalid data was provided.";
	    			else if(success == AudioTrack.ERROR_INVALID_OPERATION)
	    				error += " Invalid operation.";	    			

	    			showError(error);
	    			stop();
	    		}
	    		/*else if(success != size)
	    		{
	    			showError("Not all frames written: " + success + " of " + size);
	    		}*/
	    		if(gme.isTrackEnded())
	    		{
	    			isPlaying = false;
	    		}
    		}
    	}
		if(gme.isTrackEnded())
		{
	    	Library.getCurrentPlaylist().getNextTrack();
			gme.cleanup();
			audio.stop();
			audio.flush();
			isLoaded = false;
    		play();			
		}
	}
	
	public void stop()
	{
		if(isLoaded)
		{
			if(isPlaying)
			{
				pause();			
			}
			gme.cleanup();
			audio.stop();
			audio.flush();
			isLoaded = false;
			trackTime = 0;
		}
	}
	
	public void pause()
	{
		if(isLoaded)
		{
			isPlaying = false;
			audio.pause();
			if(t != null)
			{
				if(t.isAlive())
				{
					try {
						t.join();
					} catch (InterruptedException e) {
						t.stop();
					}
				}
			}
		}
	}
	
    public void changeTrack(int idx)
    {
    	if(Library.getCurrentPlaylist().setCurrentTrack(idx))
    	{
    		stop();
    		play();
    	}
    }
    
    public void nextTrack()
    {
    	boolean play = isPlaying;
    	stop();
    	Library.getCurrentPlaylist().getNextTrack();
    	init();
    	if(play)
    		play();
    	updateTrackInfo();
    }
    
    public void previousTrack()
    {
    	boolean play = isPlaying;
    	Library.getCurrentPlaylist().getPreviousTrack();
    	stop();
    	init();
    	if(play)
    		play();
    	updateTrackInfo();
    }
		
	public int getTime()
	{
		if(isLoaded)
		{
			if(tempo == 1.0)
			{
				trackTime = timeOffset + ((audio.getPlaybackHeadPosition() / sampleRate) * 1000);
				return trackTime;
			}
			else
			{
				trackTime = (int)gme.getTime();
				return trackTime;
			}
		}
		else
			return 0;
	}
	
	public int getTrackLength()
	{
		if(isLoaded)
			return trackLength;
		else
			return 0;
	}
	
	public void seekTo(int pos)
	{
		if(isLoaded)
		{
			boolean playTrack = isPlaying;
			if(isPlaying)
			{
				pause();			
			}
			audio.stop();
			audio.flush();
			
			if(pos < 0)
				pos = 0;
			else if (pos > trackLength)
				pos = trackLength;
			
			timeOffset = pos;
			if(pos > trackTime)
			{
				int n = pos - trackTime;
				int sec = n / 1000;
				n -= sec * 1000;
				n = (sec * sampleRate + n * sampleRate / 1000) * 2;
				gme.skip(n);
			}
			else
			{				
				gme.seek(pos);
				gme.setFade(trackLength - fadeLength, fadeLength);				
			}
			if (playTrack)
				play();
		}
	}
	
	public boolean getIsPlaying()
	{
		return isPlaying;
	}
    
	private void showError(String error)
	{
		final String err = error;
		
		final Runnable toastRunner = new Runnable() { public void run() { Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG).show();}};
		
		 Thread toast = new Thread() { public void run() { toastHandler.post(toastRunner); } };

		 toast.start(); 
	}
	
    private void setSampleRate(int rate) 
    {
    	sampleRate = rate;
    	if(isLoaded)
    	{
			int time = trackTime;
			boolean play = isPlaying;
			stop();
			init();
    		if(play)
    		{
    			seekTo(time);
    			play();
    		}     		
    	}		
	}

	private void setBuffer(float buf)
    {
    	buffer = buf;
    	if(isLoaded)
    	{
			int time = trackTime;
			boolean play = isPlaying;
			stop();
			init();
    		if(play)
    		{
    			seekTo(time);
    			play();
    		}   		
    	}
	}
    
	private void setFadeLength(int fade)
    {
    	fadeLength = fade;
    	if(isLoaded)
    	{
			int time = trackTime;
			boolean play = isPlaying;
			stop();
			init();
    		if(play)
    		{
    			seekTo(time);
    			play();
    		} 		
    	}		
	}
	
	private void setTempo(double tempo)
	{
    	this.tempo = tempo;
    	if(isLoaded)
    	{
			gme.setTempo(tempo);	
    	}	
		
	}
}