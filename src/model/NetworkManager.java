package model;

public class NetworkManager {

    private int port = 8080;
    private String host = "localhost";
    private boolean isRunning = false;


    public void startServer(){
        this.isRunning = true;
        System.out.println("Server started on "+host+":"+port);

    }

    public void stopServer(){
        this.isRunning = false;
        System.out.println("Server stopped");

    }

    public void handleSubscriptionRequest(Subscription sub){

    }


}
