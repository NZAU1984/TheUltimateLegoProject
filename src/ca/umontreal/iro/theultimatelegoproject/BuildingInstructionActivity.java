package ca.umontreal.iro.theultimatelegoproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class BuildingInstructionActivity extends Activity
{
	private TextView textviewBuildingInstructionsForSet;
	private String setId;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_building_instruction);

		Intent intent	= getIntent();

		setId	= intent.getStringExtra("set_id");

		initiateTextViews();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.building_instruction, menu);
		return true;
	}

	private void initiateTextViews()
	{
		textviewBuildingInstructionsForSet	= (TextView) findViewById(R.id.textview_building_instructions_for_set);

		textviewBuildingInstructionsForSet.setText(String.format(getResources().getString(R.string.garbage_buildingInstructionsForSet), setId));
	}

}
