package com.rnd.compass;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class CompassRnDActivity extends Activity
{
    
    private CompassManagerRnD compassManager; 
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);
	compassManager = new CompassManagerRnD(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
	// Inflate the menu; this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.compass_rn_d, menu);
	return true;
    }
    
    @Override
    protected void onResume()
    {
        super.onResume();
        compassManager.registerListeners();
    }
    
    @Override
    protected void onPause()
    {
        super.onPause();
        compassManager.unRegisterListeners();
    }

}
