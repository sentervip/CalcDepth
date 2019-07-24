#include <jni.h>
#include <string>
#include "config_macro.h"
#include "Z1_jni.hpp"
#include <fstream>
#include <opencv2/opencv.hpp>
#include <android/bitmap.h>
#ifdef __cplusplus
extern "C"{
#else;
#endif

/*fill bitmap with rgba8888*/
/*fill bitmap with rgba8888*/
static void fill_bitmap(AndroidBitmapInfo*  info, void *pixels, uint32_t * pdata,int pdataStride)
{
    uint32_t * pSrc = pdata;
    uint8_t * pDst = (uint8_t *) pixels;

    //LOG("bitmap: pixel=%p, pdata=%d,stride=%d,info->width=%d,info->height=%d\n", pixels, pdata,pdataStride,info->width,info->height); 
    for (int i = 0; i < info->height; i++)
    {
        for (int j = 0; j < info->width; j++)
        {
            pDst[j * 4] =     pSrc[j] >> 16 & 0xFF; //R
            pDst[j * 4 + 1] = pSrc[j] >> 8 & 0xFF; //G
            pDst[j * 4 + 2] = pSrc[j] & 0xFF; //B
            pDst[j * 4 + 3] = 0xFF;  //A
        }
        pSrc += info->width;
        pDst += info->width * 4;

    }
}
JNIEXPORT int JNICALL
Java_com_marvoto_fat_MeasureDepth_DrawBitmap(JNIEnv * env, jobject thiz, jobject bitmap, jintArray _byteArry)
{
    int iRet = 0;
    void *   pixels = NULL;
    AndroidBitmapInfo  info={0};
    jint * pByteArry = NULL;
    uint32_t *pOut;

    if(_byteArry == NULL){
        LOGE("BmDrawBitmap nullPtr,exit\n");
        return ENULL_PTR;
    }
    LOGL();
    if ((iRet = AndroidBitmap_getInfo( env,bitmap, &info)) < 0) {
        LOGE("BmAndroidBitmap_getInfo() failed ! error=%d", iRet);
        return EBUSY;
    }

    if ((iRet = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
        LOGE("BmAndroidBitmap_lockPixels() failed ! error=%d", iRet);
    }

    if(pixels){
        pByteArry = (jint*) env->GetIntArrayElements(_byteArry, 0);
        pOut = (unsigned int*)pByteArry;
    }

    if(pOut) {
        fill_bitmap(&info, pixels, pOut,info.width);
        iRet = ESUCCED;
    }else{
        iRet = EFAILURE;
    }

    AndroidBitmap_unlockPixels(env, bitmap);
    if(pByteArry){
        env->ReleaseIntArrayElements(_byteArry,pByteArry,0);
    }
    LOG("drawBitmap over");
    return iRet;
}
JNIEXPORT jstring JNICALL
Java_com_marvoto_fat_MeasureDepth_TestString(JNIEnv* env, jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
JNIEXPORT int JNICALL
Java_com_marvoto_fat_MeasureDepth_SaveBmp(JNIEnv* env, jobject thiz, jbyteArray buffer,
                                         int w, int h,jstring _pdir)
{
    int iRet;
    jbyte* img = NULL;
    const char*  pdir = env->GetStringUTFChars(_pdir,0);


    int length = env->GetArrayLength(buffer);
    img = env->GetByteArrayElements(buffer,NULL);
    if(img == NULL)
    {
        LOGE("ImgDspDataWrite GetByteArrayElements FAILED");
        return EINVALID;
    }
    LOG("name:%s, %d x %d,%x,%x,%x,%x\n",pdir,w,h,img[0],img[1],img[2],img[3]);
    cv::Mat data = cv::Mat(h,w,CV_8UC1,img);
    cv::imwrite(pdir,data);  LOGL();
    env->ReleaseByteArrayElements(buffer,img,0);
    return iRet;
}
#ifdef __cplusplus
}
#endif