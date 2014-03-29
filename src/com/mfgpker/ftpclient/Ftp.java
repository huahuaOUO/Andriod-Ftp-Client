/**
 * Project: FtpClient
 * Package: com.mfgpker.ftpclient
 * File Name: Ftp.java
 * Author: mfgpker
 * Date: 23/03/2014
 * Time: 12.24.38
 */
package com.mfgpker.ftpclient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPFile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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

public class Ftp extends Activity implements OnClickListener, OnItemClickListener, OnItemLongClickListener {

	MyFTPClient ftpclient;

	String workingDir, orginalDir;
	private static final String TAG = "FTP";

	private Context cntx = null;

	private String ip, user, pass, port;
	private ListView contentList;
	private TextView txtPath;
	private List<Content> rcontents = new ArrayList<Content>();

	private Button btnUpload, btnDisconnect, btnContent;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ftp);

		ftpclient = new MyFTPClient();
		cntx = this.getBaseContext();
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

		btnDisconnect.setEnabled(false);
		btnUpload.setEnabled(false);
		btnContent.setEnabled(false);

		Bundle gotBasket = getIntent().getExtras();
		ip = gotBasket.getString("ip");
		port = gotBasket.getString("port");
		user = gotBasket.getString("user");
		pass = gotBasket.getString("pass");

		if (port.isEmpty())
			port = "21";

		// finishActivity(0);

		new Login().execute(ip, port, user, pass);
		// contentList.setAdapter(new ArrayAdapter<String>(Ftp.this,
		// android.R.layout.simple_list_item_1, realcontents));
	}

	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.upload:
			boolean hm = isConnected();
			if (hm) {
				showFileChooser();
			}
			break;
		case R.id.getContent:
			boolean hm1 = isConnected();
			if (hm1) {
				for (Content g : rcontents) {
					Log.d(TAG, g.getName());
				}
				updateList();
			}

			break;
		case R.id.disconnect:
			Disconnect();
			logout();
			break;
		}

	}

	private void switchview(Button bdown, Button bopen, Button brename, Button bdelete, Button bcancal, ImageView icon, TextView name, TextView size, boolean switc) {
		if (switc) {
			bdown.setVisibility(View.VISIBLE);
			bopen.setVisibility(View.VISIBLE);
			brename.setVisibility(View.VISIBLE);
			bdelete.setVisibility(View.VISIBLE);
			bcancal.setVisibility(View.VISIBLE);

			icon.setVisibility(View.GONE);
			name.setVisibility(View.GONE);
			size.setVisibility(View.GONE);
		} else {
			bdown.setVisibility(View.GONE);
			bopen.setVisibility(View.GONE);
			brename.setVisibility(View.GONE);
			bdelete.setVisibility(View.GONE);
			bcancal.setVisibility(View.GONE);

			icon.setVisibility(View.VISIBLE);
			name.setVisibility(View.VISIBLE);
			size.setVisibility(View.VISIBLE);
		}
	}

	public boolean onItemLongClick(AdapterView<?> parent, View v, int pos, long id) {
		boolean hm = isConnected();
		final Content cur = rcontents.get(pos);
		final String cont = cur.getName();
		String type = cur.getType();
		Log.d(TAG, type + " : LONG : " + cont);

		if (hm && type.equals("file")) {

			final Button bdown = (Button) v.findViewById(R.id.menu_btnDownload);
			final Button bopen = (Button) v.findViewById(R.id.menu_btnOpen);
			final Button brename = (Button) v.findViewById(R.id.menu_btnRename);
			final Button bdelete = (Button) v.findViewById(R.id.menu_btnDelete);
			final Button bcancal = (Button) v.findViewById(R.id.menu_btnCancel);

			final ImageView icon = (ImageView) v.findViewById(R.id.item_icon);
			final TextView name = (TextView) v.findViewById(R.id.item_txtName);
			final TextView size = (TextView) v.findViewById(R.id.item_txtsize);

			switchview(bdown, bopen, brename, bdelete, bcancal, icon, name, size, true);

			bdown.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					new DownloadFile().execute(cont, Environment.getExternalStorageDirectory().getPath());
					switchview(bdown, bopen, brename, bdelete, bcancal, icon, name, size, false);
				}
			});

			bopen.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					//new DownloadFile().execute(cont, Environment.getExternalStorageDirectory().getPath());
					switchview(bdown, bopen, brename, bdelete, bcancal, icon, name, size, false);
				}
			});

			
			brename.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					switchview(bdown, bopen, brename, bdelete, bcancal, icon, name, size, false);
				}
			});
			
			
			bdelete.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					switchview(bdown, bopen, brename, bdelete, bcancal, icon, name, size, false);
				}
			});
			
			bcancal.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					switchview(bdown, bopen, brename, bdelete, bcancal, icon, name, size, false);
				}
			});
		}

		return true;
	}

	public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
		boolean hm = isConnected();
		Content cur = rcontents.get(pos);
		String cont = cur.getName();
		String type = cur.getType();
		Log.d(TAG, type + ": " + cont);

		if (hm) {
			if (type.equals("dir")) {
				new ChangeDir().execute(cont);
			} else if (type.equals("file")) {
				new DownloadFile().execute(cont, Environment.getExternalStorageDirectory().getPath());
			}
		}

	}

	private static final int FILE_SELECT_CODE = 0;

	private void showFileChooser() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);

		try {
			startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
		} catch (android.content.ActivityNotFoundException ex) {
			// Potentially direct the user to the Market with a Dialog
			Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case FILE_SELECT_CODE:
			if (resultCode == RESULT_OK) {
				// Get the Uri of the selected file
				Uri uri = data.getData();
				Log.d(TAG, "File Uri: " + uri.toString());
				// Get the path
				String path;
				try {
					path = getPath(this, uri);
					File fil = new File(path);
					String name = fil.getName();
					System.out.println(fil.exists());
					new UploadFile().execute(path, name);
					Log.d(TAG, "File Path: " + path + ", name: " + name);
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}

				// Get the file instance
				// File file = new File(path);
				// Initiate the upload
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public static String getPath(Context context, Uri uri) throws URISyntaxException {
		if ("content".equalsIgnoreCase(uri.getScheme())) {
			String[] projection = { "_data" };
			Cursor cursor = null;

			try {
				cursor = context.getContentResolver().query(uri, projection, null, null, null);
				int column_index = cursor.getColumnIndexOrThrow("_data");
				if (cursor.moveToFirst()) {
					return cursor.getString(column_index);
				}
			} catch (Exception e) {
				// Eat it
			}
		} else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	private void logout() {
		rcontents.clear();
		Intent i = new Intent(Ftp.this, MainActivity.class);
		startActivity(i);
		this.finish();
		Log.d(TAG, "BYE BYE");
	}

	void Disconnect() {
		rcontents.clear();
		new Thread(new Runnable() {
			public void run() {
				ftpclient.ftpDisconnect();
				Log.d(TAG, "Disconnect");
			}
		}).start();
	}

	void Reconnect() {
		new Thread(new Runnable() {
			public void run() {
				ftpclient.ftpConnect(ip, user, pass, Integer.parseInt(port));
				ftpclient.ftpChangeDirectory(workingDir);
				updateList();
				Log.d(TAG, "Reconnect");
			}
		}).start();
	}

	protected void onDestroy() {
		super.onDestroy();
		Disconnect();
	}

	private boolean yolo = false;

	private void getContent() {
		new Thread("contentThread") {
			public void run() {
				rcontents.clear();
				String[] contents;

				contents = ftpclient.getContentList(workingDir);
				for (int i = 0; i < contents.length; i++) {
					Content content;
					String con = contents[i];
					FTPFile file = null;
					String type;
					int iconID = -1;
					long size = 0;

					if (con.startsWith("file:")) {
						con = con.substring(5);
						try {
							file = ftpclient.mFTPClient.mlistFile(workingDir + "/" + con);
						} catch (IOException e) {
							e.printStackTrace();
						}

						if (file != null) {
							size = file.getSize();
						}
						Log.d(TAG, "file: " + con);
						Log.d(TAG, "filesize: " + size);
						iconID = R.drawable.file;
						type = "file";
					} else {
						con = con.substring(10) + "/";
						Log.d(TAG, "dir: " + con);
						type = "dir";
						iconID = R.drawable.dir;
					}

					content = new Content(con, type, size, file, iconID);
					rcontents.add(content);
				}
				Log.d(TAG, "*realcontents, length: " + rcontents.size());
				yolo = true;
			}
		}.start();
	}

	private void updateList() {
		yolo = false;
		getContent();
		while (!yolo) {
		}
		txtPath.setText("Path: " + workingDir);
		Log.d(TAG, "yolo is " + yolo);
		ArrayAdapter<Content> adapter = new MyListAdapter();
		contentList.setAdapter(adapter);
		// contentList.setAdapter(new ArrayAdapter<String>(Ftp.this,
		// android.R.layout.simple_list_item_1, realcontents));
		Toast.makeText(Ftp.this, "Updated", Toast.LENGTH_SHORT).show();
	}

	public void onBackPressed() {
		if (!workingDir.equals(orginalDir)) {
			new ChangeDir().execute("../");
		}

	}

	boolean yy;

	private synchronized boolean isConnected() {
		boolean connect = ftpclient.mFTPClient.isConnected();
		Log.d(TAG, connect ? "con = true" : "con = false");
		yy = connect;
		if (!connect) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			builder.setTitle("You have been disconnected from server.");
			builder.setMessage("Do you want to reconnect?");

			builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					Reconnect();
					yy = true;
					dialog.dismiss();
				}

			});

			builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					logout();
					yy = true;
					dialog.dismiss();
				}
			});

			AlertDialog alert = builder.create();
			alert.show();

		}
		return yy;
	}

	public class Login extends AsyncTask<String, Integer, String> {

		ProgressDialog dialog;

		protected void onPreExecute() {
			dialog = new ProgressDialog(Ftp.this);
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dialog.setMax(100);
			dialog.show();

		}

		protected String doInBackground(String... args) {

			try {
				boolean status = false;
				String res = "false";
				status = ftpclient.ftpConnect(args[0], args[2], args[3], Integer.parseInt(args[1]));
				for (int i = 0; i < 20; i++) {
					publishProgress(5);
					try {
						Thread.sleep(88);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if (status == true) {
					Log.d("login", "Connection Success");
					workingDir = ftpclient.ftpGetCurrentWorkingDirectory();
					orginalDir = workingDir;
					res = "true";

					String featureValue;
					try {

						boolean success = ftpclient.mFTPClient.features();
						if (success) {
							Log.d(TAG, ftpclient.mFTPClient.getReplyString());
						} else {
							Log.e(TAG, "Could not query server features");
						}

						success = ftpclient.mFTPClient.hasFeature("SIZE");

						if (success) {
							Log.d(TAG, "The server supports SIZE feature.");
						} else {
							Log.e(TAG, "The server does not support SIZE feature.");
						}

						featureValue = ftpclient.mFTPClient.featureValue("AUTH");
						if (featureValue == null) {
							Log.e(TAG, "The server does not support this feature.");
						} else {
							Log.d(TAG, "Value of AUTH feature: " + featureValue);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}

				} else {
					Log.e("login", "Connection failed: " + MyFTPClient.replay);
					res = "false";
				}
				dialog.dismiss();
				return res;
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
			return null;
		}

		protected void onProgressUpdate(Integer... progress) {
			dialog.incrementProgressBy(progress[0]);
		}

		protected void onPostExecute(String result) {
			if (result == null) {
				Toast.makeText(Ftp.this, "ERROR", Toast.LENGTH_LONG).show();
			}

			if (result == "true") {
				Toast.makeText(Ftp.this, MyFTPClient.replay, Toast.LENGTH_LONG).show();
				btnDisconnect.setEnabled(true);
				btnUpload.setEnabled(true);
				btnContent.setEnabled(true);

				@SuppressWarnings("unused")
				Bitmap bmd, bmu;
				InputStream isd, isu;
				if (ftpclient.isAllowToDownload()) {
					isd = getResources().openRawResource(R.drawable.green);
				} else {
					isd = getResources().openRawResource(R.drawable.red);
				}

				if (ftpclient.isAllowToUpload()) {
					isu = getResources().openRawResource(R.drawable.green);
				} else {
					isu = getResources().openRawResource(R.drawable.red);
				}

				bmd = BitmapFactory.decodeStream(isd);
				bmu = BitmapFactory.decodeStream(isu);

				// imagedownload.setImageBitmap(bmd);
				// imageupload.setImageBitmap(bmu);

				try {
					isd.close();
					isu.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

				updateList();
				Log.d(TAG, "worksdir: " + workingDir);
			} else {
				Disconnect();
				Intent i = new Intent(Ftp.this, MainActivity.class);
				Bundle b = new Bundle();
				b.putString("failed", "Connection failed");
				i.putExtras(b);
				startActivity(i);
			}

		}
	}

	public class ChangeDir extends AsyncTask<String, Integer, String> {

		protected String doInBackground(String... args) {
			ftpclient.ftpChangeDirectory(args[0]);
			workingDir = ftpclient.ftpGetCurrentWorkingDirectory();

			Log.d(TAG, "ChangeDir");
			return "";
		}

		protected void onPostExecute(String result) {
			updateList();
		}
	}

	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	/* Checks if external storage is available to at least read */
	public boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}

	public class UploadFile extends AsyncTask<String, Integer, String> {

		protected void onPreExecute() {

		}

		protected String doInBackground(String... args) {
			boolean status = false;
			String path = args[0];
			Log.d(TAG, "path: " + path);
			String name = args[1];
			Log.d(TAG, "name: " + name);

			File f = new File(path);
			String abspath = f.getAbsolutePath();

			status = ftpclient.ftpUpload(abspath, f.getName(), "/", cntx);
			String errorcode = ftpclient.mFTPClient.getReplyString();
			Log.d(TAG, "Code: " + errorcode);
			if (status == true) {
				Log.d(TAG, "Upload success");
			} else {
				Log.e(TAG, "Upload failed**");

			}
			return errorcode;
		}

		protected void onProgressUpdate(Integer... progress) {
		}

		protected void onPostExecute(String result) {

			Toast.makeText(cntx, result, Toast.LENGTH_SHORT).show();
			updateList();
		}
	}

	public class DownloadFile extends AsyncTask<String, Integer, String> {

		String name;

		protected String doInBackground(String... args) {
			boolean status = false;
			name = args[0];
			String path = args[1];
			Log.d(TAG, "name: " + name);
			Log.d(TAG, "path: " + path);
			System.out.println(name);

			String s = workingDir.equals("/") ? "" : workingDir;
			System.out.println(s);

			try {
				ftpclient.mFTPClient.setControlKeepAliveTimeout(300);
				ftpclient.mFTPClient.enterLocalPassiveMode();
				ftpclient.mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
				InputStream is = ftpclient.mFTPClient.retrieveFileStream(name);
				String error = ftpclient.mFTPClient.getReplyString();
				System.out.println("Replay from server: " + error);

				if (is != null) {

					byte[] bytes = IOUtils.toByteArray(is);
					is.close();
					// Log.d(TAG, "content*: " + new String(bytes));

					boolean writeable = isExternalStorageWritable();
					boolean readable = isExternalStorageReadable();
					if (!writeable)
						Log.e(TAG, "writeable is " + writeable);
					if (!readable)
						Log.e(TAG, "readable is " + readable);
					// create folder..
					try {
						File newFolder = new File(Environment.getExternalStorageDirectory(), "ftp-clients-downloads");
						if (!newFolder.exists()) {
							newFolder.mkdir();
						}
						try {
							File file = new File(newFolder, name);
							if (!file.exists())
								file.createNewFile();
							FileOutputStream fos;
							try {
								fos = new FileOutputStream(file);
								fos.write(bytes);
								status = true;
								fos.flush();
								fos.close();
							} catch (FileNotFoundException e) {
								Log.e(TAG, e.getMessage());
							}
						} catch (Exception e) {
							Log.e(TAG, e.getMessage());
						}
					} catch (Exception e) {
						Log.e(TAG, e.getMessage());
					}
				} else {
					Log.e(TAG, "inputstream is null");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			ftpclient.ftpConnect(ip, user, pass, Integer.parseInt(port));
			ftpclient.ftpChangeDirectory(workingDir);
			String statuss = "failed";
			if (status == true) {
				Log.d(TAG, "Download success");
				statuss = "success";
			} else {
				Log.e(TAG, "Download failed");
			}

			return statuss;
		}

		protected void onPostExecute(String result) {
			Toast.makeText(cntx, "Download " + result, Toast.LENGTH_SHORT).show();
		}
	}

	public class PlayVideo extends AsyncTask<String, Integer, String> {

		String name;

		protected String doInBackground(String... args) {
			boolean status = false;
			name = args[0];
			Log.d(TAG, "name: " + name);
			System.out.println(name);

			String s = workingDir.equals("/") ? "" : workingDir;
			System.out.println(s);
			String localFilePath = "";
			try {

				File newFolder = new File(Environment.getExternalStorageDirectory() + "/ftp-clients-downloads", "temp");
				if (!newFolder.exists()) {
					newFolder.mkdir();
				}

				ftpclient.mFTPClient.setKeepAlive(true);
				ftpclient.mFTPClient.enterLocalPassiveMode();
				ftpclient.mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
				ftpclient.mFTPClient.setBufferSize(2224 * 2224);
				localFilePath = newFolder.getAbsolutePath() + "/" + name;
				Log.d(TAG, "localFilePath: " + localFilePath);
				FileOutputStream fos = new FileOutputStream(localFilePath);
				status = ftpclient.mFTPClient.retrieveFile(name, fos);
				fos.close();
				String error = ftpclient.mFTPClient.getReplyString();
				System.out.println("Replay from server: " + error);

			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			// ftpclient.ftpConnect(ip, user, pass, Integer.parseInt(port));
			// ftpclient.ftpChangeDirectory(workingDir);
			if (status == true) {
				Log.d(TAG, "Download success");
			} else {
				Log.e(TAG, "Download failed");
			}

			return localFilePath;
		}

		protected void onPostExecute(String result) {
			if (!result.isEmpty()) {
				Intent i = new Intent(Ftp.this, VideoPlayer.class);
				Bundle b = new Bundle();
				b.putString("name", name);
				b.putString("path", result);
				i.putExtras(b);
				startActivity(i);
			}
		}
	}

	@SuppressWarnings("unused")
	private static String convertStreamToString(InputStream is) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;

		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}

		is.close();
		reader.close();

		return sb.toString();
	}

	private class MyListAdapter extends ArrayAdapter<Content> {

		public MyListAdapter() {
			super(Ftp.this, R.layout.item_view, rcontents);

		}

		public View getView(int pos, View convertView, ViewGroup parent) {
			View itemView = convertView;
			if (itemView == null) {
				itemView = getLayoutInflater().inflate(R.layout.item_view, parent, false);
			}

			// find car work with,
			Content currentcont = rcontents.get(pos);

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
