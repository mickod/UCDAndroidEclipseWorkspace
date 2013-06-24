package com.amodtech.meshdisplaycontroller;

import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MeshDisplayControllerEngine {
	/*
	 * This class represents the engine of the Mesh Display Controller. It contains
	 * the eventID and list of clientIDs of the event, as well as the base URLs.
	 */
	
	//Attributes
	public String eventID = null;
	HashMap<String, MeshDisplayClient> clientsMap = new HashMap<String, MeshDisplayClient>();
	public int deviceLatitude = 0;
	public int deviceLongitude = 0;
	private Context appContext = null;
	private final String AWS_BASE_URL = "http://ec2-54-228-103-112.eu-west-1.compute.amazonaws.com/index.php/api/example";
	private final String MAMP_BASE_URL = "http://10.0.2.2:8888/codeigniter-restserver-master/index.php/api/example";
	private final String serverBaseUrl = MAMP_BASE_URL;
	private final String reservedControllerName = "Controller";
	
	public MeshDisplayControllerEngine(Context contextFromApplictaion) {
		this.appContext = contextFromApplictaion;
	}
	
	public String getServerBaseURL() {
		//Getter for the base URL
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.appContext);
        String baseURL = sharedPref.getString("ServerURL", serverBaseUrl);
		return baseURL + "/index.php/api/example";
	}
	
	public String getReservedControllerName() {
		//Getter for the reserved controller name
		return this.reservedControllerName;
	}
	
}
