#include <android/log.h>
#include <time.h>
#include "com_zld_photo_DecodeManager.h"
#include "tcpclient.h"
#include <stdio.h>
#include "tcpclient.h"
#include "string.h"
#include "tools.h"

static JNIEnv * g_env;
static jobject g_pMainAct;
static int bmpWidth = 702;
static int bmpHeight = 288;
static jobject resultBitmap;
static void *pBmpResultImgBuf;
jobject createBitmap(JNIEnv *pEnv, int pWidth, int pHeight) {
	int i;
	//get Bitmap class and createBitmap method ID
	jclass javaBitmapClass = (jclass)(*pEnv)->FindClass(pEnv, "android/graphics/Bitmap");
	jmethodID mid = (*pEnv)->GetStaticMethodID(pEnv, javaBitmapClass, "createBitmap", "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");
	//create Bitmap.Config
	//reference: https://forums.oracle.com/thread/1548728
	const wchar_t* configName = L"ARGB_8888";
	int len = wcslen(configName);
	jstring jConfigName;
	if (sizeof(wchar_t) != sizeof(jchar)) {
		//wchar_t is defined as different length than jchar(2 bytes)
		jchar* str = (jchar*)malloc((len+1)*sizeof(jchar));
		for (i = 0; i < len; ++i) {
			str[i] = (jchar)configName[i];
		}
		str[len] = 0;
		jConfigName = (*pEnv)->NewString(pEnv, (const jchar*)str, len);
	} else {
		//wchar_t is defined same length as jchar(2 bytes)
		jConfigName = (*pEnv)->NewString(pEnv, (const jchar*)configName, len);
	}
	jclass bitmapConfigClass = (*pEnv)->FindClass(pEnv, "android/graphics/Bitmap$Config");
	jobject javaBitmapConfig = (*pEnv)->CallStaticObjectMethod(pEnv, bitmapConfigClass,
			(*pEnv)->GetStaticMethodID(pEnv, bitmapConfigClass, "valueOf", "(Ljava/lang/String;)Landroid/graphics/Bitmap$Config;"), jConfigName);
	//create the bitmap
	return (*pEnv)->CallStaticObjectMethod(pEnv, javaBitmapClass, mid, pWidth, pHeight, javaBitmapConfig);
}

char*   Jstring2CStr(JNIEnv*   env,   jstring   jstr)
{
	 char*   rtn   =   NULL;
	 jclass   clsstring   =   (*env)->FindClass(env,"java/lang/String"); //String
	 jstring   strencode   =   (*env)->NewStringUTF(env,"GB2312");  // 得到一个java字符串 "GB2312"
	 jmethodID   mid   =   (*env)->GetMethodID(env,clsstring,   "getBytes",   "(Ljava/lang/String;)[B"); //[ String.getBytes("gb2312");
	 jbyteArray   barr=   (jbyteArray)(*env)->CallObjectMethod(env,jstr,mid,strencode); // String .getByte("GB2312");
	 jsize   alen   =   (*env)->GetArrayLength(env,barr); // byte数组的长度
	 jbyte*   ba   =   (*env)->GetByteArrayElements(env,barr,JNI_FALSE);
	 if(alen   >   0)
	 {
	  rtn   =   (char*)malloc(alen+1);         //"\0"
	  memcpy(rtn,ba,alen);
	  rtn[alen]=0;
	 }
	 (*env)->ReleaseByteArrayElements(env,barr,ba,0);  //
	 return rtn;
}

jstring stoJstring(JNIEnv* env, const char* pat)
{
	jstring result;
	jclass strClass = (*env)->FindClass(env,"java/lang/String");
	jmethodID ctorID = (*env)->GetMethodID(env,strClass, "<init>", "([BLjava/lang/String;)V");
	jbyteArray bytes = (*env)->NewByteArray(env,strlen(pat));
	(*env)->SetByteArrayRegion(env,bytes, 0, strlen(pat), (jbyte*)pat);
	jstring encoding = (*env)->NewStringUTF(env,"GB2312");
	result = (jstring)(*env)->NewObject(env, strClass, ctorID, bytes, encoding);
	(*env)->DeleteLocalRef(env,bytes);
	(*env)->DeleteLocalRef(env,encoding);
	(*env)->DeleteLocalRef(env,strClass);
	(*env)->DeleteLocalRef(env,ctorID);
	return result;

}
jbyteArray firstMacArray;
jbyteArray secondMacArray;
char szFilename[200];
jbyte *carNum;
jbyte *img;
jstring ip;
jbyte *tem;
jbyte *imgTem;
void SaveResult(JNIEnv *pEnv, jobject pObj, char* cameraIp, char *pImg, int imgLength,  char *pRecoResult, int left, int right, int top, int bottom, int resType, int carPlateColor, int nType) {

	jmethodID sSaveFrameMID;
	jclass mainActCls;
	 firstMacArray = (*pEnv)->NewByteArray(pEnv, 256 );
	 secondMacArray = (*pEnv)->NewByteArray(pEnv, imgLength);
	carNum = (*pEnv)->GetByteArrayElements(pEnv, firstMacArray, 0);
	img = (*pEnv)->GetByteArrayElements(pEnv, secondMacArray, 0);
	 ip = stoJstring(pEnv, cameraIp);
	int i = 0;
	tem = pRecoResult;
	for ( i = 0; i < strlen(pRecoResult); i++ )
	{
		carNum[i] = tem[i];
	}
	//LOGI("imgLength = %d", imgLength );
	imgTem = pImg;
	for(i = 0; i < imgLength; i++)
	{
		img[i] = imgTem[i];
	}

	(*pEnv)->SetByteArrayRegion(pEnv, firstMacArray, 0, 256, carNum );
	(*pEnv)->SetByteArrayRegion(pEnv, secondMacArray, 0, imgLength, img );

	mainActCls = (*pEnv)->GetObjectClass(pEnv, pObj);
	sSaveFrameMID = (*pEnv)->GetMethodID(pEnv, mainActCls, "yitijiSaveResult", "(Ljava/lang/String;[B[BIIIIIII)V");
	(*pEnv)->CallVoidMethod(pEnv, pObj, sSaveFrameMID, ip, firstMacArray, secondMacArray, bottom - top + 1, right - left + 1, left, top, resType, carPlateColor, nType);
	(*pEnv)->ReleaseByteArrayElements(pEnv, firstMacArray, carNum, 0);
	(*pEnv)->ReleaseByteArrayElements(pEnv, secondMacArray, img, 0);
	(*pEnv)->DeleteLocalRef(pEnv, ip);
	(*pEnv)->DeleteLocalRef(pEnv,mainActCls);
	(*pEnv)->DeleteLocalRef(pEnv,sSaveFrameMID);
	(*pEnv)->DeleteLocalRef(pEnv,firstMacArray);
	(*pEnv)->DeleteLocalRef(pEnv,secondMacArray);
	(*pEnv)->DeleteLocalRef(pEnv,carNum);
	(*pEnv)->DeleteLocalRef(pEnv,img);
	(*pEnv)->DeleteLocalRef(pEnv,tem);
	(*pEnv)->DeleteLocalRef(pEnv,imgTem);
	(*pEnv)->DeleteLocalRef(pEnv,szFilename);
}

/**
 *param： nType车牌类别，警车，武警车等等
 */
void YitijiSaveResultCB(unsigned char* cameraIp, unsigned char *pImg, int imgLength, char *pRecoResult, int left, int right, int top, int bottom, int resType, int carPlateColor, int nType)
{
	/*LOGI("carplate is %s", pRecoResult);
	NV16ToARGB(pImg, pBmpResultImgBuf, bmpWidth, bmpHeight);*/
	SaveResult(g_env, g_pMainAct, cameraIp, pImg, imgLength, pRecoResult, left, right, top, bottom, resType, carPlateColor, nType);
}

void openCameraRetCB()
{
	jclass mainActCls;
	jmethodID sSaveFrameMID;

	mainActCls = (*g_env)->GetObjectClass(g_env, g_pMainAct);
	sSaveFrameMID = (*g_env)->GetMethodID(g_env, mainActCls, "openCameraSuccess", "(I)V");
	(*g_env)->CallVoidMethod(g_env, g_pMainAct, sSaveFrameMID, 1);
	(*g_env)->DeleteLocalRef(g_env,mainActCls);
}

void keepAliveRetCB()
{
	jclass mainActCls;
	jmethodID sSaveFrameMID;

	mainActCls = (*g_env)->GetObjectClass(g_env, g_pMainAct);
	sSaveFrameMID = (*g_env)->GetMethodID(g_env, mainActCls, "getKeepAlive", "(I)V");
	(*g_env)->CallVoidMethod(g_env, g_pMainAct, sSaveFrameMID, 1);
	(*g_env)->DeleteLocalRef(g_env,mainActCls);
}

JNIEXPORT jstring JNICALL Java_com_zld_photo_DecodeManager_runYitiji
  (JNIEnv * env, jclass object, jobject pMainAct, jstring jstr)
{
	char *cameraIp;
	int ret = 0;
	g_env = env;
    g_pMainAct = pMainAct;


	cameraIp = Jstring2CStr(env, jstr);
	LOGI("Java_com_zld_photo_DecodeManager_runYitiji cameraIP = %s", cameraIp);
	ret = yitijiRun(cameraIp, YitijiSaveResultCB, openCameraRetCB, keepAliveRetCB);
	if (ret == -1)
	{
		LOGI("111111111111111111111111111111111111 cameraIP = %s", cameraIp);
		return (*env)->NewStringUTF(env, "recvfailed");
	}
	else
	{
		LOGI("222222222222222222222222222222222222 cameraIP = %s", cameraIp);
		return (*env)->NewStringUTF(env, "connecterro");
	}
}

JNIEXPORT jstring JNICALL Java_com_zld_photo_DecodeManager_controlPole
  (JNIEnv * env, jclass object, jint x, jstring jstr)
{
	char* cameraIp;
	cameraIp = Jstring2CStr(env, jstr);
	ctlPole(x, cameraIp);
	LOGI("==================x = %d", x);
}

JNIEXPORT jstring JNICALL Java_com_zld_photo_DecodeManager_getOneImg
  (JNIEnv * env, jclass object, jstring jstr)
{
	LOGI("@@@@@@@@@@@@@getOneImg@@@@@@@@@@@@@");
	char* cameraIp;
	cameraIp = Jstring2CStr(env, jstr);
	int res = 0;
	res = getOneImg(cameraIp);
	if (res == 0){
		return (*env)->NewStringUTF(env, "success");
	}else if (res == -1){
		return (*env)->NewStringUTF(env, "fail");
	}
}

JNIEXPORT jstring JNICALL Java_com_zld_photo_DecodeManager_stopYitiji
  (JNIEnv * env, jclass object)
{
	stop();
}

JNIEXPORT jint JNICALL Java_com_zld_photo_DecodeManager_getConfidenceLevel
(JNIEnv *env, jclass object)
{
	return getConfidenceLevel();
}

JNIEXPORT void JNICALL Java_com_zld_photo_DecodeManager_setConfidenceLevel
(JNIEnv *env, jclass object, jint level)
{
	setConfidenceLevel(level);
}

