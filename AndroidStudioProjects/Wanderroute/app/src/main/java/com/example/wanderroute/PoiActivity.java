package com.example.wanderroute;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.wanderroute.model.AppDatabase;
import com.example.wanderroute.model.Poi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PoiActivity extends AppCompatActivity {

    private Uri imageUri;
    private static final int TAKE_PICTURE = 1;
    private long routeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi);
        FloatingActionButton fab = findViewById(R.id.fabPhoto);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto(null);

                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
            }
        });

        Bundle extras = getIntent().getExtras();
        routeId = extras.getLong("route_id");

        readDatabase();
    }

    private class PoiArrayAdapter extends ArrayAdapter<Poi> {
        public PoiArrayAdapter(Context context, int resource, List<Poi> objects) {
            super(context, resource, objects);
        }
    }

    private void readDatabase() {
        // Read all entries of database
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<Poi>> future = executor.submit(new Callable<List<Poi>>() {
            @Override
            public List<Poi> call() {
                List<Poi> list;
                AppDatabase db = Room.databaseBuilder(PoiActivity.this, AppDatabase.class, "routes").build();
                list = db.poiDao().getAll(routeId);
                return list;
            }
        });

        List<Poi> list = null;
        try {
            list = future.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        ListView listView = findViewById(R.id.lvPois);
        PoiArrayAdapter adapter = new PoiArrayAdapter(this, android.R.layout.simple_list_item_1, list /*Arrays.asList(eintrag)*/);
        listView.setAdapter(adapter);

        final ListView finalListView = listView;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                //Log.d("MainActivity", (String)parent.getItemAtPosition(position));
                final Poi poi = (Poi) finalListView.getItemAtPosition(position);
                showPoiData(poi);
                Log.d("RouteActivity", "MA pos " + String.valueOf(poi.poiId));
            }
        });
    }

    public void showPoiData(Poi poi) {
        Intent i = new Intent(this, NewPoiActivity.class);
        i.putExtra("route_id", routeId);
        i.putExtra("poi_id", poi.poiId);
        startActivity(i);
    }

    public void goBack(View view) {
        Intent i = new Intent(this, NewRouteActivity.class);
        i.putExtra("route_id", routeId);
        startActivity(i);
    }

    public void takePhoto(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), "Pic.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);
        Log.d("Camera", imageUri.toString());
        startActivityForResult(intent, TAKE_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = imageUri;

                    Intent i = new Intent(this, NewPoiActivity.class);
                    i.putExtra("route_id", routeId);
                    i.putExtra("image_uri", imageUri.toString());
                    startActivity(i);
                }
        }
    }


}