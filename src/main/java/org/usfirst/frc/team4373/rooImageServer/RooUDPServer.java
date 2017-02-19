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
    private volatile RooSerializableImage currentImage = null;
    private Lock currentImageLock;

    public RooUDPServer(int port, int buffSize) throws IOException {
        this.port = port;
        socket = new DatagramSocket(port);
    }

    private synchronized int toInt(byte[] data) {
        // byte[] -> int
        int len = 0;
        for (int i = 0; i < 4; ++i) {
            len |= (data[3 - i] & 0xff) << (i << 3);
        }
        return len;
    }

    private synchronized boolean checkSum(int actualSize, int receivedSize, int totalPackets) {
        return (actualSize == receivedSize * totalPackets);
    }

    private synchronized boolean checkSum(int actualSize, int isSize) {
        return (actualSize == isSize);
    }

    private synchronized byte[] toByteArray(byte[][] bs, int size) {
        int i = 0;
        byte[] nbs = new byte[size];
        for(byte[] b : bs) {
            for(int j = i; j < i + b.length; j++) {
                nbs[j] = b[j];
                i += b.length;
            }
        }
        return nbs;
    }

    public void run() {
        try {
            byte[] incomingData;
            byte[] imageLength = new byte[4];
            byte[][] intBuffer = new byte[2][4];
            int[] sizes = new int[2];

            while (true) {
                //if (socket.isConnected()) {
                System.out.println("Received!!");
                DatagramPacket[] sizePackets = new DatagramPacket[2];
                int i = 0;
                for(DatagramPacket p : sizePackets) {
                    p = new DatagramPacket(intBuffer[i], 4);
                    socket.receive(p);
                    sizes[i] = toInt(intBuffer[i]);
                    i++;
                }
                byte[][] image = new byte[sizes[1]][(sizes[0] / sizes[1])];

                for(byte[] part : image) {
                    DatagramPacket dp = new DatagramPacket(part, part.length);
                    socket.receive(dp);

                }

                byte[] finalImage = toByteArray(image, sizes[0]);

                ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(finalImage));
                currentImageLock.lock();
                try {
                    RooSerializableImage img = (RooSerializableImage) is.readObject();
                } catch(ClassNotFoundException e) {
                    e.printStackTrace();
                }
                currentImageLock.unlock();
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

    public void finalize() {
        socket.close();
    }
}