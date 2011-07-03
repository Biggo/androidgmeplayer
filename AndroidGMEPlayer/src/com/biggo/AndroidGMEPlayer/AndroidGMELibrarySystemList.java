package com.biggo.AndroidGMEPlayer;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;
import android.widget.TwoLineListItem;

public class AndroidGMELibrarySystemList extends ExpandableListActivity  {

	SimpleCursorTreeAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Cursor systems = Library.getTags(Playlist.TYPE_SYSTEM, null, null);
        
        // Set up our adapter
        mAdapter = new SystemExpandableTreeAdapter(this, systems, 
        		android.R.layout.simple_expandable_list_item_2, new String[]{Library.KEY_SYSTEM}, new int[]{android.R.id.text2},
        		android.R.layout.simple_expandable_list_item_2, new String[]{Library.KEY_ROWID}, new int[]{android.R.id.text2});
        setListAdapter(mAdapter);
        ExpandableListView listView = getExpandableListView();
        listView.setOnChildClickListener(clickListener);
        //registerForContextMenu(getExpandableListView());
    }
    
    private ExpandableListView.OnChildClickListener clickListener = new ExpandableListView.OnChildClickListener() {

		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			Cursor cursor = mAdapter.getChild(groupPosition, childPosition);
			int idxGame = cursor.getColumnIndex(Library.KEY_GAME);
			String game = cursor.getString(idxGame);
			Playlist p = Library.getPlaylist(Playlist.TYPE_GAME, game);
			Library.setCurrentPlaylist(p);
	    	Intent in = new Intent(AndroidGMELibrarySystemList.this, AndroidGMEPlaylist.class);
	    	startActivity(in);
			return true;
		}
    	
    };


    public class SystemExpandableTreeAdapter extends SimpleCursorTreeAdapter
    {

		public SystemExpandableTreeAdapter(Context context, Cursor cursor,
				int groupLayout, String[] groupFrom, int[] groupTo,
				int childLayout, String[] childFrom, int[] childTo) {
			super(context, cursor, groupLayout, groupFrom, groupTo, childLayout, childFrom,
					childTo);
		}


		@Override
		protected void bindGroupView(View view, Context context, Cursor cursor,
				boolean isExpanded) {
			TwoLineListItem txt = (TwoLineListItem)view;
			int idxSystem = cursor.getColumnIndex(Library.KEY_SYSTEM);
			String system = cursor.getString(idxSystem);
			txt.getText1().setText(system);
		}
		

		@Override
		protected void bindChildView(View view, Context context, Cursor cursor,
				boolean isLastChild) {	
			TwoLineListItem txt = (TwoLineListItem)view;
			int idxGame = cursor.getColumnIndex(Library.KEY_GAME);
			String game = cursor.getString(idxGame);
			txt.getText1().setText(game);
		}


		@Override
		protected Cursor getChildrenCursor(Cursor parent) {
			int idxSystem = parent.getColumnIndex(Library.KEY_SYSTEM);
			String currentSystem = parent.getString(idxSystem);
			return Library.getTags(Playlist.TYPE_GAME, Library.KEY_SYSTEM, currentSystem);
		}
    }
}
