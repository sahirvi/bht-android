package com.example.wanderfuehrer.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "route")
public class Route  {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "groupId")
    public long groupId;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "begin")
    public String begin;

    @ColumnInfo(name = "end")
    public String end;

    @ColumnInfo(name = "gpxfile")
    public String gpxfile;

    @ColumnInfo(name = "duration")
    public String duration;

    public Route(String name, String begin, String end, String gpxfile, String duration) {
        this.name = name;
        this.begin = begin;
        this.end = end;
        this.gpxfile = gpxfile;
        this.duration = duration;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String getAllValues()
    {
        StringBuilder s = new StringBuilder();
        s.append(String.format("groupId: %s\r\n", this.groupId));
        s.append(String.format("name: %s\r\n", this.name));
        s.append(String.format("begin: %s\r\n", this.begin));
        s.append(String.format("end: %s\r\n", this.end));
        s.append(String.format("gpxfile: %s\r\n", this.gpxfile));
        s.append(String.format("duration: %s\r\n", this.duration));
        return s.toString();
    }
}