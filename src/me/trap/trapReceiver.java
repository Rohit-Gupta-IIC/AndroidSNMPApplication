package me.trap;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import me.main.R;
import me.main.fileSelectionSplashActivity;

import org.apache.http.conn.util.InetAddressUtils;
import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.CommunityTarget;
import org.snmp4j.MessageDispatcher;
import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.Priv3DES;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.TransportIpAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.transport.AbstractTransportMapping;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class trapReceiver implements CommandResponder {

	NotificationManager mNotificationManager;
	Context context;
	
	public trapReceiver(Context context){
		
		this.context = context;
	}
	/**
	 * Trap Listener
	 */
	public synchronized void listen(TransportIpAddress address)
			throws IOException {
		@SuppressWarnings("rawtypes")
		AbstractTransportMapping transport;
		if (address instanceof TcpAddress) {
			transport = new DefaultTcpTransportMapping((TcpAddress) address);
		} else {
			transport = new DefaultUdpTransportMapping((UdpAddress) address);
		}
		
		Log.e("", "listening");

		MessageDispatcher mDispathcher = new MessageDispatcherImpl();

		// add message processing models
		mDispathcher.addMessageProcessingModel(new MPv1());
		mDispathcher.addMessageProcessingModel(new MPv2c());

		// add all security protocols
		SecurityProtocols.getInstance().addDefaultProtocols();
		SecurityProtocols.getInstance().addPrivacyProtocol(new Priv3DES());

		// Create Target
		CommunityTarget target = new CommunityTarget();
		target.setCommunity(new OctetString("public"));

		Snmp snmp = new Snmp(mDispathcher, transport);
		snmp.addCommandResponder(this);

		try{
		transport.listen();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		Log.i("", "Listening on " + address);

	}

	/**
	 * This method will be called whenever a pdu is received on the given port
	 * specified in the listen() method
	 */
	@Override
	public synchronized void processPdu(CommandResponderEvent cmdRespEvent) {
		
		PDU pdu = cmdRespEvent.getPDU();
		if (pdu != null) {
			
			try {
			
				Log.i("", "Recieved PDU");
				Log.i("", context.toString());
				int NOTIFICATION_ID = 1;
				mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

				int icon = R.drawable.icon;
				long when = System.currentTimeMillis();
				Notification notification = new Notification(icon, "", when);
				notification.flags |= Notification.FLAG_AUTO_CANCEL;

				RemoteViews contentView = new RemoteViews(context.getPackageName(),
						R.layout.custom_notification);
//				contentView.setImageViewResource(R.id.notification_image,
//						R.drawable.icon);
				contentView.setTextViewText(R.id.notification_title,
						"Alarm Triggered on "+pdu.getVariable(SnmpConstants.snmpTrapAddress));
				contentView.setTextViewText(R.id.notification_text,
						"Device1: Processes");
				notification.contentView = contentView;
//
				 Intent notificationIntent = new Intent(context,
				 fileSelectionSplashActivity.class);
				 PendingIntent contentIntent = PendingIntent.getActivity(context,
				 0, notificationIntent, 0);
				
				 notification.contentIntent = contentIntent;

				notification.flags |= Notification.FLAG_NO_CLEAR; // Do not
																	// clear the
																	// notification
				notification.defaults |= Notification.DEFAULT_LIGHTS; // LED
				notification.defaults |= Notification.DEFAULT_VIBRATE; // Vibration
				notification.defaults |= Notification.DEFAULT_SOUND; // Sound
				Log.e("Launching", "");

				try{
				Log.e("Launching2", "");	
				mNotificationManager.notify(NOTIFICATION_ID, notification);
				Log.i("notification", "launched");
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		
		}
	}

	public static String getLocalIpAddress() {
	    try {
	    	String ipv4 = "";
	        for (Enumeration<NetworkInterface> en = NetworkInterface
	                .getNetworkInterfaces(); en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf
	                    .getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                //System.out.println("ip1--:" + inetAddress);
	               // System.out.println("ip2--:" + inetAddress.getHostAddress());

	      // for getting IPV4 format
	      if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipv4 = inetAddress.getHostAddress())) {

	                    String ip = inetAddress.getHostAddress().toString();
	                    //System.out.println("ip---::" + ip);
	       
	                    // return inetAddress.getHostAddress().toString();
	                    return ipv4;
	                }
	            }
	        }
	    } catch (Exception ex) {
	        Log.e("IP Address", ex.toString());
	    }
	    return null;
	}



}