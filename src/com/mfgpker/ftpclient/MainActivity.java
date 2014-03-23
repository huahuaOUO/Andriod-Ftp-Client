package com.mfgpker.ftpclient;

import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener {

	MyFTPClient ftpclient;

	String workingDir;
	private static final String TAG = "MainActivity";
	private static final String TEMP_FILENAME = "test.txt";
	private Context cntx = null;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		cntx = this.getBaseContext();
		View createButton = findViewById(R.id.login);
		createButton.setOnClickListener(this);
		View downloadButton = findViewById(R.id.upload);
		downloadButton.setOnClickListener(this);
		View uploadButton = findViewById(R.id.disconnect);
		uploadButton.setOnClickListener(this);
		View contentButton = findViewById(R.id.getContent);
		contentButton.setOnClickListener(this);
		View exitButton = findViewById(R.id.exit);
		exitButton.setOnClickListener(this);

		createDummyFile();

		ftpclient = new MyFTPClient();
		
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login:
			new Thread(new Runnable() {
				public void run() {
					boolean status = false;
					// Replace your UID & PW here
					status = ftpclient.ftpConnect("192.168.1.121", "bob", "frank", 800);
					if (status == true) {
						Log.d(TAG, "Connection Success");
						//status = ftpclient.ftpUpload(TEMP_FILENAME, TEMP_FILENAME, "/", cntx);

					} else {
						// Toast.makeText(getApplicationContext(),
						// "Connection failed", 2000).show();
						//d
						Log.d(TAG, "Connection failed");
						
					}
				}
			}).start();
			break;
		case R.id.upload:
			new Thread(new Runnable() {
				public void run() {
					boolean status = false;
					status = ftpclient.ftpUpload(TEMP_FILENAME, TEMP_FILENAME, "/", cntx);
					if (status == true) {
						Log.d(TAG, "Upload success");
						//Toast.makeText(MainActivity.this,"Upload success.", Toast.LENGTH_LONG).show();
					} else {
						Log.d(TAG, "Upload failed");
						
						//Toast.makeText(MainActivity.this, "Upload failed.", Toast.LENGTH_LONG).show();
					}
				}
			}).start();
			break;
		case R.id.getContent:
			new Thread(new Runnable() {
				public void run() {
					workingDir = ftpclient.ftpGetCurrentWorkingDirectory();
					ftpclient.ftpPrintFilesList(workingDir);
				}
			}).start();
			break;
		case R.id.disconnect:
			Disconnect();
			break;
		case R.id.exit:
			Disconnect();
			this.finish();
			Log.d(TAG, "Exit");
			break;
		}

	}

	void Disconnect() {
		new Thread(new Runnable() {
			public void run() {
				ftpclient.ftpDisconnect();
				Log.d(TAG, "Disconnect");
			}
		}).start();
	}

	
	protected void onDestroy() {
		super.onDestroy();
		Disconnect();
	}
	
	public void createDummyFile() {
		try {
			FileOutputStream fos;
			String file_content = "Hi this is a sample new file to upload for android FTP client example";

			fos = openFileOutput(TEMP_FILENAME, MODE_PRIVATE);
			fos.write(file_content.getBytes());
			fos.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	


}
