package com.erickson.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.erickson.scorecard.R;

public class EntryPointActivity extends Activity implements OnClickListener
{
	//Logcat debug
	public static final String TAG = "EntryPoint";
	
	NumberPicker  np;                    //User-defined number of players involved in the game
	private Toast toast;                 //Toast used to warn user about accidentally exiting app
	private long  lastBackPressTime = 0; //toast display timer
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		setContentView(R.layout.entrypointlayout);
		
		//Set up the number picker wheel
		np = (NumberPicker) findViewById(R.id.npPlayerCountPicker);
		np.setMaxValue(10);
		np.setMinValue(1);
		np.setWrapSelectorWheel(false);
		
		//Setup button listener
		Button nextButton = (Button) findViewById(R.id.bEntryPoint);
		nextButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) 
	{
		try
		{
			//If they press the next button, go ahead and set up the player count, start next activity 
			if(view.getId() == R.id.bEntryPoint)
			{
				int players = np.getValue();
				Log.d(TAG, "Players set to: "+players);
				
				//Start the player setup activity
				Intent intent = new Intent(this, PlayerSetUpActivity.class);
				intent.putExtra("com.erickson.activity.EntryPointActivity.Players", players);//We're going to pass the number of players in the game to the next activity
				
				startActivity(intent);//start playerSetUpActivity
			}
		}
		catch(Exception e)
		{
			Log.d(TAG, "Activity not found!");
		}
	}//end onClick

	@Override
	public void onBackPressed() 
	{
		//Protect user from accidentally closing the app with a back button press.
		//We'll pop a toast warning on first back press, then actually close on the second
	  if (this.lastBackPressTime < System.currentTimeMillis() - 4000) 
	  {
	    toast = Toast.makeText(this, "Press back again to exit", 4000);
	    toast.show();
	    this.lastBackPressTime = System.currentTimeMillis();
	  }//end if 
	  else 
	  {
	    if (toast != null) 
	    {
	    	toast.cancel();
	    }
	    
	    super.onBackPressed();
	  }//end else
	}//end onBackPressed()
	
	@Override
	protected void onPause()
	{
		super.onPause();
		finish();//We're not going to allow a partial setup to be saved, so end the activity on pause.
	}
}//end EntryPointActivity
