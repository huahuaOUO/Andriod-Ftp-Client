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
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.VideoView;

public class VideoPlayer extends Activity implements OnCompletionListener, OnPreparedListener {

	private String filename, filepath, TAG = "VideoPlayer";

	private VideoView videoPlayer;

	//private MediaController ctlr;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vidoe);
		Bundle gotBasket = getIntent().getExtras();
		filename = gotBasket.getString("name");
		filepath = gotBasket.getString("path");
		videoPlayer = (VideoView) findViewById(R.id.videoView);
		videoPlayer.setOnPreparedListener(this);
		videoPlayer.setOnCompletionListener(this);
		videoPlayer.setKeepScreenOn(true);

		File root = Environment.getExternalStorageDirectory();
		Log.i(TAG, "Root external storage=" + root);
		Log.i(TAG, "filename = " + filename + ", filepath = " + filepath);
		// Get path to external video file and point videoPlayer to that file
		//String externalFilesDir = getExternalFilesDir(null).toString();
		//Log.i(TAG, "External files directory = " + externalFilesDir);
		// String videoResource = externalFilesDir +"/" + fileName;
		//Log.i(TAG,"videoPath="+videoResource);
		videoPlayer.setVideoPath(filepath);
	}

	public void onPrepared(MediaPlayer vp) {

		// Don't start until ready to play.  The arg of seekTo(arg) is the start point in
		// milliseconds from the beginning. Normally we would start at the beginning but,
		// for purposes of illustration, in this example we start playing 1/5 of
		// the way through the video if the player can do forward seeks on the video.

		if (videoPlayer.canSeekForward())
			videoPlayer.seekTo(videoPlayer.getDuration() / 5);
		videoPlayer.start();
	}

	/** This callback will be invoked when the file is finished playing */
	public void onCompletion(MediaPlayer mp) {
		// Statements to be executed when the video finishes.
		this.finish();
	}

	/** Use screen touches to toggle the video between playing and paused. */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			if (videoPlayer.isPlaying()) {
				videoPlayer.pause();
			} else {
				videoPlayer.start();
			}
			return true;
		} else {
			return false;
		}
	}
}
