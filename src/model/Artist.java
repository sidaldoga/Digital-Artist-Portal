package model;

import java.util.Objects;

public class Artist {
    private String fname;
    private String lname;
    private String username;
    private String style;
    private String medium;
    private boolean isDigital;
    private String password;

    public Artist(String fname, String lname, String username, String style, String medium, boolean isDigital, String password) {
        this.fname = fname;
        this.lname = lname;
        this.username = username;
        this.style = style;
        this.medium = medium;
        this.isDigital = isDigital;
        this.password = password;
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

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public boolean isDigital() {
        return isDigital;
    }

    public void setDigital(boolean digital) {
        isDigital = digital;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString(){
        return "Artist {"+"First Name="+fname+", Last Name="+lname+"\n" + "Username="+username+"\n" + "Style="+style+"\n" + "Medium="+medium+"\n" + "Digital="+isDigital+"\n" + "Password="+password+"}";
    }
    @Override
    public boolean equals(Object object ){
        if (this==object)return true;
        if (!(object instanceof Artist artist))return false;
        return Objects.equals(fname,artist.fname) && Objects.equals(lname, artist.lname) && Objects.equals(username,artist.username)&& Objects.equals(style,artist.style)&&Objects.equals(medium,artist.medium)&& isDigital == artist.isDigital &&Objects.equals(password,artist.password);

    }

    @Override
    public int hashCode(){
        return Objects.hash(fname,lname,username,style,medium,isDigital,password);
    }
}

