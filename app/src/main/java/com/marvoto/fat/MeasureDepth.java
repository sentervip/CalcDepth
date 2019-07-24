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
    public static native  String TestString();
    public static native  int SaveBmp(byte[] data, int w, int h,String dir );
    public static native  int DrawBitmap(Bitmap var0, int[] var1);
}
