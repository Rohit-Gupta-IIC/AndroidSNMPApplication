package me.main;

import java.util.List;

import me.map.CurrentMapOverlay;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class networkMapActivity extends MapActivity {
	
	MapView map = null;
	private MapController mapController;
	private List<Overlay> mapOverlays;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.map);
	
	map = (MapView)findViewById(R.id.mapview);
	mapOverlays = map.getOverlays();
	
	/* Use the LocationManager class to obtain GPS locations using network */
    LocationManager mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    Location location = mlocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    
    double mylongi = location.getLongitude();
    double mylat = location.getLatitude();
    
    final GeoPoint start = new GeoPoint((int)(mylat*1E6), (int) (mylongi*1E6));
    

    
  //define the mapview to be displayed on the page
  		map.setBuiltInZoomControls(true);
  		mapController = map.getController();
  		mapController.setZoom(14);
  		mapController.animateTo(start);
  		
      Drawable home = this.getResources().getDrawable(R.drawable.icon);
      CurrentMapOverlay cpOverlay = new CurrentMapOverlay(home,this);
      OverlayItem o =  new OverlayItem( start, "Food Title 1", "Food snippet 1");
      cpOverlay.addOverlay(o);
      mapOverlays.add(cpOverlay);
    
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	public class MyLocationListener implements LocationListener
	{
		
	  @Override
	public void onLocationChanged(Location loc)
	  {

//	    mylat = loc.getLatitude();
//	    mylongi = loc.getLongitude();
//
//	    String Text = "My current location is: " +
//	    "Latitud = " + mylat +
//	    "Longitud = " + mylongi;
//
//	    Toast.makeText( getApplicationContext(), Text, Toast.LENGTH_LONG).show();
	  }

	  @Override
	public void onProviderDisabled(String provider)
	  {
	    Toast.makeText( getApplication(), "Wifi Disabled", Toast.LENGTH_SHORT ).show();
	  }

	  @Override
	public void onProviderEnabled(String provider)
	  {
	    Toast.makeText( getApplicationContext(), "Wifi Enabled", Toast.LENGTH_SHORT).show();
	  }

	  @Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	  {

	  }
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    //AppConsts.ref_currentActivity = this;

	}
}
