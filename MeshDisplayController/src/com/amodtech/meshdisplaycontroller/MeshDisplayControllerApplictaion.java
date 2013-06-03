package com.amodtech.meshdisplaycontroller;

import android.app.Application;

public class MeshDisplayControllerApplictaion extends Application{

	/*
	 * This class reprsents the MeshDisplayController Applictaion
	 */
	
	private MeshDisplayControllerEngine meshDispControllerEngine;
	
	public void onCreate() {
		
		//Instantiate the meshDisplayEngine
		this.meshDispControllerEngine = new MeshDisplayControllerEngine();
	}
	
	public MeshDisplayControllerEngine getAppMeshDisplayControllerEngine() {
		//This method returns the application MeshDisplayEngine
		
		return this.meshDispControllerEngine;
	}

	
}
