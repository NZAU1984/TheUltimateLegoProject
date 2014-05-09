package com.skulg.tulp;

import android.content.Context;
import android.database.Cursor;
import ca.umontreal.iro.theultimatelegoproject.Tools;
import ca.umontreal.iro.theultimatelegoproject.UpdateDbActivity;

public class ImportLegoSets extends TulpAPICaller
{

	private UpdateDbActivity	updateDbActivity;
	String lalala;

	public ImportLegoSets(Context context, dbHelper dbh, UpdateDbActivity argUpdateDbActivity)
	{
		super(context, dbh);

		updateDbActivity	= argUpdateDbActivity;
	}

	@Override
	protected String doInBackground(String... arg0)
	{
		//Log.d("GetAllLegoSets", "doInBack...");

		Cursor cursor	= dbh.getAllSetsToBeImported();

		int keyIndex					= cursor.getColumnIndex(dbh.KEY_ID);
		int buildingInstructionsIdIndex	= cursor.getColumnIndex(dbh.IMPORT_BUILDING_INSTRUCTIONS_ID);
		cursor.moveToFirst();
		while(!cursor.isAfterLast())
		{
			String setId					= cursor.getString(keyIndex);
			String buildingInstructionId	= cursor.getString(buildingInstructionsIdIndex);
			if(Tools.currentSets < Tools.totalSets)
			{
				new LegoSetsApiCaller(context, dbh, updateDbActivity).execute(setId, buildingInstructionId);

				++Tools.currentSets;
			}
			cursor.moveToNext();
		}
		cursor.close();
		return null;
	}
	
	@Override
	protected void onPostExecute(String result)
    {

    }

}
