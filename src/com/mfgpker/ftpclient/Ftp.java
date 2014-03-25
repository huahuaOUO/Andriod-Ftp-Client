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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.ListView;
import android.widget.Toast;

public class Ftp extends Activity implements OnClickListener, OnItemClickListener {

	MyFTPClient ftpclient;

	String workingDir, orginalDir;
	private static final String TAG = "MainActivity";
	private static final String TEMP_FILENAME = "test.txt";

	private Context cntx = null;

	private String ip, user, pass, port;
	private ListView contentList;
	private List<String> realcontents = new ArrayList<String>();
	Map<String,String> userMap = new HashMap<String,String>();
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

		if(port.isEmpty()) port = "21";
		
		//finishActivity(0);
		
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
			//updateList();
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
		if(type.equals("dir")){
			new ChangeDir().execute(cont);
		} else if (type.equals("file")){
			new DownloadFile().execute(cont,  Environment.getExternalStorageDirectory().getPath());
		}
		
	}

	
	private static final int FILE_SELECT_CODE = 0;

	private void showFileChooser() {
	    Intent intent = new Intent(Intent.ACTION_GET_CONTENT); 
	    intent.setType("*/*"); 
	    intent.addCategory(Intent.CATEGORY_OPENABLE);

	    try {
	        startActivityForResult(
	                Intent.createChooser(intent, "Select a File to Upload"),
	                FILE_SELECT_CODE);
	    } catch (android.content.ActivityNotFoundException ex) {
	        // Potentially direct the user to the Market with a Dialog
	        Toast.makeText(this, "Please install a File Manager.", 
	                Toast.LENGTH_SHORT).show();
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
	    }
	    else if ("file".equalsIgnoreCase(uri.getScheme())) {
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
				//updateList();
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

	
	public void onBackPressed (){
		if(!workingDir.equals(orginalDir)){
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

				//getContent();
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
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
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
			Log.d(TAG, path);
			String name = args[1];
			Log.d(TAG, name);
			
			File f = new File(path);
			System.out.println("ph: " + f.getPath());	
			System.out.println("getAbsolutePath: " + f.getAbsolutePath());	
			System.out.println("findes den: " + f.exists());	
			System.out.println("read: " + f.canRead());
			System.out.println("write: " + f.canWrite());
			String abspath = f.getAbsolutePath();
			
			status = ftpclient.ftpUpload(abspath, abspath, workingDir, cntx);
			
			//ftpclient.mFTPClient.
			/*try {
			 FileInputStream srcFileStream = cntx.openFileInput(path);
				status = ftpclient.mFTPClient.storeFile(path, srcFileStream);
				srcFileStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}catch (IOException e) {
				e.printStackTrace();
			}
			*/

			// change working directory to the destination directory
			// if (ftpChangeDirectory(desDirectory)) {
			 
			// }
			
			
			
			if (status == true) {
				Log.d(TAG, "Upload success");
			} else {
				Log.e(TAG, "Upload failed");

			}
			return "";
		}

		protected void onProgressUpdate(Integer... progress) {
		}

		protected void onPostExecute(String result) {
			//updateList();
		}
	}
	
	public class DownloadFile extends AsyncTask<String, Integer, String> {

		protected void onPreExecute() {

		}

		protected String doInBackground(String... args) {
			boolean status = false;
			String name = args[0];
			String path = args[1];
			Log.d(TAG, name);
			
			System.out.println(name);
			//status = ftpclient.ftpDownload(name, path+"/");
			String s = workingDir.equals("/")? "" : workingDir;
			System.out.println(s);
			try {
				
				InputStream is =  ftpclient.mFTPClient.retrieveFileStream(name);
				if(is != null){
				System.out.println("is: " + is);
				String isst = convertStreamToString(is);
				System.out.println("isst: " + isst);
				is.close();
				Log.d(TAG, "content: " + isst);
				ftpclient.ftpConnect(ip, user, pass, Integer.parseInt(port));
				FileOutputStream fous = openFileOutput(name, Context.MODE_WORLD_WRITEABLE);
				fous.write(isst.getBytes());
				fous.close();
				} else {
					Log.d(TAG, "inputstream is null");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (status == true) {
				Log.d(TAG, "Download success");
			} else {
				Log.e(TAG, "Download failed");

			}
			return "";
		}

		protected void onProgressUpdate(Integer... progress) {
		}

		protected void onPostExecute(String result) {
			//updateList();
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
}
