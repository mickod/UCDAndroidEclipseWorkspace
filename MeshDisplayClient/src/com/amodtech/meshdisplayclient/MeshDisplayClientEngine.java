package com.amodtech.meshdisplayclient;

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
	private final String AWS_BASE_URL = "http://ec2-54-228-103-112.eu-west-1.compute.amazonaws.com/index.php/api/example";
	private final String MAMP_BASE_URL = "http://10.0.2.2:8888/codeigniter-restserver-master/index.php/api/example";
	private final String serverBaseUrl = AWS_BASE_URL;
	public String textToDisplay = "";
	
	public String getServerBaseURL() {
		//Getter for the base URL
		return this.serverBaseUrl;
	}
	
}
