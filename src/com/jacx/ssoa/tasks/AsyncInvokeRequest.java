package com.jacx.ssoa.tasks;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.jacx.ssoa.android.data.xml.XmlNode;
import com.jacx.ssoa.bean.SoapRequest;
import com.jacx.ssoa.utils._FakeX509TrustManager;

import com.jacx.ssoa.android.R;

/**
 * An {@link AsyncTask} used to load an XML document from a file
 */
public class AsyncInvokeRequest extends AsyncTask<String, String, Void> {



	/**
	 * An interface to listen to events occuring while invoking request
	 */
	public static interface InvokeRequestListener {

		/**
		 * Called when the XML document has been loaded successfully
		 */
		void onRequestComplete(String response);

		/**
		 * Called when an error occured while trying to read the document
		 */
		void onRequestError(Throwable throwable, String message);
	}

	/** The current application context */
	protected final Context mContext;
	/** The progress dialog */
	private ProgressDialog mDialog;

	protected String mResponse;
	protected String mResponseDebug;
	protected SoapRequest mSoapRequest;
	
	/** The loaded file */
	protected File mFile;
	/** The loaded file's Hash */
	protected String mHash;
	/** The loaded file's encoding */
	protected String mEncoding;
	/** the loaded file's XML root */

	/** Ignores the source file */
	private boolean mIgnoreFile;
	/** Force file as read only ? */
	private boolean mForceReadOnly;

	/** the listener for this loader's events */
	protected final InvokeRequestListener mListener;

	/** Throwable thrown while loading */
	private Throwable mThrowable;

	/**
	 * 
	 * @param context
	 * @param listener
	 * @param soapRequest 
	 * @param flags
	 */
	public AsyncInvokeRequest(final Context context,
			final InvokeRequestListener listener, SoapRequest soapRequest) {
		mContext = context;
		mListener = listener;
		mSoapRequest = soapRequest;
		
	}

	/**
	 * @see android.os.AsyncTask#onCancelled(java.lang.Object)
	 */
	@Override
	protected void onCancelled(final Void result) {

		if (mDialog != null) {
			mDialog.dismiss();
		}
	}

	/**
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		if (mDialog == null) {
			mDialog = new ProgressDialog(mContext);
			mDialog.setTitle(R.string.ui_sending_request);
			mDialog.setMessage(mContext.getString(R.string.ui_wait));
		}
		mDialog.show();
		mDialog.setCancelable(true);
	}

	/**
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Void doInBackground(final String... params) {
		if (params == null) {
			throw new IllegalArgumentException(new NullPointerException());
		}

		if (params.length != 1) {
			throw new IllegalArgumentException();
		}

		if (params[0] == null) {
			throw new IllegalArgumentException(new NullPointerException());
		}

		doRequest(params[0]);

		return null;
	}

	/**
	 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
	 */
	@Override
	protected void onProgressUpdate(final String... values) {
		mDialog.setTitle(TextUtils.concat(values));
		super.onProgressUpdate(values);
	}

	/**
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(final Void result) {
		super.onPostExecute(result);

		if (mThrowable == null) {
			mListener.onRequestComplete(mResponse);
		} else {
			mListener.onRequestError(mThrowable, null);
		}

		mDialog.dismiss();
		mDialog = null;
	}

	/**
	 * Starts reading the file with the correct parser
	 * 
	 * @param file
	 */
	protected void doRequest(final String file) {

		String targetURL = mSoapRequest.getEndpoint();
		String urlParameters = mSoapRequest.getRequestMessage();
		
		URL url;
		HttpURLConnection connection = null;  
		try {
			//Create connection
			url = new URL(targetURL);

			// for not trusted site (https)
			_FakeX509TrustManager.allowAllSSL();
			
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("POST");

			
			connection.setRequestProperty("SOAPAction", "");
			connection.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
			connection.setRequestProperty("Accept-Encoding","gzip,deflate");
			connection.setRequestProperty("User-Agent", "SweetAndSOA Android Client");
			
			connection.setUseCaches (false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			

			//Send request
			DataOutputStream wr = new DataOutputStream (
					connection.getOutputStream ());
			
			wr.writeBytes (urlParameters);
			
			wr.flush ();
			wr.close ();

			//Get Response    
			InputStream is ;
			
			Log.i("response", "code="+connection.getResponseCode());
			if(connection.getResponseCode()<=400){
				is=connection.getInputStream();
			
			}else{
				/* error from server */
				is = connection.getErrorStream();
			
			} 
			
			// is= connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			
			String line;

			StringBuffer response = new StringBuffer();


			while((line = rd.readLine()) != null) {

				response.append(line);
				response.append('\r');
			}

			rd.close();

			Log.i("response", ""+response.toString());

			mResponse = response.toString();

		} catch (Exception e) {

			Log.e("error https", "", e);

			mThrowable = e;

			//return null;


		} finally {

			if(connection != null) {
				connection.disconnect(); 
			}
		}



	}
	
//	public String setSoapMsg(String targetURL, String urlParameters){
//		mResponse += " @ - ";
//        URL url;
//        HttpURLConnection connection = null;  
//        try {
//          //Create connection
//          url = new URL(targetURL);
//
//         // for not trusted site (https)
//         // _FakeX509TrustManager.allowAllSSL();
//         // System.setProperty("javax.net.debug","all");
//
//          connection = (HttpURLConnection)url.openConnection();
//          connection.setRequestMethod("POST");
//
//          mResponse += " 1 - ";
//          connection.setRequestProperty("SOAPAction", "");
//          connection.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
//          connection.setRequestProperty("Accept-Encoding","gzip,deflate");
//          connection.setRequestProperty("User-Agent", "SweetAndSOA Android HTTP Client");
//          mResponse += " 2 - ";
//          connection.setUseCaches (false);
//          connection.setDoInput(true);
//          connection.setDoOutput(true);
//          mResponse += " 3 - ";
//
//          //Send request
//          DataOutputStream wr = new DataOutputStream (
//                       connection.getOutputStream ());
//          mResponse += " 4 - ";
//          wr.writeBytes (urlParameters);
//          mResponse += " 5 - ";
//          wr.flush ();
//          wr.close ();
//
//          mResponse += " 6 - ";
//          //Get Response    
//          InputStream is ;
//          mResponse += " 7 - ";
//          Log.i("response", "code="+connection.getResponseCode());
//          if(connection.getResponseCode()<=400){
//              is=connection.getInputStream();
//              mResponse += " 8 - ";
//          }else{
//              /* error from server */
//              is = connection.getErrorStream();
//              mResponse += " 9 - ";
//        } 
//          mResponse += " 10 - ";
//         // is= connection.getInputStream();
//          BufferedReader rd = new BufferedReader(new InputStreamReader(is));
//          
//          mResponse += " 11 - ";
//          String line;
//          mResponse += " 12 - ";
//          StringBuffer response = new StringBuffer();
//          
//          mResponse += " 13 - ";
//          while((line = rd.readLine()) != null) {
//        	  mResponse += " x - ";
//            response.append(line);
//            response.append('\r');
//          }
//          mResponse += " 14 - ";
//          rd.close();
//          mResponse += " 15 - ";
//          Log.i("response", ""+response.toString());
//          return response.toString();
//
//        } catch (Exception e) {
//
//         Log.e("error https", "", e);
//         
//         mResponse += " 16 - ";
//         
//         return null;
//          
//          
//        } finally {
//
//          if(connection != null) {
//            connection.disconnect(); 
//          }
//        }
//        
//
//      }

	

}
