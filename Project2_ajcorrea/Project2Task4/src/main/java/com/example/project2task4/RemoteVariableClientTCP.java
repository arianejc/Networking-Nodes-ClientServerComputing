// Ariane Correa
// ajcorrea

package com.example.project2task4;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Scanner;

// Reference: EchoClientUDP.java from Coulouris text (https://github.com/CMU-Heinz-95702/Project-2-Client-Server)
// Reference: EchoClientTCP.java from Coulouris text (https://github.com/CMU-Heinz-95702/Project-2-Client-Server)

public class RemoteVariableClientTCP {

    static int serverPort;
    static Socket clientSocket = null; // Declare and Initialize Client Socket object

    // args[] gives server hostname and message content
    public static void main(String args[]) {

        System.out.println("The TCP client is running.");
        Scanner sc = new Scanner(System.in);
        System.out.println("Provide server side port number");

        try {
            serverPort = sc.nextInt();  // Read the server's port number from the user.
            clientSocket = new Socket("localhost", serverPort);

            while (!clientSocket.isClosed()) {

                // Display Menu of Operation Options to Client Console
                System.out.println("1. Add a value to your sum.\n" +
                        "2. Subtract a value from your sum.\n" +
                        "3. Get your sum.\n" +
                        "4. Exit client");

                int option = sc.nextInt();
                int value = 0;
                String op = "";

                // Check the user's choice and set the operation and value accordingly
                if (option == 1) {
                    System.out.println("Enter value to add:");
                    value = sc.nextInt();
                    op = "add";
                }

                if (option == 2) {
                    System.out.println("Enter value to subtract:");
                    value = sc.nextInt();
                    op = "substract";
                }

                if (option == 3)
                    op = "get";

                if (option == 4) {
                    System.out.println("Client side quitting. The remote variable server is still running.");
                    break;
                }

                System.out.println("Enter your ID:");
                int userId = sc.nextInt();

                String message = userId + "," + op + "," + value;

                if (message != null) {
                    int x = result(message);
                    System.out.println("The server returned " + x);
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

    // Method that returns the input from the server
    public static int result(String str) throws IOException {

        // Create a BufferedReader to read the response from the server
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        // Create a PrintWriter to send the message to the server
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));

        // Send the message to the server
        out.println(str);
        out.flush();

        // Read the response from the server
        String line = in.readLine();

        // Print the received response to the client's console
        System.out.println("Received: " + line);

        // Parse the response as an integer and return it
        return Integer.parseInt(line);
    }

}
