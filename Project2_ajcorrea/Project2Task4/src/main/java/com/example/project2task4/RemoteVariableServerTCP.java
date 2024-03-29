// Ariane Correa
// ajcorrea

package com.example.project2task4;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

// Reference: EchoServerUDP.java from Coulouris text (https://github.com/CMU-Heinz-95702/Project-2-Client-Server)
// Reference: EchoServerTCP.java from Coulouris text (https://github.com/CMU-Heinz-95702/Project-2-Client-Server)

public class RemoteVariableServerTCP {

    // Map to store user IDs and their corresponding values
    static Map<Integer, Integer> UserID_Value_Map = new TreeMap<>();

    public static void main(String args[]) {

        Socket clientSocket = null; // Declare and Initialize Client Socket
        System.out.println("The TCP server is running.");

        try {

            Scanner sc = new Scanner(System.in);
            System.out.println("Provide port number server should listen on");
            int serverPort = sc.nextInt();  // Read the port number from the user.
            ServerSocket listenSocket = new ServerSocket(serverPort); //Initialize Server Socket

            /*
             * Block waiting for a new connection request from a client.
             * When the request is received, "accept" it, and the rest
             * the tcp protocol handshake will then take place, making
             * the socket ready for reading and writing.
             */
            clientSocket = listenSocket.accept();
            // If we get here, then we are now connected to a client.

            // Set up "in" to read from the client socket
            Scanner in;
            in = new Scanner(clientSocket.getInputStream());

            // Set up "out" to write to the client socket
            PrintWriter out;
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));

            /*
             * Forever,
             *   read a line from the socket
             *   print it to the console
             *   echo it (i.e. write it) back to the client
             */

            while (!clientSocket.isClosed())  {

                // Check if there is a line available to be read
                // If not, it is because Client side has quit, but server can continue running
                if (in.hasNextLine()) {
                    String requestString = in.nextLine();

                    // Use the delimiter to fetch different values
                    String userId = requestString.split(",")[0];
                    String operation = requestString.split(",")[1];
                    String value = requestString.split(",")[2];

                    System.out.println("User " + userId + " requested " + operation + " operation.");

                    // Initialize integer variable that will store the updated value
                    // after the operation is performed on value
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
                    out.println(n);
                    out.flush();
                }
            }
        }
        catch (IOException e) {
            // Handle IO Exception by displaying an error message
            System.out.println("IO Exception: " + e.getMessage());
        }
        finally {
            try {
                // Ensure that the clientSocket is closed, if it's not null
                if (clientSocket != null) {
                    clientSocket.close();
                }
            }
            catch (IOException e) {
                // Handle any potential IOException that may occur when closing the socket
                // This is typically ignored, as the main goal is to ensure the socket is closed
            }
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
