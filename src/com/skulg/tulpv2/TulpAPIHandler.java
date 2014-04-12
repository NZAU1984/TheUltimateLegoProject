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

public abstract class TulpAPIHandler extends AsyncTask<String, String, String> {
	protected static final String GET_LEGO_SET_URL = "http://www.cubiculus.com/api-rest/lego-set/";
	protected static final String API_KEY = "fchi4855j4iqah1c5v7mr28publlbp05dhe7fk3cjtbgaltglj88l6labhe9u8t6";
	protected static final String GET_ALL_BUILDINGS_INSTRUCTIONS_URL ="http://www.cubiculus.com/api-rest/building-instruction/"+API_KEY;
	Context context;
	dbHelper dbh;
	public TulpAPIHandler(Context context , dbHelper dbh) {
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