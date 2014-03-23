package com.primary.instanthelp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class IncomingSms extends BroadcastReceiver {
    
    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();
     
    public void onReceive(Context context, Intent intent) {
     
        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();
 
        try {
             
            if (bundle != null) {
                
            	
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                 
                for (int i = 0; i < pdusObj.length; i++) {
                     
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                     
                    String senderNum = phoneNumber.trim();
                    String message = currentMessage.getDisplayMessageBody();
                    String src=null,dest=null;
                    String[] splitMsg=null;
                    
                    if(message.contains("$$$") && message.contains("Source") && message.contains("Destination"))
                    {
                    	Log.i("SmsReceiver", "senderNum: "+ senderNum + "; message: " + message);
                    	splitMsg=message.split("\n");
                    	src=splitMsg[1].trim();
                    	dest=splitMsg[2].trim();
                    	
                    	src=src.split(":")[1].trim();
                    	dest=dest.split(":")[1].trim();
                    }
                    
                    //call back to the activity
                    																																																																						Intent calback = new Intent(context, MapElement.class);
                    calback.putExtra("Source", src);
                    calback.putExtra("Destination", dest);
                    calback.putExtra("PhoneNo", senderNum);
                    calback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(calback);
                } // end for loop
              } // bundle is null
 
        } 
        catch (Exception e) 
        {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);
             
        }
    }
}

	