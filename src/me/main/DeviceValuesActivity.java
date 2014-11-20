package me.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import snmp.snmpMethods;


import me.adapter.snmpDeviceAdapterView;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

//this class will list all the values from the device, these values shall update on polling periods
public class DeviceValuesActivity extends ListActivity {

	public Handler handler;
	public snmpDeviceAdapterView adapter;
	public Context context = this;
	static ResponseEvent response;
	
	public String[] desc, oids = new String[]{};
	public int position;
	public String address,name,file;
	public ListView listView;
	public boolean init,firstParse = false;
	public boolean isRunning = true; 
	
	public EditText ipAddress;
	public Button change;
	
	Snmp snmp;
	Bundle extras;
	Bundle bundle;
	int selectedPosition;
	int positionDeleted;	
	
	static String str;
	
	public Button add = null;
	List<String> descriptions,valuesRetrieved = new ArrayList<String>();
	
	Timer tim = new Timer();
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.devicevalues);
		
		listView = getListView();
		registerForContextMenu(listView);
		add = (Button) findViewById(R.id.add);
		change = (Button) findViewById(R.id.changeButton);
		ipAddress = (EditText) findViewById(R.id.ipAddress);
		
		bundle = new Bundle();
		Intent in = getIntent();
		extras = in.getExtras();

		position = extras.getInt("position");
		address = extras.getString("Address");
		desc = extras.getStringArray("desc");
		oids = extras.getStringArray("values");
		name = extras.getString("Name");
		file = extras.getString("fileSelected");
		
		ipAddress.setText(address);
		
		setProgressBarIndeterminateVisibility(true);
		
		//populate listview
		for (int j = 0; j < oids.length; j++) {
			try {
				valuesRetrieved.add(snmpMethods.snmpGet(address, "public", oids[j]));
				Log.e("value", valuesRetrieved.get(j));
			} catch (NullPointerException e) {
				valuesRetrieved.add("val missing");
			}
		}
		setProgressBarIndeterminateVisibility(false);
		
		Log.e("value", valuesRetrieved.size()+"");
		
		adapter = new snmpDeviceAdapterView(context, desc, valuesRetrieved);
		listView.setAdapter(adapter);
		
		valuesRetrieved = new ArrayList<String>();
		
		add.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				
				Intent i = new Intent(context, mibActivity.class);
				bundle.putString("Name", name);
				Log.e("2", name+"");
				bundle.putString("fileSelected", file);
				i.putExtras(bundle);
				startActivity(i);
			}
			
		});

		//schedule when to update the listview
		tim.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if(isRunning == true){
				valuesRetrieved.clear();
				
				for (int i = 0; i < oids.length; i++) {
					try {

						String s = snmpMethods.snmpGet(address, "public", oids[i]);
						Log.e("Retrieved value",s);
						valuesRetrieved.add(s);
						

					} catch (NullPointerException e) {
						valuesRetrieved.add("val missing");
					}
				}
				
				
				
				runOnUiThread(new Runnable() {
				    @Override
					public void run() {
				    	adapter = new snmpDeviceAdapterView(context, desc, valuesRetrieved);
				    	listView.setAdapter(adapter);
				    	adapter.notifyDataSetChanged();
				    }
				});
				
			}
			}
		}, 0, 5000);
		
		//selecting a value shall bring us to a page with the value and its description
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int pos,
					long id) {
				
				Intent i = new Intent(context,ValueSettingsActivity.class);
				
				bundle.putString("Name", name);
				bundle.putString("Address", address);
				bundle.putString("valueDesc", desc[pos]);
				bundle.putString("valueOID", oids[pos]);
				
				bundle.putString("currValue", valuesRetrieved.get(pos));
				
				i.putExtras(bundle);
				startActivity(i);
			}
		});
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView parent, View view,
					int position, long id) {
				positionDeleted = position;
				return false;

			}
		});

		change.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				File file = new File(Environment.getExternalStorageDirectory()
				.toString()
				+ "/snmpApp/"
				+ extras.getString("fileSelected"));
				changeIP(file,ipAddress.getText().toString());
				Intent intent = new Intent(context, fileSelectionSplashActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
				startActivity(intent);
				finish();
				
			}
			
		});
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, v.getId(), 0, "Delete?");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getTitle() == "Delete?") {
			File file = new File(Environment.getExternalStorageDirectory()
					.toString()
					+ "/snmpApp/"
					+ extras.getString("fileSelected"));
			deleteValue(file);
			valuesRetrieved.remove(positionDeleted);
			listView.setAdapter(getListAdapter());
			Intent intent = new Intent(context, fileSelectionSplashActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
					| Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
			startActivity(intent);
			finish();
		} else {
			return false;
		}
		return true;
	}
	

	
	public void deleteValue(File file) {
		
		Document doc = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(file);
		} catch (java.io.IOException e) {
			Log.e("error","Can't find the file");
		} catch (Exception e) {
			Log.e("error","Problem parsing the file.");
		}	
		  Element element = (Element)doc.getElementsByTagName("reading").item(positionDeleted);  

          element.getParentNode().removeChild(element);
          
        Transformer xformer;
		try {
			xformer = TransformerFactory.newInstance().newTransformer();
			xformer.transform(new DOMSource(doc), new StreamResult(new File(Environment.getExternalStorageDirectory()
					+ "/snmpApp/"+extras.getString("fileSelected"))));
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	    
		
		
	}
	
	public void changeIP(File file, String str){
		Document doc = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(file);
		} catch (java.io.IOException e) {
			Log.e("error","Can't find the file");
		} catch (Exception e) {
			Log.e("error","Problem parsing the file.");
		}	
		
		Node value = doc.getElementsByTagName("address").item(position);
		Log.e("", value.getTextContent());
		Log.e("", str);
		value.setTextContent(str);

		Transformer xformer;
		try {
			xformer = TransformerFactory.newInstance().newTransformer();
			xformer.transform(new DOMSource(doc), new StreamResult(new File(Environment.getExternalStorageDirectory()
					+ "/snmpApp/"+extras.getString("fileSelected"))));
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();

		isRunning = false;
	}
}
