package vnt.NonActivities.weatherforecast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import vnt.activities.weatherforecast.R;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.style.DrawableMarginSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;



public class WeatherFragment extends android.app.Fragment {
    Typeface weatherFont;
     
    TableLayout tl;
    TableRow tr;
    LayoutParams lp;
    Handler handler;
 TextView tvCity,tvCountry;
    public WeatherFragment(){   
        handler = new Handler();
    }
    public void createTableRow(Context context,String date,String temp,String humidity,String pressure,String speed,String clouds,String weather) {
    	   lp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);  
    	tr = new TableRow(context);
    	  tr.setLayoutParams(lp);
    	  GradientDrawable gd = new GradientDrawable();
         
          gd.setStroke(2, 0xFF000000);
    	  TextView tvdate = new TextView(context);
    	  tvdate.setLayoutParams(lp);
    	  tvdate.setWidth(300);
    	  tvdate.setHeight(200);
    	tvdate.setTextColor(Color.WHITE);
    	  tvdate.setText(date);
    	  
    	  TextView tvTemp = new TextView(context);
    	  tvTemp.setLayoutParams(lp);
    	  tvTemp.setWidth(300);
    	  tvTemp.setHeight(200);
    	  tvTemp.setTextColor(Color.WHITE);
    	  tvTemp.setText("  "+temp);
    	  
    	  TextView tvHumidity = new TextView(context);
    	  tvHumidity.setLayoutParams(lp);
    	  tvHumidity.setWidth(300);
    	  tvHumidity.setHeight(200);
    	  tvHumidity.setTextColor(Color.WHITE);
    	  tvHumidity.setText("   "+humidity+"%");
    	  
    	  TextView tvPressure = new TextView(context);
    	  tvPressure.setLayoutParams(lp);
    	  tvPressure.setWidth(300);
    	  tvPressure.setHeight(200);
    	  tvPressure.setTextColor(Color.WHITE);
    	  tvPressure.setText(pressure+"hpa");
    	  
    	  TextView tvSpeed = new TextView(context);
    	  tvSpeed.setLayoutParams(lp);
    	  tvSpeed.setWidth(300);
    	  tvSpeed.setHeight(200);
    	  tvSpeed.setTextColor(Color.WHITE);
    	 tvSpeed.setText(speed);
    	  
    	  TextView tvClouds = new TextView(context);
    	  tvClouds.setLayoutParams(lp);
    	  tvClouds.setWidth(300);
    	  tvClouds.setHeight(200);
    	  tvClouds.setTextColor(Color.WHITE);
    	  tvClouds.setText(clouds);
    	  
    	  TextView tvWeather = new TextView(context);
    	  tvWeather.setLayoutParams(lp);
    	  tvWeather.setWidth(300);
    	  tvWeather.setHeight(200);
    	  tvWeather.setTextColor(Color.WHITE);
    	 
    	  tvWeather.setText(weather);
    	  

    	  tr.addView(tvdate);
    	  tr.addView(tvTemp);
    	  tr.addView(tvHumidity);
    	  tr.addView(tvPressure);
    	  tr.addView(tvSpeed);
    	  tr.addView(tvClouds);
    	  tr.addView(tvWeather);

    	  tl.addView(tr, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
    	}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        tl = (TableLayout) rootView.findViewById(R.id.tlGridTable);
 	tvCity=(TextView) rootView.findViewById(R.id.tvCity);
 	tvCountry=(TextView) rootView.findViewById(R.id.tvCountry);
 	
       
        return rootView; 
    }
    

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);  
	    //weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");     
	    weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "weather.ttf");     
	    updateWeatherData(getActivity(),new CityPreference(getActivity()).getCity());
	}
     
	
	private void updateWeatherData(final Context context,final String city){
	    new Thread(){
	        public void run(){
	            final JSONObject json = RemoteFetch.getJSON(getActivity(), city);
	            if(json == null){
	                handler.post(new Runnable(){
	                    public void run(){
	                        Toast.makeText(context, 
	                                "Sorry, no weather data found.", 
	                                Toast.LENGTH_LONG).show(); 
	                    }
	                });
	            } else {
	                handler.post(new Runnable(){
	                    public void run(){
	                        renderWeather(json,context);
	                    }
	                });
	            }               
	        }
	    }.start();
	}
	
	private void renderWeather(JSONObject json, Context context){
		
	    try {
	    	
	    	
	    	String name=json.getJSONObject("city").getString("name");
	    	 String country=json.getJSONObject("city").getString("country");
	    	 tvCity.setText("City: "+name);
	    	 tvCity.setTextColor(Color.WHITE);
	    	 tvCountry.setText("Country: "+country);
	    	 tvCountry.setTextColor(Color.WHITE);
	    	 createTableRow(context, "Date", "Temperature", "Humidity", "Pressure", "Speed", "Clouds", "Weather");
	    	 for(int i=0;i<json.getJSONArray("list").length();i++)
	    	  {
	    		  JSONObject listArray= json.getJSONArray("list").getJSONObject(i);
	    	  String day= listArray.getJSONObject("temp").getString("day");
	    	  String min=listArray.getJSONObject("temp").getString("min");
	    	  String max= listArray.getJSONObject("temp").getString("max");
	    	  String night=listArray.getJSONObject("temp").getString("night");
	    	  String eve=listArray.getJSONObject("temp").getString("eve");
	    	  String morn= listArray.getJSONObject("temp").getString("morn");
	    	  String humidity= listArray.getString("humidity");
	    	  String pressure=listArray.getString("pressure");
	    	  String speed=listArray.getString("speed");
	    	  String clouds=listArray.getString("clouds");
	    	  JSONArray weather=listArray.getJSONArray("weather");
	    	  Long date=listArray.getLong("dt");
	    	   DateFormat df = DateFormat.getDateTimeInstance();
		        String updatedOn = df.format(new Date(date*1000));
		        createTableRow(context, updatedOn, "day:"+day+"℃"+" min:"+min+"℃"+" max:"+max+"℃"+" Night:"+night+"℃"+" eve:"+eve+"℃"+" morning:"+morn+"℃ ", humidity, pressure, speed, clouds, weather.getJSONObject(0).getString("description"));
	    	  }
		        

	    }catch(Exception e){
	        Log.e("SimpleWeather", "One or more fields not found in the JSON data");
	    }
	}



	public void changeCity(Context context,String city){
	    updateWeatherData( context,city);
	}

}
