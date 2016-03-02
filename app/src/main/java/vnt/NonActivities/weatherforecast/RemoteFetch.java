package vnt.NonActivities.weatherforecast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import vnt.activities.weatherforecast.R;
import android.content.Context;
import android.util.Log;
 
public class RemoteFetch {
 
    private static final String OPEN_WEATHER_MAP_API = 
            "http://api.openweathermap.org/data/2.5/forecast/daily?q=%s&mode=JSON&units=metric&cnt=14";
    //http://api.openweathermap.org/data/2.5/weather?q=%s&mode=JSON&units=metric&cnt=14
    //api.openweathermap.org/data/2.5/forecast/daily?q=London&mode=xml&units=metric&cnt=7
    //http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric
    public static JSONObject getJSON(Context context, String city){
        try {
            URL url = new URL(String.format(OPEN_WEATHER_MAP_API, city));           
            HttpURLConnection connection = 
                    (HttpURLConnection)url.openConnection();
             
            connection.addRequestProperty("x-api-key", 
                   "6209f607bb96b12b4453cda6b63b2b10");
             
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
             
            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();
             
            JSONObject data = new JSONObject(json.toString());
             
            // This value will be 404 if the request was not
            // successful
            if(data.getInt("cod") != 200){
                return null;
            }
             
            return data;
        }catch(Exception e){
            return null;
        }
    }   
}