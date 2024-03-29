// Ariane Correa
// ajcorrea

package com.example.project2task2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Scanner;

// Reference: EchoClientUDP.java from Coulouris text (https://github.com/CMU-Heinz-95702/Project-2-Client-Server)

public class AddingClientUDP {

    static int serverPort;
    static DatagramSocket aSocket = null;

    // args[] gives server hostname and message content
    public static void main(String args[]) {

        try {
            System.out.println("The UDP client is running.");

            Scanner sc = new Scanner(System.in);
            System.out.println("Provide server side port number");
            serverPort = sc.nextInt();  // Read the server's port number from the user.

            aSocket = new DatagramSocket();  // Create a DatagramSocket for sending and receiving UDP packets.
            String nextLine;
            BufferedReader typed = new BufferedReader(new InputStreamReader(System.in));

            while ((nextLine = typed.readLine()) != null)
            {

                // If "halt!" is received, close the client socket
                if (nextLine.equalsIgnoreCase("halt!")) {
                    System.out.println("UDP Client side quitting");
                    break;
                }
                // Else, send value to server
                else {
                    int x = add(Integer.parseInt(nextLine));
                    System.out.println("The server returned " + x + ".");
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

    public static int add(int i) throws IOException {

        // Define the server host (in this case, localhost).
        InetAddress aHost = InetAddress.getByName("localhost");

        // Convert user input to bytes to send via UDP
        byte[] m = String.valueOf(i).getBytes();

        // Create a DatagramPacket for sending data to the server.
        DatagramPacket request = new DatagramPacket(m, m.length, aHost, serverPort);
        aSocket.send(request); // Send the data to the server.

        // Initialize a new buffer that will store contents of the reply message
        byte[] buffer = new byte[1000];

        // Create a DatagramPacket for receiving data from the server.
        DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
        aSocket.receive(reply); // Receive data from the server.

        // Use reply.getLength() to determine the actual length of the received message
        byte[] replyArray = new byte[reply.getLength()];

        // Copy relevant reply data into the new array based on reply length
        System.arraycopy(reply.getData(), 0, replyArray, 0, reply.getLength());

        // Convert the byte array back to an integer using ByteBuffer
        // Reference Code: https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/nio/ByteBuffer.html
        int n = ByteBuffer.wrap(replyArray).getInt();
        return n;
    }
}
