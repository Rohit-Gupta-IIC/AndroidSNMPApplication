package me.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//create an xml file
public class xmlCreationActivity extends Activity {

	int time = 3000;
	File xmlfile;
	FileWriter xmlwriter;
	Context con = this;
	EditText filename, name, address;
	Button create;
	List<String> OIDs = new ArrayList<String>();
	List<String> valueName = new ArrayList<String>();
	Context context = this;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xmlcreate);

		filename = (EditText) findViewById(R.id.fileName);
		name = (EditText) findViewById(R.id.Name);
		address = (EditText) findViewById(R.id.Address);

		create = (Button) findViewById(R.id.Create);

		create.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				File root = Environment.getExternalStorageDirectory();
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {

					if (filename.getText().toString().equalsIgnoreCase("")) {
						Toast toast = Toast.makeText(context,
								"File name must be defined", 5000);
						toast.show();
					} else {
						try {
							xmlfile = new File(Environment
									.getExternalStorageDirectory()
									+ "/snmpApp/"
									+ filename.getText().toString() + ".xml");
							Log.e("des", root.getAbsolutePath());
							Log.e("filename", filename.getText().toString() + ".xml");
							xmlwriter = new FileWriter(xmlfile);
						} catch (IOException e) {
							Log.e("Error", "Unable to create file");
							e.printStackTrace();
						}
						BufferedWriter out = new BufferedWriter(xmlwriter);

						if (name.getText().toString().equalsIgnoreCase("")) {
							
							Toast toast = Toast.makeText(context,
									"XML file created", 5000);
							toast.show();
							
							try {
								out.write("<list>" + "\n" + "</list>");

							} catch (IOException e) {
								Log.e("ERROR", "error");
								e.printStackTrace();
							}
						} else {

							try {
								Toast toast = Toast.makeText(context,
										"XML File created with device", 5000);
								toast.show();
								
								out.write("<list>" + "\n" + "<device>" + "\n"
										+ "<name>" + name.getText().toString()
										+ "</name>" + "\n" + "<address>"
										+ address.getText().toString()
										+ "</address>" + "\n"
										+ "<polling>5000</polling>" + "\n"
										+ "<readings>" + "\n" + "</readings>"
										+ "\n" + "</device>" + "\n" + "</list>");

							} catch (IOException e) {
								Log.e("ERROR", "error");
								e.printStackTrace();
							}
						}

						name.setText("");
						address.setText("");

						try {
							out.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				} else {
					Toast toast = Toast.makeText(context,
							"SD card required to save data", 5000);
					toast.show();
				}
				
				Intent i = new Intent(con, fileSelectionSplashActivity.class);
				startActivity(i);
				finish();
			}
			
			
		});
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
