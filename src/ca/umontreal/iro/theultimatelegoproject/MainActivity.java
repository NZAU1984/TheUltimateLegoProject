package ca.umontreal.iro.theultimatelegoproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity
{
	TulpApplication	tulpApplication;
	private Button	searchButton;
	private Button	favoritesButton;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tulpApplication	= (TulpApplication) getApplication();

		if(!tulpApplication.isDbCreated())
		{
			//Tools.shortToast(getApplicationContext(), "...");
			//launchUpdateDbActivity();
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

	private void launchUpdateDbActivity()
	{
		Intent launchUpdateDbActivity = new Intent(this, UpdateDbActivity.class);
		startActivity(launchUpdateDbActivity);
	}

}
