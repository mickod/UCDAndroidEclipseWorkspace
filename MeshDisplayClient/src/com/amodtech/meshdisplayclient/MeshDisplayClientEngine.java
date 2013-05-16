package com.amodtech.meshdisplayclient;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MeshDisplayClientEngine {
	/*
	 * This class is the main engine (or Model in MVC terms) of the Mesh Display
	 * Client.
	 */
	
	//Attributes
	private String eventID = null;
	private String clintID = null;
	private String serverBaseUrl = "";
	private String textToDisplay = "";
	private Activity liveEventDisplayACtvity;

	public void joinEvent(String eventToJoin, String clientIDForEvent, Handler responseHandler) {
		//This method adds this client to a live event
		
		//Check the client and event ids are not null
		if (eventToJoin == null || clientIDForEvent == null) {
			return;
		}
		
		//If the event id is not null then we are already in an event so leave it first
		if (this.eventID != null) {
			this.leaveEvent();
		}
		
		//Set the event and client ID
		this.eventID = eventToJoin;
		this.clintID = clientIDForEvent;
		
		//Message the server with the client and event ID
		
		//Start the timer to poll the server
		
		//Respond to the calling activity with a notification that the event has been started
		Message newMessage = new Message();
		newMessage.arg1 = 1;
		responseHandler.sendMessage(newMessage);
		Log.d("msg sent", "xxx");
	}
	
	public void leaveEvent() {
		// This method removes the client from a live event
		
		//Check first that there is a current event
		if (this.eventID == null) {
			return;
		}
		
		//Message the server to remove the client from the event
		
		//Reset the event if and display
		this.eventID = null;
		this.textToDisplay = "";
		
		//Respond to the calling activity with a notification that the client has left the event
	}
	
	public void getTextFromServer() {
	    //This method polls the server looking for text to display. It polls the server 
		//and it is recognised this may not be the most efficient from a better y point of view
		//but it is considered sufficient for this proof of concept.
		
		//Message the server
		
		//Message the display activity to display the text
		
		
	}
	
	
}
