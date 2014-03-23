/**
 * Project: FtpClient
 * Package: com.mfgpker.ftpclient
 * File Name: MainActivity.java
 * Author: mfgpker
 * Date: 22/03/2014
 * Time: 12.24.38
 */
package com.mfgpker.ftpclient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener   {
	
	TextView ip, port, username, password;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		View createButton = findViewById(R.id.login);
		createButton.setOnClickListener(this);

		View exitButton = findViewById(R.id.exit);
		exitButton.setOnClickListener(this);
		
		ip = (TextView) findViewById(R.id.etIP);
		port = (TextView) findViewById(R.id.etPort);
		username = (TextView) findViewById(R.id.etusername);
		password = (TextView) findViewById(R.id.etpassword);
		Bundle gotBasket = getIntent().getExtras();
		if(gotBasket != null){
			String res = gotBasket.getString("failed");
			Toast.makeText(MainActivity.this, res, Toast.LENGTH_LONG).show();
			
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login:
			
			String ip = this.ip.getText().toString();
			String port =this.port.getText().toString();
			String user = this.username.getText().toString();
			String pass = this.password.getText().toString();
			
			 //new Login().execute(ip, port, user, pass);
			Bundle basket = new Bundle();
			basket.putString("ip", ip);
			basket.putString("port", port);
			basket.putString("user", user);
			basket.putString("pass", pass);
			Intent i = new Intent(MainActivity.this, Ftp.class);
			i.putExtras(basket);
			startActivity(i);
			break;

		case R.id.exit:
			this.finish();
			break;
		}

	}

	protected void onDestroy() {
		super.onDestroy();

	}

	
	public class lLogin extends AsyncTask<String, Integer, String> {

		ProgressDialog dialog;
		
		protected void onPreExecute() {
			dialog = new ProgressDialog(MainActivity.this);
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
				//status = ftpclient.ftpConnect(args[0], args[2], args[3], Integer.parseInt(args[1]));
				if (status == true) {
					Log.d("login", "Connection Success");
					//status = ftpclient.ftpUpload(TEMP_FILENAME, TEMP_FILENAME, "/", cntx);
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
			if(result == "true"){
				Toast.makeText(MainActivity.this, "succes", Toast.LENGTH_LONG).show();
				//basket.put
				Intent i = new Intent(MainActivity.this, Ftp.class);
				//i.putExtra("ftpclient", ftpclient);
				startActivity(i);
			} else {
				Toast.makeText(MainActivity.this, "Connection failed", Toast.LENGTH_LONG).show();
			}

		}
	}



	
}
