package xml;

import java.util.ArrayList;
import java.util.List;
import me.pojo.snmpObject;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class xmlHandler extends DefaultHandler {
	
	String tempVal = "";
	snmpObject tempSnmp;
	ArrayList<snmpObject> myObj = new ArrayList<snmpObject>();
	List<String> val = new ArrayList<String>();
	List<String> desc = new ArrayList<String>();
	List<String> alertval = new ArrayList<String>();
	List<String> alertdesc = new ArrayList<String>();
	
	// Event Handlers
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// reset
		tempVal = "";
		if (qName.equalsIgnoreCase("device")) {
			// create a new instance
			tempSnmp = new snmpObject("", "", 0, desc, val);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		tempVal = new String(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// System.out.println("endElement"+qName+" value:"+tempVal);
		if (qName.equalsIgnoreCase("device")) {
			
			// add it to the list
			myObj.add(tempSnmp);
//			for(int i = 0; i< tempSnmp.getVs().size(); i++){
//			Log.e("NAME", tempSnmp.getVs().get(i));
//			}
			val = new ArrayList<String>();
			desc = new ArrayList<String>();
			
			tempSnmp = new snmpObject("", "", 0, desc, val);

		} else if (qName.equalsIgnoreCase("name")) {
			tempSnmp.setName(tempVal);
		} else if (qName.equalsIgnoreCase("address")) {
			tempSnmp.setAddress(tempVal);
		} else if (qName.equalsIgnoreCase("polling")) {
			tempSnmp.setPoll(Integer.parseInt(tempVal));
		} else if (qName.equalsIgnoreCase("description")) {
			desc.add(tempVal);
			tempSnmp.setDesc(desc);
		} else if (qName.equalsIgnoreCase("value")) {
			val.add(tempVal);
			tempSnmp.setVs(val);
		}
	}

	public ArrayList<snmpObject> getList() {
		return myObj;
	}

}
