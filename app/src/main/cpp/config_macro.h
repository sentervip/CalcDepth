#ifndef _CONFIG_MACRO_H_
#define _CONFIG_MACRO_H_

#include <stdio.h>
#include <iostream>
#include <android/log.h>

#define   APP_VERSION   "V010SP01B160628R71"
#define nullptr         NULL
#define LOG(...)     __android_log_print(ANDROID_LOG_INFO,"MAVT",__VA_ARGS__)
#define LOGE(...)    __android_log_print(ANDROID_LOG_ERROR,"MAVT",__VA_ARGS__)
#define LOGL(...)   LOG("L=%d\n",__LINE__);

//Error Code
#define ESUCCED    (1)
#define EFAILURE   (0)
#define EFAILED    (-1)
#define EBUSY      (-50)
#define ENULL_PTR  (-51)
#define EINVALID   (-52)
#define EOVERFLOW  (-53)
#define EBAD_ALLOC  (-54)

#endif