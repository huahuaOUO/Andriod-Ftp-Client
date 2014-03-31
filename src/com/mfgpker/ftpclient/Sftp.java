/**
 * Project: FtpClient
 * Package: com.mfgpker.ftpclient
 * File Name: Ssh.java
 * Author: mfgpker
 * Date: 24/03/2014
 * Time: 12.24.38
 */
package com.mfgpker.ftpclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;

import org.apache.commons.net.ftp.FTPFile;

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

public class Sftp extends Activity implements OnClickListener, OnItemClickListener, OnItemLongClickListener {

	private static String TAG = "SFTP";

	private List<Content> myContents = new ArrayList<Content>();

	String SFTPHOST;
	String SFTPUSER;
	String SFTPPASS;
	String workingDir, orginalDir;

	private Button btnUpload, btnDisconnect, btnContent;

	private SftpClient sftpclient;

	private ListView contentList;
	private TextView txtPath;

	boolean islogin;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ftp);

		Bundle gotBasket = getIntent().getExtras();

		SFTPHOST = gotBasket.getString("ip");
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
		sftpclient = new SftpClient();

		new Login().execute(SFTPHOST, SFTPUSER, SFTPPASS);
		// Disconnect();
	}

	private void Disconnect() {
		sftpclient.Disconnect();
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
			Log.d(TAG, "edfewfrefgrewgtrwgtrw");
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

	private class Login extends AsyncTask<String, String, Integer> {

		protected Integer doInBackground(String... args) {
			String host = args[0];
			String user = args[1];
			String password = args[2];

			try {
				SSHClient ssh = new SSHClient();
				ssh.loadKnownHosts();
				ssh.connect(host, 22);
				ssh.authPublickey(user);
				ssh.authPassword(user, password);
				final Session session = ssh.startSession();
				final Command cmd = session.exec("ping -c 1 google.com");
				System.out.println(IOUtils.readFully(cmd.getInputStream()).toString());
				cmd.join(5, TimeUnit.SECONDS);
				System.out.println("\n** exit status: " + cmd.getExitStatus());
				
				
				
				session.close();
				ssh.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			} 

			return 0;

		}

		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);

			if (result == 1) {
				workingDir = sftpclient.getHome();
			}

			String con = result == 1 ? "true" : "false";
			Toast.makeText(Sftp.this, con, Toast.LENGTH_SHORT).show();
		}

	}

	private class Updatelist extends AsyncTask<String, String, String> {

		protected String doInBackground(String... params) {
			myContents.clear();
			String[] contents = null;
			int id = 0;

			for (int i = 0; i < contents.length; i++) {
				Content content;
				String con = "";
				FTPFile file = null;
				String type = "";
				int iconID = -1;
				long size = 0;
				String checksum = "";

				content = new Content(id, con, type, size, file, iconID, checksum);
				myContents.add(content);
				id++;
			}
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
			Toast.makeText(Sftp.this, R.string.Updated, Toast.LENGTH_SHORT).show();
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
