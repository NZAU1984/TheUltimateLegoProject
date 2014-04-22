package com.skulg.tulpv2;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;

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
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

}
