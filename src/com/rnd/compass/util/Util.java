package com.rnd.compass.util;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

public class Util
{
    public static void printLog(String TAG, String msg)
    {
	Log.d(Const.APP_TAG, "[" + TAG + "]" + msg);
    }

    public static Point getCenter(Context context)
    {
	Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
	Point center = new Point();
	Point size = new Point();
	display.getSize(size);
	center.x = size.x / 2;
	center.y = size.y / 2;
	Util.printLog(Const.APP_TAG, new Exception().getStackTrace()[0].getMethodName() + "() called center:" + center);
	return center;
    }

    public static float dipToPixels(Context context, float dipValue)
    {
	DisplayMetrics metrics = context.getResources().getDisplayMetrics();
	return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    public static float pixelsToSp(Context context, Float px)
    {
	float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
	return px / scaledDensity;
    }

}
