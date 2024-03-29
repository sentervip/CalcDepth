#ifndef counter_hpp
#define counter_hpp

#include <stdint.h>
#include <stdio.h>
#include <vector>
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"
using namespace std;
using namespace cv;
//#define MAX(x,y)   (x>y?x:y)
//#define MIN(x,y)   (x>y?y:x)
typedef struct StrMonmentTAG{
Point2f * pMc;
Point2f mc;
int contourIndex;
int d;
int s;
}StrMonmentTag;
typedef struct StrCandicateTAG{
cv::Point2i  pt;
float MeanValue;
float StandDev;
}StrCandicateTag;
typedef struct StrAreaTAG{
	uint32_t lineIndex; //line index
	uint32_t mcIndex; //monmet index;
	//struct StrMonmentTAG  mc;
    uint32_t s; //surface of contour
    //uint32_t d; //diameter of contour
    uint32_t wws; // white width of score
    uint32_t bws; // black width of score
    uint32_t doc; // degree of confidence
	Point    captruePoint; 
	int32_t  boxUp;
	int32_t  boxDown;
	float    Depth;
/*
    uint32_t x1;
    uint32_t x2; 
    uint32_t y1;
    uint32_t y2;
	uint32_t      AverCenterIndex; //center point of cadicate points       
	int32_t      AverCount;
	uint32_t     AverValue;
	cv::Point  AverPoint[100];  // aver point for calc
	*/
}strAreaTag;
void ParaSet(float SampleRate, float OscFre);
float measure(int w, int h, uint8_t * src, uint8_t* dest);






#endif /* counter_hpp */
