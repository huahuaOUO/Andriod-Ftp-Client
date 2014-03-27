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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.io.Util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class Ftp extends Activity implements OnClickListener, OnItemClickListener {

	MyFTPClient ftpclient;

	String workingDir, orginalDir;
	private static final String TAG = "FTP";
	private static final String TEMP_FILENAME = "test.txt";

	private Context cntx = null;

	private String ip, user, pass, port;
	private ListView contentList;
	private List<String> realcontents = new ArrayList<String>();
	Map<String, String> userMap = new HashMap<String, String>();
	private Button btnUpload, btnDisconnect, btnContent;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ftp);

		ftpclient = new MyFTPClient();
		createDummyFile();
		cntx = this.getBaseContext();
		btnUpload = (Button) findViewById(R.id.upload);
		btnDisconnect = (Button) findViewById(R.id.disconnect);
		btnContent = (Button) findViewById(R.id.getContent);

		btnDisconnect.setOnClickListener(this);
		btnUpload.setOnClickListener(this);
		btnContent.setOnClickListener(this);

		contentList = (ListView) findViewById(R.id.contentList);
		contentList.setOnItemClickListener(this);

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
			showFileChooser();
			break;
		case R.id.getContent:
			for (String g : realcontents) {
				Log.d(TAG, g);
			}
			updateList();
			// updateList();
			// SimpleAdapter simpleAdpt = new SimpleAdapter(Ftp.this,
			// realcontents, R.id.contentList);
			// contentList.setAdapter(new ArrayAdapter<String>(Ftp.this,
			// R.id.contentList, realcontents));
			break;
		case R.id.disconnect:
			Disconnect();
			logout();
			break;
		}

	}

	public void onItemClick(AdapterView<?> arg0, View v, int pos, long id) {
		String cont = realcontents.get(pos).toString();
		String type = userMap.get(cont);
		Log.d(TAG, type + ": " + cont);
		if (type.equals("dir")) {
			new ChangeDir().execute(cont);
		} else if (type.equals("file")) {
			new DownloadFile().execute(cont, Environment.getExternalStorageDirectory().getPath());
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
		realcontents.clear();
		Intent i = new Intent(Ftp.this, MainActivity.class);
		startActivity(i);
		this.finish();
		Log.d(TAG, "BYE BYE");
	}

	void Disconnect() {
		realcontents.clear();
		new Thread(new Runnable() {
			public void run() {
				ftpclient.ftpDisconnect();
				Log.d(TAG, "Disconnect");
			}
		}).start();
	}

	protected void onDestroy() {
		super.onDestroy();
		Disconnect();
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

	private boolean yolo = false;

	private void getContent() {
		new Thread("contentThread") {
			public void run() {
				realcontents.clear();
				userMap.clear();
				String[] contents;

				// ftpclient.ftpPrintFilesList(workingDir);
				contents = ftpclient.getContentList(workingDir);
				Log.d(TAG, "begin----------------------------begin");
				Log.d(TAG, "length: " + contents.length);
				for (int i = 0; i < contents.length; i++) {
					// Log.d(TAG, con);
					String con = contents[i];
					if (con.startsWith("file:")) {
						con = con.substring(5);
						Log.d(TAG, "file: " + con);
						userMap.put(con, "file");
					} else {
						con = con.substring(10) + "/";
						Log.d(TAG, "dir: " + con);
						userMap.put(con, "dir");
					}
					realcontents.add(con);
				}
				Log.d(TAG, "Done----------------------------Done");
				Log.d(TAG, "*realcontents, length: " + realcontents.size());
				yolo = true;
				// updateList();
			}
		}.start();
	}

	private void updateList() {
		yolo = false;
		getContent();
		while (!yolo) {
		}

		Log.d(TAG, "yolo is " + yolo);
		contentList.setAdapter(new ArrayAdapter<String>(Ftp.this, android.R.layout.simple_list_item_1, realcontents));
		Toast.makeText(Ftp.this, "Updated", Toast.LENGTH_SHORT).show();
	}

	public void onBackPressed() {
		if (!workingDir.equals(orginalDir)) {
			new ChangeDir().execute("../");
		}

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
				// Replace your UID & PW here
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
					// status = ftpclient.ftpUpload(TEMP_FILENAME,
					// TEMP_FILENAME, "/", cntx);
					workingDir = ftpclient.ftpGetCurrentWorkingDirectory();
					orginalDir = workingDir;
					res = "true";
				} else {
					// Toast.makeText(getApplicationContext(),
					// "Connection failed", 2000).show();
					Log.e("login", "Connection failed: " + MyFTPClient.replay);
					res = "false";
				}
				dialog.dismiss();
				return res;
			} catch (Exception e) {
				e.printStackTrace();
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
				// basket.put
				btnDisconnect.setEnabled(true);
				btnUpload.setEnabled(true);
				btnContent.setEnabled(true);

				// getContent();
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

		protected void onPreExecute() {

		}

		protected String doInBackground(String... args) {
			ftpclient.ftpChangeDirectory(args[0]);
			workingDir = ftpclient.ftpGetCurrentWorkingDirectory();

			Log.d(TAG, "ChangeDir");
			return "";
		}

		protected void onProgressUpdate(Integer... progress) {
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
			int errorcode = ftpclient.mFTPClient.getReplyCode();
			Log.d(TAG, "Code: " + errorcode);

			if (status == true) {
				Log.d(TAG, "Upload success");
			} else {
				Log.e(TAG, "Upload failed**");

			}
			return "";
		}

		protected void onProgressUpdate(Integer... progress) {
		}

		protected void onPostExecute(String result) {
			updateList();
		}
	}

	public class DownloadFile extends AsyncTask<String, Integer, String> {

		Bitmap bitmap;
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
			
			
			
			if (name.endsWith(".jpg.") || name.endsWith(".png.")) {
				System.out.println("shoit");
				try {
					ftpclient.mFTPClient.enterLocalPassiveMode();
					ftpclient.mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
					ftpclient.mFTPClient.setControlKeepAliveTimeout(300);
					InputStream is = ftpclient.mFTPClient.retrieveFileStream(name);
					System.out.println("is: " + is);
					ftpclient.ftpConnect(ip, user, pass, Integer.parseInt(port));
					Bitmap bitmap1 = BitmapFactory.decodeStream(is);
					
					ByteArrayOutputStream blob = new ByteArrayOutputStream();
					System.out.println("BLOB: " + blob);
					if (name.endsWith(".png"))
						bitmap1.compress(CompressFormat.PNG, 0, blob);
					else if (name.endsWith(".jpg"))
						bitmap1.compress(CompressFormat.JPEG, 0, blob);
					byte[] bitmapdata = blob.toByteArray();
					bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
					
					is.close();
					//blob.close();

					try {
						File newFolder = new File(Environment.getExternalStorageDirectory(), "ftp-clients-downloads");
						if (!newFolder.exists()) {
							newFolder.mkdir();
						}
						try {
							File file = new File(newFolder, name);
							if (!file.exists())
								file.createNewFile();
							FileOutputStream fos = null;
							try {
								fos = new FileOutputStream(file);

								if (name.endsWith(".png"))
									bitmap.compress(Bitmap.CompressFormat.PNG, 80, fos);
								else if (name.endsWith(".jpg"))
									bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);

								// bitmap.compress(Bitmap.CompressFormat.PNG,
								// 90, fos);
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							} finally {
								try {
									fos.close();
								} catch (Throwable ignore) {
								}
							}
						} catch (Exception ex) {
							System.out.println("ex: " + ex);
						}
					} catch (Exception e) {
						System.out.println("e: " + e);
					} // done }

					System.out.println("=======u=====");
				} catch (IOException e) {
					e.printStackTrace();
				}

			} else {

				try {
					ftpclient.mFTPClient.setControlKeepAliveTimeout(300);
					ftpclient.mFTPClient.enterLocalPassiveMode();
					ftpclient.mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
					InputStream is = ftpclient.mFTPClient.retrieveFileStream(name);
					String error = ftpclient.mFTPClient.getReplyString();
					System.out.println("status: " + status + ", error: " + error);

					if (is != null) {
						ftpclient.ftpConnect(ip, user, pass, Integer.parseInt(port));
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
									e.printStackTrace();
								}
							} catch (Exception ex) {
								System.out.println("ex: " + ex);
							}
						} catch (Exception e) {
							System.out.println("e: " + e);
						} // done }
					} else {
						Log.d(TAG, "inputstream is null");
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			if (status == true) {
				Log.d(TAG, "Download success");
			} else {
				Log.e(TAG, "Download failed");
			}

			return name;
		}

		protected void onPostExecute(String result) {

		}
	}

	public static String convertStreamToString(InputStream is) throws Exception {
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

	private String Readfile(String name) {
		String ret = "";
		try {
			InputStream gg = openFileInput(name);
			if (gg != null) {
				InputStreamReader inputStreamReader = new InputStreamReader(gg);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String receiveString = "";
				StringBuilder stringBuilder = new StringBuilder();

				while ((receiveString = bufferedReader.readLine()) != null) {
					stringBuilder.append(receiveString);
				}

				gg.close();
				ret = stringBuilder.toString();
				Log.d(TAG, "*Content: " + ret);
			}
		} catch (FileNotFoundException e) {
			Log.e("login activity", "File not found: " + e.toString());
		} catch (IOException e) {
			Log.e("login activity", "Can not read file: " + e.toString());
		}
		return ret;
	}

	Bitmap bitmap;

	private synchronized boolean DownloadImage(String name) {
		boolean status = false;

		try {
			// ftpclient.mFTPClient.enterLocalPassiveMode();
			ftpclient.mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);

			InputStream is = ftpclient.mFTPClient.retrieveFileStream(name);
			ftpclient.ftpConnect(ip, user, pass, Integer.parseInt(port));
			Bitmap bitmap1 = BitmapFactory.decodeStream(is);
			ByteArrayOutputStream blob = new ByteArrayOutputStream();
			System.out.println("BLOB: " + blob);
			if (name.endsWith(".png"))
				bitmap1.compress(CompressFormat.PNG, 0, blob);
			else if (name.endsWith(".jpg"))
				bitmap1.compress(CompressFormat.JPEG, 0, blob);
			byte[] bitmapdata = blob.toByteArray();
			bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);

			is.close();
			blob.close();

			try {
				File newFolder = new File(Environment.getExternalStorageDirectory(), "ftp-clients-downloads");
				if (!newFolder.exists()) {
					newFolder.mkdir();
				}
				try {
					File file = new File(newFolder, name);
					if (!file.exists())
						file.createNewFile();
					FileOutputStream fos = null;
					try {
						fos = new FileOutputStream(file);

						if (name.endsWith(".png"))
							bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
						else if (name.endsWith(".jpg"))
							bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);

						// bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} finally {
						try {
							fos.close();
						} catch (Throwable ignore) {
						}
					}
				} catch (Exception ex) {
					System.out.println("ex: " + ex);
				}
			} catch (Exception e) {
				System.out.println("e: " + e);
			} // done }

			System.out.println("=======u=====");
		} catch (IOException e) {
			e.printStackTrace();
		}

		return status;
	}
}
