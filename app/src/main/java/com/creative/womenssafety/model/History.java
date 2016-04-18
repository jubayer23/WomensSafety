package com.creative.womenssafety.model;

/**
 * Created by comsol on 26-Dec-15.
 */
public class History {
    String event_id;
    String lat;
    String lng;
    String sms;
    String event_time;
    String seen;
    String name;

    public History(String event_id, String lat, String lng, String sms, String event_time, String seen, String name) {
        this.event_id = event_id;
        this.lat = lat;
        this.lng = lng;
        this.sms = sms;
        this.event_time = event_time;
        this.seen = seen;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public String getEvent_time() {
        return event_time;
    }

    public void setEvent_time(String event_time) {
        this.event_time = event_time;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }
}
