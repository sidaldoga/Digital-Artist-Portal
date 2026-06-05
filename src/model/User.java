package model;

import java.util.Objects;

public abstract class User {
    private String fname;
    private String lname;
    private String username;
    private String password;
    private String email;

    public User(String fname, String lname, String username, String password, String email) {
        this.fname = fname;
        this.lname = lname;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString(){
        return "User {"+"First Name="+fname+", Last Name="+lname+"\n" + "Username="+username+"\n" +  "Email="+email+" }";
    }
    @Override
    public boolean equals(Object object ){
        if (this==object)return true;
        if (!(object instanceof User user))return false;
        return Objects.equals(email,user.email);

    }

    @Override
    public int hashCode(){
        return Objects.hash(email);
    }
}

