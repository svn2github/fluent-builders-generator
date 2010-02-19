package com.sabre.buildergenerator.example.company;

public class CompanyBuilder extends CompanyBuilderBase<CompanyBuilder> {
    public static CompanyBuilder company() {
        return new CompanyBuilder();
    }

    public CompanyBuilder() {
        super(new com.sabre.buildergenerator.example.company.Company());
    }

    public com.sabre.buildergenerator.example.company.Company build() {
        return getInstance();
    }
}

@SuppressWarnings("unchecked")
class CompanyBuilderBase<GeneratorT extends CompanyBuilderBase> {
    private com.sabre.buildergenerator.example.company.Company instance;

    protected CompanyBuilderBase(com.sabre.buildergenerator.example.company.Company aInstance) {
        instance = aInstance;
    }

    protected com.sabre.buildergenerator.example.company.Company getInstance() {
        return instance;
    }

    public GeneratorT withLocation(com.sabre.buildergenerator.example.company.Address aValue)
            throws java.lang.Exception {
        instance.setLocation(aValue);

        return (GeneratorT) this;
    }

    public LocationAddressBuilder withLocation() throws java.lang.Exception {
        com.sabre.buildergenerator.example.company.Address location = new com.sabre.buildergenerator.example.company.Address();

        return withLocation(location).new LocationAddressBuilder(location);
    }

    public class LocationAddressBuilder extends AddressBuilderBase<LocationAddressBuilder> {
        public LocationAddressBuilder(com.sabre.buildergenerator.example.company.Address aInstance) {
            super(aInstance);
        }

        public GeneratorT endLocation() {
            return (GeneratorT) CompanyBuilderBase.this;
        }
    }

    public GeneratorT withEmployees(java.util.List<? extends com.sabre.buildergenerator.example.company.Person> aValue) {
        instance.setEmployees(aValue);

        return (GeneratorT) this;
    }

    public GeneratorT withAddedEmployee(com.sabre.buildergenerator.example.company.Person aValue) {
        if (instance.getEmployees() == null) {
            instance.setEmployees(new java.util.ArrayList<com.sabre.buildergenerator.example.company.Person>());
        }

        ((java.util.ArrayList<com.sabre.buildergenerator.example.company.Person>) instance.getEmployees()).add(aValue);

        return (GeneratorT) this;
    }

    public EmployeePersonBuilder withAddedEmployee() {
        com.sabre.buildergenerator.example.company.Person employee = new com.sabre.buildergenerator.example.company.Person();

        return withAddedEmployee(employee).new EmployeePersonBuilder(employee);
    }

    public class EmployeePersonBuilder extends PersonBuilderBase<EmployeePersonBuilder> {
        public EmployeePersonBuilder(com.sabre.buildergenerator.example.company.Person aInstance) {
            super(aInstance);
        }

        public GeneratorT endEmployee() {
            return (GeneratorT) CompanyBuilderBase.this;
        }
    }

    public GeneratorT withName(java.lang.String aValue) {
        instance.setName(aValue);

        return (GeneratorT) this;
    }
}

@SuppressWarnings("unchecked")
class PersonBuilderBase<GeneratorT extends PersonBuilderBase> {
    private com.sabre.buildergenerator.example.company.Person instance;

    protected PersonBuilderBase(com.sabre.buildergenerator.example.company.Person aInstance) {
        instance = aInstance;
    }

    protected com.sabre.buildergenerator.example.company.Person getInstance() {
        return instance;
    }

    public GeneratorT withAddress(com.sabre.buildergenerator.example.company.Address aValue) {
        instance.setAddress(aValue);

        return (GeneratorT) this;
    }

    public AddressAddressBuilder withAddress() {
        com.sabre.buildergenerator.example.company.Address address = new com.sabre.buildergenerator.example.company.Address();

        return withAddress(address).new AddressAddressBuilder(address);
    }

    public class AddressAddressBuilder extends AddressBuilderBase<AddressAddressBuilder> {
        public AddressAddressBuilder(com.sabre.buildergenerator.example.company.Address aInstance) {
            super(aInstance);
        }

        public GeneratorT endAddress() {
            return (GeneratorT) PersonBuilderBase.this;
        }
    }

    public GeneratorT withFirstName(java.lang.String aValue) {
        instance.setFirstName(aValue);

        return (GeneratorT) this;
    }

    public GeneratorT withLastName(java.lang.String aValue) {
        instance.setLastName(aValue);

        return (GeneratorT) this;
    }
}

@SuppressWarnings("unchecked")
class AddressBuilderBase<GeneratorT extends AddressBuilderBase> {
    private com.sabre.buildergenerator.example.company.Address instance;

    protected AddressBuilderBase(com.sabre.buildergenerator.example.company.Address aInstance) {
        instance = aInstance;
    }

    protected com.sabre.buildergenerator.example.company.Address getInstance() {
        return instance;
    }

    public GeneratorT withCity(java.lang.String aValue) {
        instance.setCity(aValue);

        return (GeneratorT) this;
    }

    public GeneratorT withNumber(int aValue) {
        instance.setNumber(aValue);

        return (GeneratorT) this;
    }

    public GeneratorT withStreet(java.lang.String aValue) {
        instance.setStreet(aValue);

        return (GeneratorT) this;
    }
}
