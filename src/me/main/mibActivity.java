package me.main;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.percederberg.mibble.Mib;
import net.percederberg.mibble.MibLoaderException;

import me.main.R;
import mib.mibReading;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class mibActivity extends ListActivity{
	
	Context context = this;
	public ListView list;
	String name, file = null;
	String[] names = new String[] {};
	List<String> mibFiles = new ArrayList<String>();
	Bundle bundle;
	
	Mib mib = null;
	HashMap<String,String> hash = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mib);
		
		bundle = new Bundle();
		Intent in = getIntent();
		Bundle extras = in.getExtras();
		name = extras.getString("Name");
		file = extras.getString("fileSelected");
		Log.e("file", file+"");
		
		 File folder = new File(Environment.getExternalStorageDirectory().toString()+"/Android/data/snnmpApp/mib");
	     folder.mkdirs();
	     String extStorageDirectory = folder.toString();
	     
	     listFiles(extStorageDirectory);
	        
		
		 list = getListView();
		 list.setTextFilterEnabled(true);
		
		 list.setOnItemClickListener(new OnItemClickListener() {
	            @Override
				public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
	            	
	            	Log.e("file selected", names[pos]);
	            	File selectedMib = new File(Environment.getExternalStorageDirectory().toString()+"/snmpApp/mib/"+names[pos]);
	            	
	            	try {
						mib = mibReading.loadMib(selectedMib);
					} catch (MibLoaderException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
	            	
	            	hash = mibReading.extractOids(mib);
	            	
	            	
	            	Intent i = new Intent(context, mibValuesAddingActivity.class);
	        	    bundle.putString("Name", name);
	        	    bundle.putString("fileSelected", file);
	        		bundle.putSerializable("hashmap", hash);
	        		i.putExtras(bundle);
	        		startActivity(i);
	            }
	        });
		
		
	}
	
	public void listFiles(String dir) {

	    File directory = new File(dir);

	    if (!directory.isDirectory()) {
	      Log.e("Error","No directory provided");
	      return;
	    }

	    //create a FilenameFilter and override its accept-method
	    FilenameFilter filefilter = new FilenameFilter() {

	      @Override
		public boolean accept(File dir, String name) {
	        //if the file extension is .mib return true, else false
	        return name.endsWith("");
	      }
	    };

	    names = directory.list(filefilter);
	    for (String n : names) {
	    	Log.e("names", n);
	    }
	    setListAdapter(new ArrayAdapter<String>(this, R.layout.filerow, names));
	    
	  }

}
