package me.pojo;

import java.util.ArrayList;
import java.util.List;

//POJO for SNMP object
public class snmpObject /*implements Runnable*/ {

	String address;
	String name;
	
	// period before next retrieval of values
	int poll = 0;

	// Lists for values and descriptions
	public List<String> values = new ArrayList<String>();
	public List<String> parsedValues = new ArrayList<String>();
	public List<String> OIDs = new ArrayList<String>();
	public List<String> descriptions = new ArrayList<String>();

	boolean done = false;

	// SNMP community for reading OIDs
	public static final String READ_COMMUNITY_PUBLIC = "public";
	public static final String READ_COMMUNITY_PRIVATE = "private";
	
	
	public snmpObject(String ip, String n, int p, List<String> d, List<String> v) {
		name = n; // refer to device class
		address = ip;
		poll = p;
		values = v;
		descriptions = d;
	}
	
	public snmpObject(String n) {
		name = n; // refer to device class
	}

	//getters and setters
	public int getPoll() {
		return poll;
	}

	public void setPoll(int poll) {
		this.poll = poll;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getVs() {
		return values;
	}

	public void setVs(List<String> vs) {
		this.values = vs;
	}

	public List<String> getDesc() {
		return descriptions;
	}

	public void setDesc(List<String> desc) {
		this.descriptions = desc;
	}

}