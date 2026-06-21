package network.dto;

import java.io.Serializable;

public class AddressDTO implements Serializable {
    private int id;
    private String street;
    private String city;
    private String zipCode;
    private String country;

    public AddressDTO(String street, String city, String zipCode, String country) {
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
        this.country = country;
    }

    public AddressDTO(int id, String street, String city, String zipCode, String country) {
        this.id = id;
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
        this.country = country;
    }

    public int getId() {
        return id;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public String toString() {
        return "Address{id=" + id + ", street='" + street + "', city='" + city
                + "', zipCode='" + zipCode + "', country='" + country + "'}";
    }
}
