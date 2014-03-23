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
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener {



	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		View createButton = findViewById(R.id.login);
		createButton.setOnClickListener(this);

		View exitButton = findViewById(R.id.exit);
		exitButton.setOnClickListener(this);


	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login:
			
			break;

		case R.id.exit:
			this.finish();
			break;
		}

	}

	protected void onDestroy() {
		super.onDestroy();

	}

}
