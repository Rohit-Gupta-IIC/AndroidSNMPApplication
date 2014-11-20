package xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import me.pojo.alertObject;
import me.pojo.snmpObject;

import org.xml.sax.SAXException;

import android.net.ParseException;


public class XMLParser {

	public ArrayList<snmpObject> myObj = new ArrayList<snmpObject>();
	public ArrayList<alertObject> myAlerts = new ArrayList<alertObject>();
	
	public void parseDocument(InputStream b) {
		
		// get a factory
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {

			// get a new instance of parser
			SAXParser sp = spf.newSAXParser();

			// parse the file and also register this class for call backs
			xmlHandler handler = new xmlHandler();
			//positionXmlHandler posHandler = new positionXmlHandler();
			sp.parse(b, handler);
			myObj= handler.getList();


		} catch (SAXException se) {
			se.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}
	
	public void parseStoredAlertsDocument(InputStream b) {
		
		// get a factory
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {

			// get a new instance of parser
			SAXParser sp = spf.newSAXParser();

			// parse the file and also register this class for call backs
			alertXMLHandler handler = new alertXMLHandler();
			//positionXmlHandler posHandler = new positionXmlHandler();
			sp.parse(b, handler);
			myAlerts = handler.getList();


		} catch (SAXException se) {
			se.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		} catch (ParseException ie) {
			ie.printStackTrace();
		}
	}

}
