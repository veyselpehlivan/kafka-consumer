package com.veyselpehlivan.dashboard.kafkaconsumer.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;


@Document(indexName = "log2", type = "default")
public class Log {

    @Id
    int id;

    String timeStamp;
    String logLevel;
    String cityName;
    String logDetail;

    public Log(int id, String timeStamp, String logLevel, String cityName, String logDetail) {
        this.id = id;
        this.timeStamp = timeStamp;
        this.logLevel = logLevel;
        this.cityName = cityName;
        this.logDetail = logDetail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getLogDetail() {
        return logDetail;
    }

    public void setLogDetail(String logDetail) {
        this.logDetail = logDetail;
    }

    @Override
    public String toString() {
        return "Log{" +
                "id=" + id +
                ", timeStamp='" + timeStamp + '\'' +
                ", logLevel='" + logLevel + '\'' +
                ", cityName='" + cityName + '\'' +
                ", logDetail='" + logDetail + '\'' +
                '}';
    }
}
