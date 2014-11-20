package xml;

import java.util.ArrayList;
import me.pojo.alertObject;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class alertXMLHandler extends DefaultHandler {
	
	String tempVal = "";
	alertObject tempAlert;
	ArrayList<alertObject> myAlerts = new ArrayList<alertObject>();
	String OID, address, command, thresholdString;
	
	// Event Handlers
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// reset
		tempVal = "";
		if (qName.equalsIgnoreCase("alert")) {
			// create a new instance
			tempAlert = new alertObject("", "", "","");
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
		if (qName.equalsIgnoreCase("alert")) {
			
			// add it to the list
			myAlerts.add(tempAlert);
			
			tempAlert = new alertObject("", "", "", "");

		} else if (qName.equalsIgnoreCase("oid")) {
			tempAlert.setOID(tempVal);
		} else if (qName.equalsIgnoreCase("address")) {
			tempAlert.setAddress(tempVal);
		} else if (qName.equalsIgnoreCase("value")) {
			tempAlert.setThresholdString(tempVal);
		} else if (qName.equalsIgnoreCase("action")) {
			tempAlert.setCommand(tempVal);
		} 
	}

	public ArrayList<alertObject> getList() {
		return myAlerts;
	}

}
