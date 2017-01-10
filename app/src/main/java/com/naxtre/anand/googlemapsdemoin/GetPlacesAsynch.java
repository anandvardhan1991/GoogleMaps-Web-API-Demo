package com.naxtre.anand.googlemapsdemoin;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Anand Vardhan on 10/19/2016.
 */
public class GetPlacesAsynch extends AsyncTask<String, Integer, JSONObject> {
    Context context;
    ProgressDialog pd;
    String[] paramaterOfLatAndLong;
    String urlPlaces = "https://maps.googleapis.com/maps/api/place/search/json?";
    String APIKey = "AIzaSyAdCWvHdyFEVGEHY2HMnGPLUdpNi1beGbM";
    StringBuffer stringBufferUrl;

    public GetPlacesAsynch(Context context, String[] paramaterOfLatAndLong) {
        this.context = context;
        this.paramaterOfLatAndLong = paramaterOfLatAndLong;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        stringBufferUrl = new StringBuffer(urlPlaces);
        stringBufferUrl.append("key=" + APIKey + "&");
        stringBufferUrl.append("location=" + paramaterOfLatAndLong[0] + "," + paramaterOfLatAndLong[1] + "&");
        stringBufferUrl.append("radius=500&");
        stringBufferUrl.append("sensor=false");
        Log.e("StringBuffer URL", ": " + stringBufferUrl.toString());

    }

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject result=null;
        try {
            URL url = new URL(stringBufferUrl.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setDoInput(true);
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("key",APIKey);
//            jsonObject.put("location",paramaterOfLatAndLong[0]+","+paramaterOfLatAndLong[1]);
//            jsonObject.put("radius",500);
//            jsonObject.put("sensor",false);
//            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
//            dataOutputStream.writeBytes(jsonObject.toString());
//            dataOutputStream.flush();
//            dataOutputStream.close();
            connection.connect();

            Log.e("Connection ", ": " + connection.toString());
            Log.e("Connection Established?", ": " + String.valueOf(connection.getResponseCode()));
            int responseCode = connection.getResponseCode();
            String responseMsg = connection.getResponseMessage();
            Log.e("Response Code", ":  " + String.valueOf(responseCode));
            Log.e("Response Msg", ": " + responseMsg);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.e("SuccessFull", " ++ ");
                InputStream in = new BufferedInputStream(connection.getInputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String inputLine="";
                Log.e("Input Stream", ": " + in.toString());
                StringBuffer stringBuffer = new StringBuffer();
                while ((inputLine=br.readLine())!=null) {
                    stringBuffer.append(inputLine);
                }
//                Log.e("String Buffer Contents", ": " + stringBuffer.toString());
                result=new JSONObject(stringBuffer.toString());
//                Log.e("JSON Object",":"+result.toString());

            } else {
                Log.e("Invalid ", " ++ ");
            }
            connection.disconnect();
            Log.e("Connection Terminated", " ++++ Byeeeeee ");


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        return result;

    }

    @Override
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
//        Log.e("Json Object", ": " + result.toString());

    }
}
