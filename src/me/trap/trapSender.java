package me.trap;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import android.util.Log;

public class trapSender {

	public static final String community = "public";


	public static String Oid = "";

	// IP of Local Host
	public static final String ipAddress = trapReceiver.getLocalIpAddress();

	// Ideally Port 162 should be used to send receive Trap, any other available
	// Port can be used
	public static final int port = 5228;

	public void sendTrap_Version2(String oid) {
		try {
			Oid = oid;
			// Create Transport Mapping
			TransportMapping transport = new DefaultUdpTransportMapping();
			transport.listen();

			// Create Target
			CommunityTarget cTarget = new CommunityTarget();
			cTarget.setCommunity(new OctetString(community));
			cTarget.setVersion(SnmpConstants.version2c);
			cTarget.setAddress(new UdpAddress(ipAddress + "/" + port));
			cTarget.setRetries(2);
			cTarget.setTimeout(5000);

			// Create PDU for V2
			PDU pdu = new PDU();

			pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, new OID(Oid)));
			pdu.add(new VariableBinding(SnmpConstants.snmpTrapAddress,
					new IpAddress(ipAddress)));

			pdu.add(new VariableBinding(new OID(Oid), new OctetString("Major")));
			pdu.setType(PDU.NOTIFICATION);

			// Send the PDU
			Snmp snmp = new Snmp(transport);
			Log.i("","Sending V2 Trap...");
			snmp.send(pdu, cTarget);
			snmp.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}