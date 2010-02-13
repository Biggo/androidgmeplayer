package com.biggo.AndroidGMEPlayer;


import com.biggo.AndroidGMEPlayer.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class AndroidGMEPlayer extends Activity {
	
	private final int PLAYLIST_START_TRACK = 0;
	
	private final int MENU_SETTINGS = 0;
	
	private TextView file;
	private TextView tracks;
	private TextView song;
	private TextView game;
	private TextView system;
	private TextView author;
	private TextView copyright;
	private TextView comment;
	private TextView dumper;
	private TextView shuffle;
	private ToggleButton btnShuffleToggle;
	
	public final static String ACTION_UPDATE_TRACK_INFO = "AndroidGMEPlayer_ACTION_UPDATE_TRACK_INFO";
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.player);
    	
        Library.init(getApplicationContext());
      
        file = (TextView)findViewById(R.id.txtFile);
        tracks = (TextView)findViewById(R.id.txtTracks);
        song = (TextView)findViewById(R.id.txtSong);
        game = (TextView)findViewById(R.id.txtGame);
        system = (TextView)findViewById(R.id.txtSystem);
        author = (TextView)findViewById(R.id.txtAuthor);
        copyright = (TextView)findViewById(R.id.txtCopyright);
        comment = (TextView)findViewById(R.id.txtComment);
        dumper = (TextView)findViewById(R.id.txtDumper);  
        
        shuffle = (TextView)findViewById(R.id.txtShuffle);
        shuffle.setText(R.string.shuffle);
        btnShuffleToggle = (ToggleButton)findViewById(R.id.btnShuffleToggle);
        btnShuffleToggle.setOnCheckedChangeListener(toggleShuffleListener);
        btnShuffleToggle.setChecked(Library.getCurrentPlaylist().getRandomMode());

        IntentFilter inFilter  = new IntentFilter();
        inFilter.addAction(ACTION_UPDATE_TRACK_INFO);        
        this.registerReceiver(this.updateTrackInfoReceiver, inFilter);
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
        updateTrackInfo();
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
	    menu.add(0, MENU_SETTINGS, 0, "Settings").setIcon(android.R.drawable.ic_menu_preferences);
	    return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case MENU_SETTINGS:
	        return true;
	    }
	    return false;
	}	
	
	private CompoundButton.OnCheckedChangeListener toggleShuffleListener = new
	CompoundButton.OnCheckedChangeListener() {
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	    	Playlist curr = Library.getCurrentPlaylist();
	    	curr.setRandomMode(isChecked);
    	}
    };
	
	private void updateTrackInfo()
	{
		Track track = Library.getCurrentPlaylist().getCurrentTrack();
		if(track != null)
		{
	    	file.setText("File: " + track.getFilename());
			tracks.setText("Track: " + (track.getTrackNum() + 1) + "/" + track.getTrackCount());
	    	song.setText("Song: " + track.getSong());
	    	game.setText("Game: " + track.getGame());
	    	system.setText("System: " + track.getSystem());
	    	author.setText("Author: " + track.getAuthor());
	    	copyright.setText("Copyright: " + track.getCopyright());
	    	comment.setText("Comment: " + track.getComment());
	    	dumper.setText("Dumper: " + track.getDumper());			
		}
	}
    
    public BroadcastReceiver updateTrackInfoReceiver = new BroadcastReceiver(){

    	@Override
    	public void onReceive(Context arg0, Intent arg1) {
    		if(arg1.getAction().compareTo(ACTION_UPDATE_TRACK_INFO) == 0)
    		{
    			updateTrackInfo();
    		}

    	}
    };
}
    
