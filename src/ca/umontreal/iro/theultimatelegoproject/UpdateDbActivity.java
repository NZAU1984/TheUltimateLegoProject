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
	private Object			lock;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_db);

		// Let's simply disable change of orientation. Otherwise, activity will be recreated and everything starts back. Annoying.
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

		// Dummy object for synchronized blocks.
		lock = new Object();

		// Initializing status variables.
		nbCurrentSets	= 0;
		nbErrorSets		= 0;
		nbTotalSets		= 0;

		canPressBack	= true;

		// Let's get the update strategy that was passed by MainActivity. This tells us if we must fetch all building instructions
		// to get set id's, or to simply check in the import table to get set id's to import.
		strategy		= getIntent().getStringExtra("strategy");

		// Let's init the view components.
		initElements();

		// Let's create a new database helper.
		dbHelper	= new dbHelper(getApplicationContext());

		// Let's open the writable database.
		dbHelper.openWritableDatabase();

		if(strategy.equals("from_building_instructions"))
		{
			// We must fetch all building instructions to get set id's.

			// User can't press back until updating is complete.
			canPressBack	= false;

			// At first, we show an indeterminate progressbar because there is a big delay to fetch / parse all building instructions.
			progressBarProgressBar.setIndeterminate(true);

			// Let's tell the user that we are initializing update.
			textviewPercentageDone.setText(R.string.update_db_initializing);

			// Let's create the asynctask that will fetch all building instructions.
			// When that task has succeeded, AllBuildingInstructionsAPICallerHasFinished() is called.
			// If that task fails (eg. network error / JSON error), AllBuildingInstructionsAPICallerHasFailed() is called.
			(new AllBuildingInstructionsAPICaller(getApplicationContext(), dbHelper, this)).execute("");
		}
		else if(strategy.equals("from_import_table"))
		{
			// We must only fetch set info from the import table. Let's go.
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
		// Let's allow back button only if the used can (all sets imported with or without error / no asynctasks running).
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

		// ActionListener for 'retry' button (when there are errors).
		buttonErrorRetry.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				showProgressLayout();
				importSetsFromDb();
			}
		});

		// At first, we want to show the progress layout (not the error layout).
		showProgressLayout();

		// At first, we set the percentage to 0 because nothing was loaded.
		setCurrentPercentage(0);
	}

	/**
	 * Show the 'progress' layout.
	 */
	private void showProgressLayout()
	{
		relativeLayoutProgress.setVisibility(View.VISIBLE);
		relativeLayoutError.setVisibility(View.GONE);
	}

	/**
	 * Import set info by using sets stored in the import table.
	 */
	private void importSetsFromDb()
	{
		// We don't want the user to press back until loading has finished.
		canPressBack	= false;

		// Let's open the database for writing.
		dbHelper.openWritableDatabase();

		// Let's set the total number of sets to the number of rows in the import table.
		setTotalNumberOfSets(dbHelper.getNumberOfSetsToBeImported());

		// Let's set the current percentage to 0.
		setCurrentPercentage(0);

		// Reinitializing status variables.
		nbCurrentSets	= 0;
		nbErrorSets		= 0;

		// Let's launch the ImportLegoSets asynctask.
		(new ImportLegoSets(getApplicationContext(), dbHelper, this)).execute("");
	}

	/**
	 * Sets the total number of sets to be loaded.
	 */
	public void setTotalNumberOfSets(int argNbSets)
	{
		// Because threads might call this method at the same time, we use a synchronized block to prevent errors
		// in the incrementation process.
		synchronized (lock)
		{
			nbTotalSets	= argNbSets;
		}
	}

	/**
	 * Increments the number of (network) errors.
	 */
	public void incrementNumberOfErrors()
	{
		// Because threads might call this method at the same time, we use a synchronized block to prevent errors
		// in the incrementation process.
		synchronized(lock)
		{
			++nbErrorSets;
		}
	}

	/**
	 * Increments the number of sets loaded (or associated with a (network) error).
	 */
	public void incrementNumberOfSets()
	{
		// Because threads might call this method at the same time, we use a synchronized block to prevent errors
		// in the incrementation process.
		synchronized (lock)
		{
			++nbCurrentSets;

			if(0 != nbTotalSets)
			{
				// Simple check to prevent division by 0, in case...
				setCurrentPercentage((double) nbCurrentSets / nbTotalSets);
			}

			if(nbCurrentSets == nbTotalSets)
			{
				// All sets were loaded.
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
		// Now the user can press back.
		canPressBack	= true;

		// Database doesn't need to be writable anymore so let's close it.
		dbHelper.closeWritableDatabase();

		if(0 != nbErrorSets)
		{
			// If there were (network) errors, let's change the visible layout to show there were errors and let's update the 'error status'
			// according to if all sets failed or only some of them.
			relativeLayoutProgress.setVisibility(View.GONE);
			relativeLayoutError.setVisibility(View.VISIBLE);

			textviewErrorStatus.setText((nbErrorSets == nbTotalSets) ? R.string.update_db_total_errors_status : R.string.update_db_partial_errors_status);
		}
		else
		{
			// Everything went without a single error. Let's press back for the user and go back to MainActivity.
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

		// Let's import sets from the import table.
		importSetsFromDb();
	}

	/**
	 * Called then the asynctask AllBuildingInstructionsAPICaller failed to fetch or parse building instructions.
	 */
	public void AllBuildingInstructionsAPICallerHasFailed()
	{
		// TODO !!
	}
}
