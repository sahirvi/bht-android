package com.example.wanderroute;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.wanderroute.model.AppDatabase;
import com.example.wanderroute.model.Poi;
import com.example.wanderroute.model.Route;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RouteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Fill database with fake data
        //new AgentAsyncTask(this).execute();
        readDatabase();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertNew();
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
            }
        });
    }

    void insertNew() {
        Intent i = new Intent(this, NewRouteActivity.class);
        startActivity(i);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static class AgentAsyncTask extends AsyncTask<Void, Void, Integer> {

        WeakReference<Activity> activity;
        AgentAsyncTask(Activity activity) {
            this.activity = new WeakReference<>(activity);
            Log.d("ContactActivity","AgentAsyncTask");
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            Log.d("MainActivity","doInBackground");
            AppDatabase db = Room.databaseBuilder(activity.get(), AppDatabase.class, "routes").build();

            Route r1 = new Route("Teufelsberg", "52°, 13°", "52°, 13.5°", "r1.gpx", "5.20" );
            Route r2 = new Route("Ziebchenberg", "51°, 12°", "52°, 12°", "r2.gpx", "1.15");
            Route r3 = new Route("Brocken", "51°, 11°", "52°, 11°", "r3.gpx", "1.00");
            Route r4 = new Route("Ilsetal", "51°, 11.5°", "51°, 12°", "r4.gpx", "0.59");

            Poi p1 = new Poi(1, "poi1", "xx.xx, xx.xx", "test", "photo.jpg");
            Poi p2 = new Poi(2, "poi2", "xx.xx, xx.xx", "test", "photo.jpg");

            db.routeDao().reset();
            db.routeDao().insertAll(r1, r2, r3, r4);
            db.poiDao().reset();
            db.poiDao().insertAll(p1, p2);

            List list = db.routeDao().getAll();

            for(int i = 0; i < list.size(); i++) {
                Route r = (Route) list.get(i);
                Log.d("MainActivity", r.toString());
            }
            return 0;
        }
    }

    private class MyArrayAdapter extends ArrayAdapter<Route> {
        public MyArrayAdapter(Context context, int resource, List<Route> objects) {
            super(context, resource, objects);
        }
    }

    private void readDatabase() {
        // Read all entries of database
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<Route>> future = executor.submit(new Callable<List<Route>>() {
            @Override
            public List<Route> call() {
                List<Route> list;
                AppDatabase db = Room.databaseBuilder(RouteActivity.this, AppDatabase.class, "routes").build();
                list = db.routeDao().getAll();
                return list;
            }
        });

        List<Route>list = null;
        try {
            list = future.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        ListView listView = findViewById(R.id.lvRoutes);
        MyArrayAdapter adapter = new MyArrayAdapter(this, android.R.layout.simple_list_item_1, list /*Arrays.asList(eintrag)*/);
        listView.setAdapter(adapter);

        final ListView finalListView = listView;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                //Log.d("MainActivity", (String)parent.getItemAtPosition(position));
                final Route route = (Route) finalListView.getItemAtPosition(position);
                showRouteData(route);
                Log.d("RouteActivity", "MA pos " + String.valueOf(route.groupId));
            }
        });
    }

    public void showRouteData(Route route) {
        Intent i = new Intent(this, NewRouteActivity.class);
        i.putExtra("route_id", route.groupId);
        //i.putExtra("route_name", route.name);
        //i.putExtra("route_begin", route.begin);
        ///i.putExtra("route_end", route.end);
        //i.putExtra("route_gpx", route.gpxfile);
        //i.putExtra("route_duration", route.duration);
        startActivity(i);
        //startActivityForResult(i, 1);
    }

}