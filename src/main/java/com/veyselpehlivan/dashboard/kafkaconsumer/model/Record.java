package com.veyselpehlivan.dashboard.kafkaconsumer.model;

public class Record {

    private String time;
    private String city;

    public Record(String time, String city) {
        this.time = time;
        this.city = city;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "Record{" +
                "time='" + time + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
