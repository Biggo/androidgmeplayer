package com.biggo.AndroidGMEPlayer;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;
import android.widget.TwoLineListItem;

public class AndroidGMELibraryGameList extends ExpandableListActivity  {

	SimpleCursorTreeAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Cursor games = Library.getTags(Playlist.TYPE_GAME, null, null);
                
        // Set up our adapter
        mAdapter = new GameExpandableTreeAdapter(this, games, 
        		android.R.layout.simple_expandable_list_item_2, new String[]{Library.KEY_GAME}, new int[]{android.R.id.text2},
        		android.R.layout.simple_expandable_list_item_2, new String[]{Library.KEY_ROWID}, new int[]{android.R.id.text2});
        setListAdapter(mAdapter);
        //registerForContextMenu(getExpandableListView());
    }


    public class GameExpandableTreeAdapter extends SimpleCursorTreeAdapter
    {

		public GameExpandableTreeAdapter(Context context, Cursor cursor,
				int groupLayout, String[] groupFrom, int[] groupTo,
				int childLayout, String[] childFrom, int[] childTo) {
			super(context, cursor, groupLayout, groupFrom, groupTo, childLayout, childFrom,
					childTo);
		}


		@Override
		protected void bindGroupView(View view, Context context, Cursor cursor,
				boolean isExpanded) {
			TwoLineListItem txt = (TwoLineListItem)view;
			int idxGame = cursor.getColumnIndex(Library.KEY_GAME);
			String game = cursor.getString(idxGame);
			txt.getText1().setText(game);
		}
		

		@Override
		protected void bindChildView(View view, Context context, Cursor cursor,
				boolean isLastChild) {
			TwoLineListItem txt = (TwoLineListItem)view;
			int idxSong = cursor.getColumnIndex(Library.KEY_SONG);
			String song = cursor.getString(idxSong);
			String text = "";
			if( !song.equalsIgnoreCase(""))
			{
				text = song;
			}
			else
			{
				int idxFilename = cursor.getColumnIndex(Library.KEY_FILENAME);
				String filename = cursor.getString(idxFilename);
				int idxTrackNum = cursor.getColumnIndex(Library.KEY_TRACKNUM);
				int trackNum = cursor.getInt(idxTrackNum);
				int idxTrackCount = cursor.getColumnIndex(Library.KEY_TRACKCOUNT);
				int trackCount = cursor.getInt(idxTrackCount);					
				text =  filename + " Track: " + (trackNum + 1) + "/" + trackCount;
			}
			txt.getText1().setText(text);			
		}


		@Override
		protected Cursor getChildrenCursor(Cursor parent) {
			int idxGame = parent.getColumnIndex(Library.KEY_GAME);
			String game = parent.getString(idxGame);
			Playlist gamePlaylist = Library.getPlaylist(Playlist.TYPE_GAME, game);
			return gamePlaylist.getSongs();
		}
    }
}
