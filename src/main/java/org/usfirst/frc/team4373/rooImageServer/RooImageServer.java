package org.usfirst.frc.team4373.rooImageServer;

/**
 * The main image server class.
 * @author aaplmath
 */
public class RooImageServer {

    public static void runTests() {
        System.out.println("initializing server...");
        try {

            RooUDPServer udpServer = new RooUDPServer(4373, 2048);
            System.out.println("Server initialized. running...");
            udpServer.run();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void runImageStream() {
        // TODO
    }

    public static void showImageStream() {
        // TODO
    }

    public static void main(String[] args) {
        // advent of a gigantic if...
        if(args[1].equals("--runTests")) {
            runTests();
        } else if(args[1].equals("--runImageStream")) {
            runImageStream();
        } else if(args[1].equals("--showImageStream")) {
            showImageStream();
        }
    }
}
