package com.example.wanderroute.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RouteDao {
    @Query("SELECT * FROM [route] WHERE groupId=:id")
    Route loadSingle(long id);

    @Query("SELECT * FROM [route]")
    List<Route> getAll();

    @Query("DELETE from [route]")
    void reset();

    @Insert
    void insert(Route route);

    @Insert
    void insertAll(Route... routes);

    @Update
    public abstract int update(Route route);

    @Delete
    void delete(Route route);
}