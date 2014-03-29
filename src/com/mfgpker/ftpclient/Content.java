/**
 * Project: FtpClient
 * Package: com.mfgpker.ftpclient
 * File Name: Content.java
 * Author: mfgpker
 * Date: 29/03/2014
 * Time: 17.25.16
 */
package com.mfgpker.ftpclient;

import org.apache.commons.net.ftp.FTPFile;

public class Content {

	private String name;
	private String type;
	private Long size;
	private FTPFile ftpFile;
	private int iconID;
	
	
	public Content(String name, String type, Long size, FTPFile ftpFile, int iconID) {
		super();
		this.name = name;
		this.type = type;
		this.size = size;
		this.ftpFile = ftpFile;
		this.iconID = iconID;
	}


	public String getName() {
		return name;
	}


	public String getType() {
		return type;
	}


	public Long getSize() {
		return size;
	}


	public FTPFile getFtpFile() {
		return ftpFile;
	}


	public int getIconID() {
		return iconID;
	}
	
	
}
