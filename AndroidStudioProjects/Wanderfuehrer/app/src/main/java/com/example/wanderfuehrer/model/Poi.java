package com.example.wanderfuehrer.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "poi")
public class Poi {
    @PrimaryKey(autoGenerate = true)
    public long poiId;

    @ColumnInfo(name = "routeOwnerId")
    public long routeOwnerId;

    @ColumnInfo(name = "place")
    public String place;

    @ColumnInfo(name = "coordinates")
    public String coordinates;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "photo")
    public String photo;

    public Poi(long routeOwnerId, String place, String coordinates, String description, String photo) {
        this.routeOwnerId = routeOwnerId;
        this.place = place;
        this.coordinates = coordinates;
        this.description = description;
        this.photo = photo;
    }

    @Override
    public String toString() {
        return this.place;
    }
}