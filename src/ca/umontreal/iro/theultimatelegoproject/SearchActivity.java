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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;

public class SearchActivity extends Activity
{

	private EditText searchBox;
	private EditText pieceText;
	private Button searchGo;
	private static ArrayList<String> historique;

	private String[] tableauAnnee = new String[]{"","1970 - 1980","1980 - 1990","1990 - 2000","2000 - 2010","> 2010"};
	private String[] tableauPrix = new String[]{"","0-50","50-100","100-150","150-200",">200"};

	private int anneeSelectPosition;
	private int prixSelectPosition;
	private String searchText;
	private int piecesMaximum;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		tableauAnnee[0]	= getApplicationContext().getString(R.string.search_select_year_range);
		tableauPrix[0]	= getApplicationContext().getString(R.string.search_select_price_range);
		// Show the Up button in the action bar.
		setupActionBar();

		initiateComponents();


	}

	/* � d�beug�
	@Override
	public void onStop(){

		historique.add(
		((searchText==null)?"":searchText) + ";" + this.getAnnee() + ";" + this.getPrix() + ";" + this.getPiecesMax());
	}
 	*/

	private void initiateComponents(){

				//boutton de recherche
				initiateButton();

				//Barre de recherche
				initiateEditText();

				//menu d�roulant ann�e
				initiateSpinnerAnnee();

				//menu d�roulant prix
				initiateSpinnerPrix();

				//Seek Bar nombre de pi�ces
				initiateSeekBar();

				//Formule la demande sql
				//toSqlDemand();

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
		searchGo = (Button) findViewById(R.id.button_search);

		searchGo.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				launchSearchResultActivity();

			}
		});

	}


	private void initiateEditText() {

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

	}


	private void initiateSpinnerAnnee(){


		final Spinner spinnerAnnee = (Spinner)findViewById(R.id.spinnerAnnee);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,tableauAnnee);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinnerAnnee.setAdapter(adapter);
		spinnerAnnee.setEnabled(true);

		anneeSelectPosition = spinnerAnnee.getSelectedItemPosition();

	}


	private void initiateSpinnerPrix(){

		final Spinner spinnerPrix = (Spinner)findViewById(R.id.spinnerPrix);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,tableauPrix);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinnerPrix.setAdapter(adapter);
		spinnerPrix.setEnabled(true);

		prixSelectPosition = spinnerPrix.getSelectedItemPosition();

	}


	private void initiateSeekBar(){

		final SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar);
		pieceText = (EditText)findViewById(R.id.nombre_pieces_text);

		//on ne modifi pas ce champ manuellement, il s'adapte � la seekBar
		pieceText.setClickable(false);

		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			//La barre va de 0 � 100, j'ai multipli� par 30 sans vraiment savoir si 3000 �tait le nombre de pi�ces maximum
	        @Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
	            //pieceText.setText("Nombres de pi�ces maximum :" + (seekBar.getProgress()*30));
	        	int numberOfPieces	= seekBar.getProgress() * 30;
	        	pieceText.setText(String.format(getApplicationContext().getString(R.string.search_number_of_pieces), numberOfPieces));
	        }

	        @Override
			public void onStartTrackingTouch(SeekBar seekBar) {

	        }

	        @Override
			public void onStopTrackingTouch(SeekBar seekBar) {
	        	piecesMaximum = seekBar.getProgress()*30;
	        }});
	}

	public ArrayList<String> getHistorique(){
		return SearchActivity.historique;
	}

	public String getAnnee(){

		String temp="";
		if(anneeSelectPosition!=0){
			temp = tableauAnnee[anneeSelectPosition];
		};
		return temp;
	}

	public String getPrix(){

		String temp="";
		if(prixSelectPosition!=0){
			temp = tableauPrix[prixSelectPosition];
		};
		return temp;
	}

	public String getPiecesMax(){
		return (""+piecesMaximum);
	}

//	private void toSqlDemand(){}

}
