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
import android.util.Log;
import ca.umontreal.iro.theultimatelegoproject.UpdateDbActivity;

public class GetAllLegoSets extends TulpAPICaller
{

	private UpdateDbActivity	updateDbActivity;
	String lalala;

	public GetAllLegoSets(Context context, dbHelper dbh, UpdateDbActivity argUpdateDbActivity)
	{
		super(context, dbh);

		updateDbActivity	= argUpdateDbActivity;
	}

	@Override
	protected String doInBackground(String... arg0)
	{
		Log.d("GetAllLegoSets", "doInBack...");
		HttpEntity legoSetEntry;
		try
		{
			//legoSetEntry	= postJSONData(GET_ALL_LEGO_SETS_URL, "{\"currentPageNo\":0,\"propertiesParams\":{\"idPropertyValues\":[],\"priceRange\":{\"from\":\"0\",\"to\":\"999999\"},\"yearRange\":{\"from\":\"0\",\"to\":\"2014\"}},\"rowPerPage\":100}");
			legoSetEntry	= postJSONData(GET_ALL_LEGO_SETS_URL, "{\"currentPageNo\":0,\"propertiesParams\":{\"idPropertyValues\":[],\"priceRange\":{\"from\":\"0\",\"to\":\"999999\"},\"yearRange\":{\"from\":\"0\",\"to\":\"2014\"}},\"rowPerPage\":100}");

			lalala	= EntityUtils.toString(legoSetEntry, HTTP.UTF_8);

			try
			{
				//
				JSONObject	jsonObj		= new JSONObject(lalala);

				// using .toString() instead of jsonObj.getJSONArray() because it requires API 19 !
				JSONArray jsArraySet = new JSONArray(jsonObj.get("currentPage").toString());

				JSONObject set0	= jsArraySet.getJSONObject(0);

				//JSONObject currentJsonLegoSet = jsArraySet.getJSONObject(0);

				//setText(currentJsonLegoSet.toString());
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//updateDbActivity.setText(theStr);

			//Log.d("GetAllLegoSets", theStr);
		}
		catch (ClientProtocolException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

/*
{"currentPageNo":0,"propertiesParams":{"idPropertyValues":[],"priceRange":{"from":"0","to":"999999"},"yearRange":{"from":"0","to":"2014"}},"rowPerPage":100}
 */

		return null;
	}

	@Override
	protected void onPostExecute(String result)
    {
		updateDbActivity.setText(lalala);
       //textView.setText(result);
    }

}
