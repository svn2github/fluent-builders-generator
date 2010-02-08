package com.sabre.buildergenerator.example.company;

import java.util.ArrayList;
import java.util.List;


public class MainOld {
    public static void main(String[] args) throws Exception {
        Company sabrePolska = new Company();

        sabrePolska.setName("Sabre Polska");

        Address address = new Address();

        address.setCity("Krakow");
        address.setNumber(6);
        address.setStreet("Wadowicka");
        sabrePolska.setLocation(address);

        List<Person> employees = new ArrayList<Person>();

        // employee 1.
        Person employee = new Person();

        employee.setFirstName("Grzegorz");
        employee.setLastName("Brzeczyszczykiewicz");

        Address empAddress = new Address();

        empAddress.setCity("Krakow");
        empAddress.setNumber(99);
        empAddress.setStreet("Ukryta");
        employee.setAddress(empAddress);

        employees.add(employee);

        employee = new Person();
        employee.setFirstName("Jan");
        employee.setLastName("Kowalski");

        empAddress = new Address();
        empAddress.setCity("Krakow");
        empAddress.setStreet("Nieznana");
        empAddress.setNumber(13);
        employee.setAddress(empAddress);

        employees.add(employee);

        sabrePolska.setEmployees(employees);

        System.out.println(sabrePolska.toString());
    }
}
