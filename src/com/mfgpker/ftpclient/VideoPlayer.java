/**
 * Project: FtpClient
 * Package: com.mfgpker.ftpclient
 * File Name: VideoPlayer.java
 * Author: mfgpker
 * Date: 28/03/2014
 * Time: 00.18.50
 */
package com.mfgpker.ftpclient;

import java.io.File;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoPlayer extends Activity implements OnCompletionListener, OnPreparedListener{

	private String filename, TAG = "VideoPlayer";
	
	private VideoView videoPlayer;
	//private MediaController ctlr;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vidoe);
		Bundle gotBasket = getIntent().getExtras();
		filename = gotBasket.getString("name");
		videoPlayer = (VideoView) findViewById(R.id.videoView);
		videoPlayer.setOnPreparedListener(this);
        videoPlayer.setOnCompletionListener(this);
        videoPlayer.setKeepScreenOn(true); 

        File root = Environment.getExternalStorageDirectory(); 
        Log.i(TAG, "Root external storage="+root);
        
        
	}

	public void onPrepared(MediaPlayer mp) {
		
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		
	}
}
