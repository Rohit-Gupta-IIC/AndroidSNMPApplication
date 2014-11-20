package me.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

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

import me.adapter.snmpAdapterView;
import me.pojo.snmpObject;
import xml.XMLParser;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;

// displays a list of devices in the xml file we have selected. We also have the option of adding devices to the xml file
public class DeviceListActivity extends ListActivity implements OnClickListener {
	/** Called when the activity is first created. */

	public final Context context = this;
	public static ArrayList<snmpObject> parsedObjects = new ArrayList<snmpObject>();
	public static snmpAdapterView adapter = null;
	
	public int selected;
	public ListView listView;
	public Bundle extras;

	XMLParser parser = null;
	Handler handler;

	FileInputStream aFile = null;
	File thefile = null;

	Button add = null;

	boolean done = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.devicelist);

		Intent i = getIntent();
		extras = i.getExtras();
		add = (Button) findViewById(R.id.addDeviceXML);

		//set-up listview
		listView = getListView();
		registerForContextMenu(listView);
		listView.setCacheColorHint(Color.WHITE);
		
		handler = new Handler();
		
		// get relevant xml file from sd card, retrieve the name of the xml file
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Log.e("Access", "true");
//			File get = new File(Environment.getExternalStorageDirectory()
//					+ "/Android/data/snnmpApp/" + extras.getString("fileSelected"));
			File get = new File(Environment.getExternalStorageDirectory()
					+ "/snmpApp/" + extras.getString("fileSelected"));

			try {
				aFile = new FileInputStream(get);
				Log.e("Caught", "");

				parser = new XMLParser();
				parser.parseDocument(aFile);
				parsedObjects = parser.myObj;

			} catch (FileNotFoundException e) {
				Log.e("Reading error", e.getMessage());
			}
		} else {
			Log.e("Error", "Unable to retrieve");
		}

		// go to the list of values belonging to that device with the Device's Name, IP, polling period and readings
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int pos,
					long id) {

				snmpObject so = parsedObjects.get(pos);

				Intent intent = new Intent(context, DeviceValuesActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("position", pos);
				bundle.putString("fileSelected", extras.getString("fileSelected"));
				bundle.putString("Name", so.getName());
				Log.e("1", so.getName() + "");
				bundle.putString("Address", so.getAddress());
				bundle.putInt("poll", so.getPoll());

				String[] d = so.getDesc().toArray(
						new String[so.getDesc().size()]);
				bundle.putStringArray("desc", d);

				String[] v = so.getVs().toArray(new String[so.getVs().size()]);
				bundle.putStringArray("values", v);

				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		//set onLongItem click event
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView parent, View view,
					int position, long id) {
				Log.e("here", "here");
				selected = position;
				return false;

			}
		});

		//adapter for displaying the listview
		adapter = new snmpAdapterView(this, parsedObjects);
		setListAdapter(adapter);

		// go to activity for adding a device to an xml file
		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(context, xmlAddDeviceActivity.class);
//				i.putExtra("path", (Environment.getExternalStorageDirectory()
//						+ "/Android/data/snnmpApp/" + extras
//						.getString("fileSelected")).toString());
				i.putExtra("path", (Environment.getExternalStorageDirectory()
						+ "/snmpApp/" + extras
						.getString("fileSelected")).toString());
				startActivity(i);
				finish();
			}

		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, v.getId(), 0, "Delete?");
	}

	@Override
	//method for deleting a device from the xml file and from listview
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getTitle() == "Delete?") {
//			File file = new File(Environment.getExternalStorageDirectory()
//					.toString()
//					+ "/Android/data/snnmpApp/"
//					+ extras.getString("fileSelected"));
			File file = new File(Environment.getExternalStorageDirectory()
			.toString()
			+ "/snmpApp/"
			+ extras.getString("fileSelected"));
			Log.e("Selected file", file.getAbsolutePath());
			deleteDevice(file.getAbsolutePath());
//			Log.e("sgvb", file.getAbsolutePath());
			parsedObjects.remove(selected);
			listView.setAdapter(getListAdapter());
		} else {
			return false;
		}
		return true;
	}

	//method used for removing the device object from the xml file
	public void deleteDevice(String file) {

		File docFile = new File(file);

		Document doc = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(docFile);
		} catch (java.io.IOException e) {
			Log.e("", "Can't find the file");
		} catch (Exception e) {
			Log.e("", "Problem parsing the file.");
		}

		Log.e("delete", "here");
		Element element = (Element) doc.getElementsByTagName("name").item(selected);
		Element parent = (Element) element.getParentNode();
		parent.getParentNode().removeChild(parent);

		Transformer xformer;
		try {
			xformer = TransformerFactory.newInstance().newTransformer();
//			xformer.transform(new DOMSource(doc), new StreamResult(new File(
//					Environment.getExternalStorageDirectory()
//					+ "/Android/data/snnmpApp/"+extras.getString("fileSelected"))));
			xformer.transform(new DOMSource(doc), new StreamResult(new File(
					Environment.getExternalStorageDirectory()
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
	    //AppConsts.ref_currentActivity = this;

	}
}