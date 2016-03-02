package vnt.activities.weatherforecast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import vnt.NonActivities.weatherforecast.CityPreference;
import vnt.NonActivities.weatherforecast.WeatherFragment;
import android.text.InputType;
import android.util.Log;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class WeatherActivity extends Activity {
	WeatherFragment wf;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	    if (savedInstanceState == null) {
	        getFragmentManager().beginTransaction()
	                .add(R.id.container, new WeatherFragment())
	                .commit();
	    }
	    setContentView(R.layout.activity_weather);
		
		
		String city=getIntent().getExtras().getString("city");
		changeCity(city);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
	//	getMenuInflater().inflate(R.menu.weather, menu);
		return true;
	}

	 
	public void changeCity(String city){
		  wf= new WeatherFragment(){
			    @Override
			    public void onActivityCreated(Bundle savedInstanceState){
			    	   wf = (WeatherFragment)this.getFragmentManager()
			                   .findFragmentById(R.id.container);
			    }
			};
	  
	    wf.changeCity(getApplicationContext(),city);
	    new CityPreference(this).setCity(city);
	}


	
}
