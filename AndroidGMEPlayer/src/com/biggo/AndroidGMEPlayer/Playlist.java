package com.biggo.AndroidGMEPlayer;

import java.util.ArrayList;
import java.util.Random;

import android.database.Cursor;

public class Playlist {	
	
public static final int TYPE_ALL = 0;
public static final int TYPE_GAME = 1;
public static final int TYPE_SYSTEM = 2;
public static final int TYPE_PLAYLIST = 3;
	
private Cursor list;
private boolean random;
private ArrayList<Integer> randomOrder;
private ArrayList<Integer> availablePicks;
private int currentIdx;
private Track currentTrack;
private int randIdx;
private int size;
private Random rand;
private int type;
private String tag;

public Playlist(Cursor result)
	{
		list = result;
		list.moveToLast();
		list.getColumnIndex(Library.KEY_ROWID);
		random = false;
		randomOrder = new ArrayList<Integer>();
		availablePicks = new ArrayList<Integer>();
		randIdx = 0;
		size = list.getCount();
		rand = new Random();
		type = -1;
		tag = "";
		this.setCurrentTrack(0);
	}
	
	public Track getNextTrack()
	{
		if(size > 0)
		{			
			if(random)
			{
				int randSize = randomOrder.size();
				if(randSize != size)
				{
					randIdx++;
					if(randIdx < randomOrder.size())
					{
						currentIdx = randomOrder.get(randIdx);
					}
					else
					{
						int availableSize = availablePicks.size();
						int pickIdx = rand.nextInt(availableSize);
						currentIdx = availablePicks.get(pickIdx);
						randomOrder.add(currentIdx);
						availablePicks.remove(pickIdx);						
					}
				}
				else
				{
					randIdx++;
		    		if(randIdx >= randSize)
		    			randIdx = 0;	
					currentIdx = randomOrder.get(randIdx);				
				}
			}
			else
			{
				currentIdx++;
	    		if(currentIdx >= size)
	    			currentIdx = 0;
			}
			this.setCurrentTrack(currentIdx);
			return currentTrack;
		}
		else
			return null;
		
	}
	
	public Track getPreviousTrack()
	{
		if(size > 0)
		{
			if(random)
			{
				int randSize = randomOrder.size();
				if(randSize != size)
				{
					randIdx--;
					if(randIdx >= 0)
					{
						currentIdx = randomOrder.get(randIdx);
					}
					else
					{
						int availableSize = availablePicks.size();
						int pickIdx = rand.nextInt(availableSize);
						currentIdx = availablePicks.get(pickIdx);
						randomOrder.add(currentIdx);
						randIdx = randomOrder.size() - 1;
						availablePicks.remove(pickIdx);						
					}
				}
				else
				{
					randIdx--;
		    		if(randIdx < 0)
		    			randIdx = randSize - 1;	
					currentIdx = randomOrder.get(randIdx);				
				}
			}
			else
			{
        		currentIdx--;
        		if(currentIdx < 0)
        			currentIdx = size - 1;
			}
    		this.setCurrentTrack(currentIdx);
			return currentTrack;
		}
		else
			return null;		
	}
	
	public Track getCurrentTrack()
	{
		if(size > 0)
			return currentTrack;
		else
			return null;				
	}
	
	public int getCurrentTrackIdx()
	{
		return currentIdx;
	}
	
	public boolean setCurrentTrack(int idx)
	{
		if(idx >= 0 && idx < size)
		{
			currentIdx = idx;
			list.moveToPosition(idx);
			currentTrack = Library.getTrack(list);
			return true;
		}
		return false;
	}
	
	public int getSize()
	{
		return size;
	}
	
	public Cursor getSongs()
	{
		return list;
	}
	
	public boolean getRandomMode()
	{
		return random;
	}
	
	public void setRandomMode(boolean random)
	{
		if(!this.random && random)
		{
			randomOrder.clear();
			availablePicks.clear();
			randIdx = 0;
			for(int i = 0; i < size; i++)
				availablePicks.add(Integer.valueOf(i));
		}
		this.random = random;
	}
	
	public int getType()
	{
		return type;
	}
	
	public void setType(int type)
	{
		this.type = type;
	}
	
	public String getTag()
	{
		return tag;
	}
	
	public void setTag(String tag)
	{
		this.tag = tag;
	}
	
	public boolean equals(Object o)
	{
		if(o instanceof Playlist)
		{
			Playlist other = (Playlist)o;
			return list.equals((Object)other.getSongs());
		}
		else
		{
			return super.equals(o);
		}
	}
}
