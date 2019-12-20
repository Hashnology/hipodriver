package com.karimapps.hipodriver.models;

public class Passenger
{
    private String FirstName;
    private String LastName;
    private String PhoneNumber;

    public Passenger() {
    }

    public Passenger(String firstName, String lastName, String phoneNumber) {
        FirstName = firstName;
        LastName = lastName;
        PhoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return FirstName;
    }

    public String getLastName() {
        return LastName;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }
}
