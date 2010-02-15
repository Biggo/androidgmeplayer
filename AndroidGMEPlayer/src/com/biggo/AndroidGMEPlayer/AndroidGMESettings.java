package com.biggo.AndroidGMEPlayer;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class AndroidGMESettings extends PreferenceActivity 
{    

	@Override
	protected void onCreate(Bundle savedInstanceState) {	      
		super.onCreate(savedInstanceState);
		PreferenceManager pm = this.getPreferenceManager();
		pm.setSharedPreferencesName(PlayerService.PLAYER_SETTINGS);
		pm.setSharedPreferencesMode(0);
		addPreferencesFromResource(R.layout.settings);
	}
	
	

}

