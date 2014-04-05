package com.rnd.compass;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.LinearLayout;

public class CompassRnDActivity extends Activity
{
    
    private CompassManagerRnD compassManager; 
    private LinearLayout compassViewHolder;
    private CompassRnDView compassRnDView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);
	compassViewHolder = (LinearLayout) findViewById(R.id.compass_view_holder);
	compassManager = new CompassManagerRnD(this);
	compassRnDView = new CompassRnDView(this, compassManager);
	compassViewHolder.addView(compassRnDView);
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
        compassRnDView.setRunning(true);
        compassRnDView.startDrawingThread();
        
    }
    
    @Override
    protected void onPause()
    {
        super.onPause();
        compassRnDView.setRunning(false);
        compassManager.unRegisterListeners();
    }

}
