package me.service;

import java.io.IOException;
import org.snmp4j.smi.UdpAddress;

import me.trap.trapReceiver;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class snmpService extends Service {

	IBinder mBinder = new LocalBinder();

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;

	}

	public class LocalBinder extends Binder {
		public snmpService getServerInstance() {
			return snmpService.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("snmpService", "started");

			trapReceiver snmp4jTrapReceiver = new trapReceiver(this);
			try {
				snmp4jTrapReceiver.listen(new UdpAddress(trapReceiver
						.getLocalIpAddress() + "/5228"));
			} catch (IOException e) {
				e.printStackTrace();
				Toast t = Toast.makeText(this, "Error starting service", 3000);
				t.show();
				Log.e("", "service error");
			} catch (IllegalArgumentException e) {
				Toast t = Toast
						.makeText(
								this,
								"Unable to start listener service, please check your internet connection",
								3000);
				t.show();
				Log.e("", "service error");
			} 
		
	}

	@Override
	public void onDestroy() {
		 Toast.makeText(this, "Service destroyed ...",
				 Toast.LENGTH_LONG).show();
				 Log.e("here", "end");
		 super.onDestroy();
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		onStart(intent, startId);
		return START_STICKY;
	}
}

