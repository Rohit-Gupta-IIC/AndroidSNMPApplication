package me.service;

import java.util.Timer;
import me.pojo.alertObject;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class monitoringService extends Service {

	Timer tim = new Timer();
	monitorTask mt = new monitorTask();
	IBinder im = new LocalBinder();

	@Override
	public IBinder onBind(Intent intent) {	
		return im;
	}
	
	public class LocalBinder extends Binder {
		  public monitoringService getServiceInstance() {
		   return monitoringService.this;
		  }
		 }

	@Override
	public void onCreate() {
		super.onCreate();
		tim.scheduleAtFixedRate(mt, 0, 10000);
		Log.i("monitoringService", "started");

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Toast.makeText(this, "Service destroyed ...", Toast.LENGTH_LONG).show();
		Log.e("here", "end");
	}
	
	 @Override   
	public int onStartCommand(Intent intent, int flags, int startId) {

		 onStart(intent, startId);
	        return START_STICKY;
	}
	 
	 public void addNewAlert(alertObject obj){
		 
		 mt.addAlert(obj);
		 Log.e("added","added");
		 
	 }
	 
	 public monitorTask getMonitorTask(){
		 
		 return mt;
	 }

}
