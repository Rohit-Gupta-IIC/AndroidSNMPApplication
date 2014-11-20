package me.service;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import me.pojo.alertObject;
import snmp.snmpMethods;
import android.util.Log;

public class monitorTask extends TimerTask {

	List<alertObject> alerts = new ArrayList<alertObject>();

	public void performAction(){
		
		Log.e("im", "here " + alerts.size());
		if (alerts.size() > 0) {

			Log.e("im", "here2");
			for (int i = 0; i < alerts.size(); i++) {

				Log.e("im", "here3");
				alertObject curr = alerts.get(i);

				try {
					String s = snmpMethods.snmpGet(curr.getAddress(), "public",
							curr.getOID());
					s = s.trim();
					int currvalue = Integer.parseInt(s);
					Log.e("current", currvalue + "");

					if (Integer.parseInt(curr.getThresholdString()) <= currvalue
							&& curr.isAlerted() == false) {

						Log.e("", "alerted");
						curr.alerted();
						curr.setAlerted(true);
						alerts.remove(i);
					}
				} catch (Exception e) {
					Log.e("Value", "Not obtained");
				}
			}
		}

	}
	
	@Override
	public void run() {

		performAction();
	}

	public void addAlert(alertObject a) {
		Log.e("", "got");
		alerts.add(a);
	}
	
	public int getAlertSize(){
		
		return alerts.size();
	}

}
