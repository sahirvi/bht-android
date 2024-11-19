package com.example.gps_tracker;

import android.content.Context;
import android.location.Location;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GPXHandler {
    List<Date> dateList = new ArrayList<>();
    File file_storage_dir;

    void addDate(Date date) {
        dateList.add(date);
    }


    public void writePath(List<Location> points, Context context) {

        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?><gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"MapSource 6.15.5\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\"><trk>\n";
        String name = "<name>" + "TDM" + "</name><trkseg>\n";

        String segments = "";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        for (Location loc : points) {
            segments += "<trkpt lat=\"" + loc.getLatitude() + "\" lon=\"" + loc.getLongitude() + "\">" + "<ele>" + loc.getAltitude() + "</ele>" + "<time>" + dateFormat.format(new Date(loc.getTime())) + "Z</time></trkpt>\n";
        }

        String footer = "</trkseg></trk></gpx>";

        try {
            file_storage_dir = null;
            file_storage_dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);

            String time_stamp = new SimpleDateFormat("yyyyMMdd__HHmmss").format(new Date());

            FileWriter writer = new FileWriter(new File(file_storage_dir.getPath() + File.separator + "data" + time_stamp + ".gpx"), false);
            writer.append(header);
            writer.append(name);
            writer.append(segments);
            writer.append(footer);
            writer.flush();
            writer.close();
        } catch (IOException e) {

        }
    }
}
