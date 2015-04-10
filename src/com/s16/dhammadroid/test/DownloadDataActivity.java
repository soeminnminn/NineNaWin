package com.s16.dhammadroid.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
 
import com.s16.dhammadroid.R;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
 
public class DownloadDataActivity extends Activity implements OnClickListener {
 
	private DownloadManager downloadManager;
	private long downloadReference;
	
	public static class Country {
		String code = null;
		String name = null;

		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	} 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download_data);

		//start download button
		Button startDownload = (Button) findViewById(R.id.startDownload);
		startDownload.setOnClickListener(this);

		//display all download button
		Button displayDownload = (Button) findViewById(R.id.displayDownload);
		displayDownload.setOnClickListener(this);

		//check download status button
		Button checkStatus = (Button) findViewById(R.id.checkStatus);
		checkStatus.setOnClickListener(this);
		checkStatus.setEnabled(false);

		//cancel download button
		Button cancelDownload = (Button) findViewById(R.id.cancelDownload);
		cancelDownload.setOnClickListener(this);
		cancelDownload.setEnabled(false);

		//set filter to only when download is complete and register broadcast receiver
		IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
		registerReceiver(downloadReceiver, filter);
	}
	
	@Override
    public void onDestroy() {
		unregisterReceiver(downloadReceiver);
		super.onDestroy();
    }

	public void onClick(View v) {

		switch (v.getId()) {

			//start the download process
			case R.id.startDownload:

				downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
				Uri Download_Uri = Uri.parse("http://demo.mysamplecode.com/Sencha_Touch/CountryServlet?start=0&limit=999");
				DownloadManager.Request request = new DownloadManager.Request(Download_Uri);

				//Restrict the types of networks over which this download may proceed.
				request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
				//Set whether this download may proceed over a roaming connection.
				request.setAllowedOverRoaming(false);
				//Set the title of this download, to be displayed in notifications (if enabled).
				request.setTitle("My Data Download");
				//Set a description of this download, to be displayed in notifications (if enabled)
				request.setDescription("Android Data download using DownloadManager.");
				//Set the local destination for the downloaded file to a path within the application's external files directory
				request.setDestinationInExternalFilesDir(this,Environment.DIRECTORY_DOWNLOADS,"CountryList.json");

				//Enqueue a new download and same the referenceId
				downloadReference = downloadManager.enqueue(request);

				TextView showCountries = (TextView) findViewById(R.id.countryData);
				showCountries.setText("Getting data from Server, Please WAIT...");

				Button checkStatus = (Button) findViewById(R.id.checkStatus);
				checkStatus.setEnabled(true);
				Button cancelDownload = (Button) findViewById(R.id.cancelDownload);
				cancelDownload.setEnabled(true);
				break;

			//display all downloads 
			case R.id.displayDownload: 

				Intent intent = new Intent();
				intent.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
				startActivity(intent);
				break;

			//check the status of a download 
			case R.id.checkStatus: 

				Query myDownloadQuery = new Query();
				//set the query filter to our previously Enqueued download 
				myDownloadQuery.setFilterById(downloadReference);

				//Query the download manager about downloads that have been requested.
				Cursor cursor = downloadManager.query(myDownloadQuery);
				if(cursor.moveToFirst()){
					checkStatus(cursor);
				}
				break;

			//cancel the ongoing download 
			case R.id.cancelDownload: 

				downloadManager.remove(downloadReference);
				checkStatus = (Button) findViewById(R.id.checkStatus);
				checkStatus.setEnabled(false);
				showCountries = (TextView) findViewById(R.id.countryData);
				showCountries.setText("Download of the file cancelled...");

				break; 

				// More buttons go here (if any) ...

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	private void checkStatus(Cursor cursor){

		//column for status
		int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
		int status = cursor.getInt(columnIndex);
		//column for reason code if the download failed or paused
		int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
		int reason = cursor.getInt(columnReason);
		//get the download filename
		int filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
		String filename = cursor.getString(filenameIndex);

		String statusText = "";
		String reasonText = "";

		switch(status){
			case DownloadManager.STATUS_FAILED:
				statusText = "STATUS_FAILED";
				switch(reason){
					case DownloadManager.ERROR_CANNOT_RESUME:
						reasonText = "ERROR_CANNOT_RESUME";
						break;
					case DownloadManager.ERROR_DEVICE_NOT_FOUND:
						reasonText = "ERROR_DEVICE_NOT_FOUND";
						break;
					case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
						reasonText = "ERROR_FILE_ALREADY_EXISTS";
						break;
					case DownloadManager.ERROR_FILE_ERROR:
						reasonText = "ERROR_FILE_ERROR";
						break;
					case DownloadManager.ERROR_HTTP_DATA_ERROR:
						reasonText = "ERROR_HTTP_DATA_ERROR";
						break;
					case DownloadManager.ERROR_INSUFFICIENT_SPACE:
						reasonText = "ERROR_INSUFFICIENT_SPACE";
						break;
					case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
						reasonText = "ERROR_TOO_MANY_REDIRECTS";
						break;
					case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
						reasonText = "ERROR_UNHANDLED_HTTP_CODE";
						break;
					case DownloadManager.ERROR_UNKNOWN:
						reasonText = "ERROR_UNKNOWN";
						break;
				}
				break;
			case DownloadManager.STATUS_PAUSED:
				statusText = "STATUS_PAUSED";
				switch(reason){
					case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
						reasonText = "PAUSED_QUEUED_FOR_WIFI";
						break;
					case DownloadManager.PAUSED_UNKNOWN:
						reasonText = "PAUSED_UNKNOWN";
						break;
					case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
						reasonText = "PAUSED_WAITING_FOR_NETWORK";
						break;
					case DownloadManager.PAUSED_WAITING_TO_RETRY:
						reasonText = "PAUSED_WAITING_TO_RETRY";
						break;
				}
				break;
			case DownloadManager.STATUS_PENDING:
				statusText = "STATUS_PENDING";
				break;
			case DownloadManager.STATUS_RUNNING:
				statusText = "STATUS_RUNNING";
				break;
			case DownloadManager.STATUS_SUCCESSFUL:
				statusText = "STATUS_SUCCESSFUL";
				reasonText = "Filename:\n" + filename;
				break;
		}
		
		Toast toast = Toast.makeText(DownloadDataActivity.this, 
			statusText + "\n" + 
			reasonText, 
			Toast.LENGTH_LONG);
		toast.setGravity(Gravity.TOP, 25, 400);
		toast.show();

	}

	private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

		@SuppressWarnings("resource")
		@Override
		public void onReceive(Context context, Intent intent) {

			//check if the broadcast message is for our Enqueued download
			long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
			if(downloadReference == referenceId){
			 
				Button cancelDownload = (Button) findViewById(R.id.cancelDownload);
				cancelDownload.setEnabled(false);
				 
				int ch;
				ParcelFileDescriptor file;
				StringBuffer strContent = new StringBuffer("");
				StringBuffer countryData = new StringBuffer("");
				 
				//parse the JSON data and display on the screen
				try {
					 file = downloadManager.openDownloadedFile(downloadReference);
					 FileInputStream fileInputStream = new ParcelFileDescriptor.AutoCloseInputStream(file);

					 while( (ch = fileInputStream.read()) != -1)
						strContent.append((char)ch);
					  
					 JSONObject responseObj = new JSONObject(strContent.toString()); 
					 JSONArray countriesObj = responseObj.getJSONArray("countries");

					 for (int i=0; i<countriesObj.length(); i++){
						 Gson gson = new Gson();
						 String countryInfo = countriesObj.getJSONObject(i).toString();
						 Country country = gson.fromJson(countryInfo, Country.class);
						 countryData.append(country.getCode() + ": " + country.getName() +"\n");
					 }
					  
					 TextView showCountries = (TextView) findViewById(R.id.countryData);
					 showCountries.setText(countryData.toString());
					  
					 Toast toast = Toast.makeText(DownloadDataActivity.this, "Downloading of data just finished", Toast.LENGTH_LONG);
					 toast.setGravity(Gravity.TOP, 25, 400);
					 toast.show();
				  
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}
	}; 
}