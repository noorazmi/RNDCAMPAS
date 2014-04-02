package com.rnd.compass;

import java.util.Arrays;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.rnd.compass.util.Util;

public class CompassManagerRnD implements SensorEventListener
{
    private static final String TAG = CompassManagerRnD.class.getName();
    private final SensorManager sensorManager;
    private final Context context;
    private final Sensor magneticFieldSensor;
    private final Sensor accelerometerSensor;
    private float[] inclinationMatrix_I;
    private float[] rotationMatrix_R;
    private float[] valuesAccelerometer; // is an array of 3 floats containing the gravity vector expressed in the device's coordinate. You can simply use the values returned by a SensorEvent of a Sensor of type TYPE_ACCELEROMETER. 
    private float[] valuesMagneticField; //is an array of 3 floats containing the geomagnetic vector expressed in the device's coordinate. You can simply use the values returned by a SensorEvent of a Sensor of type TYPE_MAGNETIC_FIELD.
    private float[] matrixValues;

    private float azimuth; // from getOrientation(matrixR, matrixValues)[0]: azimuth = rotation around the Z axis.
    private float pitch; // from getOrientation(matrixR, matrixValues)[1]: pitch = rotation around the X axis.
    private float roll; // from getOrientation(matrixR, matrixValues)[2]: roll = rotation around the Y axis.
    
    
    public CompassManagerRnD(Context context)
    {
	this.context = context;
	sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
	magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
	accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
	Util.printLog(TAG, new Exception().getStackTrace()[0].getMethodName() + "() called ");

    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
	Util.printLog(TAG, new Exception().getStackTrace()[0].getMethodName() + "() called "+Arrays.toString(event.values));
	
	
	// save the data from the sensor
	switch (event.sensor.getType())
	{
	case Sensor.TYPE_MAGNETIC_FIELD:
	    valuesMagneticField = event.values.clone();
	    break;
	case Sensor.TYPE_ACCELEROMETER:
	    valuesMagneticField = event.values.clone();
	    break;
	}
	
	boolean success = sensorManager.getRotationMatrix(rotationMatrix_R, inclinationMatrix_I, valuesAccelerometer, valuesMagneticField);
	
    }

    public void registerListeners()
    {
	sensorManager.registerListener(this, magneticFieldSensor, SensorManager.SENSOR_DELAY_UI);
	sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
    }

    public void unRegisterListeners()
    {
	sensorManager.unregisterListener(this, magneticFieldSensor);
	sensorManager.unregisterListener(this, accelerometerSensor);
    }

    public float[] getValuesAccelerometer()
    {
        return valuesAccelerometer;
    }

    public void setValuesAccelerometer(float[] valuesAccelerometer)
    {
        this.valuesAccelerometer = valuesAccelerometer;
    }

    public float[] getValuesMagneticField()
    {
        return valuesMagneticField;
    }

    public void setValuesMagneticField(float[] valuesMagneticField)
    {
        this.valuesMagneticField = valuesMagneticField;
    }
    
    
    

}
