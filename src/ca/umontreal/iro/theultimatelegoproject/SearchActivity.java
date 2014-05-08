package ca.umontreal.iro.theultimatelegoproject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.skulg.tulp.dbHelper;

public class SearchActivity extends Activity
{

	private EditText editTextKeyword, editTextYearFrom, editTextYearTo, editTextPriceFrom, editTextPriceTo, editTextPieceFrom, editTextPieceTo;
	private View searchGo;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		setupActionBar();
		initiateButton();
		initiateEditTexts();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar()
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	private void launchSearchResultActivity(String keyword, String yearFrom, String yearTo, String priceFrom, String priceTo, String nbPiecesFrom, String nbPiecesTo)
	{
		Intent launchSearchResultActivity = new Intent(this, SearchResultActivity.class);

		launchSearchResultActivity.putExtra("keyword", keyword);
		launchSearchResultActivity.putExtra("year_from", yearFrom);
		launchSearchResultActivity.putExtra("year_to", yearTo);
		launchSearchResultActivity.putExtra("price_from", priceFrom);
		launchSearchResultActivity.putExtra("price_to", priceTo);
		launchSearchResultActivity.putExtra("pieces_from", nbPiecesFrom);
		launchSearchResultActivity.putExtra("pieces_to", nbPiecesTo);

		startActivity(launchSearchResultActivity);
	}


	private void initiateButton()
	{
		searchGo = findViewById(R.id.button_search);
		searchGo.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String keyword		= editTextKeyword.getText().toString();
				String yearFrom		= editTextYearFrom.getText().toString();
				String yearTo		= editTextYearTo.getText().toString();
				String priceFrom	= editTextPriceFrom.getText().toString();
				String priceTo		= editTextPriceTo.getText().toString();
				String nbPiecesFrom	= editTextPieceFrom.getText().toString();
				String nbPiecesTo	= editTextPieceTo.getText().toString();

				if(
					!TextUtils.isDigitsOnly(yearFrom)
					||
					!TextUtils.isDigitsOnly(yearTo)
					||
					(!priceFrom.equals("") && !priceFrom.matches("-?\\d+(\\.\\d+)?"))
					||
					(!priceTo.equals("") && !priceTo.matches("-?\\d+(\\.\\d+)?"))
					||
					!TextUtils.isDigitsOnly(nbPiecesFrom)
					||
					!TextUtils.isDigitsOnly(nbPiecesTo)
				)
				{
					Tools.shortToast(getApplicationContext(), getApplicationContext().getString(R.string.search_enter_numeric_values_only));

					return;
				}

				if(!yearFrom.equals("") && !yearTo.equals("") && (0 < yearFrom.compareTo(yearTo)))
				{
					String temp	= yearTo;
					yearTo		= yearFrom;
					yearFrom	= temp;
					editTextYearFrom.setText(yearFrom);
					editTextYearTo.setText(yearTo);
				}

				if(!priceFrom.equals("") && !priceTo.equals("") && (Float.valueOf(priceFrom) > Float.valueOf(priceTo)))
				{
					String temp	= priceTo;
					priceTo		= priceFrom;
					priceFrom	= temp;

					editTextPriceFrom.setText(priceFrom);
					editTextPriceTo.setText(priceTo);
				}

				if(!nbPiecesFrom.equals("") && !nbPiecesTo.equals("") && (0 < nbPiecesFrom.compareTo(nbPiecesTo)))
				{
					String temp		= nbPiecesTo;
					nbPiecesTo		= nbPiecesFrom;
					nbPiecesFrom	= temp;

					editTextPieceFrom.setText(nbPiecesFrom);
					editTextPieceTo.setText(nbPiecesTo);
				}

				dbHelper dbHelper	= new dbHelper(getApplicationContext());
				Cursor cursor		= dbHelper.searchLegoSets(keyword, priceFrom, priceTo, yearFrom, yearTo, nbPiecesFrom, nbPiecesTo, false, true);
				int count			= 0;

				if(null != cursor)
				{
					if(0 < cursor.getCount())
					{
						cursor.moveToFirst();

						count	= cursor.getInt(0);
					}

					cursor.close();
				}

				if(0 == count)
				{
					Tools.longToast(getApplicationContext(), getApplicationContext().getString(R.string.search_no_results));

					return;
				}

				launchSearchResultActivity(keyword, yearFrom, yearTo, priceFrom, priceTo, nbPiecesFrom, nbPiecesTo);
			}
		});

	}

	private void initiateEditTexts()
	{
		editTextKeyword		= (EditText)findViewById(R.id.searchBox);
		editTextYearFrom	= (EditText)findViewById(R.id.annee_min_search);
		editTextYearTo		= (EditText)findViewById(R.id.annee_max_search);
		editTextPriceFrom	= (EditText)findViewById(R.id.prix_min_search);
		editTextPriceTo 	= (EditText)findViewById(R.id.prix_max_search);
		editTextPieceFrom 	= (EditText)findViewById(R.id.piece_min_search);
		editTextPieceTo		= (EditText)findViewById(R.id.piece_max_search);
	}
}
