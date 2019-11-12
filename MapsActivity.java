package com.example.admin.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private static final int LOCATION_REQUEST = 500;
    private LocationManager locationManager;
    Location location;
    ArrayList<LatLng> listpoints;
    ArrayList<LatLng> trekpoint;
    ArrayList<LatLng> bluepoint;
    ArrayList<Polyline> lines;
    LatLng latLng,storeLocation;
    int flag=0,bluepoints=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        listpoints = new ArrayList<>();
        bluepoint = new ArrayList<>();
        trekpoint = new ArrayList<>();
        lines =new ArrayList<>();
        Button SetLocation=(Button) findViewById(R.id.SetLocation);
        final TextView distance=(TextView)findViewById(R.id.distance);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);


        if(locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER))
        {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Double latitude = location.getLatitude();
                    Double longitude = location.getLongitude();
                    latLng=new LatLng(latitude,longitude);
                    /*CameraUpdate cu=CameraUpdateFactory.newLatLngZoom(latLng,15);
                    mMap.animateCamera(cu);
                    locationManager.removeUpdates(this);*/

                    if (trekpoint.size() == 1) {
                        trekpoint.clear();


                    }
                    trekpoint.add(latLng);
                    mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
                    if(flag ==1) {
                        if (lines.size() == 1) {
                            lines.clear();
                            mMap.clear();
                            mMap.addMarker(new MarkerOptions().position(storeLocation).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                        }
                        Polyline line = mMap.addPolyline(new PolylineOptions().add(storeLocation, latLng).width(5).color(Color.BLACK).geodesic(true));
                        lines.add(line);
                        double dist=(int)(distance(latLng.latitude,latLng.longitude,storeLocation.latitude,storeLocation.longitude)*1000);
                        System.out.println("distance="+dist);
                        distance.setText(String.valueOf(dist));
                    }

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {


                }

            });
        }
        else if(locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)){
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Double latitude = location.getLatitude();
                    Double longitude = location.getLongitude();

                    LatLng latLng=new LatLng(latitude,longitude);
                    if (trekpoint.size() == 1) {
                        trekpoint.clear();
                    }
                    trekpoint.add(latLng);
                    mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                    if(flag ==1) {
                        if (lines.size() == 1) {
                            lines.clear();
                            mMap.clear();
                        }
                        Polyline line = mMap.addPolyline(new PolylineOptions().add(storeLocation, latLng).width(5).color(Color.BLACK).geodesic(true));
                        lines.add(line);
                        double dist=distance(latLng.latitude,latLng.longitude,storeLocation.latitude,storeLocation.longitude);

                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }
        SetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeLocation=latLng;
                flag=1;
                if(bluepoint.size()==1)
               {
                    bluepoint.clear();
                    mMap.clear();
                   mMap.addMarker(new MarkerOptions().position(storeLocation).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
               }
               else
                {
                    mMap.addMarker(new MarkerOptions().position(storeLocation).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                }
            }
        });


    }
    public double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    public double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    public double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
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
        //LatLng point=new LatLng(location2.getLatitude(),location2.getLongitude());

        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST);
            return;
        }
        mMap.setMyLocationEnabled(true);
        LatLng point=new LatLng(location.getLatitude(),location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,15));
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (listpoints.size() == 2) {
                    listpoints.clear();
                    mMap.clear();
                }
                listpoints.add(latLng);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                if (listpoints.size() == 1) {
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                }
                else
                {
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }
                mMap.addMarker(markerOptions);
                if(listpoints.size()==2)
                {

                    String url= getRequestUrl(listpoints.get(0),listpoints.get(1));
                }
            }
        });


    }
    private String getRequestUrl(LatLng origin,LatLng dest)
    {
        String str_org="origin="+origin.latitude+","+origin.longitude;
        String str_dest="destination="+dest.latitude+","+dest.longitude;
        String sensor="sensor=false";
        String mode="mode=walking";
        String parm=str_org+"&"+str_dest+"&"+sensor+"&"+mode;
        String output="jason";
        String url="https://maps.googleapis.com/maps/api/directons/"+output+"?"+parm;
        return url;

    }
    private String requestdirection(String requrl) throws IOException {
        String responsestring="";
        InputStream inputStream=null;
        HttpURLConnection httpURLConnection=null;
        try{
            URL url=new URL(requrl);
            httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.connect();
            inputStream=httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer=new StringBuffer();
            String line="";
            while((line=bufferedReader.readLine()) != null)
            {
                stringBuffer.append(line);
            }
            responsestring=stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            if(inputStream!=null)
            {
                inputStream.close();
            }
            httpURLConnection.connect();
        }
        return responsestring;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case LOCATION_REQUEST:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
                    mMap.setMyLocationEnabled(true);
                break;

        }

    }

    @Override
    public void onLocationChanged(Location location) {
        double longitude=location.getLongitude();
        double latitude=location.getLatitude();
        LatLng mylocation=new LatLng(longitude,latitude);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(mylocation);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));



    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public class taskrequestdirection extends AsyncTask<String,Void,String >{

        @Override
        protected String doInBackground(String... strings) {
            String responcestring="";
            try {
                responcestring=requestdirection(strings[0]);

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return responcestring;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Change the map type based on the user's selection.
        switch (item.getItemId()) {
            case R.id.normal_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.hybrid_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.satellite_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.terrain_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



}
