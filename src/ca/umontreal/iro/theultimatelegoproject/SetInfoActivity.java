package ca.umontreal.iro.theultimatelegoproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SetInfoActivity extends Activity
{
	private Button buttonShowBuildingInstructions;
	private TextView textviewInfoForSet;
	private String setId;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_info);

		Intent intent	= getIntent();

		setId	= intent.getStringExtra("set_id");

		initiateButtons();
		initiateTextViews();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.set_info, menu);
		return true;
	}

	private void initiateButtons()
	{
		buttonShowBuildingInstructions	= (Button) findViewById(R.id.button_show_building_instructions);

		buttonShowBuildingInstructions.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				launchBuildingInstructionActivity();
			}
		});
	}

	private void initiateTextViews()
	{
		textviewInfoForSet	= (TextView) findViewById(R.id.textview_info_for_set);

		textviewInfoForSet.setText(String.format(getResources().getString(R.string.garbage_infoForSet), setId));
	}

	private void launchBuildingInstructionActivity()
	{
		Intent launchBuildingInstructionActivity = new Intent(this, BuildingInstructionActivity.class);

		launchBuildingInstructionActivity.putExtra("set_id", setId);

		startActivity(launchBuildingInstructionActivity);
	}

}
