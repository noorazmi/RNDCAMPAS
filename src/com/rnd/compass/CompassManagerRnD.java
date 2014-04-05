package com.rnd.compass;

import java.util.Arrays;

import android.content.Context;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.rnd.compass.util.Util;

public class CompassManagerRnD implements SensorEventListener
{
    
    public static final int STATUS_GOOD = 0;
    public static final int STATUS_INTERFERENCE = 1;
    public static final int STATUS_INACTIVE = 2;
    private static final float MAGNETIC_INTERFERENCE_THRESHOLD_MODIFIER = 1.05f;
    private static final String TAG = CompassManagerRnD.class.getName();
    private static final int LOCATION_UPDATE_MINIMUM_TIME = 60000; // the minimum time in millisecs
    private static final int LOCATION_UPDATE_MINIMUM_DISTANCE = 10000; // the minimum distance in metres.
    private static int geoMagneticFieldStrengthStatus;
    private Location currentLocation;
    private final SensorManager sensorManager;
    private final Context context;
    private final LocationManager locationManager;
    private CompassLocationListener compassLocationListener;
    private GeomagneticField geoMagneticField;
    private final Sensor magneticFieldSensor;
    private final Sensor accelerometerSensor;
    private float[] inclinationMatrix_I;//is an array 9 floats
    private float[] rotationMatrix_R;//is an array 9 floats
    private float[] valuesAccelerometer; // is an array of 3 floats containing the gravity vector expressed in the device's coordinate. You can simply use the values returned by a SensorEvent of a Sensor of type TYPE_ACCELEROMETER. 
    private float[] valuesMagneticField; //is an array of 3 floats containing the geomagnetic vector expressed in the device's coordinate. You can simply use the values returned by a SensorEvent of a Sensor of type TYPE_MAGNETIC_FIELD.
    private float[] orientationValues;// Hold the values of azimuth, pitch and roll.Values in orientationValues can be get using SensorManager.getOrientation(rotationMatrix_R, orientationValues); 

    private float azimuth; // from getOrientation(matrixR, orientationValues)[0]: azimuth = rotation around the Z axis.
    private float pitch; // from getOrientation(matrixR, orientationValues)[1]: pitch = rotation around the X axis.
    private float roll; // from getOrientation(matrixR, orientationValues)[2]: roll = rotation around the Y axis.
    
    private boolean ready = false;// used if the we got the data from the sencsor and ready to use. 
    
    private boolean newDataReceived; // compute only when new data arrived.Prevents unnecessary calculations
    
    public CompassManagerRnD(Context context)
    {
	this.context = context;
	locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
	magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
	accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	inclinationMatrix_I = new float[16];
	rotationMatrix_R = new float[16];
	valuesAccelerometer = new float[3];
	valuesMagneticField = new float[3];
	orientationValues = new float[3];
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
	    setValuesMagneticField(event.values.clone());
	    setNewDataReceived(true);
	    testGeoMagneticFieldStrengthStatus(getValuesMagneticField());
	    break;
	case Sensor.TYPE_ACCELEROMETER:
	    setValuesAccelerometer(event.values.clone());
	    setNewDataReceived(true);
	    break;
	}
    }

    private void testGeoMagneticFieldStrengthStatus(float[] values)
    {
	// get the expected values
	float threshold = getExpectedFieldStrength() * MAGNETIC_INTERFERENCE_THRESHOLD_MODIFIER;
	float totalStrength = 1f;
	// loop through the values and test that they are not more than X% above the expected values
	for (int i = 0; i < values.length; i++)
	{
	    totalStrength *= values[i];
	}
	if (totalStrength > threshold)
	{
	    // report possible interference
	    geoMagneticFieldStrengthStatus = STATUS_INTERFERENCE;
	}
	else
	{
	    geoMagneticFieldStrengthStatus = STATUS_GOOD;
	}
    }
    
    
    private float getExpectedFieldStrength()
    {
	// a geo field is required for accurate data
	if (getGeoMagneticField() != null)
	{
	    return getGeoMagneticField().getFieldStrength();
	}
	else
	{
	    // provide a field strength over average
	    return 60 * 60 * 60f;
	}
    }

    public int testGeoMagneticFieldStrengthStatus()
    {
	return geoMagneticFieldStrengthStatus;
    }
    
    
    
    public void registerListeners()
    {
	sensorManager.registerListener(this, magneticFieldSensor, SensorManager.SENSOR_DELAY_UI);
	sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
	try
	{
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_UPDATE_MINIMUM_TIME, LOCATION_UPDATE_MINIMUM_DISTANCE, compassLocationListener);
	}
	catch (IllegalArgumentException e)
	{
	    //an exception will be thrown if the network provider does not exist
	    e.printStackTrace();
	}
    }

    public void unRegisterListeners()
    {
	sensorManager.unregisterListener(this, magneticFieldSensor);
	sensorManager.unregisterListener(this, accelerometerSensor);
	locationManager.removeUpdates(compassLocationListener);
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

    private boolean isNewDataReceived()
    {
        return newDataReceived;
    }

    private void setNewDataReceived(boolean newDataReceived)
    {
        this.newDataReceived = newDataReceived;
    }

    public float[] getOrientationValues()
    {
	if( !isNewDataReceived() || getValuesMagneticField() == null || getValuesAccelerometer() == null)
	{
	    return orientationValues;
	}
	
	// compute the orientation data
	float[] R = new float[16];
	float[] I = new float[16];
	SensorManager.getRotationMatrix(R, I, getValuesAccelerometer(), getValuesMagneticField());
	setOrientationValues(new float[3]);
	SensorManager.getOrientation(R, orientationValues);
	setNewDataReceived(false);
	

	boolean success = SensorManager.getRotationMatrix(rotationMatrix_R, inclinationMatrix_I, valuesAccelerometer, valuesMagneticField);
	Util.printLog(TAG, new Exception().getStackTrace()[0].getMethodName() + "() called success "+success);
	
	
        return orientationValues;
    }

    public void setOrientationValues(float[] orientationValues)
    {
        this.orientationValues = orientationValues;
    }
    
    public float getPositiveBearing(boolean trueNorth)
    {
	// take the given bearing and convert it into 0 <= x < 360
	float bearing = getBearing(trueNorth);
	if(bearing < 0 )
	{
	    bearing += 360;
	}
	
	return bearing;
    }
    
    public float getBearing(boolean trueNorth)
    {
	// update the values
	float[] orientationValues = getOrientationValues(); 
	
	//return if the data is null
	if(orientationValues == null)
	{
	    return 0f;
	}
	
	// convert the orientation data into a bearing
	float azimuth = orientationValues[0];
	float bearing = (float) Math.toDegrees(azimuth);
	
	// if we need it in true north
	if(trueNorth)
	{
	    bearing = convertToTrueNorth(bearing);
	    
	}
	
	return bearing;
    }
    
    private float convertToTrueNorth(float bearing)
    {
	return bearing + getDeclination();
    }
    
    public float getDeclination()
    {
	// if there is no geomagnetic field, just use the normal bearing
	if(getGeoMagneticField() != null)
	{
	    return getGeoMagneticField().getDeclination();
	    
	}
	
	return 0f;//set declination to zero
    }
    
    private GeomagneticField getGeoMagneticField()
    {
	return geoMagneticField;
    }
    
    
    
   private class CompassLocationListener implements LocationListener
   {

        @Override
        public void onLocationChanged(Location newLocation)
        {
    		
            updateCurrentLocation(newLocation);
            updateCurrentGeoField(); // update the Geomagnetic field based on the current location.
            
        }
    
        @Override
        public void onProviderDisabled(String provider)
        {
    	
        }
    
        @Override
        public void onProviderEnabled(String provider)
        {
    	
        }
    
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
    	
        }
       
   }
   
   private void updateCurrentLocation(Location newLocation)
   {
	this.currentLocation = newLocation;
   }
   
   private Location getCurrentLocation()
   {
	return currentLocation;
   }
   
   private void updateCurrentGeoField()
   {
       // we can do nothing without location
       Location currentLocation = getCurrentLocation();
    	if (currentLocation != null)
    	{
    	    // update the Geomagnetic field
    	    geoMagneticField = new GeomagneticField(Double.valueOf(currentLocation.getLatitude()).floatValue(), Double.valueOf(currentLocation.getLongitude()).floatValue(), Double.valueOf(currentLocation.getAltitude()).floatValue(), System.currentTimeMillis());
    	} 
   }
    

}
