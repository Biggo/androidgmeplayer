package com.biggo.AndroidGMEPlayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.MediaController.MediaPlayerControl;

public class AndroidGMEPlayerMediaPlayerControl implements MediaPlayerControl {

	private PlayerService playerService;
	private ServiceConnection mConnection;

	private boolean mIsBound = false;
	
	
	public AndroidGMEPlayerMediaPlayerControl(AndroidGMEPlayer source) {    
        
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
        mIsBound = source.bindService(new Intent(source, PlayerService.class), mConnection, Context.BIND_AUTO_CREATE);
		
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
