package network.dto;

import java.io.Serializable;

public class AddressDTO implements Serializable {
    private String street;
    private String city;
    private String zipCode;
    private String country;

    public AddressDTO(String street, String country, String zipCode, String city) {
        this.street = street;
        this.country = country;
        this.zipCode = zipCode;
        this.city = city;
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
}
