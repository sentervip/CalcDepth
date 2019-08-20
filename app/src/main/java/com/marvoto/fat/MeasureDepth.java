package com.marvoto.fat;
import android.graphics.Bitmap;


public final class MeasureDepth{
    private static final String TAG = "MAVTJ";
    private static int iRet;

    public MeasureDepth() {

    }
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("fat");
    }
    //test for myself
    public static native  String TestString();
    public static native  int SaveBmp(byte[] data, int w, int h,String dir );
    /**
     * desc   calculate depth and show the image
     * var0   Bitmap show
     * var1   argb8888 image
     * ret    depth of fat/unit: mm
     * <0     failed
     * -2     没有接触好
     * -3     图像不清晰
     * >0     测量脂肪厚度
     */
    public static native  int DrawBitmap(Bitmap var0, byte[] var1);

    /**
     * desc   Set parameter from fpga
     * var0   Sample Rate
     * var1   OscFre
     * ret    no
     */
    public static native  int ParaSet(float SampleRate, float OscFre);
}
