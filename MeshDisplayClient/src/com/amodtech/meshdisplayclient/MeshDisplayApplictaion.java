package com.amodtech.meshdisplayclient;

import android.app.Application;

public class MeshDisplayApplictaion extends Application {
	/*
	 * This class reprsents the MeshDisplayApplictaion
	 */
	
	private MeshDisplayClientEngine meshDispEngine;
	
	public void onCreate() {
		
		//Instantiate the meshDisplayEngine
		this.meshDispEngine = new MeshDisplayClientEngine(this.getApplicationContext());
	}
	
	public MeshDisplayClientEngine getAppMeshDisplayEngine() {
		//This method returns the application MeshDisplayEngine
		
		return this.meshDispEngine;
	}

}
