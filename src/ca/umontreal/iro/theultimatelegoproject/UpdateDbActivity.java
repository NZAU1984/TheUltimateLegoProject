package ca.umontreal.iro.theultimatelegoproject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.skulg.tulp.AllBuildingInstructionsAPICaller;
import com.skulg.tulp.GetAllLegoSets;
import com.skulg.tulp.dbHelper;

public class UpdateDbActivity extends Activity
{
	private ProgressBar	progressBarProgressBar;
	private TextView	textviewPercentageDone;
	private dbHelper	dbHelper;
	private GetAllLegoSets getAllLegoSets;
	private AllBuildingInstructionsAPICaller allBuildingInstructionsAPICaller;
	private int nbTotalSets;
	private int nbCurrentSets;



	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_db);

		dbHelper	= new dbHelper(getApplicationContext());

		/*
		 * insertLegoSets(String description, int boxNumber,
			String imageUrl, String name, String modelName, int nbPieces,
			double price, int released)
		 */

		//dbHelper.insertLegoSets("test", 123, "/", "lego set", "modelname", 123, 50.50, 2014);

		//getAllLegoSets	= new GetAllLegoSets(getApplicationContext(), dbHelper, this);

		//getAllLegoSets.execute("");

		allBuildingInstructionsAPICaller	= new AllBuildingInstructionsAPICaller(getApplicationContext(), dbHelper, this);

		allBuildingInstructionsAPICaller.execute("");

		//LegoSetsApiCaller legoSetsApiCaller	= new LegoSetsApiCaller(getApplicationContext(), dbHelper);

		//legoSetsApiCaller.execute("60031");

		initElements();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.update_db, menu);
		return true;
	}

	private void initElements()
	{
		progressBarProgressBar	= (ProgressBar) findViewById(R.id.progressbar_progressbar);
		textviewPercentageDone	= (TextView)	findViewById(R.id.textview_percentage_done);

		setCurrentPercentage(0);
	}

	public void setTotalNumberOfSets(int argNbSets)
	{
		nbTotalSets	= argNbSets;
	}

	public void incrementNumberOfSets()
	{
		++nbCurrentSets;

		if(0 != nbTotalSets)
		{
			setCurrentPercentage((double) nbCurrentSets / nbTotalSets);
		}
		else
		{
			Log.d("...", "number of sets is 0 !!");
		}
	}

	private void setCurrentPercentage(double fraction)
	{
		double percentage	= Math.floor(fraction * progressBarProgressBar.getMax());
		textviewPercentageDone.setText(String.format(getString(R.string.update_db_percentage_done), percentage) + " " + nbCurrentSets + "/" + nbTotalSets);
		progressBarProgressBar.setProgress((int) percentage);
	}

	public void setText(String text)
	{
		TextView chose	= (TextView) findViewById(R.id.fname);
		chose.setText(text);
	}

}
