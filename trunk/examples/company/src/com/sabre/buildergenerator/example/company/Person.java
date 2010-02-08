package com.sabre.buildergenerator.example.company;

public class Person {
    private Address address;
    private String firstName;
    private String lastName;

    /**
     * @return the address
     */
    public Address getAddress() {
        return address;
    }

    /**
     * @param aAddress the address to set
     */
    public void setAddress(Address aAddress) {
        address = aAddress;
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param aFirstName the firstName to set
     */
    public void setFirstName(String aFirstName) {
        firstName = aFirstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param aLastName the lastName to set
     */
    public void setLastName(String aLastName) {
        lastName = aLastName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "[Person]\n+-first name: " + getFirstName() + "\n+-last name: " + getLastName()
            + (getAddress() != null ? "\n+-address:\n  " + getAddress().toString().replace("\n", "\n  ") : "");
    }
}
