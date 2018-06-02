package com.example.simeonm.da;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import android.location.Location;
import android.location.LocationManager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Modules.DirectionFinder;
import Modules.DirectionFinderListener;
import Modules.DirectionString;
import Modules.Route;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener {

    TextToSpeech t1;
    private GoogleMap mMap;
    private static final int REQUEST_LOCATION = 1;
    private Button btnFindPath;
    private EditText etOrigin;
    private EditText etDestination;
    private TextView textDir;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private UiSettings mUiSettings;
    private LocationManager locationManager;
    private String lattitude,longitude;
    private Location l;
    private double lat;
    private double lan;
    LatLng latLngSource,latLngDestination;
    Button e1_source,e2_destination, Nav, Dir;
    Marker sourceMarker,destinationMarker;
    Boolean CL;
    String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        CL = false;

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocation();
        }
        btnFindPath = (Button) findViewById(R.id.btnFindPath);
        Nav= (Button)findViewById(R.id.btnNav);
        Dir= (Button)findViewById(R.id.btnDir);
        textDir= (TextView)findViewById(R.id.textDir);
        textDir.setText("");
        Nav.setVisibility(View.INVISIBLE);
        Dir.setVisibility(View.INVISIBLE);
        btnFindPath.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Nav.setVisibility(View.VISIBLE);
                Dir.setVisibility(View.VISIBLE);
                btnFindPath.setVisibility(View.INVISIBLE);
                sendRequest();
            }
        });
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!=TextToSpeech.ERROR)
                {
                    t1.setLanguage(Locale.ENGLISH);
                }
            }
        });

        Dir.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(Dir.getText().equals("See Directions")) {
                    textDir.setText(text);
                    Dir.setText("Close Directions");
                }
                else{
                    textDir.setText("");
                    Dir.setText("See Directions");
                }
            }
        });
        Nav.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(Nav.getText().equals("Start Navigation")) {
                    Nav.setText("End Navigation");
                    t1.speak(text, TextToSpeech.QUEUE_FLUSH,null);

                }
                else{
                    t1.stop();
                    Nav.setText("Start Navigation");
                }


            }
        });

        e1_source= (Button)findViewById(R.id.editText);
        e2_destination = (Button)findViewById(R.id.editText2);


        e1_source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Nav.setVisibility(View.INVISIBLE);
                Dir.setVisibility(View.INVISIBLE);
                btnFindPath.setVisibility(View.VISIBLE);
                try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(MapsActivity.this);
                    startActivityForResult(intent,200);
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                }
            }
        });

        e2_destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Nav.setVisibility(View.INVISIBLE);
                Dir.setVisibility(View.INVISIBLE);
                btnFindPath.setVisibility(View.VISIBLE);
                try {

                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(MapsActivity.this);
                    startActivityForResult(intent, 400);



                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 200)
        {
            if(resultCode == RESULT_OK)
            {
                try
                {
                    Place place = PlaceAutocomplete.getPlace(this, data);
                    String name = place.getName().toString();

                    e1_source.setText(name);
                    latLngSource = place.getLatLng();
                    if(sourceMarker!=null)
                    {
                        sourceMarker.remove();
                    }

                    CameraUpdate updateSource = CameraUpdateFactory.newLatLngZoom(latLngSource,15);
                    mMap.moveCamera(updateSource);


                    MarkerOptions optionsSource = new MarkerOptions();
                    optionsSource.title("Current Location");
                    optionsSource.position(latLngSource);
                    sourceMarker = mMap.addMarker(optionsSource);
                }catch(Exception e)
                {
                    e.printStackTrace();
                }



            }
        }
        else if(requestCode == 400)
        {
            if(resultCode == RESULT_OK)
            {

                try
                {
                    Place place = PlaceAutocomplete.getPlace(this,data);
                    latLngDestination = place.getLatLng();
                    String name = place.getName().toString();

                    e2_destination.setText(name);
                    if(destinationMarker!=null)
                    {
                        destinationMarker.remove();
                    }
                    CameraUpdate updateSource = CameraUpdateFactory.newLatLngZoom(latLngDestination,15);
                    mMap.moveCamera(updateSource);

                    MarkerOptions optionsSource = new MarkerOptions();
                    optionsSource.title("Destination Location");
                    optionsSource.position(latLngDestination);
                    destinationMarker = mMap.addMarker(optionsSource);


                    // Direction
                    StringBuilder sb;

                    Object[] dataTransfer = new Object[4];

                    sb = new StringBuilder();
                    sb.append("https://maps.googleapis.com/maps/api/directions/json?");
                    sb.append("origin=" + latLngSource.latitude + "," + latLngSource.longitude);
                    sb.append("&destination=" + latLngDestination.latitude + "," + latLngDestination.longitude);
                    sb.append("&alternatives=true");


/*
                   GetDirectionsData getDirectionsData = new GetDirectionsData(getApplicationContext());
                   dataTransfer[0] = mMap;
                   dataTransfer[1] = sb.toString();
                   dataTransfer[2] = new LatLng(latLngSource.latitude, latLngSource.longitude);
                   dataTransfer[3] = new LatLng(latLngDestination.latitude, latLngDestination.longitude);
                   getDirectionsData.execute(dataTransfer);

*/


                }catch(Exception e)
                {
                    e.printStackTrace();
                }


            }

        }

    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {

            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location location2 = locationManager.getLastKnownLocation(LocationManager. PASSIVE_PROVIDER);

            if(location != null) {
                lat = location.getLatitude();
                lan = location.getLongitude();
                lattitude = String.valueOf(lat);
                longitude = String.valueOf(lan);

            } if (location1 != null) {
                lat = location1.getLatitude();
                lan  = location1.getLongitude();
                lattitude = String.valueOf(lat);
                longitude = String.valueOf(lan);




            } else  if (location2 != null) {
                lat = location2.getLatitude();
                lan  = location2.getLongitude();
                lattitude = String.valueOf(lat);
                longitude = String.valueOf(lan);



            }


            else{

                Toast.makeText(this,"Unble to Trace your location",Toast.LENGTH_SHORT).show();

            }
        }
    }

    protected void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    private void sendRequest() {
        String origin = e1_source.getText().toString();
        String destination = e2_destination.getText().toString();
        if (origin.equals("Current Location")) {

            origin = getAddress(lat, lan );


        }
        if (destination.equals("Search Destination...")) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            new DirectionFinder(this, origin, destination).execute();
            new DirectionString(this, origin, destination).execute();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
        String add = null;
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            add = obj.getAddressLine(0);
            add = add + "\n" + obj.getCountryName();
            add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + obj.getPostalCode();
            add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getLocality();
            add = add + "\n" + obj.getSubThoroughfare();

            Log.v("IGA", "Address" + add);
            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();
            // TennisAppActivity.showDialog(add);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return add;
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


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


        mUiSettings = mMap.getUiSettings();
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setZoomControlsEnabled(true);
        LatLng school = new LatLng(lat, lan);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(school, 18));
        originMarkers.add(mMap.addMarker(new MarkerOptions()
                .title("My Location")
                .position(school)));

        mMap.setMyLocationEnabled(true);
    }


    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }
    public void onDirectionStringSuccess(String res) {
     // text = res;
    }
    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();
        List<String> ldir = new ArrayList<String>();

        for (Route route : routes) {
           ldir.add(route.directions.toString());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }

        text = ldir.toString();
        text = text.replace("</b>", "");
        text = text.replace("<b>", "");
        text = text.replace("<div style=", "");
        text = text.replace("font-size:0.9em", "");
        text = text.replace(">", "");
        text = text.replace("</div", "");
        text = text.replace("</div]]", "");
        text = text.replace("[[", "");
        text = text.replace("]", "");
        text = text.replace("Destination w", "\n"+"\n"+"Destination w");
        text = text.replace(",", "\n"+"\n");
    }



}
