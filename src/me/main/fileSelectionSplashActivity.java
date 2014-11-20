package me.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import xml.XMLParser;

import me.pojo.alertObject;
import me.service.monitoringService;
import me.service.snmpService;
import me.service.monitoringService.LocalBinder;

import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

//here we select the xml file we read from as well as be able to create an xml file
public class fileSelectionSplashActivity extends ListActivity implements
		OnItemClickListener {
//
	ListView listView;
	Button xmlCreate, networkDiscovery;
	FileInputStream aFile;
	ArrayList<String> names = null;
	Context context = this;
	int selected;
	XMLParser parser = null;
	monitoringService ms = new monitoringService();
	List<alertObject> parsedAlertObjects = new ArrayList<alertObject>();
	
	ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.e("INFO", "Service bound ");
			LocalBinder mLocalBinder = (LocalBinder)service;
			   ms = mLocalBinder.getServiceInstance();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.e("INFO", "Service Unbound ");
			getApplication().unbindService(conn);
		}
		
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fileselectionxml);
		
		

		try {
			Intent i = new Intent(getApplication(), snmpService.class);
			Intent i2 = new Intent(getApplication(), monitoringService.class);
			startService(i);
			startService(i2);
			//getApplication().bindService(i, conn, Context.BIND_AUTO_CREATE);
			getApplication().bindService(i2, conn, Context.BIND_AUTO_CREATE);
			
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("", "failed to start");
		}
		
		File folder = new File(Environment.getExternalStorageDirectory()
				.toString() + "/snmpApp");
		File mibfolder = new File(Environment.getExternalStorageDirectory()
				.toString() + "/snmpApp/mib");
//		File alertfolder = new File(Environment.getExternalStorageDirectory()
//				.toString() + "/snmpApp/store");
		folder.mkdirs();
		mibfolder.mkdirs();
//		alertfolder.mkdirs();
	
//		try {
//			File file = new File(Environment.getExternalStorageDirectory()
//					.toString() + "/snmpApp/store/store.xml");
//			FileInputStream aFile = new FileInputStream(file);
//			
////			parser = new XMLParser();
////			parser.parseStoredAlertsDocument(aFile);
////			parsedAlertObjects = parser.myAlerts;
//			
////			for(int i = 0; i< parsedAlertObjects.size(); i++){
////				Log.e("",parsedAlertObjects.get(i).getAddress());
////				ms.addNewAlert(parsedAlertObjects.get(i));
////			}
//
//			//clearAlerts();
//			
//		} catch (FileNotFoundException e) {
//			Log.e("Reading error", e.getMessage());
//		}
//
		// Save the path as a string value
		String extStorageDirectory = folder.toString();

		xmlCreate = (Button) findViewById(R.id.addXML);
		networkDiscovery = (Button) findViewById(R.id.discover);

		// setup listview and setup onLongClick event
		listView = getListView();
		listView.setTextFilterEnabled(true);
		registerForContextMenu(listView);

		listFiles(extStorageDirectory);

		// when we click on an xml file in the listview, take us to the
		// DeviceListActivity and pass in the name of the xml file
		// and list all devices in that file
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int pos,
					long id) {

				Intent intent = new Intent(context, DeviceListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("fileSelected", names.get(pos));

				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		// Bring up an option to delete the xml file on LongClick
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(@SuppressWarnings("rawtypes") AdapterView parent, View view,
					int position, long id) {
				Log.e("here", "here");
				selected = position;
				return false;

			}
		});

		// Bring up a toast
		Toast to = Toast.makeText(getApplicationContext(),
				"Select file to read from.", Toast.LENGTH_SHORT);
		to.show();

		// Go to xmlCreateActivity when we want to create another xml file
		xmlCreate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(context, xmlCreationActivity.class);
				startActivity(i);
				finish();
			}

		});

		// //TO-DO: Go to network Discovery activity
		// networkDiscovery.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		//
		// Intent i = new Intent(context,networkMapActivity.class);
		// startActivity(i);
		// finish();
		// }
		//
		// });
	}

	// retrieve all xml files from the root directory of the phone's sd-card
	public void listFiles(String dir) {

		File directory = new File(dir);

		if (!directory.isDirectory()) {
			System.out.println("No directory provided");
			return;
		}

		// create a FilenameFilter and override its accept-method
		FilenameFilter filefilter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				// if the file extension is .xml return true, else false
				return name.endsWith(".xml");
			}
		};

		String[] name = directory.list(filefilter);

		names = new ArrayList<String>();
		for (int i = 0; i < name.length; i++) {

			names.add(name[i]);
			Log.e("", name[i]);
		}

		setListAdapter(new ArrayAdapter<String>(this, R.layout.filerow, names));

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

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
//			File file = new File(Environment.getExternalStorageDirectory()
//					.toString()
//					+ "/Android/data/snnmpApp/"
//					+ names.get(selected));
			File file = new File(Environment.getExternalStorageDirectory()
			.toString()
			+ "/snmpApp/"
			+ names.get(selected));
			Log.e("Deleted file", file.getAbsolutePath());
			names.remove(selected);
			file.delete();
			listView.setAdapter(getListAdapter());
		} else {
			return false;
		}
		return true;
	}

	public void clearAlerts(){
		
		Document doc = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(new File(Environment.getExternalStorageDirectory()
					.toString() + "/snmpApp/store/store.xml"));
			
		} catch (java.io.IOException e) {
			Log.e("error","Can't find the file");
		} catch (Exception e) {
			Log.e("error","Problem parsing the file.");
		}	
		
		Element store = doc.getDocumentElement();
		
		for(int i = 0; i<= store.getChildNodes().getLength(); i++){
			
			Node childNode = store.getChildNodes().item(i);
			store.removeChild(childNode);
		}
          
        Transformer xformer;
		try {
			xformer = TransformerFactory.newInstance().newTransformer();
			xformer.transform(new DOMSource(doc), new StreamResult(new File(Environment.getExternalStorageDirectory()
					.toString() + "/snmpApp/store/store.xml")));
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	    
	}
}
