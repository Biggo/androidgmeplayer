package com.biggo.AndroidGMEPlayer;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;



public class AndroidGMEPlaylist extends ListActivity {
    
	private Playlist playlist;
	
	@Override
    public void onCreate(Bundle icicle) {
        try {
        	super.onCreate(icicle);
        	setContentView(R.layout.songlist);
        	
        	playlist = Library.getCurrentPlaylist();  
        	if(playlist != null)
        	{
        		int pos = playlist.getCurrentTrackIdx();
	    		populatePlayList();
	    		setSelection(pos);
        	}
        
        } catch (NullPointerException e) {
        	Log.v(getString(R.string.app_name), e.getMessage());
        }
    }
    
    public void populatePlayList()
    {
		ArrayAdapter<String> songList = new ArrayAdapter<String>(this,R.layout.song_item,playlist.getSongs());
		setListAdapter(songList);	
    }
    
	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
		try {
    		Intent in = new Intent(AndroidGMEPlaylist.this, PlayerService.class);
    		in.setAction(PlayerService.ACTION_CHANGE_TRACK);
        	in.putExtra("TrackNumber", position);
        	startService(in);
		} catch(Exception e) {
		} 
	}
}