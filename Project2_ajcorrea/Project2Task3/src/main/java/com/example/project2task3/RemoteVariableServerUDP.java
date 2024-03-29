// Ariane Correa
// ajcorrea

package com.example.project2task3;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

// Reference: EchoServerUDP.java from Coulouris text (https://github.com/CMU-Heinz-95702/Project-2-Client-Server)

public class RemoteVariableServerUDP {

    //static int i = 0;

    // Map to store user IDs and their corresponding values
    static Map<Integer, Integer> UserID_Value_Map = new HashMap<>();

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

                // Use the delimiter to fetch different values
                String userId = requestString.split(",")[0];
                String operation = requestString.split(",")[1];
                String value = requestString.split(",")[2];

                byte[] byteArray;
                System.out.println("User " + userId + " requested " + operation + " operation.");

                // Initialize integer variable that will store the updated value after operation is performed on value
                int n = 0;

                // If the operation selected is Addition
                if (operation.equalsIgnoreCase("add")) {
                    n = add(Integer.parseInt(userId), Integer.parseInt(value));
                }
                // If the operation selected is Subtraction
                else if (operation.equalsIgnoreCase("substract")) {
                    n = substract(Integer.parseInt(userId), Integer.parseInt(value));
                }
                // If the operation selected is Get
                else if (operation.equalsIgnoreCase("get")) {
                    n = get(Integer.parseInt(userId));
                }

                // Print to Console
                System.out.println("Returning " + n + " to client");

                // Convert the value of ByteBuffer to return back to Client
                // Reference Code: https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/nio/ByteBuffer.html
                byteArray = ByteBuffer.allocate(4).putInt(n).array();

                // Create a new datagram packet with the newly created reply array, request hosts and port array
                DatagramPacket reply = new DatagramPacket(byteArray,
                        byteArray.length, request.getAddress(), request.getPort());
                aSocket.send(reply); // Send a reply back to the client.

                // If the user enters halt!, Close the Server Socket
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

    // Adds value received to the specific user
    public static int add(int userId, int n) {
        int result;
        if (UserID_Value_Map.containsKey(userId)) {
            result = UserID_Value_Map.get(userId) + n;
            UserID_Value_Map.put(userId, result);
        }
        else {
            result = n;
            UserID_Value_Map.put(userId, n);
        }
        return result;
    }

    // Subtracts value received from the specific user
    public static int substract(int userId, int n) {
        int result;
        if (UserID_Value_Map.containsKey(userId)) {
            result = UserID_Value_Map.get(userId) - n;
            UserID_Value_Map.put(userId, result);
        }
        else {
            result = n;
            UserID_Value_Map.put(userId, n);
        }
        return result;
    }

    //  Gets value received from the specific user
    public static int get(int userId) {
        int result;
        if (UserID_Value_Map.containsKey(userId)) {
            result = UserID_Value_Map.get(userId);
        }
        else {
            result = 0;
            UserID_Value_Map.put(userId, 0);
        }
        return result;
    }
}
