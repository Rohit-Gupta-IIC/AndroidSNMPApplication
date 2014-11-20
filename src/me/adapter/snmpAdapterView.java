package me.adapter;

import java.util.List;

import me.main.R;
import me.pojo.snmpObject;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class snmpAdapterView extends BaseAdapter {

	private final Context context;
	private List<snmpObject> devices;
	TextView deviceName = null;

	public snmpAdapterView(Context theContext, List<snmpObject> theListDevice) {
		this.context = theContext;
		this.devices = theListDevice;
	}

	@Override
	public int getCount() {
		return devices.size();
	}

	@Override
	public snmpObject getItem(int position) {
		return devices.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		snmpObject entry = devices.get(position);

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.row, null);
		}

		deviceName = (TextView) convertView.findViewById(R.id.deviceName);
		

		if (entry.getName() != null) {
			deviceName.setText(entry.getName());
		}
		else {
			deviceName.setText("null");
		}

		return convertView;

	}
	
	public void setList(List<snmpObject> theListDevice) {
		this.devices = theListDevice;
	}
}
