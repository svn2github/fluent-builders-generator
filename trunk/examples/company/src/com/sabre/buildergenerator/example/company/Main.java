package com.sabre.buildergenerator.example.company;

public class Main {
    public static void main(String[] args) throws Exception {
        Company sabrePolska = CompanyBuilder.company().withName("Sabre Polska")
                                            // location
                                            .withLocation().withCity("Krakow").withStreet("Wadowicka").withNumber(6)
                                            .endLocation()
                                            // employee 1
                                            .withAddedEmployee().withFirstName("Grzegorz")
                                            .withLastName("Brzeczyszczykiewicz")
                                            // employee 1 address
                                            .withAddress().withCity("Krakow").withStreet("Ukryta").withNumber(99)
                                            .endAddress().endEmployee()
                                            // employee 2
                                            .withAddedEmployee().withFirstName("Jan").withLastName("Kowalski")
                                            // employee 2 address
                                            .withAddress().withCity("Krakow").withStreet("Nieznana").withNumber(13)
                                            .endAddress().endEmployee()
                                            // done
                                            .build();

        System.out.println(sabrePolska.toString());
    }
}
