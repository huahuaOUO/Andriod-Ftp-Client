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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	TextView ip, port, username, password;

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
			Intent i = new Intent(MainActivity.this, Ftp.class);
			i.putExtras(basket);
			startActivity(i);
			break;

		}

	}

	}
