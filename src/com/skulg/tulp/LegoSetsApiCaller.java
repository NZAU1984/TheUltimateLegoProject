package com.skulg.tulp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

public class LegoSetsApiCaller extends TulpAPICaller {

	int buildingInstructionId;
	public LegoSetsApiCaller(Context context , dbHelper dbh) {
		super(context , dbh);
		// TODO Auto-generated constructor stub
	}

	public LegoSetsApiCaller(Context context , int buildingInstructionId, dbHelper dbh) {
		super(context,dbh);
		this.buildingInstructionId=buildingInstructionId;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String doInBackground(String... legoSets) {
		//Getting # of Lego Sets in request
		int count = legoSets.length;
		for (int i=0;i<count;i++){
			HttpEntity legoSetPage;
			try {
				legoSetPage = getHttp(GET_LEGO_SET_URL+API_KEY+"/"+legoSets[i]);
				String jsonSet = EntityUtils.toString(legoSetPage, HTTP.UTF_8);
				JSONArray jsArraySet = new JSONArray(jsonSet);
				for (int j=0 ; j<jsArraySet.length(); j++){
					JSONObject currentJsonLegoSet = jsArraySet.getJSONObject(j);

					//Parsing info from json

					String name = currentJsonLegoSet.getString("name");
					int boxNumber = currentJsonLegoSet.getInt("boxNo");
					String description = currentJsonLegoSet.getString("description");
					String imageUrl = currentJsonLegoSet.getString("imageUrl");
					String modelName = currentJsonLegoSet.getString("legoModelName");
					int nbPieces=0;
					if (!currentJsonLegoSet.isNull("pieces")){
						nbPieces = currentJsonLegoSet.getInt("pieces");
					}
					double price =0;
					if (!currentJsonLegoSet.isNull("price")){						
						price = currentJsonLegoSet.getDouble("price"); 
					}
					int released =0;
					if(!currentJsonLegoSet.isNull("released")){	
						released = currentJsonLegoSet.getInt("released"); 					
					}
					Log.d("TULP","SetName: "+name);
					dbh.insertLegoSets(description, boxNumber, imageUrl, name, modelName, nbPieces, price, released);
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		return null;
	}
}