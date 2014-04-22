package com.skulg.tulpv2;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.AsyncTask;

public abstract class TulpAPICaller extends AsyncTask<String, String, String> {
	protected static final String GET_LEGO_SET_URL = "http://www.cubiculus.com/api-rest/lego-set/";
	protected static final String API_KEY = "75mbg89mmad8as06fvc3kq9fl91t1b6j67ihou4cmq9l4830222gco2qnaffjqtf";
	protected static final String GET_ALL_BUILDINGS_INSTRUCTIONS_URL ="http://www.cubiculus.com/api-rest/building-instruction/"+API_KEY;
	Context context;
	dbHelper dbh;
	public TulpAPICaller(Context context , dbHelper dbh) {
		super();
		this.context=context;
		this.dbh=dbh;
	}
	protected HttpEntity getHttp(String url) throws ClientProtocolException,
			IOException {			
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet http = new HttpGet(url);
				HttpResponse response = httpClient.execute(http);
				return response.getEntity();    		
			}
}