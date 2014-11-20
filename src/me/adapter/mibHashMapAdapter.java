package me.adapter;

import java.util.HashMap;
import me.main.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class mibHashMapAdapter extends BaseAdapter {
	
	private final Context context;
    private HashMap<String, String> mData = new HashMap<String, String>();
    private HashMap<String, String> checkedOIDs = new HashMap<String, String>();
    
    private String[] mKeys;
    
    TextView OIDname = null;
    TextView OIDval = null;
    
    public mibHashMapAdapter(Context theContext,HashMap<String, String> data){
    	this.context = theContext;
        mData  = data;
        mKeys = mData.keySet().toArray(new String[data.size()]);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(mKeys[position]);
    }
    
    @Override
    public long getItemId(int arg0) {
        return arg0;
    }


	@Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        final String key = mKeys[pos].trim();
        final String value = getItem(pos).toString().replaceAll(" ", "");

        if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.oids, null);
		}
        
        OIDname = (TextView) convertView.findViewById(R.id.OIDname);
        OIDval = (TextView) convertView.findViewById(R.id.OIDvalue);
        CheckBox cb = (CheckBox) convertView.findViewById(R.id.checkBox1);
        
        if (key != null) {
			OIDname.setText(key);
		}
		else {
			OIDname.setText("null");
		}
        
        if (value != null) {
			OIDval.setText(value);
		}
		else {
			OIDval.setText("value");
		}
        
        cb.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				
				if(isChecked)
				{
					checkedOIDs.put(key, value);
				}
				else
				{   
				    checkedOIDs.remove(key);               
				}
				
			}
        	
        	
        	
        });

        return convertView;
    }
	
	public HashMap<String,String> getChecked()
	{
		return checkedOIDs;
		
	}
}
