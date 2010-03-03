package com.biggo.AndroidGMEPlayer;

import java.io.File;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.biggo.AndroidGMEPlayer.Track;

public class Library {
	
	private static Playlist library;
	
	private static Playlist currentPlaylist;
	
	private static final String PATH = "/sdcard/media/vgmusic/";
	
	private static Context currentContext;
	private static Handler loadHandler;

	private static boolean isInitialized = false;
	
	private static Thread loadingThread;
	
	private static String dialogMessage = "";	
	
    public static final String KEY_ROWID = "_id";
    public static final String KEY_PATH = "path";
    public static final String KEY_FILENAME = "filename";
    public static final String KEY_TRACKNUM = "tracknum";
    public static final String KEY_TRACKCOUNT = "trackcount";
    public static final String KEY_TYPE = "type";
    public static final String KEY_SONG = "song";
    public static final String KEY_GAME = "game";
    public static final String KEY_SYSTEM = "system";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_COPYRIGHT = "copyright";
    public static final String KEY_COMMENT = "comment";
    public static final String KEY_DUMPER = "dumper";

    private static final String TAG = "Library";
    private static DatabaseHelper mDbHelper;
    private static SQLiteDatabase mDb;
    

    
    /*
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE_LIBRARY =
            "create table library (_id integer primary key autoincrement, "
                    + "path varchar(1000) not null, filename varchar(100) not null, "
                    + "tracknum integer not null, trackcount integer not null, "
                    + "type integer null, song varchar(500) null, game varchar(500) null, "
                    + "system varchar(500) null, author varchar(500) null, "
                    + "copyright varchar(500) null, comment varchar(500) null, "
                    + "dumper varchar(500) null);";

    private static final String DATABASE_NAME = "vgmedia";
    private static final String DATABASE_TABLE = "library";
    private static final int DATABASE_VERSION = 1;   
	
	public static void init(Context context, Handler handler)
	{
		currentContext = context;
		loadHandler = handler;
		
		open();
		library = new Playlist();
		if(librarySize() == 0)
		{
			File basePath = new File(PATH);
			if( basePath.exists())
			{
				populateLibrary(basePath);
			}
		}

        dialogMessage = "Loading Playlist...";
        Message msg = loadHandler.obtainMessage();
        Bundle b = new Bundle();
        b.putInt("LoadingStatus", AndroidGMETabs.LOAD_UPDATE_MESSAGE);
        msg.setData(b);        
        loadHandler.sendMessage(msg);
        
		library = fillPlaylist(fetchAllTracks());
		library.setType(Playlist.TYPE_ALL);
		library.setTag("");
		
		currentPlaylist = library;
		
		close();
		dialogMessage = "";
		isInitialized = true;
	}

	public static void update()
	{
		if(isInitialized)
		{
			open();
			File basePath = new File(PATH);
			if( basePath.exists())
			{
				populateLibrary(basePath);
			}

	        dialogMessage = "Loading Playlist...";
	        Message msg = loadHandler.obtainMessage();
	        Bundle b = new Bundle();
	        b.putInt("LoadingStatus", AndroidGMETabs.LOAD_UPDATE_MESSAGE);
	        msg.setData(b);        
	        loadHandler.sendMessage(msg);
			
			library = fillPlaylist(fetchAllTracks());
			currentPlaylist = library;
			close();
			dialogMessage = "";
		}
		
	}
	
	private static Playlist filterPlaylist(int type, String tag)
	{
		Playlist playlist = new Playlist();
		CharSequence tagSeq = tag.subSequence(0, tag.length());
		
		for(int i = 0; i < library.getSize(); i++)
		{
			Track track = library.getTrack(i);
			switch(type)
			{
				case Playlist.TYPE_GAME: 
					String game = track.getGame();
					if(game.contains(tagSeq))
					{
						playlist.addTrack(track);								
					}
					break;
				case Playlist.TYPE_SYSTEM:
					String system = track.getSystem();
					if(system.contains(tagSeq))
					{
						playlist.addTrack(track);								
					}
					break;
				case Playlist.TYPE_PLAYLIST:
					break;
			}
		}
		return playlist;
	}

	private static void populateLibrary(File currentFile)
	{
		if(currentFile.isDirectory())
		{
			File[] directory = currentFile.listFiles();
			for(int i = 0; i < directory.length; i++)
				populateLibrary(directory[i]);
		}
		else
		{
			String path = currentFile.getAbsolutePath();
			String filename = currentFile.getName();
			if(!trackExists(path))
				addFileToLibrary(path, filename.toLowerCase());
		}
	}	
	
	private static boolean addFileToLibrary(String path, String filename)
	{
		String[] fileInfo;
		GMEPlayerLib gme = new GMEPlayerLib();
		int type;
		
		boolean multiTrackFile = false;
		
		if(filename.endsWith(".nsf"))
		{
			type = Track.TYPE_NSF;
			multiTrackFile = true;
		}
		else if(filename.endsWith(".nsfe"))
		{
			type = Track.TYPE_NSFE;
			multiTrackFile = true;
		}
		else if(filename.endsWith(".spc"))
			type = Track.TYPE_SPC;
		else if(filename.endsWith(".gbs"))
		{
			type = Track.TYPE_GBS;
			multiTrackFile = true;
		}
		else if(filename.endsWith("vgz")|| filename.endsWith(".vgm"))
			type = Track.TYPE_VGM;
		else if(filename.endsWith(".ay"))
		{
			type = Track.TYPE_AY;
			multiTrackFile = true;
		}
		else if(filename.endsWith(".hes"))
		{
			type = Track.TYPE_HES;
			multiTrackFile = true;
		}
		else if(filename.endsWith(".kss"))
		{
			type = Track.TYPE_KSS;
			multiTrackFile = true;
		}
		else if(filename.endsWith(".sap"))
		{
			type = Track.TYPE_SAP;
			multiTrackFile = true;
		}
		else
			return false;
		

		fileInfo = gme.getFileInfo(path, 0);

		
		if (fileInfo == null)
		{
			String error = gme.getLastError();
            dialogMessage = error;
            Message msg = loadHandler.obtainMessage();
            Bundle b = new Bundle();
            b.putInt("LoadingStatus", AndroidGMETabs.LOAD_UPDATE_MESSAGE);
            msg.setData(b);
            loadHandler.sendMessage(msg);			
			return false;
		}
		else
		{
            dialogMessage = "Adding " + filename;
            Message msg = loadHandler.obtainMessage();
            Bundle b = new Bundle();
            b.putInt("LoadingStatus", AndroidGMETabs.LOAD_UPDATE_MESSAGE);
            msg.setData(b);
            loadHandler.sendMessage(msg);			
			if(multiTrackFile)
			{
				int count = Integer.parseInt(fileInfo[0]);
				boolean result =  true;
				for(int i = 0; i < count; i++)
				{
					fileInfo = gme.getFileInfo(path, i);
					Track track = new Track(path, 
							filename, 
							i, 
							count, 
							fileInfo[4], 
							fileInfo[5], 
							fileInfo[6], 
							fileInfo[7], 
							fileInfo[8], 
							fileInfo[9], 
							fileInfo[10], 
							type);
					int rowID = (int) saveTrack(track);
					track.setRowID(rowID);
					result = result && library.addTrack(track);
					
				}
				return result;				
			}
			else
			{
				Track track = new Track(path, 
						filename, 
						0, 
						1, 
						fileInfo[4], 
						fileInfo[5], 
						fileInfo[6], 
						fileInfo[7], 
						fileInfo[8], 
						fileInfo[9], 
						fileInfo[10], 
						type);
				int rowID = (int) saveTrack(track);
				track.setRowID(rowID);
				return library.addTrack(track);				
			}
		}		
	}

	public static void setLoadHandler(Handler loadHandler) {
		Library.loadHandler = loadHandler;
	}
	
	public static Thread getLoadingThread() {
		return loadingThread;
	}

	
	public static String getDialogMessage() {
		return dialogMessage;
	}

	public static void setLoadingThread(Thread loadingThread) {
		Library.loadingThread = loadingThread;
	}
	
	public static boolean isLoadingThreadRunning()
	{
		if(loadingThread == null)
		{
			return false;
		}
		else
		{
			return loadingThread.isAlive();
		}
	}

	public static boolean isInitialized() {
		return isInitialized;
	}
	
	public static Playlist getPlaylist(int type, String tag)
	{
		if(type == Playlist.TYPE_ALL)
			return library;
		else
		{
			return filterPlaylist(type, tag);
		}
	}
	
	public static Playlist getCurrentPlaylist()
	{
		return currentPlaylist;
	}
	
	public static void setCurrentPlaylist(Playlist playlist)
	{
		if(!currentPlaylist.equals(playlist))
			currentPlaylist = playlist;
	}
    
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE_LIBRARY);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS library");
            onCreate(db);
        }
    }
    
    private static void open() throws SQLException {
        mDbHelper = new DatabaseHelper(currentContext);
        mDb = mDbHelper.getWritableDatabase();
    }
    
    private static void close() {
        mDbHelper.close();
    }

    private static long saveTrack(Track track) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_PATH, track.getPath());
        initialValues.put(KEY_FILENAME, track.getFilename());
        initialValues.put(KEY_TRACKNUM, track.getTrackNum());
        initialValues.put(KEY_TRACKCOUNT, track.getTrackCount());
        initialValues.put(KEY_TYPE, track.getType());
        initialValues.put(KEY_SONG, track.getSong());
        initialValues.put(KEY_GAME, track.getGame());
        initialValues.put(KEY_SYSTEM, track.getSystem());
        initialValues.put(KEY_AUTHOR, track.getAuthor());
        initialValues.put(KEY_COPYRIGHT, track.getCopyright());
        initialValues.put(KEY_COMMENT, track.getComment());
        initialValues.put(KEY_DUMPER, track.getDumper());

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    private static boolean deleteTrack(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    private static Cursor fetchAllTracks() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_PATH,
        		KEY_FILENAME, KEY_TRACKNUM, KEY_TRACKCOUNT, KEY_TYPE, KEY_SONG,
        		KEY_GAME, KEY_SYSTEM, KEY_AUTHOR, KEY_COPYRIGHT, KEY_COMMENT, KEY_DUMPER}, 
        		null, null, null, null, null);
    }
	
	private static Playlist fillPlaylist(Cursor result)
	{
		Playlist p =  new Playlist();
		if(result != null && result.getCount() > 0)
		{
			boolean OKSoFar = true;
			while(result.moveToNext() && OKSoFar)
			{
				int idxRowID = result.getColumnIndex(KEY_ROWID);
				int rowID = result.getInt(idxRowID);
				int idxPath = result.getColumnIndex(KEY_PATH);
				String path = result.getString(idxPath);
				int idxFilename = result.getColumnIndex(KEY_FILENAME);
				String filename = result.getString(idxFilename);
				File filePath = new File(path);
				if( filePath.exists())
				{
					int idxTrackNum = result.getColumnIndex(KEY_TRACKNUM);
					int idxTrackCount = result.getColumnIndex(KEY_TRACKCOUNT);
					int idxType = result.getColumnIndex(KEY_TYPE);
					int idxSong = result.getColumnIndex(KEY_SONG);
					int idxGame = result.getColumnIndex(KEY_GAME);
					int idxSystem = result.getColumnIndex(KEY_SYSTEM);
					int idxAuthor = result.getColumnIndex(KEY_AUTHOR);
					int idxCopyright = result.getColumnIndex(KEY_COPYRIGHT);
					int idxComment = result.getColumnIndex(KEY_COMMENT);
					int idxDumper = result.getColumnIndex(KEY_DUMPER);
					Track track
					= new Track(path,
							filename,
							result.getInt(idxTrackNum),
							result.getInt(idxTrackCount),
							result.getString(idxSong),
							result.getString(idxGame),
							result.getString(idxSystem),
							result.getString(idxAuthor),
							result.getString(idxCopyright),
							result.getString(idxComment),
							result.getString(idxDumper), 
							result.getInt(idxType));
					track.setRowID(rowID);
					OKSoFar = p.addTrack(track);
				}
				else
				{
		            dialogMessage = "Removing " + filename;
		            Message msg = loadHandler.obtainMessage();
		            Bundle b = new Bundle();
		            b.putInt("LoadingStatus", AndroidGMETabs.LOAD_UPDATE_MESSAGE);
		            msg.setData(b);
		            loadHandler.sendMessage(msg);
		            deleteTrack(rowID);					
				}
			}
		}
		result.close();
		return p;
	}
    
    private static boolean trackExists(String path) throws SQLException {
    	Boolean exists = false;
    	path = path.replace("'", "''");
        Cursor mCursor =
                mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID}, 
                		KEY_PATH + "='" + path + "'", null,
                        null, null, null, null);
        if (mCursor != null && mCursor.getCount() > 0) {
        	exists = true;
        }
        mCursor.close();
        return exists;
    }

    private static Track fetchTrack(long rowId) throws SQLException {
    	Track track = null;
        Cursor mCursor =
                mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_PATH,
                		KEY_FILENAME, KEY_TRACKNUM, KEY_TRACKCOUNT, KEY_TYPE, KEY_SONG,
                		KEY_GAME, KEY_SYSTEM, KEY_AUTHOR, KEY_COPYRIGHT, KEY_COMMENT, KEY_DUMPER}, 
                		KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
			int idxRowID = mCursor.getColumnIndex(KEY_ROWID);
			int rowID = mCursor.getInt(idxRowID);
			int idxPath = mCursor.getColumnIndex(KEY_PATH);
			int idxFilename = mCursor.getColumnIndex(KEY_FILENAME);
			int idxTrackNum = mCursor.getColumnIndex(KEY_TRACKNUM);
			int idxTrackCount = mCursor.getColumnIndex(KEY_TRACKCOUNT);
			int idxType = mCursor.getColumnIndex(KEY_TYPE);
			int idxSong = mCursor.getColumnIndex(KEY_SONG);
			int idxGame = mCursor.getColumnIndex(KEY_GAME);
			int idxSystem = mCursor.getColumnIndex(KEY_SYSTEM);
			int idxAuthor = mCursor.getColumnIndex(KEY_AUTHOR);
			int idxCopyright = mCursor.getColumnIndex(KEY_COPYRIGHT);
			int idxComment = mCursor.getColumnIndex(KEY_COMMENT);
			int idxDumper = mCursor.getColumnIndex(KEY_DUMPER);
			track
			= new Track(mCursor.getString(idxPath),
					mCursor.getString(idxFilename),
					mCursor.getInt(idxTrackNum),
					mCursor.getInt(idxTrackCount),
					mCursor.getString(idxSong),
					mCursor.getString(idxGame),
					mCursor.getString(idxSystem),
					mCursor.getString(idxAuthor),
					mCursor.getString(idxCopyright),
					mCursor.getString(idxComment),
					mCursor.getString(idxDumper), 
					mCursor.getInt(idxType));
			track.setRowID(rowID);   
        }        
        return track;

    }

    private static boolean updateTrack(Track track) {
        ContentValues args = new ContentValues();
        args.put(KEY_PATH, track.getPath());
        args.put(KEY_FILENAME, track.getFilename());
        args.put(KEY_TRACKNUM, track.getTrackNum());
        args.put(KEY_TRACKCOUNT, track.getTrackCount());
        args.put(KEY_TYPE, track.getType());
        args.put(KEY_SONG, track.getSong());
        args.put(KEY_GAME, track.getGame());
        args.put(KEY_SYSTEM, track.getSystem());
        args.put(KEY_AUTHOR, track.getAuthor());
        args.put(KEY_COPYRIGHT, track.getCopyright());
        args.put(KEY_COMMENT, track.getComment());
        args.put(KEY_DUMPER, track.getDumper());
        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + track.getRowID(), null) > 0;
    }
    
    private static int librarySize()
    {
    	String[] select = {KEY_ROWID};
    	Cursor mCursor = mDb.query(DATABASE_TABLE, select , null, null, null, null, null);
    	int count =  mCursor.getCount();
    	mCursor.close();
    	return count;
    }
}
