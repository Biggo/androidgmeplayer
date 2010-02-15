package com.biggo.AndroidGMEPlayer;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AndroidGMEPlaylist extends ListActivity {
    
	private Playlist playlist;
	private ArrayAdapter<String> songList;
	private ListView listView;
	
	@Override
    public void onCreate(Bundle icicle) {
        try {
        	super.onCreate(icicle);
        	setContentView(R.layout.songlist);        	
        	listView = (ListView)findViewById(android.R.id.list);
        	initPlaylist();
        
        } catch (NullPointerException e) {
        }
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
		if(songList != null && playlist != null)
		{
			int pos = playlist.getCurrentTrackIdx();
    		int y = listView.getHeight();
    		listView.setSelectionFromTop(pos, y / 2);
		}
		else
		{        	
			initPlaylist();
		}
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
	
	private void initPlaylist()
	{        	
    	playlist = Library.getCurrentPlaylist();  
    	if(playlist != null)
    	{
    		int pos = playlist.getCurrentTrackIdx();
    		populatePlayList();
    		int y = listView.getHeight();
    		listView.setSelectionFromTop(pos, y / 2);
    	}
		
	}
    public void populatePlayList()
    {
    	songList = new ArrayAdapter<String>(this,R.layout.song_item,playlist.getSongs());
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