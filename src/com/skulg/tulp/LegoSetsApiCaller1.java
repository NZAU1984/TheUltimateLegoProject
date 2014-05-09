package com.skulg.tulp;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ParseException;
import ca.umontreal.iro.theultimatelegoproject.SubTaskCallbacks;
import ca.umontreal.iro.theultimatelegoproject.UpdateDbActivity;

public class LegoSetsApiCaller1 extends TulpAPICaller
{
	private SubTaskCallbacks callingAsyncTask;
	int buildingInstructionId;
	private UpdateDbActivity updateDbActivity;

	public LegoSetsApiCaller1(Context context, dbHelper dbh, SubTaskCallbacks argCallingAsyncTask)
	{
		super(context, dbh);

		callingAsyncTask	= argCallingAsyncTask;
	}

	@Override
	protected Boolean doInBackground(String... legoSets)
	{
		Boolean success	= false;
		int count		= legoSets.length;

		for (int i = 0; i < count; i++)
		{
			String setId					= legoSets[0];
			String buildingInstructionsId	= legoSets[1];
			Boolean removeFromImportTable	= false;
			HttpEntity legoSetPage;

			try
			{
				legoSetPage				= getHttpWithTimeout(GET_LEGO_SET_URL + API_KEY + "/" + setId);
				String jsonSet			= EntityUtils.toString(legoSetPage, HTTP.UTF_8);
				JSONArray jsArraySet	= new JSONArray(jsonSet);

				for (int j = 0, jsArraySetLength = jsArraySet.length(); j < jsArraySetLength; j++)
				{
					JSONObject currentJsonLegoSet = jsArraySet.getJSONObject(j);

					String name			= currentJsonLegoSet.getString("name");
					int boxNumber		= currentJsonLegoSet.getInt("boxNo");
					String description	= currentJsonLegoSet.getString("description");
					String imageUrl		= currentJsonLegoSet.getString("imageUrl");
					String modelName	= currentJsonLegoSet.getString("legoModelName");
					int nbPieces		= 0;

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

					dbh.insertLegoSets(description, boxNumber, imageUrl, name, modelName, nbPieces, price, released, buildingInstructionsId);

					removeFromImportTable	= true;
					success					= true;
				}
			}
			catch(Exception e)
			{
				if((e instanceof JSONException) || (e instanceof ParseException))
				{
					success	= true;

					removeFromImportTable	= true;
				}
				else if((e instanceof ClientProtocolException) || (e instanceof IOException) || (e instanceof IllegalStateException))
				{
					e.printStackTrace();

					success	= false;
				}
			}

			if(removeFromImportTable)
			{
				dbh.removeSetFromImportTable(Integer.valueOf(setId));
			}
		}

		return success;
	}

	@Override
	protected void onPostExecute(Boolean result)
	{
		if(result)
		{
			callingAsyncTask.onSubTaskSuccess();
		}
		else
		{
			callingAsyncTask.onSubTaskError();
		}
	}
}