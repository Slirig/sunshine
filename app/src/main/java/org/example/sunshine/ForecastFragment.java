package org.example.sunshine;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Master on 2015-08-24.
 */
public class ForecastFragment extends Fragment {



    //Om vi sätter inflater till en View kan vi får tag i alla saker som ligger i denna view samtidigt
    // som en skapas, bättre än att sätta det i onViewCreated
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ArrayList<String> forecastList = new ArrayList<>();
        forecastList.add("Today - Sunny - 66 / 63");
        forecastList.add("Tomorrow - Foggy - 70 / 67");
        forecastList.add("Weds - Cloudy - 66 / 55");
        forecastList.add("Thurs - Rainy - 64 / 51");
        forecastList.add("Fri - Rainy - 33 / 55");
        forecastList.add("Sat - Sunny - 78 / 68");
        forecastList.add("Sun - Foggy - 11 / 33");


        ArrayAdapter<String> forecastAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textView, forecastList);

        ListView listView = (ListView) rootView.findViewById(R.id.listView_forecast);
        listView.setAdapter(forecastAdapter);



        return rootView;
    }

    public class FetchWeatherInfo extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            InputStream inputStream = null;
            // Will hold the raw JSON response as a string
            String forecastJsonStr = null;

            ConnectivityManager connMan = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMan.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isConnected()) {

                try {
                    // Construct a URl
                    URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=Sk%C3%B6vde&mode=json&units=metric&cnt=7&APPID=1b87b567111c2f31155dd70dc4649c3e");

                    //Create a request and open connection
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    int code = urlConnection.getResponseCode();
                    Log.d("placeholderFragment", "Response code: " + code);

                    //Read the inputStream
                    inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    //---- SHOULD BE DONE IN ASYNC?
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        //Stream was empty. no point in parsing
                        return null;
                    }
                    forecastJsonStr = buffer.toString();
                    // ---
                } catch (Exception e) {
                    e.printStackTrace();
                    forecastJsonStr = null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }else{
                Log.d("noConnection", "NO connection");

            }
            return null;
        }


    }

}
