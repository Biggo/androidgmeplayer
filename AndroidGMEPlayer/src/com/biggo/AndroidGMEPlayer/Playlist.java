package com.biggo.AndroidGMEPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Playlist {	
	
public static final int TYPE_ALL = 0;
public static final int TYPE_GAME = 1;
public static final int TYPE_SYSTEM = 2;
public static final int TYPE_PLAYLIST = 3;
	
private ArrayList<Track> list;
private boolean random;
private ArrayList<Integer> randomOrder;
private ArrayList<Integer> availablePicks;
private ArrayList<String> songs;
private int currentTrack;
private int randIdx;
private int size;
private Random rand;
private int type;
private String tag;

	public Playlist()
	{
		list = new ArrayList<Track>();
		songs = new ArrayList<String>();
		random = false;
		randomOrder = new ArrayList<Integer>();
		availablePicks = new ArrayList<Integer>();
		currentTrack = 0;
		randIdx = 0;
		size = 0;
		rand = new Random();
		type = -1;
		tag = "";
	}
	
	public Track getTrack(int idx)
	{
		if(idx >= 0 && idx < size)
			return list.get(idx);
		else
			return null;		
	}
	
	public boolean addTrack(Track track)
	{
		if(list.add(track) && songs.add(track.toString()))
		{
			randomOrder.add(Integer.valueOf(-1));
			availablePicks.add(Integer.valueOf(size));
			size++;
			return true;
		}
		else
		{
			return false;
		}		
	}
	
	public boolean removeTrack(int idx)
	{
		if(idx > 0 && idx < size)
		{
			if(list.remove(idx) != null && songs.remove(idx) != null)
			{
				int randPos = randomOrder.indexOf(Integer.valueOf(idx));
				if(randPos != -1 )
					randomOrder.remove(randPos);
				else
					randomOrder.remove(randomOrder.size() - 1);	
				
				int available = availablePicks.indexOf(Integer.valueOf(idx));
				if (available != -1)
				{
					availablePicks.remove(available);
					randIdx--;
				}
				size--;
				if(idx == currentTrack)
					getPreviousTrack();
				return true;
			}
		}
		return false;		
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
						currentTrack = randomOrder.get(randIdx);
					}
					else
					{
						int availableSize = availablePicks.size();
						int pickIdx = rand.nextInt(availableSize);
						currentTrack = availablePicks.get(pickIdx);
						randomOrder.add(currentTrack);
						availablePicks.remove(pickIdx);						
					}
				}
				else
				{
					randIdx++;
		    		if(randIdx >= randSize)
		    			randIdx = 0;	
					currentTrack = randomOrder.get(randIdx);				
				}
			}
			else
			{
				currentTrack++;
	    		if(currentTrack >= size)
	    			currentTrack = 0;
			}
			return list.get(currentTrack);
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
						currentTrack = randomOrder.get(randIdx);
					}
					else
					{
						int availableSize = availablePicks.size();
						int pickIdx = rand.nextInt(availableSize);
						currentTrack = availablePicks.get(pickIdx);
						randomOrder.add(currentTrack);
						randIdx = randomOrder.size() - 1;
						availablePicks.remove(pickIdx);						
					}
				}
				else
				{
					randIdx--;
		    		if(randIdx < 0)
		    			randIdx = randSize;	
					currentTrack = randomOrder.get(randIdx);				
				}
			}
			else
			{
        		currentTrack--;
        		if(currentTrack < 0)
        			currentTrack = size - 1;
			}
			return list.get(currentTrack);
		}
		else
			return null;		
	}
	
	public Track getCurrentTrack()
	{
		if(size > 0)
			return list.get(currentTrack);
		else
			return null;				
	}
	
	public int getCurrentTrackIdx()
	{
		return currentTrack;
	}
	
	public boolean setCurrentTrack(int idx)
	{
		if(idx > 0 && idx < size)
		{
			currentTrack = idx;
			return true;
		}
		return false;
	}
	
	public int getSize()
	{
		return list.size();
	}
	
	public List<String> getSongs()
	{
		return songs;
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
			CharSequence tagSeq = tag.subSequence(0, tag.length());
			return other.tag.contentEquals(tagSeq) && other.type == type;
		}
		else
		{
			return super.equals(o);
		}
	}
}
