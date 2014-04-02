package com.rnd.compass;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.rnd.compass.util.Util;

public class CompassRnDView extends SurfaceView implements SurfaceHolder.Callback
{

    private DrawingThread drawingThread;
    private static final String TAG = CompassRnDView.class.getName();
    private SurfaceHolder surfaceHolder;
    private Context context;
    private int centerX;
    private int centerY;

    private static final int MARKER_LENGHT_LEVEL_1 = 7;// in dp
    private static final int MARKER_LENGHT_LEVEL_2 = 9;// in dp
    private static final int MARKER_LENGHT_LEVEL_3 = 12;// in dp

    private static final int INTERNAL_RADIUS_LEVEL_1 = 100;// in dp
    private static final int INTERNAL_RADIUS_LEVEL_2 = 100;// in dp
    private static final int INTERNAL_RADIUS_LEVEL_3 = 100;// in dp

    private static final int STROKE_WIDTH_LEVEL_2 = 2;
    private static final int STROKE_WIDTH_LEVEL_3 = 4;

    private static final double MULTIPLIER_LEVEL_1 = Math.PI / 90;
    private static final double MULTIPLIER_LEVEL_2 = Math.PI / 18;
    private static final double MULTIPLIER_LEVEL_3 = Math.PI / 6;

    private static final double SECTIONS_LEVEL_1 = 180;
    private static final double SECTIONS_LEVEL_2 = 36;
    private static final double SECTIONS_LEVEL_3 = 12;

    //private int mrkerLength_level_1;
    //private int mrkerLength_level_2;
    //private int mrkerLength_level_3;

    private int mInternalRadiusLevel_1;// For drawing the thinest/smallest lines on the circle
    private int mInternalRadiusLevel_2;// For drawing the medium lines
    private int mInternalRadiusLevel_3;// For drawing the biggest lines

    private Paint paintLevel_1;// For drawing the thinest/smallest lines on the circle
    private Paint paintLevel_2;// For drawing the medium lines
    private Paint paintLevel_3;// For drawing the biggest lines
    private Paint directionsTextPaint;

    private int mExternalRadiusLevel_1;
    private int mExternalRadiusLevel_2;
    private int mExternalRadiusLevel_3;

    private static final double HALF_PI = Math.PI / 2;

    //private static final String DIRECTIONS[] = {"1","2","3","4"};
    private static final String DIRECTIONS[] = {"N", "E", "S","W" };
    private static final int DIRECTIONS_COUNT = 4;
    private static final double MULTIPLIER_DIRECTIONS = Math.PI / 2;
    private int directionsRadius;
    private static final int DIRECTIONS_REDIUS_MARGIN = 5;// in dp
    private static final int DIRECTIONS_TEXT_STROKE_WIDTH = 5;

    public CompassRnDView(Context context, AttributeSet attrs)
    {
	super(context, attrs);
	this.context = context;
	this.surfaceHolder = getHolder();
	surfaceHolder.addCallback(this);
	centerX = Util.getCenter(context).x;
	centerY = Util.getCenter(context).y;

	//mrkerLength_level_1 = (int) Util.dipToPixels(context, MARKER_LENGHT_LEVEL_1);
	//mrkerLength_level_2 = (int) Util.dipToPixels(context, MARKER_LENGHT_LEVEL_2);
	//mrkerLength_level_3 = (int) Util.dipToPixels(context, MARKER_LENGHT_LEVEL_3);

	mInternalRadiusLevel_1 = (int) Util.dipToPixels(context, INTERNAL_RADIUS_LEVEL_1);
	mInternalRadiusLevel_2 = (int) Util.dipToPixels(context, INTERNAL_RADIUS_LEVEL_2);
	mInternalRadiusLevel_3 = (int) Util.dipToPixels(context, INTERNAL_RADIUS_LEVEL_3);

	mExternalRadiusLevel_1 = (int) Util.dipToPixels(context, (INTERNAL_RADIUS_LEVEL_1 + MARKER_LENGHT_LEVEL_1));
	mExternalRadiusLevel_2 = (int) Util.dipToPixels(context, (INTERNAL_RADIUS_LEVEL_2 + MARKER_LENGHT_LEVEL_2));
	mExternalRadiusLevel_3 = (int) Util.dipToPixels(context, (INTERNAL_RADIUS_LEVEL_3 + MARKER_LENGHT_LEVEL_3));

	directionsRadius = (int) Util.dipToPixels(context, (INTERNAL_RADIUS_LEVEL_3 + MARKER_LENGHT_LEVEL_3 + DIRECTIONS_REDIUS_MARGIN));

	initPaints();
    }

    // ************************************************* Callback Methods  ******************************************************************//
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
	Util.printLog(TAG, new Exception().getStackTrace()[0].getMethodName() + "() called");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
	Util.printLog(TAG, new Exception().getStackTrace()[0].getMethodName() + "() called");
	drawingThread = new DrawingThread(surfaceHolder, context, this);
	Thread thread = new Thread(drawingThread);
	thread.start();
	drawingThread.setRunning(true);

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
	Util.printLog(TAG, new Exception().getStackTrace()[0].getMethodName() + "() called");
    }

    // ************************************************* Callback Methods End******************************************************************//

    // ************************************************* Helper Methods ******************************************************************//

    public void drawCustomSrface(Canvas canvas)
    {
	Util.printLog(TAG, new Exception().getStackTrace()[0].getMethodName() + "() called");

	// Paint entire window with the color
	canvas.drawColor(getResources().getColor(android.R.color.holo_red_light));

	for (int i = 0; i < SECTIONS_LEVEL_1; i++)
	{
	    double angle = HALF_PI - i * MULTIPLIER_LEVEL_1;
	    float x = (float) Math.cos(angle);
	    float y = (float) Math.sin(angle);
	    canvas.drawLine(centerX + mInternalRadiusLevel_1 * x, centerY - mInternalRadiusLevel_1 * y, centerX + mExternalRadiusLevel_1 * x, centerY - mExternalRadiusLevel_1 * y, paintLevel_1);
	}

	for (int i = 0; i < SECTIONS_LEVEL_2; i++)
	{
	    double angle = HALF_PI - i * MULTIPLIER_LEVEL_2;
	    float x = (float) Math.cos(angle);
	    float y = (float) Math.sin(angle);
	    canvas.drawLine(centerX + mInternalRadiusLevel_2 * x, centerY - mInternalRadiusLevel_2 * y, centerX + mExternalRadiusLevel_2 * x, centerY - mExternalRadiusLevel_2 * y, paintLevel_2);
	}

	for (int i = 0; i < SECTIONS_LEVEL_3; i++)
	{
	    double angle = HALF_PI - i * MULTIPLIER_LEVEL_3;
	    float x = (float) Math.cos(angle);
	    float y = (float) Math.sin(angle);
	    canvas.drawLine(centerX + mInternalRadiusLevel_3 * x, centerY - mInternalRadiusLevel_3 * y, centerX + mExternalRadiusLevel_3 * x, centerY - mExternalRadiusLevel_3 * y, paintLevel_3);

	}

	for (int i = 0; i < DIRECTIONS_COUNT; i++)
	{
	    double angle = HALF_PI - i * MULTIPLIER_DIRECTIONS;
	    float x = (float) Math.cos(angle);
	    float y = (float) Math.sin(angle);
	    //canvas.drawLine(centerX + mInternalRadiusLevel_3 * x, centerY - mInternalRadiusLevel_3 * y, centerX + mExternalRadiusLevel_3 * x, centerY - mExternalRadiusLevel_3 * y, paintLevel_3);
	    canvas.drawText(DIRECTIONS[i], centerX + directionsRadius * x, centerY - directionsRadius * y, directionsTextPaint);

	}

	// Draw central point
	float xx = centerX;
	float yy = centerY;
	float[] f = { xx, yy };
	paintLevel_1.setColor(getResources().getColor(android.R.color.holo_blue_bright));
	canvas.drawPoints(f, paintLevel_1);

    }

    private void initPaints()
    {
	// Level 1 paint object. Will be used to draw the sections on the interval of 2 degrees.
	paintLevel_1 = new Paint(Paint.ANTI_ALIAS_FLAG);
	paintLevel_1.setAntiAlias(true);
	paintLevel_1.setDither(true);
	paintLevel_1.setColor(Color.BLACK);
	paintLevel_1.setStyle(Paint.Style.STROKE);
	paintLevel_1.setStrokeJoin(Paint.Join.ROUND);
	paintLevel_1.setColor(getResources().getColor(android.R.color.black));

	// Level 2 paint object. Will be used to draw the sections on the interval of 10 degrees.
	paintLevel_2 = new Paint(Paint.ANTI_ALIAS_FLAG);
	paintLevel_2.setAntiAlias(true);
	paintLevel_2.setDither(true);
	paintLevel_2.setColor(Color.BLACK);
	paintLevel_2.setStyle(Paint.Style.STROKE);
	paintLevel_2.setStrokeJoin(Paint.Join.ROUND);
	paintLevel_2.setColor(getResources().getColor(android.R.color.black));
	paintLevel_2.setStrokeWidth(Util.dipToPixels(context, STROKE_WIDTH_LEVEL_2));

	// Level 2 paint object. Will be used to draw the sections on the interval of 30 degrees.
	paintLevel_3 = new Paint(Paint.ANTI_ALIAS_FLAG);
	paintLevel_3.setAntiAlias(true);
	paintLevel_3.setDither(true);
	paintLevel_3.setColor(Color.BLACK);
	paintLevel_3.setStyle(Paint.Style.STROKE);
	paintLevel_3.setStrokeJoin(Paint.Join.ROUND);
	paintLevel_3.setColor(getResources().getColor(android.R.color.black));
	paintLevel_3.setStrokeWidth(Util.dipToPixels(context, STROKE_WIDTH_LEVEL_3));

	directionsTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	directionsTextPaint.setColor(getResources().getColor(android.R.color.holo_blue_bright));
	directionsTextPaint.setStrokeWidth(Util.pixelsToSp(context, (float) DIRECTIONS_TEXT_STROKE_WIDTH));
    }

    private class DrawingThread implements Runnable
    {
	private boolean isRunning;
	private Canvas mCanvas;
	private SurfaceHolder surfaceHolder;
	private Context context;
	private CompassRnDView compassRnDView;

	public DrawingThread(SurfaceHolder surfaceHolder, Context context, CompassRnDView compassRnDView)
	{
	    this.context = context;
	    this.surfaceHolder = surfaceHolder;
	    this.compassRnDView = compassRnDView;
	    this.isRunning = false;
	}

	@Override
	public void run()
	{
	    while (isRunning)

	    {

		mCanvas = surfaceHolder.lockCanvas();

		if (mCanvas != null)

		{

		    compassRnDView.drawCustomSrface(mCanvas);
		    surfaceHolder.unlockCanvasAndPost(mCanvas);
		    isRunning = false;

		}

	    }

	}

	void setRunning(boolean isRunning)

	{
	    this.isRunning = isRunning;

	}

    }

}
