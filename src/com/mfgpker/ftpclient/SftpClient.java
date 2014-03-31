/**
 * Project: FtpClient
 * Package: com.mfgpker.ftpclient
 * File Name: SftpClient.java
 * Author: mfgpker
 * Date: 31/03/2014
 * Time: 18.43.49
 */
package com.mfgpker.ftpclient;


public class SftpClient {
	private static final String TAG = "SftpClient";
	private String host, user, password;

	public boolean Login(String host, String user, String password) {
		boolean status = false;
		return status;
	}

	public boolean Disconnect() {
		return false;
	}

	public String getHome() {
		return "/home/" + user;
	}
}
