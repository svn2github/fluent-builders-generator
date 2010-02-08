package com.sabre.buildergenerator.example.company;

import java.util.List;


public class Company extends Base {
    private List<? extends Person> employees;
    private Address location;

    /**
     * @return the location
     */
    public Address getLocation() {
        return location;
    }

    /**
     * @param aLocation the location to set
     */
    public void setLocation(Address aLocation) throws Exception {
        if (aLocation == null) {
            throw new Exception("bummer!");
        }

        location = aLocation;
    }

    /**
     * @return the employees
     */
    public List<? extends Person> getEmployees() {
        return employees;
    }

    /**
     * @param aEmployees the employees to set
     */
    public void setEmployees(List<? extends Person> aEmployees) {
        employees = aEmployees;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        String string = "[Company]\n+-name: " + getName() + "\n+-location:\n| "
            + getLocation().toString().replace("\n", "\n| ") + "\n+-employees:";

        int i = getEmployees().size();

        for (Person employee : getEmployees()) {
            boolean isLast = --i == 0;

            string += "\n  +-" + employee.toString().replace("\n", isLast ? "\n    " : "\n  | ");
        }

        return string;
    }
}
