package ca.umontreal.iro.theultimatelegoproject;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.skulg.tulp.AllBuildingInstructionsAPICaller;
import com.skulg.tulp.ImportLegoSets;
import com.skulg.tulp.dbHelper;

public class UpdateDbActivity extends Activity
{
	private RelativeLayout	relativeLayoutProgress;
	private RelativeLayout	relativeLayoutError;
	private ProgressBar		progressBarProgressBar;
	private TextView		textviewPercentageDone;
	private TextView		textviewErrorStatus;
	private Button			buttonErrorRetry;
	private dbHelper		dbHelper;
	private int 			nbTotalSets;
	private int 			nbCurrentSets;
	private int 			nbErrorSets;
	private String 			strategy;
	private Boolean 		canPressBack;
	public Object			lock;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_db);
		// Let's simply disable change of orientation. Otherwise, activity will be recreated and everything starts back. Annoying.
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

		lock = new Object();

		// Initializing status variables.
		nbCurrentSets	= 0;
		setNbErrorSets(0);
		nbTotalSets		= 0;

		canPressBack	= true;
		strategy		= getIntent().getStringExtra("strategy");
		initElements();

		dbHelper	= new dbHelper(getApplicationContext());

		dbHelper.openWritableDatabase();

		if(strategy.equals("from_building_instructions"))
		{
			// We must fetch all building instructions to get set id's.
			canPressBack	= false;
			progressBarProgressBar.setIndeterminate(true);

			// Let's tell the user that we are initializing update.
			textviewPercentageDone.setText(R.string.update_db_initializing);

			// Fetching all building instructions.
			(new AllBuildingInstructionsAPICaller(getApplicationContext(), dbHelper, this)).execute("");
		}
		else if(strategy.equals("from_import_table"))
		{
			importSetsFromDb();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.update_db, menu);
		return true;
	}

	@Override
	public void onBackPressed()
	{
		if(canPressBack)
		{
			super.onBackPressed();
		}
	}

	/**
	 * Initializes view components.
	 */
	private void initElements()
	{
		progressBarProgressBar	= (ProgressBar) 	findViewById(R.id.progressbar_progressbar);
		textviewPercentageDone	= (TextView)		findViewById(R.id.textview_percentage_done);
		textviewErrorStatus		= (TextView)		findViewById(R.id.textview_error_status);

		// There are two layouts in this activity :
		//     1. 'progress' layout when we are fetching building instructions / set info;
		//     2. 'error' layout when all asynctasks are finished but some errors occured.
		relativeLayoutProgress	= (RelativeLayout)	findViewById(R.id.relativelayout_progress_layout);
		relativeLayoutError		= (RelativeLayout)	findViewById(R.id.relativelayout_error_layout);

		buttonErrorRetry		= (Button)			findViewById(R.id.button_retry);

		buttonErrorRetry.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				relativeLayoutProgress.setVisibility(View.VISIBLE);
				relativeLayoutError.setVisibility(View.GONE);
				importSetsFromDb();
			}
		});

		relativeLayoutProgress.setVisibility(View.VISIBLE);
		relativeLayoutError.setVisibility(View.GONE);
		setCurrentPercentage(0);
	}

	/**
	 * Import set info by using sets stored in the import table.
	 */
	private void importSetsFromDb()
	{
		canPressBack	= false;
		dbHelper.openWritableDatabase();
		synchronized (lock)
		{
			nbTotalSets	= dbHelper.getNumberOfSetsToBeImported();
		}
		setCurrentPercentage(0);

		// Reinitializing status variables.
		nbCurrentSets	= 0;
		setNbErrorSets(0);

		(new ImportLegoSets(getApplicationContext(), dbHelper, this)).execute("");
	}

	/**
	 * Increments the number of sets loaded (or associated with a (network) error).
	 */
	public void incrementNumberOfSets()
	{
		synchronized (lock)
		{
			++nbCurrentSets;
			if(0 != nbTotalSets)
			{
				setCurrentPercentage((double) nbCurrentSets / nbTotalSets);
			}
			if(nbCurrentSets == nbTotalSets)
			{
				allSetsLoaded();
			}
		}
	}

	/**
	 * Shows the current loading percentage in progressbar and textview.
	 * @param fraction the percentage of loading (between 0 and 1)
	 */
	private void setCurrentPercentage(double fraction)
	{
		double percentage	= Math.floor(fraction * progressBarProgressBar.getMax());
		textviewPercentageDone.setText(String.format(getString(R.string.update_db_percentage_done), percentage) + " " + nbCurrentSets + "/" + nbTotalSets);
		progressBarProgressBar.setProgress((int) percentage);
	}

	/**
	 * Handles the 'all sets loaded' event. Everything might have gone without errors or not.
	 */
	private void allSetsLoaded()
	{
		canPressBack	= true;

		dbHelper.closeWritableDatabase();

		if(0 != getNbErrorSets())
		{
			// If there were (network) errors, let's change the visible layout to show there were errors and let's update the 'error status'
			// according to if all sets failed or only some of them.
			relativeLayoutProgress.setVisibility(View.GONE);
			relativeLayoutError.setVisibility(View.VISIBLE);

			textviewErrorStatus.setText((getNbErrorSets() == nbTotalSets) ? R.string.update_db_total_errors_status : R.string.update_db_partial_errors_status);
		}
		else
		{
			onBackPressed();
		}
	}

	/**
	 * Called then the asynctask AllBuildingInstructionsAPICaller fetched all building instructions and stored numeric
	 * set id's in the import table.
	 */
	public void AllBuildingInstructionsAPICallerHasFinished()
	{
		// We will now load a precise number of sets so the progressbar will not be indeterminate.
		progressBarProgressBar.setIndeterminate(false);
		importSetsFromDb();
	}

	/**
	 * Called then the asynctask AllBuildingInstructionsAPICaller failed to fetch or parse building instructions.
	 */
	public void AllBuildingInstructionsAPICallerHasFailed()
	{
		// TODO !!
	}

	public int getNbErrorSets() {
		return nbErrorSets;
	}

	public void setNbErrorSets(int nbErrorSets) {
		this.nbErrorSets = nbErrorSets;
	}
}
