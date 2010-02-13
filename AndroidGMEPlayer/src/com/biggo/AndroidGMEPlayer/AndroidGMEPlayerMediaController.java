package com.biggo.AndroidGMEPlayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.MediaController;

public class AndroidGMEPlayerMediaController extends MediaController {
	
	private PlayerService playerService;
	private ServiceConnection mConnection;	
	private boolean mIsBound = false;
	private Context mContext;

	public AndroidGMEPlayerMediaController(Context context)
	{
		super(context);
		mContext = context;
		this.setMediaPlayer(new AndroidGMEPlayerMediaPlayerControl());
	}
	
	public void bind()
	{
		if(mConnection == null && !mIsBound)
		{
	    	mConnection = new ServiceConnection() {
	            public void onServiceConnected(ComponentName className, IBinder service) {
	                // This is called when the connection with the service has been
	                // established, giving us the service object we can use to
	                // interact with the service.  Because we have bound to a explicit
	                // service that we know is running in our own process, we can
	                // cast its IBinder to a concrete class and directly access it.
	                playerService = ((PlayerService.LocalBinder)service).getService();
	              }
	
	            public void onServiceDisconnected(ComponentName className) {
	                // This is called when the connection with the service has been
	                // unexpectedly disconnected -- that is, its process crashed.
	                // Because it is running in our same process, we should never
	                // see this happen.
	                playerService = null;
	            }
	        };        
	        mIsBound = mContext.bindService(new Intent(mContext, PlayerService.class), mConnection, Context.BIND_AUTO_CREATE);
		}
	}
		
	public void unbind()
	{
		if(mIsBound)
		{
			try
			{
				mContext.unbindService(mConnection);
				mIsBound = false;
				mConnection = null;
			}
			catch (Exception e)
			{
				//this is bad, but I don't know why I'm getting "Service not registered"
			}
		}

	}

	public class AndroidGMEPlayerMediaPlayerControl implements MediaPlayerControl {
		
		public AndroidGMEPlayerMediaPlayerControl() {
			super();
		}
		@Override
	    public int getBufferPercentage() {
	    	return 100;
	    }
	    
	    @Override
	    public int getCurrentPosition() {
	    	if(mIsBound)
	    		return playerService.getTime();
	    	else
	    		return 0;
	    }
	    
	    @Override
	    public int getDuration() {
	    	if(mIsBound)
	    		return playerService.getTrackLength();
	    	else
	    		return 0;
	    }
	    
	    @Override
	    public boolean isPlaying() {
	    	if(mIsBound)
	    		return playerService.getIsPlaying();
	    	else
	    		return false;
	    }
	    
	    @Override
	    public void pause() {
	    	if(mIsBound)
	    		playerService.pause();
	    }
	    
	    @Override
	    public void seekTo(int pos) {
	    	if(mIsBound)
	    		playerService.seekTo(pos);
	    }
	    
	    @Override
	    public void start() {
	    	if(mIsBound)
	    		playerService.play();
	    }
	
	}
}
