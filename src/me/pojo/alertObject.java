package me.pojo;

import java.io.IOException;
import java.net.SocketException;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import telnet.telnetInstance;
import me.trap.trapSender;

public class alertObject implements Parcelable {

	String OID, address, command, thresholdString1, thresholdstring2;
	boolean alerted = false;
	trapSender ts = new trapSender();

	public alertObject(String n, String a, String t, String c) {

		OID = n;
		address = a;
		thresholdString1 = t;
		command = c;

	}

	public alertObject(String n, String a, String t1, String t2, String c) {

		OID = n;
		address = a;
		thresholdString1 = t1;
		thresholdstring2 = t2;
		command = c;

	}

	public String getOID() {
		return OID;
	}

	public void setOID(String name) {
		this.OID = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getThresholdString() {
		return thresholdString1;
	}

	public void setThresholdString(String thresholdString) {
		this.thresholdString1 = thresholdString;
	}

	public boolean isAlerted() {
		return alerted;
	}

	public void setAlerted(boolean alerted) {
		this.alerted = alerted;
	}

	public void alerted() {

		try {
			ts.sendTrap_Version2(OID);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (command != null) {
			try {
				if (command.equals("taskkill")) {
					Log.e("here", command);
					Log.e("here", address);
					telnetInstance.telnetKillProcess(address, "test", "pass");
				} else {
					Log.e("here", "else");
					telnetInstance.telnetMethod(address, "test", "pass",command);
				}
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {

		dest.writeString(OID);
		dest.writeString(address);
		dest.writeString(command);
		dest.writeString(thresholdString1);
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		@Override
		public alertObject createFromParcel(Parcel in) {
			return new alertObject(in);
		}

		@Override
		public alertObject[] newArray(int size) {
			return new alertObject[size];
		}
	};

	public alertObject(Parcel in) {

		OID = in.readString();
		address = in.readString();
		command = in.readString();
		thresholdString1 = in.readString();
	}

}
