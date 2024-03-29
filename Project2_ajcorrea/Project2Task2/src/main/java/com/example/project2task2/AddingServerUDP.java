// Ariane Correa
// ajcorrea

package com.example.project2task2;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Scanner;

// Reference: EchoServerUDP.java from Coulouris text (https://github.com/CMU-Heinz-95702/Project-2-Client-Server)

public class AddingServerUDP {
    static int i = 0;

    public static void main(String args[]) {

        System.out.println("The UDP server is running.");

        DatagramSocket aSocket = null; // Declare a DatagramSocket for UDP communication.

        // We declare a byte array that will receive client messages
        byte[] buffer = new byte[1000];

        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("Provide port number server should listen on");
            int serverPort = sc.nextInt();  // Read the port number from the user.
            aSocket = new DatagramSocket(serverPort); // Create a DatagramSocket bound to the specified port.
            // Create a new datagram packet to fetch the request
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);

            while (true) {

                aSocket.receive(request); // Receive a UDP packet and store it in 'request'.

                byte[] replyArray = new byte[request.getLength()];
                // Copy the message to the reply array which is the length of the request
                System.arraycopy(request.getData(), 0, replyArray, 0, request.getLength());
                String requestString = new String(replyArray);

                byte[] byteArray;
                System.out.println("Adding " + requestString + " to " + i);

                // Add the value received by client
                int n = add(Integer.parseInt(requestString));
                System.out.println("Returning sum of " + n + " to client");

                // Convert the value of ByteBuffer to return back to Client
                // Reference Code: https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/nio/ByteBuffer.html
                byteArray = ByteBuffer.allocate(4).putInt(n).array();

                // Create a new datagram packet with the newly created reply array, request hosts and port array
                DatagramPacket reply = new DatagramPacket(byteArray,
                        byteArray.length, request.getAddress(), request.getPort());
                aSocket.send(reply); // Send a reply back to the client.
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

    // Adds the value received by the client to global sum variable
    public static int add(int n) {
        i += n;
        return i;
    }

}
