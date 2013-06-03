package com.amodtech.meshdisplaycontroller;

import java.util.HashMap;

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
	private final String AWS_BASE_URL = "http://ec2-54-216-7-173.eu-west-1.compute.amazonaws.com/index.php/api/example";
	private final String MAMP_BASE_URL = "http://10.0.2.2:8888/codeigniter-restserver-master/index.php/api/example";
	private final String serverBaseUrl = MAMP_BASE_URL;
	
	public String getServerBaseURL() {
		//Getter for the base URL
		return this.serverBaseUrl;
	}
	
}
