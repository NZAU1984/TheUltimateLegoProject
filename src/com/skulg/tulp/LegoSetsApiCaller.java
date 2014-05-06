package com.skulg.tulp;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import ca.umontreal.iro.theultimatelegoproject.UpdateDbActivity;

public class LegoSetsApiCaller extends TulpAPICaller
{

	int buildingInstructionId;
	private UpdateDbActivity updateDbActivity;

	public LegoSetsApiCaller(Context context, dbHelper dbh, UpdateDbActivity argUpdateDbActivity)
	{
		super(context, dbh);

		updateDbActivity	= argUpdateDbActivity;
		// TODO Auto-generated constructor stub
	}

	public LegoSetsApiCaller(Context context, int buildingInstructionId, dbHelper dbh)
	{
		super(context, dbh);

		this.buildingInstructionId = buildingInstructionId;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String doInBackground(String... legoSets)
	{
		// There is only one set because we create a new instance of LegoSetsApiCalled() for each set to be imported.
		String setId	= legoSets[0];
		Boolean removeFromImportTable	= false;
		HttpEntity legoSetPage;

		try
		{
			//Log.d("LegoSetsApiCaller", "fetching set " + setId);
			legoSetPage = getHttpWithTimeout(GET_LEGO_SET_URL + API_KEY + "/" + setId); //getHttp(GET_LEGO_SET_URL + API_KEY + "/" + setId);
			String jsonSet = EntityUtils.toString(legoSetPage, HTTP.UTF_8);
			JSONArray jsArraySet = new JSONArray(jsonSet);
			for (int j = 0; j < jsArraySet.length(); j++)
			{
				JSONObject currentJsonLegoSet = jsArraySet.getJSONObject(j);

				// Parsing info from json

				String name = currentJsonLegoSet.getString("name");
				int boxNumber = currentJsonLegoSet.getInt("boxNo");
				String description = currentJsonLegoSet.getString("description");
				String imageUrl = currentJsonLegoSet.getString("imageUrl");
				String modelName = currentJsonLegoSet.getString("legoModelName");
				int nbPieces = 0;
				if (!currentJsonLegoSet.isNull("pieces"))
				{
					nbPieces = currentJsonLegoSet.getInt("pieces");
				}
				double price = 0;
				if (!currentJsonLegoSet.isNull("price"))
				{
					price = currentJsonLegoSet.getDouble("price");
				}
				int released = 0;
				if (!currentJsonLegoSet.isNull("released"))
				{
					released = currentJsonLegoSet.getInt("released");
				}

				dbh.insertLegoSets(description, boxNumber, imageUrl, name, modelName, nbPieces, price, released);

				removeFromImportTable	= true;
			}
		}
		catch(Exception e)
		{
			if((e instanceof JSONException) || (e instanceof ParseException))
			{
				removeFromImportTable	= true;
			}
			else if((e instanceof ClientProtocolException) || (e instanceof IOException) || (e instanceof IllegalStateException))
			{
				e.printStackTrace();

				updateDbActivity.incrementNumberOfErrors();
			}
		}

		if(removeFromImportTable)
		{
			dbh.removeSetFromImportTable(Integer.valueOf(setId));
		}

		return null;
	}

	@Override
	protected void onPostExecute(String result)
	{
        updateDbActivity.incrementNumberOfSets();
    }
}