package org.usfirst.frc.team4373.rooImageServer;

import org.usfirst.frc.team4373.roosight.RooSerializableImage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class RooUDPServer implements Runnable {

    private int port;
    private int buffSize;
    private DatagramSocket socket;
    private byte[] incomingData;
    private RooSerializableImage currentImage = null;

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

    private synchronized byte[] to1DByteArray(byte[][] twoDArray, int size) {
        int i = 0;
        byte[] oneDArray = new byte[size];
        for (byte[] el : twoDArray) {
            for (int j = i; j < i + el.length; ++j) {
                oneDArray[j] = el[j];
                i += el.length;
            }
        }
        return oneDArray;
    }

    private synchronized byte[][] one2two(byte[] one) {
        byte[] new1 = new byte[4];
        byte[] new2 = new byte[4];
        int i;
        for (i = 0; i < 4; i++) {
            new1[i] = one[i];

        }
        for (; i < 8; i++) {
            new2[i - 4] = one[i];
        }
        return new byte[][]{new1, new2};
    }

    private synchronized byte[][] packet2two(byte[] one) {
        byte[] order = new byte[4];
        byte[] part = new byte[one.length - 4];
        int i;
        for (i = 0; i < 4; i++) {
            order[i] = one[i];
        }
        for (; i < one.length; i++) {
            part[i - 4] = one[i];
        }
        return new byte[][]{order, part};
    }

    public void run() {
        try {
            byte[] incomingData;
            byte[] imageLength = new byte[4];
            byte[][] intBuffer = new byte[2][8];
            int[] sizes = new int[2];

            while (true) {
                //if (socket.isConnected()) {
                System.out.println("Received!!");
                DatagramPacket[] sizePackets = new DatagramPacket[2];
                int i = 0;
                for (DatagramPacket p : sizePackets) {
                    p = new DatagramPacket(intBuffer[i], 8);
                    socket.receive(p);
                    byte[][] b = one2two(intBuffer[i]);
                    sizes[toInt(b[0])] = toInt(b[1]);
                    i++;
                }
                System.out.println("a: " + sizes[0]);
                System.out.println("b: " + sizes[1]);
                byte[][] image = new byte[sizes[1]][(sizes[0] / sizes[1])];
                for (int j = 0; j < image.length; j++) {
                    byte[] rcv = new byte[(sizes[0] / sizes[1]) + 4];
                    DatagramPacket ip = new DatagramPacket(rcv, rcv.length);
                    socket.receive(ip);
                    byte[][] n = packet2two(rcv);
                    image[toInt(n[0])] = n[1];
                }

                byte[] finalImage = to1DByteArray(image, sizes[0]);

                ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(finalImage));
                if (currentImage == null) {
                    try {
                        RooSerializableImage img = (RooSerializableImage) is.readObject();
                        currentImage = img;
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    synchronized (currentImage) {
                        try {
                            RooSerializableImage img = (RooSerializableImage) is.readObject();
                            currentImage = img;
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized RooSerializableImage safeGetCurrentImage() {
        synchronized (currentImage) {
            return currentImage;
        }
    }

    public void finalize() {
        socket.close();
    }
}