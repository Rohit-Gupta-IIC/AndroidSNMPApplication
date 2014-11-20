package me.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

//add device to xml file
public class xmlAddDeviceActivity extends Activity {

	Button add;
	EditText name, address;
	TextView t1, t2;

	int linenum = 0;
	
	File f;
	Intent i;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xmlcreatedevice);

		add = (Button) findViewById(R.id.add);
		name = (EditText) findViewById(R.id.Name);
		address = (EditText) findViewById(R.id.Address);
		
		i = getIntent();
		Bundle extras = i.getExtras();
		
		f = new File(i.getStringExtra("path"));

		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String n = name.getText().toString();
				String a = address.getText().toString();
				
				String lineToAdd = "<device>"+"\n"+
				"<name>"+n+"</name>"+"\n"+
				"<address>"+a+"</address>"+"\n"+
				"<polling>5000</polling>"+"\n"+
				"<readings>"+"\n"+
				"</readings>"+"\n"+
				"</device>";
				
				String line;
				Pattern p = Pattern.compile("</list>",Pattern.CASE_INSENSITIVE);
				Log.e("reg", p.pattern());

				Log.e("", "pattern");
				BufferedReader bf = null;
				try {
					bf = new BufferedReader(new FileReader(i.getStringExtra("path")));
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
				try {
					while ((line = bf.readLine()) != null) {
						
						linenum++;
						Matcher m = p.matcher(line);

						// indicate all matches on the line
						while (m.find()) {
							Log.e("found", "found");
							insertStringInFile(f, linenum, lineToAdd);
						}
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		});
	}

	public void insertStringInFile(File inFile, int lineno,
			String lineToBeInserted) throws Exception {
		
		Log.e("made", "it");
		// temp file
		File outFile = new File(Environment.getExternalStorageDirectory()
				+ "/Android/data/snnmpApp/"+"$$$$$$$$.tmp");

		// input
		FileInputStream fis = new FileInputStream(inFile);
		BufferedReader in = new BufferedReader(new InputStreamReader(fis));

		// output
		FileOutputStream fos = new FileOutputStream(outFile);
		PrintWriter out = new PrintWriter(fos);

		String thisLine = "";
		int i = 1;
		while ((thisLine = in.readLine()) != null) {
			if (i == lineno)
				out.println(lineToBeInserted);
			out.println(thisLine);
			i++;
		}
		out.flush();
		out.close();
		in.close();

		inFile.delete();
		outFile.renameTo(inFile);
		
		Log.e("done", "it");
	}
	
	@Override
	public void onBackPressed() {
		startActivity(new Intent(this, fileSelectionSplashActivity.class));
		finish();
	 }
	
	@Override
	protected void onResume() {
	    super.onResume();
	    //AppConsts.ref_currentActivity = this;

	}
}
