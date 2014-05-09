package ca.umontreal.iro.theultimatelegoproject;

import org.apache.http.HttpEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.skulg.tulp.LegoSetsApiCaller1;
import com.skulg.tulp.TulpAPICaller;
import com.skulg.tulp.dbHelper;

/*
 * Source : http://www.androiddesignpatterns.com/2013/04/retaining-objects-across-config-changes.html
 */

/**
 * This Fragment manages a single background task and retains itself across
 * configuration changes.
 */
public class UpdateDbFragment extends Fragment implements SubTaskCallbacks
{
	private TaskCallbacks callingActivity;
	private ImportAllBuildingInstructionsAsyncTask specificBuildingInstructionsAsyncTask;

	private dbHelper dbHelper;

	private Object lock			= new Object();
	private int nbTotalSets		= 0;
	private int nbCurrentSets	= 0;
	private int nbErrorSets		= 0;

	/**
	 * Hold a reference to the parent Activity so we can report the task's
	 * current progress and results. The Android framework will pass us a
	 * reference to the newly created Activity after each configuration change.
	 */
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);

		callingActivity	= (TaskCallbacks) activity;
	}

	/**
	 * This method will only be called once when the retained Fragment is first
	 * created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Retain this fragment across configuration changes.
		setRetainInstance(true);

		//String theId	= getArguments().getString("building_instructions_id");

		dbHelper	= new dbHelper(getActivity().getApplicationContext());

		if(getArguments().getString("strategy").equals("from_building_instructions"))
		{
			importAllBuildingInstructions(); //getActivity().getApplicationContext());
		}
		else
		{
			Log.d("fragment", "== importSetsFromDb ==");
			importSetsFromDb();
		}

		// Create and execute the background task.
		//specificBuildingInstructionsAsyncTask = new SpecificBuildingInstructionsAsyncTask(getActivity().getApplicationContext(), dbHelper, theId);
		//specificBuildingInstructionsAsyncTask.execute();
	}

	/**
	 * Set the callback to null so we don't accidentally leak the Activity
	 * instance.
	 */
	@Override
	public void onDetach()
	{
		super.onDetach();
		callingActivity = null;
	}

	public void importAllBuildingInstructions()
	{
		(new ImportAllBuildingInstructionsAsyncTask(null, dbHelper)).execute("");
	}

	public void importSetsFromDb()
	{
		//(new ImportSetsFromDbAsyncTask(null, dbHelper)).execute("");
		nbTotalSets	= dbHelper.getNumberOfSetsToBeImported();

		if (null != callingActivity)
		{
			callingActivity.onPreExecute("from_import_table");
			callingActivity.onProgressUpdate(0);
		}

		Cursor cursor	= dbHelper.getAllSetsToBeImported();

		if(null == cursor)
		{
			nbTotalSets	= 1;
			nbErrorSets	= 1;

			onSubTaskError();

			return;
		}

		int keyIndex					= cursor.getColumnIndex(dbHelper.KEY_ID);
		int buildingInstructionsIdIndex	= cursor.getColumnIndex(dbHelper.IMPORT_BUILDING_INSTRUCTIONS_ID);

		cursor.moveToFirst();

		while(!cursor.isAfterLast())
		{
			String setId					= cursor.getString(keyIndex);
			String buildingInstructionId	= cursor.getString(buildingInstructionsIdIndex);

			new LegoSetsApiCaller1(null, dbHelper, this).execute(setId, buildingInstructionId);

			cursor.moveToNext();
		}

		cursor.close();

//		return true;

	}

	private void updateAndCheckTotal()
	{
		double fraction	= 0f;

		if(0 != nbTotalSets)
		{
			fraction	= ((float) nbCurrentSets) / nbTotalSets;
		}

		callingActivity.onProgressUpdate(fraction);

		if(nbCurrentSets == nbTotalSets)
		{
			Log.d("kaka", "total " + nbTotalSets + ", current = " + nbCurrentSets + ", errors = " + nbErrorSets);
			if(0 != nbErrorSets)
			{
				callingActivity.onPostExecute("from_import_table", false);

				return;
			}

			callingActivity.onPostExecute("from_import_table", true);
		}
	}

	@Override
	public void onSubTaskError()
	{
		Log.d("HHHHHHHHH", "onSubTaskError");

		synchronized(lock)
		{
			++nbCurrentSets;
			++nbErrorSets;
		}

		updateAndCheckTotal();
	}

	@Override
	public void onSubTaskSuccess()
	{
		//Log.d("crotte", "onSubTaskSuccess");

		synchronized(lock)
		{
			++nbCurrentSets;
		}

		updateAndCheckTotal();
	}

	private class ImportAllBuildingInstructionsAsyncTask extends TulpAPICaller
	{
		public ImportAllBuildingInstructionsAsyncTask(Context context, dbHelper dbh)
		{
			super(context, dbh);
		}

		@Override
		protected void onPreExecute()
		{
			if (null != callingActivity)
			{
				callingActivity.onPreExecute("from_building_instructions");
			}
		}


		@Override
		protected void onProgressUpdate(Float... fraction)
		{
			if (null != callingActivity)
			{
				callingActivity.onProgressUpdate(fraction[0]);
			}
		}

		@Override
		protected void onCancelled()
		{
			if (null != callingActivity)
			{
				callingActivity.onCancelled();
			}
		}

		@Override
		protected void onPostExecute(Boolean result)
		{
			if (null != callingActivity)
			{
				callingActivity.onPostExecute("from_building_instructions", result);
			}

			importSetsFromDb();
		}

		@Override
		protected Boolean doInBackground(String... arg0)
		{
			Boolean success	= false;

			dbh.openWritableDatabase();

			try
			{
				HttpEntity buildingInstructionsPage		= getHttp(GET_ALL_BUILDINGS_INSTRUCTIONS_URL);
				String json                       		= EntityUtils.toString(buildingInstructionsPage, HTTP.UTF_8);
				JSONArray jsArrayBuildingInstructions	= new JSONArray(json);

				for (int i = 0, nbInstructions = jsArrayBuildingInstructions.length(); i < nbInstructions; i++)
				{

					JSONObject currentJsonBuildingInstuction	= jsArrayBuildingInstructions.getJSONObject(i);
					String buildingInstructionName				= currentJsonBuildingInstuction.getString("name");
					String buildingInstructionId				= currentJsonBuildingInstuction.getString("idInstruction");

					if (TextUtils.isDigitsOnly(buildingInstructionName))
					{
						Log.d("private task", "insert : " + buildingInstructionName + ", " + buildingInstructionId);
						dbh.insertImportSet(buildingInstructionName, buildingInstructionId);
					}
				}

				success	= true;
			}
			catch (Exception e)
			{
				success	= false;

				e.printStackTrace();
			}
			finally
			{
				dbh.closeWritableDatabase();
			}

			return success;
		}
	}

	/*private class ImportSetsFromDbAsyncTask extends TulpAPICaller implements SubTaskCallbacks
	{
		private Object lock			= new Object();
		private int nbTotalSets		= 0;
		private int nbCurrentSets	= 0;
		private int nbErrorSets		= 0;

		public ImportSetsFromDbAsyncTask(Context context, dbHelper dbh)
		{
			super(context, dbh);
		}

		@Override
		protected void onPreExecute()
		{
			if (null != callingActivity)
			{
				nbTotalSets	= dbh.getNumberOfSetsToBeImported();

				callingActivity.onPreExecute("from_import_table");
				callingActivity.onProgressUpdate(0);
			}
		}


		@Override
		protected void onProgressUpdate(Float... fraction)
		{
			if (null != callingActivity)
			{
				callingActivity.onProgressUpdate(fraction[0]);
			}
		}

		@Override
		protected void onCancelled()
		{
			if (null != callingActivity)
			{
				callingActivity.onCancelled();
			}
		}

		@Override
		protected void onPostExecute(Boolean result)
		{
			if (null != callingActivity)
			{
				callingActivity.onPostExecute("from_import_table", result);
			}
		}

		@Override
		protected Boolean doInBackground(String... arg0)
		{
			Cursor cursor	= dbh.getAllSetsToBeImported();

			int keyIndex					= cursor.getColumnIndex(dbh.KEY_ID);
			int buildingInstructionsIdIndex	= cursor.getColumnIndex(dbh.IMPORT_BUILDING_INSTRUCTIONS_ID);

			cursor.moveToFirst();

			while(!cursor.isAfterLast())
			{
				String setId					= cursor.getString(keyIndex);
				String buildingInstructionId	= cursor.getString(buildingInstructionsIdIndex);

				new LegoSetsApiCaller1(null, dbh, this).execute(setId, buildingInstructionId);

				cursor.moveToNext();
			}

			cursor.close();

			return true;
		}

		@Override
		public void onSubTaskError()
		{
			synchronized(lock)
			{
				++nbErrorSets;
			}
		}

		@Override
		public void onSubTaskSuccess()
		{
			synchronized(lock)
			{
				++nbCurrentSets;

				if(0 != nbTotalSets)
				{
					publishProgress(((float) nbCurrentSets) / nbTotalSets);
				}
			}
		}
	}*/
}
