package com.biggo.AndroidGMEPlayer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;


public class PlayerService extends Service {
	

	//SharedPreference keys	
	public static final String PLAYER_SETTINGS = "PLAYER_SETTINGS";
	public static final String PLAYER_SETTINGS_SAMPLE_RATE = "PLAYER_SETTINGS_SAMPLE_RATE";
	public static final String PLAYER_SETTINGS_BUFFER_SIZE = "PLAYER_SETTINGS_BUFFER_SIZE";
	public static final String PLAYER_SETTINGS_FADE_LENGTH = "PLAYER_SETTINGS_FADE_LENGTH";
	public static final String PLAYER_SETTINGS_TEMPO = "PLAYER_SETTINGS_TEMPO";
	
	//Intent keys
	public final static String ACTION_NEXT_TRACK = "AndroidGMEPlayer_ACTION_NEXT_TRACK";	
	public final static String ACTION_PREVIOUS_TRACK = "AndroidGMEPlayer_ACTION_PREVIOUS_TRACK";
	public final static String ACTION_CHANGE_TRACK = "AndroidGMEPlayer_ACTION_CHANGE_TRACK";
	
	//True while player loop is executing
	private boolean isPlaying = false;
	//True when a file is loaded
	private boolean isLoaded = false;
	
	//internal variables for playback settings
	private float buffer; //ms
	private int bufferSize;
	private int sampleRate; //hz
	private int fadeLength; //ms
	private double tempo; //1.0 is normal
		
	//internal variables for playback info
	private int trackLength = 0;
	private int trackTime = 0;
	private int timeOffset = 0;
			
	//the main playback thread
	private Thread t;
	//the wrapper for GME libraries
	private GMEPlayerLib gme;
		
	//AudioTrack for playback
	private AudioTrack audio;
		
	//Handler for toast messages
	final Handler toastHandler = new Handler();
			
	//ID variable for notification
	private static final int NOTIFICATION_CURRENT_TRACK = 1;
	    
	//true when listening for phone state to pause when phone rings
	private boolean phoneStateListening = false;
	
	//binder for service
	private final IBinder mBinder = new LocalBinder();
	
	//methods for service foreground control backwards/forwards compatibility   
	private Method mDebug_startForeground;
	private Method mDebug_stopForeground;
    
   
	//intent handling for playback control
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
    	initPreferences();
    	initPhoneStateListener();
    }    
    
    //initialize preferences and phone state listener on create
    @Override
    public void onCreate() {
    	super.onCreate();
    	
    	initPreferences(); 	
    	initPhoneStateListener();
    }
    
	@Override
    public void onDestroy() {
		super.onDestroy();
		stop();
    }
	

    public class LocalBinder extends Binder {
    	PlayerService getService() {
            return PlayerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }
    
    private void initPreferences()
    {    	
    	SharedPreferences settings = this.getSharedPreferences(PlayerService.PLAYER_SETTINGS, 0);
    	settings.registerOnSharedPreferenceChangeListener(settingsChangedListener);
    	//setSampleRate(settings.getString(PlayerService.PLAYER_SETTINGS_SAMPLE_RATE, "44100"));
    	setSampleRate("44100");//AudioTrack doesn't behave very well when trying to change buffer size
    	//setBuffer(settings.getFloat(PlayerService.PLAYER_SETTINGS_BUFFER_SIZE, (float) 500.0));
    	setBuffer(500.0f);//AudioTrack doesn't behave very well when trying to change buffer size
    	setFadeLength(settings.getFloat(PlayerService.PLAYER_SETTINGS_FADE_LENGTH, 1.5f));
    	setTempo(settings.getFloat(PlayerService.PLAYER_SETTINGS_TEMPO, (float) 1.0));
    	settings.registerOnSharedPreferenceChangeListener(settingsChangedListener);
    }
	
	private OnSharedPreferenceChangeListener settingsChangedListener = new OnSharedPreferenceChangeListener() {

		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			
			/*if(key.compareTo(PlayerService.PLAYER_SETTINGS_SAMPLE_RATE) == 0)
			{
		    	setSampleRate(sharedPreferences.getString(key, "44100"));
				
			}*/
			/*else if(key.compareTo(PlayerService.PLAYER_SETTINGS_BUFFER_SIZE) == 0)
			{
		    	setBuffer(sharedPreferences.getFloat(key, (float) 500.0));
				
			}*/
			if(key.compareTo(PlayerService.PLAYER_SETTINGS_FADE_LENGTH) == 0)
			{
		    	setFadeLength(sharedPreferences.getFloat(key, 1.5f));
				
			}
			else if(key.compareTo(PlayerService.PLAYER_SETTINGS_TEMPO) == 0)
			{
		    	setTempo(sharedPreferences.getFloat(key, (float) 1.0));
				
			}
			
		}

	};
    
    
    private void initPhoneStateListener()
    {
    	if(!phoneStateListening)
    	{
        	TelephonyManager tlm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        	tlm.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        	phoneStateListening = true;
    	}
    }
    
    private PhoneStateListener phoneStateListener = new PhoneStateListener() {
    	@Override
		public void  onCallStateChanged  (int state, String incomingNumber)
    	{
    		if(state == TelephonyManager.CALL_STATE_OFFHOOK || state == TelephonyManager.CALL_STATE_RINGING)
    		{
    			pause();
    		}    		
    	}
    };
    
    private void initCompatibility() {
 	   if(mDebug_startForeground == null)
 	   {
 	       try {
 	           mDebug_startForeground = Service.class.getMethod(
 	                   "startForeground", new Class[] { int.class, Notification.class } );
 	           /* success, this is a newer device */
 	       } catch (NoSuchMethodException nsme) {
 	           /* failure, must be older device use the no-op equivalent method*/    	    	   
 	       }
 	   }
 	   if(mDebug_stopForeground == null)
 	   {
 	       try {
 	    	   mDebug_stopForeground = Service.class.getMethod(
 	                   "stopForeground", new Class[] { boolean.class} );
 	           /* success, this is a newer device */
 	       } catch (NoSuchMethodException nsme) {
 	           /* failure, must be older device use the no-op equivalent method*/    	    	   
 	       }
 	   }
    }
    
    private void startForegroundWrapper(int id, Notification notification) throws IOException
    {
        try {
     	   mDebug_startForeground.invoke(this, id, notification);
        } catch (InvocationTargetException ite) {
            /* unpack original exception when possible */
            Throwable cause = ite.getCause();
            if (cause instanceof IOException) {
                throw (IOException) cause;
            } else if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else if (cause instanceof Error) {
                throw (Error) cause;
            } else {
                /* unexpected checked exception; wrap and re-throw */
                throw new RuntimeException(ite);
            }
        } catch (IllegalAccessException ie) {
        }
    }
    
    private void stopForegroundWrapper(boolean removeNotification) throws IOException
    {
        try {
     	   mDebug_stopForeground.invoke(this, removeNotification);
        } catch (InvocationTargetException ite) {
            /* unpack original exception when possible */
            Throwable cause = ite.getCause();
            if (cause instanceof IOException) {
                throw (IOException) cause;
            } else if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else if (cause instanceof Error) {
                throw (Error) cause;
            } else {
                /* unexpected checked exception; wrap and re-throw */
                throw new RuntimeException(ite);
            }
        } catch (IllegalAccessException ie) {
        }
    }
    
    private void enableNotification(String ticker, String title, String text)
    {
 	   int icon = android.R.drawable.ic_media_play;
 	   long when = System.currentTimeMillis();
 	   
 	   Notification mNotification = new Notification(icon, ticker, when);
    
 	   Context context = getApplicationContext();
 	   Intent notificationIntent = new Intent(this, AndroidGMETabs.class);
 	   PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
 	   mNotification.setLatestEventInfo(context, title, text, contentIntent);	
 	   
 	   initCompatibility();
 	   
 	   if (mDebug_startForeground != null) {
            /* feature is supported */
            try {
         	   startForegroundWrapper(PlayerService.NOTIFICATION_CURRENT_TRACK , mNotification);
            } catch (IOException ie) {
                System.err.println("dump failed!");
            }
        } else {
            /* feature not supported, use the setForeground instead */
     	   String ns = Context.NOTIFICATION_SERVICE;
     	   NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
     	   mNotificationManager.notify(PlayerService.NOTIFICATION_CURRENT_TRACK , mNotification);
           setForeground(true);
        }
    }
    
    private void disableNotification()
    {		   
 	   initCompatibility();
 	   
 	   if (mDebug_stopForeground != null) {
            /* feature is supported */
            try {
         	   stopForegroundWrapper(true);
            } catch (IOException ie) {
                System.err.println("dump failed!");
            }
        } else {
            /* feature not supported, use the setForeground instead */
     	   String ns = Context.NOTIFICATION_SERVICE;
     	   NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
     	   mNotificationManager.cancel(PlayerService.NOTIFICATION_CURRENT_TRACK);
           setForeground(false);
        }
 	   
    }

    private void init()
	{
    	Playlist p = Library.getCurrentPlaylist();
    	if(p != null)
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
			bufferSize = (int)(sampleRate * (buffer / 1000.0)) * 2;			
			int size = (AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_CONFIGURATION_STEREO, AudioFormat.ENCODING_PCM_16BIT));
			bufferSize = Math.max(size, bufferSize);
			audio = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, AudioFormat.CHANNEL_CONFIGURATION_STEREO, AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM);		
			
			Track track = p.getCurrentTrack();
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
					
					trackLength += fadeLength;
					
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
		else
		{
			showError("No Playlist Loaded");
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
				Track track = Library.getCurrentPlaylist().getCurrentTrack();
				String text = track.toString();
				String ticker = "Now playing: " + text;
				enableNotification(ticker, track.getSystem(), text);
			}
			else
			{
				showError("Failed to start playback.");
			}
		}	
	}
	
	private void playLoop()
	{				
		boolean trackEnded = false;
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
	    		trackEnded = gme.isTrackEnded();
	    		if(trackEnded)
	    		{
	    			isPlaying = false;
	    		}
    		}
    	}
		if(trackEnded)
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
			disableNotification();
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
    	if(Library.getCurrentPlaylist() != null)
    	{
	    	boolean play = isPlaying;
	    	stop();
	    	Library.getCurrentPlaylist().getNextTrack();
	    	init();
	    	if(play)
	    		play();
	    	updateTrackInfo();
    	}
    }
    
    public void previousTrack()
    {
    	if(Library.getCurrentPlaylist() != null)
    	{	    	
	    	boolean play = isPlaying;
	    	Library.getCurrentPlaylist().getPreviousTrack();
	    	stop();
	    	init();
	    	if(play)
	    		play();
	    	updateTrackInfo();
    	}
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
		
		final Runnable toastRunner = new Runnable() { public void run() { Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();}};
		
		 Thread toast = new Thread() { @Override
		public void run() { toastHandler.post(toastRunner); } };

		 toast.start(); 
	}
	
    private void setSampleRate(String rate) 
    {
    	sampleRate = Integer.parseInt(rate);
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
    
	private void setFadeLength(float fade)
    {
		if(fade > 0)
	    	fadeLength = (int)fade * 1000;
		else
			fadeLength = 10;
			
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
