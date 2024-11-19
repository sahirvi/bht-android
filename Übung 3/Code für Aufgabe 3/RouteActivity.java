package com.example.gps_tracker;

import static java.lang.Double.parseDouble;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RouteActivity extends AppCompatActivity {
    List<String> longitudes;
    List<String> latitudes;
    String utm[];
    List<String> longitudesUTM;
    List<String> latitudesUTM;
    float min_longitudes;
    float max_longitudes;
    float min_latitudes;
    float max_latitutes;
    float latitude;
    float longitude;
    float size;

    Paint paint;
    Paint paint_background;
    Canvas canvas;
    Bitmap bitmap;
    float bitmap_width;
    float bitmap_height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        Intent intent = getIntent();
        longitudes = new ArrayList<>(intent.getStringArrayListExtra("longitudes"));
        latitudes = new ArrayList<>(intent.getStringArrayListExtra("latitudes"));
        longitudesUTM = new ArrayList<>();
        latitudesUTM = new ArrayList<>();
        gpsToUMT();
    }


    void createCanvas() {

        ImageView imageView = findViewById(R.id.imageView_routeTracker);

        bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint_background = new Paint();
        Paint paint_route = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint_background.setStyle(Paint.Style.FILL);
        paint_background.setColor(Color.WHITE);

        paint.setColor(Color.BLACK);
        paint_route.setColor(Color.RED);

        bitmap_width = size = bitmap.getWidth();
        bitmap_height = bitmap.getHeight();

        imageView.setImageBitmap(bitmap);
    }

    void draw(List<String> lons, List<String> lats) {
        canvas.drawPaint(paint_background);

        canvas.drawLine(0, bitmap_height - bitmap_height / 4, bitmap_width, bitmap_height - bitmap_height / 4, paint);
        canvas.drawLine(0, bitmap_height - 2 * bitmap_height / 4, bitmap_width, bitmap_height - 2 * bitmap_height / 4, paint);
        canvas.drawLine(0, bitmap_height - 3 * bitmap_height / 4, bitmap_width, bitmap_height - 3 * bitmap_height / 4, paint);
        canvas.drawLine(0, bitmap_height - 4 * bitmap_height / 4, bitmap_width, bitmap_height - 4 * bitmap_height / 4, paint);
        canvas.drawLine(0, bitmap_height, bitmap_width, bitmap_height, paint);

        canvas.drawLine(bitmap_width - 4 * bitmap_width / 4, 0, bitmap_width - 4 * bitmap_width / 4, bitmap_height, paint);
        canvas.drawLine(bitmap_width - 3 * bitmap_width / 4, 0, bitmap_width - 3 * bitmap_width / 4, bitmap_height, paint);
        canvas.drawLine(bitmap_width - 2 * bitmap_width / 4, 0, bitmap_width - 2 * bitmap_width / 4, bitmap_height, paint);
        canvas.drawLine(bitmap_width - bitmap_width / 4, 0, bitmap_width - bitmap_width / 4, bitmap_height, paint);
        canvas.drawLine(bitmap_width, 0, bitmap_width, bitmap_height, paint);

        for (int i = 1; i < lons.size(); i++) {
            canvas.drawLine(getX(Float.parseFloat(lons.get(i - 1))), getY(Float.parseFloat(lats.get(i - 1))), getX(Float.parseFloat(lons.get(i))), getY(Float.parseFloat(lats.get(i))), paint);
        }
    }

    public void resetCanvasGPS(View view) {
        if (longitudes.size() > 0) {
            calcs(longitudes, latitudes);
            createCanvas();
            draw(longitudes, latitudes);
        } else
            Snackbar.make(findViewById(R.id.route_layout), "Keine Werte aufgenommen!", Snackbar.LENGTH_LONG).show();
    }

    public void resetCanvasUTM(View view) {
        if (longitudes.size() > 0) {
            calcs(longitudesUTM, latitudesUTM);
            createCanvas();
            draw(longitudesUTM, latitudesUTM);
        } else
            Snackbar.make(findViewById(R.id.route_layout), "Keine Werte aufgenommen!", Snackbar.LENGTH_LONG).show();
    }

    void calcs(List<String> longitudeList, List<String> latitudeList) {
        List<Float> longitude_values = new ArrayList<Float>();
        List<Float> latitude_values = new ArrayList<Float>();

        String lonString;
        String lat;

        for (int i = 0; i < longitudes.size(); i++) {

            lonString = longitudeList.get(i);
            lat = latitudeList.get(i);

            longitude_values.add(Float.valueOf(lonString));
            latitude_values.add(Float.valueOf(lat));
        }

        Collections.sort(longitude_values);
        Collections.sort(latitude_values);

        min_longitudes = longitude_values.get(0);
        max_longitudes = longitude_values.get(longitude_values.size() - 1);
        min_latitudes = latitude_values.get(0);
        max_latitutes = latitude_values.get(latitude_values.size() - 1);
        latitude = max_latitutes - min_latitudes;
        longitude = max_longitudes - min_longitudes;

    }

    float getX(float longitude) {
        return (((longitude - min_longitudes) * size) / getGeoSize());
    }

    float getY(float latitude) {
        float y = (((latitude - min_latitudes) * size) / getGeoSize());
        return size - y;
    }

    float getGeoSize() {
        if (latitude > longitude) {
            return latitude;
        } else {
            return longitude;
        }

    }

    void gpsToUMT() {
        for (int i = 0; i < longitudes.size(); i++) {
            LatLng ll4 = new LatLng(parseDouble(latitudes.get(i)), parseDouble(longitudes.get(i)));
            UTMRef utm2 = ll4.toUTMRef();
            utm = utm2.toString().split(" ");
            String erdteil = utm[0];
            String utmx = utm[1];
            String utmy = utm[2];
            latitudesUTM.add(utmx);
            longitudesUTM.add(utmy);
        }
    }
}