package com.biggo.AndroidGMEPlayer;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class AndroidGMEPlaylist extends ListActivity {
    
	private Playlist playlist;
	private ArrayAdapter<String> songList;
	private SimpleCursorAdapter curAdapter;
	private ListView listView;
	
	@Override
    public void onCreate(Bundle icicle) {
        try {
        	super.onCreate(icicle);
        	setContentView(R.layout.songlist);        	
        	listView = (ListView)findViewById(android.R.id.list);
        	listView.setFastScrollEnabled(true);
        
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
		if( (songList == null || playlist == null || playlist != Library.getCurrentPlaylist()))
		{
			initPlaylist();
		}
		setSelection();
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
    		populatePlayList();
    	}
		
	}
	private void setSelection()
	{
		int pos = playlist.getCurrentTrackIdx();
		int y = listView.getHeight();
		listView.setSelectionFromTop(pos, y / 2);
		
	}
    private void populatePlayList()
    {
    	/*List<String> songs = playlist.getSongs();
    	songList = new AlphaIndexerAdapter<String>(this,R.layout.song_item,songs);
    	setListAdapter(songList);*/
    	Cursor songs = playlist.getSongs();
    	curAdapter = new SimpleCursorAdapter(this, R.layout.song_item, songs, new String[]{Library.KEY_ROWID}, new int[] {R.id.SongText});
    	curAdapter.setViewBinder(songViewBinder);
    	setListAdapter(curAdapter);
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
	
    private SimpleCursorAdapter.ViewBinder songViewBinder = new SimpleCursorAdapter.ViewBinder(){

		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

			int idxRowID =	cursor.getColumnIndex(Library.KEY_ROWID);
			if(columnIndex == idxRowID )
			{
				TextView txt = (TextView) view;
				int idxGame = cursor.getColumnIndex(Library.KEY_GAME);
				String game = cursor.getString(idxGame);
				int idxSong = cursor.getColumnIndex(Library.KEY_SONG);
				String song = cursor.getString(idxSong);
				String text = "";
				if( !(game.equalsIgnoreCase("") || song.equalsIgnoreCase("")) )
				{
					text =  game + " - " + song;
				}
				else
				{
					int idxFilename = cursor.getColumnIndex(Library.KEY_FILENAME);
					String filename = cursor.getString(idxFilename);
					int idxTrackNum = cursor.getColumnIndex(Library.KEY_TRACKNUM);
					int trackNum = cursor.getInt(idxTrackNum);
					int idxTrackCount = cursor.getColumnIndex(Library.KEY_TRACKCOUNT);
					int trackCount = cursor.getInt(idxTrackCount);					
					text =  filename + " Track: " + trackNum + "/" + trackCount;
				}
				txt.setText(text);
				return true;
			}
			return false;
		}    
    };
}
