package com.biggo.AndroidGMEPlayer;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TabHost;

public class AndroidGMETabs extends TabActivity {
	
	private AndroidGMEPlayerMediaController mc;
	private final int MENU_SETTINGS = 0;	
	private final int MENU_UPDATE_LIBRARY = 1;	
	static final int INIT_DIALOG = 0;
	static final int UPDATE_DIALOG = 1;
	private boolean progressFeatureEnabled = false;
	
	private ProgressDialog libraryDialog;
	private LibraryLoadThread libraryLoadThread;
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    progressFeatureEnabled = this.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
	    setContentView(R.layout.main);

	    Resources res = getResources(); // Resource object to get Drawables
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab

	    // Create an Intent to launch an Activity for the tab (to be reused)
	    intent = new Intent().setClass(this, AndroidGMEPlayer.class);

	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec("player").setIndicator("Player",
	                      res.getDrawable(android.R.drawable.ic_media_play))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    // Do the same for the other tabs
	    intent = new Intent().setClass(this, AndroidGMEPlaylist.class);
	    spec = tabHost.newTabSpec("playlist").setIndicator("Playlist",
	                      res.getDrawable(android.R.drawable.ic_menu_agenda))
	                  .setContent(intent);
	    tabHost.addTab(spec);	    
	    
		Intent in = new Intent(AndroidGMETabs.this, PlayerService.class);
		in.setAction("NO_ACTION");
		startService(in);	    

        mc = new AndroidGMEPlayerMediaController(this);
        mc.setPrevNextListeners(nextTrckListener, prevTrckListener);
        mc.setEnabled(true);
        mc.setAnchorView(getTabHost());

	    tabHost.setCurrentTab(0);
	}
    
    @Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	    if(!Library.isInitialized() || Library.isLoadingThreadRunning())
	    {
	    	showDialog(INIT_DIALOG);        
	    	if(progressFeatureEnabled)
	        	setProgressBarIndeterminateVisibility(Library.isLoadingThreadRunning());
	    }
		mc.bind();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(libraryDialog != null)
		{
			removeDialog(INIT_DIALOG);
        	libraryDialog = null;
		}
		mc.unbind();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		showMediaController();
		return super.onTouchEvent(event);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
	    menu.add(0, MENU_SETTINGS, 0, "Settings").setIcon(android.R.drawable.ic_menu_preferences);
	    menu.add(0, MENU_UPDATE_LIBRARY, 0, "Update Library").setIcon(android.R.drawable.ic_menu_agenda);
	    return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case MENU_SETTINGS:
	    	Intent settingsActivity = new Intent(this, AndroidGMESettings.class);
	    	startActivity(settingsActivity);
	        return true;
	    case MENU_UPDATE_LIBRARY:
	        showDialog(INIT_DIALOG);
	        return true;	       	        	
	    }
	    return false;
	}
		
	private void showMediaController()
	{
		if(mc != null && !mc.isShowing())
		{
			try
			{
				mc.show(0);
			}
			catch(Exception e)
			{}
		}
	}


	private View.OnClickListener prevTrckListener = new
    View.OnClickListener() {
    	public void onClick(View v) {
    		Intent in = new Intent(AndroidGMETabs.this, PlayerService.class);
    		in.setAction(PlayerService.ACTION_PREVIOUS_TRACK);
    		startService(in);
    	}
    };
    
    private View.OnClickListener nextTrckListener = new
    View.OnClickListener() {
    	public void onClick(View v) {
    		Intent in = new Intent(AndroidGMETabs.this, PlayerService.class);
    		in.setAction(PlayerService.ACTION_NEXT_TRACK);
    		startService(in);
    	}
    };    
    
    
    protected Dialog onCreateDialog(int id) {
        switch(id) {
        case INIT_DIALOG: 
        	if(libraryDialog == null)
        		libraryDialog = new ProgressDialog(AndroidGMETabs.this);
			libraryDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			libraryDialog.setTitle("Loading Library");
			
			String currentMessage = Library.getDialogMessage();
			
			if(!Library.isInitialized())
			{
				if(currentMessage.length() == 0)
					libraryDialog.setMessage("Initializing Library...");
				else
					libraryDialog.setMessage(currentMessage);
				libraryDialog.setCancelable(false);
			}
			else
			{
				if(currentMessage.length() == 0)
					libraryDialog.setMessage("Updating Library...");
				else
					libraryDialog.setMessage(currentMessage);
				libraryDialog.setCancelable(true);
			}
			

        	initLoadHandler();
                
            if(!Library.isLoadingThreadRunning())
            {
            	libraryLoadThread = new LibraryLoadThread(loadHandler);
            	libraryLoadThread.start();
            	Library.setLoadingThread(libraryLoadThread);
            }
            else
            {
            	libraryLoadThread = (LibraryLoadThread)Library.getLoadingThread();
            	libraryLoadThread.mHandler = loadHandler;
            	Library.setLoadHandler(loadHandler);
            }
            if(progressFeatureEnabled)
            	this.setProgressBarIndeterminateVisibility(true);
            return libraryDialog;
        default:
            return null;
        }
    }
    
    private void initLoadHandler()
    {
    	loadHandler = new Handler() {
            public void handleMessage(Message msg) {
            	int loadStatus = msg.getData().getInt("LoadingStatus");
            	if(libraryDialog != null)
            	{
                	switch (loadStatus)
                	{
        	        	case LOAD_UPDATE_MESSAGE:
        	        		libraryDialog.setMessage(Library.getDialogMessage());
        	        		break;
        	        	case LOAD_DONE_LOADING:
        	                removeDialog(INIT_DIALOG);
        	                libraryDialog = null;
                    		Intent intent = new Intent();
                            if(progressFeatureEnabled)
                            	setProgressBarIndeterminateVisibility(false);
                    		intent.setAction(AndroidGMEPlayer.ACTION_UPDATE_TRACK_INFO);
                    		sendBroadcast(intent);
        	                break;
        	        	default:
        	        		break;
                	} 
            		
            	}
            }
        };
    }

    public static final int LOAD_UPDATE_MESSAGE = 0;
    public static final int LOAD_DONE_LOADING = 1;
    // Define the Handler that receives messages from the thread and update the progress
    private Handler loadHandler; 
    
    /** Nested class that performs progress calculations (counting) */
    private class LibraryLoadThread extends Thread {
        public Handler mHandler;
  
        LibraryLoadThread(Handler h) {
            mHandler = h;
        }
       
        public void run() {
        	if(isSdPresent())
        	{
            	if(!Library.isInitialized())
            	{
            		Library.init(AndroidGMETabs.this, mHandler);
            	}
            	else
            		Library.update();        		
        	}
        		
            Message msg = mHandler.obtainMessage();
            Bundle b = new Bundle();
            b.putInt("LoadingStatus", LOAD_DONE_LOADING);
            msg.setData(b);
            mHandler.sendMessage(msg);
        }
    }
    
    public static boolean isSdPresent() {
    	return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);  
    }  

}
