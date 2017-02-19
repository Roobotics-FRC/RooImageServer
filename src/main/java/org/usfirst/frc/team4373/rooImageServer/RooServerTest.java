package org.usfirst.frc.team4373.rooImageServer;

/**
 * Created by derros on 2/19/17.
 */
public class RooServerTest {

    public static void main(String[] args) {
        System.out.println("Initializing UDP Server...");

        Thread ts;

        try {
            RooUDPServer server = new RooUDPServer(4373, 4096);
            System.out.println("starting async server...");
            ts = new Thread(server);
            ts.start();
            System.out.println("server running");

        } catch (Exception e) {
            System.out.println("UDPServer Initialization error.");
            e.printStackTrace();
        }
    }
}
