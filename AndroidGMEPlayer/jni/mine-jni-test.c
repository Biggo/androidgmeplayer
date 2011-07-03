/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
#include <string.h>
#include <jni.h>
#include "driver.h"
/* This is a trivial JNI example where we use a native method
 * to return a new VM String. See the corresponding Java source
 * file located at:
 *
 *   apps/samples/hello-jni/project/src/com/example/HelloJni/HelloJni.java
 */

PSFINFO *PSFInfo=NULL;
jstring
Java_mine_test_jnitest_MineJNITest_minejnifunction( JNIEnv* env,
                                                  jobject thiz )
{
	if((PSFInfo = sexy_load("1.psf"))!=NULL)
		return (*env)->NewStringUTF(env, "Open 1.psf success!!!");
	else
		return (*env)->NewStringUTF(env, "Open 1.psf fail!!!");
}
