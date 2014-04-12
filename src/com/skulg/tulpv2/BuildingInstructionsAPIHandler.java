package com.skulg.tulpv2;

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
import android.util.Log;


public class BuildingInstructionsAPIHandler extends TulpAPIHandler  {

	// Sera null s'il n'y a pas d'erreur
	String erreur;

	BuildingInstructionsAPIHandler(Context context , dbHelper dbh){
		super(context , dbh);
	}

	public String doInBackground (String...strings ){

		erreur = null;
		try {
			HttpEntity buildingInstructionsPage = getHttp(GET_ALL_BUILDINGS_INSTRUCTIONS_URL);
			String json = EntityUtils.toString(buildingInstructionsPage, HTTP.UTF_8);
			JSONArray jsArrayBuildingInstructions = new JSONArray(json);
			for (int i=0 ; i<jsArrayBuildingInstructions.length(); i++){
				JSONObject currentJsonBuildingInstuction = jsArrayBuildingInstructions.getJSONObject(i);			
				String buildingInstructionsDescription =currentJsonBuildingInstuction.getString("description");
				int idInstruction = currentJsonBuildingInstuction.getInt("idInstruction");
				String buildingInstuctionsName = currentJsonBuildingInstuction.getString("name");
				String shortcutPicture = currentJsonBuildingInstuction.getString("shortcutPicture");
				JSONArray stepgroups = currentJsonBuildingInstuction.getJSONArray("stepGroups");
				Log.d("TULP","Instructions name :"+ buildingInstuctionsName);
				Log.d("TULP","Instructions description :"+ buildingInstructionsDescription);
				
				this.dbh.insertBuildingInstructions(idInstruction,buildingInstructionsDescription, shortcutPicture, buildingInstuctionsName);				
				if (TextUtils.isDigitsOnly(buildingInstuctionsName)){
					new LegoSetsApiCaller(this.context , this.dbh).execute(buildingInstuctionsName);					
				}
			}

		} catch (ClientProtocolException e) {
			erreur = "Erreur HTTP (protocole) :"+e.getMessage();
		} catch (IOException e) {
			erreur = "Erreur HTTP (IO) :"+e.getMessage();
		} catch (ParseException e) {
			erreur = "Erreur JSON (parse) :"+e.getMessage();
		} catch (JSONException e) {
			erreur = "Erreur JSON :"+e.getMessage();
		}
		return "Success";
	}

}
