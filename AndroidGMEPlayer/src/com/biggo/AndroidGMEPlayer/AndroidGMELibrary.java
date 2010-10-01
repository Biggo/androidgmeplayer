package com.biggo.AndroidGMEPlayer;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;

public class AndroidGMELibrary extends TabActivity {
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.library);

	    Resources res = getResources(); // Resource object to get Drawables
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab

	    intent = new Intent();
	    intent.setClass(this, AndroidGMELibrarySystemList.class);
	    spec = tabHost.newTabSpec("playlist").setIndicator("Systems",
	                      res.getDrawable(android.R.drawable.ic_menu_agenda))
	                  .setContent(intent);
	    tabHost.addTab(spec);	    

	    intent = new Intent();
	    intent.setClass(this, AndroidGMELibraryGameList.class);
	    spec = tabHost.newTabSpec("playlist").setIndicator("Games",
	                      res.getDrawable(android.R.drawable.ic_menu_agenda))
	                  .setContent(intent);
	    tabHost.addTab(spec);	 
	    	    
	    intent = new Intent().setClass(this, AndroidGMEPlaylist.class);
	    spec = tabHost.newTabSpec("playlist").setIndicator("Current Playlist",
	                      res.getDrawable(android.R.drawable.ic_menu_agenda))
	                  .setContent(intent);
	    tabHost.addTab(spec);	    
	    
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
	}

	@Override
	protected void onPause() {
		super.onPause();
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
	public boolean onCreateOptionsMenu(Menu menu)
	{
	    return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	       	        	
	    }
	    return false;
	}
		
    
}
