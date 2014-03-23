/**
 * Project: FtpClient
 * Package: com.mfgpker.ftpclient
 * File Name: Ftp.java
 * Author: mfgpker
 * Date: 23/03/2014
 * Time: 12.24.38
 */
package com.mfgpker.ftpclient;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class Ftp extends Activity implements OnClickListener {

	MyFTPClient ftpclient;

	String workingDir;
	private static final String TAG = "MainActivity";
	private static final String TEMP_FILENAME = "test.txt";

	private Context cntx = null;

	private String ip, user, pass, port;
	private ListView contentList;
	private List<String> realcontents = new ArrayList<String>();
	private Button btnUpload, btnDisconnect, btnContent, btnExit;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ftp);

		ftpclient = new MyFTPClient();
		createDummyFile();
		cntx = this.getBaseContext();
		btnUpload = (Button) findViewById(R.id.upload);
		btnDisconnect = (Button) findViewById(R.id.disconnect);
		btnContent = (Button) findViewById(R.id.getContent);
		btnExit = (Button) findViewById(R.id.exit);

		btnDisconnect.setOnClickListener(this);
		btnUpload.setOnClickListener(this);
		btnContent.setOnClickListener(this);
		btnExit.setOnClickListener(this);
		
		contentList = (ListView) findViewById(R.id.contentList);

		btnDisconnect.setEnabled(false);
		btnUpload.setEnabled(false);
		btnContent.setEnabled(false);

		Bundle gotBasket = getIntent().getExtras();
		ip = gotBasket.getString("ip");
		port = gotBasket.getString("port");
		user = gotBasket.getString("user");
		pass = gotBasket.getString("pass");

		new Login().execute(ip, port, user, pass);
		//contentList.setAdapter(new ArrayAdapter<String>(Ftp.this, android.R.layout.simple_list_item_1, realcontents));
	}

	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.upload:
			new Thread(new Runnable() {
				public void run() {
					boolean status = false;
					status = ftpclient.ftpUpload(TEMP_FILENAME, TEMP_FILENAME, "/", cntx);
					if (status == true) {
						Log.d(TAG, "Upload success");
						// Toast.makeText(MainActivity.this,"Upload success.",
						// Toast.LENGTH_LONG).show();
					} else {
						Log.e(TAG, "Upload failed");

						// Toast.makeText(MainActivity.this, "Upload failed.",
						// Toast.LENGTH_LONG).show();
					}
				}
			}).start();
			break;
		case R.id.getContent:
			for (String g : realcontents) {
				Log.d(TAG, g);
			}
			updateList();
			// SimpleAdapter simpleAdpt = new SimpleAdapter(Ftp.this,
			// realcontents, R.id.contentList);
			// contentList.setAdapter(new ArrayAdapter<String>(Ftp.this,
			// R.id.contentList, realcontents));
			break;
		case R.id.disconnect:
			Disconnect();
			logout();
			break;
		case R.id.exit:
			Disconnect();
			this.finish();
			Log.d(TAG, "Exit");
			break;
		}

	}

	private void logout() {
		Intent i = new Intent(Ftp.this, MainActivity.class);
		startActivity(i);
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

	private void getContent() {
		new Thread("contentThread") {
			public void run() {
				String[] contents;

				// ftpclient.ftpPrintFilesList(workingDir);
				contents = ftpclient.getContentList(workingDir);

				for (int i = 0; i < contents.length; i++) {
					// Log.d(TAG, con);
					String con = contents[i];
					if (con.startsWith("file:")) {
						con = con.substring(5);
						Log.d(TAG, con);
					} else {
						con = con.substring(10);
						Log.d(TAG, con);
					}
					realcontents.add(con);
				}
			}
		}.start();
	}
	
	private void updateList(){
		getContent();
		contentList.setAdapter(new ArrayAdapter<String>(Ftp.this, android.R.layout.simple_list_item_1, realcontents));
	}
	
	public class Login extends AsyncTask<String, Integer, String> {

		ProgressDialog dialog;

		protected void onPreExecute() {
			dialog = new ProgressDialog(Ftp.this);
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dialog.setMax(100);
			dialog.show();

		}

		protected String doInBackground(String... args) {

			for (int i = 0; i < 20; i++) {
				publishProgress(5);
				try {
					Thread.sleep(88);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			dialog.dismiss();
			try {
				boolean status = false;
				String res = "false";
				// Replace your UID & PW here
				status = ftpclient.ftpConnect(args[0], args[2], args[3], Integer.parseInt(args[1]));
				if (status == true) {
					Log.d("login", "Connection Success");
					// status = ftpclient.ftpUpload(TEMP_FILENAME,
					// TEMP_FILENAME, "/", cntx);
					workingDir = ftpclient.ftpGetCurrentWorkingDirectory();
					
					res = "true";
				} else {
					// Toast.makeText(getApplicationContext(),
					// "Connection failed", 2000).show();
					Log.e("login", "Connection failed");
					res = "false";
				}
				return res;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onProgressUpdate(Integer... progress) {
			dialog.incrementProgressBy(progress[0]);
		}

		protected void onPostExecute(String result) {
			if (result == null) {
				Toast.makeText(Ftp.this, "ERROR", Toast.LENGTH_LONG).show();
			}

			if (result == "true") {
				Toast.makeText(Ftp.this, "succes", Toast.LENGTH_LONG).show();
				// basket.put
				btnDisconnect.setEnabled(true);
				btnUpload.setEnabled(true);
				btnContent.setEnabled(true);
				
				//getContent();
				updateList();
				
			} else {
				Disconnect();
				Intent i = new Intent(Ftp.this, MainActivity.class);
				Bundle b = new Bundle();
				b.putString("failed", "Connection failed");
				i.putExtras(b);
				startActivity(i);
			}

		}
	}

}
