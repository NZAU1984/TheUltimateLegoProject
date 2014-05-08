package ca.umontreal.iro.theultimatelegoproject;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.skulg.tulp.TulpAPICaller;
import com.skulg.tulp.dbHelper;

/*
 * Source : http://www.androiddesignpatterns.com/2013/04/retaining-objects-across-config-changes.html
 */

/**
 * This Fragment manages a single background task and retains itself across
 * configuration changes.
 */
public class SpecificBuildingInstructionsFragment extends Fragment
{

	/**
	 * Callback interface through which the fragment will report the task's
	 * progress and results back to the Activity.
	 */
	static interface TaskCallbacks
	{
		void onPreExecute();

		void onProgressUpdate(int percent);

		void onCancelled();

		void onPostExecute();
	}

	private TaskCallbacks callingActivity;
	private SpecificBuildingInstructionsAsyncTask specificBuildingInstructionsAsyncTask;


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

		String theId	= getArguments().getString("building_instructions_id");

		Log.d("== fragment ==", "theId = " + theId);

		dbHelper dbHelper	= new dbHelper(getActivity().getApplicationContext());


		// Create and execute the background task.
		specificBuildingInstructionsAsyncTask = new SpecificBuildingInstructionsAsyncTask(getActivity().getApplicationContext(), dbHelper, theId);
		specificBuildingInstructionsAsyncTask.execute();
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

	/**
	 * A dummy task that performs some (dumb) background work and proxies
	 * progress updates and results back to the Activity.
	 *
	 * Note that we need to check if the callbacks are null in each method in
	 * case they are invoked after the Activity's and Fragment's onDestroy()
	 * method have been called.
	 */
	private class SpecificBuildingInstructionsAsyncTask extends TulpAPICaller
	{
		private String buildingInstructionsId;

		public SpecificBuildingInstructionsAsyncTask(Context context, dbHelper dbh, String argBuildingInstructionsId)
		{
			super(context, dbh);

			buildingInstructionsId	= argBuildingInstructionsId;
		}

		@Override
		protected void onPreExecute()
		{
			if (null != callingActivity)
			{
				callingActivity.onPreExecute();
			}
		}


		protected void onProgressUpdate(Integer... percent)
		{
			if (null != callingActivity)
			{
				callingActivity.onProgressUpdate(percent[0]);
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
		protected void onPostExecute(String result)
		{
			if (null != callingActivity)
			{
				callingActivity.onPostExecute();
			}
		}

		@Override
		protected String doInBackground(String... arg0)
		{
			HttpEntity buildingInstructionsPage;
			try
			{
				buildingInstructionsPage					= getHttp(GET_ALL_BUILDINGS_INSTRUCTIONS_URL + "/" + buildingInstructionsId);
				String json									= EntityUtils.toString(buildingInstructionsPage, HTTP.UTF_8);
				JSONObject currentJsonBuildingInstuction	= new JSONObject(json);
				JSONArray stepgroups;

				//String buildingInstructionsDescription = currentJsonBuildingInstuction.getString("description");
				//int idInstruction = currentJsonBuildingInstuction.getInt("idInstruction");
				//String buildingInstuctionsName = currentJsonBuildingInstuction.getString("name");
				//String shortcutPicture = currentJsonBuildingInstuction.getString("shortcutPicture");

				try
				{
					stepgroups	= currentJsonBuildingInstuction.getJSONArray("stepGroups");

					Log.d("TULP", "FOUND A STEPGROUP");

					for (int j = 0, stepgroupsLength = stepgroups.length(); j < stepgroupsLength; j++)
					{
						JSONObject currentStepGroup	= stepgroups.getJSONObject(j);
						JSONArray filenames			= currentStepGroup.getJSONArray("fileNames");

						for (int k = 0, filenamesLength = filenames.length(); k < filenamesLength; k++)
						{
							long currentImageId = dbh.insertImages("" + filenames.getString(k), Integer.valueOf(buildingInstructionsId));

							Log.d("TULP", "Filename :" + filenames.getString(k));
						}
					}
				}
				catch (JSONException e)
				{
					e.printStackTrace();
				}
				//Log.d("TULP", "Instructions name :" + buildingInstuctionsName);
				//Log.d("TULP", "Instructions description :" + buildingInstructionsDescription);

				//dbh.insertBuildingInstructions(idInstruction, buildingInstructionsDescription, shortcutPicture,
				//		buildingInstuctionsName);
				//if (TextUtils.isDigitsOnly(buildingInstuctionsName))
				//{
					//new LegoSetsApiCaller(context, dbh, updateDbActivity).execute(buildingInstuctionsName);
				//}

			}
			catch (ParseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}
	}
}
