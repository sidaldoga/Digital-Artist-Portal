package model;

import java.util.List;
import java.util.Objects;

public class Subscriber extends User {
    private String address;
    private List<Artist> subscriptionlist;
    private int subscriberpoint = 0;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Artist> getSubscriptionlist() {
        return subscriptionlist;
    }

    public void setSubscriptionlist(List<Artist> subscriptionlist) {
        this.subscriptionlist = subscriptionlist;
    }

    public int getSubscriberpoint() {
        return subscriberpoint;
    }

    public void setSubscriberpoint(int subscriberpoint) {
        this.subscriberpoint = subscriberpoint;
    }

    public Subscriber(String fname, String lname, String username, String password, String address, List<Artist> subscriptionlist, Integer subscriberpoint,String email) {
        super(fname, lname, username, password, email);
        this.address = address;
    }

    @Override
    public String toString(){
        return "Subscriber {"+"First Name="+getFname()+", Last Name="+getLname()+"\n" + "Username="+getUsername()+"\n" + "Address="+address+"\n" + "Subscriptions= "+subscriptionlist+"\n" + "Subscriber Point="+subscriberpoint+"\n" + "Password="+getPassword()+"\"Email="+getEmail()+"}";
    }

    @Override
    public boolean equals(Object object ){
        if (this==object)return true;
        if (!(object instanceof Subscriber subscriber))return false;
        return Objects.equals(getFname(),subscriber.getFname()) && Objects.equals(getLname(), subscriber.getLname()) && Objects.equals(getUsername(),subscriber.getUsername())&& Objects.equals(subscriptionlist,subscriber.subscriptionlist)&&Objects.equals(subscriberpoint,subscriber.subscriberpoint)&& Objects.equals(address,subscriber.address) &&Objects.equals(getPassword(),subscriber.getPassword())&&Objects.equals(getEmail(),subscriber.getEmail());

    }

    @Override
    public int hashCode(){
        return Objects.hash(getFname(),getLname(),getUsername(),address,subscriptionlist,subscriberpoint,getPassword(),getEmail());
    }
}
    