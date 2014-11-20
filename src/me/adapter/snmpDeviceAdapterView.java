package me.adapter;

import java.util.List;

import me.main.R;

import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class snmpDeviceAdapterView extends BaseAdapter {
	
	static ResponseEvent response;
	Snmp snmp;
	public static String str;
	private final Context context;
	private String[] descriptions;
	private List<String> parsedValues;
	TextView desc, val = null;
	
	public snmpDeviceAdapterView(Context theContext, String[] d, List<String> v) {
		
		context = theContext;
		descriptions = d;
		parsedValues = v;
		//Log.e("desc",descriptions.size()+"");
		//Log.e("val",parsedValues.size()+"");

	}

	@Override
	public int getCount() {
		return descriptions.length;
	}

	@Override
	public Object getItem(int pos) {
		return parsedValues.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup arg2) throws IndexOutOfBoundsException {

		String valueDesc = "";
		String valueActual = "";
		
		try {
			valueDesc = descriptions[pos].toString();
			valueActual = parsedValues.get(pos);
		} catch (IndexOutOfBoundsException e) {
			valueActual = "Val missing";
		}
		
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.valuesrow, null);
		}
		
		desc = (TextView) convertView.findViewById(R.id.txt1);
		val = (TextView) convertView.findViewById(R.id.txt2);
		
		try {
			if (valueDesc.equals("")) {
				desc.setText("Retrieving value");
			} else {
				desc.setText(valueDesc);
			}
		} catch (NullPointerException e) {
			desc.setText("Error");
		}

		try {
			if (valueActual.equals("")) {
				val.setText("Retrieving value");
			} else {
				val.setText(valueActual);
			}
		} catch (NullPointerException e) {
			val.setText("Error");
		}
		return convertView;
	}
}
