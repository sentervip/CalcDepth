#include "counter.hpp"
#include "fstream"
#include <iostream>
#include <string>
#include <strstream>
#include "math.h"
#include <stdlib.h>
#include <algorithm>
#include <stdio.h>
#include "config_macro.h"

#define  WIN_W    20
#define  WIN_H    10
static float m_c0,m_osc;
StrCandicateTag Candicate;
vector<StrCandicateTag> g_StrCandicateTag;
vector<StrCandicateTag> * pStrCandicateTag = &g_StrCandicateTag;

void ParaSet(float SampleRate, float OscFre)
{
    m_c0 = SampleRate;
    m_osc = OscFre;
}
int SortCmp(StrCandicateTag a1, StrCandicateTag a2)
{
	return a1.StandDev < a2.StandDev;
}

//用deque<Point> 描述曲线
//随机取色画曲线
Scalar random_color(RNG& _rng)
{
	int icolor = (unsigned)_rng;
	return Scalar(icolor & 0xFF, (icolor >> 8) & 0xFF, (icolor >> 16) & 0xFF);
}
int SliderWin(Mat src,cv::Point2i pt, int w,int h, StrCandicateTag *pData)
{	
	cv::Scalar     mean;  
    cv::Scalar     dev; 
	cv::Rect2i rect(pt.x, pt.y, w,h);
	if(src.rows < rect.y || src.cols < rect.x){
		printf("roi invalid\n");
	    return -1;
	}
	Mat Roi = src(rect);
    cv::meanStdDev ( Roi, mean, dev );  
    pData->MeanValue = mean.val[0];  
    pData->StandDev = dev.val[0];  
    std::cout << pData->MeanValue << ",\t" << pData->StandDev; 
	return 0;
}
float measure(int width,int height,uint8_t * pSrc ,uint8_t* pOut )
{
    int i,iRet = 0;
	char dist[40];
    int mc_valid[300] = {1};
    RNG g_rng(12345);
    Mat g_cannyMat_output;
    cv::Point2i pt;
    vector<Vec4i> g_vHierarchy;
    //Mat img = imread("/sdcard/1/1.jpg", 1);
    Mat gray = Mat(height,width,CV_8UC1,pSrc);
    cv::GaussianBlur(gray,gray,cv::Size(15,15),1,1);

//    Mat gray = Mat::zeros(img.size(),CV_8UC1);
//    for ( i = 0; i < height; i++)
//    {
//        for (int j = 0; j < width; j++)
//        {
//            int pix = img.at<Vec3b>(i, j)[0] + img.at<Vec3b>(i, j)[2] - img.at<Vec3b>(i, j)[1];
//            if (pix > 255)
//                pix = 255;
//            if (pix < 0)
//                pix = 0;
//            gray.at<uchar>(i, j) = (uchar)pix;
//        }
//    }
    imwrite("/sdcard/1/1gray.jpg",gray);
    Mat DestRGB;
    Scalar color = CV_RGB(255,0,0);
    if(pStrCandicateTag->size() > 0) {
        pStrCandicateTag->clear();

    }
	for(i=WIN_H+20; i< (gray.rows - WIN_H); i++)
    {
		//LOG("[%d]\n", i);
		Candicate.pt= cv::Point2i(gray.cols/2, i);
		iRet = SliderWin(gray,cv::Point2i(gray.cols/2, i), WIN_W, WIN_H,&Candicate);
		pStrCandicateTag->push_back(Candicate);
    }
    cv::cvtColor(gray,DestRGB,COLOR_GRAY2RGB);


    std::sort(g_StrCandicateTag.begin(),g_StrCandicateTag.end(),SortCmp);
    if(g_StrCandicateTag.at(g_StrCandicateTag.size()-1).StandDev < 40){
        LOGE("invalid img");
        return -1;
    }
    pt = g_StrCandicateTag.at(g_StrCandicateTag.size()-1).pt;
	iRet = pt.y+WIN_H/2;
    float depth = 1540/m_osc/1000000 *m_c0 * iRet*1000; // 1540/m_osc*sampeRate/1000000*iRet;
    iRet = (int) round(depth);
    LOG("depth:%f,iRet:%d,pix:%d,m_osc:%f,m_c0:%f", depth, iRet, pt.y,m_osc, m_c0);

	//measure
    memset(dist,0,sizeof(dist));
    sprintf(dist," %0.1f mm", depth);
    putText(DestRGB,dist,Point2i(pt.x+WIN_W/2,pt.y/2), CV_FONT_HERSHEY_DUPLEX,0.7f,color);
    cv::arrowedLine(DestRGB,Point(gray.cols/2+WIN_W/2,0),Point2i(gray.cols/2+WIN_W/2,pt.y+WIN_H/2),color,1);
    cv::Rect2i rect(gray.cols/2,pt.y, WIN_W,WIN_H);
    cv::rectangle(DestRGB,rect,color,2);

    imwrite("/sdcard/1/dest.jpg",DestRGB);
	memcpy(pOut, DestRGB.data, width*height*3);
    //LOG("depth:%d mm", depth);
    return depth;
}
