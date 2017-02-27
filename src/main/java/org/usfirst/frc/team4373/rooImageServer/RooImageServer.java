package org.usfirst.frc.team4373.rooImageServer;

import in.derros.jni.UDPStreamer;


class RooImageServer {
    public static void main(String args[]) {
        if(args.length < 2) { 
		System.out.println("Usage: ./RooImageServer.jar <full path to libUDPStreamer.so>");
		return;
        }
	boolean isCustom = false;
	if(args.length == 3) { isCustom = true; }

	String libraryPath = args[1];
	UDPStreamer streamer;

	if(isCustom) { streamer = new UDPStreamer(libraryPath, Integer.parseInt(args[2]); }
    	else { streamer = new UDPStreamer(libraryPath, "server"); }
    	streamer.serverShowFrameBlocking();
    }
}
