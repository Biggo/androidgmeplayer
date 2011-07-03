package com.biggo.AndroidGMEPlayer.PlayerLibs;

import com.biggo.AndroidGMEPlayer.Track;

public class PlayerLibFactory {

	public static IPlayerLib getPlayerLib(int type)
	{
		switch(type) 
		{
			case Track.TYPE_AY : return new GMEPlayerLib();
			case Track.TYPE_GBS : return new GMEPlayerLib();
			case Track.TYPE_GYM : return new GMEPlayerLib();
			case Track.TYPE_HES : return new GMEPlayerLib();
			case Track.TYPE_KSS : return new GMEPlayerLib();
			case Track.TYPE_NSF : return new GMEPlayerLib();
			case Track.TYPE_NSFE : return new GMEPlayerLib();
			case Track.TYPE_SAP : return new GMEPlayerLib();
			case Track.TYPE_SPC : return new GMEPlayerLib();
			case Track.TYPE_VGM : return new GMEPlayerLib();
			case Track.TYPE_PSF : return new SexypsfPlayerLib();
			default : return null;
		}
	}
}
