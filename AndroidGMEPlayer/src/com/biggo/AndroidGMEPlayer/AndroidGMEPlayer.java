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
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;

public class AndroidGMEPlayer extends Activity {
	
	private final int PLAYLIST_START_TRACK = 0;
	
	private final int MENU_SHUFFLE = 0;
	
	private MediaController mc;
	private TextView file;
	private TextView tracks;
	private TextView song;
	private TextView game;
	private TextView system;
	private TextView author;
	private TextView copyright;
	private TextView comment;
	private TextView dumper;
	
	public final static String ACTION_UPDATE_TRACK_INFO = "AndroidGMEPlayer_ACTION_UPDATE_TRACK_INFO";
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);
    	LinearLayout v = (LinearLayout)findViewById(R.layout.main); 
        
        Library.init(getApplicationContext());
        
        mc = new MediaController(this);
        mc.setMediaPlayer(new AndroidGMEPlayerMediaPlayerControl(this));
        mc.setPrevNextListeners(nextTrckListener, prevTrckListener);
        mc.setEnabled(true);
        mc.setAnchorView(v);
        
        file = (TextView)findViewById(R.layout.txtFile);
        tracks = (TextView)findViewById(R.layout.txtTracks);
        song = (TextView)findViewById(R.layout.txtSong);
        game = (TextView)findViewById(R.layout.txtGame);
        system = (TextView)findViewById(R.layout.txtSystem);
        author = (TextView)findViewById(R.layout.txtAuthor);
        copyright = (TextView)findViewById(R.layout.txtCopyright);
        comment = (TextView)findViewById(R.layout.txtComment);
        dumper = (TextView)findViewById(R.layout.txtDumper);        
        
        Button btnShowPlaylist = (Button)findViewById(R.layout.btnShowPlaylist);
        btnShowPlaylist.setOnClickListener(showPlaylist);        

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
	    menu.add(0, MENU_SHUFFLE, 0, "Shuffle").setIcon(android.R.drawable.ic_menu_rotate);
	    return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case MENU_SHUFFLE:
	    	Playlist curr = Library.getCurrentPlaylist();
	    	Boolean shuffle = curr.getRandomMode();
	    	curr.setRandomMode(!shuffle);
	        return true;
	    }
	    return false;
	}

	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(!mc.isShowing())
		{
			try
			{
				mc.show(0);
			}
			catch(Exception e)
			{}
		}
		return super.onTouchEvent(event);
	}
	
	private void updateTrackInfo()
	{
		Track track = Library.getCurrentPlaylist().getCurrentTrack();
		if(track != null)
		{
	    	file.setText("File: " + track.getPath());
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

	private View.OnClickListener prevTrckListener = new
    View.OnClickListener() {
    	public void onClick(View v) {
    		//playerService.previousTrack();
    		//updateTrackInfo();
    		Intent in = new Intent(AndroidGMEPlayer.this, PlayerService.class);
    		in.setAction(PlayerService.ACTION_PREVIOUS_TRACK);
    		startService(in);
    	}
    };
    
    private View.OnClickListener nextTrckListener = new
    View.OnClickListener() {
    	public void onClick(View v) {
    		//playerService.nextTrack();
    		//updateTrackInfo();
    		Intent in = new Intent(AndroidGMEPlayer.this, PlayerService.class);
    		in.setAction(PlayerService.ACTION_NEXT_TRACK);
    		startService(in);
    	}
    };    
    
    private View.OnClickListener showPlaylist = new
    View.OnClickListener() {
	    public void onClick(View v) {
	    	try {
				Intent in = new Intent();
				in.setClassName("com.biggo.AndroidGMEPlayer", "com.biggo.AndroidGMEPlayer.AndroidGMEPlaylist");
				in.setAction("AndroidGMEPlayer_ACTION_VIEW_PLAYLIST");
				Playlist playlist = Library.getCurrentPlaylist();
				in.putExtra("PlaylistType", playlist.getType());
				in.putExtra("PlaylistTag", playlist.getTag());
				in.putExtra("CurrentTrack", playlist.getCurrentTrackIdx());
				startActivityForResult(in, PLAYLIST_START_TRACK);
			}
			catch (Exception e){
				
			}
	    }
    };
    
 // Listen for results.
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        // See which child activity is calling us back.
        switch (requestCode) {
            case PLAYLIST_START_TRACK:
                // This is the standard resultCode that is sent back if the
                // activity crashed or didn't doesn't supply an explicit result.
                if (resultCode == RESULT_CANCELED){} 
                else
                {
            		Intent in = new Intent(AndroidGMEPlayer.this, PlayerService.class);
            		in.setAction(PlayerService.ACTION_CHANGE_TRACK);
                	in.putExtra("TrackNumber", data.getIntExtra("TrackNumber", 0));
                	startService(in);
                }
            default:
                break;
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
    
