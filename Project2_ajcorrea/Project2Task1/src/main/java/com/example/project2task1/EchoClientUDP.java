// Ariane Correa
// ajcorrea

package com.example.project2task1;

import java.net.*;
import java.io.*;
import java.util.Scanner;

// Reference: EchoClientUDP.java from Coulouris text (https://github.com/CMU-Heinz-95702/Project-2-Client-Server)
public class EchoClientUDP {

    // args[] gives server hostname and message content
    public static void main(String args[]) {

        DatagramSocket aSocket = null; // Declare a DatagramSocket for UDP communication.

        try {
            System.out.println("The UDP client is running.");

            // Define the server host (in this case, localhost).
            InetAddress aHost = InetAddress.getByName("localhost");

            Scanner sc = new Scanner(System.in);
            System.out.println("Provide server side port number");

            int serverPort = sc.nextInt(); // Read the server's port number from the user.

            aSocket = new DatagramSocket(); // Create a DatagramSocket for sending and receiving UDP packets.
            String nextLine;
            BufferedReader typed = new BufferedReader(new InputStreamReader(System.in));

            while ((nextLine = typed.readLine()) != null) {
                byte[] m = nextLine.getBytes(); // Convert user input to bytes.

                // Create a DatagramPacket for sending data to the server.
                DatagramPacket request = new DatagramPacket(m, m.length, aHost, serverPort);
                aSocket.send(request); // Send the data to the server.

                // Initialize a new buffer that will store contents of the reply message
                byte[] buffer = new byte[1000];

                // Create a DatagramPacket for receiving data from the server.
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(reply); // Receive data from the server.

                // Use reply.getLength() to determine the actual length of the received message
                byte [] replyArray = new byte[reply.getLength()];

                // Copy relevant reply data into the new array based on request length
                System.arraycopy(reply.getData(), 0, replyArray, 0, reply.getLength());
                String requestString = new String(replyArray, 0, reply.getLength()); // Convert received bytes to a string.
                System.out.println("Reply: " + requestString); // Print the server's reply.

                // Exit if "halt!" is received
                if (requestString.equalsIgnoreCase("halt!")) {
                    System.out.println("UDP Client side quitting");
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
