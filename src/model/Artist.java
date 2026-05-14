package model;

import java.util.Objects;

public class Artist extends User{
    private String style;
    private String medium;
    private boolean isDigital;

    public Artist(String fname, String lname,String username,String style, String medium, boolean isDigital,String password, String email) {
        super(fname,lname,username,password,email);
        this.style = style;
        this.medium = medium;
        this.isDigital = isDigital;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public boolean isDigital() {
        return isDigital;
    }

    public void setDigital(boolean digital) {
        isDigital = digital;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    @Override
    public String toString(){
        return "Artist {"+"First Name="+getFname()+", Last Name="+getLname()+"\n" + "Username="+getUsername()+"\n" + "Style="+style+"\n" + "Medium="+medium+"\n" + "Digital="+isDigital+"\n" + "Password="+getPassword()+ "Email="+getEmail()+"}";
    }
    @Override
    public boolean equals(Object object ){
        if (this==object)return true;
        if (!(object instanceof Artist artist))return false;
        return Objects.equals(getFname(),artist.getFname()) && Objects.equals(getLname(), artist.getLname()) && Objects.equals(getUsername(),artist.getUsername())&& Objects.equals(style,artist.style)&&Objects.equals(medium,artist.medium)&& isDigital == artist.isDigital &&Objects.equals(getPassword(),artist.getPassword())&&Objects.equals(getEmail(),artist.getEmail());

    }

    @Override
    public int hashCode(){
        return Objects.hash(getFname(),getLname(),getUsername(),style,medium,isDigital,getPassword(),getEmail());
    }
}

