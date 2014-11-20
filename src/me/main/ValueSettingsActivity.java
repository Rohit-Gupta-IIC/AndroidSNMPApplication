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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import me.pojo.alertObject;
import me.service.monitoringService;
import me.service.monitoringService.LocalBinder;



import snmp.snmpMethods;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

//here we can set alerts for a value or delete the value
public class ValueSettingsActivity extends Activity {

	Context context = this;
	String name, address, description, valueOID, currValue;
	TextView desc, val;
	Button button;
	EditText value, action;
	int valuePosition;
	Timer tim;
	boolean start = false;

	GraphViewData[] values;
	private int graphPOS = 1;
	public IBinder service;
	
	public List<alertObject> monitoredValues = new ArrayList<alertObject>();
	monitoringService mService;
	
	ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.e("INFO", "Service bound ");
			LocalBinder mLocalBinder = (LocalBinder)service;
			   mService = mLocalBinder.getServiceInstance();
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
		setContentView(R.layout.valueselected);

		Intent in = getIntent();
		Bundle extras = in.getExtras();

		name = extras.getString("Name");
		address = extras.getString("Address");
		description = extras.getString("valueDesc");
		valueOID = extras.getString("valueOID");
		currValue = extras.getString("currValue");

		button = (Button) findViewById(R.id.button);
		value = (EditText) findViewById(R.id.editText1);
		action = (EditText) findViewById(R.id.editText2);
		
		

		final LinearLayout layout = (LinearLayout) findViewById(R.id.graph);

		float fValue = 0;

		final GraphViewSeries exampleSeries = new GraphViewSeries(values);

		final LineGraphView graphView = new LineGraphView(this // context
				, "Data recieved" // heading
		);
		graphView.setScrollable(true);
		graphView.addSeries(exampleSeries);
		layout.addView(graphView);

		tim = new Timer();

		try {
			fValue = Float.parseFloat(currValue);
			start = true;
		} catch (NumberFormatException e) {
			layout.removeView(graphView);
			start = false;
		}

		values = new GraphViewData[] { 
				new GraphViewData(graphPOS, fValue)
		// , new GraphViewData(2, 1.5d)
		// , new GraphViewData(3, 2.5d)
		// , new GraphViewData(4, 1.0d)
		// , new GraphViewData(5, 20.0d)
		};
		
		Intent i2 = new Intent(getApplication(), monitoringService.class);
		getApplication().bindService(i2, conn, Context.BIND_AUTO_CREATE);

		button.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("static-access")
			@Override
			public void onClick(View v) {

				try {
					alertObject aoi = new alertObject(valueOID, address, value.getText().toString(), action.getText().toString());
					Toast to = Toast.makeText(getApplicationContext(),
							"Alert added", Toast.LENGTH_SHORT);
					to.show();
					 
					mService.addNewAlert(aoi);
					//addToFile(aoi);
					
					Intent i2 = new Intent(getApplication(), monitoringService.class);
					getApplication().bindService(i2, conn, Context.BIND_AUTO_CREATE);
					
					Log.e("", "alert added to watch");
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}

		});
//
		if (start == true) {
			tim.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							graphPOS++;
							//Log.e("address", address);
							String updatedValue = snmpMethods.snmpGet(address, "public",
									valueOID);
							
							updatedValue = updatedValue.trim();
							int toBeConverted = Integer.parseInt(updatedValue);
							float floatVer = toBeConverted;
							//Log.e("float value", "" + floatVer);
							GraphViewData g = new GraphViewData(graphPOS,
									floatVer);
							appendData(g);
							exampleSeries.resetData(values);
						}
					});

				}
			}, 0, 5000);
		}
	}

	public void appendData(GraphViewData thisValue) {
		GraphViewData[] newValues = new GraphViewData[values.length + 1];

		System.arraycopy(values, 0, newValues, 0, values.length);
		newValues[values.length] = thisValue;
		values = newValues;

	}
	
	public void addToFile(alertObject a){
		Document doc = null;
		File file = new File(Environment.getExternalStorageDirectory()
				.toString() + "/snmpApp/store/store.xml");

		Log.e("",file.getAbsolutePath());
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(file);
		} catch (java.io.IOException e) {
			System.out.println("Can't find the file");
		} catch (Exception e) {
			System.out.print("Problem parsing the file.");
		}

		Element store = doc.getDocumentElement();
		
		Element alert = doc.createElement("alert");
		
		Element OID = doc.createElement("oid");
		Text t = doc.createTextNode(a.getOID()); 
		OID.appendChild(t);
		
		Element address = doc.createElement("address");
		t = doc.createTextNode(a.getAddress()); 
		address.appendChild(t);
		
		Element value = doc.createElement("value");
		t = doc.createTextNode(a.getThresholdString()); 
		value.appendChild(t);
		
		Element action = doc.createElement("action");
		t = doc.createTextNode(a.getCommand()); 
		action.appendChild(t);
		
		alert.appendChild(OID);
		alert.appendChild(address);
		alert.appendChild(value);
		alert.appendChild(action);
		store.appendChild(alert); 
		 
		Transformer xformer;
		try {
			xformer = TransformerFactory.newInstance().newTransformer();
			xformer.transform(new DOMSource(doc), new StreamResult(new File(Environment.getExternalStorageDirectory()
					+ "/snmpApp/store/store.xml")));
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	    
	}
	
	public int getNumGraphValues(){
		
		return values.length;
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	}

}
