package com.jacx.ssoa.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.jacx.ssoa.bean.SoapRequest;
import com.jacx.ssoa.constant.SSOAConstant;
import com.jacx.ssoa.tasks.AsyncInvokeRequest;
import com.jacx.ssoa.tasks.AsyncInvokeRequest.InvokeRequestListener;

import com.jacx.ssoa.android.R;

public class SSOASendRequestActivity extends Activity {

	
	private String xmlRequest;
	
	private AsyncInvokeRequest mRequestAction;
	
	private Button buttonSubmit;
	
	private TextView textConsole;
	private TextView textConsoleHeader;
	
	private AutoCompleteTextView textViewEndpoint;
	
	public static final String PREFS_NAME = "MyPrefsFile";
	
	public static final String STORE_ENDPOINTS = "endpoints";
	
	public static final String NULL = "NULL";
	
	
	/** the current application context */
	private Context mContext;
	
	/*
	Android HTTP Access - Tutorial 
	http://www.vogella.com/articles/AndroidNetworking/article.html
	
	Android, sending XML via HTTP POST (SOAP)
	http://stackoverflow.com/questions/2559948/android-sending-xml-via-http-post-soap
	post: setSoapMsg(String targetURL, String urlParameters)
	
	*/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ssoa_send_request);
		
		
		//Settings.System.putString(getContentResolver(), Settings.System.HTTP_PROXY, "proxy:8080"); 
		
		mContext = this;
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		     xmlRequest = extras.getString(SSOAConstant.VAR_SOAP_REQUEST_MESSAGE);		    
		}
		
		textConsole = (TextView) findViewById(R.id.textConsole);
		textConsoleHeader = (TextView) findViewById(R.id.textConsoleHeader);
		
		buttonSubmit = (Button) findViewById(R.id.buttonSubmit);
		 
		buttonSubmit.setOnClickListener(new OnClickListener() {
 		
			@Override
			public void onClick(View arg0) {
					
				
				SoapRequest soapRequest = new SoapRequest(xmlRequest, textViewEndpoint.getText().toString());
								
				mRequestAction = new AsyncInvokeRequest(mContext, mInvokeRequestListener, soapRequest);
				mRequestAction.execute(xmlRequest);
				
				storeNewEndpoint(textViewEndpoint.getText().toString());
        		refreshEndpointField();
  
			}
 
		});
		
		
		 textViewEndpoint = (AutoCompleteTextView)
	                findViewById(R.id.autoTextEndpoints);
	       
	        refreshEndpointField();
		
		
		//console
	    textConsoleHeader.setText(R.string.ui_request);
		textConsole.setText(xmlRequest);
		
		

		 
	
	}

	
	
	
	//endpoint selection
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.send_request, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.clear_history:
	            clearHistory();
	            return true;	        
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	

	private void clearHistory() {
		
		 SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	      SharedPreferences.Editor editor = settings.edit();
	      editor.putString(STORE_ENDPOINTS, "");

	      // Commit the edits!
	      editor.commit();
	      
	      refreshEndpointField();
		
	}

	private void refreshEndpointField() {

		if(textViewEndpoint == null){
			return;
		}
		
		String[] endpointArrays = fetchStoredEndpointArray();

		if(endpointArrays != null){

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_dropdown_item_1line, endpointArrays);
			textViewEndpoint.setAdapter(adapter);        
			textViewEndpoint.setThreshold(1);

		}

	}

	
	private String[] fetchStoredEndpointArray() {
		
		String[] returnArray = null;
		
		String delimintedString = fetchStoredEndpoint();
		
		if(!NULL.equals(delimintedString)){
		
			returnArray =  delimintedString.split(",");
			
		}
		
		
		
		return returnArray;
	}

	private String fetchStoredEndpoint(){
		
		// Restore preferences
	       SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	       String savedEndpoints = settings.getString(STORE_ENDPOINTS, NULL);
	    
	       return savedEndpoints;
	}
	
	private void storeNewEndpoint(String thisEndpoint){
		
		
		if (TextUtils.isEmpty(thisEndpoint)) {
			return;
		}
		
		if(endpointExists(thisEndpoint)){
			return;
		}
		
		thisEndpoint = thisEndpoint.toLowerCase();
		
		String newEndpointRecord = thisEndpoint+","+fetchStoredEndpoint();
		
		
		// We need an Editor object to make preference changes.
	      // All objects are from android.context.Context
	      SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	      SharedPreferences.Editor editor = settings.edit();
	      editor.putString(STORE_ENDPOINTS, newEndpointRecord);

	      // Commit the edits!
	      editor.commit();
	      
	}

	private boolean endpointExists(String thisEndpoint) {
		
		boolean result = false;
		
		String[] endpointArrays = fetchStoredEndpointArray();

		if(endpointArrays != null){
			
			for (int i = 0; i < endpointArrays.length; i++) {
				
				if(endpointArrays[i].equalsIgnoreCase(thisEndpoint)){
					
					result = true;
					break;
				}
				
			}

		}

		return result;
	}
	
	
	/** the File loader listener for this editor */
	private InvokeRequestListener mInvokeRequestListener = new InvokeRequestListener() {

		@Override
		public void onRequestComplete(String response) {
		
			textConsoleHeader.setText(R.string.ui_response);
			textConsole.setText(response);
			textConsole.setTextColor(Color.BLACK);
			
			Intent i = new Intent(getApplicationContext(), AxelActivity.class);
			i.putExtra(SSOAConstant.VAR_SOAP_RESPONSE_MESSAGE,response);
			startActivity(i);
			
		}

		@Override
		public void onRequestError(Throwable throwable, String message) {
					
			
		    
			try {
				throw throwable;
			}  catch (Throwable e) {
				
				textConsoleHeader.setText(R.string.ui_error);
				textConsole.setText(e.getMessage());
				textConsole.setTextColor(Color.RED);
				
				//Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);	
			    //toast.show();
			    
				//errorNotification(message);
				e.printStackTrace();
			}
			
		}
		
	};
	
//	private void errorNotification(String message){
//		Crouton.makeText(this, message, Style.ALERT).show();
//	}

}
