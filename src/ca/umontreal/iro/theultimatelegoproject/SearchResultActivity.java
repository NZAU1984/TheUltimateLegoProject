package ca.umontreal.iro.theultimatelegoproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class SearchResultActivity extends Activity
{
	private Button showSet456;
	private Button showSet123;
	private String sqlRequest;

	// TEMP !!
	private Boolean isFav	= false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_result);

		Intent intent	= getIntent();

		sqlRequest	= intent.getStringExtra("sql_request");

		// TEMP, the SQL request sent will tell this activity what to search, no need to tell it it's showing favorites !
		isFav	= intent.getBooleanExtra("favorites", false);

		initiateButtons();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search_result, menu);
		return true;
	}

	private void initiateButtons()
	{
		showSet123	= (Button) findViewById(R.id.button_showset123);
		showSet456	= (Button) findViewById(R.id.button_showset456);

		if(isFav)
		{
			showSet456.setVisibility(View.GONE);
		}
		else
		{
			showSet123.setVisibility(View.GONE);
		}

		showSet123.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				launchSetInfoActivity("123");
			}
		});

		showSet456.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				launchSetInfoActivity("456");
			}
		});
	}

	private void launchSetInfoActivity(String setId)
	{
		Intent launchSetInfoActivity = new Intent(this, SetInfoActivity.class);

		launchSetInfoActivity.putExtra("set_id", setId);

		startActivity(launchSetInfoActivity);
	}

}
