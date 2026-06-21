package main;

import network.Command;
import network.Response;
import network.ServerConnection;
import network.dto.SubscriberDTO;

public class RaceConditionTest {
    private static final int ITERATIONS = 25;

    public static void main(String[] args) throws InterruptedException {
        Runnable updateTask = () -> {
            String who = Thread.currentThread().getName();
            try (ServerConnection conn = new ServerConnection("localhost", 8080)) {
                for (int i = 0; i < ITERATIONS; i++) {
                    SubscriberDTO dto = new SubscriberDTO(
                            1, who, "Iteration" + i, "race_user",
                            who + "@mail.com", "SUBSCRIBER", i, null);
                    Response r = conn.send(Command.UPDATE_USER, dto);
                    System.out.println(who + " UPDATE_USER #" + i + " -> " + r);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        Thread t1 = new Thread(updateTask, "Client-A");
        Thread t2 = new Thread(updateTask, "Client-B");
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        try (ServerConnection conn = new ServerConnection("localhost", 8080)) {
            Response finalState = conn.send(Command.GET_USER, 1);
            System.out.println("\nFINAL state of user id 1 -> " + finalState);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
