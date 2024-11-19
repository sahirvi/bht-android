package com.example.wanderroute.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
@Dao
public interface PoiDao {
    @Query("SELECT * FROM [poi] WHERE routeOwnerId=:route_id AND poiId=:poi_id")
    Poi loadSingle(long route_id, long poi_id);

    @Query("SELECT * FROM [poi] WHERE routeOwnerId=:route_id")
    List<Poi> getAll(long route_id);

    @Query("DELETE from [poi]")
    void reset();

    @Insert
    void insert(Poi poi);

    @Insert
    void insertAll(Poi... poi);

    @Update
    public abstract int update(Poi poi);

    @Delete
    void delete(Poi poi);
}
