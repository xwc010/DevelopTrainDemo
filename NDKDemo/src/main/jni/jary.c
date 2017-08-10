//
// Created by Administrator on 2017/8/10.
//

#include "xwc_com_ndkdemo_JniTest.h"

JNIEXPORT jstring JNICALL Java_xwc_com_ndkdemo_JniTest_getString(JNIEnv *env,jobject jobject1){
    return (*env)->NewStringUTF(env,"hello JNI!");
}