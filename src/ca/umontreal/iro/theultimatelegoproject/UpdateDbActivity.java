package ca.umontreal.iro.theultimatelegoproject;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.skulg.tulp.dbHelper;

public class UpdateDbActivity extends android.support.v4.app.FragmentActivity implements TaskCallbacks
{
	private static final String TAG_TASK_FRAGMENT = "update_db_fragment";
	private UpdateDbFragment updateDbFragment;

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
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

		lock = new Object();

		// Initializing status variables.
		nbCurrentSets	= 0;
		nbErrorSets		= 0;
		nbTotalSets		= 0;

		canPressBack	= false;

		initElements();

		strategy	= getIntent().getStringExtra("strategy");

		manageFragment(false);
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

				deleteFragment();

				manageFragment(true);
			}
		});

		relativeLayoutProgress.setVisibility(View.VISIBLE);
		relativeLayoutError.setVisibility(View.GONE);
		setCurrentPercentage(0);
	}

	private void manageFragment(Boolean forceCreate)
	{
		FragmentManager fragmentManager	= getSupportFragmentManager();
		updateDbFragment				= (UpdateDbFragment) fragmentManager.findFragmentByTag(TAG_TASK_FRAGMENT);

	    if(forceCreate || (null == updateDbFragment))
	    {
	    	updateDbFragment	= new UpdateDbFragment();

	    	Bundle arguments	= new Bundle();

	    	arguments.putString("strategy", strategy);

	    	updateDbFragment.setArguments(arguments);

	    	fragmentManager.beginTransaction().add(updateDbFragment, TAG_TASK_FRAGMENT).commit();
	    }
	}

	private void deleteFragment()
	{
		FragmentManager fragmentManager	= getSupportFragmentManager();
		updateDbFragment				= (UpdateDbFragment) fragmentManager.findFragmentByTag(TAG_TASK_FRAGMENT);

		if(null != updateDbFragment)
		{
			fragmentManager.beginTransaction().remove(updateDbFragment).commit();
		}

		updateDbFragment	= null;
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

	public void showLoadingLayout()
	{
		relativeLayoutProgress.setVisibility(View.VISIBLE);
		relativeLayoutError.setVisibility(View.GONE);

	}

	public void showErrorLayout()
	{
		relativeLayoutProgress.setVisibility(View.GONE);
		relativeLayoutError.setVisibility(View.VISIBLE);

		textviewErrorStatus.setText(R.string.update_db_partial_errors_status);
	}


	@Override
	public void onPreExecute(String fromWho)
	{
		if(fromWho.equals("from_building_instructions"))
		{
			progressBarProgressBar.setIndeterminate(true);
			textviewPercentageDone.setText(R.string.update_db_initializing);
		}
	}

	@Override
	public void onProgressUpdate(double fraction)
	{
		double percentage	= Math.floor(fraction * progressBarProgressBar.getMax());
		textviewPercentageDone.setText(String.format(getString(R.string.update_db_percentage_done), percentage));
		progressBarProgressBar.setProgress((int) percentage);
	}

	@Override
	public void onCancelled()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onPostExecute(String fromWho, Boolean success)
	{
		if(fromWho.equals("from_building_instructions"))
		{
			progressBarProgressBar.setIndeterminate(false);

			if(!success)
			{
				strategy	= "from_building_instructions";

				showErrorLayout();
			}
		}
		else if(fromWho.equals("from_import_table"))
		{
			if(!success)
			{
				strategy	= "from_import_table";

				showErrorLayout();
			}
			else
			{
				canPressBack	= true;

				onBackPressed();
			}
		}
	}
}
