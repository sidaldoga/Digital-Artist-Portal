package network.dto;


import java.io.Serializable;

public class SubscriberDTO extends UserDTO implements Serializable {
    private int subscriberPoint;
    private AddressDTO address;

    public SubscriberDTO(int id, String fname, String lname, String username, String email, String role, int subscriberPoint, AddressDTO address, String password) {
        super(id, fname, lname, username, email, role, password);
        this.subscriberPoint = subscriberPoint;
        this.address = address;
    }

    public int getSubscriberPoint() {
        return subscriberPoint;
    }

    public AddressDTO getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "Subscriber{id=" + getId() + ", username='" + getUsername() + "', fname='" + getFname()
                + "', lname='" + getLname() + "', email='" + getEmail() + "', subscriberPoint=" + subscriberPoint
                + ", address=" + address + ", password='" + getPassword() + "'}";
    }
}