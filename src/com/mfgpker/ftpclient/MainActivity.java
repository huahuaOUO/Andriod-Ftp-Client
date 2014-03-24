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
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener, OnItemSelectedListener {

	TextView ip, port, username, password;
	String TAG = "MainActivity";

	private enum Mode {
		FTP, SFTP
	}

	private Mode mode = Mode.FTP;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button createButton = (Button) findViewById(R.id.login);
		createButton.setOnClickListener(this);

		ip = (TextView) findViewById(R.id.etIP);
		port = (TextView) findViewById(R.id.etPort);
		username = (TextView) findViewById(R.id.etusername);
		password = (TextView) findViewById(R.id.etpassword);
		Bundle gotBasket = getIntent().getExtras();
		if (gotBasket != null) {
			String res = gotBasket.getString("failed");
			Toast.makeText(MainActivity.this, res, Toast.LENGTH_LONG).show();

		}

		Spinner spinner = (Spinner) findViewById(R.id.connection_protocol);
		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.connection_protocol_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login:

			String ip = this.ip.getText().toString();
			String port = this.port.getText().toString();
			String user = this.username.getText().toString();
			String pass = this.password.getText().toString();

			// new Login().execute(ip, port, user, pass);
			Bundle basket = new Bundle();
			basket.putString("ip", ip);
			basket.putString("port", port);
			basket.putString("user", user);
			basket.putString("pass", pass);
			Intent i;
			if(mode == Mode.FTP){
				i = new Intent(MainActivity.this, Ftp.class);
				i.putExtras(basket);
				startActivity(i);
			}
			else if (mode == Mode.SFTP){
				i = new Intent(MainActivity.this, Sftp.class);
				i.putExtras(basket);
				startActivity(i);
			}
			
			break;

		}

	}

	public void onItemSelected(AdapterView<?> av, View v, int pos, long id) {
		mode = (pos == 0) ? Mode.FTP : Mode.SFTP;
		Log.d(TAG, mode.toString());
	}

	public void onNothingSelected(AdapterView<?> arg0) {

	}

}
