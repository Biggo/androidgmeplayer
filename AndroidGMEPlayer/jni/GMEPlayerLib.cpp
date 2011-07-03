
const char* lastError;
const char* lastTrackError;

#include "gme/Music_Emu.h"
#include <stdlib.h>
#include <stdio.h>
#include "string.h"
#include "com_biggo_AndroidGMEPlayer_PlayerLibs_GMEPlayerLib.h"
#include "com_biggo_AndroidGMEPlayer_PlayerLibs_GMETrackInfo.h"


Music_Emu* emu;
bool loaded = false;


JNIEXPORT jstring JNICALL Java_com_biggo_AndroidGMEPlayer_PlayerLibs_GMEPlayerLib_getLastError
  (JNIEnv * env, jobject obj)
{
	jstring str = env->NewStringUTF(lastError);
	return str;
}

JNIEXPORT jboolean JNICALL Java_com_biggo_AndroidGMEPlayer_PlayerLibs_GMEPlayerLib_initFile
  (JNIEnv * env, jobject obj, jstring file, jint track, jint rate)
{
	if(loaded)
	{
		delete emu;
		loaded = false;
	}

	const char* filename = env->GetStringUTFChars(file, NULL);

	long sample_rate = (long)rate; // number of samples per second

	// Determine file type
	gme_type_t file_type;
	lastError = gme_identify_file(filename, &file_type );
	if( lastError )
		return false;
	if ( !file_type )
	{
		lastError = "Unsupported music type";
		return false;//handle_error( "Unsupported music type" );
	}

	// Create emulator and set sample rate
	emu = file_type->new_emu();
	if ( !emu )
	{
		lastError = "Out of memory";
		return false;// handle_error( "Out of memory" );
	}

	lastError = emu->set_sample_rate( sample_rate ) ;
	if(lastError )
		return false;

	// Load music file into emulator
	lastError = emu->load_file(filename);
	if( lastError )
		return false;

	// Start track
	lastError = emu->start_track(track);
	if( lastError )
		return false;

	loaded = true;
	return loaded;
}

JNIEXPORT jlong JNICALL Java_com_biggo_AndroidGMEPlayer_PlayerLibs_GMEPlayerLib_getTime
  (JNIEnv * env, jobject obj)
{
	return (jlong)emu->tell();
}

JNIEXPORT jshortArray JNICALL Java_com_biggo_AndroidGMEPlayer_PlayerLibs_GMEPlayerLib_getSample
  (JNIEnv * env, jobject obj, jlong size)
{
	short buf [size];
	jshortArray arr = env->NewShortArray(size);
	//lastError = emu->play(size, buf);
	emu->play(size, buf);
	env->SetShortArrayRegion(arr, 0, size, buf);
	return arr;
}

JNIEXPORT jboolean JNICALL Java_com_biggo_AndroidGMEPlayer_PlayerLibs_GMEPlayerLib_cleanup
  (JNIEnv * env, jobject obj)
{
	if(emu != NULL)
	{
		delete emu;
		emu = NULL;
	}
	return true;
}

JNIEXPORT jboolean JNICALL Java_com_biggo_AndroidGMEPlayer_PlayerLibs_GMEPlayerLib_startTrack
  (JNIEnv * env, jobject obj, jint track)
{
	lastError = emu->start_track( track );
	bool result = (bool)lastError;
	return !result;
}

JNIEXPORT jint JNICALL Java_com_biggo_AndroidGMEPlayer_PlayerLibs_GMEPlayerLib_getTrack
  (JNIEnv * env, jobject obj)
{
	return emu->current_track();
}

JNIEXPORT jboolean JNICALL Java_com_biggo_AndroidGMEPlayer_PlayerLibs_GMEPlayerLib_seekTo
  (JNIEnv * env, jobject obj, jlong pos)
{
	lastError = emu->seek(pos);
	bool result = (bool)lastError;
	return !result;
}

JNIEXPORT jboolean JNICALL Java_com_biggo_AndroidGMEPlayer_PlayerLibs_GMEPlayerLib_skip
  (JNIEnv * env, jobject obj, jlong samples)
{
	lastError = emu->skip(samples);
	bool result = (bool)lastError;
	return !result;
}

JNIEXPORT jboolean JNICALL Java_com_biggo_AndroidGMEPlayer_PlayerLibs_GMEPlayerLib_isTrackEnded
  (JNIEnv * env, jobject obj)
{
	return emu->track_ended();
}

JNIEXPORT void JNICALL Java_com_biggo_AndroidGMEPlayer_PlayerLibs_GMEPlayerLib_setFade
  (JNIEnv * env, jobject obj, jlong start_msec, jlong length_msec)
{
	emu->set_fade(start_msec,length_msec);
}

JNIEXPORT jobjectArray JNICALL Java_com_biggo_AndroidGMEPlayer_PlayerLibs_GMEPlayerLib_getCurrentTrackInfo
  (JNIEnv * env, jobject obj)
{
	track_info_t  trackInfo;
	lastError = emu->track_info(&trackInfo);
	if(lastError)
		return NULL;

	char info [256];

	jobjectArray result = NULL;
	jclass stringClass = env->FindClass("java/lang/String");
	result = env->NewObjectArray(11, stringClass, NULL); //create an array for the 1st dimension

	sprintf(info, "%d", trackInfo.track_count);
	env->SetObjectArrayElement(result, 0, env->NewStringUTF(info));
	sprintf(info, "%d", trackInfo.length);
	env->SetObjectArrayElement(result, 1, env->NewStringUTF(info));
	sprintf(info, "%d", trackInfo.intro_length);
	env->SetObjectArrayElement(result, 2, env->NewStringUTF(info));
	sprintf(info, "%d", trackInfo.loop_length);
	env->SetObjectArrayElement(result, 3, env->NewStringUTF(info));
	env->SetObjectArrayElement(result, 4, env->NewStringUTF(trackInfo.song));
	env->SetObjectArrayElement(result, 5, env->NewStringUTF(trackInfo.game));
	env->SetObjectArrayElement(result, 6, env->NewStringUTF(trackInfo.system));
	env->SetObjectArrayElement(result, 7, env->NewStringUTF(trackInfo.author));
	env->SetObjectArrayElement(result, 8, env->NewStringUTF(trackInfo.copyright));
	env->SetObjectArrayElement(result, 9, env->NewStringUTF(trackInfo.comment));
	env->SetObjectArrayElement(result, 10, env->NewStringUTF(trackInfo.dumper));

	return result;
}

JNIEXPORT void JNICALL Java_com_biggo_AndroidGMEPlayer_PlayerLibs_GMEPlayerLib_setTempo
  (JNIEnv * env, jobject obj, jdouble tempo)
{
	emu->set_tempo(tempo);
}


JNIEXPORT jobjectArray JNICALL Java_com_biggo_AndroidGMEPlayer_PlayerLibs_GMETrackInfo_getFileInfo
(JNIEnv * env, jobject obj, jstring file, jint track)
{
	const char* filename = env->GetStringUTFChars(file, NULL);

	gme_type_t file_type;
	lastTrackError = gme_identify_file(filename, &file_type );
	if( lastTrackError )
		return NULL;
	if ( !file_type )
	{
		lastTrackError = "Unsupported music type ";
		return NULL;//handle_error( "Unsupported music type" );
	}

	Music_Emu* fileInfo = file_type->new_info();
	if ( !fileInfo )
	{
		lastTrackError = "Out of memory";
		delete fileInfo;
		return NULL;// handle_error( "Out of memory" );
	}

	// Load music file into emulator
	lastTrackError = fileInfo->load_file(filename);
	if( lastTrackError )
	{
		delete fileInfo;
		return NULL;
	}

	track_info_t  trackInfo;
	lastTrackError = fileInfo->track_info(&trackInfo, track);
	if(lastTrackError)
	{
		delete fileInfo;
		return NULL;
	}

	char info [256];

	jobjectArray result = NULL;
	jclass stringClass = env->FindClass("java/lang/String");
	result = env->NewObjectArray(11, stringClass, NULL); //create an array for the 1st dimension

	sprintf(info, "%d", trackInfo.track_count);
	env->SetObjectArrayElement(result, 0, env->NewStringUTF(info));
	sprintf(info, "%d", trackInfo.length);
	env->SetObjectArrayElement(result, 1, env->NewStringUTF(info));
	sprintf(info, "%d", trackInfo.intro_length);
	env->SetObjectArrayElement(result, 2, env->NewStringUTF(info));
	sprintf(info, "%d", trackInfo.loop_length);
	env->SetObjectArrayElement(result, 3, env->NewStringUTF(info));
	env->SetObjectArrayElement(result, 4, env->NewStringUTF(trackInfo.song));
	env->SetObjectArrayElement(result, 5, env->NewStringUTF(trackInfo.game));
	env->SetObjectArrayElement(result, 6, env->NewStringUTF(trackInfo.system));
	env->SetObjectArrayElement(result, 7, env->NewStringUTF(trackInfo.author));
	env->SetObjectArrayElement(result, 8, env->NewStringUTF(trackInfo.copyright));
	env->SetObjectArrayElement(result, 9, env->NewStringUTF(trackInfo.comment));
	env->SetObjectArrayElement(result, 10, env->NewStringUTF(trackInfo.dumper));

	delete fileInfo;

	return result;
}


JNIEXPORT jstring JNICALL  Java_com_biggo_AndroidGMEPlayer_PlayerLibs_GMETrackInfo_getLastError
(JNIEnv * env, jobject obj)
{
	jstring str = env->NewStringUTF(lastTrackError);
	return str;
}







