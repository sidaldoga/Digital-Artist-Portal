package network.dto;

import java.io.Serializable;

public class UserDTO implements Serializable {
    private int id;
    private String fname;
    private String lname;
    private String username;
    private String email;
    private String role;

    public UserDTO(int id, String fname, String lname, String username, String email, String role) {
        this.id = id;
        this.fname = fname;
        this.lname = lname;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}
