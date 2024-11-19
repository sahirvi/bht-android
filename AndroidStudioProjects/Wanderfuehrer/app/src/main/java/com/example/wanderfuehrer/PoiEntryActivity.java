package com.example.wanderfuehrer;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.wanderfuehrer.model.AppDatabase;
import com.example.wanderfuehrer.model.Poi;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PoiEntryActivity extends AppCompatActivity {
    long routeId = 0;
    long poiId = 0;

    EditText etPlace;
    EditText etCoordinates;
    EditText etDescription;
    //EditText etPhoto;
    String photoURL;
   

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.poi_entry);

        //etPhoto = findViewById(R.id.etPhoto);

        Bundle extras = getIntent().getExtras();
        routeId = extras.getLong("route_id");
        poiId = extras.getLong("poi_id", 0);

        etPlace= findViewById(R.id.etPlace);
        etCoordinates = findViewById(R.id.etCoordinates);
        etDescription = findViewById(R.id.etDescription);

        String url = extras.getString("image_uri", "");
        if (!url.equals("")) {
            //etPhoto.setText(url);
            photoURL = url;
            showPhoto(url);
        }
        else
            readDatabase();
    }

    void readDatabase() {
        // Read all entries of database
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Poi> future = executor.submit(new Callable<Poi>() {
            @Override
            public Poi call() {
                Poi poi;
                AppDatabase db = Room.databaseBuilder(PoiEntryActivity.this, AppDatabase.class, "routes").build();
                poi = db.poiDao().loadSingle(routeId, poiId);
                return poi;
            }
        });

        Poi poi = null;
        try {
            poi = future.get();
            if(poi != null) {
                etPlace.setText(poi.place);
                etCoordinates.setText(poi.coordinates);
                etDescription.setText(poi.description);
                //etPhoto.setText(poi.photo);
                photoURL = poi.photo;
                showPhoto(photoURL);
            }


        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



    }

    void showPhoto(String url) {
        if(url == null)
            return;
        if(url.isEmpty())
            return;
        try {
            getContentResolver().notifyChange(Uri.parse(url), null);
            ImageView imageView = (ImageView) findViewById(R.id.imagePhoto);
            ContentResolver cr = getContentResolver();
            Bitmap bitmap;
            bitmap = android.provider.MediaStore.Images.Media
                    .getBitmap(cr, Uri.parse(url));
            imageView.setImageBitmap(bitmap);
            //Toast.makeText(this, url,
            //        Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                    .show();
            Log.e("Camera", e.toString());
        }
    }

    public void goBack(View view) {
        Intent i = new Intent(this, PoiListActivity.class);
        i.putExtra("route_id", routeId);
        startActivity(i);
    }

    public void update(View view) {
        if(poiId == 0) {
            insertPoi(null);
            goBack(null);
            return;
        }
        // Read specific entry of database
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Integer> future = executor.submit(new Callable<Integer>() {
            @Override
            public Integer call() {
                Poi poi = new Poi(routeId,
                        etPlace.getText().toString(),
                        etCoordinates.getText().toString(),
                        etDescription.getText().toString(),
                        //etPhoto.getText().toString(),
                        photoURL);
                poi.poiId = poiId;
                AppDatabase db = Room.databaseBuilder(PoiEntryActivity.this, AppDatabase.class, "routes").build();
                db.poiDao().update(poi);
                return 0;
            }
        });
        goBack(null);
    }
    public void insertPoi(View view) {
        //System.out.println("PoiEntryActivity::insertPoi");
        // Read specific entry of database
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Integer> future = executor.submit(new Callable<Integer>() {
            @Override
            public Integer call() {
                Poi poi = new Poi(routeId,
                        etPlace.getText().toString(),
                        etCoordinates.getText().toString(),
                        etDescription.getText().toString(),
                        //etPhoto.getText().toString(),
                        photoURL);
                AppDatabase db = Room.databaseBuilder(PoiEntryActivity.this, AppDatabase.class, "routes").build();
                db.poiDao().insert(poi);
                return 0;
            }
        });

    }

    public void deletePoi(View view) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Void> future = executor.submit(new Callable<Void>() {
            @Override
            public Void call() {
                Poi poi = new Poi(routeId,
                        etPlace.getText().toString(),
                        etCoordinates.getText().toString(),
                        etDescription.getText().toString(),
                        //etPhoto.getText().toString()
                        photoURL);
                poi.poiId = poiId;
                AppDatabase db = Room.databaseBuilder(PoiEntryActivity.this, AppDatabase.class, "routes").build();
                db.poiDao().delete(poi);
                return null;
            }
        });
        goBack(null);
    }

}
