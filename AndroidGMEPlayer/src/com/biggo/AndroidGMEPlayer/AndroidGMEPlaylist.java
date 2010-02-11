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
        	Bundle extras  = getIntent().getExtras(); 
        	if(extras != null)
        	{
        		int type = extras.containsKey("PlaylistType")?extras.getInt("PlaylistType"):Playlist.TYPE_ALL;
        		String tag = extras.containsKey("PlaylistTag")?extras.getString("PlaylistTag"):""; 
        		int pos = extras.containsKey("CurrentTrack")?extras.getInt("CurrentTrack"):0; 
        		populatePlayList(type, tag);
        		setSelection(pos);
        	}
        } catch (NullPointerException e) {
        	Log.v(getString(R.string.app_name), e.getMessage());
        }
    }
    
    public void populatePlayList(int type, String tag) {
    	if(playlist == null)
    		playlist = Library.getPlaylist(type, tag);		
    		ArrayAdapter<String> songList = new ArrayAdapter<String>(this,R.layout.song_item,playlist.getSongs());
    		setListAdapter(songList);
    		
    }
    
	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
		try {
			Library.setCurrentPlaylist(playlist);			
            Intent in = getIntent();
            in.putExtra("TrackNumber", position);
            setResult( RESULT_OK, in);
            finish();
		} catch(Exception e) {
		} 
	}
}