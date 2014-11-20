package snmp;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import android.util.Log;

public class snmpMethods {

	static ResponseEvent response;
	static String str;
	
	//method to retrieve SNMP value over OID
		public static String snmpGet(String strAddress, String community,
				String strOID) {
			try {

				Snmp snmp;
				PDU pdu = new PDU(); // container for info grouped together as a
										// unit

				OctetString community1 = new OctetString(community);
				strAddress = strAddress + "/" + 161;
				Address targetaddress = new UdpAddress(strAddress);
				TransportMapping<?> transport = new DefaultUdpTransportMapping(); // transport
																					// route
																					// for
																					// UDP
																					// connection
				transport.listen();// listen for responses, not setting this makes
									// the class time-out instantly

				CommunityTarget comtarget = new CommunityTarget(); // define
																	// location of
																	// machine
				comtarget.setCommunity(community1);
				comtarget.setVersion(SnmpConstants.version1);
				comtarget.setAddress(targetaddress);
				comtarget.setRetries(1);
				comtarget.setTimeout(2000);

				try {
					pdu.add(new VariableBinding(new OID(strOID))); // look for OID
				} catch (NumberFormatException e) {
					return null;
				}
				pdu.setType(PDU.GET); // GET method
				snmp = new Snmp(transport); // new SNMP object

				response = snmp.get(pdu, comtarget);
				//Thread.sleep(500);
				
				if (response != null) {
					if (response.getResponse().getErrorStatusText()
							.equalsIgnoreCase("Success")) {

						PDU pduresponse = response.getResponse();
						str = pduresponse.getVariableBindings().firstElement()
								.toString(); // get variable from the response and
												// set it to a
												// string

						if (str.contains("=")) { // remove the string of numbers
													// before the actual info we
													// want

							int len = str.indexOf("=");

							try {
								str = str.substring(len + 1, str.length());
							} catch (StringIndexOutOfBoundsException e) {
							}

						}
					} else {
						Log.e("ERROR", "Timeout Occurred");
					}
				}
				snmp.close(); // close connection
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("ERROR", "Timeout Occurred");
			}

			return str;
		}
}
