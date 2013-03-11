package com.erickson.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.erickson.scorecard.R;

public class ScorecardActivity extends Activity implements OnClickListener 
{
	//Debug
	public static final String TAG = "ScorecardActivity";

	private Toast toast;                   //used to display warning before exit when pressing back
	private long lastBackPressTime = 0;
	
	private ArrayList<String> playerNames; // A list of just the names received from previous activity
										   
	//private ArrayList<Player> playerList; // List of Player reference objects; Leaving this out for now since we're keeping data as simple as possible. Available for potential future updates
	
	//Id maps will allow us to keep track of how dynamically generated view objects are associated with 
	//	each other, row by row in the layout. 
	
	SparseIntArray plusToCurrent;          //Mapping of plus button id's to current TextView id's
	SparseIntArray minusToCurrent;         //Mapping of minus button id's to current TextView id's
	SparseIntArray currentToTotal;         //Mapping of current textView id's to total textView id's
	
	private Button nextRoundButton;        //Button press by user will start the dump of current scores into the total column

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		setContentView(R.layout.scorecardlayout);

		// Set up initial player name list
		initializeList();

		// Setup next round button
		nextRoundButton = (Button) findViewById(R.id.bNextRound);
		nextRoundButton.setOnClickListener(this);
		
		// Populate the listView
		populate();

	}// end onCreate

	//Pre:None
	//Post: Initializes a row in the view containing a name text field, score text field, plus/minus buttons and a total textview for each player
	//      Initializes mappings for plus/minus button id's to a current score textfield id, and current to total id for easy reference
	public void populate()
	{
		//Initialize maps - eliminate view id guesswork & hardcoding by mapping related id's to one another
		//The minus/plus buttons will point to a current score TextView id
		//each current score TextView will point to its associated total score TextView id
		plusToCurrent = new SparseIntArray();
		minusToCurrent = new SparseIntArray();
		currentToTotal = new SparseIntArray();
		
		//Temp variables for initializing the list
		Button plusButton;
		Button minusButton;
		TextView name;
		TextView total;
		TextView current;
		LinearLayout rowLayout,mainList;
		
		//Only need to worry about the main listView while we populate so we instantiate here
		mainList = (LinearLayout) findViewById(R.id.lPlayerList);
		
		//Since we use the for loop to generate id's we have to modify the parameters a bit for everything to make sense
		//One pass will use five values, so the loop must increment i by 5 each time
		for(int i=0; i < (playerNames.size()*5); i += 5)
		{
			//playerList.add(new Player(playerNames.get(i)));//May reactivate if we want to start making persistent data
			Log.d(TAG, "i: "+i);
			//container for one row
			rowLayout = new LinearLayout(this);
			rowLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
			rowLayout.setWeightSum(200);
			Log.d(TAG,"Rowlayout initialized");
			
			//Player name in one row
			name = new TextView(this);
			name.setId(i);
			name.setText(playerNames.get(i/5));//divide i by five to match correct element in name list
			name.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT, 30f));
			name.setTextSize(15);
			
			//Player total score in one row
			current = new TextView(this);
			current.setId(i+1);
			current.setText("0");
			current.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT, 40f));
			current.setGravity(Gravity.CENTER);
			
			//Plus button
			plusButton = new Button(this);
			plusButton.setOnClickListener(this);
			plusButton.setId(i+2);
			plusButton.setText("+");
			plusButton.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT, 45f));
			
			
			//Minus Button
			minusButton = new Button(this);
			minusButton.setOnClickListener(this);
			minusButton.setId(i+3);
			minusButton.setText("-");
			minusButton.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT, 45f));

            //Player total score in one row
			total = new TextView(this);
			total.setId(i+4);
			total.setText("0");
			total.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT, 40f));
			total.setGravity(Gravity.CENTER_HORIZONTAL);
			
			//Add elements to row layout
			rowLayout.addView(name);
			rowLayout.addView(current);
			rowLayout.addView(plusButton);
			rowLayout.addView(minusButton);
			rowLayout.addView(total);
			
			//add the row to the main list
			mainList.addView(rowLayout);	
			
			//Bind buttons to current score
			plusToCurrent.append(plusButton.getId(), current.getId());
			minusToCurrent.append(minusButton.getId(), current.getId());
			
			//Bind current score to total
			currentToTotal.append(current.getId(), total.getId());
			
		}//end for
	}//end populate

	//Pre: None
	//Post: Initializes playerNames to the array of string names passed by PlayerSetUpActivity
	public void initializeList() 
	{
		// Attempt to receive the player count from calling activity
		try 
		{
			playerNames = getIntent().getExtras().getStringArrayList("com.erickson.activity.PlayerSetUpActivity.actualNames");
			Log.d(TAG, "PlayerCountReceived is: " + playerNames.size());
		} 
		catch (Exception e) 
		{
			Log.d(TAG, "Unable to receive the list of player names");
		}
	}// end initializeList

	@Override
	public void onBackPressed() 
	{
		if (this.lastBackPressTime < System.currentTimeMillis() - 4000)
		{
			toast = Toast.makeText(this, "Press back again to exit", 4000);
			toast.show();
			this.lastBackPressTime = System.currentTimeMillis();
		}// end if
		else 
		{
			if (toast != null)
				toast.cancel();

			super.onBackPressed();
		}// end else
	}// end onBackPressed()

	@Override
	public void onClick(View view)
	{
		Log.d(TAG, "View id received: "+view.getId());

		try 
		{
			//Check if it was a Plus button click
			if(plusToCurrent.get(view.getId()) != 0)
			{
				Log.d(TAG, "Plus Button event");
				
				//Use button id as key to fetch the current TextView id mapped to it. Parse the text to an int
				String temp =  (String) ((TextView) findViewById(plusToCurrent.get(view.getId()))).getText();
				int num = Integer.parseInt(temp);
				
				//Increment
				num++;
				
				//Update the text
				((TextView) findViewById(plusToCurrent.get(view.getId()))).setText(Integer.toString(num));

			}//end if
			//Check if it was a minus button click
			else if(minusToCurrent.get(view.getId()) != 0)
			{
				Log.d(TAG, "Minus button event");
				
				//Use button id as key to fetch the current TextView id mapped to it. Parse the text to an int
				String temp =  (String) ((TextView) findViewById(minusToCurrent.get(view.getId()))).getText();
				int num = Integer.parseInt(temp);
				
				//Decrement
				if(num != 0)
					num--;
				
				//Update the text
				((TextView) findViewById(minusToCurrent.get(view.getId()))).setText(Integer.toString(num));
				
			}//end else if
			else if(view.getId() == R.id.bNextRound)
			{
				updateTotals();//migrate current round scores into the aggregate total
			}//end else if
		} 
		catch (NumberFormatException e) 
		{
			Log.d(TAG, "Read error");
			e.printStackTrace();
		}
		
	}//end onClick
	
	//Pre: playerNames contains one or more strings. 
	//Post: For each row in the list, we will add the current score value with the total score value and store that as the new total score value.
	//      The current score for each row will be reset to 0, indicating the start of a new round.
	public void updateTotals()
	{
		int currentId, currentVal, totalVal;
		TextView currentView, totalView;
		
		for(int i=0; i<currentToTotal.size(); i++)
		{
			currentId = currentToTotal.keyAt(i);
			
			currentView = (TextView) findViewById(currentId);
			totalView = (TextView) findViewById(currentToTotal.get(currentId));
			
			currentVal = Integer.parseInt(currentView.getText().toString());			
			totalVal =  Integer.parseInt(totalView.getText().toString());
			
			totalVal += currentVal;
			
			currentView.setText("0");
			totalView.setText(Integer.toString(totalVal));
		}
	}//end updateTotals

}// End ScorecardActivity
