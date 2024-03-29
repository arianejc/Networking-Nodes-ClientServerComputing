// Ariane Correa
// ajcorrea

package com.example.project2task5;

import java.io.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.Scanner;

// Reference: EchoServerUDP.java from Coulouris text (https://github.com/CMU-Heinz-95702/Project-2-Client-Server)
// Reference: EchoServerTCP.java from Coulouris text (https://github.com/CMU-Heinz-95702/Project-2-Client-Server)

public class SigningClientTCP {

    static int serverPort;
    static Socket clientSocket = null; // Declare and Initialize Client Socket object
    static BigInteger n; // n is the modulus for both the private and public keys
    static BigInteger e; // e is the exponent of the public key
    static BigInteger d; // d is the exponent of the private key

    // args[] gives server hostname
    public static void main(String args[]) {

        System.out.println("The TCP client is running.");
        Scanner sc = new Scanner(System.in);
        System.out.println("Provide server side port number");

        try {
            serverPort = sc.nextInt();  // Read the server's port number from the user.
            clientSocket = new Socket(InetAddress.getByName("localhost"), serverPort);

            // Generate keys once
            generateKeys();
            // Generate the userId once
            String id = generateId(String.valueOf(e) + String.valueOf(n));

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
                    op = "subtract";
                }

                if (option == 3)
                    op = "get";

                if (option == 4) {
                    System.out.println("Client side quitting. The remote variable server is still running.");
                    break;
                }

                //generateKeys();
                //String id = generateId(String.valueOf(e) + String.valueOf(n));

                String message = id + "," + String.valueOf(e) + String.valueOf(n) + "," + op + "," + value;

                if (message != null) {
                    String x = result(message);
                    System.out.println("The server returned " + x);
                }
            }
        } catch (SocketException e) {
            // Handle Socket Exception by displaying an error message
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            // Handle IO Exception by displaying an error message
            System.out.println("IO Exception: " + e.getMessage());
        } catch (NoSuchAlgorithmException ex) {
            // Handle Runtime Exception by displaying an error message
            throw new RuntimeException(ex);
        } finally {
            try {
                // Ensure that the clientSocket is closed, if it's not null
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                // Handle any potential IOException that may occur when closing the socket
                // This is typically ignored, as the main goal is to ensure the socket is closed
            }
        }
    }

    // Method that returns the input from the server @throws IOException @throws NoSuchAlgorithmException
    public static String result(String n) throws IOException, NoSuchAlgorithmException {

        // Combine the input 'n' with its cryptographic signature
        String encrpytedMsg = n + "," + sign(n);

        // Set up input and output streams for communication
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));

        // Send the encrypted message to the server
        out.println(encrpytedMsg);
        out.flush();

        // Read the server's response
        String data = in.readLine(); // Reads a line of data from the stream
        System.out.println("Received: " + data);

        // Return the received data
        return data;
    }

    // Method to generate public and private keys for user
    public static void generateKeys() {
        // Each public and private key consists of an exponent and a modulus
        // Reference Code: RSAExample.java (https://github.com/CMU-Heinz-95702/Project-2-Client-Server)
        Random rnd = new Random();

        // Step 1: Generate two large random prime numbers, p and q.
        // We use 400 bits here, but best practice for security is 2048 bits.
        // Change 400 to 2048, recompile, and run the program again, and you will
        // notice it takes much longer to do the math with that many bits.
        BigInteger p = new BigInteger(400, 100, rnd);
        BigInteger q = new BigInteger(400, 100, rnd);

        // Step 2: Compute n by the equation n = p * q.
        n = p.multiply(q);

        // Step 3: Compute phi(n) = (p-1) * (q-1)
        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

        // Step 4: Select a small odd integer e that is relatively prime to phi(n).
        // By convention, the prime 65537 is used as the public exponent.
        e = new BigInteger("65537");

        // Step 5: Compute d as the multiplicative inverse of e modulo phi(n).
        d = e.modInverse(phi);

        // Step 6: Output the public key (e, n), which is used for encryption.
        System.out.println("Public key = " + e + n);

        // Step 7: Output the private key (d, n), which is used for decryption.
        System.out.println("Private key = " + d + n);
    }

    // Method that returns the input from the server @throws NoSuchAlgorithmException
    public static String generateId(String key) throws NoSuchAlgorithmException {
        // Step 1: Create a MessageDigest instance using SHA-256 hashing algorithm.
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        // Step 2: Generate a hash of the input 'key'.
        byte[] hashBytes = digest.digest(key.getBytes());

        // Step 3: Create a new byte array to store the final 20 bytes of the hash.
        byte[] finalBytes = new byte[20];

        // Step 4: Copy the least significant 20 bytes of the hash into the final array.
        // This is done to ensure that the output is a 20-byte (160-bit) hash.
        System.arraycopy(hashBytes, hashBytes.length - 20, finalBytes, 0, 20);

        // Step 5: Convert the byte array to a hexadecimal string representation.
        return bytesToHex(finalBytes);
    }

    // Helper function to convert a byte array to a hexadecimal string
    // Reference Code: https://mkyong.com/java/java-how-to-convert-bytes-to-hex/
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            // Convert each byte to a 2-character hexadecimal representation
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                // Pad with leading zero if necessary
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    // Method to sign the message sent to client using private key @throws UnsupportedEncodingException
    // @throws NoSuchAlgorithmException
    // Reference Code : ShortMessageSign.java (https://github.com/CMU-Heinz-95702/Project-2-Client-Server)
    public static String sign(String message) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        // Step 1: Convert the input 'message' to bytes using UTF-8 encoding.
        byte[] bytesOfMessage = message.getBytes("UTF-8");

        // Step 2: Compute the SHA-256 digest of the message.
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] bigDigest = md.digest(bytesOfMessage);

        // Step 3: Set the most significant byte of the digest to 0 (zero).
        bigDigest[0] = 0;

        // Step 4: Convert the modified digest to a BigInteger.
        BigInteger m = new BigInteger(bigDigest);

        // Step 5: Encrypt the digest using the private key (d, n) with modular exponentiation.
        BigInteger c = m.modPow(d, n);

        // Step 6: Return the encrypted digest as a string in BigInteger format.
        return c.toString();
    }

}


