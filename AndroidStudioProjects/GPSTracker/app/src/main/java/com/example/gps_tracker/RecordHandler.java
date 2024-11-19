package com.example.gps_tracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecordHandler {
    FileOutputStream outputStream = null;

    Context context;

    private List<String> longitudes;
    private List<String> latitudes;


    RecordHandler(Context _context) {
        context = _context;
        longitudes = new ArrayList<String>();
        latitudes = new ArrayList<String>();
    }


    public List<String> getLatitudes() {
        return latitudes;
    }

    public List<String> getLongitudes() {
        return longitudes;
    }

    void log(String s) {
        Log.d(this.getClass().getSimpleName(), s);
    }


    void record_start() {

        try {
            outputStream = new FileOutputStream(file_create());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    void record_stop() {
        try {
            if (outputStream != null) {
                outputStream.close();
                outputStream = null;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    void write(String text) {
        text += "\r\n";
        try {
            outputStream.write(text.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("DefaultLocale")
    void writeSample(String time, double longitude, double latitude, double altitude) {

        if (outputStream == null) {
            return;
        }

        longitudes.add(String.valueOf(longitude));
        latitudes.add(String.valueOf(latitude));

        write(String.format("%s:\t %f° (Latitude)\t %f° (Longitude)\t %fm (Altitude)\t", time, longitude, latitude, altitude));
    }


    private File file_create() {

        File file_storage_dir = null;
        file_storage_dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);

        String time_stamp = new SimpleDateFormat("yyyyMMdd__HHmmss").format(new Date());

        return new File(file_storage_dir.getPath() + File.separator + "data" + time_stamp + ".csv");
    }


}
