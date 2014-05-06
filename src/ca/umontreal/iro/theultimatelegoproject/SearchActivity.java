package ca.umontreal.iro.theultimatelegoproject;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class SearchActivity extends Activity
{

	private EditText searchBox, anneeMinBox, anneeMaxBox, prixMinBox, prixMaxBox, pieceMinBox, pieceMaxBox;
	private String searchText;
	private int anneeMin, anneeMax, prixMin, prixMax, pieceMin, pieceMax;
	private View searchGo;
	private static ArrayList<String> historique;




	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		// Show the Up button in the action bar.
		setupActionBar();

		initiateButton();


	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
	    super.onPostCreate(savedInstanceState);

	    // Sync the toggle state after onRestoreInstanceState has occurred.
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
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	private void launchSearchResultActivity()
	{
		String sqlRequest = "SELECT * FROM sets WHERE id=456";

		Intent launchSearchResultActivity = new Intent(this, SearchResultActivity.class);

		launchSearchResultActivity.putExtra("sql_request", sqlRequest);

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
				launchSearchResultActivity();

			}
		});

	}


	private void initiateEditTexts() {

		//KeyWord EditText

		searchBox = (EditText)findViewById(R.id.searchBox);
		 TextWatcher textWatcher = new TextWatcher(){

			    @Override
			    public void onTextChanged(CharSequence s, int start, int before, int count) {}

			    @Override
			    public void beforeTextChanged(CharSequence s, int start, int count,
			      int after) {}

			    @Override
			    public void afterTextChanged(Editable s) {

			    	searchText = s.toString();
			    }
		};
		searchBox.addTextChangedListener(textWatcher);


		//anne_min EditText

		anneeMinBox = (EditText)findViewById(R.id.annee_min_search);
		 TextWatcher textWatcher2 = new TextWatcher(){

			    @Override
			    public void onTextChanged(CharSequence s, int start, int before, int count) {}

			    @Override
			    public void beforeTextChanged(CharSequence s, int start, int count,
			      int after) {}

			    @Override
			    public void afterTextChanged(Editable s) {

			    	anneeMin = Integer.parseInt(s.toString());
			    }
		};
		anneeMinBox.addTextChangedListener(textWatcher2);

		//annee_max EditText

		anneeMaxBox = (EditText)findViewById(R.id.annee_max_search);
		 TextWatcher textWatcher3 = new TextWatcher(){

			    @Override
			    public void onTextChanged(CharSequence s, int start, int before, int count) {}

			    @Override
			    public void beforeTextChanged(CharSequence s, int start, int count,
			      int after) {}

			    @Override
			    public void afterTextChanged(Editable s) {

			    	anneeMax = Integer.parseInt(s.toString());
			    }
		};
		anneeMaxBox.addTextChangedListener(textWatcher3);

		//prix_min EditText

		prixMinBox = (EditText)findViewById(R.id.prix_min_search);
		 TextWatcher textWatcher4 = new TextWatcher(){

			    @Override
			    public void onTextChanged(CharSequence s, int start, int before, int count) {}

			    @Override
			    public void beforeTextChanged(CharSequence s, int start, int count,
			      int after) {}

			    @Override
			    public void afterTextChanged(Editable s) {

			    	prixMin = Integer.parseInt(s.toString());
			    }
		};
		prixMinBox.addTextChangedListener(textWatcher4);

		//prix_max EditText

		prixMaxBox = (EditText)findViewById(R.id.prix_max_search);
		 TextWatcher textWatcher5 = new TextWatcher(){

			    @Override
			    public void onTextChanged(CharSequence s, int start, int before, int count) {}

			    @Override
			    public void beforeTextChanged(CharSequence s, int start, int count,
			      int after) {}

			    @Override
			    public void afterTextChanged(Editable s) {

			    	prixMax = Integer.parseInt(s.toString());
			    }
		};
		prixMaxBox.addTextChangedListener(textWatcher5);

		//piece_min EditText

		pieceMinBox = (EditText)findViewById(R.id.piece_min_search);
		 TextWatcher textWatcher6 = new TextWatcher(){

			    @Override
			    public void onTextChanged(CharSequence s, int start, int before, int count) {}

			    @Override
			    public void beforeTextChanged(CharSequence s, int start, int count,
			      int after) {}

			    @Override
			    public void afterTextChanged(Editable s) {

			    	pieceMin = Integer.parseInt(s.toString());
			    }
		};
		pieceMinBox.addTextChangedListener(textWatcher6);


		//piece_max EditText

		pieceMaxBox = (EditText)findViewById(R.id.piece_max_search);
		 TextWatcher textWatcher7 = new TextWatcher(){

			    @Override
			    public void onTextChanged(CharSequence s, int start, int before, int count) {}

			    @Override
			    public void beforeTextChanged(CharSequence s, int start, int count,
			      int after) {}

			    @Override
			    public void afterTextChanged(Editable s) {

			    	pieceMax = Integer.parseInt(s.toString());
			    }
		};
		pieceMaxBox.addTextChangedListener(textWatcher7);
	}





	//private void toSqlDemand(){}

}
