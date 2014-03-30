/**
 * Project: FtpClient
 * Package: com.mfgpker.ftpclient
 * File Name: Ssh.java
 * Author: mfgpker
 * Date: 24/03/2014
 * Time: 12.24.38
 */
package com.mfgpker.ftpclient;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class Sftp extends Activity implements OnClickListener, OnItemClickListener, OnItemLongClickListener {

	Session session = null;
	Channel channel = null;
	ChannelSftp channelSftp = null;
	JSch jsch = null;
	private static String TAG = "SFTP";

	private List<Content> myContents = new ArrayList<Content>();

	String SFTPHOST;
	int SFTPPORT;
	String SFTPUSER;
	String SFTPPASS;
	String workingDir, orginalDir;

	private Button btnUpload, btnDisconnect, btnContent;

	private ListView contentList;
	private TextView txtPath;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ftp);

		Bundle gotBasket = getIntent().getExtras();
		SFTPHOST = gotBasket.getString("ip");
		String port = gotBasket.getString("port");
		SFTPPORT = Integer.parseInt(port.isEmpty() ? "22" : port);
		SFTPUSER = gotBasket.getString("user");
		SFTPPASS = gotBasket.getString("pass");

		btnUpload = (Button) findViewById(R.id.upload);
		btnDisconnect = (Button) findViewById(R.id.disconnect);
		btnContent = (Button) findViewById(R.id.getContent);
		txtPath = (TextView) findViewById(R.id.txtPath);

		btnDisconnect.setOnClickListener(this);
		btnUpload.setOnClickListener(this);
		btnContent.setOnClickListener(this);

		contentList = (ListView) findViewById(R.id.contentList);
		contentList.setOnItemClickListener(this);
		contentList.setOnItemLongClickListener(this);

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

					workingDir = channelSftp.getHome();
					orginalDir = workingDir;
					channelSftp.cd(workingDir);
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
		case R.id.upload:
			break;
		case R.id.getContent:
			updateList();
			break;

		case R.id.disconnect:
			Disconnect();
			Log.d("guirehgreu", "edfewfrefgrewgtrwgtrw");
			break;
		}
	}

	public boolean onItemLongClick(AdapterView<?> parent, View v, int pos, long id) {
		// TODO Auto-generated method stub
		Content cont = myContents.get(pos);
		return true;
	}

	public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
		// TODO Auto-generated method stub
		Content cont = myContents.get(pos);
	}

	private void updateList() {
		getContent();
		txtPath.setText("Path: " + workingDir);
		ArrayAdapter<Content> adapter = new MyListAdapter();
		contentList.setAdapter(adapter);
		Toast.makeText(Sftp.this, "Updated", Toast.LENGTH_SHORT).show();
	}

	private void getContent() {
		// TODO: get name of files and folders
		try {
			Vector filelist = channelSftp.ls(workingDir);
			for (int i = 0; i < filelist.size(); i++) {
				Log.d(TAG, filelist.get(i).toString());
			}
		} catch (SftpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class MyListAdapter extends ArrayAdapter<Content> {

		public MyListAdapter() {
			super(Sftp.this, R.layout.item_view, myContents);

		}

		public View getView(int pos, View convertView, ViewGroup parent) {
			View itemView = convertView;
			if (itemView == null) {
				itemView = getLayoutInflater().inflate(R.layout.item_view, parent, false);
			}

			// find car work with,
			Content currentcont = myContents.get(pos);

			// fill the view
			// icon
			ImageView image = (ImageView) itemView.findViewById(R.id.item_icon);
			image.setImageResource(currentcont.getIconID());

			// name
			TextView maketext = (TextView) itemView.findViewById(R.id.item_txtName);
			maketext.setText(currentcont.getName());

			// size
			TextView conditiontext = (TextView) itemView.findViewById(R.id.item_txtsize);
			conditiontext.setText("" + currentcont.getSize());

			return itemView;
		}

	}

}
