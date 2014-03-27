/**
 * Project: FtpClient
 * Package: com.mfgpker.ftpclient
 * File Name: VideoPlayer.java
 * Author: mfgpker
 * Date: 28/03/2014
 * Time: 00.18.50
 */
package com.mfgpker.ftpclient;

import android.app.Activity;
import android.os.Bundle;

public class VideoPlayer extends Activity{

	private String filename;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vidoe);
		
		Bundle gotBasket = getIntent().getExtras();
		filename = gotBasket.getString("name");
		
	}
}
