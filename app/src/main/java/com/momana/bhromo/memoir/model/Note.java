package com.momana.bhromo.memoir.model;

import com.google.android.gms.location.places.Place;

import java.util.Calendar;
import java.util.Locale;

public class Note {

    // Attributes
    public String id;
    public String title;
    public String body;
    public Calendar calendar;
    public Location location;

    // Getters
    public String getSimpleBody() {
        return android.text.Html.fromHtml(body).toString();
    }
    public String getSimpleBodyWithoutLines() {
        return getSimpleBody().replace("\n", " ");
    }
    public String getDayOfMonth() {
        String dayOfMon = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        if (dayOfMon.length() == 1) {
            return "0" + dayOfMon;
        } else {
            return dayOfMon;
        }
    }
    public String getDayOfWeek() {
        return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
    }
    public String getMonthAndYear() {
        return calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " " + calendar.get(Calendar.YEAR);

    }
    public String getHourAndMin() {
        String hour = Integer.toString(calendar.get(Calendar.HOUR));
        if (hour.length() == 1) {
            hour = "0" + hour;
        }
        String min = Integer.toString(calendar.get(Calendar.MINUTE));
        if (min.length() == 1) {
            min = "0" + min;
        }
        return hour + ":" + min;
    }
    public String getAMPM() {
        return calendar.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.getDefault());
    }

    // Setters
    public void setLocation(Place place) {
        location = new Location(place.getName().toString(), place.getLatLng().latitude, place.getLatLng().longitude);
    }

    // Constructor
    public Note(String id, String title, String body) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.calendar = Calendar.getInstance();
        this.location = new Location();
    }
    public Note(String id, String title, String body, Calendar calendar) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.calendar = calendar;
        this.location = new Location();
    }
    public Note(String id, String title, String body, Location location) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.calendar = Calendar.getInstance();
        this.location = location;
    }
    public Note(String id, String title, String body, Calendar calendar, Location location) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.calendar = calendar;
        this.location = location;
    }
}