package com.karimapps.hipodriver.models;

import java.sql.Timestamp;

public class Driver
{
    public String access_token;
    public String token_type;
    public String expires_in;
    public String DriverFirstName;
    public String DriverLastName;
    public String SubOrganizationId;
    public String DriverPlanLineId;
    /*public Timestamp DepartureDate;*/
    public String Lat;
    public String Long;
    public String LineName;
    public String SubOrganizationName;
    public String issued;
    public String expires;

    public Driver() {
    }

    public Driver(String access_token, String token_type, String expires_in,
                  String driverFirstName, String driverLastName,
                  String subOrganizationId, String driverPlanLineId,
                  String lat, String aLong, String lineName, String subOrganizationName, String issued, String expires) {
        this.access_token = access_token;
        this.token_type = token_type;
        this.expires_in = expires_in;
        DriverFirstName = driverFirstName;
        DriverLastName = driverLastName;
        SubOrganizationId = subOrganizationId;
        DriverPlanLineId = driverPlanLineId;
        /*DepartureDate = departureDate;*/
        Lat = lat;
        Long = aLong;
        LineName = lineName;
        SubOrganizationName = subOrganizationName;
        this.issued = issued;
        this.expires = expires;
    }
}
