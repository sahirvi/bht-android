package com.example.gpsreceivermenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public LocationManager locationManager;
    public LocationListener locationListener;
    public double latitudeValue;
    public double longitudeValue;
    public double altitudeValue;
    public float speedValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {

            public void onLocationChanged(Location location) {
                latitudeValue = location.getLatitude(); // Wert wird in globalem Attribut gespeichert, um es wiederzuverwenden
                longitudeValue = location.getLongitude(); // Wert wird in globalem Attribut gespeichert, um es wiederzuverwenden
                altitudeValue = location.getAltitude(); // Wert wird in globalem Attribut gespeichert, um es wiederzuverwenden
                speedValue = location.getSpeed(); // Wert wird in globalem Attribut gespeichert, um es wiederzuverwenden
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }

        };

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // Erzeugt das Menü
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // Aktion falls ein Menü Item ausgewählt wurde

        switch (item.getItemId()) {
            case R.id.networkprovider:
                // Register the listener with the Location Manager to receive location updates
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                Toast.makeText(this, "Network-Provider ausgewählt", Toast.LENGTH_SHORT).show(); // Zeigt eine Nachricht an, sobald das Menu Item ausgewählt wurde
                TextView lat = (TextView) findViewById(R.id.textLat); // textView mit bestimmter ID aus activity_main
                lat.setText("geografische Breite: " + latitudeValue); // Breitenwert durch getLatitude
                TextView lng = (TextView) findViewById(R.id.textLng);
                lng.setText("geografische Länge: " + longitudeValue); // Längenwert durch getLongitude
                TextView altitude = (TextView) findViewById(R.id.textAltitude);
                altitude.setText("geografische Höhe: " + altitudeValue + " m"); // Höhe durch getAltitude
                TextView speed = (TextView) findViewById(R.id.textSpeed);
                speed.setText("Geschwindigkeit: " + speedValue + " km/h"); // Geschwindigkeit durch getSpeed
                return true;
            case R.id.gpsprovider:
                // Register the listener with the Location Manager to receive location updates
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                TextView lat2 = (TextView) findViewById(R.id.textLat); // textView mit bestimmter ID aus activity_main
                lat2.setText("geografische Breite: " + latitudeValue); // Breitenwert durch getLatitude
                TextView lng2 = (TextView) findViewById(R.id.textLng);
                lng2.setText("geografische Länge: " + longitudeValue); // Längenwert durch getLongitude
                TextView altitude2 = (TextView) findViewById(R.id.textAltitude);
                altitude2.setText("geografische Höhe: " + altitudeValue + " m"); // Höhe durch getAltitude
                TextView speed2 = (TextView) findViewById(R.id.textSpeed);
                speed2.setText("Geschwindigkeit: " + speedValue + " km/h"); // Geschwindigkeit durch getSpeed
                Toast.makeText(this, "GPS-Provider ausgewählt", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}