/**
 * Project: FtpClient
 * Package: com.mfgpker.ftpclient
 * File Name: Ssh.java
 * Author: mfgpker
 * Date: 24/03/2014
 * Time: 12.24.38
 */
package com.mfgpker.ftpclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class Sftp extends Activity implements OnClickListener {

	Session session = null;
	Channel channel = null;
	ChannelSftp channelSftp = null;
	JSch jsch = null;

	String SFTPHOST;
	int SFTPPORT;
	String SFTPUSER;
	String SFTPPASS;
	String SFTPWORKINGDIR;

	Button disc;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sftp);

		Bundle gotBasket = getIntent().getExtras();
		SFTPHOST = gotBasket.getString("ip");
		String port = gotBasket.getString("port");
		SFTPPORT = Integer.parseInt(port.isEmpty()? "22" : port);
		SFTPUSER = gotBasket.getString("user");
		SFTPPASS = gotBasket.getString("pass");

		disc = (Button) findViewById(R.id.disconnectstp);
		disc.setOnClickListener(this);

		ConnectAndLogin();
	}

	public void ConnectAndLogin() {
		new Thread(new Runnable() {
			public void run() {

				try {
					jsch = new JSch();
					session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
					session.setPassword(SFTPPASS);
					java.util.Properties config = new java.util.Properties();
					config.put("StrictHostKeyChecking", "no");
					session.setConfig(config);
					session.connect(); // crash here!
					channel = session.openChannel("sftp");
					channel.connect();

					channelSftp = (ChannelSftp) channel;

					SFTPWORKINGDIR = channelSftp.getHome();
					channelSftp.cd(SFTPWORKINGDIR);
					System.out.println(channelSftp.getHome());
					// channelSftp.get("remotefile.txt", "localfile.txt");
					// File f = new File(FILETOTRANSFER);
					// channelSftp.put(new FileInputStream(f), f.getName());

				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}
		}).start();

	}

	private void Disconnect() {
		channelSftp.exit();
		session.disconnect();
		Intent i = new Intent(Sftp.this, MainActivity.class);
		startActivity(i);
		this.finish();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.disconnectstp:
			Disconnect();
			Log.d("guirehgreu" , "edfewfrefgrewgtrwgtrw");
			break;
		}
	}

}
