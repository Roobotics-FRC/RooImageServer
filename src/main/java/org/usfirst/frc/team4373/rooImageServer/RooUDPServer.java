package org.usfirst.frc.team4373.rooImageServer;

import org.usfirst.frc.team4373.roosight.RooSerializableImage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.locks.Lock;

public class RooUDPServer implements Runnable {

    private int port;
    private int buffSize;
    private DatagramSocket socket;
    private volatile byte[] incomingData;
    private volatile RooSerializableImage currentImage;
    private Lock currentImageLock;

    public RooUDPServer(int port, int buffSize) throws IOException {
        this.port = port;
        socket = new DatagramSocket(port);
    }

    public void run() {
        try {
            byte[] incomingData = new byte[buffSize];

            while (true) {
                DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
                socket.receive(incomingPacket);
                ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(incomingPacket.getData()));
                // acquire our little lock
                currentImageLock.lock();
                try {
                    RooSerializableImage img = (RooSerializableImage) is.readObject();
                    System.out.println("Student object received = "+ img);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                currentImageLock.unlock();

                //InetAddress IPAddress = incomingPacket.getAddress();
                //int port = incomingPacket.getPort();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RooSerializableImage safeGetCurrentImage() {
        currentImageLock.lock();
        RooSerializableImage temp = currentImage;
        currentImageLock.unlock();
        return temp;
    }
}