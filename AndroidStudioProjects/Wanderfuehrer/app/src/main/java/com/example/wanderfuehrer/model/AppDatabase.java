package com.example.wanderfuehrer.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Route.class, Poi.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract RouteDao routeDao();
    public abstract PoiDao poiDao();
}


