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
import android.util.Log;


public class AllBuildingInstructionsAPICaller extends TulpAPICaller  {

	// Sera null s'il n'y a pas d'erreur
	String erreur;

	AllBuildingInstructionsAPICaller(Context context , dbHelper dbh){
		super(context , dbh);
	}

	public String doInBackground (String...strings ){

		erreur = null;
		try {
			HttpEntity buildingInstructionsPage = getHttp(GET_ALL_BUILDINGS_INSTRUCTIONS_URL);
			String json = EntityUtils.toString(buildingInstructionsPage, HTTP.UTF_8);
			JSONArray jsArrayBuildingInstructions = new JSONArray(json);
			for (int i=0 ; i<jsArrayBuildingInstructions.length(); i++){
				
				//new SpecificBuildingInstructionsAPICaller(context, dbh, i+"").execute();
				
				
				/*
				 * All this will be replaced by SpecificBuildingInstructionsAPICall
				*/ 
				
				
				
				JSONObject currentJsonBuildingInstuction = jsArrayBuildingInstructions.getJSONObject(i);			
				//String buildingInstructionsDescription =currentJsonBuildingInstuction.getString("description");
				//int idInstruction = currentJsonBuildingInstuction.getInt("idInstruction");
				String buildingInstuctionsName = currentJsonBuildingInstuction.getString("name");
				/*
				String shortcutPicture = currentJsonBuildingInstuction.getString("shortcutPicture");
				JSONArray stepgroups;
				try {
					 stepgroups = currentJsonBuildingInstuction.getJSONArray("stepGroups");
					 Log.d("TULP", "FOUND A STEPGROUP");
				}catch (JSONException e) {
					erreur = "Erreur JSON :"+e.getMessage();
					e.printStackTrace();
				}
				Log.d("TULP","Instructions name :"+ buildingInstuctionsName);
				Log.d("TULP","Instructions description :"+ buildingInstructionsDescription);
				
				
				
				
				/*
				for (int j=0 ; j<stepgroups.length(); j++){
					JSONObject currentStepGroup = stepgroups.getJSONObject(j);			
					String name = currentStepGroup.getString("name");
					JSONArray filenames = currentStepGroup.getJSONArray("filenames");
					for (int k=0 ; k<filenames.length(); k++){
						Log.d("TULP","Filename :"+ filenames.getJSONObject(k));
					}
						
				}
				
				
				this.dbh.insertBuildingInstructions(idInstruction,buildingInstructionsDescription, shortcutPicture, buildingInstuctionsName);				
				*/
				if (TextUtils.isDigitsOnly(buildingInstuctionsName)){
					new LegoSetsApiCaller(this.context , this.dbh).execute(buildingInstuctionsName);					
				}
				
			}

		} catch (ClientProtocolException e) {
			erreur = "Erreur HTTP (protocole) :"+e.getMessage();
			e.printStackTrace();
		} catch (IOException e) {
			erreur = "Erreur HTTP (IO) :"+e.getMessage();
			e.printStackTrace();
		} catch (ParseException e) {
			erreur = "Erreur JSON (parse) :"+e.getMessage();
			e.printStackTrace();
		} catch (JSONException e) {
			erreur = "Erreur JSON :"+e.getMessage();
			e.printStackTrace();
		}
		return "Success";
	}

}
