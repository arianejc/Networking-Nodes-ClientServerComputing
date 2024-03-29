// Ariane Correa
// ajcorrea

package com.example.project2task0;

import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

// Reference: EchoServerUDP.java from Coulouris text (https://github.com/CMU-Heinz-95702/Project-2-Client-Server)

public class EchoServerUDP {
    public static void main(String args[]) {

        System.out.println("The UDP server is running.");

        DatagramSocket aSocket = null; // Declare a DatagramSocket for UDP communication.

        // We declare a byte array that will receive client messages
        byte[] buffer = new byte[1000];
        try {

            Scanner sc = new Scanner(System.in);
            System.out.println("Provide port number server should listen on");
            int serverPort = sc.nextInt(); // Read the port number from the user.
            aSocket = new DatagramSocket(serverPort); // Create a DatagramSocket bound to the specified port.
            // Create a new datagram packet to fetch the request
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            while (true) {

                aSocket.receive(request); // Receive a UDP packet and store it in 'request'.

                byte[] replyArray = new byte[request.getLength()];

                // Copy the message to the reply array which is the length of the request
                System.arraycopy(request.getData(), 0, replyArray, 0, request.getLength());
                String requestString = new String(replyArray); // Convert the received bytes to a string.
                System.out.println("Echoing: " + requestString);

                // Create a new datagram packet with the newly created reply array, request hosts and port array
                DatagramPacket reply = new DatagramPacket(replyArray,
                        request.getLength(), request.getAddress(), request.getPort());

                aSocket.send(reply); // Send a reply back to the client.

                // If the user enters "halt!", Server shuts down
                if (requestString.equalsIgnoreCase("halt!")) {
                    System.out.println("UDP Server side quitting");
                    break;
                }
            }
        }
        catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage()); // Handle SocketException if it occurs.
        }
        catch (IOException e) {
            System.out.println("IO: " + e.getMessage()); // Handle IOException if it occurs.
        }
        finally {
            if (aSocket != null) aSocket.close(); // Close the DatagramSocket when done.
        }
    }
}
