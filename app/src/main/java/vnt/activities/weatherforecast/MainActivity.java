package vnt.activities.weatherforecast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import vnt.NonActivities.weatherforecast.City;
import vnt.NonActivities.weatherforecast.CityPreference;
import vnt.NonActivities.weatherforecast.DatabaseHelper;
import vnt.NonActivities.weatherforecast.NameAdapter;
import vnt.NonActivities.weatherforecast.WeatherFragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemClickListener,OnClickListener,ConnectionCallbacks,
OnConnectionFailedListener, com.google.android.gms.location.LocationListener{
	private Button buttonAdd;
	private DatabaseHelper dbHelper;
	private EditText etCity;
	private ListView list;
	private ImageView imageGps;
	private List<City> cityList;
	private NameAdapter adapter;
	private LocationManager locationMangaer=null;
	 private LocationListener locationListener=null;
	 private Boolean flag = false;


	  private static final String TAG = MainActivity.class.getSimpleName();

	    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

	    private Location mLastLocation;

	    // Google client to interact with Google API
	    private GoogleApiClient mGoogleApiClient;

	    // boolean flag to toggle periodic location updates
	    private boolean mRequestingLocationUpdates = false;

	    private LocationRequest mLocationRequest;

	    // Location updates intervals in sec
	    private static int UPDATE_INTERVAL = 10000; // 10 sec
	    private static int FATEST_INTERVAL = 5000; // 5 sec
	    private static int DISPLACEMENT = 10; // 10 meters

	    // UI elements
	    WeatherFragment wf;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		dbHelper=new DatabaseHelper(getApplicationContext());
		if (checkPlayServices()) {

			// Building the GoogleApi client
			buildGoogleApiClient();

			createLocationRequest();
		}


		initialiseViews();

	}
	 @Override
	    protected void onStart() {
	        super.onStart();
	        if (mGoogleApiClient != null) {
	            mGoogleApiClient.connect();
	        }
	    }

	    @Override
	    protected void onResume() {
	        super.onResume();

	        checkPlayServices();

	        // Resuming the periodic location updates
	        if (mGoogleApiClient.isConnected() ) {
	            startLocationUpdates();
	        }
	    }

	    @Override
	    protected void onStop() {
	        super.onStop();
	        if (mGoogleApiClient.isConnected()) {
	            mGoogleApiClient.disconnect();
	        }
	    }

	    @Override
	    protected void onPause() {
	        super.onPause();
	       stopLocationUpdates();
	    }

	    /**
	     * Method to display the location on UI
	     * */
	    private void displayLocation() {

	        mLastLocation = LocationServices.FusedLocationApi
	                .getLastLocation(mGoogleApiClient);

	        if (mLastLocation != null) {
	            double latitude = mLastLocation.getLatitude();
	            double longitude = mLastLocation.getLongitude();


	            Geocoder geocoder;
	            List<Address> addresses;
	            geocoder = new Geocoder(this, Locale.getDefault());
	            String city=null,address=null,state=null,country=null,postalCode=null,knownName=null;
	            try{


	            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
	               address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
	                city = addresses.get(0).getLocality();
	                 state = addresses.get(0).getAdminArea();
	                 country = addresses.get(0).getCountryName();
	                postalCode = addresses.get(0).getPostalCode();
	                 knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
	            }
	            catch(IOException e)
	            {
	                e.printStackTrace();
	            }

	            Intent intent =new Intent(MainActivity.this,WeatherActivity.class);
			    intent.putExtra("city",city);
			    startActivity(intent);

	        } else {

	        	 alertbox("Gps Status!!", "Your GPS is: OFF");
	        }
	    }
	private void initialiseViews()
	{
		buttonAdd=(Button) findViewById(R.id.btnAdd);

		etCity=(EditText) findViewById(R.id.etCity);
		imageGps= (ImageView) findViewById(R.id.imgGps);
		list=(ListView) findViewById(R.id.list);
		imageGps.setOnClickListener(this);




		buttonAdd.setOnClickListener(this);

		list.setOnItemClickListener(this);
		cityList=new ArrayList<City>();
		cityList=dbHelper.GetData();
		adapter = new NameAdapter(getApplicationContext(),
				R.layout.activity_listview, cityList);
		list.setAdapter(adapter);
		locationMangaer = (LocationManager)
				  getSystemService(Context.LOCATION_SERVICE);
	}
	/*----Method to Check GPS is enable or disable ----- */
	 private Boolean displayGpsStatus() {
	  ContentResolver contentResolver = getBaseContext()
	  .getContentResolver();
	  boolean gpsStatus = Settings.Secure
	  .isLocationProviderEnabled(contentResolver,
	  LocationManager.GPS_PROVIDER);
	  if (gpsStatus) {
	   return true;

	  } else {
	   return false;
	  }
	 }
	 /*----------Method to create an AlertBox ------------- */
	 protected void alertbox(String title, String mymessage) {
	  AlertDialog.Builder builder = new AlertDialog.Builder(this);
	  builder.setMessage("Your Device's GPS is Disable")
	  .setCancelable(false)
	  .setTitle("** Gps Status **")
	  .setPositiveButton("Gps On",
	   new DialogInterface.OnClickListener() {
	   public void onClick(DialogInterface dialog, int id) {
	   // finish the current activity
	   // AlertBoxAdvance.this.finish();
	   Intent myIntent = new Intent(
	   Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	   startActivity(myIntent);
	      dialog.cancel();
	   }
	   })
	   .setNegativeButton("Cancel",
	   new DialogInterface.OnClickListener() {
	   public void onClick(DialogInterface dialog, int id) {
	    // cancel the dialog box
	    dialog.cancel();
	    }
	   });
	  AlertDialog alert = builder.create();
	  alert.show();
	 }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, final int pos, long arg3) {

		new AlertDialog.Builder(this)

				.setTitle(cityList.get(pos).getCityName())


				.setMessage("Delete city or see weather of selected city")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.setNegativeButton("delete", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						City cty=cityList.get(pos);
						dbHelper.deleteData(cty);
						list.setAdapter(adapter);
						adapter.remove(cty);

					}
				})
				.setNeutralButton("see weather", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent =new Intent(MainActivity.this,WeatherActivity.class);
						intent.putExtra("city",cityList.get(pos).getCityName().toString());
						startActivity(intent);

					}
				})




				.show();


	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnAdd:
		String city=etCity.getText().toString();

		if(!city.equals("") || !city.equals(null))
		{

			City cty=new City();
			cty.setCityName(city);

				cityList.add(cty);
				dbHelper.addData(cty);
				list.setAdapter(adapter);

			etCity.setText("");
		}

			break;
		case R.id.imgGps:

		        displayLocation();
			break;

		default:
			break;
		}
		
	}
	 /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Creating location request object
     * */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Starting the location updates
     * */
    protected void startLocationUpdates() {

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
      //  displayLocation();

        if (mRequestingLocationUpdates) {
          startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        // Assign the new location
        mLastLocation = location;

        Toast.makeText(getApplicationContext(), "Location changed!",
                Toast.LENGTH_SHORT).show();

        // Displaying the new location on UI
      //  displayLocation();
    }
	 /*----------Listener class to get coordinates ------------- */
	 private class MyLocationListener implements LocationListener {  
	        @Override  
	        public void onLocationChanged(Location loc) {  
	            
	           

	            Toast.makeText(getBaseContext(),"Location changed : Lat: " +  
	   loc.getLatitude()+ " Lng: " + loc.getLongitude(),  
	   Toast.LENGTH_SHORT).show();  
	            String longitude = "Longitude: " +loc.getLongitude();    
	     
	      String latitude = "Latitude: " +loc.getLatitude();  
	      
	            
	    /*----------to get City-Name from coordinates ------------- */  
	      String cityName=null;                
	      Geocoder gcd = new Geocoder(getBaseContext(),   
	   Locale.getDefault());               
	      List<Address>  addresses;    
	      try {    
	      addresses = gcd.getFromLocation(loc.getLatitude(), loc  
	   .getLongitude(), 1);    
	      if (addresses.size() > 0)    
	         System.out.println(addresses.get(0).getLocality());    
	         cityName=addresses.get(0).getLocality();    
	        } catch (IOException e) {              
	        e.printStackTrace();    
	      }   
	            
	      String s = longitude+"\n"+latitude +  
	   "\n\nMy Currrent City is: "+cityName;  
	      Toast.makeText(getApplicationContext(), "city:"+cityName, Toast.LENGTH_LONG).show();
	          
	        }

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub
				
			}  
	  
	     
	    }
	
	}


