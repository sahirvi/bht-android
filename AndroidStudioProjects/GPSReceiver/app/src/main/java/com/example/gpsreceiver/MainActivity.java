package com.example.gpsreceiver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Erstellen des LocationManagers
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Erstellen des LocationListeners, sowie die Definierung der Standardmethoden
        LocationListener locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                TextView lat = (TextView) findViewById(R.id.textLat); // textView mit bestimmter ID aus activity_main
                lat.setText("geografische Breite: " + location.getLatitude()); // Breitenwert durch getLatitude
                TextView lng = (TextView) findViewById(R.id.textLng);
                lng.setText("geografische Länge: " + location.getLongitude()); // Längenwert durch getLongitude
                TextView altitude = (TextView) findViewById(R.id.textAltitude);
                altitude.setText("geografische Höhe: " + location.getAltitude() + " m"); // Höhe durch getAltitude
                TextView speed = (TextView) findViewById(R.id.textSpeed);
                speed.setText("Geschwindigkeit: " + location.getSpeed() + " km/h"); // Geschwindigkeit durch getSpeed
            }

            public void onStatusChanged(String provider, int status, Bundle extras) { // undefinierte Standardmethode
            }

            public void onProviderEnabled(String provider) { // undefinierte Standardmethode
            }

            public void onProviderDisabled(String provider) { // undefinierte Standardmethode
            }

        };

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
        // Überprüft immer wieder, ob Erlaubnis für Standortzugriff erteilt wurde

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        // aktualisiert den locationManager für ein bestimmtes Intervalle, sodass bei Änderung des Standorts der aktuelle Wert steht
    }
}