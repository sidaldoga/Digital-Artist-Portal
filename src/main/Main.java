package main;


import model.User;
import model.Artist;
public class Main {

    static void main() {

        Artist Yusuf= new Artist("Yusuf", "Yasti", "josephyast","goth", "pastel",false ,"1453", "joseph");
        System.out.println(Yusuf);

        Yusuf.setFname("Yusufiye");
        System.out.println(Yusuf.getFname());

        System.out.println("Hello World");
    }
}