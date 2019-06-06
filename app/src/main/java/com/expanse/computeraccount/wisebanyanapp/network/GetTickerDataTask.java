package com.expanse.computeraccount.wisebanyanapp.network;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.expanse.computeraccount.wisebanyanapp.MainActivityListener;
import com.expanse.computeraccount.wisebanyanapp.pojo.StockTickerObject;
import com.expanse.computeraccount.wisebanyanapp.pojo.TickerDateInfoObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class GetTickerDataTask extends AsyncTask<String, Void, String> {

    MainActivityListener mListener;
    public void setListener(MainActivityListener listener){
        mListener = listener;
    }


    @Override
    protected String doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String apiJsonStr = null;

        try {

            final String CALL_TYPE_KEY = "function";
            final String CALL_TYPE_VALUE = "TIME_SERIES_DAILY";
            final String CALL_SYMBOL_KEY = "symbol";
            final String CALL_SYMBOL_VALUE = params[0];
            final String CALL_OUTPUT_KEY = "outputsize";
            final String CALL_OUTPUT_VALUE = "full";
            final String CALL_API_KEY = "apikey";
            final String CALL_API_VALUE = "R3Y7HU5SANY66YRE";




            String uri = Uri.parse("https://www.alphavantage.co/query").buildUpon()
                    .appendQueryParameter(CALL_TYPE_KEY,CALL_TYPE_VALUE )
                    .appendQueryParameter(CALL_SYMBOL_KEY,CALL_SYMBOL_VALUE)
                    .appendQueryParameter(CALL_OUTPUT_KEY,CALL_OUTPUT_VALUE)
                    .appendQueryParameter(CALL_API_KEY,CALL_API_VALUE)
                    .build().toString();

            Log.v("URI STRING",uri);

            URL url = new URL(uri);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            int status = urlConnection.getResponseCode();
            Log.v("status GetTickerData", String.valueOf(status));
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            apiJsonStr = buffer.toString();
        } catch (IOException e) {
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
            }
        }
        return apiJsonStr;
    }

    @Override
    protected void onPostExecute(String stringReturn) {
        super.onPostExecute(stringReturn);
        Log.v("RETURNSTRING",stringReturn);
        if(stringReturn.contains("Error")){

            mListener.returnError();
        }else if(stringReturn!=null){
            try {
                mListener.returnTickerData(jsonToPojo(stringReturn));
            } catch (JSONException e) {
                e.printStackTrace();
            }

//            Log.v("STRING RETURN",stringReturn);
//            try {
//                JSONObject metaData = new JSONObject(stringReturn);
//              //  ArrayList<String> listOfUsers = new ArrayList<>();
//                Log.v("meta siz3", String.valueOf(metaData.length()));
//                JSONObject metaDataData = metaData.getJSONObject("Meta Data");
//                JSONObject metaTimeValues = metaData.getJSONObject("Time Series (Daily)");
//                Log.v("datasize", String.valueOf(metaDataData.length()));
//                Log.v("TICKERNAME", metaDataData.getString("2. Symbol"));
//                Log.v("values size", String.valueOf(metaTimeValues.length()));
//
//
//                for (int x = 0;x<metaData.length();x++){
//
//
//                }
//
//                mListener.returnTickerData("");
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }

        }

    }


    private StockTickerObject jsonToPojo(String json) throws JSONException {

        StockTickerObject createdObject = new StockTickerObject();

        JSONObject metaData = new JSONObject(json);
        JSONObject metaDataTickerInfo = metaData.getJSONObject("Meta Data");

        Log.v("SYMBOL RETURNed",metaDataTickerInfo.getString("2. Symbol"));
        createdObject.symbol = metaDataTickerInfo.getString("2. Symbol");
        createdObject.timeZone = metaDataTickerInfo.getString("5. Time Zone");
        createdObject.lastRefreshed = metaDataTickerInfo.getString("3. Last Refreshed");

        ArrayList<TickerDateInfoObject> listOfHistoryData = new ArrayList<>();
        JSONObject metaDataHistory = metaData.getJSONObject("Time Series (Daily)");
        Log.v("DATASIZE", String.valueOf(metaDataHistory.length()));
        Iterator<String> keys = metaDataHistory.keys();
        while( keys.hasNext() )
        {
            TickerDateInfoObject createdDateInfo = new TickerDateInfoObject();
            String key = keys.next();
            //  Log.v("**********", "**********");
            //  Log.v("category key", key);
            JSONObject innerJObject = metaDataHistory.getJSONObject(key);

            // Log.v("test 1",key);
            // Log.v("test 2",innerJObject.getString("1. open"));
            createdDateInfo.date = key;
            createdDateInfo.open = innerJObject.getString("1. open");
            //  Log.v("test 3",innerJObject.getString("2. high"));
            createdDateInfo.high = innerJObject.getString("2. high");
            //   Log.v("test 4",innerJObject.getString("3. low"));
            createdDateInfo.low = innerJObject.getString("3. low");
            //   Log.v("test 5",innerJObject.getString("4. close"));
            createdDateInfo.close = innerJObject.getString("4. close");
            //  Log.v("test 6",innerJObject.getString("5. volume"));
            createdDateInfo.volume = innerJObject.getString("5. volume");
            listOfHistoryData.add(createdDateInfo);
        }
        createdObject.dataList = listOfHistoryData;

//        for (int x = 0; x < metaDataHistory.length() ; x++){
//            JSONObject dateValues = metaDataHistory.getJSONObject(x);
//            Log.v("TEST", String.valueOf(dateValues.keys()));
//        }
        return createdObject;
    }
}
