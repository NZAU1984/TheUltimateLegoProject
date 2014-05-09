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
import android.text.TextUtils;
import ca.umontreal.iro.theultimatelegoproject.UpdateDbActivity;

public class AllBuildingInstructionsAPICaller extends TulpAPICaller
{

	// Sera null s'il n'y a pas d'erreur
	String	erreur;
	private UpdateDbActivity updateDbActivity;
	private Boolean			success;

	public AllBuildingInstructionsAPICaller(Context context, dbHelper dbh, UpdateDbActivity argUpdateDbActivity)
	{
		super(context, dbh);

		updateDbActivity	= argUpdateDbActivity;
		success				= false;
	}

	@Override
	public Boolean doInBackground(String... strings)
	{

		erreur = null;
		try
		{
			HttpEntity buildingInstructionsPage    = getHttp(GET_ALL_BUILDINGS_INSTRUCTIONS_URL);
			String     json                        = EntityUtils.toString(buildingInstructionsPage, HTTP.UTF_8);
			JSONArray  jsArrayBuildingInstructions = new JSONArray(json);
			int			nbInstructions				= jsArrayBuildingInstructions.length();

			for (int i = 0; i < nbInstructions; i++)
			{

				JSONObject currentJsonBuildingInstuction = jsArrayBuildingInstructions.getJSONObject(i);
				String buildingInstructionName	= currentJsonBuildingInstuction.getString("name");
				String buildingInstructionId	= currentJsonBuildingInstuction.getString("idInstruction");
				if (TextUtils.isDigitsOnly(buildingInstructionName))
				{
					dbh.insertImportSet(buildingInstructionName, buildingInstructionId);
				}

			}

			success	= true;

		}
		catch (ClientProtocolException e)
		{
			erreur = "Erreur HTTP (protocole) :" + e.getMessage();
			e.printStackTrace();
		}
		catch (IOException e)
		{
			erreur = "Erreur HTTP (IO) :" + e.getMessage();
			e.printStackTrace();
		}
		catch(IllegalStateException e)
		{
			erreur = "Erreur HTTP (IllegalState) :" + e.getMessage();
			e.printStackTrace();
		}
		catch (ParseException e)
		{
			erreur = "Erreur JSON (parse) :" + e.getMessage();
			e.printStackTrace();
		}
		catch (JSONException e)
		{
			erreur = "Erreur JSON :" + e.getMessage();
			e.printStackTrace();
		}

		return true;
	}

	@Override
	protected void onPostExecute(Boolean result)
	{
		if(success)
		{
			updateDbActivity.AllBuildingInstructionsAPICallerHasFinished();
		}
		else
		{
			updateDbActivity.AllBuildingInstructionsAPICallerHasFailed();
		}
    }

}
