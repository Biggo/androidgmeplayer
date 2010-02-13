package com.biggo.AndroidGMEPlayer;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TabHost;

public class AndroidGMETabs extends TabActivity {
	
	private AndroidGMEPlayerMediaController mc;
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
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
		mc.bind();
	}

	@Override
	protected void onPause() {
		super.onPause();
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
    		//playerService.previousTrack();
    		//updateTrackInfo();
    		Intent in = new Intent(AndroidGMETabs.this, PlayerService.class);
    		in.setAction(PlayerService.ACTION_PREVIOUS_TRACK);
    		startService(in);
    	}
    };
    
    private View.OnClickListener nextTrckListener = new
    View.OnClickListener() {
    	public void onClick(View v) {
    		//playerService.nextTrack();
    		//updateTrackInfo();
    		Intent in = new Intent(AndroidGMETabs.this, PlayerService.class);
    		in.setAction(PlayerService.ACTION_NEXT_TRACK);
    		startService(in);
    	}
    };    

}
