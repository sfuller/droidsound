/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_ssb_droidsound_utils_ID3Tag */

#ifndef _Included_com_ssb_droidsound_utils_ID3Tag
#define _Included_com_ssb_droidsound_utils_ID3Tag
#ifdef __cplusplus
extern "C" {
#endif
#undef com_ssb_droidsound_utils_ID3Tag_ID3INFO_GENRE
#define com_ssb_droidsound_utils_ID3Tag_ID3INFO_GENRE 100L
#undef com_ssb_droidsound_utils_ID3Tag_ID3INFO_COMMENT
#define com_ssb_droidsound_utils_ID3Tag_ID3INFO_COMMENT 101L
#undef com_ssb_droidsound_utils_ID3Tag_ID3INFO_ALBUM
#define com_ssb_droidsound_utils_ID3Tag_ID3INFO_ALBUM 102L
#undef com_ssb_droidsound_utils_ID3Tag_ID3INFO_TRACK
#define com_ssb_droidsound_utils_ID3Tag_ID3INFO_TRACK 103L
/*
 * Class:     com_ssb_droidsound_utils_ID3Tag
 * Method:    checkForTag
 * Signature: ([BII)I
 */
JNIEXPORT jint JNICALL Java_com_ssb_droidsound_utils_ID3Tag_checkForTag
  (JNIEnv *, jobject, jbyteArray, jint, jint);

/*
 * Class:     com_ssb_droidsound_utils_ID3Tag
 * Method:    parseTag
 * Signature: ([BII)Z
 */
JNIEXPORT jboolean JNICALL Java_com_ssb_droidsound_utils_ID3Tag_parseTag
  (JNIEnv *, jobject, jbyteArray, jint, jint);

/*
 * Class:     com_ssb_droidsound_utils_ID3Tag
 * Method:    openID3Tag
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_ssb_droidsound_utils_ID3Tag_openID3Tag
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_ssb_droidsound_utils_ID3Tag
 * Method:    closeID3Tag
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_ssb_droidsound_utils_ID3Tag_closeID3Tag
  (JNIEnv *, jobject);

/*
 * Class:     com_ssb_droidsound_utils_ID3Tag
 * Method:    getStringInfo
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_ssb_droidsound_utils_ID3Tag_getStringInfo
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_ssb_droidsound_utils_ID3Tag
 * Method:    getIntInfo
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_ssb_droidsound_utils_ID3Tag_getIntInfo
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_ssb_droidsound_utils_ID3Tag
 * Method:    getBinaryInfo
 * Signature: (I)[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_ssb_droidsound_utils_ID3Tag_getBinaryInfo
  (JNIEnv *, jobject, jint);

#ifdef __cplusplus
}
#endif
#endif
