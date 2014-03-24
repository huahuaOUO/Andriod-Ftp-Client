/**
 * Project: FtpClient
 * Package: com.mfgpker.ftpclient
 * File Name: Ssh.java
 * Author: mfgpker
 * Date: 24/03/2014
 * Time: 12.24.38
 */
package com.mfgpker.ftpclient;

import java.io.FileOutputStream;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class Sftp extends Activity {
	private static final String TEMP_FILENAME = "test.txt";
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sftp);

		String SFTPHOST = "192.168.1.129";
		int SFTPPORT = 22;
		String SFTPUSER = "frank";
		String SFTPPASS = "blankrank";
		String SFTPWORKINGDIR = "/home/frank/";

		Session session = null;
		Channel channel = null;
		ChannelSftp channelSftp = null;

		try {
			JSch jsch = new JSch();
			session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
			session.setPassword(SFTPPASS);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			//session.setConfig(config);
			session.connect(); // crash here!
			channel = session.openChannel("sftp");
			channel.connect();
			channelSftp = (ChannelSftp) channel;
			
			channelSftp.cd(SFTPWORKINGDIR);
			channelSftp.get("remotefile.txt", "localfile.txt");
			//File f = new File(FILETOTRANSFER);
			//channelSftp.put(new FileInputStream(f), f.getName());
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		/*
		 * JSch jsch = new JSch(); Session session = null; try { session =
		 * jsch.getSession("frank", "192.168.1.129", 21);
		 * session.setConfig("StrictHostKeyChecking", "no");
		 * session.setPassword("blankrank"); session.connect();
		 * 
		 * Channel channel = session.openChannel("sftp"); channel.connect();
		 * ChannelSftp sftpChannel = (ChannelSftp) channel; String d = sftpChannel.getHome(); Log.d("jscj", d);
		 * //sftpChannel.get("remotefile.txt", "localfile.txt");
		 * sftpChannel.exit(); session.disconnect(); } catch (JSchException e) {
		 * e.printStackTrace(); //To change body of catch statement use File |
		 * Settings | File Templates. } catch (SftpException e) {
		 * e.printStackTrace(); } finally{
		 * 
		 * }
		 */
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
	
}
