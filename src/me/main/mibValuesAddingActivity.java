package me.main;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import me.adapter.mibHashMapAdapter;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class mibValuesAddingActivity extends ListActivity {

	public mibHashMapAdapter adapter = null;
	File file = null;

	String name = null;
	Context context = this;
	Bundle bundle;
	ListView lv = null;
	Button add, change = null;
	EditText ipAddress = null;
	HashMap<String, String> hash, OIDSadded = null;
	NodeList nodes = null;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.devicevalues);
		lv = getListView();

		bundle = getIntent().getExtras();
		file = new File(Environment.getExternalStorageDirectory().toString()
				+ "/snmpApp/" + bundle.getString("fileSelected"));
		name = bundle.getString("Name");

		hash = (HashMap<String, String>) bundle.getSerializable("hashmap");

		add = (Button) findViewById(R.id.add);

		change = (Button) findViewById(R.id.changeButton);
		ipAddress = (EditText) findViewById(R.id.ipAddress);
		
		adapter = new mibHashMapAdapter(context, hash);
		lv.setAdapter(adapter);
		
		//Remove views carried over from deviceValuesActivity
		((ViewManager)change.getParent()).removeView(change);
		((ViewManager)ipAddress.getParent()).removeView(ipAddress);

		add.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("rawtypes")
			@Override
			public void onClick(View v) {
				OIDSadded = adapter.getChecked();
				Log.e("dfgffdbbc", OIDSadded.size() + "");
				Document doc = null;

				Iterator it = OIDSadded.entrySet().iterator();
				while (it.hasNext()) {
					HashMap.Entry pairs = (HashMap.Entry) it.next();
					String desc = pairs.getKey().toString();
					String OID = pairs.getValue().toString();

					try {

						// add in text entry function
						DocumentBuilderFactory domFactory = DocumentBuilderFactory
								.newInstance();
						domFactory.setIgnoringComments(true);
						DocumentBuilder builder;
						try {
							builder = domFactory.newDocumentBuilder();
							doc = builder.parse(file);
						} catch (ParserConfigurationException e) {
							e.printStackTrace();
						} catch (SAXException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}

						XPath xpath = XPathFactory.newInstance().newXPath();

						nodes = (NodeList) xpath.evaluate("//device[name='"
								+ name + "']/readings", doc,
								XPathConstants.NODESET);
						
						Log.e("line", "//device[name='"+ name + "']/readings");
						
//						nodes = (NodeList) xpath.evaluate("//device[name='Device1']/readings", doc,
//						        XPathConstants.NODESET);

						// Log.e("here", desc);
						// Log.e("here", OID);
						Text a = doc.createTextNode(desc);
						Element p = doc.createElement("description");
						p.appendChild(a);

						Text a2 = doc.createTextNode(OID);
						Element p2 = doc.createElement("value");
						p2.appendChild(a2);

						Element reading = doc.createElement("reading");
						Log.e("", "fgttgfcnhtr");
						reading.appendChild(p);
						reading.appendChild(p2);

						nodes.item(0).appendChild(reading);

					} catch (XPathExpressionException e1) {
						e1.printStackTrace();
					}

					Transformer xformer;
					try {
						xformer = TransformerFactory.newInstance()
								.newTransformer();
						xformer.transform(new DOMSource(doc), new StreamResult(
								file));
					} catch (TransformerConfigurationException e) {
						e.printStackTrace();
					} catch (TransformerFactoryConfigurationError e) {
						e.printStackTrace();
					} catch (TransformerException e) {
						e.printStackTrace();
					}

					Log.e("text added", "yes");
					it.remove();
				}
				Toast.makeText(getApplicationContext(), "Values added", Toast.LENGTH_SHORT).show();
				
				//go back to front page
				Intent i = new Intent(context, fileSelectionSplashActivity.class);
				i.setAction(Intent.ACTION_MAIN);
				i.addCategory(Intent.CATEGORY_LAUNCHER);
				startActivity(i);
			}

		});
	}

	
	@Override
	protected void onResume() {
	    super.onResume();
	    //AppConsts.ref_currentActivity = this;

	}
}
