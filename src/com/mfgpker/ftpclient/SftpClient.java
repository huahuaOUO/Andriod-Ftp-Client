/**
 * Project: FtpClient
 * Package: com.mfgpker.ftpclient
 * File Name: SftpClient.java
 * Author: mfgpker
 * Date: 31/03/2014
 * Time: 18.43.49
 */
package com.mfgpker.ftpclient;

import net.neoremind.sshxcute.core.ConnBean;
import net.neoremind.sshxcute.core.SSHExec;

public class SftpClient {
	public SSHExec ssh;
	private static final String TAG = "SftpClient";
	private String host, user, password;

	public boolean Login(String host, String user, String password) {
		boolean status;
		ConnBean cb = new ConnBean(host, user, password);
		ssh = SSHExec.getInstance(cb);
		status = ssh.connect();
		if (status) {
			this.host = host;
			this.user = user;
			this.password = password;
			
			/*CustomTask ct1 = new ExecCommand("echo 123");
			try {
				Result res = ssh.exec(ct1);
				if (res.isSuccess)
				{
				    Log.d(TAG, "Return code: " + res.rc);
				    Log.d(TAG, "sysout: " + res.sysout);
				}
				else
				{
					Log.e(TAG, "Return code: " + res.rc);
					Log.e(TAG, "error message: " + res.error_msg);
				}
			} catch (TaskExecFailException e) {
				Log.e(TAG, "ERROR: " + e.getMessage());
			}
			*/
		}

		return status;
	}

	public boolean Disconnect() {
		return ssh.disconnect();
	}

	public String getHome() {
		return "/home/" + user;
	}
}
