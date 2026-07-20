package network.dto;

import java.io.Serializable;

public class ArtistDTO extends UserDTO implements Serializable {

    private boolean isDigital;

    public ArtistDTO(int id, String fname, String lname, String username, String email, String role, boolean isDigital, String password) {
        super(id, fname, lname, username, email, role, password);
        this.isDigital = isDigital;
    }

    public boolean isDigital() {
        return isDigital;
    }

    @Override
    public String toString() {
        return "Artist{id=" + getId() + ", username='" + getUsername() + "', fname='" + getFname()
                + "', lname='" + getLname() + "', email='" + getEmail() + "', isDigital=" + isDigital
                + "', password='" + getPassword() + "'}";
    }
}
