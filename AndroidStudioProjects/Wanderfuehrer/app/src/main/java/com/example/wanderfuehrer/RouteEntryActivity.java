package com.example.wanderfuehrer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.wanderfuehrer.model.AppDatabase;
import com.example.wanderfuehrer.model.Route;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RouteEntryActivity extends AppCompatActivity {

    long routeId = 0;
    EditText etName;
    EditText etBegin;
    EditText etEnd;
    EditText etGPX;
    EditText etDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_entry);

        Bundle extras = getIntent().getExtras();
        if(extras != null)
            routeId = extras.getLong("route_id");
        else
            routeId = 0;
        System.out.println(routeId);

        etName= findViewById(R.id.tvName);
        etBegin = findViewById(R.id.tvBegin);
        etEnd = findViewById(R.id.tvEnd);
        etGPX = findViewById(R.id.tvGPX);
        etDuration = findViewById(R.id.tvDuration);

        if(routeId != 0)
            readDatabase();
    }

    void readDatabase() {
        // Read all entries of database
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Route> future = executor.submit(new Callable<Route>() {
            @Override
            public Route call() {
                Route route;
                AppDatabase db = Room.databaseBuilder(RouteEntryActivity.this, AppDatabase.class, "routes").build();
                route = db.routeDao().loadSingle(routeId);
                return route;
            }
        });

        Route route = null;
        try {
            route = future.get();
            etName.setText(route.name);
            etBegin.setText(route.begin);
            etEnd.setText(route.end);
            etGPX.setText(route.gpxfile);
            etDuration.setText(route.duration);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



    }

    public void goBack(View view) {
        Intent i = new Intent(this, RouteListActivity.class);
        startActivity(i);
    }

    public void showPoiList(View view) {
        Intent i = new Intent(this, PoiListActivity.class);
        i.putExtra("route_id", routeId);
        startActivity(i);
    }


    public void update(View view) {
        if(routeId == 0) {
            insert(null);
            goBack(null);
            return;
        }

        // Read specific entry of database
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Integer> future = executor.submit(new Callable<Integer>() {
            @Override
            public Integer call() {
                Route route = new Route(etName.getText().toString(),
                                        etBegin.getText().toString(),
                                        etEnd.getText().toString(),
                                        etGPX.getText().toString(),
                                        etDuration.getText().toString());
                route.groupId = routeId;
                AppDatabase db = Room.databaseBuilder(RouteEntryActivity.this, AppDatabase.class, "routes").build();
                db.routeDao().update(route);
                return 0;
            }
        });

        goBack(null);

    }

    public void insert(View view) {
        System.out.println("RouteEntryActivity::insert called");
        // Read specific entry of database
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Integer> future = executor.submit(new Callable<Integer>() {
            @Override
            public Integer call() {
                Route route = new Route(etName.getText().toString(),
                        etBegin.getText().toString(),
                        etEnd.getText().toString(),
                        etGPX.getText().toString(),
                        etDuration.getText().toString());
                //route.groupId = routeId;
                AppDatabase db = Room.databaseBuilder(RouteEntryActivity.this, AppDatabase.class, "routes").build();
                db.routeDao().insert(route);
                return 0;
            }
        });

    }

    public void deleteRoute(View view)  {
        // Read specific entry of database
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Integer> future = executor.submit(new Callable<Integer>() {
            @Override
            public Integer call() {
                Route route = new Route(etName.getText().toString(),
                        etBegin.getText().toString(),
                        etEnd.getText().toString(),
                        etGPX.getText().toString(),
                        etDuration.getText().toString());
                route.groupId = routeId;
                AppDatabase db = Room.databaseBuilder(RouteEntryActivity.this, AppDatabase.class, "routes").build();
                db.routeDao().delete(route);
                return 0;
            }
        });

        goBack(null);
    }

    public void setStart(View view) {
        new GetLocation(getApplicationContext(), (EditText)findViewById(R.id.tvBegin));
    }

    public void setEnd(View view) {
        new GetLocation(getApplicationContext(), (EditText)findViewById(R.id.tvEnd));
    }

}
