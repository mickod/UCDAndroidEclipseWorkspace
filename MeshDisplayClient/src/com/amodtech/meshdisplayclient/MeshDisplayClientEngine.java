package com.amodtech.meshdisplayclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MeshDisplayClientEngine {
	/*
	 * This class represents the engine of the Mesh Display Client. It contains
	 * the eventID and clientID of the event, as well as the base URLs.
	 */
	
	//Attributes
	public String eventID = null;
	public String clintID = null;
	public int deviceLatitude = 0;
	public int deviceLongitude = 0;
	private Context appContext = null;
	private final String AWS_BASE_URL = "http://ec2-54-228-103-112.eu-west-1.compute.amazonaws.com";
	private final String MAMP_BASE_URL = "http://10.0.2.2:8888/codeigniter-restserver-master";
	private final String serverBaseUrl = AWS_BASE_URL;
	public String textToDisplay = "";
	
	public MeshDisplayClientEngine(Context contextFromApplictaion) {
		this.appContext = contextFromApplictaion;
	}
	
	public String getServerBaseURL() {
		//Getter for the base URL
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.appContext);
        String baseURL = AWS_BASE_URL; //sharedPref.getString("ServerURL", serverBaseUrl);
		return baseURL + "/index.php/api/example";
	}
	
}
