package com.primary.instanthelp;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Build;
import android.provider.ContactsContract;

public class UIElement extends Activity {


	//static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final int PICK_CONTACT = 0;
	Button buttonSend;
	Button buttonBrowse;
	EditText textPhoneNo;
	EditText sourceString;
	EditText destinationString;
	static String phoneNo;
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_uielement);


		buttonSend = (Button) findViewById(R.id.sendbutton);
		buttonBrowse = (Button) findViewById(R.id.browsebutton);
		textPhoneNo = (EditText) findViewById(R.id.PhoneNumber);
		sourceString = (EditText) findViewById(R.id.Source);
		destinationString = (EditText) findViewById(R.id.Destination);

		buttonBrowse.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(intent, PICK_CONTACT);
			}
		});
		buttonSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if(textPhoneNo.hasSelection())
					phoneNo = textPhoneNo.getText().toString();
				String source = sourceString.getText().toString();
				String destination = destinationString.getText().toString();
				String sms = "$$$"+"\n"+"Source:"+source+"\n"+"Destination:"+destination;
				try {
					SmsManager smsManager = SmsManager.getDefault();
					Toast.makeText(getApplicationContext(),
							phoneNo,
							Toast.LENGTH_LONG).show();
					smsManager.sendTextMessage(phoneNo, null, sms, null, null);
					Toast.makeText(getApplicationContext(), "SMS Sent!",
							Toast.LENGTH_LONG).show();
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(),
							"SMS faild, please try again later!",
							Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}

			}
		});
	}

	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);

		switch (reqCode) {
		case (PICK_CONTACT) :
			if (resultCode == Activity.RESULT_OK){
				Cursor cursor =  getContentResolver().query(data.getData(), null, null, null, null);

				while (cursor.moveToNext()) 
				{           
					String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
					String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)); 

					String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

					if ( hasPhone.equalsIgnoreCase("1"))
						hasPhone = "true";
					else
						hasPhone = "false" ;

					if (Boolean.parseBoolean(hasPhone)) 
					{
						Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,null, null);
						while (phones.moveToNext()) 
						{
							phoneNo = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						}
						phones.close();
					}
				}
				break;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.uielement, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


}
