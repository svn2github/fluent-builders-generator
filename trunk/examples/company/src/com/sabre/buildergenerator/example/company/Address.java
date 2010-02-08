package com.sabre.buildergenerator.example.company;

public class Address {
    private String city;
    private int number;
    private String street;

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param aCity the city to set
     */
    public void setCity(String aCity) {
        city = aCity;
    }

    /**
     * @return the number
     */
    public int getNumber() {
        return number;
    }

    /**
     * @param aNumber the number to set
     */
    public void setNumber(int aNumber) {
        number = aNumber;
    }

    /**
     * @return the street
     */
    public String getStreet() {
        return street;
    }

    /**
     * @param aStreet the street to set
     */
    public void setStreet(String aStreet) {
        street = aStreet;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "[Address]\n+-city: " + getCity() + "\n+-street: " + getStreet() + "\n+-number: " + getNumber();
    }
}
