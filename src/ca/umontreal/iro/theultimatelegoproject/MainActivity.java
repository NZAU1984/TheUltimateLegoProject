package ca.umontreal.iro.theultimatelegoproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.skulg.tulp.dbHelper;

public class MainActivity extends Activity
{
	TulpApplication	tulpApplication;
	private Button	searchButton;
	private Button	favoritesButton;
	private dbHelper dbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tulpApplication	= (TulpApplication) getApplication();
		dbHelper	= new dbHelper(getApplicationContext());
	}

	@Override
	protected void onStart()
	{
		super.onStart();

		// If database file does not exist we'll show UpdateDbActibity and create it and load values in it.
		Boolean updateDb	= !dbHelper.databaseExists(getApplicationContext());

		// By default, we want to first fetch all building instructions and then get the sets from them.
		String updateIntent	= "from_building_instructions";

		if(!updateDb)
		{
			// Let's check if the last time we updated is older than today - ...
			// if(...) { ... }
			// else ...

			// Let's check if there are sets to be imported
			if(0 < dbHelper.getNumberOfSetsToBeImported())
			{
				updateDb		= true;
				updateIntent	= "from_import_table";
			}
		}

		if(updateDb)
		{
			launchUpdateDbActivity(updateIntent);
		}

		initiateButtons();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void initiateButtons()
	{
		searchButton = (Button) findViewById(R.id.button_search_acceuil);
		favoritesButton = (Button) findViewById(R.id.button_favorites);

		searchButton.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				launchSearchActivity();
			}
		});

		favoritesButton.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				launchSearchResultActivity();
			}
		});
	}

	private void launchSearchActivity()
	{
		Intent launchSearchActivity = new Intent(this, SearchActivity.class);
		startActivity(launchSearchActivity);
	}

	private void launchSearchResultActivity()
	{
		String sqlRequest = "SELECT * FROM sets WHERE is_favorite=1";
		Intent launchSearchResultActivity = new Intent(this, SearchResultActivity.class);
		launchSearchResultActivity.putExtra("sql_request", sqlRequest);
		launchSearchResultActivity.putExtra("favorites", true);
		startActivity(launchSearchResultActivity);
	}

	private void launchUpdateDbActivity(String updateIntent)
	{
		Intent launchUpdateDbActivity = new Intent(this, UpdateDbActivity.class);
		launchUpdateDbActivity.putExtra("strategy", updateIntent);
		startActivity(launchUpdateDbActivity);
	}

}
