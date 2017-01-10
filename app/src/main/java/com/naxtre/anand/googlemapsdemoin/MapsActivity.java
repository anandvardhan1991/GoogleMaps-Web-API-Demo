package com.naxtre.anand.googlemapsdemoin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;
import android.widget.ZoomButtonsController;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener, GoogleApiClient.OnConnectionFailedListener, LoadImageTask.Listener,GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private double latitude;
    private double longitude;
    GPSTracker gpsTracker;
    List<Address> addresses;
    private Geocoder geocoder;
//    Marker marker;
//    int PLACE_PICKER_REQUEST = 1;
    PlacePicker.IntentBuilder builder;
//    private GoogleApiClient mGoogleApiClient;
    private JSONObject jsonObjectResponse;
    private GetPlacesAsynch getPlacesAsynch;
    private JSONArray resultArrays;
    ArrayList<Bitmap> bitmapArrayList;
    ArrayList<Marker> markersArrayList;
    Marker lastOpenned = null;
    Bitmap bitmapFromDrawable;

    LinkedHashMap<Integer, ArrayMap<String, String>> linkedHashMapToSaveDateInOrder = new LinkedHashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        geocoder = new Geocoder(this, Locale.getDefault());
        gpsTracker = new GPSTracker(this);
        if (gpsTracker.canGetLocation()) {
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
        } else {
            gpsTracker.showSettingsAlert();
        }

        mapFragment.getMapAsync(this);

    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //Bitmap icon = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_place_black_24dp);
        Log.e("Lat", ": " + String.valueOf(latitude));
        Log.e("Long", ": " + String.valueOf(longitude));
        LatLng currentLocation = new LatLng(latitude, longitude);
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(currentLocation, 17);
        googleMap.animateCamera(yourLocation);
//        marker = googleMap.addMarker(new MarkerOptions().position(currentLocation));

//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMapClickListener(this);
        googleMap.setOnCameraMoveStartedListener(this);
        googleMap.setOnCameraMoveListener(this);
        googleMap.setOnCameraIdleListener(this);
        googleMap.setOnMarkerClickListener(this);
        builder = new PlacePicker.IntentBuilder();


//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        //Add a marker to my current location and Move the camera
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }


//        googleMap.addMarker(new MarkerOptions().position())
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mMap.clear();
        try {
            Log.e("Lat :", "" + String.valueOf(latLng.latitude));
            Log.e("Long :", "" + String.valueOf(latLng.longitude));
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
            Toast.makeText(MapsActivity.this, "Address: " + address + "\nCity: " + city + "\nState: " + state + "\nCountry: " + country + "\nZip: " + postalCode
                    + "\n Know Name: " + knownName, Toast.LENGTH_SHORT).show();
//            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        } catch (GooglePlayServicesRepairableException e) {
//            e.printStackTrace();
//        } catch (GooglePlayServicesNotAvailableException e) {
//            e.printStackTrace();
//        }

//        mGoogleApiClient = new GoogleApiClient
//                .Builder(this)
//                .addApi(Places.GEO_DATA_API)
//                .addApi(Places.PLACE_DETECTION_API)
//                .enableAutoManage(this, this)
//                .build();
//
//        Log.e("mGoogleApiClient",": "+mGoogleApiClient.toString());

    }

    @Override
    public void onCameraMoveStarted(int i) {
        Log.e("onCameraMoveStarted ","Called");
        mMap.clear();
    }

    @Override
    public void onCameraMove() {

//        marker.remove();
        final LatLng latitudeAndLongitutde = mMap.getCameraPosition().target;
        latitude = latitudeAndLongitutde.latitude;
        longitude = latitudeAndLongitutde.longitude;
//        marker = mMap.addMarker(new MarkerOptions().position(latitudeAndLongitutde));

    }


    @Override
    public void onCameraIdle() {
        Log.e("OnCameraIdle", " Called");
        Log.e("Latitude Camera Idle", "+" + String.valueOf(mMap.getCameraPosition().target.latitude));
        Log.e("Longitude Camera Idle", "+" + String.valueOf(mMap.getCameraPosition().target.longitude));
        String[] latAndLong = new String[2];
        latAndLong[0] = String.valueOf(mMap.getCameraPosition().target.latitude);
        latAndLong[1] = String.valueOf(mMap.getCameraPosition().target.longitude);
        getPlacesAsynch = new GetPlacesAsynch(this, latAndLong);

        Handler handler1 = new Handler();
        handler1.post(new Runnable() {
            @Override
            public void run() {
                try {
                    jsonObjectResponse = getPlacesAsynch.execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                Log.e("JSON Main", ": " + jsonObjectResponse.toString());
//            if(jsonObjectResponse.getJSONArray("next_page_token")!=null){
//                Log.e("There is next Token","");
//                //Not handling it now. Later we will do it.
//            }
                try {
                    resultArrays = jsonObjectResponse.getJSONArray("results");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("JSON Result Array", ": " + resultArrays.length());
                parseJSONArrayIntoHashMap();
                markersArrayList = new ArrayList<>();
                bitmapArrayList = new ArrayList<>();
                for (int i = 0; i < resultArrays.length(); i++) {
                    markersArrayList.add(i, mMap.addMarker(parsingTheLinkedHashMapForPOI(i)));
                }

            }
        });


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("", "++++");
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == PLACE_PICKER_REQUEST) {
//            if (resultCode == RESULT_OK) {
//                Place place = PlacePicker.getPlace(data, this);
//                String toastMsg = String.format("Place: %s", place.getName());
//                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
//            }
//        }
//    }

    public void parseJSONArrayIntoHashMap() {
        ArrayMap<String, String> paramatersForArrayElements;
        for (int i = 0; i < resultArrays.length(); i++) {
            try {
                JSONObject jsonObject = resultArrays.getJSONObject(i);
                paramatersForArrayElements = new ArrayMap<>();
                paramatersForArrayElements.put("icon", jsonObject.getString("icon"));
                paramatersForArrayElements.put("name", jsonObject.getString("name"));
                paramatersForArrayElements.put("vicinity", jsonObject.getString("vicinity"));
                JSONObject geometryObject = jsonObject.getJSONObject("geometry");
                JSONObject locationObject = geometryObject.getJSONObject("location");
                paramatersForArrayElements.put("lat", locationObject.getString("lat"));
                paramatersForArrayElements.put("lng", locationObject.getString("lng"));
                linkedHashMapToSaveDateInOrder.put(i, paramatersForArrayElements);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public MarkerOptions parsingTheLinkedHashMapForPOI(int position) {
//        Bitmap bitmap=null;
        ArrayMap<String, String> retrievingEachValueOfLinkedHashMap = new ArrayMap<>();
        retrievingEachValueOfLinkedHashMap = linkedHashMapToSaveDateInOrder.get(position);
        String lat = retrievingEachValueOfLinkedHashMap.get("lat");
        String lng = retrievingEachValueOfLinkedHashMap.get("lng");
        LatLng latAndLng = retrieveTheLatAndLng(lat, lng);
        String icon = retrievingEachValueOfLinkedHashMap.get("icon");
//        try {
//            bitmap=new LoadImageTask(MapsActivity.this).execute(icon).get();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
        String name = retrievingEachValueOfLinkedHashMap.get("name");
        String vicinity = retrievingEachValueOfLinkedHashMap.get("vicinity");
//        if(bitmap==null){
        return new MarkerOptions().position(latAndLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).title(name).snippet(vicinity).visible(true);
//        }
//        else{
//            return new MarkerOptions().position(latAndLng).icon(BitmapDescriptorFactory.fromBitmap(bitmap)).title(name).snippet(vicinity).visible(true);
//        }
    }

    public LatLng retrieveTheLatAndLng(String lat, String lng) {
        return new LatLng(Double.valueOf(lat), Double.valueOf(lng));
    }


    @Override
    public void onImageLoaded(Bitmap bitmap) {
        bitmapArrayList.add(bitmap);
    }

    @Override
    public void onError() {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (lastOpenned != null) {
            // Close the info window
            lastOpenned.hideInfoWindow();

            // Is the marker the same marker that was already open
            if (lastOpenned.equals(marker)) {
                // Nullify the lastOpenned object
                lastOpenned = null;
                // Return so that the info window isn't openned again
                return true;
            }
        }

        // Open the info window for the marker
        marker.showInfoWindow();
        // Re-assign the last openned such that we can close it later
        lastOpenned = marker;

        // Event was handled by our code do not launch default behaviour.
        return true;
    }
}


