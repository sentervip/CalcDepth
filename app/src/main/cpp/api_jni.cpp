#include <jni.h>
#include <string>
#include "config_macro.h"
#include "api_jni.hpp"
#include <fstream>
#include <opencv2/opencv.hpp>
#include <android/bitmap.h>
#include "counter.hpp"
#ifdef __cplusplus
extern "C"{
#else
#endif
uint8_t  pOut[640*480*3];

/*fill bitmap with rgba8888*/
static void fill_bitmap(AndroidBitmapInfo*  info, void* pixels, uint8_t* pdata,int pdataStride)
{
    uint8_t * pSrc = pdata;
    uint8_t * pDst = (uint8_t *) pixels;
    int step1,step2;

    //LOG("bitmap: pixel=%p, pdata=%d,stride=%d,info->width=%d,info->height=%d\n", pixels, pdata,pdataStride,info->width,info->height); 
    for (int i = 0; i < info->height; i++)
    {
        for (int j = 0; j < info->width; j++)
        {
            pDst[step2] =  pSrc[step1+2]; //R
            pDst[step2+1] = pSrc[step1+1]; //G
            pDst[step2+ 2] =pSrc[step1]; //B
            pDst[step2+ 3] = 0xFF;  //A
            step1 = j*3;
            step2 = j*4;

        }
        pSrc += info->width*3;
        pDst += info->width*4;
    }
}
JNIEXPORT void JNICALL
Java_com_marvoto_fat_MeasureDepth_ParaSet(JNIEnv * env, jobject thiz, float SampleRate, float OscFre)
{
    ParaSet(SampleRate,OscFre );
}

JNIEXPORT float JNICALL
Java_com_marvoto_fat_MeasureDepth_DrawBitmap(JNIEnv * env, jobject thiz,  jobject bitmap, jbyteArray _byteArry)
{
    float iRet = 0;
    void *   pixels = NULL;
    AndroidBitmapInfo  info={0};
    jbyte * pByteArry = NULL;

    if(_byteArry == NULL){
        LOGE("BmDrawBitmap nullPtr,exit\n");
        return ENULL_PTR;
    }
    if ((iRet = AndroidBitmap_getInfo( env,bitmap, &info)) < 0) {
        LOGE("BmAndroidBitmap_getInfo() failed ! error=%d", iRet);
        return EBUSY;
    }

    if ((iRet = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
        LOGE("BmAndroidBitmap_lockPixels() failed ! error=%d", iRet);
    }

    if(pixels){
        pByteArry = (jbyte *) env->GetByteArrayElements(_byteArry, 0);
    }

    if(pByteArry) {
        //memset(pOut,0, sizeof(pOut));
        iRet = measure(info.width,info.height,(uint8_t *)pByteArry,pOut);
        if(iRet < 0){
            goto fail;
        }
        fill_bitmap(&info, pixels, pOut,info.width);
        //iRet = ESUCCED;
    }else{
        iRet = EFAILURE;
    }

fail:
    AndroidBitmap_unlockPixels(env, bitmap);
    if(pByteArry){
        env->ReleaseByteArrayElements(_byteArry,pByteArry,0);
    }
    LOG("drawBitmap over");
    return iRet;
}
JNIEXPORT jstring JNICALL
Java_com_marvoto_fat_MeasureDepth_TestString(JNIEnv* env, jobject /* this */) {
    std::string hello = "Hello from C++";
    //for(int i=0;i<50;i++) {
       // measure();
     //   LOG("i=%d",i);
    //}
    return env->NewStringUTF(hello.c_str());
}
JNIEXPORT int JNICALL
Java_com_marvoto_fat_MeasureDepth_SaveBmp(JNIEnv* env, jobject thiz, jbyteArray buffer,
                                         int w, int h,jstring _pdir)
{
    int iRet;
    jbyte* img = NULL;
    const char*  pdir = env->GetStringUTFChars(_pdir,0);

#if 1
    unsigned char Buf[120];
    ifstream fp("/sdcard/1/2.txt", ios::in|ios::binary);
    if(fp.bad())
    {
        LOGE("read 1.txt: open failed\n");
        return NULL;
    }
   fp.read((char*)Buf, 120);
   fp.close();
   LOG("read 1.txt ok,%s",Buf);
#endif

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