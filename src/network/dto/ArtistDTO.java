package network.dto;

import java.io.Serializable;

public class ArtistDTO extends UserDTO implements Serializable {

    private boolean isDigital;

    public ArtistDTO(int id, String fname, String lname, String username, String email, String role, boolean isDigital) {
        super(id, fname, lname, username, email, role);
        this.isDigital = isDigital;
    }

    public boolean isDigital() {
        return isDigital;
    }
}
