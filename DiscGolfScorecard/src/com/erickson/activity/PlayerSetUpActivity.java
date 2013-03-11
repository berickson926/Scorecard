package com.erickson.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.erickson.scorecard.R;

public class PlayerSetUpActivity extends Activity implements OnClickListener
{
	//Logcat Debug
	public static final String TAG = "PlayerSetUpActivity";
	
	private int 			playerCount;             //Number of players in this game
	private Button		   	nextButton;              //nextButton will be used to try and move to the scorecard activity one the list is prepared
	private LinearLayout	lLayout;                 //Main view layout               
	private List<EditText>	editTextList;            //Array list containing all EditText views for player name entry
	private Toast           toast;                   //Accidental exit warning & invalid player input warning
	private long            lastBackPressTime = 0;   //toast timer
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		setContentView(R.layout.playersetuplayout);
		
		//Init the number of players from previous activity
		retrievePlayerCount();
	    
		//Populate the list with empty text fields (with a hint text) for the user to fill out with player names
		setUpTextFields();
		
	}//end onCreate
	
	@Override
	protected void onPause()
	{
		super.onPause();
		finish(); //Remove activity from stack; we don't want to return to it.
	}
	
	//Pre: None
	//Post: int playerCount is intialized to the value passed by activity EntryPointActivity. 
	private void retrievePlayerCount()
	{
		//Attempt to receive the player count from calling activity
		try
		{
			playerCount = getIntent().getExtras().getInt("com.erickson.activity.EntryPointActivity.Players");
			Log.d(TAG, "PlayerCountReceived is: "+playerCount);
		}
		catch(Exception e)
		{
			Log.d(TAG, "Unable to receive player count");
		}
	}//end retreivePlayerCount

	@Override
	public void onClick(View view) 
	{
		if(view.getId() == R.id.bPlayerSetup)
		{
			
			boolean validNameData = true;                           //Flag used during text parse. Will be set to false if none/long player names are encountered 
			ArrayList<String> actualNames = new ArrayList<String>();//Temp array list to hold names we read from the view, which will be passed on to the scorecardActivity
			String temp;                                            //Temp player name that we'll use for adding t
			
			for(int i=0; i < editTextList.size(); i++)
			{
				temp = editTextList.get(i).getText().toString();
				Log.d(TAG, "Read in: "+temp);
				
				if(temp.length() <= 0)
				{
					validNameData = false;              //we encountered an empty or too large player name, abort
					editTextList.get(i).requestFocus(); //focus on first invalid text field
					toast = Toast.makeText(this, "Enter a name for player "+(i+1), 5000);
					break;                              //short circuit text parse and let user double-check their data
				}//end if
				else if(temp.length() > 14)
				{
					validNameData = false;              //we encountered an empty or too large player name, abort
					editTextList.get(i).requestFocus(); //focus on first invalid text field
					toast = Toast.makeText(this, "Player "+(i+1)+" exceeds 14 character max.", 5000);
					break;                              //short circuit text parse and let user double-check their data
				}
				else
					actualNames.add(temp);              //The name checks out so we'll go ahead and keep building the list
			}//end for
			
			if(validNameData)
			{
				//start new activity, pass name list on
				Log.d(TAG, "All player names entered correctly. Moving on...");
				
				//Start the scorecard activity
				Intent intent = new Intent(this, ScorecardActivity.class);
				intent.putExtra("com.erickson.activity.PlayerSetUpActivity.actualNames", actualNames);//We're going to pass the list of names in the game to the next activity
				
				startActivity(intent);//start scorecardActivity
			}
			else
			{
				//pop toast indicating invalid input
				Log.d(TAG, "Invalid name data exists. Fix before proceeding.");
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
		}//end if
	}//end onClick
	
	@Override
	public void onBackPressed() 
	{
		//Pop a toast message warning user about exiting the program so we can avoid accidental exits
	  if (this.lastBackPressTime < System.currentTimeMillis() - 4000) 
	  {
	    toast = Toast.makeText(this, "Press back again to exit", 4000);
	    toast.show();
	    this.lastBackPressTime = System.currentTimeMillis();
	  }//end if 
	  else 
	  {
	    if (toast != null) 
	    	toast.cancel();
	    
	    super.onBackPressed();
	  }//end else
	}//end onBackPressed()
	
	//Pre:None
	//Post: Generate a number of textfields equal to playerCount that will be added to the view in a list format
	private void setUpTextFields()
	{
		lLayout = (LinearLayout) findViewById(R.id.lPlayerList);
	    
		nextButton = (Button) findViewById(R.id.bPlayerSetup);
		nextButton.setOnClickListener(this);
		
		editTextList = new ArrayList<EditText>();
		EditText ed;    
		
		for(int i=0; i<playerCount; i++)
		{
			ed = new EditText(this);
			
			ed.setId(i);//May wish to check availability prior to assignment of id
			ed.setHint("Enter Player "+(i+1));//bump it up by 1 so we don't have a "player 0" situation. 
			ed.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
			
			editTextList.add(ed);
			lLayout.addView(ed);
			
		}//end for
	}//end setUpTextFields

}//end PlayerSetUpActivity
