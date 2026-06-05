package model;

import java.util.Objects;

public class Artist extends User{
    private boolean isDigital;

    public Artist(String fname, String lname, String username, String password, String email, boolean isDigital) {
        super(fname, lname, username, password, email);
        this.isDigital = isDigital;
    }


    public boolean isDigital() {
        return isDigital;
    }

    public void setDigital(boolean digital) {
        isDigital = digital;
    }

    @Override
    public String toString(){
        return "Artist {"+"First Name="+getFname()+", Last Name="+getLname()+"\n" + "Username="+getUsername()+"\n"+ "Digital="+isDigital+"\n" + "Password="+getPassword()+ "Email="+getEmail()+"}";
    }
    @Override
    public boolean equals(Object object ){
        if (this==object)return true;
        if (!(object instanceof Artist artist))return false;
        return Objects.equals(getFname(),artist.getFname()) && Objects.equals(getLname(), artist.getLname()) && Objects.equals(getUsername(),artist.getUsername())&& isDigital == artist.isDigital &&Objects.equals(getPassword(),artist.getPassword())&&Objects.equals(getEmail(),artist.getEmail());

    }

    @Override
    public int hashCode(){
        return Objects.hash(getFname(),getLname(),getUsername(),isDigital,getPassword(),getEmail());
    }
}

