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
import com.biggo.AndroidGMEPlayer.PlayerLibs.ITrackInfo;
import com.biggo.AndroidGMEPlayer.PlayerLibs.TrackInfoFactory;

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
    public static final String KEY_TRACKLENGTH = "tracklength";
    public static final String KEY_INTROLENGTH = "introlength";
    public static final String KEY_LOOPLENGTH = "looplength";

    private static final String TAG = "Library";
    private static DatabaseHelper mDbHelper;
    private static SQLiteDatabase mDb;
    

    
    /*
     * Database creation sql statement
     */
    private static final String DATABASE_NAME = "vgmedia";
    
    private static final String DATABASE_TABLE_TRACKS = "tracks";
    private static final String DATABASE_CREATE_TRACKS =
            "create table tracks (_id integer primary key autoincrement, "
                    + "path varchar(1000) not null, " 
                    + "filename varchar(100) not null, "
                    + "tracknum integer not null, " 
                    + "trackcount integer not null, "
                    + "type integer null, " 
                    + "song varchar(500) null, " 
                    + "game varchar(500) null, "
                    + "system varchar(500) null, " 
                    + "author varchar(500) null, "
                    + "copyright varchar(500) null, " 
                    + "comment varchar(500) null, "
                    + "dumper varchar(500) null, " 
                    + "tracklength int null, "
                    + "introlength int null, " 
                    + "looplength int null); "
    				+ "create index if not exists tracks_index on tracks(song, game, system);";


    private static final String DATABASE_TABLE_SYSTEMS = "systems";
    private static final String DATABASE_CREATE_SYSTEMS =
    		"create table systems (_id integer primary key autoincrement, "
    				+ "system varchar(500) unique);" 
    				+ "create index if not exists systems_index on systems(system);"
   					+ "Insert into systems(system) values('UNKNOWN')";
    
    private static final String DATABASE_TABLE_GAMES = "games";
    private static final String DATABASE_CREATE_GAMES =
		"create table games (_id integer primary key autoincrement, "
					+ "game varchar(500) unique,"
                    + "system varchar(500) null);" 
    				+ "create index if not exists games_index on games(game);"
					+ "Insert into games(game) values('UNKNOWN')";	
    
    private static final int DATABASE_VERSION = 5;   
	
	public static void init(Context context, Handler handler)
	{
		currentContext = context;
		loadHandler = handler;
		open();
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
        
        Cursor result = fetchTracks(null, null);
		library = new Playlist(result);
		library.setType(Playlist.TYPE_ALL);
		library.setTag("");
		
		currentPlaylist = library;
		
		dialogMessage = "";
		isInitialized = true;
	}

	public static void update()
	{
		if(isInitialized)
		{
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
			
	        Cursor result = fetchTracks(null, null);
			library = new Playlist(result);
			currentPlaylist = library;
			dialogMessage = "";
		}
		
	}
	
	private static Playlist filterPlaylist(int type, String tag)
	{
		return new Playlist(fetchTracks(selectionFromType(type) , new String[] {tag}));
	}
	
	private static String selectionFromType(int type)
	{
		String selection = "";
		switch(type)
		{
			case Playlist.TYPE_GAME:
				selection = KEY_GAME + " = ?";
				break;
			case Playlist.TYPE_SYSTEM:
				selection = KEY_SYSTEM + " = ?";
				break;
			case Playlist.TYPE_PLAYLIST:
				break;
		}
		return selection;
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
		ITrackInfo fileInfo = TrackInfoFactory.getTrackInfo(path,filename);
		
		if (fileInfo == null)
		{
			/*String error = "Invalid file format";
            dialogMessage = error;
            Message msg = loadHandler.obtainMessage();
            Bundle b = new Bundle();
            b.putInt("LoadingStatus", AndroidGMETabs.LOAD_UPDATE_MESSAGE);
            msg.setData(b);
            loadHandler.sendMessage(msg);	*/		
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
            Track track = fileInfo.getTrack();
        	boolean result =  track != null;
        	if(result)
        	{
        		String game = track.getGame();
        		String system = track.getSystem();
        		if(!systemExists(system))
        		{
        			saveSystem(system);
        		}
        		
        		if(!gameExists(game))
        		{
        			saveGame(game, system);
        		}
                int rowID = (int) saveTrack(track);
                track.setRowID(rowID);
            	result = result && rowID != -1;
        	}
            while(fileInfo.hasTracks())
            {
            	track = fileInfo.getTrack();
                int rowID = (int) saveTrack(track);
                track.setRowID(rowID);
            	result = result && rowID != -1; //&& library.addTrack(track);
			}
			return result;
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
	
	public static Cursor getTags(int type, String filterType, String filter)
	{
		String col = "";
		String selection = null;
		String[] selectionArgs = null;
		Cursor list = null;
		
		if(type == Playlist.TYPE_GAME)
		{
			list = fetchGames(filter);
		}
		else if(type == Playlist.TYPE_SYSTEM)
		{
			list = fetchSystems();
		}
		else
		{	
			if(filterType != null)
			{
				selection = filterType + " = ?";
				selectionArgs = new String[] {filter};
			}
		
			list = fetchColumn(col, selection, selectionArgs);
		}
		return list;
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
		{
			currentPlaylist.getSongs().close();
			currentPlaylist = playlist;
		}
	}
    
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE_TRACKS);
            db.execSQL(DATABASE_CREATE_SYSTEMS);
            db.execSQL(DATABASE_CREATE_GAMES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS tracks");
            db.execSQL("DROP TABLE IF EXISTS systems");
            db.execSQL("DROP TABLE IF EXISTS games");
            db.execSQL("DROP index IF EXISTS tracks_index");
            db.execSQL("DROP index IF EXISTS systems_index");
            db.execSQL("DROP index IF EXISTS games_index");
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

    private static int saveTrack(Track track) {
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
        initialValues.put(KEY_TRACKLENGTH, track.getTrackLength());
        initialValues.put(KEY_INTROLENGTH, track.getIntroLength());
        initialValues.put(KEY_LOOPLENGTH, track.getLoopLength());

        return (int)mDb.insert(DATABASE_TABLE_TRACKS, null, initialValues);
    }

    private static int saveGame(String game, String system) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_GAME, game);
        initialValues.put(KEY_SYSTEM, system);
        return (int)mDb.insert(DATABASE_TABLE_GAMES, null, initialValues);
    }

    private static int saveSystem(String system) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_SYSTEM, system);
        return (int)mDb.insert(DATABASE_TABLE_SYSTEMS, null, initialValues);
    }

    private static boolean deleteTrack(long rowId) {

        return mDb.delete(DATABASE_TABLE_TRACKS, KEY_ROWID + "=" + rowId, null) > 0;
    }

    private static Cursor fetchTracks(String selection, String[] selectionArgs) {    	
        return mDb.query(DATABASE_TABLE_TRACKS, new String[] {KEY_ROWID, KEY_PATH,
        		KEY_FILENAME, KEY_TRACKNUM, KEY_TRACKCOUNT, KEY_TYPE, KEY_SONG,
        		KEY_GAME, KEY_SYSTEM, KEY_AUTHOR, KEY_COPYRIGHT, KEY_COMMENT, KEY_DUMPER,
        		KEY_TRACKLENGTH, KEY_INTROLENGTH, KEY_LOOPLENGTH}, 
        		selection, selectionArgs, null, null, KEY_GAME);
    }

    private static Cursor fetchSystems() {    	
        return mDb.query(DATABASE_TABLE_SYSTEMS, new String[] {KEY_ROWID, KEY_SYSTEM}, 
        		null, null, null, null, KEY_SYSTEM);
    }

    private static Cursor fetchGames(String system) {
    	String selection = null;
    	String[] selectionArgs = null;
    	if(system != null)
    	{
    		selection = KEY_SYSTEM + " = ?";
    		selectionArgs = new String[]{system};
    	}
        return mDb.query(DATABASE_TABLE_GAMES, new String[] {KEY_ROWID, KEY_GAME, KEY_SYSTEM}, 
        		selection, selectionArgs, null, null, KEY_GAME);
    }
    
    private static Cursor fetchColumn(String col, String selection, String[] selectionArgs) {
    	
        return  mDb.query(true, DATABASE_TABLE_TRACKS, new String[] {col}, 
        		selection, selectionArgs, null, null, null, null);
    }
    
    private static boolean gameExists(String game) throws SQLException 
    {
    	Boolean exists = false; 
        Cursor mCursor =
                mDb.query(true, DATABASE_TABLE_GAMES, new String[] {KEY_ROWID}, 
                		KEY_GAME + "= ?", new String[]{game},
                        null, null, null, null);
        if (mCursor != null && mCursor.getCount() > 0) {
        	exists = true;
        }
        mCursor.close();
        return exists;    	
    }
    
    private static boolean systemExists(String system) throws SQLException 
    {
    	Boolean exists = false;
        Cursor mCursor =
                mDb.query(true, DATABASE_TABLE_SYSTEMS, new String[] {KEY_ROWID}, 
                		KEY_SYSTEM + "= ?", new String[]{system},
                        null, null, null, null);
        if (mCursor != null && mCursor.getCount() > 0) {
        	exists = true;
        }
        mCursor.close();
        return exists;    	
    }
	
    private static boolean trackExists(String path) throws SQLException {
    	Boolean exists = false;
    	String[] args = {path}; 
        Cursor mCursor =
                mDb.query(true, DATABASE_TABLE_TRACKS, new String[] {KEY_ROWID}, 
                		KEY_PATH + "= ?", args,
                        null, null, null, null);
        if (mCursor != null && mCursor.getCount() > 0) {
        	exists = true;
        }
        mCursor.close();
        return exists;
    }
    
    public static Track getTrack(Cursor result)
    {
    	Track track = null;
    	if (result != null) {
			int idxRowID = result.getColumnIndex(KEY_ROWID);
			int rowID = result.getInt(idxRowID);
			int idxPath = result.getColumnIndex(KEY_PATH);
			int idxFilename = result.getColumnIndex(KEY_FILENAME);
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
			int idxTrackLength = result.getColumnIndex(KEY_TRACKLENGTH);
			int idxIntroLength = result.getColumnIndex(KEY_INTROLENGTH);
			int idxLoopLength = result.getColumnIndex(KEY_LOOPLENGTH);
			
			track = new Track();
			track.setPath(result.getString(idxPath));
			track.setFilename(result.getString(idxFilename));
			track.setTrackNum(result.getInt(idxTrackNum));
			track.setTrackCount(result.getInt(idxTrackCount));
			track.setSong(result.getString(idxSong));
			track.setGame(result.getString(idxGame));
			track.setSystem(result.getString(idxSystem));
			track.setAuthor(result.getString(idxAuthor));
			track.setCopyright(result.getString(idxCopyright));
			track.setComment(result.getString(idxComment));
			track.setDumper(result.getString(idxDumper));
			track.setType(result.getInt(idxType));
			track.setTrackLength(result.getInt(idxTrackLength));
			track.setIntroLength(result.getInt(idxIntroLength));
			track.setLoopLength(result.getInt(idxLoopLength));

			track.setRowID((int)rowID);   
        }
        return track;
    }

    private static Cursor fetchTrack(long rowID) throws SQLException {
    	Cursor mCursor =
                mDb.query(true, DATABASE_TABLE_TRACKS, new String[] {KEY_ROWID, KEY_PATH,
                		KEY_FILENAME, KEY_TRACKNUM, KEY_TRACKCOUNT, KEY_TYPE, KEY_SONG,
                		KEY_GAME, KEY_SYSTEM, KEY_AUTHOR, KEY_COPYRIGHT, KEY_COMMENT, KEY_DUMPER,
                		KEY_TRACKLENGTH, KEY_INTROLENGTH, KEY_LOOPLENGTH}, 
                		KEY_ROWID + "=" + rowID, null,
                        null, null, null, null);
        mCursor.moveToLast();;
        return mCursor;

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
        return mDb.update(DATABASE_TABLE_TRACKS, args, KEY_ROWID + "=" + track.getRowID(), null) > 0;
    }
    
    private static int librarySize()
    {
    	String[] select = {KEY_ROWID};
    	Cursor mCursor = mDb.query(DATABASE_TABLE_TRACKS, select , null, null, null, null, null);
    	mCursor.moveToLast();
    	int count =  mCursor.getCount();    	
    	mCursor.close();
    	return count;
    }
}
