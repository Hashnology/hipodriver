package com.karimapps.hipodriver.models;

public class Station {
    public Station(String stationPlanId, String stationName, String totalPassengers, String position, String lat, String aLong, String ETD) {
        StationPlanId = stationPlanId;
        StationName = stationName;
        TotalPassengers = totalPassengers;
        Position = position;
        Lat = lat;
        Long = aLong;
        this.ETD = ETD;
    }

    private String StationPlanId;
    private String StationName;
    private String TotalPassengers;
    private String Position;
    private String Lat;
    private String Long;
    private String ETD;




    public Station() {
    }

    public Station(String lat, String aLong) {
        Lat = lat;
        Long = aLong;
    }

//    public Station(String stationPlanId, String stationName,
//                   String totalPassengers, String lat, String aLong, String ETD) {
//        this.StationPlanId = stationPlanId;
//        this.StationName = stationName;
//        this.TotalPassengers = totalPassengers;
//        this.Position = position;
//        this.Lat = lat;
//        this.Long = aLong;
//        this.ETD = ETD;
//    }

    public String getStationPlanId() {
        return StationPlanId;
    }

    public String getStationName() {
        return StationName;
    }

    public String getTotalPassengers() {
        return TotalPassengers;
    }

    public String getPosition() {
        return Position;
    }

    public String getLat() {
        return Lat;
    }

    public String getLong() {
        return Long;
    }

    public String getETD() {
        return ETD;
    }

    public void setStationPlanId(String stationPlanId) {
        StationPlanId = stationPlanId;
    }

    public void setStationName(String stationName) {
        StationName = stationName;
    }

    public void setTotalPassengers(String totalPassengers) {
        TotalPassengers = totalPassengers;
    }

    public void setPosition(String position) {
        Position = position;
    }

    public void setLat(String lat) {
        Lat = lat;
    }

    public void setLong(String aLong) {
        Long = aLong;
    }

    public void setETD(String ETD) {
        this.ETD = ETD;
    }
}
