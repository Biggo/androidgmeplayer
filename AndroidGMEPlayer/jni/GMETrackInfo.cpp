const char* lastTrackError;

#include "gme/Music_Emu.h"
#include <stdlib.h>
#include <stdio.h>
#include "string.h"
#include "com_biggo_AndroidGMEPlayer_PlayerLibs_GMETrackInfo.h"

JNIEXPORT jobjectArray JNICALL Java_com_biggo_AndroidGMEPlayer_PlayerLibs_GMEPlayerLib_GMETrackInfo_getFileInfo
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


JNIEXPORT jstring JNICALL  Java_com_biggo_AndroidGMEPlayer_PlayerLibs_GMEPlayerLib_GMETrackInfo_getLastError
(JNIEnv * env, jobject obj)
{
	jstring str = env->NewStringUTF(lastTrackError);
	return str;
}
