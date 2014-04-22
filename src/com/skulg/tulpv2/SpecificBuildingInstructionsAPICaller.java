package com.skulg.tulpv2;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

public class SpecificBuildingInstructionsAPICaller extends TulpAPICaller {
	private String id;
	public SpecificBuildingInstructionsAPICaller(Context context, dbHelper dbh , String id) {
		super(context, dbh);
		this.id=id;
		
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String doInBackground(String... arg0) {
		// TODO Auto-generated method stub
		
		HttpEntity buildingInstructionsPage;
		try {
			buildingInstructionsPage = getHttp(GET_ALL_BUILDINGS_INSTRUCTIONS_URL+"/"+id);
		
			String json = EntityUtils.toString(buildingInstructionsPage, HTTP.UTF_8);
			JSONObject currentJsonBuildingInstuction = new JSONObject(json);
			String buildingInstructionsDescription =currentJsonBuildingInstuction.getString("description");
			int idInstruction = currentJsonBuildingInstuction.getInt("idInstruction");
			String buildingInstuctionsName = currentJsonBuildingInstuction.getString("name");
			String shortcutPicture = currentJsonBuildingInstuction.getString("shortcutPicture");
			JSONArray stepgroups;
			try {
				 stepgroups = currentJsonBuildingInstuction.getJSONArray("stepGroups");
				 Log.d("TULP", "FOUND A STEPGROUP");
				 
					for (int j=0 ; j<stepgroups.length(); j++){
						JSONObject currentStepGroup = stepgroups.getJSONObject(j);			
						String name = currentStepGroup.getString("name");
						long currentStepGroupId=dbh.insertStepGroup(name);
						dbh.insertStepGroupInstructionsLink(idInstruction, (int) currentStepGroupId);
						JSONArray filenames = currentStepGroup.getJSONArray("fileNames");
						for (int k=0 ; k<filenames.length(); k++){
							long currentImageId= dbh.insertImages(""+filenames.getString(k));
							dbh.insertStepGroupImageLink((int)currentImageId,(int) currentStepGroupId);
							Log.d("TULP","Filename :"+ filenames.getString(k));
						}						
					}
			}catch (JSONException e) {
				
				e.printStackTrace();
			}
			Log.d("TULP","Instructions name :"+ buildingInstuctionsName);
			Log.d("TULP","Instructions description :"+ buildingInstructionsDescription);
			
			this.dbh.insertBuildingInstructions(idInstruction,buildingInstructionsDescription, shortcutPicture, buildingInstuctionsName);				
			if (TextUtils.isDigitsOnly(buildingInstuctionsName)){
				new LegoSetsApiCaller(this.context , this.dbh).execute(buildingInstuctionsName);					
			}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
