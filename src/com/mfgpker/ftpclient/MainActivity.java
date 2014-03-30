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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener, OnItemSelectedListener {
	public static final String PREFS_NAME = "FTPINFOPrefsFile";

	TextView ip, port, username, password;
	String ips, ports, usernames, passwords;
	int modes;
	String TAG = "MainActivity";
	CheckBox save;
	Spinner profil_spinner, protocol_spinner;

	private enum Mode {
		FTP, SFTP
	}

	int saveprofil = 0;

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
		save = (CheckBox) findViewById(R.id.checkBoxsavedata);
		Bundle gotBasket = getIntent().getExtras();
		if (gotBasket != null) {
			String res = gotBasket.getString("failed");
			Toast.makeText(MainActivity.this, res, Toast.LENGTH_LONG).show();

		}
		String savepro = String.valueOf(saveprofil);
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		ips = settings.getString("ip" + savepro, "");
		ports = settings.getString("port" + savepro, "21");
		usernames = settings.getString("user" + savepro, "");
		passwords = settings.getString("pass" + savepro, "");
		modes = settings.getInt("mode" + savepro, 0);
		mode = (modes == 0) ? Mode.FTP : Mode.SFTP;
		
		ip.setText(ips);
		port.setText(ports);
		username.setText(usernames);
		password.setText(passwords);

		

		protocol_spinner = (Spinner) findViewById(R.id.connection_protocol);
		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.connection_protocol_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		protocol_spinner.setAdapter(adapter);
		protocol_spinner.setOnItemSelectedListener(this);
		
		
		profil_spinner = (Spinner) findViewById(R.id.spinnerSaveinfo);
		ArrayAdapter<CharSequence> adapterpr = ArrayAdapter.createFromResource(this, R.array.Saveprofil_array, android.R.layout.simple_spinner_item);
		adapterpr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		profil_spinner.setAdapter(adapterpr);
		profil_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
				saveprofil = pos;
				Log.d(TAG, "profil " + saveprofil);
				
				String savepro = String.valueOf(saveprofil);
				SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
				ips = settings.getString("ip" + savepro, "");
				ports = settings.getString("port" + savepro, "21");
				usernames = settings.getString("user" + savepro, "");
				passwords = settings.getString("pass" + savepro, "");
				modes = settings.getInt("mode" + savepro, 0);
				mode = (modes == 0) ? Mode.FTP : Mode.SFTP;
				
				ip.setText(ips);
				port.setText(ports);
				username.setText(usernames);
				password.setText(passwords);
			}

			public void onNothingSelected(AdapterView<?> arg0) {}
		});

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login:

			String ip = this.ip.getText().toString();
			String port = this.port.getText().toString();
			String user = this.username.getText().toString();
			String pass = this.password.getText().toString();
			String savepro = String.valueOf(saveprofil);
			Log.d(TAG, savepro);
			if (save.isChecked()) {
				SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("ip" + savepro, ip);
				editor.putString("port" + savepro, port);
				editor.putString("user" + savepro, user);
				editor.putString("pass" + savepro, pass);
				editor.putInt("mode" + savepro, modes);
				// Commit the edits!
				editor.commit();
				Log.d(TAG, "Saved!");
			}

			// new Login().execute(ip, port, user, pass);
			Bundle basket = new Bundle();
			basket.putString("ip", ip);
			basket.putString("port", port);
			basket.putString("user", user);
			basket.putString("pass", pass);
			Intent i;
			if (mode == Mode.FTP) {
				i = new Intent(MainActivity.this, Ftp.class);
				i.putExtras(basket);
				startActivity(i);
			} else if (mode == Mode.SFTP) {
				i = new Intent(MainActivity.this, Sftp.class);
				i.putExtras(basket);
				startActivity(i);
			}

			break;

		}

	}

	public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
		mode = (pos == 0) ? Mode.FTP : Mode.SFTP;
		Log.d(TAG, mode.toString());
		
	}

	public void onNothingSelected(AdapterView<?> arg0) {
	}

}
