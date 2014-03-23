package com.primary.instanthelp;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MessagePullService extends IntentService {

	public MessagePullService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        String dataString = workIntent.getDataString();

    }

}
