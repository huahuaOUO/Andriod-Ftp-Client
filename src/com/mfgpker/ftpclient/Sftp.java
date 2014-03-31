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
import android.os.AsyncTask;
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

	}

	private void Disconnect() {
		Intent i = new Intent(Sftp.this, MainActivity.class);
		startActivity(i);
		this.finish();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.upload:
			break;
		case R.id.getContent:
			new Updatelist().execute();
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

	private class Updatelist extends AsyncTask<String, String, String> {

		protected String doInBackground(String... params) {
			myContents.clear();
			int id = 0;
			/*
			 * try {
			 * 
			 * for (int i = 0; i < filelist.size(); i++) { Content content; String filename = filelist.get(i).toString(); String type = ""; int iconID = R.drawable.file; long size = 0; String checksum = "";
			 * 
			 * content = new Content(id, filename, type, size, null, iconID, checksum); myContents.add(content); id++; Log.d(TAG, filename); } } catch (SftpException e) { e.printStackTrace(); }
			 */
			return null;
		}

		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			txtPath.setText("Path: " + workingDir);
			ArrayAdapter<Content> adapter = new MyListAdapter();
			contentList.setAdapter(adapter);
			Toast.makeText(Sftp.this, "Updated", Toast.LENGTH_SHORT).show();
		}

	}

	// my listadapter
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
