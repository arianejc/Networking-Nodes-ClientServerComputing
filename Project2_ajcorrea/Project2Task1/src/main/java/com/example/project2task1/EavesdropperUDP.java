// Ariane Correa
// ajcorrea

package com.example.project2task1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

public class EavesdropperUDP {

    public static void main(String args[]) {

        System.out.println("The Eavesdropper UDP is running.");

        DatagramSocket aSocket = null; // Socket for listening to the client.
        DatagramSocket bSocket = null; // Socket for communication with the server.

        byte[] buffer = new byte[1000];

        try {

            // Define the server host (in this case, localhost).
            InetAddress aHost = InetAddress.getByName("localhost");
            Scanner sc = new Scanner(System.in);
            System.out.println("Provide port number Eavesdropper should listen on");
            int serverPortListener = sc.nextInt(); // Read the port number Eavesdropper should listen on
            System.out.println("Provide port number Eavesdropper is masquerading as");
            int serverPortMasq = sc.nextInt(); // Read the port number Eavesdropper is masquerading as

            aSocket = new DatagramSocket(serverPortListener); // Listen for client messages.
            bSocket = new DatagramSocket(); // Create a socket for communicating with the server.

            DatagramPacket clientRequest = new DatagramPacket(buffer, buffer.length);

            while (true) {

                aSocket.receive(clientRequest); // Receive client's request.

                //copy the relevant message to the reply array , which would be the length of the request
                byte[] replyArray = new byte[clientRequest.getLength()];
                System.arraycopy(clientRequest.getData(), 0, replyArray, 0, clientRequest.getLength());
                String requestString = new String(replyArray);
                System.out.println("Client Message: " + requestString);

                // Check if the request contains the word "like" as a whole word.
                if (containsWholeWord(requestString, "like")) {
                    // ACTIVE ATTACK: Replace "like" with "dislike" in the client's request.
                    requestString = replaceWholeWord(requestString, "like", "dislike");
                }

                byte[] m = requestString.getBytes();
                DatagramPacket serverRequest = new DatagramPacket(m, m.length, aHost, serverPortMasq);
                bSocket.send(serverRequest); // Forward the modified request to the server.
                bSocket.receive(serverRequest); // Receive server's reply.

                byte[] serverReplyArray = new byte[serverRequest.getLength()];
                System.arraycopy(serverRequest.getData(), 0,
                        serverReplyArray, 0, serverRequest.getLength());
                String serverReply = new String(serverReplyArray);

                System.out.println("Server Reply: " + serverReply);

                byte[] n = serverReply.getBytes();

                DatagramPacket reply = new DatagramPacket(n,
                        n.length, clientRequest.getAddress(), clientRequest.getPort());

                aSocket.send(reply); // Send the server's reply to the client.

            }

        }
        catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage()); // Handle SocketException if it occurs.
        }
        catch (IOException e) {
            System.out.println("IO: " + e.getMessage()); // Handle IOException if it occurs.
        }
        finally {
            // Close the DatagramSockets when done.
            if (aSocket != null) aSocket.close();
            if (bSocket != null) bSocket.close();
        }
    }

    // Function to check if a string contains a whole word.
    private static boolean containsWholeWord(String input, String word) {
        String regex = "\\b" + word + "\\b";
        return input.matches(".*" + regex + ".*");
    }

    // Function to replace a whole word in a string.
    private static String replaceWholeWord(String input, String wordToReplace, String replacement) {
        String regex = "\\b" + wordToReplace + "\\b";
        return input.replaceAll(regex, replacement);
    }
}
